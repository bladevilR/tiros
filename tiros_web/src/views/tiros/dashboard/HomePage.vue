<template>
  <grid-layout ref="gridlayout" :layout="layoutWidgetsList"
               :col-num="100"
               :row-height="20"
               :autoSize="true"
               :is-draggable="false"
               :is-resizable="false"
               :responsive="false"
               :prevent-collision="true"
               :vertical-compact="false"
               :use-css-transforms="true"
  >
    <a-empty v-if="layoutWidgetsList.length==0" />
    <grid-item :key="item.i" v-for="item in layoutWidgetsList"
               :x="item.x"
               :y="item.y"
               :w="item.w"
               :h="item.h"
               :i="item.i"
    >
      <na-card style="width: 100%; height: 100%;" :title="item.extInfo.title" :moreUrl="item.extInfo.moreUrl">
        <component v-bind:is="item.viewComponent"></component>
      </na-card>
    </grid-item>
  </grid-layout>
</template>

<script>
import {GridLayout, GridItem} from "vue-grid-layout"
import {getMainLayoutInfo} from '@api/tirosLayoutApi'
export default {
  name: 'HomePage',
  components: {
    GridLayout,
    GridItem
  },
  data () {
    return {
      layoutWidgetsList: []
    }
  },
  mounted () {
    this.loadLayout()
  },
  methods: {
    loadLayout () {
      getMainLayoutInfo().then(res => {
        if (res.success) {
          console.log(res.result.layoutWidgetsList)
          res.result.layoutWidgetsList.forEach(widget => {
            console.log(widget)
            this.layoutWidgetsList.push({
              x: widget.xPos,
              y: widget.yPos,
              w: widget.defWidth,
              h: widget.defHeight,
              i: widget.id,
              viewComponent: resolve => require(['@/' + this.trimStrat(widget.componentPath) +'.vue'], resolve),
              extInfo: widget
            })
          })
        }
      })
    },
    trimStrat (source) {
      const rg = new RegExp('^/*')
      return source.replace(rg, '')
    },
  }
}
</script>

<style scoped>

</style>