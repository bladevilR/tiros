package org.jeecg.modules.outsource.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.outsource.bean.BuOutsourceResource;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceResourceQueryVO;

import java.util.List;

/**
 * <p>
 * 委外过程资料 服务类
 * </p>
 *
 * @author youGen
 * @since 2020 -10-16
 */
public interface BuOutsourceResourceService extends IService<BuOutsourceResource> {
    /**
     * 分页查询
     *
     * @param queryVO  the query vo
     * @param pageNo   the page no
     * @param pageSize the page size
     * @return page page
     */
    IPage<BuOutsourceResource> resourcePage(BuOutsourceResourceQueryVO queryVO, Integer pageNo, Integer pageSize);

    /**
     * Remove batch boolean.
     *
     * @param ids the ids
     * @return the boolean
     * @throws Exception the exception
     */
    boolean removeBatch(List<String> ids) throws Exception;

    /**
     * Save batch resource boolean.
     *
     * @param outsourceResources the outsource resources
     * @return the boolean
     */
    boolean saveBatchResource(List<BuOutsourceResource> outsourceResources);
}
