package org.jeecg;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.modules.third.common.LoginUser;
import org.jeecg.modules.third.jdx.service.*;
import org.jeecg.modules.third.maximo.bean.*;
import org.jeecg.modules.third.maximo.service.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 第三方接口测试
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-03-30
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class FromMaximoOnceTest {

    @Autowired
    private JdxFailurelistOutService jdxFailurelistOutService;
    @Autowired
    private BuFaultCodeNewThirdService buFaultCodeNewThirdService;
    @Autowired
    private JdxRealassetOutService jdxRealassetOutService;
    @Autowired
    private BuMaterialToolsThirdService buMaterialToolsThirdService;
    @Autowired
    private JdxInvbalancesOutService jdxInvbalancesOutService;
    @Autowired
    private BuMtrWarehouseThirdService buMtrWarehouseThirdService;
    @Autowired
    private BuMaterialTypeThirdService buMaterialTypeThirdService;
    @Autowired
    private JdxWoOutService jdxWoOutService;
    @Autowired
    private JdxMatusetransOutService jdxMatusetransOutService;
    @Autowired
    private JdxLabtransOutService jdxLabtransOutService;
    @Autowired
    private BuTrainHistoryWorkThirdService buTrainHistoryWorkThirdService;
    @Autowired
    private BuRptMaterialUseRecordThirdService buRptMaterialUseRecordThirdService;
    @Autowired
    private JdxSrOutService jdxSrOutService;
    @Autowired
    private BuTrainHistoryFaultThirdService buTrainHistoryFaultThirdService;
    @Autowired
    private JdxRealassettransOutService jdxRealassettransOutService;
    @Autowired
    private BuTrainHistoryChangeThirdService buTrainHistoryChangeThirdService;
    @Autowired
    private JdxAssetOutService jdxAssetOutService;
    @Autowired
    private BuTrainInfoThirdService buTrainInfoThirdService;
    @Resource
    private JdxProjectOutService jdxProjectOutService;
    @Resource
    private JdxLocationsOutService jdxLocationsOutService;
    @Resource
    private BuMaximoLocationThirdService buMaximoLocationThirdService;
    @Resource
    private BuMaximoTrainAssetThirdService buMaximoTrainAssetThirdService;
    @Resource
    private BuMaximoFinanceItemThirdService buMaximoFinanceItemThirdService;

    private final int PAGE_SIZE = 100000;


    /**
     * 获取登录人
     */
    @Test
    public void getUser() throws Exception {
        // 获取登录人信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        System.out.println(JSON.toJSONString(sysUser));
    }

    /**
     * 车辆--通过资产中数据导入车辆
     */
    @Test
    public void readTrain() throws Exception {
        String lineId = "1";
        String companyId = "YY1";
        String workshopId = "CJ1";

        buTrainInfoThirdService.deleteByLineId(lineId);

        int pageSize = PAGE_SIZE;
        int total = jdxAssetOutService.countTrainTotal(lineId);
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;

        while (currentPage <= totalPage) {
            IPage<JdxAssetOut> maximoPage = jdxAssetOutService.pageTrain(currentPage, pageSize, lineId);
            List<JdxAssetOut> maximoTrainList = maximoPage.getRecords();
            buTrainInfoThirdService.insertTrainFromMaximoData(maximoTrainList, lineId, companyId, workshopId);

            currentPage++;
        }
    }

    /**
     * 资产位置
     */
    @Test
    public void readAllLocation() throws Exception {
        List<JdxLocationsOut> maximoLocationsList = jdxLocationsOutService.listAll();
        buMaximoLocationThirdService.insertAllLocationFromMaximoData(maximoLocationsList, true);
    }

    /**
     * 资产设备
     */
    @Test
    public void readAllAsset() throws Exception {
        List<JdxAssetOut> maximoAssetList = jdxAssetOutService.listAll();
        buMaximoTrainAssetThirdService.insertAllAssetFromMaximoData(maximoAssetList, true);
    }

    /**
     * 财务项目
     */
    @Test
    public void readAllFinance() throws Exception {
        List<JdxProjectOut> maximoFinanceList = jdxProjectOutService.listAll();
        buMaximoFinanceItemThirdService.insertAllFinanceFromMaximoData(maximoFinanceList, true);
    }

    /**
     * 仓库--通过物资类型和库存中数据导入仓库
     */
    @Test
    public void readWarehouse() throws Exception {
        Map<String, List<String>> warehouseMap = jdxInvbalancesOutService.getAllWarehouse();
        // 导入到二级(架大修)
        buMtrWarehouseThirdService.insertWarehouseFromMaximoData(warehouseMap);
    }

    /**
     * 仓库--获取不应该存在的仓库信息（跟maximo对比后，获取3级库及以上）
     */
    @Test
    public void getShouldNotExistWarehouseIds() throws Exception {
        Map<String, List<String>> warehouseMap = jdxInvbalancesOutService.getAllWarehouse();
        // 跟maximo对比后，获取不应该存在的仓库id
        String notExistWarehouseIds = buMtrWarehouseThirdService.getShouldNotExistWarehouseIds(warehouseMap);
        System.out.println(notExistWarehouseIds);
    }

    /**
     * 物资类型和库存
     */
    @Test
    public void readMaterialAndStock() throws Exception {
        List<JdxInvbalancesOut> maximoStockList = new ArrayList<>();
        int pageSize = PAGE_SIZE;
        int total = jdxInvbalancesOutService.countTotal();
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;
        while (currentPage <= totalPage) {
            IPage<JdxInvbalancesOut> maximoPage = jdxInvbalancesOutService.pageMaterial(currentPage, pageSize);
            List<JdxInvbalancesOut> maximoPageList = maximoPage.getRecords();
            maximoStockList.addAll(maximoPageList);
            currentPage++;
        }

        buMaterialTypeThirdService.initConsumeMaximoStockData(maximoStockList);
    }

    /**
     * 工器具
     */
    @Test
    public void readTools() throws Exception {
        List<JdxRealassetOut> maximoToolList = new ArrayList<>();
        int pageSize = PAGE_SIZE;
        int total = jdxRealassetOutService.countTotal();
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;
        while (currentPage <= totalPage) {
            IPage<JdxRealassetOut> maximoPage = jdxRealassetOutService.pageTool(currentPage, pageSize);
            List<JdxRealassetOut> maximoPageList = maximoPage.getRecords();
            maximoToolList.addAll(maximoPageList);
            currentPage++;
        }

        buMaterialToolsThirdService.insertToolFromMaximoData(maximoToolList);
    }

    /**
     * 故障分类
     */
    @Test
    public void readAllFaultCode() throws Exception {
        List<JdxFailurelistOut> maximoFaultCodeList = jdxFailurelistOutService.listAll();
        buFaultCodeNewThirdService.insertAllFaultCodeFromMaximoData(maximoFaultCodeList);
    }

    /**
     * 工单
     */
    @Test
    public void readOrder() throws Exception {
        List<JdxWoOut> maximoOrderList = new ArrayList<>();
        int pageSize = PAGE_SIZE;
        int total = jdxWoOutService.countTotal();
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;
        while (currentPage <= totalPage) {
            IPage<JdxWoOut> maximoPage = jdxWoOutService.pageOrder(currentPage, pageSize);
            List<JdxWoOut> maximoPageList = maximoPage.getRecords();
            maximoOrderList.addAll(maximoPageList);
            currentPage++;
        }

        List<JdxAssetOut> maximoAssetList = jdxAssetOutService.listAssetOut(null);
        buTrainHistoryWorkThirdService.initConsumeMaximoOrderData(maximoOrderList, maximoAssetList);
    }

    /**
     * 工单物料
     */
    @Test
    public void readOrderMaterial() throws Exception {
        int pageSize = PAGE_SIZE;
        int total = jdxMatusetransOutService.countTotal();
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;

        while (currentPage <= totalPage) {
            IPage<JdxMatusetransOut> maximoPage = jdxMatusetransOutService.pageOrderMaterial(currentPage, pageSize);
            List<JdxMatusetransOut> maximoOrderMaterialList = maximoPage.getRecords();
            buRptMaterialUseRecordThirdService.insertOrderMaterialFromMaximoData(maximoOrderMaterialList);

            currentPage++;
        }
    }

    /**
     * 工单人员--暂时无操作
     */
    @Test
    public void readOrderUser() throws Exception {
        int pageSize = PAGE_SIZE;
        int total = jdxLabtransOutService.countTotal();
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;

        while (currentPage <= totalPage) {
            IPage<JdxLabtransOut> maximoPage = jdxLabtransOutService.pageOrderUser(currentPage, pageSize);
            List<JdxLabtransOut> maximoOrderUserList = maximoPage.getRecords();
            buTrainHistoryWorkThirdService.insertOrderUserFromMaximoData(maximoOrderUserList);

            currentPage++;
        }
    }

    /**
     * 故障
     */
    @Test
    public void readFault() throws Exception {
        List<JdxSrOut> maximoFaultList = new ArrayList<>();
        int pageSize = PAGE_SIZE;
        int total = jdxSrOutService.countTotal();
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;
        while (currentPage <= totalPage) {
            IPage<JdxSrOut> maximoPage = jdxSrOutService.pageFault(currentPage, pageSize);
            List<JdxSrOut> maximoPageList = maximoPage.getRecords();
            maximoFaultList.addAll(maximoPageList);
            currentPage++;
        }

        List<JdxAssetOut> maximoAssetList = jdxAssetOutService.listAssetOut(null);
        buTrainHistoryFaultThirdService.initConsumeMaximoFaultData(maximoFaultList, maximoAssetList);
    }

    /**
     * 换上换下
     */
    @Test
    public void readChange() throws Exception {
        List<JdxRealassettransOut> maximoChangeList = new ArrayList<>();
        int pageSize = PAGE_SIZE;
        int total = jdxRealassettransOutService.countTotal();
        int totalPage = (total / pageSize) + 1;
        int currentPage = 1;
        while (currentPage <= totalPage) {
            IPage<JdxRealassettransOut> maximoPage = jdxRealassettransOutService.pageChange(currentPage, pageSize);
            List<JdxRealassettransOut> maximoPageList = maximoPage.getRecords();
            maximoChangeList.addAll(maximoPageList);
            currentPage++;
        }

        List<JdxAssetOut> maximoAssetList = jdxAssetOutService.listAssetOut(null);
        buTrainHistoryChangeThirdService.initConsumeMaximoChangeData(maximoChangeList, maximoAssetList);
    }

}
