<template>
  <div na-flex-height-full>
    <a-tabs default-active-key="1" na-flex-height-full>
      <a-tab-pane key="1" tab="业务表单" force-render na-flex-height-full>
        <a-spin id="naWorkflowSpin" :spinning="loading" v-show="currentView===1" na-flex-height-full>
          <component ref="wfForm" v-bind:is="currentComponent" v-if="!refresh && taskInfo" :processInstanceId="processInstanceId" :wfTask="taskInfo"
                     :businessKey="taskInfo.businessKey" @ok="saveOk" @fail="saveFail" @cancel="saveCancel" @loaded="onBizLoaded"
                     @close="closeWin" :variables="taskInfo.variables" :attributes="attributes" :fromFlow="true"
                     :isReadonly="readOnly" style="height: 100%;"></component>
        </a-spin>
      </a-tab-pane>
      <a-tab-pane key="3" tab="审批历史" force-render style="height: calc(100% - 0px)">
        <ProcessInstanceHistories v-if="processInstanceId"
                                  :processInstanceId="processInstanceId"></ProcessInstanceHistories>
      </a-tab-pane>
      <a-tab-pane key="2" tab="流程图" force-render style="height: calc(100% - 0px)">
        <ProcessImageViewer v-if="processDefinitionId && processInstanceId" :processDefinitionId="processDefinitionId"
                            :processInstanceId="processInstanceId"></ProcessImageViewer>
      </a-tab-pane>
      <div slot="tabBarExtraContent">
        <a-space>
          <a-button :loading="loading" style="margin-right: 6px" v-if="opType===2 && canSave" :disabled="!isLoaded" @click="saveForm({needComplete: false})">
            保存
          </a-button>
          <a-button :loading="loading" type="primary" v-if="opType===2" :disabled="!isLoaded" @click="saveForm({needComplete:true})">
            {{ btnTitle }}
          </a-button>
          <a-button :loading="loading" type="primary" block v-if="opType===1 || opType===3" :disabled="!isLoaded" @click="showApprove">
            审批
          </a-button>
<!--          const doActions = [
          {value: '0', label: '退回开始'},
          {value: '1', label: '退回'},
          {value: '2', label: '转办'},
          {value: '3', label: '委办'},
          {value: '4', label: '终止'}
          ];-->
          <a-dropdown :loading="loading" v-if="actions.includes('0') || actions.includes('1') ">
            <a-menu slot="overlay" @click="handleReturnMenuClick">
              <a-menu-item key="1" v-if="actions.includes('1')">
                退回上一环节
              </a-menu-item>
              <a-menu-item key="0" v-if="actions.includes('0')">
                退回开始环节
              </a-menu-item>
              <!--              <a-menu-item key="3">
                              退回指定环节
                            </a-menu-item>-->
            </a-menu>
            <a-button> 退回
              <a-icon type="down" />
            </a-button>
          </a-dropdown>
          <a-button :loading="loading" v-if="actions.includes('2')">
            转办
          </a-button>
          <a-button :loading="loading" v-if="actions.includes('3')">
            委办
          </a-button>
          <a-button :loading="loading" v-if="actions.includes('4')" @click="cancelProcess">
            终止
          </a-button>
        </a-space>
      </div>
    </a-tabs>

    <a-modal
      centered
      :width="800"
      :bodyStyle="{height: '400px'}"
      :title="taskInfo.taskName"
      :visible="approveVisible"
      @cancel="approveVisible=false"
      :destroyOnClose="true"
      v-if="approveVisible"
      :confirmLoading="loading"
      @ok="saveApprove"
    >
      <TaskApprove ref="approveModal" :show.sync="approveVisible" :task-id="taskId" @ok="executeSuccess"
                   @fail="executeFail" :variables="this.variables"></TaskApprove>
    </a-modal>
  </div>
</template>

<script>
import TaskApprove from '@views/workflow/TaskApprove'

export default {
  name: 'TaskHandler',
  components: {
    TaskApprove
  },
  props: ['taskId', 'show', 'processInstanceId', 'processDefinitionId', 'variables'],
  data () {
    return {
      refresh: false,
      loading: false,
      showSidebar: false,
      currentView: 1,
      currentComponent: null,
      taskInfo: null,
      approveVisible: false,
      opType: 0,
      wfTemplate: '',
      vars: {},
      attributes: {},
      readOnly: false,
      canSave: false,
      btnTitle: '提交',
      needSaveComplete: false,
      needApproveComplete: false,
      actions: [],
      isLoaded: false // 用于表示业务组件的数据是否加载完成
    }
  },
  mounted () {
    this.vars = Object.assign({}, this.variables)
    this.loadTaskInfo()
  },
  methods: {
    async saveApprove () {
      this.loading = true
      if (this.opType === 3) {
        let check = await this.$refs.approveModal.checkInput()
        if (check) {
          // 先保存表单
          if (this.$refs.wfForm && this.$refs.wfForm.save) {
            let ops = { onlySave: false, needComplete: true, flowAction: 1, vars: {} }
            ops.flowAction = this.$refs.approveModal.getApproveAction()

            if (ops.flowAction === 1) { // 同意
              ops.onlySave = false
            } else {
              ops.onlySave = true
            }
            this.saveForm(ops)
          } else {
            // 没有找到save 方法, 就不保存表单了
            this.$refs.approveModal.completeTask()
          }
        }
      } else if (this.opType === 1) {
        // 直接提交
        this.$refs.approveModal.completeTask()
      }
    },
    /**
     * 加载当前任务信息
     */
    loadTaskInfo () {
      this.$workflowApi.getTaskData(this.taskId).then(res => {
        if (res.success) {
          this.taskInfo = res.result
          //this.processDefinitionId = this.taskInfo.processDefinitionId;
          //this.processInstanceId = this.taskInfo.processInstanceId;
          // 1 审批  2  办理(表单)  3 审批&办理
          this.opType = this.taskInfo.opType
          // console.log('taskInfo=', this.taskInfo)

          if (this.taskInfo.taskConfig) {
            let config = this.taskInfo.taskConfig
            if (config.sbText && config.sbText.replace(/\s*/g, '')) {
              this.btnTitle = config.sbText
            }
            this.canSave = config.canSave
            // config.noticeNext
            // config.starterComplete

            if (this.taskInfo.taskConfig.doActions) {
              this.actions = this.taskInfo.taskConfig.doActions.split(',')
            }
          }

          // 找到这个当前路由对象，在他的下面添加组件
          // formType 分：URL，ONLINE，COMPONENT
          try {
            let url = ''
            // 暂时注释掉根据是编辑表单还是查看表单来确定操作
            if (!!this.taskInfo.editForm && this.taskInfo.editForm.formType === 'COMPONENT') {
              url = this.taskInfo.editForm.formTarget
              // 如果表单是编辑, 默认为办理类型
              // this.opType = 2;
            } else if (!!this.taskInfo.viewForm && this.taskInfo.viewForm.formType === 'COMPONENT') {
              url = this.taskInfo.viewForm.formTarget
              //  如果是查看表单，则为审批类型
              // this.opType = 1;
            }
            if (url) {
              const rg = new RegExp('^/*')
              url = url.trim().replace(rg, '')
              // this.$set(component, 'planInfoId', this.taskInfo.businessKey);
              this.currentComponent = resolve => require(['@/' + url + '.vue'], resolve)
            } else {
              // 没有表单，
              this.currentComponent = null
              this.currentView = 2
              // 没有表单，只能审批了
              this.opType = 1
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
                // 自定义的属性中是否有保存属性
                if (attr.attrKey.toLowerCase() == 'save') {
                  this.canSave = true
                }
              })
            }

            if (attrReadonly || this.opType == 1 || this.opType === 3 && !this.taskInfo.editForm) {
              // 为审批，或者 审批&提交 且 任务没有编辑表单时，为只读
              this.readOnly = true
            }
            console.log(url)
            //console.log('opType:', this.opType)
          } catch (error) {
            console.error('加载流程表单异常：', error)
            this.$message.error('加载流程表单异常')
          }
        } else {
          this.$message.error(res.message)
          console.error('获取流程信息错误', res.message)
        }
      }).catch(err => {
        this.$message.error('获取流程信息异常：', err)
        console.error('获取流程信息异常', err)
      })
    },
    /**
     * 显示审批界面
     */
    showApprove () {
      this.approveVisible = true
    },
    /**
     * 保存表单
     */
    saveForm (opts) {
      opts.actionType = this.opType  // 1 审批（不保存表单，只审批）  2 提交（保存表单，不审批）  3 审批&提交（既保存，又审批）
      // 构造默认参数
      let _opts = { needComplete: false, flowAction: 1, vars: {} }
      Object.assign(_opts, opts)

      // 保存后是否需要提交流程
      this.needSaveComplete = false
      // 保存成功后是否需要提交审批
      this.needApproveComplete = false

      if (opts.actionType === 2) {
        this.needSaveComplete = _opts.needComplete
      } else {
        this.needApproveComplete = _opts.needComplete
      }

      // 如果没有设置业务表单保存操作是否是只保存，则默认不提交则表示只保存
      if (_opts.onlySave === null || _opts.onlySave === undefined) {
        _opts.onlySave = !_opts.needComplete
      }

      // 有保存方法
      if (this.$refs.wfForm && this.$refs.wfForm.save) {
        this.loading = true
        this.$refs.wfForm.save(_opts)
        //console.debug("保存流程表单返回结果：", result);
      } else {
        // 表单中没有保存方法，但是当前操作类型又是opType==2
        this.saveOk({})
        // this.$message.warn('无法保存业务数据，请确认业务表单已加载完成？')
      }
    },
    onBizLoaded (data) {
      this.isLoaded = true
    },
    saveOk (opts) {
      // 直接提交表单
      if (this.needSaveComplete) {
        let cusVars = opts.vars
        if (cusVars) {
          for (const cusVarsKey in cusVars) {
            this.vars[cusVarsKey] = cusVars[cusVarsKey]
          }
        }
        this.completeTask()
        return
      }

      // 如果需要审批，提交审批
      if (this.needApproveComplete) {
        // 需要审批提交
        this.$refs.approveModal.completeTask()
        return
      }

      //  不要提交当前环节，也不要提交审批，那么估计就只是保存表单，那么保存后，刷新下
      this.loading = false
      this.$message.success('操作成功！')
      // 保存成功后刷新下
      this.refresh = true
      setTimeout(() => {
        this.refresh = false
      }, 200)
    },
    saveFail (data) {
      if (data.res && data.res.message) {
        this.$message.error(data.res.message)
      } else {
        this.$message.error('操作失败')
      }
      this.loading = false
    },
    saveCancel (data) {
      this.loading = false
      if (data && data.close) {
        this.closeWin(true)
      }
    },
    closeWin (refresh) {
      if (refresh) {
        this.$emit('handleSuccess')
      }
      this.loading = false
    },
    /**
     * 审批成功回调
     * @param data
     */
    executeSuccess (data) {
      this.$emit('handleSuccess', data)
      // this.saveForm(false)
      this.close()
      this.loading = false
    },
    executeFail (data) {
      this.loading = false
    },
    /**
     * 提交到下个流程节点
     */
    completeTask () {
      let _vars = Object.assign({ 'approved': true }, this.vars)
      this.$workflowApi.completeTask({
        'taskId': this.taskId,
        'message': '办理完毕',
        'vars': _vars
      }).then(res => {
        if (res.success) {
          // 获取当前任务的下一个任务是什么
          this.$message.success('操作成功')
          this.$emit('handleSuccess', res.result)
          this.close()
        } else {
          this.$message.error('操作失败')
          console.error('提批失败：', res.message)
        }
        this.loading = false
      }).catch(err => {
        this.$message.error('操作异常：', err)
        console.error('提交异常', err)
      })
    },
    close () {
      this.$emit('update:show', false)
    },
    changeSidebar () {
      this.showSidebar = !this.showSidebar
    },
    handleReturnMenuClick (e) {
      let action = e.key
      this.$confirm({
        content: '确认要进行退回操作？',
        onOk: () => {
          // 退回开始
          if (action === '0') {

          }
          // 退回上一环节
          if (action === '1') {
            //退回
            this.$workflowApi.backPreTask({ 'taskId': this.taskId, 'comment': '退回上一环节' }).then(res => {
              if (res.success) {
                this.$message.success('操作成功')
                this.$emit('handleSuccess', res.result)
                this.close()
              } else {
                this.$message.error('操作失败')
                console.error('提批失败：', res.message)
              }
            })
          }
        }
      })
    },
    // 终止流程
    cancelProcess () {
      this.$confirm({
        content:'你确认要终止当前流程?',
        onOk: () => {
          this.$workflowApi.stopProcess(this.processInstanceId, this.taskId).then(res => {
            if (res.success) {
              this.$message.success('操作成功')
              this.$emit('handleSuccess', res.result)
              this.close()
            } else {
              this.$message.error('操作失败')
              console.error('取消流程失败：', res.message)
            }
          })
        }
      })

    }
  }
}
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
  }
;

  .sidebar-menu {
    width: 110px;
    float: left;
    height: 100%;
    padding-left: 8px;
  }
}

.slide-fade-enter-active {
  transition: all .2s ease;
}

.slide-fade-leave-active {
  transition: all .2s cubic-bezier(1.0, 0.5, 0.8, 1.0);
}

.slide-fade-enter, .slide-fade-leave-to {
  transform: translateX(8px);
  opacity: 0;
}

/deep/ .ant-tabs-top-content {
  height: calc(100% - 50px);
}

/deep/ .ant-spin-container {
  height: calc(100% - 0px) !important;
}

/deep/ .ant-card-body {
  height: calc(100% - 0px) !important;
}
</style>
<style>
#sidebar .ant-btn {
  margin: 0px !important;
  margin-bottom: 8px !important;
  display: list-item;
}


</style>
