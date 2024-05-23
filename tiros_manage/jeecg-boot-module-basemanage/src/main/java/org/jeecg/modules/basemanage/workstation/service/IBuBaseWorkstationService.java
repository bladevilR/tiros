package org.jeecg.modules.basemanage.workstation.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.basemanage.workstation.entity.BuBaseWorkstation;
import org.jeecg.modules.basemanage.workstation.entity.CompanyTree;
import org.jeecg.modules.basemanage.workstation.entity.vo.BuBaseWorkstationQueryVO;

import java.util.List;

/**
 * <p>
 * 工位信息 服务类
 * </p>
 *
 * @author 喻有根
 * @since 2020-07-08
 */
public interface IBuBaseWorkstationService extends IService<BuBaseWorkstation> {

    /**
     * 工位树形结构查询
     *
     * @return 公司-车辆段-车间
     * @throws Exception 异常信息
     */
    List<CompanyTree> selectTreeForWorkstation() throws Exception;

    /**
     * 分页查询工位
     *
     * @param page
     * @param workstation
     * @return
     */

    Page<BuBaseWorkstation> selectWorkstationPage(Page<BuBaseWorkstation> page, BuBaseWorkstationQueryVO workstation);

    /**
     * 根据班组找工位
     *
     * @param id 班组id
     * @return 该班组下的工位
     * @throws Exception 异常信息
     */
    List<BuBaseWorkstation> selectByWorkGroupId(String id) throws Exception;

    /**
     * 新增工位
     *
     * @param buBaseWorkstation 工位信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean saveWorkstation(BuBaseWorkstation buBaseWorkstation) throws Exception;

    /**
     * 修改工位
     *
     * @param buBaseWorkstation 工位信息
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean updateWorkstation(BuBaseWorkstation buBaseWorkstation) throws Exception;

    /**
     * 删除工位(批量)
     *
     * @param ids 工位id，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常信息
     */
    boolean deleteWorkstationBatch(String ids) throws Exception;

}
