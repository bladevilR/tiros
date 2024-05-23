 <template>
  <div class="bodyWrapper">
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="设备">
              <a-input v-model="queryParam.searchText" placeholder="名称或编码" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" placeholder="请选择" dictCode="bu_tools_status" />
            </a-form-item>
          </a-col>
          <a-col :md="12">
            <a-space>
              <a-button @click="findList">查询</a-button>
              <a-button type="primary" @click="handleAdd">新增</a-button>
              <a-button :disabled="!tableForm.edit" @click="handleEdit">编辑</a-button>
              <a-button :disabled="!tableForm.del" @click="handleDel">删除</a-button>
              <a-button :disabled="!tableForm.edit" @click="showView(tableForm.editRow)">设备日历</a-button>
            </a-space>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100% - 100px)">
      <vxe-table
        border
        ref="listTable"
        align="center"
        height="auto"
        :data="tableData"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        @checkbox-change="tableForm.update()"
        @checkbox-all="tableForm.update()"
      >
        <vxe-table-column type="checkbox" width="60px" />
        <vxe-table-column field="assetCode" title="设备编码" />
        <vxe-table-column field="name" title="设备名称">
          <!-- <template v-slot="{ row }">
            <a @click.stop="showView(row)">{{ row.name }}</a>
          </template> -->
        </vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" />
        <vxe-table-column field="workshopName" title="所属车间" />
        <vxe-table-column field="supplierName" title="厂商" />
        <vxe-table-column field="manufNo" title="出厂编码" />
        <vxe-table-column field="materialCode" title="物资编码" />
        <vxe-table-column field="brand" title="品种" />
        <vxe-table-column field="model" title="规格" />
        <vxe-table-column field="leaveFactory" title="出厂日期" />
        <vxe-table-column field="useDate" title="投入日期" />
        <vxe-table-column field="dutyUserName" title="责任人" />
      </vxe-table>
    </div>
    <div style="height: 45px; background-color: #fff">
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="page.totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div>
    <SpecialMaterialModal ref="specialMaterialModal" @ok="findList"></SpecialMaterialModal>
    <SpecialMaterialView ref="viewModal"></SpecialMaterialView>
  </div>
</template>
<script>
import TableFormUtil from '@views/tiros/util/TableFormUtil'
import { getSpecassetList, setSpecassetItem, deleteSpecassetItem } from '@/api/tirosMaterialApi'
import SpecialMaterialModal from '@views/tiros/material/specialmaterial/SpecialMaterialModal'
import SpecialMaterialView from '@views/tiros/material/specialmaterial/SpecialMaterialView'

export default {
  name: 'SpecialMaterial',
  components: { SpecialMaterialModal, SpecialMaterialView },
  data() {
    return {
      tableData: [],
      queryParam: {
        pageNo: 1,
        pageSize: 10,
        searchText: '',
        status: null,
      },
      page: {
        totalResult: 1,
      },
      tableForm: new TableFormUtil(this, 'listTable'),
    }
  },
  mounted() {
    this.findList()
  },
  methods: {
    findList() {
      getSpecassetList(this.queryParam).then((res) => {
        if (res.success) {
          this.page.totalResult = res.result.total
          this.tableData = res.result.records
        } else {
          this.$message.error('获取数据失败')
        }
      }).finally(()=>{
        this.tableForm.update()
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleAdd() {
      this.$refs.specialMaterialModal.title = '新增'
      this.$refs.specialMaterialModal.add()
    },
    handleEdit() {
      // let record = this.$refs.listTable.getCheckboxRecords()[0]
      this.$refs.specialMaterialModal.title = '编辑'
      this.$refs.specialMaterialModal.edit(this.tableForm.editRow)
    },
    handleDel() {
      this.tableForm.handleDel().then((res) => {
        if (res) {
          let formData = new FormData()
          formData.append('ids', res)
          deleteSpecassetItem(formData).then((res) => {
            if (res.success) {
              this.$message.success('删除成功')
              this.findList()
            } else {
              this.$message.error(res.message)
            }
          })
        }
      })
    },
    showView(row) {
      this.$refs.viewModal.showModal(row)
    },
  },
}
</script>
