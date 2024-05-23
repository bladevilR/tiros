<template>
  <div class="logo">
    <router-link :to="{name:'dashboard'}">
      <!-- update-begin- author:sunjianlei --- date:20190814 --- for: logo颜色根据主题颜色变化 -->
      <img v-if="navTheme === 'dark'" src="~@/assets/tiros/images/logo.gif" alt="logo">
      <img v-else src="~@/assets/tiros/images/logo.gif" alt="logo">
      <!-- update-begin- author:sunjianlei --- date:20190814 --- for: logo颜色根据主题颜色变化 -->

      <h1 v-if="showTitle">{{ title }}</h1>
    </router-link>
  </div>
</template>

<script>
  import { mixin } from '@/utils/mixin.js'

  export default {
    name: 'Logo',
    mixins: [mixin],
    props: {
      title: {
        type: String,
        default: '架大修管理系统',
        required: false
      },
      showTitle: {
        type: Boolean,
        default: true,
        required: false
      }
    }
  }
</script>
<style lang="less" scoped>
  /*缩小首页布 局顶部的高度*/
  @height: 50px;

  .sider {
    box-shadow: none !important;
    .logo {
      height: @height !important;
      line-height: @height !important;
      box-shadow: none !important;
      transition: background 300ms;

      a {
        color: white;
        &:hover {
          color: rgba(255, 255, 255, 0.8);
        }
      }

      img {
        height: 22px;
      }

      h1 {
        font-size: 15px;
        font-weight: bold;
      }
    }

    &.light .logo {
      background-color: @primary-color;
    }
  }
  .layout .top-nav-header-index .header-index-wide .header-index-left .logo.top-nav-header h1{
    font-size: 15px !important;
    color: white !important;
    font-weight: bold;
  }
</style>