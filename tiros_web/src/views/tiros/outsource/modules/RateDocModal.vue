<template>
  <a-modal
    title="评分附件"
    :width="'80%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    cancelText="关闭"
    :footer="null"
    :destroyOnClose="true"
  >
         <a-card>
           <vxe-table
             border
             :data="tableData"
             show-overflow="tooltip"
             :edit-config="{ trigger: 'manual', mode: 'row' }"
           >
             <div class=""></div>
             <vxe-table-column
               type="seq"
               title="序号"
               width="50px"
               header-align="center"
               align="center"
             ></vxe-table-column>
             <vxe-table-column field="name" title="文件名" header-align="center" align="left"></vxe-table-column>
             <vxe-table-column
               field="type"
               title="文件类型"
               width="120px"
               header-align="center"
               align="center"
             ></vxe-table-column>
             <vxe-table-column
               field="fileSize"
               title="文件大小"
               width="180px"
               header-align="center"
               align="center"
             ></vxe-table-column>
             <vxe-table-column title="操作" width="160px" header-align="center" align="center">
               <template v-slot="{ row, rowIndex }">
                 <a style="margin-right: 12px" @click.stop="handleSeeing(row)">查看</a>
                 <a @click.stop="handleDelete(row,rowIndex)">删除</a>
               </template>
             </vxe-table-column>
           </vxe-table>
           <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
         </a-card>
  </a-modal>
</template>

<script>
import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import { delRateAttachment } from '@api/tirosOutsourceApi'

export default {
  name: 'RateDocModal',
  components: { DocPreviewModal },
  data () {
    return {
      visible: false,
      model: {},
      confirmLoading: false,
      tableData:[],
      fileName:'',
    }
  },
  methods:{
    show(data){
      this.visible=true
      this.tableData=data.annexes
    },
     handleSeeing(data) {
        this.fileName = data.name
        this.$refs.docPreview.handleFilePath(data.savepath)
      },
    handleDelete(data,index) {
      // 删除文件
      let that = this
      that.$confirm({
        title: '提示',
        content: '确定删除该条附件吗？',
        okText: '确定',
        okType: 'danger',
        cancelText: '取消',
        onOk() {
          delRateAttachment("ids="+data.id).then((res)=>{
            if(res.success){
              that.tableData.splice(index, 1)
              that.$message.success(res.message)
            }else {
              that.$message.error(res.message)
            }
          })
        },
        onCancel() {},
      })
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

<style scoped lang="less">
.content {
  .info-wrapper {
    border: 1px solid #eee;
    position: relative;
    border-radius: 8px;
    padding: 10px;
    margin-top: 10px;
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

  .ant-form-item {
    margin: 0;
  }
}
</style>