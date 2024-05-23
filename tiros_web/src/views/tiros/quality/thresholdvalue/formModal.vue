<template>
  <div>
    <a-modal
      title="阈值设置"
      width="100%"
      :visible="visible"
      :footer="null"
      dialogClass="fullDialog no-footer"
      destroyOnClose
      @cancel="handleCancel"
    >
      <div>
        <a-row type="flex" justify="space-between" style="margin-bottom: 13px">
          <a-col :span="18">
            <div
              v-if="selectItem"
              style="line-height: 32px; padding-left: 15px; font-weight: bold; color: red"
            >
              {{ selectItem.itemName }}：{{ selectItem.linkDomain }}
              {{ selectItem.operator_dictText }}
              {{ selectItem.threshold }}
              <span v-if="selectItem.operator2 && selectItem.threshold2">
                且 {{ selectItem.operator2_dictText }} {{ selectItem.threshold2 }}
              </span>
              <span style="margin-left: 10px; color: #cccccc">
                提示：{{ selectItem.template }}
              </span>
            </div>
            <div v-else style="line-height: 32px; padding-left: 15px; font-weight: bold; color: #cccccc">
              <span>没有设置阈值</span>
            </div>
          </a-col>
          <a-col :span="6" style="text-align: right">
            <a-space v-if="!readOnly">
              <a-button v-show="selectFormId" type="primary" @click="setValue">
                设置阈值
              </a-button>
              <a-button
                v-show="selectFormId"
                type="dashed"
                class="primary-color"
                @click="showDrawer"
              >
                已设置列表
              </a-button>
            </a-space>
          </a-col>
        </a-row>
        <div
          id="luckysheet"
          style="margin: 0px; padding: 0px; width: 100%; height: calc(100vh - 115px)"
        ></div>
      </div>
<!--    不知道加个关闭干嘛，右上角由关闭  <template slot="footer">
        <a-button @click="handleCancel"> 关闭 </a-button>
      </template>-->
    </a-modal>
    <a-drawer
      title="已设置列表"
      placement="right"
      :closable="true"
      :visible="drawerVisible"
      :destroyOnClose="true"
      :zIndex="99998"
      :width="800"
      @close="onClose"
    >
      <vxe-table
        border
        ref="listTable"
        :align="allAlign"
        :data="settingList"
        :auto-resize="true"
        show-overflow="ellipsis"
        :edit-config="{ trigger: 'manual', mode: 'row' }"
      >
        <vxe-table-column
          field="itemName"
          title="测量项名"
          min-width='200'
          align="left"
          header-align="center"
        ></vxe-table-column>
        <vxe-table-column field="linkDomain" title="区域" width="120"></vxe-table-column>
        <vxe-table-column
          field="operator_dictText"
          title="操作符"
          width="80"
        ></vxe-table-column>
        <vxe-table-column field="threshold" title="阈值" width="100"></vxe-table-column>
        <vxe-table-column
          field="assetTypeName"
          title="设备"
          min-width='120'
        ></vxe-table-column>
        <vxe-table-column title="操作" width="100" fixed="right">
          <template v-slot="{ row }">
            <a-space>
              <a-button type="link" @click="editSetting(row)">编辑</a-button>
              <a-button type="link" @click="deleteSetting(row)">删除</a-button>
            </a-space>
          </template>
        </vxe-table-column>
      </vxe-table>
    </a-drawer>
    <ThresholdEdit
      v-if="editVisible && selectFormId"
      :setting="curSetting"
      :form-id="selectFormId"
      :visible.sync="editVisible"
      :zones="selectZones"
      :zoneNames="selectZoneNames"
    ></ThresholdEdit>
    <train-asset-type ref="selectForm" :multiple="false" @ok="selectTrainSys"></train-asset-type>
  </div>
</template>

<script>
import {
  getFormThresholds,
  deleteThreshold,
  getRowColThreshold,
} from "@api/tirosQualityApi";
import TrainAssetType from "@/views/tiros/common/selectModules/TrainAssetType";
import { getFormContent } from "@api/tirosApi";
import qs from "qs";
import clone from "clone";

import ThresholdEdit from "@views/tiros/quality/thresholdvalue/ThresholdEdit"; 

export default {
  name: "formModal",
  components: { TrainAssetType, ThresholdEdit },
  data() {
    return {
      visible: false,
      readOnly: false,
      drawerVisible: false,
      selectFormId: "",
      editVisible: false,
      loading: false,
      queryParam: {
        name: "",
        lineId: "",
        assetTypeId: "",
        pageNo: 1,
        pageSize: 1000,
      },
      allAlign: "center",
      tableData: [],
      assetTypeName: undefined,
      colNames: [],
      defaultOptions: {
        container: "luckysheet", //luckysheet为容器id
        title: "sheet",
        column: 26, // 列数
        row: 50, // 行数
        lang: "zh", // 设定表格语言
        allowEdit: false, // 允许编辑
        showinfobar: false, // 名称栏
        sheetFormulaBar: false,
        showsheetbar: false, // 底部sheet页
        showstatisticBar: false, // 底部计数栏
        enableAddRow: false, // 允许添加行
        enableAddCol: false, // 允许添加列
        showtoolbar: false, // 是否第二列显示工具栏
        hook: {
          cellMousedown: (cell, postion, sheetFile, ctx) => {
            // console.log('cell click:', cell, postion)
            this.getRowColData(postion.r, postion.c);
          },
        },
      },
      selectZones: "",
      selectZoneNames: "",
      curFormId: null,
      settingList: [],
      curSetting: null,
      selectItem: null,
    };
  },
  mounted() {
    mini.parse();
    for (let i = 65; i < 91; i++) {
      this.colNames.push(String.fromCharCode(i));
    }
  },
  beforeDestroy() {
    // 离开销毁Luckysheet
    luckysheet.destroy();
  },
  methods: {
    showModal(row, readOnly=false) {
      this.readOnly = readOnly
      this.visible = true;
      this.$nextTick(() => {
        if (row) {
          this.loadForm(row.id);
          this.curFormId = row.id;
        } else {
          this.curFormId = null;
        }
      });
    },
    // 关闭
    handleCancel() {
      // this.$emit("close");
      this.visible = false;
      // Object.assign(this.queryParam, this.$options.data().queryParam);
    },
    selectTrainSys(data) {
      if (data.length) {
        this.queryParam.assetTypeId = data[0].id;
        this.assetTypeName = data[0].name;
      }
    },
    loadForm(formId) {
      luckysheet.destroy();
      getFormContent(formId)
        .then((res) => {
          if (res.success) {
            this.selectFormId = formId;
            const sheet = JSON.parse(res.result);
            // 设置工作表保护, 设置可以编辑的单元格
            /*if (!sheet.config.authority || sheet.config.authority.allowRangeList.length < 1) {
              sheet.config.authority = {
                sheet: 1,
                hintText: '禁止编辑',
                allowRangeList: []
              }
              sheet.celldata.map((col, cindex, cols) => {
                if ( col.v && (col.v.v || (col.v.ct && col.v.ct.s))) { // 列存在值，则设置为只读和灰色背景
                  col.v['ae'] = 0
                  col.v['bg'] = '#eae9e9'
                } else {
                  if (!col.v.mc) {
                    const colName = this.colNames[col.c]
                    // const allCol = '$' + colName + '$' + col.r+':$'+colName+'$'+ (col.r + 1)
                    const allCol = '$' + colName + '$' + (col.r + 1)
                    const name = 'area_' + colName + col.r
                    sheet.config.authority.allowRangeList.push({
                      name: name, //名称
                      hintText: "", //提示文字
                      algorithmName: "None",//加密方案：MD2,MD4,MD5,RIPEMD-128,RIPEMD-160,SHA-1,SHA-256,SHA-384,SHA-512,WHIRLPOOL
                      saltValue: null, //密码解密的盐参数，为一个自己定的随机数值
                      sqref: allCol //区域范围
                    })
                  }
                }
                // col.v["ht"] = 0; //水平居中
                col.v["vt"] = 0; //垂直居中
              });
            }*/

            const op = clone(this.defaultOptions);

            op.data = [sheet];

            luckysheet.create(op);
          } else {
            this.selectFormId = "";
          }
        })
        .catch((err) => {
          console.error("加载表单内容异常：", err);
          this.$message.error("加载表单内容异常");
          luckysheet.create(op);
        });
    },
    setValue() {
      const ranges = luckysheet.getRange();
      console.log(ranges)
      if (ranges) {
        const zones = [];
        const zoneNames = [];
        ranges.forEach((range) => {
          const rangeName =
            this.colNames[range.column[0]] +
            range.row[0] +
            ":" +
            this.colNames[range.column[1]] +
            range.row[1];
          zoneNames.push(rangeName);
          zones.push({ row: range.row, col: range.column });
        });
        // 如果有选择多个区域，默认设置第一个区域
        this.selectZones = zones[0];
        this.selectZoneNames = zoneNames[0];
        this.curSetting = null;
        this.editVisible = true;
      }
    },
    showDrawer() {
      // 加载当前已经设置的列表
      this.loadFormThresholds();
      this.drawerVisible = true;
    },
    onClose() {
      this.drawerVisible = false;
    },
    loadFormThresholds() {
      getFormThresholds(this.curFormId)
        .then((res) => {
          if (res.success) {
            this.settingList = res.result;
          } else {
            this.$message.error("加载已设置的数据错误");
            console.error("加载已设置的数据错误:", res.message);
          }
        })
        .catch((err) => {
          this.$message.error("加载已设置的数据错误");
          console.error("加载已设置的数据错误:", res.message);
        });
    },
    deleteSetting(row) {
      this.$confirm({
        zIndex: 99999,
        content: "你确定要删除该设置",
        onOk: () => {
          deleteThreshold(qs.stringify({ id: row.id }))
            .then((res) => {
              if (res.success) {
                this.$message.success("删除成功");
                this.loadFormThresholds();
              } else {
                this.$message.error("删除失败");
                console.error("删除失败：", res.message);
              }
            })
            .catch((err) => {
              this.$message.error("删除异常");
              console.error("删除异常：", err);
            });
        },
      });
    },
    editSetting(row) {
      this.curSetting = row;
      this.drawerVisible = false;
      this.editVisible = true;
    },
    getRowColData(row, col) {
      this.selectItem = null;
      getRowColThreshold({
        customId: this.curFormId,
        row1: row,
        row2: row,
        col1: col,
        col2: col,
      }).then((res) => {
        if (res.success) {
          this.selectItem = res.result;
        } else {
          console.error("获取行%s,列%s，错误", row, col, res.message);
        }
      });
    },
  },
};
</script>

<style lang="less" scoped>
.listBox {
  height: calc(100vh - 120px);
}
</style>
