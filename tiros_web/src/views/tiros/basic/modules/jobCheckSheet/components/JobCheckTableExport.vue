<template>
  <div></div>
</template>

<script>
import * as Excel from "exceljs/dist/exceljs.min.js";
import * as FileSaver from "file-saver";
const EXCEL_TYPE =
  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
import { getWorkcheck } from "@api/tirosQualityApi";

export default {
  name: "JobCheckTableExport",
  data() {
    return {};
  },
  props: {},
  created() {
    // this.sheetData = Object.assign({}, this.data)
    // this.sheetData.itemList = this.sortArray(this.sheetData.itemList)
  },
  mounted() {
    // console.log(this.fromData)
  },
  methods: {
    sortArray(array) {
      array.sort(this.compare("sortNo"));
      return array;
    },
    compare(property) {
      return function (a, b) {
        return a[property] - b[property];
      };
    },
    printByRow(row) {
      console.log(row);
      row.itemList = this.sortArray(row.itemList);
      this.initWorkBook(row);
    },
    printById(id) {
      getWorkcheck({
        id: id,
      }).then((res) => {
        if (res.success) {
          console.log(res.result);
          res.result.itemList = this.sortArray(res.result.itemList);
          this.initWorkBook(res.result);
        }
      });
    },
    initWorkBook(result) {
      // Create workbook and worksheet
      let workbook = new Excel.Workbook();

      // Set Workbook Properties
      workbook.creator = "GY";
      workbook.lastModifiedBy = "2021-10-28";
      workbook.created = new Date();
      workbook.modified = new Date();
      workbook.lastPrinted = new Date();

      // Add a Worksheet
      const sheet = workbook.addWorksheet("Sheet1", {
        properties: {
          defaultRowHeight: 30,
          defaultColWidth: 22,
        },
        pageSetup: { paperSize: 9 },
      });

      // 先设置公共头
      // 设置标题
      sheet.addRows([["车辆连挂作业过程检查项点"]]);
      // 设置车号
      sheet.getCell("A2").value = "车号：" + (result.trainNo || "");
      // 设置部件名称
      sheet.getCell("D2").value = "部件名称：" + (result.assetName || "");
      // 设置车号
      sheet.getCell("G2").value = "作业周期：" + (result.period || "");
      // 设置部件编号
      sheet.getCell("A3").value = "部件编号：" + (result.assetNo || "");
      // 设置作业位置
      sheet.getCell("D3").value = "作业位置：" + (result.location || "");
      // 设置作业班组
      sheet.getCell("G3").value = "作业班组：" + (result.groupName || "");

      // 设置A、C、B列宽
      sheet.getColumn("A").width = 15;
      sheet.getColumn("B").width = 30;
      sheet.getColumn("C").width = 60;

      // 第一行合并单元格
      sheet.mergeCells("A1:J1");
      // 第二行
      sheet.mergeCells("A2:C2");
      sheet.mergeCells("D2:F2");
      sheet.mergeCells("G2:J2");
      // 第三行
      sheet.mergeCells("A3:C3");
      sheet.mergeCells("D3:F3");
      sheet.mergeCells("G3:J3");

      // 设置列头
      sheet.addRows([
        [
          "序号",
          "项点",
          "检查内容",
          "等级",
          "检查情况",
          "结果",
          "作业时间",
          "检查方式",
          "检查人",
          "检查时间",
        ],
      ]);
      let _arr = [];
      // 设置检查项目内容
      (result.itemList || []).forEach((item, index, arr) => {
        // 设置等级
        let lever = "";
        for (let i = 0, len = 5; i < len; i++) {
          if (item.checkLevel > i) {
            lever += "★";
          } else {
            lever += "☆";
          }
        }
        _arr.push([
          item.sortNo || "",
          item.title || "",
          item.content || "",
          lever,
          item.checkDesc || "",
          item.checkUserName ? item.checkResult_dictText : "",
          item.workTime || "",
          item.checkMethod || "",
          item.checkUserName || "",
          item.checkTime || "",
        ]);
      });
      sheet.addRows(_arr);

      // 空一行
      const _ROWINDEX = 4 + (result.itemList || []).length + 1; // 不可修改逻辑
      sheet.mergeCells(`A${_ROWINDEX}:J${_ROWINDEX}`);

      // 设置参考工艺文件
      sheet.getCell(`A${_ROWINDEX + 1}`).value = "参考工艺文件";
      // 合并单元格
      sheet.mergeCells(`A${_ROWINDEX + 1}:B${_ROWINDEX + 1}`);
      sheet.mergeCells(`C${_ROWINDEX + 1}:J${_ROWINDEX + 1}`);
      // 设置参考工艺文件值
      let craftFileText = "";
      (result.techLinkList || []).forEach((item, index, arr) => {
        craftFileText += `${index + 1}、${item.teckBookName}${
          index < arr.length - 1 ? "\n" : ""
        }`;
      });
      sheet.getCell(`C${_ROWINDEX + 1}`).value = craftFileText;
      // 自动计算行高  （字体大小 * 多少工艺文件[如果没有则默认为1 计算总合等于默认高度] + 留空数值）
      const craftFileHeight = 12 * ((result.techLinkList || []).length || 1) + 18;

      // 空一行
      const _ROWINDEX_2 = _ROWINDEX + 2; // 不可修改逻辑
      sheet.mergeCells(`A${_ROWINDEX_2}:J${_ROWINDEX_2}`);

      // 设置不合格项 列头
      sheet.mergeCells(`A${_ROWINDEX_2 + 1}:G${_ROWINDEX_2 + 1}`);
      sheet.getCell(`A${_ROWINDEX_2 + 1}`).value = "不合格项汇总";
      sheet.getCell(`H${_ROWINDEX_2 + 1}`).value = "整改情况";
      sheet.getCell(`I${_ROWINDEX_2 + 1}`).value = "复查时间";
      sheet.getCell(`J${_ROWINDEX_2 + 1}`).value = "复查人";
      // 设置不合格项 内容
      (result.rectifyList || []).forEach((item, index, arr) => {
        sheet.mergeCells(`A${_ROWINDEX_2 + index + 2}:G${_ROWINDEX_2 + index + 2}`);
        sheet.getCell(`A${_ROWINDEX_2 + index + 2}`).value = item.itemInfo;
        sheet.getCell(`H${_ROWINDEX_2 + index + 2}`).value = item.rectifyDesc;
        sheet.getCell(`I${_ROWINDEX_2 + index + 2}`).value = item.reviewTime;
        sheet.getCell(`J${_ROWINDEX_2 + index + 2}`).value = item.reviewUserName;
      });

      // 空一行
      const _ROWINDEX_3 = _ROWINDEX_2 + (result.rectifyList || []).length + 2; // 不可修改逻辑
      sheet.mergeCells(`A${_ROWINDEX_3}:J${_ROWINDEX_3}`);

      // 设置评定
      sheet.getCell(`A${_ROWINDEX_3 + 1}`).value = "评定";
      // 合并单元格
      sheet.mergeCells(`A${_ROWINDEX_3 + 1}:B${_ROWINDEX_3 + 1}`);
      sheet.mergeCells(`C${_ROWINDEX_3 + 1}:J${_ROWINDEX_3 + 1}`);
      // 设置评定值
      let evaluateText = "";
      (result.judgeList || []).forEach((item, index, arr) => {
        evaluateText += `评定人：${item.jdUserName}；评定时间：${
          item.jdTime
        }；评定内容：${item.jdContent}；${index < arr.length - 1 ? "\n" : ""}`;
      });
      sheet.getCell(`C${_ROWINDEX_3 + 1}`).value = evaluateText;
      // 自动计算行高  （字体大小 * 多少评定[如果没有则默认为1 计算总合等于默认高度] + 留空数值）
      const evaluateHeight = 12 * ((result.judgeList || []).length || 1) + 18;

      // 空一行
      const _ROWINDEX_4 = _ROWINDEX_3 + 2; // 不可修改逻辑
      sheet.mergeCells(`A${_ROWINDEX_4}:J${_ROWINDEX_4}`);

      // 设置说明
      const explainList = [
        "表中只列出关键项点，检查人员在检查时参照工艺文件标准执行。",
        "检查人员必须对当天作业内容进行检查。",
        "表中所有内容全部现场检查时填写完成，并悬挂于部件上，随部件流转。",
        "作业时间由作业班组填写，其余由检查人员填写。“检查情况”一栏详细记录检查情况；“结果”一栏对检查合格的画“√”不合格的画“×”；“检查方式”一栏写明检查手段，如：目视、测量、操作等。",
        "检查人员对不合格项视情节严重情况开具整改单，及时提出整改意见，并督促整改，整改完成后及时进行复查。",
        "由检查人员、质量工程师及专业工程师对整体作业质量做出最终判定。",
      ];
      // 设置说明
      sheet.getCell(`A${_ROWINDEX_4 + 1}`).value = "说明";
      // 合并单元格
      sheet.mergeCells(`A${_ROWINDEX_4 + 1}:B${_ROWINDEX_4 + 1}`);
      sheet.mergeCells(`C${_ROWINDEX_4 + 1}:J${_ROWINDEX_4 + 1}`);
      let explainText = "";
      explainList.forEach((item, index, arr) => {
        explainText += `${index + 1}、${item}${index < arr.length - 1 ? "\n" : ""}`;
      });
      sheet.getCell(`C${_ROWINDEX_4 + 1}`).value = explainText;
      // 自动计算行高  （字体大小 * 多少工艺文件[如果没有则默认为1 计算总合等于默认高度] + 留空数值）
      const explainHeight = 12 * (explainList.length || 1) + 18;

      console.log(sheet);

      // 设置边框对其方式 以及填充等其他样式
      sheet._rows.forEach((item, rowIndex) => {
        console.log(rowIndex);
        // 设置行高
        if (_ROWINDEX == rowIndex) {
          // 工艺文件高度设置
          sheet.getRow(rowIndex + 1).height = craftFileHeight;
        } else if (_ROWINDEX_3 == rowIndex) {
          // 质量评定高度设置
          sheet.getRow(rowIndex + 1).height = evaluateHeight;
        } else if (_ROWINDEX_4 == rowIndex) {
          // 说明高度设置
          sheet.getRow(rowIndex + 1).height = explainHeight;
        } else {
          // 其他高度
          sheet.getRow(rowIndex + 1).height = 30;
        }
        item._cells.forEach((item2, colIndex) => {
          const cell = sheet.getCell(item2._address);
          // 设置边框
          cell.border = {
            top: { style: "thin" },
            left: { style: "thin" },
            bottom: { style: "thin" },
            right: { style: "thin" },
          };

          // 设置对其方式
          if (
            // 顶部四行
            [1, 2].indexOf(rowIndex) > -1 ||
            // 检查内容 B、C 、E列
            (rowIndex - 3 >= 0 &&
              rowIndex - 3 <= result.itemList.length &&
              [1, 2, 4].indexOf(colIndex) > -1) ||
            // 工艺文件对其方式
            (_ROWINDEX == rowIndex && colIndex >= 2) ||
            // 不合格项目
            (_ROWINDEX_3 > rowIndex && _ROWINDEX_2 < rowIndex && colIndex < 7) ||
            // 质量评定对其方式
            (_ROWINDEX_3 == rowIndex && colIndex >= 2) ||
            // 说明对其方式
            (_ROWINDEX_4 == rowIndex && colIndex >= 2)
          ) {
            // 左对齐
            cell.alignment = { vertical: "middle", horizontal: "left", wrapText: true };
          } else {
            // 剧中对其
            cell.alignment = { vertical: "middle", horizontal: "center", wrapText: true };
          }
          // 设置填充和字体样式
          if ([0, 1, 2, 3, _ROWINDEX_2 /*不合格整改项目标题*/].indexOf(rowIndex) > -1) {
            // 填充
            cell.fill = {
              type: "pattern",
              pattern: "darkGray",
              fgColor: { argb: "FFe9e9e9" },
            };
            // 字体
            cell.font = {
              name: "黑体",
              family: 4,
              size: 14,
              underline: false,
              bold: true,
            };
          } else if (rowIndex < _ROWINDEX && colIndex == 3) {
            // 字体
            cell.font = {
              name: "黑体",
              family: 4,
              size: 18,
              underline: false,
              bold: false,
            };
          } else {
            cell.font = {
              name: "黑体",
              family: 4,
              size: 12,
              underline: false,
              bold: false,
            };
          }
        });
      });

      // return
      // Generate Excel File
      workbook.xlsx.writeBuffer().then((data) => {
        const blob = new Blob([data], { type: EXCEL_TYPE });
        // Given name
        FileSaver.saveAs(blob, `${result.title}.xlsx`);
      });
    },
  },
};
</script>

<style scoped lang="less"></style>
