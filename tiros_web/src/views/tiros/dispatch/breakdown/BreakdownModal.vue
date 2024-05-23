<template>
  <a-modal
    title='故障编辑'
    width="100%"
    centered
    :visible='visible'
    dialogClass="fullDialog"
    :confirmLoading='confirmLoading'
    @ok='handleOk'
    @cancel='handleCancel'
    cancelText='关闭'>
    <div style='height: calc(100vh - 5px); overflow-y: auto'>
        <BreakdownFormItem ref="breakdownForm" :fromWriteReport="fromWriteReport" :workStationsList="workStationsList" v-if="visible" @ok="onSaveOk" @fail="onSaveFail" @cancel="onSaveCancel"></BreakdownFormItem>
    </div>

  </a-modal>
</template>

<script>
import BreakdownFormItem from '@views/tiros/dispatch/breakdown/BreakdownFormItem'
export default {
  name: 'BreakdownModal',
  components: {
    BreakdownFormItem
  },
  props: {
    showWorkStation:{
      type: Boolean,
      default: false
    },
    fromWriteReport: {
      type: Boolean,
      default: false,
    }
  },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      workStationsList: []
    }
  },
  created() {

  },
  methods: {
    setWorkStations(data){
      this.workStationsList.length = 0
      data.forEach(element => {
        if (!this.workStationsList.find(e => e.workstationId === element.workstationId)) {
          this.workStationsList.push(element)
        }
      });
    },
    add(orderId, taskId, formType) {
      this.edit({
        workOrderId: orderId,
        orderTaskId: taskId,
        formType: formType,
        createType: formType //  1 表示来自调度， 2 表示来自工班
      })
    },
    edit(record) {
      this.visible = true
      this.$nextTick(()=>{
        this.$refs.breakdownForm.edit(record)
      })
    },
    // 确定
    handleOk() {
      this.confirmLoading = true
      this.$refs.breakdownForm.save()
    },
    onSaveOk () {
      this.confirmLoading = false
      this.visible=false
      this.$message.success('保存成功')
      this.$emit('ok')
    },
    onSaveFail () {
      this.$message.error('保存失败')
      this.confirmLoading = false
    },
    onSaveCancel () {
      this.confirmLoading = false
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.confirmLoading = false
      this.visible = false
    }

  }
}
</script>

<style lang='less' scoped>
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