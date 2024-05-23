<template>
  <a-modal
    :title="'作业指导书'"
    width="100%"
    dialogClass="fullDialog no-footer"
    :visible="visible"
    @cancel="handleCancel"
    :footer="null"
  >
    <div class="sop-body">
      <div style="display: flex;align-content: center;flex-wrap: wrap;">
        <a-spin v-if="loading" tip="下载中..." />
      </div>
      <div v-if="!loading" class="sop-body-page" ref="mainDom"></div>
    </div>
  </a-modal>
</template>

<script>
import { getSopDetailPage, getSopDetailRecord } from '@/api/tirosApi'

export default {
  name: 'WorkSopView',
  components: {},
  data() {
    return {
      visible: false,
      tableData: [],
      queryParam: {
        bookId: '',
      },
      loading: false
    }
  },
  created() {},
  methods: {
    findList() {
      // this.queryParam.bookId = null
      // this.tableData = []
      // this.isCheckRow = false
      // this.isCheckRows = false
      // if (bookId) {
      //   this.queryParam.bookId = bookId
      // }
      // if (this.queryParam.bookId) {
      //   getSopDetailPage(this.queryParam).then((res) => {
      //     if (res.success) {
      //       this.tableData = res.result
      //     } else {
      //       this.$message.error(res.message)
      //     }
      //   })
      // }
    },
    // 关闭
    handleCancel() {
      this.visible = false
    },
    showBook(id) {
      this.visible = true
      this.loading = true
      getSopDetailPage({
        bookId: id,
      }).then((res) => {
        if (res.success) {
          let promiseList = []
          res.result.forEach((element) => {
            promiseList.push(
              getSopDetailRecord({
                detailId: element.id,
              }).then((res) => {
                if (res.success) {
                  let record = JSON.parse(JSON.stringify(res.result))
                  return record
                } else {
                  this.$message.error(res.message)
                  return null
                }
              })
            )
          })
          Promise.all(promiseList).then(res =>{
            res.sort((a, b) => a.stepNum - b.stepNum)
            let content = ''
            res.forEach((e) => {
              if (e) {
                content += `<h2>${e.stepNum}<span style="margin-left:15px;margin-top: 24px;">${e.stepTitle}</span></h2>`
                content += e.stepContent || ''
              }
            })
            this.loading = false
            this.$nextTick.call(this, () => {
              this.$refs.mainDom.innerHTML = content
            })
          })


          // let content = ''
          // res.result.forEach((e) => {
          //   content += `<h2>${e.stepNum}<span style="margin-left:15px;">${e.stepTitle}</span></h2>`
          //   content += e.stepContent || ''
          // })
          // this.$nextTick.call(this, () => {
          //   this.$refs.mainDom.innerHTML = content
          // })
        }
      })
    },
    showSopDetail(ids) {
      this.visible = true
      let promistList = []
      ids.forEach((e) => {
        promistList.push(
          getSopDetailRecord({
            detailId: e,
          }).then((res) => {
            if (res.success) {
              return res.result
            } else {
              return ''
            }
          })
        )
      })
      Promise.all(promistList).then((res) => {
        let content = ''
        res.forEach((e) => {
          content += `<h2>${e.stepNum}<span style="margin-left:15px;">${e.stepTitle}</span></h2>`
          content += e.stepContent
        })
        this.$refs.mainDom.innerHTML = content
      })
    },
  },
}
</script>
<style lang="less" scoped>
.sop-body {
  width: 100%;
  min-height: 100%;
  display: flex;
  justify-content: center;
  background: #f5f5f5;
  padding: 25px;
}

</style>

<style lang="less">
.sop-body-page{
  img{
    max-width: 100%;
  }
}
</style>