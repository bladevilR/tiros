package org.jeecg.modules.basemanage.workshop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.basemanage.workshop.entity.BuMtrCompany;

import java.util.List;

/**
 * <p>
 * 运营公司 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-23
 */
public interface BuMtrCompanyMapper extends BaseMapper<BuMtrCompany> {

    /**
     * 获取所有运营公司
     *
     * @return 所有运营公司列表
     */
    List<BuMtrCompany> selectAllList();

}
