import Vue from 'vue'
import App from './App.vue'
import Storage from 'vue-ls'
import router from './router'
import store from './store/'

import { VueAxios } from "@/utils/request"

import moment from 'moment';
import Antd, { Modal } from 'ant-design-vue'
import Viser from 'viser-vue'
import 'ant-design-vue/dist/antd.less';  // or 'ant-design-vue/dist/antd.less'

import 'xe-utils'
import VXETable from 'vxe-table'
import 'vxe-table/lib/index.css'

import '@/permission' // permission control
import '@/utils/filter' // base filter
import Print from 'vue-print-nb-jeecg'
/*import '@babel/polyfill'*/
import preview from 'vue-photo-preview'
import 'vue-photo-preview/dist/skin.css'

require('@jeecg/antd-online-beta220')
require('@jeecg/antd-online-beta220/dist/OnlineForm.css')

import {
    ACCESS_TOKEN,
    DEFAULT_COLOR,
    DEFAULT_THEME,
    DEFAULT_LAYOUT_MODE,
    DEFAULT_COLOR_WEAK,
    SIDEBAR_TYPE,
    DEFAULT_FIXED_HEADER,
    DEFAULT_FIXED_HEADER_HIDDEN,
    DEFAULT_FIXED_SIDEMENU,
    DEFAULT_CONTENT_WIDTH_TYPE,
    DEFAULT_MULTI_PAGE
} from "@/store/mutation-types"
import config from '@/defaultSettings'

import tirosComponents from '@/components/tiros'
import JDictSelectTag from './components/dict/index.js'
import JDictSelectSeachTag from '@/components/dict/JDictSelectSeachTag.vue'
Vue.component('JDictSelectSeachTag',JDictSelectSeachTag)
import hasPermission from '@/utils/hasPermission'
import vueBus from '@/utils/vueBus';
import JeecgComponents from '@/components/jeecg/index'
import '@/assets/less/JAreaLinkage.less'
import '@/assets/less/vxe-table.less'
import '@/assets/less/index.less'
import '@/assets/less/gantt.less'
import VueAreaLinkage from 'vue-area-linkage'

import workflow from 'nannar-workflow-plus';
import 'nannar-workflow-plus/workflow-plus.css';
import { WebuiMixin } from '@/mixins/WebuiMixin'
import { windowClient } from '@/utils/class/windowClient.class'

Vue.prototype.$moment = moment;
Vue.config.productionTip = false

// 设置工作流接口组件的服务地址 add by jakgong
// http://106.53.225.45:8088/wf
let wfUrl = window._CONFIG['workflowURL'] || process.env.domianURL;
if (wfUrl) {
    Vue.use(workflow, wfUrl)
} else {
    console.error("流程服务地址为空，则无法使用工作流相关功能")
}

// add by jakgong
VXETable.setup({
    table: {
        stripe: true,
        showOverflow:"tooltip",
        tooltipConfig: {
          zIndex:'1200',
          theme: 'dark',
          enterable: false,
          // 重写省略部分 tooltip 显示规则
          contentMethod(data){
            try { // 执行块
              let {cell} = data;
              const text = cell.innerText;
              let span = document.createElement('span');
              span.innerHTML  = text;
              cell.append(span)
              const textWidth = span.offsetWidth;
              cell.removeChild(span)

              let div =  document.createElement('div');
              let boxDom = cell.children[0];
              boxDom.append(div);
              const divWidth = div.offsetWidth;
              boxDom.removeChild(div);

              if( divWidth <= textWidth){
                return text
              }

            } catch (error) {
               console.log(error)
            }
          },
        },
        resizable: true,
        keepSource: false,
        autoResize: true
    }
})

Vue.use(Storage, config.storageOptions)
Vue.use(Antd)
Vue.use(VXETable)
Vue.use(VueAxios, router)
Vue.use(Viser)
Vue.use(hasPermission)
Vue.use(JDictSelectTag)
Vue.use(tirosComponents)
Vue.use(Print)
Vue.use(preview)
Vue.use(vueBus);
Vue.use(JeecgComponents);
Vue.use(VueAreaLinkage);

// 添加全局过滤器 add by jakgong
Vue.filter('dateformat', function (date) {
    if (!date) return ''
    if (date instanceof Date) {
        return moment(date).format("YYYY-MM-DD HH:mm");
    } else {
        try {
            const temp = new Date(date);
            return moment(temp).format("yyyy-MM-DD HH:mm");
        } catch (e) {
            console.error("将 %o 转成Date类型异常", date);
            return date;
        }
    }
});

Vue.mixin(WebuiMixin);

// 解决使用this.$confirm时英文问题， add by jakgong
Vue.$confirm = Modal.confirm

// 设置弹窗，默认点击其他不进行关闭 add by jakgong
Modal.props.maskClosable.default = false

let vm = new Vue({
  router,
  store,
  mounted () {
    store.commit('SET_SIDEBAR_TYPE', Vue.ls.get(SIDEBAR_TYPE, true))
    store.commit('TOGGLE_THEME', Vue.ls.get(DEFAULT_THEME, config.navTheme))
    store.commit('TOGGLE_LAYOUT_MODE', Vue.ls.get(DEFAULT_LAYOUT_MODE, config.layout))
    store.commit('TOGGLE_FIXED_HEADER', Vue.ls.get(DEFAULT_FIXED_HEADER, config.fixedHeader))
    store.commit('TOGGLE_FIXED_SIDERBAR', Vue.ls.get(DEFAULT_FIXED_SIDEMENU, config.fixSiderbar))
    store.commit('TOGGLE_CONTENT_WIDTH', Vue.ls.get(DEFAULT_CONTENT_WIDTH_TYPE, config.contentWidth))
    store.commit('TOGGLE_FIXED_HEADER_HIDDEN', Vue.ls.get(DEFAULT_FIXED_HEADER_HIDDEN, config.autoHideHeader))
    store.commit('TOGGLE_WEAK', Vue.ls.get(DEFAULT_COLOR_WEAK, config.colorWeak))
    store.commit('TOGGLE_COLOR', Vue.ls.get(DEFAULT_COLOR, config.primaryColor))
    store.commit('SET_TOKEN', Vue.ls.get(ACCESS_TOKEN))
    store.commit('SET_MULTI_PAGE', Vue.ls.get(DEFAULT_MULTI_PAGE, config.multipage))
  },
  render: h => h(App)
}).$mount('#app')
window.$vue = vm
