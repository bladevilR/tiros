import * as echarts from 'echarts'

// 将echart转为图片
function echartToImg(option, width = 1300, height = 600) {
  let canvas = document.createElement('canvas')
  canvas.id = 'CursorLayer'
  let myChart = echarts.init(canvas)
  myChart.clear()
  myChart.setOption(option)
  myChart.resize({ width: width, height: height, silent: true })
  const img = myChart.getDataURL({pixelRatio:2})
  canvas = null
  return img
}

// 导出柱状图
function comEchart(data) {
  const width = data.width || 1200
  const height = data.height || 740
  // ecahrt配置
  let option = {
    // backgroundColor: "#f6f6f6",
    title: {
      text: data.title,
      left: 'center'
    },
    legend: {
      left: 'right'
    },
    xAxis: {
      show: false,
      type: 'category',
      data: data.xAxisData
    },
    yAxis: {
      type: 'value',
      nameLocation: 'start'
    },
    grid: {
      left: data.tableHeaderWidth + 'px',
      right: '0%',
      bottom: data.tableHeight * (data.seriesData.length + 1) + 'px',
      containLabel: false
    },
    series: [],
    graphic: []
  }

  // 循环展示数据 以及 循环左侧表格头显示
  let series = JSON.parse(JSON.stringify(data.seriesData))
  Array.from(series, (item, index) => {
    item.type = data.seriesType || 'bar'
    item.label = {
      show: data.seriesLabel ? true : false,
      position: 'top'
    }
    item.showBackground = true
    option.graphic.push({
      type: 'group',
      left: 0,
      bottom: (series.length - 1 - index) * data.tableHeight, // i == 0 ? 120 : i == 1 ? 60 : 0,
      children: [
        {
          type: 'rect',
          z: 100,
          left: 'center',
          top: 'middle',
          shape: {
            width: data.tableHeaderWidth,
            height: data.tableHeight
          },
          style: {
            fill: 'rgba(0,0,0,0)',
            stroke: '#ccc',
            shadowBlur: 8,
            shadowOffsetX: 3,
            shadowOffsetY: 3,
            shadowColor: 'rgba(0,0,0,0)'
          }
        },
        {
          type: 'text',
          z: 100,
          top: 'middle',
          style: {
            fill: '#333',
            textAlign: 'center',
            width: data.tableHeaderWidth,
            overflow: 'break',
            text: item.name,
            font: '14px Microsoft YaHei'
          }
        }
      ]
    })
  })
  option.series = series

  // 侧边文字
  option.graphic.push({
    type: 'text',
    rotation: Math.PI / 2,
    z: 100,
    left: '30',
    top: '55',
    style: {
      font: '1.5em "STHeiti", sans-serif',
      text: data.leftTitle
    }
  })

  // 循环标题
  const leftWidth = data.tableHeaderWidth
  option.xAxis.data.forEach((item, index, arr) => {
    for (let i = 0, len = option.series.length + 1; i < len; i++) {
      option.graphic.push({
        type: 'group',
        left: leftWidth + ((width - leftWidth) / arr.length) * index,
        bottom: (len - 1 - i) * data.tableHeight, // i == 0 ? 120 : i == 1 ? 60 : 0,
        children: [
          {
            type: 'rect',
            z: 100,
            left: 'center',
            top: 'middle',
            shape: {
              width: (width - leftWidth) / arr.length,
              height: data.tableHeight
            },
            style: {
              fill: 'rgba(0,0,0,0)',
              stroke: '#ccc',
              // lineWidth: 1,
              shadowBlur: 8,
              shadowOffsetX: 3,
              shadowOffsetY: 3,
              shadowColor: 'rgba(0,0,0,0)'
            }
          },
          {
            type: 'text',
            z: 100,
            top: 'middle',
            style: {
              fill: '#333',
              textAlign: 'center',
              width: (width - leftWidth) / arr.length,
              overflow: 'break',
              text: i == 0 ? item : option.series[i - 1].data[index],
              font: '14px Microsoft YaHei'
            }
          }
        ]
      })
    }
  })
  // 右边第一条线
  option.graphic.push({
    type: 'line',
    shape: {
      x1: width,
      y1: height,
      x2: width,
      y2: height - data.tableHeight * (series.length + 1) // height - 180
    },
    style: {
      stroke: '#ccc'
    }
  })
  return echartToImg(option, width, height)
}

// 扇形统计图
function comEchart2(data) {
  const width = data.width || 1200
  const height = data.height || 740
  // ecahrt配置
  let option = {
    title: {
      text: data.title,
      left: 'left'
    },
    legend: {
      type: 'scroll',
      orient: 'vertical',
      right: 0,
      top: 20,
      bottom: 20,
      data: data.legendData
    },
    series: [
      {
        type: 'pie',
        radius: ['0%', '40%'],
        labelLine: {
          length: 30
        },
        label: {
          formatter: '{b|{b}：}{c}  {per|{d}%}  ',
          // backgroundColor: '#F6F8FC',
          borderColor: '#8C8D8E',
          borderWidth: 1,
          padding: [0, 0, 0, 10],
          borderRadius: 4,
          rich: {
            b: {
              color: '#4C5058',
              fontSize: 14,
              fontWeight: 'bold',
              lineHeight: 33
            },
            per: {
              color: '#fff',
              backgroundColor: '#4C5058',
              padding: [3, 4],
              borderRadius: 4
            }
          }
        },
        data: data.seriesData
      }
    ]
  }
  return echartToImg(option, width, height)
}

// 柱状统计图样式2
function comEcahrt3(data) {
  const width = data.width || 1200
  const height = data.height || 740

  const labelOption = {
    show: true,
    position: 'insideBottom',
    align: 'left',
    verticalAlign: 'middle',
    rotate: 90,
    fontSize: 10
  }

  let option = {
    title: {
      text: data.title
    },
    legend: {
      right: 0,
      data: []
    },
    grid: {
      left: data.gridLeft || '5%',
      right: data.gridRight || '4%',
      bottom: data.gridBottom || '5%',
      containLabel: false
    },
    xAxis: [
      {
        axisLabel: { interval: 0, rotate: data.axisLabelRotate },
        type: 'category',
        data: data.xAxisData
      }
    ],
    yAxis: [
      {
        type: 'value'
      }
    ],
    graphic: [],
    series: []
  }

  if (data.min) {
    option.yAxis[0].min = data.min
  }

  if (data.max) {
    option.yAxis[0].max = data.max
  }

  // 循环展示数据 以及 循环左侧表格头显示
  let series = JSON.parse(JSON.stringify(data.seriesData))
  Array.from(series, (item, index) => {
    item.type = 'bar'
    item.label = labelOption
    option.legend.data.push(item.name)
  })

  // 设置标注线
  if (data.markLine) {
    let markLine = {
      data: []
    }
    data.markLine.forEach((item, index, arr) => {
      markLine.data.push({
        ...item,
        lineStyle: { color: 'red', width: 4 },
        label: {
          lineHeight: 40,
          overflow: 'break',
          formatter: '{b}{c} 个',
          fontSize: 24,
          borderWidth: 2,
          borderRadius: 5,
          padding: 10,
          width: 180,
          borderColor: '#ccc',
          backgroundColor: '#f5f5f5'
        }
      })
    })
    series[0].markLine = markLine
  }

  option.series = series

  // 侧边文字
  if (data.leftTitle) {
    option.graphic.push({
      type: 'text',
      rotation: Math.PI / 2,
      z: 100,
      left: '30',
      top: '55',
      style: {
        font: '1.5em "STHeiti", sans-serif',
        text: data.leftTitle
      }
    })
  }

  return echartToImg(option, width, height)
}

export const chart1 = result => {
  const dayCompareList = result.getSummaryProgress.current.dayCompareList
  const data = {
    // width: 1200, // chart 宽度
    // height: 740, // chart 高度
    // 顶部标题
    title: result.getSummaryDetail.trainNo + '车架修进度',
    // 左边标题
    leftTitle: '工作日/天',
    // 横坐标名称
    xAxisData: [
      // '镟轮',
    ],
    // 对应数据
    seriesData: [
      {
        name: '计划工作日',
        data: [
          // 20
        ]
      },
      {
        name: '实际工作日',
        data: [
          // 21
        ]
      }
    ],
    // 表格坐标头宽度
    tableHeaderWidth: 100,
    // 底部表格的高度
    tableHeight: 60
  }
  dayCompareList.forEach((item, index, arr) => {
    data.xAxisData.push(item.name)
    // 计划工作日
    data.seriesData[0].data.push(item.plannedDays)
    // 实际工作日
    data.seriesData[1].data.push(item.actualDays)
  })
  return comEchart(data)
}

export const chart2 = result => {
  const linePeriodDayList = result.getSummaryPeriod.linePeriodDayList
  // console.log(linePeriodDayList)

  const data = {
    // width: 1200, // chart 宽度
    // height: 740, // chart 高度
    // 顶部标题
    title: '架修周期',
    // 左边标题
    leftTitle: '工作日（天）',
    // 横坐标名称
    xAxisData: [
      // '1',
    ],
    // 对应数据
    seriesData: [
      {
        name: '计划架修周期',
        data: [
          // 20
        ]
      },
      {
        name: '实际架修周期',
        data: [
          // 120
        ]
      }
    ],
    // 表格坐标头宽度
    tableHeaderWidth: 100,
    // 底部表格的高度
    tableHeight: 40
  }

  //
  linePeriodDayList.forEach((item, index, arr) => {
    data.xAxisData.push(item.repairIndex)
    // 计划架修周期
    data.seriesData[0].data.push(item.plannedDays)
    // 实际架修周期
    data.seriesData[1].data.push(item.actualDays)
  })

  return comEchart(data)
}

export const chart3 = result => {
  const systemList = result.getSummaryCost.current.systemList
  const data = {
    width: 1600, // chart 宽度
    // 顶部标题
    title: result.getSummaryDetail.trainNo + '车各系统消耗金额',
    // 左边标题
    leftTitle: '消耗金额（元）',
    // 横坐标名称
    xAxisData: [
      // '转向架'
    ],
    // 对应数据
    seriesData: [
      {
        name: '备品备件(元)-合计',
        data: [
          // 418414
        ]
      },
      {
        name: '耗材(元)',
        data: [
          // 23109.9
        ]
      }
    ],
    // 表格坐标头宽度
    tableHeaderWidth: 150,
    // 底部表格的高度
    tableHeight: 60
  }

  systemList.forEach((item, index, arr) => {
    data.xAxisData.push(item.systemName)
    data.seriesData[0].data.push(item.mustRandomCost)
    data.seriesData[1].data.push(item.consumeCost)
  })

  return comEchart(data)
}

export const chart4 = result => {
  // console.log(result)
  const materialTopList = result.getSummaryCost.current.materialTopList
  const data = {
    width: 1100, // chart 宽度
    height: 600, // chart 高度
    // 顶部标题
    title: result.getSummaryDetail.trainNo + '车主要消耗占比',
    // 右侧颜色标识
    legendData: [
      // '节点、齿轮箱球铰',
    ],
    seriesData: [
      // { value: 20, name: '节点、齿轮箱球铰' },
    ]
  }

  // 处理数据
  materialTopList.forEach((item, index, arr) => {
    data.legendData.push(item.materialTypeName)
    data.seriesData.push({
      value: Number(item.cost.toFixed(2)),
      name: item.materialTypeName
    })
  })

  return comEchart2(data)
}

export const chart5 = result => {
  const trainList = result.getSummaryCost.line.trainList
  // console.log(trainList)

  const data = {
    width: 1200, // chart 宽度
    height: 1000, // chart 高度
    // 顶部标题
    title: result.getSummaryDetail.lineId + '号线车辆架修成本情况',
    // 左边标题
    leftTitle: '金额（万元）',
    // 横坐标名称
    xAxisData: [
      // '第1列0204车',
    ],
    // 对应数据
    seriesData: [
      {
        name: '自主维修金额',
        data: [
          // 418414,
        ]
      },
      {
        name: '委外修金额',
        data: [
          // 23109.9,
        ]
      },
      {
        name: '每列车架修成本',
        data: [
          // 418414,
        ]
      },
      {
        name: '每节车成本',
        data: [
          // 418414,
        ]
      }
    ],
    // 表格坐标头宽度
    tableHeaderWidth: 200,
    // 底部表格的高度
    tableHeight: 90
  }

  //
  trainList.forEach((item, index, arr) => {
    data.xAxisData.push(`第${item.repairIndex}列${item.trainNo}车`)
    // 自主维修金额
    data.seriesData[0].data.push(item.selfRepair)
    // 委外修金额
    data.seriesData[1].data.push(item.outsourceRepair)
    // 每列车架修成本
    data.seriesData[2].data.push(item.trainAverage)
    // 每节车成本
    data.seriesData[3].data.push(item.carAverage)
  })

  return comEchart(data)
}

export const chart6 = result => {
  const systemList = result.getSummaryFault.current.systemList
  // console.log(systemList)
  const data = {
    width: 1200, // chart 宽度
    height: 500, // chart 高度
    // 顶部标题
    title: result.getSummaryDetail.trainNo + '车系统故障分类统计',
    // 左边标题
    leftTitle: '故障个数',
    // 图表偏移 左
    gridLeft: '10%',
    // 图表偏移 下
    gridBottom: '15%',
    // X 坐标名称旋转
    axisLabelRotate: 45,
    // 横坐标名称
    xAxisData: [
      // '车体及内装',
    ],
    // 对应数据
    seriesData: [
      {
        name: '系统内故障数',
        data: [
          // 320
        ]
      },
      {
        name: 'A/B类故障个数',
        data: [
          // 220
        ]
      },
      {
        name: '未处理故障',
        data: [
          // 150
        ]
      }
    ]
  }

  //
  systemList.forEach((item, index, arr) => {
    data.xAxisData.push(item.systemName)
    // 系统内故障数
    data.seriesData[0].data.push(item.total)
    // A/B类故障个数
    data.seriesData[1].data.push(item.levelAB)
    // 未处理故障
    data.seriesData[2].data.push(item.unhandled)
  })

  return comEcahrt3(data)
}

export const chart7 = result => {
  const lineTrainList = result.getSummaryFault.lineTrainList
  // console.log(lineTrainList)

  const data = {
    width: 1200, // chart 宽度
    height: 560, // chart 高度
    // 顶部标题
    title: result.getSummaryDetail.lineId + '号线所有架修列车实际架修周期内故障数',
    // 左边标题
    leftTitle: '故障数/个',
    // 统计图样式 默认柱状图
    seriesType: 'line',
    // 统计图是否显示顶部数字
    seriesLabel: true,
    // 横坐标名称
    xAxisData: [
      // '第1列0204车',
    ],
    // 对应数据
    seriesData: [
      {
        name: '故障数',
        data: [
          // 20
        ]
      }
    ],
    // 表格坐标头宽度
    tableHeaderWidth: 120,
    // 底部表格的高度
    tableHeight: 60
  }

  //
  lineTrainList.forEach((item, index, arr) => {
    data.xAxisData.push(`第${item.repairIndex}列${item.trainNo}车`)
    data.seriesData[0].data.push(item.total)
  })

  return comEchart(data)
}

export const chart8 = result => {
  const trainList = result.getSummaryFault.warranty.trainList
  // console.log(trainList)

  const data = {
    width: 1200, // chart 宽度
    height: 500, // chart 高度
    // 顶部标题
    title: result.getSummaryDetail.lineId + '号线架修车质保期各车故障情况',
    // 图表偏移 左
    gridLeft: '5%',
    // 图表偏移 下
    gridBottom: '15%',
    // X 坐标名称旋转
    axisLabelRotate: 45,
    // 横坐标名称
    xAxisData: [
      // '第1列0204车'
    ],
    // 对应数据
    seriesData: [
      {
        name: '故障数量',
        data: [
          // 20
        ]
      },
      {
        name: '正线故障',
        data: [
          // 20
        ]
      },
      {
        name: 'B类及以上故障',
        data: [
          // 20
        ]
      }
    ]
  }

  //
  trainList.forEach((item, index, arr) => {
    // console.log(item, index, arr)
    data.xAxisData.push(`第${item.repairIndex}列${item.trainNo}车`)
    // 故障数量
    data.seriesData[0].data.push(item.total)
    // 正线故障
    data.seriesData[1].data.push(item.warrantyOnline)
    // B类及以上故障
    data.seriesData[2].data.push(item.warrantyAB)
  })

  return comEcahrt3(data)
}

export const chart9 = result => {
  const itemList = result.getSummaryOutsource.itemList
  // console.log(itemList)

  const data = {
    width: 1200, // chart 宽度
    height: 700, // chart 高度
    // 顶部标题
    title: '委外部件送修周期情况',
    // 图表偏移 左
    gridLeft: '5%',
    // 图表偏移 右
    gridRight: '20%',
    // 图表偏移 下
    gridBottom: '15%',
    // X 坐标名称旋转
    axisLabelRotate: 45,
    // 设置最大最小值
    min: 0,
    max: function(value) {
      return value.max + 45
    },
    // 横坐标名称
    xAxisData: [
      // '制动'
    ],
    // 标注线
    markLine: [
      {
        name: '轴承要求',
        yAxis: 45
      },
      {
        name: '其他合同要求',
        yAxis: 16
      }
    ],
    // 对应数据
    seriesData: [
      // {
      //   name: '0211车',
      //   data: [20, 20, 50, 50, 30, 44, 33, 22]
      // },
    ]
  }

  //
  itemList.forEach((item, index, arr) => {
    data.xAxisData.push(item.itemName)
    for (let i = 0, len = item.trainList.length; i < len; i++) {
      if (index == 0) {
        data.seriesData.push({
          name: `第${item.trainList[i].repairIndex}列${item.trainList[i].trainNo}车`,
          data: [item.trainList[i].days]
        })
      } else {
        data.seriesData[i].data.push(item.trainList[i].days)
      }
    }
  })

  return comEcahrt3(data)
}

export const chart10 = result => {
  const problemList = result.getSummaryOutsource.inProblemInfo.problemList
  // console.log(problemList)
  const data = {
    width: 1200, // chart 宽度
    height: 700, // chart 高度
    // 顶部标题
    title: '委外部件验收问题',
    // 图表偏移 左
    // gridLeft: '5%',
    // 图表偏移 右
    // gridRight: '20%',
    // 图表偏移 下
    gridBottom: '15%',
    // X 坐标名称旋转
    axisLabelRotate: 45,
    // 横坐标名称
    xAxisData: [
      // '思锐（车钩）',
    ],
    // 对应数据
    seriesData: [
      {
        name: '已完成',
        data: [
          // 2
        ]
      },
      {
        name: '跟踪',
        data: [
          // 2
        ]
      },
      {
        name: '总数',
        data: [
          // 2
        ]
      }
    ]
  }

  problemList.forEach((item, index, arr) => {
    data.xAxisData.push(item.itemName)
    // 已完成
    data.seriesData[0].data.push(item.handled)
    // 跟踪
    data.seriesData[1].data.push(item.tracking)
    // 总数
    data.seriesData[2].data.push(item.total)
  })

  return comEcahrt3(data)
}

export const chart11 = result => {
  const groupHoursList = result.getSummaryWorkTime.current.groupHoursList
  // console.log(groupHoursList)
  const data = {
    width: 1100, // chart 宽度
    height: 600, // chart 高度
    // 顶部标题
    title: result.getSummaryDetail.trainNo + '车各班组工作量/人*小时',
    // 右侧颜色标识
    legendData: [
      // '转向架工班',
    ],
    seriesData: [
      // { value: 20, name: '转向架工班' },
    ]
  }

  groupHoursList.forEach((item, index, arr) => {
    data.legendData.push(item.groupName)
    data.seriesData.push({
      value: item.hours,
      name: item.groupName
    })
  })

  return comEchart2(data)
}

export const chart12 = result => {
  const lastThree = result.getSummaryWorkTime.lastThree
  // console.log(lastThree)
  const data = {
    // width: 1200, // chart 宽度
    // height: 740, // chart 高度
    // 顶部标题
    title: '工班作业工时情况',
    // 左边标题
    leftTitle: '工时/人*小时',
    // 横坐标名称
    xAxisData: [
      // '转向架工班',
    ],
    // 对应数据
    seriesData: [
      // {
      //   name: '第21列0211车架修',
      //   data: [20, 20, 50, 50, 30, 210, 230, 30]
      // },
    ],
    // 表格坐标头宽度
    tableHeaderWidth: 200,
    // 底部表格的高度
    tableHeight: 60
  }

  //
  lastThree.forEach((item, index, arr) => {
    // console.log(item, index, arr)
    data.seriesData.push({
      name: `第${item.repairIndex}列${item.trainNo}车架修`,
      data: []
    })
    for (let i = 0, len = item.groupHoursList.length; i < len; i++) {
      data.seriesData[index].data.push(item.groupHoursList[i].hours)
      if (index == 0) {
        data.xAxisData.push(item.groupHoursList[i].groupName)
      }
    }
  })

  return comEchart(data)
}

export const chart13 = result => {
  const lineTrainList = result.getSummaryWorkTime.lineTrainList
  // console.log(lineTrainList)

  const data = {
    width: 1200, // chart 宽度
    height: 500, // chart 高度
    // 顶部标题
    title: result.getSummaryDetail.lineId + '号线所有列车架修作业工时情况',
    // 左边标题
    // leftTitle: '故障个数',
    // 图表偏移 左
    // gridLeft: '10%',
    // 图表偏移 下
    gridBottom: '15%',
    // X 坐标名称旋转
    axisLabelRotate: 45,
    // 横坐标名称
    xAxisData: [
      // '第1列0204车',
    ],
    // 对应数据
    seriesData: [
      {
        name: '作业工时',
        data: [
          // 20
        ]
      }
    ]
  }

  //
  lineTrainList.forEach((item, index, arr) => {
    data.xAxisData.push(`第${item.repairIndex}列${item.trainNo}车`)
    data.seriesData[0].data.push(item.hours)
  })

  return comEcahrt3(data)
}
