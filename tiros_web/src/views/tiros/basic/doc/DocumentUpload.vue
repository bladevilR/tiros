<template>
  <a-modal
    width="800px"
    :visible="visible"
    :title="'文件上传'"
    centered
    :bodyStyle="{ height: '500px' }"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :destroyOnClose="true"
    :footer="null"
  >
    <a-form :form="form">
      <a-row :gutter="24">
        <a-col :md="24" :sm="24">
          <a-form-item>
            <DocUpload
              ref="upload"
              @upLoadingEnd="upLoadingEnd"
              :default-file-list="defaultFileList"
              @uploaded="successUploadFile"
              @removed="onRemoveFile"
              @uploadFail="uploadFail"
              :show-upload="false"
              :path="save_path"
            ></DocUpload>
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="6">
          <a-form-item label="是否共享？">
            <a-switch v-model="shared" />
          </a-form-item>
        </a-col>
        <a-col :span="6" v-if="shared">
          <a-form-item label="共享到">
            <!--            <j-tree-select
                          v-model="sharedId"
                          placeholder="请选择目录"
                          dict="bu_doc_directory,name,id"
                          pidField="parent_id"
                        >
                        </j-tree-select>-->
            <a-tree-select
              tree-data-simple-mode
              allow-clear
              :tree-data="selectTreeNode"
              :replaceFields="{ title: 'title', value: 'id' }"
              placeholder="请选择"
              :load-data="loadSelectNodeMethod"
              v-model="sharedId"
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row :gutter="24" style="margin-top: 8px">
        <a-col :md="24" :sm="24">
          <a-textarea :maxLength="201" @change="remarkChange" v-model="remark" placeholder="备注"></a-textarea>
        </a-col>
      </a-row>
    </a-form>
    <div id="fileTag">
      <template v-for="(tag) in tags">
        <a-tooltip v-if="tag.length > 10" :key="tag" :title="tag">
          <a-tag color="blue" :key="tag" :closable="true" @close="() => handleClose(tag)">
            {{ `${tag.slice(0, 10)}...` }}
          </a-tag>
        </a-tooltip>
        <a-tag color="blue" v-else :key="tag" :closable="true" @close="() => handleClose(tag)">
          {{ tag }}
        </a-tag>
      </template>
      <a-input
        v-if="inputVisible"
        ref="input"
        type="text"
        :style="{ width: '78px' }"
        :value="inputValue"
        @change="handleInputChange"
        @blur="handleInputConfirm"
        @keyup.enter="handleInputConfirm"
      />
      <a-tag v-else color="blue" style="background: #fff; borderstyle: dashed" @click="showInput">
        <a-icon type="plus" />
        添加标签
      </a-tag>
    </div>
    <div
      style="margin-top: 80px; position: absolute; bottom: 10px; text-align: right; padding-right: 25px; width: 100%"
    >
      <a-space>
        <a-button type="primary" :loading="isLoading" @click="beginUpload">上传至服务器</a-button>
        <a-button @click="visible = false">关闭</a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<script>
import JTreeSelect from '@/components/jeecg/JTreeSelect'
import { addFile, getSharedDir } from '@/api/tirosApi'
import DocUpload from '@views/tiros/common/doc/DocUpload'

export default {
  name: 'DocumentUpload',
  components: { JTreeSelect, DocUpload },
  props: ['save_path'],
  data () {
    return {
      selectTreeNode: [],
      form: this.$form.createForm(this),
      visible: false,
      confirmLoading: false,
      directoryId: '',
      isLoading: false,
      defaultFileList: [],
      shared: false,
      sharedId: '',
      tags: [],
      remark: '',
      inputVisible: false,
      inputValue: '',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      }
    }
  },
  watch: {
    shared: {
      immediate: false,
      handler (value) {
        if (value) {
          this.loadSelectNodeMethod(null)
        }
      }
    }
  },
  methods: {
    loadSelectNodeMethod (node) {
      return new Promise((resolve) => {
        getSharedDir(node==null?'-1':node.dataRef.id).then((res) => {
          if (res.success) {
            res.result.map((item) => {
              this.selectTreeNode = this.selectTreeNode.concat(this.genSelectTreeNode(item))
            })
          } else {
            this.$message.error(res.message)
          }
        })
        resolve()
      })
    },
    genSelectTreeNode (node) {
      return {
        id: node.id,
        pId: node.parentId,
        value: node.id,
        title: node.name,
        isLeaf: node.hasChildren ? false : true
      }
    },
    remarkChange(){
      if(this.remark.length>200){
        this.$message.error('备注不能超过255个字符');
        return false;
      }else{
        return true;
      }
    },
    showModal (dirId) {
      this.tags = []
      this.remark = ''
      this.shared = false
      this.sharedId = ''

      this.directoryId = dirId
      this.visible = true
    },
    successUploadFile (fileInfos) {
      this.confirmLoading = false
      if (!fileInfos || fileInfos.length < 1) {
        return
      }
      let info = {
        directoryId: this.directoryId,
        sharedId: this.sharedId,
        remark: this.remark,
        fileTags: this.tags.length > 0 ? this.tags.join(',') : ''
      }
      fileInfos.map((item) => {
        Object.assign(item, info)
      })
      // console.log('upload files:', fileInfos)
      addFile(fileInfos).then((res) => {
        if (res.success) {
          this.$message.success(res.message)
          this.$emit('ok')
           this.handleCancel()
        } else {
          this.$message.error(res.message)
        }
      })
    },
    uploadFail (file) {
      this.confirmLoading = false
      this.$message.error('上传失败！')
    },
    onRemoveFile (file) {
    },
    handleClose (removedTag) {
      const tags = this.tags.filter((tag) => tag !== removedTag)
      this.tags = tags
    },
    showInput () {
      this.inputVisible = true
      this.$nextTick(function () {
        this.$refs.input.focus()
      })
    },
    handleInputChange (e) {
      this.inputValue = e.target.value
    },
    handleInputConfirm () {
      const inputValue = this.inputValue
      let tags = this.tags
      if (inputValue && tags.indexOf(inputValue) === -1) {
        tags = [...tags, inputValue]
      }
      Object.assign(this, {
        tags,
        inputVisible: false,
        inputValue: ''
      })
    },
    beginUpload () {
      if(this.tags.length>10){
        this.$message.warn("最多只能添加10个标签！")
        return
      }
      this.isLoading = true
      this.confirmLoading = true
      this.$refs.upload.beginUpload()
    },
    upLoadingEnd () {
      this.isLoading = false
    },
    handleCancel(){
      this.visible=false
      this.selectTreeNode=[]
    }
  }
}
</script>

<style scoped lang="less">
#fileTag {
  margin-top: 10px;
  margin-left: 8px;

  .ant-tag {
    height: 30px;
    padding-top: 5px;
  }
}
</style>