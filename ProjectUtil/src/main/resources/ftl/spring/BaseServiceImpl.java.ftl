package generate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import generate.mapper.${tableNameCamelCaseUpperFirst}Mapper;
import generate.model.entity.${tableNameCamelCaseUpperFirst}DO;
import generate.service.${tableNameCamelCaseUpperFirst}Service;

@Service
public class ${tableNameCamelCaseUpperFirst}ServiceImpl
    extends ServiceImpl<${tableNameCamelCaseUpperFirst}Mapper, ${tableNameCamelCaseUpperFirst}DO>
    implements ${tableNameCamelCaseUpperFirst}Service {

}
