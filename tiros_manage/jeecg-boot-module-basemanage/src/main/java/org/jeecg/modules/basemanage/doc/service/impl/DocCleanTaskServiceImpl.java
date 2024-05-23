package org.jeecg.modules.basemanage.doc.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.tiros.task.service.DocCleanTaskService;
import org.jeecg.common.util.MinioUtil;
import org.jeecg.modules.basemanage.doc.bean.BuDocFile;
import org.jeecg.modules.basemanage.doc.mapper.BuDocFileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * <p>
 * 文件清理定时任务 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-25
 */
@Service
public class DocCleanTaskServiceImpl implements DocCleanTaskService {

    @Resource
    private BuDocFileMapper fileMapper;


    /**
     * @see DocCleanTaskService#cleanDoc()
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean cleanDoc() throws RuntimeException {
        List<BuDocFile> fileList = fileMapper.selectList(Wrappers.<BuDocFile>lambdaQuery().eq(BuDocFile::getStatus, 0));
        if (CollectionUtils.isNotEmpty(fileList)) {
            for (BuDocFile file : fileList) {
                if (file.getUpdateTime() != null) {
                    String savePath = file.getSavepath();
                    if (StrUtil.isNotEmpty(savePath)) {
                        String fileName = savePath.substring(savePath.lastIndexOf("/") + 1);
                        LocalDate today = LocalDate.now();
                        LocalDate updateTime = file.getUpdateTime().toInstant()
                                .atZone(ZoneId.systemDefault()).toLocalDate();
                        long daysDiff = ChronoUnit.DAYS.between(updateTime, today);
                        if (daysDiff > 100) {
                            MinioUtil.removeObject(MinioUtil.getBucketName(), fileName);
                        }
                    }
                }
            }
        }

        return true;
    }
}
