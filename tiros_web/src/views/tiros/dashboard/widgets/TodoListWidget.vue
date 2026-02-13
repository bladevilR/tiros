<template>
  <div style="height: 100%">
    <vxe-table
      border="none"
      ref="listTable"
      align="center"
      :data="filterList"
      show-overflow="tooltip"
      :edit-config="{ trigger: 'manual', mode: 'row' }"
      max-height="100%"
    >
      <vxe-table-column field="processInstanceName" title="业务名称" align="left">
        <template v-slot="{row}">
          <a @click.stop="approve(row)">{{formatName(row)}}</a>
        </template>
      </vxe-table-column>
      <vxe-table-column field="name" title="任务名称"  align="left"></vxe-table-column>
      <vxe-table-column field="preAssigneeName" title="提交人" width="80px"></vxe-table-column>
      <vxe-table-column field="startUser" title="发起人" width="80px"></vxe-table-column>
      <vxe-table-column
        field="createTime"
        title="开始时间"
        :formatter="formatTime"
        align="center"
      ></vxe-table-column>
    </vxe-table>
    <a-modal
      centered
      :width="'100%'"
      :title="title || '流程办理'"
      dialogClass="fullDialog no-footer"
      :visible="handlerVisible"
      @cancel="handlerVisible = false"
      :footer="null"
      :destroyOnClose="true"
      v-if="handlerVisible"
    >
      <taskHandler
        :show.sync="handlerVisible"
        :task-id="curTask.id"
        v-if="curTask && curInstance && !!curTask.id && !!curInstance.id && !!curInstance.processDefinitionId"
        :process-instance-id="curInstance.id"
        :process-definition-id="curInstance.processDefinitionId"
        @handleSuccess="handleSuccess"
      ></taskHandler>
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
import TaskHandler from '@/views/workflow/TaskHandler2'

export default {
  name: 'TodoListWidget',
  components: { TaskHandler },
  props: {
    autoRefresh: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      handlerVisible: false,
      selfHandlerVisible: false,
      currentComponent: null,
      title: '',
      dataSource: [],
      todoList: [],
      filterList: [],
      curTask: null,
      curInstance: null,
    }
  },
  created() {
    this.loadTodo()
  },
  methods: {
    loadTodo() {
      if (!this.$workflowApi || typeof this.$workflowApi.listTodo !== 'function') {
        this.todoList = []
        this.filterList = []
        return
      }
      this.$workflowApi
        .listTodo({ taskName: '' })
        .then((res) => {
          if (res.success) {
            this.todoList = res.result
            this.filterList = [...this.todoList]
          }
        })
        .catch((err) => {
          console.error('加载当前人员待办异常：', err)
        })
    },
    approve(row) {
      this.curTask = row
      if(this.curTask.configs && this.curTask.configs.approveForm){
        //
        let form=this.curTask.configs.approveForm;
        if (form.formType === 'COMPONENT') {
          let url = form.formTarget
          const rg = new RegExp('^/*')
          url = url.replace(rg, '')
          this.currentComponent = resolve => require(['@/' + url + '.vue'], resolve)
        }
        this.selfHandlerVisible=true;
      } else {
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
    },
    handleSuccess(param) {
      this.handlerVisible = false;
      this.loadTodo();
    },
  },
  // 离开路由之前执行
  beforeRouteLeave(to, from, next) {
    // clearInterval(this.timer)
    next()
  },
}
</script>

<style scoped>
</style>
