import { getAction, deleteAction, putAction, postAction, httpAction } from '@/api/manage'
import Vue from 'vue'

// 进度看板
const getFinishQuality = (params)=>getAction("/board/centerProgress/finishQuality",params);
const getBurnDownChartData = (params)=>getAction("/board/centerProgress/burnDownChartData",params);
const getListProgress = (params)=>getAction("/board/centerProgress/listProgress",params);

// 生产看板
const getAreaChart = (params)=>getAction("/board/centerCost/getAreaChart",params);
const getDepotCost = (params)=>getAction("/board/centerCost/getDepotCost",params);
const getMaterialCostTopTen = (params)=>getAction("/board/centerCost/getMaterialCostTopTen",params);
const getLastTenTrainCost = (params)=>getAction("/board/centerCost/getLastTenTrainCost",params);
const getLastTenTrainNo = (params)=>getAction("/board/centerCost/getLastTenTrainNo",params);

// 质量看板
const getDepotFault = (params)=>getAction("/board/centerQuality/getDepotFault",params);
const getFaultTrend = (params)=>getAction("/board/centerQuality/getFaultTrend",params);
const getSysFault = (params)=>getAction("/board/centerQuality/getSysFault",params);

// 进度看板-车间
const getTaskTrend = (params)=>getAction("/board/workshopProgress/getTaskTrend",params);
const getWorkGroupOrderInfo = (params)=>getAction("/board/workshopProgress/getWorkGroupOrderInfo",params);
const getListOrderTask = (params)=>getAction("/board/workshopProgress/listOrderTask",params);
const getListOutsourceOrderProgress = (params)=>getAction("/board/workshopProgress/listOutsourceOrderProgress",params);
const getListOutsourceTaskProgress = (params)=>getAction("/board/workshopProgress/listOutsourceTaskProgress",params);
const getListWorkGroupTaskProgress = (params)=>getAction("/board/workshopProgress/listWorkGroupTaskProgress",params);

// 生产看板-车间
const getCostItem = (params)=>getAction("/board/workshopCost/getCostItem",params);
const getCostItem2 = (params)=>getAction("/board/workshopCost/get-month-data",params);
const getWorkShopAreaCost=(params)=>getAction("/board/workshopCost/getAreaChart",params);
const getCostProportion = (params)=>getAction("/board/workshopCost/getCostProportion",params);
const getThresholdAlert = (params)=>getAction("/board/workshopCost/getThresholdAlert",params);

// 质量看板-车间
const getDayFaultTrend = (params)=>getAction("/board/workshopQuality/getDayFaultTrend",params);
const getFaultRanking = (params)=>getAction("/board/workshopQuality/getFaultRanking",params);
const getFaultSystemCount = (params)=>getAction("/board/workshopQuality/getFaultSystemCount",params);
const getLatestFault = (params)=>getAction("/board/workshopQuality/getLatestFault",params);
const getLatestOutsourceFault = (params)=>getAction("/board/workshopQuality/getLatestOutsourceFault",params);

// 工位看板-车间
const getWorkstation= (params)=>getAction("/board/workstation/list",params);
const getWorkstationTask = (params)=>getAction("/board/workstation/getTask",params);
const getWorkstationMustReplace = (params)=>getAction("/board/workstation/getMustReplace",params);
const getWorkstationFault = (params)=>getAction("/board/workstation/getFault",params);
const getWorkstationWorker= (params)=>getAction("/board/workstation/getWorker",params);
const getWorkstationInfo= (params)=>getAction("/board/workstation/get",params);
// 首页看板
const getAlert = (params)=>getAction("/board/homepage/data/item/alert",params);
const getData = (params)=>getAction("/board/homepage/data/item/data",params);
const getListLatest = (params)=>getAction("/board/homepage/fault/listLatest",params);
const getFaultInfo = (params)=>getAction("/board/homepage/fault/get",params);
const getAllPlans = (params) => getAction('/board/homepage/plan/list', params)

// 班组看板
// 班组工单进度
const getBoardOrder = (params) => getAction('/board/group/order/total/count', params)
// 班组人员排名
const getBoardPerson = (params) => getAction('/board/group/person/rank', params)
// 任务明细
const getBoardToday = (params) => getAction('/board/group/order/today/list', params)
// 物料预警
const getBoardMaterial = (params) => getAction('/board/group/material/lack', params)
// 工器具预警
const getBoardTool = (params) => getAction('/board/group/tool/check-alert', params)

export {
  getBoardTool,
  getBoardMaterial,
  getBoardToday,
  getBoardPerson,
  getBoardOrder,
  getFinishQuality,
  getBurnDownChartData,
  getListProgress,
  getAreaChart,
  getDepotCost,
  getMaterialCostTopTen,
  getLastTenTrainNo,
  getLastTenTrainCost,
  getDepotFault,
  getFaultTrend,
  getSysFault,
  getTaskTrend,
  getWorkGroupOrderInfo,
  getListOrderTask,
  getListOutsourceOrderProgress,
  getListWorkGroupTaskProgress,
  getCostItem,
  getCostItem2,
  getCostProportion,
  getWorkShopAreaCost,
  getThresholdAlert,
  getDayFaultTrend,
  getFaultRanking,
  getFaultSystemCount,
  getLatestFault,
  getLatestOutsourceFault,
  getAlert,
  getData,
  getListLatest,
  getFaultInfo,
  getAllPlans,
  getWorkstation,
  getWorkstationFault,
  getWorkstationMustReplace,
  getWorkstationTask,
  getWorkstationWorker,
  getWorkstationInfo,
  getListOutsourceTaskProgress
}