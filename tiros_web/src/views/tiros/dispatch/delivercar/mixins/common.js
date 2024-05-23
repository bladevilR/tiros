import { addExchange, editExchange, getExChangeByTrain,getListDeliverable } from '@api/tirosDispatchApi'
import moment from 'moment'
import { UUID_V4 } from '@/utils/util'
import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'
import UserList from '@views/tiros/common/selectModules/UserList'
import departWindow from '@views/system/modules/DepartWindow'
import { everythingIsEmpty } from '@/utils/util'
import { getDepartTree } from '@/api/tirosApi'

export default {
  components: { LineSelectList, UserList, departWindow },
  props: {
    carStateData:{
      required:true
    },
    isReadonly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      dictCodeStr:
        'bu_train_info,train_no,train_no,exists(select id from bu_repair_exchang where bu_repair_exchang.train_no=bu_train_info.train_no and bu_repair_exchang.status=2 order by bu_repair_exchang.create_time desc limit 1)',
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
        acceptMileage: { rules: [{ required: true, message: '请输入走形公里数!' }] },
        programId: { rules: [{ required: true, message: '请选择修程!' }] },
        acceptDate: { rules: [{ required: true, message: '请选择接车日期!' }] },
        planFinishDate: { rules: [{ required: true, message: '请选择接车日期!' }] },
        sendDeptId: { rules: [] },
        sendUserId: { rules: [] },
        lineId: { rules: [{ required: true, message: '请选择线路!' }] },
        // itemNo: { rules: [{ required: true, message: '请输入项目序号!' }] },
        remark: { rules: [{ max: 255, message: '输入长度不能超过255字符!' }] },
      },
      allAlign: 'center',
      tableData: [],
      trainList:[],
      receiveCarOpenItemData: [], //开口项列表
      receiveCarRectifyData: [], //整改项列表
    }
  },
  created() {
    getListDeliverable().then((res)=>{
      if(res.success && res.result && res.result.length>0){
        this.trainList = res.result
      }
    })
  },
  mounted() {
    this.findList()
    this.$bus.$on('setReceiveCarOpenItemData', (value) => {
      this.receiveCarOpenItemData = value || []
    })
    this.$bus.$on('setReceiveCarRectifyData', (value) => {
      this.receiveCarRectifyData = value || []
    })
  },
  methods: {
    changeLine(data) {
      this.form.setFieldsValue({ trainNo: '' })
      if (data) {
        this.dictCodeStr =
          'bu_train_info,train_no,train_no,line_id=' +
          data +
          ' and exists(select id from bu_repair_exchang where bu_repair_exchang.train_no=bu_train_info.train_no and bu_repair_exchang.status=2 order by bu_repair_exchang.create_time desc limit 1)'
      } else {
        this.dictCodeStr =
          'bu_train_info,train_no,train_no,exists(select id from bu_repair_exchang where bu_repair_exchang.train_no=bu_train_info.train_no and bu_repair_exchang.status=2 order by bu_repair_exchang.create_time desc limit 1)'
      }
    },
    acceptMileageChange(data){
      this.$emit('update:acceptMileage', data)
    },
    handleTrainNo(data) {
      if (data) {
        const arr = this.trainList.filter((item) => {
            return  item.trainNo === data //条件;
        });
        if(arr.length>0){
          this.model.receiveId = arr[0].id ;
          this.form.setFieldsValue({
            receiveId: arr[0].id
          })
        }

        getExChangeByTrain({ trainNo: data }).then((res) => {
          if (res.success) {
            let receiveCar = res.result;
            if (receiveCar) {
              this.$emit('update:acceptMileage', receiveCar.acceptMileage) 
              this.$nextTick(() => {
                this.form.setFieldsValue({
                  programId: receiveCar.programId,
                  acceptMileage: receiveCar.acceptMileage,
                  acceptDate: receiveCar.acceptDate ? moment(receiveCar.acceptDate, 'YYYY-MM-DD') : null,
                  trainIndex: receiveCar.trainIndex,
                  itemNo: receiveCar.itemNo,
                })
              })
            }else{
              this.$nextTick(() => {
                this.form.setFieldsValue({
                  programId: undefined,
                  acceptDate: undefined,
                  acceptMileage: undefined,
                  trainIndex: undefined,
                  itemNo: undefined,
                })
              })
            }
          }
        })
      }else{
        this.form.setFieldsValue({
          trainNo:undefined,
          receiveId: undefined
        })
      }
      this.model.trainNo = data
      this.$emit('update:carStateData', this.model)
    },
    onUserSelect(data) {
      if (!everythingIsEmpty(data)) {
        this.sendUserId = data[0].id
        this.form.setFieldsValue({
          sendUserName: data[0].realname,
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
        this.sendDeptName = data.departIdList[0].title
      }
    },
    changeClose(checked) {
      this.isClose = checked
    },
    findList() {
      this.model = Object.assign({}, this.carStateData)
      // 设置默认的交付部门，交付人
      if (!this.model.sendDeptId) {
        this.model.sendDeptId = this.$store.getters.userInfo.departId
        this.model.sendDeptName = this.$store.getters.userInfo.departName
      }
      if (!this.model.sendUserId) {
        this.model.sendUserId = this.$store.getters.userInfo.id
        this.model.sendUserName = this.$store.getters.userInfo.realname
      }
      this.$bus.$emit('setExchangeId',this.model.id)
      this.$nextTick(() => {
        if(this.form){
          this.form.getFieldDecorator('sendDeptName', {})
          this.form.setFieldsValue({
            trainNo: this.model.trainNo,
            receiveId: this.model.receiveId,
            acceptMileage: this.model.acceptMileage,
            programId: this.model.programId,
            acceptDate: this.model.acceptDate ? moment(this.model.acceptDate, 'YYYY-MM-DD') : undefined,
            planFinishDate: this.model.planFinishDate ? moment(this.model.planFinishDate, 'YYYY-MM-DD') : moment(new Date(), 'YYYY-MM-DD'),
            sendDeptName: this.model.sendDeptName,
            sendUserName: this.model.sendUserName || this.$store.getters.userInfo.realname,
            lineId: this.model.lineId,
            itemNo: this.model.itemNo,
            remark: this.model.remark,
            trainIndex: this.model.trainIndex,
          })
        }else{
          this.formCopy = JSON.parse(JSON.stringify(this.model))
          console.log(this.formCopy)
        }
        this.sendDeptId = this.model.sendDeptId
        this.sendUserId = this.model.sendUserId || this.$store.getters.userInfo.id
        // 判断是否默认设置部门
        if (!this.model.sendDeptId) {
          getDepartTree().then((res) => {
            if (res.success) {
              treeForeach(res.result, this.$store.getters.userInfo.departId, (data) =>{
                treeForeach(res.result, this.$store.getters.userInfo.departId, (data) =>{
                  this.form.setFieldsValue({
                    sendDeptName: data.title,
                  })
                  this.sendDeptId = data.id
                })
              })

              function treeForeach (tree, departId, func) {
                tree.forEach(data => {
                  if (data.key === departId) {
                    func(data)
                    return
                  }
                  data.children && treeForeach(data.children, departId, func) // 遍历子树
                })
              }
            }
          })
        }
      })
    },
    handleOk() {
      const that = this
      return new Promise((resolve, reject) => {
        that.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            let formData = Object.assign(that.model, values, {
              acceptDate: moment(values.acceptDate).format('YYYY-MM-DD'),
              planFinishDate: moment(values.planFinishDate).format('YYYY-MM-DD'),
              tradeType: 1,
              sendDeptId: this.sendDeptId,
              sendUserId: this.sendUserId,
            })
            let obj
            if (!that.model.id) {
              let id = UUID_V4
              formData['id'] = id
              this.model['id'] = id
              obj = addExchange(formData)
            } else {
              obj = editExchange(formData)
            }
            // 开口整改项
            formData.leaveList = this.receiveCarOpenItemData
            formData.rectifyList = this.receiveCarRectifyData

            obj
              .then((res) => {
                if (res.success) {
                  resolve(res.message)
                } else {
                  reject(res.message)
                }
              })
              .finally(() => {
                that.confirmLoading = false
              })
          } else {
            reject('请先填写完接车信息!')
          }
        })
      })
    },
    save() {
      const that = this
      return new Promise((resolve, reject) => {
        that.form.validateFields((err, values) => {
          if (!err) {
            that.confirmLoading = true
            let formData = Object.assign(that.model, values, {
              acceptDate: moment(values.acceptDate).format('YYYY-MM-DD'),
              planFinishDate: moment(values.planFinishDate).format('YYYY-MM-DD'),
              tradeType: 1,
              sendDeptId: this.sendDeptId,
              sendUserId: this.sendUserId,
            })
            let obj
            if (!that.model.id) {
              let id = UUID_V4
              formData['id'] = id
              this.model['id'] = id
              obj = addExchange(formData)
            } else {
              obj = editExchange(formData)
            }
            // 开口整改项
            formData.leaveList = this.receiveCarOpenItemData
            formData.rectifyList = this.receiveCarRectifyData

            obj
              .then((res) => {
                if (res.success) {
                  resolve(res.message)
                } else {
                  reject(res.message)
                }
              })
              .finally(() => {
                that.confirmLoading = false
              })
          } else {
            reject('请先填写完接车信息!')
          }
        })
      })
    },
  },
}