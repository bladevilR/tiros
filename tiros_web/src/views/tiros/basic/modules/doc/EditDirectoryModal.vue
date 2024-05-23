<template>
  <a-modal
    :title="title"
    :width="700"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    :destroyOnClose="true"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="名称">
              <a-input placeholder="名称" v-decorator.trim="[ 'name', validatorRules.name]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="24" :sm="24">
            <a-form-item label="备注" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <a-textarea v-decorator="[ 'remark',validatorRules.remark]" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24" v-if="model.isFile==1">
          <a-col :md="24" :sm="24">
            <a-form-item label="标签" :labelCol="labelCol" :wrapperCol="wrapperCol">
              <div id="fileTag">
                <template v-for="(tag, index) in tags">
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
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </a-spin>
  </a-modal>

</template>

<script>


import { editDirectoryOrFile } from '@api/tirosApi'
import { everythingIsEmpty } from '@/utils/util'

export default {
  name: 'EditDirectoryModal',
  data () {
    return {
      title: '操作',
      visible: false,
      model: {},
      tags: [],
      inputVisible: false,
      inputValue: '',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 }
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        name: { rules: [{ required: true, message: '请输入名称!' },{ max:50, message: '输入长度不能超过50字符!' }] },
        remark: { rules: [{ max:255, message: '输入长度不能超过255字符!' }] }
      }
    }
  },
  created () {

  },
  methods: {
    edit (record) {
      this.visible = true
      this.form.resetFields()
      this.model = Object.assign({}, record)
      if (!everythingIsEmpty(this.model.fileTags)) {
        this.tags = this.model.fileTags.split(',')
      }
      this.$nextTick(() => {
        this.form.setFieldsValue({
          name: this.model.name,
          remark: this.model.remark
        })
      })
    },
    // 确定
    handleOk () {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          if(that.tags.length>10){
            that.$message.warn("最多只能添加10个标签！")
            return
          }
          that.confirmLoading = true
          let formData = Object.assign(that.model, values,{fileTags:that.tags.length>0?that.tags.join(","):''})
          editDirectoryOrFile(formData).then((res) => {
            if (res.success) {
              that.$message.success(res.message)
              that.$emit('ok')
            } else {
              that.$message.warning(res.message)
            }
          })
            .finally(() => {
              that.confirmLoading = false
              that.close()
            })
          this.visible = false
        }
      })
    },
    // 关闭
    handleCancel () {
      this.close()
    },
    close () {
      this.$emit('close')
      this.visible = false
      this.tags = []
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
    }
  }
}
</script>