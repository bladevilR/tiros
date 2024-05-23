<template>
  <j-modal
    :title="title"
    :visible="visible"
    centered
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :destroyOnClose="true"
    fullscreen
    cancelText="关闭"
  >
    <a-form :form="form">
      <div class="info-wrapper info-top-wrapper">
        <h4>基本信息</h4>
        <a-row>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="检查表名称">
              <a-input
                placeholder="请输入检查表名称"
                allow-clear
                :maxLength="65"
                v-decorator="['title', validatorRules.title]"
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属线路">
              <line-select-list v-decorator="['lineId', validatorRules.lineId]"> </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="修程类型">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator="['repairProId', validatorRules.repairProId]"
                dictCode="bu_repair_program,name,id"
                placeholder="请选择修程类型"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="是否有效">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator="['status', validatorRules.status]"
                dictCode="bu_effective"
                placeholder="请选择是否有效"
              />
            </a-form-item>
          </a-col>
          <a-col :span="8">
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
      </div>
      <div class="info-wrapper info-top-wrapper">
        <h4>内容明细</h4>
        <a-row>
          <a-tabs default-active-key="1">
            <a-tab-pane key="1" tab="检查项点">
              <CheckItemList :data="itemList" @ok="addCheckItemData" @del="delCheckItemData"></CheckItemList>
            </a-tab-pane>
            <a-tab-pane key="2" tab="参考工艺文件">
              <FileList :data="techLinkList" @ok="addFileItemData" @del="delFileItemData"></FileList>
            </a-tab-pane>
          </a-tabs>
        </a-row>
      </div>
    </a-form>
    <train-asset-type ref="selectForm" :multiple="false" @ok="selectTrainSys"></train-asset-type>
  </j-modal>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import { getWorkcheck, saveOrUpdateWorkCheckItem } from '@/api/tirosApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import TrainAssetType from '@views/tiros/common/selectModules/TrainAssetType'

import CheckItemList from './components/CheckItemList'
import FileList from './components/FileList'

export default {
  name: 'JobCheckItemModal',
  components: { LineSelectList, CheckItemList, FileList, TrainAssetType },
  data() {
    return {
      title: '操作',
      visible: false,
      model: {},
      assetTypeId: '',
      assetTypeName: '',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        title: { rules: [{ required: true, message: '请输入作业检查表名称!' },{ max: 64, message: '不能超过64个字符' }] },
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        repairProId: { rules: [{ required: true, message: '请选择修程类型!' }] },
        status: { rules: [{ required: true, message: '请选择是否有效!' }] },
        assetTypeName: { rules: [{ required: true, message: '请选择设备类型!' }] },
      },
      itemList: [],
      techLinkList: [],
    }
  },
  created() {},
  methods: {
    moment,
    openModal() {
      this.$refs.selectForm.showModal()
      this.$refs.mySelect.blur()
    },
    selectTrainSys(data) {
      if (data.length) {
        this.assetTypeId = data[0].id
        this.assetTypeName = data[0].name
      }
      this.form.setFieldsValue({
        assetTypeName: this.assetTypeName,
      })
    },
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.visible = true
      if (record.id) {
        getWorkcheck({
          id: this.model.id,
        }).then((res) => {
          if (res.success) {
            this.model = Object.assign({}, res.result)
            this.$nextTick(() => {
              this.form.setFieldsValue({
                title: this.model.title,
                lineId: this.model.lineId,
                repairProId: this.model.repairProId,
                status: this.model.status,
                assetTypeName: this.model.assetTypeName,
              })
              this.assetTypeId = this.model.assetTypeId
              this.itemList = this.model.itemList || []
              this.techLinkList = this.model.techLinkList || []
            })
          }
        })
      }
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            assetTypeId: that.assetTypeId,
            itemList: that.itemList,
            techLinkList: that.techLinkList,
          })
          let obj
          if (that.model.id) {
            formData.id = that.model.id
          }
          obj = saveOrUpdateWorkCheckItem(formData)
          obj
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
                that.close()
              } else {
                that.$message.error(res.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
            })
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
      this.itemList = []
      this.techLinkList = []
    },
    addCheckItemData(data) {
      let that = this
      if (that.itemList.length) {
        for (let i = 0; i < that.itemList.length; i++) {
          let item = that.itemList[i]
          if (item._XID == data._XID) {
            that.$set(that.itemList, i, data)
            return false
          }
        }
        that.itemList.push(data)
      } else {
        that.itemList.push(data)
      }
    },
    delCheckItemData(data) {
      let that = this
      for (let i = 0; i < data.length; i++) {
        const item = data[i]
        for (let j = 0; j < that.itemList.length; j++) {
          const workCheckItem = that.itemList[j]
          if (workCheckItem._XID == item._XID) {
            that.itemList.splice(j, 1)
          }
        }
      }
    },
    addFileItemData(data) {
      let that = this
      that.techLinkList.push(data)
    },
    delFileItemData(data) {
      let that = this
      for (let i = 0; i < data.length; i++) {
        const item = data[i]
        for (let j = 0; j < that.techLinkList.length; j++) {
          const techLinkListItem = that.techLinkList[j]
          if (techLinkListItem._XID == item._XID) {
            that.techLinkList.splice(j, 1)
          }
        }
      }
    },
  },
}
</script>

<style lang="less" scoped>
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
</style>