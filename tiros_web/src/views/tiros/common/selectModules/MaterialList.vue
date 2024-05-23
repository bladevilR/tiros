<template>
  <!-- 物料类型选择弹窗 -->
  <a-modal
      width='97%'
      title='物料选择'
      :visible='visible'
      :confirmLoading='confirmLoading'
      @ok='handleOk'
      @cancel='handleCancel'
      cancelText='关闭'
      centered
      :destroyOnClose='true'
      :bodyStyle="{ height: '80vh' }"
  >
    <!-- <a-card :bordered="false"> -->
    <a-tabs :default-active-key='tabIndex' @change='setTabIndex'>
      <a-tab-pane v-if='mode.indexOf(3) > -1' key='3' tab='班组物料'>
        <GroupStock :multiple='multiple' :materialTypeCode='materialTypeCode' :groupId='willChangeQueryParam.groupId'
                    ref='groupStock' :key='tabIndex' :isComponent='true' />
      </a-tab-pane>
      <a-tab-pane v-if='mode.indexOf(2) > -1' key='2' tab='必换件清单'>
        <div class='table-page-search-wrapper'>
          <a-form
              layout='inline'
              @keyup.enter.native='findList'
              :label-col='formItemLayout.labelCol'
              :wrapper-col='formItemLayout.wrapperCol'
          >
            <a-row :gutter='24'>
              <a-col :md='4' :sm='12'>
                <a-form-item label='物资'>
                  <a-input
                      allowClear
                      placeholder='物资编码或者名称'
                      v-model='willChangeQueryParam.searchText'
                  ></a-input>
                </a-form-item>
              </a-col>
              <a-col :md='3' :sm='24'>
                <a-form-item label='所属线路'>
                  <line-select-list
                      v-model='willChangeQueryParam.lineId'
                  ></line-select-list>
                </a-form-item>
              </a-col>
              <a-col :md='4' :sm='24'>
                <a-form-item label='所属修程'>
                  <j-dict-select-tag
                      v-model='willChangeQueryParam.repairProgramId'
                      placeholder='请选择'
                      dictCode='bu_repair_program,name,id'
                  />
                </a-form-item>
              </a-col>
              <a-col :md='4' :sm='24'>
                <a-form-item label='所属系统'>
                  <j-dict-select-tag
                      v-model='willChangeQueryParam.sysId'
                      placeholder='请选择'
                      dictCode='bu_train_asset_type,name,id,struct_type=1 and parent_id is null'
                  />
                </a-form-item>
              </a-col>
              <a-col :md='3' :sm='24'>
                <a-form-item label='班组'>
                  <!--                    :disabled="!!groupId"-->
                  <j-dict-select-tag
                      v-model='willChangeQueryParam.groupId'
                      placeholder='请选择班组'
                      :dictCode='dictGroupStr'
                  />
                </a-form-item>
              </a-col>
              <a-col :md='3' :sm='24'>
                <a-form-item label='工位'>
                  <j-dict-select-tag
                      v-model='willChangeQueryParam.workstationId'
                      placeholder='请选择'
                      :dictCode='dictCodeStrWork'
                  />
                </a-form-item>
              </a-col>
              <a-col :md='2' :sm='8'>
                <a-form-item>
                  <a-button @click='findList'>查询</a-button>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <div style='height: calc(80vh - 186px)'>
          <vxe-table
              border
              max-height='100%'
              style='height: calc(80vh - 186px)'
              ref='listTable'
              :align='allAlign'
              :data='tableData2'
              highlight-current-row
              show-overflow='tooltip'
              :radio-config="!multiple ? { trigger: 'row' } : {}"
              :checkbox-config="
              multiple ? { trigger: 'row', highlight: true, range: true } : {}
            "
              :edit-config="{ trigger: 'click', mode: 'cell', showIcon: 'true' }"
              @cell-click='onCellClick'
          >
            <vxe-table-column
                v-if='multiple'
                type='checkbox'
                width='40'
            ></vxe-table-column>
            <vxe-table-column v-else type='radio' width='40'></vxe-table-column>
            <vxe-table-column
                field='code'
                title='物料编码'
                width='150'
            ></vxe-table-column>
            <vxe-table-column
                field='name'
                title='物料名称'
                align='left'
                header-align='center'
            ></vxe-table-column>
            <vxe-table-column
                field='amount'
                title='当前库存'
                align='right'
                header-align='center'
                width='80'
            ></vxe-table-column>
            <template v-if='tabIndex === 1'>
              <vxe-table-column
                  field='category1_dictText'
                  title='类别'
                  width='120'
              ></vxe-table-column>
              <vxe-table-column
                  field='category2_dictText'
                  title='属性'
                  width='120'
              ></vxe-table-column>
              <vxe-table-column field='unit' title='单位' width='150'></vxe-table-column>
            </template>
            <template v-if='tabIndex === 2'>
              <vxe-table-column
                  field='spec'
                  title='物资描述'
                  align='left'
                  header-align='center'
              ></vxe-table-column>
              <vxe-table-column
                  field='groupName'
                  title='所属班组'
                  align='left'
                  header-align='center'
                  width='150'
              ></vxe-table-column>
              <vxe-table-column
                  field='workstationName'
                  title='所属工位'
                  align='left'
                  header-align='center'
                  width='150'
              ></vxe-table-column>
              <vxe-table-column
                  field='lineName'
                  title='线路'
                  width='100'
              ></vxe-table-column>
              <vxe-table-column
                  field='repairProgramName'
                  title='修程'
                  width='120'
              ></vxe-table-column>
            </template>
            <vxe-table-column
                field='num'
                title='所需数量'
                width='80'
                :edit-render="{
                name: '$input',
                immediate: true,
                props: { type: 'number', Min: 0 },
              }"
            ></vxe-table-column>
          </vxe-table>
        </div>
        <vxe-pager
            perfect
            :current-page.sync='willChangeQueryParam.pageNo'
            :page-size.sync='willChangeQueryParam.pageSize'
            :total='totalResult'
            :layouts="[
            'PrevJump',
            'PrevPage',
            'Number',
            'NextPage',
            'NextJump',
            'Sizes',
            'FullJump',
            'Total',
          ]"
            @page-change='handlePageChange'
        ></vxe-pager>
      </a-tab-pane>
      <a-tab-pane v-if='mode.indexOf(1) > -1' key='1' tab='其他物资'>
        <div class='table-page-search-wrapper'>
          <a-form
              layout='inline'
              @keyup.enter.native='findList'
              :label-col='formItemLayout.labelCol'
              :wrapper-col='formItemLayout.wrapperCol'
          >
            <a-row :gutter='24'>
              <a-col :md='4' :sm='24'>
                <a-form-item label='物料名称'>
                  <a-input
                      allowClear
                      placeholder='物料名称或编码'
                      v-model='queryParam.searchText'
                  ></a-input>
                </a-form-item>
              </a-col>
              <!--              <a-col :md="4" :sm="24">
                <a-form-item label="物料种类">
                  <j-dict-select-tag v-model="queryParam.type" dictCode="bu_material_kind" />
                </a-form-item>
              </a-col>-->
              <a-col :md='4' :sm='24'>
                <a-form-item label='物料分类'>
                  <j-dict-select-tag
                      :hidden-array='hiddenType'
                      v-model='queryParam.category1'
                      dictCode='bu_material_type'
                  />
                </a-form-item>
              </a-col>
              <a-col :md='4' :sm='24'>
                <a-form-item label='物料属性'>
                  <j-dict-select-tag
                      v-model='queryParam.attr'
                      dictCode='bu_material_attr'
                  />
                </a-form-item>
              </a-col>
              <!--      选择物资类型的时候，不需要仓库    <a-col :md="5" :sm="24">
            <a-form-item label="仓库">
              <j-tree-select
                dict="bu_mtr_warehouse,name,id"
                pidField="parent_id"
                v-model="queryParam.warehouseId"
                :disabled="disabled"
              >
              </j-tree-select>
            </a-form-item>
          </a-col>-->
              <a-col :md='3' :sm='8'>
                <a-form-item>
                  <a-button @click='findList'>查询</a-button>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
        <div style='height: calc(80vh - 186px)'>
          <vxe-table
              border
              max-height='100%'
              style='height: calc(80vh - 186px)'
              ref='listTable'
              :align='allAlign'
              :data='tableData'
              highlight-current-row
              show-overflow='tooltip'
              :radio-config="!multiple ? { trigger: 'row' } : {}"
              :checkbox-config="
              multiple ? { trigger: 'row', highlight: true, range: true } : {}
            "
              :edit-config="{ trigger: 'click', mode: 'cell', showIcon: 'true' }"
              @cell-click='onCellClick'
          >
            <vxe-table-column
                v-if='multiple'
                type='checkbox'
                width='40'
            ></vxe-table-column>
            <vxe-table-column v-else type='radio' width='40'></vxe-table-column>
            <vxe-table-column
                field='code'
                title='物料编码'
                width='150'
            ></vxe-table-column>
            <vxe-table-column
                field='name'
                title='物料名称'
                align='left'
                header-align='center'
            ></vxe-table-column>
            <vxe-table-column
                field='category1_dictText'
                title='类别'
                width='120'
            ></vxe-table-column>
            <vxe-table-column
                field='category2_dictText'
                title='属性'
                width='120'
            ></vxe-table-column>
            <vxe-table-column field='unit' title='单位' width='80'></vxe-table-column>
            <vxe-table-column
                field='amount'
                title='当前库存'
                align='right'
                header-align='center'
                width='80'
            ></vxe-table-column>
            <vxe-table-column
                field='spec'
                title='物资描述'
                align='left'
                header-align='center'
            ></vxe-table-column>
            <vxe-table-column
                field='num'
                title='所需数量'
                width='100'
                :edit-render="{
                name: '$input',
                immediate: true,
                props: { type: 'number', Min: 0 },
              }"
            ></vxe-table-column>
          </vxe-table>
        </div>
        <vxe-pager
            perfect
            :current-page.sync='queryParam.pageNo'
            :page-size.sync='queryParam.pageSize'
            :total='totalResult2'
            :layouts="[
            'PrevJump',
            'PrevPage',
            'Number',
            'NextPage',
            'NextJump',
            'Sizes',
            'FullJump',
            'Total',
          ]"
            @page-change='handlePageChange'
        ></vxe-pager>
      </a-tab-pane>
    </a-tabs>
  </a-modal>
</template>

<script>
import { getMaterialList, getWillChangeList } from '@/api/tirosMaterialApi'
import JTreeSelect from '@comp/jeecg/JTreeSelect'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import GroupStock from '@/views/tiros/material/groupStock/List.vue'

export default {
  name: 'MaterialList',
  components: { JTreeSelect, LineSelectList, GroupStock },
  props: {
    multiple: {
      type: Boolean,
      default: true
    },
    disabled: {
      type: Boolean,
      default: false,
      required: false
    },
    materialTypeCode: {
      type: String,
      default: ''
    },
    defaultCheckedKeys: {
      type: String,
      default: ''
    },
    mode: {
      type: Array,
      default: () => [1, 2]  // 总共有三个参数 [1, 2, 3]
    },
    showTypeCode: {
      type: Boolean,
      default: false
    },
    typeCodeList: {
      type: Array,
      default: () => []
    },
    // 传递此参数 必换件则不可选择班组 不传则可以选择班组查询
    groupId: {
      type: String,
      default: null
    },
    // 自动带入线路信息
    lineId: {
      type: [Number, String],
      default: null
    },
    // 修程id
    repairProgramId: {
      type: [Number, String],
      default: null
    }
  },
  watch: {
    defaultCheckedKeys: {
      immediate: true,
      handler(id) {
        this.queryParam.warehouseId = id
      }
    },
    lineId: {
      immediate: true,
      handler(val) {
        if ((this.mode.indexOf(2) > -1) && val) {
          this.willChangeQueryParam.lineId = val
        }
      }
    },
    repairProgramId: {
      immediate: true,
      handler(val) {
        if ((this.mode.indexOf(2) > -1) && val) {
          this.willChangeQueryParam.repairProgramId = val
        }
      }
    },
    //工班
    'willChangeQueryParam.groupId': {
      handler(newVal, oldVal) {
        if (newVal) {
          this.dictCodeStrWork = `bu_mtr_workstation,name,id,id in (select workstation_id from bu_group_workstation where group_id='${newVal}')`
        } else {
          this.dictCodeStrWork = 'bu_mtr_workstation,name,id'
        }
      },
      immediate: true,
      deep: true
    }
  },
  data() {
    /*const numValid = ({ cellValue, row }) => {
      return new Promise((resolve, reject) => {
        if (cellValue > row.amount) {
          reject(new Error('数量已超出当前库存量'))
        } else if (cellValue < 1) {
          reject(new Error('数量不能小于1'))
        } else {
          resolve()
        }
      })
    }*/
    return {
      planIdCopy: '',
      hiddenType: [],
      dictGroupStr:
          'bu_mtr_workshop_group,group_name,id,workshop_id=\'' +
          this.$store.getters.userInfo.workshopId +
          '\'|workshop_id',
      dictCodeStrWork: 'bu_mtr_workstation,name,id',
      queryParam: {
        type: '1',
        category1: '',
        searchText: '',
        attr: null,
        warehouseId: '',
        materialTypeCodeList: [],
        pageNo: 1,
        pageSize: 10
      },
      willChangeQueryParam: {
        searchText: '',
        lineId: '',
        repairProgramId: '',
        sysId: '',
        groupId: this.groupId || '',
        workstationId: '',
        materialTypeCodeList: [],
        pageNo: 1,
        pageSize: 10
      },
      allAlign: 'center',
      totalResult: 0,
      tableData: [],
      totalResult2: 0,
      tableData2: [],
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
      tabIndex: this.mode[0] + ''
    }
  },
  created() {
  },
  methods: {
    setTabIndex(index) {
      this.tabIndex = index
      this.findList()
    },
    showModal() {
      this.visible = true
      // console.log(this.groupId)
      if (this.groupId) {
        this.willChangeQueryParam.groupId = this.groupId
      }
      if (this.typeCodeList.length > 1) {
        this.hiddenType = [1]
      }
      if (this.mode && this.mode.length === 1) {
        this.tabIndex = (this.mode[0] + '').trim()
      }
      this.findList()
    },
    findList() {
      let obj = ''
      switch (this.tabIndex) {
        case '1':
          let query = Object.assign({}, this.queryParam)

          if (!query.category1 && this.typeCodeList && this.typeCodeList.length) {
            query.category1 = this.typeCodeList.length ? this.typeCodeList.join(',') : ''
          }

          obj = getMaterialList(query)
          break
        default:
          /* if (this.showTypeCode) {
            this.willChangeQueryParam.materialTypeCodeList = this.typeCodeList.length ? this.typeCodeList : [-1]
          }*/
          obj = getWillChangeList(this.willChangeQueryParam)
          break
      }
      obj.then((res) => {
        // console.log(res,this.tabIndex)
        if (this.tabIndex === '1') {
          this.totalResult2 = res.result.total
          this.tableData = res.result.records
          this.tableData.forEach((item) => {
            this.$set(item, 'num', 1)
          })
        } else {
          this.totalResult = res.result.total
          this.tableData2 = res.result.records
          this.tableData2.forEach((item) => {
            this.$set(item, 'num', 1)
            if (this.tabIndex === '2') {
              this.$set(item, 'num', item.needAmount)
            }
          })
        }

      })
    },
    handlePageChange({ currentPage, pageSize }) {
      switch (this.tabIndex) {
        case '1':
          this.queryParam.pageNo = currentPage
          this.queryParam.pageSize = pageSize
          break
        default:
          this.willChangeQueryParam.pageNo = currentPage
          this.willChangeQueryParam.pageSize = pageSize
          break
      }
      this.findList()
    },
    handleOk() {
      let selectRecords = []
      if (this.multiple) {
        selectRecords = this.tabIndex === '3' ? this.$refs.groupStock.getCheckboxRecords() : this.$refs.listTable.getCheckboxRecords()
      } else {
        if (this.$refs.listTable || this.$refs.groupStock) {
          selectRecords.push(this.tabIndex === '3' ? this.$refs.groupStock.getCheckboxRecords() : this.$refs.listTable.getRadioRecord())
        }
      }
      // console.log(selectRecords)
      if (this.tabIndex === '2' || this.tabIndex === '3') {
        let temps = []
        selectRecords.forEach((item) => {
          let obj = Object.assign({}, item)

          if (this.tabIndex === '3') {
            // 班组库存，需要返回班组库存的id
            obj.groupStockId = item.id
            obj.usableAmount = item.usableAmount
          }
          obj.id = item.materialTypeId

          if (this.tabIndex === '3') {
            obj.num = 1
            obj.category1 = -1
            obj.category1_dictText = '其他'
            obj.name = item.materialTypeName
            obj.code = item.materialTypeCode
          } else {
            obj.category1 = 1
            obj.category1_dictText = '必换件'
          }
          temps.push(obj)
        })
        selectRecords = temps
      }
      Array.from(selectRecords, (item, index) => {
        item.useCategory = item.category1
        item.useCategory_dictText = item.category1_dictText
      })
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
      this.tabIndex = '1'
    },
    // fix 点击数量编辑时取消选择问题
    onCellClick({ row, column }) {
      if (column.property === 'num') {
        this.$refs.listTable.setCheckboxRow([row], true)
      }
    }
  }
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
