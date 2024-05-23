package org.jeecg.modules.third.jdx.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.third.jdx.bean.BuMaximoLocation;
import org.jeecg.modules.third.jdx.mapper.BuMaximoLocationThirdMapper;
import org.jeecg.modules.third.jdx.service.BuMaximoLocationThirdService;
import org.jeecg.modules.third.maximo.bean.JdxLocationsOut;
import org.jeecg.modules.third.utils.DatabaseBatchSubUtil;
import org.jeecg.modules.third.utils.UUIDGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * maximo资产位置 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-08-25
 */
@Slf4j
@Service
public class BuMaximoLocationThirdServiceImpl extends ServiceImpl<BuMaximoLocationThirdMapper, BuMaximoLocation> implements BuMaximoLocationThirdService {

    @Resource
    private BuMaximoLocationThirdMapper buMaximoLocationThirdMapper;


    /**
     * @see BuMaximoLocationThirdService#insertAllLocationFromMaximoData(List, Boolean)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean insertAllLocationFromMaximoData(List<JdxLocationsOut> maximoLocationsList, Boolean needDeleteAllOld) throws Exception {
        if (CollectionUtils.isEmpty(maximoLocationsList)) {
            return true;
        }

        Date now = new Date();

        // 删除旧数据
        if (null != needDeleteAllOld && needDeleteAllOld) {
            int deleteCount = buMaximoLocationThirdMapper.delete(Wrappers.emptyWrapper());
            log.info("初始化同步maximo位置：删除了" + deleteCount + "条旧数据；");
        }

        List<BuMaximoLocation> locationList = new ArrayList<>();
        for (JdxLocationsOut jdxLocationsOut : maximoLocationsList) {
            BuMaximoLocation location = new BuMaximoLocation()
                    .setId(UUIDGenerator.generate())
                    .setCode(jdxLocationsOut.getLocation())
                    .setName(jdxLocationsOut.getDescription())
                    .setParentCode(jdxLocationsOut.getParent())
                    .setStatus(1)
                    .setRemark("maximo初始化同步；")
                    .setMaximoStatus(jdxLocationsOut.getStatus())
                    .setMaximoOrgId(jdxLocationsOut.getOrgid())
                    .setMaximoSiteId(jdxLocationsOut.getSiteid())
                    .setCreateTime(now)
                    .setCreateBy("admin");
            locationList.add(location);
        }

        if (CollectionUtils.isNotEmpty(locationList)) {
            List<List<BuMaximoLocation>> batchSubList = DatabaseBatchSubUtil.batchSubList(locationList);
            for (List<BuMaximoLocation> batchSub : batchSubList) {
                buMaximoLocationThirdMapper.insertList(batchSub);
            }
            log.info("初始化同步maximo位置：新增了" + locationList.size() + "条新数据；");
        }

        return true;
    }

}
