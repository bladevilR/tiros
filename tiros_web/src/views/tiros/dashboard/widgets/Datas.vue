<template>
  <a-row :gutter="24" :style="{ position: 'relative', top: '50%', transform: 'translateY(-53%)' }">
    <a-col :md="8" v-for="(item, index) in dataSource" style="text-align: center" :key="index">
      <div class="circle" :style="`border-color:${item.itemColor}`">
        <div style="margin-top: 28px" :style="`color:${item.itemColor}`">
          <span style="font-weight: bold">{{ item.itemValue }}</span
          ><br />
          {{ item.itemTitle }}
        </div>
      </div>
    </a-col>
    <a-empty v-if="dataSource.length == 0" />
  </a-row>
</template>

<script>
import { getAlert, getData } from '@api/tirosKanbanApi'

export default {
  name: 'Datas',
  props: {
    autoRefresh: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      dataSource: [],
      colorlist: [ 
        '#58A48F',
        '#14BDFF',
        '#15bb15',
        '#8552a1',
        '#FFAC71',
        '#00CCE1',
      ],
      timer: null,
    }
  },
  created() {
    this.loadData()
  },
  activated() {
    if (this.autoRefresh && !this.timer) {
      this.timer = setInterval(() => {
        this.loadData()
        // this.initGantt()
      }, 1000 * 60)
    }
  },
  methods: {
    loadData() {
      getData().then((res) => {
        console.log(res.result)
        this.dataSource = res.result;
        Array.from(this.dataSource,(item,index)=>{
          if(this.colorlist[index]){
            item.itemColor = this.colorlist[index]
          }
        })
      })
    },
    jumppage(e) {
      if (e == 'wlkcyj') {
        this.$router.push({ path: `/tiros/material/earlywarning` })
      } else if (e == 'qjsjyj') {
        this.$router.push({ path: `/tiros/material/equipment/needcheck` })
      } else if (e == 'wzyxyj') {
        this.$router.push({ path: `/tiros/material/earlywarning` })
      } else if (e == 'bjzbqyj') {
        this.$router.push({ path: `/tiros/outsource/quality` })
      } else if (e == 'clsjyj') {
        this.$router.push({ path: `/tiros/quality/earlywarningcheck` })
      } else if (e == 'wwyqyj') {
        this.$router.push({ path: `/tiros/outsource/perform` })
      } else if (e == 'yqgdyj') {
        this.$router.push({ path: `/tiros/dispatch/workorder` })
      } else if (e == 'wclgzyj') {
        this.$router.push({ path: `/tiros/dispatch/breakdown` })
      }
    },
  },
  // 离开路由之前执行
  beforeRouteLeave(to, from, next) {
    clearInterval(this.timer)
    next()
  },
}
</script>

<style scoped>
.circle {
  width: 115px;
  height: 115px;
  border-style: solid;
  border-width: 5px;
  border-radius: 50%;
  -moz-border-radius: 50%;
  -webkit-border-radius: 50%;
  /*border-color: #1E90FF;*/
  margin: 0 auto;
  margin-top: 15px;
  cursor: pointer;
}
</style>