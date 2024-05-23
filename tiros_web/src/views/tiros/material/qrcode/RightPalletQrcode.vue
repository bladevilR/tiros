<template>
  <a-card :body-style="cardStyle">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <a-form-item label="托盘">
                <a-input placeholder="请输入编码或者名称" v-model="queryParam.searchText" allowClear></a-input>
              </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
              <a-button @click="findList">查询</a-button>
              <a-button  @click="print">打印</a-button>
                </a-space>
            </span>
          </a-col>
          <a-col :md="6" :sm="8">
            <a-form-item  label="是否显示二维码">
              <a-switch v-model="state" @change="handleState" />
            </a-form-item>
          </a-col>
        </a-row>
      </a-form>
    </div>
      <div v-if="!state" style="height: calc(100vh - 250px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(100vh - 250px)"
        ref="listTable"
        :data="tableData"
        show-overflow="tooltip"
        :checkbox-config="{trigger: 'row', highlight: true, range: true}"
        :edit-config="{trigger: 'manual', mode: 'row'}"
        :align="allAlign"
      >
        <vxe-table-column type="checkbox" width="40" ></vxe-table-column>
        <vxe-table-column field="code" title="编码" width="15%">
          <template v-slot="{ row }">
            <a  @click.stop="listPalletCode(row)">{{ row.code }}</a>
          </template>
        </vxe-table-column>
        <vxe-table-column field="name" title="名称" width="25%" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column field="size" title="尺寸" width="20%" header-align="center" align="left"></vxe-table-column>
        <!--<vxe-table-column field="manufNo" title="出厂序列号" width="10%"></vxe-table-column>-->
        <vxe-table-column field="status_dictText" title="状态"  width="10%" ></vxe-table-column>
        <vxe-table-column field="remark" title="备注" align="left" header-align="center"></vxe-table-column>
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
      <print-qrcode @change="(e)=>{this.$emit('change',e)}" :state="state" ref="printQrcode" :isPallet="true"></print-qrcode>
      <qrcode-img-modal  ref="qrCodeImgModal"></qrcode-img-modal>
    </a-card>
</template>
<script>
  import { selectPallet} from '@/api/tirosMaterialApi'
  import PrintQrcode from './PrintQrcode'
  import QrcodeImgModal from '../modules/QrcodeImgModal'

  export  default {
    name:'RightPalletQrcode',
    components:{PrintQrcode,QrcodeImgModal},
    data (){
      return {
        state:false,
        queryParam: {
          searchText: '',
          depotId: '',
          workshopId:'',
          pageNo: 1,
          pageSize: 10
        },
        totalResult: 0,
        allAlign: 'center',
        tableData: [],
        cardStyle: {
          'padding': '10px',
          'height': 'calc(100vh - 120px)',
        }
      }
    },
    created() {
      this.findList()
    },
    methods:{
      findList(){
        selectPallet(this.queryParam).then((res) => {
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
      print(){
        let record=this.state?[]:this.getSelectRecords()
        this.$refs.printQrcode.print(record)
      },
      handleState(checked){
        this.$refs.printQrcode.handleState()
      },
      getSelectRecords() {
        return  this.$refs.listTable.getCheckboxRecords()
      },
      listPalletCode(row){
        this.$refs.qrCodeImgModal.qrCodeImg(row.qrcodeImgUrl)
      }

    }

  }
</script>
<style>
  .active {
    border:1px solid #1890FF;
  }
</style>