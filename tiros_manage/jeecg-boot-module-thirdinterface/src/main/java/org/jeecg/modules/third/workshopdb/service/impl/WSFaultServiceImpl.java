package org.jeecg.modules.third.workshopdb.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.third.dynamic.DataSource;
import org.jeecg.modules.third.dynamic.DataSourceEnum;
import org.jeecg.modules.third.workshopdb.bean.WSFault;
import org.jeecg.modules.third.workshopdb.mapper.WSFaultMapper;
import org.jeecg.modules.third.workshopdb.service.WSFaultService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车间故障 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2023-03-23
 */
@Slf4j
@Service
public class WSFaultServiceImpl implements WSFaultService {

    @Resource
    private WSFaultMapper wsFaultMapper;


    /**
     * @see WSFaultService#countAll(Map)
     */
    @DataSource(DataSourceEnum.workshopdb)
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public String countAll(Map<String, String> lineNameIdMap) {
        List<String> lineNameList = new ArrayList<>(lineNameIdMap.keySet());
        Long count = wsFaultMapper.countAll(lineNameList);
        return String.format("当前车间数据库故障存在%s条。", count);
    }

    /**
     * @see WSFaultService#selectAllList(Map)
     */
    @DataSource(DataSourceEnum.workshopdb)
//    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<WSFault> selectAllList(Map<String, String> lineNameIdMap) {
        List<String> lineNameList = new ArrayList<>(lineNameIdMap.keySet());
        return wsFaultMapper.selectAllList(lineNameList);
    }

}
