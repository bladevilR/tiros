import { downloadFile, getAction, postAction, uploadAction } from '@/api/manage'
import qs from 'qs'

//交接车管理
const addExchange = (params) => postAction('/dispatch/exchange/add', params)
const delExchange = (params) => postAction('/dispatch/exchange/delete', params)
const editExchange = (params) => postAction('/dispatch/exchange/edit', params)
const editExchangeRemark = (params) => postAction('/dispatch/exchange/edit-remark', params)
const getExchangeList = (params) => getAction('/dispatch/exchange/page', params)
const getExChangeDetail = (params) => getAction('/dispatch/exchange/get', params)
const getExChangeByTrain = (params) => getAction('/dispatch/exchange/getByTrainNo', params)
const getListDeliverable = (params) => getAction('/dispatch/exchange/list-deliverable', params)

const getExChangeLeaveList = (params) => getAction('/dispatch/exchange/leave/page', params)
const addExChangeLeave = (params) => postAction('/dispatch/exchange/leave/add', params)
const editExChangeLeave = (params) => postAction('/dispatch/exchange/leave/edit', params)
const delExChangeLeave = (params) => postAction('/dispatch/exchange/leave/delete', params)
const getRectifyList = (params) => getAction('/dispatch/exchange/rectify/page', params)
const addRectify = (params) => postAction('/dispatch/exchange/rectify/add', params)
const editRectify = (params) => postAction('/dispatch/exchange/rectify/edit', params)
const delRectify = (params) => postAction('/dispatch/exchange/rectify/delete', params)
const getItemNo = (params) => getAction(`/dispatch/exchange/getItemNo/${params}`)
const getReveiveCarTrainNumber = () => getAction(`/dispatch/exchange/getTrainNumber/`)
const getReveiveCarTrainDetails = (params) => getAction(`/dispatch/plan/year/detail/list-by-year`, params)


// 远期规划
const addFarPlan = (params) => postAction('/dispatch/plan/far/add', params)
const editFarPlan = (params) => postAction('/dispatch/plan/far/edit', params)
const getFarPlanList = (params) => getAction('/dispatch/plan/far/page', params)
const delFarPlan = (params) => postAction('/dispatch/plan/far/delete', params)
const getFarPlanDetail = (params) => getAction('/dispatch/plan/far/get', params)
const getPlanAmount = (params) => getAction('/dispatch/plan/far/getPlanAmount', params)

// 年计划
const addYearPlan = (params) => postAction('/dispatch/plan/year/add', params)
const editYearPlan = (params) => postAction('/dispatch/plan/year/edit', params)
const getYearPlanList = (params) => getAction('/dispatch/plan/year/page', params)
const yearPlanExport = (fileName, params) => downloadFile('/dispatch/plan/year/yearPlanExport', fileName, params)
const delYearPlan = (params) => postAction('/dispatch/plan/year/delete', params)
const getYearPlanDetail = (params) => getAction('/dispatch/plan/year/get', params)
const yearPlanDetailAutoGenerate = (params) => getAction('/dispatch/plan/year/detail/autoGenerate', params)
const getYearPlanDetailGantt = (params) => getAction('/dispatch/plan/year/get-gantt', params)
const saveYearPlanDetailGantt = (params) => postAction('/dispatch/plan/year/detail/save', params)

// 列计划
const getTrainPlanList = (params) => getAction('/serialPlan/plan/page', params)
const addTrainPlan = (params) => postAction('/serialPlan/plan/add', params)
const delTrainPlan = (params) => postAction('/serialPlan/plan/delete', params)
const updateTrainPlan = (params) => postAction('/serialPlan/plan/edit', params)
const getTrainPlanTrains = (params) => getAction('/serialPlan/plan/exchange', params)
const getExchangeDetails = (params) => getAction('/dispatch/exchange/get', params)
const getTrainPlanDetail = (params) => getAction(`/serialPlan/plan/get/${params}`)
const getTrainPlanRelevanceInfo = (params) => getAction(`/serialPlan/plan/relevanceInfo/${params}`)
const getTrainPlanStartTime = (params) => getAction('/serialPlan/plan/startDate', params)
const getTrainPlanTemplates = (params) => getAction('/serialPlan/plan/tpPlan', params)
const getTrainPlanTaskDetail = (id) => getAction(`/serialplan/task/get/${id}`)
const addOrUpdateTrainPlanTask = (params) => postAction('/serialplan/task/saveOrUpdate', params)
const updateTrainPlanTaskNoAndWbs = (params) => postAction('/serialplan/task/updateTaskNoAndWbs', params)
const deleteTrainPlanTask = (params) => postAction('/serialplan/task/delete', qs.stringify(params))
const listTrainPlanForm = (params) => getAction('/serialPlan/plan/forms/list', params)
const pageTrainPlanForm = (params) => getAction('/serialPlan/plan/forms/page', params)
const addTrainPlanForm = (params) => postAction('/serialPlan/plan/forms/add', params)
const editTrainPlanForm = (params) => postAction('/serialPlan/plan/forms/edit', params)
const delTrainPlanForm = (params) => postAction('/serialPlan/plan/forms/delete', params)
const addTrainOrderGenrate = (params) => postAction('/task/order-generate', qs.stringify(params))
const stopTrainPlan = (params) => postAction('/serialPlan/plan/suspend', params)
const startTrainPlan = (params) => postAction('/serialPlan/plan/activate', params)
const getUnFinishList = (params) => getAction('/serialPlan/plan/unFinish/get', params)
const unFinishSetfinish = (params) => postAction('/serialPlan/plan/unFinish/finish', params)
const importHistoryPlanFromExcel = (params) => postAction('/serialPlan/plan/importExcel', params)
const importPlanHistoryCostDataFromExcel = (params) => postAction('/serialPlan/plan/plan-history-cost/import', params)


//工单管理
const addWorkOrder = (params) => postAction('/workorder/order/add', params)
const editWorkOrder = (params) => postAction('/workorder/order/edit', params)
const issueWorkOrder = (params) => postAction('/workorder/order/issuing', params)
const issueWorkOrderList = (params) => postAction('/workorder/order/submit-wf', params)
const closeWorkOrder = (params) => postAction('/workorder/order/close', params)
const approveCloseWorkOrder = (params) => postAction('/workorder/order/approveClose', params)
const getWorkOrderList = (params) => getAction('/workorder/order/page', params)
const delWorkOrder = (params) => postAction('/workorder/order/delete', params)
const getWorkOrderDetail = (params) => getAction(`/workorder/order/relevanceInfo/${params}`)
const deleteCheckRecord = (params) => postAction('/workGroup/workRecord/check-record/delete', params)
const getWorkOrderForms = (params) => getAction(`/workorder/order/forms`, params)
const createMaterialOrder = (params) => postAction('/workorder/order/add-material-apply-order', params)

const getWorkOrderAttached = (params) => getAction('/workorder/order/annex/list', params)
const addWorkOrderAttached = (params) => postAction('/workorder/order/annex/save', params)
const delWorkOrderAttached = (params) => postAction('/workorder/order/annex/delete', params)

const suspendWorkOrder = (params) => postAction('/workorder/order/suspend', params)
const activityWorkOrder = (params) => postAction('/workorder/order/activity', params)
const cancelWorkOrder = (params) => postAction('/workorder/order/cancel', params)
const updateFormTitleOrAsset = (params) => postAction('/serialPlan/plan/forms/edit/titleOrAsset', params)
const listPendingProductionNotice = (params) => getAction('/workorder/order/list-pending-production-notice', params)


//车号
const getTrainNo = (params) => getAction('/serialPlan/plan/exchange', params)

//故障管理
const addBreakdown = (params) => postAction('/dispatch/fault/info/add', params)
const cancelBreakdown = (params) => postAction('/dispatch/fault/info/cancel', params)
const delBreakdown = (params) => postAction('/dispatch/fault/info/delete', params)
const closeBreakdown = (params) => postAction('/dispatch/fault/info/close', params)
const editBreakdown = (params) => postAction('/dispatch/fault/info/edit', params)
const listFaultWfStatus = (params) => getAction('/dispatch/fault/info/list-fault-wfStatus', params)
const getBreakdownList = (params) => getAction('/dispatch/fault/info/page', params)
const getTrainAssetList = (params) => getAction('/trainAsset/listChildren', params)
const getBreakdownReason = (params) => getAction('/dispatch/fault/reason/list', params)
const getBreakdownSolution = (params) => getAction('/dispatch/fault/solution/list', params)
const getBreakdownInfo = (params) => getAction('/dispatch/fault/info/get', params)
const getBreakdownCategoryList = (params) => getAction('/dispatch/fault/category/page', params)
const getBreakdownCodeList = (params) => getAction('/dispatch/fault/code/page', params)
const getBreakdownReasonList = (params) => getAction('/dispatch/fault/reason/page', params)
const getBreakdownSolutionList = (params) => getAction('/dispatch/fault/solution/page', params)
const getFaultCodeLevel = (params) => getAction('/dispatch/fault/code/faultCodeLevel', params)
const getFaultdetail = (params) => getAction('/fault/faultCodeNew/parent/get', params)

const getFaultCodeNew = (params) => getAction('/fault/faultCodeNew/get', params)
const getFaultCodeListNew = (params) => getAction('/fault/faultCodeNew/list', params)


//工装运用、保养
const addToolplan = (params) => postAction('/dispatch/toolplan/add', params)
const delToolplan = (params) => postAction('/dispatch/toolplan/delete', params)
const editToolplan = (params) => postAction('/dispatch/toolplan/edit', params)
const getToolplanList = (params) => getAction('/dispatch/toolplan/page', params)

// 特种设备运用、保养
const getSpecasset = (params) => getAction('/material/specasset/page', params)
const addSpecassetPlan = (params) => postAction('/dispatch/specasset-plan/add', params)
const editSpecassetPlan = (params) => postAction('/dispatch/specasset-plan/edit', params)
const getSpecassetPlanList = (params) => getAction('/dispatch/specasset-plan/page', params)
const delSpecassetPlan = (params) => postAction('/dispatch/specasset-plan/delete', params)


//现场影音
// const addToolplan = (params) => postAction("/dispatch/toolplan/add", params);
// const delToolplan = (params) => postAction("/dispatch/toolplan/delete", params);
// const editToolplan = (params) => postAction("/dispatch/toolplan/edit", params);
const getPlayContent = (params) => getAction('/dispatch/display/config/getPlayContent', params)
const getPlayContentWork = (params) => getAction('/dispatch/display/config/getPlayContent', params)
const getPlayScreenList = (params) => getAction('/dispatch/display/screen/list', params)
const getPlayResourceList = (params) => getAction('/dispatch/display/resource/list', params)
const setPlayResourceToScreen = (params) => postAction('/dispatch/display/config/setResourceToScreen', params)
const setPlayCustomToScreen = (params) => uploadAction('/dispatch/display/config/setCustomToScreen', params)
const deletePlayCustomToScreen = (params) => postAction('/dispatch/display/config/delete', params)


export {
  addExchange,
  delExchange,
  editExchange,
  editExchangeRemark,
  getExchangeList,
  getExChangeDetail,
  addFarPlan,
  editFarPlan,
  getFarPlanList,
  getFarPlanDetail,
  delFarPlan,
  getPlanAmount,
  addYearPlan,
  editYearPlan,
  getYearPlanList,
  yearPlanExport,
  delYearPlan,
  getYearPlanDetail,
  getYearPlanDetailGantt,
  saveYearPlanDetailGantt,
  addWorkOrder,
  createMaterialOrder,
  issueWorkOrder,
  issueWorkOrderList,
  closeWorkOrder,
  approveCloseWorkOrder,
  editWorkOrder,
  getWorkOrderList,
  delWorkOrder,
  getWorkOrderDetail,
  deleteCheckRecord,
  getTrainNo,
  addBreakdown,
  cancelBreakdown,
  closeBreakdown,
  delBreakdown,
  editBreakdown,
  listFaultWfStatus,
  getBreakdownList,
  getTrainAssetList,
  addToolplan,
  delToolplan,
  editToolplan,
  getToolplanList,
  getSpecasset,
  addSpecassetPlan,
  editSpecassetPlan,
  getSpecassetPlanList,
  delSpecassetPlan,
  getPlayContent,
  getPlayContentWork,
  getTrainPlanList,
  addTrainPlan,
  delTrainPlan,
  updateTrainPlan,
  getTrainPlanTrains,
  getExchangeDetails,
  getTrainPlanDetail,
  getTrainPlanRelevanceInfo,
  getTrainPlanStartTime,
  getTrainPlanTemplates,
  getTrainPlanTaskDetail,
  addOrUpdateTrainPlanTask,
  updateTrainPlanTaskNoAndWbs,
  deleteTrainPlanTask,
  addTrainOrderGenrate,
  stopTrainPlan,
  startTrainPlan,
  getUnFinishList,
  unFinishSetfinish,
  importHistoryPlanFromExcel,
  importPlanHistoryCostDataFromExcel,
  listTrainPlanForm,
  pageTrainPlanForm,
  addTrainPlanForm,
  editTrainPlanForm,
  delTrainPlanForm,
  yearPlanDetailAutoGenerate,
  getBreakdownReason,
  getBreakdownSolution,
  getBreakdownInfo,
  getExChangeLeaveList,
  addExChangeLeave,
  editExChangeLeave,
  delExChangeLeave,
  getRectifyList,
  addRectify,
  editRectify,
  delRectify,
  getBreakdownCategoryList,
  getBreakdownCodeList,
  getBreakdownReasonList,
  getBreakdownSolutionList,
  getFaultCodeLevel,
  getFaultdetail,
  getExChangeByTrain,
  getListDeliverable,
  getPlayScreenList,
  getPlayResourceList,
  setPlayResourceToScreen,
  setPlayCustomToScreen,
  deletePlayCustomToScreen,
  getWorkOrderAttached,
  addWorkOrderAttached,
  delWorkOrderAttached,
  getWorkOrderForms,
  getItemNo,
  getReveiveCarTrainNumber,
  getReveiveCarTrainDetails,
  getFaultCodeNew,
  getFaultCodeListNew,
  suspendWorkOrder,
  activityWorkOrder,
  updateFormTitleOrAsset,
  listPendingProductionNotice,
  cancelWorkOrder
}
