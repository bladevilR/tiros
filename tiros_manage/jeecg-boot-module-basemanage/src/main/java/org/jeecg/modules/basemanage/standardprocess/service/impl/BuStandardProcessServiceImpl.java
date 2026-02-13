package org.jeecg.modules.basemanage.standardprocess.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.basemanage.standardprocess.entity.BuStandardProcess;
import org.jeecg.modules.basemanage.standardprocess.mapper.BuStandardProcessMapper;
import org.jeecg.modules.basemanage.standardprocess.service.IBuStandardProcessService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BuStandardProcessServiceImpl extends ServiceImpl<BuStandardProcessMapper, BuStandardProcess> implements IBuStandardProcessService {

    @Override
    public IPage<BuStandardProcess> queryPage(BuStandardProcess query, Integer pageNo, Integer pageSize) {
        QueryWrapper<BuStandardProcess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("del_flag", 0);
        if (query != null) {
            if (StringUtils.isNotBlank(query.getProcessNo())) {
                queryWrapper.like("process_no", query.getProcessNo());
            }
            if (StringUtils.isNotBlank(query.getProcessName())) {
                queryWrapper.like("process_name", query.getProcessName());
            }
            if (StringUtils.isNotBlank(query.getProcessType())) {
                queryWrapper.eq("process_type", query.getProcessType());
            }
            if (StringUtils.isNotBlank(query.getTrainType())) {
                queryWrapper.like("train_type", query.getTrainType());
            }
            if (query.getStandardDuration() != null) {
                queryWrapper.eq("standard_duration", query.getStandardDuration());
            }
        }
        queryWrapper.orderByDesc("create_time");
        Page<BuStandardProcess> page = new Page<>(pageNo, pageSize);
        return this.page(page, queryWrapper);
    }

    @Override
    public boolean saveRecord(BuStandardProcess record) {
        if (record == null) {
            throw new JeecgBootException("参数不能为空");
        }
        record.setCreateTime(new Date());
        if (record.getDelFlag() == null) {
            record.setDelFlag(0);
        }
        if (record.getStandardDuration() != null && record.getStandardDuration() < 0) {
            throw new JeecgBootException("标准工时不能小于0");
        }
        return this.save(record);
    }

    @Override
    public boolean updateRecord(BuStandardProcess record) {
        if (record == null || StringUtils.isBlank(record.getId())) {
            throw new JeecgBootException("标准工序ID不能为空");
        }
        if (record.getStandardDuration() != null && record.getStandardDuration() < 0) {
            throw new JeecgBootException("标准工时不能小于0");
        }
        record.setUpdateTime(new Date());
        return this.updateById(record);
    }

    @Override
    public boolean deleteRecord(String ids) {
        List<String> idArray = parseIdList(ids);
        if (idArray.isEmpty()) {
            throw new JeecgBootException("标准工序ID不能为空");
        }
        Date now = new Date();
        UpdateWrapper<BuStandardProcess> wrapper = new UpdateWrapper<>();
        wrapper.in("id", idArray)
                .eq("del_flag", 0)
                .set("del_flag", 1)
                .set("update_time", now);
        this.update(wrapper);
        return true;
    }

    private List<String> parseIdList(String ids) {
        if (StringUtils.isBlank(ids)) {
            return Collections.emptyList();
        }
        return Arrays.stream(ids.split(","))
                .map(StringUtils::trimToNull)
                .filter(StringUtils::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public byte[] exportPdf(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new JeecgBootException("导出ID不能为空");
        }

        List<BuStandardProcess> records = new ArrayList<>(this.listByIds(ids));
        if (records.isEmpty()) {
            throw new JeecgBootException("未找到要导出的记录");
        }

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: SimSun, serif; font-size: 12pt; margin: 20px; }");
        html.append("h1 { text-align: center; font-size: 18pt; margin-bottom: 20px; }");
        html.append("h2 { font-size: 14pt; margin-top: 30px; border-bottom: 2px solid #333; padding-bottom: 5px; }");
        html.append("table { width: 100%; border-collapse: collapse; margin: 10px 0; }");
        html.append("th, td { border: 1px solid #333; padding: 8px; text-align: left; }");
        html.append("th { background-color: #f0f0f0; font-weight: bold; }");
        html.append(".step-item { margin: 15px 0; padding: 10px; border: 1px solid #ddd; background: #fafafa; }");
        html.append(".step-header { font-weight: bold; margin-bottom: 8px; }");
        html.append(".step-image { text-align: center; margin: 10px 0; }");
        html.append(".step-image img { max-width: 500px; max-height: 400px; }");
        html.append(".page-break { page-break-after: always; }");
        html.append("</style></head><body>");

        for (int i = 0; i < records.size(); i++) {
            BuStandardProcess record = records.get(i);

            html.append("<h1>标准工序</h1>");
            html.append("<table>");
            html.append("<tr><th width='20%'>工序编码</th><td>").append(escapeHtml(record.getProcessNo())).append("</td>");
            html.append("<th width='20%'>工序名称</th><td>").append(escapeHtml(record.getProcessName())).append("</td></tr>");
            html.append("<tr><th>工序类型</th><td>").append(getProcessTypeName(record.getProcessType())).append("</td>");
            html.append("<th>车型</th><td>").append(escapeHtml(record.getTrainType())).append("</td></tr>");
            html.append("<tr><th>标准工时(分钟)</th><td>").append(record.getStandardDuration() != null ? record.getStandardDuration() : 0).append("</td>");
            html.append("<th>创建时间</th><td>").append(formatDateTime(record.getCreateTime())).append("</td></tr>");
            html.append("</table>");

            List<Map<String, Object>> steps = parseSteps(record.getProcessSteps());
            if (!steps.isEmpty()) {
                html.append("<h2>工序步骤</h2>");
                for (Map<String, Object> step : steps) {
                    html.append("<div class='step-item'>");
                    html.append("<div class='step-header'>步骤 ").append(step.get("stepNo")).append(": ").append(escapeHtml((String) step.get("name"))).append("</div>");

                    String content = (String) step.get("content");
                    if (StringUtils.isNotBlank(content)) {
                        html.append("<div><strong>内容：</strong>").append(escapeHtml(content)).append("</div>");
                    }

                    String checkPoint = (String) step.get("checkPoint");
                    if (StringUtils.isNotBlank(checkPoint)) {
                        html.append("<div><strong>检验点：</strong>").append(escapeHtml(checkPoint)).append("</div>");
                    }

                    String remark = (String) step.get("remark");
                    if (StringUtils.isNotBlank(remark)) {
                        html.append("<div><strong>备注：</strong>").append(escapeHtml(remark)).append("</div>");
                    }

                    String imageUrl = (String) step.get("imageUrl");
                    if (StringUtils.isNotBlank(imageUrl)) {
                        html.append("<div class='step-image'><img src='").append(escapeHtml(imageUrl)).append("' alt='步骤图片'/></div>");
                    }

                    html.append("</div>");
                }
            }

            if (StringUtils.isNotBlank(record.getRemark())) {
                html.append("<h2>备注</h2>");
                html.append("<div style='padding: 10px; background: #fafafa; border: 1px solid #ddd;'>");
                html.append(escapeHtml(record.getRemark()));
                html.append("</div>");
            }

            if (i < records.size() - 1) {
                html.append("<div class='page-break'></div>");
            }
        }

        html.append("</body></html>");

        return renderPdf(html.toString());
    }

    private List<Map<String, Object>> parseSteps(String processSteps) {
        if (StringUtils.isBlank(processSteps)) {
            return Collections.emptyList();
        }
        try {
            Gson gson = new Gson();
            List<Map<String, Object>> steps = gson.fromJson(processSteps, new TypeToken<List<Map<String, Object>>>(){}.getType());
            return steps != null ? steps : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private String getProcessTypeName(String type) {
        if ("1".equals(type)) return "拆卸";
        if ("2".equals(type)) return "检修";
        if ("3".equals(type)) return "安装";
        return type != null ? type : "";
    }

    private String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }

    private byte[] renderPdf(String html) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(outputStream);
            builder.run();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new JeecgBootException("PDF导出失败");
        }
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}
