<template>
  <a-card :body-style="cardStyle">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="7" :sm="24">
            <a-form-item label="部件名称">
              <a-input placeholder="部件名称或编码" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId"  @change="changeLine"></line-select-list>
            </a-form-item>
          </a-col>

          <a-col :md="5" :sm="24">
            <a-form-item label="车号">
              <j-dict-select-seach-tag
                v-model="queryParam.trainNo"
                placeholder="请选择"
                :dictCode="dictTrainStr"
                @focus="handleSysFocus"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="8">
                <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                  <a-space>
                  <a-button @click="findList">查询</a-button>
                  <a-button @click.stop="handleEdit">设置质保日期</a-button>
                  </a-space>
                </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 255px)">
      <vxe-table
        border
        auto-resize
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 255px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="100"></vxe-table-column>
        <vxe-table-column field="trainNo" title="车号" width="100"></vxe-table-column>
        <vxe-table-column field="assetNo" align="left" header-align="center" title="部件编号" min-width="120"></vxe-table-column>
        <vxe-table-column field="assetName" title="委外部件" min-width="120" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="supplier" title="委外厂商" min-width="140" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="acceptanceGroup" title="验收人" width="100"></vxe-table-column>
        <vxe-table-column field="startDate" title="质保开始日期" width="200"></vxe-table-column>
        <vxe-table-column field="endDate" title="质保结束日期" width="200"></vxe-table-column>
        <vxe-table-column field="qualityDayStr" title="质保天数" width="80" header-align="center" align="right"></vxe-table-column>
        <vxe-table-column field="checkDate" title="功能验收日期" width="200"></vxe-table-column>

        <!-- <vxe-table-column field="surplusDays" title="剩余天数" width="80" header-align="center"
                          align="right"></vxe-table-column> -->
<!--        <vxe-table-column field="currentPosition" title="当前位置" min-width="120" header-align="center"
                          align="left"></vxe-table-column>-->
<!--        <vxe-table-column field="status" title="状态" width="80"></vxe-table-column>-->
        <vxe-table-column field="remark" title="备注" min-width="120" header-align="center"
                          align="left"></vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
    <quality-item-module ref="itemFrom" @ok="findList"></quality-item-module>
  </a-card>
</template>

<script>
import { getQuality } from '@api/tirosOutsourceApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import QualityItemModule from '@views/tiros/outsource/modules/QualityItemModule'

export default {
  name: 'RightQuality',
  props: ['assetTypeId'],
  components: { LineSelectList, QualityItemModule },
  data () {
    return {
      dictTrainStr: '',
      queryParam: {
        lineId: '',
        searchText: '',
        trainNo: '',
        assetTypeId: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      showEdit: false,
      cardStyle: {
        'padding': '10px',
        'height': 'calc(100vh - 120px)'
      }
    }
  },
  created () {
    this.findList()
  },
  watch: {
    assetTypeId: {
      immediate: false,
      handler (id) {
        this.queryParam.assetTypeId = id
        this.tableData = []
        this.findList()
      }
    }
  },
  methods: {
    changeLine(data, edit) {
      this.dictTrainStr = ''
      if (data) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + data + '|train_struct_id'
      }
    },
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
    },
    findList () {
      getQuality(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
      })
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleEdit () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let data;
        if (selectRecords.length == 1) {
          data = {
            detailId: selectRecords[0].id,
            checkDate: selectRecords[0].checkDate,
            startDate: selectRecords[0].startDate,
            endDate: selectRecords[0].endDate,
            dayCount: selectRecords[0].dayCount,
            remark: selectRecords[0].remark
          }
          
        } else {
          const ids = selectRecords.map((item) => {
            return  item.id //条件;
          }).join(',');
          data = {
            detailId: ids,
          }
        }
        this.$refs.itemFrom.edit(data)
      } else {
        this.$message.warn('尚未选中任何数据!')
      }

    }
  }
}
</script>

<style scoped lang="less">

</style>