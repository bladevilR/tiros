<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="findList">
        <a-row :gutter="24">
          <a-col :md="5" :sm="24">
            <a-form-item label="线路">
<!--              <j-dict-select-tag v-model="queryParam.lineId" dictCode="bu_mtr_line,line_name,line_id" />-->
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="编码">
              <a-input allow-clear placeholder="车辆编码" v-model="queryParam.trainNo" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="状态">
              <j-dict-select-tag v-model="queryParam.status" dictCode="bu_train_status" />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row>
          <a-col>
            <span class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button :disabled="records.length != 1" @click="handleEdit(records[0])">编辑</a-button>
                <a-button :disabled="records.length < 1" @click="deleteRecord">删除</a-button>
                <a-button @click="syncMaximo" v-has="'maximo:asset:init'" :loading="syncMaximoAssetLoading" >同步maximo资产</a-button>
                <template v-if="records.length == 1">
                  <a-button
                    :disabled="records[0].isGenAsset == 1"
                    @click="handleMenuClick({key:item.id},k)"
                    :loading="item.loading"
                    v-for="(item,k) in menuList"
                    :key="item.id"
                  >
                    生成车辆设备
<!--                    {{ item.name }}-生成车辆设备-->
                  </a-button>
                </template>
                <!-- <a-button>修改</a-button> -->
                <!-- <a-button @click="getTrainAsset">生成车辆设备</a-button> -->

              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 272px)">
        <vxe-table
          border
          ref="listTable"
          :align="allAlign"
          :data="tableData"
          show-overflow="tooltip"
          max-height="100%"
          auto-resize
          style="height: calc(100vh - 272px)"
          @checkbox-change="checkboxChange"
          @checkbox-all="checkboxChange"
          :checkbox-config="{trigger: 'row', highlight: true, range: true}"
          :edit-config="{ trigger: 'manual', mode: 'row' }"
        >
          <vxe-table-column type="checkbox" width="40"></vxe-table-column>
          <vxe-table-column field="line" title="所属线路" width="100"></vxe-table-column>
          <vxe-table-column title="车辆编码" width="150">
            <template v-slot="{ row }">
              <a @click.stop="jumpInfo(row)">{{ row.trainNo }}</a>
            </template>
          </vxe-table-column>
          <vxe-table-column field="trainType" title="车型" width="100"></vxe-table-column>
          <vxe-table-column field="groupNum" title="编组数" width="100"></vxe-table-column>
          <vxe-table-column field="status_dictText" title="车辆状态" width="120"></vxe-table-column>
          <vxe-table-column field="mileage" title="走行里程" width="120" align="right" header-align="center"></vxe-table-column>
          <vxe-table-column field="useDate" title="投入运营" width="150"></vxe-table-column>
          <vxe-table-column field="warrantyDate" title="质保有效期" width="150"></vxe-table-column>
          <vxe-table-column field="track" title="停放股道" width="120"></vxe-table-column>
          <vxe-table-column field="supplier" title="所属厂商" min-width="180" ></vxe-table-column>
          <vxe-table-column field="isGenAsset_dictText" title="是否已经生成车辆设备" min-width="180" ></vxe-table-column>
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
    <train-item-modal ref="modalForm" @ok="loadData"></train-item-modal>
    <train-asset-new ref="trainAsset"></train-asset-new>
  </div>
</template>

<script>
import TrainItemModal from './modules/trainInfo/TrainItemModal'
import { getTrainInfoList, delTrainInfo, produceTrainAsset, getTrainStructList } from '@/api/tirosApi'
import TrainAssetNew from '@views/tiros/basic/modules/trainInfo/TrainAssetNew'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import axios from 'axios'
export default {
  components: { TrainItemModal ,TrainAssetNew,LineSelectList},
  name: 'TrainList',
  data() {
    return {
      syncMaximoAssetLoading:false,
      records:[],
      queryParam: {
        lineId: '',
        status: '',
        trainNo: '',
        pageNo: 1,
        pageSize: 10,
      },
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      activeTrainId: '',
      menuList: [],
    }
  },
  created() {
    this.findList()
  },
  methods: {
    syncMaximo(){
      this.syncMaximoAssetLoading = true;
      axios({
        url: window._CONFIG['syncMaximo'] + '/third/maximo/read/init/asset',
        method:'get'
      }).then((res)=>{
        this.$message[res.success ? 'success' : 'warning'](res.message)
        this.syncMaximoAssetLoading = false;
      }).catch((err) => {
        this.syncMaximoAssetLoading = false;
      });
    },
    checkboxChange(e){
      this.records = e.records;
      if(e.records.length == 1){
        this.getTrainStruct(e.records[0])
      }else{
        this.menuList = [];
      }
    },
    findList() {
      this.loading = true
      getTrainInfoList(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
          this.loading = false
          this.records = [];
          this.tableData = res.result.records
        }
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
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
          content: `是否删除选中${selectRecords.length}条数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delTrainInfo({ ids: idsStr }).then((res) => {
              if (res.success) {
                this.$message.success(res.message)
                this.findList()
              } else {
                this.$message.warning(res.message)
              }
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    getTrainStruct(record) {
      this.activeTrainId = record.id
      this.menuList = []
      let param = {
        lineId: record.lineId,
      }
      getTrainStructList(param).then((res) => {
        if (res.success) {
          let record = res.result;
          Array.from(record,(item,index)=>{   //将类数组对象或可迭代对象转化为数组。
            item.loading = false;
          })
          this.menuList = record
        }
      })
    },
    getTrainAsset() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否为选中的车辆生成车辆设备？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            var idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            produceTrainAsset({ id: idsStr }).then((res) => {
              if (res.success) {
                this.$message.success(res.message)
              }
            })
          },
        })
      } else {
        this.$message.error('尚未选中车辆数据!')
      }
    },
    handleMenuClick(e,key) {
      let param = {
        id: this.activeTrainId,
        structId: e.key,
      }
      this.$confirm({
        content: '是否为车辆自动生成设备,将会覆盖原有的数据?',
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          this.menuList[key].loading = true;
          produceTrainAsset(param).then((res) => {
            if (res.success) {
              this.findList();
              this.$message.success(res.message)
            }
            this.menuList[key].loading = false;
          }).catch((err) => {
             this.menuList[key].loading = false;
          });
        },
      })
    },

    jumpInfo(row) {
      this.$refs.trainAsset.show({ trainId: row.trainNo, trainTypeId: row.trainTypeId, lineId: row.lineId});
     // this.$router.push({ path: `/tiros/basic/traininfo/${row.trainNo}/${row.trainTypeId}` })
    },
    loadData() {
      this.findList()
    },
  },
}
</script>

<style lang="less">
#trainListContent {
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