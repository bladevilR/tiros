package org.jeecg.modules.material.specasset.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.specasset.bean.BuSpecAssets;
import org.jeecg.modules.material.specasset.bean.vo.BuSpecAssetQueryVO;

/**
 * <p>
 * 特种设备 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-02
 */
public interface BuSpecAssetsService extends IService<BuSpecAssets> {

    /**
     * 根据条件分页查询特种设备
     *
     * @param queryVO  查询条件
     * @param pageNo   页码
     * @param pageSize 页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuSpecAssets> pageSpecAsset(BuSpecAssetQueryVO queryVO, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询特种设备
     *
     * @param id 特种设备id
     * @return 特种设备
     * @throws Exception 异常
     */
    BuSpecAssets getSpecAssetById(String id) throws Exception;

    /**
     * 新增或修改特种设备
     *
     * @param specAsset 特种设备
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean saveSpecAsset(BuSpecAssets specAsset) throws Exception;

    /**
     * 批量删除特种设备
     *
     * @param ids 特种设备ids，多个逗号分隔
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

}
