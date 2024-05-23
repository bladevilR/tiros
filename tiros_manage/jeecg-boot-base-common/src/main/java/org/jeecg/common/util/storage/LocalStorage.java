package org.jeecg.common.util.storage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * <p>
 * 本地对象存储服务
 * </p>
 *
 * @author zhaiyantao
 * @since 2021-04-09
 */
public class LocalStorage {

    public static final String LOCAL_PATH = "D:\\nannar\\code\\qrcode\\";

    public static String storeToLocal(BufferedImage bufferedImage, String imageName, String imageType) throws IOException {
        File file = new File(String.format("%s%s.%s", LOCAL_PATH, imageName, imageType));
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        ImageIO.write(bufferedImage, imageType, file);

        return file.getAbsolutePath();
    }
}
