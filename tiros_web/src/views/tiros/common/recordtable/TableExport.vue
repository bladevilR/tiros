<template>
  <div ref="calcHeight"></div>
</template>

<script>
import * as Excel from "exceljs/dist/exceljs.min.js";
import * as FileSaver from "file-saver";
const EXCEL_TYPE =
  "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8";
import { getOldWorkRecord } from "@/api/tirosApi";

export default {
  name: "TableExport",
  data() {
    return {};
  },
  mounted() {},
  methods: {
    printById(id) {
      // this.recordSheet = table
      getOldWorkRecord({ id: id })
        .then((res) => {
          if (res.success) {
            console.log(res.result);
            this.initWorkBook(res.result);
          } else {
            this.$message.error("获取记录表详情失败");
            console.error("获取记录表详情失败", res.message);
          }
        })
        .catch((err) => {
          this.$message.error("获取记录表详情异常");
          console.error("获取记录表详情异常：", err);
        });
    },
    printByData(records) {
      console.log(records);
      this.initWorkBook(records);
    },
    initWorkBook(result) {
      console.log(result);
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
          // defaultRowHeight: 30,
          defaultColWidth: 22,
          alignment: { vertical: "middle", horizontal: "left", wrapText: true },
        },
        pageSetup: { paperSize: 9 },
      });

      const STYLE_SETING = [];

      // 设置公共头部

      // 车号
      sheet.mergeCells("A1:C1");
      sheet.getCell("A1").value = "车号：" + (result.trainNo || "");
      STYLE_SETING.push({
        cell: "A1",
        horizontal: "left",
        fill: true,
        size: 14,
        bold: true,
      });

      if (result.updown === 0) {
        // 部件号
        sheet.mergeCells("A2:C2");
        sheet.getCell("A2").value = "部件号：" + (result.manufNo || "");
        STYLE_SETING.push({
          cell: "A2",
          horizontal: "left",
          fill: true,
          size: 14,
          bold: true,
        });
      } else if (result.updown === 1) {
        // 原部件序列号
        sheet.mergeCells("A2:C2");
        sheet.getCell("A2").value = "原部件序列号：" + (result.manufNo || "");
        STYLE_SETING.push({
          cell: "A2",
          horizontal: "left",
          fill: true,
          size: 14,
          bold: true,
        });

        // 新部件序列号
        sheet.mergeCells("A3:C3");
        sheet.getCell("A3").value = "新部件序列号：" + (result.manufNoUp || "");
        STYLE_SETING.push({
          cell: "A3",
          horizontal: "left",
          fill: true,
          size: 14,
          bold: true,
        });
      } else {
        sheet.mergeCells("A1:C2");
      }

      // 设置标题
      if (result.updown === 1) {
        sheet.mergeCells("D1:F3");
      } else {
        sheet.mergeCells("D1:F2");
      }
      sheet.getCell("D1").value = result.title || result.workRecName || "";
      STYLE_SETING.push({
        cell: "D1",
        fill: true,
        size: 14,
        bold: true,
      });

      // 设置作业日期
      sheet.mergeCells("G1:H1");
      sheet.getCell("G1").value = "作业日期：" + (result.workDate || "");
      STYLE_SETING.push({
        cell: "G1",
        fill: true,
        horizontal: "left",
        size: 14,
        bold: true,
      });

      // 设置完工日期
      sheet.mergeCells("I1:J1");
      sheet.getCell("I1").value = "完工日期：" + (result.finishDate || "");
      STYLE_SETING.push({
        cell: "I1",
        fill: true,
        horizontal: "left",
        size: 14,
        bold: true,
      });

      // 设置作业班组
      if (result.updown === 1) {
        sheet.mergeCells("G2:J3");
      } else {
        sheet.mergeCells("G2:J2");
      }

      sheet.getCell("G2").value = "作业班组：" + (result.workGroupName || "");
      STYLE_SETING.push({
        cell: "G2",
        fill: true,
        horizontal: "left",
        size: 14,
        bold: true,
      });

      // 设置列宽
      sheet.getColumn("A").width = 15;
      sheet.getColumn("B").width = 30;
      sheet.getColumn("C").width = 60;
      sheet.getColumn("D").width = 15;
      sheet.getColumn("E").width = 60;
      sheet.getColumn("F").width = 60;

      // 设置行高
      sheet.getRow(1).height = 30;
      sheet.getRow(2).height = 30;
      sheet.getRow(3).height = 30;
      if (result.updown === 1) {
        sheet.getRow(4).height = 30;
      }

      // 设置内容列头
      const COL_HEADER = {
        A: "序号",
        B: "项目",
        C: "作业内容",
        D: "控制点",
        E: "技术要求",
        F: "作业情况",
        G: "结果",
        H: "自检",
        I: "互检",
        J: "专检",
      };
      for (let i in COL_HEADER) {
        // 遍历循环
        // console.log(i, COL_HEADER[i]);
        sheet.getCell(i + (result.updown === 1 ? 4 : 3)).value = COL_HEADER[i];
        STYLE_SETING.push({
          cell: i + (result.updown === 1 ? 4 : 3),
          fill: true,
          size: 14,
          bold: true,
        });
      }

      let ROW_INDEX = result.updown === 1 ? 5 : 4;
      let ROW_INDEX_1 = 0;

      // 设置内容
      result.categoryList.forEach((item, index) => {
        // 序号
        sheet.getCell(`A${ROW_INDEX + index}`).value = item.recIndex;
        STYLE_SETING.push({
          cell: `A${ROW_INDEX + index}`,
        });
        sheet.mergeCells(
          `A${ROW_INDEX + index}:A${ROW_INDEX + index + item.detailList.length - 1}`
        );
        // 标题
        sheet.getCell(`B${ROW_INDEX + index}`).value = item.reguTitle;
        STYLE_SETING.push({
          cell: `B${ROW_INDEX + index}`,
          horizontal: "left",
        });
        sheet.mergeCells(
          `B${ROW_INDEX + index}:B${ROW_INDEX + index + item.detailList.length - 1}`
        );
        // 设置明细内容
        item.detailList.forEach((item2, index2) => {
          //
          sheet.getCell(`C${ROW_INDEX + index + index2}`).value = `\n${
            item2.workContent || ""
          }\n`;
          STYLE_SETING.push({
            cell: `C${ROW_INDEX + index + index2}`,
            horizontal: "left",
          });
          //
          sheet.getCell(`D${ROW_INDEX + index + index2}`).value =
            item2.checkLevel_dictText || "";
          STYLE_SETING.push({
            cell: `D${ROW_INDEX + index + index2}`,
          });
          //
          sheet.getCell(`E${ROW_INDEX + index + index2}`).value = `\n${
            item2.techRequire || ""
          }\n`;
          STYLE_SETING.push({
            cell: `E${ROW_INDEX + index + index2}`,
            horizontal: "left",
          });
          //
          sheet.getCell(`F${ROW_INDEX + index + index2}`).value =
            item2.isIgnore == 0 ? item2.workInfo : item2.ignoreDesc;
          STYLE_SETING.push({
            cell: `F${ROW_INDEX + index + index2}`,
            horizontal: "left",
          });
          //
          sheet.getCell(`G${ROW_INDEX + index + index2}`).value = item2.result
            ? "√"
            : "□";
          STYLE_SETING.push({
            cell: `G${ROW_INDEX + index + index2}`,
          });
          //
          sheet.getCell(`H${ROW_INDEX + index + index2}`).value = item2.selfCheck || "";
          STYLE_SETING.push({
            cell: `H${ROW_INDEX + index + index2}`,
          });
          //
          sheet.getCell(`I${ROW_INDEX + index + index2}`).value =
            item2.guarderCheck || "";
          STYLE_SETING.push({
            cell: `I${ROW_INDEX + index + index2}`,
          });
          //
          sheet.getCell(`J${ROW_INDEX + index + index2}`).value =
            item2.monitorCheck || "";
          STYLE_SETING.push({
            cell: `J${ROW_INDEX + index + index2}`,
          });
        });

        ROW_INDEX += item.detailList.length - 1;
        ROW_INDEX_1 += item.detailList.length;
      });

      ROW_INDEX_1 += result.updown === 1 ? 5 : 4;
      // 空一行
      sheet.mergeCells(`A${ROW_INDEX_1}:J${ROW_INDEX_1}`);
      sheet.getRow(ROW_INDEX_1).height = 30;
      STYLE_SETING.push({
        cell: `A${ROW_INDEX_1}`,
        bold: true,
      });

      ROW_INDEX_1 += 1;
      // 工器具行
      const measureToolList = result.measureToolList || [];

      if (measureToolList.length) {
        // 标题
        sheet.mergeCells(`A${ROW_INDEX_1}:B${ROW_INDEX_1 + measureToolList.length - 1}`);
        sheet.getCell(`A${ROW_INDEX_1}`).value = "计量器具使用";
        STYLE_SETING.push({
          cell: `A${ROW_INDEX_1}`,
        });

        measureToolList.forEach((item, index, arr) => {
          //
          sheet.mergeCells(`C${ROW_INDEX_1 + index}:D${ROW_INDEX_1 + index}`);
          sheet.getCell(`C${ROW_INDEX_1 + index}`).value =
            " \n名称：" + (item.name || "") + "\n";
          STYLE_SETING.push({
            cell: `C${ROW_INDEX_1 + index}`,
            horizontal: "left",
          });
          //
          // sheet.mergeCells(`E${ROW_INDEX_1 + index}:D${ROW_INDEX_1 + index}`);
          sheet.getCell(`E${ROW_INDEX_1 + index}`).value =
            " \n规格型号：" + (item.model || "") + "\n";
          STYLE_SETING.push({
            cell: `E${ROW_INDEX_1 + index}`,
            horizontal: "left",
          });
          //
          // sheet.mergeCells(`E${ROW_INDEX_1 + index}:D${ROW_INDEX_1 + index}`);
          sheet.getCell(`F${ROW_INDEX_1 + index}`).value =
            " \n出厂编号：" + (item.serialNo || "") + "\n";
          STYLE_SETING.push({
            cell: `F${ROW_INDEX_1 + index}`,
            horizontal: "left",
          });
          //
          sheet.mergeCells(`G${ROW_INDEX_1 + index}:H${ROW_INDEX_1 + index}`);
          sheet.getCell(`G${ROW_INDEX_1 + index}`).value =
            " \n上次检验时间：" + (item.lastCheckTime || "") + "\n";
          STYLE_SETING.push({
            cell: `G${ROW_INDEX_1 + index}`,
            horizontal: "left",
          });
          //
          sheet.mergeCells(`I${ROW_INDEX_1 + index}:J${ROW_INDEX_1 + index}`);
          sheet.getCell(`I${ROW_INDEX_1 + index}`).value =
            " \n下次检验时间：" + (item.nextCheckTime || "") + "\n";
          STYLE_SETING.push({
            cell: `I${ROW_INDEX_1 + index}`,
            horizontal: "left",
          });
        });
        ROW_INDEX_1 += measureToolList.length;
        // 空一行
        sheet.mergeCells(`A${ROW_INDEX_1}:J${ROW_INDEX_1}`);
        sheet.getRow(ROW_INDEX_1).height = 30;
        STYLE_SETING.push({
          cell: `A${ROW_INDEX_1}`,
          bold: true,
        });
        ROW_INDEX_1 += 1;
      }

      // 设置说明
      const explainList = [
        "控制点分为H点和W点，“H”点为该项工序操作过程必须经过专业工程师现场检查确认的工序，“W”点为班组长可在工序完工后确认的工序。",
        "“自检”栏为作业者本人进行签字；“互检”栏由作业监护人员进行检查签字；“专检”栏由班组长进行检查签字。“质量负责人”由相关专业工程师进行签字。",
        "“作业情况”需根据实际情况填写。若该项工序无异常则由作业者在框中填入√；若该项工序存在异常，则将实际故障情况填入框内。",
        "“结果”栏方框在完成该项作业及检查后，由工班长进行填写。若无异常则在方框内填入√，若有异常，工班长需对故障情况进行确认，在方框内填入×，并将故障在“备注”栏进行汇总。",
        "作业内容中有数据要求的一并填入“作业情况”栏。",
      ];
      // 合并单元格
      sheet.mergeCells(`A${ROW_INDEX_1}:J${ROW_INDEX_1}`);
      let explainText = "";
      explainList.forEach((item, index, arr) => {
        explainText += `${index + 1}、${item}${index < arr.length - 1 ? "\n" : ""}`;
      });

      explainText = "说明：\n" + explainText + "\n";

      explainText =
        `\n检查结果：${
          result.checkResult === 0 ? "未通过" : "通过"
        }                                        检查日期：${
          result.checkDate || "            "
        }                                        质量负责人：${
          result.checkUserName || "            "
        }\n\n` + explainText;

      sheet.getCell(`A${ROW_INDEX_1}`).value = explainText;
      STYLE_SETING.push({
        cell: `A${ROW_INDEX_1}`,
        horizontal: "left",
        border: top,
      });
      // 自动计算行高  （字体大小 * 多少工艺文件[如果没有则默认为1 计算总合等于默认高度] + 留空数值）
      const explainHeight = 12 * (explainList.length + 4) + 18;
      sheet.getRow(ROW_INDEX_1).height = explainHeight;

      // 设置样式
      STYLE_SETING.forEach((item, index, arr) => {
        const CELL = sheet.getCell(item.cell);
        CELL.alignment = {
          vertical: "middle",
          horizontal: item.horizontal || "center",
          wrapText: true,
        };
        if (item.fill) {
          CELL.fill = {
            type: "pattern",
            pattern: "darkGray",
            fgColor: { argb: "FFe9e9e9" },
          };
        }
        CELL.font = {
          name: "黑体",
          family: 4,
          size: item.size || 12,
          underline: false,
          bold: item.bold || false,
        };
        CELL.padding = {
          top: 10,
          left: 10,
          bottom: 10,
          right: 10,
        };
        CELL.border = {
          top: { style: "thin" },
          left: { style: "thin" },
          bottom: { style: "thin" },
          right: { style: "thin" },
        };
      });

      // console.log(sheet);

      // return;
      // Generate Excel File
      workbook.xlsx.writeBuffer().then((data) => {
        console.log(data);
        const blob = new Blob([data], { type: EXCEL_TYPE });
        // Given name
        FileSaver.saveAs(blob, `${result.title || result.workRecName}.xlsx`);
      });
    },
  },
};
</script>

<style scoped lang="less"></style>
