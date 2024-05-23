<template>
  <a-modal
    :width="'85%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    dialogClass="fullDialog no-title no-footer"
    @cancel="handleCancel"
    :destroyOnClose="true"
    :closable="false"
    :zIndex="50"
  >
  <a-row type="flex" :gutter="16">
    <a-col :md="5" :sm="24">
      <train-type-sys-left :trainTypeId="trainTypeId" v-model="sysInfo" />
    </a-col>
    <a-col :md="24-5" :sm="24">
      <train-type-sys-right :trainTypeId="trainTypeId" v-model="sysInfo" @close="handleCancel()" />
    </a-col>
  </a-row>
  </a-modal>
</template>
<script>
  import TrainTypeSysLeft from './modules/trainTypeSys/TrainTypeSysLeft'
  import TrainTypeSysRight from './modules/trainTypeSys/TrainTypeSysRight'

  export default {
    name: 'AddressList',
    components: { TrainTypeSysLeft ,TrainTypeSysRight},
    data() {
      return {
        trainTypeId :'',
        sysInfo: '',
        visible:false,
        confirmLoading:false
      }
    },
    methods: {
      show(value){
        this.visible=true
        this.trainTypeId=value.id
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
