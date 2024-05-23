<template>
  <a-modal
    :width="500"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :destroyOnClose="true"
    :footer="null">
    <div>
      <img alt="没找到资源" style="width: 100%" :src="imgSrc" />
    </div>
  </a-modal>
</template>

<script>
  export default {
    name: 'QrcodeImgModal',
    data() {
      return {
        visible: false,
        confirmLoading: false,
        imgSrc:''
      }
    },
    methods: {
      qrCodeImg(data) {
        this.visible = true
        this.imgSrc = window._CONFIG['domianURL']+data
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