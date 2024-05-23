<template>
  <!--  车辆结构选择 -->
  <a-modal
    width="90%"
    title="车辆设备选择"
    centered
    :visible="visible"
    :bodyStyle="{ height: '80vh' }"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="8">
          <a-col :md="5" :sm="24">
            <a-form-item label="名称" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-input placeholder="名称或编码" v-model="queryParam.searchText" allow-clear></a-input>
            </a-form-item>
          </a-col>
<!--          <a-col :md="5" :sm="24">
            <a-form-item label="状态" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <j-dict-select-tag v-model="queryParam.status" dictCode="bu_enable" />
            </a-form-item>
          </a-col>-->
          <a-col :md="9" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div class="tableHeight" style="height: calc(80vh - 85px)">
      <vxe-table
        border
        row-id="id"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        max-height="100%"
        style="height: calc(80vh - 85px)"
        :radio-config="{trigger: !multiple ? 'row' : 'default',highlight: true,checkMethod: checkRadioMethod}"
        :checkbox-config="{trigger: multiple ? 'row' : 'default', highlight: true,checkStrictly: true,checkMethod: checkRadioMethod}"
        :tree-config="{ lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod }"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="name" title="名称" tree-node align="left" header-align="center"></vxe-table-column>
        <vxe-table-column align="left" header-align="center" field="code" title="编码" width="150"></vxe-table-column>
        <vxe-table-column field="structType_dictText" title="类型" width="120"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" width="100"></vxe-table-column>
        <vxe-table-column field="placeId" title="位置编码" width="150"></vxe-table-column>
        <vxe-table-column field="placeDesc" title="位置描述" width="200" align="left"
                          header-align="center"></vxe-table-column>
      </vxe-table>
    </div>
  </a-modal>
</template>

<script>
import { getTrainStructDetailList } from '@api/tirosApi'

export default {
  name: 'TrainStructureList',
  props: {
    multiple: {
      type: Boolean,
      default: false,
    },
    // 1 系统 2子系统 3 部件 4 子部件
    canSelectType: {
      type: Array,
      default: ()=>{
        return [1,2,3,4]}
    },
    lineId: {
      type: String,
      default: '',
    },
  },
  data () {
    return {
      visible: false,
      queryParam: {
        id: '',
        searchText: '',
        status: null,
        trainStructId: '',

      },
      selectParent: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 5 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 19 }
      },
      allAlign: 'center',
      tableData: [],
      trainStructId:''
    }
  },
  methods: {
    showModal(structureId) {
      this.queryParam.trainStructId = structureId
      this.queryParam.lineId=this.lineId
      this.trainStructId=structureId
      this.visible = true
      this.findList()
    },
    // 获取车辆结构明细
    findList () {
      getTrainStructDetailList(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = res.result
        }
      })
    },
    // 加载子记录
    loadChildrenMethod ({ row }) {
      let param = {
        trainStructId: this.trainStructId,
        id: row.id
      }
      return new Promise((resolve) => {
        getTrainStructDetailList(param).then((res) => {
          if (res.success) {
            resolve(res.result)
          } else {
            this.$message.error(`${res.msg}`)
          }
        })
      })
    },
    handleOk () {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if(this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      this.$emit('ok', selectRecords)
      this.close()
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
    },
    checkRadioMethod ({ row }) {
      //   1 系统 2子系统 3 部件 4 子部件
      if (this.canSelectType.indexOf(row.structType) > -1) {
        return true
      } else {
        return false
      }
    }
  }
}
</script>

<style scoped>

</style>