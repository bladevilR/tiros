package org.jeecg.modules.quality.exceptiontransfer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.quality.exceptiontransfer.bean.BuWorkExceptionTransfer;
import org.jeecg.modules.quality.exceptiontransfer.bean.vo.BuWorkExceptionTransferQueryVO;

public interface BuWorkExceptionTransferMapper extends BaseMapper<BuWorkExceptionTransfer> {

    IPage<BuWorkExceptionTransfer> selectPageByCondition(IPage<BuWorkExceptionTransfer> page,
                                                         @Param("queryVO") BuWorkExceptionTransferQueryVO queryVO);
}

