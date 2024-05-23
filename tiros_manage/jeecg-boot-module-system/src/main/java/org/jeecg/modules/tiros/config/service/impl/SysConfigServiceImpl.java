package org.jeecg.modules.tiros.config.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.config.bean.SysConfig;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.util.DateUtils;
import org.jeecg.modules.tiros.config.mapper.SysConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 系统配置 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-11
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Resource
    private SysConfigMapper sysConfigMapper;


    /**
     * @see SysConfigService#pageConfig(String, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<SysConfig> pageConfig(String searchText, Integer pageNo, Integer pageSize) throws Exception {
        return sysConfigMapper.selectPageBySearchText(new Page<>(pageNo, pageSize), searchText);
    }

    /**
     * @see SysConfigService#deleteBatch(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteBatch(String codes) throws Exception {
        List<String> codeList = Arrays.asList(codes.split(","));
        sysConfigMapper.deleteBatchIds(codeList);

        return true;
    }

    /**
     * @see SysConfigService#getScheduleTaskLastTime(String)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public Date getScheduleTaskLastTime(String configCode) {
        if (StringUtils.isBlank(configCode)) {
            throw new JeecgBootException("configCode（主键）不能为空");
        }
        if (configCode.length() > 32) {
            throw new JeecgBootException("configCode（主键）长度不能大于32");
        }

        // 通过系统配置表中的数据，获取上次更新时间，做增量更新
        // code是硬编码
        Date date;
        SysConfig sysConfig = sysConfigMapper.selectById(configCode);
        if (null != sysConfig) {
            String time = sysConfig.getConfigValue();
            try {
                date = DateUtils.datetimeFormat.get().parse(time);
            } catch (ParseException e) {
                date = getLongAgoTime();
            }
        } else {
            date = getLongAgoTime();
        }

        return date;
    }

    /**
     * @see SysConfigService#updateScheduleTaskLastTime(String, String, Date)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateScheduleTaskLastTime(String code, String name, Date now) {
        // 删除记录
        sysConfigMapper.deleteById(code);

        // 新增记录
        SysConfig config = new SysConfig()
                .setConfigCode(code)
                .setConfigValue(DateUtils.datetimeFormat.get().format(now))
                .setConfigName(name)
                .setConfigRemark("上次执行时间，用于增量生成/更新数据");
        sysConfigMapper.insert(config);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateScheduleTaskLastDays(String configCode, String days) {
        SysConfig sysConfig = new SysConfig().setConfigCode(configCode)
                .setConfigValue(days);
        sysConfigMapper.update(sysConfig, Wrappers.<SysConfig>lambdaUpdate().eq(SysConfig::getConfigCode, configCode));
    }

    @Override
    public String getConfigValueByCode(String configCode) {
        if (StringUtils.isBlank(configCode)) {
            throw new JeecgBootException("configCode（主键）不能为空");
        }
        if (configCode.length() > 32) {
            throw new JeecgBootException("configCode（主键）长度不能大于32");
        }

        // 通过系统配置表中的数据，获取上次更新时间，做增量更新
        // code是硬编码

        SysConfig sysConfig = sysConfigMapper.selectById(configCode);
        if (null != sysConfig) {
            return sysConfig.getConfigValue();
        }
        return "0";
    }


    private Date getLongAgoTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 1900);
        return calendar.getTime();
    }

}
