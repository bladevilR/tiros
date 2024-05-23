<template>
  <a-card id="rightDocument">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="7" :sm="24">
            <a-form-item label="文件名称">
              <a-input placeholder="请输入文件名称" v-model="queryParam.name" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="7" :sm="24">
            <a-form-item label="显示">
              <!--  0 文件和目录， 1 目录， 2  文件 -->
              <j-dict-select-tag v-model="queryParam.showType" placeholder="请选择" dictCode="bu_file_show_type" />
            </a-form-item>
          </a-col>

          <a-col :md="7" :sm="24">
            <a-form-item label="文件类型">
              <a-select placeholder="请选择" v-model="queryParam.type" allowClear>
                <a-select-option :value="undefined">请选择</a-select-option>
                <a-select-option value="">所有文件</a-select-option>
                <a-select-option v-for="t in fileType" :key="t" :value="t">
                  {{ t }}
                </a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col :md="9" :sm="12">
            <span class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleUpload">上传</a-button>
                <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
                <!--<a-button type="primary" @click="handleUpload"  v-else  v-has="'doc:admin:upload'">上传</a-button>
                <a-button type="primary" @click="handleUpload"  v-else v-has="'doc:group:upload'">上传</a-button>-->
                <a-button @click="handleAddDic">新建目录</a-button>
                <!--<a-button @click="handleAddDic" v-else v-has="'doc:admin:add'">新建目录</a-button>
                <a-button @click="handleAddDic" v-else v-has="'doc:group:add'">新建目录</a-button>-->
                <a-button :disabled="records.length < 1" @click="handleAddPrivilege">设置权限</a-button>
                <!--<a-button @click="handleAddPrivilege"  v-has="'doc:group:setting'">设置权限</a-button>-->
                <a-button :disabled="records.length < 1" @click="deleteRecord">删除</a-button>
                <a-button @click="findList">查询</a-button>
                <!--<a-button @click="deleteRecord" v-else v-has="'doc:admin:delete'">删除</a-button>
                <a-button @click="deleteRecord"  v-else v-has="'doc:group:delete'">删除</a-button>-->
                <!--<a-dropdown>
                <a-menu slot="overlay"> &lt;!&ndash;@click="handleMenuClick"&ndash;&gt;
                &lt;!&ndash;<a-menu-item key="1" @click="handleAddDic" v-if="dirParentId=='1'" >新建目录</a-menu-item>&ndash;&gt;
                <a-menu-item key="1" @click="handleAddDic"   v-has="'doc:group:create'">新建目录</a-menu-item>
                <a-menu-item key="1" @click="handleAddDic"   v-has="'doc:group:create'">新建目录</a-menu-item>
                <a-menu-item key="2" @click="handleAddPrivilege" v-has="'doc:admin:create'">设置权限</a-menu-item>
                </a-menu>
                <a-button type="primary">更多按钮<a-icon type="down" /></a-button>
                </a-dropdown>-->
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 282px)">
      <vxe-table
        border
        auto-resize
        max-height="100%"
        style="height: calc(100vh - 282px)"
        ref="listTable"
        :data="tableData"
        show-overflow="tooltip"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        header-align="center"
        align="center"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column title="-" width="90">
          <template v-slot="{ row }">
            <span v-if="row.isFile === 1">
              <a @click.stop="handleSeeing(row)">查看</a>
              <a @click.stop="handleDownload(row)" style="margin-left: 3px">下载</a>
            </span>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="name"
          title="文件名称"
          min-width="160"
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column field="type" title="文件类型" width="80">
          <template v-slot="{ row }">
            {{ row.type }}
            <!--  下面的方式，如果是jpg类型，则无法显示 jpg.png-->
            <!--          <img :src="require('@assets/images/filetype/'+row.type+'.png')" style="width: 30px;height: 30px"/>-->
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="fileSize"
          title="大小(KB)"
          width="100"
          header-align="center"
          align="right"
        ></vxe-table-column>
        <vxe-table-column
          field="uploadDate"
          title="上传日期"
          width="100"
          header-align="center"
          align="right"
        ></vxe-table-column>
        <vxe-table-column field="fileTags" title="标签" min-width="120" header-align="center" align="left">
          <template v-slot="{ row }">
            <span v-if="row.fileTags">
              <a-tag v-for="(item, index) in row.fileTags.split(',')" :key="index" color="blue">{{ item }}</a-tag>
            </span>
          </template>
        </vxe-table-column>
        <vxe-table-column field="remark" title="备注" width="160"></vxe-table-column>
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
    <add-directory-modal ref="modalForm" @ok="loadData()" :directoryId="directoryId"></add-directory-modal>
    <edit-directory-modal ref="editModalForm" @ok="loadData()"></edit-directory-modal>
    <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
    <document-upload :save_path="curPath" ref="docUpload" @ok="findList()"></document-upload>
    <privilege-add ref="privilegeAdd" @ok="loadData()"></privilege-add>
  </a-card>
</template>

<script>
import { delDocument, getDirParentId, getDocumentPage, getFileType, isPrivilege } from '@/api/tirosApi'
import { download } from '@api/tirosFileApi'
import AddDirectoryModal from '../modules/doc/AddDirectoryModal'
import EditDirectoryModal from '../modules/doc/EditDirectoryModal'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import DocumentUpload from '@views/tiros/basic/doc/DocumentUpload'
import PrivilegeAdd from '@views/tiros/basic/doc/PrivilegeAdd'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'RightDocument',
  props: ['directoryId', 'curPath'],
  components: { AddDirectoryModal, EditDirectoryModal, DocPreviewModal, DocumentUpload, PrivilegeAdd },
  data() {
    return {
      records:[],
      toggleSearchStatus: false,
      fileType: [],
      filePath: '',
      fileName: '',
      queryParam: {
        id: '',
        name: '',
        showType: '2',
        type: null,
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      tableData: [],
      showEdit: false,
      status: false,
      dirParentId: '',
    }
  },
  created() {
    this.getFileType()
    this.findList()
  },
  watch: {
    directoryId: {
      immediate: false,
      handler(id) {
        this.queryParam.id = id
        this.records = [];
        this.tableData = []
        this.findList()
        this.getDirParentId(id)
      },
    },
  },
  methods: {
    checkboxChange(e){
      this.records = e.records;
    },
    getDirParentId(id) {
      getDirParentId(id).then((res) => {
        if (res.success) {
          this.dirParentId = res.result
        }
      })
    },
    getFileType() {
      getFileType().then((res) => {
        this.fileType = res.result
      })
    },
    async handlePrivilege(id, operation) {
      let param = { id: id, operation: operation }
      await isPrivilege(param).then((res) => {
        this.status = res.result
      })
    },
    async handleSeeing(data) {
      await this.handlePrivilege(data.id, 2)
      if (!this.status) {
        this.$message.error('您没有权限!')
      } else {
        this.fileName = data.fileName
        // this.filePath = window._CONFIG['onlinePreviewDomainURL'] + '?url=' + encodeURIComponent(data.savepath)
        this.$refs.docPreview.handleFilePath(data.savepath)
      }
    },
    async handleDownload(data) {
      await this.handlePrivilege(data.id, 3)
      if (!this.status) {
        this.$message.error('您没有权限!')
      } else {
        download(data.savepath)
      }
    },

    findList() {
      getDocumentPage(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.records = [];
        this.tableData = res.result.records
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
      this.$emit('selectTree')
    },
    async handleUpload() {
      if (this.directoryId) {
        await this.handlePrivilege(this.directoryId, 1)
        if (!this.status) {
          this.$message.error('您没有权限!')
        } else {
          this.$refs.docUpload.showModal(this.directoryId)
        }
      } else {
        this.$message.warn('请选择目录!')
      }
    },
    async handleEdit(record) {
      await this.handlePrivilege(record.id, 5)
      if (!this.status) {
        this.$message.error('您没有权限!')
      } else {
        this.$refs.editModalForm.edit(record)
        this.$refs.editModalForm.title = '编辑'
      }
    },
    async handleAddDic() {
      if (this.directoryId) {
        await this.handlePrivilege(this.directoryId, 5)
        if (!this.status) {
          this.$message.error('您没有权限!')
        } else {
          this.$refs.modalForm.add()
          this.$refs.modalForm.title = '新增'
        }
      } else {
        this.$message.warn('请选择目录!')
      }
    },
    async handleAddPrivilege() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (this.directoryId || selectRecords.length === 1) {
        let dicId = this.directoryId
        let objType = 1
        if (selectRecords.length === 1) {
          dicId = selectRecords[0].id
          objType = selectRecords[0].isFile === 0 ? 1 : 2
        } else if (selectRecords.length > 1) {
          this.$message.warn('只能选择一条数据!')
          return
        }
        await this.handlePrivilege(dicId, 5)
        if (!this.status) {
          this.$message.error('您没有权限!')
        } else {
          let data = {
            directoryId: dicId,
            objType: objType,
          }
          this.$refs.privilegeAdd.show(data)
        }
      } else if (selectRecords.length > 1) {
        this.$message.warn('只能选择一条数据!')
      } else {
        this.$message.warn('请选择目录或者文件!')
      }
    },
    getSelectEvent() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      this.$XModal.alert(selectRecords.length)
    },
    async deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (!selectRecords.length > 0) {
        if (!everythingIsEmpty(this.directoryId)) {
          if (
            this.directoryId !== '1' &&
            this.directoryId !== '2' &&
            this.directoryId !== '3' &&
            this.directoryId !== '4'
          ) {
            selectRecords.push({ id: this.directoryId })
          } else {
            this.$message.warn('初始化数据不能删除!')
            return
          }
        }
      }
      if (selectRecords.length > 0) {
        for (const item of selectRecords) {
          if (item.id !== '1' && item.id !== '2' && item.id !== '3' && item.id !== '4') {
            await this.handlePrivilege(item.id, 4)
            if (!this.status) {
              this.$message.error('您没有权限!')
              return
            }
          } else {
            this.$message.warn('初始化数据不能删除!')
            return
          }
        }
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          okText: '确认',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delDocument('ids=' + idsStr).then((res) => {
              if (res.success) {
                this.$emit('selectTree')
                this.findList()
                this.$message.success(res.message)
              } else {
                this.$message.error(res.message)
              }
            })
            // selectRecords.forEach(item => delFile('fileName='+item.name ))
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
  },
}
</script>

<style lang="less">
#rightDocument {
  .ant-card-body {
    padding: 5px;
    height: calc(100vh - 115px);
    // overflow-y: auto;
  }
}
</style>