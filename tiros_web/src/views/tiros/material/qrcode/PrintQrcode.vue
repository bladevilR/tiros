<template>
  <div >
    <div v-if="state">
      <div style="width:100%;overflow:auto;overflow-x:hidden;height: calc(100vh - 250px)">
        <template>
          <ul>
            <a-row :gutter="24">
              <a-col :md="4" v-for="(item,index) in tableData" :key="index">
                <li v-bind:class='{active:checkedIndex.includes(item.id)}' :key="index" @click="handleChecked(item)"
                    style="float:left; height:162px; list-style-type:none;  margin-top:30px;">
                  <img
                    :src="prefix+item.qrcodeImgUrl"
                    slot="extra"
                    width="150"
                    height="160"
                    alt="资源找不到11111"
                  />
                </li>
              </a-col>
            </a-row>
          </ul>
          <div v-if="tableData.length==0" align="center">
            暂无数据
          </div>
        </template>
      </div>
      <div >
        <vxe-pager
          perfect
          :current-page.sync="queryParam.currentPage"
          :page-size.sync="queryParam.pageSize"
          :total="totalResult"
          :layouts="['PrevJump', 'PrevPage', 'Number', 'NextPage', 'NextJump', 'Sizes', 'FullJump', 'Total']"
          @page-change="handlePageChange"
        ></vxe-pager>
      </div>
    </div>
    <!--  -->
    <!-- <div> -->
    <div hidden>
      <!--  -->
      <!-- <iframe id='iframe'> -->
      <iframe id='iframe' style="display:none">
        <!-- <ul ref="pdfDom">
          <a-row :gutter="24">
            <a-col v-if="printData.length >0" :md="24" v-for="(item,index) in printData" :key="index">
              <li :key="index"
                  style="float:left; height:162px; list-style-type:none;  margin-top:30px; margin-left: 30px">
                <img
                  :src="prefix+item.qrcodeImgUrl"
                  slot="extra"
                  width="150"
                  height="160"
                  alt="资源找不到"
                />
              </li>
            </a-col>
          </a-row>
        </ul> -->
        <div ref="pdfDom">
          <div style="page-break-after: always;height:38mm;text-align:center" v-for="(item,index) in printData" :key="index">
            <!-- {{prefix+item.qrcodeImgUrl}} -->
            <img
              :src="item.base64"
              slot="extra"
              style="z-index:999;width:auto;height:37mm"
              alt="资源找不到"
            />
          </div>
        </div>
      </iframe>
    </div>
  </div>

</template>
<script>
import { selectPallet, selectWarehouse } from '@/api/tirosMaterialApi'

export default {
  name: 'PrintQrcode',
  props: ['state', 'isPallet'],
  data () {
    return {
      prefix:window._CONFIG['domianURL'],
      tableData: [],
      checkedIndex: [],
      printData: [],
      queryParam: {
        searchText: '',
        depotId: '',
        workshopId: '',
        pageNo: 1,
        pageSize: 10
      },
      totalResult: 0,
      cardStyle: {
        'padding': '10px',
        'height': 'calc(100vh - 120px)',
      }
    }

  },
  methods: {
    findList () {
      if (this.isPallet) {
        selectPallet(this.queryParam).then((res) => {
          this.totalResult = res.result.total
          this.tableData = res.result.records
        })
      } else {
        selectWarehouse(this.queryParam).then((res) => {
          this.totalResult = res.result.total
          this.tableData = res.result.records
        })
      }
    },
    handlePageChange ({ currentPage, pageSize }) {
      this.queryParam.pageNo = currentPage
      this.queryParam.pageSize = pageSize
      this.findList()
    },
    handleChecked (item) {
      if (this.checkedIndex.includes(item.id)) {
        this.checkedIndex = this.checkedIndex.filter(function (ele) {
          return ele !== item.id
        })
      } else {
        this.checkedIndex.push(item.id)
      }
      this.printData = this.tableData.filter((ele) => {
        return this.checkedIndex.includes(ele.id)
      })

    },
    print (selectRecords) {
      this.printData = [];
      this.$nextTick(()=>{
        let printData;
        if (selectRecords.length > 0 || this.checkedIndex.length > 0) {
          if (!this.state) {
            printData = selectRecords
          }
        } else if (!this.state) {
          this.$message.error('尚未选中任何数据!')
          return
        } else {
          printData = this.tableData
        }
        // 父页面动画
        this.$emit('change',{loading:true,sum:printData.length,success:0});
        // 使用图片转base64 解决打印时出现的空白
        this.loadBase64(printData).then((res)=>{
          this.printData = res;
          this.$nextTick(()=>{
            setTimeout(()=>{
              let html = this.$refs.pdfDom.innerHTML
              // console.log(html)
              const document = window.document
              const iframe = window.frames[0]
              // iframe.width = '60mm';
              // iframe.height = '40mm';
              iframe.document.head.innerHTML = document.head.innerHTML
              iframe.document.body.innerHTML = html
              // 父页面动画
              this.$emit('change',{loading:false,sum:printData.length,success:printData.length});
              iframe.window.print()
            },1000)
          })
        })
      })
    },
    loadBase64(urls){
      let list = urls;
      let index = 0;
      return new Promise((resolve, reject) => { 
        // 递归调用
        let fun = ()=>{
          if(index>list.length - 1){
            resolve(list)
          }else{
            this.convertImgToBase64(list[index].qrcodeImgUrl,(base64Img)=>{
              // console.log(base64Img)
              list[index].base64 = base64Img;
              this.$emit('change',{loading:true,sum:urls.length,success:index+1});
              index++;
              fun();
            })
            
          }
        }
        fun();
      });
    },
    convertImgToBase64(url, callback, outputFormat){
      var canvas = document.createElement('CANVAS'),
          ctx = canvas.getContext('2d'),
          img = new Image;
      img.crossOrigin = 'Anonymous';
      img.onload = function(){
          canvas.height = img.height;
          canvas.width = img.width;
          ctx.drawImage(img,0,0);
          var dataURL = canvas.toDataURL(outputFormat || 'image/png');
          callback.call(this, dataURL);
          canvas = null; 
      };
      img.src = this.prefix + url;
    },
    handleState () {
      this.findList()
    }
  }
}

</script>
<style>
.active {
  border: 1px solid #1890FF;
}
</style>