<template>
  <a-modal
    :title="title"
    :width="'100%'"
    dialogClass="fullDialog"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :destroyOnClose="true"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row style="width: 100%">
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="规程序号">
              <a-input-number
                style="width: 100%"
                :min="1"
                :max="999999"
                placeholder="规程序号"
                v-decorator="['no', validatorRules.no]"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="名称">
              <a-input placeholder="规程名称" v-decorator="['title', validatorRules.title]" />
            </a-form-item>
          </a-col>
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="类型">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator="['type', validatorRules.type]"
                @change="handleChangeType"
                dictCode="bu_regu_type"
              />
            </a-form-item>
          </a-col>
          <a-col :span="24 / 2">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="上级">
              <!-- <a-tree-select
                 tree-data-simple-mode
                 allow-clear
                 :tree-data="selectTreeNode"
                 placeholder="请选择"
                 v-decorator="['parentId', validatorRules.parentId]"
                 :load-data="loadSelectNodeMethod"
               />-->
              <a-select
                placeholder="请选择"
                :open="false"
                :showArrow="true"
                v-decorator.trim="['parentName', validatorRules.parentName]"
                @focus="openModal"
                ref="mySelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="安全提示">
              <a-textarea placeholder="请输入内容" v-decorator="['safeNotice', validatorRules.safeNotice]" />
            </a-form-item>
          </a-col>
        </a-row>
        <template v-if="reguTypeId == '2'">
          <!-- <template> -->
          <item-form
            ref="itemForm"
            :method.sync="detail.method"
            :qualityLevel.sync="detail.qualityLevel"
            :important.sync="detail.important"
            :measure.sync="detail.measure"
            :mustReplace.sync="detail.mustReplace"
            :workTime.sync="detail.workTime"
            :outsource.sync="detail.outsource"
            :requirement.sync="detail.requirement"
            :reguMaterials.sync="detail.reguMaterials"
            :reguTools.sync="detail.reguTools"
            :reguPersons.sync="detail.reguPersons"
            :reguForms.sync="detail.reguForms"
            :assetTypeId.sync="detail.assetTypeId"
            :assetTypeName.sync="detail.assetTypeName"
            :techFiles.sync="detail.reguTechFiles"
            :techBookDetails.sync="detail.techBookDetails"
            :trainTypeId="trainTypeId"
            :regu-detail-id="detail.id"
          ></item-form>
        </template>

        <a-row>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="备注描述">
              <a-textarea placeholder="请输入内容" v-decorator="['remark', validatorRules.remark]" />
            </a-form-item>
          </a-col>
        </a-row>
        <!-- <a-row>
          <a-col :span="24">
            <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="工艺文件">
              <a-button type="primary">添加文件</a-button>
              <a-button type="primary" style="margin-left:10px;">上传至服务器</a-button>
            </a-form-item>
          </a-col>
        </a-row>-->

        <!-- <a-row>
           <a-col style="text-align: right;">
             <a-space>
               <a-button type="primary" @click="handleSave">保存</a-button>
               <a-button @click="handleBack">返回</a-button>
             </a-space>
           </a-col>
         </a-row>-->
      </a-form>

      <regulation-list
        ref="regulationModalForm"
        :regu-id="reguId"
        :train-type-id="trainTypeId"
        :only-project="true"
        @ok="addTarget"
      ></regulation-list>
    </a-spin>
  </a-modal>
</template>

<script>
import { getReguDeteil, saveReguDetail } from '@/api/tirosApi'
import { everythingIsEmpty } from '@/utils/util'
import RegulationList from '@views/tiros/common/selectModules/RegulationList'
import ItemForm from '@views/tiros/basic/modules/regulation/ItemForm'

export default {
  components: { ItemForm, RegulationList },
  name: 'ReguAddModal',
  props: ['reguId', 'trainTypeId'],
  data () {
    return {
      title: '操作',
      confirmLoading: false,
      visible: false,
      parent: {},
      detail: {},
      form: this.$form.createForm(this),
      itemFormData: {},
      reguTypeId: '',
      parentId: '',
      parentName: '',
      itemFormField: true,
      validatorRules: {
        no: { rules: [{ required: true, message: '请输入规程序号!' }] },
        title: {
          rules: [
            { required: true, message: '请输入规程名称!' },
            { max: 128, message: '输入长度不能超过32字符!' }
          ]
        },
        type: { rules: [{ required: true, message: '请选择类型!' }] },
        parentId: { rules: [] },
        safeNotice: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] }
      },
      selectTreeNode: [],
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 4 }
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 16 }
      },
      labelCol2: {
        xs: { span: 24 },
        sm: { span: 2 }
      },
      wrapperCol2: {
        xs: { span: 24 },
        sm: { span: 22 }
      }
    }
  },
  created () {
    this.findSelectList()
  },
  methods: {
    handleOk () {
      this.form.validateFields((err, values) => {
        if (!err) {
          if (this.reguTypeId == '2') {
            this.$refs.itemForm.handleCheckFiled()
            this.itemFormField = !this.$refs.itemForm.filedRight
          }
          if (this.itemFormField) {
            let p = Object.assign({}, this.detail, values, {
              reguId: this.reguId,
              parentId: this.parent.id || ''
            })
            this.confirmLoading = true
            saveReguDetail(p)
              .then((res) => {
                if (res.success) {
                  if (p.id) {
                    this.$emit('reloadTreeChilds', this.parent.id)
                  } else {
                    if (this.parent && this.parent.id) {
                      this.$emit('reloadTreeChilds', this.parent.id)
                    } else {
                      this.$emit('ok')
                    }
                  }
                  this.$message.success(res.message)
                  this.close()
                } else {
                  this.$message.error(res.message)
                }
              })
              .finally(() => {
                this.confirmLoading = false
              })
          }
        }
      })
    },
    openModal () {
      this.$refs.regulationModalForm.showModal()
      this.$refs.mySelect.blur()
    },
    addTarget (data) {
      if (this.model.id && data[0].id == this.model.id) {
        this.$message.warn('上级不能是自身!')
        return
      }
      this.form.setFieldsValue({
        parentName: data[0].title
      })
      this.parent.name = data[0].title
      this.parent.id = data[0].id
    },
    add (parent) {
      this.parent = parent
      this.edit({})
    },
    edit (record) {
      this.visible = true
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.reguTypeId = this.model.type
      if (!everythingIsEmpty(record)) {
        this.parent = { id: record.parentId, name: record.parentName }
        this.detail = record
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({
          no: this.model.no,
          title: this.model.title,
          type: this.model.type,
          parentName: this.parent.name || '',
          safeNotice: this.model.safeNotice,
          remark: this.model.remark
        })
      })
    },
    handleChangeType (value) {
      this.reguTypeId = value
    },
    findSelectList () {
      let param = {
        reguId: this.reguId,
        type: 1
      }
      getReguDeteil(param).then((res) => {
        if (res.success) {
          this.loading = false
          this.tableData = res.result
          this.selectTreeNode = res.result.map((item) => {
            return this.genSelectTreeNode(item)
          })
        }
      })
    },
    loadSelectNodeMethod (node) {
      let param = {
        reguId: this.reguId,
        parentId: node.dataRef.id,
        type: this.reguTypeId
      }
      return new Promise((resolve) => {
        getReguDeteil(param).then((res) => {
          if (res.success) {
            let childrenNode = res.result.map((item) => {
              this.selectTreeNode = this.selectTreeNode.concat(this.genSelectTreeNode(item))
            })
          } else {
            this.$message.warning(res.message)
          }
        })
        resolve()
      })
    },
    genSelectTreeNode (node) {
      return {
        id: node.id,
        pId: node.parentId,
        value: node.id,
        title: node.title,
        isLeaf: node.hasChildren ? false : true
      }
    },
    handleBack () {
      let value = {
        reguId: this.reguId,
        trainTypeId: this.trainTypeId
      }
      this.$router.push({
        name: 'tiros-basic-regulation',
        params: {
          value: value
        }
      })
      // this.$router.back();
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
      this.detail = {}
    }
  }
}
</script>

<style>
</style>