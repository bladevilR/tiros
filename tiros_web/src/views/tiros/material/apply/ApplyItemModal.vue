<template>
  <a-modal
    :title="title"
    :width="'80%'"
    :bodyStyle="{height:'70vh'}"
    centered
    :visible="visible"
    :destroyOnClose="true"
    :confirmLoading="confirmLoading"
    @ok="handleOk"
    @cancel="handleCancel"
    cancelText="关闭">
    <a-tabs default-active-key="1">
      <a-tab-pane key="1" tab="基本信息">
        <a-spin :spinning="confirmLoading">
          <a-form :form="form">
            <a-row :gutter="24">
<!--              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="领料编码">
                  <a-input placeholder="请输入领料编码" v-decorator.trim="[ 'code']"/>
                </a-form-item>
              </a-col>-->
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="领料单名称">
                  <a-input placeholder="请输入领料单名称" v-decorator.trim="['title',validatorRules.title]"/>
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="申请时间">
                  <a-date-picker  v-decorator.trim="[ 'applyDate',validatorRules.applyDate]"/>
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="申请人">
                  <div @click.stop="showUserModal()">
                    <a-select v-decorator.trim="[ 'userName', validatorRules.userId]" placeholder="请选择人员" :open="false" >
                      <a-icon slot="suffixIcon" type="ellipsis" @click.stop="showUserModal()"/>
                    </a-select>
                  </div>
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="申请班组">
                  <j-dict-select-tag
                    v-decorator.trim="[ 'deptId', validatorRules.deptId]"
                    :triggerChange="true"
                    placeholder="请选择"
                    :dictCode="dictGroupStr"
                    @change="onGroupChange"
                  />
                </a-form-item>
              </a-col>
            </a-row>
            <a-row :gutter="24">
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="关联工单">
                  <div @click.stop="showOrderModal()">
                  <a-select v-decorator.trim="[ 'workOrderName']" placeholder="请选择工单" :open="false" >
                    <a-icon slot="suffixIcon" type="ellipsis" @click.stop="showOrderModal()"/>
                  </a-select>
                  </div>
<!--                  <a-input placeholder="请选择工单" v-decorator.trim="[ 'workOrderId']"/>-->
                </a-form-item>
              </a-col>
<!--              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="领料人">
                  <j-dict-select-tag
                    :triggerChange="true"
                    placeholder="请选择"
                    dictCode="sys_user,username,id"
                    v-decorator.trim="[ 'receiveUserId',validatorRules.receiveUserId]"
                  />
                </a-form-item>
              </a-col>-->
            </a-row>

            <!--                      <a-row :gutter="24">
        <a-col :md="12" :sm="24">
                        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="领料班组">
                          <j-dict-select-tag
                            :triggerChange="true"
                            placeholder="请选择"
                            dictCode="bu_mtr_workshop_group,group_name,id"
                            v-decorator.trim="[ 'receiveGroupId',validatorRules.receiveGroupId]"
                          />
                        </a-form-item>
                      </a-col>
                      <a-col :md="12" :sm="24">
                        <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="领用日期">
                          <a-date-picker  v-decorator.trim="[ 'receiveTime']"/>
                        </a-form-item>
                      </a-col>
            </a-row>-->
            <a-row :gutter="24">
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="线路">
<!--                  <j-dict-select-tag
                    v-decorator.trim="[ 'lineId', validatorRules.lineId]"
                    :triggerChange="true"
                    placeholder="请选择线路"
                    dictCode="bu_mtr_line,line_name,line_id"
                  />-->

                  <line-select-list
                    v-decorator="['lineId', validatorRules.lineId]"
                    @change="changeLine">
                  </line-select-list>
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="车辆">
                  <j-dict-select-seach-tag
                    v-decorator.trim="[ 'trainNo', validatorRules.trainNo]"
                    :triggerChange="true"
                    placeholder="请选择"
                    :dictCode="dictTrainStr"
                  />
                </a-form-item>
              </a-col>
            </a-row>

<!--            <a-row :gutter="24">
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属车间">
                  <j-dict-select-tag
                    v-decorator.trim="[ 'workshopId', validatorRules.workshopId]"
                    :triggerChange="true"
                    placeholder="请选择"
                    dictCode="bu_mtr_workshop,name,id"
                  />
                </a-form-item>
              </a-col>
              <a-col :md="12" :sm="24">
                <a-form-item :labelCol="labelCol" :wrapperCol="wrapperCol" label="所属车辆段">
                  <j-dict-select-tag
                    v-decorator.trim="[ 'depotId', validatorRules.depotId]"
                    :triggerChange="true"
                    placeholder="请选择"
                    dictCode="bu_mtr_depot,name,id"
                  />
                </a-form-item>
              </a-col>
            </a-row>-->
           </a-form>
        </a-spin>

      </a-tab-pane>
      <a-tab-pane key="2" tab="物资信息" force-render>
        <a-spin :spinning="confirmLoading">
          <a-row :gutter="24">
            <a-col :md="12" :sm="24">
            <span style="float: left;overflow: hidden;margin-bottom: 8px;" class="table-page-search-submitButtons">
              <a-button type="primary" @click="handleAdd" style="margin-left: 8px">新增</a-button>
              <a-button style="margin-left: 8px" @click="deleteRecord">删除</a-button>
            </span>
            </a-col>
          </a-row>
          <vxe-table
            border
            ref="listTable"
            :align="allAlign"
            :data="tableData"
            show-overflow="tooltip"
            :edit-config="{trigger: 'manual', mode: 'row'}"
            :edit-rules="validRules"
          >
            <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
            <vxe-table-column field="materialTypeCode" title="物资编码" width="100" fixed="left"></vxe-table-column>
            <vxe-table-column field="materialTypeName" title="物资名称" min-width="150" align="left"></vxe-table-column>
            <vxe-table-column field="materialTypeSpec" title="规格型号" min-width="200"></vxe-table-column>
            <vxe-table-column field="materialTypeUnit" title="单位" width="100"></vxe-table-column>
            <vxe-table-column field="amount" title="领用数量" width="100"
                              :edit-render="{name: '$input', props: {type: 'number',min: 0}}">
            </vxe-table-column>
<!--            <vxe-table-column field="receive" title="发料数量" width="10%"
                              :edit-render="{name: '$input', props: {type: 'number',min: 0}}">
            </vxe-table-column>-->
            <vxe-table-column field="warehouseName" title="库位" width="100"></vxe-table-column>
<!--            <vxe-table-column field="palletName" title="存放托盘" width="10%"
                              :edit-render="{name: 'select', options: palletList, props: {multiple: false}}"
            ></vxe-table-column>-->
            <vxe-table-column field="remark" title="备注" width="100"></vxe-table-column>
            <vxe-table-column title="操作" width="150" fixed="right">
              <template v-slot="{ row }">
                <template v-if="$refs.listTable.isActiveByRow(row)">
                  <a-space>
                    <a-button size="small" @click.stop="saveMaterial(row)">保存</a-button>
                    <a-button size="small" @click.stop="cancelMaterial(row)">取消</a-button>
                  </a-space>
                </template>
                <template v-else>
                  <a-button size="small" @click.stop="editMaterial(row)">编辑</a-button>
                </template>
              </template>
            </vxe-table-column>
          </vxe-table>
          <material-list ref="materialForm" :multiple="true" @ok="addTarget"></material-list>
          <work-order-select ref="orderSelectModal" :multiple="false" @ok="onOrderSelect"></work-order-select>
          <user-list ref="userSelectModal" :multiple="false" @ok="onUserSelect"></user-list>
        </a-spin>
      </a-tab-pane>
    </a-tabs>
  </a-modal>
</template>

<script>
  import {
    addApply, delApply, editApply,
  } from '@api/tirosMaterialApi'
  import MaterialList from '@views/tiros/common/selectModules/MaterialList'
  import { ajaxGetDictItems } from '@api/api'
  import moment from 'moment'
  import WorkOrderSelect from '@views/tiros/common/selectModules/WorkOrderSelect'
  import UserList from '@views/tiros/common/selectModules/UserList'
  import LineSelectList from '@views/tiros/common/selectModules/LineSelectList'

  export default {
    name: 'ApplyItemModal',
    components: { MaterialList, WorkOrderSelect, UserList,LineSelectList },
    data() {
      return {
        dictTrainStr: '',
        dictGroupStr: 'bu_mtr_workshop_group,group_name,id,workshop_id=\'' + this.$store.getters.userInfo.workshopId + '\'|workshop_id',
        tableData: [],
        palletList: [],
        state: true,
        title: '操作',
        visible: false,
        model: {},
        allAlign: 'center',
        labelCol: {
          xs: { span: 24 },
          sm: { span: 7 }
        },
        wrapperCol: {
          xs: { span: 24 },
          sm: { span: 15 }
        },
        confirmLoading: false,
        form: this.$form.createForm(this),
        validatorRules: {
          title: { rules: [{ required: true, message: '请输入领用单标题!' }] },
          // receiveUserId: { rules: [{ required: true, message: '请选择领用人!' }] },
          // receiveGroupId: { rules: [{ required: true, message: '请选择领用工班!' }] },
          lineId: { rules: [{ required: true, message: '请选择线路!' }] },
          trainNo: { rules: [{ required: true, message: '请选择车辆!' }] },
          applyDate: { rules: [{ required: true, message: '请选择申请日期!' }] },
          // workshopId: { rules: [{ required: true, message: '请选择车间!' }] },
          // depotId: { rules: [{ required: true, message: '请选择车辆段!' }] },
          deptId: { rules: [{ required: true, message: '请选择申请部门!' }] },
          userId: { rules: [{ required: true, message: '请选择申请人!' }] },
          userName: { rules: [{ required: true, message: '请选择申请人!' }] }
        },
        validRules: {
          reason: [
            { required: true, message: '请输入申请数量' }
          ]
        },
        dictCode: 'bu_material_pallet,name,id'
      }
    },
    created() {
      this.initDictData()
    },
    methods: {
      saveMaterial(row){
        this.$refs.listTable.clearActived()
      },
      cancelMaterial(row){
        this.$refs.listTable.clearActived()
      },
      editMaterial(row){
        this.$refs.listTable.setActiveRow(row)
      },
      unique(arr) {
        const res = new Map()
        return arr.filter((arr) => !res.has(arr.materialTypeId) && res.set(arr.materialTypeId, 1))
      },
      add() {
        this.edit({
          workOrderId: undefined,
        })
      },
      edit(record) {
        this.form.resetFields()
        this.tableData=[]
        this.model = Object.assign({}, record)
        this.visible = true
        if(this.model.detailList&&this.model.detailList.length>0){
          this.model.detailList.forEach((t)=>{
           t.palletName=t.palletId
           this.tableData.push(t)
         })
        }
        this.$nextTick(() => {
          this.form.setFieldsValue({
            // code: this.model.code,
            title: this.model.title,
            // workOrderId: this.model.workOrderId,
            // receiveUserId: this.model.receiveUserId,
            // receiveGroupId: this.model.receiveGroupId,
            // receiveTime: moment(this.model.receiveTime || undefined),
            lineId: this.model.lineId,
            trainNo: this.model.trainNo,
            userId: this.model.userId,
            userName: this.model.userName,
            deptId: this.model.deptId,
            // depotId: this.model.depotId,
            applyDate: moment(this.model.applyDate || undefined),
            // workshopId: this.model.workshopId,
          })
        })

      },
      // 确定
      handleOk() {
        const that = this
        that.form.validateFields((err, values) => {
          if (!err) {
            // that.confirmLoading = true
            let formData = Object.assign(that.model, values,{
              receiveTime: moment(values.receiveTime).format('YYYY-MM-DD'),
              applyDate: moment(values.applyDate).format('YYYY-MM-DD')})
            let detailList=[]
            this.tableData.forEach((t)=>{
              t.palletId=t.palletName
              t.palletName=''
              detailList.push(t)
            })
            formData['detailList']=detailList

            console.log('save data:', formData)

            let obj
            if (!that.model.id) {
              obj = addApply(formData)
            } else {
              obj = editApply(formData)
            }
            obj.then((res) => {
                if (res.success) {
                  that.$message.success(res.message)
                  that.$emit('ok')
                } else {
                  that.$message.warning(res.message)
                }
              }).finally(() => {
                that.confirmLoading = false
                that.close()
              })
            this.visible = false
          }
        })
      },
      // 关闭
      handleCancel() {
        this.close()
      },
      close() {
        this.$emit('close')
        this.visible = false
      },
      handleAdd() {
        this.$refs.materialForm.showModal()
      },
      deleteRecord() {
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        if (selectRecords.length > 0) {
          this.$confirm({
            content: `是否删除选中${selectRecords.length}数据？`,
            onOk: () => {
              if(this.model.detailList){
                let idsStr = selectRecords
                  .map(function(obj, index) {
                    return obj.id
                  }).join(',')
                delApply('ids=' + idsStr).then((res) => {
                  this.findList()
                  this.$message.success(res.message)
                })
              }

               this.tableData=this.tableData.filter(item=>{!selectRecords.includes(item)})

            }
          })
        } else {
          this.$message.error('尚未选中任何数据!')
        }
      },

      addTarget(data) {
        console.log('data' + data)
        if (data) {
          data.map(d => {
            this.tableData = this.tableData.concat(this.getMaterial(d))
          })
          this.tableData=this.unique(this.tableData)
        }
      },

      getMaterial(item) {
        return {
          materialTypeCode: item.code,
          materialTypeName: item.name,
          materialTypeSpec: item.spec,
          materialTypeUnit: item.unit,
          materialTypeId:item.id,
          amount: item.num,
          receive: null,
          remark: '',
          palletId: '',
          palletName:'',
        }
      },
      initDictData() {
          ajaxGetDictItems(this.dictCode, null).then((res) => {
            if (res.success) {
                if(res.result){
                  res.result.map(item=>{
                    this.palletList=this.palletList.concat({
                      'label':item.text,
                      'value':item.value
                    })
                  })
                }
            }
          })
      },
      changeLine(data) {
        if (data) {
          this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + data
        } else {
          this.dictTrainStr = ''
        }
      },

      showUserModal() {
        this.$refs.userSelectModal.showModal()
      },
      onUserSelect(data) {
        if (data && data.length > 0) {
          this.$nextTick(() => {
            this.form.setFieldsValue({
              userName:  data[0].realname
            })
          })
          this.model.userId= data[0].id
        }
      },
      showOrderModal () {
        this.$refs.orderSelectModal.showModal()
      },
      onOrderSelect (data) {
        // orderName, id, lineId, trainNo
        if (data && data.length > 0) {
          this.model.workOrderId = data[0].id
          this.$nextTick(() => {
            if (data[0].lineId) {
              this.dictTrainStr = 'bu_train_info,train_no,train_no,line_id=' + data[0].lineId
            }
            this.form.setFieldsValue({
              workOrderName: data[0].orderName,
              lineId: data[0].lineId,
              trainNo: data[0].trainNo
            })
          })
        }

        //console.log('order:', data)
      },
      onGroupChange (value,data) {
        if (data.extFields && data.extFields.workshop_id) {
          this.model.workshopId = data.extFields.workshop_id
          this.model.depotId = window._CONFIG['depotId']
        }
      }
    }
  }
</script>