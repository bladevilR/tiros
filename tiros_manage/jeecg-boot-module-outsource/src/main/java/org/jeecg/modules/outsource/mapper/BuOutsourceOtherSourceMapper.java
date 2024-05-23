package org.jeecg.modules.outsource.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.outsource.bean.BuOutsourceOtherSource;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceResourceQueryVO;

/**
 * <p>
 * 委外文档资料，设备质量保证书、测试报告、监修日志、过程记录、质量报告 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
public interface BuOutsourceOtherSourceMapper extends BaseMapper<BuOutsourceOtherSource> {
    /**
     * 分页
     *
     * @param page
     * @param resource
     * @return
     */
    IPage<BuOutsourceOtherSource> page(Page<Object> page, @Param("resource") BuOutsourceResourceQueryVO resource);
}
