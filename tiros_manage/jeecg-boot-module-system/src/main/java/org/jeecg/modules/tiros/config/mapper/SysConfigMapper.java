package org.jeecg.modules.tiros.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.tiros.config.bean.SysConfig;


/**
 * <p>
 * 系统配置 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-11
 */
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    /**
     * 分页查询系统配置项
     *
     * @param page       分页信息
     * @param searchText 编码或名称
     * @return 分页结果
     */
    IPage<SysConfig> selectPageBySearchText(IPage<SysConfig> page, @Param("searchText") String searchText);

}
