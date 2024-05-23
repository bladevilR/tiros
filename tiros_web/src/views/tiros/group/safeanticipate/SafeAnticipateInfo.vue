<template>
  <a-modal
    :width="'80%'"
    :visible="visible"
    :footer="null"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <div>
      <h2 align="center">{{detail.title}}</h2>
    <hr>
      <div style="margin-bottom: 25px">
      <p >
        {{detail.content}}
      </p>
      </div>
      <div>
        <h3>已阅读人员</h3>
        <p style="color: #9f9f9f">{{detail.readUserListStr}}</p>
      </div>
      <div>
        <h3>未阅读人员</h3>
        <p style="color: #9f9f9f">{{detail.unReadUserListStr}}</p>
      </div>
    </div>

  </a-modal>

</template>

<script>
  export default {
    name: 'SafeAnticipateInfo',
    data () {
      return {
        visible:false,
        confirmLoading:false,
        detail:{}
      }
    },
    methods:{
      show(data){
        this.visible=true
        this.detail=data
      },
      handleOk(){

      },
      handleCancel () {
        this.close()
      },
      close () {
        this.$emit('close')
        this.$emit('ok')
        this.visible = false
      }
    }
  }
</script>

<style scoped lang="less">
p{
  text-indent: 2em
}

</style>