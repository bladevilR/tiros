<template>
  <div :style="{ cursor, userSelect, flexDirection }" class="vue-splitter" @mouseup="onUp" @mousemove="onMouseMove" @touchmove="onMove" @touchend="onUp">
    <div :style="leftPaneStyle" class="left-pane splitter-pane">
      <slot name="left-panel"></slot>
    </div>
    <div class="splitter" :style ="splitterStyle" @mousedown="onDown" @touchstart.prevent="onDown">
      <div @click="onClick" :class="splitterClass"></div>
    </div>
    <div :style="rightPaneStyle" class="right-pane splitter-pane">
      <slot name="right-panel"></slot>
    </div>
  </div>
</template>
<script>
export default {
  name: 'na-splitter',
  props: {
    horizontal: {
      type: Boolean,
      default: false
    },
    /*
    * left/right top/bottom
    */
    activeSide: {
      type: String,
      default: 'left'
    },
    defaultSize: {
      type: Number,
      default: 200
    }
  },
  data () {
    return {
      active: false,
      open: true,
      activeSideSize: 200,
      splitterSize: 7,
      hasMoved: false,
      minMove: 5,
      startEvent: null,
      resetSize: 0
    }
  },
  computed: {
    subSize () {
      return this.activeSideSize + this.splitterSize + 2
    },
    flexDirection () { return this.horizontal ? 'column' : 'row' },
    splitterClass() {
      return {
        'arrow-left': !this.horizontal && this.activeSide === 'left' && this.open || !this.horizontal && this.activeSide === 'right' && !this.open,
        'arrow-right': !this.horizontal && this.activeSide === 'left' && !this.open || !this.horizontal && this.activeSide === 'right' && this.open,
        'arrow-top': this.horizontal && this.activeSide === 'top' && this.open || this.horizontal && this.activeSide === 'bottom' && !this.open,
        'arrow-bottom': this.horizontal && this.activeSide === 'top' && !this.open || this.horizontal && this.activeSide === 'bottom' && this.open
      };
    },
    splitterStyle () { return this.horizontal ? { height: this.splitterSize + 'px', cursor: 'ns-resize' } : { width: this.splitterSize+ 'px', cursor: 'ew-resize' } },
    leftPaneStyle () {
      if (this.horizontal) {
        const style = {width: '100%'};
        if(this.activeSide ==='top') {
          style['height'] = this.activeSideSize + 'px';
        } else {
          style['height'] = 'calc(100vh - ' + this.subSize + 'px)';
        }
        return style;
      } else {
        const style = {height: '100%'};
        if(this.activeSide === 'left') {
          style ['width'] = this.activeSideSize + 'px';
        } else {
          style ['width'] = 'calc(100vw - ' + this.subSize + 'px)';
        }
        return style;
      }
    },
    rightPaneStyle () {
      if (this.horizontal) {
        const style = {width: '100%'};
        if(this.activeSide ==='top') {
          style['height'] = 'calc(100vh - ' + this.subSize + 'px)';
        } else {
          style['height'] = this.activeSideSize + 'px';
        }
        return style;
      } else {
        const style = {height: '100%'};
        if(this.activeSide === 'left') {
          style['width'] = 'calc(100vw - ' + this.subSize + 'px)';
        } else {
          style['width'] =  this.activeSideSize + 'px';
        }
        return style;
      }
    },
    userSelect () { return this.active ? 'none' : '' },
    cursor () { return this.active ? this.horizontal ? 'ns-resize' : 'ew-resize' : '' },
  },
  created () {
    this.activeSideSize = this.defaultSize
  },
  methods: {
    onClick () {
      if (!this.hasMoved) {
        if (this.open) {
          this.resetSize = this.activeSideSize
          this.activeSideSize = 0
        } else {
          this.activeSideSize = this.resetSize
        }
        this.open = !this.open
        this.$emit('resize');
      }
    },
    onDown (e) {
      this.active = true;
      this.hasMoved = false;
      this.startEvent = e
    },
    onUp () {
      this.active = false;
      this.startEvent = null
    },
    onMove (e) {
      let move = 0;
      if (this.active) {
        // 0 表示减少  1 表示增加
        let direction = 0
        if (this.horizontal) {
          if(this.startEvent){
            move =  e.pageY - this.startEvent.pageY;
            if (move > 0) {
              direction = 1
            } else {
              direction = 0
              move = Math.abs(move)
            }
          }
        } else {
          if(this.startEvent){
            move =  e.pageX - this.startEvent.pageX;
            if (move > 0) {
              direction = 1
            } else {
              direction = 0
              move = Math.abs(move)
            }
          }
        }
        /*if (percent > this.minWidth && percent < e.currentTarget.offsetWidth - this.minWidth) {
          this.leftWidth = percent;
        }*/

        if (direction === 0) {
          // left/right top/bottom
          if(this.activeSide==='left' || this.activeSide==='top') {
            this.activeSideSize = this.activeSideSize - move
          }
          if (this.activeSide === 'right' || this.activeSide === 'bottom') {
            this.activeSideSize = this.activeSideSize + move
          }
        } else {
          if(this.activeSide==='left' || this.activeSide==='top') {
            this.activeSideSize = this.activeSideSize + move
          }
          if (this.activeSide === 'right' || this.activeSide === 'bottom') {
            this.activeSideSize = this.activeSideSize - move
          }
        }

        this.$emit('resize');
        this.startEvent = e
        this.hasMoved = true;
      }
    },
    onMouseMove (e) {
      if (e.buttons === 0 || e.which === 0) {
        this.active = false;
      }
      this.onMove(e);
    }
  }
}
</script>
<style lang="less">
.vue-splitter {
  height: inherit;
  display: flex;
  .splitter-pane {
    height: inherit;
    overflow-y: auto;
  }
  .splitter {
    position: relative;
    background-color: #f8f8f8;
    border: 1px solid #f6f6f6;
    .arrow-left{
      width: 100%;
      height: 35px;
      display: block;
      background: url('~@/assets/images/splitter/mini-left.gif') no-repeat;
      -moz-background-size:100% 100%; background-size:100% 100%;
      cursor: pointer;
      position: absolute;
      margin-top: -17px;
      top: 50%;
    }
    .arrow-right{
      width: 100%;
      height: 35px;
      display: block;
      background: url('~@/assets/images/splitter/mini-right.gif') no-repeat;
      -moz-background-size:100% 100%; background-size:100% 100%;
      cursor: pointer;
      position: absolute;
      margin-top: -17px;
      top: 50%;
    }
    .arrow-top{
      display:inline-block;
      width:35px;height:5px;zoom:1;float:left;
      background: url('~@/assets/images/splitter/mini-top.gif') no-repeat;
      -moz-background-size:100% 100%; background-size:100% 100%;
      position: absolute;
      margin-left: -17px;
      left: 50%;
    }
    .arrow-bottom{
      display:inline-block;
      width:35px;height:5px;zoom:1;float:left;
      background: url('~@/assets/images/splitter/mini-bottom.gif') no-repeat;
      -moz-background-size:100% 100%; background-size:100% 100%;
      position: absolute;
      margin-left: -17px;
      left: 50%;
    }
  }
}
</style>