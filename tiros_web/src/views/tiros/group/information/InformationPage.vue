<template>
  <div style="padding: 5px; height: calc(100% - 0px); overflow: hidden;">
    <a-row>
      <a-col :xl="24" :lg="24" :md="24" :sm="24" :xs="24">
        <template>
          <a-tabs :activeKey="activeKey" @change="changeTab">
            <a-tab-pane key="1" tab="基本信息">
              <basic-info></basic-info>
            </a-tab-pane>
            <a-tab-pane key="2" tab="人员信息">
              <person-info></person-info>
            </a-tab-pane>
            <a-tab-pane key="3" tab="工装信息">
              <equip-info></equip-info>
            </a-tab-pane>
            <a-tab-pane key="4" tab="工具信息">
              <tools-info></tools-info>
            </a-tab-pane>
            <a-tab-pane key="5" tab="工位信息">
              <wook-info></wook-info>
            </a-tab-pane>
          </a-tabs>
        </template>
      </a-col>
    </a-row>
  </div>
</template>

<script>

import PersonInfo from './PersonInfo'
import BasicInfo from './BasicInfo'
import EquipInfo from './EquipInfo'
import ToolsInfo from './ToolsInfo'
import WookInfo from './WookInfo'

export default {
  name: 'InformationPage',
  components: { WookInfo, ToolsInfo, EquipInfo, BasicInfo, PersonInfo },
  data() {
    return {
      activeKey: '1',
      queryParam: {
        name: '',
        position: ''
      },
      page: {
        currentPage: 1,
        pageSize: 10,
        totalResult: 3
      },
      allAlign: 'center'
    }
  },
  created() {
  },
  methods: {
    changeTab(activeKey) {
      this.activeKey = activeKey
    }
  }
}
</script>

<style scoped>
.titleDiv {
  width: 100%;
  background: #eee;
  padding: 10px;
  text-align: center;
}

.leftPart {
  border: 1px solid #eee;
  border-radius: 10px;
  overflow: hidden;
  margin-right: 20px;
  min-height: 70vh;
}

.centerDiv {
  width: 100%;
  text-align: center;
  padding: 10px 0;
}

.updateBtn {
  width: 90%;
}

.rightPart {
  border: 1px solid #eee;
  border-radius: 10px;
  overflow: hidden;
  padding: 10px;
  height: calc(100vh - 110px);
}
</style>