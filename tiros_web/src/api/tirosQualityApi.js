import { getAction, postAction, downloadFile } from '@/api/manage'

//整改管理
const addRectify = (params) => postAction("/quality/rectify/add", params);
const delRectify = (params) => postAction("/quality/rectify/delete", params);
const editRectify = (params) => postAction("/quality/rectify/edit", params);
const getRectifyList = (params) => getAction("/quality/rectify/page", params);
const closeRectify = (params) => postAction("/quality/rectify/close", params);
const getRectify = (params) => getAction("/quality/rectify/get", params);
const getRectifyCode = (params) => getAction("/quality/rectify/getCode", params);


//测量预警
const getEarlyWarningList = (params) => getAction("/quality/measurealert/page", params);
const getEarlyWarningInfoList = (params) => getAction("/quality/measurealert/formresult/listByFormObjId", params);


//测量阈值
const pageThresholdForm = (params) => getAction("/quality/measurethreshold/form/page-data-form", params);
const saveThreshold = (params) => postAction('/quality/measurethreshold/threshold/save', params);
const getFormThresholds = (formId) => getAction(`/quality/measurethreshold/threshold/list/${formId}`)
const getFormAlertRecords = (formId) => getAction(`/serialPlan/plan/forms/online-form/alert-records`, { dataRecordId: formId })

const editThreshold = (params) => postAction('/quality/measurethreshold/threshold/edit', params)
const deleteThreshold = (params) => postAction('/quality/measurethreshold/threshold/delete', params)
const getRowColThreshold = (params) => getAction('/quality/measurethreshold/threshold/get', params)
//开口项管理
const addOpenItem = (params) => postAction("/quality/leaverecord/add", params);
const delOpenItem = (params) => postAction("/quality/leaverecord/delete", params);
const closedOpenItem = (params) => postAction("/quality/leaverecord/close", params);
const editOpenItem = (params) => postAction("/quality/leaverecord/edit", params);
const getOpenItemList = (params) => getAction("/quality/leaverecord/page", params);
const leaveRecordExport = (fileName, params) => downloadFile(`/quality/leaverecord/workLeaveRecordExport`, fileName, params);


//故障管理
const getBreakDownList = (params) => getAction("/quality/fault/info/page", params);

// 作业记录表确认
const getRecordTable = (params) => getAction('/quality/record-confirm/list', params)
const confirmCheck = (params) => postAction('/quality/record-confirm/confirm', params)
const getRecordTableDetail = (params) => getAction('/quality/record-confirm/work-record/get', params)
const getOnlineFormItem = (params) => getAction('/quality/record-confirm/online-form/get', params)
const getWorkCheckForm = (params) => getAction('/serialPlan/plan/forms/check-record/get', params)
const getWorkcheck = (params) => getAction('/workcheck/get', params)
const confirmForm = (params) => postAction('/quality/record-confirm/confirm', params)

// 作业检查
const pageFormInstanceRecord = (params) => getAction('/quality/record-confirm/page', params)
const pageFormCheckRecord = (params) => getAction('/quality/record-confirm/page/check-record', params)
const setJudgeRecord = (params) => postAction('/serialPlan/plan/forms/judge/save', params)
const delJudgeRecord = (params) => postAction('/serialPlan/plan/forms/judge/delete', params)



export {
  addRectify,
  delRectify,
  editRectify,
  closeRectify,
  getRectify,
  getRectifyList,
  getEarlyWarningList,
  getEarlyWarningInfoList,
  pageThresholdForm,
  saveThreshold,
  getFormThresholds,
  editThreshold,
  deleteThreshold,
  getRowColThreshold,
  addOpenItem,
  delOpenItem,
  editOpenItem,
  getOpenItemList,
  leaveRecordExport,
  getBreakDownList,
  getRecordTable,
  confirmCheck,
  getRecordTableDetail,
  pageFormInstanceRecord,
  getWorkCheckForm,
  getWorkcheck,
  closedOpenItem,
  getOnlineFormItem,
  confirmForm,
  getFormAlertRecords,
  pageFormCheckRecord,
  setJudgeRecord,
  delJudgeRecord,
  getRectifyCode
}