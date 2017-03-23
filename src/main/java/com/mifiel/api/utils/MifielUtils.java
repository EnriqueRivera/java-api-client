package com.mifiel.api.utils;

import java.io.FileInputStream;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import com.mifiel.api.exception.MifielException;

public final class MifielUtils {
	private final static ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
	private final static ObjectMapper objectMapper = new ObjectMapper();
	
	private MifielUtils() {}
	
	public static String getDocumentHash(final String filePath) throws MifielException {
		try {
			byte[] fileContent = IOUtils.toByteArray(new FileInputStream(filePath));
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			digest.update(fileContent);
			return new String(Base64.encodeBase64(digest.digest()));
		} catch (final Exception e) {
			throw new MifielException("Error calculating Hash(SHA-256)", e);
		}
	}
	
	public static String calculateHMAC(final String secret, final String data, 
										final String algorithm) throws MifielException {
		try {
			final SecretKeySpec signingKey = new SecretKeySpec(secret.getBytes(), algorithm);
			final Mac mac = Mac.getInstance(algorithm);
			mac.init(signingKey);
			final byte[] rawHmac = mac.doFinal(data.getBytes());
			return new String(Base64.encodeBase64(rawHmac));
		} catch (final GeneralSecurityException e) {
			throw new MifielException("Error calculating HMAC", e);
		}
	}
	
	public static String calculateMD5(final String content) throws MifielException {
		try {
			final MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(content.getBytes());
			return new String(Base64.encodeBase64(digest.digest()));
		} catch (final NoSuchAlgorithmException e) {
			throw new MifielException("Error calculating MD5", e);
		}
	}

	public static String convertObjectToJson(final Object object) throws MifielException {
		try {
			return objectWriter.writeValueAsString(object);
		} catch (final Exception e) {
			throw new MifielException("Error converting object to JSON", e);
		}
	}
	
	public static Object convertJsonToObject(final String json, final String className) throws MifielException {
		try {
			return objectMapper.readValue(json, Class.forName(className));
		} catch (final Exception e) {
			throw new MifielException("Error converting JSON to Object", e);
		}
	}
	
	public static List<Object> convertJsonToObjects(final String json, final String className) throws MifielException {
		try {
			return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Class.forName(className)));
		} catch (final Exception e) {
			throw new MifielException("Error converting JSON to List<Object>", e);
		}
	}
	
	public static void appendParamToHttpBody(final StringBuilder httpBody, 
											final String paramName, final String paramValue) 
											throws MifielException  {
		try {
			final String separator = httpBody.length() == 0 ? "" : "&";
			if (!StringUtils.isEmpty(paramValue)) {
				httpBody.append(separator + paramName + "=" + paramValue);//URLEncoder.encode(paramValue, "UTF-8"));
			}
		} catch (final Exception e) {
			throw new MifielException("Error appending param to HTTP body", e);
		}
	}
}