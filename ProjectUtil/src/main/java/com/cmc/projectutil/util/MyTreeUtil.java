package com.cmc.projectutil.util;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ReflectUtil;
import com.cmc.projectutil.model.entity.BaseEntityFour;
import lombok.SneakyThrows;

import java.util.*;
import java.util.stream.Collectors;

public class MyTreeUtil {

    /**
     * 比原始的递归快
     * 原理：运用了对象地址引用原理
     * childrenFlag: 【true】 children 一直为集合 【false】 有子节点时，children为集合，无子节点时，children 为 null
     */
    @SneakyThrows
    public static <T extends BaseEntityFour<T>> List<T> listToTree(List<T> list, boolean childrenFlag) {

        HashMap<Long, T> listMap = MapUtil.newHashMap(); // 把 list的所有元素转换为：id -> 元素，格式
        List<T> resultList = new ArrayList<>(); // 返回值

        for (T item : list) {

            T mapDTO = listMap.get(item.getId());

            if (mapDTO == null) {
                mapDTO = item;
                if (childrenFlag) {
                    mapDTO.setChildren(new ArrayList<>()); // children 一直为集合
                } else {
                    mapDTO.setChildren(null); // 无子节点时，children 为 null
                }
                listMap.put(item.getId(), mapDTO);
            } else {
                // 如果存在，则只有 children属性，并 补全其他属性
                BeanUtil.copyProperties(item, mapDTO, "children");
            }

            if (mapDTO.getParentId() == 0) {
                resultList.add(mapDTO);
                continue;
            }

            T parentDTO = listMap.get(mapDTO.getParentId());

            if (parentDTO == null) {
                parentDTO = (T)ReflectUtil.newInstance(item.getClass());
                ArrayList<T> children = new ArrayList<>();
                children.add(mapDTO);
                parentDTO.setChildren(children); // 给父节点设置 children属性
                listMap.put(mapDTO.getParentId(), parentDTO);
            } else {
                List<T> children = parentDTO.getChildren();
                if (children == null) {
                    children = new ArrayList<>();
                    children.add(mapDTO);
                    parentDTO.setChildren(children); // 给父节点设置 children属性
                } else {
                    children.add(mapDTO);
                }
            }
        }

        return resultList;
    }

    /**
     * 根据底级节点 list，逆向生成整棵树，有子节点时，children才是集合
     */
    public static <T extends BaseEntityFour<T>> List<T> getFullTreeByDeepNode(List<T> deepNodeList, List<T> allList) {
        return listToTree(getFullTreeList(deepNodeList, allList), false);
    }

    /**
     * 根据底级节点 list，逆向生成整棵树 list
     *
     * @param allList      所有的list
     * @param deepNodeList 底级节点list
     */
    public static <T extends BaseEntityFour<T>> List<T> getFullTreeList(List<T> deepNodeList, List<T> allList) {

        List<T> resultList = new ArrayList<>(deepNodeList); // 先添加底级节点

        Set<Long> parentIdSet = deepNodeList.stream().map(BaseEntityFour::getParentId).collect(Collectors.toSet());

        Map<Long, T> allMap = allList.stream().collect(Collectors.toMap(BaseEntityFour::getId, o -> o));

        for (Long item : parentIdSet) {
            getFullTreeListHandle(allMap, resultList, item); // 添加父级节点
        }

        return resultList;
    }

    private static <T extends BaseEntityFour<T>> void getFullTreeListHandle(Map<Long, T> allMap, List<T> resultList,
        Long parentId) {
        if (parentId == 0) {
            return;
        }

        T item = allMap.get(parentId); // 找到父节点

        long count = resultList.stream().filter(it -> item.getId().equals(it.getId())).count();
        if (count != 0) {
            // 如果父节点已经添加，则结束方法
            return;
        }

        resultList.add(item); // 添加父节点

        getFullTreeListHandle(allMap, resultList, item.getParentId());
    }

}
