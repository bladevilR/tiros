<template>
  <a-modal
    :title="title"
    :width="1100"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资名称">
              <a-select
                v-decorator.trim="['name', validatorRules.name]"
                placeholder="请选择关联设备"
                :open="false"
                :showArrow="true"
                @focus="openmaterModal"
                ref="mymaterSelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资编码">
              <a-input placeholder="请输入" disabled v-decorator.trim="['materialCode', validatorRules.materialCode]" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="资产编码">
              <a-input placeholder="请输入" v-decorator.trim="['assetCode', validatorRules.assetCode]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
              <!--              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="[ 'lineId', validatorRules.lineId ]"
                dictCode="bu_mtr_line,line_name,line_id"
              />-->
              <line-select-list v-decorator="['lineId', validatorRules.lineId]"> </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车间">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['workshopId', validatorRules.workshopId]"
                dictCode="bu_mtr_workshop,name,id"
                @change="changeWorkShop"
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属班组">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['groupId', validatorRules.groupId]"
                :dictCode="dictCodeStr"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="序列号">
              <a-input placeholder="请输入" v-decorator.trim="['manufNo', validatorRules.manufNo]" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="单价">
              <a-input placeholder="请输入" v-decorator.trim="['price', validatorRules.price]" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="设备类型">
              <a-select
                placeholder="请选择关联设备"
                :open="false"
                :showArrow="true"
                v-decorator.trim="['assetTypeName', validatorRules.assetTypeName]"
                @focus="openModal"
                ref="mySelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="保管人员">
              <a-select
                placeholder="请选择保管人员"
                :open="false"
                :showArrow="true"
                v-decorator.trim="['dutyUserName', validatorRules.dutyUserName]"
                @focus="openuserModal"
                ref="myuserSelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="状态">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['status', validatorRules.status]"
                dictCode="bu_turnover_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="当前位置">
              <a-input placeholder="请输入" v-decorator.trim="['currentLocation', validatorRules.currentLocation]" />
            </a-form-item>
          </a-col>
        </a-row>

        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="出库日期" width="40">
              <a-date-picker v-decorator.trim="['outDate', validatorRules.outDate]" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="出库单号">
              <a-input placeholder="请输入" v-decorator.trim="['outOrderNo', validatorRules.outOrderNo]" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="规格型号">
              <a-input placeholder="请输入" v-decorator.trim="['model', validatorRules.model]" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
    <train-asset-type ref="selectForm" :multiple="false" @ok="selectTrainSys"></train-asset-type>
    <user-list ref="UserModalForm" :multiple="false" @ok="addTarget"></user-list>
    <material-list ref="materModalForm" :multiple="false" @ok="selectmater"></material-list>
  </a-modal>
</template>

<script>
import { addRotables, editRotables, getSystemName } from '../../../../api/tirosMaterialApi'
import MaterialList from '../../common/selectModules/MaterialList'
import UserList from '../../common/selectModules/UserList'
import TrainAssetType from '../../common/selectModules/TrainAssetType'
import moment from 'moment'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'RotablesItemModal',
  components: { MaterialList, UserList, TrainAssetType, LineSelectList },
  data() {
    return {
      dictCodeStr: 'bu_mtr_workshop_group,group_name,id',
      value: 1,
      title: '操作',
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 },
      },
      trainTypeSysId: '',
      trainTypeSysName: '',
      dutyUsername: '',
      dutyUserid: '',
      systemIds: '',
      systemNames: '',
      materName: '',
      materId: '',
      matercode: '',
      isClose: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      dictcode: '',
      validatorRules: {
        name: { rules: [{ required: true, message: '请选择物资名称!' }] },
        materialCode: { rules: [{ required: true, message: '请输入物资编码!' }] },
        materialTypeId: { rules: [] },
        assetCode: { rules: [] },
        workshopId: { rules: [] },
        groupId: { rules: [] },
        manufNo: { rules: [] },
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        price: { rules: [] },
        assetTypeName: { rules: [{ required: true, message: '请选择设备类型!' }] },
        assetTypeId: { rules: [{ required: true, message: '请选择设备类型!' }] },
        dutyUserName: { rules: [{ required: true, message: '请选择保管人员!' }] },
        status: { rules: [{ required: true, message: '请选择状态!' }] },
        currentLocation: { rules: [] },
        outDate: { rules: [] },
        outOrderNo: { rules: [] },
        model: { rules: [] },
      },
    }
  },

  created() {},
  methods: {
    changeWorkShop(data) {
      console.log(data)
      if (data) {
        this.dictCodeStr = 'bu_mtr_workshop_group,group_name,id,workshop_id=' + data
      } else {
        this.dictCodeStr =
          "bu_mtr_workshop_group,group_name,id,workshop_id='" +
          this.$store.getters.userInfo.workshopId +
          "'|workshop_id"
      }
    },
    changeClose(checked) {
      this.isClose = checked
    },
    add() {
      this.edit({})
    },
    edit(record) {
      if (record.id) {
        this.isClose = record['close'] && record['close'] == 1 ? true : false
      } else {
        this.isClose = false
      }
      // this.$form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          name: this.model.name,
          materialCode: this.model.materialCode,
          materialTypeId: this.model.materialTypeId,
          assetCode: this.model.assetCode,
          workshopId: this.model.workshopId,
          groupId: this.model.groupId,
          manufNo: this.model.manufNo,
          lineId: this.model.lineId,
          price: this.model.price,
          assetTypeId: this.model.assetTypeId,
          assetTypeName: this.model.assetTypeName,
          dutyUserId: this.model.dutyUserId,
          dutyUserName: this.model.dutyUserName,
          status: this.model.status,
          currentLocation: this.model.currentLocation,
          outDate: moment(this.model.outDate || new Date(), 'YYYY-MM-DD'),
          outOrderNo: this.model.outOrderNo,
          model: this.model.model,
        })
      })
    },
    openModal() {
      this.$refs.selectForm.showModal()
      this.$refs.mySelect.blur()
    },
    openuserModal() {
      this.$refs.UserModalForm.showModal()
      this.$refs.myuserSelect.blur()
    },
    openmaterModal() {
      this.$refs.materModalForm.showModal()
      this.$refs.mymaterSelect.blur()
    },
    selectTrainSys(data) {
      console.log(data)
      if (data.length) {
        this.assetTypeId = data[0].id
        this.assetTypeName = data[0].name
        getSystemName({ id: this.assetTypeId }).then((res) => {
          this.systemNames = res.result.name
        })
      }
      this.form.setFieldsValue({
        assetTypeName: this.assetTypeName,
        assetTypeId: this.assetTypeId,
      })
      console.log(this.assetTypeId)
    },

    addTarget(data) {
      // console.log(data)
      if (data.length) {
        this.dutyUsername = data[0].realname
        this.dutyUserid = data[0].id
      }
      this.form.setFieldsValue({
        dutyUserName: this.dutyUsername,
        dutyUserId: this.dutyUserid,
      })
    },
    selectmater(data) {
      // console.log(data)
      if (data.length) {
        this.materName = data[0].name
        this.materId = data[0].id
        this.matercode = data[0].code
      }
      this.form.setFieldsValue({
        name: this.materName,
        materialCode: this.matercode,
        materialTypeId: this.materId,
      })
    },

    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true

          let formData = Object.assign(that.model, values, {
            outDate: moment(values.outDate).format('YYYY-MM-DD HH:mm:ss'),
          })

          console.log(formData)
          let obj
          if (!that.model.id) {
            formData['assetTypeId'] = that.assetTypeId
            formData['dutyUserId'] = that.dutyUserid
            formData['materialTypeId'] = that.materId
            formData['materialCode'] = that.matercode
            obj = addRotables(formData)
          } else {
            obj = editRotables(formData)
          }
          obj
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
              } else {
                that.$message.warning(res.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
              that.close()
            })
          this.visible = false
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
    },
  },
}
</script>