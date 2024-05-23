import { getAction, postAction } from '@/api/manage'

//车辆履历
const treeLineTrains = (params) => getAction('/produce/train/history/tree/line-train', params)
const listMaximoAssetChild = (params) => getAction('/produce/train/history/maximo-asset/list-child', params)
const getTrainInfo = (params) => getAction('/produce/train/history/train/get', params)
const getTrainTime = (params) => getAction('/produce/train/history/train/period', params)
const getMaximoAsset = (params) => getAction('/produce/train/history/maximo-asset/get', params)
const getWorkRecord = (params) => getAction('/produce/train/history/work/page', params)
const getMeasureRecord = (params) => getAction('/produce/train/history/measure/page', params)
const getFaultRecord = (params) => getAction('/produce/train/history/fault/page', params)
const getRelatedForm = (params) => getAction('/produce/train/history/forms/page', params)
const getChangeRecord = (params) => getAction('/produce/train/history/change/page', params)
const getTrainPeriod = (params) => getAction('/produce/train/history/train/period', params)
const getOpenItemRecord = (params) => getAction('/produce/train/history/workLeaveRecord/page', params)
const getRectifyRecord = (params) => getAction('/produce/train/history/workRectify/page', params)

// 故障监控
const getFaultCount = (params) => getAction('/produce/fault/detail/count', params)
const getFaultList = (params) => getAction('/produce/fault/detail/page', params)
const getFaultStatistics = (params) => getAction('/produce/fault/statistics', params)
const getFaultCompare = (params) => postAction('/produce/fault/compare', params)
const getEffectivenessData = (params) => getAction('/produce/fault/time-effect/data', params)

// 成本监控
const getCostCount = (params) => getAction('/produce/cost/detail/count', params)
const getCostPage = (params) => getAction('/produce/cost/detail/page', params)
const getCostStatistics = (params) => postAction('/produce/cost/statistics', params)
const getCostCountSixYear = (params) => getAction('/produce/cost/compare/count-6year', params)
const getCostCompare = (params) => postAction('/produce/cost/compare', params)

// 计划监控
const getPlanList = (params) => getAction('/produce/plan/list', params) // 查询列计划列表
const getPlanTaskList = (params) => getAction(`/produce/plan/task/listByPlanId/${params}`) // 查询列计划任务列表
const getPlanDetail = (params) => getAction(`/produce/plan/get/${params}`) // 查询指定列计划详情
const getTrainPlanDetail = (params) => getAction(`/serialPlan/plan/get/${params}`) // 查询指定任务详情
const getTrainTaskDetail = (params) => getAction(`/produce/plan/task/get/${params}`) // 查询列任务详情
const getPlanReguList = (params) => getAction(`/produce/plan/regu/list/${params}`) // 规程结构方式-查询规程进度列表
const getPlanTrainStructList = (params) => getAction(`/produce/plan/train-struct/list/${params}`) // 车辆结构方式-查询车辆结构进度列表
const getPlanTrainStructOrdersList = (params) => getAction(`/produce/plan/train-struct/orders/${params}`) // 车辆结构方式-查询工单列表

// 车间监控
const getWorkShopList = (params) => getAction('/workshop/list', params)
const getLastFaultList = (params) => getAction('/produce/workshop/fault/latest', params)
const getStationStatus = (params) => getAction('/produce/workshop/workstation/list', params)
const getTrainTrackInfo = (params) => getAction('/produce/workshop/train-track', params)

// 贡献度统计
const getKpiGroupList = (params) => getAction('/produce/kpi/group', params)
const getKpiUserList = (params) => getAction('/produce/kpi/user', params)
const getKpiPlanList = (params) => getAction('/produce/kpi/plan/list', params)
const getKpiPlanInfo = (params) => getAction('/produce/kpi/plan/get', params)

//
const getLackMaterial = (params) => getAction('/serialPlan/plan/material/lack', params)

// 列计划统计：工单和故障
const getLastWorkingPlan = (params) => getAction('/board/plan-group-statistic/get-last-working-plan', params)
const listPlanGroupStatistic = (params) => getAction('/board/plan-group-statistic/list', params)

export {
  treeLineTrains,
  listMaximoAssetChild,
  getTrainInfo,
  getTrainTime,
  getMaximoAsset,
  getFaultCount,
  getFaultList,
  getFaultCompare,
  getEffectivenessData,
  getFaultStatistics,
  getCostCount,
  getCostPage,
  getCostStatistics,
  getCostCountSixYear,
  getCostCompare,
  getPlanList,
  getPlanTaskList,
  getPlanDetail,
  getTrainPlanDetail,
  getTrainTaskDetail,
  getPlanReguList,
  getPlanTrainStructList,
  getPlanTrainStructOrdersList,
  getWorkShopList,
  getLastFaultList,
  getStationStatus,
  getFaultRecord,
  getRelatedForm,
  getMeasureRecord,
  getWorkRecord,
  getChangeRecord,
  getTrainTrackInfo,
  getKpiGroupList,
  getKpiUserList,
  getKpiPlanList,
  getTrainPeriod,
  getOpenItemRecord,
  getRectifyRecord,
  getKpiPlanInfo,
  getLackMaterial,
  getLastWorkingPlan,
  listPlanGroupStatistic
}