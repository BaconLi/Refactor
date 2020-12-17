package com.dospyer.refactor.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @Author: peigen
 * @Date: 2020/12/17 下午4:21
 */
public class Resource {

    public static String getFileContent(String name) throws IOException {
        return getContentUtf8(name,StandardCharsets.UTF_8.name());
    }

    public static String getContentUtf8(String name,String charset) throws IOException {
        URL resource = Resource.class.getClassLoader().getResource("./" + name);
        if(resource == null){
            throw new RuntimeException("file not found");
        }
        return FileUtils.readFileToString(new File(resource.getFile()), charset);
    }
}
