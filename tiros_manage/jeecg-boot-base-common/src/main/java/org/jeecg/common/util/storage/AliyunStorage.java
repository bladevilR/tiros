package org.jeecg.common.util.storage;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

/**
 * <p>
 * 阿里云存储服务
 * </p>
 *
 * @author zhaiyantao
 * @since 2020-09-25
 */
@Slf4j
public class AliyunStorage {

    private static final String OSS_END_POINT = "oss-cn-shanghai.aliyuncs.com";
    /**
     * OSS 的key值 - 从配置文件读取
     */
    private static final String OSS_ACCESS_KEY_ID = "${oss.access.key.id}";
    /**
     * OSS 的secret值 - 从配置文件读取
     */
    private static final String OSS_ACCESS_KEY_SECRET = "${oss.access.key.secret}";
    /**
     * OSS 的bucket名字
     */
    private static final String OSS_BUCKET_NAME = "wt-sh-default";
    /**
     * 设置URL过期时间为1小时（可自定义）
     */
    private final static Date OSS_URL_EXPIRATION = new Date(System.currentTimeMillis() + 3600 * 1000);

    private static OSSClient instance;

    /**
     * @return OSS工具类实例
     */
    private static OSSClient getOSSClient() {
        if (instance == null) {
            instance = new OSSClient(OSS_END_POINT, OSS_ACCESS_KEY_ID, OSS_ACCESS_KEY_SECRET);
        }
        return instance;
    }

    /**
     * 上传文件到OSS
     *
     * @param file 文件
     * @return 文件URL
     * @throws IOException IO异常
     */
    public static String uploadFile(MultipartFile file) throws IOException {
        OSSClient ossClient = getOSSClient();
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String fileName = RandomStringUtils.randomAlphanumeric(10) + "." + extension;

        InputStream inputStream = file.getInputStream();
        ossClient.putObject(OSS_BUCKET_NAME, fileName, inputStream);
        URL url = ossClient.generatePresignedUrl(OSS_BUCKET_NAME, fileName, OSS_URL_EXPIRATION);
        return url.toString();
    }

    /**
     * 上传文件到OSS（带自定义文件名）
     *
     * @param file 文件
     * @param baseName 基础文件名
     * @return 文件URL
     * @throws IOException IO异常
     */
    public static String storeFile(MultipartFile file, String baseName) throws IOException {
        OSSClient ossClient = getOSSClient();
        String originalFilename = file.getOriginalFilename();
        String extension = FilenameUtils.getExtension(originalFilename);
        String fileName = baseName + "_" + RandomStringUtils.randomAlphanumeric(6) + "." + extension;

        InputStream inputStream = file.getInputStream();
        ossClient.putObject(OSS_BUCKET_NAME, fileName, inputStream);
        URL url = ossClient.generatePresignedUrl(OSS_BUCKET_NAME, fileName, OSS_URL_EXPIRATION);
        return url.toString();
    }

    /**
     * 删除OSS文件
     *
     * @param fileName 文件名
     */
    public static void deleteFile(String fileName) {
        OSSClient ossClient = getOSSClient();
        ossClient.deleteObject(OSS_BUCKET_NAME, fileName);
    }
}
