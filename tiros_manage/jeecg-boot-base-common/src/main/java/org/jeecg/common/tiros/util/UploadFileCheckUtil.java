package org.jeecg.common.tiros.util;


import org.jeecg.common.exception.JeecgBootException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * <p>
 * 上传文件检验工具类
 * </p>
 *
 * @author zhaiyantao
 * @since 2021/12/15
 */
public class UploadFileCheckUtil {
    // 记录各个文件头信息及对应的文件类型
    public static Map<String, String> FILETYPE_MAP = new HashMap<>();
    public static Map<String, List<String>> SPECIAL_PATH_FILETYPES_MAP = new HashMap<>();

    // 所有合法的文件后缀
    public static List<String> FILETYPE_LIST = Arrays.asList(".jpg", ".png", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx", ".txt", ".pdf", ".avi", ".mp4", ".mp3", ".rar", ".zip");

    static {
        // images
        FILETYPE_MAP.put("FFD8FFE0", ".jpg");
        FILETYPE_MAP.put("89504E47", ".png");
//        mFileTypes.put("47494638", ".gif");
//        mFileTypes.put("49492A00", ".tif");
//        mFileTypes.put("424D", ".bmp");

        // PS和CAD
//        mFileTypes.put("38425053", ".psd");
//        mFileTypes.put("41433130", ".dwg"); // CAD
//        mFileTypes.put("252150532D41646F6265", ".ps");

        // 办公文档类
        FILETYPE_MAP.put("D0CF11E0", ".doc");// .doc .xls .ppt
        FILETYPE_MAP.put("D0CF11E0A1B11AE1", ".doc");// .doc .xls .ppt
        FILETYPE_MAP.put("504B0304", ".docx");// .docx .xlsx .pptx
        FILETYPE_MAP.put("504B030414000600", ".docx");// .docx .xlsx .pptx

        // 注意由于文本文档录入内容过多，则读取文件头时较为多变-START
        FILETYPE_MAP.put("0D0A0D0A", ".txt");//txt
        FILETYPE_MAP.put("0D0A2D2D", ".txt");//txt
        FILETYPE_MAP.put("0D0AB4B4", ".txt");//txt
        FILETYPE_MAP.put("B4B4BDA8", ".txt");//文件头部为汉字
        FILETYPE_MAP.put("73646673", ".txt");//txt,文件头部为英文字母
        FILETYPE_MAP.put("32323232", ".txt");//txt,文件头部内容为数字
        FILETYPE_MAP.put("0D0A09B4", ".txt");//txt,文件头部内容为数字
        FILETYPE_MAP.put("3132330D", ".txt");//txt,文件头部内容为数字
        // 注意由于文本文档录入内容过多，则读取文件头时较为多变-END

        // 日记本
//        mFileTypes.put("7B5C727466", ".rtf");

        // pdf
        FILETYPE_MAP.put("255044462D312E", ".pdf");

        // 视频或音频类
//        mFileTypes.put("3026B275", ".wma");
//        mFileTypes.put("57415645", ".wav");
        FILETYPE_MAP.put("41564920", ".avi");
//        mFileTypes.put("4D546864", ".mid");
//        mFileTypes.put("2E524D46", ".rm");
//        mFileTypes.put("000001BA", ".mpg");
//        mFileTypes.put("000001B3", ".mpg");
//        mFileTypes.put("6D6F6F76", ".mov");
//        mFileTypes.put("3026B2758E66CF11", ".asf");
        FILETYPE_MAP.put("000000186674797033677035", ".mp4");
        FILETYPE_MAP.put("FFFB50", ".mp3");
        FILETYPE_MAP.put("494433", ".mp3");

        // 压缩包
        FILETYPE_MAP.put("52617221", ".rar");
//        mFileTypes.put("1F8B08", ".gz");
        FILETYPE_MAP.put("504B3030", ".zip");
        FILETYPE_MAP.put("504B03", ".zip");
        FILETYPE_MAP.put("504B3030504B0304", ".zip");

        // 程序文件
//        mFileTypes.put("3C3F786D6C", ".xml");
//        mFileTypes.put("68746D6C3E", ".html");
//        mFileTypes.put("7061636B", ".java");
//        mFileTypes.put("3C254020", ".jsp");
//        mFileTypes.put("4D5A9000", ".exe");

        // 邮件
//        mFileTypes.put("44656C69766572792D646174653A", ".eml");
        // Access数据库文件
//        mFileTypes.put("5374616E64617264204A", ".mdb");

//        mFileTypes.put("46726F6D", ".mht");
//        mFileTypes.put("4D494D45", ".mhtml");
    }

    static {
        SPECIAL_PATH_FILETYPES_MAP.put("/img", Arrays.asList(".jpg", ".png"));
    }


    /**
     * 判断上传的文件是否合法
     *
     * @param file 文件
     */
    public static void checkFileValid(MultipartFile file, String path, List<String> allowFiletypeList) {
        boolean fileValid = fileValid(file, path, allowFiletypeList);
        if (!fileValid) {
            throw new JeecgBootException("文件 " + file.getOriginalFilename() + " 类型不合法");
        }
    }

    /**
     * 判断上传的文件是否合法
     * 1、检查文件的扩展名
     * 2、检查文件的MIME类型
     *
     * @param file 文件
     */
    public static boolean fileValid(MultipartFile file, String path, List<String> allowFiletypeList) {
        // 为真表示符合上传条件，为假表标不符合
        boolean valid = false;
        if (file != null) {
            String originalFilename = file.getOriginalFilename();

            System.out.println("------用户上传的文件：" + originalFilename + "------");
            System.out.println("------用户上传的路径：" + path + "------");

            if (null == allowFiletypeList) {
                allowFiletypeList = new ArrayList<>();
            } else {
                allowFiletypeList = new ArrayList<>(allowFiletypeList);
            }
            if (allowFiletypeList.isEmpty()) {
                if (null != path && SPECIAL_PATH_FILETYPES_MAP.containsKey(path)) {
                    allowFiletypeList = SPECIAL_PATH_FILETYPES_MAP.get(path);
                } else {
                    allowFiletypeList = FILETYPE_LIST;
                }
            }
            if (allowFiletypeList.contains(".xls") || allowFiletypeList.contains(".ppt")) {
                allowFiletypeList.add(".doc");
            }
            if (allowFiletypeList.contains(".xlsx") || allowFiletypeList.contains(".pptx")) {
                allowFiletypeList.add(".docx");
            }

            System.out.println("------允许的文件类型：" + String.join(" ", allowFiletypeList) + "------");

            if (!"".equals(originalFilename) && !"null".equals(originalFilename)) {
                assert originalFilename != null;
                if (originalFilename.contains(".")) {
                    // 返回在此字符串中最右边出现的指定子字符串的索引
                    String suffixName = originalFilename.substring(originalFilename.lastIndexOf("."));

                    // 统一转换为小写
                    suffixName = suffixName.toLowerCase();

                    // 1、检查文件扩展名，是否符合要求范围
                    valid = allowFiletypeList.contains(suffixName);

                    System.out.println("------用户上传的文件后缀：" + suffixName + "------");

                    // 2、获取上传附件的文件头，判断属于哪种类型,并获取其扩展名
                    // 直接读取文件的前几个字节，来判断上传文件是否符合格式，防止上传附件变更扩展名绕过校验
                    if (valid) {
                        String fileType = null;
                        try {
                            fileType = getFileType(file.getInputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("------用户上传的文件类型：" + fileType + "------");
                        // 检查文件扩展名，是否符合要求范围
                        if (fileType != null && !"".equals(fileType) && !"null".equals(fileType)) {
                            // 校验上传的文件扩展名，是否在其规定范围内
                            valid = allowFiletypeList.contains(fileType);
                        } else {
                            // 特殊情况校验,如果用户上传的扩展名为,文本文件,则允许上传
                            valid = suffixName.contains(".txt");
                        }
                    }
                }
            }
        }

        return valid;
    }

    /**
     * 根据文件的输入流获取文件头信息
     *
     * @param inputStream 文件的输入流
     * @return 文件头信息
     */
    public static String getFileType(InputStream inputStream) {
        byte[] bytes = new byte[4];
        if (inputStream != null) {
            try {
                int read = inputStream.read(bytes, 0, bytes.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String fileHeader = getFileHeader(bytes);
        System.out.println("------用户上传的文件头：" + fileHeader + "------");
        return FILETYPE_MAP.get(fileHeader);
    }

    /**
     * 根据文件转换成的字节数组获取文件头信息
     *
     * @param bytes 字节数组
     * @return 文件头信息
     */
    public static String getFileHeader(byte[] bytes) {
        return bytesToHexString(bytes);
    }

    /**
     * 将要读取文件头信息的文件的byte数组转换成string类型表示
     * 下面这段代码就是用来对文件类型作验证的方法，
     * 将字节数组的前四位转换成16进制字符串，并且转换的时候，要先和0xFF做一次与运算。
     * 这是因为，整个文件流的字节数组中，有很多是负数，进行了与运算后，可以将前面的符号位都去掉，
     * 这样转换成的16进制字符串最多保留两位，如果是正数又小于10，那么转换后只有一位，
     * 需要在前面补0，这样做的目的是方便比较，取完前四位这个循环就可以终止了
     *
     * @param src 要读取文件头信息的文件的byte数组
     * @return 文件头信息
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (byte b : src) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(b & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }

        return builder.toString();
    }

}
