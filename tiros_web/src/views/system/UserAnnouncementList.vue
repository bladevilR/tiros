<template>
  <a-card ref="card_cont" :bordered="false">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="searchQuery">
        <a-row :gutter="24">
          <a-col :span="6">
            <a-form-item label="标题">
              <a-input placeholder="请输入标题" v-model="queryParam.titile"></a-input>
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item label="发布人">
              <a-input placeholder="请输入发布人" v-model="queryParam.sender"></a-input>
            </a-form-item>
          </a-col>

          <a-col :span="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="searchQuery" icon="search">查询</a-button>
                <a-button type="primary" @click="searchReset" icon="reload">重置</a-button>
                <a-button type="primary" @click="readAll" icon="book">全部标注已读</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>

    <!--
    <div class="table-operator">
      <a-button type="primary" @click="readAll" icon="book">全部标注已读</a-button>
    </div>
-->
    <!-- <div>
      <vxe-table
        border
        align="center"
        ref="listTable"
        height="auto"
        auto-resize
        :data="dataSource"
      >
        <vxe-table-column field="titile" title="标题" />
        <vxe-table-column field="msgCategory" title="消息类型" />
        <vxe-table-column field="taskName" title="工单任务" />
        <vxe-table-column field="taskName" title="工单任务" />
        <vxe-table-column field="taskName" title="工单任务" />
        <vxe-table-column field="taskName" title="工单任务" />
        <vxe-table-column field="taskName" title="工单任务" />
      </vxe-table>
    </div> -->

    <a-table
      ref="table"
      size="default"
      bordered
      rowKey="id"
      :columns="columns"
      :dataSource="dataSource"
      :pagination="ipagination"
      :loading="loading"
      :scroll="{ y: autoHeight }"
      @change="handleTableChange"
    >
      <span slot="action" slot-scope="text, record">
        <a @click="showAnnouncement(record)">查看</a>
      </span>
    </a-table>
    <show-announcement ref="ShowAnnouncement"></show-announcement>
    <dynamic-notice ref="showDynamNotice" :path="openPath" :formData="formData" />
  </a-card>
</template>

<script>
import { filterObj } from '@/utils/util'
import { getAction, putAction } from '@/api/manage'
import ShowAnnouncement from '@/components/tools/ShowAnnouncement'
import { JeecgListMixin } from '@/mixins/JeecgListMixin'
import DynamicNotice from '../../components/tools/DynamicNotice'

export default {
  name: 'UserAnnouncementList',
  mixins: [JeecgListMixin],
  components: {
    DynamicNotice,
    ShowAnnouncement,
  },
  data() {
    return {
      autoHeight: 300,
      description: '系统通告表管理页面',
      queryParam: {},
      columns: [
        {
          title: '标题',
          align: 'left',
          customHeaderCell: (column) =>{
            console.log(column);
            column.align = 'center'
            return column
          },
          dataIndex: 'titile',
        },
        {
          title: '消息类型',
          align: 'center',
          dataIndex: 'msgCategory',
          width: 160,
          customRender: function (text) {
            if (text == '1') {
              return '通知公告'
            } else if (text == '2') {
              return '系统消息'
            } else {
              return text
            }
          },
        },
        {
          title: '发布人',
          align: 'center',
          dataIndex: 'sender',
          width: 160,
        },
        {
          title: '发布时间',
          align: 'center',
          dataIndex: 'sendTime',
          width: 180
        },
        {
          title: '优先级',
          align: 'center',
          dataIndex: 'priority',
          width: 120,
          customRender: function (text) {
            if (text == 'L') {
              return '低'
            } else if (text == 'M') {
              return '中'
            } else if (text == 'H') {
              return '高'
            } else {
              return text
            }
          },
        },
        {
          title: '阅读状态',
          align: 'center',
          dataIndex: 'readFlag',
          width: 120,
          customRender: function (text) {
            if (text == '0') {
              return '未读'
            } else if (text == '1') {
              return '已读'
            } else {
              return text
            }
          },
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          width: 120,
          scopedSlots: { customRender: 'action' },
        },
      ],
      url: {
        list: '/sys/sysAnnouncementSend/getMyAnnouncementSend',
        editCementSend: 'sys/sysAnnouncementSend/editByAnntIdAndUserId',
        readAllMsg: 'sys/sysAnnouncementSend/readAll',
      },
      loading: false,
      openPath: '',
      formData: '',
    }
  },
  mounted() {
    let that = this
    that.autoHeight = that.$refs.card_cont.$refs.cardContainerRef.offsetHeight - 210
    window.onresize = function () {
      that.autoHeight = that.$refs.card_cont.$refs.cardContainerRef.offsetHeight - 210
    }
  },
  methods: {
    handleDetail: function (record) {
      this.$refs.sysAnnouncementModal.detail(record)
      this.$refs.sysAnnouncementModal.title = '查看'
    },
    showAnnouncement(record) {
      putAction(this.url.editCementSend, { anntId: record.anntId }).then((res) => {
        if (res.success) {
          this.loadData()
          this.syncHeadNotic(record.anntId)
        }
      })
      if (record.openType === 'component') {
        this.openPath = record.openPage
        this.formData = { id: record.busId }
        this.$refs.showDynamNotice.detail()
      } else {
        this.$refs.ShowAnnouncement.detail(record)
      }
    },
    syncHeadNotic(anntId) {
      getAction('sys/annountCement/syncNotic', { anntId: anntId })
    },
    readAll() {
      var that = this
      that.$confirm({
        title: '确认操作',
        content: '是否全部标注已读?',
        onOk: function () {
          putAction(that.url.readAllMsg).then((res) => {
            if (res.success) {
              that.$message.success(res.message)
              that.loadData()
              that.syncHeadNotic()
            }
          })
        },
      })
    },
  },
}
</script>
<style scoped>
.ant-card {
  height: 100%;
}
.ant-card .ant-card-body {
  height: 100% !important;
}
.ant-card-body .table-operator {
  margin-bottom: 18px;
}
.anty-row-operator button {
  margin: 0 5px;
}
.ant-btn-danger {
  background-color: #ffffff;
}
z .ant-modal-cust-warp {
  height: 100%;
}
.ant-modal-cust-warp .ant-modal-body {
  height: calc(100% - 110px) !important;
  overflow-y: auto;
}
.ant-modal-cust-warp .ant-modal-content {
  height: 90% !important;
  overflow-y: hidden;
}
</style>