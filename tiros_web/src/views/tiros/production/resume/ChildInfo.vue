<template>
  <div class='bodyWrapper'>
    <div style='height: calc(100vh - 190px)'>
      <vxe-table
        border
        max-height='100%'
        style='height: calc(100vh - 190px)'
        ref='listTable'
        :align='allAlign'
        :data='tableData'
        show-overflow='tooltip'
        :edit-config="{trigger: 'manual', mode: 'row'}"
      >
        <vxe-table-column type='checkbox' width='40'></vxe-table-column>
        <vxe-table-column field='name' align='left' header-align='center' title='设备名称' min-width='120'>
          <!--          <template v-slot="{ row }">
                      <a @click="jumpInfo(row)">{{ row.formObjName }}</a>
                    </template>-->
        </vxe-table-column>
        <vxe-table-column field='code' title='设备编码' width='120'></vxe-table-column>
        <vxe-table-column field='maximoAssetType' title='设备类型' width='100'></vxe-table-column>
        <vxe-table-column field='vendor' title='厂商' width='120'></vxe-table-column>
        <vxe-table-column field='brand' title='品牌' width='120'></vxe-table-column>
        <vxe-table-column field='manufNo' title='出厂编号' width='120'></vxe-table-column>
        <vxe-table-column field='model' title='规格类型' min-width='140' align='left'
                          header-align='center'></vxe-table-column>
        <!--        <vxe-table-column field="alertDescribe" title="制造日期" width="10%"></vxe-table-column>
                <vxe-table-column field="alertDescribe" title="购入日期" width="10%"></vxe-table-column>
                <vxe-table-column field="alertDescribe" title="运营日期" width="10%"></vxe-table-column>
                <vxe-table-column field="alertDescribe" title="使用年限" width="10%"></vxe-table-column>-->
      </vxe-table>
    </div>
  </div>
</template>

<script>
import { listMaximoAssetChild } from '@api/tirosProductionApi'

export default {
  name: 'ChileInfo',
  props: {
    trainNo: {
      type: String,
      default: ''
    },
    structureDetailId: {
      type: String,
      default: ''
    },
  },
  data() {
    return {
      queryParam: {
        structureDetailId: '',
        trainNo: ''
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      count: ''
    }
  },
  created() {
    // this.findList()
  },
  watch: {
    structureDetailId: {
      immediate: true,
      handler(id) {
        this.queryParam.parentId = id
        this.queryParam.trainNo = this.trainNo
        this.findList()
      }
    }
  },
  methods: {
    findList() {
      this.loading = true
      listMaximoAssetChild(this.queryParam).then((res) => {
        this.loading = false
        this.tableData = res.result
        if (res.result != null && res.result.ext != null) {
          this.tableData.brand = res.result.ext.brand
          this.tableData.model = res.result.ext.model
          this.tableData.vendor = res.result.ext.vendor
          this.tableData.manufNo = res.result.ext.manufNo
        }
      })
    },
    loadData() {
      this.findList()
    },
    jumpInfo(row) {
      // alert(row);
      //  this.$router.push({ path: `/tiros/quality/earlywarningcheck/${row.id}` })
    }
  }
}
</script>

<style scoped>

</style>