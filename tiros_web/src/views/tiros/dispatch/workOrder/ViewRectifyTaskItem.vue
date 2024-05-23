<template>
  <j-modal
    :title="title"
    :width="'90%'"
    centered
    :visible="visible"
    :footer="null"
    @cancel="handleCancel"
    :destroyOnClose="true"
    fullscreen
    cancelText="关闭"
  >
    <a-spin :spinning="confirmLoading">
      <a-form :form="form">
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="编码">
              <a-input placeholder="请输入" disabled v-decorator.trim="['rectifyNo']" />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="整改类型">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['rectifyType']"
                dictCode="bu_work_rectify_type"
                disabled
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联工单">
              <!--              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['orderId', validatorRules.orderId]"
                dictCode="bu_work_order,order_name,id"
                @change="changeOrder"
              />-->
              <span>
                <a-select
                  allow-clear
                  placeholder="请选择工单"
                  :open="false"
                  :showArrow="true"
                  v-decorator.trim="['orderName']"
                  ref="myOrderSelect"
                  disabled
                >
                  <a-icon slot="suffixIcon" type="ellipsis" />
                </a-select>
              </span>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="责任班组">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['groupId']"
                :dictCode="dictGroupStr"
                @change="changeGroup"
                disabled
              />
            </a-form-item>
          </a-col>
          <!-- <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="责任人员">
              <a-select
                placeholder="请选择保管人员"
                :open="false"
                :showArrow="true"
                v-decorator.trim="['dutyUserName', validatorRules.dutyUserName]"
                @focus="openuserModal"
                ref="myuserSelect"
              >
                <a-icon slot="suffixIcon" type="ellipsis" />
              </a-select>
            </a-form-item>
          </a-col> -->
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="整改状态">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['status']"
                dictCode="bu_work_rectify_status"
                disabled
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="整改工位">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['workstationId']"
                :dictCode="dictCodeStr"
                disabled
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆段">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['depotId']"
                dictCode="bu_mtr_depot,name,id"
                disabled
              />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
              <line-select-list v-decorator="['lineId']" disabled> </line-select-list>
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="整改工序">
              <j-dict-select-tag
                :triggerChange="true"
                v-decorator.trim="['orderTaskId']"
                :dictCode="dictCodeStrOrder"
                disabled
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24">
          <a-col :md="16" :sm="24">
            <a-form-item :labelCol="labelCol2" :wrapperCol="wrapperCol2" label="任务名称">
              <a-input placeholder="请输入" v-decorator.trim="['title']" disabled />
            </a-form-item>
          </a-col>
          <a-col :md="8" :sm="24">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="下发日期" width="40">
              <a-date-picker v-decorator.trim="['sendDate']" style="width: 100%" disabled />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="24"> </a-row>
        <a-row :gutter="24">
          <a-col :md="23" :sm="24">
            <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="备注说明">
              <a-textarea placeholder="请输入" v-decorator.trim="['remark']" disabled />
            </a-form-item>
          </a-col>
        </a-row>
        <a-divider orientation="left" style="font-size: 14px">已上传列表</a-divider>
        <vxe-table
          style="margin-top: 12px"
          border
          :data="annexList"
          show-overflow="tooltip"
          :edit-config="{ trigger: 'manual', mode: 'row' }"
        >
          <div class=""></div>
          <vxe-table-column
            type="seq"
            title="序号"
            width="50px"
            header-align="center"
            align="center"
          ></vxe-table-column>
          <vxe-table-column field="title" title="文件名" header-align="left" align="left"></vxe-table-column>
          <vxe-table-column title="操作" width="160px" header-align="center" align="center">
            <template v-slot="{ row }">
              <a style="margin-right: 12px" @click.stop="handleSeeing(row)">查看</a>
            </template>
          </vxe-table-column>
        </vxe-table>
        <doc-preview-modal :title="fileName" ref="docPreview"></doc-preview-modal>
      </a-form>
    </a-spin>
  </j-modal>
</template>

<script>
import moment from 'moment'

import DocPreviewModal from '@views/tiros/common/doc/DocPreviewModal'
import { getRectify } from '@/api/tirosQualityApi'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
  name: 'ViewFaultTaskItem',
  components: { DocPreviewModal, LineSelectList },
  data() {
    return {
      title: '操作',
      dictGroupStr:
        "bu_mtr_workshop_group,group_name,id,workshop_id='" + this.$store.getters.userInfo.workshopId + "'|workshop_id",
      fileList: [],
      annexList: [],
      rectifyId: '',
      filePath: '',
      fileName: '',
      status: false,
      uploading: false,
      dictCodeStr: 'bu_mtr_workstation,name,id',
      dictCodeStrOrder: '',
      title: '整改项信息',
      visible: false,
      model: {},
      labelCol: {
        xs: { span: 24 },
        sm: { span: 7 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 13 },
      },
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 2 },
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 22 },
      },
      labelCol2: {
        xs: { span: 24 },
        sm: { span: 3 },
      },
      wrapperCol2: {
        xs: { span: 24 },
        sm: { span: 19 },
      },
      dutyUsername: '',
      dutyUserId: '',
      isClose: false,
      confirmLoading: false,
      form: this.$form.createForm(this),
      workOrderId: '',
    }
  },
  created() {},
  methods: {
    view(value) {
      this.visible = true
      this.confirmLoading = true

      getRectify({ id: value.taskObjId }).then((res) => {
        this.confirmLoading = false
        if (res.success) {
          this.model = res.result
          this.$nextTick(() => {
            this.form.setFieldsValue({
              rectifyNo: this.model.rectifyNo,
              rectifyType: this.model.rectifyType,
              groupId: this.model.groupId,
              workstationId: this.model.workstationId,
              lineId: this.model.lineId,
              status: this.model.status,
              sendDate: moment(this.model.sendDate || new Date(), 'YYYY-MM-DD'),
              depotId: this.model.depotId,
              title: this.model.title,
              remark: this.model.remark,
              orderTaskId: this.model.orderTaskId,
              orderName: this.model.orderName,
            })
            this.annexList = this.model.annexList || []
          })
        }
      })
    },
    changeGroup(data) {
      if (data) {
        this.dictCodeStr =
          "bu_mtr_workstation,name,id,id in (select workstation_id from bu_group_workstation where group_id = '" +
          data +
          "')"
      } else {
        this.dictCodeStr = 'bu_mtr_workstation,name,id'
      }
    },
    handleSeeing(data) {
      this.fileName = data.title
      this.$refs.docPreview.handleFilePath(data.address)
    },

    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
      this.handleModel = {}
    },
  },
}
</script>
<style lang="less" scoped>
.info-top-wrapper {
  /deep/.ant-descriptions-item-label {
    width: 140px;
    text-align: right;
  }
  /deep/.ant-descriptions-item-content {
    width: 300px;
  }
}

.info-wrapper {
  border: 1px solid #eee;
  position: relative;
  border-radius: 8px;
  padding: 10px;
  margin-bottom: 20px;
}

.info-wrapper h4 {
  position: absolute;
  top: -14px;
  padding: 1px 8px;
  margin-left: 16px;
  color: #777;
  border-radius: 2px 2px 0 0;
  background: #fff;
  font-size: 14px;
  width: auto;
}
li {
  width: 100%;
  list-style: none;
  line-height: 2rem;
  color: black;
  transition: background-color 1s linear, color 1s linear;
  -webkit-transition: background-color 1s linear, color 1s linear;
  -moz-transition: background-color 1s linear, color 1s linear;
  -o-transition: background-color 1s linear, color 1s linear;
}
li:hover {
  background-color: #ddeeff;
  color: #19aaff;
}
</style>