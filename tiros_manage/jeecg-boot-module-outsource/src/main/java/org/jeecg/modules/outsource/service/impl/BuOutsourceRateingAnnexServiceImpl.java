package org.jeecg.modules.outsource.service.impl;

import org.jeecg.modules.outsource.bean.BuOutsourceRateingAnnex;
import org.jeecg.modules.outsource.mapper.BuOutsourceRateingAnnexMapper;
import org.jeecg.modules.outsource.service.BuOutsourceRateingAnnexService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 * 评分附件 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2020-10-16
 */
@Service
public class BuOutsourceRateingAnnexServiceImpl extends ServiceImpl<BuOutsourceRateingAnnexMapper, BuOutsourceRateingAnnex> implements BuOutsourceRateingAnnexService {
    @Resource
    private BuOutsourceRateingAnnexMapper annexMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBatch(List<String> ids) throws Exception {
        ids.forEach(id -> {
            BuOutsourceRateingAnnex annex = annexMapper.selectById(id);
            if (annex != null) {
                annexMapper.deleteFileById(annex.getFileId());
            }
        });
        annexMapper.deleteBatchIds(ids);
        return true;
    }
}
