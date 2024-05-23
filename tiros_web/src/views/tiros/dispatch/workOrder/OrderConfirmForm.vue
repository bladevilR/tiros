<template>
<div>
  确认提交该工单？
  <a-form-model ref="checkForm" :model="form" :rules="rules" :label-col="labelCol" :wrapper-col="wrapperCol">
    <a-form-model-item label="专工检查？">
      <j-switch v-model="form.needCheck" :options="[1,0]"></j-switch>
    </a-form-model-item>
    <a-form-model-item label="专工选择" v-if="form.needCheck===1" prop="checkUserName">
      <a-select
        ref="userSelect"
        v-model="form.checkUserName"
        placeholder="请选择人员"
        :open="false"
        @focus="showUserSelectModal()"
      >
        <a-icon slot="suffixIcon" type="ellipsis" @click="showUserSelectModal()" />
      </a-select>
    </a-form-model-item>
  </a-form-model>
  <user-list ref="tUserModalForm" :multiple="false" @ok="onUserSelect"></user-list>
</div>
</template>

<script>
import JSwitch from '@comp/jeecg/JSwitch'
import UserList from '@views/tiros/common/selectModules/UserList'
export default {
  name: 'OrderConfirmForm',
  components: { JSwitch,UserList },
  props: ['forms'],
  data () {
    return {
      labelCol: { span: 4 },
      wrapperCol: { span: 14 },
      form: {
        needCheck: 0,
        checkUserId: '',
        checkUserName: ''
      },
      rules: {
        checkUserName: [
          { required: true, message: '请选择一个专业工程师进行作业记录表检查', trigger: 'blur' }
        ]
      }
    }
  },
  mounted () {
    if (this.forms && this.forms.length > 0) {
      let need= false
      this.forms.forEach(form => {
        if (form.instType === 3) {
          need = true
          return
        }
      })
      if (need) {
        this.form.needCheck = 1
      }
    }
  },
  methods: {
    showUserSelectModal () {
      this.$refs.tUserModalForm.showModal()
      this.$refs.userSelect.blur()
    },
    onUserSelect (users) {
      if (users.length) {
        let user = users[0]
        this.form.checkUserId = user.id
        this.form.checkUserName = user.realname
        this.$refs.checkForm.validateField('checkUserName')
        this.$forceUpdate()
      }
    },
    save () {
      this.$refs.checkForm.validate(valid => {
        if (valid) {
          this.$emit('ok', this.form)
        } else {
          this.$emit('fail')
        }
      })
    }
  }
}
</script>

<style scoped>

</style>