package org.jeecg.modules.group.information.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.group.information.bean.BuGroupWorkstation;
import org.jeecg.modules.group.information.bean.BuMtrWorkstation;

/** The interface Bu group workstation service.
 * @author yyg*/
public interface BuGroupWorkstationService extends IService<BuGroupWorkstation> {

    /**
     * 根据班组id分页查询班组已关联工位
     *
     * @param groupId  班组id
     * @param name  工位名或编码
     * @param position 位置
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 工位分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMtrWorkstation> pageWorkstationByGroupId(String groupId,String name,String position, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据班组id分页查询班组未关联工位
     *
     * @param groupId  班组id
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 班组未关联工位分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMtrWorkstation> pageUnrelatedWorkstationByGroupId(String groupId, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 添加班组工位关联关系
     *
     * @param buGroupWorkstation 班组id + 工位id
     * @return 是否添加成功
     * @throws Exception 异常信息
     */
    boolean addWorkstationToGroup(BuGroupWorkstation buGroupWorkstation) throws Exception;

    /**
     * 删除班组工位关联关系
     *
     * @param buGroupWorkstation 班组id + 工位id
     * @return 是否删除成功
     * @throws Exception 异常信息
     */
    boolean deleteWorkstationFromGroup(BuGroupWorkstation buGroupWorkstation) throws Exception;

}
