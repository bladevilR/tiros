<template>
  <a-modal
    :title="`${queryParam.id ? '编辑' : '新增'}${title}`"
    width="100%"
    dialogClass="fullDialog"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <div class="bodyWrapper" style="height: 100% !important">
      <a-spin class="full-srceen" :spinning="confirmLoading" style="padding: 0 12px; height: 100% !important">
        <!-- <div class="table-page-search-wrapper"> -->
        <NaCardContent title="基本信息">
          <a-form :form="form" layout="inline">
            <a-row :gutter="24">
              <a-col :md="4" :sm="24">
                <a-form-item label="步骤" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }" style="width: 100%">
                  <!-- <a-input v-decorator="['fileNo', validatorRules.fileNo]" placeholder="请输入内容"></a-input> -->
                  <a-input-number
                    :max="999999999"
                    v-decorator="['stepNum', validatorRules.stepNum]"
                    placeholder="请输入步骤"
                    style="width: 100%"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="8" :sm="24">
                <a-form-item label="步骤标题" style="width: 100%" :labelCol="{ span: 6 }" :wrapperCol="{ span: 18 }">
                  <a-input
                    :maxLength="65"
                    v-decorator="['stepTitle', validatorRules.stepTitle]"
                    placeholder="请输入内容"
                    style="width: 100%"
                  ></a-input>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
          <!-- </div> -->
        </NaCardContent>
        <NaCardContent title="步骤明细" height="100%">
          <a-tabs v-model="activeKey" @tabClick="onTabClick" @change="tabChangeEvent" :forceRender="true">
            <a-tab-pane :key="1" tab="步骤内容">
              <j-editor
                v-if="isShowEditor"
                v-model="queryParam.stepContent"
                @change="onEditor"
                triggerChange
              ></j-editor>
            </a-tab-pane>
            <a-tab-pane :key="2" tab="额定物料">
              <div style="height: calc(100vh - 334px)">
                <vxe-table
                  border
                  ref="materialListTable"
                  align="center"
                  height="auto"
                  :data="queryParam.materialList"
                  :edit-rules="validRules"
                  :keep-source="true"
                  :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
                  :edit-config="{ trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
                  @checkbox-change="rangeChange"
                  @checkbox-all="rangeChange"
                >
                  <vxe-table-column type="checkbox" width="60px" />
                  <vxe-table-column field="name" title="物料名称" />
                  <vxe-table-column field="category2_dictText" title="物料分类" />
                  <vxe-table-column field="amount" title="所需数量" :edit-render="{ name: 'input' }">
                    <template v-slot:edit="{ row }">
                      <a-input-number
                        v-model="row.amount"
                        :defaultValue="1"
                        :min="1"
                        :max="999999"
                        style="width: 100%"
                      />
                    </template>
                  </vxe-table-column>
                  <vxe-table-column field="unit" title="单位" />
                  <vxe-table-column field="code" title="物料编码" />
                  <vxe-table-column title="操作" width="120">
                    <template v-slot="{ row }">
                      <template v-if="$refs.materialListTable.isActiveByRow(row)">
                        <a-space>
                          <a-button type="dashed" size="small" @click.stop="saveRowData()">保存</a-button>
                          <a-button type="dashed" size="small" @click.stop="cencelRowData()">取消</a-button>
                        </a-space>
                      </template>
                      <template v-else>
                        <a-button type="dashed" :disabled="false" size="small" @click.stop="handleEdit(row)">编辑</a-button>
                      </template>
                    </template>
                  </vxe-table-column>
                </vxe-table>
              </div>
            </a-tab-pane>
            <a-tab-pane :key="3" tab="所需工具">
              <!-- 工器具 -->
              <div style="height: calc(100vh - 334px)">
                <vxe-table
                  border
                  ref="toolListTable"
                  align="center"
                  height="auto"
                  :data="queryParam.toolList"
                  :edit-rules="validRules"
                  :keep-source="true"
                  :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
                  :edit-config="{ trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
                  @checkbox-change="rangeChange"
                  @checkbox-all="rangeChange"
                >
                  <vxe-table-column type="checkbox" width="60px" />
                  <vxe-table-column field="name" title="工器具名称" />
                  <vxe-table-column field="kind_dictText" title="工器具分类" />
                  <vxe-table-column field="amount" title="所需数量" :edit-render="{ name: 'input' }">
                    <template v-slot:edit="{ row }">
                      <a-input-number
                        v-model="row.amount"
                        :defaultValue="1"
                        :min="1"
                        :max="999999"
                        style="width: 100%"
                      />
                    </template>
                  </vxe-table-column>
                  <vxe-table-column field="unit" title="单位" />
                  <vxe-table-column field="code" title="物料编码" />
                  <vxe-table-column title="操作" width="120">
                    <template v-slot="{ row }">
                      <template v-if="$refs.toolListTable.isActiveByRow(row)">
                        <a-space>
                          <a-button type="dashed" size="small" @click.stop="saveRowData()">保存</a-button>
                          <a-button type="dashed" size="small" @click.stop="cencelRowData()">取消</a-button>
                        </a-space>
                      </template>
                      <template v-else>
                        <a-button type="dashed" :disabled="false" size="small" @click.stop="handleEdit(row)">编辑</a-button>
                      </template>
                    </template>
                  </vxe-table-column>
                </vxe-table>
              </div>
            </a-tab-pane>
            <template #tabBarExtraContent>
              <a-space>
                <a-button type="primary" :disabled="isSaveData" v-if="activeKey !== 1" @click="openFormModal">新增</a-button>
                <!-- <a-button type="dashed" :disabled="!(isCheckRow && !isSaveData )" v-if="activeKey !== 1" @click="handleEdit"
                  >编辑</a-button
                > -->
                <a-button type="dashed" :disabled="!(isCheckRows && !isSaveData)" v-if="activeKey !== 1" @click="handleDel"
                  >删除</a-button
                >
                <a-button type="primary" v-if="isSaveData && activeKey !== 1" @click="saveRowData">保存</a-button>
                <a-button v-if="isSaveData && activeKey !== 1" @click="cencelRowData">取消</a-button>
              </a-space>
            </template>
          </a-tabs>
        </NaCardContent>
      </a-spin>
    </div>
    <material-list ref="materialModal" :multiple="true" @ok="onSelectMaterial"></material-list>
    <ToolsList ref="toolsModal" batch :multiple="true" @ok="onSelectTool" :canSelectType="[4, 5]"></ToolsList>
  </a-modal>
</template>

<script>
import NaCardContent from '@comp/tiros/NaCardContent'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import ToolTypeSelect from '@views/tiros/common/selectModules/ToolTypeSelect'
import { saveSopDetailRecord, getSopDetailRecord } from '@/api/tirosApi'
import JEditor from '@/components/jeecg/JEditor'
import { randomUUID } from '@/utils/util'

export default {
  name: 'WorkSopDetailModal',
  components: { LineSelectList, JEditor, NaCardContent, MaterialList, ToolsList: ToolTypeSelect },
  data() {
    return {
      isCheckRow: false,
      isCheckRows: false,
      isSaveData: false,
      isShowEditor: true,
      value: 1,
      title: '步骤',
      visible: false,
      activeKey: 1,
      contentStr: '',
      model: {},
      curRow: null,
      confirmLoading: false,
      form: this.$form.createForm(this),
      validRules: {
        amount: [{ required: true, message: '数量必须填写' }],
      },
      validatorRules: {
        stepNum: { rules: [{ required: true, message: '请输入步骤' }] },
        stepTitle: { rules: [{ required: true, message: '请输入标题' },{ max: 64, message: '不能超过64个字符' }] },
      },
      queryParam: {
        bookId: null,
        stepNum: null,
        stepTitle: null,
        stepContent: null,
        materialList: [],
        toolList: [],
      },
      historyActiveKey: null,
      maxNumber: -1,
    }
  },
  computed: {
    activeTableRef() {
      if (this.activeKey === 3) {
        return this.$refs.toolListTable
      } else {
        return this.$refs.materialListTable
      }
    },
  },
  created() {},
  methods: {
    add(bookId, stepNum) {
      this.queryParam.bookId = bookId
      this.maxNumber = stepNum
      this.edit({})
    },
    edit(record) {
      this.confirmLoading = false
      this.visible = true
      this.activeKey = 1
      this.isSaveData = false
      if (record.id) {
        // record = Object.assign(this.queryParam, JSON.parse(JSON.stringify(record)))
        getSopDetailRecord({
          detailId: record.id,
        }).then((res) => {
          if (res.success) {
            record = Object.assign(this.queryParam, JSON.parse(JSON.stringify(res.result)))
          } else {
            this.$message.error(res.message)
            this.visible = false
          }
        })
      } else {
        this.queryParam.id = null
        this.queryParam.stepNum = null
        this.queryParam.stepTitle = null
        this.queryParam.stepContent = null
        this.queryParam.materialList = []
        this.queryParam.toolList = []
        if (this.maxNumber > 0) {
          this.queryParam.stepNum = this.maxNumber
        }
        record = JSON.parse(JSON.stringify(this.queryParam))
      }
      this.form.resetFields()
      this.model = JSON.parse(JSON.stringify(record))
      this.visible = true
      this.$nextTick(() => {
        this.isShowEditor = true
        this.form.setFieldsValue({
          stepNum: record.stepNum,
          stepTitle: record.stepTitle,
        })
      })
      this.queryParam.stepNum = -1
    },
    // 确定
    handleOk() {
      console.log(2323232)
      this.save()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.isShowEditor = false
      this.visible = false
    },
    // 保存数据
    save() {
      this.confirmLoading = true
      this.form.validateFields((err, values) => {
        if (!err) {
          Object.assign(this.queryParam, values)
          saveSopDetailRecord(this.queryParam).then((res) => {
            if (res.success) {
              this.$message.success('保存成功')
              this.isShowEditor = false
              this.$emit('ok', this.queryParam.bookId)
              this.visible = false
            } else {
              this.$message.error(res.message)
            }
          }).finally(()=>{
            this.confirmLoading = false
          })
        } else {
          this.confirmLoading = false
        }
      })
    },
    //保存Row数据
    saveRowData() {
      let record = this.activeTableRef.getActiveRecord()
      if (!record) {
        return
      }
      this.activeTableRef.validate(record.row, (valid) => {
        if (!valid) {
          // if (!record.row.id) {
          if (this.activeKey === 2) {
            if (!this.queryParam.materialList.find((e) => e === record.row)) {
              record.row.id = randomUUID()
              this.queryParam.materialList.push(Object.assign({}, record.row))
            }
          } else {
            if (!this.queryParam.toolList.find((e) => e === record.row)) {
              record.row.id = randomUUID()
              this.queryParam.toolList.push(Object.assign({}, record.row))
            }
          }
          // }
          this.isSaveData = false
          this.activeTableRef.clearActived()
        } else {
          for (const validKey in valid) {
            let vals = valid[validKey]
            vals.forEach((item) => {
              if (item.rule) {
                this.$message.error(item.rule.message)
              }
            })
          }
        }
      })
      this.rangeChange()
    },
    // 取消编辑或保存
    cencelRowData() {
      let record = this.activeTableRef.getActiveRecord()
      if (record) {
        if (record.row.id) {
          // 还原行数据
          this.activeTableRef.revertData(record.row)
        } else {
          // 新增,点击取消
          this.activeTableRef.remove(record.row)
        }
      }
      this.activeTableRef.clearActived()
      this.isSaveData = false
    },
    handleEdit(row) {
      // if (this.activeTableRef.getActiveRecord()) {
      //   this.$message.warn('有未保存数据')
      //   return
      // }
      // let record = this.activeTableRef.getCheckboxRecords()[0]
      this.activeTableRef.setActiveRow(row)
      this.isSaveData = true
    },
    handleDel() {
      let records = this.activeTableRef.getCheckboxRecords()
      this.$confirm({
        content: `是否删除选中${records.length}条数据？`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          records.forEach((record) => {
            if (this.activeKey === 2) {
              this.queryParam.materialList.splice(
                this.queryParam.materialList.findIndex((e) => e === record),
                1
              )
            } else if (this.activeKey === 3) {
              this.queryParam.toolList.splice(
                this.queryParam.toolList.findIndex((e) => e === record),
                1
              )
            }
            this.isCheckRow = false
            this.isCheckRows = false
          })
        },
      })
    },
    openFormModal() {
      switch (this.activeKey) {
        case 2:
          this.$refs.materialModal.showModal()
          break

        case 3:
          this.$refs.toolsModal.showModal()
          break
      }
    },
    onEditor(value) {
      // console.log('输出内容')
      this.queryParam.stepContent = value
    },
    // 选择物料
    onSelectMaterial(data) {
      data.forEach((element) => {
        if (this.queryParam.materialList.find((e) => e.materialTypeId === element.id)) {
          this.$message.warn(`${element.name}物料已经存在列表中`)
          return
        }
        let item = {
          amount: Number(element.num),
          bookDetailId: this.queryParam.id,
          category1: element.category1,
          category1_dictText: element.category1_dictText,
          category2: element.category2,
          category2_dictText: element.category2_dictText,
          code: element.code,
          id: '',
          kind: element.kind,
          materialTypeId: element.id,
          name: element.name,
          price: element.price,
          spec: element.spec,
          unit: element.unit,
        }
        this.queryParam.materialList.push(item)
        // this.$refs.materialListTable.insertAt.call(this, item, -1).then(({ row }) => {
        //   this.isSaveData = true
        //   this.$refs.materialListTable.setActiveCell(row, 'amount')
        // })
      })
    },
    // 选择工器具
    onSelectTool(data) {
      data.forEach((element) => {
        if (this.queryParam.toolList.find((e) => e.toolTypeId === element.id)) {
          this.$message.warn(`${element.name}工器具已经存在列表中`)
          return
        }
        let item = {
          amount: Number(element.num),
          bookDetailId: this.queryParam.id,
          kind_dictText: element.kind_dictText,
          code: element.code,
          id: '',
          toolTypeId: element.id,
          unit: element.unit,
          name: element.name,
          status_dictText: element.status_dictText,
        }
        this.queryParam.toolList.push(item)
        // this.$refs.toolListTable.insertAt.call(this, item, -1).then(({ row }) => {
        //   this.isSaveData = true
        //   this.$refs.toolListTable.setActiveCell(row, 'amount')
        // })
      })
    },
    // 用户选择记录触发
    rangeChange() {
      let records = this.activeTableRef.getCheckboxRecords()
      this.isCheckRow = records.length === 1
      this.isCheckRows = records.length > 0
    },
    testEvent(event) {
      event.preventDefault()
      return false
    },
    onTabClick() {
      if (this.activeTableRef && this.activeTableRef.getActiveRecord()) {
        this.historyActiveKey = this.activeKey
        this.$message.warn('有未保存数据')
        return
      } else {
        this.historyActiveKey = null
      }
    },
    tabChangeEvent() {
      if (this.historyActiveKey) {
        this.activeKey = this.historyActiveKey
      }
      this.isShowEditor = this.activeKey === 1
    },
  },
}
</script>