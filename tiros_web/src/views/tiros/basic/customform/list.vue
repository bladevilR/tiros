<template>
  <div id="customForm">
    <na-splitter :defaultSize="200">
      <div slot="left-panel">
        <a-tree
          :tree-data="treeData"
          :selectedKeys="selectedKeys"
          @select="selectTreeNode"
          :load-data="loadTreeChild"
        />
      </div>
      <div slot="right-panel" style="height: calc(100vh - 115px)">
        <div style="padding-top: 5px;padding-left: 5px;">
          <a-spin :spinning="loading">
            <div class="table-page-search-wrapper">
              <a-form layout="inline" @keyup.enter.native="loadDataList">
                <a-row style="margin:0" :gutter="24">
                  <a-col :md="5" :sm="24">
                    <a-form-item label="名称">
                      <a-input placeholder="编码或名称" v-model="queryParam.name" allow-clear></a-input>
                    </a-form-item>
                  </a-col>
                  <a-col :md="4" :sm="24">
                    <a-form-item label="修程">
                      <j-dict-select-tag @change="handleProgram" triggerChange v-model="queryParam.repairProId" dictCode="bu_repair_program,name,id" />
                    </a-form-item>
                  </a-col>
                  <a-col :md="5" :sm="24">
                    <a-form-item label="规程">
                      <j-dict-select-tag
                        triggerChange
                        v-model="queryParam.reguId"
                        :dictCode="dicStr"
                        @focus="handleFocusRegu"
                      />
                    </a-form-item>
                  </a-col>
<!--  线路条件暂时去掉，没太大作业，规程本来就与线路关联了的，先去掉，要不按钮空间不够
                  <a-col :md="4" :sm="24">
                    <a-form-item label="线路">
                      <line-select-list v-model="queryParam.lineId"></line-select-list>
                    </a-form-item>
                  </a-col>-->

                  <a-col :md="10" :sm="24">
                    <a-space>
                      <a-button @click="loadDataList">查询</a-button>
                      <a-button type="primary" @click="handleAdd">新增</a-button>
                      <a-button :disabled="records.length !== 1" @click="handleEdit(records[0])">编辑</a-button>
<!--                      <a-button :disabled="records.length < 1" @click="deleteRecord" v-has="'customform:delete'">删除</a-button>-->
                      <a-button :disabled="records.length < 1" @click="validRecord">启用</a-button>
                      <a-button :disabled="records.length < 1" @click="invalidRecord">禁用</a-button>
                      <!--                        <a-button :disabled="records.length != 1" @click="print">打印</a-button>-->
                    </a-space>
                  </a-col>
                </a-row>
              </a-form>
            </div>
            <div style="height: calc(100vh - 232px)">
              <vxe-table
                border
                max-height="100%"
                style="height: calc(100vh - 232px)"
                ref="listTable"
                :align="'center'"
                :data="tableData"
                auto-resize
                show-overflow="tooltip"
                @checkbox-change="checkboxChange"
                @checkbox-all="checkboxChange"
                :checkbox-config="{trigger: 'row', highlight: true, range: true}"
                :edit-config="{ trigger: 'manual', mode: 'row' }"
              >
                <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                <vxe-table-column field="name" title="表单名称" align="left" header-align="center"
                                  min-width="160">
                  <template v-slot="{ row }">
                    <a href="javascript:;" @click.stop="viewForm(row)">{{ row.name }}</a>
                  </template>
                </vxe-table-column>
                <vxe-table-column field="code" title="表单编码" align="left" header-align="center"
                                  width="80"></vxe-table-column>
                <vxe-table-column field="categoryName" title="所属分类" align="center" header-align="center"
                                  width="100"></vxe-table-column>
                <vxe-table-column field="type_dictText" title="表单类型" align="center" header-align="center"
                                  width="80"></vxe-table-column>
                <vxe-table-column field="reguName" title="技术规程" width="200">
                  <template v-slot="{ row }">
                    <span v-if="row.reguName">{{row.reguName + (row.reguVersion ? `(${row.reguVersion})` : '')}}</span>
                  </template>
                </vxe-table-column>
                <vxe-table-column field="lineName" title="线路" width="100"></vxe-table-column>
                <vxe-table-column field="repairProgramName" title="修程" width="100"></vxe-table-column>
                <vxe-table-column field="createGroupName" align="left" header-align="center" title="创建班组" width="150"></vxe-table-column>
                <vxe-table-column field="status_dictText" title="状态" align="center" header-align="center" width="80">
                  <template v-slot="{ row }">
                    <div :style="{ backgroundColor: statusColor[row.status + ''], borderRadius: '4px' }">
                      {{ row.status_dictText }}
                    </div>
                  </template>
                </vxe-table-column>
                <vxe-table-column width="150" field="remark" title="备注" align="left" header-align="center"></vxe-table-column>
              </vxe-table>
              <vxe-pager
                perfect
                :loading="loading"
                :current-page.sync="queryParam.pageNo"
                :page-size.sync="queryParam.pageSize"
                :total="totalResult"
                :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
                @page-change="loadDataList"
              ></vxe-pager>
            </div>
          </a-spin>
        </div>
      </div>
    </na-splitter>
    <LuckySheetPrint ref="luckySheetPrint"></LuckySheetPrint>
    <a-modal
      centered
      :width="'100%'"
      dialogClass="fullDialog no-title no-footer"
      :visible="editVisible"
      @cancel="editVisible=false"
      :footer="null"
      :destroyOnClose="true"
    >
      <formEdit :formInfo="curForm" :visible.sync="editVisible" @saveSuccess="saveSuccess"></formEdit>
    </a-modal>
    <FormViewModal ref="viewModal" :form-name="curForm?curForm.name:''"></FormViewModal>
  </div>
</template>

<script>
import { loadSysCategory } from '@api/tirosSystemApi'
import { pageCustomForm, deleteCustomForm, validCustomForm, invalidCustomForm } from '@api/tirosApi'
import edit from '@views/tiros/basic/customform/edit'
import NaSplitter from '@comp/tiros/Na-splitter'
import LuckySheetPrint from '@views/tiros/common/recordtable/LuckySheetPrint'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import FormViewModal from '@views/tiros/basic/customform/FormViewModal'
import { everythingIsEmpty } from '@/utils/util'


export default {
  name: 'list',
  components: { NaSplitter, 'formEdit': edit , LuckySheetPrint, LineSelectList,FormViewModal},
  data () {
    return {
      records:[],
      selectedKeys: [],
      treeData: [],
      tableData: [],
      totalResult: 0,
      loading: false,
      dicStr: '',
      statusColor: {
        0: '#dedede',
        1: '#bad795',
      },
      queryParam: {
        category: '',
        createGroupId: '',
        name: '',
        repairProId: '',
        reguId: '',
        lineId: '',
        pageNo: 1,
        pageSize: 10
      },
      editVisible: false,
      curForm: null,
      curCategory: null
    }
  },
  mounted () {
    this.queryTreeData()
  },
  methods: {
    handleFocusRegu () {
      let programId = this.queryParam.repairProId
      if (everythingIsEmpty(programId)) this.$message.warn('请先选择修程类型')
    },
    handleProgram (data) {
      if (data) {
        this.dicStr = `bu_repair_regu_info,name,id,status=1 and repair_pro_id='${data}'`
      } else {
        this.queryParam.reguId = '';
        this.dicStr = ''
      }
    },
    checkboxChange(e){
      this.records = e.records;
    },
    queryTreeData () {
      let param = {
        'async': false,
        'pcode': 'B03'
      }
      loadSysCategory(param).then((resp) => {
        if (resp.success) {
          this.treeData = resp.result
          this.selectedKeys = []
          if (this.treeData.length > 0) {
            // this.defaultSelectedKeys.push(this.treeData[0].key)
            this.selectedKeys.push(this.treeData[0].key)
            this.loadDataList()
          }
        } else {
          this.$message.warning(resp.message)
        }
      })
    },
    loadTreeChild (parentNode) {
      let param = {
        'async': false,
        'pcode': parentNode ? parentNode.dataRef.code : ''
      }

      if (parentNode.dataRef.children) {
        resolve()
        return
      }
      loadSysCategory(param).then((res) => {
        if (res.success) {
          parentNode.dataRef.children = res.result
          parentNode.dataRef.children.map((item) => {
            item.isLeaf = !item.hasChildren
          })
          this.treeData = [...this.treeData]
        } else {
          this.$message.warning(res.message)
        }
        resolve()
      })
    },
    selectTreeNode (id) {
      this.selectedKeys = id
      if(this.selectedKeys && this.selectedKeys.length>0) {
        this.curCategory = this.selectedKeys[0]
      }

      this.loadDataList()
    },
    loadDataList () {
      console.log('selectedKeys:', this.selectedKeys)
      this.queryParam.category = ''
      if (this.selectedKeys.length > 0) {
        this.queryParam.category = this.selectedKeys[0]
      }
      this.loading = true
      // if (this.$store.getters.userInfo.username === 'admin') {
      //   this.queryParam.createGroupId = null
      // } else {
      //   this.queryParam.createGroupId=this.$store.getters.userInfo.departId
      // }
      pageCustomForm(this.queryParam).then(res => {
        if (res.success) {
          this.totalResult = res.result.total
          this.records = [];
          this.tableData = res.result.records

        } else {
          this.$message.error('加载列表数据失败')
        }
        this.loading = false
      }).catch(err => {
        this.loading = false
        console.error('加载列表数据异常：', err)
      })
    },
    handleAdd () {
      this.curForm = { category: this.selectedKeys[0] }
      this.editVisible = true
    },
    handleEdit (row) {
      this.curForm = row
      this.editVisible = true
    },
    saveSuccess () {
      this.loadDataList()
    },
    deleteRecord () {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}条数据？`,
          onOk: () => {
            let idsStr = selectRecords.map(function (obj, index) {
              return obj.id
            }).join(',')
            deleteCustomForm({ ids: idsStr }).then((res) => {
              if (res.success) {
                this.$message.success('删除成功')
                this.loadDataList()
              } else {
                console.error('删除表单失败：', res.message)
                this.$message.error('删除失败')
              }
            }).catch(err => {
              console.error('删除表单异常：', res.message)
              this.$message.error('删除异常')
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    validRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.filter((r) => { return r.status === 1 }).length > 0) {
          this.$message.warn('有效的数据不能启用')
          return
        }
        this.$confirm({
          content: `是否启用选中的表单？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            validCustomForm('ids=' + idsStr)
              .then((res) => {
                if (res.success) {
                  this.$message.success('启用成功')
                  this.loadDataList()
                } else {
                  this.$message.error( res.message)
                }
              })
              .catch((err) => {
                this.$message.error('启用异常')
                console.log('启用异常：', err)
              })
          },
        })
      } else {
        this.$message.error('尚未选中数据!')
      }
    },
    invalidRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.filter((r) => { return r.status === 0 }).length > 0) {
          this.$message.warn('无效的数据不能禁用')
          return
        }
        this.$confirm({
          content: `是否禁用选中的表单？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            invalidCustomForm('ids=' + idsStr)
              .then((res) => {
                if (res.success) {
                  this.$message.success('禁用成功')
                  this.loadDataList()
                } else {
                  this.$message.error( res.message)
                }
              })
              .catch((err) => {
                this.$message.error('禁用异常')
                console.log('禁用异常：', err)
              })
          },
        })
      } else {
        this.$message.error('尚未选中数据!')
      }
    },
    print(){
      let selectRecord = this.$refs.listTable.getCheckboxRecords()[0]
      this.$refs.luckySheetPrint.printById(selectRecord.id)
    },
    viewForm (row) {
      this.$refs.viewModal.showModal(row.id)
    }
  }
}
</script>

<style lang="less" scoped>
</style>