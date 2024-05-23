<template>
  <div style="height: calc(100% - 0px)">
    <!-- 查询区域 -->
    <div class="table-page-search-wrapper">
        <a-row :gutter="24">
          <a-col :md="6" :sm="8">
            <span style="float: left;overflow: hidden;" class="table-page-search-submitButtons">
              <a-space>
                <a-button type="primary" @click="handleAdd" >新增</a-button>
                <a-button  @click="deleteRecord">删除</a-button>
              </a-space>
            </span>
          </a-col>
        </a-row>
    </div>
     <div  style="height: calc(100vh - 185px)">
      <vxe-table
        border
        max-height="100%"
        style="height: calc(100vh - 235px)"
        ref="listTable"
        :align="allAlign"
        :data.sync="certList"
        show-overflow="tooltip"
        :keep-source="true"
        :checkbox-config="{trigger: 'row', highlight: true }"
        :edit-config="{key: 'id', trigger: 'manual', mode: 'row', autoClear: false, showStatus: true}"
      >
        <vxe-table-column type="checkbox" width="40"></vxe-table-column>
        <vxe-table-column field="certNo" title="证件编号" min-width="80" :edit-render="{name: 'input'}"></vxe-table-column>
        <vxe-table-column field="certName" title="证件名称" min-width="100" align="left" header-align="center" :edit-render="{name: 'input'}"></vxe-table-column>
        <vxe-table-column field="certCategory_dictText" title="证件类型" width="140" :edit-render="{name: 'input'}">
          <template v-slot:edit="{row}">
            <j-dict-select-tag style="width: 100%" :triggerChange="true" v-model="row.certCategory" dictCode="bu_user_cert_category" @change="onCategoryChange"/>
          </template>
        </vxe-table-column>
        <vxe-table-column field="validDate" title="证件有效期" min-width="100" :edit-render="{name: 'input'}">
          <template v-slot:edit="{ row }">
            <a-date-picker v-model="row.validDate" style="width: 100%" placeholder="请选择有效期"  format="YYYY-MM-DD"></a-date-picker>
          </template>
        </vxe-table-column>
        <vxe-table-column field="scanFile" title="扫描件" min-width="120"></vxe-table-column>
        <vxe-table-column field="remark" title="备注" align="left" header-align="center" :edit-render="{name: 'input'}"></vxe-table-column>
        <vxe-table-column title="操作" width="120" fixed="right">
          <template v-slot="{ row }">
            <template v-if="$refs.listTable.isActiveByRow(row)">
              <a-space>
                <a-button type="dashed" size="small" @click.stop="saveRowEvent(row)">保存</a-button>
                <a-button type="dashed" size="small" @click.stop="cancelRowEvent(row)">取消</a-button>
              </a-space>
            </template>
            <template v-else>
              <a-button type="dashed" size="small" @click.stop="editRowEvent(row)">编辑</a-button>
            </template>
          </template>
        </vxe-table-column>
      </vxe-table>
      <vxe-pager
        perfect
        :current-page.sync="queryParam.currentPage"
        :page-size.sync="queryParam.pageSize"
        :total="totalResult"
        :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
      ></vxe-pager>
    </div>
    <!-- <track-item-modal ref="modalForm" @add="addTrack($event)" @edit="editTrack($event)"></track-item-modal> -->
  </div>
</template>

<script>
import { getUser } from '@api/tirosApi'
import moment from 'moment'
import { ajaxGetDictItems } from '@api/api'
import { randomUUID } from '@/utils/util'

export default {
  props:{
    certList: {
      type: Array,
      default: () => []
    },
    userId:{
      type:String,
      default: ''
    }
  },
  mounted () {
    this.totalResult=this.certList.length
    getUser({id:this.userId}).then((res)=>{
      if(res.success){
        this.$emit('update:certList', res.result.certList)
      }
    })
  },
  data() {
    return {
      curEditMode: 0, // 0 没有编辑，1 新增， 2 修改
      curRow: null,
      queryParam: {
        title: '',
        trainTime: '',
        userId: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center',
      validRules: {
        code: [
          { required: true, message: '请选择工器具' }
        ],
        amount: [
          { required: true, message: '数量必须填写' }
        ]
      }
    }
  },
  methods: {
    handleAdd() {
      if (this.$refs.listTable.getActiveRecord()) return
      this.curEditMode = 1
       let record = {
        id: randomUUID()
       }
      this.$refs.listTable.insertAt(record, -1)
        .then(({ row }) => {
          this.$refs.listTable.setActiveCell(row, 'certNo')
        })
    },
    editRowEvent (row) {
      this.curEditMode = 2
      this.$refs.listTable.setActiveRow(row)
    },
    delRowEvent (row) {
      this.certList.map((item2, index2) => {
        if (row.id === item2.id) {
          this.certList.splice(index2, 1)
          this.$emit('update:certList', this.certList)
        }
      })
    },
    saveRowEvent (row) {
      this.$refs.listTable.validate(row, valid => {
        if (!valid) {
          if(this.curEditMode === 1){
            row['validDate']=moment(row.validDate).format('YYYY-MM-DD')
            ajaxGetDictItems('bu_user_cert_category').then((res)=>{
              if(res.success){
                res.result.map((item)=>{
                  if(item.value===row.trainType){
                    row['certCategory_dictText']=item.title
                  }
                })
                this.certList.push(row)
                this.$emit('update:certList', this.certList)
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

    selectAllEvent({ checked, records }) {
      console.log(checked ? '所有勾选事件' : '所有取消事件', records)
    },
    selectChangeEvent({ checked, records }) {
      console.log(checked ? '勾选事件' : '取消事件', records)
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          onOk: () => {
            // let temp = this.certList.filter(item => {
            //   let idList = selectRecords.map(s => s.id)
            //   return !idList.includes(item.id)
            // })
            this.certList.map((item,index) => {
              if(selectRecords.find((item2) => item.id == item2.id)){
                this.certList.splice(index,1)
              }
            });
            this.$emit('update:certList', this.certList)
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    onCategoryChange(value, option){
      let {row} = this.$refs.listTable.getActiveRecord()
      row.certCategory_dictText = option.title
    }
  }
}
</script>

<style>
</style>