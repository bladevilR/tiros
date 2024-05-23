import { getAction, postAction } from '@/api/manage'
import qs from 'qs'

//故障管理
const subGroupFault = (params) => postAction('/group/fault/info/submit', params)
const cancelGroupFault = (params) => postAction('/group/fault/info/cancel', params)
const delGroupFault = (params) => postAction('/group/fault/info/delete', params)
const editGroupFault = (params) => postAction('/group/fault/info/edit', params)
const getGroupFaultList = (params) => getAction('/group/fault/info/page', params)
const getGroupFaultInfo = (params) => getAction('/group/fault/info/get', params)

//安全预想
const addSafeassumeRead = (params) => postAction('/group/safeassume/read/add', params)
const addSafeassume = (params) => postAction('/group/safeassume/add', params)
const delSafeassume = (params) => postAction('/group/safeassume/delete', params)
const editSafeassume = (params) => postAction('/group/safeassume/edit', params)
const getSafeassumeList = (params) => getAction('/group/safeassume/page', params)
const getSafeassumeDetail = (params) => getAction('/group/safeassume/get', params)

//周转件更换
const getRotableChangeList = (params) => getAction('/group/turnoverchange/page', params)
const getRotableChangeInfo = (params) => getAction('/material/spare-part/get', params)
const delRotableChange = (params) => postAction('/group/turnoverchange/delete', params)
const editRotableChange = (params) => postAction('/group/turnoverchange/edit', params)


//测量修复
const getMeasurealertList = (params) => getAction('/group/measurealert/page', params)
const closeMeasurealert = (params) => postAction('/group/measurealert/close', params)

//列管备件管理
const getRotablesManageList = (params) => getAction('/group/spare-part/page', params)
const getRotableManageInfo = (params) => getAction('/group/spare-part/get', params)
const getRotableManageUseRecord = (params) => getAction('/produce/train/history/asset/use/page', params)

//工装管理
const getToolEquipmentList = (params) => getAction('/group/toolequipment/page', params)
const getToolEquipmentInfo = (params) => getAction('/group/toolequipment/get', params)
const setToolEquipment = (params) => postAction('/group/toolequipment/setStatus', params)
const equipmentUseRecord = (params) => getAction('/group/toolequipment/usage', params)

//工器具管理
const getToolList = (params) => getAction('/group/tool/page', params)
const getToolInfo = (params) => getAction('/group/tool/get', params)
const setTool = (params) => postAction('/group/tool/setDutyUser', params)
const toolUseRecord = (params) => getAction('/group/tool/usage', params)

//综合信息管理
const getGroupBasicInfo = (params) => getAction('/group/information/basic', params)
const addTools = (params) => postAction('/group/information/tools/add', params)
const deleteTools = (params) => postAction('/group/information/tools/delete', params)
const pageRelatedWorkstation = (params) => getAction('/group/information/workstation/page-related', params)
const pageUnRelatedWorkstation = (params) => getAction('/group/information/workstation/page-unrelated', params)
const addWorkstation = (params) => postAction('/group/information/workstation/add', params)
const deleteWorkstation = (params) => postAction('/group/information/workstation/delete', params)
// 工单管理
const editWorkOrder = (params) => postAction('/workGroup/workOrder/edit', params)
const getWorkOrderDetail = (params) => getAction(`/workGroup/workOrder/relevanceInfo/${params}`)
const submitOrderToDispatcher = (params) => postAction('/workGroup/workOrder/submit-to-dispatcher', qs.stringify(params))

// 我的作业
const getWorkTaskList = (params) => getAction('/workGroup/workRecord/list', params)

const getTrainPlanOnlineFormById = (params) => getAction('/serialPlan/plan/forms/online-form/get', params)
const getTrainPlanRecordFormById = (params) => getAction('/serialPlan/plan/forms/work-record/get', params)
const getTrainPlanCheckRecordForm = (params) => getAction(`/serialPlan/plan/forms/check-record/get`, params)

const getWorkOrderList = (params) => getAction('/workGroup/workOrder/page', params)
const getApplyWorkOrderList = (params) => getAction('/workGroup/workOrder/apply/page', params)
const getTaskRecordForm = (params) => getAction('/workGroup/workRecord/recordForm', params)
const getTaskRelevanceInfo = (params) => getAction(`/workGroup/workRecord/relevanceInfo/${params}`)
const getTaskBookSteps = (params) => getAction(`/workGroup/workRecord/relevanceInfo/bookSteps/${params}`)
const saveFormValue = (params) => postAction('/workGroup/workRecord/save/form-result', params)
const saveCheck = (params) => postAction('/workGroup/workRecord/check', params)
const getTaskQRCode = (params) => postAction('/workGroup/workRecord/getTaskQRCode', params)
const saveRcordTableAssetNo = (params) => postAction('/workGroup/workRecord/work-record/save-manufNo',  qs.stringify(params))
const saveMaterial = (params) => postAction('/workGroup/workRecord/save/material', params)
const actMaterial = (params) => postAction('/workGroup/workRecord/save/actMaterial', params)
const saveWorkTime = (params) => postAction('/workGroup/workRecord/save/work-time', params)
const delWorkArrange = (params) => postAction('/workGroup/workRecord/arrange/delete', params)
const getMaterialList = (params) => getAction(`/workGroup/workRecord/list/material`, params)
const commitTempOrderMaterial = (params) => postAction('/workGroup/workOrder/material/commit-temp-apply', params)
const getMaterialApplyList = (params) => getAction(`/workGroup/workOrder/material/apply/list`, params)
const saveWorkRecordExcelData = (params) => postAction('/workGroup/workRecord/check/saveExcelData', params)
const getWorkRecordExcelData = (formInstId) => getAction(`/workGroup/workRecord/get/excelData/${formInstId}`)

const getCheckRecordDetail = (params) => getAction(`/serialPlan/plan/forms/check-record/item/list`, params)
const saveCheckRecord = (params) => postAction('/workGroup/workRecord/check-record/set-check', params)
const saveCheckTime = (params) => postAction('/workGroup/workRecord/check-record/set-time', params)

const delMaterial = (params) => postAction('/workGroup/workRecord/delete/material', qs.stringify(params))
const submitTask = (params) => postAction('/workGroup/workRecord/submit-task', params)
const saveTool = (params) => postAction('/workGroup/workRecord/save/tool', params)
const delTool = (params) => postAction('/workGroup/workRecord/delete/tool', qs.stringify(params))
const saveOutTaskInfo = (params) => postAction('/workGroup/workRecord/save/outsourceOutin', params)
const getOutTaskInfo = (params) => getAction('/workGroup/workRecord/outsourceOutin', params)
const saveOutInInfo = (params) => postAction('/workGroup/workRecord/save/outsourceOutin', params)
const getOutAssetList = (params) => getAction('/workGroup/workRecord/outin/not-returns', params)
const saveSafeAssumeRead = (params) => postAction('/group/safeassume/read/add', qs.stringify(params))

// 查询列计划
const getSerialList = (params) => getAction('/serialPlan/plan/page', params)

export {
  subGroupFault,
  cancelGroupFault,
  delGroupFault,
  editGroupFault,
  getGroupFaultList,
  addSafeassume,
  delSafeassume,
  editSafeassume,
  getSafeassumeList,
  addSafeassumeRead,
  getRotableChangeList,
  delRotableChange,
  editRotableChange,
  getRotableChangeInfo,
  getMeasurealertList,
  closeMeasurealert,
  getRotablesManageList,
  getRotableManageInfo,
  getToolEquipmentList,
  getToolEquipmentInfo,
  setToolEquipment,
  getToolList,
  getToolInfo,
  setTool,
  getGroupBasicInfo,
  addTools,
  deleteTools,
  pageRelatedWorkstation,
  pageUnRelatedWorkstation,
  addWorkstation,
  deleteWorkstation,
  getWorkTaskList,
  getTrainPlanRecordFormById,
  getTrainPlanOnlineFormById,
  getTaskRecordForm,
  getTaskRelevanceInfo,
  saveSafeAssumeRead,
  saveFormValue,
  saveCheck,
  getTaskQRCode,
  saveWorkRecordExcelData,
  getWorkRecordExcelData,
  saveRcordTableAssetNo,
  saveMaterial,
  actMaterial,
  delMaterial,
  submitTask,
  saveTool,
  delTool,
  getSerialList,
  editWorkOrder,
  getWorkOrderList,
  getWorkOrderDetail,
  submitOrderToDispatcher,
  saveOutTaskInfo,
  getOutTaskInfo,
  saveOutInInfo,
  getOutAssetList,
  getGroupFaultInfo,
  getSafeassumeDetail,
  toolUseRecord,
  equipmentUseRecord,
  getRotableManageUseRecord,
  saveWorkTime,
  delWorkArrange,
  getTaskBookSteps,
  getTrainPlanCheckRecordForm,
  getCheckRecordDetail,
  saveCheckRecord,
  saveCheckTime,
  getMaterialList,
  commitTempOrderMaterial,
  getMaterialApplyList,
  getApplyWorkOrderList
}