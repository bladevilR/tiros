<template>
<!--  <div class="bgm">
    <div class="text_tip">登录检查中...</div>
  </div>-->
  <div id='loader-wrapper' >
    <div id='loader'></div>
    <div class='loader-section section-left'></div>
    <div class='loader-section section-right'></div>
    <div class='load_title' @click="goMyLogin">正在加载,请耐心等待</div>
  </div>
</template>

<script>
import Vue from 'vue'
import { ACCESS_TOKEN } from '@/store/mutation-types'

export default {
  name: 'SsoLoginCheck',
  data(){
    timeoutProcess:null
  },
  mounted () {
    let token = Vue.ls.get(ACCESS_TOKEN)
    console.log('/sso/check , access_token:', token)
    let url = window._CONFIG['domianURL'] + `/oauth2/login?token=` + token
    console.log("go go go", url)
    this.timeoutProcess = setTimeout(() => {
      window.location.href = url
    }, 3000)
  },
  methods: {
    goMyLogin () {
      if (this.timeoutProcess) {
        clearTimeout(this.timeoutProcess)
      }
      //window.location.href = "/user/login"
      this.$router.push('/user/login')
    }
  }
}
</script>

<style lang="less" scoped>
.bgm {
  height: calc(100% - 0px);
  width: 100%;
  text-align: center;
  vertical-align: center;
  background: #f5f2f2;
//  background: #f0f2f5 url(~@/assets/tiros/images/login_bg.jpg) no-repeat center;
  background-size: cover;
  -webkit-background-size: cover;
  -o-background-size: cover;
  background-position: center 0;

  position: relative;

  .text_tip {
    position: absolute;
    top: calc(50% - 10px);
    left: calc(50% - 40px);
    text-align: center;
  }
}
</style>