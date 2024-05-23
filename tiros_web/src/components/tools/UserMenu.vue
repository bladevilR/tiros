<template>
  <div class="user-wrapper" :class="theme">
    <!-- update_begin author:zhaoxin date:20191129 for: 做头部菜单栏导航 -->
    <!-- update-begin author:sunjianlei date:20191@20 for: 解决全局样式冲突的问题 -->
    <span class="action action-full" @click="showClick">
      <a-icon type="search"></a-icon>
    </span>
    <!-- update-begin author:sunjianlei date:20200219 for: 菜单搜索改为动态组件，在手机端呈现出弹出框 -->
    <component :is="searchMenuComp" v-show="searchMenuVisible || isMobile()" class="borders"
               :visible="searchMenuVisible" title="搜索菜单" :footer="null" @cancel="searchMenuVisible=false">
      <a-select
        class="search-input"
        showSearch
        :showArrow="false"
        placeholder="搜索菜单"
        optionFilterProp="children"
        :filterOption="filterOption"
        :open="isMobile()?true:null"
        :getPopupContainer="(node) => node.parentNode"
        :style="isMobile()?{width: '100%',marginBottom:'50px'}:{}"
        @change="searchMethods"
        @blur="hiddenClick"
      >
        <a-select-option v-for="(site,index) in searchMenuOptions" :key="index" :value="site.id">{{ site.meta.title }}
        </a-select-option>
      </a-select>
    </component>
    <!-- update-end author:sunjianlei date:20200219 for: 菜单搜索改为动态组件，在手机端呈现出弹出框 -->
    <!-- update-end author:sunjianlei date:20191220 for: 解决全局样式冲突的问题 -->
    <!-- update_end  author:zhaoxin date:20191129 for: 做头部菜单栏导航 -->
<!--     <span class="action action-full">
       <a class="logout_title" title="用户手册">
         <a-icon type="question-circle-o"></a-icon>
       </a>
     </span>-->
    <a-popover
      trigger="click"
      placement="bottomRight"
      :autoAdjustOverflow="true"
      :arrowPointAtCenter="true">
      <span  class="action action-full " title="用户手册">
      <a-icon style="font-size: 16px; padding: 4px" type="question-circle-o" />
    </span>
      <template slot="content">

          <ul class="my_ul">
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-核心业务流程操作手册.pdf">核心流程-操作手册</a>
            </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-操作手册-生产计划.pdf">生产计划-操作手册</a>
        </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-操作手册-物资管理.pdf">物资管理-操作手册</a>
        </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-操作手册-生产作业.pdf">生产作业-操作手册</a>
        </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-操作手册-质量管理.pdf">质量管理-操作手册</a>
        </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-操作手册-故障管理.pdf">故障管理-操作手册</a>
        </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-操作手册-生产监控.pdf">生产监控-操作手册</a>
        </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-操作手册-委外管理.pdf">委外管理-操作手册</a>
        </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-操作手册-生产综合.pdf">生产综合-操作手册</a>
        </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-操作手册-系统管理.pdf">系统管理-操作手册</a>
        </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-操作手册-统计分析.pdf">统计分析-操作手册</a>
        </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-操作手册-基础数据.pdf">基础数据-操作手册</a>
        </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统项目-操作手册-报表中心.pdf">报表中心-操作手册</a>
        </li>
            <li>
        <a target="_blank" href="/js/pdfjs/web/viewer.html?file=/manual/苏州市轨道交通3号线车辆架大修信息化管理系统APP项目-操作手册.pdf">APP-操作手册</a>
        </li>
          </ul>
      </template>
    </a-popover>
    <header-notice class="action action-full" />
    <a-dropdown>
      <span class="action action-full ant-dropdown-link user-dropdown-menu">
        <a-avatar class="avatar" size="small" :src="imgUrl" />
        <span style="margin-left: 5px;padding-top: 10px;" v-if="isDesktop()"
              :title="'欢迎您,'+nickname()">欢迎，{{ getShortName() }}</span>
      </span>
      <a-menu slot="overlay" class="user-dropdown-menu-wrapper">
        <!--        <a-menu-item key="0">
                  <router-link :to="{ name: 'account-center' }">
                    <a-icon type="user"/>
                    <span>个人中心</span>
                  </router-link>
                </a-menu-item>-->
        <!--        <a-menu-item key="1">
                  <router-link :to="{ name: 'account-settings-base' }">
                    <a-icon type="setting"/>
                    <span>账户设置</span>
                  </router-link>
                </a-menu-item>-->
        <a-menu-item key="3" @click="systemSetting">
          <a-icon type="tool" />
          <span>系统设置</span>
        </a-menu-item>
        <a-menu-item key="4" @click="updatePassword">
          <a-icon type="setting" />
          <span>密码修改</span>
        </a-menu-item>
        <!--        <a-menu-item key="5" @click="updateCurrentDepart">
                  <a-icon type="cluster"/>
                  <span>切换部门</span>
                </a-menu-item>-->
        <!-- <a-menu-item key="2" disabled>
           <a-icon type="setting"/>
           <span>测试</span>
         </a-menu-item>
         <a-menu-divider/>
         <a-menu-item key="3">
           <a href="javascript:;" @click="handleLogout">
             <a-icon type="logout"/>
             <span>退出登录</span>
           </a>
         </a-menu-item>-->
      </a-menu>
    </a-dropdown>
    <span class="action action-full" v-if="!is_uniapp">
      <a class="logout_title" href="javascript:;" @click="handleLogout">
        <a-icon type="logout" />
        <span v-if="isDesktop()">&nbsp;退出登录</span>
      </a>
    </span>
    <user-password ref="userPassword"></user-password>
    <depart-select ref="departSelect" :closable="true" title="部门切换"></depart-select>
    <setting-drawer ref="settingDrawer" :closable="true" title="系统设置"></setting-drawer>
  </div>
</template>

<script>
import HeaderNotice from './HeaderNotice'
import UserPassword from './UserPassword'
import SettingDrawer from '@/components/setting/SettingDrawer'
import DepartSelect from './DepartSelect'
import { mapActions, mapGetters, mapState } from 'vuex'
import { mixinDevice } from '@/utils/mixin.js'
import { getFileAccessHttpUrl, getFileAccessPathUrl } from '@/api/manage'
import { everythingIsEmpty, stringLength, stringSubByLen } from '@/utils/util'
import Vue from 'vue'

export default {
  name: 'UserMenu',
  mixins: [mixinDevice],
  data () {
    return {
      is_uniapp: false,
      imgUrl: '',
      // update-begin author:sunjianlei date:20200219 for: 头部菜单搜索规范命名 --------------
      searchMenuOptions: [],
      searchMenuComp: 'span',
      searchMenuVisible: false
      // update-begin author:sunjianlei date:20200219 for: 头部菜单搜索规范命名 --------------
    }
  },
  components: {
    HeaderNotice,
    UserPassword,
    DepartSelect,
    SettingDrawer
  },
  props: {
    theme: {
      type: String,
      required: false,
      default: 'dark'
    }
  },
  /* update_begin author:zhaoxin date:20191129 for: 做头部菜单栏导航*/
  created () {
    let lists = []
    this.searchMenus(lists, this.permissionMenuList)
    this.searchMenuOptions = [...lists]
    this.getAvatar()
    this.is_uniapp = localStorage.getItem('is_uniapp');
  },
  computed: {
    ...mapState({
      // 后台菜单
      permissionMenuList: state => state.user.permissionList

    })
  },
  /* update_end author:zhaoxin date:20191129 for: 做头部菜单栏导航*/
  watch: {
    // update-begin author:sunjianlei date:20200219 for: 菜单搜索改为动态组件，在手机端呈现出弹出框
    device: {
      immediate: true,
      handler () {
        this.searchMenuVisible = false
        this.searchMenuComp = this.isMobile() ? 'a-modal' : 'span'
      }
    }
    // update-end author:sunjianlei date:20200219 for: 菜单搜索改为动态组件，在手机端呈现出弹出框
  },
  methods: {
    getShortName () {
      let len = stringLength(this.nickname())

      if (len > 6) {
        return stringSubByLen(this.nickname(), 6, true)
      } else {
        return this.nickname()
      }
    },
    /* update_begin author:zhaoxin date:20191129 for: 做头部菜单栏导航*/
    showClick () {
      this.searchMenuVisible = !this.searchMenuVisible
    },
    hiddenClick () {
      this.shows = false
    },
    /* update_end author:zhaoxin date:20191129 for: 做头部菜单栏导航*/
    ...mapActions(['Logout']),
    ...mapGetters(['nickname', 'avatar', 'userInfo']),
    getAvatar () {
      if (!everythingIsEmpty(this.avatar())) {
        getFileAccessPathUrl(this.avatar()).then((res) => {
          this.imgUrl = res
        })
      }
    },
    handleLogout () {
      const that = this

      this.$confirm({
        title: '提示',
        content: '真的要注销登录吗 ?',
        onOk () {
          return that.Logout({}).then((res) => {
            // document.cookie = 'UserInfo=; path=/; domain=.sz-mtrtest.com; expires=' + new Date(0).toUTCString();
            // document.cookie = 'EipSsoUserInfo=; path=/; domain=.sz-mtrtest.com; expires=' + new Date(0).toUTCString();
            // document.cookie = 'EACToken=; path=/; domain=.sz-mtrtest.com; expires=' + new Date(0).toUTCString();
            // document.cookie = 'EACTokenVUSER=; path=/; domain=.sz-mtrtest.com; expires=' + new Date(0).toUTCString();
            let login_info = Vue.ls.get('LOGIN_INFO');
            let is_uniapp = localStorage.getItem('is_uniapp')
            if(login_info) {
              login_info.autoLogin = false;
            }
            window.sessionStorage.clear()
            window.localStorage.clear()
            if(is_uniapp){
              localStorage.setItem('is_uniapp',is_uniapp)
            }
            Vue.ls.set('LOGIN_INFO',login_info)
            if(res && res.result){
              window.location.href = res.result
            } else {
              window.location.href = '/user/login'
            }
          }).catch(err => {
            that.$message.error({
              title: '错误',
              description: err.message
            })
          })
        },
        onCancel () {
        }
      })
    },
    updatePassword () {
      let username = this.userInfo().username
      this.$refs.userPassword.show(username)
    },
    updateCurrentDepart () {
      this.$refs.departSelect.show()
    },
    systemSetting () {
      this.$refs.settingDrawer.showDrawer()
    },
    /* update_begin author:zhaoxin date:20191129 for: 做头部菜单栏导航*/
    searchMenus (arr, menus) {
      for (let i of menus) {
        if (!i.hidden && 'layouts/RouteView' !== i.component) {
          arr.push(i)
        }
        if (i.children && i.children.length > 0) {
          this.searchMenus(arr, i.children)
        }
      }
    },
    filterOption (input, option) {
      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    },
    // update_begin author:sunjianlei date:20191230 for: 解决外部链接打开失败的问题
    searchMethods (value) {
      let route = this.searchMenuOptions.filter(item => item.id === value)[0]
      if (route.meta.internalOrExternal === true || route.component.includes('layouts/IframePageView')) {
        window.open(route.meta.url, '_blank')
      } else {
        this.$router.push({ path: route.path })
      }
      this.searchMenuVisible = false
    }
    // update_end author:sunjianlei date:20191230 for: 解决外部链接打开失败的问题
    /*update_end author:zhaoxin date:20191129 for: 做头部菜单栏导航*/
  }
}
</script>

<style lang="less" scoped>
.my_ul {
  padding: 0px;

  li {
    list-style-type: none;
    padding-top: 5px;
    width:150px;
    text-align: center;
  }
  li:hover {
    background: #eeeeee;
  }
}
/* update_begin author:zhaoxin date:20191129 for: 让搜索框颜色能随主题颜色变换*/
/* update-begin author:sunjianlei date:20191220 for: 解决全局样式冲突问题 */
.user-wrapper .search-input {
  width: 180px;
  color: inherit;

  /deep/ .ant-select-selection {
    background-color: inherit;
    border: 0;
    border-bottom: 1px solid white;

    &__placeholder, &__field__placeholder {
      color: inherit;
    }
  }
}

.layout .header.dark .user-wrapper .action, .layout .top-nav-header-index.dark .user-wrapper .action {
  color: #FFFFFF !important;

  .anticon {
    color: #FFFFFF !important;
  }

  .header-notice {
    color: #FFFFFF !important;
  }
}

.layout .header .user-wrapper .action .avatar, .layout .top-nav-header-index .user-wrapper .action .avatar {
  margin: 0px !important;
}

/* update-end author:sunjianlei date:20191220 for: 解决全局样式冲突问题 */
/* update_end author:zhaoxin date:20191129 for: 让搜索框颜色能随主题颜色变换*/
</style>

<style scoped>
.logout_title {
  color: inherit;
  text-decoration: none;
}



</style>