<template>
  <a-modal
    :title="title"
    :width="1200"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    :maskClosable="false"
    :bodyStyle="{ maxHeight: '70vh', overflowY: 'auto' }"
  >
    <a-spin :spinning="confirmLoading">
      <a-alert
        type="info"
        show-icon
        style="margin-bottom: 12px"
        message="支持从车辆结构选择并自动填充，也支持手动编辑；线别、位置支持多选。"
      />
      <a-form-model
        ref="form"
        :model="model"
        :rules="validatorRules"
        :label-col="{ span: 6 }"
        :wrapper-col="{ span: 16 }"
      >
        <a-row :gutter="12">
          <a-col :span="8">
            <a-form-model-item label="车型" prop="trainType">
              <a-input v-model="model.trainType" placeholder="默认电客车，也可手动编辑" />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="编码" prop="bomCode">
              <a-input v-model="model.bomCode" placeholder="请输入编码" />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="BOM名称" prop="bomName">
              <a-input v-model="model.bomName" placeholder="请输入BOM名称" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row :gutter="12">
          <a-col :span="12">
            <a-form-model-item label="线别(可多选)">
              <a-select mode="multiple" v-model="lineValues" placeholder="请选择线别" allowClear>
                <a-select-option v-for="item in lineOptions" :key="item" :value="item">{{ item }}</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
          <a-col :span="12">
            <a-form-model-item label="位置(可多选)">
              <a-select mode="multiple" v-model="positionValues" placeholder="请选择位置" allowClear>
                <a-select-option v-for="item in positionOptions" :key="item" :value="item">{{ item }}</a-select-option>
              </a-select>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row :gutter="12">
          <a-col :span="8">
            <a-form-model-item label="所属系统">
              <a-input v-model="model.system" placeholder="请输入所属系统" />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="二级模块">
              <a-input v-model="model.moduleLevel2" placeholder="请输入二级模块" />
            </a-form-model-item>
          </a-col>
          <a-col :span="8">
            <a-form-model-item label="三级模块">
              <a-input v-model="model.moduleLevel3" placeholder="选填" />
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="结构关联" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-space>
                <a-select
                  style="width: 260px"
                  v-model="selectedStructId"
                  placeholder="请选择车辆结构"
                  allowClear
                  showSearch
                  optionFilterProp="children"
                  @change="onStructChange"
                >
                  <a-select-option v-for="item in structOptions" :key="item.id" :value="item.id">
                    {{ item.name }}
                  </a-select-option>
                </a-select>
                <a-button @click="openStructDetailSelect" :disabled="!selectedStructId">从结构明细选择并填充</a-button>
              </a-space>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="爆炸图URL" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-space style="width: 100%">
                <a-input v-model="model.explosionDiagram" placeholder="请输入爆炸图URL或上传图片" style="width: 520px" />
                <j-image-upload
                  text="上传爆炸图"
                  :isMultiple="false"
                  :bizPath="'/quota-bom/explosion'"
                  v-model="model.explosionDiagram"
                />
              </a-space>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="关联物资" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-space style="width: 100%">
                <a-input
                  :value="materialSummary"
                  placeholder="请选择关联物资"
                  disabled
                  style="width: 520px"
                />
                <a-button @click="openMaterialSelect">选择物资</a-button>
                <a-button @click="clearSelectedMaterials" :disabled="selectedMaterialRows.length === 0">清空</a-button>
              </a-space>
              <div class="material-tag-list" v-if="selectedMaterialRows.length > 0">
                <a-tag v-for="item in selectedMaterialRows" :key="item.id" style="margin-top: 6px">
                  {{ item.code ? `${item.code} - ${item.name}` : item.name }}
                </a-tag>
              </div>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="部件明细" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <vxe-table
                border
                size="mini"
                max-height="280"
                :data="partRows"
                :edit-config="{ trigger: 'click', mode: 'cell' }"
              >
                <vxe-table-column type="seq" width="50" title="#"></vxe-table-column>
                <vxe-table-column field="partNo" title="件号" min-width="110" :edit-render="{ name: 'input' }"></vxe-table-column>
                <vxe-table-column field="name" title="名称" min-width="120" :edit-render="{ name: 'input' }"></vxe-table-column>
                <vxe-table-column field="spec" title="规格型号" min-width="120" :edit-render="{ name: 'input' }"></vxe-table-column>
                <vxe-table-column field="drawingQty" title="图上数量" width="90" :edit-render="{ name: '$input', props: { type: 'number', min: 0 } }"></vxe-table-column>
                <vxe-table-column field="trainQty" title="1列车数量" width="95" :edit-render="{ name: '$input', props: { type: 'number', min: 0 } }"></vxe-table-column>
                <vxe-table-column field="materialCode" title="物资编码" min-width="110" :edit-render="{ name: 'input' }"></vxe-table-column>
                <vxe-table-column field="reqFirstFrame" title="一架" width="70" :edit-render="{ name: '$checkbox' }"></vxe-table-column>
                <vxe-table-column field="reqFirstMajor" title="一大" width="70" :edit-render="{ name: '$checkbox' }"></vxe-table-column>
                <vxe-table-column field="reqSecondFrame" title="二架" width="70" :edit-render="{ name: '$checkbox' }"></vxe-table-column>
                <vxe-table-column field="reqSecondMajor" title="二大" width="70" :edit-render="{ name: '$checkbox' }"></vxe-table-column>
                <vxe-table-column field="reqThirdFrame" title="三架" width="70" :edit-render="{ name: '$checkbox' }"></vxe-table-column>
                <vxe-table-column title="操作" width="80" fixed="right">
                  <template slot-scope="scope">
                    <a @click="removePartRow(scope.rowIndex)">删除</a>
                  </template>
                </vxe-table-column>
              </vxe-table>
              <a-space style="margin-top: 8px">
                <a-button size="small" @click="addPartRow">新增明细行</a-button>
                <a-button size="small" @click="normalizePartRows">清理空行</a-button>
              </a-space>
            </a-form-model-item>
          </a-col>
        </a-row>

        <a-row>
          <a-col :span="24">
            <a-form-model-item label="备注" :label-col="{ span: 3 }" :wrapper-col="{ span: 20 }">
              <a-textarea v-model="model.remark" :rows="2" placeholder="请输入备注" />
            </a-form-model-item>
          </a-col>
        </a-row>
      </a-form-model>
    </a-spin>

    <select-parent
      ref="structDetailSelect"
      :train-struct-id="selectedStructId"
      :multiple="false"
      @ok="onStructDetailSelected"
    ></select-parent>

    <material-list
      ref="materialSelect"
      :multiple="true"
      :mode="[1]"
      :typeCodeList="[]"
      @ok="onMaterialSelected"
    ></material-list>
  </a-modal>
</template>

<script>
import { saveQuotaBom, updateQuotaBom, getTrainStructList } from '@/api/tirosApi'
import SelectParent from '@views/tiros/basic/modules/trainStructure/SelectParent'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
import JImageUpload from '@/components/jeecg/JImageUpload'

export default {
  name: 'QuotaBomModal',
  components: { SelectParent, MaterialList, JImageUpload },
  computed: {
    materialSummary() {
      if (!this.selectedMaterialRows || this.selectedMaterialRows.length === 0) {
        return ''
      }
      if (this.selectedMaterialRows.length <= 3) {
        return this.selectedMaterialRows
          .map(item => item.code ? `${item.code}-${item.name}` : item.name)
          .join('；')
      }
      const first = this.selectedMaterialRows
        .slice(0, 3)
        .map(item => item.code ? `${item.code}-${item.name}` : item.name)
        .join('；')
      return `${first} 等${this.selectedMaterialRows.length}项`
    }
  },
  data() {
    return {
      title: '',
      visible: false,
      confirmLoading: false,
      model: {},
      partRows: [],
      selectedMaterialRows: [],
      lineValues: [],
      positionValues: [],
      selectedStructId: '',
      structOptions: [],
      lineOptions: ['1号线', '1号线增购', '2号线', '2号线增购', '2号线延线', '3号线', '4号线', '4号线增购', '5号线', '6号线', '7号线', '11号线'],
      positionOptions: ['TC1', 'MP1', 'M1', 'M2', 'MP2', 'TC2', 'ALL'],
      validatorRules: {
        trainType: [{ required: true, message: '请输入车型', trigger: 'blur' }],
        bomCode: [{ required: true, message: '请输入编码', trigger: 'blur' }],
        bomName: [{ required: true, message: '请输入BOM名称', trigger: 'blur' }]
      }
    }
  },
  methods: {
    add() {
      this.title = '新增定额BOM'
      this.visible = true
      this.model = { trainType: '电客车' }
      this.partRows = [this.createEmptyPartRow()]
      this.selectedMaterialRows = []
      this.lineValues = []
      this.positionValues = []
      this.selectedStructId = ''
      this.loadStructOptions()
    },
    edit(record) {
      this.title = '编辑定额BOM'
      this.visible = true
      this.model = Object.assign({}, record)
      this.lineValues = this.splitCsv(record.line)
      this.positionValues = this.splitCsv(record.position)
      this.partRows = this.parsePartRows(record.partDetails)
      this.selectedMaterialRows = this.parseMaterialLinks(record.materialLinks)
      this.selectedStructId = ''
      this.loadStructOptions()
    },
    handleOk() {
      this.$refs.form.validate(valid => {
        if (!valid) {
          return
        }
        this.normalizePartRows()
        this.model.line = this.joinCsv(this.lineValues)
        this.model.position = this.joinCsv(this.positionValues)
        if (!this.model.trainType) {
          this.model.trainType = '电客车'
        }
        this.model.materialLinks = JSON.stringify(this.selectedMaterialRows || [])
        this.model.partDetails = JSON.stringify(this.partRows)
        this.confirmLoading = true
        const req = this.model.id ? updateQuotaBom(this.model) : saveQuotaBom(this.model)
        req.then(res => {
          if (res.success) {
            this.$message.success(this.model.id ? '修改成功' : '新增成功')
            this.visible = false
            this.$emit('ok')
          } else {
            this.$message.error(res.message || '操作失败')
          }
        }).finally(() => {
          this.confirmLoading = false
        })
      })
    },
    handleCancel() {
      this.visible = false
      this.$refs.form.resetFields()
      this.partRows = []
      this.selectedMaterialRows = []
    },
    loadStructOptions() {
      getTrainStructList().then(res => {
        if (res && res.success) {
          this.structOptions = res.result || []
        } else {
          this.structOptions = []
        }
      }).catch(() => {
        this.structOptions = []
      })
    },
    onStructChange() {
      // 仅记录结构选择，具体填充通过“结构明细选择”完成
    },
    openMaterialSelect() {
      this.$refs.materialSelect.showModal()
    },
    onMaterialSelected(rows) {
      const normalized = (rows || []).map(item => ({
        id: item.id,
        code: item.code,
        name: item.name,
        category1: item.category1,
        category1_dictText: item.category1_dictText
      })).filter(item => item.id)
      const exists = {}
      this.selectedMaterialRows = normalized.filter(item => {
        if (exists[item.id]) {
          return false
        }
        exists[item.id] = true
        return true
      })
      this.model.materialLinks = JSON.stringify(this.selectedMaterialRows)
    },
    clearSelectedMaterials() {
      this.selectedMaterialRows = []
      this.model.materialLinks = '[]'
    },
    openStructDetailSelect() {
      if (!this.selectedStructId) {
        this.$message.warning('请先选择车辆结构')
        return
      }
      this.$refs.structDetailSelect.showModal()
    },
    onStructDetailSelected(rows) {
      if (!rows || rows.length === 0) {
        return
      }
      const node = rows[0]
      this.model.bomCode = this.model.bomCode || node.code || ''
      this.model.bomName = this.model.bomName || node.name || ''
      this.model.system = this.model.system || node.parentName || ''
      this.model.moduleLevel2 = this.model.moduleLevel2 || node.name || ''
      this.model.moduleLevel3 = this.model.moduleLevel3 || ''
      if (node.placeCode) {
        this.positionValues = this.mergeUnique(this.positionValues, [node.placeCode])
      }
      const currentPart = this.createEmptyPartRow()
      currentPart.partNo = node.code || ''
      currentPart.name = node.name || ''
      currentPart.spec = node.shortName || ''
      this.partRows.push(currentPart)
      this.$message.success('已按结构明细填充，可继续手动编辑')
    },
    addPartRow() {
      this.partRows.push(this.createEmptyPartRow())
    },
    removePartRow(index) {
      this.partRows.splice(index, 1)
      if (this.partRows.length === 0) {
        this.partRows.push(this.createEmptyPartRow())
      }
    },
    normalizePartRows() {
      this.partRows = (this.partRows || []).filter(row => {
        return Object.keys(row).some(key => {
          if (key.indexOf('req') === 0) {
            return !!row[key]
          }
          return String(row[key] || '').trim() !== ''
        })
      })
      if (this.partRows.length === 0) {
        this.partRows.push(this.createEmptyPartRow())
      }
    },
    createEmptyPartRow() {
      return {
        partNo: '',
        name: '',
        spec: '',
        drawingQty: 0,
        trainQty: 0,
        materialCode: '',
        reqFirstFrame: false,
        reqFirstMajor: false,
        reqSecondFrame: false,
        reqSecondMajor: false,
        reqThirdFrame: false
      }
    },
    parsePartRows(value) {
      if (!value) {
        return [this.createEmptyPartRow()]
      }
      try {
        const list = JSON.parse(value)
        if (!Array.isArray(list) || list.length === 0) {
          return [this.createEmptyPartRow()]
        }
        return list.map(item => ({
          partNo: item.partNo || '',
          name: item.name || '',
          spec: item.spec || '',
          drawingQty: this.toNumber(item.drawingQty),
          trainQty: this.toNumber(item.trainQty),
          materialCode: item.materialCode || '',
          reqFirstFrame: !!item.reqFirstFrame,
          reqFirstMajor: !!item.reqFirstMajor,
          reqSecondFrame: !!item.reqSecondFrame,
          reqSecondMajor: !!item.reqSecondMajor,
          reqThirdFrame: !!item.reqThirdFrame
        }))
      } catch (e) {
        return [this.createEmptyPartRow()]
      }
    },
    parseMaterialLinks(value) {
      if (!value) {
        return []
      }
      try {
        const list = JSON.parse(value)
        if (!Array.isArray(list)) {
          return []
        }
        return list.map(item => ({
          id: item.id || item.materialTypeId || item.materialCode || item.code,
          code: item.code || item.materialCode || '',
          name: item.name || item.materialName || '',
          category1: item.category1,
          category1_dictText: item.category1_dictText
        })).filter(item => item.id)
      } catch (e) {
        return []
      }
    },
    splitCsv(value) {
      if (!value) {
        return []
      }
      return String(value).split(',').map(item => item.trim()).filter(Boolean)
    },
    joinCsv(values) {
      return (values || []).map(item => String(item).trim()).filter(Boolean).join(',')
    },
    mergeUnique(target, source) {
      const map = {}
      ;(target || []).forEach(item => { map[item] = true })
      ;(source || []).forEach(item => { if (item) map[item] = true })
      return Object.keys(map)
    },
    toNumber(value) {
      const num = Number(value)
      if (Number.isNaN(num) || num < 0) {
        return 0
      }
      return num
    }
  }
}
</script>
