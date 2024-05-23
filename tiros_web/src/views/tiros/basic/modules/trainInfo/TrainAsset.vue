<template>
  <a-modal
    title="查看"
    :width="'100%'"
    height="auto"
    :visible="visible"
    dialogClass="fullDialog"
    :confirmLoading="confirmLoading"
    :footer="null"
    @cancel="handleCancel"
    :destroyOnClose="true"
    :zIndex="50"
  >
    <div style="display: flex; justify-content: space-between; ; height: calc(100vh - 68px);">
      <!-- <div style="width: 16%; height: 100%;">
        <left-train-asset v-model="trainInfo"  ref="leftTrainAsset"/>
      </div> -->
      <div style="width: 100%; height: 100%;">
        <train-info v-model="trainInfo" @close="handleCancel()"/>
      </div>
    </div>

    <!-- <a-row type="flex" :gutter="16" style="max-height: 80vh">
      <a-col :md="4" :sm="24" style="height: 100%">
        <left-train-asset v-model="trainInfo" />
      </a-col>
      <a-col :md="24-4" :sm="24" style="height: 100%">
        <train-info v-model="trainInfo" @close="handleCancel()"/>
      </a-col>
    </a-row> -->
  </a-modal>
</template>

<script>

import TrainInfo from '@views/tiros/basic/modules/trainInfo/TrainInfo'
import LeftTrainAsset from '@views/tiros/basic/modules/trainInfo/LeftTrainAsset'

export default {
  name: 'TrainAsset',
  components: { LeftTrainAsset, TrainInfo },
  data () {
    return {
      visible: false,
      confirmLoading: false,
      trainInfo: {
        trainId: '',
        trainTypeId: ''
      }
    }
  },
  methods: {
    show (value) {
      this.visible = true
      this.trainInfo.trainId=value.trainId
      this.trainInfo.trainTypeId=value.trainTypeId
      this.$nextTick(()=>{
        this.$refs.leftTrainAsset.queryTreeData()
      })

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

<style scoped>
</style>