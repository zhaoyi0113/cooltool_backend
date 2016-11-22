package com.cooltoo.util;

import com.cooltoo.exception.BadRequestException;
import com.cooltoo.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.ConnectException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hp on 2016/8/10.
 */
public final class NetworkUtil {

    private static Logger logger = LoggerFactory.getLogger(NetworkUtil.class);

    public static final long _2M = 2 * 1024 * 1024;

    /**
     * 抓取网络文件落地到本地；如果有一个抓取失败，则全部删掉。
     * @param urls 网络文件路径
     * @param localStoragePath 本地存储路径
     * @return 网络文件路径对保存在本地的文件路径的映射关系
     */
    public final static Map<String, String> fetchAllWebFile(List<String> urls, String localStoragePath) {
        if (VerifyUtil.isListEmpty(urls)) {
            return new HashMap<>();
        }
        if (VerifyUtil.isStringEmpty(localStoragePath)) {
            logger.error("local storage path is empty");
            throw new BadRequestException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        File directory = new File(localStoragePath);
        if (!(directory.exists() && directory.isDirectory())) {
            logger.error("local storage path is not existed or not a directory");
            throw new BadRequestException(ErrorCode.RECORD_NOT_EXIST);
        }

        final FileUtil fileUtil = FileUtil.getInstance();

        Map<String, String> result = new HashMap<>();
        byte[] buffer = new byte[1024*1024];
        boolean error = false;
        for (int i=0; i<urls.size(); i++) {
            InputStream is = null;
            OutputStream os = null;
            String tmpUrl = urls.get(i);
            try {
                if (result.containsKey(tmpUrl)) {
                    continue;
                }


                URL url = null;
                try { url = new URL(tmpUrl); }
                catch (IOException ex) {}
                if (null==url) {
                    continue;
                }

                URLConnection conn = url.openConnection();
                long contentLength = conn.getContentLengthLong();

                // big than 2M
                if (contentLength > _2M) {
                    error = true;
                }

                String fileName = fileUtil.getFileName(url.getFile());
                fileName += "_"+i;
                File file = new File(directory, fileName);

                if (!error) {
                    try { is = conn.getInputStream(); }
                    catch (IOException io) { continue; }

                    os = new FileOutputStream(file);
                    int read = 0;
                    while ((read = is.read(buffer, 0, buffer.length)) > 0) {
                        os.write(buffer, 0, read);
                    }
                    os.flush();
                    is.close();
                    os.close();
                    result.put(tmpUrl, file.getAbsolutePath());
                }
            }
            catch (IOException ex) {
                logger.error("download image failed; image={}", tmpUrl);
                logger.error("download image failed; exception={}", ex);
                error = true;
                if (null!=is) {
                    try { is.close(); } catch (IOException ioex) {}
                }
                if (null!=os) {
                    try { os.close(); } catch (IOException ioex) {}
                }
            }

            if (error) {
                break;
            }
        }

        // delete files and clear result set
        if (error) {
            logger.error("download image failed; clear image downloaded={}", result);
            Set<String> keys = result.keySet();
            for (String tmp : keys) {
                String val = result.get(tmp);
                fileUtil.deleteFile(val);
            }
            result.clear();
        }

        return result;
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     */
    public final static String getIpAddress(HttpServletRequest request) throws IOException {
        // 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址

        String ip = request.getHeader("X-Forwarded-For");
        if (logger.isInfoEnabled()) {
            logger.info("getIpAddress(HttpServletRequest) - X-Forwarded-For - String ip=" + ip);
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - WL-Proxy-Client-IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_CLIENT_IP - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - HTTP_X_FORWARDED_FOR - String ip=" + ip);
                }
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
                if (logger.isInfoEnabled()) {
                    logger.info("getIpAddress(HttpServletRequest) - getRemoteAddr - String ip=" + ip);
                }
            }
        } else if (ip.length() > 15) {
            String[] ips = ip.split(",");
            for (int index = 0; index < ips.length; index++) {
                String strIp = (String) ips[index];
                if (!("unknown".equalsIgnoreCase(strIp))) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    /**
     * 发送https请求
     * @param requestUrl 请求地址
     * @param requestMethod 请求方式（GET、POST）
     * @param outputStr 提交的数据
     * @return 返回微信服务器响应的信息
     */
    public final static String httpsRequest(String requestUrl, String requestMethod, String outputStr, TrustManager[] tm) {
        try {
            // 创建SSLContext对象，并使用我们指定的信任管理器初始化
            if (null==tm || tm.length==0) {
                tm = new TrustManager[]{ new DefaultX509TrustManager() };
            }
            SSLContext sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
            SSLSocketFactory ssf = sslContext.getSocketFactory();
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setSSLSocketFactory(ssf);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            return buffer.toString();
        } catch (ConnectException ce) {
            logger.error("连接超时：{}", ce);
        } catch (Exception e) {
            logger.error("https请求异常：{}", e);
        }
        return null;
    }

    // X.509是一种非常通用的证书格式。所有的证书都符合ITU-T X.509国际标准，
    // 因此(理论上)为一种应用创建的证书可以用于任何其他符合X.509标准的应用。
    private static class DefaultX509TrustManager implements X509TrustManager {
        @Override public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException { }
        @Override public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException { }
        @Override public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
    }
}
