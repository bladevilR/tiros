<template>
  <div>
    <div class="table-page-search-wrapper">
      <a-form layout="inline" @keyup.enter.native="loadTodo">
        <a-row :gutter="24">
          <a-col :md="6" :sm="24">
            <a-form-item label="搜索">
              <a-input placeholder="请输入业务名称" v-model="queryParam.title" allow-clear></a-input>
            </a-form-item>
          </a-col>

          <a-col :md="6" :sm="8">
            <span style="float: left; overflow: hidden" class="table-page-search-submitButtons">
              <a-space>
                <a-button @click="loadTodo">查询</a-button>
                <a-button :disabled="records.length != 1" @click.stop="approve(records[0])">处理</a-button>
              </a-space>
            </span>
          </a-col>
          <a-col :md="6" :sm="8">
            <div style=" width:100%; margin:auto;padding-top: 5px; font-weight: bold; color: #cc0000" >
            你共计有：{{this.todoList.length}} 条待办处理
            </div>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div style="height: calc(100vh - 180px)">
    <vxe-table
      border
      stripe
      ref="xTable"
      max-height="100%"
      show-header-overflow
      show-overflow
      highlight-hover-row
      highlight-current-row
      @checkbox-change="checkboxChange"
      @checkbox-all="checkboxChange"
      :checkbox-config="{trigger: 'row', highlight: true, range: true}"
      :data="filterList">
      <vxe-table-column type="seq" width="40"></vxe-table-column>
      <vxe-table-column align="center" type="checkbox" width="40"></vxe-table-column>
      <vxe-table-column field="processInstanceName" title="业务名称" min-width="250">
        <template v-slot="{row}">
          <div>
            <a @click.stop="viewDetail(row)">{{formatName(row)}}</a>
          </div>
        </template>
      </vxe-table-column>
      <vxe-table-column field="name" title="任务名称" width="180"></vxe-table-column>
      <vxe-table-column field="preAssigneeName" title="提交人" width="80px"></vxe-table-column>
<!--      <vxe-table-column field="dueDate" title="到期日期" width="150" :formatter="formatTime" align="center"></vxe-table-column>-->
      <vxe-table-column field="startUser" title="发起人" width="150" align="center"></vxe-table-column>
      <vxe-table-column field="createTime" title="发起时间" width="150" :formatter="formatTime" align="center"></vxe-table-column>
      <!--      <vxe-table-column field="owner" title="所有人"></vxe-table-column>-->
<!--      <vxe-table-column field="assignee" title="执行人" width="150" align="center"></vxe-table-column>-->
    </vxe-table>
    </div>
    <a-modal
      centered
      :width="'100%'"
      title="流程查看"
      dialogClass="fullDialog no-footer"
      :visible="detailVisible"
      @cancel="detailVisible=false"
      :footer="null"
      :destroyOnClose="true"
      v-if="detailVisible"
    >
      <ProcessInstanceDetail v-if="curTask && curInstance"  :processInstance="curInstance"></ProcessInstanceDetail>
    </a-modal>
    <a-modal
      centered
      :width="'100%'"
      :title="title||'流程办理'"
      dialogClass="fullDialog no-footer"
      :visible="handlerVisible"
      @cancel="handlerVisible=false"
      :footer="null"
      :destroyOnClose="true"
      v-if="handlerVisible"
    >
      <taskHandler  :show.sync="handlerVisible" :task-id="curTask.id" v-if="curTask && curInstance && !!curTask.id && !!curInstance.id && !!curInstance.processDefinitionId" :process-instance-id="curInstance.id" :process-definition-id="curInstance.processDefinitionId" @handleSuccess="handleSuccess"></taskHandler>
    </a-modal>
    <!-- 自定义流程处理界面 -->
    <a-modal
      centered
      :width="'100%'"
      dialogClass="fullDialog no-title no-footer"
      :visible="selfHandlerVisible"
      @cancel="selfHandlerVisible=false"
      :footer="null"
      :destroyOnClose="true"
      v-if="selfHandlerVisible"
    >
      <!--    <taskHandler :show.sync="handlerVisible" :task-id="curTask.id"
                       v-if="!!curTask.id && curInstance && !!curInstance.id && !!curInstance.processDefinitionId"
                       :process-instance-id="curInstance.id" :process-definition-id="curInstance.processDefinitionId"
                       @handleSuccess="handleSuccess"></taskHandler>-->
      <component ref="wfForm" v-bind:is="currentComponent" v-if="selfHandlerVisible" :businessKey="curTask.businessKey"  :fromFlow="true" @close="closeHandler" style="height: calc(100% - 10px); overflow-y: auto;"></component>
    </a-modal>
  </div>
</template>

<script>
import TaskHandler from "@/views/workflow/TaskHandler2";

export default {
  name: "ProcessTodo",
  components: {'taskHandler': TaskHandler},
  data() {
    return {
      records:[],
      handlerVisible: false,
      detailVisible: false,
      selfHandlerVisible: false,
      currentComponent: null,
      todoList: [],
      filterList: [],
      curTask: null,
      curInstance: null,
      title: '流程处理',
      queryParam: {
        title: ''
      }
    }
  },
  mounted() {
    this.loadTodo();
  },
  methods: {
    checkboxChange(e){
      this.records = e.records;
    },
    search () {
      this.records = [];
      if (this.queryParam.title) {
        this.filterList = this.todoList.filter(item => {
          return item.processInstanceName.indexOf(this.queryParam.title) > -1 || item.name.indexOf(this.queryParam.title) > -1  || item.startUser.indexOf(this.queryParam.title) > -1
        })
      } else {
        this.filterList = [...this.todoList]
      }
    },
    loadTodo() {
      this.$workflowApi.listTodo({ search: this.queryParam.title}).then(res => {
        if (res.success) {
          this.todoList = res.result;
          this.records = [];
          this.filterList = [...this.todoList]
        }
      }).catch(err => {
        console.error("加载当前人员待办异常：", err);
      });
    },
    approve(row) {
      this.curTask = row;
      if(this.curTask.configs && this.curTask.configs.approveForm){
        console.log(1)
        //
        let form=this.curTask.configs.approveForm;
        if (form.formType === 'COMPONENT') {
          let url = form.formTarget
          const rg = new RegExp('^/*')
          url = url.replace(rg, '')
          console.log(url)
          this.currentComponent = resolve => require(['@/' + url + '.vue'], resolve)
        }
        this.selfHandlerVisible=true;
      } else {
        console.log(2)
        this.curInstance = {'id': row.processInstanceId, 'processDefinitionId': row.processDefinitionId};
        this.title = this.curTask.name
        this.handlerVisible = true;
      }
    },
    closeHandler (status) {
      this.selfHandlerVisible=false
      if (status === "ok") {
        this.loadTodo()
      }
    },
    handleSuccess(param) {
      this.handlerVisible = false;
      this.loadTodo();
    },
    viewDetail(row) {
      this.curTask = row;
      this.curInstance = {'id': row.processInstanceId, 'processDefinitionId': row.processDefinitionId};
      this.detailVisible = true;
    },
    formatTime({ cellValue }) {
      if(cellValue) {
        return this.$moment(cellValue).format('YYYY-MM-DD HH:mm:ss');
      } else {
        return ''
      }
    },
    formatName (row) {
      let name = row.processInstanceName
      if (row.businessCode) {
        name += '-[' + row.businessCode + ']'
      }
      return name
    }
  }
}
</script>

<style scoped>

</style>
