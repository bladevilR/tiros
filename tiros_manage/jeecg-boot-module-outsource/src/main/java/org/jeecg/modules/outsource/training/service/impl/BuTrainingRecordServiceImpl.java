package org.jeecg.modules.outsource.training.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.util.UUIDGenerator;
import org.jeecg.modules.outsource.bean.vo.BuContractPayQueryVO;
import org.jeecg.modules.outsource.training.bean.BuTrainingAnnex;
import org.jeecg.modules.outsource.training.bean.BuTrainingRecord;
import org.jeecg.modules.outsource.training.bean.BuTrainingUsers;
import org.jeecg.modules.outsource.training.bean.vo.BuTrainingRecordQueryVO;
import org.jeecg.modules.outsource.training.mapper.BuTrainingAnnexMapper;
import org.jeecg.modules.outsource.training.mapper.BuTrainingRecordMapper;
import org.jeecg.modules.outsource.training.mapper.BuTrainingUsersMapper;
import org.jeecg.modules.outsource.training.service.BuTrainingRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author youGen
 * @since 2021-05-02
 */
@Service
public class BuTrainingRecordServiceImpl extends ServiceImpl<BuTrainingRecordMapper, BuTrainingRecord> implements BuTrainingRecordService {

    @Resource
    private BuTrainingRecordMapper trainingRecordMapper;
    @Resource
    private BuTrainingUsersMapper trainingUsersMapper;
    @Resource
    private BuTrainingAnnexMapper trainingAnnexMapper;

    @Override
    public IPage<BuTrainingRecord> page(BuTrainingRecordQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        IPage<BuTrainingRecord> page = trainingRecordMapper.selectPageByCondition(new Page<>(pageNo, pageSize), queryVO);
        setUserListStr(page);
        return page;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveTrainingRecord(BuTrainingRecord trainingRecord) throws Exception {
        String id = UUIDGenerator.generate();
        trainingRecord.setId(id);
        trainingRecord.insert();
        saveOtherInfo(trainingRecord);
        return true;
    }

    @Override
    public boolean editTrainingRecord(BuTrainingRecord trainingRecord) throws Exception {
        trainingRecord.updateById();
        delOtherInfo(trainingRecord.getId());
        saveOtherInfo(trainingRecord);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean delBatch(List<String> ids) throws Exception {
        ids.forEach(id -> delOtherInfo(id));
        return trainingRecordMapper.deleteBatchIds(ids) > 0;
    }

    @Override
    public IPage<BuTrainingRecord> userPage(BuTrainingRecordQueryVO queryVO, Integer pageNo, Integer pageSize) {
        IPage<BuTrainingRecord> page = trainingRecordMapper.selectUserPageByCondition(new Page<>(pageNo, pageSize), queryVO);
        setUserListStr(page);
        return page;
    }

    private void setUserListStr(IPage<BuTrainingRecord> page) {
        if (CollectionUtils.isNotEmpty(page.getRecords())) {
            page.getRecords().forEach(item -> {
                List<BuTrainingUsers> trainingUsersList = item.getTrainingUsersList();
                if (CollectionUtils.isNotEmpty(trainingUsersList)) {
                    String trainingUserName = trainingUsersList.stream().map(BuTrainingUsers::getRealName).collect(Collectors.toList())
                            .stream().collect(Collectors.joining(","));
                    item.setTrainingUsers(trainingUserName);
                }
            });
        }
    }

    private void saveOtherInfo(BuTrainingRecord trainingRecord) {
        List<BuTrainingUsers> trainingUsersList = trainingRecord.getTrainingUsersList();
        List<BuTrainingAnnex> trainingAnnexList = trainingRecord.getTrainingAnnexList();
        if (CollectionUtils.isNotEmpty(trainingUsersList)) {
            trainingUsersList.forEach(item -> {
                item.setTrainingId(trainingRecord.getId());
                item.insert();
            });
        }
        if (CollectionUtils.isNotEmpty(trainingAnnexList)) {
            trainingAnnexList.forEach(item -> {
                item.setTrainingId(trainingRecord.getId());
                item.insert();
            });
        }
    }

    private void delOtherInfo(String id) {
        trainingAnnexMapper.delete(Wrappers.<BuTrainingAnnex>lambdaUpdate().eq(BuTrainingAnnex::getTrainingId, id));
        trainingUsersMapper.delete(Wrappers.<BuTrainingUsers>lambdaUpdate().eq(BuTrainingUsers::getTrainingId, id));
    }
}
