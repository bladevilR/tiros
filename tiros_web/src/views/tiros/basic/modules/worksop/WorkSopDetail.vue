<template>
  <NaCardContent title="内容明细" height="100%">
    <a-row :gutter="24">
      <a-col :span="24" style="margin-bottom: 12px">
        <a-space>
          <!-- <a-button @click="findList()">刷新</a-button> -->
          <a-button type="primary" @click="handleAdd">新增</a-button>
          <a-button type="dashed" :disabled="!isCheckRow" @click="handleEdit">编辑</a-button>
          <a-button type="dashed" :disabled="!isCheckRows" @click="handleDel">删除</a-button>
        </a-space>
      </a-col>
    </a-row>
    <div class="table-page-body-wrapper" style="height: 100%">
      <div class="table-body-context">
        <vxe-table
          border
          ref="listTable"
          align="center"
          height="auto"
          :loading="loading"
          :data="tableData"
          :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          @checkbox-change="rangeChange"
          @checkbox-all="rangeChange"
        >
          <vxe-table-column type="checkbox" width="60px" />
          <vxe-table-column field="stepNum" title="步骤" width="120px" />
          <vxe-table-column field="stepTitle" title="步骤名称" align="left">
            <template v-slot="{ row }">
              <a @click.stop="showDetail(row)">{{ row.stepTitle }}</a>
            </template>
          </vxe-table-column>
        </vxe-table>
      </div>
    </div>
    <WorkSopDetailModal ref="workSopDetailModal" @ok="findList"></WorkSopDetailModal>
    <WorkSopView ref="workSopViewModal"></WorkSopView>
  </NaCardContent>
</template>

<script>
import NaCardContent from '@comp/tiros/NaCardContent'
import WorkSopDetailModal from '@views/tiros/basic/modules/worksop/WorkSopDetailModal'
import { getSopDetailPage, saveSopDetailRecord, delSopDetailRecord } from '@/api/tirosApi'
import WorkSopView from '@views/tiros/basic/modules/worksop/WorkSopView'

export default {
  name: 'WorkSopDetail',
  components: { NaCardContent, WorkSopDetailModal, WorkSopView },
  data() {
    return {
      isCheckRow: false,
      isCheckRows: false,
      visible: false,
      confirmLoading: false,
      loading: false,
      tableData: [],
      queryParam: {
        bookId: '',
      },
    }
  },
  created() {},
  methods: {
    findList(bookId = null) {
      console.log(2323232)
      // this.queryParam.bookId = null
      // this.tableData = []
      this.isCheckRow = false
      this.isCheckRows = false
      if (bookId) {
        this.queryParam.bookId = bookId
      }
      if (this.queryParam.bookId) {
        this.loading = true
        getSopDetailPage(this.queryParam).then((res) => {
          if (res.success) {
            this.tableData = res.result
          } else {
            this.$message.error(res.message)
          }
        }).finally(()=>{
          this.loading = false
        })
      }
    }, // 保存数据
    save() {},
    // 用户选择记录触发
    rangeChange({ records }) {
      this.isCheckRow = records.length === 1
      this.isCheckRows = records.length > 0
    },
    handleAdd() {
      if (!this.queryParam.bookId) {
        this.$emit('beforeSave')
        return
      }
      let maxNum = 1
      this.tableData.forEach((element) => {
        if (Number(element.stepNum) >= Number(maxNum)) {
          maxNum = Number(element.stepNum) + 1
        }
        // else if (Number(element.stepNum) === Number(maxNum)) {
        //   maxNum = Number(element.stepNum) + 1
        // }
      })
      console.log('步骤最大值')
      console.log(maxNum)
      this.$refs.workSopDetailModal.add(this.queryParam.bookId, maxNum)
    },
    handleEdit() {
      let record = this.$refs.listTable.getCheckboxRecords()[0]
      this.$refs.workSopDetailModal.edit(record)
    },
    handleDel() {
      let records = this.$refs.listTable.getCheckboxRecords()
      this.$confirm({
        content: `是否删除选中${records.length}条数据？`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          let delList = records.map((item) => {
            return item.id
          })
          let formData = new FormData()
          formData.append('ids', delList.join(','))
          delSopDetailRecord(formData).then((res) => {
            if (res.success) {
              this.$message.success('删除成功')
              this.findList()
            } else {
              this.$message.error(res.message)
            }
          })
        },
      })
    },
    showDetail(row) {
      this.$refs.workSopViewModal.showSopDetail([row.id])
    },
  },
}
</script>