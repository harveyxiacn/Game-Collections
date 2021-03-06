package com.itgarage.harvey.gamecollections.amazon_product_advertising_api;

//import android.util.Base64;

import android.util.Log;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class is a helper helps to sign the URL prepare to use.
 */
public class SignedRequestsHelper {
    private static final String UTF8_CHARSET = "UTF-8";
    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
    private static final String REQUEST_URI = "/onca/xml";
    private static final String REQUEST_METHOD = "GET";

    private String endpoint = "ecs.amazonaws.com"; // must be lowercase

    // change this for the keys, get from aws console, use root account to generate. IAM is unavailable.
    // change in Keys_Tag class
    private String awsAccessKeyId = Keys_Tag.AWS_ACCESS_KEY_ID;
    private String awsSecretKey = Keys_Tag.AWS_SECRET_KEY;

    private SecretKeySpec secretKeySpec = null;
    private Mac mac = null;


    public SignedRequestsHelper() throws UnsupportedEncodingException, NoSuchAlgorithmException, InvalidKeyException {
        byte[] secretyKeyBytes = awsSecretKey.getBytes(UTF8_CHARSET);
        secretKeySpec =
                new SecretKeySpec(secretyKeyBytes, HMAC_SHA256_ALGORITHM);
        mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
        mac.init(secretKeySpec);
    }

    /**
     * Sign the url and pass to Parser's getResponceNodeList function as a param.
     * @param params Pass a Map object with params from UrlParameterHandler's functions buildMapForItemSearch or buildMapForItemSearch
     * @return A signed URL that can get result from Amazon Product Advertising API.
     */
    public String sign(Map<String, String> params) {
        params.put("AWSAccessKeyId", awsAccessKeyId);
        params.put("Timestamp", timestamp());

        SortedMap<String, String> sortedParamMap =
                new TreeMap<String, String>(params);
        String canonicalQS = canonicalize(sortedParamMap);
        String toSign =
                REQUEST_METHOD + "\n"
                        + endpoint + "\n"
                        + REQUEST_URI + "\n"
                        + canonicalQS;
        //Log.i("url", "toSign: " + toSign);
        String hmac = hmac(toSign);
        //Log.i("signature", ""+hmac);
        String sig = percentEncodeRfc3986(hmac);
        //Log.i("encoded signature", ""+sig);
        String url = "http://" + endpoint + REQUEST_URI + "?" +
                canonicalQS + "&Signature=" + sig;

        return url;
    }

    /**
     * Encode to get signature.
     * @param stringToSign String that is used for encode.
     * @return Encoded signature.
     */
    private String hmac(String stringToSign) {
        String signature = null;
        byte[] data;
        byte[] rawHmac;
        try {
            data = stringToSign.getBytes(UTF8_CHARSET);
            rawHmac = mac.doFinal(data);
            Base64 encoder = new Base64();
            signature = new String(encoder.encode(rawHmac));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(UTF8_CHARSET + " is unsupported!", e);
        }
        //Log.i("url", "signature:"+signature);
        return signature;
    }

    /**
     * Get time stamp.
     * @return Needed time stamp.
     */
    private String timestamp() {
        String timestamp = null;
        Calendar cal = Calendar.getInstance();
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dfm.setTimeZone(TimeZone.getTimeZone("GMT"));
        timestamp = dfm.format(cal.getTime());
        return timestamp;
    }

    /**
     * Order the parameters.
     * @param sortedParamMap Unordered parameters.
     * @return Ordered parameters.
     */
    private String canonicalize(SortedMap<String, String> sortedParamMap)
    {
        if (sortedParamMap.isEmpty()) {
            return "";
        }

        StringBuffer buffer = new StringBuffer();
        Iterator<Map.Entry<String, String>> iter =
                sortedParamMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<String, String> kvpair = iter.next();
            buffer.append(percentEncodeRfc3986(kvpair.getKey()));
            buffer.append("=");
            buffer.append(percentEncodeRfc3986(kvpair.getValue()));
            if(kvpair.getKey().equals("Keywords")){
                Log.i("Keywords", ""+percentEncodeRfc3986(kvpair.getValue()));
            }
            if (iter.hasNext()) {
                buffer.append("&");
            }
        }
        return buffer.toString();
    }

    /**
     * Encode URL.
     * @param s Undone codes.
     * @return Encoded codes.
     */
    private String percentEncodeRfc3986(String s) {
        String out;
        try {
            out = URLEncoder.encode(s, UTF8_CHARSET)
                    .replace("+", "%20")
                    .replace("*", "%2A")
                    .replace("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            out = s;
        }
        return out;
    }
}
