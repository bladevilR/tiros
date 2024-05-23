<template>
  <na-splitter :default-size="200">
    <div slot="left-panel" style="height: calc(100vh - 120px)">
      <div>
        <a-button type="link" @click="openFile" block>
          设置背景
          <input ref="file" type="file" style="display: none" @change="bgFileChange" >
        </a-button>
      </div>
      <div>
        <a-button type="link" @click="clearCanvas" block>
          清除设置
        </a-button>
      </div>
      <div>
        <a-button type="link" @click="testCanvas" block>
          测试功能
        </a-button>
      </div>
      <div style="background-color: #eeeeee;width: 100%; line-height: 30px; margin-top: 10px; padding-left: 10px; font-weight: bold;">
        工位列表
      </div>
      <div class="listWorkStation">
        <div class="selectItem" draggable="true" workNo="1" workName="1 号" @dragstart="dragStart">
          <div class="logo">
            <img src="~@/assets/tiros/images/workstation/station_blue.png"  />
          </div>
          <div class="title">
            1 号工位
          </div>
          <div style="clear: both;"></div>
        </div>
        <div class="selectItem" draggable="true" workNo="2" workName="2 号" @dragstart="dragStart">
          <div class="logo">
            <img src="~@/assets/tiros/images/workstation/station_blue.png"  />
          </div>
          <div class="title">
            2 号工位
          </div>
          <div style="clear: both;"></div>
        </div>
      </div>
    </div>
    <div ref="rightPanel" slot="right-panel" style="height: calc(100vh - 120px); position: relative" @contextmenu="onContextmenu">
      <canvas ref="main" id="main" style="width: 100%;height: 100%;" ></canvas>
      <div ref="hoverPop" style="width: 200px; height: 200px; position: absolute; top:0px; left: 0px; background-color: aquamarine; display: none; z-index: 999">
        <div v-if="hoverItem">
        {{hoverItem.workNo}}-{{hoverItem.workName}}
        </div>
      </div>
      <div ref="contextMenu" style="width: 100px; height: 100px; position: absolute; top:0px; left: 0px; background-color: aquamarine; display: none; z-index: 999">
        <div @click="deleteWorkStation">
          删除
        </div>
        <div @click="changeImage">
          改变图片
        </div>
      </div>
    </div>
  </na-splitter>
</template>

<script>
import { fabric } from 'fabric'
import NaSplitter from '@comp/tiros/Na-splitter'

fabric.NamedImage = fabric.util.createClass(fabric.Image, {
  type: 'named-image',
  initialize: function(element, options) {
    this.callSuper('initialize', element, options);
    options && this.set('name', options.name) && this.set('id',options.id);
  },
  toObject: function() {
    return fabric.util.object.extend(this.callSuper('toObject'), { name: this.name });
  }
});

fabric.NamedImage.fromObject = function(object, callback) {
  fabric.util.loadImage(object.src, function(img) {
    callback && callback(new fabric.NamedImage(img, object));
  });
};

export default {
  name: 'canvas_demo',
  components: { NaSplitter, splitter},
  data () {
    return {
      imgAllow: ['jpeg','jpg','gif','png'],
      canvas: null,
      dragItem: null,
      hoverItem: null,
      contextObj: null,
      imgSize:{w:30, h: 30}
    }
  },
  mounted () {
    // console.log('panel w:%s,h:%s',this.$refs.rightPanel.offsetWidth,this.$refs.rightPanel.offsetHeight)
    const w = this.$refs.rightPanel.clientWidth
    const h = this.$refs.rightPanel.clientHeight
    // console.log('w:%s,h:%s', w, h)
    this.$refs.main.width = w
    this.$refs.main.height = h
    this.canvas = new fabric.Canvas('main');
    this.canvas.on('drop', this.dropImage);
    this.canvas.on('mouse:over',this.mouseHover);
    this.canvas.on('mouse:out', this.mouseOut)
    this.canvas.on('mouse:down', this.onMouseDown)
    this.setBackgroundImage(require('@/assets/tiros/images/workstation/workshop_bg.jpg'))
    // 是否跳过所有选中（不能hover了）
    // this.canvas.skipTargetFind=true;
  },
  methods: {
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
          scaleX: w,
          scaleY: h
        }, {crossOrigin: 'anonymous'});
      });
    },
    dragStart (e) {
      const imgEle = e.target.children[0].children[0]
      //console.log('src:', imgEle.src)
      this.dragItem = {
        workNo: e.target.attributes['workNo'].value,
        workName: e.target.attributes['workName'].value,
        img: imgEle
      }
    },
    dropImage (e) {
      if (!this.dragItem) {
        return;
      }
      // console.log(e)
      const {offsetX, offsetY} = e.e

      // check
      if (this.checkObjectId(this.dragItem.workNo)) {
        this.$message.error(this.dragItem.workName + '已经存在了，不能重复创建')
        return
      }

      fabric.util.loadImage(this.dragItem.img.src, (img) => {
        const image = new fabric.NamedImage(img, {
          width: this.dragItem.img.naturalWidth,
          height: this.dragItem.img.naturalHeight,
          scaleX: this.imgSize.w / this.dragItem.img.naturalWidth,
          scaleY: this.imgSize.h / this.dragItem.img.naturalHeight,
          top: 0,
          left: 0,
          id: this.dragItem.workNo,
          name: this.dragItem.workName
        })

        image.set('selectable', false)
        const text = new fabric.Text(this.dragItem.workName, {
          top: this.imgSize.h + 3,
          left: 5,
          fontSize: 12
        });
        text.set('selectable', false)


        const mTop = offsetY - this.imgSize.h / 2.5, mLeft = offsetX - this.imgSize.w / 2.5
        //进行组合
        const group = new fabric.Group([image, text], {
          left: mLeft,
          top: mTop
        })
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
          //console.log('hover e:', e)
          //console.log('item:',e.target._objects[0])
          /*let top = e.e.pageY;
          let left = e.e.pageX;*/
          let top = e.target.top + 30;
          let left = e.target.left + 30;
          // console.log('top:%s,left:%s', top, left);
          if (e.target._objects && e.target._objects.length > 0) {
            const item = e.target._objects[0]
            this.hoverItem = {
              workNo: item.id,
              workName: item.name
            }
            hoverElement.style.left = left + "px";
            hoverElement.style.top = top + 'px';
            hoverElement.style.display = '';
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
        // console.log('contextMenu:', ele)
        const item = ele._objects[0]
        this.contextObj = ele
   /*     this.hoverItem = {
          workNo: item.id,
          workName: item.name
        }*/

        let top = ele.top + 30;
        let left = ele.left + 30;
        this.$refs.contextMenu.style.left = left + "px";
        this.$refs.contextMenu.style.top = top + 'px';
        this.$refs.contextMenu.style.display = '';
      }
      e.preventDefault();
    },
    deleteWorkStation () {
      if (this.contextObj) {
        this.canvas.remove(this.contextObj);
        this.contextObj = null
        // 从数据库删除？
        const contextMenu = this.$refs.contextMenu
        if (contextMenu) {
          contextMenu.style.display = 'none';
        }
      }
    },
    changeImage () {
      if (this.contextObj) {
        if (this.contextObj.get('type') === 'group') {
          const imgEle = this.contextObj._objects[0]
          if (imgEle.get('type') === 'named-image') {
            const imgUrl = require('@/assets/tiros/images/workstation/station_red.png')

            fabric.util.loadImage(imgUrl, (img) => {
              const scaleX= this.imgSize.w / img.naturalWidth;
              const scaleY= this.imgSize.h / img.naturalHeight;

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
    clearCanvas () {
      // 清除所有，包括背景图片
      // this.canvas.clear()
      this.canvas.forEachObject((obj) => {
        this.canvas.remove(obj)
      });
    }
  }
}
</script>

<style scoped lang="less">
  .listWorkStation {
    margin-top: 10px;
    .selectItem {
      margin-top: 10px;
      &:hover{
        background-color: #eeeeee;
      }

      .logo {
        width: 25px;
        float: left;
        height: 25px;
        line-height: 25px;
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
</style>