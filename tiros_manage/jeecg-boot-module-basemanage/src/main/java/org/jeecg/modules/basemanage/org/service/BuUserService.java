package org.jeecg.modules.basemanage.org.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.basemanage.org.bean.BuUser;
import org.jeecg.modules.basemanage.org.bean.vo.BuUserQueryVO;
import org.jeecg.modules.basemanage.workstation.entity.CompanyTree;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * <p>
 * 用户 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
public interface BuUserService {

    /**
     * 组织人员树形结构查询
     *
     * @return 公司-车辆段-车间-工班
     * @throws Exception 异常信息
     */
    List<CompanyTree> selectTreeForOrgUser() throws Exception;

    /**
     * 根据条件分页查询人员信息
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuUser> pageUser(BuUserQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    BuUser getUser(String id) throws Exception;

    Boolean saveUser(BuUser user) throws Exception;

    Boolean deleteUser(String ids);

    /**
     * 人员技能项对比
     *
     * @param ids 人员id,多个逗号分隔
     * @return 对比结果
     * @throws Exception 异常信息
     */
    List<LinkedHashMap<String, Object>> compareUserSkill(String ids) throws Exception;

}
