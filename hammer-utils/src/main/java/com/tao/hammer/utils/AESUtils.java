package com.tao.hammer.utils;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author tyq
 * @version 1.0, 2017/11/2
 */
public class AESUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AESUtils.class);

    private static final String ENCRYPT_ALGORITHM = "AES";
    private static final String ENCODING = "UTF-8";

    /**
     * 加密
     * @param content
     * @param encryptKey
     * @return
     */
    public static String encrypt(String content, String encryptKey) {
        try {
            SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(ENCODING), ENCRYPT_ALGORITHM);
            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
            byte[] byteContent = content.getBytes(ENCODING);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] result = cipher.doFinal(byteContent);
            return Hex.encodeHexString(result);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 解密
     * @param content
     * @param encryptKey
     * @return
     */
    public static String decrypt(String content, String encryptKey) {
        try {
            SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(ENCODING), ENCRYPT_ALGORITHM);

            byte[] byteContent = content.getBytes(ENCODING);
            Hex hex = new Hex(ENCODING);
            byteContent = hex.decode(byteContent);

            Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] result = cipher.doFinal(byteContent);
            return new String(result, ENCODING);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
