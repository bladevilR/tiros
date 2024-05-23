<template>
  <a-modal
    :width="'85%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    dialogClass="fullDialog no-title no-footer"
    @cancel="handleCancel"
    :destroyOnClose="true"
  >
    <div class="bodyWrapper" >
      <a-tabs :active-key="activeKey" @change="handleActive">
        <a-tab-pane key="1" tab="合同基本信息">
          <contract-info-basic :contractDetail="detail"></contract-info-basic>
        </a-tab-pane>
        <a-tab-pane key="2" tab="合同监控">
<!--          <contract-pay-detail :detail="detail" ref="contractPayDetail"></contract-pay-detail>-->

          <div class="content">
            <div class="info-wrapper info-top-wrapper" v-if="contractMonitor!=null">
              <div style="text-align: center">
                <a-row :gutter="24" :style="{marginLeft:'50px'}">
                  <a-col :md="4" v-if="contractMonitor.payed!=null">
                    <progress-chart :value="contractMonitor.payed" have-color="#389AF4" no-color="#dfeaff"
                                    :rotateZ="`rotateZ(${Math.round( contractMonitor.payed*3.6 )}deg)`" />
                    <p style="color:#389AF4 ">已支付比例</p>
                  </a-col>
                  <a-col :md="4" v-if="contractMonitor.bakMoney!=null">
                    <progress-chart :value="contractMonitor.bakMoney" have-color="#FF8C37" no-color="#ffdcc3"
                                    :rotateZ="`rotateZ(${Math.round(contractMonitor.bakMoney*3.6)}deg)`" />
                    <p style="color:#FF8C37 ">暂列金使用</p>
                  </a-col>
                  <a-col :md="4" v-if="contractMonitor.score!=null">
                    <progress-chart :value="contractMonitor.score" have-color="#FFC257" no-color="#ffedcc"
                                    :rotateZ="`rotateZ(${Math.round(contractMonitor.score*3.6)}deg)`" />
                    <p style="color:#FFC257 ">评分值</p>
                  </a-col>
                  <!--                <a-col :md="4" v-if="contractMonitor.contractProcess!=null">
                                    <progress-chart  :value="contractMonitor.contractProcess"  have-color="#FD6F97" no-color="#fed4e0"
                                                     :rotateZ="`rotateZ(${Math.round(contractMonitor.contractProcess*3.6)}deg)`"/>
                                    <p style="color:#FD6F97 ">合同进度</p>
                                  </a-col>-->
                  <a-col :md="4" v-if="contractMonitor.quality!=null">
                    <progress-chart :value="contractMonitor.quality" have-color="#A587FC" no-color="#e3d9fe"
                                    :rotateZ="`rotateZ(${Math.round(contractMonitor.quality*3.6)}deg)`" />
                    <p style="color:#A587FC ">质保期限</p>
                  </a-col>
                </a-row>
              </div>
            </div>
          </div>


        </a-tab-pane>
<!--        <a-tab-pane key="3" tab="暂列金使用记录">
          <contract-bak-detail :detail="detail" ref="contractBakDetail"></contract-bak-detail>
        </a-tab-pane>-->
<!--        <a-button slot="tabBarExtraContent"
                  @click="handleCancel">返回
        </a-button>-->
      </a-tabs>
    </div>
  </a-modal>
</template>

<script>
import PieDonut from '@/components/chart/PieDonut'
import { getContractMonitor } from '@/api/tirosOutsourceApi'
import ProgressChart from '@views/tiros/outsource/contract/ProgressChart'
import ContractInfoBasic from '@views/tiros/outsource/contract/ContractInfoBasic'
import ContractPayDetail from '@views/tiros/outsource/contract/ContractPayDetail'
import ContractBakDetail from '@views/tiros/outsource/contract/ContractBakDetail'

export default {
  name: 'ContractInfoDetail',
  components: { ContractInfoBasic, PieDonut, ContractPayDetail, ContractBakDetail, ProgressChart },
  data () {
    return {
      detail: {},
      contractMonitor: null,
      visible: false,
      confirmLoading: false,
      title: '合同详情',
      activeKey: '1'
    }
  },
  methods: {
    show (value) {
      this.visible = true
      this.detail = value
      this.contractMonitor = null
      this.findList()
    },
    findList () {
      let queryParam = {
        id: this.detail.id
      }
      getContractMonitor(queryParam).then((res) => {
        this.contractMonitor = res.result
        let data = [{ type: 'payed', value: this.contractMonitor.payed },
          { type: 'bakMoney', value: this.contractMonitor.bakMoney },
          { type: 'score', value: this.contractMonitor.score },
          { type: 'quality', value: this.contractMonitor.quality }
        ]
      })
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.activeKey = '1'
      this.$emit('close')
      this.visible = false
    },
    handleActive (e) {
      this.activeKey = e
    }

  }
}
</script>

<style scoped lang="less">
.circle {
  width: 115px;
  height: 115px;
  border-style: solid;
  border-width: 5px;
  border-radius: 50%;
  -moz-border-radius: 50%;
  -webkit-border-radius: 50%;
  /*border-color: #1E90FF;*/
  margin-top: 10px;
  cursor: pointer;
}

.content {
  .info-wrapper {
    border: 1px solid #eee;
    position: relative;
    border-radius: 8px;
    padding: 10px;
    margin-bottom: 20px;
    margin-top: 10px;
  }

  .info-wrapper h4 {
    position: absolute;
    top: -14px;
    padding: 1px 8px;
    margin-left: 16px;
    color: #777;
    border-radius: 2px 2px 0 0;
    background: #fff;
    font-size: 14px;
    width: auto;
  }

  .ant-form-item {
    margin: 0;
  }
}

</style>