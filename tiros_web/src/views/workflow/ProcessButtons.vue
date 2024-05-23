<template>
<span>
  <a-button class="margin-left8" v-if="buttons.start && canStart" @click="start" type="dashed">流程启动</a-button>
  <a-button class="margin-left8" v-if="buttons.handle && tasks.length == 1" @click="handle" type="dashed">流程办理</a-button>
  <a-dropdown class="margin-left8"  v-if="tasks.length > 1" >
    <a-menu slot="overlay" @click="handleMenuClick">
      <template v-for="t in tasks">
      <a-menu-item :key="t.id">
        <span>{{ t.name }}</span>
      </a-menu-item>
      </template>
    </a-menu>
    <a-button> 流程处理 <a-icon type="down" /> </a-button>
  </a-dropdown>
  <a-button class="margin-left8" v-if="buttons.view" @click="viewDetail" type="dashed">流程查看</a-button>
  <a-button class="margin-left8" v-if="buttons.cancel" @click="cancelProcess" type="dashed">终止流程</a-button>
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
    <ProcessInstanceDetail :processInstance="curInstance"></ProcessInstanceDetail>
  </a-modal>
  <!-- 通用流程处理界面 --->
  <a-modal
    centered
    :width="'100%'"
    :title="title||taskName"
    dialogClass="fullDialog no-footer"
    :visible="handlerVisible"
    @cancel="handlerVisible=false"
    :footer="null"
    :destroyOnClose="true"
    v-if="handlerVisible"
  >
    <taskHandler :show.sync="handlerVisible" :task-id="curTask.id" v-if="!!curTask.id && !!curInstance.id && !!curInstance.processDefinitionId" :process-instance-id="curInstance.id" :process-definition-id="curInstance.processDefinitionId" @handleSuccess="handleSuccess"></taskHandler>
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
      <component ref="wfForm" v-bind:is="currentComponent" v-if="selfHandlerVisible" :businessKey="businessKey" @close="closeHandler"  :fromFlow="true" style="height: calc(100% - 10px); overflow-y: auto;"></component>
  </a-modal>
</span>
</template>

<script>
import TaskHandler from "@/views/workflow/TaskHandler2";

export default {
  name: "ProcessButtons",
  components: {
    'taskHandler': TaskHandler
  },
  props: {
    'solutionCode': {
      type: String,
      default: null
    },
    'businessKey': {
      type: String,
      default: null
    },
    'businessTitle': {
      type: String,
      default: null
    },
    'variables': {
      type: Object,
      default: {  }
    }, 'processInstanceId': {
      type: String,
      default: null
    },
    'title': {
      type: String,
      default: null
    },
    'canStart': {
      type: Boolean,
      default: true
    }
  },
  watch: {
    businessKey() {
      this.curInstance.id = this.processInstanceId;
      this.refreshButtons();
    }
  },
  data() {
    return {
      detailVisible: false,
      handlerVisible: false,
      selfHandlerVisible: false,
      currentComponent: null,
      curInstance: {
        "processDefinitionId": '',
        "id": ''
      },
      curTask: null,
      tasks: [],
      taskName: '流程办理',
      buttons: {
        start: false,
        handle: false,
        view: false,
        cancel: false
      }
    };
  },
  mounted() {
    if (this.businessKey) {
      this.curInstance.id = this.processInstanceId;
      this.refreshButtons();
    }
  },
  methods: {
    refreshButtons() {
      if (!this.businessKey) {
        return;
      }
      const params = {
        "solutionCode": this.solutionCode,
        "businessKey": this.businessKey,
        "tenantId": this.tenantId
      };
      if (this.processInstanceId) {
        params["processInstanceId"] = this.processInstanceId;
      }
      this.$workflowApi.getButtons(params).then(res => {
        if (res.success) {
          const {result} = res;

          this.curInstance.id = result.processInstanceId || this.processInstanceId;
          this.curInstance.processDefinitionId = result.processDefinitionId;

          // 有查看流程的权限，且没有启动流程, 且没有传流程实列id过来
          this.buttons.start = result.power && !result.started && !this.curInstance.id;
          // 有权限，且有设置流程实列ID
          this.buttons.view = result.power && !!this.curInstance.id;
          // 有任务才出现处理按钮
          this.buttons.handle = result.tasks && result.tasks.length > 0;
          // 是启动者， 流程没有结束，有流程实列ID，有任务，才能终止流程
          // this.buttons.cancel = data.owner && !data.finished && !!this.curInstance.id && data.tasks.length > 0;
          // 暂时去掉取消功能
          this.buttons.cancel = false;

          if (result.tasks) {
            this.tasks = result.tasks;
            if (this.tasks.length === 1) {
              this.curTask = this.tasks[0];
              this.taskName = this.curTask.name || '流程办理'
            }
          }

          // console.log('buttons:', this.buttons);
        } else {
          this.buttons.start = false;
          this.buttons.view = false;
          // 有任务才出现处理按钮
          this.buttons.handle = false;
          // 是启动者才能撤销流程
          this.buttons.cancel = false;
          this.curInstance.processDefinitionId = "";
          this.curInstance.id = "";
          this.tasks = [];
        }
      });
    },
    start() {
      if (!this.solutionCode || !this.businessKey) {
        this.$message.error("没有指定流程映射编码和业务主键值，无法启动流程");
        return;
      }
      const params = {
        "solutionCode": this.solutionCode,
        "businessKey": this.businessKey,
        "title": this.businessTitle,
        "variables": this.variables
      };
      this.$workflowApi.startSolution(params).then(res => {
        if (res.success) {
          //  启动成功
          this.$message.success("启动成功")
          this.curInstance.id = res.result.processInstanceId;
          this.refreshButtons();
          this.$emit("StartSuccess", {processInstanceId: res.result.processInstanceId, businessKey: this.businessKey});
        } else {
          this.$emit("StartFailure", {solutionCode: this.solutionCode, businessKey: this.businessKey});
        }
      });
    },
    handleSuccess(data) {
      const result = {};
      result["businessKey"] = this.businessKey;
      result["processInstanceId"] = this.processInstanceId || this.curInstance.id;
      result["solutionCode"] = this.solutionCode;
      result["nextTask"] = data;
      this.$emit("handleSuccess", result);
    },
    handle() {
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
        this.handlerVisible = true;
      }
    },
    closeHandler (status) {
      // status 有： ok, fail, close
      this.selfHandlerVisible=false
      if (status === "ok") {
        this.refreshButtons()
      }
    },
    handleMenuClick(e) {
      console.log("click e:", e);
    },
    viewDetail() {
      this.detailVisible = true;
    },
    cancelProcess() {
      const insId = this.processInstanceId || this.curInstance.id;
      if (this.tasks.length > 0) {
        const params = {'processInstanceId': insId, 'taskId': this.tasks[0].id};
        this.$workflowApi.stopProcess(insId, this.tasks[0].id).then(res => {
          if (res.success) {
            this.$message.success("终止成功");
            this.$emit("cancelSuccess", params);
            this.refreshButtons();
          } else {
            this.$message.error("终止流程异常");
          }
        }).catch(err => {
          this.$message.error("终止流程异常");
          console.error("终止流程异常", err);
        });
      }
      /*const insId = this.processInstanceId || this.curInstance.id;
      if (this.tasks.length > 0) {
        api.stopProcess(insId, this.tasks[0].id).then(res => {
          if (res.success) {
            this.$message.success("发送成功")
            this.refreshButtons();
          } else {
            this.$message.error("终止流程异常");
          }
        }).catch(err => {
          this.$message.error("终止流程异常");
          console.error("终止流程异常", err);
        });
      }*/
    }
  }
};
</script>

<style scoped>

</style>
