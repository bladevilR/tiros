<template>
  <div class="bodyWrapper">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="5" :sm="12">
            <a-form-item label="物资">
              <a-input placeholder="物资编码或者名称" v-model="queryParam.searchText" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="所属线路">
              <line-select-list v-model="queryParam.lineId"></line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <a-form-item label="所属修程">
              <j-dict-select-tag
                v-model="queryParam.repairProgramId"
                placeholder="请选择"
                dictCode="bu_repair_program,name,id"
              />
            </a-form-item>
          </a-col>
          <a-col :md='4' :sm='24'>
            <a-form-item label='工班'>
              <j-dict-select-tag
                v-model='queryParam.groupId'
                placeholder='请选择'
                :dictCode='dictGroupStr'
              />
            </a-form-item>
          </a-col>
          <a-col :md="4" :sm="24">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="findList">查询</a-button>
                <!-- <a-button type="primary" @click="handleAdd">新增</a-button>
                <a-button @click="handleEdit" :disabled="!btnStatus.edit">修改</a-button>
                <a-button @click="deleteRecord" :disabled="!btnStatus.del">删除</a-button>
                <a-button @click="$refs.importModal.show()">导入</a-button> -->
                <a-button><a :style="{fontSize: '12px'}" @click='toggle'>
                更多条件 <a-icon :type="expand ? 'up' : 'down'" />
                </a></a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>

        <a-row :gutter='24' :style="{ display: expand ? 'block' : 'none' }"
        >

          <a-col :md='6' :sm='24'>
            <a-form-item label='所属工位'>
              <j-dict-select-tag
                v-model='queryParam.workstationId'
                placeholder='请选择'
                :dictCode='dictCodeStrWork'
              />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="24">
            <a-form-item label="所属系统">
              <j-dict-select-tag
                v-model="queryParam.sysId"
                placeholder="请选择"
                dictCode="bu_train_asset_type,name,id,struct_type=1 and parent_id is null"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row v-has="'willchange:admin'">
          <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
          <a-space>
            <a-button type="primary" @click="handleAdd" v-has="'willchange:admin'">新增</a-button>
            <a-button @click="handleEdit" :disabled="!btnStatus.edit" v-has="'willchange:admin'">修改</a-button>
            <a-button @click="deleteRecord" :disabled="!btnStatus.del" v-has="'willchange:admin'">删除</a-button>
            <a-button @click="validRecord">启用</a-button>
            <a-button @click="invalidRecord">禁用</a-button>
            <a-button @click="groupVisible = true" :disabled="!btnStatus.del" v-has="'willchange:admin'">设置工班</a-button>
            <a-button @click="$refs.importModal.show()" v-has="'willchange:admin'">导入</a-button>
          </a-space>
        </span>
        </a-row>
      </a-form>
    </div>
    <div :style="{
      height: `calc(100vh - ${expand ? '310' : '268'}px)`
    }">
      <vxe-table
        border
        :align="allAlign"
        ref="listTable"
        height="100%"
        auto-resize
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{ trigger: 'row', highlight: true, range: true }"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
        @checkbox-change="btnStatus.update()"
        @checkbox-all="btnStatus.update()"
      >
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column field="code" title="物资编码" width="150"></vxe-table-column>
        <vxe-table-column field="name" title="物资名称" min-width="180" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="spec" title="物资描述" min-width="220" header-align="center"  align="left"></vxe-table-column>
        <vxe-table-column field="lineName" title="线路" width="100"></vxe-table-column>
        <vxe-table-column field="repairProgramName" title="修程" width="120" header-align="center"  align="left"></vxe-table-column>
        <vxe-table-column
          field="sysName"
          title="系统"
          min-width="120"
          header-align="center"
          align="left"
        ></vxe-table-column>
        <vxe-table-column field="workstationName" title="工位" width="140"></vxe-table-column>
        <vxe-table-column field="groupName" title="所属工班" align="left" header-align="center" width="140"></vxe-table-column>
        <vxe-table-column field="materialTypeCategory_dictText" title="属性" width="100"></vxe-table-column>
        <vxe-table-column field="unit" title="单位" width="100"></vxe-table-column>
        <vxe-table-column field="needAmount" title="单列所需" align="right" header-align="center" width="100"></vxe-table-column>
        <vxe-table-column field="status_dictText" title="状态" align="center" header-align="center" width="80">
          <template v-slot="{ row }">
            <div :style="{ backgroundColor: statusColor[row.status + ''], borderRadius: '4px' }">
              {{ row.status_dictText }}
            </div>
          </template>
        </vxe-table-column>
        <vxe-table-column
          field="locationName"
          title="安装部位"
          min-width="150"
          header-align="center"
          align="left"
        ></vxe-table-column>
        <vxe-table-column
          field="remark"
          title="备注"
          min-width="100"
          header-align="center"
          align="left"
        ></vxe-table-column>
        <!-- <vxe-table-column title="操作" width="80" fixed="right">
          <template v-slot="{ row }">
            <a @click.stop="handleEdit(row)">编辑</a>
          </template>
        </vxe-table-column> -->
      </vxe-table>
    </div>
    <vxe-pager
        perfect
        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
        @page-change="handlePageChange"
      ></vxe-pager>
    <will-change-item-modal ref="modalForm" @ok="loadData()"></will-change-item-modal>
    <willChangeImport ref="importModal" url="/import/materialMust" @ok="loadData()" />
    <borrow-detail-modal ref="detailForm"></borrow-detail-modal>

    <a-modal
      :title="'设置工班'"
      :visible="groupVisible"
      @ok="setGroup"
      @cancel="groupVisible = false"
      cancelText="关闭"
      :destroyOnClose="true"
    >
      <div style="display: flex; align-items: center; padding: 18px">
        <div>工班：</div>
        <j-dict-select-tag
          v-model='settingGroup'
          placeholder='请选择'
          :dictCode='dictGroupStr'
          style="width: calc(100% - 45px)"
        />
      </div>
    </a-modal>
  </div>
</template>


<script>
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { confirmBorrowReturn, delWillChangeItem, getWillChangeList, setWillChangeGroup, validMustList, invalidMustList } from '@api/tirosMaterialApi'
import WillChangeItemModal from '../modules/WillChangeItemModal'
import willChangeImport from '../modules/willChangeImport'
import BorrowDetailModal from '../modules/BorrowDetailModal'
import moment from 'moment'
import 'moment/locale/zh-cn'
import { everythingIsEmpty } from '@/utils/util'
import TableBtnUtil from '@views/tiros/util/TableBtnUtil'

export default {
  name: 'List',
  components: { WillChangeItemModal, BorrowDetailModal, LineSelectList, willChangeImport },
  data() {
    return {
      dictGroupStr: 'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      dictCodeStrWork: 'bu_mtr_workstation,name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
      queryParam: {
        searchText: '',
        lineId: '',
        programId: '',
        systemId: '',
        groupId:'',
        workstationId:'',
        pageNo: 1,
        pageSize: 10,
      },
      expand: false,
      totalResult: 0,
      allAlign: 'center',
      tableData: [],
      btnStatus: new TableBtnUtil(this, 'listTable'),
      tableHeight: {
        top: 'height: calc(100vh - 225px)',
        bottom: 'height: calc(100vh - 225px)',
        size: '100%',
      },
      groupVisible: false,
      settingGroup: null,
      statusColor: {
        0: '#dedede',
        1: '#bad795',
      },
    }
  },
  watch:{
    //工班
    'queryParam.groupId' (newVal, oldVal) {
      if (newVal) {
        this.dictCodeStrWork = `bu_mtr_workstation,name,id,id in (select workstation_id from bu_group_workstation where group_id='${newVal}')`
      } else {
        this.dictCodeStrWork = 'bu_mtr_workstation,name,id'
      }
    }
  },
  created() {
    this.findList()
  },
  methods: {
    toggle () {
      this.expand = !this.expand
      // if (this.expand) {
      //   this.tableHeight = {
      //     top: 'height: calc(100vh - 315px)',
      //     bottom: 'height: calc(100vh - 350px)',
      //     size: '90%'
      //   }
      // } else {
      //   this.tableHeight = {
      //     top: 'height: calc(100vh - 225px)',
      //     bottom: 'height: calc(100vh - 225px)',
      //     size: '100%'
      //   }
      // }
    },
    findList() {
      getWillChangeList(this.queryParam).then((res) => {
        this.totalResult = res.result.total
        this.loading = false
        this.tableData = res.result.records
        this.btnStatus.update()
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
    handleAdd() {
      this.$refs.modalForm.add()
      this.$refs.modalForm.title = '新增'
    },
    handleEdit() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length === 1) {
        this.$refs.modalForm.edit(selectRecords[0])
        this.$refs.modalForm.title = '编辑'
      } else {
        this.$message.error('请选中一项数据!')
      }
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            delWillChangeItem('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    validRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.filter((r) => { return r.status === 1 }).length > 0) {
          this.$message.warn('有效的数据不能启用')
          return
        }
        this.$confirm({
          content: `是否启用选中的必换件清单？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            validMustList('ids=' + idsStr)
              .then((res) => {
                if (res.success) {
                  this.$message.success('启用成功')
                  this.findList()
                } else {
                  this.$message.error( res.message)
                }
              })
              .catch((err) => {
                this.$message.error('启用异常')
                console.log('启用异常：', err)
              })
          },
        })
      } else {
        this.$message.error('尚未选中数据!')
      }
    },
    invalidRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.filter((r) => { return r.status === 0 }).length > 0) {
          this.$message.warn('无效的数据不能禁用')
          return
        }
        this.$confirm({
          content: `是否禁用选中的必换件清单？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            invalidMustList('ids=' + idsStr)
              .then((res) => {
                if (res.success) {
                  this.$message.success('禁用成功')
                  this.findList()
                } else {
                  this.$message.error( res.message)
                }
              })
              .catch((err) => {
                this.$message.error('禁用异常')
                console.log('禁用异常：', err)
              })
          },
        })
      } else {
        this.$message.error('尚未选中数据!')
      }
    },
    handlerReturn() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否设置选中${selectRecords.length}数据为已归还？`,
          onOk: () => {
            let idsStr = selectRecords
              .map(function (obj, index) {
                return obj.id
              })
              .join(',')
            confirmBorrowReturn('ids=' + idsStr).then((res) => {
              this.findList()
              this.$message.success(res.message)
            })
          },
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    borrowDetail(row) {
      this.$refs.detailForm.detail(row)
    },
    handlerReadyMaterial() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        if (selectRecords.length > 1) {
          this.$message.warning('只能选中一条数据!')
        } else {
          this.$refs.readyMaterial.edit(row.id)
        }
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    // 弹出日历和关闭日历的回调
    handleOpenChange(status) {
      this.yearPickShow = status
    },
    // 得到年份选择器的值
    handlePanelChange(value) {
      this.year = value
      this.queryParam.year = moment(value).format('YYYY')
      this.yearPickShow = false
    },
    handleChanYearChange(value) {
      if (everythingIsEmpty(value)) this.queryParam.year = ''
    },
    handleMonthChange(value) {
      if (everythingIsEmpty(value)) {
        this.queryParam.year = ''
        this.queryParam.month = ''
      }
    },
    setGroup(){
      if (!this.settingGroup) {
        this.$message.warn('请选择工班')
        return
      }
      let records = this.$refs.listTable.getCheckboxRecords()
      setWillChangeGroup({
        groupId: this.settingGroup,
        idList: records.map(e => e.id)
      }).then(res => {
        if (res.success) {
          this.$message.success('执行成功')
          this.groupVisible = false
          this.findList()
        } else {
          this.$message.error(res.message)
        }
      })
    }
  },
}
</script>