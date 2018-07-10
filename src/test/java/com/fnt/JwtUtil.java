package com.fnt;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

import io.jsonwebtoken.SignatureException;

/**
 * <dependency> <groupId>com.auth0</groupId> <artifactId>java-jwt</artifactId>
 * <version>2.1.0</version> </dependency> </dependencies>
 * 
 */
public class JwtUtil {

	private static JwtUtil _instance = null;
	private static String jwtSecretKey;
	private static JWTSigner signer;
	private static JWTVerifier verifier;

	private static final String TOKEN_TYPE = "fnttokentype";
	private static final String CLIENT_ID = "client";
	private static final String HOST = "host";
	private static final String issuer = "Fanto Software";
	private static final Integer tokenValidForMinutes = 60 * 24;

	private void LOGGER(String s) {
		System.out.println(s);

	}

	private void LOGGER(String s, Throwable e) {
		LOGGER(s + "     "  +  e.toString());

	}

	private JwtUtil() {

	}

	/*
	 * public static synchronized void setUp(String jwtSecretKeyPath) throws
	 * Exception {
	 * 
	 * if (_instance == null) { _instance = new JwtUtil();
	 * 
	 * FileReader fileReader = new FileReader(jwtSecretKeyPath); byte[] bytes =
	 * IOUtils.toByteArray(fileReader.readFile()); jwtSecretKey = new String(bytes);
	 * fileReader.close();
	 * 
	 * signer = new JWTSigner(jwtSecretKey); verifier = new
	 * JWTVerifier(jwtSecretKey); } }
	 */

	public static synchronized void setUp(byte[] bytes) throws Exception {

		if (_instance == null) {
			_instance = new JwtUtil();

			jwtSecretKey = new String(bytes);

			signer = new JWTSigner(jwtSecretKey);
			verifier = new JWTVerifier(jwtSecretKey);
		}
	}

	public static synchronized JwtUtil getInstance() {
		if (_instance == null) {
			throw new RuntimeException("Run setup first");
		}
		return _instance;
	}

	public String createEncodedJwt(String userId, String host, String clientId, String accessType,
			Map<String, Object> customClaims) {

		final Map<String, Object> claims = new HashMap<>();
		claims.put(TOKEN_TYPE, accessType);
		claims.put(CLIENT_ID, clientId);
		claims.put(HOST, host);
		claims.put("_id", userId);

		final long iat = System.currentTimeMillis() / 1000l; // issued at claim
		final long exp = iat + tokenValidForMinutes * 60L; // expires claim. In this case the token expires in 60
															// minutes

		claims.put("iss", issuer);
		claims.put("exp", exp);
		claims.put("iat", iat);

		if (customClaims != null) {
			customClaims.entrySet().forEach(entry -> claims.putIfAbsent(entry.getKey(), entry.getValue()));
		}

		final String jwt = signer.sign(claims);

		return jwt;
	}

	public String createEncodedJwt(String userId, String host, String clientId, String accessType) {
		return createEncodedJwt(userId, host, clientId, accessType, null);
	}

	public Map<String, Object> getClaims(String encodedJwt) {
		if (encodedJwt == null || encodedJwt.isEmpty()) {
			return null;
		}

		try {
			final Map<String, Object> claims = verifier.verify(encodedJwt);
			if (claims == null) {
				return null;
			}

			LOGGER("Claims: " + claims);
			String id = (String) claims.get("_id");
			String host = (String) claims.get(HOST);

			boolean tokenIsValid = false;
			if (JwtUtil.getInstance().tokenIsExpired(claims)) {
				LOGGER("Token expired");
			} else if (id == null || id.isEmpty()) {
				LOGGER("_id missing in claim");
			} else if (host == null || host.isEmpty()) {
				LOGGER("email missing in claim");
			} else {
				tokenIsValid = true;
			}

			if (tokenIsValid) {
				String[] accessTokenArray = encodedJwt.split("\\.");
				LOGGER("USER:" + id + " - SIGNATURE:" + accessTokenArray[2]);
				return claims;
			} else {
				return null;
			}
		} catch (IllegalStateException e) {
			LOGGER("Unable to verity jwt token", e);
		} catch (NoSuchAlgorithmException e) {
			LOGGER("User is not authorized, NoSuchAlgorithmException", e);
		} catch (InvalidKeyException e) {
			LOGGER("User is not authorized, InvalidKeyException", e);
		} catch (IOException e) {
			LOGGER("User is not authorized, IOException", e);
		} catch (SignatureException e) {
			LOGGER("User is not authorized, SignatureException", e);
		} catch (JWTVerifyException e) {
			LOGGER("User is not authorized, JWTVerifyException", e);
		} catch (Throwable t) {
			LOGGER("Unknown exception in jwt validation", t);
		}
		return null;
	}

	public boolean tokenIsExpired(Map<String, Object> claims) {
		long now = System.currentTimeMillis() / 1000l; // issued at claim
		long exp = ((Number) claims.get("exp")).longValue();

		return (now > exp);

	}

	public boolean tokenIsExpired(String jwtEncrypted)
			throws NoSuchAlgorithmException, SignatureException, JWTVerifyException, InvalidKeyException, IOException {
		return tokenIsExpired(getClaims(jwtEncrypted));
	}
}
