import Vue from 'vue'
import { LAST_REFRESH_TIME } from '@/store/mutation-types'
import moment from 'moment'
import store from '@/store'
const whiteList = ['/sys/login','/user/login', '/user/register', '/user/register-result', '/user/alteration', '/403', '/404', '/500', '/board/homepage', '/refresh/token', '/sys/randomImage']

export function checkRefreshToken (url) {
  let needCheck=whiteList.filter(w=>{
    return url.indexOf(w) >= 0
  })
  if(needCheck.length > 0 ) {
    return;
  }
  // 判断token时间
  let d = Vue.ls.get(LAST_REFRESH_TIME)
  if (d) {
    let old = moment(d)
    let now = moment()
    let diff = now.diff(old, 'minute')

    if (diff >= 60) {
      // 超过60分钟
      console.log('Token已经超过60分钟了，开始自动刷新Token......')
      store.dispatch('Refresh').then(res => {
        console.log(res)
      }).catch(err => {
        console.error(err)
      })
    } else {
      // console.log('Token 时长：%s 分钟', diff)
    }
  }
}