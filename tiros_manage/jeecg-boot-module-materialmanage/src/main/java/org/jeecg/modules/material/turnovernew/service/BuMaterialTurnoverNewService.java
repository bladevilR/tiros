package org.jeecg.modules.material.turnovernew.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.turnovernew.bean.BuMaterialTurnoverNew;
import org.jeecg.modules.material.turnovernew.bean.vo.BuMaterialTurnoverQueryVONew;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 周转件 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-09-18
 */
public interface BuMaterialTurnoverNewService extends IService<BuMaterialTurnoverNew> {

    /**
     * 分页查询周转件
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     */
    IPage<BuMaterialTurnoverNew> pageTurnover(BuMaterialTurnoverQueryVONew queryVO, Integer pageNo, Integer pageSize);

    /**
     * 根据周转件id查询周转件
     *
     * @param id 周转件id
     * @return 周转件
     */
    BuMaterialTurnoverNew getTurnoverById(String id);

    /**
     * 保存周转件
     *
     * @param turnover 周转件
     * @return 保存结果
     */
    boolean saveTurnover(BuMaterialTurnoverNew turnover);

    /**
     * 批量删除周转件
     *
     * @param ids 周转件ids，多个逗号分隔
     * @return 删除结果
     */
    boolean deleteBatch(String ids);

    /**
     * excel导入周转件信息
     *
     * @param excelFile excel文件
     * @param clearOldData 是否清空旧数据
     * @return 是否导入成功
     */
    boolean importTurnoverFromExcel(MultipartFile excelFile, Boolean clearOldData) throws Exception;

}
