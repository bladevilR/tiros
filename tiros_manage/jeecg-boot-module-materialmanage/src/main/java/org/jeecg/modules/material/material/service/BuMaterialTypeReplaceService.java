package org.jeecg.modules.material.material.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.material.bean.BuMaterialTypeReplace;
import org.jeecg.modules.material.material.bean.vo.MaterialReplaceSaveVO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 可替换物资 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2022-07-21
 */
public interface BuMaterialTypeReplaceService extends IService<BuMaterialTypeReplace> {

    /**
     * 查询可替换物资列表
     *
     * @param id 物资id
     * @return 可替换物资列表
     */
    List<BuMaterialType> listReplace(String id);

    /**
     * 保存可替换物资
     *
     * @param saveVO 可替换物资保存vo
     * @return 保存结果
     */
    boolean saveReplace(MaterialReplaceSaveVO saveVO);

    /**
     * 导入可替换物资信息
     *
     * @param file excel文件
     * @return 导入结果
     */
    boolean importMaterialReplace(MultipartFile file) throws IOException;

    /**
     * 根据物资id查询可替换物资
     *
     * @param materialTypeId 物资id
     * @return 可替换物资列表
     */
    List<String> listCanReplaceMaterialTypeIdByMaterialTypeId(String materialTypeId);

    /**
     * 根据物资编码或者名称查询可替换物资id
     *
     * @param materialTypeText 物资编码或者名称
     * @return 可替换物资id列表
     */
    List<String> listCanReplaceMaterialTypeIdByMaterialTypeText(String materialTypeText);

    /**
     * 根据物资编码或者名称查询自身物资id
     *
     * @param materialTypeText 物资编码或者名称
     * @return 自身物资id列表
     */
    List<String> listSelfMaterialTypeIdByMaterialTypeText(String materialTypeText);

    /**
     * 根据物资id列表查询可替换物资
     *
     * @param materialTypeIdList 物资id列表
     * @return 可替换物资列表map
     */
    Map<String, List<String>> mapCanReplaceMaterialTypeIdListByMaterialTypeIdList(List<String> materialTypeIdList);

}
