 <template>
  <div class="bodyWrapper">
    <a-spin :spinning="loading" tip="同步中..." na-spin-flex1>
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="24">
            <a-col :md="8" :sm="24">
              <a-form-item label="故障代码">
                <a-input v-model="queryParam.searchText" placeholder="请输入编码和描述"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="12">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button @click="syncCode">从检修系统同步</a-button>
                <!-- <a-button type="primary" @click="handleAdd">新增</a-button> -->
                <!-- <a-button type="dashed" v-if="isCheckRow" @click="handleEdit">编辑</a-button>
              <a-button type="dashed" v-if="isCheckRows" @click="handleDel">删除</a-button> -->
              </a-space>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <div style="flex: 1">
        <vxe-table
          border
          ref="listTable"
          align="center"
          height="auto"
          :data="tableData"
          :tree-config="{ lazy: false, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod}"
          @checkbox-change="rangeChange"
          @checkbox-all="rangeChange"
        >
          <!-- <vxe-table-column type="checkbox" width="60px" /> -->
          <vxe-table-column field="fltCode" title="故障代码" align="left" header-align="center" width="260" tree-node></vxe-table-column>
          <vxe-table-column field="fltLevel_dictText" title="类型" width="150"></vxe-table-column>
          <vxe-table-column field="fltName" title="代码描述" align="left" header-align="center"></vxe-table-column>
        </vxe-table>
      </div>
    </a-spin>
    <!-- <div style="height: 45px; background-color: #fff">
      <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="page.totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    </div> -->
  </div>
</template>
<script>
import { getFaultCodeListNew } from '@api/tirosDispatchApi'
import Vue from 'vue'
import { ACCESS_TOKEN } from '@/store/mutation-types'
import axios from 'axios'

export default {
  name: 'SpecialMaterial',
  components: {},
  data() {
    return {
      loading: false,
      isCheckRow: false,
      isCheckRows: false,
      tableData: [],
      defaultExpandRowKeys:[],
      queryParam: {
        searchText: '',
      },
      page: {
        totalResult: 1,
      },
    }
  },
  mounted() {
    this.findList()
  },
  methods: {
    findList() {
      this.isCheckRow = false
      this.isCheckRows = false
      getFaultCodeListNew(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = this.listToTreeList(res.result)
          this.$nextTick(()=>{
            if (this.tableData.length) {
              this.$refs.listTable.setTreeExpand(this.tableData.find(e => e.children.length), true)  
            }
          })
        } else {
          this.$message.error(res.message)
        }
      })
    },
    syncCode() {
      this.$confirm({
        // title: 'Do you want to delete these items?',
        content: '同步将从检修系统更新所有故障代码，确定要同步故障编码吗？',
        onOk: () => {
          // 开启动画
          this.loading = true
          // const token = Vue.ls.get(ACCESS_TOKEN);
          const url = window._CONFIG['syncMaximo'] + '/third/maximo/read/init/faultCode'
          axios({
            url: url,
            // headers: { "X-Access-Token": token },
            method: 'GET',
          })
            .then((res) => {
              if (res.data.success) {
                this.$message.success('操作成功')
                // 刷新
                this.findList()
              }
              // 关闭加载中动画
              this.loading = false
            })
            .catch((err) => {
              this.$emit('change', false)
              this.$notification.error({
                message: '服务器出错了',
                description: 'Network Error',
                duration: 4,
              })
              this.loading = false;
            })
        },
        onCancel() {},
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleAdd() {
      this.$refs.workSopModal.add()
    },
    handleEdit() {
      let record = this.$refs.listTable.getCheckboxRecords()[0]
      this.$refs.workSopModal.edit(record)
    },
    handleDel() {
      // let records = this.$refs.listTable.getCheckboxRecords()
    },
    // 用户选择记录触发
    rangeChange({ records }) {
      this.isCheckRow = records.length === 1
      this.isCheckRows = records.length > 0
    },
    listToTreeList(list) {
      // 将普通列表转换为树结构的列表
      if (!list || !list.length) {
        return []
      }
      let treeListMap = {}
      for (let item of list) {
        treeListMap[item.fltCode] = item
      }
      for (let i = 0; i < list.length; i++) {
        if (list[i].parentCode && treeListMap[list[i].parentCode]) {
          if (!treeListMap[list[i].parentCode].children) {
            treeListMap[list[i].parentCode].children = []
          }
          treeListMap[list[i].parentCode].children.push(list[i])
          list.splice(i, 1)
          i--
        }
      }
      list.sort((a, b ) => {
        return a.fltCode - b.fltCode
      })
      return list
    },
    loadChildrenMethod({row}){
      return row.children
    }
  },
}
</script>

<style lang="less" scoped>
.bodyWrapper {
  display: flex;
  flex-direction: column;
}
</style>
