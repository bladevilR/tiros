<template>
  <a-form-model ref="approveForm" :model="form" layout="vertical" :rules="rules">
    <a-form-model-item label="审批动作" prop="action">
      <a-radio-group v-model="form.action" :options="actions" @change="onActionChange"></a-radio-group>
    </a-form-model-item>
    <a-form-model-item label="转办人员" v-if="form.action===5" prop="assignName">
      <a-input-search v-model="form.assignName" placeholder="请选择转办人员" @search="onSelectUser(false,'assign')">
        <a-button slot="enterButton" icon="search"></a-button>
      </a-input-search>
    </a-form-model-item>
    <a-form-model-item label="委派人员" v-if="form.action===6" prop="delegateName">
      <a-input-search v-model="form.delegateName" placeholder="请选择委派人员" @search="onSelectUser(false,'delegate')">
        <a-button slot="enterButton" icon="search"></a-button>
      </a-input-search>
    </a-form-model-item>
    <a-form-model-item label="意见" prop="comment">
      <a-input v-model="form.comment" type="textarea"/>
    </a-form-model-item>
    <UserInfoWindow
      v-if="showUserSelect"
      :show.sync="showUserSelect"
      :multiple="selectMultiple"
      ref="userSelect"
      @callback="userSelectCallBack"></UserInfoWindow>
  </a-form-model>
</template>

<script>
const actionMap = {
  '0': {label: '不同意', value: 0},
  '1': {label: '同意', value: 1},
  /*'2': {label: '驳回', value: 2},
   '3': {label: '驳回开始', value: 3},
   '4': {label: '终止', value: 4},
   '5': {label: '转办', value: 5},
   '6': {label: '委派', value: 6}*/
};
export default {
  name: "TaskApprove",
  props: ['taskId', 'show', 'variables'],
  data() {
    return {
      title: '',
      selectType: "",
      showUserSelect: false,
      selectMultiple: false,
      actions: [
      ],
      form: {
        action: 1,
        assign: null,
        assignName: null,
        delegate: null,
        delegateName: null,
        comment: '同意'
      },
      rules: {
        action: [
          {required: true, message: '请选择审批动作', trigger: 'blur'}
        ],
        assignName: [
          {required: true, message: '请选择转办人员', trigger: 'blur'}
        ],
        delegateName: [
          {required: true, message: '请选择委派人员', trigger: 'blur'}
        ],
        comment: [
          {required: true, message: '请输入处理意见', trigger: 'blur'},
        ],
      }
    };
  },
  mounted() {
    this.init_actions();
  },
  methods: {
    init_actions() {
      for (const attr in actionMap) {
        this.actions.push(actionMap[attr]);
      }
    },
    loadTaskInfo() {
      // this.taskId
      this.$workflowApi.getTaskData(this.taskId).then(res => {
        if (res.success) {
          this.title = res.result.taskName;
          console.debug("taskInfo:", res.result);
        } else {
          this.$message.error(res.message);
          console.error("获取流程信息错误", res.message);
        }
      }).catch(err => {
        this.$message.error("获取流程信息异常：", err);
        console.error("获取流程信息异常", err);
      });
    },
    cancelHandle() {
      this.$emit('update:show', false);
    },
    onActionChange(e) {
      // console.log('radio2 checked', e.target.value);
      this.form.comment = actionMap[this.form.action + ''].label;
      // eslint-disable-next-line no-empty
      if (this.form.action === 5 || this.form.action === 6) {

      }
    },
    onSelectUser(multi, type) {
      this.selectType = type;
      this.selectMultiple = multi;
      this.showUserSelect = true;
    },
    userSelectCallBack(selectedUsers) {
      const userIds = []
      const userNames = []
      for (let i = 0; i < selectedUsers.length; i++) {
        userIds.push(selectedUsers[i].id)
        userNames.push(selectedUsers[i].userName)
      }
      if (this.selectType === 'assign') {
        this.form.assign = userIds.join(',')
        this.form.assignName = userNames.join(',')
      } else if (this.selectType === 'delegate') {
        this.form.delegate = userIds.join(',')
        this.form.delegateName = userNames.join(',')
      }
      // this.showUserSelect = false;
      this.$forceUpdate()
    },
    checkInput () {
      let check=false
      this.$refs.approveForm.validate(valid => {
        if (valid) {
          check=true
        } else {
          check=false
        }
      })
      return check;
    },
    completeTask() {
      this.$refs.approveForm.validate(valid => {
        if (valid) {
          let vars = Object.assign( {'approved': this.form.action === 1}, this.variables)
          if (this.form.action === 0 || this.form.action === 1) {
            this.$workflowApi.completeTask({
              'taskId': this.taskId,
              'message': this.form.comment,
              'vars': vars
            }).then(res => {
              if (res.success) {
                this.$message.success("审批成功");
                // 获取当前任务的下一个任务是什么？
                this.$emit("ok", res.result);
              } else {
                this.$message.error("审批失败");
                console.error("提交审批失败：", res.message);
                this.$emit('fail', res.message)
              }
            }).catch(err => {
              this.$message.error("提交审批异常：", err);
              console.error("提交审批异常", err);
              this.$emit('fail', res.message)
            });
          }
          if (this.form.action === 2) {
            // 驳回
            this.$workflowApi.backPreTask({'taskId': this.taskId, 'comment': this.form.comment}).then(res => {
              if (res.success) {
                this.$message.success("驳回成功");
                this.$emit("ok", res.result);
              } else {
                this.$message.error(res.message);
                this.$emit('fail', res.message)
              }
            });
          }
        } else {
          // console.log('error submit!!');
          this.$emit('fail', valid)
        }
      });
    },
    getApproveAction () {
      // 参考 上面的 actionMap 的值
      return parseInt(this.form.action)
    }
  }
}
</script>

<style scoped>

</style>
