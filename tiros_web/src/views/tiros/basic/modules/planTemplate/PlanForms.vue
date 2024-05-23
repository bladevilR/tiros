<template>
  <a-modal
    centered
    :width="'100%'"
    :title="'计划表单关联'"
    dialogClass='fullDialog no-footer'
    :visible='visible'
    :confirmLoading='loading'
    @ok='close'
    @cancel='close'
    :footer='null'
    :destroyOnClose='true'
  >
    <div class='table-page-search-wrapper'>
      <a-form layout='inline'>
        <a-row :gutter='24'>
          <a-col :md='5' :sm='24'>
            <a-form-item label='表单名称'>
              <a-input placeholder='请输入内容' v-model='queryParam.searchText' allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='表单类型'>
              <a-select v-model='queryParam.formType' allowClear>
                <a-select-option :value='3'> 作业记录表</a-select-option>
                <a-select-option :value='1'> 数据记录表</a-select-option>
                <a-select-option :value='4'> 检查记录表</a-select-option>
              </a-select>
            </a-form-item>
          </a-col>
          <!--          <a-col :md="4" :sm="24">
                      <a-form-item label="修程">
                        <j-dict-select-tag triggerChange v-model="queryParam.repairProgramId" dictCode="bu_repair_program,name,id" />
                      </a-form-item>
                    </a-col>
                    <a-col :md="4" :sm="24">
                      <a-form-item label="线路">
                        <line-select-list v-model="queryParam.lineId"></line-select-list>
                      </a-form-item>
                    </a-col>-->
          <a-col :md='6' :sm='8'>
            <a-form-item>
              <a-space>
                <a-button class='primary-color' @click='loadForms'>查询</a-button>
                <a-button type='dashed' class='primary-color' @click='handleAdd'>添加</a-button>
                <a-button type='dashed' class='primary-color' @click='importForms'>批量导入</a-button>
                <a-button type='dashed' @click='handleDel'>删除</a-button>
              </a-space>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style='height:calc(100vh - 135px)'>
      <vxe-table
        border
        height='auto'
        ref='listTable'
        align='center'
        :data.sync='forms'
        :edit-rules='validRules'
        :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
      >
        <vxe-table-column type='checkbox' width='40'></vxe-table-column>
        <vxe-table-column
          field='formName'
          title='表单名称'
          min-width='180'
          :edit-render="{ name: 'input' }"
          align='left'
          header-align='center'
        >
          <template v-slot='{row}'>
            <a @click='handleSeeing(row)'>{{ row.title }}</a>
          </template>
          <!--        @focus="openFormSelectModal(row)"-->
          <template v-slot:edit='{ row }'>
            <a-input
              ref='formSelect'
              v-model='row.title'
              placeholder='请选择表单'
              :open='false'
              style='width: 100%'
            >
              <a-icon slot='suffix' type='ellipsis' @click='openFormSelectModal(row)' />
            </a-input>
          </template>
        </vxe-table-column>
        <vxe-table-column field='formType_dictText' title='表单类型' width='120' :edit-render="{ name: 'input' }">
          <template v-slot='{ row }'>
            {{ row.formType !== 3 ? row.formType_dictText : row.workRecordType === 1 ? '作业记录表(老)' : '作业记录表(新)' }}
          </template>
          <template v-slot:edit='{ row }'>
            <a-input placeholder='请选择表单' disabled v-model='row.formType_dictText' />
          </template>
        </vxe-table-column>
        <vxe-table-column field='reguName' title='所属规程' width='120'>
          <template v-slot='{ row }'>
            {{ row.reguName }}
          </template>
          <template v-slot:edit='{ row }'>
            <a-input placeholder='请选择表单' disabled v-model='row.reguName' />
          </template>
        </vxe-table-column>
        <vxe-table-column field='reguVersion' title='规程版本' width='120'>
          <template v-slot='{ row }'>
            {{ row.reguVersion }}
          </template>
          <template v-slot:edit='{ row }'>
            <a-input placeholder='请选择表单' disabled v-model='row.reguVersion' />
          </template>
        </vxe-table-column>
        <vxe-table-column
          field='trainStructName'
          title='关联设备'
          width='160'
          :edit-render="{ name: 'input' }"
          align='left'
          header-align='center'
        >
          <template v-slot:edit='{ row }'>
            <a-select
              ref='trainStruct'
              v-model='row.trainStructName'
              placeholder='请选择关联设备'
              :open='false'
              style='width: 100%'
              @focus='openAssetTypeModal(row)'
            >
              <a-icon slot='suffixIcon' type='ellipsis' @click='openAssetTypeModal(row)' />
            </a-select>
          </template>
        </vxe-table-column>
        <vxe-table-column field='remark' title='备注' width='160' :edit-render="{ name: 'input' }" align='left'
                          header-align='center'>
          <template v-slot:edit='{ row }'>
            <a-input @change='remarkChangeValue(row)' :maxLength='201' placeholder='请输入备注' v-model='row.remark' />
          </template>
        </vxe-table-column>
        <vxe-table-column title='操作' width='160'>
          <template v-slot='{ row }'>
            <template v-if='$refs.listTable.isActiveByRow(row)'>
              <a-space>
                <a-button type='dashed' size='small' @click='saveRowEvent(row)'>保存</a-button>
                <a-button type='dashed' size='small' @click='cancelRowEvent(row)'>取消</a-button>
              </a-space>
            </template>
            <template v-else>
              <a-button type='dashed' size='small' @click='editRowEvent(row)'>编辑</a-button>
              <!--  <a-button type="dashed" size="small" @click="delRowEvent(row)">删除</a-button>-->
            </template>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <FormsList ref='modalForm' :multiple='false' @ok='onSelectForm'></FormsList>
    <FormsList ref='modalForms' @ok='onSelectForms'></FormsList>
    <!--    <TrainAssetType
          ref="assetTypeModal"
          title="设备选择"
          :multiple="false"
          :trainTypeId="trainTypeId"
          @ok="onAssetTypeSelect"
          @cancel="onCancelAssetTypeSelect"
        ></TrainAssetType>-->
    <RecordTableView ref='recordTableView'></RecordTableView>
    <FormViewModal ref='formViewModal'></FormViewModal>
    <JobCheckTableView ref='jobCheckTableView'></JobCheckTableView>
    <train-structure-list ref='assetSelect' :multiple='false' @ok='addTrainStruct'
                          :line-id='lineId'></train-structure-list>
  </a-modal>
</template>

<script>
import { everythingIsEmpty, randomUUID } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import FormsList from '@views/tiros/common/selectModules/FormsList'
import TrainAssetType from '@views/tiros/common/selectModules/TrainAssetType'
import { addTpPlanForm, delTpPlanForm, editTpPlanForm, listTpPlanForms } from '@/api/tirosApi'
import RecordTableView from '@views/tiros/basic/modules/workRecordSheet/RecordTableView'
import JobCheckTableView from '@views/tiros/basic/modules/jobCheckSheet/JobCheckTableView'
import FormViewModal from '@views/tiros/basic/customform/FormViewModal'
import { getWorkcheck } from '@/api/tirosQualityApi'
import TrainStructureList from '@views/tiros/common/selectModules/TrainStructureList'

export default {
  name: 'PlanForms',
  components: {
    FormsList, TrainAssetType, RecordTableView, JobCheckTableView, FormViewModal, TrainStructureList,
    LineSelectList
  },
  data() {
    return {
      queryParam: {
        tpPlanId: '',
        searchText: '',
        formType: ''
      },
      title: '表单关联管理',
      visible: false,
      loading: false,
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      validRules: {
        title: [{ required: true, message: '表单必须选择' }],
        remark: [
          { max: 200, type: 'string', message: '备注不能超过200个字符' }
        ]
      },
      lineId: '',
      repairProgramId: '',
      forms: [],
      planId: '',
      trainTypeId: ''
    }
  },
  methods: {
    remarkChangeValue({ remark }) {
      if (remark.length > 200) {
        this.$message.error('备注不能超过200个字符')
      }
    },
    show(planInfo) {
      this.planId = planInfo.id
      this.trainTypeId = planInfo.trainTypeId
      this.lineId = planInfo.lineId
      this.repairProgramId = planInfo.repairProgramId
      this.loadForms()
      this.visible = true
    },
    // 关闭
    close() {
      this.$emit('close')
      this.visible = false
      // 重置数据
      Object.assign(this.$data, this.$options.data())
    },
    loadForms() {
      this.forms = []
      this.queryParam.tpPlanId = this.planId
      listTpPlanForms(this.queryParam)
        .then((res) => {
          if (res.success) {
            this.forms = res.result
          } else {
            this.$message.error('获取模板表单失败')
            console.error('获取模板表单失败:', res.message)
          }
        })
        .catch((err) => {
          this.$message.error('获取模板表单异常')
          console.error('获取模板表单异常:', err)
        })
    },
    handleAdd() {
      if (this.curEditMode > 0) {
        return
      }
      this.curEditMode = 1
      let form = {
        id: randomUUID(),
        tpPlanId: this.planId,
        formName: ''
      }
      this.$refs.listTable.insertAt(form, -1).then(({ row }) => {
        this.$refs.listTable.setActiveCell(row, 'remark')
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
          delTpPlanForm('ids=' + ids)
            .then((res) => {
              if (res.success) {
                m.forEach((item1) => {
                  this.forms.forEach((item2, index2) => {
                    if (item1.id === item2.id) {
                      this.forms.splice(index2, 1)
                    }
                  })
                })
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
          if (this.curEditMode === 1) {
            addTpPlanForm(row)
              .then((res) => {
                if (res.success) {
                  this.forms.push(row)
                  this.$refs.listTable.clearActived()
                  this.curEditMode = 0
                  this.$message.success('保存成功')
                } else {
                  this.$message.error('保存失败')
                  console.error('保存模板表单失败：', res.message)
                }
              })
              .catch((err) => {
                this.$message.error('保存异常')
                console.error('保存模板表单异常：', err)
              })
          }
          if (this.curEditMode === 2) {
            // 更新
            editTpPlanForm(row)
              .then((res) => {
                if (res.success) {
                  this.$refs.listTable.clearActived()
                  this.curEditMode = 0
                } else {
                  this.$message.error('保存失败')
                  console.error('保存模板表单失败：', res.message)
                }
              })
              .catch((err) => {
                this.$message.error('保存异常')
                console.error('保存模板表单异常：', err)
              })
          }
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

        // 不需要进行重复验证：可以1个表单添加n次，表示n份
        // // 判断选择的表单是否已经存在
        // let flag = 0
        // this.forms.map((f) => {
        //   if (f.objId === form.id) {
        //     flag = 1
        //     return false
        //   }
        // })
        //
        // if (flag === 1) {
        //   this.$message.error('你选择的表单已经在列表中存在，请选择其他表单')
        //   return
        // }

        this.curRow.title = form.title
        this.curRow.objId = form.id
        // 当选择的formType 是2或者3时，都设置为 3
        this.curRow.formType = form.formType
        this.curRow.formType_dictText = form.formType_dictText
        // 设置记录表类型
        this.curRow.workRecordType = form.workRecordType
        this.curRow.reguId = form.reguId
        this.curRow.reguName = form.reguName
        this.curRow.reguVersion = form.reguVersion
      } else {
        this.curRow.title = ''
        this.curRow.objId = ''
        this.curRow.formType = ''
        this.curRow.formType_dictText = ''
        this.curRow.workRecordType = 0
        this.curRow.reguId = ''
        this.curRow.reguName = ''
        this.curRow.reguVersion = ''
      }
    },
    onSelectForms(data) {
      if (data && data.forms.length > 0) {
        // let isWarn = false
        data.forms.forEach((e) => {
          if (this.forms.find((formItem) => formItem.objId === e.id)) {
            this.$message.warn(`${e.title} 已经在列表中存在`)
          } else {
            let form = {
              id: randomUUID(),
              tpPlanId: this.planId,
              title: e.title,
              assetTypeName: null,
              formType: e.formType,
              formType_dictText: e.formType_dictText,
              workRecordType: e.workRecordType,
              objId: e.id,
              remark: null,
              reguId: e.reguId,
              reguName: e.reguName,
              reguVersion: e.reguVersion
            }

            // 批量保存
            addTpPlanForm(form)
              .then((res) => {
                if (res.success) {
                  this.forms.push(form)
                  this.$refs.listTable.clearActived()
                  this.curEditMode = 0
                } else {
                  this.$message.error('保存失败')
                  console.error('保存模板表单失败：', res.message)
                }
              })
              .catch((err) => {
                this.$message.error('保存异常')
                console.error('保存模板表单异常：', err)
              })
          }
        })
      }
    },
    // 弹出设备选择界面
    openAssetTypeModal(row) {
      this.curRow = row
      this.$refs.assetSelect.showModal()
      this.$refs.trainStruct.blur()
    },
    // 设备选择回调
    /*  onAssetTypeSelect(data) {
        if (data.length) {
          const item = data[0]
          if (item) {
            this.curRow.assetTypeName = item.name
            this.curRow.assetTypeId = item.id
          } else {
            this.curRow.assetTypeName = ''
            this.curRow.assetType = ''
          }
        } else {
          this.curRow.assetTypeName = ''
          this.curRow.assetType = ''
        }
      },*/
    // 设备选择取消回调
    onCancelAssetTypeSelect() {
    },
    handleSeeing(data) {
      if (data.formType === 1) {
        this.$refs.formViewModal.showModal(data.objId)
      }
      if (data.formType === 3) {
        if (data.workRecordType === 1) {
          this.$refs.recordTableView.show(data.objId)
        } else if (data.workRecordType === 2) {
          this.$refs.formViewModal.showModal(data.objId)
        }
      }
      if (data.formType === 4) {
        getWorkcheck({
          id: data.objId
        }).then((res) => {
          if (res.success && res.result) {
            let formData = res.result
            this.$refs.jobCheckTableView.show(formData)
          } else {
            this.$message.error('加载记录数据异常')
            console.error('加载记录数据失败', res.message)
          }
        })
      }
    },

    addTrainStruct(data) {
      if (!everythingIsEmpty(data)) {
        // this.curRow.trainStructName =data[0].name;
        // this.curRow.trainStructId = data[0].id;
        this.$set(this.curRow, 'trainStructName', data[0].name)
        this.$set(this.curRow, 'trainStructId', data[0].id)
      }
    }
  }
}
</script>

<style scoped>
</style>