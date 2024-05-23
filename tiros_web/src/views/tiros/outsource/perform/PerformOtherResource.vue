<template>
    <div class="bodyWrapper">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="24">
            <a-col :md="8" :sm="24">
              <a-form-item label="文件名称">
                <a-input placeholder="请输入文件名称" v-model="queryParam.fileName" allowClear></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="资料类型">
                <j-dict-select-tag
                  v-model="queryParam.fileType"
                  placeholder="请选择"
                  dictCode="bu_outin_other_file_type"
                />
              </a-form-item>
            </a-col>
            <a-col :md="10" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
              <a-button @click="findList">查询</a-button>
              <a-button @click="handleUpload" >上传</a-button>
               <a-button @click="deleteRecord">删除</a-button>
              </a-space>
            </span>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <div style="height: calc(100vh - 318px)">
        <vxe-table
          border
          ref="listTable"
          height="auto"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
          :checkbox-config="{trigger: 'row', highlight: true, range: true}"
          :edit-config="{trigger: 'manual', mode: 'row'}"
        >
          <vxe-table-column type="checkbox" width="5%" ></vxe-table-column>
          <vxe-table-column  title="-" width="8%" >
            <template v-slot="{ row }">
            <span>
              <a-space>
                 <a @click.stop="handleSeeing(row)">查看</a>
                <a @click.stop="handleDownload(row)">下载</a>
              </a-space>
            </span>
            </template>

          </vxe-table-column>
          <vxe-table-column field="name" title="资料名称" width="25%" header-align="center" align="left"></vxe-table-column>
          <vxe-table-column field="type" title="文件类型" width="8%">
<!--            <template v-slot="{ row }" >
              <img  v-if="row.type!=null" :src="require('@assets/images/filetype/'+row.type+'.png')" style="width: 30px;height: 30px"/>
              <span v-else>{{row.type}}</span>
            </template>-->
          </vxe-table-column>
          <vxe-table-column field="fileSize" title="大小(KB)" width="8%" header-align="center" align="right"></vxe-table-column>
          <vxe-table-column field="uploadDate" title="上传日期" width="10%"></vxe-table-column>
          <vxe-table-column field="remark" title="备注" width="25%" header-align="center" align="left"></vxe-table-column>
          <vxe-table-column title="操作" width="11%">
            <template v-slot="{ row }">
              <a @click.stop="handleEdit(row)">编辑</a>
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
      <resource-upload-module ref="modalForm" @ok="loadData()" :outinDetailId="outinDetailId" :other="true"></resource-upload-module>
      <edit-file-modal ref="editModalForm" @ok="loadData()" :other="true"></edit-file-modal>
      <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
    </div>
</template>

<script>
import { delOtherResource, getOtherResource, } from '@api/tirosOutsourceApi'
import EditFileModal from '@views/tiros/outsource/modules/EditFileModal'
import ResourceUploadModule from '@views/tiros/outsource/modules/ResourceUploadModule'
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import { download } from '@api/tirosFileApi'

  export default {
    name: 'PerformOtherResource',
    props:['outinDetailId'],
    components:{ResourceUploadModule,EditFileModal,DocPreviewModal},
    data(){
      return{
        queryParam:{
          fileName:'',
          outinDetailId: '',
          fileType: null,
          pageNo: 1,
          pageSize: 10
        },
        fileName:'',
        filePath:'',
        totalResult: 0,
        allAlign: 'center',
        tableData: []
      }
    },
    created() {
      this.findList()
    },
    methods:{
      findList() {
        this.queryParam.outinDetailId=this.outinDetailId
          getOtherResource(this.queryParam).then((res) => {
          this.totalResult = res.result.total
          this.loading = false
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
      },
      handleUpload(){
        this.$refs.modalForm.handleAdd()
      },
      handleSeeing(data) {
        this.fileName = data.name
        this.$refs.docPreview.handleFilePath(data.savepath)
      },
      handleDownload(data) {
        download(data.savepath)
      },
      handleEdit(record) {
        this.$refs.editModalForm.edit(record)
        this.$refs.editModalForm.title = '编辑'
      },
      deleteRecord() {
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        if (selectRecords.length > 0) {
          this.$confirm({
            content: `是否删除选中${selectRecords.length}数据？`,
            okText:"确认",
            cancelText:'取消',
            onOk: () => {
              let idsStr = selectRecords
                .map(function(obj, index) {
                  return obj.id
                }).join(',')
              delOtherResource('ids=' + idsStr).then((res) => {
                if(res.success)
                  this.findList()
                this.$message.success(res.message)
              })
            }
          })
        } else {
          this.$message.error('尚未选中任何数据!')
        }
      }
    }

  }
</script>

<style scoped>

</style>