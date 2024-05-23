<template>
  <div class="loading">
    <div class="left" :style="{'--haveColor':haveColor,'--noColor':noColor,'--rotateZ':rotateZ}"></div>
    <div class="right" :style="{'--noColor':noColor,'--haveColor':haveColor,'--rotateZ':rotateZ}">
    </div>
    <div class="progress" :style="{'--haveColor':haveColor}">{{value}}%</div>
  </div>

</template>

<script>
export default {
name: "ProgressChart",
  props:{
     value: {
      default: '',
    },
    haveColor:{
       type:String,
      default: '#389AF4'
    },
    noColor:{
      type:String,
      default: '#dfeaff'
    },
    rotateZ:{
       type:String,
       default:'rotateZ(180deg)'
    }
  },
  created () {
  }
}

</script>

<style scoped lang="less">
.loading {
  margin: 10px auto;
  width: 8em;
  height: 8em;
  position: relative;
}

.loading .progress {
  position: absolute;
  width: 6em;
  height: 6em;
  background-color: white;
  border-radius: 50%;
  left: 1em;
  top: 1em;
  line-height: 6em;
  text-align: center;
  color: var(--haveColor);
}

.left,
.right {
  width: 4em;
  height: 8em;
  overflow: hidden;
  position: relative;
  float: left;
  background-color:  var(--noColor);
}

.left {
  border-radius: 8em 0 0 8em;
}

.right {
  border-radius: 0 8em 8em 0;
}

.left:after,
.right:after {
  content:'';
  position: absolute;
  display: block;
  width: 4em;
  height: 8em;
  border-radius: 8em 0 0 8em;
  background-color: var(--haveColor);
}

.right:after {
  content: "";
  position: absolute;
  display: block;
  border-radius: 0 8em 8em 0;
}
.left:after {
  transform-origin: right center;

}

.right:after {
  transform-origin: left center;
  transform: var(--rotateZ);
}
/*:style="{transformOrigin: 'left center',transform: `rotateZ(180deg)`
    ,backgroundColor:'#389AF4'}"*/
</style>