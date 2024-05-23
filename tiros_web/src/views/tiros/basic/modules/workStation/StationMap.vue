<template>
  <na-splitter :default-size="250" style="border: 1px dotted #cccccc">
    <div slot="left-panel" style="height: calc(100vh - 122px); padding-right: 5px;">
      <a-collapse v-model="activeKey">
        <a-collapse-panel key="1" header="功能区域">
          <div class="btn-div">
            <a-button type="link" @click="openFile" block>
              修改背景
              <input ref="file" type="file" style="display: none" @change="bgFileChange">
            </a-button>
          </div>
          <div class="btn-div">
            <a-button type="link" @click="clearCanvas" block>
              清除所有
            </a-button>
          </div>
        <!--  <div class="btn-div">
            <a-button type="link" @click="testCanvas" block>
              测试功能
            </a-button>
          </div>-->
          <div class="btn-div">
            <a-button type="link" @click="saveSetting" block>
              保存
            </a-button>
          </div>
          <div class="btn-div">
            <a-button type="link" @click="closeWindow" block>
              关闭
            </a-button>
          </div>
        </a-collapse-panel>
        <a-collapse-panel key="2" header="工位列表(拖动到右边)">
          <div class="listWorkStation">
            <div v-for="item in stationList" :key="item.id" class="selectItem" draggable="true" :type="'station'" :id="item.id" :code="item.stationNo" :name="item.name" @dragstart="dragStart">
              <div class="logo">
                <img src="~@/assets/tiros/images/workstation/station_blue.png" />
              </div>
              <div class="title">
                [{{item.stationNo}}]-{{ item.name }}
              </div>
              <div style="clear: both;"></div>
            </div>
          </div>
        </a-collapse-panel>
        <a-collapse-panel key="3" header="股道区域(拖动到右边)">
          <div class="listWorkStation">
            <div v-for="item in trackList" :key="item.id" class="selectItem" draggable="true" :type="'track'" :id="item.id" :code="item.code" :name="item.name" @dragstart="dragStart">
              <div class="logo">
                <img src="~@/assets/tiros/images/workstation/color_bg.png" />
              </div>
              <div class="title">
                {{ item.name }}
              </div>
              <div style="clear: both;"></div>
            </div>
          </div>
        </a-collapse-panel>
      </a-collapse>
    </div>
    <div ref="rightPanel" slot="right-panel" style="height: calc(100vh - 25px); position: relative;" @contextmenu.stop="onContextmenu">
      <canvas ref="myCanvas" id="myCanvas"></canvas>
      <div ref="hoverPop" class="hoverPop">
        <div v-if="hoverItem">
          {{ hoverItem.code }}-{{ hoverItem.name }}
        </div>
      </div>
      <div ref="contextMenu" class="contextMenu">
        <div class="btnDiv">
          <a-button type="link" @click="deleteObject" block>
            删除
          </a-button>
        </div>
        <div class="btnDiv">
          <a-button type="link" @click="changeImage" block>
            改变图片
          </a-button>
        </div>
      </div>
    </div>
  </na-splitter>
</template>

<script>
import { fabric } from 'fabric'
import { getStationList, getTrackList, getWorkshop, saveWorkshop } from '@api/tirosApi'
import NaSplitter from '@comp/tiros/Na-splitter'

fabric.NamedImage = fabric.util.createClass(fabric.Image, {
  type: 'named-image',
  initialize: function (element, options) {
    this.callSuper('initialize', element, options)
    options && this.set('name', options.name) && this.set('id', options.id) && this.set('code', options.code) && this.set('iType', options.iType)
  },
  toObject: function () {
    return fabric.util.object.extend(this.callSuper('toObject'), { id: this.id, code: this.code, name: this.name, iType: this.iType })
  }
})

fabric.NamedImage.fromObject = function (object, callback) {
  fabric.util.loadImage(object.src, function (img) {
    callback && callback(new fabric.NamedImage(img, object))
  })
}
export default {
  name: 'StationMap',
  components: { NaSplitter},
  props: ['visible', 'depotId', 'workshopId'],
  data () {
    return {
      zoom: 1,
      changed: false,
      activeKey: 1,
      stationList: [],
      trackList:[],
      imgAllow: ['jpeg','jpg','gif','png'],
      canvas: null,
      dragItem: null,
      hoverItem: null,
      contextObj: null,
      imgSize:{w: 30, h: 30},
      workShop: null
    }
  },
  mounted () {
    this.loadTracks();
    this.loadStationList();
    // console.log('panel w:%s,h:%s',this.$refs.rightPanel.offsetWidth,this.$refs.rightPanel.offsetHeight)
    setTimeout(()=>{
      const w = this.$refs.rightPanel.clientWidth
      const h = this.$refs.rightPanel.clientHeight
      this.$refs.myCanvas.width = w
      this.$refs.myCanvas.height = h
      this.canvas = new fabric.Canvas('myCanvas');
      this.canvas.on('drop', this.dropImage);
      this.canvas.on('mouse:over',this.mouseHover);
      this.canvas.on('mouse:out', this.mouseOut);
      this.canvas.on('mouse:down', this.onMouseDown);
      this.canvas.on('object:moved', () => {
        this.changed = true
      })

      // 是否跳过所有选中（不能hover了）
      // this.canvas.skipTargetFind=true;
      this.loadWorkshop()
    },300)

  },
  methods: {
    saveSetting () {
      // canvas.toJSON()
      this.workShop.graphAddress = JSON.stringify(this.canvas.toJSON())
      saveWorkshop(this.workShop).then(res => {
        if (res.success) {
          this.$message.success('保存成功')
          this.changed = false
        } else {
          console.error('保存车间平面设置失败：', res.message)
          this.$message.error('保存失败')
        }
      })
    },
    loadWorkshop () {
      getWorkshop(this.workshopId).then(res => {
        if(res.success){
          this.workShop = res.result
          //console.log('workShop:', this.workShop)
          if (this.workShop.graphAddress) {
            // console.log('graphAddress:', this.workShop.graphAddress)
            this.canvas.loadFromJSON(this.workShop.graphAddress, () => {
              this.zoomBgToFitCanvas()
            })
          } else {
            this.setBackgroundImage(require('@assets/tiros/images/workstation/workshop_bg.jpg'))
          }
        } else {
          this.setBackgroundImage(require('@assets/tiros/images/workstation/workshop_bg.jpg'))
        }
      }).catch(err=>{
        this.setBackgroundImage(require('@assets/tiros/images/workstation/workshop_bg.jpg'))
      })
    },
    loadTracks () {
      getTrackList({ depotId: this.depotId, pageNo: 1, pageSize: 1000}).then(res=>{
        if (res.success) {
          this.trackList = res.result.records
        }
      })
    },
    loadStationList () {
      getStationList({ id: this.workshopId, pageNo: 1, pageSize: 1000}).then((res) => {
        if (res.success) {
          this.stationList = res.result.records
        }
      })
    },
    openFile() {
      this.$refs.file.dispatchEvent(new MouseEvent('click'))
    },
    bgFileChange (e) {
      const files = e.target.files;
      if(files==null || files.length === 0){
        return;
      }

      // 后缀获取
      let suffix = ''
      try {
        const fileArr = files[0].name.split('.')
        suffix = fileArr[fileArr.length - 1]
      } catch (err) {
        suffix = ''
      }
      if (suffix === '' || this.imgAllow.indexOf(suffix)<0) {
        this.$message.error('不是有效的图片文件,只支持' + this.imgAllow.join(',') + '图片文件')
        return
      }
      const reader = new FileReader();
      reader.onload = (f) => {
        const dataURL = f.target.result;
        // console.log('file data:', data)
        this.changed = true
        this.setBackgroundImage(dataURL)
      }
      reader.readAsDataURL(files[0]);
    },
    setBackgroundImage (url) {
      fabric.Image.fromURL(url, (img) => {
        // add background image
        const rate= img.height / img.width;
        const w = this.canvas.width / img.width
        const h = (this.canvas.width * rate)/ img.height

        this.canvas.setBackgroundImage(img, this.canvas.renderAll.bind(this.canvas), {
          originX: 'left',
          originY: 'top',
          left: 0,
          top: 0,
          scaleX: w,
          scaleY: h
        }, {crossOrigin: 'anonymous'});
      });
    },
    dragStart (e) {
      if(!e.target.children[0] || !e.target.children[0].children[0]){
        this.dragItem = null
        return false;
      }
      const imgEle = e.target.children[0].children[0]
      //console.log('src:', imgEle.src)
      this.dragItem = {
        id: e.target.attributes['id'].value,
        code: e.target.attributes['code'].value,
        name: e.target.attributes['name'].value,
        type: e.target.attributes['type'].value,
        img: imgEle
      }
    },
    dropImage (e) {
      this.changed = true
      if (!this.dragItem) {
        return;
      }
      // console.log(e)
      const {offsetX, offsetY} = e.e

      // check
      if (this.checkObjectId(this.dragItem.id)) {
        this.$message.error(this.dragItem.name + '已经存在了，不能重复创建')
        return
      }

      fabric.util.loadImage(this.dragItem.img.src, (img) => {
        let group = null
        if(this.dragItem.type==='station') {
          const image = new fabric.NamedImage(img, {
            top: 0,
            left: 0,
            width: this.dragItem.img.naturalWidth,
            height: this.dragItem.img.naturalHeight,
            scaleX: this.imgSize.w / this.dragItem.img.naturalWidth,
            scaleY: this.imgSize.h / this.dragItem.img.naturalHeight,
            id: this.dragItem.id,
            code: this.dragItem.code,
            name: this.dragItem.name,
            iType: this.dragItem.type
          });
          image.set('selectable', false)
          // 计算当前图标显示的坐标
          let _zoom = 1 + 1 - this.zoom
          let mTop = offsetY - this.imgSize.h / 2.5,
            mLeft = offsetX - this.imgSize.w / 2.5
          if (this.zoom < 1) {
              mTop = offsetY * _zoom + this.imgSize.h / 2.5,
              mLeft = offsetX * _zoom + this.imgSize.w / 2.5
          }

          //进行组合
          group = new fabric.Group([image], {
            left: mLeft,
            top: mTop
          })
        } else if(this.dragItem.type==='track') {
          const image = new fabric.NamedImage(img, {
            top: 0,
            left: 20,
            width: 800,
            height: 30,
            backgroundColor: '#e3d3d3',
            scaleX: 1,
            scaleY: 1,
            id: this.dragItem.id,
            code: this.dragItem.code,
            name: this.dragItem.name,
            iType: this.dragItem.type
          })
          image.set('selectable', false)

          const text = new fabric.Text(this.dragItem.name, {
            top: 7,
            left: -26,
            fontSize: 15
          });
          text.set('selectable', false)
          //进行组合
          group = new fabric.Group([image, text], {
            left: offsetX - 41,
            top: offsetY - 15
          })
        }

        // 禁止选中
        // group.set('selectable', false)

        this.canvas.add(group)
        this.dragItem = null
      });
    },
    mouseHover (e) {
      const hoverElement = this.$refs.hoverPop
      if(hoverElement) {
        if (e.target) {
          let top = e.e.offsetY  + 10
          let left = e.e.offsetX  + 10
          // console.log('top:%s,left:%s', top, left);
          if (e.target._objects && e.target._objects.length > 0) {
            const item = e.target._objects[0]
            this.hoverItem = {
              id: item.id,
              code: item.code,
              name: item.name
            }
            hoverElement.style.left = left + "px";
            hoverElement.style.top = top + 'px';
            hoverElement.style.display = 'block';
          }

        } else {
          this.hoverItem = null
          hoverElement.style.display = 'none';
        }
      }
    },
    mouseOut (e) {
      const hoverElement = this.$refs.hoverPop
      if (hoverElement) {
        if (e.target) {
          hoverElement.style.display = 'none';
        }
      }
    },
    onContextmenu (e) {
      // const pointer = this.canvas.getPointer(event.originalEvent);
      // 隐藏弹出的pop
      const hoverElement = this.$refs.hoverPop
      if (hoverElement) {
        hoverElement.style.display = 'none';
      }

      const ele = this.canvas.findTarget(e, false)
      if (ele && ele._objects && ele._objects.length > 0) {
        this.contextObj = ele

        /*let top = ele.top + 30;
        let left = ele.left + 30;*/
        let top = e.offsetY  + 10
        let left = e.offsetX  + 10
        this.$refs.contextMenu.style.left = left + "px";
        this.$refs.contextMenu.style.top = top + 'px';
        this.$refs.contextMenu.style.display = 'block';

        // console.log('contentxObj:', this.contextObj)
      }
      e.preventDefault();
    },
    deleteObject () {
      if (this.contextObj) {
        this.canvas.remove(this.contextObj);
        this.contextObj = null
        // 从数据库删除？
        const contextMenu = this.$refs.contextMenu
        if (contextMenu) {
          contextMenu.style.display = 'none';
        }
        this.changed=true
      }
    },
    changeImage () {
      if (this.contextObj) {
        if (this.contextObj.get('type') === 'group') {
          const imgEle = this.contextObj._objects[0]
          if (imgEle.get('type') === 'named-image') {
            let imgUrl = require('@assets/tiros/images/workstation/station_red.png')
            const imgUrl2 = require('@assets/tiros/images/workstation/station_yellow.png')
            if (imgEle.getSrc(false).indexOf(imgUrl) >= 0) {
              imgUrl = imgUrl2
            }
            fabric.util.loadImage(imgUrl, (img) => {
              // 下面两行一定要，如果不改变，则图片无法更新
              imgEle.scaleX = 1
              imgEle.scaleY = 1
              const scaleX= this.imgSize.w / img.naturalWidth;
              const scaleY= this.imgSize.w / img.naturalHeight;
              imgEle.setElement(img,{
                scaleX: scaleX,
                scaleY: scaleY
              });
              this.canvas.renderAll();
            });
          }
        }
      }
    },
    onMouseDown (e) {
      const hoverPop = this.$refs.hoverPop
      if (hoverPop) {
        hoverPop.style.display = 'none';
      }
      const contextMenu = this.$refs.contextMenu
      if (contextMenu) {
        contextMenu.style.display = 'none';
      }
      this.hoverItem = null
    },
    checkObjectId (id) {
      let flag=false;
      this.canvas.forEachObject((obj) => {
        // console.log('type:', obj.get('type'))
        if(this.checkIdEqual(obj, id)){
          flag = true
        }
      })
      return flag
    },
    checkIdEqual (obj,id) {
      if (obj.get('type') === 'group') {
        let flag = false
        for (let i = 0; i < obj._objects.length; i++) {
          flag=this.checkIdEqual(obj._objects[i], id);
          if(flag){
            break;
          }
        }
        return flag
      } else {
        if (obj.get('type') === 'named-image') {
          if (obj.id === id){
            return true;
          } else{
            return false;
          }
        } else {
          return false;
        }
      }
    },
    testCanvas () {
      /*this.canvas.forEachObject((obj) => {
        console.log('obj:', obj)
        // 禁止选中
        obj.set('selectable', false)
      })*/
    },
    //缩放移动视图，使其适应Canvas大小
    zoomBgToFitCanvas () {
      //先还原缩放比例与位置
      this.canvas.setZoom(1);
      this.canvas.absolutePan({x:0, y:0});

      // 计算缩放比例: 原背景图显示尺寸，到当前显示尺寸的缩放比例
      let bgWidth = this.canvas.backgroundImage.width * this.canvas.backgroundImage.scaleX
      let zoom =this.canvas.width / bgWidth
      // console.log('canvas:%d, bg:%d, zoom: %f', this.canvas.width, bgWidth, zoom)
      this.zoom = zoom
      //计算缩放中心
      let zoomPoint = new fabric.Point(0, 0);
      //开始缩放
      this.canvas.zoomToPoint(zoomPoint, zoom);
    },
    clearCanvas () {
      // 清除所有，包括背景图片
      // this.canvas.clear()
      this.canvas.forEachObject((obj) => {
        this.canvas.remove(obj)
      });
      this.changed = true
    },
    closeWindow () {
      if (this.changed) {
        this.$confirm({
          content: '你的设置已经修改，是否放弃保存？',
          onOk: () => {
            this.changed = false
            this.$emit('update:visible', false)
          },
          onCancel: () => {
            // 先保存一下呗
          }
        })
      } else {
        this.changed = false
        this.$emit('update:visible', false)
      }
    },
    strLen(str) {
      let len = 0;
      for (let i=0; i<str.length; i++) {
        if (str.charCodeAt(i)>127 || str.charCodeAt(i)==94) {
          len += 2;
        } else {
          len ++;
        }
      }
      return len;
    }
  }
}
</script>

<style scoped lang="less">
.btn-div{
  text-align: center;
  border-bottom: 1px dotted #cccccc;

  &:hover{
    background-color: #f3f1f1;
  }
}
.listWorkStation {
  margin-top: 10px;
  .selectItem {
    margin-top: 10px;
    padding-left: 10px;
    &:hover{
      background-color: #eeeeee;
    }

    .logo {
      width: 25px;
      float: left;
      height: 25px;
      line-height: 25px;
      img {
        -webkit-user-drag: none;
      }
    }
    .title {
      float: left;
      line-height: 30px;
      padding-left: 8px;
    }
  }

  img {
    width: 25px;
    height: 25px;
  }
}
.hoverPop{
  position: absolute;
  top:0px;
  left: 0px;
  width: 200px;
  height: 200px;
  display: none;
  z-index: 999;
  background-color: #f8f8f8;
  -moz-box-shadow:2px 2px 5px #333333;
  -webkit-box-shadow:2px 2px 5px #333333;
  box-shadow:2px 2px 5px #333333;
  padding: 10px;
}
.contextMenu{
  position: absolute;
  display: none;
  width: 100px;
  height: 100px;
  top:0;
  left: 0;
  background-color: #f8f8f8;
  z-index: 999;
  -moz-box-shadow:2px 2px 5px #333333;
  -webkit-box-shadow:2px 2px 5px #333333;
  box-shadow:2px 2px 5px #333333;
  .btnDiv{
    text-align: center;
    border-bottom: 1px dotted #cccccc;
    cursor: pointer;
    &:hover{
      background-color: #f1eeee;
    }
  }
}
</style>