package org.jeecg.modules.group.information.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.group.information.bean.BuGroupWorkstation;
import org.jeecg.modules.group.information.bean.BuMtrWorkstation;

/**
 * <p>
 * 班组工位关联 Mapper 接口
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-10-14
 */
public interface BuGroupWorkstationMapper extends BaseMapper<BuGroupWorkstation> {

    /**
     * 根据班组id分页查询班组已关联工位
     *
     * @param page    分页参数
     * @param groupId 班组id
     * @param name  工位名或编码
     * @param position 位置
     * @return 工位分页结果
     */
    IPage<BuMtrWorkstation> pageWorkstationByGroupId(IPage<BuMtrWorkstation> page, @Param("groupId") String groupId,
                                                     @Param("name")String name,@Param("position")String position);

    /**
     * 根据班组id分页查询班组未关联工位
     *
     * @param groupId 班组id
     * @return 班组未关联工位分页结果
     */
    IPage<BuMtrWorkstation> pageUnrelatedWorkstationByGroupId(IPage<BuMtrWorkstation> page, @Param("groupId") String groupId);

}
