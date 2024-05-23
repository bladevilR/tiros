package org.jeecg.modules.tiros.qualityTransformation;

import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.jeecg.common.system.api.QualityTransformationAPI;
import org.jeecg.common.tiros.serialnumber.SerialNumberGenerate;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchang;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangLeave;
import org.jeecg.modules.dispatch.exchange.bean.BuRepairExchangRectify;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangLeaveMapper;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangMapper;
import org.jeecg.modules.dispatch.exchange.mapper.BuRepairExchangRectifyMapper;
import org.jeecg.modules.quality.leaverecord.bean.BuWorkLeaveRecord;
import org.jeecg.modules.quality.leaverecord.mapper.BuWorkLeaveRecordMapper;
import org.jeecg.modules.quality.rectify.bean.BuWorkRectify;
import org.jeecg.modules.quality.rectify.mapper.BuWorkRectifyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yuyougen
 * @title: QualityTransformationImpl
 * @projectName tiros_manage
 * @description: TODO
 * @date 2021/5/1114:34
 */
@Service
public class QualityTransformationImpl implements QualityTransformationAPI {

    @Resource
    private BuRepairExchangMapper repairExchangMapper;
    @Resource
    private BuWorkRectifyMapper rectifyMapper;
    @Resource
    private BuWorkLeaveRecordMapper leaveRecordMapper;
    @Resource
    private BuRepairExchangLeaveMapper buRepairExchangLeaveMapper;
    @Resource
    private BuRepairExchangRectifyMapper buRepairExchangRectifyMapper;
    @Resource
    private SerialNumberGenerate serialNumberGenerate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rectifyTransformation(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        BuRepairExchangRectify rectify = objectMapper.convertValue(obj, BuRepairExchangRectify.class);
        BuWorkRectify workRectify = new BuWorkRectify()
                .setId(rectify.getId())
                .setRectifyNo(RandomUtil.randomString(8))
                .setTitle(rectify.getChangeName())
                .setRectifyType(4)
                .setGroupId(rectify.getDept())
                .setDutyUserId(rectify.getDutyMan())
                .setCheckDate(rectify.getCheckDate())
                .setSendDate(new Date())
                .setChangeResult(rectify.getChangeResult())
                .setFormType(2);
        BuRepairExchang exchang = repairExchangMapper.selectById(rectify.getExchangeId());
        if (exchang != null) {
            workRectify.setLineId(exchang.getLineId());
            workRectify.setTrainNo(exchang.getTrainNo());
        }
        workRectify.insert();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void leaveRecordTransformation(Object obj) {
        ObjectMapper objectMapper = new ObjectMapper();
        BuRepairExchangLeave leave = objectMapper.convertValue(obj, BuRepairExchangLeave.class);
        String serialNumber = serialNumberGenerate.generateSerialNumberByCode("JDXKKX");
        BuWorkLeaveRecord leaveRecord = new BuWorkLeaveRecord()
                .setId(leave.getId())
                .setRecordCode(serialNumber)
                .setRecordDesc(leave.getLeaveContent())
                .setRecordName(leave.getOperation())
                .setStatus(0);
        BuRepairExchang exchang = repairExchangMapper.selectById(leave.getExchangeId());
        if (exchang != null) {
            leaveRecord.setLineId(exchang.getLineId());
            leaveRecord.setTrainNo(exchang.getTrainNo());
        }
        leaveRecord.insert();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTransformationBatch(List<String> ids) {
        new Thread(() -> {
            List<BuRepairExchangLeave> repairExchangLeaves = buRepairExchangLeaveMapper.selectList(Wrappers.<BuRepairExchangLeave>lambdaQuery().select(BuRepairExchangLeave::getId)
                    .in(BuRepairExchangLeave::getExchangeId, ids));
            if (CollectionUtils.isNotEmpty(repairExchangLeaves)) {
                List<String> leaveIds = repairExchangLeaves.stream().map(BuRepairExchangLeave::getId).collect(Collectors.toList());
                leaveRecordMapper.deleteBatchIds(leaveIds);
            }
        }).start();
        new Thread(() -> {
            List<BuRepairExchangRectify> rectifyList = buRepairExchangRectifyMapper.selectList(Wrappers.<BuRepairExchangRectify>lambdaQuery().select(BuRepairExchangRectify::getId)
                    .in(BuRepairExchangRectify::getExchangeId, ids));
            if (CollectionUtils.isNotEmpty(rectifyList)) {
                List<String> rectifyIds = rectifyList.stream().map(BuRepairExchangRectify::getId).collect(Collectors.toList());
                rectifyMapper.deleteBatchIds(rectifyIds);
            }
        }).start();
    }
}

