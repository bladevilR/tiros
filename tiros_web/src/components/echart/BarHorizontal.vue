<template>
  <div :id="id" :style="{ padding: '10px 0 32px 32px', height: height + 'px' }"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  props: {
    id: {
      type: String,
      default: 'barChart',
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
    height: {
      type: Number,
      default: 400,
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
      myBarChart: '',
    }
  },
  mounted() {
    setTimeout(() => {
      this.initChart(this.dataSource)
      window.onresize = () => {
        //  根据窗口大小调整曲线大小
        this.myBarChart.resize()
      }
    }, 200)
  },
  methods: {
    initChart(data) {
      let that = this
      if (!data || data.length < 1) {
        return
      }
      that.myBarChart = echarts.init(document.getElementById(this.id))
      let option = {
        title: {
          text: that.title,
          subtext: that.subTitle,
        },
        tooltip: {
          trigger: 'axis',
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true,
        },
        xAxis: {
          type: 'value',
          axisLine: {
            show: true,
          },
        },
        yAxis: {
          type: 'category',
          data: data.map((d) => {
            return d.item
          }),
        },
        series: [
          {
            type: 'bar',
            showBackground: true,
            backgroundStyle: {
              color: 'rgba(180, 180, 180, 0.2)',
            },
            data: data.map((d) => {
              return d.count
            }),
          },
        ],
      }
      that.myBarChart.setOption(option)
    },
  },
}
</script>
