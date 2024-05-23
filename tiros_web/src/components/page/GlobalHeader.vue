<template>
  <!-- , width: fixedHeader ? `calc(100% - ${sidebarOpened ? 256 : 80}px)` : '100%'  -->
  <a-layout-header
    v-if="!headerBarFixed"
    :class="[fixedHeader && 'ant-header-fixedHeader', sidebarOpened ? 'ant-header-side-opened' : 'ant-header-side-closed', ]"
    :style="{ padding: '0' }">
    <!-- 侧边菜单模式 -->
    <div v-if="mode === 'sidemenu'" class="header" :class="theme">
      <a-icon
        v-if="device==='mobile'"
        class="trigger"
        :type="collapsed ? 'menu-fold' : 'menu-unfold'"
        @click="toggle"></a-icon>
      <a-icon
        v-else
        class="trigger"
        :type="collapsed ? 'menu-unfold' : 'menu-fold'"
        @click="toggle"/>
      <!--<span v-if="device === 'desktop'">欢迎进入 Jeecg-Boot 企业级快速开发平台</span>-->
      <span v-else></span>
      <user-menu :theme="theme"/>
    </div>
    <!-- 顶部导航栏模式 -->
    <div v-if="mode === 'topmenu'"  :class="['top-nav-header-index', theme]">
      <div class="header-index-wide">
        <div class="header-index-left" :style="topMenuStyle.headerIndexLeft">
          <logo class="top-nav-header" :show-title="device !== 'mobile'" :style="topMenuStyle.topNavHeader"/>
          <div v-if="device !== 'mobile'" class="topmenu" :style="topMenuStyle.topSmenuStyle">
            <s-menu
              mode="horizontal"
              :menu="menus"
              :theme="theme"></s-menu>
          </div>
          <a-icon
            v-else
            class="trigger"
            :type="collapsed ? 'menu-fold' : 'menu-unfold'"
            @click="toggle"></a-icon>
        </div>
        <user-menu class="header-index-right" :theme="theme" :style="topMenuStyle.headerIndexRight"/>
      </div>
    </div>
    <!-- 混合模式(顶部一级菜单，侧边二级菜单及以下) -->
    <div v-if="mode === 'mixmenu'" class="header mixheader" :class="['top-nav-header-index', theme]">
      <div :style="{'width':'58px','float':'left'}">
        <a-icon
          v-if="device==='mobile'"
          class="trigger"
          :type="collapsed ? 'menu-fold' : 'menu-unfold'"
          @click="toggle"></a-icon>
        <a-icon
          v-else
          class="trigger"
          :type="collapsed ? 'menu-unfold' : 'menu-fold'"
          @click="toggle"/>
      </div>
      <div v-if="device !== 'mobile'" class="mixmenu header-index-left" :style="{'width': 'calc(100% - 420px)'}">
        <a-menu v-model="selectMenus" :theme="theme" :multiple="false" mode="horizontal" @select="selectMenu" :style="{'width': '100%'}">
          <a-menu-item v-for="m in rootMenus" :key="m.id" :title="m.meta && m.meta.title ? m.meta.title : m.name">
            <a-icon v-if="m.meta && m.meta.icon" :type="m.meta.icon" />
            {{m.meta && m.meta.title ? m.meta.title : m.name}}
          </a-menu-item>
        </a-menu>
      </div>
      <user-menu class="header-index-right" :theme="theme" :style="topMenuStyle.headerIndexRight"/>
    </div>
  </a-layout-header>
</template>

<script>
  import UserMenu from '../tools/UserMenu'
  import SMenu from '../menu/'
  import Logo from '../tools/Logo'

  import { mixin } from '@/utils/mixin.js'

  export default {
    name: 'GlobalHeader',
    components: {
      UserMenu,
      SMenu,
      Logo
    },
    mixins: [mixin],
    props: {
      mode: {
        type: String,
        // sidemenu, topmenu
        default: 'sidemenu'
      },
      menus: {
        type: Array,
        required: true
      },
      theme: {
        type: String,
        required: false,
        default: 'dark'
      },
      collapsed: {
        type: Boolean,
        required: false,
        default: false
      },
      device: {
        type: String,
        required: false,
        default: 'desktop'
      }
    },
    data() {
      return {
        rootMenus: [],
        selectMenus: [], // 当前选中菜单
        headerBarFixed: false,
        //update-begin--author:sunjianlei---date:20190508------for: 顶部导航栏过长时显示更多按钮-----
        topMenuStyle: {
          headerIndexLeft: {},
          topNavHeader: {},
          headerIndexRight: {},
          topSmenuStyle: {}
        }
      }
    },
    watch: {
      /** 监听设备变化 */
      device() {
        if (this.mode === 'topmenu' || this.mode === 'mixmenu') {
          this.buildTopMenuStyle()
        }
      },
      /** 监听导航栏模式变化 */
      mode(newVal) {
        if (newVal === 'topmenu' || newVal === 'mixmenu') {
          this.buildTopMenuStyle()
        }
      },
    },
    created () {
      this.rootMenus = this.getRootMenus()
    },
    //update-end--author:sunjianlei---date:20190508------for: 顶部导航栏过长时显示更多按钮-----
    mounted() {
      window.addEventListener('scroll', this.handleScroll)
      //update-begin--author:sunjianlei---date:20190508------for: 顶部导航栏过长时显示更多按钮-----
      if (this.mode === 'topmenu') {
        this.buildTopMenuStyle()
      }
      //update-end--author:sunjianlei---date:20190508------for: 顶部导航栏过长时显示更多按钮-----
    },
    methods: {
      getRootMenus () {
        let curOpenMenuId = ''
        if (this.$route.meta.id) {
          curOpenMenuId = this.$route.meta.id
        }

        const r_menus = []
        let openModule = null
        this.menus.map((item) => {
          if(!item.hidden) {
            const m = {};
            Object.assign(m, item);
            m.children = null
            r_menus.push(m);
            if (curOpenMenuId && item.children && item.children.length>0) {
              const childs = item.children.filter(c => c.id === curOpenMenuId)
              if (childs && childs.length > 0) {
                openModule = m
              }
            }
          }
        });
        // console.log('the root menus:', r_menus)

        if (r_menus.length > 0 && !openModule) {
          let item = Object.assign({ key: r_menus[0].id }, r_menus[0])
          this.selectMenu(item)
          this.selectMenus = [item.id]
        } else {
          // 设置当前路径所属模块为选中模块
          let item = Object.assign({ key: openModule.id }, openModule)
          this.selectMenu(item)
          this.selectMenus = [item.id]
        }
        return r_menus;
      },
      handleScroll() {
        if (this.autoHideHeader) {
          let scrollTop = window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop
          if (scrollTop > 100) {
            this.headerBarFixed = true
          } else {
            this.headerBarFixed = false
          }
        } else {
          this.headerBarFixed = false
        }
      },
      toggle() {
        this.$emit('toggle')
      },
      //update-begin--author:sunjianlei---date:20190508------for: 顶部导航栏过长时显示更多按钮-----
      buildTopMenuStyle() {
        if (this.mode === 'topmenu' || this.mode === 'mixmenu') {
          if (this.device === 'mobile') {
            // 手机端需要清空样式，否则显示会错乱
            this.topMenuStyle.topNavHeader = {}
            this.topMenuStyle.topSmenuStyle = {}
            this.topMenuStyle.headerIndexRight = {}
            this.topMenuStyle.headerIndexLeft = {}
          } else {
            let rightWidth = '360px'
            this.topMenuStyle.topNavHeader = { 'min-width': '200px' }
            this.topMenuStyle.topSmenuStyle = { 'width': 'calc(100% - 165px)' }
            this.topMenuStyle.headerIndexRight = { 'min-width': rightWidth }
            this.topMenuStyle.headerIndexLeft = { 'width': `calc(100% - ${rightWidth})` }
          }
        }
      },
      //update-begin--author:sunjianlei---date:20190508------for: 顶部导航栏过长时显示更多按钮-----
      // 菜单选中
      selectMenu (item) {
        this.$emit('selectRoot', item.key)
      }
    }
  }
</script>

<style lang="less" scoped>
  /* update_begin author:scott date:20190220 for: 缩小首页布局顶部的高度*/
  @height: 50px;
  .layout {
    .top-nav-header-index {
      .header-index-wide {
        margin-left: 10px;

        .ant-menu.ant-menu-horizontal {
          height: @height;
          line-height: @height;
        }

        .header-index-left{
          .logo.top-nav-header{
            height: @height;
            line-height: @height;
          }
        }
      }
      .trigger {
        /*line-height: 64px;*/
        &:hover {
          background: rgba(0, 0, 0, 0.05);
        }
      }
    }

    .header {
      z-index: 2;
      color: white;
      height: @height;
      background-color: @primary-color;
      transition: background 300ms;

      /* dark 样式 */
      &.dark {
        color: #ffffff;
        box-shadow: 0 0 4px rgba(0, 0, 0, 0.2);
        background-color: #001529 !important;
      }
    }

    .header,.top-nav-header-index {
      &.dark .trigger:hover {
        background: rgba(0, 0, 0, 0.05);
      }
    }

    .trigger{
      padding: 0px 18px;
      padding-top: 8px;
    }

    .mixmenu {
      .ant-menu {
        height: @height;
        line-height: @height;
        color: white;
        background-color: transparent;

        .ant-menu-item {
          padding: 0 13px !important;
          .anticon {
            margin-right: 0px !important;
          }

        }
        /*颜色在color.less 中设置*/
       /* .ant-menu-item.ant-menu-item-selected{
          background-color: #827f7f;
          opacity:0.8;
          color: white;
        }
        .ant-menu-item:hover{
          background-color: #827f7f;
          opacity:0.8;
          color: white;
        }*/

        &.ant-menu-dark {
          height: @height;
          line-height: @height;
          color: white;
          background-color: #001529;
        }
        &.ant-menu-horizontal {
          border-bottom: 0px solid #e8e8e8 !important;
        }

      }
    }
    .mixheader{
      .header-index-left{
        float: left;
        flex: 1 1;
        display: flex;
      }
      .header-index-right{
        width: 360px;
        float: right;
        height: @height;
        overflow: hidden;
        .action:hover {
          background-color: rgba(0, 0, 0, 0.05);
        }
      }
    }

    .topmenu {
      .ant-menu.ant-menu-horizontal.ant-menu-root.ant-menu-light {
        height: @height;
        line-height: @height;
        color: white !important;
        background-color: transparent !important;
      }
    }
  }

  .ant-layout-header {
    height: @height;
    line-height: @height;
  }

  /* update_end author:scott date:20190220 for: 缩小首页布局顶部的高度*/

</style>
<style scoped>

</style>