<template>
  <v-chart :forceFit="true" :height="height" :data="data" :padding="[20, 20, 95, 20]" :scale="scale">
    <v-tooltip />
    <v-axis :dataKey="axis1Opts.dataKey" :line="axis1Opts.line" :tickLine="axis1Opts.tickLine" :grid="axis1Opts.grid" />
    <v-axis :dataKey="axis2Opts.dataKey" :line="axis2Opts.line" :tickLine="axis2Opts.tickLine" :grid="axis2Opts.grid" />
    <v-legend dataKey="user" marker="circle" :offset="30" />
    <v-coord type="polar" radius="0.8" />
    <v-line position="skillName*score" color="user" :size="2" />
    <v-point position="skillName*score" color="user" :size="4" shape="circle" />
    <v-area position="skillName*score" color="user" />
  </v-chart>
</template>

<script>
  import { getUser } from '@api/tirosApi'
  import DataSet from '@antv/data-set'

  const axis1Opts = {
    dataKey: 'skillName',
    line: null,
    tickLine: null,
    grid: {
      lineStyle: {
        lineDash: null
      },
      hideFirstLine: false,
    },
  };
  const axis2Opts = {
    dataKey: '评分',
    line: null,
    tickLine: null,
    grid: {
      type: 'polygon',
      lineStyle: {
        lineDash: null,
      },
    },
  };
  const scale = [
    {
      dataKey: '评分',
      min: 0,
      max: 100
    }
  ]



  export default {
    name: 'Radar',
    props: {
      height: {
        type: Number,
        default: 254
      },
      skillList: {
        type: Array,
        default: () => []
      },
      userId:{
        type:String,
        default: ''
      }
    },

    mounted () {
      // 注释by jakgong  技能列表不是已经传过来了么
      getUser({id:this.userId}).then((res)=>{
        if(res.success){
          this.$nextTick(()=>{
            this.$emit('update:skillList', res.result.skillList)
          })
        }
      })
    },


    data() {
      return {
        axis1Opts,
        axis2Opts,
        scale,
        data: [],
      }
    },
    watch: {
      skillList(newVal) {
        if (newVal.length === 0) {
          this.data = this.skillList
        } else {
          this.data = newVal
        }
        const dv = new DataSet.View().source(this.data);
        dv.transform({
          type: 'fold',
          fields: ['评分'],
          key: 'user',
          value: '评分',
        });
        this.data = dv.rows;
      }
    }
  }
</script>

<style scoped>

</style>