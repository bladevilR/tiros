package org.jeecg.modules.basemanage.workstation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.workstation.entity.BuBaseWorkstation;
import org.jeecg.modules.basemanage.workstation.entity.CompanyTree;
import org.jeecg.modules.basemanage.workstation.entity.vo.BuBaseWorkstationQueryVO;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 工位信息 Mapper 接口
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-08
 */
@Component("buBaseWorkstationMapper")
public interface BuBaseWorkstationMapper extends BaseMapper<BuBaseWorkstation> {

    /**
     * 树形结构查询
     *
     * @return 公司-车辆段-车间
     */
    List<CompanyTree> selectCompanyTree();

    /**
     * 分页查找工位
     *
     * @param page
     * @param workstation
     * @return
     */
    List<BuBaseWorkstation> selectWorkstationPage(Page<BuBaseWorkstation> page, @Param("workstation") BuBaseWorkstationQueryVO workstation);

    /**
     * 根据班组id查找班组关联工位
     *
     * @param workGroupId 班组id
     * @return 班组关联工位列表
     */
    List<BuBaseWorkstation> selectByWorkGroupId(@Param("workGroupId") String workGroupId);

}
