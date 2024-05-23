package org.jeecg.modules.basemanage.techbook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.techbook.bean.BuRepairTechBook;
import org.jeecg.modules.basemanage.techbook.bean.vo.BuRepairTechBookQueryVO;

/**
 * <p>
 * 作业指导书(工艺) Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-27
 */
public interface BuRepairTechBookMapper extends BaseMapper<BuRepairTechBook> {

    /**
     * 根据条件分页查询作业指导书
     *
     * @param page       分页信息
     * @param queryVO 搜索条件
     * @return 分页结果
     */
    IPage<BuRepairTechBook> selectPageBySearchText(IPage<BuRepairTechBook> page, @Param("queryVO") BuRepairTechBookQueryVO queryVO);

    /**
     * 根据id查询作业指导书
     *
     * @param id 作业指导书id
     * @return 作业指导书
     */
    BuRepairTechBook selectTechBookById(@Param("id") String id);

}
