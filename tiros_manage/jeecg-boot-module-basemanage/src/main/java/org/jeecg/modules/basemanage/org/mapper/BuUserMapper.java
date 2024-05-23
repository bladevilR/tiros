package org.jeecg.modules.basemanage.org.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.basemanage.org.bean.*;
import org.jeecg.modules.basemanage.org.bean.bo.BuDepartBO;
import org.jeecg.modules.basemanage.org.bean.vo.BuUserQueryVO;
import org.jeecg.modules.basemanage.workstation.entity.CompanyTree;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 人员 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-01-19
 */
public interface BuUserMapper extends BaseMapper<BuUser> {

    /**
     * 树形结构查询
     *
     * @return 公司-车辆段-车间-工班
     */
    List<CompanyTree> selectCompanyTree();

    /**
     * 根据条件分页查询人员信息
     *
     * @param page    分页信息
     * @param queryVO 查询条件
     * @return 分页结果
     */
    IPage<BuUser> selectUserPage(IPage<BuUser> page, @Param("queryVO") BuUserQueryVO queryVO);

    /**
     * 根据id查询人员信息
     *
     * @param id 分页信息
     * @return 人员信息
     */
    BuUser selectUserById(String id);


    List<SysUserTag> selectTagTitleListByUserIdList(@Param("userIdList") List<String> userIdList);

    List<BuUserCert> selectCertListByUserIdList(@Param("userIdList") List<String> userIdList);

    List<BuTraining> selectTrainingListByUserIdList(@Param("userIdList") List<String> userIdList);

    List<BuUserSkill> selectSkillListByUserIdList(@Param("userIdList") List<String> userIdList);

    List<BuPostionInfo> selectPositionListByUserIdList(@Param("userIdList") List<String> userIdList);

    List<BuDepartBO> selectDepartBOListByUserIdList(@Param("userIdList") List<String> userIdList);

    List<Map<String,Object>> selectUserIdRoleNameListByUserIdList(@Param("userIdList") List<String> userIdList);

    /**
     * 根据id更新用户
     *
     * @param user 用户
     */
    void updateUserById(@Param("user") BuUser user);

}
