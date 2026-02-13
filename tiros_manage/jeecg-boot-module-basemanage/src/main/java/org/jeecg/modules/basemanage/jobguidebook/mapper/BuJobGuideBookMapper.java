package org.jeecg.modules.basemanage.jobguidebook.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.jobguidebook.bean.BuJobGuideBook;
import org.jeecg.modules.basemanage.jobguidebook.bean.vo.BuJobGuideBookQueryVO;

public interface BuJobGuideBookMapper extends BaseMapper<BuJobGuideBook> {

    IPage<BuJobGuideBook> selectPageBySearchText(IPage<BuJobGuideBook> page, @Param("queryVO") BuJobGuideBookQueryVO queryVO);

    BuJobGuideBook selectJobGuideBookById(@Param("id") String id);
}
