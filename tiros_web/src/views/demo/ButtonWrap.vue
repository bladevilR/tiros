<template>
<ToolButtonWrapper style="width: 500px;" :space="true">
  <a-button>test1</a-button>
  <a-button>test2</a-button>
  <a-button>test3</a-button>
  <a-button>test4</a-button>
  <a-button>test5</a-button>
  <a-button>test6</a-button>
  <a-button>test7</a-button>
  <a-button>test8</a-button>
  <a-button>test9</a-button>
  <a-button>test10</a-button>
  <a-button>test11</a-button>
  <a-button>test12</a-button>
  <a-button>test13</a-button>
  <a-button>test14</a-button>
  <a-button>test15</a-button>
  <a-button>test16</a-button>
  <a-button>test17</a-button>
  <a-button @click="clickBtn('18')">test18888888</a-button>
</ToolButtonWrapper>
</template>

<script>
import ToolButtonWrapper from '@views/demo/ToolButtonWrapper'

export default {
  name: 'ButtonWrap',
  components:{ ToolButtonWrapper},
  mounted () {

  },
  methods: {
    clickBtn (v) {
      console.log('click button:', v )
    }
  }

}
</script>

<style scoped>

</style>