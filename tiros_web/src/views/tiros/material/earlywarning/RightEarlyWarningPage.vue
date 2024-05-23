<template>
  <div na-flex-height-full>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="物资">
              <a-input placeholder="请输入物资编码或者名称" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="物资类别">
              <j-dict-select-tag v-model="queryParam.materialType" placeholder="请选择" dictCode="bu_material_type" />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="预警类别">
              <j-dict-select-tag
                v-model="queryParam.alertType"
                placeholder="请选择"
                dictCode="bu_material_alert_type"
              />
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <!--               <a-upload-->
                <!--                 name="file"-->
                <!--                 :multiple="false"-->
                <!--                 :customRequest="customRequestImport"-->
                <!--                 :showUploadList="false">-->
                <!--              <a-button type="primary">预警值导入</a-button>-->
                <!--               </a-upload>-->

                <a-dropdown>
                  <a-menu slot="overlay" @click="handleExport">
                    <a-menu-item key="1">
                      <div>周物资预警</div>
                    </a-menu-item>
                    <a-menu-item key="2">
                      <div>月物资预警</div>
                    </a-menu-item>
                    <a-menu-item key="3">
                      <div>导出当前数据</div>
                    </a-menu-item>
                  </a-menu>
                  <a-button> 预警导出 <a-icon type="down" /> </a-button>
                </a-dropdown>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div na-flex-height-full>
      <vxe-table
        border
        height="100%"
        style="max-height: "
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="materialCode" title="物资编码" width="100" />
        <vxe-table-column field="materialName" title="物资名称" min-width="100" header-align="center" align="left" />
        <vxe-table-column field="alertType_dictText" title="预警类别" width="80" />
        <vxe-table-column field="materialType_dictText" title="物资类别" width="80" />
        <vxe-table-column field="alertStock" title="警戒库存" width="80" header-align="center" align="right" />
        <vxe-table-column field="currentStock" title="当前库存" width="80" header-align="center" align="right" />
        <vxe-table-column field="diffStock" title="差量" width="60" header-align="center" align="right" />
        <vxe-table-column field="productionDate" title="生产日期" width="100" />
        <vxe-table-column field="expirDate" title="有效日期" width="100" />
        <vxe-table-column field="expirDay" title="有效天数" width="80" header-align="center" align="right" />
        <vxe-table-column field="materialSpec" title="物料描述" min-width="120" header-align="center" align="left" />
      </vxe-table>
    </div>
    <div style="height: 45px;">
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
  </div>
</template>

<script>
import { getEarlyWarningList, exportymonthEarly, exportyweekEarly, exportTheshold } from '@/api/tirosMaterialApi'
import moment from 'moment'

export default {
  name: 'RightEarlyWarningPage',
  props: ['value'],
  data() {
    return {
      queryParam: {
        searchText: '',
        materialType: '',
        warehouseId: '',
        pageNo: 1,
        pageSize: 10,
        alertType: '',
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      showEdit: false,
      cardStyle: {
        padding: '10px',
        height: 'calc(100vh - 120px)',
      },
    }
  },
  created() {
    if (sessionStorage.getItem('DEFAULT') === '1') {
      this.queryParam.alertType = 1
      sessionStorage.removeItem('DEFAULT')
    } else if (sessionStorage.getItem('DEFAULT') === '2') {
      this.queryParam.alertType = 2
      sessionStorage.removeItem('DEFAULT')
    } else {
      this.queryParam.alertType = 1
    }
    this.findList()
  },
  watch: {
    value: {
      immediate: true,
      handler(id) {
        this.queryParam.warehouseId = id
        this.tableData = []
        this.findList()
      },
    },
  },
  methods: {
    findList() {
      getEarlyWarningList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    loadData() {
      this.findList()
      this.$emit('load')
    },
    // customRequestImport() {
    //   const formData = new FormData()
    //   formData.append('file', data.file)
    // },
    handleExport(e) {
      switch (e.key) {
        case '1':
          let fileNameWeek = '周物资预警' + '-' + moment(new Date()).format('YYYYMMDD') + '.xls'
          exportyweekEarly(fileNameWeek)
          break
        case '2':
          let fileNameMonth = '月物资预警' + '-' + moment(new Date()).format('YYYYMMDD') + '.xls'
          exportymonthEarly(fileNameMonth)
          break
        case '3':
          let fileNameNow = '当前数据' + '-' + moment(new Date()).format('YYYYMMDD') + '.xls'
          exportTheshold(fileNameNow)
        default:
          break
      }
    },
  },
}
</script>

<style scoped>
</style>