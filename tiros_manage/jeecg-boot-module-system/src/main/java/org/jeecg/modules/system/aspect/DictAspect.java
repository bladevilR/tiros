package org.jeecg.modules.system.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.common.aspect.annotation.DictIgnore;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.service.ISysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 字典aop类
 * @Author: dangzhenghui
 * @Date: 2019-3-17 21:50
 * @Version: 1.0
 */
@Aspect
@Component
@Slf4j
public class DictAspect {

    @Value("${spring.profiles.active:prod}")
    private String active;

    @Autowired
    private ISysDictService dictService;

    // 定义切点Pointcut
    @Pointcut("execution(public * org.jeecg.modules..*.*Controller.*(..))")
    public void excudeService() {
    }

    @Around("excudeService()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
        String controllerName = pjp.getSignature().getDeclaringTypeName();
        String shortControllerName = controllerName.substring(controllerName.lastIndexOf(".") + 1);
        String methodName = pjp.getSignature().getName();

        long time1 = System.currentTimeMillis();
        Object result = pjp.proceed();
        long time2 = System.currentTimeMillis();

        long time3 = System.currentTimeMillis();
        // 获取方法上是否注解了忽略字典翻译
        MethodSignature sign = (MethodSignature) pjp.getSignature();
        Method method = sign.getMethod();
        boolean ignoreDict = method.isAnnotationPresent(DictIgnore.class);
        if (!ignoreDict) {
            this.parseDictText(result);
        }
        long time4 = System.currentTimeMillis();

        if ("dev".equals(active) || "test".equals(active)) {
            String dictParseTimeLog = "字典切面-字典解析耗时" + (ignoreDict ? "(已忽略)" : "") + "：" + (time4 - time3) + "ms";
            log.info("字典切面-请求执行(" + shortControllerName + "." + methodName + "方法)耗时：" + (time2 - time1) + "ms");
            log.info(dictParseTimeLog);
            log.info("字典切面-总耗时：" + (time4 - time1) + "ms");
        }

        return result;
    }

    /**
     * 本方法针对返回对象为Result 的IPage的分页列表数据进行动态字典注入
     * 字典注入实现 通过对实体类添加注解@dict 来标识需要的字典内容,字典分为单字典code即可 ，table字典 code table text配合使用与原来jeecg的用法相同
     * 示例为SysUser   字段为sex 添加了注解@Dict(dicCode = "sex") 会在字典服务立马查出来对应的text 然后在请求list的时候将这个字典text，已字段名称加_dictText形式返回到前端
     * 例输入当前返回值的就会多出一个sex_dictText字段
     * {
     * sex:1,
     * sex_dictText:"男"
     * }
     * 前端直接取值sext_dictText在table里面无需再进行前端的字典转换了
     * customRender:function (text) {
     * if(text==1){
     * return "男";
     * }else if(text==2){
     * return "女";
     * }else{
     * return text;
     * }
     * }
     * 目前vue是这么进行字典渲染到table上的多了就很麻烦了 这个直接在服务端渲染完成前端可以直接用
     *
     * @param result
     */
   /* private void parseDictText(Object result) {
        if (result instanceof Result) {
            if (((Result) result).getResult() instanceof IPage) {
                List<JSONObject> items = new ArrayList<>();
                for (Object record : ((IPage) ((Result) result).getResult()).getRecords()) {
                    ObjectMapper mapper = new ObjectMapper();
                    String json="{}";
                    try {
                        //解决@JsonFormat注解解析不了的问题详见SysAnnouncement类的@JsonFormat
                         json = mapper.writeValueAsString(record);
                    } catch (JsonProcessingException e) {
                        log.error("json解析失败"+e.getMessage(),e);
                    }
                    JSONObject item = JSONObject.parseObject(json);
                    //update-begin--Author:scott -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
                    //for (Field field : record.getClass().getDeclaredFields()) {
                    for (Field field : oConvertUtils.getAllFields(record)) {
                    //update-end--Author:scott  -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
                        if (field.getAnnotation(Dict.class) != null) {
                            String code = field.getAnnotation(Dict.class).dicCode();
                            String text = field.getAnnotation(Dict.class).dicText();
                            String table = field.getAnnotation(Dict.class).dictTable();
                            String key = String.valueOf(item.get(field.getName()));

                            //翻译字典值对应的txt
                            String textValue = translateDictValue(code, text, table, key);

                            log.debug(" 字典Val : "+ textValue);
                            log.debug(" __翻译字典字段__ "+field.getName() + CommonConstant.DICT_TEXT_SUFFIX+"： "+ textValue);
                            item.put(field.getName() + CommonConstant.DICT_TEXT_SUFFIX, textValue);
                        }
                        //date类型默认转换string格式化日期
                        if (field.getType().getName().equals("java.util.Date")&&field.getAnnotation(JsonFormat.class)==null&&item.get(field.getName())!=null){
                            SimpleDateFormat aDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            item.put(field.getName(), aDate.format(new Date((Long) item.get(field.getName()))));
                        }
                    }
                    items.add(item);
                }
                ((IPage) ((Result) result).getResult()).setRecords(items);
            }

        }
    }*/

//    //yyg 增加不分页解析
//    private void parseDictText(Object result) {
//        if (result instanceof Result) {
//            List<JSONObject> items = new ArrayList<>();
//            if (((Result) result).getResult() instanceof IPage) {
//                for (Object record : ((IPage) ((Result) result).getResult()).getRecords()) {
//                    if (!isConvertJson(record)) {
//                        return;
//                    }
//                    JSONObject item = parseDictTextByType(record);
//                    items.add(item);
//                }
//                ((IPage) ((Result) result).getResult()).setRecords(items);
//
//            } else if (((Result) result).getResult() instanceof List) {
//                for (Object object : (List) ((Result) result).getResult()) {
//                    if (!isConvertJson(object)) {
//                        return;
//                    }
//                    JSONObject item = parseDictTextByType(object);
//                    items.add(item);
//                }
//                ((Result) result).setResult(items);
//            } else if (((Result) result).getResult() instanceof Object) {
//                if (!isConvertJson(((Result) result).getResult())) {
//                    return;
//                }
//                JSONObject item = parseDictTextByType(((Result) result).getResult());
//                ((Result) result).setResult(item);
//
//            }
//        }
//    }
    private void parseDictText(Object result) throws IllegalAccessException {
        if (result instanceof Result) {
            List<JSONObject> items = new ArrayList<>();

            if (((Result) result).getResult() instanceof IPage) {
                if (CollectionUtils.isNotEmpty(((IPage) ((Result) result).getResult()).getRecords())) {
                    Object obj = ((IPage) ((Result) result).getResult()).getRecords().get(0);
                    Map<String, Map<String, String>> dictCodeValuesMap = getDictCodeValuesMap(obj);

                    for (Object record : ((IPage) ((Result) result).getResult()).getRecords()) {
                        if (!isConvertJson(record)) {
                            return;
                        }
                        JSONObject item = parseDictTextByType(record, dictCodeValuesMap);
                        items.add(item);
                    }

                    ((IPage) ((Result) result).getResult()).setRecords(items);
                }
            } else if (((Result) result).getResult() instanceof List) {
                if (CollectionUtils.isNotEmpty(((List) ((Result) result).getResult()))) {
                    Object obj = ((List) ((Result) result).getResult()).get(0);
                    Map<String, Map<String, String>> dictCodeValuesMap = getDictCodeValuesMap(obj);

                    for (Object object : (List) ((Result) result).getResult()) {
                        if (!isConvertJson(object)) {
                            return;
                        }
                        JSONObject item = parseDictTextByType(object, dictCodeValuesMap);
                        items.add(item);
                    }

                    ((Result) result).setResult(items);
                }
            } else if (((Result) result).getResult() instanceof Object) {
                Object obj = ((Result) result).getResult();
                Map<String, Map<String, String>> dictCodeValuesMap = getDictCodeValuesMap(obj);

                if (!isConvertJson(((Result) result).getResult())) {
                    return;
                }
                JSONObject item = parseDictTextByType(obj, dictCodeValuesMap);

                ((Result) result).setResult(item);
            }
        }
    }

    private Map<String, Map<String, String>> getDictCodeValuesMap(Object obj) throws IllegalAccessException {
        // 如果注解了忽略字典翻译
        if (null != obj) {
            DictIgnore dictIgnore = obj.getClass().getAnnotation(DictIgnore.class);
            if (null != dictIgnore) {
                return new HashMap<>();
            }
        }

        long time1 = System.currentTimeMillis();

        Set<String> dictCodeSet = oConvertUtils.getObjectFieldsDictCode(obj);

        long time2 = System.currentTimeMillis();

        Map<String, Map<String, String>> dictCodeValuesMap = dictService.queryDictItemsByCodeList(new ArrayList<>(dictCodeSet));

        long time3 = System.currentTimeMillis();
        log.debug("字典切面-字典解析(反射获取dictCode)耗时：" + (time2 - time1) + "ms");
        log.debug("字典切面-字典解析(根据dictCode查数据库)耗时：" + (time3 - time2) + "ms");

        return dictCodeValuesMap;
    }

    private boolean isConvertJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        String json = "{}";
        try {
            json = mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.error("json解析失败" + e.getMessage(), e);
        }
        try {
            JSONObject item = JSONObject.parseObject(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 翻译字典文本
     *
     * @param code
     * @param text
     * @param table
     * @param key
     * @return
     */
    private String translateDictValue(String code, String text, String table, String key) {
        if (oConvertUtils.isEmpty(key)) {
            return null;
        }
        StringBuffer textValue = new StringBuffer();
        String[] keys = key.split(",");
        for (String k : keys) {
            String tmpValue = null;
            log.debug(" 字典 key : " + k);
            if (k.trim().length() == 0) {
                continue; //跳过循环
            }
            if (!StringUtils.isEmpty(table)) {
                log.debug("--DictAspect------dicTable=" + table + " ,dicText= " + text + " ,dicCode=" + code);
                tmpValue = dictService.queryTableDictTextByKey(table, text, code, k.trim());
            } else {
                tmpValue = dictService.queryDictTextByKey(code, k.trim());
            }

            if (tmpValue != null) {
                if (!"".equals(textValue.toString())) {
                    textValue.append(",");
                }
                textValue.append(tmpValue);
            }

        }
        return textValue.toString();
    }

//    //yyg 增加不分页解析
//    private JSONObject parseDictTextByType(Object obj) {
//        ObjectMapper mapper = new ObjectMapper();
//        String json = "{}";
//        try {
//            //解决@JsonFormat注解解析不了的问题详见SysAnnouncement类的@JsonFormat
//            json = mapper.writeValueAsString(obj);
//        } catch (JsonProcessingException e) {
//            log.error("json解析失败" + e.getMessage(), e);
//        }
//
//        JSONObject item = JSONObject.parseObject(json);
//        //update-begin--Author:scott -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
//        //for (Field field : record.getClass().getDeclaredFields()) {
//        for (Field field : oConvertUtils.getAllFields(obj)) {
//            //update-end--Author:scott  -- Date:20190603 ----for：解决继承实体字段无法翻译问题------
//            if (field.getAnnotation(Dict.class) != null) {
//                String code = field.getAnnotation(Dict.class).dicCode();
//                String text = field.getAnnotation(Dict.class).dicText();
//                String table = field.getAnnotation(Dict.class).dictTable();
//                String key = String.valueOf(item.get(field.getName()));
//
//                //翻译字典值对应的txt
//                String textValue = translateDictValue(code, text, table, key);
//
//                log.debug(" 字典Val : " + textValue);
//                log.debug(" __翻译字典字段__ " + field.getName() + CommonConstant.DICT_TEXT_SUFFIX + "： " + textValue);
//                item.put(field.getName() + CommonConstant.DICT_TEXT_SUFFIX, textValue);
//            }
//            //date类型默认转换string格式化日期
//            if (field.getType().getName().equals("java.util.Date") && field.getAnnotation(JsonFormat.class) == null && item.get(field.getName()) != null) {
//                SimpleDateFormat aDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                item.put(field.getName(), aDate.format(new Date((Long) item.get(field.getName()))));
//            }
//        }
//        return item;
//
//    }

    private JSONObject parseDictTextByType(Object obj, Map<String, Map<String, String>> dictCodeValuesMap) throws IllegalAccessException {
        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        ObjectMapper mapper = new ObjectMapper();
        String json = "{}";
        try {
            json = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("json解析失败" + e.getMessage(), e);
        }
        JSONObject result = JSONObject.parseObject(json, Feature.OrderedField);

        // 如果注解了忽略字典翻译
        if (null != obj) {
            DictIgnore dictIgnore = obj.getClass().getAnnotation(DictIgnore.class);
            if (null != dictIgnore) {
                return result;
            }
        }

        Field[] fields = oConvertUtils.getAllCustomFields(obj);
        fields = filterModulesFields(fields);

        for (Field field : fields) {
            if (!field.getDeclaringClass().getPackage().getName().contains("org.jeecg")) {
                continue;
            }
            if (null != field.getAnnotation(JsonIgnore.class)) {
                continue;
            }
            if ("DO".equals(field.getName())) {
                continue;
            }

            field.setAccessible(true);
            Object fieldObject = field.get(obj);

            // 列表或map为null的话，返回空集合
            if (field.getGenericType().getTypeName().contains("java.util.List") && fieldObject == null) {
                fieldObject = new ArrayList<>();
            }
            if (field.getGenericType().getTypeName().contains("java.util.Map") && fieldObject == null) {
                fieldObject = new HashMap<>();
            }

            if (fieldObject instanceof List) {
                List list = (List) fieldObject;
                JSONArray jsonArray = new JSONArray();
                for (Object listObject : list) {
                    if (!isConvertJson(listObject)) {
                        jsonArray.add(listObject);
                    } else {
                        JSONObject jsonObject = parseDictTextByType(listObject, dictCodeValuesMap);
                        jsonArray.add(jsonObject);
                    }
                }
                result.put(field.getName(), jsonArray);
            } else if (fieldObject instanceof Map) {
                Map map = (Map) fieldObject;
                JSONArray jsonArray = new JSONArray();
                for (Object mapObject : map.entrySet()) {
                    if (mapObject instanceof Map.Entry) {
                        Map.Entry mapEntry = (Map.Entry) mapObject;

                        if (!isConvertJson(mapEntry.getValue())) {
                            JSONObject jsonObject = new JSONObject();
                            Object mapEntryKey = mapEntry.getKey();
                            if (mapEntryKey instanceof String) {
                                jsonObject.put((String) mapEntryKey, mapEntry.getValue());
                            } else {
                                jsonObject.put(JSON.toJSONString(mapEntryKey), mapEntry.getValue());
                            }

                            jsonArray.add(jsonObject);
                        } else {
                            JSONObject valueObject = parseDictTextByType(mapEntry.getValue(), dictCodeValuesMap);

                            JSONObject jsonObject = new JSONObject();
                            Object mapEntryKey = mapEntry.getKey();
                            if (mapEntryKey instanceof String) {
                                jsonObject.put((String) mapEntryKey, valueObject);
                            } else {
                                jsonObject.put(JSON.toJSONString(mapEntryKey), valueObject);
                            }

                            jsonArray.add(jsonObject);
                        }
                    }
                }
                result.put(field.getName(), jsonArray);
            } else {
                if (null != fieldObject && fieldObject.getClass().getPackage().getName().contains("org.jeecg.")) {
                    JSONObject valueObject = parseDictTextByType(fieldObject, dictCodeValuesMap);

                    result.put(field.getName(), valueObject);
                } else {
                    if (!"log".equals(field.getName())
                            && !"serialVersionUID".equals(field.getName())
                            && !"java.util.Date".equals(field.getType().getName())) {
                        result.put(field.getName(), fieldObject);
                    }

                    if (null != field.getAnnotation(Dict.class)) {
                        if (null == fieldObject) {
                            result.put(field.getName() + CommonConstant.DICT_TEXT_SUFFIX, null);
                        } else {
                            String code = field.getAnnotation(Dict.class).dicCode();
                            String key = String.valueOf(fieldObject);

                            //翻译字典值对应的txt
                            StringBuilder textValue = new StringBuilder();
                            if (dictCodeValuesMap.containsKey(code)) {
                                if (key.contains(",")) {
                                    String[] keySplits = key.split(",");
                                    for (String keySplit : keySplits) {
                                        String dictText = dictCodeValuesMap.get(code).get(keySplit);
                                        if (null != dictText) {
                                            textValue.append(dictText)
                                                    .append(",");
                                        }
                                    }
                                    textValue.deleteCharAt(textValue.length() - 1);
                                } else {
                                    String dictText = dictCodeValuesMap.get(code).get(key);
                                    if (null != dictText) {
                                        textValue = new StringBuilder(dictText);
                                    }
                                }
                            }
                            result.put(field.getName() + CommonConstant.DICT_TEXT_SUFFIX, textValue.toString());
                        }
                    }

                    // date类型默认转换string格式化日期
                    if (null != fieldObject
                            && "java.util.Date".equals(field.getType().getName())
                            && null == field.getAnnotation(JsonFormat.class)) {
                        result.put(field.getName(), dateFormat.format(((Date) fieldObject)));
                    }
                }
            }

            field.setAccessible(false);
        }

        return result;
    }

    private Field[] filterModulesFields(Field[] fields) {
        List<Field> modulesFieldList = new ArrayList<>();
        for (Field field : fields) {
            if (field.getDeclaringClass().getPackage().getName().contains("org.jeecg")) {
                modulesFieldList.add(field);
            }
        }

        Field[] modulesFields = new Field[modulesFieldList.size()];
        return modulesFieldList.toArray(modulesFields);
    }

}
