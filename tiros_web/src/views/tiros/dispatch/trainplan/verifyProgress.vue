<template>
  <a-modal
    title='进度核实'
    :width="'100%'"
    dialogClass='fullDialog no-footer'
    :visible='visible'
    :okText='null'
    @cancel='handleCancel'
    @ok='saveOutInInfo'
    :footer='null'
    :destroyOnClose='true'
  >
    <div style='padding: 20px'>
      <a-row>
        <a-col :span='3'>
          <a-statistic
            title='任务总数'
            :value='total'
            style='margin-right: 50px'
          />
        </a-col>
        <a-col :span='3'>
          <a-statistic
            title='已完成'
            :value-style="{ color: '#3f8600' }"
            :value='finished'
            style='margin-right: 50px'
          />
        </a-col>
        <a-col :span='3'>
          <a-statistic
            title='未完成'
            :value-style="{ color: '#cf1322' }"
            :value='unFinished'
            style='margin-right: 50px'
          />
        </a-col>
      </a-row>
    </div>
    <div style='padding: 0 20px 20px 20px'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline' @keyup.enter.native='getListData'>
          <a-row :gutter='24'>
            <a-col :md='4' :sm='24'>
              <a-form-item label='所属班组' class='a-form-item-width-gy'>
                <j-dict-select-tag v-model='groupId' :dictCode="'bu_mtr_workshop_group,group_name,id'" />
              </a-form-item>
            </a-col>
            <a-col :md='4' :sm='24'>
              <a-form-item label='任务状态' class='a-form-item-width-gy'>
                <j-dict-select-tag v-model='status' :dictCode="'bu_task_status'" />
              </a-form-item>
            </a-col>
            <a-col :md='4' :sm='24'>
              <a-button
                @click='getListData'
                type='primary'
              >
                筛选
              </a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <div style='padding-bottom: 20px'>
        <a-button
          @click='okEvent'
          type='primary'
          :loading='loading'
          :disabled='selectRecords.length < 1'
        >
          确认完成
        </a-button>
      </div>
      <div style='height: calc(100vh - 216px)'>
        <vxe-table
          key='inTable'
          border
          height='auto'
          ref='listTable'
          :align="'center'"
          :data='unFinishedList'
          :edit-config="{ trigger: 'manual', mode: 'row' }"
          show-overflow='tooltip'
          @checkbox-change='rowSelectChange'
          @checkbox-all='rowSelectChange'
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        >
          <vxe-table-column type='checkbox' width='40'></vxe-table-column>
          <vxe-table-column
            field='taskName'
            title='任务名称'
            header-align='center'
            align='left'
            width='200'
          ></vxe-table-column>
          <vxe-table-column
            field='startTime'
            title='开始时间'
            min-width='140'
          ></vxe-table-column>
          <vxe-table-column
            field='finishTime'
            title='结束时间'
            min-width='140'
          ></vxe-table-column>
          <vxe-table-column
            field='dayIndex'
            title='第几天'
            header-align='center'
            align='right'
            width='100'
          ></vxe-table-column>
          <vxe-table-column
            field='workGroupName'
            title='所属班组'
            header-align='center'
            align='left'
            width='120'
          ></vxe-table-column>
          <vxe-table-column
            field='status_dictText'
            title='任务状态'
            min-width='80'
          ></vxe-table-column>
          <vxe-table-column
            field='genOrder_dictText'
            title='需生产工单?'
            min-width='100'
          ></vxe-table-column>
          <vxe-table-column
            field='hasGen_dictText'
            title='已生成工单?'
            min-width='100'
          ></vxe-table-column>
          <vxe-table-column
            field='orderCode'
            title='工单号'
            min-width='120'
          ></vxe-table-column>
          <vxe-table-column
            field='orderName'
            title='工单名称'
            min-width='160'
            header-align='center'
            align='left'
          ></vxe-table-column>
          <vxe-table-column
            field='orderStatus_dictText'
            title='工单状态'
            min-width='100'
            header-align='center'
            align='left'
          ></vxe-table-column>
          <vxe-table-column
            field='orderTaskName'
            title='工单任务名称'
            min-width='160'
            header-align='center'
            align='left'
          ></vxe-table-column>
          <vxe-table-column
            field='orderTaskStatus_dictText'
            title='工单任务状态'
            min-width='100'
            header-align='center'
            align='left'
          ></vxe-table-column>
        </vxe-table>
      </div>
    </div>
  </a-modal>
</template>

<script>
import { getUnFinishList, unFinishSetfinish } from '@api/tirosDispatchApi'

export default {
  data() {
    return {
      visible: false,
      planId: '',
      loading: false,
      groupId: '',
      status: null,
      selectRecords: [],
      finished: 0,
      total: 0,
      unFinished: 0,
      unFinishedList: [],
      brush: false
    }
  },
  methods: {
    okEvent() {
      const ids = this.selectRecords
        .map((item) => {
          return item.id //条件;
        })
        .join(',')
      this.$confirm({
        content: `是确认已完成选中${this.selectRecords.length}条任务？`,
        onOk: () => {
          this.loading = true
          return unFinishSetfinish(`planId=${this.planId}&planTaskIds=${ids}`).then(
            (res) => {
              this.loading = false
              if (res.success) {
                this.$message.success(res.message)
                this.brush = true
                this.getListData()
              } else {
                this.$message.warning(res.message)
              }
            }
          )
        }
      })
    },
    rowSelectChange() {
      this.selectRecords = this.$refs.listTable.getCheckboxRecords()
    },
    show(data) {
      Object.assign(this.$data, this.$options.data())
      this.visible = true
      this.planId = data.id
      this.getListData()
    },
    getListData() {
      getUnFinishList({ planId: this.planId, groupId: this.groupId, status: this.status }).then((res) => {
        if (res.success) {
          this.selectRecords = []
          this.finished = res.result.finished
          this.total = res.result.total
          this.unFinished = res.result.unFinished
          this.unFinishedList = res.result.unFinishedList
        }
      })
    },
    saveOutInInfo() {
    },
    // 关闭
    handleCancel() {
      this.$emit('close', this.brush)
      this.visible = false
    }
  }
}
</script>

<style lang='less' scoped>
/deep/ .ant-statistic {
  display: flex;
  align-items: center;

  .ant-statistic-title {
    margin-bottom: 0;
    margin-right: 10px;
  }
}
</style>
