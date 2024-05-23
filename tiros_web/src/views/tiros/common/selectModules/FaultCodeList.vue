<template>
  <a-modal
    width="70%"
    title="故障代码选择"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭"
    centered
    :destroyOnClose="true"
    :bodyStyle="{ height: '80vh' }"
  >
    <!-- <a-card :bordered="false"> -->
    <div class="table-page-search-wrapper">
      <a-form
        layout="inline"
        @keyup.enter.native="findList"
        :label-col="formItemLayout.labelCol"
        :wrapper-col="formItemLayout.wrapperCol"
      >
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="">
              <a-input placeholder="请输入编码和描述" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="3" :sm="8">
            <a-form-item>
              <a-button @click="findList">查询</a-button>
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(80vh - 82px)">
      <vxe-table
        border
        height="auto"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        highlight-current-row
        show-overflow="tooltip"
        :radio-config="!multiple ? { trigger: 'row', checkMethod: checkMethod} : {}"
        :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true, checkMethod: checkMethod } : {}"
        :tree-config="{ lazy: false, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod}"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40" ></vxe-table-column>
        <vxe-table-column field="fltCode" align="left" header-align="center" title="故障代码" width="260" tree-node></vxe-table-column>
        <vxe-table-column field="fltLevel_dictText" title="类型" width="150"></vxe-table-column>
        <vxe-table-column field="fltName" title="代码描述" align="left" header-align="center"></vxe-table-column>
      </vxe-table>
      <!-- <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager> -->
    </div>
    <!-- <fault-category-list ref="categorySelect" :multiple="false" @ok="addCategoryTarget"></fault-category-list> -->
  </a-modal>
</template>

<script>
import { getFaultCodeNew, getFaultCodeListNew } from '@api/tirosDispatchApi'
import { everythingIsEmpty } from '@/utils/util'
import FaultCategoryList from '@views/tiros/common/selectModules/FaultCategoryList'

export default {
  name: 'FaultCodeList',
  components: { FaultCategoryList },
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
    type: {  // 如果 type == unit 则只能选择部件 
      type: String,
      default: '',
    },
  },
  data() {
    return {
      categoryCode: '',
      queryParam: {
        searchText: ''
      },
      allAlign: 'center',
      totalResult: 0,
      tableData: [],
      visible: false,
      confirmLoading: false,
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
  methods: {
    showModal(id) {
      this.visible = true
      this.queryParam.categoryId = id
      this.findList()
    },
    findList() {
      this.loading = true
      // if (this.queryParam.screen) {
      getFaultCodeListNew(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = this.listToTreeList(res.result)
          this.$nextTick(()=>{
            if (this.tableData.length) {
              this.$refs.listTable.setTreeExpand(this.tableData.find(e => e.children), true)  
            }
          })
        } else {
          this.$message.error(res.message)
        }
      })
      // }else {
      //   getFaultCodeNew({code: ''}).then((res) => {
      //     res.result.forEach(element => {
      //       element.children = []
      //     });
      //     this.tableData = res.result
      //   })
      // }

      
    },
    loadChildrenMethod({row}){
      return getFaultCodeNew({code: row.fltCode}).then((res) => {
        if (res.success) {
          (res.result || []).forEach(element => {
            element.children = []
          });
          return res.result || []
        }else {
          return []
        }
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleOk() {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.$refs.listTable.getCheckboxRecords()
      } else {
        if (this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
      this.$emit('ok', selectRecords)
      this.close()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
      this.categoryCode = ''
      this.queryParam.searchText = ''
      this.queryParam.categoryId = ''
    },
    openSelectTypeModal() {
      this.$refs.categorySelect.showModal()
      this.$refs.categoryIdSelect.blur()
    },
    addCategoryTarget(data) {
      if (!everythingIsEmpty(data)) {
        this.queryParam.categoryId = data[0].id
        this.categoryCode = data[0].categoryCode
      } else {
        this.queryParam.categoryId = ''
      }
    },
    checkMethod({row}){
      if(this.type == 'unit'){
        return row.fltLevel == 3
      }else{
        return true
      }
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