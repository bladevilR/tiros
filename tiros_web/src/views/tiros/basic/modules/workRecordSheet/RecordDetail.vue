<template>
  <a-modal
    :title="recordTitle+'-明细管理'"
    :width="'100%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    dialogClass="fullDialog no-footer"
    @cancel="handleCancel"
    :destroyOnClose="true"
  >
  <a-card :bordered="false" id="recordContent">
    <!-- <div> -->
      <a-row type="flex" :gutter="16">
        <a-col :md="5" :sm="24">
          <left-page :recordId="recordId" v-model="catId" />
        </a-col>
        <a-col :md="24 - 5" :sm="24">
          <right-page :recordId="recordId" v-model="catId"  @close="handleCancel()"/>
        </a-col>
      </a-row>
    <!-- </div> -->
  </a-card>
  </a-modal>
</template>

<script>
import LeftPage from './LeftPage'
import RightPage from './RightPage'
export default {
  name: 'RecordDetail',
  components: { LeftPage, RightPage },
  data() {
    return {
      recordTitle: this.$route.query.recordTitle,
      recordId: this.$route.params.id,
      catId: '',
      visible:false,
      confirmLoading:false
    }
  },
 /* created() {
    console.log(this.recordTitle)
  },*/
  methods:{
    show(value){
      this.visible=true
      this.recordTitle=value.title
      this.recordId=value.id
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

<style lang="less">
#recordContent {
  .ant-card-body {
    padding: 0 ;
  }
  .recordTitle {
    width: 100%;
    padding: 10px;
    font-size: 16px;
    font-weight: 600;
  }
}
</style>