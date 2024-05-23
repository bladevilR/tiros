<template>
  <a-modal
    :title="`查看`"
    width="100%"
    dialogClass="fullDialog no-footer"
    :visible="visible"
    @cancel="handleCancel"
    :footer="null"
  >
  <div>
    <a-descriptions bordered>
      <a-descriptions-item label="任务名称">
        {{ model.title }}
      </a-descriptions-item>
      <a-descriptions-item label="编码">
        {{ model.rectifyNo }}
      </a-descriptions-item>
      <a-descriptions-item label="整改类型">
        {{ model.rectifyType_dictText }}
      </a-descriptions-item>
      <a-descriptions-item label="下发日期">
        {{ model.sendDate }}
      </a-descriptions-item>
      <a-descriptions-item label="责任班组">
        {{ model.groupName }}
      </a-descriptions-item>
      <a-descriptions-item label="整改状态">
        <span>{{ model.status_dictText }}</span>
      </a-descriptions-item>
      <a-descriptions-item label="车辆段">
        <span>{{ model.depotName }}</span>
      </a-descriptions-item>
      <a-descriptions-item label="线路">
        <span>{{ model.lineName }}</span>
      </a-descriptions-item>
      <a-descriptions-item label="所属车辆">
        <span>{{ model.trainNo }}</span>
      </a-descriptions-item>
      <a-descriptions-item label="关联工单">
        <span>{{ model.orderName }}</span>
      </a-descriptions-item>
      <a-descriptions-item label="整改工位">
        <span>{{ model.workstationName }}</span>
      </a-descriptions-item>
      <a-descriptions-item label="整改工序">
        <span>{{ model.orderTaskName }}</span>
      </a-descriptions-item>
      <a-descriptions-item label="备注说明">
        <span>{{ model.remark }}</span>
      </a-descriptions-item>
    </a-descriptions>
    <a-divider orientation="left" style="font-size: 14px">已上传列表</a-divider>
    <div style="margin-top: 12px;height: calc(100vh - 395px)">
      <vxe-table
        border
        height="auto"
        :data="annexList"
        show-overflow="tooltip"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <div class=""></div>
        <vxe-table-column type="seq" title="序号" width="50px" header-align="center" align="center"></vxe-table-column>
        <vxe-table-column field="title" title="文件名" header-align="left" align="left"></vxe-table-column>
        <vxe-table-column title="操作" width="160px" header-align="center" align="center">
          <template v-slot="{ row }">
            <a style="margin-right: 12px" @click.stop="handleSeeing(row)">查看</a>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
  </div>
  <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
  </a-modal>
</template>

<script>
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import { isPrivilege } from '@/api/tirosApi'

export default {
  name: 'RectifyView',
  components: {DocPreviewModal},
  data() {
    return {
      visible: false,
      model: {},
      annexList: [],
      status: false,
      fileName: ''
    }
  },
  created() {},
  methods: {
    handleCancel(){
      this.visible = false
    },
    showView(record){
      this.readOnly = true
      if (record.id) {
        if (record.annexList != null && record.annexList.length > 0) {
          this.annexList = record.annexList
        }
      } 
      this.model = Object.assign({}, record)
      this.visible = true
    },
    async handlePrivilege(id, operation) {
      let param = { id: id, operation: operation }
      return isPrivilege(param).then((res) => {
        // this.status = res.result
        if (res.success) {
          return res.result
        } else {
          this.$message.error(res.message)
          return false
        }
      }).catch(err =>{
        this.$message.error(err)
        return false
      })
    },
    async handleSeeing(data) {
      let status = await this.handlePrivilege(data.id, 2)
      if (!status) {
        this.$message.error('您没有权限!')
      } else {
        // this.fileName = data.title
        this.$refs.docPreview.handleFilePath(data.address)
      }
    },
  },
}
</script>

<style scoped>
</style>