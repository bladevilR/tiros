package org.jeecg.modules.tiros.dataisolation.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.tiros.dataisolation.bean.LineWorkshopCompany;
import org.jeecg.common.tiros.dataisolation.service.LineWorkshopCompanyService;
import org.jeecg.modules.basemanage.line.entity.BuMtrLine;
import org.jeecg.modules.basemanage.line.mapper.BuMtrLineMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 查询线路车间公司信息 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2022/8/26
 */
@Slf4j
@Service
public class LineWorkshopCompanyServiceImpl implements LineWorkshopCompanyService {

    @Resource
    private BuMtrLineMapper buMtrLineMapper;


    /**
     * @see LineWorkshopCompanyService#mapLineWorkshopCompany()
     */
    @Override
    public Map<String, LineWorkshopCompany> mapLineWorkshopCompany() {
        Map<String, LineWorkshopCompany> lineWorkshopCompanyMap = new HashMap<>();

        List<BuMtrLine> lineList = buMtrLineMapper.selectAllList();
        for (BuMtrLine line : lineList) {
            String lineId = line.getLineId();
            String workshopId = line.getWorkshopId();
            String companyId = line.getCompanyId();
            String depotId = line.getDepotId();

            LineWorkshopCompany lineWorkshopCompany = new LineWorkshopCompany()
                    .setLineId(lineId)
                    .setWorkshopId(workshopId)
                    .setCompanyId(companyId)
                    .setDepotId(depotId);
            lineWorkshopCompanyMap.put(lineId, lineWorkshopCompany);
        }

        return lineWorkshopCompanyMap;
    }

    /**
     * @see LineWorkshopCompanyService#mapLineWorkshopCompany()
     */
    @Override
    public Map<String, Map<String, Set<String>>> treeCompanyWorkshopLine() {
        Map<String, Map<String, Set<String>>> companyMap = new HashMap<>();

        List<BuMtrLine> lineList = buMtrLineMapper.selectAllList();
        for (BuMtrLine line : lineList) {
            String lineId = line.getLineId();
            String workshopId = line.getWorkshopId();
            String companyId = line.getCompanyId();

            Map<String, Set<String>> workshopMap = companyMap.getOrDefault(companyId, new HashMap<>());
            Set<String> lineSet = workshopMap.getOrDefault(workshopId, new HashSet<>());
            lineSet.add(lineId);
            workshopMap.put(workshopId, lineSet);
            companyMap.put(companyId, workshopMap);
        }

        return companyMap;
    }

}
