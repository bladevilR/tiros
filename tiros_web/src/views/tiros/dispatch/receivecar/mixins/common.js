import moment from 'moment'
import { addExchange, editExchange, getTrainPlanTemplates, getItemNo, getReveiveCarTrainNumber } from '@api/tirosDispatchApi'
import UserList from '@views/tiros/common/selectModules/UserList'
import { everythingIsEmpty, UUID_V1, UUID_V4 } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import departWindow from '@views/system/modules/DepartWindow'

export default {
  components: { UserList, LineSelectList, departWindow },
  props: {
    trainNo:{
      required:true
    },
    isReadonly: {
      type: Boolean,
      default: false,
    },
    isEdit: {
      type: Boolean,
      default: false,
    }
  },
  data() {
    return {
      dictCodeStr: '',
      cardStyle: {
        padding: '10px',
        height: 'calc(100vh - 120px)',
      },
      sendUserId: '',
      labelCol: {
        xs: { span: 24 },
        sm: { span: 6 },
      },
      wrapperCol: {
        xs: { span: 24 },
        sm: { span: 15 },
      },
      model: {},
      validatorRules: {
        trainNo: { rules: [{ required: true, message: '请选择车辆!' }] },
        acceptMileage: { rules: [{ required: true, message: '请输入走行公里数!' }] },
        programId: { rules: [{ required: true, message: '请选择修程!' }] },
        acceptDate: { rules: [{ required: true, message: '请选择接车日期!' }] },
        // planFinishDate: { rules: [] },
        planTempId:{ rules: [{ required: true, message: '请选择计划模板!' }] },
        sendDeptId: { rules: [] },
        sendUserId: { rules: [] },
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        itemNo: { rules: [{ required: true, message: '请输入修程序号!' }] },
        trainIndex: { rules: [{ required: true, message: '请输入架修序号!' }] },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
        yearDetailId: { rules: [{ required: true, message: '请选择年计划明细!' }] }
      },
      templates: [],
      allAlign: 'center',
      tableData: [],
      carStateData: null,
      id: '',
      lineId: '',
      sendDeptId: '',
      sendDeptName: '',
      receiveCarOpenItemData: [], //开口项列表
      receiveCarRectifyData: [], //整改项列表
    }
  },
  created() {
    this.$bus.$on('setReceiveCarOpenItemData', (value) => {
      this.receiveCarOpenItemData = value || []
    })
    this.$bus.$on('setReceiveCarRectifyData', (value) => {
      this.receiveCarRectifyData = value || []
    })
    this.loadTemplates()
  },
  methods: {
    handleTempChange(e) {
      this.templates.map(temp => {
        if (temp.id === e) {
          let a_date = this.form.getFieldValue('acceptDate') || new Date();
          let f_date = moment(a_date).format('YYYY-MM-DD');
          f_date = moment(f_date).add(temp.duration, 'days').format('YYYY-MM-DD')
          this.form.setFieldsValue({
            programId : temp.repairProgramId,
            acceptDate: moment(a_date, 'YYYY-MM-DD'),
            planFinishDate: moment(f_date, 'YYYY-MM-DD'),
          })
          if(this.acceptDateChange){
            this.acceptDateChange(moment(a_date));
          }
          this.onProgramChange(temp.repairProgramId)
        }
      })
    },
    acceptMileageChange(data){
      this.$emit('update:acceptMileage', data)
    },
    handleChangeTrainNo(data) {
      this.$emit('update:trainNo', data)
      let programId = this.form.getFieldValue('programId')
      this.getItemNoByParam(programId, data)
      if (data && !this.isEdit) {
        getReveiveCarTrainNumber().then((res) => {
          if (res.success) {
            this.form.setFieldsValue({
              trainIndex: res.result,
            })
          }
        })
      }
    },
    loadTemplates() {
      getTrainPlanTemplates().then(res => {
        if (res.success) {
          this.templates = res.result
        }
      })
    },
    onUserSelect(data) {
      if (!everythingIsEmpty(data)) {
        this.sendUserId = data[0].id
        this.form.setFieldsValue({
          sendUserName: data[0].realname,
        })
      }
    },
    onProgramChange(value) {
      let trainNo = this.form.getFieldValue('trainNo')
      this.getItemNoByParam(value, trainNo)
    },
    getItemNoByParam(programId, trainNo) {
      if (programId && trainNo && !this.isEdit) {
        getItemNo(programId).then((res) => {
          if (res.success) {
            this.form.setFieldsValue({
              itemNo: res.result,
            })
          }
        })
      }
    },
    showUserModal() {
      this.$refs.tUserModalForm.showModal()
      this.$refs.tmyuserSelect.blur()
    },
    showGroupModal() {
      this.$refs.departWindow.add([], this.$store.getters.userInfo.id)
      this.$refs.groupSelect.blur()
    },
    modalFormOk(data) {
      if (!everythingIsEmpty(data)) {
        this.form.setFieldsValue({ sendDeptId: data.departIdList[0].value })
        this.form.setFieldsValue({ sendDeptName: data.departIdList[0].title })
        this.sendDeptId = data.departIdList[0].value
        this.sendDeptName = data.departIdList[0].title
      }
    },
    changeLine(data) {
      this.form.setFieldsValue({ trainNo: '' })
      if (data) {
        this.dictCodeStr = 'bu_train_info,train_no,train_no,line_id=' + data + '|train_struct_id'
      }
      this.lineId = data
    },
    changeClose(checked) {
      this.isClose = checked
    },
    bindData(data) {
      if (!data.id) {
        this.id = UUID_V4()
      } else {
        this.id = data.id
        this.lineId = data.lineId
      }
      this.$bus.$emit('setExchangeId', this.id)
      this.model = Object.assign({}, data)
      this.visible = true

      // 设置默认的交付部门，交付人
      if (!this.model.sendDeptId) {
        this.model.sendDeptId = this.$store.getters.userInfo.departId
        this.model.sendDeptName = this.$store.getters.userInfo.departName
      }
      if (!this.model.sendUserId) {
        this.model.sendUserId = this.$store.getters.userInfo.id
        this.model.sendUserName = this.$store.getters.userInfo.realname
      }
      this.$nextTick(() => {
        let _item = {
          trainNo: this.model.trainNo,
          acceptMileage: this.model.acceptMileage,
          programId: this.model.programId,
          acceptDate: moment(this.model.acceptDate || new Date(), 'YYYY-MM-DD'),
          planFinishDate: moment(this.model.planFinishDate || new Date(), 'YYYY-MM-DD'),
          sendDeptName: this.model.sendDeptName,
          planTempId: this.model.planTempId,
          sendUserName: this.model.sendUserName,
          lineId: this.model.lineId,
          itemNo: this.model.itemNo,
          trainIndex: this.model.trainIndex,
          remark: this.model.remark,
          yearDetailId: this.model.yearDetailId
        }
        if(this.acceptDateChange){
          this.acceptDateChange(moment(this.model.acceptDate || new Date()));
        }
        if(this.form){
          this.form.setFieldsValue(_item)
          if(this.model.lineId){
            this.dictCodeStr = 'bu_train_info,train_no,train_no,line_id=' + this.model.lineId + '|train_struct_id'
          }
        }else{
          this.formCopy = JSON.parse(JSON.stringify(this.model))
        }

      })
      this.sendDeptId = this.model.sendDeptId
      this.sendUserId = this.model.sendUserId
    },
    save() {
      return new Promise((resolve, reject) => {
        this.form.validateFields((err, values) => {
          if (!err) {
            let formData = Object.assign(this.model, values, {
              acceptDate: moment(values.acceptDate).format('YYYY-MM-DD'),
              planFinishDate: moment(values.planFinishDate).format('YYYY-MM-DD'),
              tradeType: 0,
              sendDeptId: this.sendDeptId,
              sendUserId: this.sendUserId,
            })
            if (formData.tradeType === 0) {
              if (!formData.yearDetailId) {
                this.$message.error('接车时必须选择一条对应的年计划')
                reject({message:'接车时必须选择一条对应的年计划', success:false})
              }
            }

            let obj
            if (!this.model.id) {
              formData['id'] = this.id
              obj = addExchange(formData)
            } else {
              obj = editExchange(formData)
            }

            // 开口整改项
            formData.leaveList = this.receiveCarOpenItemData
            formData.rectifyList = this.receiveCarRectifyData

            obj.then((res) => {
              if (res.success) {
                resolve(res)
              } else {
                reject(res)
                console.error("保存交接车失败：", res.message)
              }
            })
          } else {
            reject({message:'请先填写完接车信息!', success: false})
          }
        })
      })
    },
  },
}