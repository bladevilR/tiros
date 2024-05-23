<template>
  <a-modal
    :title='title'
    width='50%'
    :visible='visible'
    :confirmLoading='confirmLoading'
    @ok='handleOk'
    @cancel='handleCancel'
    cancelText='关闭'
    :destroyOnClose='true'
  >
    <a-spin :spinning='confirmLoading'>
      <!--      <a-form :form='form' id='repairAttrForm'>-->
      <!--        <a-row :gutter='24'>-->

      <!--          <a-col :md='12' :sm='24'>-->
      <!--            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='类型'>-->
      <!--              <j-dict-select-tag-->
      <!--                :triggerChange='true'-->
      <!--                v-decorator.trim="['attributeType', validatorRules.attributeType]"-->
      <!--                placeholder='请选择'-->
      <!--                dictCode='repair_attribute_type'-->
      <!--                @change='handleChangeAttributeType'-->
      <!--              />-->
      <!--            </a-form-item>-->
      <!--          </a-col>-->

      <!--          <a-col :md='12' :sm='24' v-if="attributeType === '2' || attributeType === '3' || attributeType === '4'">-->
      <!--            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='所属上级'>-->
      <!--              <j-dict-select-tag-->
      <!--                :triggerChange='true'-->
      <!--                v-decorator.trim="['parentId', validatorRules.parentId]"-->
      <!--                placeholder='请选择'-->
      <!--                :dictCode='parentDictStr'-->
      <!--              />-->
      <!--            </a-form-item>-->
      <!--          </a-col>-->

      <!--          <a-col :md='12' :sm='24'>-->
      <!--            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='编码'>-->
      <!--              <a-input :maxLength='33' v-decorator="['attributeCode', validatorRules.attributeCode]" placeholder='请输入编码'></a-input>-->
      <!--            </a-form-item>-->
      <!--          </a-col>-->

      <!--          <a-col :md='12' :sm='24'>-->
      <!--            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='名称'>-->
      <!--              <a-input :maxLength='33' v-decorator="['attributeName', validatorRules.attributeName]" placeholder='请输入名称'></a-input>-->
      <!--            </a-form-item>-->
      <!--          </a-col>-->

      <!--          <a-col :md='12' :sm='24' v-if="attributeType === '3' || attributeType === '4'">-->
      <!--            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='关联修程'>-->
      <!--              <j-dict-select-tag-->
      <!--                trigger-change-->
      <!--                v-decorator.trim="['programId', validatorRules.programId]"-->
      <!--                placeholder='请选择'-->
      <!--                dictCode='bu_repair_program,name,id'-->
      <!--              />-->
      <!--            </a-form-item>-->
      <!--          </a-col>-->

      <!--          <a-col :md='12' :sm='24'>-->
      <!--            <a-form-item :labelCol='labelCol' :wrapperCol='wrapperCol' label='备注'>-->
      <!--              <a-textarea :maxLength='201' v-decorator="['remark', validatorRules.remark]"></a-textarea>-->
      <!--            </a-form-item>-->
      <!--          </a-col>-->

      <!--        </a-row>-->
      <!--      </a-form>-->

      <a-form-model ref='form' id='repairAttrForm' :model='bizData' :rules='rules' :label-col='{ span: 6 }' :wrapper-col='{ span: 18 }'>
        <a-row :gutter='24'>

          <a-col :md='12' :sm='24'>
            <a-form-model-item label='类型' prop='attributeType'>
              <j-dict-select-tag
                :triggerChange='true'
                placeholder='请选择'
                v-model='bizData.attributeType'
                dict-code='repair_attribute_type'
                @change='handleChangeAttributeType'
              ></j-dict-select-tag>
            </a-form-model-item>
          </a-col>

          <a-col :md='12' :sm='24' v-show="bizData.attributeType === '2' || bizData.attributeType === '3' || bizData.attributeType === '4'">
            <a-form-model-item label='所属上级' prop='parentId'>
              <j-dict-select-tag
                :triggerChange='true'
                placeholder='请选择'
                v-model='bizData.parentId'
                :dictCode='parentDictStr'
                @change='handleChangeParent'
              />
            </a-form-model-item>
          </a-col>

          <a-col :md='12' :sm='24'>
            <a-form-model-item label='编码' prop='attributeCode'>
              <a-input v-model='bizData.attributeCode' placeholder='请输入编码'></a-input>
            </a-form-model-item>
          </a-col>

          <a-col :md='12' :sm='24'>
            <a-form-model-item label='名称' prop='attributeName'>
              <a-input v-model='bizData.attributeName' placeholder='请输入名称'></a-input>
            </a-form-model-item>
          </a-col>

          <a-col :md='12' :sm='24' v-show="bizData.attributeType === '3' || bizData.attributeType === '4'">
            <a-form-model-item label='关联修程' prop='programId'>
              <j-dict-select-tag
                :triggerChange='true'
                placeholder='请选择'
                v-model='bizData.programId'
                dictCode='bu_repair_program,name,id'
                @change='handleChangeProgram'
              />
              <!--              <j-search-select-tag-->
              <!--                :triggerChange='true'-->
              <!--                placeholder='请选择'-->
              <!--                v-model='bizData.programId'-->
              <!--                dictCode='bu_repair_program,name,id'-->
              <!--                @selectCustom='selectCustomProgramName'-->
              <!--              />-->
            </a-form-model-item>
          </a-col>

          <a-col :md='12' :sm='24'>
            <a-form-model-item label='备注' prop='remark'>
              <a-textarea v-model='bizData.remark' placeholder='请输入备注' :autoSize='{ minRows: 1, maxRows: 3 }'></a-textarea>
            </a-form-model-item>
          </a-col>

        </a-row>
      </a-form-model>

    </a-spin>
  </a-modal>
</template>

<script>
import { saveRepairAttribute } from '@/api/tirosApi'

export default {
  name: 'RepairAttrModal',
  components: {},
  data() {
    return {
      title: '新增',
      visible: false,
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
      },
      validatorRules: {
        attributeType: { rules: [{ required: true, message: '请选择类型!' }] },
        attributeCode: { rules: [{ required: true, message: '请输入编码!' }, { max: 32, message: '不能超过32个字符' }] },
        attributeName: { rules: [{ required: true, message: '请输入名称!' }, { max: 32, message: '不能超过32个字符' }] },
        parentId: { rules: [{ required: false, message: '请选择上级!' }] }
      },
      rules: {
        attributeType: [{ required: true, message: '请选择类型!' }],
        attributeCode: [{ required: true, message: '请输入编码!' }, { max: 32, message: '不能超过32个字符' }],
        attributeName: [{ required: true, message: '请输入名称!' }, { max: 32, message: '不能超过32个字符' }],
        parentId: [{ required: false, message: '请选择上级!' }]
      },
      confirmLoading: false,
      // form: this.$form.createForm(this),
      bizData: {
        id: null,
        // 1工作类型、2大类、3检修等级、4检修周期
        attributeType: null,
        attributeCode: null,
        attributeName: null,
        parentId: null,
        parentType: null,
        parentName: null,
        remark: null,
        programId: null,
        programName: null,
        programNames: null
      },
      bizDataRel: {},
      parentDictStr: 'bu_repair_attribute,attribute_name,id'
    }
  },
  created() {
  },
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      console.log('RepairAttrModal edit(record) record ', record)

      // this.form = this.$form.createForm(this)
      this.bizData = {}
      if (record.id) {
        record = Object.assign(this.bizData, record)
      }

      if (record.attrProgRelList && record.attrProgRelList.length > 0) {
        this.bizDataRel = record.attrProgRelList[0]
        this.bizData.programId = this.bizDataRel.programId
        this.bizData.programName = this.bizDataRel.programName
      }
      // this.$nextTick(() => {
      //   this.form.setFieldsValue({
      //     attributeType: this.bizData.attributeType,
      //     attributeCode: this.bizData.attributeCode,
      //     attributeName: this.bizData.attributeName,
      //     parentId: this.bizData.parentId,
      //     parentName: this.bizData.parentName,
      //     programId: this.bizDataRel.programId,
      //     programName: this.bizDataRel.programName,
      //     remark: this.bizData.remark
      //   })
      // })
      this.visible = true
    },
    handleChangeAttributeType(value) {
      this.bizData.attributeType = value
      if (this.bizData.attributeType === '2') {
        this.parentDictStr = 'bu_repair_attribute,attribute_name,id,attribute_type=1'
      } else if (this.bizData.attributeType === '3') {
        this.parentDictStr = 'bu_repair_attribute,attribute_name,id,attribute_type=2'
      } else if (this.bizData.attributeType === '4') {
        this.parentDictStr = 'bu_repair_attribute,attribute_name,id,attribute_type=2'
      }
    },
    handleChangeParent(value) {
      this.bizData.parentId = value
      this.$forceUpdate()
    },
    handleChangeProgram(value) {
      this.bizData.programId = value
      this.$forceUpdate()
    },
    // 确定
    handleOk() {
      this.$refs.form.validate((valid) => {
        if (valid) {
          console.log('RepairAttrModal handleOk() this.bizData ', this.bizData)
          if (this.bizData.programId) {
            this.bizDataRel = {
              attributeType: this.bizData.attributeType,
              attributeId: this.bizData.attributeId,
              programId: this.bizData.programId
            }
            this.bizData.attrProgRelList = [this.bizDataRel]
          } else {
            this.bizData.attrProgRelList = []
          }

          this.confirmLoading = true
          saveRepairAttribute(this.bizData)
            .then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.$emit('ok')
              } else {
                this.$message.warning(res.message)
              }
            })
            .finally(() => {
              this.confirmLoading = false
            })
          this.visible = false
        } else {
          console.error('输入内容验证失败!!')
          return false
        }
      })
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    }
  }
}
</script>
<style lang='less' scoped>
#repairAttrForm {
  .ant-calendar-picker {
    width: 100%;
  }

  .hidden-ant-item {
    display: none;
  }

  [hidden] {
    display: none;
  }
}
</style>