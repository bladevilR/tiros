<template>
  <!-- 物料类型选择弹窗 -->
  <a-modal
    width="97%"
    title="物料选择"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    centered
    :destroyOnClose="true"
    :bodyStyle="{ height: '80vh' }"
  >
    <div style="height: calc(80vh - 186px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(80vh - 186px)"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        highlight-current-row
        show-overflow="tooltip"
        :radio-config="!multiple ? { trigger: 'row' } : {}"
        :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
        :edit-config="{ trigger: 'click', mode: 'cell', showIcon: 'true' }"
        @cell-click="onCellClick"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="materialTypeCode" title="物料编码" width="150"></vxe-table-column>
        <vxe-table-column field="materialTypeName" title="物料名称" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="useCategory_dictText" title="类别" width="150"></vxe-table-column>
        <vxe-table-column field="materialTypeUnit" title="单位" width="150"></vxe-table-column>
        <vxe-table-column field="receive" title="领用数量" width="150"></vxe-table-column>
        <vxe-table-column
          field="num"
          title="退还数量"
          width="200"
          :edit-render="{ name: 'input'}"
        >
          <template v-slot:edit="{row}">
            <a-input-number
                v-model="row.num"
                placeholder="请输入数量"
                :min="1"
                :max="row.receive"
                style="width: 100%"
              />
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
  </a-modal>
</template>

<script>
import JTreeSelect from '@comp/jeecg/JTreeSelect'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { getMaterialApplyList } from '@api/tirosGroupApi'

export default {
  name: 'ReturnMaterialModal',
  components: { JTreeSelect, LineSelectList },
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    /*const numValid = ({ cellValue, row }) => {
      return new Promise((resolve, reject) => {
        if (cellValue > row.amount) {
          reject(new Error('数量已超出当前库存量'))
        } else if (cellValue < 1) {
          reject(new Error('数量不能小于1'))
        } else {
          resolve()
        }
      })
    }*/
    return {
      hiddenType: [],
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" + this.$store.getters.userInfo.workshopId + "'|workshop_id",
      dictCodeStrWork:'bu_mtr_workstation,name,id',
      queryParam: {
        type: '1',
        category1: '',
        searchText: '',
        attr: null,
        warehouseId: '',
        materialTypeCodeList: [],
        pageNo: 1,
        pageSize: 10,
      },
      allAlign: 'center',
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
      /*validRules: {
        num: [{ validator: numValid }],
      },*/
      formItemLayout: {
        labelCol: {
          xs: { span: 24 },
          sm: { span: 6 },
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 18 },
        },
      },
    }
  },
  created(){

  },
  methods: {
    showModal(orderId) {
      this.loading = true
      this.tableData = []
      getMaterialApplyList({
        orderId: orderId
      }).then(res =>{
        if (res.success) {
          res.result.forEach(apply =>{
            apply.detailList.map(e => {
              e.num = 1
              return e
            })
            this.tableData = [...this.tableData, ...apply.detailList]
          })
          this.loading = false
          this.visible = true
        }else{
          this.$message.error('读取领用明细失败')
        }
      })
    },
    handleOk() {
      let selectRecords = []
      selectRecords = this.$refs.listTable.getCheckboxRecords()
      this.$emit('ok', selectRecords)
      this.visible = false
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
      this.tabIndex = '1'
    },
    // fix 点击数量编辑时取消选择问题
    onCellClick({row, column}){
      if (column.property === 'num') {
        this.$refs.listTable.setCheckboxRow([row], true)
      }
    }
  },
}
</script>
<style scoped>
.limitTitle {
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 8px;
  border-bottom: 1px solid #e1e1de;
}
.limitHeight {
  height: calc(80vh - 110px);
  overflow-y: auto;
}
</style>