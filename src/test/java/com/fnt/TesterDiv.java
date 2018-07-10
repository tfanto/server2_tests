package com.fnt;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.EncryptionMethod;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEHeader;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.RSADecrypter;
import com.nimbusds.jose.crypto.RSAEncrypter;
import com.nimbusds.jwt.EncryptedJWT;
import com.nimbusds.jwt.JWTClaimsSet;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

public class TesterDiv {

	private static final String LOGIN_END_POINT = "http://localhost:8080/auth/rest/login";

	private Client client;

	public TesterDiv() {
		client = ClientBuilder.newClient();
		client.register(new ContextResolver<ObjectMapper>() {
			@Override
			public ObjectMapper getContext(Class<?> type) {
				ObjectMapper mapper = new ObjectMapper();
				mapper.registerModule(new JavaTimeModule());
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				return mapper;
			}
		});
	}

	@Test
	public void testCreateJWE() throws KeyLengthException, JsonProcessingException, JOSEException {

		String aJWE = createJWE();
		System.out.println(aJWE);

	}

	@Test
	public void verifyJwtFunctionality() {

		Key keyForTest = MacProvider.generateKey(SignatureAlgorithm.HS512);

		try {
			byte[] byteArray = Helpers.key2bytes(keyForTest);
			Key key = Helpers.bytes2key(byteArray);
			String compactJws = Helpers.createJWT(key);
			if (Helpers.verifyJWT(key, compactJws)) {
				Assert.assertTrue(true);
			} else {
				Assert.assertTrue(false);
			}
			Assert.assertTrue(Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws).getBody().getSubject()
					.equals("fantoApplications"));

		} catch (RuntimeException e) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void verifyJwtFunctionality2() {

		Key keyForTest = MacProvider.generateKey(SignatureAlgorithm.HS512);

		try {
			byte[] byteArray = Helpers.key2bytes(keyForTest);
			String asString = new String(byteArray, "UTF-8");

			Key key = Helpers.bytes2key(asString.getBytes());
			String compactJws = Helpers.createJWT(key);
			if (Helpers.verifyJWT(key, compactJws)) {
				Assert.assertTrue(true);
			} else {
				Assert.assertTrue(false);
			}
			Assert.assertTrue(Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws).getBody().getSubject()
					.equals("fantoApplications"));

		} catch (RuntimeException | UnsupportedEncodingException e) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void verifyJwtFunctionality3() {

		Key keyForTest = MacProvider.generateKey(SignatureAlgorithm.HS512);

		Map<String, Object> claims = new HashMap<>();
		claims.put("user", "GTFA");
		claims.put("roles", "ADMIN,USER");

		try {
			byte[] byteArray = Helpers.key2bytes(keyForTest);
			Key key = Helpers.bytes2key(byteArray);
			String compactJws = Helpers.createJWT(key, "DonaldDuck", claims);
			System.out.println(compactJws);
			if (Helpers.verifyJWT(key, compactJws)) {
				Assert.assertTrue(true);
			} else {
				Assert.assertTrue(false);
			}
			Assert.assertTrue(Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws).getBody().getSubject()
					.equals("DonaldDuck"));

		} catch (RuntimeException e) {
			Assert.assertTrue(false);
		}
	}

	@Test
	public void verifyJwtFunctionality4() throws Exception {

		JwtUtil.setUp("my_secret_key".getBytes());

		JwtUtil jwt = JwtUtil.getInstance();

		Map<String, Object> claimsIn = new HashMap<String, Object>();
		claimsIn.put("UID", "LTFA");
		claimsIn.put("ROLES", "ADMIN,USER,HJON");

		String encodedJwt = jwt.createEncodedJwt("issuer", "theHost", "app", "access", claimsIn);
		Map<String, Object> claims = jwt.getClaims(encodedJwt);

		System.out.println(claims);

	}

	@Test
	public void verifyJweFunctionality() throws Exception {

		KeyPairGenerator keyPairGenerator;
		EncryptionMethod encryptionMethod = EncryptionMethod.A128GCM;

		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");

			@SuppressWarnings("unused")
			int len = encryptionMethod.cekBitLength();
			// keyPairGenerator.initialize(2048);

			KeyPair keyPair = keyPairGenerator.genKeyPair();

			// create KeyFactory and RSA Keys Specs
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			// keyFactory.

			RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
			RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(keyPair.getPrivate(), RSAPrivateKeySpec.class);

			// generate (and retrieve) RSA Keys from the KeyFactory using Keys Specs
			RSAPublicKey publicRsaKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
			RSAPrivateKey privateRsaKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);
			// byte[] k = privateRsaKey.getEncoded();

			JWTClaimsSet.Builder claimsSet = new JWTClaimsSet.Builder();
			claimsSet.issuer("https://www.fanto.se");
			claimsSet.subject("Thomas Fanto");
			claimsSet.audience(getAudience());
			claimsSet.issueTime(new Date());
			claimsSet.expirationTime(new Date(new Date().getTime() + 1000 * 60 * 10));
			claimsSet.notBeforeTime(new Date());
			claimsSet.jwtID(UUID.randomUUID().toString());
			claimsSet.claim("UID", "MyApplicationUID");
			claimsSet.claim("ROLES", "ADMIN,USER,HJON");

			// create the JWT header and specify:
			// RSA-OAEP as the encryption algorithm
			// 128-bit AES/GCM as the encryption method
			// JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP,
			// EncryptionMethod.A128GCM);
			JWEHeader header = new JWEHeader(JWEAlgorithm.RSA_OAEP_256, encryptionMethod);

			// create the EncryptedJWT object
			EncryptedJWT jwt = new EncryptedJWT(header, claimsSet.build());

			// create an RSA encrypter with the specified public RSA key
			RSAEncrypter encrypter = new RSAEncrypter(publicRsaKey);

			// do the actual encryption
			jwt.encrypt(encrypter);

			// serialize to JWT compact form
			String jwtString = jwt.serialize();
			System.out.println(jwtString);

			// in order to read back the data from the token using your private RSA key:
			// parse the JWT text string using EncryptedJWT object
			EncryptedJWT jwt2 = EncryptedJWT.parse(jwtString);

			// create a decrypter with the specified private RSA key
			RSADecrypter decrypter = new RSADecrypter(privateRsaKey);

			// do the decryption
			jwt2.decrypt(decrypter);

			// print out the claims

			System.out.println("===========================================================");
			System.out.println("Issuer           : " + jwt2.getJWTClaimsSet().getIssuer());
			System.out.println("Subject          : " + jwt2.getJWTClaimsSet().getSubject());
			System.out.println("Audience size    : " + jwt2.getJWTClaimsSet().getAudience().size());
			System.out.println("Expiration Time  : " + jwt2.getJWTClaimsSet().getExpirationTime());
			System.out.println("Not Before Time  : " + jwt2.getJWTClaimsSet().getNotBeforeTime());
			System.out.println("Issue At         : " + jwt2.getJWTClaimsSet().getIssueTime());
			System.out.println("JWT ID           : " + jwt2.getJWTClaimsSet().getJWTID());
			System.out.println("App user         : " + jwt2.getJWTClaimsSet().getStringClaim("UID"));
			System.out.println("User roles       : " + jwt2.getJWTClaimsSet().getStringClaim("ROLES"));
			System.out.println("===========================================================");

			String json = jwt2.getJWTClaimsSet().toJSONObject().toJSONString();
			System.out.println(json);

		} catch (NoSuchAlgorithmException | InvalidKeySpecException | JOSEException | ParseException e) {
			System.out.println(e.getMessage());
		}

	}

	@Test
	public void verifyJweFunctionality2() throws Exception {

		/////////////////////////////////
		// Set up and create the key
		/////////////////////////////////

		EncryptionMethod encryptionMethod = EncryptionMethod.A128CBC_HS256;

		String base64EncodedKeyString = createKey();
		// String base64EncodedKeyString =
		// "QV2V+V4UTFntDIUXJOr0GQje+PWgiS20G+4ekoUkl18=";

		byte[] key = Base64.getDecoder().decode(base64EncodedKeyString.getBytes());

		/////////////////////////////////
		// Encrypt
		/////////////////////////////////

		// Create the header
		JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, encryptionMethod);

		// Set the plain text
		Payload payload = createPayLoad();

		// Create the JWE object and encrypt it
		JWEObject jweObject = new JWEObject(header, payload);

		jweObject.encrypt(new DirectEncrypter(key));

		// Serialise to compact JOSE form...
		String jweString = jweObject.serialize();

		System.out.println(jweString);

		/////////////////////////////////
		// Decrypt
		/////////////////////////////////

		System.out.println(decrypt(base64EncodedKeyString, jweString));

	}

	private String createJWE() throws JsonProcessingException, KeyLengthException, JOSEException {

		Response response = client.target(LOGIN_END_POINT).path("thomas@fanto.se")
				.path("myPassword").request(MediaType.APPLICATION_JSON).get(Response.class);

		if (response.getStatus() == 200) {

			String jwe = response.getHeaderString("Authorization");
			if (jwe == null) {
				Assert.fail();
			}
			return jwe;

		} else if (response.getStatus() == 404) {
			assertTrue(true);
		} else {
			Assert.fail();
		}

		return "";
	}

	@SuppressWarnings("unused")
	private String createKey() throws NoSuchAlgorithmException {

		EncryptionMethod encryptionMethod = EncryptionMethod.A128CBC_HS256;
		int keyBitLength = encryptionMethod.cekBitLength();
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(keyBitLength);
		SecretKey key = keyGen.generateKey();
		byte[] byteArray = key.getEncoded();

		String base64EncodedKeyString = Base64.getEncoder().encodeToString((byteArray));
		System.out.println(base64EncodedKeyString);
		return base64EncodedKeyString;
	}

	private String decrypt(String base64EncodedKeyString, String jweString) throws ParseException, JOSEException {

		JWEObject jweObject2 = JWEObject.parse(jweString);

		byte[] decodedKey = Base64.getDecoder().decode(base64EncodedKeyString.getBytes());
		DirectDecrypter directDecrypter = new DirectDecrypter(decodedKey);

		jweObject2.decrypt(directDecrypter);

		// Get the plain text
		Payload payload2 = jweObject2.getPayload();
		return payload2.toString();

	}

	private Payload createPayLoad() throws JsonProcessingException {

		ObjectMapper MAPPER = new ObjectMapper();

		Map<String, Object> claimsSet = new HashMap<>();
		claimsSet.put("issuer", "https://www.fanto.se");
		claimsSet.put("subject", "Thomas Fanto");
		claimsSet.put("issueTime", new Date());
		claimsSet.put("expirationTime", new Date(new Date().getTime() + 1000 * 60 * 10));
		claimsSet.put("notBeforeTime", new Date());
		claimsSet.put("jwtID", UUID.randomUUID().toString());
		claimsSet.put("UID", "MyApplicationUID");
		claimsSet.put("ROLES", "ADMIN,USER,HJON");

		String json = MAPPER.writeValueAsString(claimsSet);

		return new Payload(json);

	}

	private static List<String> getAudience() {
		List<String> audience = new ArrayList<>();
		audience.add("https://infor.com");
		audience.add("https://havochvatten.se");
		audience.add("https://consid.se");
		return audience;
	}

	@Test
	public void loadKey() {
		
		loadKeyFromStore("C:\\nycklar\\keystore", "scem1000Ay");
		
	}
	private void  loadKeyFromStore(String fileName, String password) {
		
		// HMac-SHA256  key size = 256

		try {
			KeyStore keyStore = KeyStore.getInstance("JCEKS");

			File keyStoreFile = new File(fileName);
			
			FileInputStream keyStoreStream = new FileInputStream(keyStoreFile);
			
			keyStore.load(keyStoreStream, password.toCharArray());
			keyStoreStream.close();
			
			Key key = keyStore.getKey("hs256", password.toCharArray());
			
			
			/////////////////////////////////
			// Encrypt  
			/////////////////////////////////

			// Create the header
			EncryptionMethod encryptionMethod = EncryptionMethod.A128CBC_HS256;

			JWEHeader header = new JWEHeader(JWEAlgorithm.DIR, encryptionMethod);

			// Set the plain text
			Payload payload = createPayLoad();

			// Create the JWE object and encrypt it
			JWEObject jweObject = new JWEObject(header, payload);

			jweObject.encrypt(new DirectEncrypter((SecretKey) key));

			// Serialise to compact JOSE form...
			String jweString = jweObject.serialize();

			System.out.println(jweString);
			
			
			
			/////////////////////////////////
			// Decrypt
			/////////////////////////////////

			JWEObject jweObject2 = JWEObject.parse(jweString);

			DirectDecrypter directDecrypter = new DirectDecrypter((SecretKey) key);

			jweObject2.decrypt(directDecrypter);

			// Get the plain text
			Payload payload2 = jweObject2.getPayload();
			System.out.println(payload2.toString());
			
			
			
			
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyLengthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// TypeReference typeref = new TypeReference<List<CustomCode>>() {};
	// List<CustomCode> codes = mapper.readValue(json, typeref); return codes;

}
