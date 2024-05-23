<template>
  <a-row type="flex" :gutter="16">
    <a-col :md="5" :sm="24">
      <left-document v-model="directoryId" ref="leftDocument" @select="onSelect"></left-document>
    </a-col>
    <a-col :md="24-5" :sm="24">
      <right-document :directoryId="directoryId" @selectTree="selectTree()" :curPath="curPath"></right-document>
    </a-col>
  </a-row>
</template>
<script>
  import LeftDocument from './LeftDocument'
  import RightDocument from './RightDocument'
  export default {
    name: 'Document',
    components:{LeftDocument,RightDocument},
    data(){
      return{
        directoryId:'',
        curPath: ''
      }
    },
    methods:{
      onSelect (selecteds, info) {
        this.curPath=this.buildPath(info.node)
        //console.log('info:', )
      },
      selectTree(){
        this.$refs.leftDocument.selectTree();
      },
      buildPath (node) {
        let thisPath = '/'
        if (node.dataRef) {
          thisPath= '/'+node.dataRef.name
          if (node.dataRef.parentId) {
            return this.buildPath(node.$parent) + thisPath
          }
        }
        return thisPath
      }
    }
  }
</script>

<style scoped>

</style>