<template style='height: calc(100% - 5px)'>
  <div style="height: calc(100% - 5px); overflow-y: auto; overflow-x: hidden" @click="hiddenFilter">
    <div :style="{ position: 'absolute', top: 1, right: 0, zIndex: 999, cursor: 'pointer' }" @click="onShowFilter">
      <img src="~@/assets/tiros/images/magic.svg" />
    </div>
    <!-- 查询区域 -->
    <div ref="filterBox" class="filterBox" @click.stop>
      <a-form layout="horizontal" :label-col="{ span: 6 }" :wrapperCol="{ span: 18 }">
        <a-row :gutter="16">
          <a-col :md="24" :sm="24">
            <a-form-item label="车辆段">
              <j-dict-select-tag v-model="queryParam.depotId" placeholder="请选择" dictCode="bu_mtr_depot,name,id" />
            </a-form-item>
          </a-col>
          <a-col :md="24" :sm="24">
            <a-form-item label="车间">
              <j-dict-select-tag v-model="queryParam.workshopId" dictCode="bu_mtr_workshop,name,id" />
            </a-form-item>
          </a-col>

          <a-col :md="24" :sm="8">
            <a-button type="primary" @click="findList" block>切换条件</a-button>
          </a-col>
        </a-row>
      </a-form>
    </div>
    <div>
      <a-list :grid="{ gutter: 24, column: 4 }" :data-source="tableData">
        <a-list-item slot="renderItem" slot-scope="item">
          <div class="bor">
            <h4 class="titleBar">
              {{ item.workstationName}}
              <span v-if="item.task"
                >({{
                  stationMap.get(item.workstationId).task.progress >= 0
                    ? stationMap.get(item.workstationId).task.progress + '%'
                    : '无作业'
                }})</span
              >
              <span v-else>(无作业)</span>
            </h4>
            <div style="text-align: left; margin-top: -7px">
              <a-row :gutter="24" :style="{ fontSize: '13px', width: '100%', margin: '0' }" class="numberStyle">
                <a-col
                  :md="8"
                  :style="{ marginBottom: '10px', height: '170px' }"
                  :class="[item.task && stationMap.get(item.workstationId).task.progress >= 0 ? 'oneCol' : 'notTask']"
                >
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      任务总数
                      <span v-if="stationMap.get(item.workstationId).task">{{
                        stationMap.get(item.workstationId).task.total > 0
                          ? stationMap.get(item.workstationId).task.total
                          : ''
                      }}</span>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      必换件数
                      <span v-if="item.mustReplace">{{
                        stationMap.get(item.workstationId).mustReplace.total > 0
                          ? stationMap.get(item.workstationId).mustReplace.total
                          : ''
                      }}</span>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      故障数量
                      <span v-if="item.fault">{{
                        stationMap.get(item.workstationId).fault.total > 0
                          ? stationMap.get(item.workstationId).fault.total
                          : ''
                      }}</span>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      作业人数
                      <span v-if="item.worker">{{
                        stationMap.get(item.workstationId).worker.quantity > 0
                          ? stationMap.get(item.workstationId).worker.quantity
                          : ''
                      }}</span>
                    </a-col>
                  </a-row>
                </a-col>
                <a-col
                  :md="8"
                  :style="{ marginBottom: '10px', height: '170px' }"
                  :class="[item.task && stationMap.get(item.workstationId).task.progress >= 0 ? 'twoCol' : 'notTask']"
                >
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      完成任务
                      <span v-if="item.task">{{
                        stationMap.get(item.workstationId).task.finish > 0
                          ? stationMap.get(item.workstationId).task.finish
                          : ''
                      }}</span>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      已换件数
                      <span v-if="item.mustReplace">{{
                        stationMap.get(item.workstationId).mustReplace.replaced > 0
                          ? stationMap.get(item.workstationId).mustReplace.replaced
                          : ''
                      }}</span>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      已处理数
                      <span v-if="item.fault">{{
                        stationMap.get(item.workstationId).fault.handled > 0
                          ? stationMap.get(item.workstationId).fault.handled
                          : ''
                      }}</span>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      已安排数
                      <span v-if="item.worker">{{
                        stationMap.get(item.workstationId).worker.arranged > 0
                          ? stationMap.get(item.workstationId).worker.arranged
                          : ''
                      }}</span>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      <div style="text-align: center; margin-bottom: 10px; color: #4fa7f0; font-weight: bold">
                        {{ item.groupName }}
                      </div>
                    </a-col>
                  </a-row>
                </a-col>
                <a-col
                  :md="8"
                  :style="{ marginBottom: '10px', height: '170px' }"
                  :class="[item.task && stationMap.get(item.workstationId).task.progress >= 0 ? 'threeCol' : 'notTask']"
                >
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      逾期任务
                      <span v-if="item.task">{{
                        stationMap.get(item.workstationId).task.delay > 0
                          ? stationMap.get(item.workstationId).task.delay
                          : ''
                      }}</span>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      未换件数
                      <span v-if="item.mustReplace">{{
                        stationMap.get(item.workstationId).mustReplace.notReplaced > 0
                          ? stationMap.get(item.workstationId).mustReplace.notReplaced
                          : ''
                      }}</span>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      未处理数
                      <span v-if="item.fault">{{
                        stationMap.get(item.workstationId).fault.unhandled > 0
                          ? stationMap.get(item.workstationId).fault.unhandled
                          : ''
                      }}</span>
                    </a-col>
                  </a-row>
                  <a-row :gutter="24">
                    <a-col :style="{ marginBottom: '10px' }">
                      未安排数
                      <span v-if="item.worker">{{
                        stationMap.get(item.workstationId).worker.notArranged > 0
                          ? stationMap.get(item.workstationId).worker.notArranged
                          : ''
                      }}</span>
                    </a-col>
                  </a-row>
                </a-col>
              </a-row>
            </div>
          </div>
        </a-list-item>
      </a-list>
    </div>
  </div>
</template>

<script>
import { getWorkstation, getWorkstationInfo } from '@api/tirosKanbanApi'
import Icons from '@views/system/modules/icon/Icons'

const sleep = (timeout = 1000) =>
  new Promise((resolve, reject) => {
    setTimeout(resolve, timeout)
  })

export default {
  name: 'StationWorkshopPage',
  components: { Icons },
  data() {
    return {
      tableData: [],
      timer: null,
      queryParam: {
        depotId: '',
        workshopId: '',
      },
      allAlign: 'center',
      stationMap: new Map(),
    }
  },
  created() {
    this.findList()
  },
  destroyed() {
    clearInterval(this.timer)
  },
  methods: {
    hiddenFilter() {
      this.$refs.filterBox.style.display = 'none'
    },
    onShowFilter(e) {
      if (this.$refs.filterBox.style.display === 'block') {
        this.$refs.filterBox.style.display = 'none'
      } else {
        this.$refs.filterBox.style.top = e.pageY + 'px'
        this.$refs.filterBox.style.left = e.pageX - 270 + 'px'
        this.$refs.filterBox.style.display = 'block'
      }
      e.stopPropagation()
    },
    findList() {
      getWorkstation(this.queryParam)
        .then((res) => {
          this.tableData = res.result
          console.log(this.tableData.find((item) => {  
             return item.workstationName == '静态调试工位'; 
          }))
          res.result.forEach((r) => {
            this.stationMap.set(r.workstationId, r)
          })
        })
        .finally(() => {
          this.getWorkstationDetail()
        })
    },
    getWorkstationInfo(param, item) {
      getWorkstationInfo(param).then((re) => {
        if (re.success) {
          this.stationMap.get(item.workstationId).fault = re.result.fault
          this.stationMap.get(item.workstationId).mustReplace = re.result.mustReplace
          this.stationMap.get(item.workstationId).task = re.result.task
          this.stationMap.get(item.workstationId).worker = re.result.worker
        }
      })
    },
    getWorkstationDetail() {
      this.tableData.forEach((item) => {
      let param = { groupId: item.groupId, workstationId: item.workstationId }
      this.getWorkstationInfo(param, item)
      })
      this.loopTimer()
    },
    async timingWorkstationDetail(arr) {
      for (let i = 0; i < arr.length; i++) {
        let item = arr[i]
        let param = { groupId: item.groupId, workstationId: item.workstationId }
        await sleep(1000)
        this.getWorkstationInfo(param, item)
      }
    },
    loopTimer() {
      clearInterval(this.timer)
      this.timer = setInterval(() => {
        this.timingWorkstationDetail(this.tableData)
      }, 1000 * 60)
    },
  },
}
</script>

<style scoped lang='less'>
.titleBar {
  width: 100%;
  font-size: 14px;
  padding: 8px;
  background: #eee;
  text-align: center;
  margin-top: -1px;
  border-top-left-radius: 5px;
  border-top-right-radius: 5px;
}

.bor {
  border: 1px #e2e2e2 solid !important;
  border-radius: 5px;
  margin-bottom: 20px;
  font-size: 14px;
  height: 210px;
}

.numberStyle span {
  color: red;
  font-size: initial;
}

.oneCol {
  background-color: #fef7ed;
}

.twoCol {
  background-color: #f0fddc;
}

.threeCol {
  background-color: #fcedee;
}
.notTask {
  background-color: #eeeeee;
}

.filterBox {
  top: 100px;
  width: 270px;
  position: absolute;
  z-index: 999;
  padding: 10px;
  padding-right: 15px;
  background-color: #eeeeee;
  box-shadow: -3px 3px 3px 3px #cbcaca;
  display: none;
}
</style>