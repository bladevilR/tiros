<template>
  <a-row
    :gutter="24"
    :style="{ width: '99%', marginLeft: '3px', position: 'relative', top: '50%', transform: 'translateY(-53%)' }"
  >
    <a-col :md="6" v-for="(item, index) in dataSource" :key="index">
      <div
        class="square border-primary-2"
        :style="`background-color:${item.itemColor}`"
        @click="jumppage(item.itemCode)"
      >
        <div style="margin-top: 28px">
          <span style="font-weight: bold; font-size: 18px">{{ item.itemValue }}</span
          ><br />
          {{ item.itemTitle }}
        </div>
      </div>
    </a-col>
    <a-empty v-if="dataSource.length == 0" />
  </a-row>
</template>

<script>
import { getAlert } from '@api/tirosKanbanApi'

export default {
  name: 'Alerts',
  props: {
    autoRefresh: {
      type: Boolean,
      default: true,
    },
  },
  data() {
    return {
      dataSource: [],
      timer: null,
      spinning: false,
    }
  },
  computed: {
    permissionList() {
      return this.$store.getters.permissionList || []
    },
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
      this.spinning = true
      getAlert().then((res) => {
        this.spinning = false
        this.dataSource = res.result
      })
    },
    jumppage(e) {
      let routePath = ''
      switch (e) {
        case 'wlkcyj':
          routePath = `/tiros/material/earlywarning`
          break
        case 'qjsjyj':
          routePath = `/tiros/material/equipment/needcheck`
          break
        case 'wzyxyj':
          routePath = `/tiros/material/earlywarning`
          break
        case 'bjzbqyj':
          routePath = `/tiros/outsource/quality`
          break
        case 'clsjyj':
          routePath = `/tiros/group/warningoff`
          break
        case 'wwyqyj':
          routePath = `/tiros/outsource/perform`
          break
        case 'yqgdyj':
          routePath = `/tiros/dispatch/workorder`
          break
        default:  // wclgzyj
          routePath = `/tiros/dispatch/breakdown`
          break
      }
      // 增加路由权限判断
      if (this.permissionList.length) {
        for (let i = 0; i < this.permissionList.length; i++) {
          const permissionItem = this.permissionList[i]
          console.log(permissionItem)
          if (permissionItem.children) {
            for (let j = 0; j < permissionItem.children.length; j++) {
              const childrenItem = permissionItem.children[j]
              if (childrenItem.path == routePath) {
                if(e === 'wclgzyj'){
                  sessionStorage.setItem('DEFAULT','true');
                }else if(e === 'wlkcyj'){
                  sessionStorage.setItem('DEFAULT','1');
                }else if(e === 'wzyxyj'){
                  sessionStorage.setItem('DEFAULT','2');
                }else if(e == 'yqgdyj'){
                  sessionStorage.setItem('DEFAULT','true');
                }
                this.$router.push({ path: routePath })
                return false
              }
            }
          }
        }
        this.$message.warning('没有查看明细的权限!')
      }
      // if (e == 'wlkcyj') {
      //   this.$router.push({ path: `/tiros/material/earlywarning` })
      // } else if (e == 'qjsjyj') {
      //   this.$router.push({ path: `/tiros/material/equipment/needcheck` })
      // } else if (e == 'wzyxyj') {
      //   this.$router.push({ path: `/tiros/material/earlywarning` })
      // } else if (e == 'bjzbqyj') {
      //   this.$router.push({ path: `/tiros/outsource/quality` })
      // } else if (e == 'clsjyj') {
      //   this.$router.push({ path: `/tiros/group/warningoff` })
      // } else if (e == 'wwyqyj') {
      //   this.$router.push({ path: `/tiros/outsource/perform` })
      // } else if (e == 'yqgdyj') {
      //   this.$router.push({ path: `/tiros/dispatch/workorder` })
      // } else if (e == 'wclgzyj') {
      //   this.$router.push({ path: `/tiros/dispatch/breakdown` })
      // }
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
.square {
  border: 1px #efefef solid;
  height: 110px;
  margin-top: 15px;
  cursor: pointer;
  text-align: center;
}
</style>