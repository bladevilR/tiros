<template>
  <exception-page type="403" :msg="msg" />
</template>

<script>
  import ExceptionPage from './ExceptionPage'

  export default {
    components: {
      ExceptionPage
    },
    data () {
      return {
        msg: ''
      }
    },
    mounted () {
      this.msg = this.$route.query.msg
      console.log('the msg:', this.$route)
    }
  }
</script>

<style scoped>

</style>