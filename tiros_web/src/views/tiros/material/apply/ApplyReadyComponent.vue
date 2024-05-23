<template>
  <div>
    <a-row :gutter="24">
      <a-col :md="24" :sm="24">
        <span style="float: right; overflow: hidden; margin-bottom: 8px" class="table-page-search-submitButtons">
          <a-space>
            <a-button
              v-if="!isDistribution"
              :loading="confirmLoading"
              :disabled="records.length < 1"
              class="iconBtn"
              @click="autoArrange"
              >自动安排库位</a-button
            >
            <a-button
              v-if="!isDistribution"
              :loading="confirmLoading"
              :disabled="records.length < 1"
              class="iconBtn"
              @click="arrangePan"
              >批量设置托盘</a-button
            >
          </a-space>
        </span>
      </a-col>
    </a-row>
    <div style="height: calc(70vh - 45px)" v-if="!isDistribution">
      <vxe-table
        border
        max-height="100%"
        ref="listTable"
        :align="allAlign"
        :data="tableData"
        show-overflow="tooltip"
        @checkbox-change="checkboxChange"
        @checkbox-all="checkboxChange"
        :edit-config="{ trigger: 'click', mode: 'row' }"
        :checkbox-config="{ highlight: true, trigger: 'row', range: true }"
      >
        <vxe-table-column type="seq" fixed="left" width="40"></vxe-table-column>
        <vxe-table-column type="checkbox" width="40" fixed="left"></vxe-table-column>
        <vxe-table-column field="materialTypeCode" title="物资编码" width="150"></vxe-table-column>
        <vxe-table-column field="materialTypeName" title="物资名称" min-width="180" align="left"></vxe-table-column>
        <vxe-table-column field="materialTypeSpec" title="规格型号" min-width="180" align="left"></vxe-table-column>
        <vxe-table-column field="materialTypeUnit" title="单位" width="100"></vxe-table-column>
        <vxe-table-column field="useCategory_dictText" title="类别" width="100"></vxe-table-column>
        <vxe-table-column field="amount" title="申请数量" width="100"></vxe-table-column>
        <vxe-table-column field="receive" title="发料数量" width="100"></vxe-table-column>
        <vxe-table-column
          field="sourceLocationAndPalletName"
          title="发放信息"
          width="380"
          sort-type="string"
          sortable
        ></vxe-table-column>
        <!-- <vxe-table-column title="发放信息" width="380">
          <template v-slot="{row}">
            <div>
              <div v-for="(item ,k) in row.assignDetailList" :key="k">
                <span v-if="item.sourceLocationName">{{item.sourceLocationName}} | {{item.amount}}</span>
                <span v-if="item.palletName"> | {{item.palletName}}</span>
                <span v-if="item.tradeNo">｜ 第{{item.tradeNo}}批次</span>
              </div>
            </div>
          </template>
        </vxe-table-column> -->
        <vxe-table-column field="remark" title="备注" width="150" header-align="center" align="left"></vxe-table-column>
        <vxe-table-column title="操作" width="100">
          <template v-slot="{ row }">
            <a @click.stop="openDistribution(row)">发放管理</a>
          </template>
        </vxe-table-column>
      </vxe-table>
    </div>
    <!-- 发放管理显示 -->
    <div class="panel-main" style="height: calc(70vh - 45px)" v-if="isDistribution">
      <!-- 物料明细 -->
      <div class="panel-left">
        <vxe-table
          border
          ref="materialTable"
          height="auto"
          keep-source
          :data="tableData"
          :radio-config="{
            highlight: true,
            trigger: 'row',
            range: true,
            checkMethod: () => {
              return curEditMode === 0
            },
          }"
          @radio-change="onRadioChange"
          @cell-click="onMaterialCellClick"
        >
          <vxe-table-column type="radio" width="40" fixed="left"></vxe-table-column>
          <vxe-table-column field="materialTypeCode" width="160" title="物资编码"></vxe-table-column>
          <vxe-table-column field="materialTypeName" title="名称"></vxe-table-column>
          <vxe-table-column field="materialTypeSpec" title="规格"></vxe-table-column>
          <vxe-table-column field="amount" title="所需数量" width="80"></vxe-table-column>
          <vxe-table-column field="receive" title="发料数量" width="80"></vxe-table-column>
        </vxe-table>
      </div>
      <!-- 发放明细 -->
      <div class="panel-right">
        <div class="ant-alert ant-alert-info" style="margin-bottom: 16px">
          <a-space>
            <a-button type="primary" @click="addDetail">添加发放库位</a-button>
            <a-button @click="closeDistribution">关闭</a-button>
            <span style="color: red; margin-left: 15px">{{ `总量: ${selectInfo ? selectInfo.amount : 0};` }}</span>
            <span style="color: red; margin-left: 5px">{{ `差额：${calcAmount}` }}</span>
          </a-space>
        </div>
        <div style="height: calc(100% - 62px)">
          <vxe-table
            border
            ref="distributionTable"
            height="auto"
            keep-source
            :data="tableDistributionData"
            :edit-rules="validRules"
            :edit-config="{ trigger: 'manual', mode: 'row', autoClear: false, showStatus: true }"
          >
            <!-- <vxe-table-column title="物资编码"> {{ selectInfo ? selectInfo.materialTypeCode : '' }} </vxe-table-column> -->
            <vxe-table-column field="sourceLocationName" title="来源库位" :edit-render="{ name: 'input' }">
              <template v-slot:edit="{ row }">
                <a-select
                  ref="locationSelect"
                  placeholder="选择库位"
                  v-model="row.sourceLocationName"
                  :open="false"
                  style="width: 100%"
                  @dropdownVisibleChange="openlocationModal(row)"
                >
                  <a-icon slot="suffixIcon" type="ellipsis" />
                </a-select>
              </template>
            </vxe-table-column>
            <vxe-table-column field="palletName" title="存放托盘" :edit-render="{ name: 'input' }">
              <template v-slot:edit="{ row }">
                <a-select
                  placeholder="选择托盘"
                  v-model="row.palletName"
                  :open="false"
                  style="width: 100%"
                  @dropdownVisibleChange="openPalletModal()"
                >
                  <a-icon slot="suffixIcon" type="ellipsis" />
                </a-select>
              </template>
            </vxe-table-column>
            <vxe-table-column field="tradeNo" title="所属批次" width="270" :edit-render="{ name: 'input' }">
              <template v-slot:edit="{ row }">
                <div v-if="row.needChooseTradeNo">
                  <a-select v-model="row.tradeNo" @change="tradeNoCHange(row)" style="width: 100%">
                    <a-select-option v-for="(item,k) in row.tradeNoChoiceList" :key="k" :value="item.tradeNo">
                      {{item.tradeNo}} &nbsp;&nbsp; {{item.price}}
                    </a-select-option>
                  </a-select>
                </div>
                <div v-else>
                  {{row.tradeNo}}
                </div>
              </template>
            </vxe-table-column>
            <vxe-table-column
              field="amount"
              title="发放数量"
              width="120"
              align="center"
              :edit-render="{ name: '$input', props: { type: 'number', min: 0 } }"
            ></vxe-table-column>
            <!-- <vxe-table-column title="操作" width="120px" align="center" v-if="!readonly">
            <template v-slot="{ row }">
              <a-button v-if="!isMustApply" type="dashed" size="small" @click="handleDel(row)">删除</a-button>
            </template>
          </vxe-table-column> -->
            <vxe-table-column title="操作" width="120px" align="center" :show-overflow="false">
              <template v-slot="{ row }">
                <a-space v-if="row && !$refs.distributionTable.isActiveByRow(row)">
                  <a-button type="dashed" size="small" @click.stop="editRowEvent(row)">编辑 </a-button>
                  <a-button type="dashed" size="small" @click.stop="handleDel(row)">删除</a-button>
                </a-space>
                <a-space v-else>
                  <a-button type="dashed" size="small" @click.stop="saveRowEvent(row)">保存</a-button>
                  <a-button type="dashed" size="small" @click.stop="cancelRowEvent(row)">取消</a-button>
                </a-space>
              </template>
            </vxe-table-column>
          </vxe-table>
        </div>
      </div>
    </div>
    <ApplyDistributionModal ref="distributionModal" @ok="onSuccess"></ApplyDistributionModal>
    <PalletList ref="palletModal" :modeType="2" @ok="onSelectPallet"></PalletList>
    <PalletList ref="palletModal2" :modeType="2" @ok="onChoosePallet"></PalletList>
    <LocationList ref="locationModal" :modeType="2" @ok="onSelectLocation"></LocationList>
  </div>
</template>

<script>
import {
  applyReadyConfirm,
  getDetailListByOrderId,
  getApplyMustAssign,
  getApplyMustDetaial,
  autoArrangeHouse,
} from '@api/tirosMaterialApi'
import ApplyDistributionModal from '@views/tiros/material/apply/ApplyDistributionModal'
import { ajaxGetDictItems } from '@api/api'
import PalletList from '@views/tiros/common/selectModules/PalletList'
import LocationList from '@views/tiros/common/selectModules/LocationList'
import materialUtil from '@views/tiros/util/MaterialUtil'

export default {
  name: 'ApplyReadyComponent',
  components: { ApplyDistributionModal, PalletList, LocationList },
  props: {
    businessKey: {
      type: String,
      default: null,
    },
    isReadonly: {
      type: Boolean,
      default: false,
    },
    fromFlow: {
      type: Boolean,
      default: false,
    },
    orderType: {
      type: Number,
      default: '',
    },
  },
  data() {
    return {
      records: [],
      confirmLoading: false,
      tableData: [],
      tableDistributionData: [],
      palletList: [],
      // 发放
      distributionTableList: [],
      model: {},
      allAlign: 'center',
      dictCode: 'bu_material_pallet,name,id,status=1',
      queryParam: {
        status: [0],
        orderId: '',
      },
      curRow: null,
      isDistribution: false,
      selectInfo: {},
      addParam: {
        amount: '',
        applyDetailId: '',
        id: '',
        locationName: '',
        locationWbs: '',
        sourceLocationName: '',
        materialTypeId: '',
        palletId: '',
        palletName: '',
        stockPrice: '',
        tradeNo: '',
      },
      curEditMode: 0, //0: 默认状态 1: 新增 2:编辑
      validRules: {
        sourceLocationName: [{ required: true, message: '请选择库位', trigger: 'manual' }],
        tradeNo: [{ required: true, message: '批次号不能为空', trigger: 'manual' }],
        amount: [{ required: true, message: '请输入数量', trigger: 'manual' }],
      },
      // locationMaxAmount: 0,
    }
  },
  computed: {
    hasActiveRow: function () {
      return this.$refs.distributionTable.getActiveRecord() ? true : false
    },
    calcAmount() {
      return this.getAmount()
    },
  },
  mounted() {
    if (this.businessKey) {
      this.edit(this.businessKey)
    }
  },
  methods: {
    tradeNoCHange(row){
      console.log(row)
      const item = row.tradeNoChoiceList.filter((item) => {
        return  item.tradeNo == row.tradeNo //条件;
      });
      row.price = item[0].price;
    },
    checkboxChange(e) {
      this.records = e.records
    },
    edit(id) {
      this.initDictData()
      this.queryParam.orderId = id
      this.findList()
    },
    findList() {
      this.tableData = []
      this.records = []
      getDetailListByOrderId(this.queryParam).then((res) => {
        if (res.success) {
          this.tableData = [...this.tableData, ...res.result]
          // 拼接分配明细
          materialUtil.joinDistributionInfo(this.tableData)
          this.distributionTableList = []
          // 获取分配明细， 现在上面的接口直接返回了
          /*res.result.forEach((t) => {
            // 加载明细
            getApplyAssign({
              applyDetailId: t.id,
            }).then((res) => {
              if (res.success) {
                this.distributionTableList = [...this.distributionTableList, ...res.result]
              } else {
                this.$message.error('加载明细记录失败')
                console.error('加载明细记录失败：', res.message)
              }
            })
          })*/

          // 如果是非发料工单， 获取这张工单上的必换件, 获取了干嘛？ 只是用来显示？
          /*  jakgong 注释，暂时不显示吧，只显示需要发放的
          if (this.orderType !== 4) {
            getApplyMustDetaial(this.queryParam).then(res =>{
              if (res.success) {
                let mustList = res.result

                mustList.forEach(item=>{
                  item.noSave=true
                })

                this.tableData = [...this.tableData, ...mustList]
                res.result.forEach((t) => {
                  // 加载分配明细
                  getApplyMustAssign({
                    orderMaterialId: t.orderMaterialId,
                  }).then((res) => {
                    if (res.success) {
                      this.distributionTableList = [...this.distributionTableList, ...res.result]
                    } else {
                      this.$message.error('加载明细记录失败')
                      console.error('加载明细记录失败：', res.message)
                    }
                  })
                })
              } else {
                this.$message.error(res.message)
              }
            })
          }
          */
        } else {
          this.$message.error('加载数据失败')
          console.error('加载数据失败：', res.message)
        }
        this.$emit('loaded')
      })
    },
    //  自动
    autoArrange() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length <= 0) {
        this.$message.warn('请先选择要进行自动安排的记录！')
        return
      }
      this.$confirm({
        content: `将根据现有库存情况自动安排库位，并覆盖原有数据？`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          let param = {
            applyDetailIdList: [],
            type: 0,
          }
          selectRecords.forEach((item) => {
            param.applyDetailIdList.push(item.id)
          })
          this.confirmLoading = true
          autoArrangeHouse(param).then((res) => {
            if (res.success) {
              let updateRows = res.result.applyDetailList
              this.$message.success(res.result.assignResultMessage)
              // 更新分配数据
              selectRecords.forEach((row) => {
                let findRow = updateRows.find((r) => {
                  return r.id === row.id
                })
                if (findRow) {
                  findRow.sourceLocationAndPalletName = ''
                  findRow.assignDetailList.forEach((assign) => {
                    let source = assign.sourceLocationName + ' | ' + assign.amount
                    if (assign.palletName) {
                      source += ` | ${assign.palletName}`
                    }
                    if (assign.tradeNo) {
                      source += ` | 第${assign.tradeNo}批次`
                    }
                    findRow.sourceLocationAndPalletName += source + ' \n'
                  })
                  Object.assign(row, findRow)
                }
              })
            } else {
              this.$message.error(res.message)
            }
            this.confirmLoading = false
          })
        },
        onCancel: () => {},
      })
    },
    // 批量设置托盘
    arrangePan() {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      if (selectRecords.length <= 0) {
        this.$message.warn('请先选择要进行自动安排的记录！')
        return
      }
      const list = selectRecords.filter((item) => {
        return item.assignDetailList.length <= 0 //条件;
      })
      if (list.length > 0) {
        this.$message.warning('请先安排库位信息')
        return
      }
      this.$refs.palletModal.showModal()
    },
    // 托盘选择
    onSelectPallet(records) {
      let selectRecords = this.$refs.listTable.getCheckboxRecords()
      try {
        Array.from(selectRecords, (item) => {
          Array.from(item.assignDetailList, (item1) => {
            item1.palletId = records[0].id
            item1.palletName = records[0].name
          })
        })
        this.updateAssignInfo()
        this.$message.success('设置成功')
      } catch (error) {
        this.$message.error('设置失败')
      }
      // if (!this.curRow) {
      //   if (records.length > 0) {
      //     this.queryParam.palletId = records[0].id
      //     this.queryParam.palletName = records[0].name
      //     this.form.setFieldsValue({
      //       palletName: records[0].name,
      //     })
      //   }
      // } else {
      //   console.log(2)
      //   // this.curRow.palletId = records[0].id
      //   // this.curRow.palletName = records[0].name
      //   delete this.curRow
      // }
    },
    // 确定
    async save(onlySave) {
      if (!this.fromFlow) {
        let selectRecords = this.$refs.listTable.getCheckboxRecords()
        if (selectRecords.length > 0) {
          this.$confirm({
            content: `是否确认发放选中${selectRecords.length}数据？`,
            onOk: () => {
              let detailList = selectRecords.map(function (obj, index) {
                return {
                  id: obj.id,
                  palletId: obj.palletName,
                  receive: obj.receive,
                }
              })
              let param = {
                id: this.queryParam.orderId,
                detailList: detailList,
              }
              applyReadyConfirm(param)
                .then((res) => {
                  if (res.success) {
                    this.$emit('ok')
                    // this.findList()
                  } else {
                    this.$emit('fail', res)
                    console.error('保存失败:', res.message)
                  }
                })
                .catch((error) => {
                  this.$message.error('保存异常')
                  console.error('保存异常：', error)
                  this.$emit('fail')
                })
            },
          })
        } else {
          this.$message.error('尚未选中任何数据!')
        }
      } else {
        if (this.tableData && this.tableData.length > 0) {
          let param = {
            id: this.queryParam.orderId,
            onlySave: onlySave, // 是否只保存
            detailList: this.tableData.filter((t) => !t.noSave),
          }
          let isReceive = true

          let materialIsLack = this.tableData.filter((e) => {
            return e.receive === 0
          })
          // console.log('materialIsLack.length = ' ,materialIsLack.length)

          if (!onlySave && materialIsLack.length) {
            isReceive = await new Promise((resolve, reject) => {
              let confirmMessage =  materialIsLack.map((e) =>
                '[' + e.materialTypeName + '-' + e.materialTypeCode + ']'
                + '缺少' + (e.amount - e.receive)
              ).join("；")

              this.$confirm({
                // content: `还有物资没有填写发放数量，是否继续提交？`,
                content: `数量不匹配提醒：  ${confirmMessage}  ;  是否继续提交？`,
                onOk: () => {
                  resolve(true)
                },
                onCancel: () => {
                  resolve(false)
                },
              })
            })
          }
          if (!isReceive) {
            // this.$emit('fail', '请填写发放数量')
            this.$emit('cancel')
            return
          }
          // 保存领用明细
          applyReadyConfirm
            .call(this, param)
            .then((res) => {
              if (res.success) {
                this.$emit('ok')
                // this.findList()
              } else {
                this.$emit('fail', res)
                console.error('备料保存失败:', res.message)
              }
            })
            .catch((error) => {
              this.$message.error('备料保存异常')
              console.error('备料保存异常：', error)
              this.$emit('fail', error)
            })
        } else {
          this.$emit('ok')
        }
      }
    },
    close() {
      this.$emit('close')
    },
    initDictData() {
      ajaxGetDictItems(this.dictCode, null).then((res) => {
        if (res.success) {
          if (res.result) {
            res.result.map((item) => {
              this.palletList = this.palletList.concat({
                label: item.text,
                value: item.value,
              })
            })
          }
        }
      })
    },
    openDistribution(row) {
      this.curRow = row
      // this.$refs.distributionModal.showModal(row)
      this.openDistribution(row)
    },
    onSuccess(assignList) {
      // 更新发放数量，库位信息 -- 2021-08-03修改
      // this.curRow.assignDetailList = assignList
      // this.curRow.receive = 0
      // this.curRow.sourceLocationAndPalletName = ''
      // this.curRow.assignDetailList.forEach((e) => {
      //   this.curRow.receive += Number(e.amount)
      //   let source = e.sourceLocationName + ' | ' + e.amount
      //   if (e.palletName) {
      //     source += ` | ${e.palletName}`
      //   }
      //   if (e.tradeNo) {
      //     source += ` | 第${e.tradeNo}批次`
      //   }
      //   this.curRow.sourceLocationAndPalletName += source + ' \n'
      // })

      // 2021-08-03修改
      assignList.forEach((element) => {
        let record = this.tableData.find((e) => e.id === element.applyDetailId)
        record.assignDetailList = JSON.parse(JSON.stringify(assignList))
        record.receive = 0
        record.assignDetailList.forEach((e) => {
          record.receive += Number(e.amount)
          let source = e.sourceLocationName + ' | ' + e.amount
          if (e.palletName) {
            source += ` | ${e.palletName}`
          }
          if (e.tradeNo) {
            source += ` | 第${e.tradeNo}批次`
          }
          record.sourceLocationAndPalletName += source + ' \n'
        })
      })

      /*this.tableData.forEach((element) => {
        element.receive = 0
        this.distributionTableList.forEach((e) => {
          if (e.applyDetailId === element.id) {
            element.receive += Number(e.amount)
          }
        })
      })*/
    },
    updateAssignInfo() {
      this.tableData.forEach((row) => {
        row.sourceLocationAndPalletName = ''
        row.assignDetailList.forEach((assign) => {
          let source = assign.sourceLocationName + ' | ' + assign.amount
          if (assign.palletName) {
            source += ` | ${assign.palletName}`
          }
          if (assign.tradeNo) {
            source += ` | 第${assign.tradeNo}批次`
          }
          row.sourceLocationAndPalletName += source + ' \n'
        })
      })
    },

  // 打开发放管理
    openDistribution(row = null) {
      this.isDistribution = true
      this.curEditMode = 0
      this.$nextTick(() => {
        this.$refs.materialTable.setRadioRow(row || this.tableData[0] || {})
        this.setActiveRow(row || this.tableData[0])
      })
    },

    closeDistribution(){
      if (this.hasActiveRow) {
        this.$message.warn('存在未保存数据，请先保存')
        return
      }
      this.isDistribution = false
      this.curEditMode = 0
    },

    /**
     * 选择物资
     */
    setActiveRow(row) {
      console.log(row)
      this.tableDistributionData = []
      this.selectInfo = {}
      this.selectInfo = this.tableData.find((e) => e.id === row.id)
      this.selectInfo.assignDetailList.forEach((e) => {
        this.tableDistributionData.push(JSON.parse(JSON.stringify(e)))
      })
      // this.locationMaxAmount = this.selectInfo.amount
    },

    /***
     * 物资列表选择后
     */
    onRadioChange({ row }) {
      if (row && !this.hasActiveRow) {
        this.setActiveRow(row)
      }
    },

    onMaterialCellClick({ $event }) {
      $event.stopPropagation()
    },

    /**
     * 新增明细
     */
    addDetail() {
      if (this.$refs.distributionTable.getActiveRecord()) {
        return
      }
      this.curEditMode = 1
      let record = {
        applyDetailId: this.selectInfo.id,
        materialTypeId: this.selectInfo.materialTypeId,
        amount: this.getAmount(),
      }
      this.$refs.distributionTable.insertAt(record, -1).then(({ row }) => {
        this.$refs.distributionTable.setActiveRow(row)
      })
    },

    /**
     * 编辑明细
     */
    editRowEvent(row) {
      if (this.$refs.distributionTable.getActiveRecord()) {
        return
      }
      this.curEditMode = 2
      this.$refs.distributionTable.setActiveRow(row)
    },

    /**
     * 获取差额数量
     */
    getAmount() {
      if (!this.selectInfo) {
        return 0
      }
      let grantCound = 0
      this.tableDistributionData.forEach((e) => {
        grantCound += Number(e.amount)
      })
      return this.selectInfo.amount - grantCound
    },

    /**
     * 打开库位选择
     */
    openlocationModal(row) {
      // if (open) {
      this.$refs.locationModal.showModal(this.selectInfo.materialTypeId, row.applyDetailId, row.id)
      // }
    },

    //库位选择
    onSelectLocation(records) {
      // console.log(records)
      if (records.length > 0) {
        let { row } = this.$refs.distributionTable.getActiveRecord()
        if (row) {
          row.locationName = records[0].warehouseName
          row.locationWbs = records[0].warehouseWbs
          row.sourceLocationName = records[0].sourceLocationName
          row.tradeNo = records[0].tradeNo
          row.price = records[0].price
          row.needChooseTradeNo = records[0].needChooseTradeNo
          row.tradeNoChoiceList = records[0].tradeNoChoiceList
          row.locationNum = records[0].usableAmount
          // let tableAmount = 0
          // console.log(this.tableDistributionData)
          // this.tableDistributionData.forEach((e) => {
          //   if (e.locationWbs === records[0].warehouseWbs) {
          //     tableAmount += e.amount
          //   }
          // })
          // console.log(tableAmount)
          // this.locationMaxAmount = records[0].amount - tableAmount
          console.log(this.calcAmount , row.locationNum)
          if(this.calcAmount > row.locationNum){
            row.amount = row.locationNum
          }else if(this.calcAmount > 0 && this.calcAmount <= row.locationNum){
            row.amount = this.calcAmount
          }else{
            row.amount = 0
          }

          // if (this.locationMaxAmount < row.amount) {
          //   row.amount = this.locationMaxAmount
          // }
        }
      }
    },
    /**
     * 保存明细
     */
    saveRowEvent(row) {
      console.log(row)
      this.$refs.distributionTable.validate(row, (valid) => {
        if (!valid) {
          // // 保存验证判断
          //   // 发放数量判断


          if (this.tableDistributionData.find((e) => e.sourceLocationName === row.sourceLocationName && e.tradeNo === row.tradeNo) && this.curEditMode === 1) {
            this.$message.warn(`该仓库该批次的物资记录已存在`)
            return
          }

          if (typeof row.locationNum === 'number' && row.amount > row.locationNum) {
            this.$message.warn(`该仓库该批次的物资库存量不足`)
            return
          }


          let grantCound = 0
          console.log(this.tableDistributionData)
          this.tableDistributionData.forEach((e) => {
            if(!(e.sourceLocationName === row.sourceLocationName , e.tradeNo === row.tradeNo )){
              console.log(1111111)
              grantCound += Number(e.amount)
            }
            // if(e.sourceLocationName != row.sourceLocationName && e.tradeNo != row.tradeNo && e.materialTypeId != row.materialTypeId){
            //   grantCound += Number(e.amount)
            // }
          })
          if ((grantCound + Number(row.amount)) > Number(this.selectInfo.amount)) {
            this.$message.warn('选择的实际数量超过了发放数量')
            return
          }

          // console.log(this.selectInfo.amount,this.tableDistributionData)


          // return

          if (this.curEditMode === 1) {
            row.isNew = true
            this.tableDistributionData.push(JSON.parse(JSON.stringify(row)))
          }
          this.$refs.distributionTable.clearActived()
          this.onSuccess(this.tableDistributionData)
          this.curEditMode = 0
        }
      })
    },
    /**
     * 取消明细
     */
    cancelRowEvent(row) {
      this.$refs.distributionTable.clearActived()
      if (this.curEditMode === 1) {
        // 新增,点击取消
        this.$refs.distributionTable.remove(row)
      } else if (this.curEditMode === 2) {
        // 还原行数据
        this.$refs.distributionTable.revertData(row)
      }
      this.curEditMode = 0
    },
    /**
     * 删除明细
     */
    handleDel(row) {
      if (this.$refs.distributionTable.getActiveRecord()) {
        return
      }
      this.$confirm({
        content: `是否删除选中的数据？`,
        okText: '确定',
        cancelText: '取消',
        onOk: () => {
          this.tableDistributionData.splice(
            this.tableDistributionData.findIndex((e) => e.sourceLocationName === row.sourceLocationName),
            1
          )

          let record = this.tableData.find((e) => e.id === row.applyDetailId)
          if (record.assignDetailList.find((e) => e.sourceLocationName === row.sourceLocationName)) {
            record.receive -= record.assignDetailList.find((e) => e.sourceLocationName === row.sourceLocationName).amount
            record.assignDetailList.splice(
              record.assignDetailList.findIndex((e) => e.sourceLocationName === row.sourceLocationName),
              1
            )
          }
          this.$nextTick(() => {
            this.locationMaxAmount = this.calcAmount
          })
        },
      })
      this.curEditMode = 0
    },

    // 打开托盘Modal
    openPalletModal() {
      this.$refs.palletModal2.showModal(this.selectInfo.materialTypeId, this.selectInfo.materialTypeName)
    },

    /**
     * 托盘选择
     */
    onChoosePallet(records) {
      if (records.length > 0) {
        let { row } = this.$refs.distributionTable.getActiveRecord()
        if (row) {
          row.palletId = records[0].id
          row.palletName = records[0].name
        }
      }
    },
  },
}
</script>

<style lang="less" scoped>
.panel-main {
  display: flex;
  // flex-direction: column;
  justify-content: space-between;

  .ant-alert-info {
    width: 100%;
  }

  .panel-right{
    height: 100%;
    width: calc(60% - 12px);
  }
  .panel-left {
    height: 100%;
    width: calc(40% - 12px);
  }
}
</style>