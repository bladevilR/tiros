<template>
  <a-modal
    title="详情"
    :width="'80%'"
    centered
    :visible="visible"
    :footer="null"
    @cancel="handleCancel"
    :destroyOnClose="true"
    cancelText="关闭"
  >
    <div>
        <div class="info-wrapper info-top-wrapper" style="margin-top: 10px">
          <h4>开口项信息</h4>
          <a-descriptions bordered :column="3">
            <a-descriptions-item label="编号">
            {{detail.recordCode}}
            </a-descriptions-item>
            <a-descriptions-item label="开口项目名称">
              {{detail.recordName}}
            </a-descriptions-item>
            <a-descriptions-item label="所属车辆">
              {{detail.trainNo}}
            </a-descriptions-item>
            <a-descriptions-item label="内容描述" :span="2">
              {{detail.recordDesc}}
            </a-descriptions-item>
            <a-descriptions-item label="创建日期">
              {{detail.createTime}}
            </a-descriptions-item>
            <a-descriptions-item label="车辆">
              {{detail.trainNo}}
            </a-descriptions-item>
            <a-descriptions-item label="所属工单">
              {{detail.orderName}}
            </a-descriptions-item>
            <a-descriptions-item label="关联故障/任务">
              {{detail.orderTaskName}}
          </a-descriptions-item>
          </a-descriptions>
        </div>
    </div>
  </a-modal>
</template>

<script>
  export default {
    name: 'OpenItemDetailModal',
    data() {
      return {
        detail: {},
        visible: false,
      }
    },
    created() {
    },
    methods: {
      show(value){
        this.visible=true
        this.detail=value
      },

      // 关闭
      handleCancel() {
        this.close()
      },
      close() {
        this.$emit('close')
        this.visible = false
      }

    }
  }
</script>

<style lang="less" scoped>
  .info-wrapper {
    border: 1px solid #eee;
    position: relative;
    border-radius: 8px;
    padding: 10px;
    margin-bottom: 20px;
  }

  .info-wrapper h4 {
    position: absolute;
    top: -14px;
    padding: 1px 8px;
    margin-left: 16px;
    color: #777;
    border-radius: 2px 2px 0 0;
    background: #fff;
    font-size: 14px;
    width: auto;
  }
  li{
    width: 100%;
    list-style: none;
    line-height: 2rem;
    color: black;
    transition: background-color 1s linear,color 1s linear;
    -webkit-transition: background-color 1s linear,color 1s linear;
    -moz-transition: background-color 1s linear,color 1s linear;
    -o-transition: background-color 1s linear,color 1s linear;
  }
  li:hover{
    background-color: #DDEEFF;
    color: #19AAFF;
  }

</style>