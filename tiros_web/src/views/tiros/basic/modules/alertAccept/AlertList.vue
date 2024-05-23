<template>
  <div id="AlertListContent">
    <div class="info-wrapper info-top-wrapper">
      <h4>已设置列表</h4>
      <!-- <div style="text-align:right;padding:0 0 5px 0">
        <a-button @click="handleDel(0)">删除</a-button>
      </div> -->
      <div class="tableHeight">
        <vxe-table
          border
          max-height="100%"
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
          :edit-config="{ trigger: 'manual', mode: 'row' }"
        >
          <vxe-table-column type="checkbox" width="5%"></vxe-table-column>
          <vxe-table-column field="dimension_dictText" title="接收维度" width="25%"></vxe-table-column>
          <vxe-table-column field="targetName" title="接收对象" width="30%"></vxe-table-column>
          <vxe-table-column field="createTime" title="设置时间" width="30%"></vxe-table-column>
          <vxe-table-column title="操作" width="10%">
            <template v-slot="{ row }">
              <a @click.stop="handleDel(row)">删除</a>
            </template>
          </vxe-table-column>
        </vxe-table>
      </div>
    </div>
  </div>
</template>

<script>
import { getAlertAcceptList, addAlertAccept, delAlertAccept } from '@/api/tirosApi'
export default {
  name: 'AlertList',
  props: ['value', 'toggle'],
  data() {
    return {
      alertType: 1,
      allAlign: 'center',
      tableData: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
    }
  },
  watch: {
    value: {
      // immediate: true,
      handler(id) {
        this.alertType = id
        this.findList()
      },
    },
    toggle: {
      // immediate: true,
      handler(t) {
        this.findList()
      },
    },
  },
  created() {},
  methods: {
    findList() {
      this.loading = true
      getAlertAcceptList({ alertType: this.alertType }).then((res) => {
        if (res.success) {
          this.loading = false
          this.tableData = res.result
        }
      })
    },
    handleDel(data) {
      if (data == 0) {
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        if (selectRecords.length) {
          this.$confirm({
            content: `是否删除选中${selectRecords.length}条数据？`,
            onOk: () => {
              var idsStr = selectRecords
                .map(function (obj, index) {
                  return obj.id
                })
                .join(',')
              this.delAlert(idsStr)
            },
          })
        } else {
          this.$message.error('尚未选中任何数据!')
        }
      } else {
        if (data.id) {
          this.$confirm({
            content: `是否删除该条数据？`,
            onOk: () => {
              this.delAlert(data.id)
            },
          })
        }
      }
    },
    delAlert(id) {
      delAlertAccept({ ids: id }).then((res) => {
        if (res.success) {
          this.$message.success(res.message)
          this.findList()
        }
      })
    },
  },
}
</script>

<style lang="less">
#AlertListContent {
  .info-wrapper {
    border: 1px solid #eee;
    position: relative;
    border-radius: 8px;
    padding: 10px;
    margin-bottom: 10px;
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
  .tableHeight {
    height: calc(100vh - 260px);
    // overflow-y: auto;
  }
}
</style>