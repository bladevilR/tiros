<template>
  <a-modal
    title="详情"
    :width="'100%'"
    centered
    :visible="visible"
    dialogClass="fullDialog no-footer"
    :footer="null"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <BreakdowmDetailItem ref="BreakdowmDetailItem" />
  </a-modal>
</template>

<script>
import BreakdowmDetailItem from "./BreakdowmDetailItem";
export default {
  name: "BreakdownModal",
  components: { BreakdowmDetailItem },
  data() {
    return {
      visible: false,
    };
  },
  created() {},
  methods: {
    show(value) {
      this.visible = true;
      this.$nextTick(()=>{
        this.$refs.BreakdowmDetailItem.show(value);
      })
    },

    // 关闭
    handleCancel() {
      this.close();
    },
    close() {
      this.$emit("close");
      this.visible = false;
    },
  },
};
</script>

<style lang="less" scoped>

</style>
