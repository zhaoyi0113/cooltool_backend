package com.cooltoo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by hp on 2016/4/21.
 */
public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class.getName());

    public static void writeFile(InputStream input, File outputFile) throws IOException {
        logger.info("write from input--->output={}", outputFile);
        File outputDir = outputFile.getParentFile();
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        OutputStream output = null;
        output = new FileOutputStream(outputFile);
        byte[] buffer = new byte[1024];

        int read = 0;
        while ((read = input.read(buffer))>0) {
            output.write(buffer, 0, read);
        }
        output.flush();
        output.close();
    }

    public static void moveFile(String srcFilePath, String destFilePath) throws IOException {
        logger.info("move {} to {}", srcFilePath, destFilePath);
        File src = new File(srcFilePath);
        File dest = new File(destFilePath);

        if (dest.exists()) {
            logger.info("dest file is exist, destFile={}", dest);
            dest.delete();
        }

        boolean success = src.renameTo(dest);
        logger.info("move status={}", success);
        if (!success) {
            throw new IOException("Failed to move "+src+" to "+dest);
        }
    }

    public static boolean fileExist(String fileAbsolutePath) {
        logger.info("file exist, file path={}", fileAbsolutePath);
        if (VerifyUtil.isStringEmpty(fileAbsolutePath)) {
            logger.warn("file path is empty");
            return false;
        }
        boolean exist = (new File(fileAbsolutePath)).exists();
        logger.info("exist? {}", exist);
        return exist;
    }
}
