<template>
  <div class="container" style="height: calc(100vh - 213px) ; overflow: auto">
    <a-table
      size="middle"
      :columns="columns"
      :data-source="data"
      :rowKey="(record) => record.id"
      bordered
      :pagination="false"
    >
      <span slot="progress" slot-scope="progress"> {{ progress < 0 ? '无工单' : progress + '%' }} </span>
    </a-table>

    <!-- 工单列表弹窗 -->
    <a-drawer title="工单列表" width="44%" placement="right" :visible="visible" @close="onClose">
      <a-table
        size="middle"
        :columns="orderColumns"
        :data-source="orderList"
        :rowKey="(record, index) => index"
        bordered
        :pagination="false"
      >
      </a-table>
    </a-drawer>
  </div>
</template>
<script>
import { getPlanTrainStructOrdersList } from '@api/tirosProductionApi'

export default {
  name: 'VehicleStructureTable',
  props: {
    data: {
      type: Array,
      default: []
    }
  },
  data() {
    return {
      visible: false,
      orderList: []
    }
  },
  computed: {
    columns() {
      return [
        {
          title: '设备结构',
          dataIndex: 'name',
          key: 'name'
        },
        {
          title: '完成进度',
          dataIndex: 'progress',
          key: 'progress',
          scopedSlots: { customRender: 'progress' },
          customCell: this.renderTdBackground,
          align: 'center'
        },
        {
          title: '发现故障数',
          dataIndex: 'faultCount',
          key: 'faultCount',
          customCell: this.renderFaultBackground,
          align: 'center'
        },
        {
          title: '当前工单',
          key: 'currentOrderCount',
          dataIndex: 'currentOrderCount',
          align: 'center'
        },
        {
          title: '工单总数',
          key: 'orderCount',
          dataIndex: 'orderCount',
          customCell: this.renderEvent,
          align: 'center'
        }
      ]
    },
    orderColumns() {
      return [
        {
          title: '工单编号',
          dataIndex: 'orderCode',
          key: 'orderCode'
        },
        {
          title: '工单名称',
          dataIndex: 'orderName',
          key: 'orderName'
        },
        {
          title: '作业班组',
          dataIndex: 'groupName',
          key: 'groupName'
        },
        {
          title: '工单状态',
          dataIndex: 'orderStatus_dictText',
          key: 'orderStatus_dictText'
        },
        {
          title: '开始日期',
          dataIndex: 'actStart',
          key: 'actStart'
        },
        {
          title: '结束日期',
          dataIndex: 'actFinish',
          key: 'actFinish'
        }
      ]
    }
  },
  methods: {
    renderFaultBackground(record) {
      //   修改单元格颜色
      let backgroundColor = ''
      if (record.faultCount) {
        backgroundColor = '#ec808d'
      } else {
        backgroundColor = 'none'
      }
      return {
        style: {
          'background-color': backgroundColor + '!important'
        }
      }
    },
    renderTdBackground(record) {
      //   修改单元格颜色
      let backgroundColor = '#f2f2f2'
      let color = '#333'
      switch (record.progressStatus) {
        case 0:
          //未开始
          backgroundColor = '#f2f2f2'
          color = '#333';
          break
        case 1:
          // 正常(作业中)
          backgroundColor = '#0000ff'
          color = '#fff';
          break
        case 2:
          // 逾期(作业中)
          backgroundColor = '#ff0000'
          color = '#fff';
          break
        case 3:
          // 正常(完工)
          backgroundColor = '#0000ff'
          color = '#fff';
          break
        case 4:
          // 逾期(完工)
          backgroundColor = '#ff0000'
          color = '#fff';
          break
        case 5:
          // 5提前(完工)
          backgroundColor = '#008000'
          color = '#fff';
          break
        default:
          break
      }
      return {
        style: {
          'background-color': backgroundColor + '!important',
          'color': color + '!important',
        }
      }
    },
    renderEvent(record) {
      return {
        style: {
          cursor: 'pointer'
        },
        on: {
          // 事件
          click: (event) => {
            this.getPlanTrainStructOrdersList(record.orderIds)
          } // 点击
        }
      }
    },
    getPlanTrainStructOrdersList(orderIds) {
      // 车辆结构方式-查询工单列表
      getPlanTrainStructOrdersList(orderIds).then((res) => {
        if (res.success) {
          this.orderList = res.result
          this.visible = true
        }
      })
    },
    onClose() {
      this.visible = false
    }
  }
}
</script>