import { deleteAction, getAction, postAction, putAction } from '@/api/manage'

const loadSysCategory = (params) => getAction('/sys/category/loadTreeRoot', params)

export {
  loadSysCategory
}