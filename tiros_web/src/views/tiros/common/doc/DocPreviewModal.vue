<template>
  <a-modal
    width="80%"
    :visible="visible"
    :title="title"
    centered
    :bodyStyle="{height: '80vh'}"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :destroyOnClose="true"
    :footer="null">
    <div>
      <iframe class="filename" :src="filePath" width='100%' style="height: calc(80vh - 25px)" frameborder="0">
      </iframe>
    </div>
  </a-modal>
</template>

<script>
import {getAccessPath} from '@api/tirosFileApi'

export default {
    name: 'DocPreviewModal',
    props:{
      title: {
        type:String,
        default: '文件预览'

      }
    },
    data() {
      return {
        visible: false,
        confirmLoading: false,
        filePath:''
      }
    },
    methods: {
      handleFilePath(filePath) {
        getAccessPath(filePath).then(res => {
          if (res.success) {
            let fileReal = res.result
            this.visible = true
            this.filePath = window._CONFIG['onlinePreviewDomainURL'] + '?url=' + encodeURIComponent(fileReal)
          } else {
            this.$message.warning('查看文件失败')
          }
        })
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
<style scoped lang="less">
  .filename{
    padding-left: 8px;
    padding-top: 8px;
    background-color: #F8F8F9;
  }

</style>