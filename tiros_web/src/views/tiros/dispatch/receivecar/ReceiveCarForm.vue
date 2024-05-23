<template>
  <a-tabs :activeKey="activeKey" @change="changeTab" style="height: 100%">
    <a-tab-pane key="1" tab="接车信息">
      <div style="height: 20px"></div>
      <receive-car-info ref="infoForm" v-if="!isReadonly" :isEdit="isEdit" :train-no.sync="trainNo" :accept-mileage.sync="acceptMileage"></receive-car-info>
      <receive-car-view ref="infoForm" v-else :train-no.sync="trainNo" :accept-mileage.sync="acceptMileage" />
    </a-tab-pane>
    <a-tab-pane key="2" tab="车辆履历">
      <train-resume-tab ref="trainResume" :accept-mileage="acceptMileage" :train-no="trainNo"></train-resume-tab>
    </a-tab-pane>
    <a-tab-pane key="3" tab="开口项">
      <receive-car-open-item
        ref="openItem"
        :isReadonly="isReadonly"
        :trainTypeId="trainTypeId"
        :exchangeId="exchangeId"
      ></receive-car-open-item>
    </a-tab-pane>
    <a-tab-pane key="4" tab="整改项">
      <receive-car-rectify ref="rectify" :isReadonly="isReadonly" :exchange-id="exchangeId"></receive-car-rectify>
    </a-tab-pane>
    <!--    <a-button slot="tabBarExtraContent"
                  @click="handleCancel">返回
        </a-button>-->
  </a-tabs>
</template>

<script>
import ReceiveCarView from './ReceiveCarView'
import ReceiveCarInfo from './ReceiveCarInfo'
import { getExChangeDetail, getExChangeLeaveList, getRectifyList } from '@api/tirosDispatchApi'
import ReceiveCarOpenItem from '@views/tiros/dispatch/receivecar/ReceiveCarOpenItem'
import { getLine } from '@api/tirosApi'
import ReceiveCarRectify from '@views/tiros/dispatch/receivecar/ReceiveCarRectify'
import { everythingIsEmpty } from '@/utils/util'
import TrainResumeTab from '@views/tiros/dispatch/delivercar/TrainResumeTab'

export default {
  name: 'ReceiveCarItem',
  components: { ReceiveCarInfo,ReceiveCarView, ReceiveCarOpenItem, ReceiveCarRectify, TrainResumeTab },
  data() {
    return {
      isEdit:false,
      modalWidth: '80%',
      modalStyle: { top: '20px' },
      activeKey: '1',
      title: '操作',
      loading: false,
      carStateData: [],
      Data: null,
      confirmLoading: false,
      trainTypeId: '',
      exchangeId: '',
      trainNo: '',
      acceptMileage: '',
    }
  },
  props: {
    businessKey: {
      type: String,
      default: null,
    },
    isReadonly: {
      type: Boolean,
      default: false,
    },
    fromFlow: {
      type: Boolean,
      default: false,
    },
  },
  watch: {
    exchangeId(val){
      if(val){
        let queryParam = {
          exchangeId: val,
          pageNo: 1,
          pageSize: 999,
        }
        getExChangeLeaveList(queryParam).then((res) => {
            this.$bus.$emit('setReceiveCarOpenItemData', res.result.records)
        })

        getRectifyList(queryParam).then((res) => {
          this.$bus.$emit('setReceiveCarRectifyData', res.result.records)
        })
      }
    },
  },
  created(){
    this.$bus.$on('setExchangeId', (value) => {
      this.exchangeId = value || ''
    })
  },
  mounted() {
    if (this.businessKey) {
      // 调用后端接口加载数据
      getExChangeDetail({ id: this.businessKey }).then((res) => {
        if (res.success) {
          this.edit(res.result)
        } else {
          console.error('获取交接车记录明细失败：', res.message)
        }
        this.$emit('loaded')
      })
    }
  },
  methods: {
    save(opts) {
      this.exchangeId = ''
      this.$refs.infoForm.save().then(
        (res) => {
          if (!res) {
            opts.res = res
          }
          this.$emit('ok', opts)
        },
        (res) => {
          if (res) {
            opts.res = res
          }
          this.$emit('fail', opts)
        }
      )
    },
    changeTab(activeKey) {
      this.activeKey = activeKey
      let lineId = this.$refs.infoForm.lineId
      this.exchangeId = this.$refs.infoForm.id
      let id = this.$refs.infoForm.model.id
      if (activeKey === '3') {
        if (lineId) {
          getLine({ lineId: lineId }).then((res) => {
            if (res.success) {
              this.trainTypeId = res.result.trainTypeId
              // this.$refs.openItem.findList()
            }
          })
        }
      } else if (activeKey === '2') {
        if (everythingIsEmpty(this.trainNo)) {
          this.activeKey = '1'
          this.$message.warn('请先选择车辆')
        }
      } else if (activeKey === '4') {
        // this.$nextTick(() => {
        //   this.$refs.rectify.findList()
        // })
      }
    },
    // changeTab(activeKey) {
    //   this.activeKey = activeKey
    //
    //   if (activeKey === '3') {
    //     if (!id) {
    //       this.$message.warn('请先填写接车信息并保存!')
    //       this.activeKey = '1'
    //       return
    //     }
    //     if (lineId) {
    //       getLine({ lineId: lineId }).then((res) => {
    //         if (res.success) {
    //           this.trainTypeId = res.result.trainTypeId
    //           this.$refs.openItem.findList()
    //         }
    //       })
    //     } else {
    //       this.activeKey = '1'
    //       this.$message.warn('请选择接车信息中的线路')
    //     }
    //   } else if (activeKey === '2') {
    //     if (everythingIsEmpty(this.trainNo)) {
    //       this.activeKey = '1'
    //       this.$message.warn('请先选择车辆')
    //     }
    //   } else if (activeKey === '4') {
    //     if (!id) {
    //       this.activeKey = '1'
    //       this.$message.warn('请先填写接车信息并保存!')
    //       return
    //     }
    //     this.$nextTick(() => {
    //       this.$refs.rectify.findList()
    //     })
    //   }
    // },
    close() {
      // this.$emit('close')
    },
    add() {
      this.isEdit = false;
      this.$refs.infoForm.bindData({})
    },
    edit(record) {
      /* this.visible=true*/
      this.trainNo = record.trainNo
      this.isEdit = true;
      this.Data = record
      this.acceptMileage = record.acceptMileage;
      this.$refs.infoForm.bindData(record);
    },
    handleCancel() {
      this.$emit('close')
    },
  },
}
</script>

<style scoped>
</style>