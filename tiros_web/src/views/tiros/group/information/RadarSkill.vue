<template>
  <a-modal
    title="技能项对比"
    :width="'80%'"
    :visible="visible"
    dialogClass="fullDialog no-title no-footer"
    @cancel="handleCancel"
  >
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
  </a-modal>
</template>

<script>
import { getUser, skillCompare } from '@api/tirosApi'
  import DataSet from '@antv/data-set'
import { everythingIsEmpty } from '@/utils/util'

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
    dataKey: 'score',
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
      dataKey: 'score',
      min: 0,
      max: 100
    }
  ]

  export default {
    name: 'RadarSkill',
    props: {
      height: {
        type: Number,
        default: 600
      },
    },
    data() {
      return {
        axis1Opts,
        axis2Opts,
        scale,
        data: [],
        visible:false,
        ids:'',
      }
    },
   /* watch: {
      skillList(newVal) {
        if (newVal.length === 0) {
          this.data = this.skillList
        } else {
          this.data = newVal
        }
        const dv = new DataSet.View().source(this.data);
        dv.transform({
          type: 'fold',
          fields: ['score',],
          key: 'user',
          value: 'score',
        });
        this.data = dv.rows;
      }
    },*/
    methods:{
      show(ids){
        this.visible=true
        this.ids=ids
        this.findList()
      },
      findList(){
        skillCompare({ids:this.ids}).then((res)=>{
          if(res.success){
            this.data =res.result
            if(!everythingIsEmpty(this.data)){
              let fields=this.data[0].userName
              const dv = new DataSet.View().source(this.data);
              dv.transform({
                type: 'fold',
                fields: fields,
                key: 'user',
                value: 'score',
              });
              this.data = dv.rows;
            }
          }
        })
      },
      // 关闭
      handleCancel () {
        this.close()
      },
      close () {
        this.$emit('close')
        this.visible = false
        this.activeKey = '1'
      },
    }
  }
</script>

<style scoped>

</style>