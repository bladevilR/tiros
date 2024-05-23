<template>
  <a-modal
    width="600px"
    title="阈值设置"
    :visible="visible"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    centered
    :destroyOnClose="true"
    :bodyStyle="{ height: '500px' }">
    <a-form-model ref="form" :model="form" :rules="rules">
      <a-row>
        <a-col :span="24">
          <a-form-model-item label="测量项名" prop="itemName">
            <a-input v-model="form.itemName" placeholder="请输入测量项名称" ></a-input>
          </a-form-model-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="5">
          <a-form-model-item label="比较值" prop="linkDomain">
            {{form.linkDomain}}
          </a-form-model-item>
        </a-col>
        <a-col :span="5">
          <a-form-model-item label="比较符" prop="operator">
            <j-dict-select-tag
              v-model="form.operator"
              placeholder="请选择"
              dictCode="bu_operator"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="13" :offset="1">
          <a-form-model-item label="阈值" prop="threshold">
            <a-input-number v-model="form.threshold" placeholder="请输入阈值" style="width: 100%"></a-input-number>
          </a-form-model-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="5">
          <a-form-model-item>
            -
          </a-form-model-item>
        </a-col>
        <a-col :span="5">
          <a-form-model-item label="比较符2" prop="operator2">
            <j-dict-select-tag
              v-model="form.operator2"
              placeholder="请选择"
              dictCode="bu_operator"
            />
          </a-form-model-item>
        </a-col>
        <a-col :span="13" :offset="1">
          <a-form-model-item label="阈值2" prop="threshold2">
            <a-input-number v-model="form.threshold2" placeholder="请输入阈值" style="width: 100%"></a-input-number>
          </a-form-model-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="24">
          <a-form-model-item label="提示消息模版">
            <a-textarea
              v-model="form.template"
              placeholder="请输入提示模版,支持${value}"
              :auto-size="{ minRows: 2, maxRows: 3 }"
            />
          </a-form-model-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="24">
          <a-form-model-item label="指定设备">
            <a-select ref="assetType" v-model="form.assetTypeName" placeholder="请选择设备" :open="false" @focus="openAssetTypeModal">
              <a-icon slot="suffixIcon" type="ellipsis" @click="openAssetTypeModal" />
            </a-select>
          </a-form-model-item>
        </a-col>
      </a-row>
    </a-form-model>
    <train-asset-type ref="assetTypeModal" title="设备选择" :multiple="false" @ok="onAssetTypeSelect"></train-asset-type>
  </a-modal>
</template>

<script>
import TrainAssetType from '@views/tiros/common/selectModules/TrainAssetType'
import {saveThreshold, editThreshold} from '@api/tirosQualityApi'

export default {
  name: 'ThresholdEdit',
  props: ['visible', 'formId','setting', 'zones', 'zoneNames'],
  components: {TrainAssetType},
  data () {
    return {
      form: {
        customId: '',
        itemName: '',
        operator: '',
        threshold: '',
        operator2: '',
        threshold2: '',
        template: '${trainNo} > ${assetTypeName} > ${name} ${operator} ${value}',
        linkDomain:'',
        assetTypeId: '',
        assetTypeName:'',
        row1: 0,
        row2: 0,
        col1: 0,
        col2: 0
      },
      rules: {
        itemName: [
          { required: true, message: '请输入测量项名称', trigger: 'blur' },
        ],
        operator: [
          { required: true, message: '请选择比较符', trigger: 'blur' },
        ],
        threshold: [
          { required: true, message: '请输入阈值', trigger: 'blur' }
        ],
        linkDomain: [
          { required: true, message: '请选择范围', trigger: 'blur' }
        ]
      }
    }
  },
  mounted () {
    if (this.setting) {
      Object.assign(this.form, this.setting)
    } else {
      this.form.linkDomain = this.zoneNames
      this.form.row1 = this.zones.row[0]
      this.form.row2 = this.zones.row[1]
      this.form.col1 = this.zones.col[0]
      this.form.col2 = this.zones.col[1]
      this.form.customId = this.formId
    }
  },
  methods: {
    handleOk () {
      this.$refs.form.validate(valid => {
        if (valid) {
          if (!this.form.operator) {
            this.form.operator = ''
            this.form.threshold = ''
          }
          if (!this.form.operator2) {
            this.form.operator2 = ''
            this.form.threshold2 = ''
          }
          // console.log('save:', this.form)
          if(!this.setting) {
            saveThreshold(this.form).then(res => {
              if (res.success) {
                this.$message.success('保存成功')
                this.handleCancel()
              } else {
                this.$message.error(res.message)
                console.error('保存测量项目失败：', res.message)
              }
            }).catch(err => {
              console.error('保存测量项目异常：', err)
              this.$message.error('保存异常')
            })
          } else {
            editThreshold(this.form).then(res => {
              if (res.success) {
                this.$message.success('保存成功')
                this.handleCancel()
              } else {
                this.$message.error(res.message)
                console.error('保存测量项目失败：', res.message)
              }
            }).catch(err => {
              console.error('保存测量项目异常：', err)
              this.$message.error('保存异常')
            })
          }
        }
      });
    },
    handleCancel () {
      this.form = {
        customId: '',
        itemName: '',
        operator: '',
        threshold: '',
        template: '${trainNo} > ${assetTypeName} > ${name} ${operator} ${value}',
        linkDomain: '',
        assetTypeId: '',
        assetTypeName: ''
      }
      this.$emit('update:visible', false)
    },
    // 弹出设备选择界面
    openAssetTypeModal() {
      this.$refs.assetTypeModal.showModal()
      // this.$refs.assetType.focus()
      this.$refs.assetType.blur()
    },
    // 设备选择回调
    onAssetTypeSelect(data) {
      if (data.length > 0) {
        const item = data[0]
        if (item) {
          this.form.assetTypeName = item.name
          this.form.assetTypeId = item.id
        } else {
          this.form.assetTypeName = ''
          this.form.assetTypeId = ''
        }
        this.$forceUpdate()
        this.$refs.form.validateField('assetType')
      } else {
        this.form.assetTypeId = ''
      }
    }
  }
}
</script>

<style scoped>

</style>