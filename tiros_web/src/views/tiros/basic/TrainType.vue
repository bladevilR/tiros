<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="名称">
              <a-input placeholder="请输入车型编码或车型名称" v-model="queryParam.searchName" allow-clear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag
                v-model="queryParam.status"
                placeholder="请选择状态"
                dictCode="bu_enable"
              />
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <!--
                                <a-button type="primary" @click="handleAdd" style="margin-left: 8px">新增</a-button>
                -->
                <!-- <a-button style="margin-left: 8px">修改</a-button> -->
                <!--
                                <a-button style="margin-left: 8px" @click="deleteRecord">删除</a-button>
                -->
                <a-button @click="jump">系统结构管理</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

      <div style="height: calc(100vh - 225px)">
        <vxe-table
          border
          auto-resize
          style="height: calc(100vh - 225px)"
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
          max-height="100%"
          :checkbox-config="{trigger: 'row', highlight: true, range: true}"
          :edit-config="{trigger: 'manual', mode: 'row'}"
        >
          <vxe-table-column type="checkbox" width="40"></vxe-table-column>
          <vxe-table-column field="code" title="车型编号" width="13%"></vxe-table-column>
          <vxe-table-column field="name" title="车型名称" align="left" header-align="center"></vxe-table-column>
          <vxe-table-column field="status_dictText" title="状态" width="10%"></vxe-table-column>
          <vxe-table-column field="updateTime" title="更新日期" width="200"></vxe-table-column>
          <vxe-table-column field="updateBy" title="更新人员" width="10%"></vxe-table-column>
          <vxe-table-column field="remark" title="备注" width="20%" align="left" header-align="center"></vxe-table-column>
          <vxe-table-column title="操作" width="10%">
            <template v-slot="{ row }">
              <a @click.stop="handleEdit(row)">编辑</a>
            </template>
          </vxe-table-column>
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
      <train-type-modal ref="modalForm" @ok="loadData()"></train-type-modal>
    <train-type-sys ref="trainTypeSys"></train-type-sys>
  </div>

</template>

<script>
  import TrainTypeModal from './modules/trainType/TrainTypeItemModal'
  import {getTrainTypeList, delTrainType } from '@/api/tirosApi'
  import TrainTypeSys from '@views/tiros/basic/TrainTypeSys'

  export default {
    components: { TrainTypeModal ,TrainTypeSys},
    data() {
      return {
        queryParam: {
          searchName: '',
          status: null,
          pageNo: 1,
          pageSize: 10
        },
        totalResult: 0,
        allAlign: 'center',
        tableData: []
      }
    },
    created() {
      this.findList()
    },
    methods: {
      findList() {
        this.loading = true
        getTrainTypeList(this.queryParam).then((res) => {
          if (res.success) {
            // console.log(res)
            this.totalResult = res.result.total
            this.loading = false
            this.tableData = res.result.records
            // console.log(this.tableData)
          }
        })
      },
      editTrack(data) {
        // console.log(data)
        let editData = this.tableData.filter((d) => {
          return d.id == data.id
        })
        // console.log(editData, data)
        Object.keys(data).forEach((key) => {
          data[key] = editData[key]
        })
        // console.log(editData, this.tableData)
      },
      handleEdit(record) {
        this.$refs.modalForm.edit(record)
        this.$refs.modalForm.title = '编辑'
      },
      handleAdd() {
        this.$refs.modalForm.add()
        this.$refs.modalForm.title = '新增'
      },
      deleteRecord() {
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        if (selectRecords.length > 0) {
          this.$confirm({
            content: `是否删除选中${selectRecords.length}数据？`,
            onOk: () => {
              let idsStr = selectRecords
                .map(function(obj, index) {
                  return obj.id
                })
                .join(',')
              delTrainType({ ids: idsStr }).then((res) => {
                if (res.success) {
                  this.$message.success(res.message)
                  this.loadData()
                } else {
                  this.$message.error(res.message)
                }
              })
              // this.tableData = this.tableData.filter((item) => {
              //   let idList = selectRecords.map((s) => s.id)
              //   console.log(idList, item.id, idList.includes(item.id))
              //   return !idList.includes(item.id)
              // })
              // console.log(this.tableData)
            }
          })
        } else {
          this.$message.error('尚未选中任何数据!')
        }
      },

      handlePageChange({ currentPage, pageSize }) {
        this.queryParam.pageNo = currentPage
        this.queryParam.pageSize = pageSize
        this.findList()
      },
      loadData() {
        this.findList()
      },
      jump() {
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        if (selectRecords.length == 1) {
          //let stationId = selectRecords[0].id
         // this.$router.push({ path: `/tiros/traintypesys/${stationId}` })
          this.$refs.trainTypeSys.show(selectRecords[0])
        } else {
          this.$message.error('请选择1条车型数据!')
        }
      }
    }
  }
</script>

<style lang="less">
  #trainTypeContent {
    border: none;

    .ant-card-body {
      padding: 24px 24px 0;
      height: calc(100vh - 120px);
      overflow-y: auto;
    }

    .tableHeight {
      height: calc(100vh - 255px);
      // overflow-y: auto;
    }
  }
</style>