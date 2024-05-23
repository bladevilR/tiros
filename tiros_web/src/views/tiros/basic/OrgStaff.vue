<template>
  <div>
    <a-spin :spinning='loadingLeft' tip='正在同步数据'>
      <a-row type='flex' :gutter='16'>
        <a-col class='leftPart' :md='4' :sm='24'>
          <a-card id='leftPart'>
            <div class='titleDiv'>
              <a-icon type='apartment' />
              组织机构
            </div>
            <div class='centerDiv'>
              <a-button class='updateBtn' v-has="'orginfo:sync'" @click='syncDeptAndUser'>同步机构和人员</a-button>
            </div>
            <a-tree
              v-if='treeDataSource.length'
              :defaultExpandAll='true'
              :dropdownStyle="{ maxHeight: '200px', overflow: 'auto' }"
              :replaceFields="{ title: 'name', key: 'id' }"
              :treeData='treeDataSource'
              @select='handleTreeSelect'
            />
          </a-card>
        </a-col>
        <a-col class='rightPart' :md='24 - 4' :sm='24'>
          <a-card id='rightPart'>
            <div class='table-page-search-wrapper'>
              <a-form layout='inline' @keyup.enter.native='searchQuery'>
                <a-row :gutter='24'>
                  <a-col :md='4' :sm='24'>
                    <a-form-item label='角色'>
                      <j-dict-select-tag
                        :triggerChange='true'
                        v-model='queryParam.roleId'
                        v-decorator.trim="['roleId']"
                        dictCode='sys_role,role_name,id'
                        placeholder='选择角色'
                      />
                    </a-form-item>
                  </a-col>
                  <a-col :md='7' :sm='24'>
                    <a-form-item label='工号或姓名'>
                      <a-input placeholder='请输入工号或姓名' v-model='queryParam.searchText' allowClear></a-input>
                    </a-form-item>
                  </a-col>
                  <a-col :md='13' :sm='24'>
                    <a-space>
                      <a-button @click='searchQuery'>查询</a-button>
                      <a-button type='primary' @click='handleAdd' v-has="'orginfo:add'">新增</a-button>
                      <a-button
                        :disabled='records.length != 1'
                        @click='handleEdit(records[0])'
                        v-has="'orginfo:edit'"
                      >
                        编辑
                      </a-button>
                      <a-button @click='handleSkillCompare'>技能项对比</a-button>
                      <a-dropdown :disabled='records.length != 1'>
                        <a-menu slot='overlay' @click='handleMenuClick'>
                          <a-menu-item key='1' v-has="'orginfo:monitor1'">
                            设置为班长
                          </a-menu-item>
                          <a-menu-item key='2' v-has="'orginfo:monitor2'">
                            设置为副班长
                          </a-menu-item>
                        </a-menu>
                        <a-button type='primary'> 设置为...
                          <a-icon type='down' />
                        </a-button>
                      </a-dropdown>
                      <!--                    <a-button :disabled="records.length != 1" @click="setWorkMonitor(1)" v-has="'orginfo:monitor1'">设置为班长</a-button>-->
                      <!--                    <a-button :disabled="records.length != 1" @click="setWorkMonitor(2)" v-has="'orginfo:monitor2'">设置为副班长</a-button>-->
                      <a-button :disabled='records.length < 1' @click='deleteRecord' v-has="'orginfo:delete'">删除
                      </a-button>
                    </a-space>
                  </a-col>
                </a-row>
                <!--              <a-row>
                                <a-col>
                                  <span class="table-page-search-submitButtons">
                                    <a-space>
                                      <a-button @click="searchQuery">查询</a-button>
                                      <a-button type="primary" @click="handleAdd" v-has="'orginfo:add'">新增</a-button>
                                      <a-button
                                        :disabled="records.length != 1"
                                        @click="handleEdit(records[0])"
                                        v-has="'orginfo:edit'"
                                      >
                                        编辑
                                      </a-button>
                                      <a-button :disabled="records.length != 1" @click="setWorkMonitor(1)" v-has="'orginfo:monitor1'">设置为班长</a-button>
                                      <a-button :disabled="records.length != 1" @click="setWorkMonitor(2)" v-has="'orginfo:monitor2'">设置为副班长</a-button>
                                      <a-button :disabled="records.length < 1" @click="deleteRecord" v-has="'orginfo:delete'">删除</a-button>
                                      &lt;!&ndash;<a-button style="margin-left: 8px" @click="deleteRecord">添加培训信息</a-button>&ndash;&gt;
                                      &lt;!&ndash;<a-button style="margin-left: 8px" @click="">技能项对比</a-button>&ndash;&gt;
                                    </a-space>
                                  </span>
                                </a-col>
                              </a-row>-->
              </a-form>
            </div>
            <div style='height: calc(100vh - 252px)'>
              <vxe-table
                border
                auto-resize
                max-height='100%'
                style='height: calc(100vh - 252px)'
                ref='listTable'
                :align='allAlign'
                :data='tableData'
                show-overflow='tooltip'
                @checkbox-change='checkboxChange'
                @checkbox-all='checkboxChange'
                :checkbox-config="{ highlight: true, range: true ,trigger: 'row',}"
                :edit-config="{ trigger: 'manual', mode: 'row' }"
              >
                <vxe-table-column type='checkbox' width='40' />
                <vxe-table-column field='workNo' title='工号' width='100' />
                <vxe-table-column field='username' title='用户名' width='150' align='left'
                                  header-align='center'></vxe-table-column>
                <vxe-table-column field='realname' align='left' header-align='center' title='姓名' width='100'>
                  <template v-slot='{ row }'>
                    <a @click.stop='handleDetail(row)'>{{ row.realname }}</a>
                  </template>
                </vxe-table-column>
                <vxe-table-column field='groupName' align='left' header-align='center' title='班组' width='120' />
                <!-- <vxe-table-column field="positionName" title="岗位" width="120"/> -->
                <vxe-table-column field='roleNames' title='角色' min-width='180' align='left' header-align='center' />
                <vxe-table-column field='positionName' align='left' header-align='center' title='职位' width='120'>
                  <template v-slot='{row}'>
                    <span v-if='row.type == 1'>工班长</span>
                    <span v-else-if='row.type == 2'>副工班长</span>
                  </template>
                </vxe-table-column>
                <vxe-table-column field='tags' title='技能标签' min-width='120' align='left' header-align='center' />
                <vxe-table-column field='certs' title='证书情况' min-width='120' align='left' header-align='center' />
                <vxe-table-column field='sex_dictText' title='性别' width='80' />
                <vxe-table-column field='phone' title='手机' width='120' />
                <vxe-table-column field='telephone' title='固定电话' width='120' />
                <!--<vxe-table-column field="positionWages" title="薪资" width="120"/>-->
              </vxe-table>
              <vxe-pager
                perfect
                :current-page.sync='queryParam.pageNo'
                :page-size.sync='queryParam.pageSize'
                :total='totalResult'
                :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
                @page-change='handlePageChange'
              ></vxe-pager>
            </div>
            <staff-info ref='staffInfoModel' @ok='searchUserPage'></staff-info>
          </a-card>
        </a-col>
      </a-row>
    </a-spin>
    <radar-skill ref='radarSkill'></radar-skill>
  </div>
</template>

<script>
import StaffInfo from './modules/orgStaff/StaffInfo'
import RadarSkill from '@views/tiros/group/information/RadarSkill'
import { deleteUser, getOrgTree, getUser, pageUser, setWorkMonitor, syncDeptAndUser } from '@api/tirosApi'

export default {
  components: { StaffInfo, RadarSkill },
  data() {
    return {
      records: [],
      treeDataSource: [],
      tableData: [],
      loading: false,
      loadingLeft: false,
      queryParam: {
        roleId: '',
        searchText: '',
        companyId: '',
        depotId: '',
        workshopId: '',
        groupId: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      allAlign: 'center'
    }
  },
  created() {
    this.queryTreeData()
  },
  methods: {
    handleMenuClick(click) {
      if (click.key === '1') {
        // this.handleAdd()
        this.setWorkMonitor(1)
      }
      if (click.key === '2') {
        this.setWorkMonitor(1)
      }
    },
    handleSkillCompare() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        let idsStr = selectRecords.map((obj) => obj.id).join(',')
        this.$refs.radarSkill.show(idsStr)
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handleDetail(row) {
      this.$refs.staffInfoModel.edit(row)
      this.$refs.staffInfoModel.title = '详情'
      this.$refs.staffInfoModel.isDisable = true
    },
    syncDeptAndUser() {
      let workshopId = this.queryParam.workshopId
      console.log('workshopId = ' + workshopId)
      if (workshopId) {
        this.loadingLeft = true
        syncDeptAndUser({
          workshopId: workshopId
        }).then((res) => {
          if (res.success) {
            this.queryTreeData()
            this.searchUserPage()
            this.$message.success(res.result)
          } else {
            this.$message.error(res.message)
          }
        }).finally(() => this.loadingLeft = false)
      } else {
        this.$message.error('需选中要同步的车间，请慎重!')
      }
    },
    checkboxChange(e) {
      this.records = e.records
    },
    queryTreeData() {
      getOrgTree()
        .then((res) => {
          if (res.success) {
            this.treeDataSource = res.result
            this.searchUserPage()
          }
        })
        .finally(() => {
          this.loading = false
        })
    },
    handleTreeSelect(selectedKeys, e) {
      this.queryParam.workshopId = ''
      if (selectedKeys.length) {
        this.resetQueryParamIds()
        if (e.node.dataRef.type === 1) {
          this.queryParam.companyId = e.node.dataRef.id
        } else if (e.node.dataRef.type === 2) {
          this.queryParam.depotId = e.node.dataRef.id
        } else if (e.node.dataRef.type === 3) {
          this.queryParam.workshopId = e.node.dataRef.id
        } else if (e.node.dataRef.type === 4) {
          this.queryParam.groupId = e.node.dataRef.id
        }
        this.searchUserPage()
      }
    },
    resetQueryParamIds() {
      this.queryParam.companyId = ''
      this.queryParam.depotId = ''
      this.queryParam.workshopId = ''
      this.queryParam.groupId = ''
    },
    searchUserPage() {
      this.loading = true
      pageUser(this.queryParam).then((res) => {
        if (res.success) {
          this.loading = false
          this.totalResult = res.result.total
          this.records = []
          this.tableData = res.result.records
        }
      })
    },
    handlePageChange({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.searchUserPage()
    },
    searchQuery() {
      this.searchUserPage()
    },
    searchReset() {
    },
    selectAllEvent({ checked, records }) {
      console.log(checked ? '所有勾选事件' : '所有取消事件', records)
    },
    selectChangeEvent({ checked, records }) {
      console.log(checked ? '勾选事件' : '取消事件', records)
    },
    getSelectEvent() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      this.$XModal.alert(selectRecords.length)
    },
    setWorkMonitor(type) {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length === 1) {
        let record = selectRecords[0]
        this.$confirm({
          content: `是否设置选中人员为${type == 1 ? '工班长' : '副工班长'}？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            setWorkMonitor({
              groupId: record.groupId,
              userId: record.id,
              type: type
            }).then((res) => {
              if (res.success) {
                this.$message.success('操作成功')
                this.searchUserPage()
              }
            })
          }
        })
      } else {
        this.$message.error('请选中一条数据')
      }
    },
    deleteRecord() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length > 0) {
        this.$confirm({
          content: `是否删除选中${selectRecords.length}数据？`,
          okText: '确定',
          cancelText: '取消',
          onOk: () => {
            /*this.tableData = this.tableData.filter(item => {
              let idList = selectRecords.map(s => s.id)
              return !idList.includes(item.id)
            })*/
            let ids = selectRecords
              .map((r) => {
                return r.id
              })
              .join(',')
            deleteUser({ ids: ids }).then((res) => {
              if (res.success) {
                this.$message.success('删除成功')
                this.searchUserPage()
              }
            })
          }
        })
      } else {
        this.$message.error('尚未选中任何数据!')
      }
    },
    handleAdd() {
      this.$refs.staffInfoModel.add()
      this.$refs.staffInfoModel.title = '新增人员信息'
    },
    handleEdit(row) {
      let getUserParam = { id: row.id }
      getUser(getUserParam).then((res) => {
        console.log(res)
        if (res.success) {
          this.$refs.staffInfoModel.edit(res.result)
          this.$refs.staffInfoModel.title = '修改人员信息'
        }
      })
    }
  }
}
</script>

<style lang='less'>
#leftPart {
  .ant-card-body {
    padding: 0 0 24px 0;
    height: calc(100vh - 120px);
    overflow-y: auto;
  }
}

#rightPart {
  .ant-card-body {
    padding: 10px;
    height: calc(100vh - 120px);
  }
}

.titleDiv {
  width: 100%;
  background: #eee;
  padding: 10px;
  text-align: center;
}

/*.leftPart {
  border: 1px solid #eee;
  border-radius: 10px;
  overflow: hidden;
  margin-right: 20px;
  min-height: 70vh;
}*/

.centerDiv {
  width: 100%;
  text-align: center;
  padding: 10px 0;
}

.updateBtn {
  width: 90%;
}

/*.rightPart {
  border: 1px solid #eee;
  border-radius: 10px;
  overflow: hidden;
  padding: 10px;
}*/
</style>