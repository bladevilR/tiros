<template>
  <a-modal
    centered
    :width="'100%'"
    :title="title"
    dialogClass="fullDialog no-footer"
    :visible="visible"
    @cancel="handleCancel"
    :footer="null"
    :destroyOnClose="true"
    @ok="save"
  >
    <div>
      <a-tabs :activeKey="activeKey" @change="changeTab">
        <a-tab-pane key="1" tab="基本信息">
          <base-info2 ref="baseInfo" v-if="currentUser" :current-user.sync="currentUser"  :isDisable="isDisable" style="margin-top: 15px"></base-info2>
        </a-tab-pane>
        <a-tab-pane key="2" tab="培训信息">
          <training-info  ref="trainingInfo"  @trainingListMethod="trainingListMethod" :userId="currentUser.id"></training-info>
        </a-tab-pane>
        <a-tab-pane key="3" tab="作业证件">
          <cert-info :certList.sync="currentUser.certList" :userId="currentUser.id"></cert-info>
        </a-tab-pane>
        <a-tab-pane key="4" tab="技能标签">
          <skill-tags-info :tagTitleList.sync="currentUser.tagTitleList" :userId="currentUser.id"></skill-tags-info>
        </a-tab-pane>
        <a-tab-pane key="5" tab="技能分布">
          <radar :skillList.sync="currentUser.skillList" :height="600" :userId="currentUser.id"/>
        </a-tab-pane>
        <a-button v-if="!isDisable"  @click="save" class="margin-right8" type="primary" slot="tabBarExtraContent">
          保存
        </a-button>
        <a-button v-if="!isDisable" @click="handleCancel" class="margin-right8" type="dashed" slot="tabBarExtraContent">
          取消
        </a-button>
<!--        <a-button v-else  @click="handleCancel" class="margin-right8" type="dashed" slot="tabBarExtraContent">
          返回
        </a-button>-->
      </a-tabs>
    </div>
  </a-modal>
</template>


<script>
import BaseInfo from './BaseInfo'
import BaseInfo2 from '@views/tiros/basic/modules/orgStaff/BaseInfo2'
import TrainingInfo from './TrainingInfo'
import CertInfo from './CertInfo'
import SkillTagsInfo from './SkillTagsInfo'
import Radar from '@/components/chart/Radar'
import { saveUser } from '@api/tirosApi'

export default {
  components: { BaseInfo,BaseInfo2, TrainingInfo, CertInfo, SkillTagsInfo, Radar },
  name: 'StaffInfo',
  data() {
    return {
      isDisable:false,
      title: '人员信息',
      activeKey: '1',
      visible: false,
      loading: false,
      queryParam: {
        id: ''
      },
      currentUser: {
        tagTitleList: [],
        certList: [],
        trainingList: [],
        skillList: []
      },
    }
  },

  methods: {
    changeTab(activeKey) {
      this.activeKey = activeKey
    },
    handleCancel() {
      this.visible = false
      this.isDisable=false
      this.activeKey='1'
      this.currentUser={}
    },
    add() {
      this.edit({})
    },
    edit(record) {
      console.log(record)
      Object.assign(this.currentUser, record)
      this.visible = true
      console.log(this.currentUser)
    },
    save() {
      this.$refs.baseInfo.validateInput().then(()=>{
        this.$refs.baseInfo.handleSubmit().then((u)=>{
          // 基本信息
          const user = { id: u.id}

          // 关联信息
          user.tagTitleList = this.currentUser.tagTitleList
          user.certList = this.currentUser.certList
          user.trainingList = this.currentUser.trainingList

          // 转换属性


          // 保存
          saveUser(user).then(res => {
            if (res.success) {
              this.$message.success('保存成功')
              this.visible = false
              this.activeKey='1'
              this.$emit('ok')
              this.handleCancel()
            } else {
              this.$message.error('保存失败')
            }
          }).finally(()=>this.currentUser={})
        }).catch(e=>{
          console.error('保存基本信息失败：', e)
          this.$message.error('保存基本信息失败')
        })
      }).catch(err=>{
        this.$message.warn('你的输入验证不通过')
      })
    },
    trainingListMethod(value){
      this.$set(this.currentUser,"trainingList",value)
    }
  }
}
</script>

<style scoped>
.model {
  height: 80%;
}
</style>