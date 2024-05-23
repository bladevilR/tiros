<template>
  <div>
    <div :style='containerHeightStyle'>
      <div class='info-wrapper info-top-wrapper' style='margin-top: 10px'>
        <h4>故障信息</h4>

        <a-descriptions size='default' bordered :column='6'>
          <a-descriptions-item label='故障编号' :span='2'>
            {{ detail.faultSn }}
          </a-descriptions-item>
          <a-descriptions-item label='发现时间' :span='2'>
            {{ detail.happenTime }}
          </a-descriptions-item>
          <a-descriptions-item label='线路' :span='2'>
            {{ detail.lineName }}
          </a-descriptions-item>
          <a-descriptions-item label='车辆' :span='2'>
            {{ detail.trainNo }}
          </a-descriptions-item>
          <a-descriptions-item label='系统' :span='2'>
            {{ detail.sysName }}
          </a-descriptions-item>
          <a-descriptions-item label='子系统' :span='2'>
            {{ detail.subSysName }}
          </a-descriptions-item>
          <a-descriptions-item label='部件' :span='2'>
            {{ detail.faultAssetName }}
          </a-descriptions-item>
          <a-descriptions-item label='发现阶段' :span='2'>
            {{ detail.phase_dictText }}
          </a-descriptions-item>
          <a-descriptions-item label='故障等级' :span='2'>
            {{ detail.faultLevel_dictText }}
          </a-descriptions-item>
          <a-descriptions-item label='是否正线' :span='2'>
            {{ detail.faultOnline_dictText }}
          </a-descriptions-item>
          <!--          <a-descriptions-item label="是否有责" :span="2">-->
          <!--            {{ detail.hasDuty_dictText }}-->
          <!--          </a-descriptions-item>-->
          <a-descriptions-item label='是否委外' :span='2'>
            {{ detail.outsource_dictText }}
          </a-descriptions-item>
          <a-descriptions-item v-if='detail.outsource == 1' label='所属厂商' :span='6'>
            {{ detail.outSupplierName }}
          </a-descriptions-item>
          <a-descriptions-item label='列计划' :span='6'>
            {{ detail.planName }}
          </a-descriptions-item>
          <a-descriptions-item label='故障描述' :span='6'>
            {{ detail.faultDesc }}
          </a-descriptions-item>
          <!--          <a-descriptions-item label="故障分类" :span="6">-->
          <!--            {{ detail.categoryCode }}-->
          <!--          </a-descriptions-item>-->
          <!--          <a-descriptions-item label="故障分类描述" :span="6">-->
          <!--            {{ detail.categoryCodeDes }}-->
          <!--          </a-descriptions-item>-->
          <a-descriptions-item label='故障代码' :span='6'>
            {{ detail.faultCodeCode }}
            <span style='color:#c1c1c1;margin-left:20px'>
              （
              <a-space>
                <span v-for='(item,k) in getFaultdetailList'
                      :key='k'>故障{{ item.fltLevel_dictText }}：{{ item.fltCode }}-{{ item.fltName }}；</span>
                <span>故障部件：{{ detail.faultCodeCode }}-{{ detail.faultCodeCodeDes }}</span>
              </a-space>
              ）
            </span>

          </a-descriptions-item>
          <!-- <a-descriptions-item label="故障代码描述" :span="6">
            {{ detail.faultCodeCodeDes }}
          </a-descriptions-item> -->
          <a-descriptions-item label='故障状态' :span='6'>
            {{ detail.status_dictText }}
          </a-descriptions-item>
        </a-descriptions>
      </div>
      <div class='info-wrapper info-top-wrapper'>
        <h4>提报信息</h4>
        <a-descriptions bordered :column='6'>
          <a-descriptions-item label='提报班组' :span='2'>
            {{ detail.reportGroupName }}
          </a-descriptions-item>
          <a-descriptions-item label='提报人员' :span='2'>
            {{ detail.reportUserName }}
          </a-descriptions-item>
          <a-descriptions-item label='提报时间' :span='2'>
            {{ detail.reportTime }}
          </a-descriptions-item>
          <a-descriptions-item label='责任工程师' :span='2'>
            {{ detail.dutyEngineerName }}
          </a-descriptions-item>
        </a-descriptions>
      </div>
      <div class='info-wrapper info-top-wrapper'>
        <h4>处理信息</h4>
        <a-descriptions bordered :column='6'>
          <a-descriptions-item label='处理班组' :span='2'>
            {{ handleModel.solutionGroupName }}
          </a-descriptions-item>
          <a-descriptions-item label='处理人员' :span='2'>
            {{ handleModel.solutionUserName }}
          </a-descriptions-item>
          <a-descriptions-item label='处理时间' :span='2'>
            {{ handleModel.solutionTime }}
          </a-descriptions-item>
          <a-descriptions-item label='处理结果' :span='6'>
            {{ detail.handleStatus_dictText }}
          </a-descriptions-item>
          <a-descriptions-item label='关闭时间' :span='2'>
            {{ detail.closeTime }}
          </a-descriptions-item>
          <a-descriptions-item label='关闭人员' :span='4'>
            {{ detail.closeUserName }}
          </a-descriptions-item>
          <!--          <a-descriptions-item label="故障原因" :span="2">-->
          <!--            {{ handleModel.faultReasonCode }}-->
          <!--          </a-descriptions-item>-->
          <a-descriptions-item label='原因描述' :span='4'>
            {{ handleModel.reasonDesc }}
          </a-descriptions-item>
          <!--          <a-descriptions-item label="处理措施" :span="2">-->
          <!--            {{ handleModel.faultSolutionCode }}-->
          <!--          </a-descriptions-item>-->
          <a-descriptions-item label='措施描述' :span='4'>
            {{ handleModel.solutionDesc }}
          </a-descriptions-item>
        </a-descriptions>
      </div>
      <div class='info-wrapper info-top-wrapper'>
        <h4>附件</h4>
        <div v-if='annexList.length === 0'>暂无数据</div>
        <div v-else>
          <ul v-for='(item, k) in annexList' :key='k'>
            <li>
              <a-icon type='file' />
              <a @click='handleSeeing(item)'>
                {{ item.name }}</a
              >
            </li>
          </ul>
        </div>
      </div>
      <doc-preview-modal :title='fileName' ref='docPreview'></doc-preview-modal>
    </div>
  </div>
</template>

<script>
import { getBreakdownInfo, getFaultdetail } from '@api/tirosDispatchApi'
import { everythingIsEmpty } from '@/utils/util'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'

export default {
  data() {
    return {
      fileName: '',
      annexList: [],
      detail: {},
      handleModel: {},
      getFaultdetailList: [],
      containerHeightStyle: {
        height: this.height,
        overflowY: 'auto'
      }
    }
  },
  components: { DocPreviewModal },
  props: {
    businessKey: {
      type: String,
      default: null
    },
    isReadonly: {
      type: Boolean,
      default: true
    },
    fromFlow: {
      type: Boolean,
      default: false
    },
    height: {
      type: String,
      default: '100%'
    }
  },
  mounted() {
    if (this.businessKey) {
      this.show(this.businessKey)
    }
  },
  methods: {
    getFaultdetail(id) {
      // 获取故障信息
      this.getFaultdetailLoading = true
      getFaultdetail({ code: id }).then((res) => {
        if (res.success && res.result.length) {
          this.getFaultdetailList = res.result.filter((item) => {
            return item.fltLevel != 0 && item.fltLevel != 3 //条件;
          })
        }
        this.getFaultdetailLoading = false
        this.$emit('loaded')
      }).catch((err) => {
        this.getFaultdetailLoading = false
      })
    },
    show(value) {
      if (!everythingIsEmpty(value)) {
        getBreakdownInfo({ id: value }).then((res) => {
          let data = res.result
          this.detail = data
          if (data.faultCodeId) {
            this.getFaultdetail(data.faultCodeId)
          }
          this.annexList = data.annexList
          if (!everythingIsEmpty(data.handleRecordList)) {
            this.handleModel = data.handleRecordList[0]
          }
          this.$emit('loaded')
        })
      }
    },
    handleSeeing(data) {
      this.fileName = data.name
      this.$refs.docPreview.handleFilePath(data.savepath)
    }
  }
}
</script>

<style lang='less' scoped>
.info-top-wrapper {
  /deep/ .ant-descriptions-item-label {
    width: 140px;
    text-align: right;
  }

  /deep/ .ant-descriptions-item-content {
    width: 300px;
  }
}

.info-wrapper {
  border: 1px solid #eee;
  position: relative;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 20px;
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

li {
  width: 100%;
  list-style: none;
  line-height: 2rem;
  color: black;
  transition: background-color 1s linear, color 1s linear;
  -webkit-transition: background-color 1s linear, color 1s linear;
  -moz-transition: background-color 1s linear, color 1s linear;
  -o-transition: background-color 1s linear, color 1s linear;
}

li:hover {
  background-color: #ddeeff;
  color: #19aaff;
}
</style>
