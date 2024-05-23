<template>
  <div class="warpStyle">
    <div class="title">{{ title }}</div>
    <div class="table_box">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  props: ["title"],
};
</script>

<style lang="less" scoped>
.warpStyle {
  width: 100%;
  height: 100%;
  background-image: url("~@assets/tiros/images/monitor/main_bottopm_top2.png");
  // background-size:contain;
  background-size: 100% 100%;
  display: flex;
  flex-flow: column nowrap;
  .title {
    padding-top:2.4vw;
    text-indent: 4.2vw;
    color: #0efcff;
    font-size: 1.15vw;
  }
  .table_box{
    flex:1;
    height: 0;
    padding: 1vw 1vw 1vw 1vw;
  }
}
</style>
