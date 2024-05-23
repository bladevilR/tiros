<template>
  <a-card id="leftQrcode">
    <div class="typeTitle">种类选择</div>
    <a-button
      block
      v-for="(item, index) in typeList"
      :key="index"
      :class="{'listBtn':true,'active':activeValue == item.value}"
      @click="changeType(item.value)"
    >{{item.text}}</a-button>
  </a-card>
</template>

<script>
  import { ajaxGetDictItems, getDictItemsFromCache } from '@/api/api'
  export default {
    name: 'LeftQrcode',
    data() {
      return {
        dictCode: 'bu_objType_type',
        typeList: [],
        activeValue: '',
      }
    },
    created() {
      this.initDictData()
    },
    methods: {
      changeType(value) {
        this.activeValue = value
        console.log(value)
        this.$emit('input', this.activeValue)
      },
      initDictData() {
        //优先从缓存中读取字典配置
        if (getDictItemsFromCache(this.dictCode)) {
          this.typeList = getDictItemsFromCache(this.dictCode)
        } else {
          //根据字典Code, 初始化字典数组
          ajaxGetDictItems(this.dictCode, null).then((res) => {
            if (res.success) {
              this.typeList = res.result
            }
          })
        }
        this.activeValue = this.typeList[0].value
        this.$emit('input', this.activeValue)
      },
    },
  }
</script>

<style lang="less">
  #leftQrcode {
    .active {
      font-weight: bold;
    }
    .ant-card-body {
      padding: 0 0 24px 0;
      height: calc(100vh - 120px);
      overflow-y: auto;
    }
    .typeTitle {
      background: #eee;
      padding: 15px;
      text-align: center;
    }
    .listBtn {
      border: none;
      margin-top: 10px;
    }
    button[ant-click-animating-without-extra-node]:after {
      border: 0 none;
      opacity: 0;
      animation:none ;
    }
  }
</style>