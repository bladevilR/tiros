<template>
  <div id="userLayout" :class="['user-layout-wrapper', device]">
    <div class="container">
      <div class="top">
        <!--<div class="header">
          <a href="/">
            <img src="~@/assets/tiros/images/logo.png" class="logo" alt="logo">
          </a>
        </div>
        <div class="desc">
          苏州轨道交通车辆架大修信息管理系统
        </div>-->
        <img src="~@/assets/tiros/images/logo.gif" class="logo" alt="logo" style="width: 170px;">
        <div style="text-align: center; font-weight: bold; font-size: 12pt; padding: 15px; margin-bottom: 10px;">
          苏州轨道交通车辆架大修信息管理系统
        </div>
      </div>
      <route-view></route-view>
    </div>
    <div class="footer">
      <!--<div class="links">
        <a href="http://doc.jeecg.com" target="_blank">帮助</a>
        <a href="https://github.com/zhangdaiscott/jeecg-boot" target="_blank">隐私</a>
        <a href="https://github.com/zhangdaiscott/jeecg-boot/blob/master/LICENSE" target="_blank">条款</a>
      </div>-->
      <div class="copyright">
        <router-link :to="{ path: '/sso/check' }" style="margin-right: 5px">&copy;</router-link> 2020 <a href="http://www.nannar.cn" target="_blank">东莞市诺丽电子科技有限公司</a> 出品
      </div>
    </div>
  </div>
</template>

<script>
  import RouteView from "@/components/layouts/RouteView"
  import { mixinDevice } from '@/utils/mixin.js'

  export default {
    name: "UserLayout",
    components: { RouteView },
    mixins: [mixinDevice],
    data () {
      return {}
    },
    mounted () {
      document.body.classList.add('userLayout')
    },
    beforeDestroy () {
      document.body.classList.remove('userLayout')
    },
  }
</script>

<style lang="less" scoped>
  #userLayout.user-layout-wrapper {
    height: 100%;
    /*background: #f0f2f5 url(~@/assets/background.svg) no-repeat 50%;*/
    background: #f0f2f5 url(~@/assets/tiros/images/login_bg.jpg) no-repeat center;
    background-size: cover;
    -webkit-background-size: cover;
    -o-background-size: cover;
    background-position: center 0;
    &.mobile {
      .container {
        .main {
          // max-width: 368px;
          width: 98%;
        }
      }
    }

    .container {
      width: 390px;
      min-height:430px;
     /* height: 490px;*/
     /* min-height: 100%;*/
     /* padding: 100px 0 100px;*/
      padding: 20px;
      position: absolute;
      top: 50%;
      left: 50%;
      margin: -273px 0 0 -195px;
      border-radius: 0.5em;
      -webkit-box-shadow: 3px 3px 6px #666;
      -moz-box-shadow: 3px 3px 6px #666;
      box-shadow: 3px 3px 6px #666;
      background: #ffffff;

      a {
        text-decoration: none;
      }

      .top {
        text-align: center;
        margin-top: 10px;
        .header {
          height: 44px;
          line-height: 44px;

          .badge {
            position: absolute;
            display: inline-block;
            line-height: 1;
            vertical-align: middle;
            margin-left: -12px;
            margin-top: -10px;
            opacity: 0.8;
          }

          .logo {
            vertical-align: top;
            margin-right: 16px;
            border-style: none;
          }

          .title {
            font-size: 33px;
            color: rgba(0, 0, 0, .85);
            font-family: "Chinese Quote", -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", "Helvetica Neue", Helvetica, Arial, sans-serif, "Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol";
            font-weight: 600;
            position: relative;
            top: 2px;
          }
        }
        .desc {
          font-size: 14px;
          color: rgba(0, 0, 0, 0.45);
          margin-top: 12px;
          margin-bottom: 40px;
        }
      }

      .main {
        min-width: 260px;
        width: 350px;
        margin: 0 auto;
      }

    }

    .footer {
      position: absolute;
      width: 100%;
      bottom: 0;
      padding: 0 16px;
      margin: 48px 0 24px;
      text-align: center;

      .links {
        margin-bottom: 8px;
        font-size: 14px;
      }
      .copyright {
        color: #d4d0d0;
        font-size: 14px;
        a {
          color: #667e87;
          font-weight: bold;
          transition: all 0.3s;
          &:not(:last-child) {
            margin-right: 40px;
          }
        }
      }
    }
  }
</style>