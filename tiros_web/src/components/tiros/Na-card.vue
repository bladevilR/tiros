<template>
  <div class="wrapperDiv" >
    <div class="titleBar font-primary-8 border-primary-6">
      <div class="title font-primary-8 ">
        <span v-if="!$slots.title">
          {{ title }}
        </span>
        <slot name="title"></slot>
      </div>
      <div class="extra font-primary-8 ">
        <a v-if="moreUrl && !$slots.extra" @click.stop="moreClick" title="更多" class="font-primary-8" style="font-weight: bold; font-size: 17px;">...</a>
        <slot name="extra"></slot>
      </div>
      <div style="clear: both"></div>
    </div>
    <div class="content">
      <slot></slot>
    </div>
  </div>
</template>

<script>
export default {
  name: 'na-card',
  props:['title', 'moreUrl'],
  methods: {
    moreClick () {
      this.$router.push({ path: this.moreUrl })
    }
  }
}
</script>

<style scoped lang="less">
.wrapperDiv {
  border: 1px #efefef solid !important;
  border-radius:4px;
  margin-bottom: 15px;
  font-size: 14px;
  height: calc(100% - 16px);
  overflow: hidden;

  .titleBar {
    padding: 5px;
    background: #f1f1f1;
    border: 0;
    border-top-left-radius:4px;
    border-top-right-radius:4px;
    border-bottom: 2px solid;
    margin-bottom: 0px !important;
    line-height: 25px;

    .title {
      width: 60%;
      font-size: 13px;
      font-weight: bold;
      /*text-align: center;*/
      float: left;
    }
    .extra {
      width: 39%;
      height: 100%;
      float: right;
      text-align: right;
      padding-right: 5px;
    }
  }

  .content {
    height: calc(100% - 32px);
    overflow-x: hidden;
    overflow-y: hidden;
    clear: both;
  }
}

</style>