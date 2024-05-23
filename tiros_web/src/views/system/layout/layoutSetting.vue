<template>
  <a-modal
    title="布局设置"
    centered
    :width="'100%'"
    dialogClass="fullDialog"
    :visible="visible"
    @ok="handleOk"
    @cancel="visible=false"
    :destroyOnClose="true"
  >
    <na-splitter :defaultSize="200" style="height: calc(100%)" >
      <div slot="left-panel" style="overflow-y: auto; overflow-x: hidden;">
        <div style="padding: 10px; padding-left: 0px;">
          <a-input-search v-model="searchText" placeholder="部件搜索" style="width: 100%" @search="onSearch" />
        </div>
        <div style="padding: 10px; padding-left: 0px;">
              <div v-for="widget in widgets" :key="widget.id" :id="widget.id" v-if="!widget.hidden" @drag="drag" @dragend="dragend" class="droppable-element" draggable="true"  unselectable="on">
                  <div style="line-height: 20px; font-weight: bold;" :title="widget.widgetName">
                    <a-icon type="ant-design"  :style="{ fontSize: '16px', color: '#08c' }" />
                    {{ subStringBylen(widget.widgetName,8) }}
                  </div>
                <div style="color: #cccccc;" :title="widget.widgetDesc">{{ subStringBylen(widget.widgetDesc,10) }}</div>
              </div>
        </div>
      </div>
      <div slot="right-panel" style="padding-left: 5px;padding-right: 3px; height: calc(100%)">
        <div id="content">
          <grid-layout ref="gridlayout" :layout.sync="layouts"
                     :col-num="colNum"
                     :row-height="20"
                     :autoSize="true"
                     :is-draggable="true"
                     :is-resizable="true"
                     :responsive="false"
                     :prevent-collision="true"
                     :vertical-compact="false"
                     :use-css-transforms="true"
        >
          <grid-item :key="item.i" v-for="item in layouts"
                     :x="item.x"
                     :y="item.y"
                     :w="item.w"
                     :h="item.h"
                     :i="item.i"
          >
            <na-card style="width: 100%; height: 100%;">
              <div slot="title">
                <span @click.stop="editLayoutTitle(item)" v-if="!editTitle && editLayoutIndex !== item.i">{{item.extInfo.title}}</span>
                <a-input @pressEnter.stop="doneEditItem" v-if="editTitle && editLayoutIndex === item.i" v-model="item.extInfo.title" placeholder="请输入" style="width: 60%"></a-input>
              </div>
              <div slot="extra">
                <a href="javascript:void(0)" @click.stop="removeLayoutWidget(item)" title="移除该部件">X</a>
              </div>

              <component v-bind:is="item.viewComponent"></component>
            </na-card>
          </grid-item>
        </grid-layout>
        </div>
      </div>
    </na-splitter>
  </a-modal>
</template>

<script>
import {getWidgets,getLayoutInfo,saveLayout} from '@api/tirosLayoutApi'
import { stringLength, stringSubByLen, UUID_V1 } from '@/utils/util'
import {GridLayout, GridItem} from "vue-grid-layout"
let mouseXY = {"x": null, "y": null};
let DragPos = {"x": null, "y": null, "w": 1, "h": 1, "i": null};
export default {
  name: 'edit',
  components: {
    GridLayout,
    GridItem
  },
  data () {
    return {
      visible: false,
      colNum:100,
      editTitle: false,
      editLayoutIndex: null,
      searchText: '',
      layoutInfo: null,
      layouts: [],
      widgets: []
    }
  },
  beforeMount () {
    this.getWidgets()
  },
  mounted() {
    document.addEventListener("dragover", function (e) {
      mouseXY.x = e.clientX;
      mouseXY.y = e.clientY;
    }, false);
  },
  methods: {
    editLayoutTitle (item) {
      this.editTitle=true
      this.editLayoutIndex=item.i
    },
    doneEditItem () {
      this.editTitle=false
      this.editLayoutIndex=''
    },
    removeLayoutWidget (item) {
      this.layouts.forEach((w, index) => {
        if (w.i === item.i) {
          this.layouts.splice(index, 1)
        }
      })
    },
    onSearch () {
      this.widgets.forEach(item => {
        if (this.searchText) {
          if (item.widgetName.indexOf(this.searchText) > -1) {
            item.hidden = false
          } else {
            item.hidden = true
          }
        } else {
          item.hidden = false
        }
      })
    },
    getWidgets () {
      getWidgets({ pageNo: 1, pageSize: 100000, status: 1 }).then(res => {
        if (res.success) {
          this.widgets = res.result.records
        } else {
          this.$message.error('查询部件失败')
          console.error('查询部件失败：', res.message)
        }
      })
    },
    getLayoutInfo (id) {
      getLayoutInfo({ id: id }).then(res => {
        if (res.success) {
          this.layoutInfo = res.result
          this.initLayoutWidgets()
        } else {
          this.$message.error('加载部件信息失败')
          console.error('加班布局信息失败：', res.message)
        }
      })
    },
    trimStart (source) {
      const rg = new RegExp('^/*')
      return source.replace(rg, '')
    },
    initLayoutWidgets () {
      if (this.layoutInfo.layoutWidgetsList) {
        this.layouts=[]
        this.layoutInfo.layoutWidgetsList.forEach(widget => {
          this.layouts.push({
            x: widget.xPos,
            y: widget.yPos,
            w: widget.defWidth,
            h: widget.defHeight,
            i: widget.id,
            viewComponent: resolve => require(['@/' + this.trimStart(widget.componentPath) +'.vue'], resolve),
            extInfo: widget
          })
        })
      }
    },
    showModal (id) {
      if (id) {
        this.getLayoutInfo(id)
      } else {
        this.layoutInfo = {
          "byUserId": "",
          "byUserName": "",
          "id": "",
          "isMain": 0,
          "layoutCode": "",
          "layoutDesc": "",
          "layoutName": "",
          "layoutScope": 0,
          "layoutWidgetsList": [
            {
              "componentPath": "",
              "defHeight": 0,
              "defWidth": 0,
              "id": "",
              "isStatic": 0,
              "layoutId": "",
              "moreUrl": "",
              "status": 0,
              "title": "",
              "widgetCategory": "",
              "widgetCategoryName": "",
              "widgetDesc": "",
              "widgetId": "",
              "widgetName": "",
              "xPos": 0,
              "xpos": 0,
              "yPos": 0,
              "ypos": 0
            }
          ],
          "status": 0
        }
      }
      this.visible = true
    },
    handleOk () {
      this.visible = false
      let layoutWidgets = []
      this.layouts.forEach(item => {
        let widget = Object.assign({}, item.extInfo)
        widget.xPos=item.x
        widget.yPos=item.y
        widget.defWidth=item.w
        widget.defHeight=item.h
        layoutWidgets.push(widget)
      })

      this.layoutInfo.layoutWidgetsList = layoutWidgets
      saveLayout(this.layoutInfo).then(res => {
        if (res.success) {
          this.$message.success('保存成功')
          this.$emit('ok')
          this.visible = false
        } else {
          this.$message.error('保存失败')
          console.error('保存布局失败：', res.message)
        }
      })
    },
    drag (e) {
      let parentRect = document.getElementById('content').getBoundingClientRect();
      let mouseInGrid = false;
      if (((mouseXY.x > parentRect.left) && (mouseXY.x < parentRect.right)) && ((mouseXY.y > parentRect.top) && (mouseXY.y < parentRect.bottom))) {
        mouseInGrid = true;
      }
      if (mouseInGrid === true && (this.layouts.findIndex(item => item.i === 'drop')) === -1) {
        let widget = this.widgets.filter(w => w.id === e.target.id)[0]
        if (!widget) {
          mouseInGrid = false
          return
        }
        this.layouts.push({
          x: (this.layouts.length * 2) % (this.colNum || 12),
          y: this.layouts.length + (this.colNum || 12), // puts it at the bottom
          w: 1,
          h: 1,
          i: 'drop',
          viewComponent: resolve => require(['@/' + this.trimStart(widget.componentPath)+'.vue'], resolve),
          extInfo: {
            "componentPath": widget.componentPath,
            "defHeight": widget.defHeight,
            "defWidth": widget.defWidth,
            "id": UUID_V1(),
            "isStatic": 0,
            'layoutId': '',
            "moreUrl": widget.moreUrl,
            "status": 0,
            "title": widget.widgetName,
            "widgetId": widget.id
          }
        });
      }
      let index = this.layouts.findIndex(item => item.i === 'drop');
      if (index !== -1) {
        try {
          this.$refs.gridlayout.$children[this.layouts.length].$refs.item.style.display = "none";
        } catch {
        }
        let el = this.$refs.gridlayout.$children[index];
        el.dragging = {"top": mouseXY.y - parentRect.top, "left": mouseXY.x - parentRect.left};
        let new_pos = el.calcXY(mouseXY.y - parentRect.top, mouseXY.x - parentRect.left);
        if (mouseInGrid === true) {
          this.$refs.gridlayout.dragEvent('dragstart', 'drop', new_pos.x, new_pos.y, 1, 1);
          DragPos.i = String(index);
          DragPos.x = this.layouts[index].x;
          DragPos.y = this.layouts[index].y;
        }
        if (mouseInGrid === false) {
          this.$refs.gridlayout.dragEvent('dragend', 'drop', new_pos.x, new_pos.y, 1, 1);
          this.layouts = this.layouts.filter(obj => obj.i !== 'drop');
        }
      }
    },
    dragend (e) {
      let parentRect = document.getElementById('content').getBoundingClientRect();
      let mouseInGrid = false;
      if (((mouseXY.x > parentRect.left) && (mouseXY.x < parentRect.right)) && ((mouseXY.y > parentRect.top) && (mouseXY.y < parentRect.bottom))) {
        mouseInGrid = true;
      }
      if (mouseInGrid === true) {
        // alert(`Dropped element props:\n${JSON.stringify(DragPos, ['x', 'y', 'w', 'h'], 2)}`);
        let dropItem= this.layouts.filter(obj => obj.i === 'drop')[0];
        this.$refs.gridlayout.dragEvent('dragend', 'drop', DragPos.x, DragPos.y, 1, 1);
        this.layouts = this.layouts.filter(obj => obj.i !== 'drop');
        // UNCOMMENT below if you want to add a grid-item
        this.layouts.push({
          x: DragPos.x,
          y: DragPos.y,
          w: dropItem.extInfo.defWidth,
          h: dropItem.extInfo.defHeight,
          i: dropItem.extInfo.id,
          viewComponent: dropItem.viewComponent,
          extInfo: dropItem.extInfo
        })

        // this.$refs.gridLayout.dragEvent('dragend', DragPos.i, DragPos.x,DragPos.y,1,1);
        try {
            this.$refs.gridLayout.$children[this.layouts.length].$refs.item.style.display="block";
        } catch {
        }
      }
    },
    subStringBylen (str, len) {
      let ilen = stringLength(str)

      if (ilen > len) {
        return stringSubByLen(str, len, true) + '...'
      } else {
        return str;
      }
    }
  }
}
</script>

<style scoped>
.droppable-element {
  width: 100%;
  background: #fafafa;
  border: 1px solid #cdcdcd;
  margin: 10px 0;
  padding: 8px;
}

#content {
  min-height: calc(100% - 0px);
 /* background: #eee;*/

}


.vue-grid-layout {
}
.vue-grid-item:not(.vue-grid-placeholder) {
 /* background: #ccc;
  border: 1px solid black;*/
}
.vue-grid-item .resizing {
  opacity: 0.9;
}
.vue-grid-item .static {
  background: #cce;
}
.vue-grid-item .text {
  font-size: 24px;
  text-align: center;
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  margin: auto;
  height: 100%;
  width: 100%;
}
.vue-grid-item .no-drag {
  height: 100%;
  width: 100%;
}
.vue-grid-item .minMax {
  font-size: 12px;
}
.vue-grid-item .add {
  cursor: pointer;
}
.vue-draggable-handle {
  position: absolute;
  width: 20px;
  height: 20px;
  top: 0;
  left: 0;
  background: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' width='10' height='10'><circle cx='5' cy='5' r='5' fill='#999999'/></svg>") no-repeat;
  background-position: bottom right;
  padding: 0 8px 8px 0;
  background-repeat: no-repeat;
  background-origin: content-box;
  box-sizing: border-box;
  cursor: pointer;
}

</style>