package org.jeecg.modules.material.pallet.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.pallet.bean.BuMaterialPallet;
import org.jeecg.modules.material.pallet.bean.vo.PalletMaterialTypesVO;

import java.util.List;

/**
 * <p>
 * 物资托盘 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-10
 */
public interface BuMaterialPalletService extends IService<BuMaterialPallet> {
    /**
     * 分页查询托盘信息
     *
     * @param searchText 托盘名称或编码
     * @param typeId 物资类型id
     * @param status 有效状态
     * @param useStatus 使用状态
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return 分页结果
     * @throws Exception 异常
     */
    IPage<BuMaterialPallet> page(String searchText,String typeId,String status,String useStatus, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 根据id查询托盘信息
     *
     * @param id 托盘id
     * @return 托盘信息
     * @throws Exception 异常
     */
    BuMaterialPallet getPalletById(String id) throws Exception;

    /**
     * 根据物资类型id查询未使用的托盘
     *
     * @param materialTypeId 物资类型id
     * @return 未使用的托盘列表
     * @throws Exception 异常
     */
    List<BuMaterialPallet> listUnusedPalletByMaterialTypeId(String materialTypeId) throws Exception;

    /**
     * 新增托盘 兼容批量
     *
     * @param buMaterialPallet 托盘信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean savePallet(BuMaterialPallet buMaterialPallet) throws Exception;

    /**
     * 批量设置托盘物资类型
     *
     * @param palletMaterialTypesVO 设置托盘物资类型信息
     * @return 是否成功
     * @throws Exception 异常
     */
    boolean setPalletMaterialTypes(PalletMaterialTypesVO palletMaterialTypesVO) throws Exception;

    /**
     * 批量删除托盘
     *
     * @param ids 托盘ids 多个逗号分隔
     * @return 是否删除成功
     * @throws Exception 异常
     */
    boolean deleteBatch(String ids) throws Exception;

}
