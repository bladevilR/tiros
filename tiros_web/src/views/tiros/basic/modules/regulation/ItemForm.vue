<template>
  <div>
    <a-form :form="form">
      <a-row style="width: 100%">
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="维保手段">
            <!--            <j-dict-select-tag
                          :triggerChange="true"
                          v-decorator="['method', validatorRules.method]"
                          dictCode="bu_regu_method"
                          @change="handleChangeMethod"
                        />-->
            <j-multi-select-tag
              :triggerChange="true"
              v-decorator="['method', validatorRules.method]"
              dictCode="bu_regu_method"
              @change="handleChangeMethod"
            >
            </j-multi-select-tag>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="质量等级">
            <j-dict-select-tag
              :triggerChange="true"
              v-decorator="['qualityLevel', validatorRules.qualityLevel]"
              dictCode="bu_regu_quality_level"
              @change="handleChangeQualityLevel"
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row style="width: 100%">
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="关联设备">
            <a-select
              placeholder="请选择关联设备"
              :open="false"
              :showArrow="true"
              v-model="trainTypeSysName"
              @focus="openModal"
              ref="mySelect"
            >
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
          </a-form-item>
        </a-col>
        <a-col :span="24 / 2">
          <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="所需工时">
            <a-input-number
              :min="0"
              :max="999999"
              style="width: 100%"
              v-decorator="['workTime', validatorRules.workTime]"
              @change="handleChangeWorkTime"
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="24 / 4">
          <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="是否委外">
            <a-switch v-model="outsourceSwitch" @change="handleChangeOutsource" />
          </a-form-item>
        </a-col>
        <a-col :span="24 / 4">
          <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="重要工序">
            <a-switch v-model="importantSwitch" @change="handleChangeImportant" />
          </a-form-item>
        </a-col>
        <a-col :span="24 / 4">
          <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="是否必换">
            <a-switch v-model="mustReplaceSwitch" @change="handleChangeMustReplace" />
          </a-form-item>
        </a-col>
        <a-col :span="24 / 4">
          <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="是否测量">
            <a-switch v-model="measureSwitch" @change="handleChangeMeasure" />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :span="24">
          <a-form-item :labelCol="labelCol3" :wrapperCol="wrapperCol3" label="技术要求">
            <a-textarea
              placeholder="请输入内容"
              v-decorator="['requirement', validatorRules.requirement]"
              @change="handleChangeRequirement"
            />
          </a-form-item>
        </a-col>
      </a-row>
      <a-row>
        <a-col :offset="2" :span="22">
          <a-form-item>
            <a-tabs :default-active-key="activeTab" @change="callback">
              <a-tab-pane key="1" tab="额定物料">
                <vxe-table
                  border
                  ref="reguMaterialslistTable"
                  :align="allAlign"
                  :data="reguMaterials"
                  show-overflow="tooltip"
                  :edit-config="{ trigger: 'manual', mode: 'row' }"
                  :checkbox-config="{ trigger: 'row', highlight: true, checkStrictly: true }"
                >
                  <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                  <vxe-table-column field="code" title="物料编码" width="150"></vxe-table-column>
                  <vxe-table-column field="name" title="物料名称" align="left" header-align="center"></vxe-table-column>
                  <vxe-table-column field="category1_dictText" title="物料分类" width="150"></vxe-table-column>
                  <vxe-table-column field="amount" title="所需数量" width="100"></vxe-table-column>
                  <vxe-table-column field="unit" title="单位" width="100"></vxe-table-column>
                </vxe-table>
              </a-tab-pane>
              <a-tab-pane key="2" tab="所需工器具">
                <vxe-table
                  border
                  ref="reguToolslistTable"
                  :align="allAlign"
                  :data="reguTools"
                  show-overflow="tooltip"
                  :edit-config="{ trigger: 'manual', mode: 'row' }"
                  :checkbox-config="{ trigger: 'row', highlight: true, checkStrictly: true }"
                >
                  <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                  <vxe-table-column field="code" title="工器具类型编码" width="150"></vxe-table-column>
                  <vxe-table-column
                    field="name"
                    title="工器具名称"
                    align="left"
                    header-align="center"
                  ></vxe-table-column>
                  <vxe-table-column field="category1_dictText" title="工器具分类" width="150"></vxe-table-column>
                  <vxe-table-column field="amount" title="所需数量" width="100"></vxe-table-column>
                  <vxe-table-column field="unit" title="单位" width="100"></vxe-table-column>
                </vxe-table>
              </a-tab-pane>
              <a-tab-pane key="5" tab="作业指导">
                <vxe-table
                  border
                  ref="reguSopsListTable"
                  :align="allAlign"
                  :data="techBookDetails"
                  show-overflow="tooltip"
                  :checkbox-config="{ trigger: 'row', highlight: true, checkStrictly: true }"
                >
                  <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                  <vxe-table-column field="op" title="" width="60">
                    <template v-slot="{ row }">
                      <a @click.stop="showSopDeatil(row)"> 查看</a>
                    </template>
                  </vxe-table-column>
                  <vxe-table-column field="bookStepNo" title="步骤" width="60"></vxe-table-column>
                  <vxe-table-column field="bootStepTitle" title="步骤标题" align="left" header-align="center"></vxe-table-column>
                  <vxe-table-column field="requireCertificate" title="所属作业指导书" max-width="200"></vxe-table-column>
                </vxe-table>
              </a-tab-pane>
              <a-tab-pane key="3" tab="所需人员">
                <vxe-table
                  border
                  ref="reguPersonsListTable"
                  :align="allAlign"
                  :data="reguPersons"
                  show-overflow="tooltip"
                  :edit-config="{ trigger: 'manual', mode: 'row' }"
                  :checkbox-config="{ trigger: 'row', highlight: true, checkStrictly: true }"
                >
                  <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                  <vxe-table-column field="requirePostion" title="人员岗位"></vxe-table-column>
                  <vxe-table-column field="requireTech" title="技术要求"></vxe-table-column>
                  <vxe-table-column field="requireCertificate" title="证书要求"></vxe-table-column>
                  <vxe-table-column field="amount" title="所需人数" width="100"></vxe-table-column>
                </vxe-table>
              </a-tab-pane>
<!--              <a-tab-pane key="4" tab="工艺文件">
                <vxe-table
                  border
                  ref="techFileListTable"
                  :align="allAlign"
                  :data="techFiles"
                  show-overflow="tooltip"
                  :edit-config="{ trigger: 'manual', mode: 'row' }"
                  :checkbox-config="{ trigger: 'row', highlight: true, checkStrictly: true }"
                >
                  <vxe-table-column type="checkbox" width="40"></vxe-table-column>
                  <vxe-table-column title="-" width="100">
                    <template v-slot="{ row }">
                      <a @click.stop="handleSeeing(row)">查看</a>
                    </template>
                  </vxe-table-column>
                  <vxe-table-column
                    field="name"
                    title="名称"
                    width="250"
                    align="left"
                    header-align="center"
                  ></vxe-table-column>
                  <vxe-table-column field="type" title="类型" width="100"></vxe-table-column>
                  <vxe-table-column
                    field="size"
                    title="大小(kb)"
                    width="200"
                    align="right"
                    header-align="center"
                  ></vxe-table-column>
                  <vxe-table-column field="uploadDate" title="上传日期" width="150"></vxe-table-column>
                  <vxe-table-column field="remark" title="备注" align="left" header-align="center"></vxe-table-column>
                </vxe-table>
              </a-tab-pane>-->
              <!-- <a-tab-pane key="4" tab="作业表单">
                <vxe-table border ref="listTable" :align="allAlign" :data="reguForms">
                  <vxe-table-column type="checkbox" width="5%"></vxe-table-column>
                  <vxe-table-column field="stationNo" width="10%">
                    <a>查看</a>
                  </vxe-table-column>
                  <vxe-table-column field="title" title="作业记录表名称" width="25%"></vxe-table-column>
                  <vxe-table-column field="workGroupName" title="关联工位" width="20%"></vxe-table-column>
                  <vxe-table-column field="status_dictText" title="状态" width="15%"></vxe-table-column>
                  <vxe-table-column field="remark" title="备注" width="25%"></vxe-table-column>
                </vxe-table>
              </a-tab-pane>-->
            </a-tabs>
          </a-form-item>
          <div class="extraDiv">
            <a-icon class="iconBtn" type="plus-circle" theme="filled" @click="openAddModal" />
            <a-icon class="iconBtn" type="close-circle" theme="filled" @click="delTableData" />
          </div>
        </a-col>
      </a-row>
    </a-form>
    <material-list ref="MaterialModalForm" :multiple="true" @ok="addTarget"></material-list>
    <ToolTypeSelect ref="ToolsModalForm" :multiple="true" :canSelectType="[4, 5]" @ok="addTarget"></ToolTypeSelect>
    <!--<tools-list ref="ToolsModalForm" :multiple="true" :lastColumn="false" @ok="addTarget"></tools-list>-->
    <user-list ref="UserModalForm" :multiple="true" @ok="addTarget"></user-list>
    <forms-list ref="FromsModalForm" :multiple="true" @ok="addTarget"></forms-list>
    <tech-file-list ref="techFileModalForm" :multiple="true" :id="'4'" @ok="addTarget"></tech-file-list>
    <SopDetailList ref="sopDetailModalForm" :multiple="true" @ok="addTarget"></SopDetailList>
    <WorkSopView ref="workSopViewModal"></WorkSopView>
    <train-asset-type
      ref="selectForm"
      :multiple="false"
      :trainTypeId="trainTypeId"
      @ok="selectTrainSys"
    ></train-asset-type>
    <person-requirement ref="PersonModalForm" @ok="addTarget"></person-requirement>
    <doc-preview-modal ref="docPreview"></doc-preview-modal>
  </div>
</template>

<script>
import MaterialList from '../../../common/selectModules/MaterialList'
import ToolTypeSelect from '@views/tiros/common/selectModules/ToolTypeSelect'
import UserList from '../../../common/selectModules/UserList'
import FormsList from '../../../common/selectModules/FormsList'
import TrainAssetType from '../../../common/selectModules/TrainAssetType'
import PersonRequirement from '../../../common/inputModules/PersonRequirement'
import TechFileList from '../../../common/selectModules/TechFileList'
import DocPreviewModal from '../../../common/doc/DocPreviewModal'
import JMultiSelectTag from '@comp/dict/JMultiSelectTag'
import SopDetailList from '@views/tiros/common/selectModules/SopDetailList'
import WorkSopView from '@views/tiros/basic/modules/worksop/WorkSopView'
import { randomUUID } from '@/utils/util'

export default {
  name: 'ItemForm',
  components: {
    JMultiSelectTag,
    MaterialList,
    ToolTypeSelect,
    UserList,
    FormsList,
    TrainAssetType,
    PersonRequirement,
    TechFileList,
    DocPreviewModal,
    SopDetailList,
    WorkSopView,
  },
  props: {
    method: {
      type: String,
      default: '',
    },
    qualityLevel: {
      type: String,
      default: '',
    },
    important: {
      type: Number,
    },
    measure: {
      type: Number,
    },
    mustReplace: {
      type: Number,
    },
    workTime: {
      type: Number,
    },
    outsource: {
      type: Number,
    },
    requirement: {
      type: String,
      default: '',
    },
    reguMaterials: {
      type: Array,
      default: () => [],
    },
    reguTools: {
      type: Array,
      default: () => [],
    },
    reguPersons: {
      type: Array,
      default: () => [],
    },
    techBookDetails: {
      type: Array,
      default: () => [],
    },
    reguForms: {
      type: Array,
      default: () => [],
    },
    techFiles: {
      type: Array,
      default: () => [],
    },
    assetTypeId: {
      type: String,
      default: '',
    },
    assetTypeName: {
      type: String,
      default: '',
    },
    trainTypeId: {
      type: String,
      default: '',
    },
    reguDetailId: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      activeTab: '1',
      form: this.$form.createForm(this),
      importantSwitch: false,
      outsourceSwitch: false,
      mustReplaceSwitch: false,
      measureSwitch: false,
      trainTypeSysId: '',
      trainTypeSysName: '',
      filedRight: false,
      validatorRules: {
        method: { rules: [{ required: true, message: '请选择维保手段!' }] },
        qualityLevel: { rules: [{ required: true, message: '请选择质量等级!' }] },
        workTime: { rules: [{ required: true, message: '输入所需工时!' }] },
        requirement: { rules: [{ max: 450, message: '输入长度不能超过450字符!' }] },
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 4 },
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 16 },
      },
      labelCol2: {
        xs: { span: 24 },
        sm: { span: 8 },
      },
      wrapperCol2: {
        xs: { span: 24 },
        sm: { span: 12 },
      },
      labelCol3: {
        xs: { span: 24 },
        sm: { span: 2 },
      },
      wrapperCol3: {
        xs: { span: 24 },
        sm: { span: 22 },
      },
      allAlign: 'center',
    }
  },
  created() {
    this.importantSwitch = this.important ? true : false
    this.outsourceSwitch = this.outsource ? true : false
    this.measureSwitch = this.measure ? true : false
    this.mustReplaceSwitch = this.mustReplace ? true : false
    this.trainTypeSysName = this.assetTypeName
    this.form.resetFields()
    this.$nextTick(() => {
      this.form.setFieldsValue({
        method: this.method,
        qualityLevel: this.qualityLevel,
        workTime: this.workTime,
        requirement: this.requirement,
      })
    })
  },
  methods: {
    callback(key) {
      this.activeTab = key
    },
    openModal() {
      this.$refs.selectForm.showModal()
      this.$refs.mySelect.blur()
    },
    selectTrainSys(data) {
      if (data.length) {
        // this.assetTypeId = data[0].id
        this.$emit('update:assetTypeId', data[0].id)
        this.trainTypeSysName = data[0].name
      }
    },
    openAddModal() {
      switch (this.activeTab) {
        case '1':
          this.$refs.MaterialModalForm.showModal()
          break
        case '2':
          this.$refs.ToolsModalForm.showModal()
          break
        case '3':
          this.$refs.PersonModalForm.add()
          break
        case '4':
          // 工艺文件屏蔽 2021-05-02
          this.$refs.techFileModalForm.showModal()
          break
        case '5':
          // 添加作业指导书明细
          this.$refs.sopDetailModalForm.showModal()
          break
        default:
          //这里是没有找到对应的值处理
          break
      }
    },
    delTableData() {
      switch (this.activeTab) {
        case '1':
          let m = this.$refs.reguMaterialslistTable.getCheckboxRecords()

          m.map((item1, index1) => {
            this.reguMaterials.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.reguMaterials.splice(index2, 1)
              }
            })
          })
          break
        case '2':
          let t = this.$refs.reguToolslistTable.getCheckboxRecords()

          t.map((item1, index1) => {
            this.reguTools.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.reguTools.splice(index2, 1)
              }
            })
          })
          break
        case '3':
          let r = this.$refs.reguPersonsListTable.getCheckboxRecords()

          r.map((item1, index1) => {
            this.reguPersons.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.reguPersons.splice(index2, 1)
              }
            })
          })
          break
        case '4':
          let tf = this.$refs.techFileListTable.getCheckboxRecords()

          tf.map((item1, index1) => {
            this.techFiles.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.techFiles.splice(index2, 1)
              }
            })
          })
          break
        case '5':
          let records = this.$refs.reguSopsListTable.getCheckboxRecords()
          records.map((item1, index1) => {
            this.techBookDetails.map((item2, index2) => {
              if (item1.id === item2.id) {
                this.techBookDetails.splice(index2, 1)
              }
            })
          })
          break
        default:
          //这里是没有找到对应的值处理
          break
      }
    },
    addTarget(data) {
      switch (this.activeTab) {
        case '1':
          data.map((item) => {
            let tempIndex = this.reguMaterials.findIndex((m) => {
              return m.materialTypeId === item.id
            })
            if (tempIndex < 0) {
              // this.reguMaterials.push(item)
              this.reguMaterials.push(this.transformMaterialToReguMaterial(item))
            } else {
              this.$message.error('此物料已添加')
            }
          })
          this.$emit('update:reguMaterials', this.reguMaterials)
          break
        case '2':
          data.map((item) => {
            let tempIndex = this.reguTools.findIndex((m) => {
              return m.toolTypeId === item.id
            })
            if (tempIndex < 0) {
              // this.reguTools.push(item)
              this.reguTools.push(this.transformToolToReguTool(item))
            } else {
              this.$message.error('此工器具已添加')
            }
          })
          this.$emit('update:reguTools', this.reguTools)
          break
        case '3':
          if (data) {
            this.reguPersons.push(data)
          }
          this.$emit('update:reguPersons', this.reguPersons)
          break
        case '4':
          data.map((item) => {
            let tempIndex = this.techFiles.findIndex((m) => {
              return m.fileId === item.id
            })
            if (tempIndex < 0) {
              this.techFiles.push(this.transformFileToReguFile(item))
            } else {
              this.$message.error('此工艺文件已添加')
            }
          })
          this.$emit('update:techFiles', this.techFiles)
          break
        case '5':
          // 作业指导书步骤新增
          data.forEach((item) => {
            if (this.techBookDetails.find((e) => e.bookDetailId === item.id)) {
              this.$message.error(`${item.stepTitle}步骤已经添加了`)
            } else {
              this.techBookDetails.push(this.transformFileToReguSop(item))
            }
          })
          this.$emit('update:techBookDetails', this.techBookDetails)
          break
        default:
          //这里是没有找到对应的值处理
          break
      }
    },
    transformMaterialToReguMaterial(material) {
      let reguMaterial = {}
      reguMaterial.id = randomUUID()
      reguMaterial.reguDetailId = this.reguDetailId
      reguMaterial.materialTypeId = material.id
      reguMaterial.amount = material.num
      reguMaterial.code = material.code
      reguMaterial.name = material.name
      reguMaterial.unit = material.unit
      reguMaterial.category1 = material.category1
      reguMaterial.category1_dictText = material.category1_dictText
      reguMaterial.category2 = material.category2
      reguMaterial.category2_dictText = material.category2_dictText

      return reguMaterial
    },
    transformToolToReguTool(tool) {
      let reguTool = {}
      reguTool.id = randomUUID()
      reguTool.reguDetailId = this.reguDetailId
      reguTool.toolTypeId = tool.id
      reguTool.amount = tool.num
      reguTool.code = tool.code
      reguTool.name = tool.name
      reguTool.unit = tool.unit
      reguTool.category1 = tool.category1
      reguTool.category1_dictText = tool.category1_dictText
      reguTool.category2 = tool.category2
      reguTool.category2_dictText = tool.category2_dictText

      return reguTool
    },
    transformFileToReguFile(file) {
      let reguFile = {}
      reguFile.id = randomUUID()
      reguFile.reguDetailId = this.reguDetailId
      reguFile.fileId = file.id
      reguFile.savepath = file.savepath
      reguFile.name = file.name
      reguFile.type = file.type
      reguFile.size = file.size
      reguFile.uploadDate = file.uploadDate
      reguFile.remark = file.remark

      return reguFile
    },
    transformFileToReguSop(file) {
      let reguSop = {}
      reguSop.id = randomUUID()
      reguSop.reguDetailId = this.reguDetailId
      reguSop.bookDetailId = file.id
      reguSop.bookStepNo = file.stepNum
      reguSop.bootStepContent = file.stepContent
      reguSop.bootStepTitle = file.stepTitle
      reguSop.techBookId = file.bookId

      return reguSop
    },
    handleCheckFiled() {
      this.form.validateFields((err, values) => {
        this.filedRight = err
      })
    },
    handleChangeMethod(value) {
      this.$emit('update:method', value)
    },
    handleChangeQualityLevel(value) {
      this.$emit('update:qualityLevel', value)
    },
    handleChangeWorkTime(value) {
      this.$emit('update:workTime', value)
    },
    handleChangeRequirement(value) {
      this.$emit('update:requirement', value.target.value)
    },
    handleChangeOutsource(value) {
      this.outsourceSwitch = value
      let t = value ? 1 : 0
      this.$emit('update:outsource', t)
    },
    handleChangeImportant(value) {
      this.importantSwitch = value
      let t = value ? 1 : 0
      this.$emit('update:important', t)
    },
    handleChangeMustReplace(value) {
      this.mustReplaceSwitch = value
      let t = value ? 1 : 0
      this.$emit('update:mustReplace', t)
    },
    handleChangeMeasure(value) {
      this.measureSwitch = value
      let t = value ? 1 : 0
      this.$emit('update:measure', t)
    },
    handleSeeing(data) {
      // this.filePath = window._CONFIG['onlinePreviewDomainURL'] + '?url=' + encodeURIComponent(data.savepath)
      this.$refs.docPreview.handleFilePath(data.savepath)
    },
    showSopDeatil(row) {
      this.$refs.workSopViewModal.showSopDetail([row.bookDetailId])
    },
  },
}
</script>

<style lang="less">
.extraDiv {
  position: absolute;
  top: 15px;
  right: 0;
  font-size: 16px;

  .iconBtn {
    margin-right: 10px;
    font-size: 20px;
  }
}
</style>