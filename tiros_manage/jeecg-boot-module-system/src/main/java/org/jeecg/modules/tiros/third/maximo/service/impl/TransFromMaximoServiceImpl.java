package org.jeecg.modules.tiros.third.maximo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.threadlocal.ThreadLocalToken;
import org.jeecg.common.tiros.third.maximo.service.TransFromMaximoService;
import org.jeecg.common.util.RestUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Timer;

/**
 * <p>
 * 从maximo读取数据 服务实现类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-06-16
 */
@Slf4j
@Service
public class TransFromMaximoServiceImpl implements TransFromMaximoService {

    @Value("${tiros.maximo.address}")
    private String serviceAddress;

    @Value("${tiros.maximo.path.readMaterialStockChange}")
    private String readMaterialStockChangePath;

    @Value("${tiros.maximo.path.readMaterialReWrite}")
    private String readMaterialReWritePath;

    @Resource
    private ISysBaseAPI sysBaseAPI;


    /**
     * @see TransFromMaximoService#readMaterialStockChange(Timer)
     */
    @Override
    public boolean readMaterialStockChange(Timer timer) {
        try {
            // 请求路径
            String url = serviceAddress + readMaterialStockChangePath;

            // 发起请求
            HttpHeaders headers = initHeaders();
            log.info("请求maximo同步库存变动服务，地址=" + url + "，类型=POST，参数=");
            ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.POST, headers, null, null, JSONObject.class);
            JSONObject jsonObject = responseEntity.getBody();

            // 处理结果
            if (null == jsonObject) {
                throw new JeecgBootException("请求maximo服务失败，请联系开发人员");
            }

            Result<Boolean> result = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<Result<Boolean>>() {
            });
            if (result.isSuccess() && null != result.getResult() && result.getResult()) {
                return true;
            } else {
                log.error("请求maximo同步库存变动服务未成功，错误信息如下=" + result.getMessage());
                throw new JeecgBootException(result.getMessage());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }

        timer.cancel();
        return true;
    }

    /**
     * @see TransFromMaximoService#readMaterialReWrite(Timer)
     */
    @Override
    public boolean readMaterialReWrite(Timer timer) {
        try {
            // 请求路径
            String url = serviceAddress + readMaterialReWritePath;

            // 发起请求
            HttpHeaders headers = initHeaders();
            log.info("请求maximo同步消耗回写服务，地址=" + url + "，类型=POST，参数=");
            ResponseEntity<JSONObject> responseEntity = RestUtil.request(url, HttpMethod.POST, headers, null, null, JSONObject.class);
            JSONObject jsonObject = responseEntity.getBody();

            // 处理结果
            if (null == jsonObject) {
                throw new JeecgBootException("请求maximo服务失败，请联系开发人员");
            }

            Result<Boolean> result = JSON.parseObject(jsonObject.toJSONString(), new TypeReference<Result<Boolean>>() {
            });
            if (result.isSuccess() && null != result.getResult() && result.getResult()) {
                return true;
            } else {
                log.error("请求maximo同步消耗回写服务未成功，错误信息如下=" + result.getMessage());
                throw new JeecgBootException(result.getMessage());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage());
            ex.printStackTrace();
        }

        timer.cancel();
        return true;
    }


    private HttpHeaders initHeaders() {
        String authToken = getAuthToken();

        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtUtil.AuthKey, authToken);
        headers.add("content-type", "application/json;charset=UTF-8");
        return headers;
    }

    private String getAuthToken() {
        String token = ThreadLocalToken.getCurrentToken();
        if (StringUtils.isBlank(token)) {
            LoginUser sysUser = sysBaseAPI.getUserByName("admin");
            token = JwtUtil.sign(sysUser.getId(), sysUser.getUsername(), sysUser.getPassword());
            ThreadLocalToken.set(token);
        }

        return token;
    }

}
