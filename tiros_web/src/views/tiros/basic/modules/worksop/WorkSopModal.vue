<template>
  <a-modal
    :title="`${queryParam.id ? '编辑' : '新增'}${title}`"
    width="100%"
    dialogClass="fullDialog no-footer"
    :visible="visible"
    :confirmLoading="confirmLoading"
    @cancel="handleCancel"
    :footer="null"
  >
    <a-spin class="full-srceen" :spinning="confirmLoading">
      <!-- <div class="table-page-search-wrapper"> -->
      <NaCardContent
        class="table-page-search-wrapper"
        title="基本信息"
        style="background: transparent"
      >
        <a-form :form="form" layout="inline">
          <a-row :gutter="24">
            <a-col :md="6" :sm="24">
              <a-form-item label="文件编号">
                <a-input :maxLength="33" v-decorator="['fileNo', validatorRules.fileNo]" placeholder="请输入内容"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="文件名称">
                <a-input :maxLength="33" v-decorator="['fileName', validatorRules.fileName]" placeholder="请输入内容"></a-input>
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="文件版本">
                <a-input :maxLength="17" v-decorator="['fileVer', validatorRules.fileVer]" placeholder="请输入内容"></a-input>
              </a-form-item>
            </a-col>

            <a-col :md="6" :sm="24">
              <a-form-item label="线路">
                <line-select-list
                  v-decorator="['lineId', validatorRules.lineId]"
                  @change="onLineChange"
                ></line-select-list>
              </a-form-item>
            </a-col>
            <!-- <a-col :md="6" :sm="24">
              <a-form-item label="车辆" style="padding-left: 28px">
                <j-dict-select-seach-tag v-model="queryParam.trainNo" :dictCode="dictTrainStr" />
              </a-form-item>
            </a-col> -->
            <a-col :md="6" :sm="24">
              <a-form-item label="修程类型">
                <j-dict-select-tag
                  triggerChange
                  v-decorator="['repairProgramId', validatorRules.repairProgramId]"
                  dictCode="bu_repair_program,name,id"
                />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="实施日期" style="padding-left: 11px">
                <!-- <a-input v-model="queryParam.exeTime" placeholder="请输入内容"></a-input> -->
                <a-date-picker v-model="pickDate" format="YYYY-MM-DD" @change="dateChange" style="width: 100%" />
              </a-form-item>
            </a-col>
            <a-col :md="6" :sm="24">
              <a-form-item label="是否有效" style="padding-left: 11px">
                <!-- <j-switch v-model="queryParam.status"></j-switch> -->
                <a-switch v-model="bookStatus" />
              </a-form-item>
            </a-col>
            <a-col :span="24" align="right">
              <a-button type="primary" @click="save">保存</a-button>
            </a-col>
          </a-row>
        </a-form>
        <!-- </div> -->
      </NaCardContent>
      <WorkSopDetail :key="visible" ref="workSopDetailRef" @beforeSave="onBeforeSave"></WorkSopDetail>
    </a-spin>
  </a-modal>
</template>

<script>
import NaCardContent from '@comp/tiros/NaCardContent'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import { saveSopRecord, getSopPage } from '@/api/tirosApi'
import WorkSopDetail from '@views/tiros/basic/modules/worksop/WorkSopDetail'

export default {
  name: 'WorkSopModal',
  components: { LineSelectList, NaCardContent, WorkSopDetail },
  data() {
    return {
      value: 1,
      title: '作业指导书',
      visible: false,
      dictTrainStr: 'bu_train_info,train_no,train_no,status=1',
      model: {},
      labelCol1: {
        xs: { span: 24 },
        sm: { span: 3 },
      },
      wrapperCol1: {
        xs: { span: 24 },
        sm: { span: 21 },
      },
      labelCol2: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol2: {
        xs: { span: 24 },
        sm: { span: 18 },
      },
      labelCol3: {
        xs: { span: 24 },
        sm: { span: 9 },
      },
      wrapperCol3: {
        xs: { span: 24 },
        sm: { span: 15 },
      },
      confirmLoading: false,
      form: this.$form.createForm(this),
      validatorRules: {
        fileName: { rules: [{ required: true, message: '请输入文件名称!' },{ max: 32, message: '不能超过32个字符' }] },
        fileNo: { rules: [{ required: true, message: '请输入文件编号!' },{ max: 32, message: '不能超过32个字符' }] },
        fileVer: { rules: [{ required: true, message: '请文件版本!' },{ max: 16, message: '不能超过16个字符' }] },
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        repairProgramId: { rules: [{ required: true, message: '请选择修程!' }] },
      },
      pickDate: this.$moment(new Date()),
      bookStatus: true,
      closeAfterClose: false,
      queryParam: {
        exeTime: this.$moment(new Date()).format('YYYY-MM-DD'),
        fileName: null,
        fileNo: null,
        fileVer: null,
        id: null,
        lineId: null,
        repairProgramId: null,
        status: true,
      },
    }
  },
  created() {
    this.onLineChange('')
  },
  methods: {
    add() {
      this.edit({})
      this.bookStatus = true
    },
    edit(record) {
      this.visible = true
      // console.log(this.workShopDetail, record)
      if (record.id) {
        record = Object.assign(this.queryParam, record)
      } else {
        this.pickDate = this.$moment(new Date())
        this.queryParam.exeTime = this.$moment(new Date()).format('YYYY-MM-DD')
        this.queryParam.fileName = null
        this.queryParam.fileNo = null
        this.queryParam.fileVer = null
        this.queryParam.id = null
        this.queryParam.lineId = null
        this.queryParam.repairProgramId = null
        this.queryParam.status = 1
        record = Object.assign({}, this.queryParam)
      }

      this.form.resetFields()
      this.model = Object.assign({}, record)
      this.bookStatus = this.model.status === 1 ? true: false
      this.visible = true
      this.$nextTick(() => {
        this.form.setFieldsValue({
          fileName: record.fileName,
          fileNo: record.fileNo,
          fileVer: record.fileVer,
          lineId: record.lineId,
          repairProgramId: record.repairProgramId,
        })
      })
      this.updateDetail()
    },
    // 确定
    handleOk() {
      this.save()
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false;
      console.log(23232)
      // Object.assign(this.$data, this.$options.data());
    },
    // 线路变更
    onLineChange(val, option) {
      // let id = ''
      // if (val) {
      //   id = val
      // } else {
      //   id = ''
      // }
      // this.dictTrainStr = "bu_train_info,train_no,train_no,status=1 and line_id='" + id + "'"
    },
    dateChange() {
      this.queryParam.exeTime = this.$moment(this.pickDate).format('YYYY-MM-DD')
    },
    // 保存数据
    save() {
      this.form.validateFields((err, values) => {
        if (!err) {
          Object.assign(this.queryParam, values)
          // console.log(JSON.stringify(this.queryParam));
          this.queryParam.status = this.bookStatus ? 1 : 0
          saveSopRecord(this.queryParam).then((res) => {
            if (res.success) {
              if (!this.queryParam.id) {
                // getSopPage
                //   .call(this, {
                //     pageNo: 1,
                //     pageSize: 20,
                //     formName: this.queryParam.formName,
                //   })
                //   .then((res2) => {
                //     if (res2.success) {
                //       let records = res2.result.records
                //       let item = records.find((e) => {
                //         return (
                //           e.formName === this.queryParam.formName &&
                //           e.fileNo === this.queryParam.fileNo &&
                //           e.fileVer === this.queryParam.fileVer &&
                //           e.lineId === this.queryParam.lineId &&
                //           e.repairProgramId === this.queryParam.repairProgramId &&
                //           e.status === this.queryParam.status
                //         )
                //       })
                //       if (item) {
                //         this.queryParam.id = item.id
                //         this.$message.success('保存成功')
                //         this.updateDetail()
                //       } else {
                //         this.$message.error('保存失败')
                //       }
                //     }
                //   })
                if (res.result) {
                  this.queryParam.id = res.result
                  this.updateDetail()
                }
              } else {
                this.close()
              }
              this.$message.success('保存成功')
              this.$emit('change')
            } else {
              this.$message.error(res.message)
            }
          })
        }
      })
    },
    updateDetail() {
      this.$nextTick(() => {
        this.$refs.workSopDetailRef.findList(this.queryParam.id)
      })
    },
    onBeforeSave() {
      this.form.validateFields((err, values) => {
        if (!err) {
          if (!this.queryParam.id) {
            this.$confirm({
              content: `新增步骤前需要先保存作业指导书`,
              okText: '确定',
              cancelText: '取消',
              onOk: () => {
                this.save()
              },
            })
          }
        }
      })
    },
  },
}
</script>