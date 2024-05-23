<template>
    <a-modal
        :title="title"
        :width="800"
        :visible="visible"
        @ok="handleOk"
        @cancel="handleCancel"
        :destroyOnClose="true"
        cancelText="关闭"
    >
        <a-form :form="form">
            <a-row>
              <a-col :span="12">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
                  <line-select-list
                    v-decorator="['lineId', validatorRules.lineId]"
                    @changeName="changeLine"
                  ></line-select-list>
                </a-form-item>
              </a-col>
                <a-col :span="12">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="总列次">
                        <a-input-number
                            :min="1"
                            :max="999999"
                            style="width: 100%"
                            v-decorator="['trainIndex', validatorRules.trainIndex]"
                        ></a-input-number>
                    </a-form-item>
                </a-col>
              <a-col :span="12">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="修程次数">
                  <a-input-number
                    :min="0"
                    :max="999999"
                    style="width: 100%"
                    v-decorator="['programIndex', validatorRules.programIndex]"
                  ></a-input-number>
                </a-form-item>
              </a-col>

<!--                <a-col :span="12">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="数量">
                        <a-input-number
                            :min="1"
                            :max="999999"
                            style="width: 100%"
                            v-decorator="['amount', validatorRules.amount]"
                        ></a-input-number>
                    </a-form-item>
                </a-col>-->
                <a-col :span="12">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="修程">
                        <j-dict-select-tag
                            :triggerChange="true"
                            v-decorator="['programId', validatorRules.programId]"
                            dictCode="bu_repair_program,name,id"
                            @changeName="changeprogram"
                        />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="开始时间">
                        <a-date-picker
                            format="YYYY-MM-DD"
                            style="width:100%"
                            v-decorator.trim="['startDate', validatorRules.startDate]"
                        />
                    </a-form-item>
                </a-col>
                <a-col :span="12">
                    <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="结束时间">
                        <a-date-picker
                            format="YYYY-MM-DD"
                            style="width:100%"
                            v-decorator.trim="['finishDate', validatorRules.finishDate]"
                        />
                    </a-form-item>
                </a-col>
            </a-row>
            <a-row>
                <a-col :span="24">
                    <a-form-item :labelCol="labelCol1" :wrapperCol="wrapperCol1" label="备注">
                        <a-textarea v-decorator="['remark', validatorRules.remark]" />
                    </a-form-item>
                </a-col>
            </a-row>
        </a-form>
    </a-modal>
</template>

<script>
import moment from 'moment'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

export default {
    components: { LineSelectList },
    data() {
        return {
            title: '新增计划',
            visible: false,
            model: {},
            labelCol: {
                xs: { span: 24 },
                sm: { span: 6 },
            },
            wrapperCol: {
                xs: { span: 24 },
                sm: { span: 18 },
            },
            labelCol1: {
                xs: { span: 24 },
                sm: { span: 3 },
            },
            wrapperCol1: {
                xs: { span: 24 },
                sm: { span: 21 },
            },
            form: this.$form.createForm(this),
            validatorRules: {
                lineId: { rules: [{ required: true, message: '请选择线路!' }] },
                trainIndex: { rules: [{ required: true, message: '请输入第几列!' }], initialValue: 1 },
              /*  amount: { rules: [{ required: true, message: '请输入数量!' }], initialValue: 1 },*/
                programId: { rules: [{ required: true, message: '请选择修程!' }] },
                startDate: { rules: [{ required: true, message: '请选择开始时间!' }] },
                finishDate: { rules: [{ required: true, message: '请选择结束时间!' }] },
                remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
              programIndex:  { rules: [{ required: true, message: '请输入修程次数!' }] },
            },
        }
    },
    methods: {
        moment,
        add() {
            this.edit({})
        },
        edit(record) {
            // this.form.resetFields()
            this.model = Object.assign({}, record)
            this.visible = true;
              this.$nextTick(() => {
                this.form.setFieldsValue({
                  trainIndex: record.trainIndex,
                  lineId: record.lineId,
                  programId: record.programId,
                  startDate: record.startDate,
                  finishDate: record.finishDate,
                  remark: record.remark,
                  programIndex:record.programIndex,
                })
              })
        },
        handleOk() {
            this.form.validateFields((err, values) => {
                if (!err) {
                    console.log(values)
                    let formData = Object.assign(this.model, values, {
                        startDate: moment(values.startDate).format('YYYY-MM-DD'),
                        finishDate: moment(values.finishDate).format('YYYY-MM-DD'),
                         amount: 1
                    })
                    this.$emit('ok', formData)
                    this.close()
                }
            })
        },
        changeLine(e) {
            this.model.lineName = e
        },
        changeprogram(e) {
            this.model.programName = e
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

<style></style>
