<template>
  <div :style="{ padding: '10px 0 32px 32px' }">
    <h4>{{ title }}</h4>
    <p :style="{ color:'#CCCCCC', marginBottom: '20px' }">{{ subTitle }}</p>
    <v-chart :forceFit="true" :height="height" :data="dataSource">
      <v-coord type="rect" direction="LB"/>
      <v-tooltip/>
      <v-axis dataKey="item" :label="label"/>
      <v-bar position="item*count"/>
    </v-chart>
  </div>
</template>

<script>
// const DataSet = require('@antv/data-set')

// const sourceData = [
//   { country: '中国', population: 131744 },
//   { country: '印度', population: 104970 },
//   { country: '美国', population: 29034 },
//   { country: '印尼', population: 23489 },
//   { country: '巴西', population: 18203 }
// ]

// const dv = new DataSet.View().source(sourceData)
// dv.transform({
//   type: 'sort',
//   callback(a, b) {
//     return a.population - b.population > 0
//   }
// })
// const data = dv.rows

// const label = { offset: 12 }

export default {
  props: {
    title: {
      type: String,
      default: ''
    },
    subTitle: {
      type: String,
      default: ''
    },
    dataSource: {
      type: Array,
      required: true
    },
    height: {
      type: Number,
      default: 400
    }
  },
  data() {
    return {
      label: {
        offset: 6,
        formatter: function(param) {
          let value
          if (param.length <= 5) {
            value = param
            return value
          } else {
            value = param.slice(0, 5) + '...'
            return value
          }
        }
      }
    }
  }
}
</script>
