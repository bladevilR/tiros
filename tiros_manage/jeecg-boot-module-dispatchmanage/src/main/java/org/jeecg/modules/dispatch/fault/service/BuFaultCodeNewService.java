package org.jeecg.modules.dispatch.fault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.jeecg.modules.dispatch.fault.bean.BuFaultCodeNew;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 090102003, 09 表示专业编码，01 表示系统编码  02 表示 子系统编码  003 表示部件编码
 * -& 服务类
 * </p>
 *
 * @author youGen
 * @since 2021 -06-29
 */
public interface BuFaultCodeNewService extends IService<BuFaultCodeNew> {

    /**
     * 查询故障代码列表
     *
     * @param searchText the search text
     * @return the list
     */
    List<BuFaultCodeNew> selectList(String searchText);

    /**
     * 根据故障编码查询所有子类
     *
     * @param code the code
     * @return the list
     */
    List<BuFaultCodeNew> selectByParentCode(String code);

    /**
     * 根据故障编码查询所有父类
     *
     * @param code the code
     * @return the list
     */
    List<BuFaultCodeNew> selectParentCodeByCode(String code);
}
