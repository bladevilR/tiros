import Vue from 'vue'
import { ACCESS_TOKEN, LAST_REFRESH_TIME } from '@/store/mutation-types'
import moment from 'moment'
import store from '@/store'

const whiteList = ['/sys/login','/user/login', '/user/register', '/user/register-result', '/user/alteration', '/403', '/404', '/500', '/board/homepage', '/refresh/token', '/sys/randomImage']

function parseTokenPayload (token) {
  if (!token || typeof token !== 'string') {
    return null
  }
  const sections = token.split('.')
  if (sections.length < 2) {
    return null
  }
  try {
    const normalized = sections[1].replace(/-/g, '+').replace(/_/g, '/')
    const padding = '='.repeat((4 - normalized.length % 4) % 4)
    const decoded = atob(normalized + padding)
    const json = decodeURIComponent(decoded.split('').map(char => {
      return `%${('00' + char.charCodeAt(0).toString(16)).slice(-2)}`
    }).join(''))
    return JSON.parse(json)
  } catch (error) {
    return null
  }
}

export function isTokenExpired (token, bufferSeconds = 30) {
  const payload = parseTokenPayload(token)
  if (!payload || !payload.exp) {
    return false
  }
  const nowSeconds = Math.floor(Date.now() / 1000)
  return payload.exp <= nowSeconds + bufferSeconds
}

export function isNoNeedCheckUrl (url = '') {
  if (!url || typeof url !== 'string') {
    return false
  }
  return whiteList.some(w => url.indexOf(w) >= 0)
}

export function checkRefreshToken (url) {
  if (isNoNeedCheckUrl(url)) {
    return
  }
  const token = Vue.ls.get(ACCESS_TOKEN)
  if (!token || isTokenExpired(token)) {
    return
  }
  let d = Vue.ls.get(LAST_REFRESH_TIME)
  if (d) {
    let old = moment(d)
    let now = moment()
    let diff = now.diff(old, 'minute')

    if (diff >= 60) {
      store.dispatch('Refresh').catch(err => {
        console.error(err)
      })
    }
  }
}
