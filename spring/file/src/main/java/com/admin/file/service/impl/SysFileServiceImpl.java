package com.admin.file.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.admin.common.exception.BaseBizCodeEnum;
import com.admin.common.model.vo.ApiResultVO;
import com.admin.common.util.MyEntityUtil;
import com.admin.common.util.UserUtil;
import com.admin.file.mapper.SysFileMapper;
import com.admin.file.model.dto.SysFileDownloadDTO;
import com.admin.file.model.dto.SysFileRemoveDTO;
import com.admin.file.model.dto.SysFileUploadDTO;
import com.admin.file.model.entity.SysFileDO;
import com.admin.file.model.enums.SysFileUploadTypeEnum;
import com.admin.file.service.SysFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.*;

@Service
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFileDO> implements SysFileService {

    @Resource
    MinioClient minioClient;
    @Resource
    HttpServletResponse response;

    /**
     * 文件上传
     */
    @SneakyThrows
    @Override
    @Transactional
    public String upload(SysFileUploadDTO dto) {

        Assert.notNull(dto.getFile());

        SysFileUploadTypeEnum sysFileUploadTypeEnum = dto.getType();
        if (sysFileUploadTypeEnum == null) {
            ApiResultVO.error(BaseBizCodeEnum.PARAMETER_CHECK_ERROR);
        }

        sysFileUploadTypeEnum.checkFileSize(dto.getFile()); // 检查：文件大小

        String originalFilename = dto.getFile().getOriginalFilename();

        if (StrUtil.isBlank(originalFilename)) {
            ApiResultVO.error("操作失败：文件名不能为空");
        }

        String fileType = sysFileUploadTypeEnum.checkFileType(dto.getFile());
        if (fileType == null) {
            ApiResultVO.error("操作失败：暂不支持此文件类型【" + originalFilename + "】，请重新选择");
        }

        // 把文件夹路径合法化
        List<String> splitList = StrUtil.splitTrim(sysFileUploadTypeEnum.getFolderName(), '/');
        if (splitList.size() == 0) {
            ApiResultVO.error("操作失败：文件夹名不合法");
        }

        Long userId = UserUtil.getCurrentUserId();

        // 拼接路径
        String path = userId + "/" + CollUtil.join(splitList, "/") + "/";

        // 新的文件名
        String newFileName = IdUtil.simpleUUID() + "." + fileType;

        path = path + newFileName;

        // 不存在则创建桶
        checkAndCreateBucket(sysFileUploadTypeEnum.getBucketName());

        // 上传
        upload(sysFileUploadTypeEnum.getBucketName(), path, dto.getFile().getInputStream());

        String url =
            StrBuilder.create().append("/").append(sysFileUploadTypeEnum.getBucketName()).append("/").append(path)
                .toString();

        SysFileDO sysFileDO = new SysFileDO();
        sysFileDO.setUrl(url);
        sysFileDO.setFileName(originalFilename); // 存储：原始文件名
        sysFileDO.setFileExtName(fileType); // 存储：文件类型
        sysFileDO.setRemark(MyEntityUtil.getNotNullStr(dto.getRemark()));
        sysFileDO.setExtraJson(MyEntityUtil.getNotNullStr(dto.getExtraJson()));

        save(sysFileDO);

        return url;
    }

    /**
     * 检查是否存在，不存在则 创建桶
     */
    @SneakyThrows
    private void checkAndCreateBucket(String bucketName) {
        if (!bucketExists(bucketName)) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    /**
     * 备注：path 相同会被覆盖掉
     */
    @SneakyThrows
    private void upload(String bucketName, String objectName, InputStream inputStream) {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucketName).object(objectName)
            .stream(inputStream, -1, ObjectWriteArgs.MAX_PART_SIZE).build());
    }

    /**
     * 以流的形式获取一个文件对象（断点下载）
     */
    @SneakyThrows
    private InputStream getObject(String bucketName, String objectName, long offset, Long length) {
        if (bucketExists(bucketName)) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                return minioClient.getObject(
                    GetObjectArgs.builder().bucket(bucketName).object(objectName).offset(offset).length(length)
                        .build());
            }
        }
        return null;
    }

    /**
     * 以流的形式获取一个文件对象
     */
    @SneakyThrows
    private InputStream getObject(String bucketName, String objectName) {
        if (bucketExists(bucketName)) {
            StatObjectResponse statObject = statObject(bucketName, objectName);
            if (statObject != null && statObject.size() > 0) {
                return minioClient.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
            }
        }
        return null;
    }

    /**
     * 获取对象的元数据，用于判断对象是否存在
     */
    private StatObjectResponse statObject(String bucketName, String objectName) {
        try {
            return minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断桶是否存在
     */
    @SneakyThrows
    private boolean bucketExists(String bucketName) {
        if (StrUtil.isBlank(bucketName)) {
            ApiResultVO.error("判断文件桶是否存在失败：bucketName 为空");
        }
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 公用，文件下载（不需要登录）
     */
    @Override
    public void publicDownload(String url) {

        // 只能下载：包含 /public/ 的文件
        if (!url.contains("/public/")) {
            ApiResultVO.error("操作失败：url不合法：" + url);
        }

        download(new SysFileDownloadDTO(url));
    }

    /**
     * 文件下载
     */
    @SneakyThrows
    @Override
    public void download(SysFileDownloadDTO dto) {

        List<String> splitTrimList = StrUtil.splitTrim(dto.getUrl(), "/");

        if (splitTrimList.size() < 2) {
            ApiResultVO.error("操作失败：url不合法：" + dto.getUrl());
        }

        String bucketName = splitTrimList.get(0);

        InputStream inputStream = getObject(bucketName, dto.getUrl().split("/" + bucketName)[1]);
        if (inputStream != null) {
            ServletOutputStream outputStream = response.getOutputStream();
            IoUtil.copy(inputStream, outputStream);
            outputStream.flush();
            IoUtil.close(inputStream);
            IoUtil.close(outputStream);
        } else {
            ApiResultVO.error("操作失败：文件不存在");
        }
    }

    /**
     * 文件批量删除
     * selfFlag：是否只能删除自己的文件
     */
    @SneakyThrows
    @Override
    @Transactional
    public String remove(SysFileRemoveDTO dto, boolean selfFlag) {

        Long userId = UserUtil.getCurrentUserId();
        String userIdStr = Convert.toStr(userId);

        HashMap<String, Set<String>> delMap = MapUtil.newHashMap();
        for (String item : dto.getUrlSet()) { // item，例如：/bucketName/userId/folderName/fileName.xxx
            List<String> splitTrimList = StrUtil.splitTrim(item, "/");
            if (splitTrimList.size() < 4) {
                ApiResultVO.error("操作失败：url不合法：" + item);
            }
            // 只能删除自己的文件
            if (selfFlag && !userIdStr.equals(splitTrimList.get(1))) {
                ApiResultVO.error("操作失败：只能删除自己的文件：" + item);
            }
            String bucketName = splitTrimList.get(0);
            Set<String> orDefaultSet = delMap.getOrDefault(bucketName, new HashSet<>());
            orDefaultSet.add(item.split("/" + bucketName)[1]); // 添加：/userId/folderName/fileName.xxx
            delMap.put(bucketName, orDefaultSet);
        }

        for (Map.Entry<String, Set<String>> item : delMap.entrySet()) {
            // 删除文件服务器的内容
            removeObject(item.getKey(), item.getValue());
        }

        // 删除数据库的内容
        lambdaUpdate().in(SysFileDO::getUrl, dto.getUrlSet()).remove();

        return BaseBizCodeEnum.API_RESULT_OK.getMsg();
    }

    /**
     * 删除指定桶的多个文件对象，返回删除错误的对象列表，全部删除成功，则返回空列表
     */
    @SneakyThrows
    private Iterable<Result<DeleteError>> removeObject(String bucketName, Set<String> objectNameSet) {

        if (bucketExists(bucketName)) {

            List<DeleteObject> objectList = new ArrayList<>();
            for (String item : objectNameSet) {
                objectList.add(new DeleteObject(item));
            }

            return minioClient
                .removeObjects(RemoveObjectsArgs.builder().bucket(bucketName).objects(objectList).build());
        }
        return null;
    }
}
