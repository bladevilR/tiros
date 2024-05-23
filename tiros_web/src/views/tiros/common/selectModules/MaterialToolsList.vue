<template>
  <!-- 物料类型选择弹窗 -->
  <a-modal
    width="90%"
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
    <!-- <a-card :bordered="false"> -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList" :label-col="formItemLayout.labelCol" :wrapper-col="formItemLayout.wrapperCol">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="物料名称">
              <a-input placeholder="请输入物料名称或编码" v-model="queryParam.searchText"></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="物料属性" style="width: 100%">
              <j-dict-select-tag v-model="queryParam.attr" dictCode="bu_material_attr" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="仓库">
              <j-tree-select
                dict="bu_mtr_warehouse,name,id"
                pidField="parent_id"
                v-model="queryParam.warehouseId"
              >
              </j-tree-select>
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
    <div style="height: calc(80vh - 130px);">
     <vxe-table
        border
        max-height="100%"
        style="height: calc(80vh - 130px);"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        highlight-current-row
        show-overflow="tooltip"
        :radio-config="!multiple ? {trigger: 'row'} : {}"
        :checkbox-config="multiple ? { trigger: 'row', highlight: true, range: true } : {}"
        :edit-config="{trigger: 'click', mode: 'cell', showIcon:'true'}"
      >
        <vxe-table-column v-if="multiple" type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column v-else type="radio" width="40"></vxe-table-column>
        <vxe-table-column field="code" title="物料编码" width="150"></vxe-table-column>
        <vxe-table-column field="name" title="物料名称" align="left" header-align="center"></vxe-table-column>
        <vxe-table-column field="warehouse" title="库位" width="200"></vxe-table-column>
        <vxe-table-column field="category1_dictText" title="类别" width="150"></vxe-table-column>
        <!-- 类别字段？？？？？？？？？？？？ -->
        <vxe-table-column field="category2_dictText" title="属性" width="150"></vxe-table-column>
        <vxe-table-column field="unit" title="单位" width="150"></vxe-table-column>
       <!-- <vxe-table-column field="price" title="单价" width="150"></vxe-table-column>-->
        <vxe-table-column field="amount" title="当前库存" width="150"></vxe-table-column>
<!--        <vxe-table-column
          field="num"
          title="所需数量"
          width="200"
          :edit-render="{name: '$input', immediate: true, props: {type: 'number',Min:0}}"
        ></vxe-table-column>-->
      </vxe-table>
     <vxe-pager
      perfect
      :current-page.sync="queryParam.pageNo"
      :page-size.sync="queryParam.pageSize"
      :total="totalResult"
      :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
      @page-change="handlePageChange"
    ></vxe-pager>
    </div>
  </a-modal>
</template>

<script>
import { getMaterialList } from '@/api/tirosMaterialApi'
import JTreeSelect from '@comp/jeecg/JTreeSelect'
export default {
  name: 'MaterialToolsList',
  components:{JTreeSelect},
  props: {
    multiple: {
      type: Boolean,
      default: true,
    },
    category1:{
      type:Number,
      default: 4
    }
  },
  data() {
    return {
      queryParam: {
        type: '2',
        searchText: '',
        attr: null,
        warehouseId:'',
        category1: this.category1,
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
          sm: { span: 6 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 18 }
        }
      },
    }
  },
  methods: {
    showModal() {
      this.visible = true
      this.findList()
    },
    findList() {
      this.loading = true
      getMaterialList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
        this.tableData.forEach((item) => {
          this.$set(item, 'num', 1)
        })
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
        if(this.$refs.listTable.getRadioRecord()) {
          selectRecords.push(this.$refs.listTable.getRadioRecord())
        }
      }
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