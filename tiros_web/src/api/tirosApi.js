import { deleteAction, downloadFile, getAction, postAction, putAction } from '@/api/manage'
import qs from 'qs'
// 流水号
export const getSerialNumber = (params) => getAction('/sys/serialNumber/generate', params)

// 基础管理

// 组织人员信息
const getOrgTree = (params) => getAction('/org/tree', params)
const pageUser = (params) => getAction('/org/user/page', params)
const getUser = (params) => getAction('/org/user/get', params)
const saveUser = (params) => postAction('/org/user/save', params)
const deleteUser = (params) => postAction('/org/user/delete', qs.stringify(params))
const pageTraining = (params) => getAction('/org/user/training/page', params)
const skillCompare = (params) => getAction('/org/user/skill-compare', params)
const setWorkMonitor = (params) => getAction('/group/information/workShopGroup/save', params)
const syncDeptAndUser = (params) => getAction('/uuv/sync', params)

// 节假日
const addHoliday = (params) => postAction('/holiday/add', params)
const editHoliday = (params) => putAction('/holiday/edit', params)
const getHolidayList = (params) => getAction('/holiday/list', params)
const addHolidayByNet = (params) => postAction('/holiday/addByNetWork', params)
const delHoliday = (params) => deleteAction('/holiday/delete', params)

// 车间信息管理
const getWorkshop = (params) => getAction(`/workshop/get/${params}`)
const saveWorkshop = (params) => postAction('/workshop/edit', params)

// 股道信息管理
const addTrack = (params) => postAction('/track/add', params)
const editTrack = (params) => putAction('/track/edit', params)
const getTrackList = (params) => getAction('/track/list', params)
const delTrack = (params) => deleteAction('/track/delete', params)

// 工位信息
const addStation = (params) => postAction('/workstation/add', params)
const editStation = (params) => postAction('/workstation/edit', params)
const getStationList = (params) => getAction('/workstation/list', params)
const delStation = (params) => postAction('/workstation/delete', params)
const getStationtree = (params) => getAction('/workstation/workstationTree', params)
const getStationByGroupId = (params) => getAction(`/workstation/fromGroupId/${params}`)

const pageUnRelatedForm = (params) => getAction('/workstation/form/page-unrelated', params)
const listRelatedForm = (params) => getAction('/workstation/form/list-related', params)
const relateForm = (params) => postAction('/workstation/form/relate', params)
const cancelRelateForm = (params) => postAction('/workstation/form/cancel-relate', qs.stringify(params))

// 车型信息
const addTrainType = (params) => postAction('/trainType/add', params)
const editTrainType = (params) => putAction('/trainType/edit', params)
const getTrainTypeList = (params) => getAction('/trainType/list', params)
const delTrainType = (params) => deleteAction('/trainType/delete', params)
const getTrainTypeListAll = () => getAction('/trainType/listAll')
const getLineInfo = (params) => getAction('/line/get', params)
const getLineList = () => getAction('/line/list')


const trainTypeSysList = (params) => getAction('/trainAssetType/listChildren', params)
const addTrainTypeSys = (params) => postAction('/trainAssetType/add', params)
const editTrainTypeSys = (params) => putAction('/trainAssetType/edit', params)
const delTrainTypeSys = (params) => deleteAction('/trainAssetType/delete', params)
const getTrainAssetType = (params) => getAction('/trainAssetType/get', params)
const getLine = (params) => getAction('/line/get', params)

// 车辆结构
const addTrainStruct = (params) => postAction('/trainStructure/add', params)
const editTrainStruct = (params) => putAction('/trainStructure/edit', params)
const getTrainStructList = (params) => getAction('/trainStructure/list', params)
const delTrainStruct = (params) => deleteAction('/trainStructure/delete', params)
const copyTrainStruct = (params) => postAction('/trainStructure/copy', params)

const addTrainStructDetail = (params) => postAction('/trainStructureDetail/add', params)
const editTrainStructDetail = (params) => putAction('/trainStructureDetail/edit', params)
const getTrainStructDetailList = (params) => getAction('/trainStructureDetail/listChildren', params)
const delTrainStructDetail = (params) => deleteAction('/trainStructureDetail/delete', params)
const importTrainStruct = (params) => postAction('/trainStructureDetail/importStructure', params)

// 车辆信息
const addTrainInfo = (params) => postAction('/traininfo/add', params)
const getTrainInfoTree = () => getAction('traininfo/line-train-tree')
const TrainNoExist = (params) => getAction(`/traininfo/isExistTrainCode/${params}`)
const editTrainInfo = (params) => putAction('/traininfo/edit', params)
const getTrainInfoList = (params) => getAction('/traininfo/list', params)
const delTrainInfo = (params) => deleteAction('/traininfo/delete', params)
const getTrainInfoByTrainNo = (trainNo) => getAction('/traininfo/get', qs.stringify({ trainNo: trainNo }))

// 车辆设备
const addTrainAsset = (params) => postAction('/trainAsset/add', params)
const editTrainAsset = (params) => putAction('/trainAsset/edit', params)
const delTrainAsset = (params) => deleteAction('/trainAsset/delete', params)
const produceTrainAsset = (params) => getAction('/trainAsset/produceTrainAsset', params)
const getTrainAssetList = (params) => getAction('/trainAsset/listChildren', params)
const getTrainAssetListNew = (params) => getAction('/maximo-asset/list-child', params)
const getAssetInfoData = (params) => getAction('/maximo-asset/get', params)
const setAssetInfoSave = (params) => postAction('/maximo-asset/ext/save', params)
const getTrainAssetListCopy = (params) => getAction('/maximo-asset/list-child', params)
const importTrainAsset = (params) => postAction('/trainAsset/importStructure', params)
// 车辆具体设备
const getTrainAssetListByStructDetailId = (params) => getAction('/trainAsset/list-child', params)
const listOrTreeTrainAsset = (params) => getAction('/trainAsset/list-tree', params)

// 预警接受
const addAlertAccept = (params) => postAction('/alertAccept/add', params)
const getAlertAcceptList = (params) => getAction('/alertAccept/list', params)
const delAlertAccept = (params) => deleteAction('/alertAccept/delete', params)

// 用户列表
const getUserList = (params) => getAction('/sys/user/list', params)

// 角色列表
const getRoleList = (params) => getAction('/sys/role/list', params)

// 机构树
const getDepartTree = (params) => getAction('/sys/sysDepart/queryTreeList', params)

// 计划模板
const getPlanList = (params) => getAction('/tp-plan/page', params)
const addPlanTemplate = (params) => postAction('/tp-plan/save', params)
const copyPlanTemplate = (params) => postAction('/tp-plan/copy', params)
const delPlanTemplate = (params) => deleteAction('/tp-plan/delete', params)
const validPlanTemplate = (params) => postAction('/tp-plan/valid', params)
const invalidPlanTemplate = (params) => postAction('/tp-plan/invalid', params)
const getPlanDetail = (params) => getAction(`/tp-plan/detail/${params}`)
const getPlanRelation = (params) => getAction(`/tp-plan/relation/${params}`)
const addPlanTask = (params) => postAction('/tp-plan/task/save', params)
const delPlanTask = (params) => deleteAction('/tp-plan/task/delete', params)
const getTaskDetail = (params) => getAction(`/tp-plan/task/detail/${params}`)
const updateTaskNoAndWbs = (params) => postAction(`/tp-plan/task/update-no-wbs`, params)
const getPlanNoRelevanceCount = (params) => getAction('/tp-plan/not-related-regu/count', params)
const getPlanNoRelevanceDetail = (params) => getAction(`/tp-plan/not-related-regu/detail`, params)
const importTpPlan = (params) => postAction('/import/tp-plan', params)
const addTpPlanForm = (params) => postAction('/tp-plan/forms/add', params)
const editTpPlanForm = (params) => postAction('/tp-plan/forms/edit', params)
const delTpPlanForm = (params) => postAction('/tp-plan/forms/delete', params)
const pageTpPlanForms = (params) => getAction('/tp-plan/forms/page', params)
const listTpPlanForms = (params) => getAction('/tp-plan/forms/list', params)
const exportTpPlanInfo = (fileName, params) => downloadFile('/tp-plan/planInfo/export', fileName, params)

//架修规程
const getReguList = (params) => getAction('/regu/reguInfo/list/reguInfo', params)
const saveRegu = (params) => postAction('/regu/reguInfo/saveOrUpdate/reguInfo', params)
const copyRegu = (params) => postAction(`/regu/reguInfo/copy/reguInfo/${params}`)
const delRegu = (params) => deleteAction(`/regu/reguInfo/delete/${params}`)
const getRegu = (params) => getAction(`/regu/reguInfo/get/${params}`)
const saveReguDetail = (params) => postAction('/regu/reguInfo/saveOrUpdate/reguDetail', params)
const getReguDeteil = (params) => getAction('/regu/reguInfo/list/reguDetail', params)
const importTechBook = (params) => postAction('/regu/reguInfo/importTechBook', params)
const getReguDetailOtherInfo = (params) => getAction('/regu/reguInfo/detail/get', params)
const delReguInfo = (params) => deleteAction('/regu/reguInfo/deleteBatch/reguDetail', params)
const importRegu = (params) => postAction('/import/regu', params)

// 自定义表单
const pageCustomForm = (params) => getAction('/formTemplate/page', params)
const getWorkcheckList = (params) => getAction('/workcheck/list', params)

const addCustomForm = (params) => postAction('/formTemplate/add', params)
const updateCustomForm = (params) => putAction('/formTemplate/edit', params)
const deleteCustomForm = (params) => deleteAction('/formTemplate/delete', params)
const validCustomForm = (params) => postAction('/formTemplate/valid', params)
const invalidCustomForm = (params) => postAction('/formTemplate/invalid', params)
const getFormContent = (params) => getAction(`/formTemplate/getContent/${params}`)
const getDataJson = (templateId) => getAction(`/formTemplate/getDataJson/${templateId}`)

// 作业表单
const pageOldWorkRecord = (params) => getAction('/workRecord/page', params)
const getOldWorkRecord = (params) => getAction('/workRecord/get', params)
const saveOldWorkRecord = (params) => postAction('/workRecord/saveOrUpdate', params)
const deleteOldWorkRecord = (params) => deleteAction('/workRecord/delete', params)
const listOldWorkRecordCategory = (params) => getAction(`/workRecord/category/list/${params}`)
const saveOrUpdateOldWorkRecordCategory = (params) => postAction('/workRecord/category/saveOrUpdate', params)
const deleteOldWorkRecordCategory = (params) => deleteAction('/workRecord/category/delete', params)
const listOldWorkRecordDetail = (params) => getAction('/workRecord/detail/list', params)
const saveOldWorkRecordDetail = (params) => postAction('/workRecord/detail/saveOrUpdate', params)
const deleteOldWorkRecordDetail = (params) => deleteAction(`/workRecord/detail/delete/${params}`)
const importWorkRecord = (params) => postAction('/import/workRecord', params)

// 作业表单实例
const getFormEntityList = (params) => getAction('/serialPlan/plan/forms/instance/list', params)
const deleteFormEntity = (params) => postAction('/serialPlan/plan/forms/instance/delete', params)
const addFormEntityList = (params) => postAction('/serialPlan/plan/forms/instance/add', params)

//工艺文件管理
const addDirectory = (params) => postAction('/doc/file/add', params)
const delDocument = (params) => postAction('/doc/file/delete', params)
const editDirectoryOrFile = (params) => postAction('/doc/file/edit', params)
const getDocumentTree = (params) => getAction('/doc/file/directoryTree', params)
const getDocumentTechFileTree = () => getAction('/doc/file/directoryTree/techFile')
const getDocumentPage = (params) => getAction('/doc/file/page', params)
const delPrivilege = (params) => postAction(`/doc/file/privilege/delete/${params}`, params)
const getPrivilege = (params) => getAction(`/doc/file/privilege/${params}`)
const settingPrivilege = (params) => postAction('/doc/file/privilege/setting', params)
//const uploadFile = (params)=>postAction("/fdfs/file/upload",params)
//const uploadFile = (params) => postAction('/minio/file/upload', params)
//const downloadFile = (params)=>downFile("/fdfs/file/downloadFile",params)
// const downloadFile = (params) => downFile('/minio/file/downloadFile', params)
// const delFile = (params)=>postAction("/fdfs/file/delete",params)
// const delFile = (params) => postAction('/minio/file/delete', params)
const addFile = (params) => postAction('/doc/file/addFile', params)
const getFileType = () => getAction('/doc/file/fileType')
const isPrivilege = (params) => getAction('/doc/file/isPrivilege', params)
const getDirParentId = (params) => getAction(`/doc/file/dirParentId/${params}`)
const getSharedDir = (params) => getAction(`/doc/file/shared/${params}`)

// 作业指导书
const getSopPage = (params) => getAction('/base/tech-book/page', params)
const saveSopRecord = (params) => postAction('/base/tech-book/save', params)
const delSopRecord = (params) => postAction('/base/tech-book/delete', params)
const saveSopContent = (id, contentHtml) => postAction(`/base/tech-book/content/save?id=${id}`, { contentHtml })
const updateSopStatus = (params) => postAction(`/base/tech-book/status?${qs.stringify(params)}`)
const saveSopAsTemplate = (params) => postAction(`/base/tech-book/saveTemplate?${qs.stringify(params)}`)
const submitSopReview = (params) => postAction(`/base/tech-book/review/submit?${qs.stringify(params)}`)
const decisionSopReview = (params) => postAction(`/base/tech-book/review/decision?${qs.stringify(params)}`)

const getSopDetailPage = (params) => getAction('/base/tech-book/detail/page', params)
const getSopDetailRecord = (params) => getAction('/base/tech-book/detail/get', params)
const saveSopDetailRecord = (params) => postAction('/base/tech-book/detail/save', params)
const delSopDetailRecord = (params) => postAction('/base/tech-book/detail/delete', params)

// 作业检查表管理
const getWorkCheckList = (params) => getAction('/workcheck/list', params)
const getWorkcheck = (params) => getAction('/workcheck/get', params)
const saveOrUpdateWorkCheckItem = (params) => postAction('/workcheck/saveOrUpdate', params)
const delWorkCheckItem = (params) => postAction('/workcheck/deleteBatch', params)
const importWorkCheck = (params) => postAction('/import/workCheck', params)

// 检修属性
const pageRepairAttribute = (params) => getAction('/base/repair/attr/page', params)
const saveRepairAttribute = (params) => postAction('/base/repair/attr/save', params)
const deleteRepairAttribute = (params) => postAction('/base/repair/attr/delete', params)
const listRepairAttrProgRelByAttributeId = (attributeId) => getAction('/base/repair/attr-rel/list', qs.stringify({ attributeId: attributeId }))
const saveRepairAttrProgRel = (params) => postAction('/base/repair/attr-rel/save', params)
const deleteRepairAttrProgRel = (params) => postAction('/base/repair/attr-rel/delete', params)

// 生产通知单管理
const pageProductionNotice = (params) => getAction('/base/production-notice/page', params)
const getProductionNotice = (params) => getAction('/base/production-notice/get', params)
const saveProductionNotice = (params) => postAction('/base/production-notice/save', params)
const updateProductionNotice = (params) => putAction('/base/production-notice/update', params)
const deleteProductionNotice = (params) => deleteAction('/base/production-notice/delete', params)
const submitProductionNotice = (params) => postAction('/base/production-notice/submit?' + qs.stringify(params))
const publishProductionNotice = (params) => postAction('/base/production-notice/publish?' + qs.stringify(params))
const closeProductionNotice = (params) => postAction('/base/production-notice/close?' + qs.stringify(params))
const listProductionNoticeProgressDetail = (params) => getAction('/base/production-notice/progress-detail', params)

// 标准工序管理
const pageStandardProcess = (params) => getAction('/base/standard-process/page', params)
const getStandardProcess = (params) => getAction('/base/standard-process/get', params)
const saveStandardProcess = (params) => postAction('/base/standard-process/save', params)
const updateStandardProcess = (params) => putAction('/base/standard-process/update', params)
const deleteStandardProcess = (params) => deleteAction('/base/standard-process/delete', params)

// 定额BOM管理
const pageQuotaBom = (params) => getAction('/base/quota-bom/page', params)
const getQuotaBom = (params) => getAction('/base/quota-bom/get', params)
const saveQuotaBom = (params) => postAction('/base/quota-bom/save', params)
const updateQuotaBom = (params) => putAction('/base/quota-bom/update', params)
const deleteQuotaBom = (params) => deleteAction('/base/quota-bom/delete', params)

// 质量可视化管理
const pageQualityVisual = (params) => getAction('/base/quality-visual/page', params)
const getQualityVisual = (params) => getAction('/base/quality-visual/get', params)
const saveQualityVisual = (params) => postAction('/base/quality-visual/save', params)
const updateQualityVisual = (params) => putAction('/base/quality-visual/update', params)
const deleteQualityVisual = (params) => deleteAction('/base/quality-visual/delete', params)

export {
  getOrgTree,
  pageUser,
  getUser,
  saveUser,
  deleteUser,
  setWorkMonitor,
  pageTraining,
  addHoliday,
  editHoliday,
  getHolidayList,
  addHolidayByNet,
  delHoliday,
  addTrack,
  editTrack,
  getTrackList,
  delTrack,
  getWorkshop,
  saveWorkshop,
  addStation,
  editStation,
  getStationList,
  delStation,
  getStationtree,
  pageUnRelatedForm,
  listRelatedForm,
  relateForm,
  cancelRelateForm,
  addTrainType,
  editTrainType,
  getTrainTypeList,
  delTrainType,
  trainTypeSysList,
  addTrainTypeSys,
  editTrainTypeSys,
  delTrainTypeSys,
  getTrainAssetType,
  addTrainStruct,
  editTrainStruct,
  getTrainStructList,
  delTrainStruct,
  copyTrainStruct,
  addTrainStructDetail,
  editTrainStructDetail,
  getTrainStructDetailList,
  delTrainStructDetail,
  importTrainStruct,
  addTrainInfo,
  TrainNoExist,
  editTrainInfo,
  getTrainInfoList,
  delTrainInfo,
  addTrainAsset,
  editTrainAsset,
  delTrainAsset,
  produceTrainAsset,
  importTrainAsset,
  getTrainAssetList,
  getTrainAssetListNew,
  getAssetInfoData,
  setAssetInfoSave,
  getTrainAssetListCopy,
  listOrTreeTrainAsset,
  getTrainAssetListByStructDetailId,
  delAlertAccept,
  addAlertAccept,
  getAlertAcceptList,
  getUserList,
  getRoleList,
  getDepartTree,
  getPlanList,
  delPlanTemplate,
  validPlanTemplate,
  invalidPlanTemplate,
  addPlanTemplate,
  copyPlanTemplate,
  getPlanDetail,
  getPlanRelation,
  addPlanTask,
  delPlanTask,
  getTaskDetail,
  updateTaskNoAndWbs,
  getPlanNoRelevanceCount,
  getPlanNoRelevanceDetail,
  importTpPlan,
  addTpPlanForm,
  editTpPlanForm,
  delTpPlanForm,
  pageTpPlanForms,
  listTpPlanForms,
  exportTpPlanInfo,
  getReguList,
  saveRegu,
  copyRegu,
  delRegu,
  getRegu,
  delReguInfo,
  saveReguDetail,
  getReguDeteil,
  importTechBook,
  pageCustomForm,
  getWorkcheckList,
  addCustomForm,
  updateCustomForm,
  deleteCustomForm,
  validCustomForm,
  invalidCustomForm,
  getFormContent,
  getDataJson,
  pageOldWorkRecord,
  getStationByGroupId,
  saveOldWorkRecord,
  deleteOldWorkRecord,
  listOldWorkRecordCategory,
  saveOrUpdateOldWorkRecordCategory,
  deleteOldWorkRecordCategory,
  listOldWorkRecordDetail,
  saveOldWorkRecordDetail,
  deleteOldWorkRecordDetail,
  getOldWorkRecord,
  addDirectory,
  delDocument,
  getDocumentPage,
  getPrivilege,
  getDocumentTree,
  settingPrivilege,
  getFileType,
  addFile,
  editDirectoryOrFile,
  isPrivilege,
  delPrivilege,
  getDocumentTechFileTree,
  getReguDetailOtherInfo,
  getLineInfo,
  getTrainTypeListAll,
  getTrainInfoTree,
  getLine,
  getLineList,
  getTrainInfoByTrainNo,
  skillCompare,
  importRegu,
  getDirParentId,
  importWorkRecord,
  getSopPage,
  saveSopRecord,
  delSopRecord,
  saveSopContent,
  updateSopStatus,
  saveSopAsTemplate,
  submitSopReview,
  decisionSopReview,
  getSopDetailPage,
  saveSopDetailRecord,
  delSopDetailRecord,
  getSopDetailRecord,
  importWorkCheck,
  getWorkCheckList,
  delWorkCheckItem,
  getWorkcheck,
  saveOrUpdateWorkCheckItem,
  syncDeptAndUser,
  getSharedDir,
  getFormEntityList,
  deleteFormEntity,
  addFormEntityList,
  pageRepairAttribute,
  saveRepairAttribute,
  deleteRepairAttribute,
  listRepairAttrProgRelByAttributeId,
  saveRepairAttrProgRel,
  deleteRepairAttrProgRel,
  pageProductionNotice,
  getProductionNotice,
  saveProductionNotice,
  updateProductionNotice,
  deleteProductionNotice,
  submitProductionNotice,
  publishProductionNotice,
  closeProductionNotice,
  listProductionNoticeProgressDetail,
  pageStandardProcess,
  getStandardProcess,
  saveStandardProcess,
  updateStandardProcess,
  deleteStandardProcess,
  pageQuotaBom,
  getQuotaBom,
  saveQuotaBom,
  updateQuotaBom,
  deleteQuotaBom,
  pageQualityVisual,
  getQualityVisual,
  saveQualityVisual,
  updateQualityVisual,
  deleteQualityVisual
}
