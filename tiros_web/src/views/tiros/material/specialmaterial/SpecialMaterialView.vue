<template>
  <a-modal
    :title="title"
    width="70%"
    :visible="visible"
    :centered="true"
    :footer="null"
    :destroyOnClose="true"
    @cancel="visible = false"
  >
    <h2 align="center" style="margin-top: 15px">{{ `${dataRow.name}设备本月使用情况` }}</h2>
    <a-calendar id="specialCalendar" mode="month" :disabledDate="() => true" @panelChange="onPanelChange" :value="activeCalendar">
      <div slot="headerRender" class="callendar-header">
        <div class="view">{{activeCalendar.format('YYYY年MM月')}}</div>
        <a-radio-group v-model="active" @change="onGroupChange" style="margin: 15px">
          <a-radio-button :value="-1"> 上月 </a-radio-button>
          <a-radio-button :value="0"> 当月 </a-radio-button>
          <a-radio-button :value="1"> 下月 </a-radio-button>
        </a-radio-group>
      </div>
      <a-spin :spinning="spinning" />
      <div
        slot="dateFullCellRender"
        :class="{ [cellStatusAttr(value)]: true }"
        class="ant-fullcalendar-date"
        slot-scope="value"
      >
        <div class="ant-fullcalendar-value">{{ $moment(value).format('MM-DD') }}</div>
        <div class="ant-fullcalendar-content">
          <div class="view-cell">
            <GyText v-if="cellStatusText(value)" :item="cellStatusText(value)"/>
          </div>
        </div>
      </div>
    </a-calendar>
  </a-modal>
</template>

<script>
// import moment from 'moment'
// import 'moment/locale/zh-cn'
import { getSpecassetplan } from '@api/tirosMaterialApi'
import GyText from '@/views/tiros/material/specialmaterial/GyText.vue'

export default {
  name: 'SpecialMaterialView',
  components: {GyText},
  data() {
    return {
      title: '',
      visible: false,
      activeCalendar: this.$moment().add(this.active ,'M'),
      dataRow: {},
      queryParam: {
        id: null,
        materialId: null,
        materialName: null,
        useCategory: null,
      },
      active: 0,
      specassetPlan: {},
      spinning: false
    }
  },
  created() {},
  methods: {
    findList(){
      this.spinning = true
      getSpecassetplan({
        specAssetId: this.dataRow.specAssetId || this.dataRow.id,
        year: this.$moment().format('YYYY'),
        month: this.$moment().add(this.active ,'M').format('MM'),
      }).then((res) => {
        this.activeCalendar = this.$moment().add(this.active ,'M')
        if (res.success) {
          console.log(res.result)
          this.specassetPlan = res.result
        } else {
          this.$message.error(res.message)
        }
      }).finally(()=>{
        this.spinning = false
      })
    },
    showModal(row) {
      console.log(row)
      this.dataRow = row
      this.findList()
      this.visible = true
    },
    onPanelChange(value, mode) {
    },
    // 关闭
    handleCancel() {
      this.close()
    },
    close() {
      this.$emit('close')
      this.visible = false
    },
    onPanelChange(date, mode) {
      return false
    },
    onGroupChange(){
      this.findList()
    },
    currentDate(moment) {
      return true
    },
    cellStatusAttr(moment) {
      let className = 'status-free'
      let valueText = this.specassetPlan[this.$moment(moment).format('M-D')]
      if (valueText) {
        console.log(this.$moment(moment).format('M-D'),valueText)
        if (valueText.type == 2) {
          className = 'status-use'
        } else if (valueText.type == 3) {
          className = 'status-repair'
        }
      }
      if (this.$moment(moment).format('M') !== this.activeCalendar.format('M')) {
        className = `${className} not-is-momth`
      }
      if ([6,7].indexOf(this.$moment(moment).isoWeekday()) !== -1){
        className = `${className} is-work`
      }
      return className
    },
    cellStatusText(moment) {
      return this.specassetPlan[this.$moment(moment).format('M-D')] || ''
    },
  },
}
</script>
<style lang="less" scoped>
.callendar-header{
  display: flex;
  flex-wrap: nowrap;
  justify-content: flex-end;
  align-items: center;
  border-bottom: 1px solid #dddddd;

  .view{
    margin-right: auto;
    margin-left: 15px;
    color: #000;
    font-size: 24px;
  }
}


.ant-fullcalendar-disabled-cell {
  .ant-fullcalendar-date {
    cursor: initial;
    color: #000;
    border: 2px solid #e8e8e8;
    margin: 0;
    box-sizing: content-box;
    font-size: 18px;
    height: 78px;

    .ant-fullcalendar-value {
      cursor: initial;
      color: #000;
      text-align: left;
    }

    .ant-fullcalendar-content {
      height: auto;
      display: flex;
      justify-content: flex-end;
      align-items: flex-end;
      height: 55px;
      .view-cell {
        font-size: 18px;
        width: 100%;
      }
    }

    &.status-free {
      .ant-fullcalendar-content {
        color: green;
      }
    }

    &.status-use {
      .ant-fullcalendar-content {
        color: #d8d800;
      }
    }
    &.status-repair {
      .ant-fullcalendar-content {
        color: red;
      }
    }

    &.not-is-momth{
      background: #eee !important;
    }

    &.is-work{
      background: rgba(255, 0, 0, 0.03);
    }
  }
}
</style>
<style lang="less">
#specialCalendar{
  .ant-fullcalendar-column-header-inner{
    text-align: center !important;
    font-size: 18px;
    padding: 5px 0;
  }
}
</style>