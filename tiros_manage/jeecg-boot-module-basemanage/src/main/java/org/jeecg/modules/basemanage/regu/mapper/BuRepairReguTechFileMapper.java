package org.jeecg.modules.basemanage.regu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.regu.bean.BuRepairReguTechFile;

import java.util.List;

/**
 * <p>
 * 规程工艺文件,作业分类关联的文件会被下面的作业项继承 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-29
 */
public interface BuRepairReguTechFileMapper extends BaseMapper<BuRepairReguTechFile> {

    /**
     * 批量插入
     *
     * @param list 规程工艺文件列表
     */
    void insertList(@Param("list") List<BuRepairReguTechFile> list);

}
