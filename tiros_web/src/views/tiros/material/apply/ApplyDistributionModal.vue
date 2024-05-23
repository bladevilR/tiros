<template>
  <a-modal
    :title="`备料确认-${selectInfo ? selectInfo.materialTypeName : ''}物资`"
    :width="'90%'"
    :bodyStyle="{ height: '70vh' }"
    :visible="visible"
    :confirmLoading="confirmLoading"
    :closable="true"
    :centered="true"
    @ok="handleOk"
    @cancel="handleCancel"
    :forceRender="true"
    :destroyOnClose="true"
  >
    <div class="table-page-search-wrapper" v-if="!readonly" style="position: relative; z-index: 100">
      <a-form :form="form" layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item :labelCol="{ span: 10 }" :wrapperCol="{ span: 14 }" label="来源库位">
              <a-select
                allowClear
                ref="locationSelect"
                placeholder="选择库位"
                v-decorator="['sourceLocationName', validatorRules.sourceLocationName]"
                :open="false"
                style="width: 100%"
                @dropdownVisibleChange="openlocationModal"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="发放数量">
              <a-input-number
                placeholder="请输入数量"
                v-decorator="['amount', validatorRules.amount]"
                :max="locationMaxAmount"
                style="width: 100%"
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="存放托盘">
              <a-select
                @change="palletNameChange"
                allowClear
                ref="palletSelect"
                placeholder="选择托盘"
                v-decorator="['palletName', validatorRules.palletName]"
                :open="false"
                style="width: 100%"
                @dropdownVisibleChange="openPalletModal()"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col>
          <a-col :md="6">
            <a-space>
              <a-button type="primary" @click="handleAdd">添加</a-button>
              <span style="color: red; margin-left: 15px">{{`总量: ${selectInfo ? selectInfo.amount : 0};`}}</span>
              <span style="color: red; margin-left: 5px">{{ `差额：${calcAmount}`}}</span>
            </a-space>
          </a-col>
          <!-- <a-col :md="4">
            <span>{{`所需数量： 0; 还需要： 10`}}</span>
          </a-col> -->
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100% - 62px)" v-if="loading">
      <vxe-table
        border
        ref="listTable"
        height="auto"
        keep-source
        :data="tableData"
        :edit-config="readonly ? null : { trigger: 'click', mode: 'cell' }"
        @edit-actived="onEditActive"
        @edit-closed="onEditClosed"
      >
        <vxe-table-column title="物资编码"> {{ selectInfo ? selectInfo.materialTypeCode : '' }} </vxe-table-column>
        <!-- :edit-render="{ name: 'input' }" :edit-actived="onEditActive" -->
        <vxe-table-column field="sourceLocationName" title="来源库位">
          <!-- <template v-slot:edit="{row}">
            <a-select
              ref="locationSelect2"
              placeholder="选择库位"
              v-model="cellLocationName"
              :open="false"
              style="width: 100%"
              @focus="openlocationModal(row)"
            >
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
          </template> -->
        </vxe-table-column>
        <vxe-table-column field="palletName" title="存放托盘">
          <!-- <template v-slot:edit="{row}">
            <a-select
              ref="palletSelect2"
              placeholder="选择托盘"
              v-model="cellPalletName"
              :open="false"
              style="width: 100%"
              @focus="openPalletModal(row)"
            >
              <a-icon slot="suffixIcon" type="ellipsis" />
            </a-select>
          </template> -->
        </vxe-table-column>
        <vxe-table-column
          field="amount"
          title="发放数量"
          align="center"
          :edit-render="{ name: '$input', props: { type: 'number' } }"
        ></vxe-table-column>
        <vxe-table-column title="操作" width="120px" align="center" v-if="!readonly">
          <template v-slot="{ row }">
            <a-button v-if="!isMustApply" type="dashed" size="small" @click="handleDel(row)">删除</a-button>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <LocationList ref="locationModal" @ok="onSelectLocation"></LocationList>
    <PalletList ref="palletModal" @ok="onSelectPallet"></PalletList>
  </a-modal>
</template>

<script>
import LocationList from '@views/tiros/common/selectModules/LocationList'
import PalletList from '@views/tiros/common/selectModules/PalletList'

export default {
  name: 'ApplyDistributionModal',
  components: { LocationList, PalletList },
  props: {
 /*   dataList: {
      type: Array,
      default: [],
    },*/
    readonly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      loading: false,
      selectInfo: null,
      form: this.$form.createForm(this),
      chooseType: 2,
      cellLocationName: '',
      cellPalletName: '',
      locationMaxAmount: 0,
      queryParam: {
        amount: '',
        applyDetailId: '',
        id: '',
        locationName: '',
        locationWbs: '',
        sourceLocationName: '',
        materialTypeId: '',
        palletId: '',
        palletName: '',
        stockPrice: '',
        tradeNo: '',
      },
      tableData: [],
      delDataList: [],
      delRecordList: [],
      validatorRules: {
        sourceLocationName: { rules: [{ required: true, message: '请选择所属库位!' }] },
        locationName: { rules: [] },
        palletId: { rules: [{ required: true, message: '请选择托盘!' }] },
        palletName: { rules: [{ required: false, message: '请选择托盘!' }] },
        amount: { rules: [{ required: true, message: '请输入数量!' }] },
      },
      isMustApply: false
    }
  },
  updated() {
    this.$nextTick(() => {
      this.loading = true
    })
    if (!this.selectInfo) {
      this.close()
    }
  },
  computed:{
    calcAmount(){
     return this.getAmount()
    }
  },
  watch: {
    /*dataList() {
      if (this.selectInfo) {
        this.updateTableData()
      }
    },*/
  },
  methods: {
    getAmount () {
      if (!this.selectInfo) {
        return 0
      }
      let grantCound = 0
      this.tableData.forEach((e) => {
        grantCound += Number(e.amount)
      })
      return this.selectInfo.amount - grantCound
    },
    showModal(row) {
      this.selectInfo = row
      this.tableData=[]
      row.assignDetailList.forEach(e => {
        this.tableData.push(JSON.parse(JSON.stringify(e)))
      })
      // this.updateTableData()
      this.queryParam.materialTypeId = this.selectInfo.materialTypeId
      this.queryParam.applyDetailId = this.selectInfo.id
      this.queryParam.amount = this.calcAmount
      //this.isMustApply = row.useCategory == 1
      this.visible = true
      this.$nextTick(()=>{
        this.locationMaxAmount = this.calcAmount
        this.form.getFieldDecorator('amount')
        this.form.setFieldsValue({
          amount: this.calcAmount,
        })
      })
      // this.$refs.applyReady.edit(id)
    },
    /*updateTableData() {
      this.tableData = []
      this.dataList.forEach((e) => {
        if (e.applyDetailId === this.selectInfo.id && e.materialTypeId === this.selectInfo.materialTypeId) {
          this.tableData.push(e)
        }
      })
    },*/
    // 确定
    handleOk() {
      if (this.isMustApply) {
        this.close()
        return
      }
      if (this.calcAmount === 0) {
        this.$emit('ok', this.tableData)
        this.close()
        return
      }
      this.$confirm({
        content: `物料差额分配未完成，是否退出？`,
        onOk: () => {
          this.$emit('ok', this.tableData)
          this.close()
        }
      })
    },
    // 新增
    handleAdd() {
      this.form.validateFields((err, values) => {
        if (!err) {
          // 发放数量判断
          let grantCound = 0
          this.tableData.forEach((e) => {
            grantCound += Number(e.amount)
          })

          if (this.tableData.find(e => e.sourceLocationName === this.queryParam.sourceLocationName ) ) {
            this.$message.warn(`${this.queryParam.sourceLocationName}来源库位的记录已存在`)
            this.$refs.listTable.setActiveCell(this.tableData.find(e => e.sourceLocationName === this.queryParam.sourceLocationName), 'amount')
            return
          }

          if (this.locationMaxAmount === 0) {
            this.$message.warn(`${this.queryParam.sourceLocationName}来源库位的库存量不足`)
            return
          }

          if (grantCound + values.amount > Number(this.selectInfo.amount)) {
            this.$message.warn('选择的实际数量超过了发放数量')
            return
          }

          this.queryParam.amount = values.amount
          this.tableData.push(JSON.parse(JSON.stringify(this.queryParam)))
          // this.dataList.push(JSON.parse(JSON.stringify(this.queryParam)))
          // this.queryParam.amount = this.calcAmount
          // this.queryParam.locationName = undefined;
          // this.queryParam.locationWbs = undefined;
          // this.queryParam.sourceLocationName = undefined;
          // this.queryParam.palletId = ''
          // this.queryParam.palletName = ''
          // this.queryParam.stockPrice = null
          // this.form.setFieldsValue({
          //   locationName: undefined,
          //   sourceLocationName: undefined,
          //   amount: this.calcAmount,
          // })
        }
      })
    },
    // 删除
    handleDel(row) {
      /*if (row.id) {
        this.delRecordList.push(row)
      }*/
      this.tableData.splice(
        this.tableData.findIndex((e) => e === row),
        1
      )

      this.queryParam.amount = this.calcAmount

      this.$nextTick(()=>{
          this.locationMaxAmount = this.calcAmount
          this.form.getFieldDecorator('amount')
          this.form.setFieldsValue({
            amount: this.calcAmount,
          })});
    },
    // 关闭
    handleCancel() {
      this.$emit('cancel')
      this.close()
    },
    close() {
      this.visible = false
    },
    // 打开托盘Modal
    openPalletModal(row = null) {
      // this.chooseType = type
      this.curRow = row
      this.$refs.palletModal.showModal(this.selectInfo.materialTypeId, this.selectInfo.materialTypeName)
      this.$refs.palletSelect.blur()
    },
    // 打开库位选择
    openlocationModal(open) {
      if(open){
        this.$refs.locationModal.showModal(this.selectInfo.materialTypeId, null, null)
        this.$refs.locationSelect.blur()
      }
    },
    //库位选择
    onSelectLocation(records) {
      console.log(records)
      if (!this.curRow) {
        if (records.length > 0) {
          let tableAmount = 0
          this.tableData.forEach((e) => {
            if (e.locationWbs === records[0].warehouseWbs) {
              tableAmount += e.amount
            }
          })
          this.locationMaxAmount = records[0].amount - tableAmount
          if (this.locationMaxAmount < this.queryParam.amount) {
            this.form.setFieldsValue({
              amount: this.locationMaxAmount,
            })
          }
          this.queryParam.locationName = records[0].warehouseName
          this.queryParam.locationWbs = records[0].warehouseWbs
          this.queryParam.sourceLocationName = records[0].sourceLocationName
          this.queryParam.tradeNo = records[0].tradeNo
          this.form.setFieldsValue({
            locationName: records[0].warehouseName,
            sourceLocationName: records[0].sourceLocationName
          })
        }
      } else if (this.curRow) {
        this.curRow.palletName = records[0].warehouseName
        this.curRow.locationWbs = records[0].warehouseWbs
        this.curRow.sourceLocationName = records[0].sourceLocationName
        delete this.curRow
      }
    },
    palletNameChange(value){
      if(!value){
        this.queryParam.palletId = undefined;
        this.queryParam.palletName = undefined;
      }
    },
    // 托盘选择
    onSelectPallet(records) {
      if (!this.curRow) {
        console.log(1)
        if (records.length > 0) {
          this.queryParam.palletId = records[0].id
          this.queryParam.palletName = records[0].name
          this.form.setFieldsValue({
            palletName: records[0].name,
          })
        }
      } else {
        console.log(2)
        // this.curRow.palletId = records[0].id
        // this.curRow.palletName = records[0].name
        delete this.curRow
      }
    },
    onEditActive({ row }) {
      this.cellLocationName = row.locationName
      this.cellPalletName = row.palletName
    },
    onEditClosed({ row }) {
      // 发放数量判断
      let grantCound = 0
      this.tableData.forEach((e) => {
        grantCound += Number(e.amount)
      })
      if (grantCound > Number(this.selectInfo.amount)) {
        this.$refs.listTable.revertData(row)
        this.$message.warn('选择的实际数量超过了发放数量')
        return
      }
    },
  },
}
</script>