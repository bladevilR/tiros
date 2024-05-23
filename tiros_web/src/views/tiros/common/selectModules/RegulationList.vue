<template>
  <a-modal
    width='90%'
    title='选择规程'
    centered
    :visible='visible'
    :bodyStyle="{ height: '70vh' }"
    :confirmLoading='confirmLoading'
    @ok='handleOk'
    @cancel='handleCancel'
    cancelText='关闭'
    :destroyOnClose='true'
  >

    <!-- <a-card :bordered="false"> -->
    <a-row>
      <a-col :md='4' :sm='24'
             style='border-right: 1px dotted #D3E1F1; height: calc(70vh - 20px);margin-right: 8px;padding: 0px;'>
        <!--<a-tree
          v-if="treeData.length"
          defaultExpandAll
          :tree-data="treeData"
          :default-selected-keys="defaultKey"
          :dropdownStyle="{ maxHeight: '200px', overflow: 'auto' }"
          :replaceFields="{ title: 'name', key: 'id' }"
          @select="onSelect"
        />-->
        <a-menu mode='inline' @click='handleMenuClick' :default-selected-keys='defaultKey'>
          <a-menu-item v-for='menu in treeData' :key='menu.id'>
            <a-icon type='container' />
            <span>{{ menu.name }}</span>
          </a-menu-item>
        </a-menu>
      </a-col>
      <a-col :md='19' :sm='24'>
        <div v-if='this.planId' class="table-page-search-wrapper">
          <a-form
            layout="inline"
            @keyup.enter.native="findList"
            :label-col="formItemLayout.labelCol"
            :wrapper-col="formItemLayout.wrapperCol"
          >
            <a-row :gutter="24">
              <a-col :md="4" :sm="24">
                <a-form-item label="是否已关联">
                  <j-dict-select-tag
                    v-model="tpPlanRelated"
                    dictCode="bu_state"
                    allow-clear
                  />
                </a-form-item>
              </a-col>
              <a-col :md="3" :sm="8">
                <a-form-item>
                  <a-button @click="findList">查询</a-button>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <div style='height: calc(70vh - 20px);'>
          <vxe-table
            border
            row-id='id'
            ref='listTable'
            max-height='100%'
            style='height: calc(70vh - 20px);'
            :align='allAlign'
            :data='tableData'
            show-overflow='tooltip'
            :edit-config="{ trigger: 'manual', mode: 'row' }"
            :radio-config="!multiple ? {trigger: 'row', checkMethod: checkRadioMethod} : {}"
            :checkbox-config="multiple ? {checkStrictly: checkStrictly, trigger: 'row', highlight: true } : {}"
            :tree-config="{ lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod }"
          >
            <vxe-table-column v-if='multiple' type='checkbox' width='40' fixed='left'></vxe-table-column>
            <vxe-table-column v-else type='radio' width='40'></vxe-table-column>
            <vxe-table-column field='title' title='名称' tree-node align='left'></vxe-table-column>
            <vxe-table-column field='no' title='序号' width='80'></vxe-table-column>
            <vxe-table-column field='type_dictText' title='类型' width='100'></vxe-table-column>
            <vxe-table-column field='method_dictText' title='维保手段' width='120'></vxe-table-column>
            <vxe-table-column field='qualityLevel' title='质保等级' width='80'></vxe-table-column>
            <!-- <vxe-table-column field="outsource_dictText" title="是否委外" width="80"></vxe-table-column> -->
            <vxe-table-column field='safeNotice' title='安全提示' width='160'></vxe-table-column>
            <vxe-table-column field='remark' title='备注' width='120'></vxe-table-column>
            <vxe-table-column v-if='this.planId' field='tpPlanRelatedInfo' title='模板已关联' width='120'>
              <template v-slot='{ row }'>
                <div :style="{ backgroundColor: statusColor[row.tpPlanRelated + ''], borderRadius: '4px' }">
                  {{ row.tpPlanRelatedInfo }}
                </div>
              </template>
            </vxe-table-column>
          </vxe-table>
        </div>
      </a-col>
    </a-row>
    <!-- </a-card> -->
  </a-modal>
</template>

<script>
import { getRegu, getReguDeteil, getReguList } from '@/api/tirosApi'

export default {
  name: 'RegulationList',
  props: {
    multiple: {
      type: Boolean,
      default: false
    },
    checkStrictly: {
      type: Boolean,
      default: true
    },
    reguId: {
      type: String,
      default: ''
    },
    planId: {
      type: String,
      default: ''
    },
    trainTypeId: {
      type: String,
      default: ''
    },
    onlyProject: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      statusColor: {
        0: '#dedede',
        1: '#bad795',
      },
      treeData: [],
      curReguId: '',
      tableData: [],
      allAlign: 'center',
      defaultKey: [],
      tpPlanRelated: '',
      formItemLayout: {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 18 },
        },
      },
    }
  },
  methods: {
    showModal() {
      this.visible = true
      this.getTreeList()
    },
    // 只能选择作业项
    checkRadioMethod({ row }) {
      //   1 作业分类 2 作业项目
      if (this.onlyProject) {
        return row.type === 1
      } else {
        return true
      }
    },
    getTreeList() {
      if (!this.reguId) {
        getReguList({ trainTypeId: this.trainTypeId }).then((res) => {
          if (res.success) {
            this.defaultKey[0] = res.result[0].id
            this.curReguId = res.result[0].id
            this.treeData = res.result
            // console.log('treeData:', this.treeData)
            this.getReguDetailList(this.curReguId)
          } else {
            this.$message.error('获取规程信息失败')
            console.error('获取规程信息失败：', res.message)
          }
        }).catch(err => {
          this.$message.error('获取规程信息异常')
          console.error('获取规程信息异常：', err)
        })
      } else {
        // 获取指定规程
        getRegu(this.reguId).then(res => {
          if (res.success) {
            this.curReguId = this.reguId
            this.treeData = [res.result]
            this.getReguDetailList(this.curReguId)
          } else {
            this.$message.error('获取规程信息失败')
            console.error('获取规程信息失败：', res.message)
          }
        }).catch(err => {
          this.$message.error('获取规程信息异常')
          console.error('获取规程信息异常：', err)
        })

      }
    },
    findList() {
      let param = {
        reguId: this.reguId,
        tpPlanId: this.planId,
        tpPlanRelated: this.tpPlanRelated
      }
      getReguDeteil(param).then((res) => {
        this.tableData = res.result
      })
    },
    getReguDetailList(regu) {
      let param = {
        reguId: regu,
        tpPlanId: this.planId,
        tpPlanRelated: this.tpPlanRelated
      }
      getReguDeteil(param).then((res) => {
        this.tableData = res.result
      })
    },
    loadChildrenMethod(row) {
      let param = {
        reguId: this.reguId,
        parentId: row.row.id,
        tpPlanId: this.planId,
        tpPlanRelated: this.tpPlanRelated
      }
      return new Promise((resolve) => {
        getReguDeteil(param).then((res) => {
          if (res.success) {
            resolve(res.result)
          } else {
            this.$message.error(`${res.msg}`)
          }
        })
      })
    },
    onSelect(selectedKeys, event) {
      let selectedNode = event.node.dataRef
      if (selectedKeys.length > 0) {
        this.curReguId = selectedNode.id
        this.getReguDetailList(selectedNode.id)
      }
    },
    handleMenuClick(e) {
      // console.log('click menu:', e)
      this.getReguDetailList(e.key)
    },
    handleOk() {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if (this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      this.$emit('ok', selectRecords)
      this.close()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.visible = false
    }
  }
}
</script>

<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  border-bottom: 1px solid #e1e1de;
}
</style>