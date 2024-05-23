<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="12">
            <a-form-item label="名称">
              <a-input placeholder="输入编码或者名称" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="12">
            <a-form-item label="年份">
              <a-date-picker
                mode="year"
                placeholder="请选择年份"
                format="YYYY"
                v-model="queryParam.sbYear"
                :open="yearPickShow"
                style="width: 100%"
                @panelChange="handlePanelChange"
                @openChange="handleOpenChange"
              />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="所属线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="所属修程">
              <j-dict-select-tag
                v-model="queryParam.repairProgramId"
                placeholder="请选择"
                dictCode="bu_repair_program,name,id"
              />
            </a-form-item>
          </a-col>
          <!--          <a-col :md="3" :sm="24">
            <a-form-item label="提报人">
              <a-select
                v-model="sbUserName"
                placeholder="请选择提报人员"
                :open="false"
                :showArrow="true"
                @focus="openUserSelectModal"
                ref="myUserSelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>-->
          <a-col :md="4" :sm="24">
            <a-form-item label="是否汇总">
              <a-checkbox v-model="queryParam.summary"> </a-checkbox>
            </a-form-item>
          </a-col>
          <!-- <a-col :md="4" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
              </a-space>
            </span>
          </a-col> -->
        </a-row>
        <a-row>
          <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
            <a-space>
              <a-button @click="findList">查询</a-button>
              <a-button type="primary" @click="handleAdd">新增</a-button>
              <a-button @click="deleteRecord" :disabled="!btnStatus.edit">删除</a-button>
              <a-button :loading="exportLoading" @click="exportMaterialPlanEXFile">导出</a-button>
            </a-space>
          </span>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 270px)">
      <vxe-table
        border
        :align="allAlign"
        ref="listTable"
        height="100%"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-change="btnStatus.update()"
        @checkbox-all="btnStatus.update()"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="100"></vxe-table-column>
        <vxe-table-column field="sbYear" title="所属年份" width="100"></vxe-table-column>
        <vxe-table-column field="repairProgramName" title="所属修程" width="120"></vxe-table-column>
        <vxe-table-column field="code" title="物资编码" width="180"></vxe-table-column>
        <vxe-table-column
          field="name"
          title="物资名称"
          width="180"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column
          field="spec"
          title="物资描述"
          header-align="center"
          align="left"
          min-width="320"
        ></vxe-table-column>
        <vxe-table-column
          field="sbAmount"
          title="提报数量"
          width="100"
          header-align="center"
          align="right"
        ></vxe-table-column>
        <vxe-table-column field="unit" title="单位" width="100"></vxe-table-column>
        <vxe-table-column field="sbType_dictText" title="类别" width="100"></vxe-table-column>
        <vxe-table-column field="materialTypeCategory_dictText" title="属性" width="150"></vxe-table-column>

        <vxe-table-column field="sbUserName" title="提报人员" width="100"></vxe-table-column>
        <vxe-table-column field="sbDate" title="提报时间" width="120"></vxe-table-column>
        <!-- <vxe-table-column title="操作" width="80" fixed="right">
          <template v-slot="{ row }">
            <a @click.stop="handleEdit(row)">编辑</a>
          </template>
        </vxe-table-column> -->
      </vxe-table>
    </div>
    <vxe-pager
      perfect
      :current-page.sync="queryParam.pageNo"
      :page-size.sync="queryParam.pageSize"
      :total="totalResult"
      :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
      @page-change="handlePageChange"
    ></vxe-pager>
    <user-list ref="userSelectModal" :dep-id="dispatchGroupId" :multiple="false" @ok="userSelect"></user-list>
    <material-plan-item-modal ref="modalForm" @ok="loadData()"></material-plan-item-modal>
  </div>
</template>


<script>
import moment from 'moment'

import { everythingIsEmpty } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import {
  getPartMaterialPlanList,
  exportMaterialPlanEXFile,
  deleteMaterialPlanItem,
} from '../../../../api/tirosMaterialApi'
import MaterialPlanItemModal from '../modules/MaterialPlanItemModal'
import UserList from '@views/tiros/common/selectModules/UserList'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'List',
  components: { MaterialPlanItemModal, LineSelectList, UserList },
  data() {
    return {
      exportLoading: false,
      yearPickShow: false,
      dispatchGroupId: '',
      sbUserName: '',
      queryParam: {
        searchText: '',
        sbYear: '',
        lineId: '',
        repairProgramId: '',
        sbDeptId: '',
        sbUserId: this.$store.getters.userInfo.id,
        summary: false,
        pageNo: 1,
        pageSize: 10,
      },
      isSummary: false,
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      tableHeight: {
        top: 'height: calc(100vh - 225px)',
        bottom: 'height: calc(100vh - 225px)',
        size: '100%',
      },
      btnStatus: new TableBtnUtil(this, 'listTable', {
        attrs: [
          {
            key: 'edit',
            judge: (e) => !this.isSummary,
          },
        ],
      }),
    }
  },
  created() {
    this.findList()
  },
  methods: {
    // 弹出日历和关闭日历的回调
    handleOpenChange(status) {
      this.yearPickShow = status
    },
    // 得到年份选择器的值
    handlePanelChange(value) {
      this.$set(this.queryParam, 'sbYear', moment(value).format('YYYY'))
      this.yearPickShow = false
    },
    openUserSelectModal() {
      this.$refs.userSelectModal.showModal()
      this.$refs.myUserSelect.blur()
    },
    userSelect(data) {
      if (!everythingIsEmpty(data)) {
        this.sbUserName = data[0].realname
        this.$set(this.queryParam, 'sbUserId', data[0].id)
      }
    },
    findList() {
      if (this.queryParam.summary) {
        this.isSummary = true
      } else {
        this.isSummary = false
      }
      getPartMaterialPlanList(this.queryParam).then((res) => {
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
    loadData() {
      this.findList()
      this.$emit('load')
    },
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit(row) {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (row.id) {
        this.$refs.modalForm.edit(row)
        this.$refs.modalForm.title = '编辑'
      } else if (selectRecords.length == 1) {
        this.$refs.modalForm.edit(selectRecords[0])
        this.$refs.modalForm.title = '编辑'
      } else {
        this.$message.error('请选中一项数据!')
      }
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            deleteMaterialPlanItem('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    exportMaterialPlanEXFile() {
      // 导入
      this.queryParam.lineId = this.lineId
      this.exportLoading = true;
      exportMaterialPlanEXFile(this.queryParam).then((data) => {
        if (!data) {
          this.$message.warning('文件下载失败')
          return
        }
        if (typeof window.navigator.msSaveBlob !== 'undefined') {
          window.navigator.msSaveBlob(new Blob([data], { type: 'application/vnd.ms-excel' }), fileName + '.xls')
        } else {
          let url = window.URL.createObjectURL(new Blob([data], { type: 'application/vnd.ms-excel' }))
          let link = document.createElement('a')
          link.style.display = 'none'
          link.href = url

          link.setAttribute('download', '物资计划.xls')
          document.body.appendChild(link)
          link.click()
          document.body.removeChild(link) //下载完成移除元素
          window.URL.revokeObjectURL(url) //释放掉blob对象
        }
        this.exportLoading = false;
      }).catch((err) => {
        this.exportLoading = false;
      });
    },
  },
}
</script>