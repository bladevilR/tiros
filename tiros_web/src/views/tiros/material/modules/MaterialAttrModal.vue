<template>
  <a-modal
    title="设置物资属性"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <a-form :form="form">
      <a-row :gutter="24">
        <a-col >
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="类别">
            <j-dict-select-tag
              placeholder="请选择"
              dictCode="bu_material_type"
              :triggerChange="true"
              v-decorator.trim="[ 'useCategory',validatorRules.useCategory]"
              :allowClear="true">
            </j-dict-select-tag>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col >
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属系统">
            <j-dict-select-tag
              placeholder="请选择"
              dictCode="bu_train_asset_type,name,id,parent_id is null"
              :triggerChange="true"
              v-decorator.trim="['systemId',validatorRules.systemId]"
              :allowClear="true">
            </j-dict-select-tag>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24">
        <a-col>
          <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属工位">
            <a-select
              v-decorator.trim="['workstationName', validatorRules.workstationName]"
              placeholder="请选择"
              :open="false"
              :showArrow="true"
              @focus="openWorkstationModal"
              ref="myWorkstationSelect"
            >
              <div slot="suffixIcon">
                <a-icon
                  v-if="workstationId"
                  @click.stop="clearValue"
                  type="close-circle"
                  style="padding-right: 3px"
                />
                <a-icon v-else type="ellipsis" />
              </div>
            </a-select>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <work-station-list ref="stationSelectModal" @ok="onSelectStation" :group-id="groupId"></work-station-list>
  </a-modal>
</template>

<script>
import WorkStationList from '@views/tiros/common/selectModules/WorkStationList'
import { setGroupStockAttribute } from '@api/tirosMaterialApi'

export default {
  name: 'MaterialAttrModal',
  components: { WorkStationList },
  props: ['ids'],
  data () {
    return {
      visible: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 18 }
      },
      validatorRules: {
        useCategory: { rules: [{ required: true, message: '请选择分类!' }] },
        systemId: { rules: [{ required: true, message: '请选择所属系统!' }] },
        workstationName: { rules: [{ required: true, message: "请选择工位!" }] },
      },
      workstationId: '',
      groupId:this.$store.getters.userInfo.departId

    }
  },
  methods: {
    show () {
      this.visible = true
    },
    openWorkstationModal () {
      this.$refs.stationSelectModal.showModal()
      this.$refs.myWorkstationSelect.blur()
    },
    onSelectStation (data) {
      if (data && data.length > 0) {
        this.form.setFieldsValue({
          workstationName: data[0].name,
        });
        this.workstationId = data[0].id
      }
    },
    clearValue () {
      this.workstationName = ''
      this.workstationId=''
    },
    handleOk () {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          let formData = Object.assign({}, values, {
            workstationId: that.workstationId,
            ids: that.ids
          })
          that.confirmLoading = true
          setGroupStockAttribute(formData).then((res) => {
            if (res.success) {
              that.$message.success(res.message)
              that.$emit('ok')
              that.close()
            } else {
              that.$message.error(res.message)
            }
          }).finally(() => {
            that.confirmLoading = false
          })
        }
      })
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
    }
  }
}
</script>

<style scoped>

</style>