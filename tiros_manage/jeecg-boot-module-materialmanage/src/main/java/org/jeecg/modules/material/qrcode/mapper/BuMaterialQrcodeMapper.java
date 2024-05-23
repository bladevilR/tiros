package org.jeecg.modules.material.qrcode.mapper;

import org.jeecg.modules.material.qrcode.bean.BuMaterialQrcode;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 * 查询时，自动生成该表中不存在的对应数据以及二维码 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-10
 */
public interface BuMaterialQrcodeMapper extends BaseMapper<BuMaterialQrcode> {

    /**
     * 查找二维码对象是否还存在
     *
     * @return the list
     */
    List<BuMaterialQrcode> selectListObjNotExist();

}
