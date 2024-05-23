<template>
  <div :id="id" :style="{ padding: '10px 0 32px 32px', height: height + 'px' }"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  props: {
    id: {
      type: String,
      default: 'barMultidChart',
    },
    title: {
      type: String,
      default: '',
    },
    subTitle: {
      type: String,
      default: '',
    },
    dataSource: {
      type: Array,
      required: true,
    },
    fields: {
      type: Array,
      default: () => ['Jan.', 'Feb.', 'Mar.', 'Apr.', 'May', 'Jun.', 'Jul.', 'Aug.'],
    },
    height: {
      type: Number,
      default: 400,
    },
    rotate: {
      type: Number,
      default: 0,
    },
  },
  watch: {
    dataSource: {
      handler(val) {
        setTimeout(() => {
          this.initChart(val)
        }, 200)
      },
      deep: true,
    },
  },
  data() {
    return {
      myBarMultidChart: '',
    }
  },
  mounted() {
    setTimeout(() => {
      this.initChart(this.dataSource)
      window.onresize = () => {
        //  根据窗口大小调整曲线大小
        this.myBarMultidChart.resize()
      }
    }, 200)
  },
  methods: {
    refactoredData(data) {
      //   重构数据
      let dataSource = []
      if (data.length) {
        for (const key in data) {
          let newArr = {
            name: '',
            type: 'bar',
            data: [],
          }
          if (Object.hasOwnProperty.call(data, key)) {
            const arrItem = data[key]
            for (const key in arrItem) {
              if (Object.hasOwnProperty.call(arrItem, key)) {
                const item = arrItem[key]
                if (key == 'type') {
                  newArr.name = item
                } else {
                  newArr.data.push(item)
                }
              }
            }
          }
          dataSource.push(newArr)
        }
      }
      return dataSource
    },
    initChart(data) {
      let that = this
      if (!data || data.length < 1) {
        return
      }
      let dataSource = this.refactoredData(data)
      that.myBarMultidChart = echarts.init(document.getElementById(this.id))
      let option = {
        title: {
          text: that.title,
          subtext: that.subTitle,
        },
        tooltip: {
          trigger: 'axis',
        },
        legend: {
          bottom: 0,
          data: dataSource.map((d) => {
            return d.name
          }),
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '13%',
          containLabel: true,
        },
        xAxis: {
          type: 'category',
          axisLabel: {
            rotate: that.rotate,
            interval: 0,
          },
          data: that.fields,
        },
        yAxis: {
          type: 'value',
        },
        series: dataSource,
      }
      that.myBarMultidChart.setOption(option)
    },
  },
}
</script>
