package com.fnt;

import java.util.Random;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;

public class TesterUser {

	private Random rnd = new Random();
	private static final String USER_REGISTRARTION_END_POINT = "http://localhost:8080/auth/rest/user";

	int selectedLogin = 0;
	int selectedRole = 0;

	String logins[] = { "thomas@fanto.se", "annalena@fanto.se", "madeleine@fanto.se", };
	String pwds[] = { "myPassword", "myAnnaLenaPassword", "myMadeleinePassword", };
	String roles[] = { "ADMIN", "USER", "GUEST", };

	private int OK = 200;
	private int FORBIDDEN = 403;
	// private int NOT_FOUND = 404;
	private int PRECONDITION_FAILED = 412;

	private Client client;

	public TesterUser() {
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

	@Before
	public void before() {

		selectedLogin = rnd.nextInt(logins.length);
		selectedRole = rnd.nextInt(roles.length);

	}

	@Test
	public void deleteUser() throws KeyLengthException, JsonProcessingException, JOSEException {

		String login = logins[selectedLogin];

		// remove if exists
		client.target(USER_REGISTRARTION_END_POINT).path(login).request(MediaType.APPLICATION_JSON).delete(Response.class);

	}

	@Test
	public void createUser() throws KeyLengthException, JsonProcessingException, JOSEException {

		String login = logins[selectedLogin];
		String password = pwds[selectedLogin];

		client.target(USER_REGISTRARTION_END_POINT).path(login).request(MediaType.APPLICATION_JSON).delete(Response.class);

		Response response = client.target(USER_REGISTRARTION_END_POINT).path(login).path(password)
				.request(MediaType.APPLICATION_JSON).post(null, Response.class);

		int status = response.getStatus();
		if (status == OK) {
			Assert.assertTrue(status == 200);
		} else if (status == FORBIDDEN) {
			Assert.fail();
		} else if (status == PRECONDITION_FAILED) {
			Assert.fail();
		} else {
			Assert.fail();
		}
	}

	@Test
	public void createUserDontDeleteFirst() throws KeyLengthException, JsonProcessingException, JOSEException {

		String login = logins[selectedLogin];
		String password = pwds[selectedLogin];

		Response response = client.target(USER_REGISTRARTION_END_POINT).path(login).path(password)
				.request(MediaType.APPLICATION_JSON).post(null, Response.class);

		int status = response.getStatus();
		if ((status == OK) || (status == 500)) {
			Assert.assertTrue((status == OK) || (status == 500));
		} else if (status == FORBIDDEN) {
			Assert.fail();
		} else if (status == PRECONDITION_FAILED) {
			Assert.fail();
		} else {
			Assert.fail();
		}
	}

	@Test
	public void createUserAndRolesForTesting() throws KeyLengthException, JsonProcessingException, JOSEException {

		int nLogins = logins.length;
		int nRoles = roles.length;

		for (int l = 0; l < nLogins; l++) {
			String login = logins[l];
			String password = pwds[l];

			Response response = client.target(USER_REGISTRARTION_END_POINT).path(login).path(password)
					.request(MediaType.APPLICATION_JSON).post(null, Response.class);

			for (int r = 0; r < nRoles; r++) {

				String role = roles[r];
				Response response2 = client.target(USER_REGISTRARTION_END_POINT).path(login).path(role).path("aDescription")
						.request(MediaType.APPLICATION_JSON).post(null, Response.class);
			}
		}
	}

	@Test
	public void createUserRole() throws KeyLengthException, JsonProcessingException, JOSEException {

		String login = logins[selectedLogin];
		String password = pwds[selectedLogin];
		String ROLE = roles[selectedRole];

		// Response response0 =
		// client.target(LOGIN_END_POINT).path(login).path(password)
		// .request(MediaType.APPLICATION_JSON).post(null, Response.class);

		Response response = client.target(USER_REGISTRARTION_END_POINT).path(login).path(ROLE).path("aDescription")
				.request(MediaType.APPLICATION_JSON).post(null, Response.class);

		int status = response.getStatus();
		if (status == OK) {
			// String jweString = response.getHeaderString("Authorization");
			// assertTrue(jweString != null);
		} else if (status == FORBIDDEN) {
			Assert.fail();
		} else if (status == PRECONDITION_FAILED) {
			Assert.fail();
		} else {
			Assert.fail();
		}
	}

	// TypeReference typeref = new TypeReference<List<CustomCode>>() {};
	// List<CustomCode> codes = mapper.readValue(json, typeref); return codes;

}
