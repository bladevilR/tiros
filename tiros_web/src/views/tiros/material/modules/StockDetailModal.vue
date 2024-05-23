<template>
  <a-modal
    title="详情"
    :width="'70%'"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :footer="null"
    :destroyOnClose="true"
  >
    <div style="height: 70vh">
      <a-descriptions bordered :column="{ xxl: 4, xl: 3, lg: 3, md: 3, sm: 2, xs: 1 }">
        <a-descriptions-item label="物资编码">
          {{ StockDetail.materialTypeCode }}
        </a-descriptions-item>
        <a-descriptions-item label="物资名称">
          {{ StockDetail.materialTypeName }}
        </a-descriptions-item>
        <a-descriptions-item label="库位">
          {{ StockDetail.warehouseName }}
        </a-descriptions-item>
        <a-descriptions-item label="物资类别">
          {{ StockDetail.materialType_dictText }}
        </a-descriptions-item>
        <a-descriptions-item label="物资属性">
          {{ StockDetail.materialAttr_dictText }}
        </a-descriptions-item>
        <a-descriptions-item label="物资单位">
          {{ StockDetail.unit }}
        </a-descriptions-item>

        <a-descriptions-item label="库存数量">
          {{ StockDetail.amount }}
        </a-descriptions-item>
        <a-descriptions-item label="物资单价">
          {{ StockDetail.materialTypePrice }}
        </a-descriptions-item>
        <a-descriptions-item label="生产日期">
          {{ StockDetail.productionDate }}
        </a-descriptions-item>
        <a-descriptions-item label="失效日期">
          {{ StockDetail.expirDate }}
        </a-descriptions-item>
        <a-descriptions-item label="物料描述">
          {{ StockDetail.spec }}
        </a-descriptions-item>
      </a-descriptions>
    </div>
  </a-modal>
</template>
<script>
export default {
  name: 'StockDetail',
  data() {
    return {
      visible: false,
      confirmLoading: false,
      StockDetail: '',
    }
  },
  methods: {
    detail(data) {
      this.visible = true
      console.log(data)
      this.StockDetail = data
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
  },
}
</script>