<template>
  <div>
    <a-modal
      title='核实确认'
      width='100%'
      :visible='visible'
      dialogClass='fullDialog'
      @cancel='handleCancel'
    >
      <div class='listBox'>
        <vxe-table
          border
          resizable
          show-overflow
          keep-source
          ref='xTable'
          height='auto'
          :cell-style='cellStyle'
          :data='tableData'
          :edit-rules='validRules'
          :edit-config="{ trigger: 'manual', mode: 'row',autoClear:false }"
        >
          <vxe-table-column type='seq' width='60'></vxe-table-column>
          <vxe-table-column field='materialTypeCode' title='物资编码'></vxe-table-column>
          <vxe-table-column field='materialTypeName' title='物资名称'></vxe-table-column>
          <vxe-table-column field='amount' title='需求数量'></vxe-table-column>
          <vxe-table-column
            field='actAmount'
            title='实际消耗'
            :edit-render="{ name: '$input', props: { type: 'number',disabled:true } }"
          ></vxe-table-column>
          <vxe-table-column field='isVerify_dictText' title='是否核实'></vxe-table-column>
          <vxe-table-column field='orderCode' title='工单编码'></vxe-table-column>
          <vxe-table-column field='orderName' title='工单名称'></vxe-table-column>
          <vxe-table-column field='orderTime' title='工单日期'></vxe-table-column>
          <vxe-table-column title='操作' align='center' width='160' v-if='!viewDetail'>
            <template v-slot='{ row,rowIndex }'>
              <template v-if='$refs.xTable.isActiveByRow(row)'>
                <a-space>
                  <a-button @click='saveRowEvent(row,rowIndex)' v-has="'material:consume'">保存</a-button>
                  <a-button @click='cancelRowEvent(row)' v-has="'material:consume'">取消</a-button>
                </a-space>
              </template>
              <template v-else>
                <a-button @click='editRowEvent(row)' v-has="'material:consume'">编辑</a-button>
              </template>
            </template>
          </vxe-table-column>
        </vxe-table>
      </div>
      <template slot='footer'>
        <a-button @click='handleCancel'> 关闭</a-button>
      </template>
    </a-modal>
    <VerifyTradeNos @cancel='cancelRowEvent' @ok='verifyTradeNoEvent' ref='VerifyTradeNos' />
  </div>
</template>

<script>
import VerifyTradeNos from '@/views/tiros/material/materialComsume/modules/VerifyTradeNos.vue'
import { getMaterialCostCompareDetail, verifyOrderMaterialList } from '@api/tirosMaterialApi'

export default {
  components: { VerifyTradeNos },
  data() {
    return {
      validRules: {
        actAmount: [
          { required: true, message: '请输入消耗数量' }
        ]
      },
      viewDetail: false,
      // orderMaterialId: "",
      // confirmLoading: false,
      parentBrush: false,
      isEdit: false,
      visible: false,
      loading: false,
      tableData: []
      // form: this.$form.createForm(this, { name: "check_verify" }),
    }
  },
  methods: {
    // 确定
    handleOk() {
      // this.form.validateFields((err, values) => {
      //   if (!err) {
      //     console.log("Received values of form: ", values);
      //     this.confirmLoading = true;

      //   }
      // });
    },
    cellStyle({ row, rowIndex, column, columnIndex }) {
      if (['isVerify_dictText'].indexOf(column.property) > -1) {
        if (row.isVerify == 0) {
          return {
            backgroundColor: 'red',
            color: '#fff'
          }
        }
      }
    },
    verifyTradeNoEvent(data) {
      console.log(this.$refs.xTable.getActiveRecord())
      this.$refs.xTable.getActiveRecord().row.actList = data || []
      let num = 0
      for (let i = 0, len = data.length; i < len; i++) {
        num += Number(data[i].actAmount)
      }
      this.$refs.xTable.getActiveRecord().row.actAmount = num
    },
    editRowEvent(row) {
      if (!this.isEdit) {
        this.$refs.xTable.setActiveRow(row)
        this.isEdit = true
        this.$refs.VerifyTradeNos.show(row)
        // console.log(this.$refs.xTable.isActiveByRow(row))
      } else {
        this.$message.warning('请先保存或者取消正在编辑的物资核实')
      }
    },
    saveRowEvent(row, rowIndex) {
      console.log(this.$refs.xTable.getActiveRecord())
      this.$refs.xTable.validate(row).then(() => {
        verifyOrderMaterialList([row]).then((res) => {
          if (res.success) {
            this.$message.success('操作成功')
            row.isVerify = 1
            row.isVerify_dictText = '是'
            this.$refs.xTable.clearActived()
            this.isEdit = false
            this.parentBrush = true
          } else {
            this.cancelRowEvent(row)
            this.isEdit = false
            this.$message.warning(res.message)
          }
          this.confirmLoading = false
        })
        // .catch((err) => {
        //   this.isEdit = false;
        //   this.cancelRowEvent(row)
        //   this.confirmLoading = false;
        // });
      })

    },
    cancelRowEvent(row) {
      const xTable = this.$refs.xTable
      xTable.clearActived().then(() => {
        // 还原行数据
        xTable.revertData(row)
        this.isEdit = false
      })
    },
    show(row, view = false) {
      this.viewDetail = view
      this.visible = true
      getMaterialCostCompareDetail({
        orderMaterialIds: row.orderMaterialIds
      }).then((res) => {
        console.log(res)
        if (res.success) {
          this.tableData = res.result
        }
      })
      // this.$nextTick(() => {
      //   (this.orderMaterialId = data.orderMaterialId),
      //     this.form.setFieldsValue({
      //       needAmount: data.needAmount,
      //       receiveAmount: data.receiveAmount,
      //       consumeAmount: data.consumeAmount,
      //     });
      //   this.$refs.consumeAmount.focus();
      // });
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      if (this.parentBrush) {
        this.$emit('ok')
      }
      this.visible = false
      Object.assign(this.$data, this.$options.data())
    }
  }
}
</script>

<style lang='less' scoped>
.listBox {
  height: calc(100vh - 120px);
}
</style>
