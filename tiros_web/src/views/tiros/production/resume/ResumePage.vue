<template>
  <div>
    <a-row type="flex" :gutter="16">
      <a-col class="leftPart" :md="5" :sm="24">
        <a-card id="leftPart">
          <div class="titleDiv">
            <a-icon type="apartment" />
            车辆履历树
          </div>

          <a-tree
            v-if="treeData.length"
            :tree-data="treeData"
            :replaceFields="{ title: 'longTitle', key: 'id' }"
            :defaultExpandedKeys="defaultSelectedKeys"
            @select="selectPid"
            :default-selected-keys="defaultKey"
            :load-data="queryTreeData"
          />
        </a-card>
      </a-col>
      <a-col class="rightPart" :md="24 - 5" :sm="24">
        <a-card id="rightPart">
          <a-tabs :activeKey="activeKey" @change="changeTab">
            <a-tab-pane key="1" tab="基本信息">
              <train-res-basic-info
                v-if="this.type"
                :structureDetail="FstructureDetailId"
                :train-no="trainNo"
              ></train-res-basic-info>
              <res-basic-info v-else :structureDetail="FstructureDetailId" :train-no="trainNo"></res-basic-info>
            </a-tab-pane>
            <a-tab-pane key="2" tab="工单记录">
              <work-record :trainNo="trainNo" :structureDetailId="FstructureDetailId"></work-record>
            </a-tab-pane>
            <a-tab-pane key="3" tab="更换记录">
              <replace-record :trainNo="trainNo" :structureDetailId="FstructureDetailId"></replace-record>
            </a-tab-pane>
            <a-tab-pane key="4" tab="故障记录">
              <fault-record :trainNo="trainNo" :structureDetailId="FstructureDetailId"></fault-record>
            </a-tab-pane>
            <!--            <a-tab-pane key="5" tab="测量记录">
              <measurement-record :trainNo="trainNo" :structureDetailId="FstructureDetailId"></measurement-record>
            </a-tab-pane>-->
            <a-tab-pane v-if="!this.type" key="6" tab="子部件">
              <child-info :trainNo="trainNo" :structureDetailId="FstructureDetailId"></child-info>
            </a-tab-pane>
            <a-tab-pane key="7" tab="作业表单">
              <related-form :trainNo="trainNo" :structureDetailId="FstructureDetailId"></related-form>
            </a-tab-pane>
            <a-tab-pane v-if="this.type" key="8" tab="整改项">
              <rectify-record :trainNo="trainNo"></rectify-record>
            </a-tab-pane>
            <a-tab-pane v-if="this.type" key="9" tab="开口项">
              <open-item-record :trainNo="trainNo"></open-item-record>
            </a-tab-pane>
          </a-tabs>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template> 

<script>
import ResBasicInfo from './ResBasicInfo'
import { treeLineTrains, listMaximoAssetChild } from '@api/tirosProductionApi'
import ChildInfo from './ChildInfo'
import TrainResBasicInfo from './TrainResBasicInfo'
import ReplaceRecord from '@views/tiros/production/resume/ReplaceRecord'
import FaultRecord from '@views/tiros/production/resume/FaultRecord'
import MeasurementRecord from '@views/tiros/production/resume/MeasurementRecord'
import WorkRecord from '@views/tiros/production/resume/WorkRecord'
import relatedForm from '@views/tiros/production/resume/relatedForm'
import RectifyRecord from '@views/tiros/production/resume/RectifyRecord'
import OpenItemRecord from '@views/tiros/production/resume/OpenItemRecord'

export default {
  name: 'ResumePage',
  components: {
    TrainResBasicInfo,
    ChildInfo,
    ResBasicInfo,
    ReplaceRecord,
    FaultRecord,
    MeasurementRecord,
    relatedForm,
    WorkRecord,
    RectifyRecord,
    OpenItemRecord,
  },
  data() {
    return {
      activeKey: '1',
      defaultSelectedKeys: [],
      treeData: [],
      FstructureDetailId: '',
      defaultKey: [],
      type: '',
      trainNo: '',
      queryParam: {
        structureDetailId: '',
      },
      isequ: '',
    }
  },
  created() {
    // 调用请求数据的方法
    this.treeLineTrains()
  },
  methods: {
    changeTab(activeKey) {
      this.activeKey = activeKey
    },
    treeLineTrains() {
      treeLineTrains({}).then((res) => {
        if (res.success) {
          this.treeData = res.result
          Array.from(this.treeData, (item) => {
            item.disabled = false
          })
        } else {
          this.$message.warn('获取线路车辆信息失败')
        }
      })
    },
    queryTreeData(treeNode) {
      return new Promise((resolve) => {
        if (treeNode.dataRef.type === 2) {
          this.trainNo = treeNode.dataRef.title
        }
        if (treeNode.dataRef.children && treeNode.dataRef.children.length > 0) {
          resolve()
          return
        }
        let parentId = null
        if (treeNode.dataRef.type === 3) {
          parentId = treeNode.dataRef.id_
        }
        let queryParams = { trainNo: this.trainNo, parentId: parentId }
        listMaximoAssetChild(queryParams).then((res) => {
          if (res.success) {
            treeNode.dataRef.children = res.result.map((r) => {
              return {
                id: this.trainNo + '$' + r.id,
                id_: r.id,
                title: r.name,
                longTitle: r.name,
                type: 3,
                trainNo: this.trainNo,
              }
            })
            this.treeData = [...this.treeData]
            resolve()
          }
        })
      })
    },
    selectPid(selectedKeys, e) {
      let typeData = e.node.dataRef.type
      if (typeData == 1) return
      this.FstructureDetailId = e.node.dataRef.title
      this.type = typeData === 2
      if (this.type) {
        this.trainNo = e.node.dataRef.title
      }
      if (typeData === 3) {
        this.FstructureDetailId = e.node.dataRef.id_
        this.trainNo = e.node.dataRef.trainNo
      }
    },
  },
}
</script>

<style lang="less">
.titleDiv {
  width: 100%;
  background: #eee;
  padding: 10px;
  text-align: center;
}

.leftPart {
  /*border: 1px solid #eee;
    border-radius: 10px;
    overflow-y: auto;
    margin-right: 20px;
    min-height: 70vh;*/

  .ant-card-body {
    padding: 0 0 24px 0;
    height: calc(100vh - 115px);
    overflow-y: auto;
  }
}

.centerDiv {
  width: 100%;
  text-align: center;
  padding: 10px 0;
}

.updateBtn {
  width: 90%;
}

.rightPart {
  /* border: 1px solid #eee;
   border-radius: 10px;
   overflow: hidden;
   padding: 10px;*/

  .ant-card-body {
    padding: 10px;
    height: calc(100vh - 115px);
  }
}
</style>