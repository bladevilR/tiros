<template>
  <div style="height: 100%;">
    <div class="sidebar-box bg-primary-7" @mouseover="showSidebar=true" @mouseleave="showSidebar=false">
      <div class="sidebar-btn" @click="changeSidebar">
        <span style="font-size: large">☰</span><br>
        功<br>能<br>按<br>钮
      </div>
      <div id="sidebar" class="sidebar-menu" v-show="showSidebar">
        <!--<a-button v-if="opType===2 || opType===3" @click="saveForm" >
          保存
        </a-button>-->

        <a-button type="primary" block v-if="opType===2" @click="saveForm" >
          提交
        </a-button>
        <a-button type="primary" block v-if="opType===1 || opType===3" @click="showApprove" >
          审批
        </a-button>
        <a-button block v-if="currentComponent" @click="currentView=1">
          业务数据
        </a-button>
        <a-button block @click="currentView=2">
          流程图
        </a-button>
        <a-button block @click="currentView=3">
          审批历史
        </a-button>
      </div>
    </div>
    <transition name="slide-fade">
      <a-spin :spinning="loading" v-show="currentView===1">
        <component v-show="currentView===1" ref="wfForm" v-bind:is="currentComponent" v-if="taskInfo" :businessKey="taskInfo.businessKey" @ok="saveOk" @fail="saveFail" :variables="taskInfo.variables" :attributes="attributes" :fromFlow="true" :isReadonly="readOnly" style="height: 100%;"></component>
      </a-spin>
    </transition>
    <transition name="slide-fade">
      <ProcessImageViewer v-show="currentView===2" v-if="processDefinitionId && processInstanceId" :processDefinitionId="processDefinitionId" :processInstanceId="processInstanceId"></ProcessImageViewer>
    </transition>
    <transition name="slide-fade">
      <ProcessInstanceHistories v-show="currentView===3" v-if="processInstanceId" :processInstanceId="processInstanceId"></ProcessInstanceHistories>
    </transition>
    <a-modal
      centered
      :width="800"
      :bodyStyle="{height: '400px'}"
      :title="taskInfo.taskName"
      :visible="approveVisible"
      @cancel="approveVisible=false"
      :destroyOnClose="true"
      v-if="approveVisible"
      @ok="saveApprove"
    >
      <TaskApprove ref="approveModal" :show.sync="approveVisible" :task-id="taskId" @executeSuccess="executeSuccess" :variables="this.variables"></TaskApprove>
    </a-modal>
  </div>
</template>

<script>
import TaskApprove from '@views/workflow/TaskApprove'
export default {
  name: "TaskHandler",
  components: {
    TaskApprove
  },
  props: ['taskId', 'show', 'processInstanceId', 'processDefinitionId', 'variables'],
  data() {
    return {
      loading: false,
      showSidebar: false,
      currentView: 1,
      currentComponent: null,
      taskInfo: null,
      approveVisible: false,
      opType: 0,
      wfTemplate: '',
      attributes: {},
      readOnly: false
    };
  },
  mounted() {
    this.loadTaskInfo();
  },
  methods: {
    saveApprove () {
      this.$refs.approveModal.completeTask()
    },
    /**
     * 加载当前任务信息
     */
    loadTaskInfo() {
      this.$workflowApi.getTaskData(this.taskId).then(res => {
        if (res.success) {
          this.taskInfo = res.result;
          //this.processDefinitionId = this.taskInfo.processDefinitionId;
          //this.processInstanceId = this.taskInfo.processInstanceId;
          // 1 审批  2  办理(表单)  3 审批&办理
          this.opType = this.taskInfo.opType;
          // 找到这个当前路由对象，在他的下面添加组件
          // formType 分：URL，ONLINE，COMPONENT
          try {
            let url = ''
            if (!!this.taskInfo.editForm && this.taskInfo.editForm.formType === 'COMPONENT') {
              url = this.taskInfo.editForm.formTarget
              // 如果表单实编辑, 默认为办理类型
              this.opType = 2;
            } else if (!!this.taskInfo.viewForm && this.taskInfo.viewForm.formType === 'COMPONENT') {
              url = this.taskInfo.viewForm.formTarget
              //  如果是查看表单，则为审批类型
              this.opType = 1;
            }
            if (url) {
              const rg = new RegExp('^/*')
              url = url.replace(rg, '')
              // this.$set(component, 'planInfoId', this.taskInfo.businessKey);
              this.currentComponent = resolve => require(['@/' + url + '.vue'], resolve)
            } else {
              // 没有表单，
              this.currentComponent = null;
              this.currentView = 2;
              // 没有表单，只能审批了
              this.opType = 1;
            }

            // 处理自定义属性，转成对象
            let attrReadonly = false
            if (this.taskInfo.attributes && this.taskInfo.attributes.length > 0) {
              this.taskInfo.attributes.forEach(attr => {
                let val = attr.attrValue
                if (attr.attrType === 'Number') {
                  val = Number(val)
                }
                this.attributes[attr.attrKey] = val

                // 自定义的属性中是否有设置只读属性
                if (attr.attrKey.toLowerCase() == 'readonly') {
                  attrReadonly = true
                }
              })
            }

            if(attrReadonly || this.opType==1 || this.opType===3 && !this.taskInfo.editForm){
              // 为审批，或者 审批&提交 且 任务没有编辑表单时，为只读
              this.readOnly = true
            }
            //console.log('opType:', this.opType)
          } catch (error){
            console.error('加载流程表单异常：', error)
            this.$message.error('加载流程表单异常')
          }
        } else {
          this.$message.error(res.message);
          console.error("获取流程信息错误", res.message);
        }
      }).catch(err => {
        this.$message.error("获取流程信息异常：", err);
        console.error("获取流程信息异常", err);
      });
    },
    /**
     * 显示审批界面
     */
    showApprove() {
      this.approveVisible = true;
    },
    /**
     * 保存表单
     */
    saveForm() {
      if (this.$refs.wfForm && this.$refs.wfForm.save) {
        this.loading = true
        const result = this.$refs.wfForm.save()
        this.loading = false
        //console.debug("保存流程表单返回结果：", result);
      } else {
        this.completeTask()
      }
    },
    saveOk (data) {
      this.completeTask()
    },
    saveFail (data) {

    },
    /**
     * 审批成功回调
     * @param data
     */
    executeSuccess(data) {
      this.$emit("handleSuccess", data);
      this.close();
    },
    /**
     * 提交到下个流程节点
     */
    completeTask() {
      let vars = Object.assign( {'approved': true}, this.variables)
      this.$workflowApi.completeTask({
        'taskId': this.taskId,
        'message': '办理完毕',
        'vars': vars
      }).then(res => {
        if (res.success) {
          // 获取当前任务的下一个任务是什么
          this.$message.success("提交成功");
          this.$emit("handleSuccess", res.result);
          this.close();
        } else {
          this.$message.error("提交失败");
          console.error("提批失败：", res.message);
        }
      }).catch(err => {
        this.$message.error("提交异常：", err);
        console.error("提交异常", err);
      });
    },
    close() {
      this.$emit('update:show', false);
    },
    changeSidebar () {
      this.showSidebar = !this.showSidebar
    }
  }
};
</script>
<style scoped lang="less">
.sidebar-box {
  padding: 8px;
  position: absolute;
  top: 50%;
  margin-top: -60px;
  right: 10px;
  /*background-color: #d1d2d4;*/
  z-index: 9999;
  box-shadow: 5px 5px 3px #888888;

  .sidebar-btn {
    width: 18px;
    float: left;
    text-align: center;
    cursor: pointer;
    font-weight: bold;
    color: #e2e2e2;

    &:hover {
      color: #f89d73;
    }
  };

  .sidebar-menu{
    width: 110px;
    float: left;
    height: 100%;
    padding-left: 8px;
  }
}

.slide-fade-enter-active {transition: all .2s ease;}
.slide-fade-leave-active {transition: all .2s cubic-bezier(1.0, 0.5, 0.8, 1.0);}
.slide-fade-enter, .slide-fade-leave-to{transform: translateX(8px);opacity: 0;}
</style>
<style>
#sidebar .ant-btn {
  margin: 0px !important;
  margin-bottom: 8px !important;
  display: list-item;
}
</style>
