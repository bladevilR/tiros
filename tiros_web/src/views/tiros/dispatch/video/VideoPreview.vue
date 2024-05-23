<template>
  <a-modal
    :footer="null"
    title="预览"
    :visible="visible"
    width="100%"
    height="100%"
    dialogClass="fullDialog"
    :bodyStyle="{padding: '0 !important'}"
    @cancel="closeModal()"
    :destroyOnClose="false"
  >
    <div class="panel-image">
      <iframe class="filename" :src="fileUrl" width='100%' style="height: 100%" frameborder="0">
      </iframe>
    </div>
  </a-modal>
</template>

<script>
import { getAccessPath } from '@api/tirosFileApi'

export default {
  name: 'VideoPreview',
  data() {
    return {
      visible: false,
      conpomentName: 'CostWorkshopPage',
      fileUrl: ''
    }
  },
  methods: {
    showModal(fileUrl = '') {
      getAccessPath(fileUrl).then((res) => {
        if (res.success) {
          this.fileUrl = window._CONFIG['onlinePreviewDomainURL'] + '?url=' + encodeURIComponent(res.result)
          this.visible = true
        } else {
          this.$message.error('文件预览失败')
        }
      })

      
    },
    closeModal() {
      this.visible = false
    },
  },
}
</script>

<style lang="less" scoped>
.panel-image{
  width: 100%;
  height: calc(100%);

  text-align: center;
  position: relative;

  img{
    max-height: 100%;
    max-width: 100%;
    position: absolute;
    top: 0px;
    bottom: 0px;
    margin: auto;
    left: 0px;
    right: 0px;
  }
}
.filename {
  padding-left: 8px;
  padding-top: 8px;
  background-color: #f8f8f9;
}
</style>