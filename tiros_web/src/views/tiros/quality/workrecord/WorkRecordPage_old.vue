<template>
  <div id="myWorkContent">
    <na-splitter :defaultSize="350" style="height: calc(100vh - 110px)">
      <div slot="left-panel" style="overflow-y: auto; overflow-x: hidden;padding-right: 2px">
        <div class="titleBar bg-primary-1">任务搜索</div>
        <div class="table-page-search-wrapper">
          <a-form layout="inline">
            <a-row :gutter="12">
              <a-col :md="12" :sm="24">
                <a-form-item>
                  <a-date-picker placeholder="作业日期" v-model="date" />
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item>
                  <div @click="openPlanModel">
                    <a-select
                      allow-clear
                      placeholder="列计划"
                      :open="false"
                      :showArrow="true"
                      v-model="queryParam.planName"
                      ref="myPlanSelect"
                      @change="changePlanSelect"
                    >
                      <a-icon slot="suffixIcon" type="ellipsis" />
                    </a-select>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :md="24" :sm="24">
                <a-form-item>
                  <a-input placeholder="请输入记录表名称" v-model="queryParam.name" allow-clear></a-input>
                </a-form-item>
              </a-col>
              <a-col :md="24" :sm="24">
                <a-form-item>
                <a-button type="dashed" class="border-primary-6"  @click="findList" block>查询</a-button>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <div class="titleBar bg-primary-1">任务列表</div>
        <div style="padding: 8px;">
        <a-button type="primary"  @click="confirmRecordTable" block>检查确认</a-button>
        </div>
        <div class="list-wrapper">
          <vxe-table
            border
            ref="listTable"
            :align="allAlign"
            :data="tableData"
            show-overflow="tooltip"
            :edit-config="{ trigger: 'manual', mode: 'row' }"
            :radio-config="{ trigger: 'row', highlight: true, reserve:true, checkRowKey:defaultSelectRecord }"
            @radio-change="radioChangeEvent"
          >
            <vxe-table-column type="radio" width="35"></vxe-table-column>
            <vxe-table-column field="name" title="记录名称" align="left"></vxe-table-column>
            <vxe-table-column field="progress" title="进度" align="left" width="65"></vxe-table-column>
            <vxe-table-column field="status" title="状态" align="left" width="65"></vxe-table-column>
          </vxe-table>
        </div>
      </div>
      <div slot="right-panel" style="padding-left: 5px;padding-right: 3px;">
        <RecordSheetTable ref="recordTable" v-show="curRecordData"></RecordSheetTable>
      </div>
    </na-splitter>
    <train-plan-list ref="planModalForm" @ok="onSelectPlan"></train-plan-list>
    <check-modal ref="checkModalForm" :title="'记录表确认'" @ok="onCheckRecord"></check-modal>
  </div>
</template>

<script>
import { getRecordTable, getRecordTableDetail } from '@/api/tirosQualityApi'
import TrainPlanList from '@/views/tiros/common/selectModules/TrainPlanList'
import RecordSheetTable from '@views/tiros/common/recordtable/RecordSheetTable'
import checkModal from './checkModal'
  import moment from 'moment'

  export default {
    name: 'WorkRecordPage',
    components: { TrainPlanList, checkModal, RecordSheetTable },
    data() {
      return {
        date: null,
        defaultSelectRecord: '',
        curRecordData: null,
        queryParam: {
          name: '',
          date: null,
          planId: '',
          planName: null
        },
        allAlign: 'center',
        tableData: [],
      }
    },
    created() {
      this.findList()
    },
    mounted() {
    },
    methods: {
      findList() {
        this.loading = true
        if(this.date){
          this.queryParam.date = moment(this.date).format('YYYY-MM-DD')
        }else {
          this.queryParam.date = ''
        }
        getRecordTable(this.queryParam).then((res) => {
          // this.totalResult = res.result.total
          this.loading = false
          this.tableData = res.result
        })
      },
      openPlanModel() {
        this.$refs.planModalForm.showModal()
      },
      onSelectPlan(data) {
        this.queryParam.planId = data[0].id
        this.queryParam.planName = data[0].planName
      },
      changePlanSelect (value) {
        if (!value) {
          this.queryParam.planId = ''
        }
      },
      confirmRecordTable () {
        let record = this.$refs.listTable.getRadioRecord()
        if (record) {
          if (record.progress !== 100) {
            this.$confirm({
              content: `该记录表还没有填写完成，是否继续进行检查确认？`,
              onOk: () => {
                this.$refs.checkModalForm.showModal(record.id)
              }
            })
          }
        } else {
          this.$message.error('请先选择一个记录表')
        }
      },
      onCheckRecord (data) {
        let record = this.$refs.listTable.getRadioRecord()
        this.loadRecordTable(record.id)
      },
      radioChangeEvent ({row}) {
        // 加载记录表明细
        this.loadRecordTable(row.id)
      },
      loadRecordTable (id) {
        let params = { recordId: id }
        getRecordTableDetail(params).then(res => {
          if(res.success){
            this.curRecordData = res.result
            console.log('curRecordData:', this.curRecordData)

            this.$refs.recordTable.loadData(this.curRecordData)
          } else {
            this.curRecordData = null
            this.$message.error('加载记录数据失败')
            console.error('加载记录数据失败', res.message)
          }
        }).catch(err=>{
          this.curRecordData = null
          this.$message.error('加载记录数据异常')
          console.error('加载记录数据失败', err)
        })
      }
    }
  }
</script>

<style lang="less">
  #myWorkContent {
    .table-page-search-wrapper{
      margin-top: 5px;
    }
    .list-wrapper {
      padding: 0px;
      height: calc(100vh - 435px);
      overflow-x: auto;
    }
    .titleBar {
      width: 100%;
      font-size: 14px;
      padding: 8px;
      text-align: center;
      font-weight: bold;
    }
  }
</style>