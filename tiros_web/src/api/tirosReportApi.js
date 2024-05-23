import { getAction, postAction, downloadFile } from '@/api/manage'


const getReportTemplate = (tempCode) => getAction("/report/excel/get", { tempCode: tempCode })
const saveReportTemplate = (params) => postAction('/report/excel/save', params)

// const downloadFile = (excelJson) => postAction('/report/excel/downfile', { exceldata: excelJson })
// 生产日报
const getDailyReport = (params) => getAction('/report/daily/get', params)
const createDailyReport = (params) => getAction('/report/daily/generate', params)
const saveDailyReport = (params) => postAction('/report/daily/save', params)

// 故障汇总
const getTrainFaultSum = (params) => getAction("/report/fault/count/trains", params)
const getSystemFaultSum = (params) => getAction("/report/fault/count/systems", params)

//故障明细
const getTrainFaultDetails = (params) => getAction("/report/fault/detail/list", params)
const getTrainFaultList = (params) => getAction("/report/fault/detail/list", params)
const getTrainFaultDetailsExprot = (params) => getAction("/report/fault/detail/export", params)

//消耗物料明细
const getTrainMaterialDetail = (params) => getAction('/report/cost/detail/statistic-consume', params)
const getTrainMaterialMustDetail = (params) => getAction('/report/cost/detail/statistic-must-random', params)


// 周转件明细
const getTrainRotableDetail = (params) => getAction('/report/turnover/detail/page', params)

// 人工成本
const getTrainCostKpiDetail = (params) => getAction('/report/cost/kpi/page', params)
const downloadTrainCostKpiDetail = (fileName, params) => downloadFile('/report/cost/kpi/export', fileName, params)



// 获取单列车故障汇总数据
const getOneTrainFaultSum = (params) => getAction('/report/fault/train-count/sys', params)

// 查询车辆累计消耗金额
const getTrainMaterialSum = (params) => getAction('/report/cost/train/statistic', params)

const getTrainCars = (params) => getAction('/report/cost/train/get-cars-number', params)

// 维修列计划总结
// 维修成本情况
const getSummaryCost = (params) => getAction('/produce/plan/summary/cost', params)
// 维修质量控制情况
const getSummaryFault = (params) => getAction('/produce/plan/summary/fault', params)
// 总体情况
const getSummaryDetail = (params) => getAction('/produce/plan/summary/total', params)
// 维修作业工时控制情况
const getSummaryWorkTime = (params) => getAction('/produce/plan/summary/work-time', params)
// 维修周期控制情况
const getSummaryPeriod = (params) => getAction('/produce/plan/summary/period', params)
// 委外维修部件维修进度控制情况
const getSummaryOutsource = (params) => getAction('/produce/plan/summary/outsource', params)
// 生产进度控制情况
const getSummaryProgress = (params) => getAction('/produce/plan/summary/progress', params)


export {
  getDailyReport,
  createDailyReport,
  saveDailyReport,
  getReportTemplate,
  getTrainFaultSum,
  getSystemFaultSum,
  getOneTrainFaultSum,
  getTrainMaterialSum,
  getTrainMaterialDetail,
  getTrainFaultDetails,
  getTrainFaultList,
  getTrainFaultDetailsExprot,
  getTrainRotableDetail,
  getTrainCostKpiDetail,
  downloadTrainCostKpiDetail,
  saveReportTemplate,
  getTrainCars,
  getTrainMaterialMustDetail,
  getSummaryCost,
  getSummaryFault,
  getSummaryDetail,
  getSummaryWorkTime,
  getSummaryPeriod,
  getSummaryOutsource,
  getSummaryProgress,
}