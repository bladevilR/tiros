<template>
  <div>
    <!--  车辆具体设备选择（Maximo的） -->
    <a-modal
      title='车辆设备选择'
      width='80%'
      :bodyStyle="{ height: '83vh' }"
      :visible='visible'
      :confirmLoading='confirmLoading'
      :closable='true'
      centered
      @ok='handleOk'
      @cancel='close'
      :destroyOnClose='true'
    >
      <!-- 查询区域 -->
      <div class='table-page-search-wrapper'>
        <a-form layout='inline' @keyup.enter.native='searchQuery'>
          <a-row :gutter='24'>
            <a-col :md='5' :sm='24'>
              <a-form-item label='名称'>
                <a-input placeholder='名称或编码' v-model='queryParam.title'></a-input>
              </a-form-item>
            </a-col>
            <a-col :md='5' :sm='24'>
              <a-form-item label='线路'>
                <j-dict-select-tag
                  :triggerChange='true'
                  v-model='lineId_A'
                  dictCode='bu_mtr_line,line_name,line_id'
                  @change='changeLine'
                />
              </a-form-item>
            </a-col>
            <a-col :md='5' :sm='24'>
              <a-form-item label='车辆'>
                <j-dict-select-seach-tag
                  :triggerChange='true'
                  v-model='trainNo_A'
                  :dictCode='dictTrainStr'
                />
              </a-form-item>
            </a-col>
            <a-col :md='5' :sm='24'>
              <a-form-item label='状态'>
                <j-dict-select-tag
                  :triggerChange='true'
                  v-model='queryParam.status'
                  placeholder='请选择状态'
                  dictCode='bu_valid_status'
                />
              </a-form-item>
            </a-col>

            <a-col :md='5' :sm='24'>
                            <span
                              style='float: left; overflow: hidden'
                              class='table-page-search-submitButtons'
                            >
                                <a-space>
                                    <a-button @click='searchQuery'>查询</a-button>
                                </a-space>
                            </span>
            </a-col>
          </a-row>
        </a-form>
      </div>
      <div class='tableHeight'>
        <vxe-table
          border
          resizable
          row-id='id'
          ref='listTable'
          :align='allAlign'
          height='auto'
          :data='tableData'
          show-overflow='tooltip'
          max-height='100%'
          :radio-config="{trigger: !multiple ? 'row' : 'default',highlight: true,checkMethod: checkRadioMethod}"
          :checkbox-config="{trigger: multiple ? 'row' : 'default', highlight: true,checkStrictly: true,checkMethod: checkRadioMethod}"
          :tree-config="{ lazy: true, children: 'children', hasChild: 'hasChildren', loadMethod: loadChildrenMethod }"
        >
          <vxe-table-column v-if='!multiple' type='radio' width='40' fixed='left' />
          <vxe-table-column v-else type='checkbox' width='40' fixed='left'></vxe-table-column>
          <vxe-table-column
            field='name'
            title='名称'
            tree-node
            align='left'
            header-align='center'
          ></vxe-table-column>
          <vxe-table-column align='left' header-align='center' field='code' title='编码' width='180'></vxe-table-column>
          <vxe-table-column field="status_dictText" title="状态" align="center" header-align="center" width="100">
            <template v-slot="{ row }">
              <div :style="{ backgroundColor: statusColor[row.status + ''], borderRadius: '4px' }">
                {{ row.status_dictText }}
              </div>
            </template>
          </vxe-table-column>
          <!-- <vxe-table-column field="" title="类型" width="10%"></vxe-table-column> -->
          <vxe-table-column
            field='maximoAssetType'
            title='maximo设备类型'
            width='150'
            align='left'
            header-align='center'
          ></vxe-table-column>
          <vxe-table-column
            field='locationName'
            title='位置'
            width='400'
            align='left'
            header-align='center'
          ></vxe-table-column>
        </vxe-table>
      </div>
    </a-modal>
  </div>
</template>

<script>
import { getTrainAssetListCopy } from '@/api/tirosApi'

export default {
  props: {
    // 是否多选
    multiple: {
      type: Boolean,
      default: true
    },
    trainNo: {
      type: [Number, String]
      // required: true,
    },
    lineId: {
      type: [Number, String]
      // required: true,
    }
  },
  data() {
    return {
      statusColor: {
        0: '#dedede',
        1: '#bad795',
      },
      dictTrainStr: '',
      trainNo_A: undefined,
      lineId_A: undefined,
      allAlign: 'center',
      tableData: [],
      visible: false,
      confirmLoading: false,
      queryParam: {
        trainNo: '',
        searchText: '',
        status: 1
      }
    }
  },
  created() {
  },
  methods: {
    changeLine(data, edit) {
      this.dictTrainStr = ''
      /* this.dictTrainSys = ''*/
      /*if (edit !== true) this.faultModel.sysId = ''*/
      if (data) {
        this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + data + '|train_struct_id'
        /*this.dictTrainSys = 'bu_train_asset_type,name,id,struct_type=1 and parent_id is null and train_type_id=' +
          '(select train_type_id from bu_mtr_line where line_id=' + data + ' )'*/
      }
    },
    checkRadioMethod({ row }) {
      // 不做过滤，都可以选择
      /* if (row.hasChildren && row.maximoAssetType == '列管物资') {
         return true
       } else if(!row.hasChildren){
         return true
       }else{
         return false
       }*/
      return true
    },
    searchQuery() {
      this.findList()
    },
    loadChildrenMethod({ row }) {
      let param = {
        trainNo: this.trainNo_A,
        parentId: row.id,
        searchText: this.queryParam.searchText,
        status: this.queryParam.status
      }
      return new Promise((resolve) => {
        getTrainAssetListCopy(param).then((res) => {
          if (res.success) {
            resolve(res.result)
          } else {
            this.$message.error(`${res.msg}`)
          }
        })
      })
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
      // children: null
      // code: "01020600000300095"
      // hasChildren: true
      // id: "402881827b80a132017b80a1b151351c"
      // lineId: "2"
      // lineName: "2号线"
      // locationCode: "02003315000"
      // maximoAssetType: "列管物资"
      // maximoDept: "301090200"
      // maximoOrgId: "ORG1"
      // maximoSiteId: "SITE2"
      // maximoSpecialty: "电客车"
      // maximoStatus: "OPERATING"
      // name: "第5节车厢"
      // parentCode: "01020900000200071"
      // remark: "maximo初始化同步；"
      // status: 1
      // status_dictText: "有效"
      // trainNo: "0231"


      // console.log()
      // 修改字段
      Array.from(selectRecords, (item, index) => {
        console.log(item, index)
        // item.structDetailId =
        item.assetName = item.name
        // item.id =
      })
      this.$emit('ok', selectRecords)
      this.visible = false
    },
    showModal() {
      this.visible = true
      this.lineId_A = this.lineId
      this.trainNo_A = this.trainNo
      this.findList()
    },
    close() {
      this.visible = false
    },
    findList() {
      this.queryParam.trainNo = this.trainNo_A
      this.loading = true
      getTrainAssetListCopy(this.queryParam).then((res) => {
        if (res.success) {
          this.loading = false
          this.tableData = res.result
        }
      })
    }
  }
}
</script>

<style lang='less' scoped>
.tableHeight {
  // min-height: calc(100vh - 255px);
  height: calc(100% - 68px);
  // overflow-y: auto;
}
</style>