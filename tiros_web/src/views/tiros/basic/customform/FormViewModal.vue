<template>
  <a-modal
    :title="'表单查看'"
    :width="'100%'"
    dialogClass="fullDialog no-footer"
    :visible="visible"
    @cancel="visible = false"
    :footer="null"
    :destroyOnClose="true"
  >
    <FormView :custom-id="customId" :form-name="formName"></FormView>
  </a-modal>
</template>

<script>
import FormView from './view'
export default {
  name: 'FormViewModal',
  components: { FormView },
  props: ['formName'],
  data() {
    return {
      visible: false,
      customId: null,
    }
  },
  methods: {
    showModal(id) {
      if (id) {
        this.customId = id
        this.visible = true
      }
    },
  },
}
</script>

<style scoped>
</style>