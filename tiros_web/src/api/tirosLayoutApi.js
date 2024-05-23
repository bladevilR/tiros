import { getAction, postAction } from '@/api/manage'
import qs from 'qs'

const getWidgets = (params) => getAction('/layout/widgets/page', params)
const saveLayout = (params) => postAction('/layout/layouts/save', params)
const getLayouts = (params) => getAction('/layout/layouts/page', params)
const deleteLayout = (params) => postAction('/layout/layouts/delete', qs.stringify(params))
const getLayoutInfo = (params) => getAction('/layout/layouts/get', params)
const getMainLayoutInfo=() => getAction('/layout/layouts/get/current-user-main')
export {
  getWidgets,
  saveLayout,
  getLayouts,
  deleteLayout,
  getLayoutInfo,
  getMainLayoutInfo
}