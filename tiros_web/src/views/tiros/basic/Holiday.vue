<template>
  <div>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="年份">
              <a-date-picker
                mode="year"
                placeholder="请选择年份"
                format="YYYY"
                v-model="year"
                :open="yearPickShow"
                @panelChange="handlePanelChange"
                @openChange="handleOpenChange"
                style="width:100%"
              />
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="节日">
              <a-input placeholder="请输入节日名称" v-model="queryParam.name" allowClear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd" v-has="'holiday:add'">新增</a-button>
                <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
                <a-button :disabled="records.length < 1" @click="deleteRecord" v-has="'holiday:delete'">删除</a-button>
                <a-button  @click="addFromNet" v-has="'holiday:sync'" :loading="loading">从网络获取</a-button>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 225px)">
      <vxe-table
        border
        auto-resize
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 225px)"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="year" title="年份" width="10%"></vxe-table-column>
        <vxe-table-column field="name" title="节日名称" width="10%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="startTime" title="开始日期" :formatter="fotmatDate" width="15%"></vxe-table-column>
        <vxe-table-column field="end" title="结束日期" :formatter="fotmatDate" width="15%"></vxe-table-column>
        <vxe-table-column field="days" title="共计天数" width="10%" header-align="center" align="right"></vxe-table-column>
        <vxe-table-column field="work_dictText" title="类型" width="10%"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" header-align="center" align="left"></vxe-table-column>
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
    <holiday-item-modal ref="modalForm" @ok="loadData()"></holiday-item-modal>
  </div>
</template>

<script>
import moment from 'moment'
import 'moment/locale/zh-cn'
import HolidayItemModal from './modules/holiday/HolidayItemModal'
import { getHolidayList, addHolidayByNet, delHoliday } from '@/api/tirosApi'
import { getAction } from '@api/manage'

export default {
  components: { HolidayItemModal },
  data () {
    return {
      records:[],
      year: null,
      queryParam: {
        name: '',
        year: null,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      yearPick: null, //年选择器的值
      yearPickShow: false, //年选择器的显示隐藏
      tableData: [],
      loading:false
    }
  },
  created () {
    this.findList()
  },
  methods: {
    checkboxChange(e){
      this.records = e.records;
    },
    findList () {
      this.loading = true
      if (this.year) {
        this.queryParam.year = moment(this.year).format('YYYY')
      } else {
        this.queryParam.year = ''
      }
      getHolidayList(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.records = []
          this.tableData = res.result.records
        }
      }).catch((err) => {
         this.loading = false
      });
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    addFromNet () {
      this.$confirm({
        content: `此操作将覆盖原有节假日数据，是否继续？`,
        onOk: () => {
          this.loading=true
          getAction('http://timor.tech/api/holiday/year', '').then((res) => {
            let data = res
            if (data.code == 0) {
              addHolidayByNet(res).then((response) => {
                if (response.success) {
                  this.$message.success(response.message)
                  this.findList()
                } else {
                  this.$message.error(response.message)
                }
              }).finally(()=>this.loading=false)
            } else {
              this.$message.error('网络服务器异常,请联系管理员!')
              this.loading=false
            }
          })
        }
      })
    },
    fotmatDate ({ cellValue }) {
      return (cellValue =  cellValue ? cellValue.substring(cellValue.indexOf('-') + 1) : '')
    },
    loadData () {
      this.findList()
    },
    handleAdd () {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit (record) {
      this.$refs.modalForm.edit(record)
      this.$refs.modalForm.title = '编辑'
    },
    deleteRecord () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delHoliday({ ids: idsStr }).then((res) => {
              if (res.success) {
                this.findList()
                this.$message.success(res.message)
              } else {
                this.$message.error(res.message)
              }
            })

          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    // 弹出日历和关闭日历的回调
    handleOpenChange (status) {
      this.yearPickShow = status
    },
    // 得到年份选择器的值
    handlePanelChange (value) {
      this.year = value
      this.queryParam.year = moment(value).format('YYYY')
      this.yearPickShow = false
    }
  }
}
</script>

<style scoped>
#num {
  display: inline-block;
  position: absolute;
  background: #fff849;
  padding: 0 5px;
  top: -10px;
  font-weight: 600;
}
</style>