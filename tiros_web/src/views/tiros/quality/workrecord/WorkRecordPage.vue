<template>
  <na-splitter :defaultSize="250">
    <div slot="left-panel">
        <a-tree
        :tree-data="treeNodes"
        @select="selectTreeNode"
        :load-data="onLoadTreeChild"
      />
    </div>
    <div slot="right-panel" style="width: calc(100% - 5px); padding-left: 5px;">
      <!-- 查询区域 -->
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="24">

            <a-col :md="4" :sm="24">
              <a-form-item label="线路">
                <line-select-list v-model="queryParam.lineId" @change="changeLine"></line-select-list>
              </a-form-item>
            </a-col>
            <a-col :md="4" :sm="24">
              <a-form-item label="车辆">
                <j-dict-select-seach-tag @focus="handleSysFocus" :triggerChange="true" v-model="queryParam.trainNo" :dictCode="dictCodeStr" />
              </a-form-item>
            </a-col>
            <a-col :md="4" :sm="24">
              <a-form-item label="修程">
                <j-dict-select-tag
                  v-model="queryParam.repairProgramId"
                  placeholder="请选择"
                  dictCode="bu_repair_program,name,id"
                />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="班组" :labelCol="{ span: 5 }" :wrapperCol="{ span: 19 }">
                <j-dict-select-tag v-model="queryParam.groupId" placeholder="请选择班组" :dictCode="dictGroupStr" />
              </a-form-item>
            </a-col>
            <a-col :md="5" :sm="24">
              <a-form-item label="类型">
                <j-dict-select-tag :triggerChange="true" v-model="queryParam.formType" dictCode="bu_work_form_type" />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="12">
              <a-form-item label="表单">
                <a-input placeholder="输入表单编码或者名称" v-model="queryParam.formSearchText" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="12">
              <a-form-item label="时间">
                <a-range-picker format="YYYY-MM-DD" :placeholder="['开始时间', '结束时间']" @change="onDateChange" :defaultPickerValue="defaultDateRange" />
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="列计划">
                <a-select
                  v-model="queryParam.planName"
                  placeholder="请选择列计划"
                  :open="false"
                  :showArrow="true"
                  @focus="openTrainPlanModal"
                  ref="planSelect"
                >
                  <div slot="suffixIcon">
                    <a-icon
                      v-if="queryParam.planId"
                      @click.stop="clearValue"
                      type="close-circle"
                      style="padding-right: 3px"
                    />
                    <a-icon v-else type="ellipsis" />
                  </div>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="4" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <!-- <a-button :disabled="!btnStatus.edit" @click="print">导出打印</a-button> -->
              </a-space>
            </span>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <div style="height: calc(100vh - 268px)">
        <vxe-table
          border
          :align="allAlign"
          ref="listTable"
          style="height: calc(100vh - 268px)"
          :data="tableData"
          show-overflow="tooltip"
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          :edit-config="{ trigger: 'manual', mode: 'row' }"
          @checkbox-change="btnStatus.update()"
          @checkbox-all="btnStatus.update()"
        >
          <vxe-table-column type="checkbox" width="40"></vxe-table-column>
          <vxe-table-column field="lineName" title="线路" width="80"></vxe-table-column>
          <vxe-table-column
            field="trainNo"
            title="车号"
            width="80"
            header-align="center"
            align="center"
          ></vxe-table-column>
          <vxe-table-column field="formType_dictText" title="表单类型" width="100"></vxe-table-column>
          <vxe-table-column field="title" title="表单名称" min-width="360" align="left" header-align="center">
            <template v-slot="{ row }">
              <a href="javascript:;" @click.stop="jumpFormInfo(row)">{{ row.title }}</a>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="status_dictText"
            title="是否已填"
            min-width="100"
            header-align="center"
            align="center"
          ></vxe-table-column>
          <vxe-table-column field="reguName" title="所属规程" width="120">
            <template v-slot="{ row }">
              {{ row.reguName }}
            </template>
            <template v-slot:edit="{ row }">
              <a-input placeholder="请选择表单" disabled v-model="row.reguName" />
            </template>
          </vxe-table-column>
          <vxe-table-column field="reguVersion" title="规程版本" width="120">
            <template v-slot="{ row }">
              {{ row.reguVersion }}
            </template>
            <template v-slot:edit="{ row }">
              <a-input placeholder="请选择表单" disabled v-model="row.reguVersion" />
            </template>
          </vxe-table-column>
          <vxe-table-column field="orderName" title="所属工单" width="260" align="left" header-align="center">
            <template v-slot="{ row }">
              <a href="javascript:;" @click.stop="viewOrder(row)">{{ row.orderName }}</a>
            </template>
          </vxe-table-column>
          <vxe-table-column
            field="orderTime"
            title="工单日期"
            min-width="120"
            header-align="center"
            align="center"
          ></vxe-table-column>
          <vxe-table-column
            field="planName"
            title="所属计划"
            align="left"
            header-align="center"
            width="180"
          ></vxe-table-column>
          <vxe-table-column
            field="repairProgramName"
            align="center"
            header-align="center"
            title="修程"
            width="80"
          ></vxe-table-column>
          <vxe-table-column
            field="trainStructName"
            align="left"
            header-align="center"
            title="关联设备"
            width="180"
          ></vxe-table-column>
        </vxe-table>
        <vxe-pager
          perfect
          :current-page.sync="queryParam.pageNo"
          :page-size.sync="queryParam.pageSize"
          :total="totalResult"
          :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
          @page-change="handlePageChange"
        ></vxe-pager>
        <view-work-order-modal ref="viewOrderModal"></view-work-order-modal>
        <ViewWorkForm ref="viewWorkForm" @ok="loadData()"></ViewWorkForm>
        <WorkRecordPrint :luckySheetId="'lsPrint1'" ref="workRecordPrint"></WorkRecordPrint>
      </div>
      <TrainPlanList ref="trainPlanModal" @ok="onSelectPlan"></TrainPlanList>
    </div>
  </na-splitter>
</template>


<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { pageFormInstanceRecord } from '@/api/tirosQualityApi'
import ViewWorkOrderModal from '@views/tiros/dispatch/workOrder/ViewWorkOrderModal'
import ViewWorkForm from '@views/tiros/dispatch/workOrder/ViewWorkForm'
import WorkRecordPrint from './WorkRecordPrint'
import TrainPlanList from '@views/tiros/common/selectModules/TrainPlanList'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'
import { getLineList, getTrainInfoList } from '@api/tirosApi'
import { ajaxGetDictItems } from '@api/api'
import { randomUUID } from '@/utils/util'

export default {
  name: 'List',
  components: { LineSelectList, ViewWorkOrderModal, ViewWorkForm, WorkRecordPrint, TrainPlanList },
  data() {
    return {
      dictCodeStr: '',
      queryParam: {
        planId: '',
        planName: '',
        lineId: '',
        trainNo: '',
        repairProgramId: '',
        groupId: '',
        formType: '',
        isPage: false,
        startDate: '',
        endDate: '',
        formSearchText: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      treeNodes: [],
      programs:[],
      groups: [],
      formTypes: [],
      btnStatus: new TableBtnUtil(this, 'listTable'),
      dictGroupStr:
        'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
    }
  },

  created() {
    this.findList()
    this.buildConditionTree()
  },
  methods: {
    openTrainPlanModal() {
      this.$refs.trainPlanModal.showModal()
      this.$refs.planSelect.blur()
    },
    clearValue() {
      this.queryParam.planId = ''
      this.queryParam.planName = ''
      this.queryParam.trainNo = ''
      this.queryParam.lineId = ''
    },
    onSelectPlan(data) {
      data.forEach((element) => {
        this.queryParam.trainNo = element.trainNo;
        this.queryParam.lineId = element.lineId;
        this.queryParam.planId = element.id
        this.queryParam.planName = element.planName
      })
    },
    handleSysFocus() {
      if (!this.queryParam.lineId) this.$message.warn('请先选择线路!')
    },
    changeLine(data) {
      if (data) {
        this.dictCodeStr = 'bu_train_info,train_no,train_no,line_id=' + data
      } else {
        this.dictCodeStr = ''
      }
      this.lineId = data
    },
    onDateChange(value, dateString) {
      this.queryParam.startDate = dateString[0]
      this.queryParam.endDate = dateString[1]
    },
    // 构造条件树
    async buildConditionTree () {
      // 线号-车号-修程-班组-表单分类

      // 获取线路
      let lines = await new Promise(resolve => {
        getLineList().then(res=>{
          if (res.success) {
            resolve(res.result)
          } else {
            resolve([])
          }
        })
      })

      // 获取车辆
      let trains=await new Promise(resolve => {
        getTrainInfoList({lineId:'', pageNo:1,pageSize:10000}).then(res=>{
          if(res.success){
            resolve(res.result.records)
          } else {
            resolve([])
          }
        })
      })

      // 获取修程
      this.programs = []
      ajaxGetDictItems('bu_repair_program,name,id', null).then((res) => {
        if (res.success && res.result) {
          res.result.forEach(t => {
            this.programs.push({
              key: t.value,
              value: t.value,
              title: t.text,
              type: 'pro',
              isLeaf: false,
              children: null
            })
          })
          // 排序
          this.programs.sort((p1,p2)=>{
            if (p1.key < p2.key) {
              return -1
            }
            if (p1.key === p2.key) {
              return 0
            }
            return 1
          })
        }
      })

      // 获取班组信息
      this.groups = []
      ajaxGetDictItems(this.dictGroupStr, null).then((res) => {
        if (res.success && res.result) {
          res.result.forEach(t => {
            this.groups.push({
              key: t.value,
              value: t.value,
              title: t.text,
              type: 'group',
              isLeaf: false,
              children: null
            })
          })
          // 排序
        }
      })

      // 获取表单类型 bu_work_form_type
      this.formTypes=[]
      ajaxGetDictItems('bu_work_form_type', null).then((res) => {
        if (res.success && res.result) {
          res.result.forEach(t => {
            this.formTypes.push({
              key: t.value,
              value: t.value,
              title: t.text,
              type: 'form',
              isLeaf: true,
              children: null
            })
          })
        }
      })

      // 构造树节点
      this.treeNodes = []
      lines.forEach(line=>{
        let lineNode={
          key: line.lineId,
          value: line.lineId,
          title: line.lineName,
          type: 'line',
          isLeaf: false,
          children:[],
        }

        if (trains && trains.length > 0) {
          let lineTrain = trains.filter(t => t.lineId === line.lineId)
          if (lineTrain && lineTrain.length > 0) {
            lineTrain.forEach(t => {
              let trainNode = {
                key: t.id,
                value: t.trainNo,
                title: t.trainNo,
                type: 'train',
                isLeaf: false,
                children: null
              }

              lineNode.children.push(trainNode)
            })
          } else {
            lineNode.isLeaf = true
            lineNode.children = null
          }
        } else {
          lineNode.isLeaf = true
          lineNode.children = null
        }


        this.treeNodes.push(lineNode)
      })
    },
    selectTreeNode (selectedKeys, info) {
      Object.assign(this.queryParam, {
        lineId: '',
        trainNo: '',
        repairProgramId: '',
        groupId: '',
        formType: '',
        planId: '',
        planName: '',
      })
      this.setQueryParam(info.node)
      this.$forceUpdate()
      this.findList()
    },
    setQueryParam (node) {
      if (node.dataRef) {
        let nodeType=node.dataRef.type
        let key = node.dataRef.value
        switch (nodeType) {
        case 'line':
          this.queryParam.lineId = key
          break
        case 'train':
          this.queryParam.trainNo = key
          break
        case 'pro':
          this.queryParam.repairProgramId = key
          break
        case 'group':
          this.queryParam.groupId = key
          break
        case 'form':
          this.queryParam.formType = key
          break
        }

        if (node.$parent && node.$parent.dataRef) {
          this.setQueryParam(node.$parent)
        }
      }
    },
    onLoadTreeChild (treeNode) {
      return new Promise(resolve => {
        if (treeNode.dataRef.children) {
          resolve()
        } else {
          this.getTreeNodeChild(treeNode)
          resolve()
        }
      })
    },
    getTreeNodeChild (node) {
      // node.dataRef.isLeaf = true
      let parentType = node.dataRef.type
      if (parentType === 'train') {
        // 加载修程
        node.dataRef.isLeaf = false
        node.dataRef.children = []
        this.programs.forEach(item => {
          let child = Object.assign({}, item)
          child.key = randomUUID()
          node.dataRef.children.push(child)
        })
      }

      if (parentType === 'pro') {
        // 加载班组
        node.dataRef.isLeaf = false
        node.dataRef.children = []
        this.groups.forEach(item => {
          let child = Object.assign({}, item)
          child.key = randomUUID()
          node.dataRef.children.push(child)
        })
      }

      if (parentType === 'group') {
        // 加载表单类型
        node.dataRef.isLeaf = false
        node.dataRef.children = []
        this.formTypes.forEach(item => {
          let child = Object.assign({}, item)
          child.key = randomUUID()
          node.dataRef.children.push(child)
        })
      }
    },
    findList() {
      pageFormInstanceRecord(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
        this.btnStatus.update()
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    jumpFormInfo(row) {
      let obj = {
        id: row.task2InstId,
        planFormId: row.planFormId,
        instType: row.formType,
        formObjId: row.formObjId,
        formInstId: row.id,
        workRecordType: row.workRecordType,
        formName: row.title,
      }
      this.$refs.viewWorkForm.showModal(obj, row.trainNo)
      // if (row.formType === 1) {
      //   let obj = {
      //     id: row.formObjId,
      //     instType: row.formType,
      //     formInstId: row.id,
      //     formName: row.name,
      //   }
      //   console.log(obj)
      //   this.$refs.viewWorkForm.showModal(obj, row.trainNo)
      // } else {
      //   let obj = {
      //     id: row.task2InstId,
      //     instType: row.formType,
      //     formInstId: row.formObjId,
      //     formName: row.name,
      //   }
      //   console.log(obj)
      //   this.$refs.viewWorkForm.showModal(obj, row.trainNo)
      // }
    },
    viewOrder(row) {
      this.$refs.viewOrderModal.showModal(row.orderId)
    },
    loadData() {
      this.findList()
    },
    print() {
      let selectRecord = this.$refs.listTable.getCheckboxRecords()[0]
      console.log(selectRecord)
      this.$refs.workRecordPrint.print({
        id: selectRecord.id,
        task2InstId: selectRecord.task2InstId,
        instType: selectRecord.formType,
        formInstId: selectRecord.formObjId,
        formObjId: selectRecord.formObjId,
        formName: selectRecord.name,
      })
    },
  },
}
</script>