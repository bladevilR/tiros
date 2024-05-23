package org.jeecg.modules.outsource.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.outsource.bean.BuOutsourceResource;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.outsource.bean.vo.BuOutsourceResourceQueryVO;

/**
 * <p>
 * 委外过程资料 Mapper 接口
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
public interface BuOutsourceResourceMapper extends BaseMapper<BuOutsourceResource> {
    /**
     * 分页
     * @param page
     * @param resource
     * @return
     */
    IPage<BuOutsourceResource> resourcePage(Page<Object> page, BuOutsourceResourceQueryVO resource);
}
