<template>
  <div>
    <a-modal
      title="阈值设置"
      width="100%"
      :visible="visible"
      dialogClass="fullDialog"
      destroyOnClose
      @cancel="handleCancel"
    >
      <a-row type="flex" justify="space-between" style="margin-bottom: 20px">
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
        </a-col>
        <a-col :span="6" style="text-align: right">
          <a-space>
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
      <div class="listBox">
        <div
          id="luckysheet"
          style="margin: 0px; padding: 0px; width: 60%; height: 100%"
        ></div>
        <div class="form_bnox">
          <a-form-model ref="form" :model="form" :rules="rules">
            <a-row>
              <a-col :span="24">
                <a-form-model-item label="测量项名" prop="itemName">
                  <a-input
                    v-model="form.itemName"
                    placeholder="请输入测量项名称"
                  ></a-input>
                </a-form-model-item>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span="5">
                <a-form-model-item label="比较值" prop="linkDomain">
                  {{ form.linkDomain }}
                </a-form-model-item>
              </a-col>
              <a-col :span="5">
                <a-form-model-item label="比较符" prop="operator">
                  <j-dict-select-tag
                    v-model="form.operator"
                    placeholder="请选择"
                    dictCode="bu_operator"
                  />
                </a-form-model-item>
              </a-col>
              <a-col :span="13" :offset="1">
                <a-form-model-item label="阈值" prop="threshold">
                  <a-input-number
                    v-model="form.threshold"
                    placeholder="请输入阈值"
                    style="width: 100%"
                  ></a-input-number>
                </a-form-model-item>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span="5">
                <a-form-model-item> - </a-form-model-item>
              </a-col>
              <a-col :span="5">
                <a-form-model-item label="比较符2" prop="operator2">
                  <j-dict-select-tag
                    v-model="form.operator2"
                    placeholder="请选择"
                    dictCode="bu_operator"
                  />
                </a-form-model-item>
              </a-col>
              <a-col :span="13" :offset="1">
                <a-form-model-item label="阈值2" prop="threshold2">
                  <a-input-number
                    v-model="form.threshold2"
                    placeholder="请输入阈值"
                    style="width: 100%"
                  ></a-input-number>
                </a-form-model-item>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span="24">
                <a-form-model-item label="提示消息模版">
                  <a-textarea
                    v-model="form.template"
                    placeholder="请输入提示模版,支持${value}"
                    :auto-size="{ minRows: 2, maxRows: 3 }"
                  />
                </a-form-model-item>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span="24">
                <a-form-model-item label="指定设备">
                  <a-select
                    ref="assetType"
                    v-model="form.assetTypeName"
                    placeholder="请选择设备"
                    :open="false"
                    @focus="openAssetTypeModal"
                  >
                    <a-icon
                      slot="suffixIcon"
                      type="ellipsis"
                      @click="openAssetTypeModal"
                    />
                  </a-select>
                </a-form-model-item>
              </a-col>
            </a-row>
          </a-form-model>
        </div>
      </div>
      <template slot="footer">
        <a-button @click="handleCancel"> 关闭 </a-button>
      </template>
    </a-modal>
  </div>
</template>

<script>
import { getRowColThreshold } from "@api/tirosQualityApi";
import { getFormContent } from "@api/tirosApi";
import clone from "clone";
export default {
  data() {
    return {
      selectFormId: "",
      visible: false,
      colNames: [],
      curFormId: null,
      selectZones: "",
      selectZoneNames: "",
      editVisible: false,
      selectItem: null,
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
      form: {
        customId: "",
        itemName: "",
        operator: "",
        threshold: "",
        operator2: "",
        threshold2: "",
        template: "${trainNo} > ${assetTypeName} > ${name} ${operator} ${value}",
        linkDomain: "",
        assetTypeId: "",
        assetTypeName: "",
        row1: 0,
        row2: 0,
        col1: 0,
        col2: 0,
      },
      rules: {
        itemName: [{ required: true, message: "请输入测量项名称", trigger: "blur" }],
        operator: [{ required: true, message: "请选择比较符", trigger: "blur" }],
        threshold: [{ required: true, message: "请输入阈值", trigger: "blur" }],
        linkDomain: [{ required: true, message: "请选择范围", trigger: "blur" }],
      },
    };
  },
  mounted() {
    for (let i = 65; i < 91; i++) {
      this.colNames.push(String.fromCharCode(i));
    }
  },
  methods: {
    showModal(row) {
      this.visible = true;
      this.$nextTick(() => {
        this.curFormId = row.id;
        this.loadForm(row.id).then(() => {
          const ranges = luckysheet.getRange();
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
            this.editVisible = true;
            this.form.linkDomain = this.zoneNames;
            this.form.row1 = this.zones.row[0];
            this.form.row2 = this.zones.row[1];
            this.form.col1 = this.zones.col[0];
            this.form.col2 = this.zones.col[1];
            this.form.customId = this.formId;
          }
        });
      });
    },
    loadForm(formId) {
      return new Promise((resolve, reject) => {
        luckysheet.destroy();
        getFormContent(formId)
          .then((res) => {
            if (res.success) {
              this.selectFormId = formId;
              const sheet = JSON.parse(res.result);
              let op = clone(this.defaultOptions);
              op.data = [sheet];
              luckysheet.create(op);
              resolve();
            } else {
              this.selectFormId = "";
              this.$message.warning(res.message);
              reject();
            }
          })
          .catch((err) => {
            console.error("加载表单内容异常：", err);
            this.$message.error("加载表单内容异常");
            reject();
          });
      });
    },
    // 关闭
    handleCancel() {
      // this.$emit("close");
      this.visible = false;
      Object.assign(this.$data,this.$options.data());
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
    // 弹出设备选择界面
    openAssetTypeModal() {
      this.$refs.assetTypeModal.showModal();
      // this.$refs.assetType.focus()
      this.$refs.assetType.blur();
    },
    // 设备选择回调
    onAssetTypeSelect(data) {
      if (data.length > 0) {
        const item = data[0];
        if (item) {
          this.form.assetTypeName = item.name;
          this.form.assetTypeId = item.id;
        } else {
          this.form.assetTypeName = "";
          this.form.assetTypeId = "";
        }
        this.$forceUpdate();
        this.$refs.form.validateField("assetType");
      } else {
        this.form.assetTypeId = "";
      }
    },
    showDrawer() {
      // 加载当前已经设置的列表
      this.loadFormThresholds()
      this.drawerVisible = true
    },
    loadFormThresholds() {
      getFormThresholds(this.curFormId)
        .then((res) => {
          if (res.success) {
            this.settingList = res.result
          } else {
            this.$message.error('加载已设置的数据错误')
            console.error('加载已设置的数据错误:', res.message)
          }
        })
        .catch((err) => {
          this.$message.error('加载已设置的数据错误')
          console.error('加载已设置的数据错误:', res.message)
        })
    },
  },
};
</script>

<style lang="less" scoped>
.listBox {
  height: calc(100vh - 120px);
  display: flex;
  flex-flow: row nowrap;
  justify-content: space-between;
  .form_bnox {
    width: 40%;
    height: 100%;
    overflow: auto;
  }
}
</style>
