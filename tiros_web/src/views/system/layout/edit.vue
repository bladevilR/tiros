<template>
  <a-modal
    title="布局编辑"
    centered
    :width="800"
    :bodyStyle="{height: '400px'}"
    :visible="visible"
    @ok="handleOk"
    @cancel="visible=false"
    :destroyOnClose="true"
  >
    <a-form-model ref="form" :model="model" :label-col="{span:5}"  :wrapper-col="{span:17}" :rules="rules">
      <a-row>
        <a-col :span="24">
          <a-form-model-item label="布局编码" prop="layoutCode">
            <a-input v-model="model.layoutCode" :disabled="model.layoutCode === 'default-dashboard'" />
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item label="布局名称" prop="layoutName">
            <a-input v-model="model.layoutName" />
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item label="有效状态" prop="status">
            <j-dict-select-tag v-model="model.status" dictCode="bu_valid_status" />
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item label="是否主面板" prop="isMain">
            <a-switch v-model="isMain" @change="changeIsMain" />
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item label="应用范围" prop="layoutScope">
            <j-dict-select-tag
              :trigger-change="true"
              v-model="model.layoutScope"
              dictCode="bu_layout_scope"
              @change="changeScope"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item label="所属人员" prop="byUserId">
            <div @click="showUserModal()">
              <a-select ref="userSelect" v-model="model.byUserName" placeholder="请选择人员" :open="false">
                <a-icon slot="suffixIcon" type="ellipsis" @click="showUserModal()"/>
              </a-select>
            </div>
          </a-form-model-item>
        </a-col>
        <a-col :span="24">
          <a-form-model-item label="备注" >
            <a-textarea v-model="model.layoutDesc "  :auto-size="{ minRows: 3, maxRows: 5 }"/>
          </a-form-model-item>
        </a-col>

      </a-row>
    </a-form-model>
    <user-list ref="userSelectModal" :multiple="false" @ok="onUserSelect"></user-list>
  </a-modal>
</template>

<script>
import UserList from '@views/tiros/common/selectModules/UserList'
import {saveLayout} from '@api/tirosLayoutApi'
import { duplicateCheck } from '@api/api'

export default {
  name: 'edit',
  components: { UserList },
  data () {
    return {
      visible: false,
      isMain: false,
      model: {  },
      rules: {
        layoutCode: [
          { required: true, message: '请输入布局编码', trigger: 'blur' },
          { validator: this.validateCode, trigger: 'change' },
          { min: 3, max: 25, message: '编码长度为3-25个字符', trigger: 'blur' }
        ],
        layoutName: [
          { required: true, message: '请输入布局名称', trigger: 'blur' },
          { min: 2, max: 16, message: '名称长度为2-16个字符', trigger: 'blur' }
        ],
        layoutScope: [
          { required: true, message: '请选择应用范围', trigger: 'blur' }
        ],
        byUserId: [
          { required: false, message: '请选择所属用户', trigger: 'blur' }
        ]
      }
    }
  },
  methods: {
    showModal (record) {
      if (record) {
        this.model = Object.assign({}, record)
      } else {
        this.model = {
          'byUserId': '',
          'byUserName': '',
          'id': '',
          'isMain': 0,
          'layoutCode': '',
          'layoutDesc': '',
          'layoutName': '',
          'layoutScope': 0,
          'status': 1,
          'layoutWidgetsList': []
        }
      }
      this.visible = true
    },
    handleOk () {
      this.$refs.form.validate(valid => {
        if (valid) {
          saveLayout(this.model).then(res => {
            if (res.success) {
              this.$message.success('保存成功')
              this.$emit('ok')
              this.visible = false
            } else {
              this.$message.error('保存失败')
              console.error('保存布局信息失败：', res.message)
            }
          })
        }
      })
    },
    changeIsMain () {
      if (this.isMain) {
        this.model.isMain = 1
      } else {
        this.model.isMain = 0
      }
    },
    showUserModal () {
      this.$refs.userSelectModal.showModal()
    },
    onUserSelect (data) {
      if (data.length) {
        this.model.byUserId = data[0].id
        this.model.byUserName = data[0].realname
      } else {
        this.model.byUserId = null
        this.model.byUserName = null
      }
    },
    changeScope (value) {
      if (value === 0) {
        this.rules.byUserId[0].required = false
      } else {
        this.rules.byUserId[0].required = true
      }
    },
    validateCode(rule, value, callback){
      let params = {
        tableName: 'sys_layouts',
        fieldName: 'layout_code',
        fieldVal: value,
        dataId: this.model.id,
        filterFields: [
          {
            name: 'layout_scope',
            val: this.model.layoutScope
          }
        ]
      }
      duplicateCheck(params).then((res)=>{
        if(res.success){
          callback();
        }else{
          callback(new Error('布局编码重复'));
        }
      })
    },
  }
}
</script>

<style scoped>

</style>