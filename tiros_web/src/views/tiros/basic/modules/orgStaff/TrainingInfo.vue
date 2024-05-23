<template>
  <div>
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
      <a-form layout="inline">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="培训名称">
              <a-input placeholder="请输入名称" v-model="queryParam.title" allowClear></a-input>
            </a-form-item>
          </a-col>
          <a-col :md="5" :sm="24">
            <a-form-item label="培训时间">
              <a-date-picker  v-model="queryParam.trainTime" />
            </a-form-item>
          </a-col>
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-button @click="searchQuery">查询</a-button>
              <a-button type="primary" @click="handleAdd" style="margin-left: 8px">新增</a-button>
              <!-- <a-button style="margin-left: 8px">修改</a-button> -->
              <a-button style="margin-left: 8px" @click="deleteRecord">删除</a-button>
            </span>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 185px)">
      <vxe-table
        border
        ref="listTable"
        max-height="100%"
        style="height: calc(100vh - 235px)"
        :align="allAlign"
        :data="trainingList"
        :edit-rules="validatorRules"
        show-overflow="tooltip"
        :keep-source="true"
        :checkbox-config="{trigger: 'row', highlight: true }"
        :edit-config="{key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true}"
      >
        <vxe-table-column type="checkbox" width="60"/>
        <vxe-table-column field="title" title="培训名称" min-width="160" align="left" header-align="center"
                          :edit-render="{name: 'input' }">
        </vxe-table-column>
        <vxe-table-column field="trainType_dictText" title="培训类型" min-width="120"
                          :edit-render="{name: 'input'}">
          <template v-slot:edit="{row}">
            <j-dict-select-tag style="width: 100%" :triggerChange="true" v-model="row.trainType" dictCode="bu_train_type" />
          </template>
        </vxe-table-column>
        <vxe-table-column field="address" title="培训地点" min-width="160" :edit-render="{name: 'input'}"/>
        <vxe-table-column field="teacher" title="讲师"   min-width="120" :edit-render="{name: 'input'}"/>
        <vxe-table-column field="trainTime" title="培训时间" min-width="120" :edit-render="{name: 'input'}">
          <template v-slot:edit="{ row }">
            <a-date-picker v-model="row.trainTime" style="width: 100%" placeholder="请选择培训时间"  format="YYYY-MM-DD"></a-date-picker>
          </template>
        </vxe-table-column>
        <vxe-table-column field="timeLength" title="课时" width="120" :edit-render="{name: 'input'}">
          <template v-slot:edit="{ row }">
            <a-input-number v-model="row.timeLength" :defaultValue="1" :min="1" :max="10000" style="width: 100%" />
          </template>
        </vxe-table-column>
        <vxe-table-column field="remark" title="备注" min-width="160" align="left" header-align="center" :edit-render="{name: 'input'}"/>
        <vxe-table-column title="操作" width="120" fixed="right">
          <template v-slot="{ row }">
            <template v-if="$refs.listTable.isActiveByRow(row)">
                <a-button type="dashed" size="small" @click.stop="saveRowEvent(row)">保存</a-button>
                <a-button style="margin-left: 2px" type="dashed" size="small" @click.stop="cancelRowEvent(row)">取消</a-button>
            </template>
            <template v-else>
              <a-button type="dashed" size="small" @click.stop="editRowEvent(row)">编辑</a-button>
            </template>
          </template>
        </vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect

        :current-page.sync="queryParam.pageNo"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
      ></vxe-pager>
    </div>
    </div>
</template>

<script>
import { getUser, pageTraining } from '@api/tirosApi'
import { randomUUID } from '@/utils/util'
import moment from 'moment'
import { ajaxGetDictItems } from '@api/api'

export default {
  name: 'TrainingInfo',
  props: {
   /* trainingList:{
      type: Array,
      default: () => []
    },*/
    userId:{
      type:String,
      default: ''
    }
  },
  data() {
    return {
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      form: this.$form.createForm(this),
      queryParam: {
        title: '',
        trainTime: '',
        userId: '',
        pageNo: 1,
        pageSize: 10
      },
      trainingList:[],
      totalResult: 0,
      allAlign: 'center',
      validatorRules: {
        title: [{ required: true, message: '请输入培训名称!' },{ max:32, message: '输入长度不能超过32个字符!'} ],
        trainType_dictText: [{ required: true, message: '请选择培训类型!' }] ,
        address: [{ required: true, message: '请输入培训地点!' },{ max:64, message: '输入长度不能超过64个字符!'}],
        trainTime:[{ required: true, message: '请选择培训时间!' }],
        remark:[{ max:200, message: '备注长度不能超过200个字符!'}],
        teacher:[{ max:32, message: '备注长度不能超过32个字符!'}],
      },
    }
  },
  mounted () {
    getUser({id:this.userId}).then((res)=>{
      if(res.success){
         // this.$emit('update:trainingList', res.result.trainingList)
        this.trainingList=res.result.trainingList
        this.totalResult=res.result.trainingList.length
        this.$emit('trainingListMethod',  this.trainingList)
      }
    })
    this.$forceUpdate()
  },
  methods: {
    editRowEvent (row) {
      this.curEditMode = 2
      this.$refs.listTable.setActiveRow(row)
    },
    delRowEvent (row) {
      this.taskTools.map((item2, index2) => {
        if (row.id === item2.id) {
          this.trainingList.splice(index2, 1)
          this.$emit('update:trainingList', this.trainingList)
        }
      })
    },
    saveRowEvent (row) {
      row['trainType_dictText']=row.trainType
      this.$refs.listTable.validate(row, valid => {
        if (!valid) {
          if(this.curEditMode === 1){
            row['trainTime']=moment(row.trainTime).format('YYYY-MM-DD')
            ajaxGetDictItems('bu_train_type').then((res)=>{
                  if(res.success){
                    res.result.map((item)=>{
                      if(item.value===row.trainType){
                        row['trainType_dictText']=item.title
                      }
                    })
                    this.trainingList.push(row)
                   // this.$emit('update:trainingList', this.trainingList)
                    this.$emit('trainingListMethod',  this.trainingList)
                  }
            })
          }
          this.$refs.listTable.clearActived()
          this.curEditMode = 0
        } else {
          for (const validKey in valid) {
            let vals = valid[validKey]
            vals.forEach(item => {
              if (item.rule) {
                this.$message.error(item.rule.message)
              }
            })
          }
        }
      })
    },
    cancelRowEvent (row) {
      this.$refs.listTable.clearActived()
      if (this.curEditMode === 1) {
        // 新增,点击取消
        this.$refs.listTable.remove(row)
      } else if (this.curEditMode === 2) {
        // 还原行数据
        this.$refs.listTable.revertData(row)
      }
      this.curEditMode = 0
    },
    handleAdd() {
      if (this.$refs.listTable.getActiveRecord()) {
        return
      }
      this.curEditMode = 1
      let record = {
        id: randomUUID()
      }
      this.$refs.listTable.insertAt(record, -1)
        .then(({ row }) => {
          this.$refs.listTable.setActiveCell(row, 'title')
        })
    },
    searchTrainingPage() {
      this.queryParam.trainTime=this.queryParam.trainTime?moment(this.queryParam.trainTime).format('YYYY-MM-DD'):''
      pageTraining(this.queryParam).then((res) => {
        if (res.success) {
          this.totalResult = res.result.total
         // this.$emit('update:trainingList', res.result.records)
          this.trainingList=res.result.records
        }
      })
    },
    searchQuery() {
      this.queryParam.userId = this.userId
      this.searchTrainingPage()
    },
    searchReset() {
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            let idList = selectRecords.map(s => s.id)
            this.trainingList=this.trainingList.filter(item => !idList.includes(item.id))
           /* let temp = this.trainingList.filter(item => {
              let idList = selectRecords.map(s => s.id)
              console.log(idList, item.id, idList.includes(item.id))
              return !idList.includes(item.id)
            })*/
           // this.$emit('update:trainingList',  this.trainingList)
            this.$emit('trainingListMethod',  this.trainingList)
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
  }
}
</script>

<style>
</style>