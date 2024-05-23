<template>
  <div :style="`height: calc(100% - ${getButtonShow() ? '47px' : '4px'});`">
    <a-form layout="inline" v-if="getButtonShow()">
      <a-row :gutter="24">
        <a-col :md="6" :sm="8">
          <a-form-item>
            <a-space>
              <a-button type="dashed" class="primary-color" @click="handleAdd">添加</a-button>
              <a-button type="dashed" @click="handleDel">删除</a-button>
            </a-space>
          </a-form-item>
        </a-col>
      </a-row>
    </a-form>
    <div na-flex-height-full>
      <vxe-table
        border
        align="center"
        ref="listTable"
        max-height="auto"
        auto-resize
        :data.sync="files"
        :edit-rules="validRules"
        :keep-source="true"
        show-overflow="ellipsis"
        :checkbox-config="{ trigger: 'row', highlight: getButtonShow(), range: true }"
        :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
        @blur="$refs.listTable.clearValidate()"
      >
        <vxe-table-column type="seq" width="40"></vxe-table-column>
        <vxe-table-column type="checkbox" width="40" v-if="getButtonShow()"></vxe-table-column>
        <vxe-table-column
          v-if="tasks && tasks.length>1"
          field="taskName"
          title="工单任务"
          align="left"
          :edit-render="{ name: 'input', enabled: selectTask ? false : true }"
          width="300px"
        >
          <template v-slot="{ row }">
            {{ row.taskName }}
          </template>
          <template v-slot:edit="{ row }">
            <a-select :allowClear="true" v-model="row.taskId" style="width: 100%" @change="taskSelectChange">
              <a-select-option v-for="(task, index) in tasks" :value="task.id" :key="index">
                {{ task.taskName }}
              </a-select-option>
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column field="name" title="文件名称" align="left" :edit-render="{ name: 'input' }">
          <template v-slot:edit="{ row }">
            <a-upload
              class="attachedUploadBtn"
              :before-upload="beforeLedUpload"
              :multiple="false"
              :file-list="[]"
              :showUploadList="false"
              @blur="$refs.listTable.clearValidate()"
            >
              <a-select ref="toolSelect" v-model="row.name" placeholder="请选择文件" :open="false" style="width: 100%">
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-upload>
          </template>
        </vxe-table-column>
        <vxe-table-column field="type" title="扩展名" width="150px"></vxe-table-column>
        <vxe-table-column field="fileSize" title="文件大小(KB)" width="150px"></vxe-table-column>
        <vxe-table-column field="uid" title="操作" width="150px">
          <template v-slot="{ row }">
            <span v-if="row && !$refs.listTable.isActiveByRow(row)">
              <a @click.stop="previewImage(row)">查看</a>
              <a style="margin-left: 6px" @click.stop="handleDownload(row)">下载</a>
            </span>
            <a-space v-else>
              <a-button type="dashed" size="small" @click.stop="saveRowEvent(row)">保存</a-button>
              <a-button type="dashed" size="small" @click.stop="cancelRowEvent(row)">取消</a-button>
            </a-space>
          </template>
        </vxe-table-column>
        <!-- <vxe-table-column title="操作" width="150" v-if="getButtonShow()">
        <template v-slot="{ row }">
          <template v-if="$refs.listTable.isActiveByRow(row)">
            <a-space>
              <a-button type="dashed" size="small" @click.stop="saveRowEvent(row)">保存</a-button>
              <a-button type="dashed" size="small" @click.stop="cancelRowEvent(row)">取消</a-button>
            </a-space>
          </template>
        </template>
      </vxe-table-column> -->
      </vxe-table>
    </div>
    <doc-preview-modal :title="previewFileName" ref="docPreview"></doc-preview-modal>
  </div>
</template>

<script>
import { uploadFile, download, deleteFile } from '@api/tirosFileApi'
import { delWorkOrderAttached } from '@api/tirosDispatchApi'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import { everythingIsEmpty, randomUUID } from '@/utils/util'

export default {
  name: 'TaskAttached',
  components: { DocPreviewModal },
  props: {
    files: {
      type: Array,
      default: () => {
        return []
      },
    },
    tasks: {
      type: Array,
      default: () => {
        return []
      },
    },
    readOnly: {
      type: Boolean,
      default: false,
    },
    selectTask: {
      type: Object,
      default: null,
    },
    workOrderId: {
      type: String,
      default: '',
    },
    operator: {
      type: Number,
      default: 0,
    },
  },
  data() {
    return {
      curEditMode: 1, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      previewFileName: '',
      validRules: {
        name: [{ required: true, message: '请选择文件', trigger: 'manual' }],
      },
    }
  },
  methods: {
    getButtonShow() {
      if (this.readOnly) {
        return false
      }
      // 1 核实工单、4 提交工单  5 工单关闭
      if (this.operator === 0 || this.operator === 1 || this.operator === 4)  {
        return true
      } else {
        return false
      }
    },
    handleAdd() {
      if (this.$refs.listTable.getActiveRecord()) {
        return
      }
      this.curEditMode = 1
      let file = {
        id: randomUUID(),
        fileId: '',
        taskId: '',
        workOrderId: this.workOrderId,
        name: '',
        type: '',
        savepath: '',
        fileSize: '',
      }
      if (this.selectTask) {
        Object.assign(file, {
          taskId: this.selectTask.id,
          taskName: this.selectTask.taskName,
        })
      } else {
        if (this.tasks.length) {
          console.log(this.tasks)
          Object.assign(file, {
            orderTaskId: this.tasks[0].id,
            taskName: this.tasks[0].taskName,
            taskId: this.tasks[0].id,
          })
        }
      }
      this.$refs.listTable.insertAt(file, -1).then(({ row }) => {
        this.$refs.listTable.setActiveCell(row, 'name')
      })
    },
    handleDel() {
      if (this.$refs.listTable.getActiveRecord()) {
        return
      }
      let m = this.$refs.listTable.getCheckboxRecords()
      if (m.length > 0) {
        this.$confirm({
          content: `是否删除选中${m.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let promiseObj = Promise.resolve()
            m.map((item1) => {
              promiseObj.then(() => {
                return deleteFile(item1.savepath).then((res) => {
                  if (res.success) {
                    if (!everythingIsEmpty(item1.id)) {
                      let formData = new FormData()
                      formData.append('ids', item1.id)
                      return delWorkOrderAttached(formData).then((rs) => {
                        if (rs.success) {
                          this.files.splice(
                            this.files.findIndex((e) => e.savepath === item1.savepath),
                            1
                          )
                          this.$emit('removeFile', item1)
                        } else {
                          this.$message.error('文件删除失败')
                        }
                      })
                    } else {
                      return this.files.splice(
                        this.files.findIndex((e) => e.savepath === item1.savepath),
                        1
                      )
                    }
                  } else {
                    return this.$message.error('文件删除失败')
                  }
                })
              })
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    async handleDownload(data) {
      // await this.handlePrivilege(data.id, 3)
      // if (!this.status) {
      //   this.$message.error('您没有权限!')
      // } else {
      download(data.savepath)
      // }
    },
    editRowEvent(row) {
      this.curEditMode = 2
      this.$refs.listTable.setActiveRow(row)
    },
    delRowEvent(row) {
      this.files.map((item2, index2) => {
        if (row.id === item2.id) {
          this.files.splice(index2, 1)
        }
      })
    },
    beforeLedUpload(file) {
      if (file.size / 1024 / 1024 > 10) {
        this.$message.error('文件不得大于10MB!')
        return false
      }
      let reg = /([^\\/]+)\.([^\\/]+)/i
      reg.test(file.name)
      const name = RegExp.$1
      const type = RegExp.$2
      let formData = new FormData()
      var renameFile = new File([file], `${name}${this.$moment().format('YYYYMMDDhhmmss')}.${type}`, {
        type: file.type,
      })
      formData.append('file', renameFile)
      const hide = this.$message.loading('文件上传中···', 0);
      uploadFile(formData, '/WorkOrder').then((res) => {
        this.$refs.listTable.getActiveRecord().row.name = name
        this.$refs.listTable.getActiveRecord().row.type = type
        this.$refs.listTable.getActiveRecord().row.savepath = res.result
        this.$refs.listTable.getActiveRecord().row.fileSize = (file.size / 1024).toFixed(0)
        hide();
      }).catch((err) =>{
        this.$message.error('文件上传失败，请联系管理员')
        hide();
      })

      return false
    },
    previewImage(row) {
      this.previewFileName = row.name
      this.$refs.docPreview.handleFilePath(row.savepath)
      return false
    },
    taskSelectChange(value, option) {
      if (value) {
        this.$refs.listTable.getActiveRecord().row.taskName = this.tasks[option.key].taskName
      } else {
        this.$refs.listTable.getActiveRecord().row.taskName = ''
      }
    },
    saveRowEvent(row) {
      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          this.files.push(row)
          this.$refs.listTable.clearActived()
          this.curEditMode = 0
          this.$emit('addFile', row)
        } else {
          for (const validKey in valid) {
            let vals = valid[validKey]
            vals.forEach((item) => {
              if (item.rule) {
                this.$message.error(item.rule.message)
              }
            })
          }
        }
      })
    },
    cancelRowEvent(row) {
      this.$refs.listTable.clearActived()
      this.$refs.listTable.remove(row)
      this.curEditMode = 0
    },
    getTaskName(row) {
      console.log(this.selectTask)
      if (this.selectTask) {
        return this.selectTask.taskName
      } else {
        if (this.tasks.find((e) => e.id === row.taskId)) {
          return this.tasks.find((e) => e.id === row.taskId).taskName
        }
        return ''
      }
    },
    clearValidate() {
      this.$refs.listTable.clearValidate()
    },
  },
}
</script>

<style lang="less">
.attachedUploadBtn {
  width: 100%;
  display: block;

  .ant-upload {
    width: 100%;
  }
}
</style>