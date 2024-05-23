<template>
  <a-spin
    :spinning="spinData.loading"
    :tip="'二维码图片加载中  ' + spinData.success + '/' + spinData.sum"
  >
    <a-row type="flex" :gutter="16">
      <a-col :md="3" :sm="24">
        <left-qrcode v-model="objType"></left-qrcode>
      </a-col>
      <a-col :md="24 - 3" :sm="24">
        <component @change="spinningStatus" :is="componentName"></component>
        <!-- 动态组件-->
      </a-col>
    </a-row>
  </a-spin>
</template>

<script>
import LeftQrcode from "./LeftQrcode";
import RightWarehouseQrcode from "./RightWarehouseQrcode";
import RightPalletQrcode from "./RightPalletQrcode";
export default {
  components: { LeftQrcode, RightWarehouseQrcode, RightPalletQrcode },
  data() {
    return {
      spinData: {
        loading: false,
        sun: 0,
        success: 0,
      },
      objType: 1,
      componentName: "RightWarehouseQrcode",
    };
  },
  methods: {
    spinningStatus(data) {
      // console.log(data);
      this.spinData = {
        ...data,
      };
    },
  },
  watch: {
    objType: {
      immediate: true,
      handler(type) {
        if (type === "5") {
          this.componentName = "RightWarehouseQrcode";
        } else if (type === "3") {
          this.componentName = "RightPalletQrcode";
        }
      },
    },
  },
};
</script>
