<template>
  <a-modal
    :width="'100%'"
    centered
    title="作业指导书"
    dialogClass="fullDialog no-footer"
    :visible="visible"
     @cancel="visible=false"
    :footer="null"
    :destroyOnClose="true"
  >
    <div ref="bookStepDom" style="padding: 15px"></div>
  </a-modal>
</template>

<script>
export default {
  name: 'TaskBookStepsModal',
  data () {
    return {
      title: '操作',
      visible: false,
      bookSteps:[]
    }
  },
  methods:{
    show(data){
      this.visible = true
      this.bookSteps=data
      this.$nextTick(()=>{
        this.setBookStepDom()
      })

    },
    setBookStepDom() {
      let content = ''
      this.bookSteps.forEach((e) => {
        content += `<h2>${e.bookStepNo}<span style="margin-left:15px;">${e.bootStepTitle}</span></h2>`
        content += e.bootStepContent
      })
      if (content) {
        this.$refs.bookStepDom.innerHTML = content
      }
    },
  }

}
</script>

<style scoped>

</style>