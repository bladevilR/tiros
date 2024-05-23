<template>
  <div class="na-content-body" :style="{ height: height }">
    <h4 class="na-content-title">{{ title }}</h4>
    <slot></slot>
  </div>
</template>
<script>
export default {
  props: {
    title: {
      type: String,
      default: '',
    },
    height: {
      type: String,
      default: 'auto',
    },
  },
}
</script>
<style lang="less" scoped>
.na-content-body {
  height: 100%;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-top: 18px;
  padding: 16px;
  padding-top: 24px;
  position: relative;
  display: flex;
  flex-direction: column;

  .na-content-title {
    position: absolute;
    background: #fff;
    top: -14px;
    left: 18px;
    font-size: 14px;
    padding: 0 8px;
    color: #777;
  }
}

.table-page-search-wrapper:before{
  background: transparent !important;
  border: none !important;
}
</style>