<template>
  <a-modal
    title='设置权限'
    :width="'85%'"
    :visible="visible"
    dialogClass="fullDialog no-footer"
    @cancel="handleCancel"
    :destroyOnClose="true"
  >
  <div id="PrivilegeContent">
    <div class="info-wrapper info-top-wrapper">
      <h4>可选权限</h4>
      <a-form :form="form">
        <a-row>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="权限维度">
              <j-dict-select-tag
                dictCode="bu_dimension"
                :triggerChange="true"
                v-decorator="['dimension',  { rules: [{ required: true, message: '请选择权限维度' }] },]"
                placeholder="请选择接收维度"
                @change="changeDimension"
              />
            </a-form-item>
          </a-col>
          <a-col :md="12" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="权限对象">
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
                <a-icon slot="suffixIcon" type="ellipsis"/>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="14" :sm="24">
            <a-form-item :labelCol="{ xs: { span: 24 } ,sm: { span: 2 }, }" :wrapperCol="wrapperCol" label="权限">
              <a-checkbox-group
                v-model="privilegeValue"
                :options="plainOptions"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="12">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="设置子项">
              <a-switch v-model="isSub"/>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <a-button @click="handleOk">添加权限</a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div>
      如果不设置任何权限项，默认为所有人拥有所有权限
    </div>
    <div class="info-wrapper info-top-wrapper">
      <h4>已设置权限</h4>
      <vxe-table
        stripe
        border="inner"
        align="center"
        :data="tableData">
        <vxe-table-column type="seq" width="60" title="序号"></vxe-table-column>
        <vxe-table-column field="objName" title="权限对象"></vxe-table-column>
        <vxe-table-column title="上传文件">
          <template v-slot="{ row }">
            <a-checkbox :checked="row.docPrivileges.slice(0,1)==='1'"/>
          </template>
        </vxe-table-column>
        <vxe-table-column title="查看文件">
          <template v-slot="{ row }">
            <a-checkbox :checked="row.docPrivileges.slice(1,2)==='1'"/>
          </template>
        </vxe-table-column>
        <vxe-table-column title="下载文件">
          <template v-slot="{ row }">
            <a-checkbox :checked="row.docPrivileges.slice(2,3)==='1'"/>
          </template>
        </vxe-table-column>
        <vxe-table-column title="删除文件">
          <template v-slot="{ row }">
            <a-checkbox :checked="row.docPrivileges.slice(3,4)==='1'"/>
          </template>
        </vxe-table-column>
        <vxe-table-column title="目录管理">
          <template v-slot="{ row }">
            <a-checkbox :checked="row.docPrivileges.slice(4,5)==='1'"/>
          </template>
        </vxe-table-column>
        <vxe-table-column title="操作">
          <template v-slot="{ row }">
            <a @click.stop="handleDel(row)">删除</a>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <user-list ref="UserModalForm" :multiple="true" @ok="addTarget"></user-list>
    <role-list ref="RoleModalForm" :multiple="true" @ok="addTarget"></role-list>
    <org-list ref="OrgModalForm" :multiple="true" @ok="addTarget"></org-list>

  </div>
  </a-modal>
</template>

<script>
  import UserList from '../../common/selectModules/UserList'
  import RoleList from '../../common/selectModules/RoleList'
  import OrgList from '../../common/selectModules/OrgList'
  import { delPrivilege, getPrivilege, settingPrivilege } from '@api/tirosApi'

  export default {
    name: 'PrivilegeAdd',
    components: { UserList, RoleList, OrgList },
    data() {
      return {
        plainOptions: [
          { label: '上传文件', value: '1' },
          { label: '查看文件', value: '2' },
          { label: '下载文件', value: '3' },
          { label: '删除文件', value: '4' },
          { label: '目录管理', value: '5' }
        ],
        tableData: [],
        queryParam: {
          dimension: '',
          target: ''
        },
        privilegeValue: [],
        target: [],
        targetItem: [],
        dimension: '',
        labelCol: {
          xs: { span: 24 },
          sm: { span: 5 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 16 }
        },
        form: this.$form.createForm(this),
        showSelect: true,
        confirmLoading: false,
        visible: false,
        directoryId: '',
        objType: '',
        isSub: true
      }
    },
   /* created() {
      if (this.$route.query.directoryId) {
        this.directoryId = this.$route.query.directoryId
      }
      if (this.$route.query.objType) {
        this.objType = this.$route.query.objType
      }
      this.findList()
    },*/
    methods: {
      show(data){
        this.visible=true
        this.directoryId=data.directoryId
        this.objType=data.objType
        this.findList()
      },
      findList() {
        getPrivilege(this.directoryId).then((res) => {
          if (res.success) {
            this.tableData = res.result
          }
        })
      },
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
          switch (this.queryParam.dimension) {
            case '1':
              this.target.push(item.username)
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
      getTargetIds(data) {
        data.map((item, index) => {
          if (index === 0) {
            this.queryParam.target = this.queryParam.target + item.id
          } else {
            this.queryParam.target = this.queryParam.target + ',' + item.id
          }
        })
      },
      changeDimension(data) {
        if (data !==this.queryParam.dimension) {
          this.queryParam.dimension = data
          this.queryParam.target = ''
          this.target = []
        }
      },
      handleOk() {
        if (this.queryParam.dimension && this.targetItem.length) {
          this.getTargetIds(this.targetItem)
          if(this.privilegeValue.length==0){
            this.$message.warn("至少选择一个权限!")
            return
          }
          this.form.validateFields((err, values) => {
            if (!err) {
              let formData = Object.assign({}, values, {
                objId: this.directoryId,
                docPrivileges: this.privilegeValue.join(','),
                targetId: this.queryParam.target,
                targetType: this.queryParam.dimension,
                objType: this.objType,
                isSub: this.isSub
              })
              debugger
              settingPrivilege(formData).then((res) => {
                if (res.success) {
                  this.findList()
                  this.$message.success(res.message)
                  this.queryParam = {
                    dimension: '',
                    target: ''
                  }
                  this.form.resetFields()
                  this.privilegeValue = []
                  this.target = []
                  this.targetItem = []
                  this.dimension = ''
                } else {
                  this.$message.error(res.message)
                }
              })
            }
          })
        } else if (this.queryParam.dimension && !this.targetItem.length) {
          this.$message.error('请选择接收对象！')
        } else {
          this.$message.error('请选择接收维度和接收对象！')
        }
      },
      handleDel(row) {
        delPrivilege(row.id).then((res) => {
          if (res.success) {
            this.findList()
            this.$message.success(res.message)
          } else {
            this.$message.error(res.message)
          }
        })
      },
      handleCancel () {
        this.close()
      },
      close () {
        this.$emit('close')
        this.visible = false
      },
    }
  }
</script>

<style scoped lang="less">
  #PrivilegeContent {
    .info-wrapper {
      border: 1px solid #eee;
      position: relative;
      border-radius: 8px;
      padding: 10px;
      margin-bottom: 20px;
      margin-top: 20px;
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