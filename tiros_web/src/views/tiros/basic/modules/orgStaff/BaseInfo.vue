<template>
  <a-form-model ref="form" :model="userBaseInfo" :rules="formRules">
    <div>
      <div class="info-wrapper info-bottom-wrapper">
        <h4>用户基本信息</h4>
          <a-row :gutter="12">
            <a-col :span="12">
              <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="姓名" prop="realname">
                <a-input v-model="userBaseInfo.realname"  :disabled="isDisable"/>
              </a-form-model-item>
            </a-col>
            <a-col :span="12">
              <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工号" prop="workNo">
                <a-input v-model="userBaseInfo.workNo" :disabled="isDisable"/>
              </a-form-model-item>
            </a-col>
            <a-col :span="12">
              <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="班组"  prop="groupId">
                <j-dict-select-tag
                  :trigger-change="true"
                  v-model="userBaseInfo.groupId"
                  placeholder="请选择"
                  dictCode="sys_depart,depart_name,id,org_category=4" :disabled="isDisable">
                </j-dict-select-tag>
              </a-form-model-item>
            </a-col>
            <a-col :span="12">
              <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="岗位" prop="positionId">
                <j-dict-select-tag
                  :trigger-change="true"
                  v-model="userBaseInfo.positionId"
                  placeholder="请选择"
                  dictCode="bu_postion_info,position_name,id" :disabled="isDisable">
                </j-dict-select-tag>
              </a-form-model-item>
            </a-col>
            <a-col :span="12">
              <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="性别" prop="sex">
                <a-radio-group :options="sexOptions" :value="sexOptionsValue" @change="sexOnChange" :disabled="isDisable"/>
              </a-form-model-item>
            </a-col>
            <a-col :span="12">
              <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态" prop="status">
                <a-radio-group :options="statusOptions" :value="statusOptionsValue" @change="statusOnChange" :disabled="isDisable"/>
              </a-form-model-item>
            </a-col>
            <!--          <a-col :span="12">-->
            <!--            <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="工资">-->
            <!--              <a-input v-decorator="['positionWages',validatorRules.positionWages]"/>-->
            <!--            </a-form-model-item>-->
            <!--          </a-col>-->
          </a-row>
      </div>
      <div class="info-wrapper info-bottom-wrapper">
        <h4>其他信息</h4>
          <a-row>
            <a-col :span="10">
              <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="账号" prop="username">
                <a-input v-model="userBaseInfo.username"/>
              </a-form-model-item>
            </a-col>
            <a-col :span="10">
              <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol">
                <a-button>创建账号</a-button>
              </a-form-model-item>
            </a-col>
            <a-col :span="10">
              <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="手机" prop="phone">
                <a-input v-model="userBaseInfo.phone"/>
              </a-form-model-item>
            </a-col>
            <a-col :span="10">
              <a-form-model-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="邮件" prop="email">
                <a-input v-model="userBaseInfo.email"/>
              </a-form-model-item>
            </a-col>
            <!--            <a-col :span="20">
                          <a-form-model-item :labelCol="{span:2}" :wrapperCol="{span: 18}" label="地址">
                            <a-input v-model="userBaseInfo.address"/>
                          </a-form-model-item>
                        </a-col>-->
            <a-col :span="20">
              <a-form-model-item :labelCol="{span:2}" :wrapperCol="{span: 18}" label="照片">
                <a-upload
                  name="avatar"
                  list-type="picture-card"
                  class="avatar-uploader"
                  :show-upload-list="false"
                  action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
                  :before-upload="beforeUpload"
                  @change="handleChange"
                >
                  <img v-model="userBaseInfo.avatar" v-if="imageUrl" :src="imageUrl" alt="avatar"/>
                  <div v-else>
                    <a-icon :type="loading ? 'loading' : 'plus'"/>
                    <div class="ant-upload-text">Upload</div>
                  </div>
                </a-upload>
              </a-form-model-item>
            </a-col>
          </a-row>
      </div>
    </div>
  </a-form-model>
</template>

<script>

import { everythingIsEmpty, randomUUID } from '@/utils/util'
import { duplicateCheck } from '@api/api'
import { isEmail, isMobile, isPhone } from '@/utils/validate'

export default {
  name: 'BaseInfo',
  props: {
    currentUser: {
      type: Object,
      default: {}
    },
    isDisable:{
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      userBaseInfo: {
        id: '',
        username: '',
        realname: '',
        workNo: '',
        avatar: '',
        orgCode: '',
        groupId: '',
        groupName: '',
        positionId: '',
        positionName: '',
        positionWages: 0,
        sex: 1,
        sex_dictText: '',
        phone: '',
        email: '',
        address: '',
        status: 1,
        status_dictText: '',
        tags: '',
        certs: '',
        tagTitleList: [],
        certList: [],
        trainingList: [],
        skillList: []
      },
      formRules: {
        realname: [{ required: true, message: '请输入姓名!',trigger: 'change'},{ max: 32, message: '输入长度不能超过32字符!' }],
        workNo: [{ required: true, message: '请输入工号!',trigger: 'change'},{ validator: this.validateCode, trigger: 'change'},{ max: 64, message: '输入长度不能超过64字符!' }],
        groupId: [{ required: true, message: '请输入班组!' ,trigger: 'change'}],
        positionId: [{ required: true, message: '请输入岗位!' ,trigger: 'change'}],
        sex: [{ required: true, message: '请输入性别!' ,trigger: 'change'}],
        status: [{ required: true, message: '请输入状态!',trigger: 'change' }],
        username: [{ required: true, message: '请输入账号!' ,trigger: 'change'},{ max: 32, message: '输入长度不能超过32字符!' }],
        phone: [{ validator: this.checkPhone, trigger: 'change'}],
        email: [{ validator: this.checkEmail, trigger: 'change'}],
        address: [{ required: true, message: '请输入地址!',trigger: 'change' }],
        avatar: [{ required: true, message: '请输入地址!' }]
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 4 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 12 }
      },
      buttonItemLayout: {},
      sexOptions: [
        { label: '男', value: '1' },
        { label: '女', value: '2' }
      ],
      statusOptions: [
        { label: '正常', value: '1' },
        { label: '冻结', value: '2' }
      ],
      imageUrl: '',
      loading: false,
      sexOptionsValue: '1',
      statusOptionsValue: '1',
      form: this.$form.createForm(this)
    }
  },
  mounted() {
    if (this.currentUser) {
      Object.assign(this.userBaseInfo, this.currentUser)
      // 处理数据转化
      if (this.userBaseInfo.sex === 2) {
        this.sexOptionsValue = '2'
      }
      if (this.userBaseInfo.status === 2) {
        this.statusOptionsValue = '2'
      }
    }
  },
  created() {
  },
  methods: {
    validateCode(rule, value, callback) {
      let params = {
        tableName: 'sys_user',
        fieldName: 'work_no',
        fieldVal: value,
        dataId: this.userBaseInfo.id,
      }
      if(!everythingIsEmpty(value)) {
        duplicateCheck(params).then((res) => {
          if (res.success) {
            callback()
          } else {
            callback(res.message)
          }
        })
      }else {
        callback()
      }
    },
    checkPhone (rule, value, callback) {
      if (!everythingIsEmpty(value)) {
        if (!(isMobile(value) || isPhone(value))) {
          callback('请输入正确的号码！')
        } else {
          callback()
        }
      } else {
        callback()
      }

    },
    checkEmail (rule, value, callback) {
      if (!everythingIsEmpty(value)) {
        if (!isEmail(value)) {
          callback('请输入正确的邮箱！')
        } else {
          callback()
        }
      } else {
        callback()
      }
    },
    sexOnChange() {
      if (this.sexOptionsValue === '1') {
        this.sexOptionsValue = '2'
      } else if (this.sexOptionsValue === '2') {
        this.sexOptionsValue = '1'
      }
    },
    statusOnChange() {
      if (this.statusOptionsValue === '1') {
        this.statusOptionsValue = '2'
      } else if (this.statusOptionsValue === '2') {
        this.statusOptionsValue = '1'
      }
    },
    handleChange(info) {
      if (info.file.status === 'uploading') {
        this.loading = true
        return
      }
      if (info.file.status === 'done') {
        // Get this url from response in real world.
        // getBase64(info.file.originFileObj, imageUrl => {
        //   this.imageUrl = imageUrl;
        this.loading = false
        // });
      }
    },
    beforeUpload(file) {
      const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
      if (!isJpgOrPng) {
        this.$message.error('You can only upload JPG file!')
      }
      const isLt2M = file.size / 1024 / 1024 < 2
      if (!isLt2M) {
        this.$message.error('Image must smaller than 2MB!')
      }
      return isJpgOrPng && isLt2M
    },
    getData() {
      // 处理数据转化
      if (this.sexOptionsValue === '1') {
        this.userBaseInfo.sex = 1
      } else if (this.sexOptionsValue === '2') {
        this.userBaseInfo.sex = 2
      } else {
        this.userBaseInfo.sex = 0
      }

      if (this.statusOptionsValue === '1') {
        this.userBaseInfo.status = 1
      } else {
        this.userBaseInfo.status = 2
      }
       return mini.clone(this.userBaseInfo)
    }
  }
}
</script>

<style>
.info-wrapper {
  border: 1px solid #eee;
  position: relative;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 15px;
}

.info-wrapper h4 {
  position: absolute;
  top: -14px;
  padding: 1px 8px;
  margin-left: 16px;
  color: #777;
  border-radius: 2px 2px 0 0;
  background: #fff;
  font-size: 14px;
  width: auto;
}
</style>