import axios from 'axios'
import qs from 'qs'
import { ACCESS_TOKEN } from '@/store/mutation-types'
/*import Vue from 'vue'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import { Modal, notification } from 'ant-design-vue'*/
import store from '@/store'
import { notification } from 'ant-design-vue'
import Vue from 'vue'

const fileAxios = axios.create({
  baseURL: window._CONFIG['fileServiceUrl'], // 后端文件服务地址
  timeout: 90000 // 请求超时时间
})
const err = (error) => {
  if (error.response) {
    let data = error.response.data
    const token = Vue.ls.get(ACCESS_TOKEN)
    console.log('------异常响应------', token)
    console.log('------异常响应------', error.response.status)
    switch (error.response.status) {
      case 403:
        notification.error({ message: '系统提示', description: '拒绝访问', duration: 4 })
        break
      case 500:
        //notification.error({ message: '系统提示', description:'Token失效，请重新登录!',duration: 4})
        /*if(token && data.message=="Token失效，请重新登录"){
          // update-begin- --- author:scott ------ date:20190225 ---- for:Token失效采用弹框模式，不直接跳转----
          // store.dispatch('Logout').then(() => {
          //     window.location.reload()
          // })
          Modal.error({
            title: '登录已过期',
            content: '很抱歉，登录已过期，请重新登录',
            okText: '重新登录',
            mask: false,
            onOk: () => {
              store.dispatch('Logout').then(() => {
                Vue.ls.remove(ACCESS_TOKEN)
                window.location.reload()
              })
            }
          })
          // update-end- --- author:scott ------ date:20190225 ---- for:Token失效采用弹框模式，不直接跳转----
        }*/
        notification.error({
          message: '服务器出错了',
          description: data.message,
          duration: 4
        })
        break
      case 404:
        notification.error({ message: '系统提示', description: '很抱歉，资源未找到!', duration: 4 })
        break
      case 504:
        notification.error({ message: '系统提示', description: '网络超时' })
        break
      case 401:
        if (token && data.code === 3166) {
          // update-begin- --- author:scott ------ date:20190225 ---- for:Token失效采用弹框模式，不直接跳转----
          // store.dispatch('Logout').then(() => {
          //     window.location.reload()
          // })
          // 弹出重新登录提示
          console.log('文件服务 openLogoutMessage run')
          store.dispatch('openLogoutMessage')
          // Modal.error({
          //   title: '登录已过期',
          //   content: '很抱歉，登录已过期，请重新登录',
          //   okText: '重新登录',
          //   mask: false,
          //   onOk: () => {
          //     store.dispatch('Logout').then(() => {
          //       Vue.ls.remove(ACCESS_TOKEN)
          //       window.location.reload()
          //     })
          //   }
          // })
          // update-end- --- author:scott ------ date:20190225 ---- for:Token失效采用弹框模式，不直接跳转----
        } else {
          notification.error({ message: '系统提示', description: '未授权，请重新登录', duration: 4 })
          console.log('文件服务 store.dispatch(\'Logout\') run已被注释')
          if (token) {
            store.dispatch('Logout').then(() => {
              setTimeout(() => {
                window.location.reload()
              }, 1500)
            })
          }
        }
        break
      default:
        notification.error({
          message: '系统提示',
          description: data.message,
          duration: 4
        })
        break
    }
  }
  return Promise.reject(error)
}
// request interceptor
fileAxios.interceptors.request.use(config => {
  const token = Vue.ls.get(ACCESS_TOKEN)
  if (token) {
    config.headers['X-Access-Token'] = token // 让每个请求携带自定义 token 请根据实际情况自行修改
  }
  return config
}, (error) => {
  return Promise.reject(error)
})

// response interceptor
fileAxios.interceptors.response.use((response) => {
  return response.data
}, err)

export function download(filePath) {
  getAccessPath(filePath).then(res => {
    if (res.success) {
      let fileReal = res.result
      try {
        let downer = document.createElement('a')
        downer.setAttribute('download', '')// download属性
        downer.setAttribute('href', fileReal)// href链接
        // downer.setAttribute('target', '_blank')// 下载打开新窗口（解决点开下载后退出登录的问题）
        downer.click()
      } catch (e) {
        Vue.prototype['$message'].error('文件下载异常')
      }
    }
  }).catch(err => {
    Vue.prototype['$message'].warning('文件下载异常')
  })
}

/**
 * 下载文件(建议不要使用，该下载方式为 先将文件下载到文件服务，然后再从文件服务输出到客户端，中间多了一次下载）
 * @param filePath  要下载的文件路径，是指在文件存储服务上的保存的相对路径（包括文件名称，也就是文件上传返回的路径）
 * @returns {Promise<AxiosResponse<any>>}
 */
export function downloadFile(filePath) {

  return fileAxios({
    url: '/minio/file/download',
    params: { fileName: filePath },
    method: 'get',
    responseType: 'blob'
  }).then((data) => {
    if (!data || data.size === 0) {
      Vue.prototype['$message'].warning('文件下载失败')
      return
    }
    if (typeof window.navigator.msSaveBlob !== 'undefined') {
      window.navigator.msSaveBlob(new Blob([data]), fileName)
    } else {
      let url = window.URL.createObjectURL(new Blob([data]))
      let link = document.createElement('a')
      link.style.display = 'none'
      link.href = url
      link.setAttribute('download', fileName)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link) //下载完成移除元素
      window.URL.revokeObjectURL(url) //释放掉blob对象
    }
  })
}

export function exportExcelFile(excelJson, fileName) {
  let myaxios = axios.create({
    baseURL: window._CONFIG['domianURL'], // 后端文件服务地址
    timeout: 90000 // 请求超时时间
  })

  myaxios.interceptors.request.use(config => {
    const token = Vue.ls.get(ACCESS_TOKEN)
    if (token) {
      config.headers['X-Access-Token'] = token // 让每个请求携带自定义 token 请根据实际情况自行修改
    }
    return config
  }, (error) => {
    return Promise.reject(error)
  })

  return myaxios({
    url: '/report/excel/downfile',
    data: qs.stringify({ exceldata: excelJson }),
    method: 'post',
    responseType: 'blob'
  }).then((data) => {
    if (!data || data.size === 0) {
      Vue.prototype['$message'].warning('文件下载失败')
      return
    }
    if (typeof window.navigator.msSaveBlob !== 'undefined') {
      window.navigator.msSaveBlob(new Blob([data]), fileName)
    } else {
      let url = window.URL.createObjectURL(new Blob([data]))
      let link = document.createElement('a')
      link.style.display = 'none'
      link.href = url
      link.setAttribute('download', fileName)
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link) //下载完成移除元素
      window.URL.revokeObjectURL(url) //释放掉blob对象
    }
  })
}

/**
 * 文件上传
 * @param formData FormData 对象，文件标识为'file',  一次只能上传一个文件
 * @param savePath 指定文件在文件存储服务的存放路径，不包括文件名称  (默认存放在默认桶下面的根目录)
 * @returns 返回文件存放相对路径(带文件名称)
 */
export function uploadFile(formData, savePath) {
  if (savePath) {
    formData.append('fileName', savePath)
  } else {
    formData.append('fileName', '/')
  }
  return fileAxios({
    url: '/minio/file/upload',
    data: formData,
    method: 'post',
    headers: {
      'Content-Type': 'multipart/form-data'  // 文件上传
    }
  })
}

/**
 * 删除文件
 * @param filePath 要删除的文件路径，是指在文件存储服务上的保存的相对路径（包括文件名称，也就是文件上传返回的路径）
 * @returns {AxiosPromise}
 */
export function deleteFile(filePath) {
  return fileAxios({
    url: '/minio/file/delete',
    data: qs.stringify({ fileName: filePath }),
    method: 'post'
  })
}

/**
 * 获取指定文件的外部访问地址
 * @param filePath
 */
export function getAccessPath(filePath) {
  return fileAxios({
    url: '/minio/file/path',
    params: { fileName: filePath },
    method: 'get'
  })
}
