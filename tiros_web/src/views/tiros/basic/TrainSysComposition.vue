<template>
  <div class="bodyWrapper">
    <div style="height: 100%;">
      <a-row type="flex" :gutter="16"  style="height: 100%;">
        <a-col :md="5" :sm="24">
          <train-type-sys-composition-left :trainTypeId="trainTypeId" v-model="sysInfo" ref="trainTypeLeft"/>
        </a-col>
        <a-col :md="24 - 5" :sm="24">
          <train-type-sys-composition-right :trainTypeId="trainTypeId" v-model="sysInfo" ref="trainTypeRight"/>
        </a-col>
      </a-row>
    </div>
  </div>
</template>
<script>
import TrainTypeSysCompositionLeft from './modules/trainTypeSys/TrainTypeSysCompositionLeft'
import TrainTypeSysCompositionRight from './modules/trainTypeSys/TrainTypeSysCompositionRight'
import { getTrainTypeListAll } from '@/api/tirosApi'

export default {
  name: 'TrainSysComposition',
  components: { TrainTypeSysCompositionLeft, TrainTypeSysCompositionRight },
  data() {
    return {
      trainTypeId: undefined,
      sysInfo: '',
    }
  },
  mounted() {
    getTrainTypeListAll().then((res) => {
      if (res.success) {
        if (res.result.length > 0) {
          this.trainTypeId = res.result[0].id
          this.$refs.trainTypeLeft.trainChange(this.trainTypeId)
        }
      } else {
        this.$message.error(res.message)
      }
    })
  },
  methods: {},
}
</script>
