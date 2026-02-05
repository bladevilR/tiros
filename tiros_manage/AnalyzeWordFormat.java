import org.apache.poi.xwpf.usermodel.*;
import java.io.FileInputStream;
import java.util.List;

public class AnalyzeWordFormat {
    public static void main(String[] args) throws Exception {
        String filePath = "D:\\tiros\\jdx\\苏州1号线电客车车体转向架连接大修作业指导书-2.docx";

        FileInputStream fis = new FileInputStream(filePath);
        XWPFDocument document = new XWPFDocument(fis);

        System.out.println("=== 文档分析 ===");
        System.out.println("总段落数: " + document.getParagraphs().size());
        System.out.println("总表格数: " + document.getTables().size());
        System.out.println();

        // 分析前10个段落
        System.out.println("=== 前10个段落分析 ===");
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (int i = 0; i < Math.min(10, paragraphs.size()); i++) {
            XWPFParagraph para = paragraphs.get(i);
            System.out.println("段落 " + i + ":");
            System.out.println("  文本: " + para.getText());

            List<XWPFRun> runs = para.getRuns();
            if (runs != null && runs.size() > 0) {
                XWPFRun run = runs.get(0);
                System.out.println("  字体: " + run.getFontFamily());
                System.out.println("  字号: " + run.getFontSize());
                System.out.println("  加粗: " + run.isBold());
            }
            System.out.println();
        }

        // 分析前3个表格
        System.out.println("=== 前3个表格分析 ===");
        List<XWPFTable> tables = document.getTables();
        for (int i = 0; i < Math.min(3, tables.size()); i++) {
            XWPFTable table = tables.get(i);
            System.out.println("表格 " + i + ":");
            System.out.println("  行数: " + table.getNumberOfRows());
            System.out.println("  宽度: " + table.getWidth());

            if (table.getNumberOfRows() > 0) {
                XWPFTableRow row = table.getRow(0);
                System.out.println("  第一行列数: " + row.getTableCells().size());

                if (row.getTableCells().size() > 0) {
                    XWPFTableCell cell = row.getCell(0);
                    System.out.println("  第一个单元格文本: " + cell.getText());
                    System.out.println("  第一个单元格宽度: " + cell.getWidth());

                    List<XWPFParagraph> cellParas = cell.getParagraphs();
                    if (cellParas.size() > 0 && cellParas.get(0).getRuns().size() > 0) {
                        XWPFRun run = cellParas.get(0).getRuns().get(0);
                        System.out.println("  单元格字体: " + run.getFontFamily());
                        System.out.println("  单元格字号: " + run.getFontSize());
                    }
                }
            }
            System.out.println();
        }

        fis.close();
    }
}
