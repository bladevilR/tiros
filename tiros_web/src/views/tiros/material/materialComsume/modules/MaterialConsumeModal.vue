<template>
  <div>
    <a-modal
      title='核实'
      width='100%'
      dialogClass='fullDialog'
      :visible='visible'
      :confirmLoading='confirmLoading'
      @cancel='handleCancel'
      @ok='handleOk'
      :destroyOnClose='true'
    >
      <div class='fieldset-wrapper'>
        <h4 class='title'>物料详情</h4>
        <a-form-model
          layout='inline'
          :model='form'
          :label-col='labelCol'
          :wrapper-col='wrapperCol'
        >
          <a-row>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='线路'>
                <a-input :value='form.lineName' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='车辆'>
                <a-input :value='form.trainNo' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='列计划'>
                <a-input :value='form.planName' disabled />
              </a-form-model-item>
            </a-col>
            <!--            <a-col :span="8">-->
            <!--              <a-form-model-item class="modelItemWarp" label="工单号">-->
            <!--                <a-input :value="form.orderCode" disabled />-->
            <!--              </a-form-model-item>-->
            <!--            </a-col>-->
            <!--            <a-col :span="8">-->
            <!--              <a-form-model-item class="modelItemWarp" label="工单名称">-->
            <!--                <a-input :value="form.orderName" disabled />-->
            <!--              </a-form-model-item>-->
            <!--            </a-col>-->
            <!--            <a-col :span="8">-->
            <!--              <a-form-model-item class="modelItemWarp" label="工单类型">-->
            <!--                <a-input :value="form.orderType_dictText" disabled />-->
            <!--              </a-form-model-item>-->
            <!--            </a-col>-->
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='物资编码'>
                <a-input :value='form.materialCode' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='物资名称'>
                <a-input :value='form.materialName' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='物资描述'>
                <a-input :value='form.materialSpec' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='工班'>
                <a-input :value='form.groupName' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='总额定数量'>
                <a-input :value='form.needAmount' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='总实际消耗'>
                <a-input :value='form.consumeAmount' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='是否必换件清单'>
                <a-input :value='form.isMustList_dictText' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='额定系统'>
                <a-input :value='form.requireSystemName' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='额定工位'>
                <a-input :value='form.requireWorkstationName' disabled />
              </a-form-model-item>
            </a-col>
            <a-col :span='8'>
              <a-form-model-item class='modelItemWarp' label='可替换物资'>
                <a-input :value='form.canReplace' disabled />
              </a-form-model-item>
            </a-col>

            <!--            <a-col :span='8'>-->
            <!--              <a-form-model-item class='modelItemWarp' label='类别'>-->
            <!--                <j-dict-select-tag-->
            <!--                  v-model='form.useCategory'-->
            <!--                  placeholder='请选择'-->
            <!--                  dictCode='bu_material_type'-->
            <!--                />-->
            <!--              </a-form-model-item>-->
            <!--            </a-col>-->
            <!--            <a-col :span='8'>-->
            <!--              <a-form-model-item class='modelItemWarp' label='系统'>-->
            <!--                <j-dict-select-tag-->
            <!--                  v-model='form.systemId'-->
            <!--                  placeholder='请选择'-->
            <!--                  dictCode='bu_train_asset_type,name,id,struct_type=1 and parent_id is null'-->
            <!--                />-->
            <!--              </a-form-model-item>-->
            <!--            </a-col>-->
            <!--            <a-col :span='8'>-->
            <!--              <a-form-model-item class='modelItemWarp' label='工位'>-->
            <!--                <j-dict-select-tag-->
            <!--                  v-model='form.workstationId'-->
            <!--                  placeholder='请选择工位'-->
            <!--                  dictCode='bu_mtr_workstation,name,id'-->
            <!--                />-->
            <!--              </a-form-model-item>-->
            <!--            </a-col>-->

          </a-row>
        </a-form-model>
      </div>
      <div class='fieldset-wrapper'>
        <h4 class='title'>发料详情</h4>
        <div style='height: calc(100vh - 800px)'>
          <vxe-table
            border
            resizable
            show-overflow
            keep-source
            ref='applyDetailTable'
            align='center'
            height='auto'
            :cell-style='cellStyle'
            :data='form.applyDetailList'
            :edit-rules='validRules'
            :edit-config="{ trigger: 'manual', mode: 'row',autoClear:false }"
          >
            <vxe-table-column type='seq' width='60'></vxe-table-column>
            <vxe-table-column field='orderCode' width='120' title='工单编码'></vxe-table-column>
            <vxe-table-column field='orderName' width='400' title='工单名称'></vxe-table-column>
            <vxe-table-column field='orderType_dictText' width='100' title='工单类型'></vxe-table-column>
            <vxe-table-column field='materialTypeId' width='120' title='发放物料'></vxe-table-column>
            <vxe-table-column field='receive' width='120' title='发放数量'></vxe-table-column>
            <vxe-table-column field='useCategory_dictText' width='120' title='类别'></vxe-table-column>
          </vxe-table>
        </div>
      </div>
      <div class='fieldset-wrapper'>
        <h4 class='title'>工单消耗</h4>
        <div style='padding-bottom: 10px'>
          <a-space>
            <a-button @click='$refs.workOrderSelect.showModal()' v-has="'material:consume:addorder'">添加工单消耗</a-button>
          </a-space>
        </div>
        <div style='height: calc(100vh - 600px)'>
          <vxe-table
            border
            resizable
            show-overflow
            keep-source
            ref='orderMaterialTable'
            align='center'
            height='auto'
            :cell-style='cellStyle'
            :data='form.orderMaterialList'
            :edit-rules='validRules'
            :edit-config="{ trigger: 'manual', mode: 'row',autoClear:false }"
          >
            <vxe-table-column type='seq' width='60'></vxe-table-column>
            <vxe-table-column field='orderCode' width='120' title='工单编码'></vxe-table-column>
            <vxe-table-column field='orderName' width='400' title='工单名称'></vxe-table-column>
            <vxe-table-column field='orderType_dictText' width='100' title='工单类型'></vxe-table-column>
            <vxe-table-column field='materialTypeId' width='120' title='消耗物料'></vxe-table-column>
            <vxe-table-column field='amount' width='120' title='需求数量'></vxe-table-column>
            <vxe-table-column
              field='actAmount'
              width='120'
              title='实际消耗'
              :edit-render="{ name: '$input', props: { type: 'number',disabled:true } }"
            ></vxe-table-column>
            <vxe-table-column field='remark' width='120' title='消耗备注'></vxe-table-column>

            <vxe-table-column field='useCategory' width='160' title='类别'>
              <template v-slot='{ row }'>
                <j-dict-select-tag
                  v-model='row.useCategory'
                  placeholder='请选择'
                  dictCode='bu_material_type'
                />
              </template>
            </vxe-table-column>
            <vxe-table-column field='systemName' width='260' title='系统'>
              <template v-slot='{ row }'>
                <j-dict-select-tag
                  v-model='row.systemId'
                  placeholder='请选择'
                  dictCode='bu_train_asset_type,name,id,struct_type=1 and parent_id is null'
                />
              </template>
            </vxe-table-column>
            <vxe-table-column field='workstationName' width='260' title='工位'>
              <template v-slot='{ row }'>
                <j-dict-select-tag
                  v-model='row.workstationId'
                  placeholder='请选择工位'
                  dictCode='bu_mtr_workstation,name,id'
                />
              </template>
            </vxe-table-column>

            <vxe-table-column field='isVerify_dictText' width='100' title='是否核实'></vxe-table-column>
            <vxe-table-column title='操作' align='center' min-width='160'>
              <template v-slot='{ row,rowIndex }'>
                <template v-if='$refs.orderMaterialTable.isActiveByRow(row)'>
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
      </div>
    </a-modal>
    <MaterialConsumeTradeModal @cancel='cancelRowEvent' @ok='verifyTradeNoEvent' ref='materialConsumeTradeModal' />
    <WorkOrderSelect ref='workOrderSelect' @ok='onSelectOrder' :multiple='false' :orderStatus='[4]' :selectStatus='[4]'
                     :planId='planId' :workGroupId='groupId' :orderType='1' />
  </div>
</template>

<script>
import MaterialConsumeTradeModal from '@/views/tiros/material/materialComsume/modules/MaterialConsumeTradeModal'
import WorkOrderSelect from '@views/tiros/common/selectModules/WorkOrderSelect'
import { verifyOrderMaterialList } from '@api/tirosMaterialApi'

export default {
  name: 'materialConsumeModal',
  components: { MaterialConsumeTradeModal, WorkOrderSelect },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      isEdit: false,
      form: {},
      planId: '',
      groupId: '',
      labelCol: { span: 6 },
      wrapperCol: { span: 18 },
      validRules: {
        actAmount: [
          { required: true, message: '请输入消耗数量' }
        ]
      }
    }
  },
  methods: {
    show(data) {
      this.form = { ...this.form, ...data }
      this.planId = this.form.planId
      this.groupId = this.form.groupId

      this.visible = true
    },
    cellStyle({ row, rowIndex, column, columnIndex }) {
      if (['isVerify_dictText'].indexOf(column.property) > -1) {
        if (row.isVerify === 0) {
          return {
            backgroundColor: 'red',
            color: '#fff'
          }
        }
      }
    },
    saveRowEvent(row, rowIndex) {
      this.$message.info('点击底部“确定”按钮提交核实')
      this.$refs.orderMaterialTable.clearActived()
      this.isEdit = false

      // this.$refs.orderMaterialTable.validate(row).then(() => {
      //   this.saveVerifyOrderMaterialList(1, row)
      // })
    },
    saveVerifyOrderMaterialList(type, data) {
      if (type === 1) {
        // 行保存
        verifyOrderMaterialList([data]).then((res) => {
          if (res.success) {
            this.$message.success('操作成功')
            data.isVerify = 1
            data.isVerify_dictText = '是'
            this.$refs.orderMaterialTable.clearActived()
            this.isEdit = false
          } else {
            this.cancelRowEvent(data)
            this.isEdit = false
            this.$message.warning(res.message)
          }
          this.confirmLoading = false
        })
      } else if (type === 2) {
        // 全部保存
        verifyOrderMaterialList(data).then((res) => {
          if (res.success) {
            this.$message.success('操作成功')
            this.$refs.orderMaterialTable.clearActived()
            this.isEdit = false
          } else {
            this.isEdit = false
            this.$message.warning(res.message)
          }
          this.confirmLoading = false
        })
      }
    },
    cancelRowEvent(row) {
      const orderMaterialTable = this.$refs.orderMaterialTable
      orderMaterialTable.clearActived().then(() => {
        // 还原行数据
        orderMaterialTable.revertData(row)
        this.isEdit = false
      })
    },
    editRowEvent(row) {
      if (!this.isEdit) {
        this.$refs.orderMaterialTable.setActiveRow(row)
        this.isEdit = true
        this.$refs.materialConsumeTradeModal.show(row)
      } else {
        this.$message.warning('请先保存或者取消正在编辑的物资核实')
      }
    },
    verifyTradeNoEvent(data) {

      this.$refs.orderMaterialTable.getActiveRecord().row.actList = data || []
      let num = 0
      for (let i = 0, len = data.length; i < len; i++) {
        num += Number(data[i].actAmount)
      }
      this.$refs.orderMaterialTable.getActiveRecord().row.actAmount = num
    },
    onSelectOrder(data) {
      if (data.length) {
        const list = this.form.orderMaterialList.filter((item) => {
          return item.orderId === data[0].id //条件;
        })

        if (list.length) {
          return this.$message.warning('添加的工单已经存在列表中')
        }
        this.form.orderMaterialList.push({
          id: null,
          orderId: data[0].id,
          orderCode: data[0].orderCode,
          orderName: data[0].orderName,
          orderType: data[0].orderType,
          orderType_dictText: data[0].orderType_dictText,
          groupId: data[0].groupId,
          groupName: data[0].groupName,
          materialTypeId: this.form.materialId,
          materialTypeCode: this.form.materialCode,
          materialTypeName: this.form.materialName,
          amount: 0,
          actAmount: 0,
          planAmount: 0,
          remark: '',
          needDispatchin: 0,
          useCategory: this.form.useCategory,
          isVerify: 0,
          isVerify_dictText: '否',
          opType: 2,
          isGenOrder: 0,
          systemId: null,
          workstationId: null,
          actList: []
        })
      }
    },

    handleOk() {
      this.saveVerifyOrderMaterialList(2, this.form.orderMaterialList)

      this.$emit('ok')
      this.visible = false

      Object.assign(this.$data, this.$options.data())
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false

      Object.assign(this.$data, this.$options.data())
    }
  }
}
</script>

<style lang='less' scoped>
.modelItemWarp {
  width: 100%;
}
</style>
