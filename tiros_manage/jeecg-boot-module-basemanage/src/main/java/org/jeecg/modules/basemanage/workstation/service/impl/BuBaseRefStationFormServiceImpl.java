package org.jeecg.modules.basemanage.workstation.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.modules.basemanage.workstation.entity.BuBaseRefStationForm;
import org.jeecg.modules.basemanage.workstation.entity.vo.UnRelatedFormQueryVO;
import org.jeecg.modules.basemanage.workstation.entity.vo.WorkstationFormInfoVO;
import org.jeecg.modules.basemanage.workstation.mapper.BuBaseRefStationFormMapper;
import org.jeecg.modules.basemanage.workstation.service.BuBaseRefStationFormService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 工位表单关联 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-14
 */
@Service
public class BuBaseRefStationFormServiceImpl extends ServiceImpl<BuBaseRefStationFormMapper, BuBaseRefStationForm> implements BuBaseRefStationFormService {

    @Resource
    private BuBaseRefStationFormMapper buBaseRefStationFormMapper;


    /**
     * @see BuBaseRefStationFormService#listRelatedFormByWorkstationId(UnRelatedFormQueryVO)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public List<BuBaseRefStationForm> listRelatedFormByWorkstationId(UnRelatedFormQueryVO queryVO) throws Exception {
        List<BuBaseRefStationForm> refStationFormList = buBaseRefStationFormMapper.selectListByWorkstationId(queryVO);

        List<BuBaseRefStationForm> resultList = new ArrayList<>();
        // 删除对应表单已经没有的关联关系
        List<String> needDeleteIdList = new ArrayList<>();
        for (BuBaseRefStationForm refStationForm : refStationFormList) {
            String code = refStationForm.getCode();
            if (null == code) {
                needDeleteIdList.add(refStationForm.getId());
            } else {
                resultList.add(refStationForm);
            }
        }
        if (CollectionUtils.isNotEmpty(needDeleteIdList)) {
            buBaseRefStationFormMapper.deleteBatchIds(needDeleteIdList);
        }

        return resultList;
    }

    /**
     * @see BuBaseRefStationFormService#pageUnRelatedForm(UnRelatedFormQueryVO, Integer, Integer)
     */
    @Transactional(readOnly = true, rollbackFor = Exception.class)
    @Override
    public IPage<WorkstationFormInfoVO> pageUnRelatedForm(UnRelatedFormQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception {
        return buBaseRefStationFormMapper.selectUnrelatedFormPage(new Page<>(pageNo, pageSize), queryVO);
    }

}
