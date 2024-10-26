package com.soft.base.mapper;

import com.soft.base.entity.SysDept;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soft.base.request.DeleteRequest;
import com.soft.base.vo.DeptTreeVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author cyq
* @description 针对表【sys_dept】的数据库操作Mapper
* @createDate 2024-10-26 09:06:18
* @Entity com.soft.base.entity.SysDept
*/
public interface SysDeptMapper extends BaseMapper<SysDept> {

    String getLevel(@Param("parentId") Long parentId);

    List<DeptTreeVo> getAllDept();

    void deleteDeptBatch(@Param("request") DeleteRequest request);
}




