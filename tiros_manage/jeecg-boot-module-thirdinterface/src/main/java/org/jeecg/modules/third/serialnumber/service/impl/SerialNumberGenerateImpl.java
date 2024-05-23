package org.jeecg.modules.third.serialnumber.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jeecg.modules.third.serialnumber.bean.SysSerialNumber;
import org.jeecg.modules.third.serialnumber.mapper.SysSerialNumberMapper;
import org.jeecg.modules.third.serialnumber.service.SerialNumberGenerate;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-15
 */
@Slf4j
@Service
public class SerialNumberGenerateImpl extends ServiceImpl<SysSerialNumberMapper, SysSerialNumber> implements SerialNumberGenerate {

    @Resource
    private SysSerialNumberMapper sysSerialNumberMapper;

    /**
     * 生成器锁
     */
    private final ReentrantLock lock = new ReentrantLock();
    /**
     * 预生成锁
     */
    private final ReentrantLock prepareLock = new ReentrantLock();
    /**
     * 预生成数量
     */
    private int prepareBatchSize = 0;
    /**
     * 前缀
     */
    private String prefix;
    /**
     * 是否使用日期作为中缀
     */
    private boolean infixDate = true;
    /**
     * 尾缀最小值
     */
    private final long MIN_SUFFIX = 0L;
    /**
     * 当前尾缀值
     */
    private long currentSuffix = MIN_SUFFIX;
    /**
     * 尾缀长度
     */
    private int suffixLength;
    /**
     * 尾缀最大值
     */
    private long suffixMaxValue = 0;
    /**
     * 尾缀是否用0填充
     */
    private boolean suffixFillZero = true;
    /**
     * 流水号对象
     */
    private SysSerialNumber serialNumber = new SysSerialNumber();
    /**
     * 预生成流水号
     */
    private final HashMap<String, List<String>> PREPARE_SERIAL_NUMBER_MAP = new HashMap<>();


    /**
     * 根据模块code生成预数量的序列号存放到Map中
     */
    @CachePut(value = "serialNumber", key = "#key")
    public List<String> generatePrepareSerialNumbers(String key, String currentDate) {
        // 临时List变量
        List<String> resultList = new ArrayList<>(prepareBatchSize);
        lock.lock();
        try {
            for (int i = 0; i < prepareBatchSize; i++) {
                suffixMaxValue = suffixMaxValue + 1;
                if (suffixMaxValue > MIN_SUFFIX && String.valueOf(suffixMaxValue).length() < suffixLength) {
                    currentSuffix = suffixMaxValue;
                } else {
                    // 如果尾缀动态数字长度大于设置的尾缀长度 例如：currentSuffix=9999，suffixLength=3
                    currentSuffix = suffixMaxValue = 0;
                    // 更新数据，重置当前尾缀的最大值为0，从0开始再次循环
                    serialNumber.setCurrentSuffixMaxValue("0");
                    sysSerialNumberMapper.updateById(serialNumber);
                }

                StringBuilder serialNumberBuilder = new StringBuilder();
                // 前缀
                serialNumberBuilder.append(prefix);
                // 中缀
                if (infixDate) {
                    serialNumberBuilder.append(currentDate);
                }
                // 尾缀
                if (suffixFillZero) {
                    serialNumberBuilder.append(String.format("%0" + suffixLength + "d", currentSuffix));
                } else {
                    serialNumberBuilder.append(currentSuffix);
                }

                resultList.add(serialNumberBuilder.toString());
            }
            // 更新数据
            serialNumber.setCurrentSuffixMaxValue(String.valueOf(currentSuffix));
            sysSerialNumberMapper.updateById(serialNumber);
        } finally {
            lock.unlock();
        }
        return resultList;
    }

    @Override
    public String generateSerialNumberByCode(String moduleCode) {
        LambdaQueryWrapper<SysSerialNumber> serialNumberWrapper = new LambdaQueryWrapper<>();
        serialNumberWrapper.eq(SysSerialNumber::getModuleCode, moduleCode);
        serialNumber = sysSerialNumberMapper.selectOne(serialNumberWrapper);

        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = moduleCode;
        // 以日期做中缀
        infixDate = serialNumber.getInfixDate() == 1;
        if (infixDate) {
            key = key + "-" + currentDate;
        }

        // 预序列号加锁
        prepareLock.lock();
        try {
            // 判断内存中是否还有序列号
            if (null != PREPARE_SERIAL_NUMBER_MAP.get(key) && PREPARE_SERIAL_NUMBER_MAP.get(key).size() > 0) {
                // 若有，返回第一个，并删除
                return PREPARE_SERIAL_NUMBER_MAP.get(key).remove(0);
            }
        } finally {
            // 预序列号解锁
            prepareLock.unlock();
        }

        // 清理尾缀值
        clearSuffix();
        // 预生成流水号数量
        prepareBatchSize = serialNumber.getPrepareBatchSize();
        // 前缀
        prefix = StringUtils.isBlank(serialNumber.getPrefix()) ? serialNumber.getModuleCode() : serialNumber.getPrefix();
        // 尾缀长度
        suffixLength = serialNumber.getSuffixLength();
        // 当前尾缀的最大值
        String currentSuffixMaxValue = serialNumber.getCurrentSuffixMaxValue();
        if (StringUtils.isNotBlank(currentSuffixMaxValue)) {
            suffixMaxValue = Long.parseLong(currentSuffixMaxValue.trim());
        }
        // 尾缀是否用0填充
        suffixFillZero = serialNumber.getSuffixFillZero() == 1;

        // 生成预序列号，存到缓存中
        List<String> resultList = generatePrepareSerialNumbers(key, currentDate);
        prepareLock.lock();
        try {
            PREPARE_SERIAL_NUMBER_MAP.put(key, resultList);
            return PREPARE_SERIAL_NUMBER_MAP.get(key).remove(0);
        } finally {
            prepareLock.unlock();
        }
    }

    private void clearSuffix() {
        Integer infixDate = serialNumber.getInfixDate();
        if (null == infixDate || infixDate == 0) {
            return;
        }

        Integer clearSuffixEveryDay = serialNumber.getClearSuffixEveryDay();
        if (null == clearSuffixEveryDay || clearSuffixEveryDay == 0) {
            return;
        }

        Date clearSuffixLastDay = serialNumber.getClearSuffixLastDay();
        Date now = new Date();
        if (null != clearSuffixLastDay && DateUtils.isSameDay(clearSuffixLastDay, now)) {
            return;
        }

        serialNumber.setCurrentSuffixMaxValue("0");
        serialNumber.setClearSuffixLastDay(now);
    }

}
