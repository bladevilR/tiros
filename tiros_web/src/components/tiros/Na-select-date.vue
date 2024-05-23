<template>
  <div class="s-na-select-date" :style="naStyle">
    <a-form-item label="日期:" :style="{ height: '32px' }">
      <a-row :gutter="6">
        <a-col :md="6">
          <j-dict-select-tag
            :allowClear="true"
            :hiddenArray="hiddenArray"
            class="na-select-clear"
            v-model="queryParam.dateType"
            placeholder="请选择"
            :dictCode="dictDateCode"
            @select="dateTypeChange"
          />
        </a-col>
        <a-col
          :md="Number(queryParam.dateType) === 2 ? 6 : 18"
          v-if="Number(queryParam.dateType) === 1 || Number(queryParam.dateType) === 2"
        >
          <a-form-item :style="{ height: '32px' }">
            <a-date-picker
              mode="year"
              placeholder="请选择年份"
              format="YYYY"
              :showToday="true"
              :allowClear="true"
              v-model="queryParam.year"
              @panelChange="changeYear"
              @focus="yearOpen = true"
              :open="yearOpen"
              :defaultPickerValue="this.$moment(new Date())"
              style="width: 100%"
            />
          </a-form-item>
        </a-col>
        <a-col :md="12" v-if="Number(queryParam.dateType) === 2">
          <a-form-item :style="{ height: '32px' }">
            <j-dict-select-tag
              v-model="queryParam.quarter"
              class="na-select-clear"
              placeholder="请选择"
              dictCode="bu_quarter_type"
              :allowClear="true"
              @select="dateChange"
            />
          </a-form-item>
        </a-col>
        <a-col :md="18" v-if="Number(queryParam.dateType) === 3">
          <a-form-item :style="{ height: '32px' }">
            <a-month-picker
              placeholder="选择月份"
              v-model="queryParam.month"
              format="YYYY-MM"
              :showToday="true"
              :allowClear="true"
              @change="dateChange"
              :defaultPickerValue="this.$moment(new Date())"
              style="width: 100%"
            >
            </a-month-picker>
          </a-form-item>
        </a-col>
        <a-col :md="18" v-if="Number(queryParam.dateType) === 4">
          <a-form-item :style="{ height: '32px' }">
            <a-range-picker
              :showToday="true"
              :allowClear="true"
              v-model="queryParam.dateRef"
              format="YYYY-MM-DD"
              style="width: 100%"
              @change="dateChange"
            />
          </a-form-item>
        </a-col>
      </a-row>
    </a-form-item>
  </div>
  <!-- :style="{ 'margin-bottom': '0px !important' }" -->
</template>
<script>
export default {
  name: 'na-select-date',
  data() {
    return {
      yearOpen: false,
      queryParam: {
        dateType: this.defaultType,
        quarter: 1,
        year: this.$moment(new Date()),
        month: this.$moment(new Date()),
        dateRef: [this.$moment(new Date()), this.$moment(new Date())],
      },
      result: {
        dateType: 1,
        quarter: 1,
        year: null,
        month: null,
        dateRef: [null, null],
      },
    }
  },
  model: {
    prop: 'modelVal', //指向props的参数名
    event: 'change', //事件名称
  },
  props: {
    hiddenArray: Array,
    dictDateCode:{
      type:String,
      default: 'bu_date_type'
    },
    defaultType: {
      type: Number,
      default: 1,
    },
    modelVal: {
      dateType: 1,
      quarter: 1,
      year: null,
      month: null,
      dateRef: [],
    },
    naStyle: {
      type: String,
      default: '',
    },
  },
  methods: {
    dateTypeChange() {
      this.dateChange()
    },
    changeYear(value, mode) {
      this.yearValue = value
      this.queryParam.year = value.format('YYYY')
      this.yearOpen = false
      this.dateChange()
    },
    reset() {
      this.queryParam.dateType = null
      this.queryParam.quarter = 1
      this.queryParam.year = this.$moment(new Date())
      this.queryParam.month = this.$moment(new Date())
      this.queryParam.dateRef = [this.$moment(new Date()), this.$moment(new Date())]
    },
    dateChange() {
      this.yearOpen = false
      if (this.queryParam.dateType){
        this.$set(this.result, 'dateType', Number(this.queryParam.dateType))
      } else {
        this.$set(this.result, 'dateType', null)
      }
      if (this.result.dateType === 3) {
        this.$set(this.result, 'year', Number(this.queryParam.month.format('YYYY')))
      } else {
        this.$set(
          this.result,
          'year',
          typeof this.queryParam.year === 'object'
            ? Number(this.queryParam.year.format('YYYY'))
            : Number(this.queryParam.year)
        )
      }
      this.$set(
        this.result,
        'month',
        typeof this.queryParam.month === 'object'
          ? Number(this.queryParam.month.format('MM'))
          : Number(this.queryParam.month)
      )
      this.result.quarter = Number(this.queryParam.quarter)
      this.result.dateRef = [
        this.queryParam.dateRef[0].format('YYYY-MM-DD'),
        this.queryParam.dateRef[1].format('YYYY-MM-DD'),
      ]
      this.$set(
        this.result.dateRef,
        0,
        typeof this.queryParam.dateRef[0] === 'object'
          ? this.queryParam.dateRef[0].format('YYYY-MM-DD')
          : this.queryParam.dateRef[0]
      )
      this.$set(
        this.result.dateRef,
        1,
        typeof this.queryParam.dateRef[1] === 'object'
          ? this.queryParam.dateRef[1].format('YYYY-MM-DD')
          : this.queryParam.dateRef[1]
      )
      this.$emit('change', this.result)
    },
  },
}
</script>