<template>
  <a-modal
    title="监修详情"
    :width="'80%'"
    :visible="visible"
    :footer="null"
    @cancel="handleCancel"
    :destroyOnClose="true"
    dialogClass="fullDialog no-footer"
    cancelText="关闭"
  >
    <div style="overflow-y: auto">
      <div class="info-wrapper info-top-wrapper" style="margin-top: 10px">
        <h4>基本信息</h4>
        <a-descriptions bordered :column="4">
          <a-descriptions-item label="任务名称">
            {{ detail.taskContent }}
          </a-descriptions-item>
          <a-descriptions-item label="所属线路">
            {{ detail.lineName }}
          </a-descriptions-item>
          <a-descriptions-item label="派遣工班">
            {{ detail.dispatchGroupName }}
          </a-descriptions-item>
          <a-descriptions-item label="派遣人员">
            {{ detail.dispatchUserName }}
          </a-descriptions-item>
          <a-descriptions-item label="联系方式">
            {{ detail.userPhone }}
          </a-descriptions-item>
          <a-descriptions-item label="派往公司">
            {{ detail.supplierName }}
          </a-descriptions-item>
          <a-descriptions-item label="联系方式">
            {{ detail.supplierPhone }}
          </a-descriptions-item>
          <a-descriptions-item label="联系地址">
            {{ detail.supplierAddress }}
          </a-descriptions-item>
          <a-descriptions-item label="出派时间">
            {{ detail.dispatchDate }}
          </a-descriptions-item>
          <a-descriptions-item label="返回时间">
            {{ detail.returnDate }}
          </a-descriptions-item>
          <a-descriptions-item label="所属合同">
            {{ detail.contractNo }}
          </a-descriptions-item>
          <a-descriptions-item label="设备项目">
            {{ detail.assetTypeName }}
          </a-descriptions-item>
          <a-descriptions-item label="申请时间">
            {{ detail.createTime }}
          </a-descriptions-item>
          <a-descriptions-item label="当前审批">
            {{ detail.wfStatus }}
          </a-descriptions-item>
        </a-descriptions>
      </div>

      <div class="info-wrapper info-top-wrapper">
        <h4>附件信息</h4>
        <div style="height: calc(100vh - 380px)">
          <vxe-table
            border
            ref="listTable"
            :align="allAlign"
            :data="tableData"
            max-height="90%"
            style="height: calc(100vh - 430px)"
            show-overflow="tooltip"
            :checkbox-config="{trigger: 'row', highlight: true, range: true}"
            :edit-config="{trigger: 'manual', mode: 'row'}"
          >
            <vxe-table-column type="checkbox" width="40"></vxe-table-column>
            <vxe-table-column title="-" width="100">
              <template v-slot="{ row }">
            <span>
              <a-space>
                 <a @click.stop="handleSeeing(row)">查看</a>
                <a @click.stop="handleDownload(row)">下载</a>
              </a-space>
            </span>
              </template>

            </vxe-table-column>
            <vxe-table-column field="name" title="资料名称" min-width="180" header-align="center"
                              align="left"></vxe-table-column>
            <vxe-table-column field="type" title="文件类型" width="100"></vxe-table-column>
            <vxe-table-column field="fileSize" title="大小(KB)" width="120" header-align="center"
                              align="right"></vxe-table-column>
            <vxe-table-column field="uploadDate" title="上传日期" width="120"></vxe-table-column>
            <vxe-table-column field="remark" title="备注"  header-align="center"
                              align="left"></vxe-table-column>
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
      </div>
      <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
    </div>
  </a-modal>
</template>

<script>
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import { getOtherResource } from '@api/tirosOutsourceApi'
import { download } from '@api/tirosFileApi'
import { getSuperviseByOrderTask } from '@api/tirosOutsourceApi'

export default {
  name: 'SuperviseItemDetailModal',
  components: { DocPreviewModal },
  data () {
    return {
      detail: {},
      annexList: [],
      visible: false,
      handleModel: {},
      fileName: '',
      queryParam: {
        fileType: null,
        outinDetailId: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: []
    }
  },
  methods: {
    findList () {
      this.queryParam.outinDetailId = this.detail.outinDetailId
      getOtherResource(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.tableData = res.result.records
      })
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    show (value) {
      this.visible = true
      this.detail = value
      this.findList()
    },
    showByTask(record){
      let param = {
        orderTaskId: record.id,
        workOrderId: record.orderId,
      }
      getSuperviseByOrderTask(param).then((res) => {
        if (res.success) {
          this.show(res.result)
        } else {
          this.$message.warn(res.message)
        }
      })
    },
    handleSeeing (data) {
      this.fileName = data.name
      this.$refs.docPreview.handleFilePath(data.savepath)
    },
    handleDownload (data) {
      download(data.savepath)
    },

    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
    }

  }
}
</script>

<style lang="less" scoped>
.info-wrapper {
  border: 1px solid #eee;
  position: relative;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 20px;
}

.info-wrapper h4 {
  position: absolute;
  top: -14px;
  padding: 1px 8px;
  margin-left: 16px;
  color: #777;
  border-radius: 2px 2px 0 0;
  background: #fff;
  font-size: 14px;
  width: auto;
}

</style>