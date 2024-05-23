<template>
  <a-modal
    :title="name"
    :width="'100%'"
    dialogClass="fullDialog no-footer"
    :visible="groupProgressVisible"
    :confirmLoading="confirmLoading"
    :destroyOnClose="true"
    :footer="null"
    @cancel="groupProgressVisible=false"
  >
    <a-row style="height: calc(100% - 0px)">
      <a-col class="leftPart" :xl="5" :lg="5" :md="24" :sm="24" :xs="24">
        <div class="divWrapper">
          <div class="titleDiv">
            {{ this.name }}
          </div>
          <div style="height:calc(100vh - 120px)">
            <vxe-table
              border="none"
              ref="listTable"
              row-id="id"
              height="auto"
              :align="allAlign"
              :data="tableData"
              :edit-config="{trigger: 'manual', mode: 'row'}"
              show-overflow="ellipsis"
              :radio-config="{ highlight: true, checkRowKey: defaultSelecteRow }"
              @radio-change="radioChangeEvent"
            >
              <vxe-table-column type="radio" width="40"></vxe-table-column>
              <vxe-table-column field="orderName" title="工单名称" width="50%"></vxe-table-column>
              <vxe-table-column field="unfinishedTaskCount" title="未完成任务数" width="30%"></vxe-table-column>

            </vxe-table>
          </div>
        </div>
      </a-col>
      <!--  -->
      <a-col class="rightPart" :xl="19" :lg="19" :md="24" :sm="24" :xs="24">
        <div>
          <div style="margin-top: 20px;margin-left: -70px">
            <pie-donut title="任务完成情况比例" :dataSource="tableDataPie" :height="300"></pie-donut>
          </div>
          <div style="margin-top: -80px;margin-bottom:60px;text-align: center">
            <div>应完成任务数 {{ this.taskCount }}</div>
            <div>已完成任务数 <span style="color: #4b7902">{{ this.finishedTaskCount }}</span></div>
            <div>未完成任务数 <span style="color: #d9001b">{{ this.unfinishedTaskCount }}</span></div>
          </div>
        </div>
        <div style="height: calc(100% - 366px)">
          <vxe-table
            border
            height="auto"
            ref="listTable"
            :align="allAlign"
            :data="tableDataD"
            show-overflow="tooltip"
            :edit-config="{trigger: 'manual', mode: 'row'}"
          >
            <vxe-table-column type="checkbox" width="40"></vxe-table-column>
            <vxe-table-column field="taskNo" title="任务编号" width="80"></vxe-table-column>
            <vxe-table-column field="taskName" title="任务名称" width="200" align="left"
                              header-align="center"></vxe-table-column>
            <vxe-table-column field="workTime" title="预计工时" width="100"></vxe-table-column>
            <vxe-table-column field="taskContent" title="任务要求" align="left" header-align="center"></vxe-table-column>
            <vxe-table-column field="taskStatus_dictText" title="状态" width="80"></vxe-table-column>

          </vxe-table>
        </div>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
import { getWorkGroupOrderInfo, getListOrderTask } from '../../../../api/tirosKanbanApi'
import PieDonut from '@/components/chart/PieDonut'

export default {
  name: 'ProgressWorkShopInfo',
  components: { PieDonut },
  data () {
    return {
      groupProgressVisible: false,
      confirmLoading: false,
      dataRes: [],
      tableData: [],
      tableDataPie: [],
      tableDataD: [],
      name: '工班进度',
      taskCount: '',
      unfinishedTaskCount: '',
      finishedTaskCount: '',
      allAlign: 'center',
      queryParam: {
        groupId: ''
      },
      queryParamD: {
        orderId: '',
        taskStatus: ''
      },
      defaultSelecteRow: ''
    }
  },
  mounted () {
    // 调用请求数据的方法
    if (this.groupProgressVisible) {
      this.findList()
    }
  },
  methods: {
    show (id) {
      this.queryParam.groupId = id
      this.findList()
      this.groupProgressVisible = true
    },
    findList () {
      if (!this.queryParam.groupId) {
        this.$message.warn('工班信息丢失，请重新打开！')
        this.groupProgressVisible = false
        return;
      }
      getWorkGroupOrderInfo(this.queryParam).then((res) => {
        this.dataRes = res.result
        this.tableData = res.result.orderList
        this.defaultSelecteRow = res.result.orderList[0].id
        this.getForm(this.defaultSelecteRow)
        this.tableDataPie = res.result.pieChartVOList
        this.taskCount = res.result.taskCount
        this.unfinishedTaskCount = res.result.unfinishedTaskCount
        this.finishedTaskCount = res.result.finishedTaskCount
        this.name = this.dataRes.groupName
      })

    },
    getForm (id) {
      this.queryParamD.orderId = id
      getListOrderTask(this.queryParamD).then((res) => {
        this.tableDataD = res.result
      })
    },
    radioChangeEvent ({ row }) {
      this.getForm(row.id)
    }
  }
}
</script>

<style scoped>
.titleDiv {
  width: 100%;
  background: #eee;
  padding: 10px;
  text-align: center;
}

.leftPart {

  overflow: hidden;
  min-height: 70vh;
  height: calc(100% - 0px);
}

.leftPart .divWrapper{
  border: 1px solid #eee;
  border-radius: 10px;
  height: 100%;
  margin-right: 20px;
}

.rightPart {
  border: 1px solid #eee;
  border-radius: 10px;
  overflow: hidden;
  padding: 10px;
  height: calc(100% - 0px);

}
</style>