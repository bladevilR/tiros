import Vue from 'vue'
import router from './router'
import store from './store'
import NProgress from 'nprogress' // progress bar
import 'nprogress/nprogress.css' // progress bar style
import { ACCESS_TOKEN, INDEX_MAIN_PAGE_PATH, LAST_REFRESH_TIME, USER_NAME, USER_INFO, USER_LINES } from '@/store/mutation-types'
import { generateIndexRouter } from '@/utils/util'
import { checkRefreshToken } from '@views/tiros/util/TokenUtil'


NProgress.configure({ showSpinner: false }) // NProgress Configuration

const whiteList = ['/sso/login','/sso/check','/user/login', '/user/register', '/user/register-result', '/user/alteration', '/403', '/404', '/500'] // no redirect whitelist

router.beforeEach((to, from, next) => {
  NProgress.start() // start progress bar

  if (Vue.ls.get(ACCESS_TOKEN)) {

    checkRefreshToken(to.path)

    /* has token */
    // 设置工作流接口组件的当前用户和Token（用于用户验证)
    if (Vue.prototype.$workflowApi) {
      Vue.prototype.$workflowApi.addHeader('X-Access-Token', Vue.ls.get(ACCESS_TOKEN))
    } else {
      console.error('没有找到流程组件对象,无法设置流程组件的当前用户的Token')
    }
    if (to.path === '/user/login' || to.path === '/sso/login') {
      next({ path: INDEX_MAIN_PAGE_PATH })
      NProgress.done()
    } else {
      if (store.getters.permissionList.length === 0) {
        store.dispatch('GetPermissionList').then(res => {
          const menuData = res.result.menu
          if (menuData === null || menuData === '' || menuData === undefined) {
            return
          }
          let constRoutes = generateIndexRouter(menuData)
          // 添加主界面路由
          store.dispatch('UpdateAppRouter', { constRoutes }).then(() => {
            // 根据roles权限生成可访问的路由表
            // 动态添加可访问路由表
            router.addRoutes(store.getters.addRouters)
            const redirect = decodeURIComponent(from.query.redirect || to.path)
            if (to.path === redirect) {
              // hack方法 确保addRoutes已完成 ,set the replace: true so the navigation will not leave a history record
              if (window.ledClientUrl) {
                //增加判断是大屏客户端显示
                next({ path: window.ledClientUrl })
              } else {
                next({ ...to, replace: true })
              }
            } else {
              // 增加判断是大屏客户端显示
              let toPath = window.ledClientUrl ? window.ledClientUrl : redirect;
              // 跳转到目的路由
              next({ path: toPath })
            }
          })
        }).catch((e) => {
          /* notification.error({
             message: '系统提示',
             description: '请求用户信息失败，请重试！'
           })*/

          console.error('获取用户权限信息异常：', e)
          store.dispatch('Logout').then(() => {
            if (e.code && e.code === '100010') {
              // 没有菜单权限嘛
              next({ path: '/403', query: { msg: '你的管理员可能忘记给你授权了，请立即找他' } })
            } else {
              if (to.fullPath && to.fullPath !== '') {
                next({ path: '/sso/check', query: { redirect: to.fullPath } })
              } else {
                next({ path: '/sso/check', query: {} })
              }
              store.commit('SET_ACTIVELOGOUT', true)
            }
          })
        })
      } else {
        next()
      }
    }
  } else {
    if (whiteList.indexOf(to.path) !== -1) {
      // 在免登录白名单，直接进入
      next()
    } else {
      // 增加判断要去网的地址是否为空，为空则不用redirect了
      if (to.fullPath && to.fullPath !== '') {
        next({ path: '/sso/check', query: { redirect: to.fullPath } })
      } else {
        next({ path: '/sso/check', query: {} })
      }
      NProgress.done() // if current page is login will not trigger afterEach hook, so manually handle it
    }
  }
})

router.afterEach(() => {
  NProgress.done() // finish progress bar
})

