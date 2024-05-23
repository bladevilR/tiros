<template>
  <div>
    <a-row :gutter="24" v-if="!fromFlow">
      <a-col :md="12" :sm="24">
        <span style="float: left; overflow: hidden; margin-bottom: 8px" class="table-page-search-submitButtons">
          <a-button type="primary" @click="save" style="margin-left: 8px">确认领用</a-button>
          <a-button style="margin-left: 8px" @click="close">关闭</a-button>
        </span>
      </a-col>
    </a-row>
    <div style="height: calc(70vh - 45px)">
      <vxe-table
        border
        max-height="100%"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :edit-config="{ trigger: 'click', mode: 'row' }"
        :checkbox-config="{highlight: true, range: true }"
      >
        <vxe-table-column type="seq" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column field="materialTypeCode" title="物资编码" width="150"></vxe-table-column>
        <vxe-table-column field="materialTypeName" title="物资名称" min-width="180" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="materialTypeSpec" title="规格型号" min-width="180" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="materialTypeUnit" title="单位" width="100"></vxe-table-column>
        <vxe-table-column field="useCategory_dictText" title="类别" width="100"></vxe-table-column>
<!--        <vxe-table-column field="warehouseName" title="库位" width="120"></vxe-table-column>
        <vxe-table-column field="palletName" title="存放托盘" width="150"></vxe-table-column>-->
        <vxe-table-column field="amount" title="申请数量" width="100"></vxe-table-column>
        <vxe-table-column field="receive" title="发料数量" width="100"></vxe-table-column>
        <vxe-table-column field="sourceLocationAndPalletName" title="发放信息" width="380" sort-type="string" sortable></vxe-table-column>
<!--        <vxe-table-column title="发料明细" width="180">
          <template v-slot="{ row }">
            <a @click="openDistribution(row)">发放管理</a>
          </template>
        </vxe-table-column>-->
        <vxe-table-column field="status_dictText" title="确认状态" width="100"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" width="150" header-align="center" align="left"></vxe-table-column>
      </vxe-table>
    </div>
    <ApplyDistributionModal
      ref="distributionModal"
      :dataList="distributionTableList"
      @ok="onSuccess"
    ></ApplyDistributionModal>
  </div>
</template>

<script>
import {
  applyReceiveConfirm,
  getDetailListByOrderId,
  getApplyAssign,
  getApplyMustDetaial,
  getApplyMustAssign
} from '@api/tirosMaterialApi'
import ApplyDistributionModal from '@views/tiros/material/apply/ApplyDistributionModal'
import materialUtil from '@views/tiros/util/MaterialUtil'

export default {
  name: 'ApplyConfirmComponent',
  components: { ApplyDistributionModal },
  props: {
    businessKey: {
      type: String,
      default: null,
    },
    isReadonly: {
      type: Boolean,
      default: true,
    },
    fromFlow: {
      type: Boolean,
      default: false,
    },
    orderType: {
      type: Number,
      default: null
    }
  },
  data() {
    return {
      tableData: [],
      palletList: [],
      distributionTableList: [],
      model: {},
      allAlign: 'center',
      dictCode: 'bu_material_pallet,name,id,status=1',
      queryParam: {
        orderId: '',
        status: [1]
      },
    }
  },
  mounted() {
    if (this.businessKey) {
      this.edit(this.businessKey)
    }
  },
  methods: {
    edit(id) {
      this.queryParam.orderId = id
      this.findList()
    },
    findList() {
      this.tableData = []
      getDetailListByOrderId(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = [...this.tableData, ...res.result]
          // 拼接分配明细
          materialUtil.joinDistributionInfo(this.tableData)

          // 如果是非发料工单， 获取这张工单上的必换件
          /* 这暂时不显示计划工单挂的必换件，都显示 if (this.orderType !== 4) {
            getApplyMustDetaial(this.queryParam).then(res =>{
              if (res.success) {
                let mustList = res.result

                mustList.forEach(item=>{
                  item.noSave=true
                })

                this.tableData = [...this.tableData, ...mustList]
              } else {
                this.$message.error(res.message)
              }
            })
          }*/
        } else {
          this.$message.error('加载数据失败')
          console.error('加载数据失败：', res.message)
        }
        this.$emit('loaded')
      })
    },
    // 确定
    save(onlySave) {
     /* if (!this.fromFlow) {
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        if (selectRecords.length > 0) {
          this.$confirm({
            content: `是否确认领用选中${selectRecords.length}数据？`,
            onOk: () => {
              let idList = selectRecords
                .map(function (obj, index) {
                  return obj.id
                })
                .join(',')
              let param = {
                fromType: 2, // 1托盘id 2领用明细id
                onlySave: onlySave,   // 是否只保存
                ids: idList,
              }
              // console.log('param:', param)
              applyReceiveConfirm(param)
                .then((res) => {
                  if (res.success) {
                    this.$emit('ok')
                    // this.findList()
                  } else {
                    this.$emit('fail', res)
                    console.error('保存失败:', res.message)
                  }
                })
                .catch((error) => {
                  this.$message.error('保存异常')
                  console.error('保存异常：', error)
                  this.$emit('fail')
                })
            },
          })
        } else {
          this.$message.error('尚未选中任何数据!')
        }
      } else {*/
        if (this.tableData && this.tableData.length > 0) {
          /*  不需要过滤了 let saveList = this.tableData.filter(t => !t.noSave)
          let idList = saveList.map(function (obj, index) {
              return obj.id
            }).join(',')*/
          let idList = this.tableData.map((obj, index) => {
            return obj.id
          })
          if (idList.length === 0) {
            this.$message.warn('没有要确认领料的物资，无法继续！')
            this.$emit('fail', { message: '没有要确认领料的物资，无法继续！' })
            return
          }
          let param = {
            fromType: 2, // 1托盘id 2 领用明细id
            ids: idList.join(',')
          }
          // console.log('param:', param)
          applyReceiveConfirm(param)
            .then((res) => {
              if (res.success) {
                this.$emit('ok')
                // this.findList()
              } else {
                this.$emit('fail',res)
                console.error('保存失败:', res.message)
              }
            }).catch((error) => {
              this.$message.error('保存异常')
              console.error('保存异常：', error)
              this.$emit('fail',{message:'保存异常！'})
            })
        } else {
          this.$emit('ok')
        }
      // }
    },
    close() {
      this.$emit('close')
    },
    openDistribution(row) {
      this.$refs.distributionModal.showModal(row)
    },
    onSuccess() {
      this.tableData.forEach((element) => {
        element.receive = 0
        this.distributionTableList.forEach((e) => {
          if (e.applyDetailId === element.id) {
            element.receive += Number(e.amount)
          }
        })
      })
    },
  },
}
</script>

<style scoped>
</style>