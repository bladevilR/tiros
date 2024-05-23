<template>
  <div id="videoImage">
    <img v-if="!isVideo" :src="fileUrl" />
    <video v-if="isVideo" ref="videoElement" :src="fileUrl" autoplay="autoplay" loop="loop" name="media">
    </video>
    <div v-if="spanLoading" style="position: fixed;">
      <a-spin class="loading-animation" size="large"></a-spin>
    </div>
  </div>
</template>

<script>
import { getAccessPath } from '@api/tirosFileApi'


export default {
  name: 'VideoPreview',
  data() {
    return {
      visible: false,
      conpomentName: 'CostWorkshopPage',
      fileUrl: '',
      windowEvent: window.ledFileUrl,
      isVideo: false,
      spanLoading: true
    }
  },
  created() {
    window.addEventListener('Led_Route_video', this.listenerFun)
  },
  destroyed() {
    window.removeEventListener('Led_Route_video', this.listenerFun)
    if (this.$refs.videoElement) {
      this.$refs.videoElement.removeEventListener('error', this.onload)
      this.$refs.videoElement.removeEventListener('play', this.onPlaying)
    }
    this.fileUrl = ''
  },
  mounted(){
    this.$nextTick(()=>{
      if (window.ledFileUrl) {
        this.loadingFile()
      }
    })
  },
  methods: {
    listenerFun({detail}){
      this.loadingFile()
    },
    loadingFile(){
      // 仅支持MP4格式
      const mp4Reg = new RegExp('.mp4', 'i')
      this.isVideo = (mp4Reg.test(window.ledFileUrl))
      this.spanLoading = this.isVideo
      this.$nextTick(()=>{
        if (this.isVideo && this.$refs.videoElement) {
          this.$refs.videoElement.removeEventListener('error', this.onload)
          this.$refs.videoElement.addEventListener('error', this.onload)
          this.$refs.videoElement.removeEventListener('play', this.onPlaying)
          this.$refs.videoElement.addEventListener('play', this.onPlaying)
        }
        getAccessPath(window.ledFileUrl).then((res) => {
          if (res.success) {
            if (this.$refs.videoElement) {
              this.fileUrl = res.result
              this.$nextTick(() => {
                this.$refs.videoElement.load()
              })
            } else {
              this.fileUrl =  res.result
            }
          
          } else {
            this.$message.error('文件预览失败')
          }
        })
      })
    },
    onload(err){
      console.log('重新加载');
      setTimeout(() => {
        this.$refs.videoElement.load()
      }, 500);
    },
    onPlaying(){
      console.log('开始播放');
      this.spanLoading = false
    }
  },
}
</script>

<style lang="less" scoped>
#videoImage {
  width: 100%;
  height: 100vh;
  
  display: flex;
  justify-content: center;
  align-items: center;

  img{
    max-width: 100%;
    max-height: 100%;
  }
  video{
    width: 100%;
    height: 100%;
  }
}
</style>