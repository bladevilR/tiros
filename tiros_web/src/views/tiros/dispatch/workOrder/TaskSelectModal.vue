<template>
  <a-modal
    width="70%"
    title="任务选择"
    centered
    :visible="visible"
    :bodyStyle="{ height: '70vh' }"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <vxe-table
      border
      ref="listTable"
      :align="'center'"
      :data="tasks"
      max-height="100%"
      show-overflow="ellipsis"
      :radio-config="!multiple ? {trigger: 'row'} : {}"
      :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
      :edit-config="{trigger: 'click', mode: 'cell', showIcon:'true'}"
    >
      <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
      <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
      <vxe-table-column type="seq" title="任务编号" width="100"></vxe-table-column>
      <vxe-table-column field="taskName" title="任务名称" align="left"></vxe-table-column>
      <vxe-table-column field="workTime" title="预计工时" align="right" width="100"></vxe-table-column>
      <vxe-table-column field="taskContent" title="任务要求" align="left"></vxe-table-column>
      <vxe-table-column field="remark" title="备注" align="left" width="200"></vxe-table-column>
    </vxe-table>
  </a-modal>
</template>

<script>
export default {
  name: 'TaskSelectModal',
  props: {
    multiple: {
      type: Boolean,
      default: false,
    },
  },
  data () {
    return {
      visible: false,
      tasks: []
    }
  },
  methods: {
    showModal(tasks) {
      this.tasks = tasks
      this.visible = true
    },
    handleOk() {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if(this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      this.$emit('ok', selectRecords)
      this.close()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
  }
}
</script>

<style scoped>

</style>