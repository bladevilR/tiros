import Vue from 'vue'
import { login, logout, phoneLogin, thirdLogin, refresh } from "@/api/login"
import { ACCESS_TOKEN, USER_NAME,USER_INFO,USER_AUTH,SYS_BUTTON_AUTH,UI_CACHE_DB_DICT_DATA ,USER_LINES, LAST_REFRESH_TIME} from "@/store/mutation-types"
import { welcome } from "@/utils/util"
import { queryPermissionsByUser } from '@/api/api'
import { getAction } from '@/api/manage'
import { Modal } from 'ant-design-vue'
import store from '@/store'
import router from '../../router'
import { isTokenExpired } from '@views/tiros/util/TokenUtil'

const user = {
  state: {
    token: '',
    username: '',
    realname: '',
    welcome: '',
    avatar: '',
    activeLogout: false,
    permissionList: [],
    info: {},
    lines:[]
  },

  mutations: {
    SET_TOKEN: (state, token) => {
      state.token = token
    },
    SET_NAME: (state, { username, realname, welcome }) => {
      state.username = username
      state.realname = realname
      state.welcome = welcome
    },
    SET_AVATAR: (state, avatar) => {
      state.avatar = avatar
    },
    SET_PERMISSIONLIST: (state, permissionList) => {
      state.permissionList = permissionList
    },
    SET_INFO: (state, info) => {
      state.info = info
    },
    SET_LINES: (state, lines)=>{
      state.info=lines
    },
    SET_ACTIVELOGOUT: (state, val)=>{
      state.activeLogout = val
    },
    SET_DEPARTNAME: (state, val) => {
      console.log(state,232323232323232)
      state.info.departName = val
      Vue.ls.set(USER_INFO, state.info, 7 * 24 * 60 * 60 * 1000)
    }
  },

  actions: {
    // CAS验证登录
    ValidateLogin({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
        getAction("/cas/client/validateLogin",userInfo).then(response => {
          console.log("----cas 登录--------",response);
          if(response.success){
            const result = response.result
            const userInfo = result.userInfo
            const lines=result.lines
            Vue.ls.set(ACCESS_TOKEN, result.token, 7 * 24 * 60 * 60 * 1000)
            Vue.ls.set(USER_NAME, userInfo.username, 7 * 24 * 60 * 60 * 1000)
            Vue.ls.set(USER_INFO, userInfo, 7 * 24 * 60 * 60 * 1000)
            Vue.ls.set(USER_LINES, lines, 7 * 24 * 60 * 60 * 1000)
            commit('SET_TOKEN', result.token)
            commit('SET_INFO', userInfo)
            commit('SET_NAME', { username: userInfo.username,realname: userInfo.realname, welcome: welcome() })
            commit('SET_AVATAR', userInfo.avatar)
            commit('SET_LINES',lines)
            resolve(response)
          }else{
            resolve(response)
          }
        }).catch(error => {
          reject(error)
        })
      })
    },
    // 登录
    Login({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
        login(userInfo).then(response => {
          if(response.code =='200'){
            const result = response.result
            console.log('login result:', result)
            const userInfo = result.userInfo
            const lines=result.lines
            Vue.ls.set(LAST_REFRESH_TIME, new Date())
            Vue.ls.set(ACCESS_TOKEN, result.token, 7 * 24 * 60 * 60 * 1000)
            Vue.ls.set(USER_NAME, userInfo.username, 7 * 24 * 60 * 60 * 1000)
            Vue.ls.set(USER_INFO, userInfo, 7 * 24 * 60 * 60 * 1000)
            Vue.ls.set(UI_CACHE_DB_DICT_DATA, result.sysAllDictItems, 7 * 24 * 60 * 60 * 1000)
            Vue.ls.set(USER_LINES, lines, 7 * 24 * 60 * 60 * 1000)
            commit('SET_TOKEN', result.token)
            commit('SET_INFO', userInfo)
            commit('SET_NAME', { username: userInfo.username,realname: userInfo.realname, welcome: welcome() })
            commit('SET_AVATAR', userInfo.avatar)
            commit('SET_LINES',lines)
            resolve(response)
          }else{
            reject(response)
          }
        }).catch(error => {
          reject(error)
        })
      })
    },
    //手机号登录
    PhoneLogin({ commit }, userInfo) {
      return new Promise((resolve, reject) => {
          phoneLogin(userInfo).then(response => {
          if(response.code =='200'){
        const result = response.result
        const userInfo = result.userInfo
        const lines=result.lines
        Vue.ls.set(ACCESS_TOKEN, result.token, 7 * 24 * 60 * 60 * 1000)
        Vue.ls.set(USER_NAME, userInfo.username, 7 * 24 * 60 * 60 * 1000)
        Vue.ls.set(USER_INFO, userInfo, 7 * 24 * 60 * 60 * 1000)
        Vue.ls.set(UI_CACHE_DB_DICT_DATA, result.sysAllDictItems, 7 * 24 * 60 * 60 * 1000)
        Vue.ls.set(USER_LINES, lines, 7 * 24 * 60 * 60 * 1000)
         commit('SET_TOKEN', result.token)
        commit('SET_INFO', userInfo)
        commit('SET_NAME', { username: userInfo.username,realname: userInfo.realname, welcome: welcome() })
        commit('SET_AVATAR', userInfo.avatar)
        commit('SET_LINES',lines)
        resolve(response)
      }else{
        reject(response)
      }
    }).catch(error => {
        reject(error)
      })
    })
    },
    // 获取用户信息
    GetPermissionList({ commit }) {
      return new Promise((resolve, reject) => {
        let v_token = Vue.ls.get(ACCESS_TOKEN);
        if (!v_token || isTokenExpired(v_token)) {
          reject(new Error('token expired'))
          return
        }
        let params = {token:v_token};
        queryPermissionsByUser(params).then(response => {
          console.log('user Permissions: ', response.result)

          const menuData = response.result.menu;
          const authData = response.result.auth;
          const allAuthData = response.result.allAuth;
          //Vue.ls.set(USER_AUTH,authData);
          sessionStorage.setItem(USER_AUTH,JSON.stringify(authData));
          sessionStorage.setItem(SYS_BUTTON_AUTH,JSON.stringify(allAuthData));
          if (menuData && menuData.length > 0) {
            //update--begin--autor:qinfeng-----date:20200109------for：JEECG-63 一级菜单的子菜单全部是隐藏路由，则一级菜单不显示------
            menuData.forEach((item, index) => {
              if (item["children"]) {
                let hasChildrenMenu = item["children"].filter((i) => {
                  return !i.hidden || i.hidden == false
                })
                if (hasChildrenMenu == null || hasChildrenMenu.length == 0) {
                  item["hidden"] = true
                }
              }
            })
            // console.log(" menu show json ", menuData)
            //update--end--autor:qinfeng-----date:20200109------for：JEECG-63 一级菜单的子菜单全部是隐藏路由，则一级菜单不显示------
            commit('SET_PERMISSIONLIST', menuData)
          } else {
            let err=new Error("'getPermissionList: permissions must be a non-null array !'")
            err.code="100010"
            reject(err)
          }
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },
    // 刷新用户token
    Refresh ({ commit }) {
      return new Promise((resolve, reject) => {
        let token=Vue.ls.get(ACCESS_TOKEN)
        if (token) {
          refresh(token).then(res => {
            if (res.success) {
              Vue.ls.remove(ACCESS_TOKEN)
              Vue.ls.set(LAST_REFRESH_TIME, new Date())
              Vue.ls.set(ACCESS_TOKEN, res.result.token, 7 * 24 * 60 * 60 * 1000)
              commit('SET_TOKEN', res.result.token)
              resolve("刷新token成功")
            } else {
              reject('刷新token失败')
            }
          }).catch(err=>{
            reject(err)
          })
        } else {
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
          // 弹出重新登录提示
          store.dispatch('openLogoutMessage')
          reject('登录已过期')
        }
      })
    },
    // 登出
    Logout({ commit, state }) {
      return new Promise((resolve) => {
        let logoutToken = state.token;
        commit('SET_TOKEN', '')
        commit('SET_PERMISSIONLIST', [])
        Vue.ls.remove(ACCESS_TOKEN)
        Vue.ls.remove(UI_CACHE_DB_DICT_DATA)

        logout(logoutToken).then((res) => {
          //let sevice = "http://"+window.location.host+"/";
          //let serviceUrl = encodeURIComponent(sevice);
          //window.location.href = window._CONFIG['casPrefixUrl']+"/logout?service="+serviceUrl;
          resolve(res)
        }).catch(() => {
          resolve()
        })
      })
    },
    // 第三方登录
    ThirdLogin({ commit }, token) {
      return new Promise((resolve, reject) => {
        thirdLogin(token).then(response => {
          if(response.code =='200'){
            const result = response.result
            console.log('login result:', result)
            const userInfo = result.userInfo
            const lines=result.lines
            Vue.ls.set(LAST_REFRESH_TIME, new Date())
            Vue.ls.set(ACCESS_TOKEN, result.token, 7 * 24 * 60 * 60 * 1000)
            Vue.ls.set(USER_NAME, userInfo.username, 7 * 24 * 60 * 60 * 1000)
            Vue.ls.set(USER_INFO, userInfo, 7 * 24 * 60 * 60 * 1000)
            // Vue.ls.set(UI_CACHE_DB_DICT_DATA, result.sysAllDictItems, 7 * 24 * 60 * 60 * 1000)
            Vue.ls.set(USER_LINES, lines, 7 * 24 * 60 * 60 * 1000)
            commit('SET_TOKEN', result.token)
            commit('SET_INFO', userInfo)
            commit('SET_NAME', { username: userInfo.username,realname: userInfo.realname, welcome: welcome() })
            commit('SET_AVATAR', userInfo.avatar)
            commit('SET_LINES',lines)
            resolve(response)
          }else{
            reject(response)
          }
        }).catch(error => {
          reject(error)
        })
      })
    },
    openLogoutMessage(){
      if (!this.state.user.activeLogout) {
        // if (router.currentRoute.path === '/') return
        this.state.user.activeLogout = true
        Modal.error({
          title: '登录已过期',
          content: '很抱歉，登录已过期，请重新登录',
          okText: '重新登录',
          mask: false,
          onOk: () => {
            store.dispatch('Logout').then(() => {
              Vue.ls.remove(ACCESS_TOKEN)
              // // window.location.reload()
              router.push('/user/login')
              this.state.user.activeLogout = false
            })
          }
        })
      }
    }
  }
}

export default user
