<template>
  <a-modal :title='`选择库存记录--所需数量${tableRow.amount || 0 }`' :visible='visible' width='90%' @cancel='handleCancel'>
    <!-- 表格 -->
    <div class='vxeTableBox'>
      <vxe-table
        border
        align='center'
        ref='listTable'
        height='auto'
        :data='tableData'
        show-overflow='tooltip'
        :checkbox-config="{
          trigger: 'row',
          highlight: true,
          range: true,
        }"
      >
        <vxe-table-column type='seq' width='40'></vxe-table-column>
        <!-- <vxe-table-column type="checkbox" width="40"></vxe-table-column> -->
        <vxe-table-column
          field='materialTypeCode'
          title='物资编码'
          width='140'
          header-align='center'
          align='center'
        ></vxe-table-column>
        <vxe-table-column
          field='materialTypeName'
          title='物资名称'
          width='150'
          header-align='center'
          align='left'
        ></vxe-table-column>
        <vxe-table-column
          field='spec'
          title='物资描述'
          min-width='150'
          header-align='center'
          align='left'
        ></vxe-table-column>
        <vxe-table-column field='trainNo' title='库存车号' width='80'></vxe-table-column>
        <vxe-table-column field='workOrderTrainNo' title='工单车号' width='80'></vxe-table-column>
        <vxe-table-column field='trainNoIsSame_dictText' title='车号一致？' width='120'>
          <template v-slot='{ row }'>
            <div :style="{ color: trainNoIsSameColor[row.trainNoIsSame + ''], borderRadius: '4px' }">
              {{ row.trainNoIsSame_dictText }}
            </div>
          </template>
        </vxe-table-column>
        <!-- <vxe-table-column
          field="groupName"
          title="所属班组"
          width="120"
          header-align="center"
          align="left"
        ></vxe-table-column> -->
        <vxe-table-column
          field='tradeNo'
          title='批次号'
          width='160'
          header-align='center'
          align='left'
        ></vxe-table-column>
        <vxe-table-column
          field='amount'
          title='库存数量'
          width='100'
          header-align='center'
          align='right'
        ></vxe-table-column>
        <vxe-table-column
          field='usableAmount'
          title='当前可用量'
          width='100'
          header-align='center'
          align='right'
        ></vxe-table-column>
        <vxe-table-column
          field='usedDetailInfo'
          title='占用详情'
          width='180'
          header-align='center'
          align='left'
        ></vxe-table-column>
        <vxe-table-column
          field='materialType_dictText'
          title='类别'
          width='80'
          header-align='center'
          align='left'
        ></vxe-table-column>
        <!-- <vxe-table-column
          field="materialAttr_dictText"
          title="属性"
          width="80"
          header-align="center"
          align="left"
        ></vxe-table-column> -->
        <vxe-table-column
          field='price'
          title='价格'
          width='80'
          header-align='center'
          align='right'
        ></vxe-table-column>
        <vxe-table-column title='消耗数量' fixed='right' width='120'>
          <template v-slot='{ row }'>
            <div @click.stop>
              <a-input-number
                :min='0'
                :max='parseFloat(row.usableAmount)'
                v-model='row.actAmount'
              />
            </div>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <!--  -->
    <template slot='footer'>
      <div class='flexBox'>
        <div class='redText'>
          <a-form-model ref='ruleForm' :model='form' :rules='rules' layout='inline'>
            <!-- <a-form-model-item label="实际消耗数量" prop="actAmount">
              <a-input-number
                style="width: 150px"
                v-model="form.actAmount"
                placeholder="请输入实际消耗数量"
              />
            </a-form-model-item> -->
            <a-form-model-item label='消耗备注' prop='remark'>
              <a-input
                style='width: 300px'
                v-model='form.remark'
                placeholder='请输入消耗备注,不超过255个字符'
              />
            </a-form-model-item>
          </a-form-model>
        </div>
        <div>
          <a-button @click='consumeEvent' v-if='tableData.length !== 1'>消耗为0</a-button>
          <a-button @click='handleCancel'> 取消</a-button>
          <a-button type='primary' :loading='confirmLoading' @click='handleOk'>
            确定
          </a-button>
        </div>
      </div>
    </template>
  </a-modal>
</template>

<script>
import { listGroupStockForTaskWrite } from '@/api/tirosMaterialApi'
import { actMaterial } from '@/api/tirosGroupApi'

export default {
  data() {
    return {
      visible: false,
      confirmLoading: false,
      tableData: [],
      tableRow: {},
      needAmount: 0,
      form: {
        actAmount: 0,
        remark: ''
      },
      trainNoIsSameColor: {
        0: '#f5222d',
        1: '#40a9ff'
      },
      rules: {
        // actAmount: [{ required: true, message: "请输入消耗数量!", trigger: "change" }],
        remark: [
          {
            max: 255,
            message: '输入长度不能超过255字符!',
            trigger: 'change'
          }
        ]
      }
    }
  },
  methods: {
    handleOk() {
      if (!this.tableData.length) {
        this.$message.warning('请填写物资消耗数量')
        return
      }
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          if (!this.tableData.filter((item) => typeof item.actAmount !== 'string').length) {
            this.$message.warning('请填写物资消耗数量')
            return
          }
          this.submit()
          // let trainNoIsNotSameLength = this.tableData.filter((item) => typeof item.actAmount !== 'string' && item.actAmount > 0 && 0 === item.trainNoIsSame).length
          // console.log('trainNoIsNotSameLength = ', trainNoIsNotSameLength)
          // if (trainNoIsNotSameLength) {
          //   this.$confirm({
          //     content: `有数据库存车号和工单车号不一致，是否提交？`,
          //     onOk: () => {
          //       this.submit()
          //     }
          //   })
          // } else {
          //   this.submit()
          // }
        }
      })
    },
    submit() {
      this.tableRow.remark = this.form.remark
      let num = 0
      const records = this.tableData.filter((item) => {
        num += item.actAmount
        return item.actAmount > 0
      })
      num = Number(num)
      this.form.actAmount = num
      console.log(this.tableRow.useCategory, this.form.actAmount, this.tableRow.amount)
      if (this.tableRow.useCategory === 1 && this.form.actAmount > this.tableRow.amount) {
        this.$message.warning('必换件实际消耗不能大于额定数量')
        return
      }
      this.tableRow.actAmount = this.form.actAmount
      console.log(records)
      const materialActsList = records.map((item) => {
        return {
          orderMaterialId: this.tableRow.id,
          groupStockId: item.id,
          applyDetailId: item.applyDetailId,
          applyId: item.applyId,
          actAmount: item.actAmount,
          assignDetailId: item.assignDetailId,
          tradeNo: item.tradeNo,
          price: item.price
        } //条件;
      })

      this.tableRow.materialActsList = materialActsList
      console.log(this.tableRow)
      //
      if (this.form.actAmount < this.tableRow.amount) {
        this.$confirm({
          content: '你的实际消耗数量小于需求数量，确认实际消耗数量为' + this.form.actAmount,
          onOk: () => {
            return this.actMaterial()
          }
        })
      } else {
        this.actMaterial()
      }
    },
    consumeEvent() {
      this.tableRow.remark = this.form.remark
      this.tableRow.actAmount = 0
      this.$confirm({
        content: '确定设置该物资实际消耗为0？',
        onOk: () => {
          const materialActsList = this.tableData.map((item) => {
            return {
              orderMaterialId: this.tableRow.id,
              groupStockId: item.id,
              applyDetailId: item.applyDetailId,
              applyId: item.applyId,
              actAmount: 0,
              assignDetailId: item.assignDetailId,
              tradeNo: item.tradeNo,
              price: item.price
            } //条件;
          })
          this.tableRow.materialActsList = materialActsList
          console.log(this.tableRow)
          // return
          return this.actMaterial()
        }
      })
    },
    actMaterial(resolve, reject) {
      return actMaterial(this.tableRow).then((res) => {
        if (res.success) {
          this.$message.success('操作成功')
          this.$emit('ok', this.tableRow)
          this.handleCancel()
        } else {
          this.$message.warning(res.message)
        }
      })
    },
    // confirm(title) {
    //   this.$confirm({
    //     title: title,
    //     onOk: () => {
    //       return this.submit();
    //     },
    //     onCancel() {},
    //   });
    // },
    showModal(row) {
      console.log(row)
      this.tableRow = JSON.parse(JSON.stringify(row))
      if (row.amount) {
        this.needAmount = row.amount
      }
      if (row.remark) {
        this.form.remark = row.remark
      }
      this.visible = true
      listGroupStockForTaskWrite({
        orderMaterialId: row.id,
        materialTypeId: row.materialTypeId,
        groupId: this.$store.getters.userInfo.departId
      }).then((res) => {
        let tableData = res.result
        const materialActsList = this.tableRow.materialActsList
        Array.from(tableData, (item) => {
          item.actAmount = ''
          if (materialActsList && materialActsList.length) {
            const _data = materialActsList.filter((item1) => item1.groupStockId == item.id)
            item.actAmount = _data.length > 0 ? _data[0].actAmount : 0
          }
        })
        this.tableData = tableData
      })
    },
    handleCancel() {
      this.visible = false
      Object.assign(this.$data, this.$options.data())
    }
  }
}
</script>

<style lang='less' scoped>
::v-deep {
  .redText {
    .ant-form-item-label label {
      color: red;
    }
  }
}

.vxeTableBox {
  height: 400px;
}

.flexBox {
  display: flex;
  flex-flow: row nowrap;
  justify-content: space-between;
}
</style>
