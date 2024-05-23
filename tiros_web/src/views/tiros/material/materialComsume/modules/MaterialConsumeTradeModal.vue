<template>
  <div>
    <a-modal
      title='批次明细'
      :width="'70%'"
      :visible='visible'
      :confirmLoading='confirmLoading'
      @ok='handleOk'
      @cancel='cancel'
    >
      <div class='fieldset-wrapper'>
        <h4 class='title'>实际消耗</h4>
        <div style='padding-bottom: 10px'>
          <a-space>
            <a-button @click='$refs.modalForm.showModal()'>添加</a-button>
            <a-button @click='deleteEvent'>删除</a-button>
          </a-space>
        </div>
        <div style='height: calc(100vh - 600px)'>
          <vxe-table
            key='outTable'
            border
            height='auto'
            ref='outTable'
            align='center'
            :data='form.actList'
            @checkbox-all='selectChangeEvent'
            @checkbox-change='selectChangeEvent'
            show-overflow='tooltip'
            :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
          >
            <vxe-table-column type='checkbox' width='40'></vxe-table-column>
            <vxe-table-column field='tradeNo' title='批次号'></vxe-table-column>
            <vxe-table-column field='price' title='价格'></vxe-table-column>
            <vxe-table-column field='actAmount' title='数量'>
              <template v-slot='{ row }'>
                <a-input-number v-model='row.actAmount' :min='0' :max='row.usableAmount' :precision='0' />
              </template>
            </vxe-table-column>
          </vxe-table>
        </div>
      </div>
    </a-modal>
    <material-list
      ref='modalForm'
      :lineId='form.lineId'
      :multiple='false'
      :mode='[3]'
      :materialTypeCode='form.materialTypeCode'
      :group-id='form.groupId'
      @ok='onSelectMaterial'
    ></material-list>
  </div>
</template>

<script>
import { randomUUID } from '@/utils/util.js'
import MaterialList from '@views/tiros/common/selectModules/MaterialList'

export default {
  name: 'MaterialConsumeTradeModal',
  components: { MaterialList },
  data() {
    return {
      visible: false,
      confirmLoading: false,
      form: {
        workstationId: null,
        useCategory: null,
        systemId: null,
        actList: []
      },
      selecteList: [],
      labelCol: { span: 6 },
      wrapperCol: { span: 18 },
      isEdit: false,
      validRules: {
        actAmount: [{ required: true, message: '请输入消耗数量' }]
      },
      copy: {}
    }
  },
  methods: {
    show(data) {
      console.log(data)

      this.form = { ...this.form, ...data }
      this.selecteList = []

      this.visible = true
    },
    selectChangeEvent({ records }) {
      this.selecteList = JSON.parse(JSON.stringify(records))
    },
    deleteEvent() {
      this.selecteList.forEach((item, index, arr) => {
        for (let i = 0, len = this.form.actList.length; i < len; i++) {
          if (item.id === this.form.actList[i].id) {
            this.form.actList.splice(i, 1)
            break
          }
        }
      })
    },
    onSelectMaterial(data) {
      console.log(JSON.stringify(data))
      if (data.length) {
        const list = this.form.actList.filter((item) => {
          return item.tradeNo === data[0].tradeNo //条件;
        })
        console.log(list)
        if (list.length) {
          return this.$message.warning('添加的批次已经存在列表中')
        }
        this.form.actList.push({
          id: randomUUID(),
          groupStockId: data[0].groupStockId,
          applyId: data[0].applyId,
          applyIdDetailId: data[0].applyIdDetailId,
          assignDetailId: data[0].assignDetailId,
          tradeNo: data[0].tradeNo,
          actAmount: '',
          usableAmount: data[0].usableAmount,
          price: data[0].price
        })
      }
    },
    handleOk() {
      if (!this.isEdit) {
        this.$emit('ok', this.form.actList)
        this.visible = false
      } else {
        this.$message.warning('请先保存或者取消正在编辑的物资核实')
      }

    },
    cancel() {
      this.visible = false
      this.isEdit = false
      this.$emit('cancel', this.copy)
    }
  }
}
</script>

<style lang='less' scoped>
.modelItemWarp {
  width: 100%;
}
</style>
