<template>
  <a-card id="turnoverContent">
    <a-form :form="form">
      <div class="info-wrapper info-top-wrapper">
        <h4>换下部件</h4>
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属路线">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属车辆">
              <j-dict-select-seach-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属系统">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item>
              <a-button type="primary" style="width:100%">新建不存在的部件</a-button>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件名">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="{span:10}" :wrapperCol="{span:14}" label="部件换上时间">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="责任人">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件位置">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资编码">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="资产编码">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件序列号">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件厂商">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </div>
      <div class="info-wrapper info-top-wrapper">
        <h4>换上部件</h4>
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="部件名称">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="资产编码">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="序列号">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item>
              <a-button type="primary" style="width:100%">新建不存在的部件</a-button>
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="更换原因">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="原装车号">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="原装位置">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资编码">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="更换日期">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="更换班组">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="更换人员">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="更换阶段">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
        </a-row>
        <a-row :gutter="12">
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="更换原因">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="原装车号">
              <j-dict-select-seach-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="原装位置">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
          <a-col :span="6">
            <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="物资编码">
              <j-dict-select-tag
                triggerChange
                v-decorator="['lineId', validatorRules.lineId]"
                dictCode="bu_mtr_line,line_name,line_id"
              />
            </a-form-item>
          </a-col>
        </a-row>
      </div>
    </a-form>
  </a-card>
</template>

<script>
export default {
  data() {
    return {
      form: this.$form.createForm(this),
      validatorRules: {
        lineId: { rules: [{ required: true, message: '请输入结构名称!' }] }
      },
      labelCol: {
        xs: { span: 24 },
        sm: { span: 8 }
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 16 }
      }
    }
  }
}
</script>

<style lang="less">
#turnoverContent {
  border: none;
  .ant-card-body {
    height: calc(100vh - 120px);
    overflow-y: auto;
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
}
</style>
