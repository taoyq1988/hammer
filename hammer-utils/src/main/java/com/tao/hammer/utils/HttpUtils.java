package com.tao.hammer.utils;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.*;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * @author tyq
 * @version 1.0, 2017/11/2
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static final int FORBIDDEN = 403;

    // 服务器拒绝访问
    public static final String BASIC_HTTP_FORBIDDEN = "http.forbidden";

    public static String get(String uri) {
        return get(uri, null);
    }

    public static String get(String uri, String ip, int port) {
        return get(uri, null, ip, port);
    }

    public static String get(String uri, Header[] headers) {
        HttpGet request = new HttpGet(uri);
        setHeaders(request, headers);
        String response = sendRequest(request, "", 1080);
        return response;
    }

    public static String get(String uri, Header[] headers, String ip, int port) {
        HttpGet request = new HttpGet(uri);
        setHeaders(request, headers);
        String response = sendRequest(request, ip, port);
        return response;
    }

    public static String post(String uri, HttpEntity entity, String proxyhost, int proxyport) {
        return post(uri, null, entity, proxyhost, proxyport);
    }

    public static String post(String uri, Header[] headers, HttpEntity entity, String proxyhost, int proxyport) {
        HttpPost request = new HttpPost(uri);
        setHeaders(request, headers);
        if (entity != null) {
            request.setEntity(entity);
        }

        String response = sendRequest(request, proxyhost, proxyport);
        return response;
    }

    private static void setHeaders(HttpRequestBase request, Header[] headers) {
        if (headers != null && headers.length > 0) {
            request.setHeaders(headers);
        }
    }

    private static String sendRequest(HttpRequestBase request, String ip, int port) {
        CloseableHttpResponse response = null;
        try {
            HttpHost httpHost = ip != null && ip.length() > 0 ? new HttpHost(ip, port) : null;
            CloseableHttpClient client = HttpClientBuilder.create().setProxy(httpHost).build();
            response = client.execute(request);
            HttpEntity entity = response.getEntity();
            StatusLine status = response.getStatusLine();
            int statusCode = status.getStatusCode();
            if (FORBIDDEN == statusCode) {
                throw new Exception(BASIC_HTTP_FORBIDDEN);
            }
            ContentType contentType = ContentType.getOrDefault(entity);
            Charset charset = contentType.getCharset();
            if (charset == null) {
                charset = Charset.forName("UTF-8");
            }

            String result = EntityUtils.toString(entity, charset);
            return result;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return "";
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * post方式上传文件到文件中心
     *
     * @param uri             上传http接口url
     * @param fileInputStream 要上传的文件流
     * @param charsetName     要上传文件的字符编码
     * @return 返回上传结果
     * @throws IOException
     * @see ClientProtocolException
     */
    public static String uploadToRemote(String uri,
                                        Header[] headers,
                                        InputStream fileInputStream,
                                        String charsetName,
                                        String ip,
                                        int port)
            throws IOException {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse httpResponse = null;
        try {
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .setCharset(Charset.forName(charsetName));
            multipartEntityBuilder.addBinaryBody("file",
                    fileInputStream,
                    ContentType.MULTIPART_FORM_DATA.withCharset(charsetName),
                    "file0");

            HttpEntity httpEntity = multipartEntityBuilder.build();
            RequestBuilder requestBuilder = RequestBuilder.post();
            requestBuilder.setUri(uri).setEntity(httpEntity);
            HttpUriRequest httpRequest = requestBuilder.build();
            if (headers != null && headers.length > 0) {
                httpRequest.setHeaders(headers);
            }

            HttpHost httpHost = ip != null && ip.length() > 0 ? new HttpHost(ip, port) : null;
            httpClient = HttpClientBuilder.create().setProxy(httpHost).build();
            httpResponse = httpClient.execute(httpRequest);
            return EntityUtils.toString(httpResponse.getEntity(), Charset.forName(charsetName));
        } finally {
            if (httpResponse != null) {
                try {
                    httpResponse.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    public static String uploadToRemote(String uri, Header[] headers, InputStream fileInputStream, String charsetName)
            throws IOException {
        return uploadToRemote(uri, headers, fileInputStream, charsetName, null, 0);
    }
}
