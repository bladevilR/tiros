package org.jeecg.common.util.storage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.exception.JeecgBootException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <p>
 * minio存储服务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-09
 */
public class MinioStorage {

    private static String FILE_SERVICE_URL;
    private static String FILE_SUB_PATH;

    public static void setUrl(String fileServiceUrl) {
        FILE_SERVICE_URL = fileServiceUrl;
    }

    public static void setSubPath(String subPath) {
        FILE_SUB_PATH = subPath;
    }


    public static String storeImage(BufferedImage bufferedImage, String imageName, String formatName) throws Exception {
        if (StringUtils.isBlank(FILE_SERVICE_URL)) {
            throw new JeecgBootException("请设置文件上传服务路径");
        }

//        // 图片转文件
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        ImageIO.write(bufferedImage, formatName, byteArrayOutputStream);
//        byteArrayOutputStream.flush();
//        byte[] byteArray = byteArrayOutputStream.toByteArray();
//        MultipartFile multipartFile = new ConvertToMultipartFile(byteArray, imageName, imageFullName, formatName, byteArray.length);
//        byteArrayOutputStream.close();

//        JSONObject variables = new JSONObject();
//        // 设置文件上传位置子路径
//        if (StringUtils.isNotBlank(FILE_SUB_PATH)) {
//            variables.put("fileName", FILE_SUB_PATH);
//        } else {
//            variables.put("fileName", "/");
//        }
//
//        JSONObject params = new JSONObject();
//        params.put("file", multipartFile);
//        JSONObject response = RestUtil.post(FILE_SERVICE_URL, variables, params);

        // 换行符
        final String newLine = "\r\n";
        final String boundaryPrefix = "--";
        // 数据分隔线
        String BOUNDARY = "----WebKitFormBoundary" + RandomStringUtils.randomAlphanumeric(16);
        // 服务器
        URL url = new URL(FILE_SERVICE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 设置post请求
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        // 设置请求头参数
        connection.setRequestProperty("Connection", "keep-alive");
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");
        connection.setRequestProperty("Sec-Fetch-Mode", "cors");
        connection.setRequestProperty("Accept", "application/json, text/plain, */*");

        // 获取连接输出流
        OutputStream outputStream = new DataOutputStream(connection.getOutputStream());

        // 开始上传文件
        StringBuilder outputSB = new StringBuilder();
        outputSB.append(boundaryPrefix);
        outputSB.append(BOUNDARY);
        outputSB.append(newLine);
        // 文件参数
        outputSB.append("Content-Disposition: form-data; name=\"file\"; filename=\"").append(imageName).append("\"").append(newLine);
        outputSB.append("Content-Type: image/png");
        outputSB.append(newLine);
        outputSB.append(newLine);
        outputSB.append(newLine);

        outputSB.append(boundaryPrefix);
        outputSB.append(BOUNDARY);
        outputSB.append(newLine);
        outputSB.append("Content-Disposition: form-data; name=\"fileName\"");
        outputSB.append(newLine);
        outputSB.append(newLine);

        outputSB.append(FILE_SUB_PATH);
        outputSB.append(newLine);
        outputSB.append(boundaryPrefix);
        outputSB.append(BOUNDARY);
        outputSB.append(boundaryPrefix);

        // 将参数头的数据写入到输出流中
        System.out.println(outputSB.toString());
        outputStream.write(outputSB.toString().getBytes());

        // 数据输入流，用于读取文件数据
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, formatName, byteArrayOutputStream);
        byteArrayOutputStream.flush();
        InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        DataInputStream dataInputStream = new DataInputStream(inputStream);
        byte[] bufferOut = new byte[1024];
        int bytes;
        // 每次读取1KB数据，并且将文件数据写入到输出流中
        while ((bytes = dataInputStream.read(bufferOut)) != -1) {
            outputStream.write(bufferOut, 0, bytes);
        }
        // 添加换行
        outputStream.write(newLine.getBytes());

        // 定义最后数据分割线
        byte[] endData = (newLine + boundaryPrefix + BOUNDARY + boundaryPrefix + newLine).getBytes();
        // 写上结尾标识
        outputStream.write(endData);
        outputStream.flush();

        // 读取URL响应
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String line;
        StringBuilder inputSB = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            inputSB.append(line).append(newLine);
        }

        String response = inputSB.toString();
        Result<String> result = JSON.parseObject(response, new TypeReference<Result<String>>() {
        });
        String filePath = null;
        if (200 == result.getCode()) {
            filePath = result.getResult();
        }

        byteArrayOutputStream.close();
        dataInputStream.close();
        inputStream.close();
        outputStream.close();
        reader.close();
        connection.disconnect();

        return filePath;
    }

}
