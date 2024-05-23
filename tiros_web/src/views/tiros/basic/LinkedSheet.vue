<template>
  <a-modal
    :title="name+'表单管理'"
    :width="'100%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    dialogClass="fullDialog no-footer"
    @cancel="handleCancel"
    :destroyOnClose="true"
  >
    <a-row id="sheetContent" :gutter="8">
      <a-col :md="18" :sm="24">
        <a-card class="left">
          <div class="table-page-search-wrapper">
            <a-form layout="inline" @keyup.enter.native="searchQuery">
              <a-row :gutter="8">
                <a-col :md="6" :sm="24">
                  <a-form-item label="表单">
                    <a-input placeholder="请输入编码或名称" v-model="queryParam.searchText" allowClear></a-input>
                  </a-form-item>
                </a-col>
                <a-col  :md="6" :sm="24">
                  <a-form-item label="类型">
                  <j-dict-select-tag
                    :triggerChange="true"
                    v-model="queryParam.type"
                    dictCode="bu_work_form_type"
                  />
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="修程">
                    <j-dict-select-tag triggerChange v-model="queryParam.repairProId" dictCode="bu_repair_program,name,id" />
                  </a-form-item>
                </a-col>
                <a-col :md="6" :sm="24">
                  <a-form-item label="线路">
                    <line-select-list v-model="queryParam.lineId"></line-select-list>
                  </a-form-item>
                </a-col>
              </a-row>
              <a-row>
                <a-col :md="8" :sm="8">
                  <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
                    <a-space>
                      <a-button @click="searchQuery">查询</a-button>
                      <a-button @click="link">关联</a-button>
                      <!--                    <a-button @click="handleCancel">关闭</a-button>-->
                    </a-space>
                  </span>
                </a-col>
              </a-row>
            </a-form>
          </div>
          <div style="height: calc(100vh - 247px)">
            <vxe-table
              border
              auto-resize
              max-height="100%"
              style="height: calc(100vh - 247px)"
              ref="listTable"
              :align="allAlign"
              :data="tableData"
              show-overflow="tooltip"
              :checkbox-config="{trigger: 'row', highlight: true, range: true}"
              :edit-config="{trigger: 'manual', mode: 'row'}"
            >
              <vxe-table-column type="checkbox" width="40"></vxe-table-column>
              <vxe-table-column field="formName" title="表单名称" min-width="200" align="left"
                                header-align="center"></vxe-table-column>
              <vxe-table-column field="code" title="表单编码" min-width="100" align="left"
                                header-align="center"></vxe-table-column>
              <vxe-table-column field="status_dictText" title="状态" width="100">
                <template v-slot="{ row }">
                  <div :style="{ backgroundColor: statusColor[row.status + ''], borderRadius: '4px' }">
                    {{ row.status_dictText }}
                  </div>
                </template>
              </vxe-table-column>
              <vxe-table-column field="type_dictText" title="类型" width="100"></vxe-table-column>
              <vxe-table-column field="createGroupName" title="创建班组" width="150"></vxe-table-column>
              <!--          <vxe-table-column field="createTime" title="创建日期" width="200"></vxe-table-column>-->
              <vxe-table-column field="remark" title="备注" width="180" align="left"
                                header-align="center"></vxe-table-column>
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
        </a-card>
      </a-col>
      <a-col :md="6" :sm="24">
        <a-card class="right">
          <div class="typeTitle">已关联表单</div>
          <vxe-table :show-header="false" :data="sheetList" border="none" show-overflow="tooltip">
            <vxe-table-column align="left" field="formName"></vxe-table-column>
            <vxe-table-column align="right" width="40">
              <template v-slot="{ row }">
                <a-icon @click.stop="deleteSheet(row)" type="close"/>
              </template>
            </vxe-table-column>
          </vxe-table>
        </a-card>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script>
// import { getFormList, linkedFormList, delLinkedForm, linkForms } from '@/api/tirosApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { cancelRelateForm, listRelatedForm, pageUnRelatedForm, relateForm } from '@/api/tirosApi'

export default {
  components: { LineSelectList },
  data() {
    return {
      visible: false,
      allAlign: 'center',
      id: '',
      name: '',
      statusColor: {
        0: '#dedede',
        1: '#bad795',
      },
      queryParam: {
        searchText: '',
        workstationId: '',
        repairProId: '',
        lineId: '',
        type:null,
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      sheetList: [],
      tableData: [],
      confirmLoading: false
    }
  },
  /*created() {
    this.findList()
    this.getRelatedFormList()
  },*/
  methods: {
    show(value) {
      this.visible = true
      this.id = value.id
      this.name = value.name
      this.queryParam.workstationId = value.id
      this.findList()
      this.getRelatedFormList()
    },
    findList() {
      this.loading = true
      if (this.$store.getters.userInfo.username === 'admin') {
        this.queryParam.createGroupId = null
      } else {
        this.queryParam.createGroupId=this.$store.getters.userInfo.departId
      }
      pageUnRelatedForm(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          let unRelatedFormList = res.result.records
          for (let i = 0; i < unRelatedFormList.length; i++) {
            if (!unRelatedFormList[i].type_dictText) {
              unRelatedFormList[i].type_dictText = '作业记录表'
            }
          }
          this.tableData = unRelatedFormList
        }
      })
    },
    getRelatedFormList() {
      let params={
        workstationId: this.id,
        repairProId:this.queryParam.repairProId,
        lineId:this.queryParam.lineId,
        type: this.queryParam.type
      }
      listRelatedForm(params).then((res) => {
        if (res.success) {
          this.sheetList = res.result
        }
      })
    },
    searchQuery() {
      this.findList()
      this.getRelatedFormList()
    },
    deleteSheet(row) {
      this.$confirm({
        content: '是否删除该关联表单',
        onOk: () => {
          cancelRelateForm('id=' + row.id).then((res) => {
            if (res.success) {
              this.$message.success(res.message)
              this.findList()
              this.getRelatedFormList()
            }
          })
        }
      })
    },
    getSelectEvent() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      this.$XModal.alert(selectRecords.length)
    },
    link() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否关联选中${selectRecords.length}数据？`,
          onOk: () => {
            // let workstationFormRelationList = selectRecords.slice()
            let workstationFormRelationList = []
            Object.assign(workstationFormRelationList, selectRecords)
            for (let i = 0; i < workstationFormRelationList.length; i++) {
              workstationFormRelationList[i].workstationId = this.id
            }
            relateForm(workstationFormRelationList).then((res) => {
              if (res.success) {
                this.$message.success('关联成功!')
                this.findList()
                this.getRelatedFormList()
              }
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    }
  }
}
</script>

<style lang="less">
#sheetContent {
  .pageTitle {
    font-size: 20px;
    font-weight: 600;
    height: 40px;
  }

  .right {
    .ant-card-body {
      padding: 0 0 24px 0;
      height: calc(100vh - 75px);
      overflow-y: auto;
    }

    .typeTitle {
      background: #eee;
      padding: 15px;
      text-align: center;
    }
  }

  .left {
    .ant-card-body {
      height: calc(100vh - 75px);
      padding: 10px;
      overflow-y: auto;
      overflow-x: hidden;
    }

    .tableHeight {
      height: calc(100vh - 290px);
      // overflow-y: auto;
    }
  }
}
</style>