<template>
  <a-modal
    title="详情"
    width="70%"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :footer="null"
    :destroyOnClose="true"
  >
    <div style="height: 70vh">
      <a-descriptions
        bordered
        :column="{ xxl: 4, xl: 3, lg: 3, md: 3, sm: 2, xs: 1 }"
      >
        <a-descriptions-item label="物资编码">
          {{borrowDetail.materialTypeCode}}
        </a-descriptions-item>
        <a-descriptions-item label="物资名称">
          {{borrowDetail.materialTypeName}}
        </a-descriptions-item>

        <a-descriptions-item label="物资描述">
          {{borrowDetail.materialTypeSpec}}
        </a-descriptions-item>
        <a-descriptions-item label="借用数量">
          {{borrowDetail.amount}}
        </a-descriptions-item>
        <a-descriptions-item label="物资单位">
          {{borrowDetail.materialTypeUnit}}
        </a-descriptions-item>
        <a-descriptions-item label="所属仓库">
          {{borrowDetail.warehouseName}}
        </a-descriptions-item>
        <a-descriptions-item label="借用组织">
          {{borrowDetail.deptName}}
        </a-descriptions-item>

        <a-descriptions-item label="借用人员">
          {{borrowDetail.borrowUserName}}
        </a-descriptions-item>
        <a-descriptions-item label="借用日期">
          {{borrowDetail.borrowDate}}
        </a-descriptions-item>
        <a-descriptions-item label="归还类型">
          {{borrowDetail.returnType_dictText}}
        </a-descriptions-item>
        <a-descriptions-item label="归还状态">
          {{borrowDetail.returnStatus_dictText}}
        </a-descriptions-item>
        <a-descriptions-item label="备注">
          {{borrowDetail.remark}}
        </a-descriptions-item>
      </a-descriptions>
    </div>

  </a-modal>
</template>


<script>
  export default {
    name: 'BorrowDetail',
    data() {
      return {
        visible: false,
        confirmLoading: false,
        borrowDetail: ''
      }
    },
    methods: {

      detail(data) {
        this.visible = true
        this.borrowDetail = data
      },
      // 关闭
      handleCancel() {
        this.close()
      },
      close() {
        this.$emit('close')
        this.visible = false
      }
    }

  }
</script>