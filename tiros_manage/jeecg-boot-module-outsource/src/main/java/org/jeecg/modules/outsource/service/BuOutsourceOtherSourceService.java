package org.jeecg.modules.outsource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.outsource.bean.BuOutsourceOtherSource;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceResourceQueryVO;

import java.util.List;

/**
 * <p>
 * 委外文档资料，设备质量保证书、测试报告、监修日志、过程记录、质量报告 服务类
 * </p>
 *
 * @author youGen
 * @since 2020 -10-16
 */
public interface BuOutsourceOtherSourceService extends IService<BuOutsourceOtherSource> {
    /**
     * 分页
     *
     * @param queryVO  the query vo
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return page
     */
    IPage<BuOutsourceOtherSource> page(BuOutsourceResourceQueryVO queryVO, Integer pageNo, Integer pageSize);

    /**
     * Save batch other source boolean.
     *
     * @param otherSource the other source
     * @return the boolean
     */
    Boolean saveBatchOtherSource(List<BuOutsourceOtherSource> otherSource);
}
