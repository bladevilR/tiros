package org.jeecg.modules.material.qrcode.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.extra.qrcode.QrCodeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.tiros.config.bean.SysConfig;
import org.jeecg.common.tiros.config.service.SysConfigService;
import org.jeecg.common.util.storage.MinioStorage;
import org.jeecg.modules.material.material.bean.BuMaterialType;
import org.jeecg.modules.material.pallet.bean.BuMaterialPallet;
import org.jeecg.modules.material.pallet.mapper.BuMaterialPalletMapper;
import org.jeecg.modules.material.qrcode.bean.BuMaterialQrcode;
import org.jeecg.modules.material.qrcode.mapper.BuMaterialQrcodeMapper;
import org.jeecg.modules.material.qrcode.service.BuMaterialQrcodeService;
import org.jeecg.modules.material.tools.bean.BuMaterialTools;
import org.jeecg.modules.material.tools.mapper.BuMaterialToolsMapper;
import org.jeecg.modules.material.sparepart.bean.BuMaterialSparePart;
import org.jeecg.modules.material.sparepart.mapper.BuMaterialSparePartMapper;
import org.jeecg.modules.material.warehouse.bean.BuMtrWarehouse;
import org.jeecg.modules.material.warehouse.mapper.BuMtrWarehouseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.font.FontDesignMetrics;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 查询时，自动生成该表中不存在的对应数据以及二维码 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-10
 */
@Slf4j
@Service
public class BuMaterialQrcodeServiceImpl extends ServiceImpl<BuMaterialQrcodeMapper, BuMaterialQrcode> implements BuMaterialQrcodeService {

    @Resource
    private BuMaterialQrcodeMapper buMaterialQrcodeMapper;
    @Resource
    private BuMtrWarehouseMapper buMtrWarehouseMapper;
    @Resource
    private BuMaterialPalletMapper buMaterialPalletMapper;
    @Resource
    private BuMaterialSparePartMapper buMaterialSparePartMapper;
    @Resource
    private BuMaterialToolsMapper buMaterialToolsMapper;
    @Resource
    private SysConfigService sysConfigService;


    /**
     * @see BuMaterialQrcodeService#pageWarehouse(String, String, String, Integer, Integer)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialQrcode> pageWarehouse(String depotId, String workshopId, String searchText, Integer pageNo, Integer pageSize) throws Exception {
        // 查询仓库信息
        LambdaQueryWrapper<BuMtrWarehouse> warehouseWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(depotId)) {
            warehouseWrapper.eq(BuMtrWarehouse::getDepotId, depotId);
        }
        if (StringUtils.isNotBlank(workshopId)) {
            warehouseWrapper.eq(BuMtrWarehouse::getWorkshopId, workshopId);
        }
        if (StringUtils.isNotBlank(searchText)) {
            warehouseWrapper.and(wrapper ->
                    wrapper.like(BuMtrWarehouse::getCode, searchText)
                            .or()
                            .like(BuMtrWarehouse::getName, searchText));
        }
        IPage<BuMtrWarehouse> warehousePage = buMtrWarehouseMapper.selectPage(new Page<>(pageNo, pageSize), warehouseWrapper);

        List<BuMtrWarehouse> warehouseList = warehousePage.getRecords();
        List<String> warehouseIdList = warehouseList.stream().map(BuMtrWarehouse::getId).collect(Collectors.toList());

        // 查仓库分页结果对应的标识码信息，没有就新增
        Map<String, BuMtrWarehouse> idBuMtrWarehouseMap = warehouseList.stream().collect(Collectors.toMap(BuMtrWarehouse::getId, Function.identity()));
        List<BuMaterialQrcode> qrcodeList = getAndSaveQrcodeList(warehouseIdList, idBuMtrWarehouseMap, 5);

        // 补全标识码仓库信息
        for (BuMaterialQrcode buMaterialQrcode : qrcodeList) {
            BuMtrWarehouse warehouse = idBuMtrWarehouseMap.get(buMaterialQrcode.getObjId());
            if (null != warehouse) {
                buMaterialQrcode
                        .setWarehouseCode(warehouse.getCode())
                        .setWarehouseName(warehouse.getName())
                        .setWarehouseType(warehouse.getType())
                        .setWarehouseLocation(warehouse.getLocation())
                        .setWarehouseRemark(warehouse.getRemark());
            }
        }

        return getPage(warehousePage, qrcodeList);
    }

//    /**
//     * @see BuMaterialQrcodeService#pageMaterial(String, String, String, Integer, Integer)
//     */
//    @Transactional(rollbackFor = Exception.class)
//    @Override
//    public IPage<BuMaterialQrcode> pageMaterial(String lineId, String systemId, String searchText, Integer pageNo, Integer pageSize) throws Exception {
//        // 查询物资信息
//        IPage<BuMaterialType> materialTypePage = buMaterialTypeMapper.selectPageByLineIdAndSystemIdAndCodeName(new Page<>(pageNo, pageSize), lineId, systemId, searchText);
//
//        List<BuMaterialType> materialTypeList = materialTypePage.getRecords();
//        List<String> materialTypeIdList = materialTypeList.stream().map(BuMaterialType::getId).collect(Collectors.toList());
//
//        // 查物资分页结果对应的标识码信息，没有就新增
//        Map<String, BuMaterialType> idBuMaterialTypeMap = materialTypeList.stream().collect(Collectors.toMap(BuMaterialType::getId, Function.identity()));
//        List<BuMaterialQrcode> qrcodeList = getAndSaveQrcodeList(materialTypeIdList, idBuMaterialTypeMap, CommonConstant.Qrcode.OBJ_TYPE_MATERIAL);
//
//        // 补全标识码物资信息
//        for (BuMaterialQrcode buMaterialQrcode : qrcodeList) {
//            BuMaterialType materialType = idBuMaterialTypeMap.get(buMaterialQrcode.getObjId());
//            if (null != materialType) {
//                buMaterialQrcode
//                        .setMaterialName(materialType.getName())
//                        .setMaterialCategory(materialType.getCategory1())
//                        .setMaterialAttr(materialType.getCategory2())
//                        .setMaterialRemark(materialType.getRemark());
//            }
//        }
//
//        return getPage(materialTypePage, qrcodeList);
//    }

    /**
     * @see BuMaterialQrcodeService#pagePallet(String, String, Integer, Integer)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialQrcode> pagePallet(String lineId, String searchText, Integer pageNo, Integer pageSize) throws Exception {
        // 查询托盘信息
        LambdaQueryWrapper<BuMaterialPallet> palletWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(searchText)) {
            palletWrapper
                    .like(BuMaterialPallet::getCode, searchText)
                    .or()
                    .like(BuMaterialPallet::getName, searchText);
        }
        IPage<BuMaterialPallet> palletPage = buMaterialPalletMapper.selectPage(new Page<>(pageNo, pageSize), palletWrapper);

        List<BuMaterialPallet> palletList = palletPage.getRecords();
        List<String> palletIdList = palletList.stream().map(BuMaterialPallet::getId).collect(Collectors.toList());

        // 查托盘分页结果对应的标识码信息，没有就新增
        Map<String, BuMaterialPallet> idBuMaterialPalletMap = palletList.stream().collect(Collectors.toMap(BuMaterialPallet::getId, Function.identity()));
        List<BuMaterialQrcode> qrcodeList = getAndSaveQrcodeList(palletIdList, idBuMaterialPalletMap, 3);

        // 补全标识码托盘信息
        for (BuMaterialQrcode buMaterialQrcode : qrcodeList) {
            BuMaterialPallet pallet = idBuMaterialPalletMap.get(buMaterialQrcode.getObjId());
            if (null != pallet) {
                buMaterialQrcode
                        .setPalletCode(pallet.getCode())
                        .setPalletName(pallet.getName())
                        .setPalletSize(pallet.getPalletSize())
                        .setPalletRemark(pallet.getRemark());
            }
        }

        return getPage(palletPage, qrcodeList);

    }

    /**
     * @see BuMaterialQrcodeService#pageSparePart(String, String, String, Integer, Integer)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialQrcode> pageSparePart(String lineId, String systemId, String searchText, Integer pageNo, Integer pageSize) throws Exception {
        // 查询列管备件信息
        IPage<BuMaterialSparePart> sparePartPage = buMaterialSparePartMapper.selectPageByLineIdAndSystemIdAndCodeName(new Page<>(pageNo, pageSize), lineId, systemId, searchText);

        List<BuMaterialSparePart> sparePartList = sparePartPage.getRecords();
        List<String> sparePartIdList = sparePartList.stream().map(BuMaterialSparePart::getId).collect(Collectors.toList());

        // 查列管备件分页结果对应的标识码信息，没有就新增
        Map<String, BuMaterialSparePart> idBuMaterialSparePartMap = sparePartList.stream()
                .collect(Collectors.toMap(BuMaterialSparePart::getId, Function.identity()));
        List<BuMaterialQrcode> qrcodeList = getAndSaveQrcodeList(sparePartIdList, idBuMaterialSparePartMap, 1);

        // 补全标识码列管备件信息
        for (BuMaterialQrcode buMaterialQrcode : qrcodeList) {
            BuMaterialSparePart sparePart = idBuMaterialSparePartMap.get(buMaterialQrcode.getObjId());
            if (null != sparePart) {
                buMaterialQrcode
                        .setTurnoverName(sparePart.getName());
            }
        }

        return getPage(sparePartPage, qrcodeList);
    }

    /**
     * @see BuMaterialQrcodeService#pageTools(String, String, String, Integer, Integer)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public IPage<BuMaterialQrcode> pageTools(String lineId, String groupId, String searchText, Integer pageNo, Integer pageSize) throws Exception {
        // 查询工器具信息
        LambdaQueryWrapper<BuMaterialTools> toolsWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(lineId)) {
            toolsWrapper.eq(BuMaterialTools::getLineId, lineId);
        }
        if (StringUtils.isNotBlank(groupId)) {
            toolsWrapper.eq(BuMaterialTools::getGroupId, groupId);
        }
        if (StringUtils.isNotBlank(searchText)) {
            toolsWrapper.and(wrapper ->
                    wrapper.like(BuMaterialTools::getCode, searchText)
                            .or()
                            .like(BuMaterialTools::getName, searchText)
                            .or()
                            .like(BuMaterialTools::getAssetCode, searchText));
        }
        IPage<BuMaterialTools> toolsPage = buMaterialToolsMapper.selectPage(new Page<>(pageNo, pageSize), toolsWrapper);

        List<BuMaterialTools> toolsList = toolsPage.getRecords();
        List<String> toolsIdList = toolsList.stream().map(BuMaterialTools::getId).collect(Collectors.toList());

        // 查工器具分页结果对应的标识码信息，没有就新增
        Map<String, BuMaterialTools> idBuMaterialToolsMap = toolsList.stream().collect(Collectors.toMap(BuMaterialTools::getId, Function.identity()));
        List<BuMaterialQrcode> qrcodeList = getAndSaveQrcodeList(toolsIdList, idBuMaterialToolsMap, 2);

        // 补全标识码工器具信息
        for (BuMaterialQrcode buMaterialQrcode : qrcodeList) {
            BuMaterialTools tools = idBuMaterialToolsMap.get(buMaterialQrcode.getObjId());
            if (null != tools) {
                buMaterialQrcode
                        .setToolsName(tools.getName());
            }
        }

        return getPage(toolsPage, qrcodeList);
    }

    /**
     * @see BuMaterialQrcodeService#confirmPrinted(String)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean confirmPrinted(String ids) throws Exception {
        List<String> idList = Arrays.asList(ids.split(","));
        if (CollectionUtils.isNotEmpty(idList)) {
            for (String id : idList) {
                BuMaterialQrcode qrcode = new BuMaterialQrcode()
                        .setId(id)
                        .setPrint(1);
                buMaterialQrcodeMapper.updateById(qrcode);
            }
        }

        return true;
    }

    @Override
    public IPage<BuMaterialPallet> pagePallets(String lineId, String searchText, Integer pageNo, Integer pageSize) throws IOException {
        String qrCodeTempPath = getTirosTempPath();
        FileUtil.del(qrCodeTempPath + "/qrcode");
        // 查询托盘信息
        LambdaQueryWrapper<BuMaterialPallet> palletWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(searchText)) {
            palletWrapper
                    .like(BuMaterialPallet::getCode, searchText)
                    .or()
                    .like(BuMaterialPallet::getName, searchText);
        }
        IPage<BuMaterialPallet> palletPage = buMaterialPalletMapper.selectPage(new Page<>(pageNo, pageSize), palletWrapper);

        List<BuMaterialPallet> palletList = palletPage.getRecords();
        if (CollectionUtils.isNotEmpty(palletList)) {
            palletList.forEach(pallet -> {
                try {
                    String qrcodeImgUrl = generateAndUploadQrcodeImg(pallet, 3, qrCodeTempPath);
                    pallet.setQrcodeImgUrl(qrcodeImgUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }

        return palletPage;
    }

    @Override
    public IPage<BuMtrWarehouse> pageWarehouses(String depotId, String workshopId, String searchText, Integer pageNo, Integer pageSize) throws Exception {
        String qrCodeTempPath = getTirosTempPath();
        FileUtil.del(qrCodeTempPath + "/qrcode");
        // 查询仓库信息
        LambdaQueryWrapper<BuMtrWarehouse> warehouseWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(depotId)) {
            warehouseWrapper.eq(BuMtrWarehouse::getDepotId, depotId);
        }
        if (StringUtils.isNotBlank(workshopId)) {
            warehouseWrapper.eq(BuMtrWarehouse::getWorkshopId, workshopId);
        }
        if (StringUtils.isNotBlank(searchText)) {
            warehouseWrapper.and(wrapper ->
                    wrapper.like(BuMtrWarehouse::getCode, searchText)
                            .or()
                            .like(BuMtrWarehouse::getName, searchText));
        }
        IPage<BuMtrWarehouse> warehousePage = buMtrWarehouseMapper.selectPage(new Page<>(pageNo, pageSize), warehouseWrapper);
        List<BuMtrWarehouse> warehouseList = warehousePage.getRecords();
        if (CollectionUtils.isNotEmpty(warehouseList)) {
            warehouseList.forEach(warehouse -> {
                try {
                    String qrcodeImgUrl = generateAndUploadQrcodeImg(warehouse, 5, qrCodeTempPath);
                    warehouse.setQrcodeImgUrl(qrcodeImgUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
        }
        return warehousePage;
    }

    private List<String> getNotExistObjIdList(List<String> objIdList, List<BuMaterialQrcode> qrcodeList) {
        List<String> existMaterialTypeIdList = qrcodeList.stream().map(BuMaterialQrcode::getObjId).collect(Collectors.toList());
        List<String> materialTypeIdListCopy = new ArrayList<>(objIdList);
        materialTypeIdListCopy.removeAll(existMaterialTypeIdList);
        return materialTypeIdListCopy;
    }

    private List<BuMaterialQrcode> getAndSaveQrcodeList(List<String> objIdList, Map<String, ? extends Model> idModelMap, int qrcodeType) throws Exception {
        List<BuMaterialQrcode> qrcodeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(objIdList)) {
            LambdaQueryWrapper<BuMaterialQrcode> qrcodeWrapper = new LambdaQueryWrapper<>();
            qrcodeWrapper.eq(BuMaterialQrcode::getObjType, qrcodeType)
                    .in(BuMaterialQrcode::getObjId, objIdList);
            qrcodeList = buMaterialQrcodeMapper.selectList(qrcodeWrapper);
        }

        String queryUrl = getConfigFileServiceUploadPath();
        MinioStorage.setUrl(queryUrl);
        MinioStorage.setSubPath("/标识码");

        String qrCodeTempPath = getTirosTempPath();

        List<String> notExistObjIdList = getNotExistObjIdList(objIdList, qrcodeList);
        for (String objId : notExistObjIdList) {
            String qrcodeImgUrl = generateAndUploadQrcodeImg(idModelMap.get(objId), qrcodeType, qrCodeTempPath);

            BuMaterialQrcode buMaterialQrcode = new BuMaterialQrcode()
                    .setObjType(qrcodeType)
                    .setObjId(objId)
                    .setPrint(0)
                    .setQrcodeImgUrl(qrcodeImgUrl);
            buMaterialQrcodeMapper.insert(buMaterialQrcode);
            qrcodeList.add(buMaterialQrcode);
        }

        return qrcodeList;
    }

    private String generateAndUploadQrcodeImg(Model model, int qrcodeType, String qrCodeTempPath) throws Exception {
        // 生成二维码
        String id = "";
        String name = "";
        String code = "";
        String qrcodeTypeName = "";
        String targetType = "";
        switch (qrcodeType) {
            case 1:
                qrcodeTypeName = "列管备件";
                BuMaterialSparePart sparePart = (BuMaterialSparePart) model;
                id = sparePart.getId();
                name = sparePart.getName();
                code = sparePart.getMaterialCode();
                targetType = "ZHOUZHUANJIAN";
                break;
            case 2:
                qrcodeTypeName = "工器具";
                BuMaterialTools tools = (BuMaterialTools) model;
                id = tools.getId();
                name = tools.getName();
                code = tools.getCode();
                targetType = "GONGQIJU";
                break;
            case 3:
                qrcodeTypeName = "托盘";
                BuMaterialPallet pallet = (BuMaterialPallet) model;
                id = pallet.getId();
                name = pallet.getName();
                code = pallet.getCode();
                targetType = "TPAN";
                break;
            case 4:
                qrcodeTypeName = "物资";
                BuMaterialType materialType = (BuMaterialType) model;
                id = materialType.getId();
                name = materialType.getName();
                code = materialType.getCode();
                targetType = "WUZI";
                break;
            case 5:
                qrcodeTypeName = "仓库";
                BuMtrWarehouse warehouse = (BuMtrWarehouse) model;
                id = warehouse.getId();
                name = warehouse.getName();
                code = warehouse.getWbs();
                targetType = "KWAI";
                break;
            default:
                break;
        }
        name = name.replaceAll("\r|\n", "");
        code = code.replaceAll("\r|\n", "");
        String qrcodeContent = String.format("{\"targetType\":\"%s\",\"code\":\"%s\",\"name\":\"%s\",\"id\":\"%s\"}", targetType, code, name, id);


        BufferedImage bufferedImage = QrCodeUtil.generate(qrcodeContent, 300, 300);
        bufferedImage = addNoteOneLine(bufferedImage, String.format("%s %s", name, qrcodeTypeName));
        bufferedImage = addNoteOneLine(bufferedImage, code);
        String formatName = "png";
        if (StringUtils.isBlank(qrCodeTempPath)) {
            qrCodeTempPath = getTirosTempPath();
        }
        File parentFile = FileUtil.mkdir(qrCodeTempPath + "/qrcode");
        if (!parentFile.exists()) {
            parentFile.createNewFile();
        }
        File file = FileUtil.newFile(parentFile.getPath() + "/" + id + "." + formatName);
        if (!file.exists()) {
            file.createNewFile();
        }

        ImageIO.write(bufferedImage, formatName, file);
        // return MinioStorage.storeImage(bufferedImage, qrcodeContent + "." + formatName, formatName);
        return "/qrcode/" + file.getName();
    }

    private IPage<BuMaterialQrcode> getPage(IPage page, List<BuMaterialQrcode> qrcodeList) {
        IPage<BuMaterialQrcode> qrcodePage = new Page<>();
        qrcodePage
                .setTotal(page.getTotal())
                .setSize(page.getSize())
                .setCurrent(page.getCurrent())
                .setPages(page.getPages())
                .setRecords(qrcodeList);
        return qrcodePage;
    }

    /**
     * 给二维码下方添加说明文字
     *
     * @param image 原二维码
     * @param note  说明文字
     * @return 带说明文字的二维码
     */
    private BufferedImage addNoteOneLine(BufferedImage image, String note) {
        Image src = image.getScaledInstance(300, 300, Image.SCALE_DEFAULT);
        BufferedImage tag = new BufferedImage(300, 322, BufferedImage.TYPE_INT_RGB);

        //设置低栏白边
        Graphics g1 = tag.getGraphics();
        //设置文字
        Graphics2D g2 = tag.createGraphics();
        Font font = new Font("微软雅黑", Font.BOLD, 18);
        int wordWidth = getWordWidth(font, note);
        g2.setFont(font);
        g2.setColor(Color.BLACK);
        g1.fillRect(0, 300, 300, 22);
        g2.drawString(note, (300 - wordWidth) / 2, 300 + font.getSize());
        g1.drawImage(src, 0, 0, null);
        g1.dispose();
        g2.dispose();
        image = tag;
        image.flush();
        return image;
    }

    /**
     * 得到该字体字符串的长度
     *
     * @param font    字体
     * @param content 字符串内容
     * @return 长度
     */
    private int getWordWidth(Font font, String content) {
        FontDesignMetrics metrics = FontDesignMetrics.getMetrics(font);
        int width = 0;
        for (int i = 0; i < content.length(); i++) {
            width += metrics.charWidth(content.charAt(i));
        }
        return width;
    }

    private String getConfigFileServiceUploadPath() {
        // 获取系统配置的文件服务上传路径
        String configCode = "fileServiceUploadPath";
        SysConfig sysConfig = sysConfigService.getById(configCode);
        if (null == sysConfig) {
            throw new JeecgBootException("无文件上传服务路径，请在系统配置中设置");
        }
        return sysConfig.getConfigValue();
    }

    private String getTirosTempPath() throws IOException {
//        // 获取系统配置的标识码图片暂存路径
//        String configCode = "qrCodeTempPath";
//        SysConfig sysConfig = sysConfigService.getById(configCode);
//        if (null == sysConfig) {
//            throw new JeecgBootException("无标识码暂存路径，请在系统配置中设置");
//        }
//        return sysConfig.getConfigValue();

        String directoryPath = "../tirosTemp";
        File directory = new File(directoryPath);
        boolean parentExists = directory.exists();
        System.out.println(parentExists);
        if (!parentExists) {
            boolean mkdir = directory.mkdir();
        }
        return directory.getCanonicalPath();
    }

}
