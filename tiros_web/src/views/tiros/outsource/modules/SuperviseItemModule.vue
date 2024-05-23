<template>
  <a-modal
    title="监修申请"
    :width="'70%'"
    :bodyStyle="{height:'50vh'}"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :forceRender="true"
  >
   <SuperviseItemForm :assetId="assetId" ref="superviseForm" @ok="saveSuccess" @fail="saveFail"></SuperviseItemForm>
  </a-modal>

</template>

<script>
import SuperviseItemForm from '@views/tiros/outsource/modules/SuperviseItemForm'

export default {
  name: 'SuperviseItemModal',
  components: { SuperviseItemForm },
  props: ['assetId'],
  data () {
    return {
      visible: false,
      confirmLoading: false
    }
  },
  methods: {
    add () {
      this.edit({})
    },
    async edit (record) {
      this.visible = true
      await this.$refs.superviseForm.edit(record)
    },
    handleOk () {
      this.$refs.superviseForm.save()
    },
    saveSuccess () {
      this.visible = false
      this.confirmLoading=false
      this.$message.success('保存成功')
      this.$emit('ok')
    },
    saveFail () {
      this.$message.error('保存失败')
      this.confirmLoading=false
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