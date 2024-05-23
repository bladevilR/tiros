<template>
  <div class="textBox" :id="textBoxId">
    <a-tooltip placement="topLeft">
      <template slot="title">
        <span v-if="tooltip">{{ item.value }}</span>
      </template>
      <span class="fontText" :id="fontTextId">{{
        item.value || (item.type == 2 ? "运用中" : item.type == 3 ? "维修中" : "")
      }}</span>
    </a-tooltip>
  </div>
</template>

<script>
import { randomUUID } from "@/utils/util";

export default {
  props: ["item"],
  data() {
    return {
      textBoxId: "textBox" + randomUUID(),
      fontTextId: "fontText" + randomUUID(),
      tooltip: false,
    };
  },
  mounted() {
    let boxWidth = document.getElementById(this.textBoxId).offsetWidth;
    let txtWidth = document.getElementById(this.fontTextId).offsetWidth;
    console.log(boxWidth, txtWidth);
    if (boxWidth <= txtWidth) {
      this.tooltip = true;
    }
  },
};
</script>

<style lang="less" scoped>
.textBox {
  width: 100%;
  text-overflow: ellipsis;
  text-overflow: ellipsis;
  white-space: nowrap;
  overflow: hidden;
  text-align: right;

  .fontText {
  }
}
</style>
