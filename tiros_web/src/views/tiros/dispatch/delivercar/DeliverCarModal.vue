<template>
  <a-modal
    :title="title"
    :width="'100%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :dialogClass="`fullDialog ${isReadonly ? 'no-footer' : ''}`"
    @cancel="handleCancel"
    @ok="handleOk"
    :destroyOnClose="true"
  >
<!--    <a-tabs :activeKey="activeKey" @change="changeTab">
      <a-tab-pane key="1" tab="交车信息">
        <deliver-car-info ref="infoForm" :carStateData.sync="Data"></deliver-car-info>
      </a-tab-pane>
      <a-tab-pane key="2" tab="车辆履历">
        <train-resume-tab ref="trainResume" :train-no="Data.trainNo"></train-resume-tab>
      </a-tab-pane>
      <a-tab-pane key="3" tab="开口项">
        <receive-car-open-item
          ref="openItem"
          :trainTypeId="trainTypeId"
          :isReadonly="isReadonly"
          :exchangeId="exchangeId"
        ></receive-car-open-item>
      </a-tab-pane>
      <a-tab-pane key="4" tab="整改项">
        <receive-car-rectify ref="rectify" :isReadonly="isReadonly" :exchange-id="exchangeId"></receive-car-rectify>
      </a-tab-pane>
    </a-tabs>-->
    <DeliverCarForm ref="deliverForm" :isReadonly="isReadonly"  @ok="saveOk()" @fail="saveFail"></DeliverCarForm>
  </a-modal>
</template>

<script>
import DeliverCarInfo from './DeliverCarInfo'

import TrainResumeTab from '@views/tiros/dispatch/delivercar/TrainResumeTab'
import DeliverCarForm from '@views/tiros/dispatch/delivercar/DeliverCarForm'

export default {
  name: 'DeliverCarViewModal',
  props: {
    isReadonly: {
      type: Boolean,
      default: false,
    },
  },
  components: { DeliverCarInfo, TrainResumeTab, DeliverCarForm },
  data() {
    return {
      modalWidth: '80%',
      modalStyle: { top: '20px' },
      title: '操作',
      visible: false,
      confirmLoading: false,
    }
  },

  methods: {
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    add() {
      this.edit({ } )
    },

    edit(record) {
      this.visible = true
      let that = this
      setTimeout(function () {
        that.$refs.deliverForm.edit(record)
      }, 0)
    },

    handleOk() {
      this.confirmLoading=true
      this.$refs.deliverForm.save()
    },
    saveOk () {
      this.confirmLoading=false
      this.$message.success('保存成功')
      this.$emit("ok")
      this.close()
    },
    saveFail () {
      this.confirmLoading=false
      this.$message.error('保存失败')
    }
  },
}
</script>

<style scoped>
</style>