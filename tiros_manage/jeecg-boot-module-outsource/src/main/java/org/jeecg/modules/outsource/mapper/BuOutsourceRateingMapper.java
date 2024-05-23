package org.jeecg.modules.outsource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.outsource.bean.BuOutsourceRateing;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceRateingQueryVO;

import java.util.List;

/**
 * <p>
 * 委外评分 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
public interface BuOutsourceRateingMapper extends BaseMapper<BuOutsourceRateing> {
    /**
     * 分页
     *
     * @param page
     * @param rateing
     * @return
     */
    IPage<BuOutsourceRateing> page(Page<Object> page, BuOutsourceRateingQueryVO rateing);

    /**
     * 根据厂商id获取厂商评分列表
     *
     * @param supplierId 厂商id
     * @return 评分列表
     */
    List<BuOutsourceRateing> selectListBySupplierId(@Param("supplierId") String supplierId);


}
