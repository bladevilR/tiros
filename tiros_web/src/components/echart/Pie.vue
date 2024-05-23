<template>
  <div :id="id" :style="{ padding: '10px 0 32px 32px', height: height + 'px' }"></div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'Pie',
  props: {
    id: {
      type: String,
      default: 'pieChart',
    },
    title: {
      type: String,
      default: '',
    },
    subTitle: {
      type: String,
      default: '',
    },
    height: {
      type: Number,
      default: 400,
    },
    dataSource: {
      type: Array,
      default: () => [],
    },
    tipName:{
      type: String,
      default: '故障数量：',
    }
  },
  watch: {
    dataSource: {
      handler(val) {
        setTimeout(() => {
          this.initChart(this.setValue(val))
        }, 200)
      },
      deep: true,
    },
  },
  data() {
    return {
      myPieChart: '',
    }
  },
  mounted() {
    setTimeout(() => {
      this.initChart(this.setValue(this.dataSource))
      window.onresize = () => {
        //  根据窗口大小调整曲线大小
        this.myPieChart.resize()
      }
    }, 200)
  },
  methods: {
    setValue(data) {
      let newArr = []
      if (data.length) {
        for (let i = 0; i < data.length; i++) {
          const item = data[i]
          newArr.push({
            name: item.item,
            value: item.count,
          })
        }
      }
      return newArr
    },
    initChart(data) {
      let that = this
      if (!data || data.length < 1) {
        return
      }
      that.myPieChart = echarts.init(document.getElementById(this.id))
      let option = {
        title: {
          text: that.title,
          subtext: that.subTitle,
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)',
        },
        legend: {
          type: 'scroll',
          orient: 'vertical',
          left: 'left',
          top: 60,
        },
        series: [
          {
            name: this.tipName,
            type: 'pie',
            radius: '80%',
            center: ['60%', '50%'],
            label: {
              normal: {
                show: false,
                formatter: '{b}:{c}: ({d}%)',
                textStyle: {
                  fontWeight: 'normal',
                  fontSize: 12,
                },
              },
            },
            data: data,
          },
        ],
      }
      that.myPieChart.setOption(option)
    },
  },
}
</script>