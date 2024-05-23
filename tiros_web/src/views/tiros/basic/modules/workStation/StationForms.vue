<template>
  <a-modal
    centered
    :width="'100%'"
    :title="station?station.name:''+'表单关联'"
    dialogClass="fullDialog no-footer"
    :visible="visible"
    :confirmLoading="loading"
    @ok="close"
    @cancel="close"
    :footer="null"
    :destroyOnClose="true"
  >
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="表单名称">
              <a-input placeholder="请输入内容" v-model="queryParam.searchContent" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="表单类型">
              <a-select v-model='queryParam.formType' allowClear>
                <a-select-option :value='3'> 作业记录表</a-select-option>
                <a-select-option :value='1'> 数据记录表</a-select-option>
                <a-select-option :value='4'> 检查记录表</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="修程">
              <j-dict-select-tag triggerChange v-model="queryParam.repairProgramId" dictCode="bu_repair_program,name,id" />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item>
              <a-space>
                <a-button class="primary-color" @click="searchEvent">查询</a-button>
                <a-button type="dashed" class="primary-color" @click="handleAdd">添加</a-button>
                <a-button type="dashed" class="primary-color" @click="importForms">批量导入</a-button>
                <a-button type="dashed" @click="handleDel">删除</a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height:calc(100vh - 135px)">
      <vxe-table
        border
        height="auto"
        ref="listTable"
        align="center"
        :data.sync="formsCopy"
        :edit-rules="validRules"
        :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column
          field="formName"
          title="表单名称"
          :edit-render="{ name: 'input' }"
          align="left"
          header-align="center"
        >
          <template v-slot="{row}">
            <a @click="handleSeeing(row)">{{row.formName}}</a>
          </template>
          <!--        @focus="openFormSelectModal(row)"-->
          <template v-slot:edit="{ row }">
            <a-input
              ref="formSelect"
              v-model="row.formName"
              placeholder="请选择表单"
              :open="false"
              style="width: 100%"
            >
              <a-icon slot="suffix" type="ellipsis" @click="openFormSelectModal(row)" />
            </a-input>
          </template>
        </vxe-table-column>
        <vxe-table-column field="formType_dictText" title="表单类型" width="120" :edit-render="{ name: 'input' }">
          <template v-slot="{ row }">
            {{ row.formType_dictText  }}
          </template>
          <template v-slot:edit="{ row }">
            <a-input placeholder="请选择表单" disabled v-model="row.formType_dictText" />
          </template>
        </vxe-table-column>
        <vxe-table-column field="reguName" title="所属规程" width="200">
          <template v-slot="{ row }">
            {{ row.reguName }}
          </template>
          <template v-slot:edit="{ row }">
            <a-input placeholder="请选择表单" disabled v-model="row.reguName" />
          </template>
        </vxe-table-column>
        <vxe-table-column field="reguVersion" title="规程版本" width="140">
          <template v-slot="{ row }">
            {{ row.reguVersion }}
          </template>
          <template v-slot:edit="{ row }">
            <a-input placeholder="请选择表单" disabled v-model="row.reguVersion" />
          </template>
        </vxe-table-column>
<!--        <vxe-table-column field="remark" title="备注" :edit-render="{ name: 'input' }" align="left" header-align="center">
          <template v-slot:edit="{ row }">
            <a-input @change="remarkChangeValue(row)" :maxLength="201" placeholder="请输入备注" v-model="row.remark" />
          </template>
        </vxe-table-column>-->
        <vxe-table-column title="操作" width="150">
          <template v-slot="{ row }">
            <template v-if="$refs.listTable.isActiveByRow(row)">
              <a-space>
                <a-button type="dashed" size="small" @click="saveRowEvent(row)">保存</a-button>
                <a-button type="dashed" size="small" @click="cancelRowEvent(row)">取消</a-button>
              </a-space>
            </template>
            <template v-else>
              <a-button type="dashed" size="small" @click="editRowEvent(row)">编辑</a-button>
              <!--  <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
            </template>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <FormsList ref="modalForm" :multiple="false" @ok="onSelectForm"></FormsList>
    <FormsList ref="modalForms" @ok="onSelectForms"></FormsList>
    <!--    <TrainAssetType
          ref="assetTypeModal"
          title="设备选择"
          :multiple="false"
          :trainTypeId="trainTypeId"
          @ok="onAssetTypeSelect"
          @cancel="onCancelAssetTypeSelect"
        ></TrainAssetType>-->
    <RecordTableView ref="recordTableView"></RecordTableView>
    <FormViewModal ref="formViewModal"></FormViewModal>
    <JobCheckTableView ref="jobCheckTableView"></JobCheckTableView>
  </a-modal>
</template>

<script>
import { everythingIsEmpty, randomUUID } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import FormsList from '@views/tiros/common/selectModules/FormsList'
import TrainAssetType from '@views/tiros/common/selectModules/TrainAssetType'
import {
  delTpPlanForm,
  listRelatedForm,
  relateForm,
  cancelRelateForm
} from '@/api/tirosApi'
import RecordTableView from '@views/tiros/basic/modules/workRecordSheet/RecordTableView'
import JobCheckTableView from '@views/tiros/basic/modules/jobCheckSheet/JobCheckTableView'
import FormViewModal from '@views/tiros/basic/customform/FormViewModal'
import { getWorkcheck } from '@/api/tirosQualityApi'
import TrainStructureList from '@views/tiros/common/selectModules/TrainStructureList'
import error from '@views/result/Error'

export default {
  name: 'PlanForms',
  components: { FormsList, TrainAssetType, RecordTableView, JobCheckTableView, FormViewModal,TrainStructureList, LineSelectList },
  data() {
    return {
      queryParam:{
        searchContent:'',
        formType:'',
        repairProgramId: '',
        lineId:'',
      },
      visible: false,
      loading: false,
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      validRules: {
        formName: [{ required: true, message: '表单必须选择' }],
      },
      forms: [],
      formsCopy: [],
      station: null
    }
  },
  methods: {
    remarkChangeValue({ remark }){
      if(remark.length > 200){
        this.$message.error('备注不能超过200个字符')
      }
    },
    show(station) {
      this.station = station
      this.queryParam.workstationId = station.id
      this.visible = true

      this.loadLinkedForms()
    },
    // 关闭
    close() {
      this.$emit('close')
      this.visible = false;
      // 重置数据
      Object.assign(this.$data,this.$options.data());
    },
    loadLinkedForms () {
      let params={
        workstationId: this.station.id,
/*        repairProId:this.queryParam.repairProgramId,
        lineId:this.queryParam.lineId,
        type: this.queryParam.formTypeList*/
      }
      this.forms = []
      this.formsCopy = []
      listRelatedForm(params).then((res) => {
        if (res.success) {
          this.forms = res.result
          console.log('sheets:', this.forms)
          this.searchEvent()
        }
      })
    },
    searchEvent(){
      const list = this.forms.filter((item) => {
        let condition=true
        if(this.queryParam.searchContent){
          condition = item.title.indexOf(this.queryParam.searchContent) > -1
        }
        if(this.queryParam.formType){
          condition= item.formType === this.queryParam.formType
        }

        if (this.queryParam.lineId) {
          condition= item.lineId === this.queryParam.lineId
        }

        if (this.queryParam.repairProgramId) {
          condition= item.repairProId === this.queryParam.repairProgramId
        }
        return condition
      });
      this.formsCopy = list;
    },
    handleAdd() {
      if (this.curEditMode > 0) {
        return
      }
      this.curEditMode = 1
      let form = {
        id: randomUUID(),
        code: '',
        formName: '',
        workstationId: this.station.id,
        formType: '',
        formId: '',
        type: 0,
        lineId: '',
        reguName: '',
        reguVersion: '',
        repairProId: '',
        repairProName: ''
      }
      this.$refs.listTable.insertAt(form, -1).then(({ row }) => {
        this.$refs.listTable.setActiveCell(row, 'formName')
      })
    },
    importForms() {
      if (this.$refs.listTable.getActiveRecord()) {
        return
      }
      this.$refs.modalForms.showModal({
        lineId: this.lineId,
        repairProgramId: this.repairProgramId
      })
    },
    handleDel() {
      let m = this.$refs.listTable.getCheckboxRecords()
      if (m.length <= 0) {
        this.$message.warn('请选择要删除的记录')
        return
      }
      this.$confirm({
        content: `是否删除选中${m.length}条数据？`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          const ids = m.map((it) => it.id).join(',')
          cancelRelateForm({ 'ids': ids }).then((res) => {
            if (res.success) {
              this.$message.success(res.message)
              this.loadLinkedForms()
            } else {
              console.error('删除关联工位关联表单失败：', res.message)
              this.$message.error('删除关联工位关联表单失败')
            }
          })
        }
      })
    },
    editRowEvent(row) {
      this.curEditMode = 2
      this.$refs.listTable.setActiveRow(row)
    },
    delRowEvent(row) {
      this.forms.map((item2, index2) => {
        if (row.id === item2.id) {
          delTpPlanForm('ids=' + row.id)
            .then((res) => {
              if (res.success) {
                this.forms.splice(index2, 1)
                this.searchEvent();
                this.$message.success('删除成功')
              } else {
                this.$message.error('删除失败')
                console.error('删除模板表单失败：', res.message)
              }
            })
            .catch((err) => {
              this.$message.error('删除异常')
              console.error('删除模板表单异常：', res.message)
            })
        } else {
          this.$message.error('删除失败')
          console.error('删除模板表单失败：', res.message)
        }
      })
    },
    saveRowEvent(row) {
      this.$refs.listTable.validate(row, (valid) => {
        if (!valid) {
          let linkList = [row]
          relateForm(linkList).then((res) => {
            if (res.success) {
              this.$message.success('关联成功!')
              this.$refs.listTable.clearActived()
              this.curEditMode = 0
              //this.getRelatedFormList()
            } else {
              this.$message.error('关联失败!')
              console.error('保存关联表单失败', res.message)
            }
          })
        }
      })
    },
    cancelRowEvent(row) {
      this.$refs.listTable.clearActived()
      if (this.curEditMode === 1) {
        // 新增,点击取消
        this.$refs.listTable.remove(row)
      }
      this.curEditMode = 0
    },
    openFormSelectModal(row) {
      this.curRow = row
      this.$refs.modalForm.showModal({
        lineId: this.lineId,
        repairProgramId: this.repairProgramId
      })
      this.$refs.formSelect.blur()
    },
    onSelectForm(data) {
      if (data && data.forms.length > 0) {
        const form = data.forms[0]

        // 判断选择的表单是否已经存在
        let flag = 0
        this.forms.map((f) => {
          if (f.formId === form.id) {
            flag = 1
            return false
          }
        })

        if (flag === 1) {
          this.$message.error('你选择的表单已经在列表中存在，请选择其他表单')
          return
        }

        this.curRow.code=form.code
        this.curRow.formName = form.title
        this.curRow.formId = form.id
        // 当选择的formType 是2或者3时，都设置为 3
        this.curRow.formType = data.formType === 2 || data.formType === 3 ? 3 : data.formType
        this.curRow.formType_dictText=data.formTypeName
        // 设置记录表类型
        this.curRow.reguName = form.reguName
        this.curRow.reguVersion = form.reguVersion

        this.curRow.lineId = form.lineId
        this.curRow.repairProId = form.repairProgramId
        this.curRow.repairProName = form.repairProgramName
      } else {
        this.curRow.code = ''
        this.curRow.formName = ''
        this.curRow.workstationId = ''
        this.curRow.formType = ''
        this.curRow.formId=''
        this.curRow.type=''
        this.curRow.lineId = ''
        this.curRow.reguName = ''
        this.curRow.reguVersion = ''
        this.curRow.repairProId = ''
        this.curRow.repairProName = ''
      }
    },
    onSelectForms(data) {
      if (data && data.forms.length > 0) {
        // let isWarn = false
        let saveLink = []
        data.forms.forEach((form) => {
          if (this.forms.find((formItem) => formItem.formId === form.id)) {
            this.$message.warn(`${form.title} 已经在列表中存在`)
          } else {
            let addForm = {
              id: randomUUID(),
              code: form.code,
              formName: form.title,
              formId: form.id,
              formType: data.formType === 2 || data.formType === 3 ? 3 : data.formType,
              formType_dictText: data.formTypeName,
              reguName: form.reguName,
              reguVersion: form.reguVersion,
              lineId: form.lineId,
              workstationId: this.station.id,
              repairProId: form.repairProgramId,
              repairProName: form.repairProgramName
            }
            saveLink.push(addForm)
          }
        })
        relateForm(saveLink).then((res) => {
          if (res.success) {
            this.$message.success('关联成功!')
            this.$refs.listTable.clearActived()
            this.curEditMode = 0
            this.forms.push(...saveLink)
            this.searchEvent()
            //this.getRelatedFormList()
          } else {
            this.$message.error('关联失败!')
            console.error('保存关联表单失败', res.message)
          }
        })
      }
    },
    handleSeeing(data) {
      if (data.formType === 1) {
        this.$refs.formViewModal.showModal(data.formId)
      }
      if(data.formType === 3) {
        this.$refs.formViewModal.showModal(data.formId)
        /*if(data.workRecordType===1) {
          this.$refs.recordTableView.show(data.formId)
        } else if(data.workRecordType === 2){
          this.$refs.formViewModal.showModal(data.formId)
        }*/
      }
      if(data.formType === 4) {
        getWorkcheck({
          id: data.formId,
        }).then((res) => {
          if (res.success && res.result) {
            let formData = res.result;
            this.$refs.jobCheckTableView.show(formData)
          } else {
            this.$message.error('加载记录数据异常')
            console.error('加载记录数据失败', res.message)
          }
        })
      }
    },
  },
}
</script>

<style scoped>
</style>