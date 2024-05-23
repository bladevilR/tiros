<template>
  <div class="bodyWrapper" style="padding: 8px; display: flex; flex-direction: column">
    <div class="table-page-body-wrapper" style="height: auto">
      <div class="table-body-context">
        <vxe-table border ref="listTable" align="center" :data="tableData" show-overflow="tooltip">
          <vxe-table-column field="no" title="序号" width="60"></vxe-table-column>
          <vxe-table-column
            field="title"
            title="名称"
            min-width="160"
            align="left"
            header-align="center"
          ></vxe-table-column>
          <vxe-table-column field="method_dictText" title="维保手段" width="120"></vxe-table-column>
          <vxe-table-column field="qualityLevel_dictText" title="质保等级" width="100"></vxe-table-column>
          <vxe-table-column
            field="requirement"
            title="技术要求"
            min-width="120"
            align="left"
            header-align="center"
          ></vxe-table-column>
          <!-- 序号、名称、维保手段、质保等级、安全提示、技术要求 -->
        </vxe-table>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TaskRegusView',
  props: ['tableData'],
  data() {
    return {
      visible: false,
      dictTrainStr: '',
    }
  },
  mounted() {
  },
  methods: {},
}
</script>

<style lang="less" scoped>
.bodyWrapper {
  .table-page-body {
    &.more-visible {
      height: calc(100% - 106px) !important;
    }
  }
}
</style>