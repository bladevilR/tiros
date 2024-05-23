package org.jeecg.modules.basemanage.doc.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.doc.bean.BuDocDirectory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 目录信息
 * 1 顶级目录(默认创建)：个人文件、共享文件、班组文件、工艺文件，通过id查找区别，分别对应1、2 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020 -08-17
 */
public interface BuDocDirectoryMapper extends BaseMapper<BuDocDirectory> {

    /**
     * 树结构
     *
     * @param id the id
     * @return list
     */
    List<BuDocDirectory> listDirectoryTree(@Param("id")String id);

    /**
     * List directory tree tech list.
     *
     * @return the list
     */
    List<BuDocDirectory> listDirectoryTreeTech();

    /**
     * Select shared directory list.
     *
     * @return the list
     */
    List<BuDocDirectory> selectSharedDirectory(@Param("id")String id);
}
