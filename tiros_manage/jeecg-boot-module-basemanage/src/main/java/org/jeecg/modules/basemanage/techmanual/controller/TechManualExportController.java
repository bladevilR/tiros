package org.jeecg.modules.basemanage.techmanual.controller;

import org.jeecg.modules.basemanage.techmanual.model.Result;
import org.jeecg.modules.basemanage.techmanual.service.TechManualExportService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.net.URLEncoder;
import java.util.Base64;

/**
 * 工艺手册导出API控制器
 *
 * @author TIROS Plugin
 * @version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/techmanual")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TechManualExportController {

    @Autowired
    private TechManualExportService exportService;

    /**
     * 将TinyMCE的HTML内容导出为Word文档
     */
    @PostMapping("/export-word")
    public ResponseEntity<byte[]> exportToWord(
            @Validated @RequestBody ExportRequest request) {
        try {
            log.info("收到工艺手册导出请求，手册名称: {}", request.getManualName());

            // 生成Word文档
            byte[] documentBytes = exportService.htmlToWord(
                    request.getHtmlContent(),
                    request.getManualName(),
                    request.getFileNo(),
                    request.getFileVer(),
                    request.getProjectNo()
            );

            // 设置文件名
            String fileName = request.getManualName() + ".docx";
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8")
                    .replaceAll("\\+", "%20");

            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.setContentLength(documentBytes.length);

            log.info("工艺手册导出成功，文件名: {}, 大小: {} bytes",
                    fileName, documentBytes.length);

            return new ResponseEntity<>(documentBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("工艺手册导出失败", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 将HTML内容导出为Word（返回Base64）
     */
    @PostMapping("/export-word/base64")
    public Result<String> exportToWordBase64(
            @Validated @RequestBody ExportRequest request) {
        try {
            log.info("收到工艺手册导出请求(Base64)，手册名称: {}", request.getManualName());

            // 生成Word文档
            byte[] documentBytes = exportService.htmlToWord(
                    request.getHtmlContent(),
                    request.getManualName(),
                    request.getFileNo(),
                    request.getFileVer(),
                    request.getProjectNo()
            );

            // 转为Base64
            String base64 = Base64.getEncoder().encodeToString(documentBytes);

            log.info("工艺手册导出成功(Base64)，大小: {} bytes", documentBytes.length);

            return Result.ok("导出成功", base64);

        } catch (Exception e) {
            log.error("工艺手册导出失败", e);
            return Result.error("导出失败: " + e.getMessage());
        }
    }

    /**
     * 导出请求模型
     */
    @Data
    public static class ExportRequest {
        /**
         * HTML内容（TinyMCE编辑器的内容）
         */
        @NotBlank(message = "HTML内容不能为空")
        private String htmlContent;

        /**
         * 手册名称
         */
        @NotBlank(message = "手册名称不能为空")
        private String manualName;

        /**
         * 文件编号
         */
        private String fileNo;

        /**
         * 文件版本
         */
        private String fileVer;

        /**
         * 项目号
         */
        private String projectNo;
    }
}
