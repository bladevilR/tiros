package org.jeecg.modules.produce.trainhistory.service.cache;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.modules.produce.trainhistory.bean.vo.BuTrainStructureDetailTreeVO;
import org.jeecg.modules.produce.trainhistory.mapper.BuTrainStructureDetailProduceMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 车辆结构信息缓存 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-13
 */
@Service
public class StructTreeCacheServiceImpl implements StructTreeCacheService {

    @Resource
    private BuTrainStructureDetailProduceMapper buTrainStructureDetailProduceMapper;


    /**
     * @see StructTreeCacheService#treeAllStructureByStructId(String)
     */
    @Override
//    @Cacheable(cacheNames = CacheConstant.TRAIN_STRUCT_DETAIL_TREE, key = "'structId='+#p0", unless = "#result == null")
    //TODO-zhaiyantao 2021/5/15 19:56 缓存暂时注解，需要转移到common中，且注意更新时机，同设备类型缓存
    public List<BuTrainStructureDetailTreeVO> treeAllStructureByStructId(String structId) throws Exception {
        if (StringUtils.isBlank(structId)) {
            return new ArrayList<>();
        }

        List<BuTrainStructureDetailTreeVO> allStructDetailVOList = buTrainStructureDetailProduceMapper.selectListForTreeByStructId(structId);

        return transformStructDetailListToTree(allStructDetailVOList);
    }


    private List<BuTrainStructureDetailTreeVO> transformStructDetailListToTree(List<BuTrainStructureDetailTreeVO> structDetailList) {
        List<BuTrainStructureDetailTreeVO> topList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(structDetailList)) {
            List<String> idList = structDetailList.stream()
                    .map(BuTrainStructureDetailTreeVO::getId)
                    .collect(Collectors.toList());

            topList = structDetailList.stream()
                    .filter(structDetail -> !idList.contains(structDetail.getParentId()))
                    .collect(Collectors.toList());

            recurseAddChild(topList, structDetailList);
        }

        return topList;
    }

    private void recurseAddChild(List<BuTrainStructureDetailTreeVO> parentList, List<BuTrainStructureDetailTreeVO> structDetailList) {
        if (CollectionUtils.isNotEmpty(parentList) && CollectionUtils.isNotEmpty(structDetailList)) {
            for (BuTrainStructureDetailTreeVO parent : parentList) {
                List<BuTrainStructureDetailTreeVO> children = structDetailList.stream()
                        .filter(structDetail -> parent.getId().equals(structDetail.getParentId()))
                        .collect(Collectors.toList());

                recurseAddChild(children, structDetailList);
                parent.setChildren(children);
            }
        }
    }

}
