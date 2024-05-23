package org.jeecg.common.system.api;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author yuyougen
 * @title: QualityTransforms
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/1114:26
 */
public interface QualityTransformationAPI {
    /**
     * 整改项转换
     *
     *
     * @return
     */
    void  rectifyTransformation(Object obj);
    /**
     * 开口项转换
     *
     *
     * @return
     */
    void  leaveRecordTransformation(Object obj);


    /**
     * 删除质量管理中的开口项或者整改
     *
     *
     * @return
     */
    void deleteTransformationBatch(List<String> ids);
}
