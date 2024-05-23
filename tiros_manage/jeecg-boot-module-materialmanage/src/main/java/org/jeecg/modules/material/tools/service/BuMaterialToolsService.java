package org.jeecg.modules.material.tools.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jeecg.modules.material.tools.bean.BuMaterialTools;
import org.jeecg.modules.material.tools.bean.vo.BuMaterialToolsQueryVO;
import org.jeecg.modules.material.tools.bean.vo.BuMaterialToolsSettingVO;

import java.util.Date;

/**
 * <p>
 * 工具信息，包括工器具、工装等信息，一种物资类型可能存在多条记录 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020 -09-11
 */
public interface BuMaterialToolsService extends IService<BuMaterialTools> {

    /**
     * 分页查询工艺装备信息
     *
     * @param queryVO  查询工艺装备条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果 page
     * @throws Exception 异常
     */
    IPage<BuMaterialTools> page(BuMaterialToolsQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询工具详情
     *
     * @param id 工具id
     * @return 工具详情 tools by id
     * @throws Exception 异常
     */
    BuMaterialTools getToolsById(String id) throws Exception;

    /**
     * 批量删除工艺装备
     *
     * @param ids 工艺装备ids 多个逗号分隔
     * @return 是否删除成功 boolean
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

    /**
     * 设置下次送检日期
     *
     * @param ids           工艺装备id
     * @param nextCheckTime 下次送检日期
     * @return 是否设置成功 next check time
     * @throws Exception 异常
     */
    boolean setNextCheckTime(String ids, Date nextCheckTime) throws Exception;

    /**
     * 查询待送检工艺装备列表（分页）
     *
     * @param searchText 名称或编码
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return 待送检工艺装备列表 page
     * @throws Exception 异常
     */
    IPage<BuMaterialTools> listNeedCheck(String searchText, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 统计待送检工艺装备数量
     *
     * @param searchText 名称或编码
     * @return 待送检工艺装备数量 integer
     * @throws Exception 异常
     */
    Integer countNeedCheck(String searchText) throws Exception;

    /**
     * 根据工器具的责任人员id，查找并设置工器具的所属工班
     *
     * @return 是否成功 boolean
     * @throws Exception 异常
     */
    boolean flushToolGroupByUser() throws Exception;

    /**
     * 设置上次自检日期
     *
     * @param ids            the id
     * @param lastSelfCheckTime the last check time
     * @return the last check time
     * @throws Exception the exception
     */
    boolean setLastCheckTime(String ids, Date lastSelfCheckTime) throws Exception;

    /**
     * 设置是否自检
     *
     * @param ids        工艺装备id
     * @param selfCheck the self check
     * @return the self check
     */
    boolean setSelfCheck(String ids, Integer selfCheck);

    /**
     * 设置责任人
     *
     * @param ids     工艺装备id
     * @param userId 责任人id
     * @return the responsible
     */
    boolean setResponsible(String ids, String userId);

    /**
     * 导出工器具
     * @param queryVO
     * @return
     */
    HSSFWorkbook exportTools(BuMaterialToolsQueryVO queryVO);

    /**
     * 设置是否电动工具
     * @param toolsSettingVO 工器具
     * @return
     */
    boolean setElectric(BuMaterialToolsSettingVO toolsSettingVO);
}
