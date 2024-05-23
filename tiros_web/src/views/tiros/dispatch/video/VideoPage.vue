<template>
  <div class="bodyWrapper">
    <a-row>
      <a-col :span="12">
        <div class="vd-box">
          <div style="margin-bottom: -20px">
            <a style="float: right; margin-top: 10px" target="_blank" @click.prevent="previewFile(1)">预览</a>
            <p style="font-size: 20px; font-weight: bold">{{ screenName }}</p>
          </div>
          <a-divider style="margin-bottom: 0" />
          <div>
            <a-row :gutter="24" type="flex" align="middle" style="height: 75px">
              <a-col flex="110px"> 投屏内容： </a-col>
              <a-col flex="200px" justify="center">
                <a-form-item
                  :labelCol="labelCol"
                  labelAlign="left"
                  :wrapperCol="wrapperCol"
                  label="大屏"
                  style="margin: 0"
                >
                  <!-- <j-dict-select-tag
                    class="na-display-jselect-clear-btn"
                    v-model="screen"
                    dictCode="bu_display_screen,screen_name,id,screen_type=1"
                    @changeName="getName"
                    style="width: 100%"
                  /> -->
                  <a-select v-model="screen" ref="select" @change="getName">
                    <a-select-option v-for="item in ledSetting" :key="item.screenCode" :value="item.id">{{
                      item.screenName
                    }}</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="9">
                <a-radio-group name="radioGroup" v-model="sysContentType" @change="handleContent">
                  <a-radio :value="1"> 系统内容 </a-radio>
                  <a-radio :value="2"> 自定义内容 </a-radio>
                </a-radio-group>
              </a-col>
            </a-row>
          </div>
          <div>
            <div :style="{ display: syscontents ? 'block' : 'none' }">
              <a-card style="min-height: 410px">
                <a-row :gutter="[28, 28]" type="flex">
                  <a-col
                    flex="190px"
                    v-for="item in screenResourceList"
                    :key="item.id"
                    class="column-imgae"
                    :class="{ active: activeLedItemAddress === item.url }"
                  >
                    <div style="width: 100%; height: 100%">
                      <h4>{{ item.title }}</h4>
                      <img class="img-box" :src="item.preview" @click="chooseLed(item.id)" />
                    </div>
                  </a-col>
                </a-row>
              </a-card>
            </div>
            <div :style="{ display: contents ? 'block' : 'none' }">
              <a-card style="min-height: 410px">
                <div style="margin-bottom: 30px; width: 300px; margin-left: 60px">
                  <a-upload
                    :before-upload="beforeLedUpload"
                    :multiple="false"
                    :file-list="activeLedFileList"
                    :remove="removeFile"
                    @preview="previewImage"
                    :disabled="uploading"
                  >
                    <a-button type="primary" :loading="uploading"> <a-icon v-if="!uploading" type="upload" /> 添加文件 </a-button>
                  </a-upload>
                </div>
              </a-card>
            </div>
          </div>
        </div>
      </a-col>
      <a-col :span="12">
        <div class="vd-box">
          <div style="margin-bottom: -20px">
            <a style="float: right; margin-top: 10px" @click.prevent="previewFile(2)">预览</a>
            <p style="font-size: 20px; font-weight: bold">{{ screenworkName }}</p>
          </div>
          <a-divider style="margin-bottom: 0" />
          <div>
            <a-row :gutter="24" type="flex" align="middle" style="height: 75px">
              <a-col flex="110px"> 投屏内容： </a-col>
              <a-col flex="280px" justify="center">
                <a-form-item
                  :labelCol="labelCol"
                  :wrapperCol="wrapperCol"
                  labelAlign="left"
                  label="工班"
                  style="margin: 0"
                >
                  <!-- <j-dict-select-tag
                    class="na-display-jselect-clear-btn"
                    v-model="screenwork"
                    dictCode="bu_mtr_workshop_group,group_name,id"
                    @changeName="getNamework"
                    style="width: 100%"
                  /> -->
                  <a-select v-model="screenwork" ref="select" @change="getNamework" style="width: 100%">
                    <a-select-option v-for="item in workSetting" :key="item.id" :value="item.id">{{
                      item.groupName
                    }}</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col :span="9">
                <a-radio-group name="radioGroup" v-model="sysGroupContentType" @change="handleContentWork">
                  <a-radio :value="1"> 系统内容 </a-radio>
                  <a-radio :value="2"> 自定义内容 </a-radio>
                </a-radio-group>
              </a-col>
            </a-row>
          </div>
          <div style="margin-top: 0">
            <div :style="{ display: syscontentswork ? 'block' : 'none' }">
              <a-card style="min-height: 410px">
                <a-row :gutter="[24, 24]" type="flex">
                  <a-col
                    flex="190px"
                    v-for="item in screenResourceList"
                    :key="item.id"
                    class="column-imgae"
                    :class="{ active: activeWorkItemAddress === item.url }"
                  >
                    <div style="width: 100%; height: 100%">
                      <h4>{{ item.title }}</h4>
                      <img class="img-box" :src="item.preview" @click="chooseWork(item.id)" />
                    </div>
                  </a-col>
                </a-row>
              </a-card>
            </div>
            <div :style="{ display: contentswork ? 'block' : 'none' }">
              <a-card style="min-height: 410px">
                <div style="margin-bottom: 30px; width: 300px; margin-left: 60px">
                  <a-upload
                    :before-upload="beforeWorkUpload"
                    :multiple="false"
                    :file-list="activeWorlFileList"
                    :remove="removeWorkFile"
                    :disabled="uploading"
                  >
                    <a-button type="primary" :loading="uploading"> <a-icon v-if="!uploading" type="upload" /> 添加文件 </a-button>
                    <!-- <a-button style="background-color: #67c23a; margin-left: 8px; color: white">上传至服务器</a-button> -->
                  </a-upload>
                </div>
              </a-card>
            </div>
          </div>
        </div>
      </a-col>
    </a-row>
    <video-preview ref="preview"></video-preview>
    <!-- <Videochd ref="modalForm" @ok="loadData()"></Videochd> -->
  </div>
</template>

<script>
import Videochd from '@views/tiros/dispatch/video/Videochd'
import {
  getPlayContent,
  getPlayContentWork,
  getPlayScreenList,
  getPlayResourceList,
  setPlayResourceToScreen,
  setPlayCustomToScreen,
  deletePlayCustomToScreen,
} from '@/api/tirosDispatchApi'
import VideoPreview from '@views/tiros/dispatch/video/VideoPreview'
import DocUpload from '@views/tiros/common/doc/DocUpload'
import { uploadFile, getAccessPath } from '@api/tirosFileApi'

export default {
  name: 'VideoPage',
  components: { Videochd, VideoPreview, DocUpload },
  data() {
    return {
      ledSetting: [],
      workSetting: [],
      syscontents: true,
      contents: false,
      syscontentswork: true,
      contentswork: false,
      Result: [],
      Resultwork: [],
      sysContentType: 1,
      sysGroupContentType: 1,
      count: '',
      screen: '1',
      screenName: 'led大屏1',
      screenwork: null,
      screenworkName: '工班显示',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
      },
      screenResourceList: [],
      uploading: false
    }
  },
  computed: {
    activeLedItem() {
      return this.ledSetting.find((e) => {
        return e.id === this.screen
      })
    },
    activeLedItemAddress() {
      if (this.activeLedItem) {
        return this.activeLedItem.routeList.length > 0 ? this.activeLedItem.routeList[0].url : ''
      }
      return ''
    },
    activeLedPreviewUrl() {
      if (this.activeLedItem) {
        return this.activeLedItem.fileList.length > 0 ? this.activeLedItem.fileList[0].url : ''
      }
      return ''
    },
    activeLedFileList() {
      return this.activeLedItem ? this.activeLedItem.fileList : []
    },

    activeWorkItem() {
      return this.workSetting.find((e) => {
        return e.id === this.screenwork
      })
    },
    activeWorkItemAddress() {
      if (this.activeWorkItem) {
        return this.activeWorkItem.routeList.length > 0 ? this.activeWorkItem.routeList[0].url : ''
      }
      return ''
    },
    activeWorlPreviewUrl() {
      if (this.activeWorkItem) {
        return this.activeWorkItem.fileList.length > 0 ? this.activeWorkItem.fileList[0].url : ''
      }
      return ''
    },
    activeWorlFileList() {
      return this.activeWorkItem ? this.activeWorkItem.fileList : []
    },
  },
  created() {
    this.initLedSetting().then(() => {
      this.findList()
    })
    this.initWorkSetting().then(() => {
      this.findListwork()
    })
    this.findScreenList()
  },
  methods: {
    initFileList(settingList) {
      let reg = /^\/video/g
      settingList.forEach((e, index) => {
        if ((e.screenType == 1 && e.id == this.screen) || (e.screenType == 2 && e.id == this.screenwork)) {
          getPlayContent({ screenId: e.id }).then((res) => {
            if (res.success && res.result.length > 0) {
              settingList[index].fileList.length = 0
              settingList[index].routeList.length = 0
              res.result.forEach((element) => {
                if (reg.test(element.address)) {
                  settingList[index].fileList.push({
                    uid: element.id,
                    name: element.title,
                    status: 'done',
                    url: element.address,
                  })
                } else {
                  settingList[index].routeList.push({
                    uid: element.id,
                    name: element.title,
                    status: 'done',
                    url: element.address,
                  })
                }
              })
            }
          })
        }
      })
    },
    async initWorkSetting() {
      if (this.ledSetting.length > 0) {
        this.workSetting.forEach((element, index) => {
          this.$set(this.workSetting[index], 'fileList', [])
          this.$set(this.workSetting[index], 'routeList', [])
        })
        this.initFileList(this.workSetting)
        return
      } else {
        return getPlayScreenList({
          screenType: 2,
        }).then((res) => {
          if (res.success && res.result.length > 0) {
            this.workSetting = res.result
            if (!this.screenwork && this.workSetting.length > 0) {
              this.screenwork = this.workSetting[0].id
            }
            this.workSetting.forEach((element, index) => {
              this.$set(this.workSetting[index], 'fileList', [])
              this.$set(this.workSetting[index], 'routeList', [])
            })
            this.initFileList(this.workSetting)
          }
        })
      }
    },
    async initLedSetting() {
      if (this.ledSetting.length > 0) {
        this.ledSetting.forEach((element, index) => {
          // // 测试数据
          this.$set(this.ledSetting[index], 'fileList', [])
          this.$set(this.ledSetting[index], 'routeList', [])
        })
        this.initFileList(this.ledSetting)
        return
      } else {
        return getPlayScreenList({
          screenType: 1,
        }).then((res) => {
          if (res.success && res.result.length > 0) {
            this.ledSetting = res.result
            this.ledSetting.forEach((element, index) => {
              // // 测试数据
              this.$set(this.ledSetting[index], 'fileList', [])
              this.$set(this.ledSetting[index], 'routeList', [])
            })
            this.initFileList(this.ledSetting)
          }
        })
      }
    },
    async findList() {
      this.loading = true
      let screen = {
        screenId: this.screen,
      }
      return getPlayContent(screen).then((res) => {
        this.Result = res.result
        if (res.success && this.Result.length > 0) {
          let reg = /^\/video/g
          if (reg.test(this.Result[0].address)) {
            //自定义内容
            this.sysContentType = 2
            this.contents = true
            this.syscontents = false
          } else {
            this.sysContentType = 1
            this.contents = false
            this.syscontents = true
          }
          this.loading = false
        }
      })
    },
    async findListwork() {
      this.loading = true
      let screenwork = {
        screenId: this.screenwork,
      }
      getPlayContentWork(screenwork).then((res) => {
        this.loading = false
        if (res.success && res.result.length > 0) {
          let reg = /^\/video/g
          if (reg.test(res.result[0].address)) {
            //自定义内容
            this.sysGroupContentType = 2
            this.contentswork = true
            this.syscontentswork = false
          } else {
            this.sysGroupContentType = 1
            this.contentswork = false
            this.syscontentswork = true
          }
          this.loading = false
        }
      })
    },
    findScreenList() {
      getPlayResourceList({}).then((res) => {
        if (res.success && res.result.length > 0) {
          this.screenResourceList = res.result
        }
      })
    },
    previewFile(type) {
      if (type === 1) {
        if (this.sysContentType === 1) {
          // this.$router.push({ path: this.activeLedItemAddress })
          let routeData = this.$router.resolve({
            path: this.activeLedItemAddress
              
          })
          window.open(routeData.href, '_blank')
          console.log(routeData)
        } else {
          this.$refs.preview.showModal(this.activeLedPreviewUrl)
        }
      }
      if (type === 2) {
        if (this.sysGroupContentType === 1) {
          // this.$router.push({ path: this.activeWorkItemAddress })
          let routeData = this.$router.resolve({
            path: this.activeWorkItemAddress
              
          })
          window.open(routeData.href, '_blank')
          console.log(routeData)
        } else {
          this.$refs.preview.showModal(this.activeWorlPreviewUrl)
        }
      }
    },

    getName(data, node) {
      this.screenName = this.ledSetting.find((e) => e.screenCode == data).screenName
      this.findList().then(() => {
        this.initLedSetting()
      })
    },
    getNamework(value, node) {
      this.findListwork().then(() => {
        this.initWorkSetting()
      })
    },
    handleContent(e) {
      if (e.target.value == 1) {
        this.contents = false
        this.syscontents = true
      }
      if (e.target.value == 2) {
        this.contents = true
        this.syscontents = false
      }
    },
    handleContentWork(e) {
      if (e.target.value == 1) {
        this.contentswork = false
        this.syscontentswork = true
      }
      if (e.target.value == 2) {
        this.contentswork = true
        this.syscontentswork = false
      }
    },
    chooseLed(id) {
      this.activeLedItem.fileList.forEach((e) => {
        this.removeFile(e)
      })
      this.activeLedItem.routeList.forEach((e) => {
        this.removeFile(e)
      })
      let format = new FormData()
      format.append('resourceId', id)
      format.append('screenId', this.activeLedItem.id)
      setPlayResourceToScreen(format).then((res) => {
        if (res.success) {
          this.initLedSetting()
        } else {
          this.$message.error('保存失败')
        }
      })
    },
    chooseWork(id) {
      this.activeWorkItem.fileList.forEach((e) => {
        this.removeWorkFile(e)
      })
      this.activeWorkItem.routeList.forEach((e) => {
        this.removeWorkFile(e)
      })
      let format = new FormData()
      format.append('resourceId', id)
      format.append('screenId', this.activeWorkItem.id)
      setPlayResourceToScreen(format).then((res) => {
        if (res.success) {
          this.initWorkSetting()
        } else {
          this.$message.error('保存失败')
        }
      })
    },
    beforeLedUpload(file, files) {
      if (this.uploading) {
        return
      }
      this.uploading = true
      if (this.activeLedItem) {
        // 清空配置
        this.activeLedItem.fileList.forEach((e) => {
          this.removeFile(e)
        })
        this.activeLedItem.routeList.forEach((e) => {
          this.removeFile(e)
        })
        this.beginUploadFile(this.activeLedItem.id, file).then((res) => {
          if (res.success) {
            this.initFileList(this.ledSetting)
          } else {
            this.$message.error(res.message)
          }
          this.uploading = false
        }).catch((err) => {
          this.uploading = false
        });
      } else {
        this.$message.error('大屏信息配置错误')
        this.uploading = false
      }
      return false
    },
    beforeWorkUpload(file) {
      if (this.uploading) {
        return
      }
      this.uploading = true
      if (this.activeWorkItem) {
        // 清空配置
        this.activeWorkItem.fileList.forEach((e) => {
          this.removeWorkFile(e)
        })
        this.activeWorkItem.routeList.forEach((e) => {
          this.removeWorkFile(e)
        })
        this.beginUploadFile(this.activeWorkItem.id, file).then((res) => {
          if (res.success) {
            this.initFileList(this.workSetting)
          } else {
            this.$message.error(res.message)
          }
          this.uploading = false
        }).catch((err) => {
          this.uploading = false
        });
      } else {
        this.uploading = false
        this.$message.error('大屏信息配置错误')
      }
      return false
    },
    // 保存自定义内容
    async beginUploadFile(screenId, file) {
      let formData = new FormData()
      formData.append('file', file)
      return uploadFile(formData, '/video').then((res) => {
        let formData2 = new FormData()
        formData2.append('filePath', res.result)
        formData2.append('screenId', screenId)
        formData2.append('title', file.name)
        return setPlayCustomToScreen(formData2)
      }).catch((err) =>{
        this.$message.error('文件上传失败，请联系管理员')
      })
    },
    removeFile(fileInfo) {
      let formData = new FormData()
      formData.append('ids', fileInfo.uid)
      deletePlayCustomToScreen(formData).then((res) => {
        if (res.success) {
          this.initLedSetting()
        }
      })
    },
    removeWorkFile(fileInfo) {
      let formData = new FormData()
      formData.append('ids', fileInfo.uid)
      deletePlayCustomToScreen(formData).then((res) => {
        if (res.success) {
          this.initWorkSetting()
        }
      })
    },
    previewImage(fileInfo) {
      this.$refs.preview.showModal(this.activeLedPreviewUrl)
      return false
    },
  },
}
</script>

<style lang="less" scoped>
.bodyWrapper {
  padding: 15px;
}
.vd-box {
  height: 280px;
  width: 95%;
}
.img-box {
  height: 100px;
  width: 100%;
}
.active {
  box-shadow: 0px 0px 10px rgba(155, 224, 254, 0.94);
  border-radius: 5px;
  background: #89dbff;
}
.display-dom {
  display: none;
}
.column-imgae {
  text-align: center;
  padding: 6px !important;
  margin: 8px;
  /* border: 1px solid; */
  img {
    cursor: pointer;
  }
}
</style>