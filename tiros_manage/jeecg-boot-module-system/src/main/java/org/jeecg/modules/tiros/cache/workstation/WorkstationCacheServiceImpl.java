package org.jeecg.modules.tiros.cache.workstation;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.cache.workstation.WorkstationBO;
import org.jeecg.common.tiros.cache.workstation.WorkstationCacheService;
import org.jeecg.common.util.MapSizeUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.board.workstation.bean.BuMtrWorkstation;
import org.jeecg.modules.board.workstation.mapper.BuMtrWorkstationBoardMapper;
import org.jeecg.modules.group.information.bean.BuGroupWorkstation;
import org.jeecg.modules.group.information.mapper.BuGroupWorkstationMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 工位 缓存服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2023-07-31
 */
@Service
public class WorkstationCacheServiceImpl implements WorkstationCacheService {

    @Resource
    private BuGroupWorkstationMapper buGroupWorkstationMapper;
    @Resource
    private BuMtrWorkstationBoardMapper buMtrWorkstationBoardMapper;
    @Resource
    private RedisUtil redisUtil;


    /**
     * @see WorkstationCacheService#map(String)
     */
    @Override
    public Map<String, WorkstationBO> map(String workshopId) {
        if (StringUtils.isBlank(workshopId)) {
            throw new JeecgBootException("使用工位缓存请指定车间id");
        }

        Map<String, Map<String, WorkstationBO>> workshopIdWorkstationMap = new HashMap<>();

        Object cacheObject = redisUtil.get(CacheConstant.WORKSTATION_CACHE_ALL);
        if (null != cacheObject) {
            if (cacheObject instanceof String) {
                String jsonString = (String) cacheObject;
                workshopIdWorkstationMap = JSON.parseObject(jsonString, new TypeReference<Map<String, Map<String, WorkstationBO>>>() {
                });
            }
        } else {
            workshopIdWorkstationMap = update();
        }

        return workshopIdWorkstationMap.getOrDefault(workshopId, new HashMap<>());
    }

    /**
     * @see WorkstationCacheService#mapKeyNo(String)
     */
    @Override
    public Map<String, WorkstationBO> mapKeyNo(String workshopId) {
        Map<String, WorkstationBO> workstationIdBOMap = map(workshopId);
        if (workstationIdBOMap.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, WorkstationBO> workstationNoBOMap = new HashMap<>();
        for (WorkstationBO workstationBO : workstationIdBOMap.values()) {
            workstationNoBOMap.put(workstationBO.getWorkstationNo(), workstationBO);
        }
        return workstationNoBOMap;
    }

    /**
     * @see WorkstationCacheService#mapKeyName(String)
     */
    @Override
    public Map<String, WorkstationBO> mapKeyName(String workshopId) {
        Map<String, WorkstationBO> workstationIdBOMap = map(workshopId);
        if (workstationIdBOMap.isEmpty()) {
            return new HashMap<>();
        }

        Map<String, WorkstationBO> workstationNameBOMap = new HashMap<>();
        for (WorkstationBO workstationBO : workstationIdBOMap.values()) {
            workstationNameBOMap.put(workstationBO.getWorkstationName(), workstationBO);
        }
        return workstationNameBOMap;
    }

    /**
     * @see WorkstationCacheService#update()
     */
    @Override
    public Map<String, Map<String, WorkstationBO>> update() {
        // 查询工位
        List<BuMtrWorkstation> workstationList = buMtrWorkstationBoardMapper.selectList(Wrappers.emptyWrapper());
        List<BuGroupWorkstation> groupWorkstationList = buGroupWorkstationMapper.selectList(Wrappers.emptyWrapper());

        List<WorkstationBO> workstationBOList = new ArrayList<>();
        for (BuMtrWorkstation workstation : workstationList) {
            String groupId = groupWorkstationList.stream()
                    .filter(item -> workstation.getId().equals(item.getWorkstationId()))
                    .findFirst()
                    .orElse(new BuGroupWorkstation())
                    .getGroupId();

            WorkstationBO workstationBO = new WorkstationBO()
                    .setId(workstation.getId())
                    .setWorkstationNo(workstation.getStationNo())
                    .setWorkstationName(workstation.getName())
                    .setWorkshopId(workstation.getWorkshopId())
                    .setGroupId(groupId)
                    .setCompanyId(workstation.getCompanyId())
                    .setLineId(workstation.getLineId())
                    .setLocation(workstation.getLocation())
                    .setContent(workstation.getContent())
                    .setStatus(workstation.getStatus())
                    .setRemark(workstation.getRemark());
            workstationBOList.add(workstationBO);
        }

        Map<String, Map<String, WorkstationBO>> workshopIdWorkstationMap = transformToMap(workstationBOList);

        redisUtil.del(CacheConstant.WORKSTATION_CACHE_ALL);
        String workstationBOMapJson = JSON.toJSONString(workshopIdWorkstationMap);
        redisUtil.set(CacheConstant.WORKSTATION_CACHE_ALL, workstationBOMapJson);

        return workshopIdWorkstationMap;
    }


    private Map<String, Map<String, WorkstationBO>> transformToMap(List<WorkstationBO> workstationBOList) {
        if (CollectionUtils.isEmpty(workstationBOList)) {
            return new HashMap<>();
        }

        Map<String, Map<String, WorkstationBO>> workshopIdWorkstationMap = new HashMap<>(2);

        Map<String, List<WorkstationBO>> workshopIdListMap = workstationBOList.stream()
                .collect(Collectors.groupingBy(WorkstationBO::getWorkshopId));
        for (Map.Entry<String, List<WorkstationBO>> workshopIdListEntry : workshopIdListMap.entrySet()) {
            String workshopId = workshopIdListEntry.getKey();
            List<WorkstationBO> list = workshopIdListEntry.getValue();

            int mapSize = MapSizeUtil.tableSizeFor(list.size());
            Map<String, WorkstationBO> workstationIdBOMap = new HashMap<>(mapSize);
            for (WorkstationBO workstationBO : list) {
                workstationIdBOMap.put(workstationBO.getId(), workstationBO);
            }

            workshopIdWorkstationMap.put(workshopId, workstationIdBOMap);
        }

        return workshopIdWorkstationMap;
    }

}
