<template>
  <vxe-table
    border="none"
    ref="listTable"
    align="center"
    :data="dataSource"
    show-overflow="tooltip"
    :edit-config="{trigger: 'manual', mode: 'row'}"
    max-height="100%"
  >
    <vxe-table-column title="故障标题" align="left" width="25%">
      <template v-slot="{ row }">
        <a @click="faultDetail(row)">{{row.faultDesc}}</a>
      </template>
    </vxe-table-column>
    <vxe-table-column field="reportGroupName" title="报告班组" width="22%"></vxe-table-column>
    <vxe-table-column field="reportTime" title="报告时间" width="20%"></vxe-table-column>
    <vxe-table-column field="sysName" title="所属系统" width="18%"></vxe-table-column>
    <vxe-table-column field="status_dictText" title="处理状态" width="15%"></vxe-table-column>
    <breakdown-detail-modal ref="breakdownDetail"></breakdown-detail-modal>
  </vxe-table>
</template>

<script>
import { getListLatest } from '@api/tirosKanbanApi'
// import { getBreakdownInfo } from '@api/tirosDispatchApi'
import BreakdownDetailModal from '@views/tiros/dispatch/breakdown/BreakdownDetailModal'

export default {
  name: 'NewFaults',
  components:{BreakdownDetailModal},
  props: {
    autoRefresh: {
      type: Boolean,
      default: true
    }
  },
  data () {
    return {
      dataSource:[],
      timer: null
    }
  },
  created () {
    this.loadData()
  },
  activated () {
    if (this.autoRefresh && !this.timer) {
      this.timer = setInterval(() => {
        this.loadData()
        // this.initGantt()
      }, 1000*60)
    }
  },
  methods: {
    loadData () {
      getListLatest().then((res) => {
        this.dataSource = res.result
      })
    },
    faultDetail(row){
      this.$refs.breakdownDetail.show(row.id)
      // getBreakdownInfo({ id: row.id }).then((res) => {
      //   let data = res.result
      //   this.$refs.breakdownDetail.show(data)
      // })
    },
  },
  // 离开路由之前执行
  beforeRouteLeave(to, from, next) {
    clearInterval(this.timer)
    next()
  }
}
</script>

<style scoped>

</style>