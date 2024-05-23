<template>
  <div id="addAlertContent">
    <div class="info-wrapper info-top-wrapper">
      <h4>接收设置</h4>
      <a-form :form="form">
        <a-row>
          <a-col :span="11">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="接收维度">
              <j-dict-select-tag
                dictCode="bu_dimension"
                :triggerChange="true"
                v-decorator="['dimension', {}]"
                placeholder="请选择接收维度"
                @change="changeDimension"
              />
            </a-form-item>
          </a-col>
          <a-col :span="11">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="接收对象">
              <a-select
                mode="multiple"
                placeholder="请选择接收对象"
                :open="false"
                :showArrow="true"
                v-model="target"
                @focus="openModal"
                @deselect="changeSelect"
                ref="mySelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :span="2">
            <a-form-item>
              <a-button @click="addAlertAccept">添加</a-button>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <user-list ref="UserModalForm" :multiple="true" @ok="addTarget"></user-list>
    <role-list ref="RoleModalForm" :multiple="true" @ok="addTarget"></role-list>
    <org-list ref="OrgModalForm" :multiple="true" @ok="addTarget"></org-list>
  </div>
</template>

<script>
import UserList from '../../../common/selectModules/UserList'
import RoleList from '../../../common/selectModules/RoleList'
import OrgList from '../../../common/selectModules/OrgList'
import { addAlertAccept } from '@/api/tirosApi'
export default {
  name: 'AddAlertAccept',
  props: ['value', 'toggle'],
  components: { UserList, RoleList, OrgList },
  data() {
    return {
      falg: false,
      queryParam: {
        alertType: 0,
        dimension: '',
        target: '',
      },
      target: [],
      targetItem: [],
      dimension: '',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      form: this.$form.createForm(this),
      showSelect: true,
      confirmLoading: false,
      visible: false,
    }
  },
  watch: {
    value: {
      immediate: true,
      handler(id) {
        this.queryParam.alertType = id
      },
    },
  },
  methods: {
    openModal() {
      if (this.queryParam.dimension) {
        switch (this.queryParam.dimension) {
          case '1':
            this.$refs.UserModalForm.showModal()
            break
          case '2':
            this.$refs.RoleModalForm.showModal()
            break
          case '3':
            this.$refs.OrgModalForm.showModal()
            break
          default:
            //这里是没有找到对应的值处理
            break
        }
      } else {
        this.$message.error('请先选择接收维度!')
      }
      this.$refs.mySelect.blur()
    },
    addTarget(data) {
      this.target = []
      this.targetItem = []
      this.queryParam.target = ''
      data.map((item, index) => {
        // this.target.push(item.realname)
        switch (this.queryParam.dimension) {
          case '1':
            this.target.push(item.realname)
            break
          case '2':
            this.target.push(item.roleName)
            break
          case '3':
            this.target.push(item.departName)
            break
          default:
            break
        }
        this.targetItem.push(item)
      })
    },
    changeSelect(value, option) {
      let index
      switch (this.queryParam.dimension) {
        case '1':
          index = this.targetItem.findIndex((item) => item.username === value)
          break
        case '2':
          index = this.targetItem.findIndex((item) => item.roleName === value)
          break
        case '3':
          index = this.targetItem.findIndex((item) => item.departName === value)
          break
        default:
          break
      }
      this.targetItem.splice(index, 1)
    },
    addAlertAccept() {
      console.log(this.queryParam.dimension, this.targetItem)
      if (this.queryParam.dimension && this.targetItem.length) {
        this.getTargetIds(this.targetItem)
        addAlertAccept(this.queryParam).then((res) => {
          if (res.success) {
            this.$message.success(res.message)
            this.$emit('changeToggle', !this.toggle)
            this.queryParam.target = ''
            this.targetItem = []
            this.target = []
          } else {
            this.$message.error(res.message)
          }
        })
      } else if (this.queryParam.dimension && !this.targetItem.length) {
        this.$message.error('请选择接收对象！')
      } else {
        this.$message.error('请选择接收维度和接收对象！')
      }
    },
    getTargetIds(data) {
      data.map((item, index) => {
        if (index == 0) {
          this.queryParam.target = this.queryParam.target + item.id
        } else {
          this.queryParam.target = this.queryParam.target + ',' + item.id
        }
      })
    },
    changeDimension(data) {
      if (data != this.queryParam.dimension) {
        this.queryParam.dimension = data
        this.queryParam.target = ''
        this.target = []
      }
    },
  },
}
</script>

<style lang="less">
#addAlertContent {
  .info-wrapper {
    border: 1px solid #eee;
    position: relative;
    border-radius: 8px;
    padding: 10px;
    margin-bottom: 20px;
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
  .ant-form-item {
    margin: 0;
  }
}
</style>