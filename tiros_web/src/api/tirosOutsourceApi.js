import { getAction, postAction } from '@/api/manage'
//委外管理

//厂商管理
const addSupplier = (params) => postAction('/outsource/supplier/add', params)
const editSupplier = (params) => postAction('/outsource/supplier/edit', params)
const getSupplierList = (params) => getAction('/outsource/supplier/page', params)
const delSupplier = (params) => postAction('/outsource/supplier/delete', params)

//合同管理
const addContractInfo = (params) => postAction('/outsource/contractInfo/add', params)
const editContractInfo = (params) => postAction('/outsource/contractInfo/edit', params)
const getContractInfoList = (params) => getAction('/outsource/contractInfo/page', params)
const delContractInfo = (params) => postAction('/outsource/contractInfo/delete', params)
const getContractMonitor = (params) => getAction('/outsource/contractInfo/monitor', params)
const getContractFiles = (params) => getAction('/outsource/contract/annex/list', params)
const saveContractFiles = (params) => getAction('/outsource/contract/annex/save', params)
const deleteContractFiles = (params) => postAction('/outsource/contract/annex/delete', params)
const getContractInfoPriceList = (params) => getAction('/outsource/contractInfo/price', params)

//支付管理
const addContractPay = (params) => postAction('/outsource/contractPay/add', params)
const editContractPay = (params) => postAction('/outsource/contractPay/edit', params)
const getContractPayList = (params) => getAction('/outsource/contractPay/page', params)
const delContractPay = (params) => postAction('/outsource/contractPay/delete', params)
//暂列金管理
const addContractBakMoney = (params) => postAction('/outsource/contractBakMoney/add', params)
const editContractBakMoney = (params) => postAction('/outsource/contractBakMoney/edit', params)
const getContractBakMoney = (params) => getAction('/outsource/contractBakMoney/page', params)
const delContractBakMoney = (params) => postAction('/outsource/contractBakMoney/delete', params)
//交接管理
const getOutinList = (params) => getAction('/outsource/outin/page', params)
const deleteOutIn = (params) => postAction('/outsource/outin/delete', params)
const getOutinDetailList = (params) => getAction('/outsource/outin/page/detail', params)

//监修管理
const addSupervise = (params) => postAction('/outsource/supervise/add', params)
const editSupervise = (params) => postAction('/outsource/supervise/edit', params)
const getSuperviseList = (params) => getAction('/outsource/supervise/page', params)
const delSupervise = (params) => postAction('/outsource/supervise/delete', params)
const getSuperviseById = (id) => getAction('/outsource/supervise/get', { id: id })
const getSuperviseByOrderTask = (param) => getAction('/outsource/supervise/get-by-order', param)
//执行管理
const vendorTree = (params) => getAction('/outsource/perform/vendor/tree', params)
const settingDelayReason = (params) => postAction('/outsource/perform/delayReason', params)
const getPerformList = (params) => getAction('/outsource/perform/page', params)
const delResource = (params) => postAction('/outsource/perform/delete/resource', params)
const getResource = (params) => getAction('/outsource/perform/resource', params)
const addResource = (params) => postAction('/outsource/perform/resource/add', params)
const addRateFile = (params) => postAction('/outsource/perform/rate/addFile', params)
const editResource = (params) => postAction('/outsource/perform/resource/edit', params)
const getRateList = (params) => getAction('/outsource/rateing/page', params)
const addRate = (params) => postAction('/outsource/rateing/add', params)
const editRate = (params) => postAction('/outsource/rateing/edit', params)
const delRate = (params) => postAction('/outsource/rateing/delete', params)
const delRateAttachment = (params) => postAction('/outsource/rateing/delete/attachment', params)
const uplaodFile = (params) => postAction('/outsource/perform/upload', params)
const delOtherResource = (params) => postAction('/outsource/otherSource/delete', params)
const editOtherResource = (params) => postAction('/outsource/otherSource/edit', params)
const addPerformOtherResource = (params) => postAction('/outsource/otherSource/add', params)
const getOtherResource = (params) => getAction('/outsource/otherSource/page', params)
const addOtherResource = (params) => postAction('/outsource/fault/countFaultGroupByPhaseAndSystem', params)

//委外质保
const getQuality = (params) => getAction('/outsource/quality/page', params)
const editQuality = (params) => postAction('/outsource/quality/edit', params)

//委外部件统计
const getParts = (params) => getAction('/outsource/part/page', params)
const getDownlocad = (params) => getAction('/fdfs/file/downloadFile', params)

//委外故障
const getcountFault = (params) => getAction('/outsource/fault/countFault', params)
const getcountFaultSystem = (params) => getAction('/outsource/fault/countFaultGroupBySystem', params)
const getFaultInfo = (params) => getAction('/outsource/fault/get', params)
const getFaultPage = (params) => getAction('/outsource/fault/page', params)
const setcountFaultPAS = (params) => postAction('/outsource/fault/countFaultGroupByPhaseAndSystem', params)

// 支付记录
const getPayRecordList = (params) => getAction('/outsource/contractPay/page', params)

// 委外培训记录
const getTrainingRecordList = (params) => getAction('/training/record/page', params)
const addTrainingRecordItem = (params) => postAction('/training/record/add', params)
const editTrainingRecordItem = (params) => postAction('/training/record/edit', params)
const delTrainingRecordItem = (params) => postAction('/training/record/delete', params)

// 财务项目列表
const getFinanceProject = (params) => getAction('/base/finance/project/list', params)

// 财务任务列表
const getFinanceTask = (params) => getAction('/base/finance/task/list', params)

// 委外成本分析
const getOutsourceCost = (params) => getAction('/outsource/cost/listCost', params)
// 
const getOutsourceDetail = (params) => getAction('/outsource/cost/detail', params)
// 
const getOutsourcePart = (params) => getAction('/outsource/cost/part', params)
// 
const getOutsourceAsset = (params) => getAction('/outsource/cost/asset', params)


export {
  addSupplier,
  editSupplier,
  getSupplierList,
  delSupplier,
  addContractInfo,
  editContractInfo,
  getContractInfoList,
  getContractInfoPriceList,
  delContractInfo,
  addContractPay,
  editContractPay,
  delContractPay,
  getContractPayList,
  addContractBakMoney,
  editContractBakMoney,
  delContractBakMoney,
  getContractBakMoney,
  getContractMonitor,
  getContractFiles,
  saveContractFiles,
  deleteContractFiles,
  getOutinList,
  deleteOutIn,
  getSuperviseList,
  delSupervise,
  getSuperviseById,
  getSuperviseByOrderTask,
  editSupervise,
  addSupervise,
  getOutinDetailList,
  getPerformList,
  getResource,
  addResource,
  delResource,
  settingDelayReason,
  vendorTree,
  getRateList,
  addRate,
  editRate,
  delRate,
  uplaodFile,
  delOtherResource,
  addOtherResource,
  getOtherResource,
  getQuality,
  getParts,
  getDownlocad,
  getcountFault,
  getcountFaultSystem,
  getFaultInfo,
  getFaultPage,
  setcountFaultPAS,
  editResource,
  addRateFile,
  editOtherResource,
  addPerformOtherResource,
  editQuality,
  getPayRecordList,
  delRateAttachment,
  getTrainingRecordList,
  addTrainingRecordItem,
  editTrainingRecordItem,
  delTrainingRecordItem,
  getFinanceProject,
  getFinanceTask,
  getOutsourceCost,
  getOutsourceDetail,
  getOutsourcePart,
  getOutsourceAsset,
}