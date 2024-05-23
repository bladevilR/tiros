<template>
  <div>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="4" :sm="12">
            <a-form-item label="物资">
              <a-input placeholder="物资编码或者名称" v-model="queryParam.materialType" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="12">
            <a-form-item label="领料单">
              <a-input placeholder="领料单编码或者名称" v-model="queryParam.applyOrder" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="12">
            <a-form-item label="领用人">
              <a-input placeholder="人员或者工班" v-model="queryParam.receiver" allowClear></a-input>
            </a-form-item>
          </a-col>
          <template v-if="toggleSearchStatus">
          <a-col :md="4" :sm="12">
            <a-form-item label="领用日期">
              <a-date-picker format="YYYY-MM-DD" v-model="queryParam.receiveTime"/>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="12">
            <a-form-item label="领料状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择"
                dictCode="bu_material_apply_status"
              />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="12">
            <a-form-item label="备料状态">
              <j-dict-select-tag
                v-model="queryParam.readyStatus"
                placeholder="请选择"
                dictCode="bu_material_apply_ready_status"
              />
            </a-form-item>
          </a-col>
          </template>
          <a-col :md="8" :sm="24">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button @click="findList">查询</a-button>
              <a-button type="primary" @click="handleAdd" style="margin-left: 8px">新增</a-button>
              <a-button style="margin-left: 8px" @click="deleteRecord">删除</a-button>
               <a-button style="margin-left: 8px" @click="handlerReadyMaterial">备料确认</a-button>
              <a-button style="margin-left: 8px" @click="handleConfirmMaterial">领料确认</a-button>
              <a @click.stop="handleToggleSearch" style="margin-left: 8px">
                {{ toggleSearchStatus ? '收起' : '展开' }}
                <a-icon :type="toggleSearchStatus ? 'up' : 'down'"/>
              </a>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div  style="height: calc(100vh - 225px)">
      <vxe-table
        border
        style="height: calc(100vh - 225px)"
        max-height="100%"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="code" title="领料单编码" width="150">
          <template v-slot="{row}">
            <a  @click.stop="applyDetail(row)">{{ row.code }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="title" title="领料单名称" min-width="200" header-align="center" align="left"></vxe-table-column>
<!--        <vxe-table-column field="confirmUserName" title="发料人" width="100" header-align="center" align="left"></vxe-table-column>-->
        <vxe-table-column field="confirmTime" title="备料日期" width="100" ></vxe-table-column>
<!--        <vxe-table-column field="materialQuantity" title="物料数量" width="100" header-align="center" align="right"></vxe-table-column>-->
        <vxe-table-column field="readyStatus_dictText" title="备料状态" width="100" ></vxe-table-column>
        <vxe-table-column field="status_dictText" title="领料状态" width="100"></vxe-table-column>
        <vxe-table-column field="deptName" title="申请工班" width="180" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="userName" title="申请人" width="100" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="receiveTime" title="领用日期" width="100" ></vxe-table-column>
        <vxe-table-column field="remark" title="备注" width="150" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column title="操作" width="100" >
          <template v-slot="{ row }">
            <a @click.stop="handleEdit(row)" >编辑</a>
          </template>
        </vxe-table-column>
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
    <apply-item-modal ref="modalForm" @ok="loadData()"></apply-item-modal>
    <apply-detail ref="materialDetail"></apply-detail>
    <apply-ready-modal ref="readyMaterial" @ok="loadData"></apply-ready-modal>
    <apply-confirm-modal ref="confirmMaterial" @ok="loadData"></apply-confirm-modal>
  </div>
</template>

<script>
  import { delApply, getApplyList } from '@/api/tirosMaterialApi'
  import  ApplyItemModal from './ApplyItemModal'
  import ApplyDetail from './ApplyDetail'
  import ApplyReadyModal from './ApplyReadyModal'
  import ApplyConfirmModal from './ApplyConfirmModal'

  export default {
    name: 'Apply',
    components:{ApplyItemModal,ApplyDetail,ApplyReadyModal, ApplyConfirmModal},
    data() {
      return {
        toggleSearchStatus:false,
        queryParam: {
          materialType: '',
          applyOrder: null,
          receiver:'',
          receiveTime:null,
          status:null,
          readyStatus:null,
          pageNo: 1,
          pageSize: 10,
        },
        totalResult: 0,
        allAlign: 'center',
        tableData: [],
        showEdit: false
      }
    },
    created() {
      this.findList()
    },
    watch: {
      value: {
        immediate: true,
        handler(id) {
          this.queryParam.parentId = id
          this.tableData = []
          this.findList()
        }
      }
    },
    methods: {
      findList() {
        getApplyList(this.queryParam).then((res) => {
          if (res.success) {
            this.totalResult = res.result.total
            this.loading = false
            this.tableData = res.result.records
          } else {
            this.$message.error('查询数据失败')
            console.error('查询领用数据失败：', res.message)
          }
        })
      },
      handlePageChange({ currentPage, pageSize }) {
        this.queryParam.pageNo = currentPage
        this.queryParam.pageSize = pageSize
        this.findList()
      },
      loadData() {
        this.findList()
        this.$emit('load')
      },
      handleAdd() {
        this.$refs.modalForm.add()
        this.$refs.modalForm.title = '新增'
      },
      handleEdit(record) {
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '编辑'
      },

      deleteRecord() {
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        if (selectRecords.length > 0) {
          this.$confirm({
            content: `是否删除选中${selectRecords.length}数据？`,
            onOk: () => {
              let idsStr = selectRecords
                .map(function(obj, index) {
                  return obj.id
                })
                .join(',')
              delApply('ids=' + idsStr).then((res) => {
                this.findList()
                this.$message.success(res.message)
              })
            }
          })
        } else {
          this.$message.error('尚未选中任何数据!')
        }
      },
      applyDetail(row) {
        this.$refs.materialDetail.detail(row.id)
      },
      handlerReadyMaterial() {
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        if (selectRecords.length > 0) {
          if(selectRecords.length>1){
            this.$message.error('只能选中一条数据!')
          }else {
            this.$refs.readyMaterial.showModal(selectRecords[0].id)
          }
        } else {
          this.$message.error('尚未选中任何数据!')
        }
      },
      handleConfirmMaterial () {
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        if (selectRecords.length > 0) {
          if(selectRecords.length>1){
            this.$message.error('只能选中一条数据!')
          }else {
            this.$refs.confirmMaterial.showModal(selectRecords[0].id)
          }
        } else {
          this.$message.error('尚未选中任何数据!')
        }
      },
      handleToggleSearch(){
        this.toggleSearchStatus=!this.toggleSearchStatus
      }
    }
  }
</script>