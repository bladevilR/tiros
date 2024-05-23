<template>
  <a-card style="height: 300px">
    <div style="margin-bottom: 30px;width: 300px;margin-left: 60px">
      <a-upload
        action="https://www.mocky.io/v2/5cc8019d300000980a055e76"
        :default-file-list="defaultFileList"
      >
        <a-button type="primary"> <a-icon type="upload" /> 添加文件 </a-button>
        <a-button style="background-color: #67c23a;margin-left: 8px;color: white ">上传至服务器</a-button>
      </a-upload>
    </div>
  </a-card>
</template>

<script>
  export default {
    name: 'Videochd',
    data(){
      return{
        defaultFileList: [
          {
            uid: '1',
            name: 'xxx.png',
            status: 'done',
            response: 'Server Error 500', // custom error message to show
            url: 'http://www.baidu.com/xxx.png',
          },
          {
            uid: '2',
            name: 'yyy.png',
            status: 'done',
            url: 'http://www.baidu.com/yyy.png',
          },
          {
            uid: '3',
            name: 'zzz.png',
            status: 'error',
            response: 'Server Error 500', // custom error message to show
            url: 'http://www.baidu.com/zzz.png',
          },
        ],
      }
    }
  }
</script>

<style scoped>

</style>