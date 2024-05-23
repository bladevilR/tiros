package org.jeecg.modules.material.qrcode.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.tiros.task.service.QrcodeCleanTaskService;
import org.jeecg.common.util.MinioUtil;
import org.jeecg.modules.material.qrcode.bean.BuMaterialQrcode;
import org.jeecg.modules.material.qrcode.mapper.BuMaterialQrcodeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 清理二维码定时任务 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
@Service
public class QrcodeCleanTaskServiceImpl implements QrcodeCleanTaskService {

    @Resource
    private BuMaterialQrcodeMapper buMaterialQrcodeMapper;


    /**
     * @see QrcodeCleanTaskService#clearQrcode()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean clearQrcode() throws RuntimeException {
        List<BuMaterialQrcode> qrcodeList = buMaterialQrcodeMapper.selectListObjNotExist();

        if (CollectionUtils.isNotEmpty(qrcodeList)) {
            for (BuMaterialQrcode qrcode : qrcodeList) {
                String fileName = qrcode.getQrcodeImgUrl().substring(qrcode.getQrcodeImgUrl().lastIndexOf("/") + 1);
                MinioUtil.removeObject(MinioUtil.getBucketName(), fileName);
                qrcode.deleteById();
            }
        }

        return true;
    }
}
