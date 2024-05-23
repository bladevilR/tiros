package org.jeecg.modules.material.alert.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.material.alert.bean.BuAlertRead;
import org.jeecg.modules.material.borrow.bean.BuMaterialBorrow;
import org.jeecg.modules.material.borrow.bean.vo.BuMaterialBorrowQueryVO;

import java.util.List;

/**
 * <p>
 * 已读预警 Mapper 接口
 * </p>
 *
 * @author yyg
 * @since 2021 -08-19
 */
public interface BuAlertReadMapper extends BaseMapper<BuAlertRead> {
}
