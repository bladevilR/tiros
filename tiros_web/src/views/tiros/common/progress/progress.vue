<template>
  <div class="progress">
    <div class="progress-show" :style="`width:${(completeCount / countTarget) * 100}%;`"></div>
    <span class="percentNum">{{(completeCount / countTarget) * 100}}%</span>
  </div>
</template>

<script>
  export default {
    name: "myProgress",
    props: ['countTarget', 'completeCount']
  }
</script>



<style scoped>
  .progress {
    width: 100%;
    /* border-radius: 8px; */
    height: 100%;
    display: inline-block;
    position: relative;
    text-align: center;
    overflow: hidden;
  }

  .progress-show {
    height: 100%;
    max-width: 100%;
    position: absolute;
    left: 0;
    top: 0;
    width: 0;
    transition: width 0.6s ease;
    /* border-top-right-radius: 5px; */
    /* border-bottom-right-radius: 5px; */
    background-color: rgb(202, 249, 130);
  }

  .percentNum {
   position: relative;
    z-index: 1;
    height: 100%;
    display: inline-block;
    /* vertical-align: top; */
    font-size: 1.3rem;
  }

  @media screen and (max-width: 1180px) {
    .percentNum {
      font-size: 0px;
    }
  }

</style>
