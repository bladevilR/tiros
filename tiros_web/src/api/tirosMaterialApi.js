import { downFile, downloadFile, getAction, postAction } from '@/api/manage'
import qs from 'qs'
// 物资管理
const getMustMaterial = (params) => getAction('/material/listMust', params)
const getMaterialList = (params) => getAction('/material/list', params)
const addMaterialType = (params) => postAction('/material/add', params)
const editMaterialType = (params) => postAction('/material/edit', params)
const deleteMaterialType = (params) => postAction('/material/delete', params)
const getToolTypeList = (params) => getAction('/material/list/tool', params)
const getMaterialType = (params) => getAction('/material/get', params)
const getMaterialToolType = (params) => getAction('/material/get/tool', params)
const getToolsList = (params) => getAction('/material/tools/page', params)
const listMaterialCanReplace = (params) => getAction('/material/can-replace/list', params)
const saveMaterialCanReplace = (params) => postAction('/material/can-replace/save', params)

// 仓库设置
const addWarehouse = (params) => postAction('/material/warehouse/add', params)
const editWarehouse = (params) => postAction('/material/warehouse/edit', params)
const getWarehouseSetupList = (params) => getAction('/material/warehouse/page', params)
const delWarehouseSetup = (params) => postAction('/material/warehouse/delete', params)
const closeWarehouse = (params) => postAction('/material/warehouse/close', params)
const importWarehouse = (params) => postAction('/material/warehouse/importExcel', params)
const listWarehouseChildren = (params) => getAction('/material/warehouse/list-child', params)
const getWarehouseAllTree = () => getAction('/material/warehouse/allTree')


const getWarehouseOne = (params) => getAction('/material/warehouse/get', params)

//托盘设置
const addPallet = (params) => postAction('/material/pallet/add', params)
const editPallet = (params) => postAction('/material/pallet/edit', params)
const getPalletList = (params) => getAction('/material/pallet/page', params)
const delPallet = (params) => postAction('/material/pallet/delete', params)
const bindMType = (params) => postAction('/material/pallet/set-materialTypes', params)


//列管备件
const addRotables = (params) => postAction('/material/spare-part/add', params)
const delRotables = (params) => postAction('/material/spare-part/delete', params)
const editRotables = (params) => postAction('/material/spare-part/edit', params)
const getRotablesInfo = (params) => getAction('/material/spare-part/get', params)
const getRotablesList = (params) => getAction('/material/spare-part/page', params)
const getSystemName = (params) => getAction('trainAssetType/system', params)

//周转件
const pageTurnover = (params) => getAction('/material/turnover/page', params)
const getTurnover = (params) => getAction('/material/turnover/get', params)
const saveTurnover = (params) => postAction('/material/turnover/save', params)
const deleteTurnoverBatch = (params) => postAction('/material/turnover/delete', params)
const importTurnoverFromExcel = (params) => postAction('/material/turnover/importExcel', params)

//标识码管理
const selectPallet = (params) => getAction('/material/qrcode/pagePallet', params)
const selectWarehouse = (params) => getAction('/material/qrcode/pageWarehouse', params)

// todo 入库管理
//入库查看（old）
const getPutStock = (params) => getAction('/material/entry/page', params)
const editPutStock = (params) => postAction('/material/entry/confirmEntry', params)
const entryPutStock = (params) => postAction('/material/entry/confirm/levelFour', params)
const entryViewStock = (params) => getAction('/material/entry/detail/get', params)


// 入库管理（new）
const getPutStockList = (params) => getAction('/material/entry/detail/page', params)
const savePutStockItem = (params) => postAction('/material/entry/save', params)
const delPutStockItem = (params) => postAction('/material/entry/detail/delete', params)
const importMaterialEntryOrder = (params) => postAction('/import/materialEntryOrder', params)

//领用管理
const getApplyList = (params) => getAction('/material/apply/page', params)
const addApply = (params) => postAction('/material/apply/add', params)
const editApply = (params) => postAction('/material/apply/edit', params)
const delApply = (params) => postAction('/material/apply/delete', params)
const getApplyInfo = (params) => getAction('/material/apply/detail/listByApplyId', params)
const applyReadyConfirm = (params) => postAction('/material/apply/readyConfirm', params)
const applyReceiveConfirm = (params) => postAction('/material/apply/receive-confirm', qs.stringify(params))
const getStockList = (params) => getAction('/material/stock/list', params)
const getApplyAssign = (params) => getAction('/material/apply/assign/list', params)
const getApplyMustDetaial = (params) => getAction('/workorder/order/material/list-must-as-applyDetail', params)
const getApplyMustAssign = (params) => getAction('/workorder/order/list-material-assign', params)
const autoArrangeHouse = (params) => postAction('/material/apply/assign/auto-assign', params)


//借用管理
const getBorrow = (params) => getAction('/material/borrow/page', params)
const addBorrow = (params) => postAction('/material/borrow/add', params)
const editBorrow = (params) => postAction('/material/borrow/edit', params)
const delBorrow = (params) => postAction('/material/borrow/delete', params)
const confirmBorrowReturn = (params) => postAction('/material/borrow/return', params)
const getBorrowDetail = (params) => getAction(`/material/borrow/detail/${params}`)
const downloadBorrow = (fileName, params) => downloadFile(`/material/borrow/export`, fileName, params)
const borrowExport = (fileName, params) => downloadFile(`/material/borrow/borrowExport`, fileName, params)


//库存管理
const pageStock = (params) => getAction('/material/stock/page', params)
const getStockAttr = (params) => getAction('/material/stock/attr/get', params)
const getStockTradeAttr = (params) => getAction('/material/stock/trade-attr/get', params)
const editStockAttr = (params) => postAction('/material/stock/attr/set', params)
const editStockTradeAttr = (params) => postAction('/material/stock/trade-attr/set', params)
const getStockSum = (params) => getAction('/material/stock/sum/get-amount', params)
const getGroupStockSum = (params) => getAction('/material/stock/group/get-amount', params)
const importStock = (params) => postAction('/material/stock/import-level4-stock', params)


//工艺装备
const addEquipment = (params) => postAction('/material/tools/add', params)
const delEquipment = (params) => postAction('/material/tools/delete', params)
const editEquipment = (params) => postAction('/material/tools/edit', params)
const getlistNeedCheck = (params) => getAction('/material/tools/listNeedCheck', params)
const getEquipmentList = (params) => getAction('/material/tools/page', params)
const getNeedCheckcount = (params) => getAction('/material/tools/countNeedCheck', params)
const setResponsible = (params) => postAction('/material/tools/setResponsible', params)
const setNextCheckTime = (params) => postAction('/material/tools/setNextCheckTime', params)
const setLastSelfCheckTime = (params) => postAction('/material/tools/setLastSelfCheckTime', params)
const setSelfCheck = (params) => postAction('/material/tools/setSelfCheck', params)
const exportEquipment = (fileName, params) => downloadFile(`/material/tools/export`, fileName, params)

//预警查看
const getEarlyWarningList = (params) => getAction('/material/alert/page', params)
const exportymonthEarly = (params) => downloadFile('/material/alert/exportMonth', params)
const exportyweekEarly = (params) => downloadFile('/material/alert/exportWeek', params)
const exportTheshold = (params) => downloadFile('/material/alert/exportTheshold', params)


// 领料明细
const getReceiveList = (params) => getAction('/report/cost/receive/detail/page', params)
const exportEXFile = (params) => downFile('/report/cost/receive/detail/export', params)

// 必换件清单
const getWillChangeList = (params) => getAction('/material/must/list', params)
const saveWillChangeItem = (params) => postAction('/material/must/add', params)
const editWillChangeItem = (params) => postAction('/material/must/edit', params)
const delWillChangeItem = (params) => postAction('/material/must/delete', params)
const setWillChangeGroup = (params) => postAction('/material/must/set-group', params)
const validMustList = (params) => postAction('/material/must/valid', params)
const invalidMustList = (params) => postAction('/material/must/invalid', params)


// 退料管理
const getReturnedList = (params) => getAction('/material/returned/page', params)
const getReturnedItem = (params) => getAction('/material/returned/get', params)
const saverReturnedItem = (params) => postAction('/material/returned/save', params)
const delReturnedItem = (params) => postAction('/material/returned/delete', params)
const confirmReturnedItem = (params) => postAction('/material/returned/confirm', params)
const getDetailListByOrderId = (params) => getAction('/material/apply/detail/listByOrderId', params)

// 物资计划
const getAllMaterialPlanList = (params) => getAction('/material/plan/list/all', params)
const getPartMaterialPlanList = (params) => getAction('/material/plan/list/part', params)
const addMaterialPlanItem = (params) => postAction('/material/plan/add', params)
const editMaterialPlanItem = (params) => postAction('/material/plan/edit', params)
const deleteMaterialPlanItem = (params) => postAction('/material/plan/delete', params)
const exportMaterialPlanEXFile = (params) => downFile('/material/plan/export', params)
const sumRepairPlanYearDetailAmount = (params) => getAction('/dispatch/plan/year/sum-year-plan-detail-amount', params)
const getMaterialYearTrain = (params) => getAction('/material/plan/materialYearTrain', params)

//物资消耗对比
const listMaterialCostCompare = (params) => getAction('/material/cost/compare/list', params)
const getMaterialCostCompareDetail = (params) => getAction('/material/cost/compare/detail', params)
const verifyBatch = (params) => postAction('/material/cost/verify-batch', params)
const verifyCheck = (params) => postAction('/material/cost/verify', params)
const verifyOrderMaterialList = (params) => postAction('/material/cost/verify-orderMaterialList', params)
const materialCostCompareExport = (params) => downFile('/material/cost/compare/export', params)

// 列计划物料核实
const planCostVerifyPage = (params) => getAction('/material/plan-cost/page', params)
const planCostDetails = (params) => getAction('/material/plan-cost/get', params)
const planCostDetailsSave = (params) => postAction('/material/plan-cost/save', params)

// 班组库存
const getGroupStockList = (params) => getAction('/material/stock/group/page', params)
const listGroupStockForTaskWrite = (params) => getAction('/material/stock/group/list-for-taskWrite', params)
const groupStockExport = (fileName, params) => downloadFile(`/material/stock/group/export`, fileName, params)
const toolSetElectric = (params) => postAction(`/material/tools/setElectric`, params)
const setGroupStockAttribute = (params) => postAction(`/material/stock/group/setAttribute`, params)
const recoverGroupStockAttribute = (params) => postAction(`/material/stock/group/recoverAttribute`, params)
const transGroupStockToTurnover = (params) => postAction(`/material/stock/group/trans-to/turnover`, params)

// 特种设备
const getSpecassetList = (params) => getAction('/material/specasset/page', params)
const setSpecassetItem = (params) => postAction('/material/specasset/save', params)
const deleteSpecassetItem = (params) => postAction('/material/specasset/delete', params)
const getSpecassetplan = (params) => getAction('/dispatch/specasset-plan/get-month-usage', params)


export {
  getMaterialList,
  getToolTypeList,
  getMaterialType,
  getMaterialToolType,
  getToolsList,
  listMaterialCanReplace,
  saveMaterialCanReplace,
  getMustMaterial,
  addMaterialType,
  editMaterialType,
  deleteMaterialType,
  addWarehouse,
  editWarehouse,
  getWarehouseSetupList,
  closeWarehouse,
  delWarehouseSetup,
  importWarehouse,
  addPallet,
  editPallet,
  getPalletList,
  delPallet,
  bindMType,
  selectPallet,
  listWarehouseChildren,
  addRotables,
  delRotables,
  editRotables,
  getRotablesInfo,
  getRotablesList,
  pageTurnover,
  getTurnover,
  saveTurnover,
  deleteTurnoverBatch,
  importTurnoverFromExcel,
  getPutStock,
  editPutStock,
  entryPutStock,
  entryViewStock,
  // 入库管理（new）
  getPutStockList,
  savePutStockItem,
  delPutStockItem,
  getWarehouseOne,
  selectWarehouse,
  getSystemName,
  delEquipment,
  editEquipment,
  getlistNeedCheck,
  setNextCheckTime,
  addEquipment,
  getEquipmentList,
  getNeedCheckcount,
  setResponsible,
  getEarlyWarningList,
  getBorrow,
  getApplyInfo,
  getApplyList,
  delApply,
  applyReadyConfirm,
  applyReceiveConfirm,
  editApply,
  addApply,
  addBorrow,
  editBorrow,
  delBorrow,
  confirmBorrowReturn,
  getBorrowDetail,
  downloadBorrow,
  borrowExport,
  pageStock,
  getStockAttr,
  getStockTradeAttr,
  editStockAttr,
  editStockTradeAttr,
  exportymonthEarly,
  exportyweekEarly,
  exportTheshold,
  getWarehouseAllTree,
  getStockList,
  getApplyAssign,
  autoArrangeHouse,
  getReceiveList,
  getApplyMustDetaial,
  getApplyMustAssign,
  exportEXFile,
  getWillChangeList,
  saveWillChangeItem,
  editWillChangeItem,
  delWillChangeItem,
  setWillChangeGroup,
  validMustList,
  invalidMustList,
  getReturnedList,
  getReturnedItem,
  saverReturnedItem,
  delReturnedItem,
  confirmReturnedItem,
  getAllMaterialPlanList,
  getPartMaterialPlanList,
  addMaterialPlanItem,
  editMaterialPlanItem,
  deleteMaterialPlanItem,
  exportMaterialPlanEXFile,
  sumRepairPlanYearDetailAmount,
  getDetailListByOrderId,
  listMaterialCostCompare,
  verifyBatch,
  verifyCheck,
  verifyOrderMaterialList,
  getMaterialCostCompareDetail,
  getGroupStockList,
  listGroupStockForTaskWrite,
  setLastSelfCheckTime,
  setSelfCheck,
  getStockSum,
  getGroupStockSum,
  importStock,
  getSpecassetList,
  setSpecassetItem,
  deleteSpecassetItem,
  getSpecassetplan,
  materialCostCompareExport,
  planCostVerifyPage,
  planCostDetails,
  planCostDetailsSave,
  groupStockExport,
  toolSetElectric,
  exportEquipment,
  setGroupStockAttribute,
  recoverGroupStockAttribute,
  transGroupStockToTurnover,
  getMaterialYearTrain
}