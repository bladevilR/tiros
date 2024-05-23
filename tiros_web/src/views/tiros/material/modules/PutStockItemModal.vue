<template>
  <j-modal
    :title="title"
    :width="'70%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    fullscreen
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24" style="margin:0">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="入库单号">
              <a-input
                style="width: 100%"
                v-decorator.trim="['entryNo']"
                placeholder="入库单号,自动生成"
                disabled
              ></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="移入日期">
              <a-date-picker style="width: 100%" v-decorator.trim="['entryDate', validatorRules.entryDate]" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="移入部门">
              <j-select-depart
                placeholder="请选择移入部门"
                v-decorator.trim="['entryDeptId', validatorRules.entryDeptId]"
                :rootOpened="false"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24" style="margin:0;padding: 0 12px">
          <a-divider orientation="left" style="margin: 0 0 12px; font-size: 14px"> 入库明细 </a-divider>
          <a-row :gutter="24" style="margin-bottom: 12px">
            <a-col :md="8" :sm="24">
              <a-button style="margin-right: 12px" @click="addDetailItem">新增</a-button>
              <a-button @click="delDetailItem">删除</a-button>
            </a-col>
          </a-row>
          <div :style="tableHeight.top">
            <vxe-table
              ref="listTable"
              :height="tableHeight.size"
              align="center"
              border
              :data="detailList"
              show-overflow="tooltip"
              :edit-rules="validRules"
              :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
              :edit-config="{ key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
            >
              <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
              <vxe-table-column field="materialTypeCode" title="物资编码" :edit-render="{ name: 'input' }">
                <template v-slot="{ row }">
                  <a-input
                    ref="formSelect"
                    v-model="row.materialTypeCode"
                    placeholder="请选择物资"
                    :open="false"
                    style="width: 100%"
                    @click="openMaterialSelectModal(row)"
                  >
                    <a-icon slot="suffix" type="ellipsis" />
                  </a-input>
                </template>
              </vxe-table-column>
              <vxe-table-column field="materialTypeName" title="物资名称">
                <template #default="{ row }">
                  <a-input
                    style="width: 100%"
                    v-model="row.materialTypeName"
                    placeholder="输入物资名称"
                    disabled
                  ></a-input>
                </template>
              </vxe-table-column>
              <vxe-table-column field="materialTypeSpec" title="物资描述" width="300">
                <template #default="{ row }">
                  <a-input
                    style="width: 100%"
                    v-model="row.materialTypeSpec"
                    placeholder="输入物资描述"
                    disabled
                  ></a-input>
                </template>
              </vxe-table-column>
              <vxe-table-column field="materialTypeUnit" title="单位" width="140">
                <template #default="{ row }">
                  <a-input style="width: 100%" v-model="row.materialTypeUnit" placeholder="输入单位" disabled></a-input>
                </template>
              </vxe-table-column>
              <vxe-table-column field="amount" title="入库数量" width="140">
                <template #default="{ row }">
                  <a-input-number
                    style="width: 100%"
                    v-model="row.amount"
                    placeholder="输入入库数量"
                    :min="0"
                  ></a-input-number>
                </template>
              </vxe-table-column>
              <vxe-table-column field="price" title="入库单价" width="140">
                <template #default="{ row }">
                  <!-- <a-input style="width: 100%" v-model="row.price" placeholder="输入入库单价"></a-input> -->
                  <a-input-number
                    style="width: 100%"
                    v-model="row.price"
                    placeholder="输入入库单价"
                    :min="0"
                  ></a-input-number>
                </template>
              </vxe-table-column>
              <vxe-table-column field="ebsWarehouseLevl2" title="移入二级库">
                <template #default="{ row }">
                  <j-dict-select-tag
                    v-model="row.ebsWarehouseLevl2"
                    dictCode="bu_mtr_warehouse,name,id,wh_level='3'" />
<!--                  <j-tree-select
                    placeholder="请选择二级库"
                    dict="bu_mtr_warehouse,name,id"
                    pidField="parent_id"
                    condition='{"wh_level":"3"}'
                    v-model="row.ebsWarehouseLevl2"
                  />-->
                </template>
              </vxe-table-column>
              <vxe-table-column field="selfWarehouseName" title="目标库位" width="220">
                <template #default="{ row }">
                  <a-select
                    v-model="row.selfWarehouseName"
                    placeholder="请选择物料"
                    :open="false"
                    style="width: 100%"
                    @dropdownVisibleChange="openWareHouseTree(row)"
                  >
                    <a-icon slot="suffixIcon" type="ellipsis" />
                  </a-select>
                  <!-- <a-input
                    ref="locationSelect"
                    placeholder="选择库位"
                    v-model="row.selfWarehouseId"
                    style="width: 100%"
                    @click="wareHouseTree(row)"
                  >
                    <a-icon slot="suffix" type="ellipsis" />
                  </a-input> -->
                </template>
              </vxe-table-column>
            </vxe-table>
          </div>
        </a-row>
      </a-form>
    </a-spin>
    <!-- <LocationList ref="locationModal" @ok="onSelectLocation"></LocationList> -->
    <WareHouseTree ref="wareHouseTree" @ok="onSelectLocation"></WareHouseTree>
    <material-list ref="modalForm" :multiple="false" @ok="onSelectMaterial"></material-list>
  </j-modal>
</template>

<script>
import moment from 'moment'
import { savePutStockItem } from '@/api/tirosMaterialApi'
import JSelectDepart from '@/components/jeecgbiz/JSelectDepart'
import LocationList from '@views/tiros/common/selectModules/LocationList'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'
import JTreeSelect from '@/components/jeecg/JTreeSelect'
import WareHouseTree from '@views/tiros/material/putStock/WareHouseTree'

export default {
  name: 'PutStockItemModal',
  components: { JSelectDepart, JTreeSelect, LocationList, MaterialList, WareHouseTree },
  data() {
    return {
      title: '操作',
      visible: false,
      model: {},
      detailItem: {},
      detailList: [],
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 14 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        entryDate: { rules: [{ required: true, message: '请选择移入日期!' }] },
        entryDeptId: { rules: [{ required: true, message: '请选择移入部门!' }] },
      },
      validRules: {
        amount: [{ required: true, message: '请输入入库数量！' }],
        price: [{ required: true, message: '请输入入库单价！' }],
        ebsWarehouseLevl2: [{ required: true, message: '请选择二级库！' }],
        selfWarehouseId: [{ required: true, message: '请选择目标库位！' }],
      },
      tableHeight: {
        top: 'height: calc(100vh - 265px)',
        bottom: 'height: calc(100vh - 265px)',
        size: '100%',
      },
      selectDetailRecordIndex: 0
    }
  },
  created() {},
  methods: {
    add() {
      this.edit({})
    },
    edit(record) {
      this.form.resetFields()
      this.visible = true
      this.model = Object.assign({}, record)
      if (record.id) {
        this.$nextTick(() => {
          this.form.setFieldsValue({
            entryNo: this.model.entryNo,
            entryDate: this.model.entryDate,
            entryDeptId: this.model.entryDeptId,
          })
        })
        this.detailList.push({
          materialTypeId: record.materialTypeId,
          materialTypeCode: record.materialTypeCode,
          materialTypeName: record.materialTypeName,
          materialTypeSpec: record.materialTypeSpec,
          materialTypeUnit: record.materialTypeUnit,
          price: record.price,
          amount: record.amount,
          ebsWarehouseLevl2: record.ebsWarehouseLevl2,
          selfWarehouseId: record.selfWarehouseId,
          selfWarehouseName: record.selfWarehouseName
        })
      }
    },
    addDetailItem() {
      this.detailList.push({
        materialTypeCode: '',
        price: '',
        amount: '',
        ebsWarehouseLevl2: '',
        selfWarehouseId: '',
      })
    },
    delDetailItem() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        selectRecords.forEach((selectItem) => {
          for (let i = 0; i < this.detailList.length; i++) {
            const detailItem = this.detailList[i]
            if (selectItem._XID == detailItem._XID) {
              this.detailList.splice(i, 1)
            }
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
      // if (this.detailList.length > 0) {
      //   this.detailList.splice(this.detailList.length - 1, 1)
      // }
    },
    openMaterialSelectModal(row) {
      this.detailItem = row
      this.$refs.modalForm.showModal()
    },
    onSelectMaterial(data) {
      if (data && data.length > 0) {
        const material = data[0]

        let flag = 0
        this.detailList.map((f) => {
          if (f.materialTypeCode === material.code) {
            flag = 1
            return false
          }
        })

        if (flag === 1) {
          this.$message.error('你选择的物料已经在列表中存在，请选择其他物料')
          return
        }
        this.detailItem.materialTypeId = material.id
        this.detailItem.materialTypeCode = material.code
        this.detailItem.materialTypeName = material.name
        this.detailItem.materialTypeSpec = material.spec
        this.detailItem.materialTypeUnit = material.unit
        this.detailItem.price = material.price

        this.detailItem.amount = ''
        this.detailItem.ebsWarehouseLevl2 = ''
        this.detailItem.selfWarehouseId = ''
      }
    },
    // 打开库位选择
    openlocationModal(row) {
      this.detailItem = row
      this.$refs.locationModal.showModal(this.detailItem.materialTypeId, null, null)
    },
    //库位选择
    onSelectLocation(records) {
      if (records.length && this.selectDetailRecordIndex > -1) {
        this.detailList[this.selectDetailRecordIndex].selfWarehouseId = records[0].id
        this.detailList[this.selectDetailRecordIndex].selfWarehouseName = records[0].name
        this.form.getFieldDecorator('selfWarehouseName')
        this.form.setFieldsValue({
          selfWarehouseName: records[0].name
        })
      }
    },
    openWareHouseTree(row){
      if (row.ebsWarehouseLevl2) {
        this.selectDetailRecordIndex = this.detailList.findIndex(e => e.materialTypeId === row.materialTypeId)
        this.$refs.wareHouseTree.showModal(row.ebsWarehouseLevl2)
      } else {
        this.$message.warn('请选择二级库位')
      }
    },
    // 确定
    handleOk() {
      const that = this
      that.form.validateFields((err, values) => {
        if (!err) {
          if (this.detailList.length == 0) {
            that.$message.warning('入库明细列表不能为空！')
            return false
          }

          for (let i = 0; i < this.detailList.length; i++) {
            const detailItem = this.detailList[i]
            for (const key in detailItem) {
              console.log(detailItem[key])
              if (detailItem[key] == '') {
                that.$message.warning('入库明细数据不能为空！')
                return false
              }
            }
          }

          that.confirmLoading = true
          let formData = Object.assign(that.model, values, {
            detailList: this.detailList,
            entryDate: moment(values.entryDate).format('YYYY-MM-DD'),
          })
          let obj
          if (!that.model.id) {
            obj = savePutStockItem(formData)
          } else {
            obj = savePutStockItem(formData)
          }
          obj
            .then((res) => {
              if (res.success) {
                that.$message.success(res.message)
                that.$emit('ok')
                that.close()
              } else {
                that.$message.warning(res.message)
              }
            })
            .finally(() => {
              that.confirmLoading = false
            })
        }
      })
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.detailList = []
      this.visible = false
    },
  },
}
</script>