package com.fnt;

import java.security.Key;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;

public class Helpers {

	public static String createJWT(Key key) {

		String compactJws = Jwts.builder().setSubject("fantoApplications").signWith(SignatureAlgorithm.HS512, key).compact();
		return compactJws;
	}
	
	
	public static String createJWT(Key key, String subject, Map<String,Object> claims) {

		String compactJws = Jwts.builder().setSubject(subject).addClaims(claims).signWith(SignatureAlgorithm.HS512, key).compact();
		return compactJws;
	}

	public static Boolean verifyJWT(Key key, String compactJws) {

		try {
			Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws);
			return true;
		} catch (SignatureException e) {
			return false;
		}
	}

	public static byte[] key2bytes(Key key)  {
		return key.getEncoded();
	}

	public static Key bytes2key(byte[] theKey) {
		return new SecretKeySpec(theKey, "HS512");
	}

}
