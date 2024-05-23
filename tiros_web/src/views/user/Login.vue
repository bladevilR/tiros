<template>
  <div class="main">
    <a-form :form="form" class="user-layout-login" ref="formLogin" id="formLogin">
      <!--<a-tabs
        :activeKey="customActiveKey"
        :tabBarStyle="{ textAlign: 'center', borderBottom: 'unset' }"
        @change="handleTabClick">
        <a-tab-pane key="tab1" tab="账号密码登陆">
          <a-form-item>
            <a-input
              size="large"
              v-decorator="['username',{initialValue:'', rules: validatorRules.username.rules}]"
              type="text"
              placeholder="请输入帐户名 / admin">
              <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }"/>
            </a-input>
          </a-form-item>

          <a-form-item>
            <a-input
              v-decorator="['password',{initialValue:'', rules: validatorRules.password.rules}]"
              size="large"
              type="password"
              autocomplete="false"
              placeholder="密码 / 123456">
              <a-icon slot="prefix" type="lock" :style="{ color: 'rgba(0,0,0,.25)' }"/>
            </a-input>
          </a-form-item>

          <a-row :gutter="0">
            <a-col :span="16">
              <a-form-item>
                <a-input
                  v-decorator="['inputCode',validatorRules.inputCode]"
                  size="large"
                  type="text"
                  @change="inputCodeChange"
                  placeholder="请输入验证码">
                  <a-icon slot="prefix" type="smile" :style="{ color: 'rgba(0,0,0,.25)' }"/>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col :span="8" style="text-align: right">
              <img v-if="requestCodeSuccess" style="margin-top: 2px;" :src="randCodeImage" @click="handleChangeCheckCode"/>
              <img v-else style="margin-top: 2px;" src="../../assets/checkcode.png" @click="handleChangeCheckCode"/>
            </a-col>
          </a-row>


        </a-tab-pane>
        <a-tab-pane key="tab2" tab="手机号登陆">
          <a-form-item>
            <a-input
              v-decorator="['mobile',validatorRules.mobile]"
              size="large"
              type="text"
              placeholder="手机号">
              <a-icon slot="prefix" type="mobile" :style="{ color: 'rgba(0,0,0,.25)' }"/>
            </a-input>
          </a-form-item>

          <a-row :gutter="16">
            <a-col class="gutter-row" :span="16">
              <a-form-item>
                <a-input
                  v-decorator="['captcha',validatorRules.captcha]"
                  size="large"
                  type="text"
                  placeholder="请输入验证码">
                  <a-icon slot="prefix" type="mail" :style="{ color: 'rgba(0,0,0,.25)' }"/>
                </a-input>
              </a-form-item>
            </a-col>
            <a-col class="gutter-row" :span="8">
              <a-button
                class="getCaptcha"
                tabindex="-1"
                :disabled="state.smsSendBtn"
                @click.stop.prevent="getCaptcha"
                v-text="!state.smsSendBtn && '获取验证码' || (state.time+' s')"></a-button>
            </a-col>
          </a-row>
        </a-tab-pane>
      </a-tabs>-->
      <a-form-item>
        <a-input
          ref="userName"
          size="large"
          v-decorator="['username',{initialValue:'', rules: validatorRules.username.rules}]"
          type="text"
          placeholder="请输入帐户名">
          <a-icon slot="prefix" type="user" :style="{ color: 'rgba(0,0,0,.25)' }"/>
        </a-input>
      </a-form-item>

      <a-form-item>
        <a-input
          v-decorator="['password',{initialValue:'', rules: validatorRules.password.rules}]"
          size="large"
          type="password"
          autocomplete="false"
          placeholder="密码">
          <a-icon slot="prefix" type="lock" :style="{ color: 'rgba(0,0,0,.25)' }"/>
        </a-input>
      </a-form-item>

      <a-row :gutter="0" v-if="NeedValid">
        <a-col :span="16">
          <a-form-item>
            <a-input
              v-decorator="['inputCode',validatorRules.inputCode]"
              size="large"
              type="text"
              @change="inputCodeChange"
              placeholder="请输入验证码">
              <a-icon slot="prefix" type="smile" :style="{ color: 'rgba(0,0,0,.25)' }"/>
            </a-input>
          </a-form-item>
        </a-col>
        <a-col :span="8" style="text-align: right">
          <img v-if="requestCodeSuccess" style="margin-top: 2px;" :src="randCodeImage" @click="handleChangeCheckCode"/>
          <img v-else style="margin-top: 2px;" src="../../assets/checkcode.png" @click="handleChangeCheckCode"/>
        </a-col>
      </a-row>

     <a-form-item>
        <a-checkbox v-decorator="['rememberMe', {initialValue: true, valuePropName: 'checked'}]" >记住密码</a-checkbox>
        <a-checkbox v-decorator="['autoLogin', {initialValue: true, valuePropName: 'checked'}]" style="float: right;">自动登陆</a-checkbox>
        <!-- <router-link :to="{ name: 'alteration'}" class="forge-password" style="float: right;">
          忘记密码
        </router-link>
        <router-link :to="{ name: 'register'}" class="forge-password" style="float: right;margin-right: 10px" >
          注册账户
        </router-link> -->
      </a-form-item>

      <div style="margin-top:24px">
        <a-row :gutter="10">
          <a-col :span="24">
            <a-button
              size="large"
              type="primary"
              htmlType="submit"
              class="login-button"
              :loading="loginBtn"
              @click.stop.prevent="handleSubmit"
              :disabled="loginBtn">{{sumitText}}
            </a-button>
          </a-col>
<!--          <a-col :span="12">
            <a-button @click="ssoLogin"  size="large" class="login-button"
                      :loading="loginBtn" :disabled="loginBtn">单点登录</a-button>
          </a-col>-->
        </a-row>
<!--        <a-row>
          <a-col :span="24">
            <a-button @click="ssoLogin" type="link"  size="large" class="login-button" :disabled="loginBtn">单点登录</a-button>
            &lt;!&ndash; 下面这个iframe 主要用于判断当前用户是否有sso登录过，登录过了直接进入系统 &ndash;&gt;
            <iframe id="ssologinframe" :src="ssoLoginUrl+'?auto=1'" style="display:none"></iframe>
          </a-col>
        </a-row>-->
      </div>

      <!--<div class="user-login-other">
        <span>其他登陆方式</span>
        <a @click="onThirdLogin('github')" title="github"><a-icon class="item-icon" type="github"></a-icon></a>
        <a @click="onThirdLogin('wechat_enterprise')" title="企业微信"><a-icon class="item-icon" type="wechat"></a-icon></a>
        <a @click="onThirdLogin('dingtalk')" title="钉钉"><a-icon class="item-icon" type="dingding"></a-icon></a>
      </div>-->
    </a-form>
    <two-step-captcha
      v-if="requiredTwoStepCaptcha"
      :visible="stepCaptchaVisible"
      @success="stepCaptchaSuccess"
      @cancel="stepCaptchaCancel"></two-step-captcha>

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

  export default {
    components: {
      TwoStepCaptcha
    },
    data () {
      return {
        sumitText: '确定',
        customActiveKey: 'tab1',
        loginBtn: false,
        // login type: 0 email, 1 username, 2 telephone
        loginType: 0,
        inputCodeNull: true,
        requiredTwoStepCaptcha: false,
        stepCaptchaVisible: false,
        form: this.$form.createForm(this),
        encryptedString: {
          key: '',
          iv: ''
        },
        state: {
          time: 60,
          smsSendBtn: false
        },
        validatorRules: {
          username: { rules: [{ required: true, message: '请输入用户名!' }, { validator: this.handleUsernameOrEmail }] },
          password: { rules: [{ required: true, message: '请输入密码!', validator: 'click' }] },
          mobile: { rules: [{ validator: this.validateMobile }] },
          inputCode: { rules: [{ required: true, message: '请输入验证码!' }] }
          /* captcha:{rule: [{ required: true, message: '请输入验证码!'}]},*/
        },
        verifiedCode: '',
        inputCodeContent: '',

        NeedValid: false,
        departList: [],
        departVisible: false,
        departSelected: '',
        currentUsername: '',
        validate_status: '',
        currdatetime: '',
        randCodeImage: '',
        requestCodeSuccess: false,
        ssoLoginUrl: window._CONFIG['domianURL'] + `/sso/login`
      }
    },
    created () {
      this.currdatetime = new Date().getTime();
      Vue.ls.remove(ACCESS_TOKEN)
      this.getRouterData();
      this.handleChangeCheckCode();
      Modal.destroyAll()
      // update-begin- --- author:scott ------ date:20190805 ---- for:密码加密逻辑暂时注释掉，有点问题
      //this.getEncrypte();
      // update-end- --- author:scott ------ date:20190805 ---- for:密码加密逻辑暂时注释掉，有点问题
    },
    mounted () {
      this.$refs.userName.focus();

      // 监听单点登录事件
      let that = this;
      let receiveMessage = function (event) {
        // let origin = event.origin
        let data = event.data
        if (typeof data === 'object') {
          return
        }
        if (typeof data === 'string') {
          try {
            let dd = JSON.parse(data)
            if (dd.type && dd.type === 'token') {
              console.log('单点登录成功，进行最后登录...')
              let token = dd.data
              that.loginBtn = true
              Vue.ls.set(LAST_REFRESH_TIME, new Date())
              that.ThirdLogin(token).then(res => {
                if (res.success) {
                  that.loginSuccess()
                } else {
                  that.loginBtn = false
                  that.requestFailed(res)
                }
              })
            }
          } catch (e) {
            console.error('单点登录异常：', e)
          }
        }
      }
      //window.addEventListener("message", receiveMessage, false);
    },
    methods: {
      ...mapActions([ "Login", "Logout","PhoneLogin","ThirdLogin" ]),
      ssoLogin () {
        let url = this.ssoLoginUrl // window._CONFIG['domianURL']+`/sso/login`
        let iWidth=850;                         //弹出窗口的宽度;
        let iHeight=500;                        //弹出窗口的高度;
        let iTop = (window.screen.height-30-iHeight)/2;       //获得窗口的垂直位置;
        let iLeft = (window.screen.width-10-iWidth)/2;        //获得窗口的水平位置;
        let result = window.open(url, `单点登录`, 'height=' + iHeight + ', width=' + iWidth + ', top=' + iTop + ', left=' + iLeft + ', toolbar=no, menubar=no, scrollbars=no, resizable=no,location=no, status=no')
        window.onfocus=function (){result.focus();};
        window.onclick=function (){result.focus();};
      },
      //第三方登录
      onThirdLogin(source){
        let url = window._CONFIG['domianURL']+`/thirdLogin/render/${source}`
        window.open(url, `login ${source}`, 'height=500, width=500, top=0, left=0, toolbar=no, menubar=no, scrollbars=no, resizable=no,location=n o, status=no')
        let that = this;
        let receiveMessage = function(event){
          let origin = event.origin

          let token = event.data
          that.ThirdLogin(token).then(res=>{
            if(res.success){
              that.loginSuccess()
            }else{
              that.requestFailed(res);
            }
          })
        }
        window.addEventListener("message", receiveMessage, false);
      },
      // handler
      handleUsernameOrEmail (rule, value, callback) {
        const regex = /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/;
        if (regex.test(value)) {
          this.loginType = 0
        } else {
          this.loginType = 1
        }
        callback()
      },
      handleTabClick (key) {
        this.customActiveKey = key
        // this.form.resetFields()
      },
      handleSubmit () {
        let that = this
        let loginParams = {};
        that.loginBtn = true;
        // 使用账户密码登陆
        if (that.customActiveKey === 'tab1') {
          const valids = ['username', 'password', 'rememberMe', 'autoLogin']
          if (this.NeedValid) {
            valids.push('inputCode')
          }
          that.form.validateFields(valids, { force: true }, (err, values) => {
            if (!err) {
              loginParams.username = values.username
              // update-begin- --- author:scott ------ date:20190805 ---- for:密码加密逻辑暂时注释掉，有点问题
              //loginParams.password = md5(values.password)
              //loginParams.password = encryption(values.password,that.encryptedString.key,that.encryptedString.iv)
              loginParams.password = values.password
              // loginParams.remember_me = values.rememberMe
              // loginParams.autoLogin = values.autoLogin
              // update-begin- --- author:scott ------ date:20190805 ---- for:密码加密逻辑暂时注释掉，有点问题
              loginParams.captcha = that.inputCodeContent
              loginParams.checkKey = that.currdatetime
              // console.log("登录参数",loginParams)
              that.Login(loginParams).then((res) => {
                if(res.success && res.code == 200){
                  const login_info = {
                    username: values.username,
                    password: values.rememberMe ? values.password : '',
                    rememberMe: values.rememberMe ? true : false,
                    autoLogin: values.autoLogin ? true : false,
                  }
                  Vue.ls.set('LOGIN_INFO', login_info);
                }
                this.departConfirm(res)
              }).catch((err) => {
                that.requestFailed(err);
              });


            }else {
              that.loginBtn = false;
            }
          })
          // 使用手机号登陆
        } else {
          that.form.validateFields([ 'mobile', 'captcha', 'rememberMe' ], { force: true }, (err, values) => {
            if (!err) {
              loginParams.mobile = values.mobile
              loginParams.captcha = values.captcha
              loginParams.remember_me = values.rememberMe
              that.PhoneLogin(loginParams).then((res) => {
                // console.log(res.result);
                this.departConfirm(res)
              }).catch((err) => {
                that.requestFailed(err);
              })

            }
          })
        }
      },
      getCaptcha (e) {
        e.preventDefault();
        let that = this;
        this.form.validateFields([ 'mobile' ], { force: true },(err,values) => {
            if(!values.mobile){
              that.cmsFailed("请输入手机号");
            }else if (!err) {
              this.state.smsSendBtn = true;
              let interval = window.setInterval(() => {
                if (that.state.time-- <= 0) {
                  that.state.time = 60;
                  that.state.smsSendBtn = false;
                  window.clearInterval(interval);
                }
              }, 1000);

              const hide = this.$message.loading('验证码发送中..', 0);
              let smsParams = {};
                  smsParams.mobile=values.mobile;
                  smsParams.smsmode="0";
              postAction("/sys/sms",smsParams)
                .then(res => {
                  if(!res.success){
                    setTimeout(hide, 0);
                    this.cmsFailed(res.message);
                  }
                  setTimeout(hide, 500);
                })
                .catch(err => {
                  setTimeout(hide, 1);
                  clearInterval(interval);
                  that.state.time = 60;
                  that.state.smsSendBtn = false;
                  this.requestFailed(err);
                });
            }
          }
        );
      },
      stepCaptchaSuccess () {
        this.loginSuccess()
      },
      stepCaptchaCancel () {
        this.Logout().then(() => {
          this.loginBtn = false
          this.stepCaptchaVisible = false
        })
      },
      handleChangeCheckCode(){
        this.currdatetime = new Date().getTime();
        getAction(`/sys/randomImage/${this.currdatetime}`).then(res=>{
          if(res.success){
            if(res.result === 'NoNeed') {
              this.NeedValid = false
            } else {
              this.randCodeImage = res.result
              this.requestCodeSuccess = true
              this.NeedValid = true
            }
          }else{
            this.$message.error(res.message)
            this.requestCodeSuccess=false
          }
        }).catch(()=>{
          this.requestCodeSuccess=false
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
        console.log('login success UserInfo:', this.$store.getters.userInfo)
        this.$notification.success({
          message: '欢迎',
          description: `${timeFix()}，欢迎回来`,
        });
      },
      cmsFailed(err){
        this.$notification[ 'error' ]({
          message: "登录失败",
          description:err,
          duration: 4,
        });
      },
      requestFailed (err) {
        this.$notification[ 'error' ]({
          message: '登录失败',
          description: ((err.response || {}).data || {}).message || err.message || "请求出现错误，请稍后再试",
          duration: 4,
        });
        this.loginBtn = false;
      },
      validateMobile(rule,value,callback){
        if (!value || new RegExp(/^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\d{8}$/).test(value)){
          callback();
        }else{
          callback("您的手机号码格式不正确!");
        }

      },
      validateInputCode(rule,value,callback){
        if(!value || this.verifiedCode==this.inputCodeContent){
          callback();
        }else{
          callback("您输入的验证码不正确!");
        }
      },
      generateCode(value){
        this.verifiedCode = value.toLowerCase()
      },
      inputCodeChange(e){
        this.inputCodeContent = e.target.value
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
            this.currentUsername=this.form.getFieldValue("username")
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
        const login_info = Vue.ls.get('LOGIN_INFO');
        console.log(login_info);
        if(login_info){
          this.form.setFieldsValue({
            ...login_info
          });
          // 自动登录
          if(login_info.autoLogin){
            this.$nextTick(()=>{
              this.sumitText = '自动登录中...'
              this.handleSubmit();
            })
          }
        }else{
          this.form.setFieldsValue({
            rememberMe: false,
            autoLogin: false
          });
        }
        // console.log(this.$route.params.username)
        // if (this.$route.params.username) {
        //   this.form.setFieldsValue({
        //     'username': this.$route.params.username
        //   });
        // }
      })
    },
    //获取密码加密规则
    getEncrypte(){
      var encryptedString = Vue.ls.get(ENCRYPTED_STRING);
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

  .user-layout-login {
    label {
      font-size: 14px;
    }

    .getCaptcha {
      display: block;
      width: 100%;
      height: 40px;
    }

    .forge-password {
      font-size: 14px;
    }

    button.login-button {
      padding: 0 15px;
      font-size: 16px;
      height: 40px;
      width: 100%;
      margin: 0 !important;
    }

    .user-login-other {
      text-align: left;
      margin-top: 24px;
      line-height: 22px;

      .item-icon {
        font-size: 24px;
        color: rgba(0,0,0,.2);
        margin-left: 16px;
        vertical-align: middle;
        cursor: pointer;
        transition: color .3s;

        &:hover {
          color: #1890ff;
        }
      }

      .register {
        float: right;
      }
    }
  }

</style>
<style>
  .valid-error .ant-select-selection__placeholder{
    color: #f5222d;
  }
</style>