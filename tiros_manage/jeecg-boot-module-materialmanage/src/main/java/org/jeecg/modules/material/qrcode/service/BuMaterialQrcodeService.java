package org.jeecg.modules.material.qrcode.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.material.pallet.bean.BuMaterialPallet;
import org.jeecg.modules.material.qrcode.bean.BuMaterialQrcode;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;

/**
 * <p>
 * 查询时，自动生成该表中不存在的对应数据以及二维码 服务类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-10
 */
public interface BuMaterialQrcodeService extends IService<BuMaterialQrcode> {
    /**
     * 分页查询仓库信息及二维码
     *
     * @param depotId    车辆段id
     * @param workshopId 车间id
     * @param searchText 仓库名称或编码
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMaterialQrcode> pageWarehouse(String depotId, String workshopId, String searchText, Integer pageNo, Integer pageSize) throws Exception;

//    /**
//     * 分页查询物资信息及二维码
//     *
//     * @param lineId     线路id 关联bu_material_stock物资库存中找到warehouse_id关联到bu_mtr_warehouse仓库信息中找到线路
//     * @param systemId   系统id 关联bu_material_type_attr物资属性表中的system_id
//     * @param searchText 物资名称或编码
//     * @param pageNo     页码
//     * @param pageSize   页大小
//     * @return 分页结果
//     * @throws Exception 异常信息
//     */
//    IPage<BuMaterialQrcode> pageMaterial(String lineId, String systemId, String searchText, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 分页查询托盘信息及二维码
     *
     * @param lineId     线路id
     * @param searchText 托盘名称或编码
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMaterialQrcode> pagePallet(String lineId, String searchText, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 分页查询列管备件信息及二维码
     *
     * @param lineId     线路id
     * @param systemId   系统id material_type_id关联到bu_material_type_attr物资属性表中的system_id
     * @param searchText 列管备件名称或物资编码或资产编码
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMaterialQrcode> pageSparePart(String lineId, String systemId, String searchText, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 分页查询工器具信息及二维码
     *
     * @param lineId     线路id
     * @param groupId    班组id
     * @param searchText 工器具名称或物资编码或资产编码或序列号（工器具编码或名称或资产编码）
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMaterialQrcode> pageTools(String lineId, String groupId, String searchText, Integer pageNo, Integer pageSize) throws Exception;

    /**
     * 更改标识码打印状态为已打印
     *
     * @param ids 标识码ids，多个逗号分隔
     * @return 是否更改成功
     * @throws Exception 异常信息
     */
    boolean confirmPrinted(String ids) throws Exception;

    IPage<BuMaterialPallet> pagePallets(String lineId, String searchText, Integer pageNo, Integer pageSize) throws Exception;
    /**
     * 分页查询仓库信息及二维码
     *
     * @param depotId    车辆段id
     * @param workshopId 车间id
     * @param searchText 仓库名称或编码
     * @param pageNo     页码
     * @param pageSize   页大小
     * @return 分页结果
     * @throws Exception 异常信息
     */
    IPage<BuMtrWarehouse> pageWarehouses(String depotId, String workshopId, String searchText, Integer pageNo, Integer pageSize) throws Exception;
}
