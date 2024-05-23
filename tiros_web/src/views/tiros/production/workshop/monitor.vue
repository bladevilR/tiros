<template>
  <div ref="rootWrapper" style="height: 100%;">
    <div v-if="leftPx !== 0" :style="{ position: 'absolute', top: 1, left: leftPx, zIndex:999 ,cursor:'pointer'}" >
      <a-dropdown :trigger="['click']">
        <!--                <a-icon class="switch_btn" type="setting" @click="(e) => e.preventDefault()" />-->
        <img src="~@/assets/tiros/images/magic.svg" @click="(e) => e.preventDefault()"  />
        <a-menu slot="overlay" style="min-width: 110px">
          <a-menu-item v-for="wkshop in workshops" :key="wkshop.id"  @click="selectShop(wkshop)"
          >{{wkshop.name}}
          </a-menu-item>
        </a-menu>
      </a-dropdown>
    </div>
    <!-- pop menu end -->
    <div style="height: 35px; overflow: auto">
      <div v-for="fault in faults" :key="fault.id">{{fault.faultAssetName}}</div>
    </div>
    <div ref="wrapDiv" style="width: 100%; height: calc(100% - 37px);position: relative;">
      <canvas ref="myCanvas" id="myCanvas"></canvas>
      <div ref="hoverPop" class="hoverPop">
        <div v-if="hoverItem && hoverItem.type==='station'">
          工位编号：{{ hoverItem.code }}<br/>
          工位名称：{{ hoverItem.name }}<br/>
          作业班组：{{ hoverItem.groupName}}<br/>
          作业内容：{{ hoverItem.content }}<br/>
          开始时间：{{ hoverItem.startTime }}<br/>
          作业状态：{{ hoverItem.workStatus }}<br/>
          工班班长：{{ hoverItem.monitor }}<br/>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  import { fabric } from 'fabric'
  import { getLastFaultList, getStationStatus, getWorkShopList, getTrainTrackInfo } from '@api/tirosProductionApi'

  fabric.NamedImage = fabric.util.createClass(fabric.Image, {
    type: 'named-image',
    initialize: function(element, options) {
      this.callSuper('initialize', element, options)
      options && this.set('name', options.name) && this.set('id', options.id) && this.set('code', options.code) && this.set('iType', options.iType)
    },
    toObject: function() {
      return fabric.util.object.extend(this.callSuper('toObject'), {
        id: this.id,
        code: this.code,
        name: this.name,
        iType: this.iType
      })
    }
  })

  fabric.NamedImage.fromObject = function(object, callback) {
    fabric.util.loadImage(object.src, function(img) {
      callback && callback(new fabric.NamedImage(img, object))
    })
  }
  const stationImgList = [
    '',
    require('@/assets/tiros/images/workstation/station_green.png'),
    require('@/assets/tiros/images/workstation/station_red.png'),
    require('@/assets/tiros/images/workstation/station_gray.png')
  ]
  const workStatusMap={
    "1": "正常作业中",
    "2": "发现故障",
    '3': '无作业'
  }
  const trainImage=[
    require('@/assets/tiros/images/workstation/train.png'),
    require('@/assets/tiros/images/workstation/color_bg_gray.png')
  ]

  export default {
    name: 'monitor',
    data() {
      return {
        canvas: null,
        dragItem: null,
        hoverItem: null,
        contextObj: null,
        imgSize: { w: 30, h: 30 },
        workshops: [],
        workShop: null,
        faults: [],
        stationMap: {},
        leftPx: 0,
        intervalId: null
      }
    },
    mounted() {
      setTimeout(() => {
        this.reSizeCanvas()
        this.loadWorkShops()
      }, 300)
    },
    activated () {
      this.intervalId = setInterval(() => {
        this.loadStationInfo()
        this.loadTrackTrainInfo()
      }, 1000 * 5)
    },
    methods: {
      // 加载车间列表
      loadWorkShops() {
        // 加载所有的车辆段和车间数据，然后默认加载第一个
        getWorkShopList({}).then(res => {
          if (res.success) {
            this.leftPx = this.$refs.rootWrapper.offsetLeft
            this.workshops = res.result
            if (this.workshops.length > 0) {
              this.workShop = this.workshops[0]
              this.loadWorkshop()
              this.loadTrackTrainInfo()
              this.loadFaults()
            }
          } else {
            this.$message.error('加载车间数据失败')
            console.error('加载车间数据失败:', res.message)
          }
        }).catch(err => {
          this.$message.error('加载车间数据异常')
          console.error('加载车间数据异常:', err)
        })
      },
      // 加载车间故障
      loadFaults() {
        if (this.workShop) {
          const params = { workshopId: this.workShop.id, num: 3 }
          getLastFaultList(params).then(res => {
            if (res.success) {
              this.faults = res.result
            } else {
              this.$message.error('加载最新故障数据失败')
              console.error('加载最新故障数据失败:', res.message)
            }
          }).catch(err => {
            this.$message.error('加载最新故障数据异常')
            console.error('加载最新故障数据异常:', err)
          })
        }
      },
      // 加载当前车间信息（平面图）
      loadWorkshop() {
        this.clearCanvas()
        if (this.workShop) {
          if (this.workShop.graphAddress) {
            try {
              this.canvas.loadFromJSON(this.workShop.graphAddress, () => {

                this.canvas.forEachObject((obj) => {
                  // 禁止选中
                  obj.set('selectable', false)

                  // 初始化为灰色
                  if (obj.get('type') === 'named-image') {
                    this.setStationImg(obj, stationImgList[3])
                  }
                  // 初始化为灰色
                  if (obj.get('type') === 'group') {
                    for (let i = 0; i < obj._objects.length; i++) {
                      let imgObj = obj._objects[i]
                      if (imgObj.get('type') === 'named-image') {
                        if(imgObj.iType === 'station') {
                          this.setStationImg(imgObj, stationImgList[3])
                        } else if(imgObj.iType === 'track'){
                          //this.setTrackImg(imgObj, require('@/assets/tiros/images/workstation/color_bg_gray.png'))
                        }
                      }
                    }
                  }
                })

                // 放大画布到充满父容器
                // this.canvas.setZoom(1.5)
                let bgWidth = this.canvas.backgroundImage.width * this.canvas.backgroundImage.scaleX
                let bgHeight = this.canvas.backgroundImage.height * this.canvas.backgroundImage.scaleY

                const cavasW = this.$refs.wrapDiv.clientWidth
                const rate = bgHeight / bgWidth
                const zoom = Math.min(cavasW / bgWidth, (cavasW * rate) / bgHeight)
                this.canvas.setZoom(zoom)
              })
            } catch (err) {
              this.$message.error('加载车间平面图JSON异常, 该车间或许没有设置平面图')
              console.log('加载车间平面图JSON异常：', err)
            }
          } else {
            this.$message.error('加载车间平面图JSON异常, 该车间或许没有设置平面图')
          }
        }
      },
      // 加载车间工位列表
      loadStationInfo() {
        if (!this.workShop) {
          return
        }
        const params = {
          workStatus: null,
          workshopId: this.workShop.id,
          workstationNo: ''
        }
        getStationStatus(params).then(res => {
          if (res.success) {
            let stations = res.result
            this.stationMap = {}
            stations.forEach(station => {
              this.stationMap['ST_' + station.id] = station
            })
            this.canvas.forEachObject((obj) => {
              if (obj.get('type') === 'named-image' || obj.get('type') === 'group' ) {
                this.updateStation(obj)
              }
            })
          } else {
            this.$message.error('查询工位状态信息失败')
            console.error('查询工位状态信息失败:', res.message)
          }
        }).catch(err => {
          this.$message.error('查询工位状态信息异常')
          console.error('查询工位状态信息异常:', err)
        })
      },
      // 加载车辆信息
      loadTrackTrainInfo () {
        getTrainTrackInfo({}).then(res => {
          if (res.success) {
            let trains = res.result
            if (!trains) {
              trains = []
            }

            this.canvas.forEachObject((obj) => {
              if (obj.get('type') === 'group') {
                for (let i = 0; i < obj._objects.length; i++) {
                  let imgObj = obj._objects[i]
                  if (imgObj.get('type') === 'named-image') {
                    if(imgObj.iType === 'track'){
                      let trackCode = imgObj.code
                      let train=trains.filter(t=>{
                        return t.trackCode === trackCode
                      })
                      if (train && train.length > 0) {
                        this.setTrackImg(imgObj, trainImage[0], train[0].trainNo)
                      } else {
                        this.setTrackImg(imgObj, trainImage[1], '无')
                      }
                    }
                  }
                }
              }
            })
          }
        })
      },
      // 更新工位信息
      updateStation(obj) {
        if (obj.get('type') === 'group') {
          for (let i = 0; i < obj._objects.length; i++) {
            this.updateStation(obj._objects[i])
          }
        }
        if (obj.get('type') === 'named-image') {
          if (obj.id) {
            const stationInfo = this.stationMap['ST_' + obj.id]
            if (stationInfo) {
              // 根据状态，设置工位图片
              if (stationInfo.workStatus && stationImgList[stationInfo.workStatus]) {
                this.setStationImg(obj, stationImgList[stationInfo.workStatus])
              }
            }
          }
        }
      },
      // 设置工位图片
      setStationImg(imgEle, imgUrl) {
        fabric.util.loadImage(imgUrl, (img) => {
          if (!img) {
            console.error('加载图片失败：', imgUrl)
            return
          }
          // 下面两行一定要，如果不改变，则图片无法更新
          imgEle.scaleX = 1
          imgEle.scaleY = 1
          const scaleX = this.imgSize.w / img.naturalWidth
          const scaleY = this.imgSize.w / img.naturalHeight
          if (imgEle.setElement) {
            imgEle.setElement(img, {
              scaleX: scaleX,
              scaleY: scaleY
            })
            this.canvas.renderAll()
          }
        })
      },
      // 设置股道图片
      setTrackImg (imgEle, imgUrl, txt) {
        // 添加车号
        if (imgEle.group._objects) {
          let objs = imgEle.group._objects
          if (objs.length == 2) {
            const text = new fabric.Text(txt, {
              top: 9,
              right: 0,
              fontSize: 15,
              fontWeight: 'bold',
              fill:'red'
            });
            text.set('selectable', false)
            imgEle.group.add(text)
          } else if (objs.length === 3) {
            objs[2].text = txt
          }
        }

        fabric.util.loadImage(imgUrl, (img) => {
          if (!img) {
            console.error('加载图片失败：', imgUrl)
            return
          }
          // 下面两行一定要，如果不改变，则图片无法更新
          imgEle.scaleX = 1
          imgEle.scaleY = 1

          img.width = 800
          img.height = 30
          const scaleX = 800 / img.naturalWidth
          const scaleY = 30 / img.naturalHeight

          if (imgEle.setElement) {
            imgEle.setElement(img, {
              scaleX: scaleX,
              scaleY: scaleY
            })
/*
            imgEle.group.setOptions({
              left: 60
            })*/

            this.canvas.renderAll()
          }
        });
      },
      reSizeCanvas() {
        const w = this.$refs.wrapDiv.clientWidth
        const h = this.$refs.wrapDiv.clientHeight
        // 设置画布大小
        this.$refs.myCanvas.width = w
        this.$refs.myCanvas.height = h
        // console.log('w:%s,h:%s', w, h)
        this.canvas = new fabric.Canvas('myCanvas')
        this.canvas.on('mouse:over', this.mouseHover)
        this.canvas.on('mouse:out', this.mouseOut)
        this.canvas.on('mouse:down', this.onMouseDown)
        this.canvas.on('mouse:wheel', (e) => {
          if (!e.e.shiftKey) return
          let zoom = (e.e.deltaY > 0 ? 0.001 : -0.001) + this.canvas.getZoom()
          zoom = Math.max(0.1, zoom) //最小为原来的1/10
          zoom = Math.min(3, zoom) //最大是原来的3倍
          // let zoomPoint = new fabric.Point(event.pageX, event.pageY);
          // this.canvas.zoomToPoint(zoomPoint, zoom);
          this.canvas.setZoom(zoom)
        })
        // 是否跳过所有选中（不能hover了）
        // this.canvas.skipTargetFind=true;
        this.loadWorkshop()
      },
      mouseHover(e) {
        const hoverElement = this.$refs.hoverPop
        const display = hoverElement.style.display
        if (hoverElement && display === 'none') {
          if (e.target) {
            let top = e.e.offsetY + 10
            let left = e.e.offsetX + 10

            // 判断是否超出底部了
            const h = this.$refs.wrapDiv.clientHeight
            const diff = h - top
            if (diff < 200) {
              top -= (diff + 10)
            }
            // console.log('top: %s, left: %s', top, left)
            if (e.target._objects && e.target._objects.length > 0) {

              const item = e.target._objects[0]

              if(item.iType && item.iType === 'station') {
                this.hoverItem = {
                  id: item.id,
                  code: item.code,
                  name: item.name,
                  type: item.iType
                }
                const stationInfo = this.stationMap['ST_' + item.id]
                if (stationInfo) {
                  let info = Object.assign({
                    groupName: '',
                    content: '',
                    startTime: '',
                    workStatus: '',
                    monitor: ''
                  }, stationInfo)
                  if(info.workStatus) {
                    info.workStatus = workStatusMap[info.workStatus + '']
                  }

                  this.hoverItem = Object.assign(info, this.hoverItem)
                } else {
                  this.hoverItem.workStatus = workStatusMap['3']
                }
                hoverElement.style.left = left + 'px'
                hoverElement.style.top = top + 'px'
                hoverElement.style.display = 'block'
              }
            }
          } else {
            this.hoverItem = null
            hoverElement.style.display = 'none'
          }
        }
      },
      onMouseDown(e) {
        const hoverPop = this.$refs.hoverPop
        if (hoverPop) {
          hoverPop.style.display = 'none'
        }
        this.hoverItem = null
      },
      mouseOut(e) {
        const hoverElement = this.$refs.hoverPop
        const display = hoverElement.style.display
        if (hoverElement && display === 'block') {
          if (e.target) {
            hoverElement.style.display = 'none'
          }
        }
      },
      selectShop(shop) {
        this.workShop = shop
        this.loadWorkshop()
        this.loadFaults()
      },
      // 清除
      clearCanvas() {
        // 清除所有，包括背景图片
        // this.canvas.clear()
        this.canvas.forEachObject((obj) => {
          this.canvas.remove(obj)
        })
      }
    },
    // 离开路由之前执行
    beforeRouteLeave(to, from, next) {
      clearInterval(this.intervalId)
      next()
    }
  }
</script>

<style scoped lang="less">
  .hoverPop {
    position: absolute;
    top: 0px;
    left: 0px;
    width: 200px;
    min-height: 200px;
    display: none;
    z-index: 999;
    background-color: #f8f8f8;
    -moz-box-shadow: 2px 2px 5px #333333;
    -webkit-box-shadow: 2px 2px 5px #333333;
    box-shadow: 2px 2px 5px #333333;
    padding: 10px;
  }

  .selectItem {
    margin-top: 10px;
    padding: 5px;
    padding-left: 10px;

    &:hover {
      background-color: #d4d2d2;
      cursor: pointer;
    }
  }
</style>