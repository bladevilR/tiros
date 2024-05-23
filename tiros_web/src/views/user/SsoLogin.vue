<template>
  <div class="bgm">
    <div id='loader-wrapper' v-show="!departVisible">
      <div id='loader'></div>
      <div class='loader-section section-left'></div>
      <div class='loader-section section-right'></div>
      <div class='load_title'>正在加载,请耐心等待</div>
    </div>
    <a-modal
      title="登录部门选择"
      :width="450"
      :visible="departVisible"
      :closable="false"
      :maskClosable="false">

      <template slot="footer">
        <a-button type="primary" @click="departOk">确认</a-button>
      </template>

      <a-form>
        <a-form-item
          :labelCol="{span:4}"
          :wrapperCol="{span:20}"
          style="margin-bottom:10px"
          :validate-status="validate_status">
          <a-tooltip placement="topLeft" >
            <template slot="title">
              <span>您隶属于多部门，请选择登录部门</span>
            </template>
            <a-avatar style="backgroundColor:#87d068" icon="gold" />
          </a-tooltip>
          <a-select @change="departChange" :class="{'valid-error':validate_status=='error'}" placeholder="请选择登录部门" style="margin-left:10px;width: 80%">
            <a-icon slot="suffixIcon" type="gold" />
            <a-select-option
              v-for="d in departList"
              :key="d.id"
              :value="d.orgCode">
              {{ d.departName }}
            </a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
  //import md5 from "md5"
  import { INDEX_MAIN_PAGE_PATH, LAST_REFRESH_TIME } from '@/store/mutation-types'
  import TwoStepCaptcha from '@/components/tools/TwoStepCaptcha'
  import { mapActions } from "vuex"
  import { timeFix } from "@/utils/util"
  import Vue from 'vue'
  import { ACCESS_TOKEN ,ENCRYPTED_STRING,USER_GROUPID} from "@/store/mutation-types"
  import { putAction,postAction,getAction } from '@/api/manage'
  import { encryption , getEncryptedString } from '@/utils/encryption/aesEncrypt'
  import store from '@/store/'
  import { USER_INFO } from "@/store/mutation-types"
  import { Modal } from 'ant-design-vue'
  import qs from 'qs'

  export default {
    components: {
      TwoStepCaptcha
    },
    data () {
      return {
        departList: [],
        departVisible: false,
        departSelected: '',
        validate_status: '',
      }
    },
    created () {
      console.log("hahaha")
      this.currdatetime = new Date().getTime();
      Vue.ls.remove(ACCESS_TOKEN)
      this.getRouterData();
      Modal.destroyAll()
      // update-begin- --- author:scott ------ date:20190805 ---- for:密码加密逻辑暂时注释掉，有点问题
      // this.getEncrypte();
      // update-end- --- author:scott ------ date:20190805 ---- for:密码加密逻辑暂时注释掉，有点问题
    },
    mounted () {
      // this.$refs.userName.focus();
      // 判断是否有携带token
      const query = qs.parse(location.search.substring(1))
      let token = query.token;
      if(!token) {
        //window.location.href = '/sso/check'
        this.$router.push('/sso/check')
      } else {
        try {
          console.log('单点登录成功，进行最后登录...')
          Vue.ls.set(LAST_REFRESH_TIME, new Date())
          this.ThirdLogin(token).then(res => {
            if (res.success) {
              // that.loginSuccess()
              this.departConfirm(res)
            } else {
              this.requestFailed(res)
            }
          })
        } catch (e) {
          console.error('单点登录异常：', e)
        }
      }

    },
    methods: {
      ...mapActions([ "ThirdLogin" ]),
      requestFailed (err) {
        this.$notification[ 'error' ]({
          message: '登录失败',
          description: ((err.response || {}).data || {}).message || err.message || "请求出现错误，请稍后再试",
          duration: 4,
        });
      },
      departConfirm(res){
        if(res.success){
          // 下面两行没有用了
          /*let groupsid = res.result.groups[0].id
          sessionStorage.setItem('data-xy',[groupsid])*/
          this.departList = res.result.departs
          let multi_depart = res.result.multi_depart
          //0:无部门 1:一个部门 2:多个部门
          if(multi_depart===0){
            this.loginSuccess()
            this.$notification.warn({
              message: '提示',
              description: `您尚未归属部门,请确认账号信息`,
              duration:3
            });
          }else if(multi_depart === 2){
            this.departVisible=true
            // this.currentUsername=this.form.getFieldValue("username")
            this.departList = res.result.departs
          }else {
            this.loginSuccess()
          }
        }else{
          this.requestFailed(res)
          this.Logout();
        }
      },
      departOk(){
        if(!this.departSelected){
          this.validate_status='error'
          return false
        }
       let obj = {
          orgCode:this.departSelected,
          username:this.form.getFieldValue("username")
        }
        putAction("/sys/selectDepart",obj).then(res=>{
          if(res.success){
            const userInfo = res.result.userInfo;
            Vue.ls.set(USER_INFO, userInfo, 7 * 24 * 60 * 60 * 1000);
            store.commit('SET_INFO', userInfo);
            // console.log("---切换组织机构---userInfo-------",store.getters.userInfo);
            this.loginSuccess()
            this.departClear()
          }else{
            this.requestFailed(res)
            this.Logout().then(()=>{
              this.departClear()
            });
          }
        })
      },
      departClear(){
        this.departList=[]
        this.departSelected=""
        this.currentUsername=""
        this.departVisible=false
        this.validate_status=''
      },
      departChange(value){
        this.validate_status='success'
        this.departSelected = value
      },
      getRouterData(){
        this.$nextTick(() => {
          if (this.$route.params.username) {
            this.form.setFieldsValue({
              'username': this.$route.params.username
            });
          }
        })
    },
      loginSuccess () {
        // update-begin- author:sunjianlei --- date:20190812 --- for: 登录成功后不解除禁用按钮，防止多次点击
        // this.loginBtn = false
        // update-end- author:sunjianlei --- date:20190812 --- for: 登录成功后不解除禁用按钮，防止多次点击
        // console.log('login success UserInfo:', this.$store.getters.userInfo)
        // 设置当前部门的名称
        let departId = this.$store.getters.userInfo.departId
        if (this.departList && this.departList.length > 0) {
          let curDept = this.departList.filter(d => {
            return d.id === departId
          })
          if (curDept && curDept.length > 0) {
            store.commit('SET_DEPARTNAME', curDept[0].departName)
          }
        }

        this.$router.push({ path: INDEX_MAIN_PAGE_PATH })
        this.$notification.success({
          message: '欢迎',
          description: `${timeFix()}，欢迎回来`,
        });
      },
    //获取密码加密规则
    getEncrypte(){
      let encryptedString = Vue.ls.get(ENCRYPTED_STRING);
      if(encryptedString == null){
        getEncryptedString().then((data) => {
          this.encryptedString = data
        });
      }else{
        this.encryptedString = encryptedString;
      }
    },
    }
  }
</script>

<style lang="less" scoped>
.bgm {
  height: calc(100% - 0px);
  width: 100%;
  text-align: center;
  vertical-align: center;
  /*background: #f0f2f5 url(~@/assets/background.svg) no-repeat 50%;*/
  //background: #f0f2f5 url(~@/assets/tiros/images/login_bg.jpg) no-repeat center;
  background-size: cover;
  -webkit-background-size: cover;
  -o-background-size: cover;
  background-position: center 0;
}
</style>
