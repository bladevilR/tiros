package org.jeecg.modules.report.excel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.report.excel.bean.BuRptExcelTemplate;

/**
 * <p>
 * 报表中心 表格模板 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-08
 */
public interface BuRptExcelTemplateMapper extends BaseMapper<BuRptExcelTemplate> {

    /**
     * 根据条件分页查询表格模板
     *
     * @param page       分页信息
     * @param searchText 搜索条件
     * @return 分页结果
     */
    IPage<BuRptExcelTemplate> selectPageBySearchText(IPage<BuRptExcelTemplate> page, @Param("searchText") String searchText);

    /**
     * 根据编码查询表格模板信息
     *
     * @param tempCode 编码
     * @return 表格模板信息
     */
    BuRptExcelTemplate selectByTempCode(@Param("tempCode") String tempCode);

}
