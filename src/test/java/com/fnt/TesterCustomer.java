package com.fnt;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
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
import com.fnt.entity.Customer;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;

public class TesterCustomer {

	private static final String REST_CUSTOMER_END_POINT = "http://localhost:8080/javaee7/rest/customer";
	private static final String LOGIN_END_POINT = "http://localhost:8080/auth/rest/login";

	@SuppressWarnings("unused")
	private Random rnd = new Random();

	private int OK = 200;
	private int FORBIDDEN = 403;
	private int NOT_FOUND = 404;
	private int PRECONDITION_FAILED = 412;

	String uuu = "thomas@fanto.se";
	String ppp = "myPassword";

	private Client client;

	public TesterCustomer() {
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

	String itemNumber = "";

	@Before
	public void before() {

		int i = 1;
		itemNumber = String.format("%05d", i);

	}

	@Test
	public void testCreateJWE() throws KeyLengthException, JsonProcessingException, JOSEException {

		@SuppressWarnings("unused")
		String jwe = getJWEFromSecurityServer(uuu, ppp);
	}

	@SuppressWarnings("unused")
	@Test
	public void create5000() throws KeyLengthException, JsonProcessingException, JOSEException {

		String jwe = getJWEFromSecurityServer(uuu, ppp);

		Response responseDelete = client.target(REST_CUSTOMER_END_POINT).path("all").request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).delete(Response.class);

		for (int i = 0; i < 50000; i++) {

			Customer customer = new Customer();

			String nbr = String.format("%05d", i);
			customer.setId("CUNO_" + nbr);
			customer.setName("Name_" + i);

			Response response = client.target(REST_CUSTOMER_END_POINT).request(MediaType.APPLICATION_JSON)
					.header("Authorization", jwe).post(Entity.json(customer), Response.class);

			int status = response.getStatus();
			if (status == OK) {
				Customer aCreatedItem = response.readEntity(new GenericType<Customer>() {
				});
			} else if (status == FORBIDDEN) {
				Assert.fail();
			} else if (status == PRECONDITION_FAILED) {
				Assert.fail();
			} else {
				Assert.fail();
			}
		}
	}

	private Customer createCustomerHelper() {
		Customer customer = new Customer();
		customer.setId("CUNO_" + UUID.randomUUID().toString());
		customer.setName("A high valued customer");
		return customer;

	}

	@Test
	public void create() throws KeyLengthException, JsonProcessingException, JOSEException {

		String jwe = getJWEFromSecurityServer(uuu, ppp);

		Response responseDelete = client.target(REST_CUSTOMER_END_POINT).path("all").request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).delete(Response.class);

		Customer customer = createCustomerHelper();

		Response response = client.target(REST_CUSTOMER_END_POINT).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).post(Entity.json(customer), Response.class);

		int status = response.getStatus();
		if (status == OK) {
			Customer aCreatedItem = response.readEntity(new GenericType<Customer>() {
			});
		} else if (status == FORBIDDEN) {
			Assert.fail();
		} else if (status == PRECONDITION_FAILED) {
			Assert.fail();
		} else {
			Assert.fail();
		}
	}

	@Test
	public void update() throws KeyLengthException, JsonProcessingException, JOSEException {

		String jwe = getJWEFromSecurityServer(uuu, ppp);

		// remove if exists
		client.target(REST_CUSTOMER_END_POINT).path(itemNumber).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).delete(Response.class);

		// and add so we can update
		Customer customer = createCustomerHelper();

		client.target(REST_CUSTOMER_END_POINT).request(MediaType.APPLICATION_JSON).header("Authorization", jwe)
				.post(Entity.json(customer), Response.class);

		Customer itemWithNewData = createCustomerHelper();
		itemWithNewData.setName("CHANGED NAME");

		Response response = client.target(REST_CUSTOMER_END_POINT).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).put(Entity.json(itemWithNewData), Response.class);

		int status = response.getStatus();
		if (status == OK) {

			if (status == OK) {
				Customer aCreatedItem = response.readEntity(new GenericType<Customer>() {
				});
			}

		} else if (status == FORBIDDEN) {
			Assert.fail();
		} else if (status == PRECONDITION_FAILED) {
			Assert.fail();
		} else {
			Assert.fail();
		}
	}

	@Test
	public void delete() throws KeyLengthException, JsonProcessingException, JOSEException {

		String jwe = getJWEFromSecurityServer(uuu, ppp);

		// remove if exists
		client.target(REST_CUSTOMER_END_POINT).path(itemNumber).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).delete(Response.class);

		// and add so we can update
		Customer customer = createCustomerHelper();

		client.target(REST_CUSTOMER_END_POINT).request(MediaType.APPLICATION_JSON).header("Authorization", jwe)
				.post(Entity.json(customer), Response.class);

		Response response = client.target(REST_CUSTOMER_END_POINT).path(itemNumber).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).delete(Response.class);

		int status = response.getStatus();
		if (status == OK) {
			assertTrue(response.getStatus() == 200);
		} else if (status == FORBIDDEN) {
			Assert.fail();
		} else if (status == PRECONDITION_FAILED) {
			Assert.fail();
		}
	}

	@Test
	public void getAll() throws KeyLengthException, JsonProcessingException, JOSEException {

		String jwe = getJWEFromSecurityServer(uuu, ppp);

		Response response = client.target(REST_CUSTOMER_END_POINT).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).get(Response.class);

		int status = response.getStatus();
		if (status == OK) {
			List<Customer> theList = response.readEntity(new GenericType<List<Customer>>() {
			});
			assertTrue(theList != null);
		} else if (status == FORBIDDEN) {
			Assert.fail();
		} else if (status == PRECONDITION_FAILED) {
			Assert.fail();
		} else {
			Assert.fail();
		}
	}

	@Test
	public void getAllCustomerIDS() throws KeyLengthException, JsonProcessingException, JOSEException {

		String jwe = getJWEFromSecurityServer(uuu, ppp);

		Response response = client.target(REST_CUSTOMER_END_POINT).path("ids").request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).get(Response.class);

		int status = response.getStatus();
		if (status == OK) {
			List<String> theList = response.readEntity(new GenericType<List<String>>() {
			});
			assertTrue(theList != null);
		} else if (status == FORBIDDEN) {
			Assert.fail();
		} else if (status == PRECONDITION_FAILED) {
			Assert.fail();
		} else {
			Assert.fail();
		}
	}

	@Test
	public void get() throws KeyLengthException, JsonProcessingException, JOSEException {

		String jwe = getJWEFromSecurityServer(uuu, ppp);

		// remove if exists
		client.target(REST_CUSTOMER_END_POINT).path(itemNumber).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).delete(Response.class);

		// and add so we can update
		Customer customer = createCustomerHelper();

		client.target(REST_CUSTOMER_END_POINT).request(MediaType.APPLICATION_JSON).header("Authorization", jwe)
				.post(Entity.json(customer), Response.class);

		Response response = client.target(REST_CUSTOMER_END_POINT).path(customer.getId())
				.request(MediaType.APPLICATION_JSON).header("Authorization", jwe).get(Response.class);

		int status = response.getStatus();
		if (status == OK) {
			Customer aService = response.readEntity(new GenericType<Customer>() {
			});

			assertTrue(aService != null);

		} else if (status == FORBIDDEN) {
			Assert.fail();
		} else if (status == NOT_FOUND) {
			Assert.fail();
		} else if (status == PRECONDITION_FAILED) {
			Assert.fail();
		} else {
			Assert.fail();
		}
	}

	private String getJWEFromSecurityServer(String login, String password)
			throws JsonProcessingException, KeyLengthException, JOSEException {

		Response response = client.target(LOGIN_END_POINT).path(login).path(password)
				.request(MediaType.APPLICATION_JSON).get(Response.class);

		if (response.getStatus() == 200) {

			String jwe = response.getHeaderString("Authorization");
			if (jwe == null) {
				Assert.fail();
			}
			return jwe;
		} else if (response.getStatus() == 404) {
			Assert.fail();
			return "";
		} else {
			Assert.fail();
			return "";

		}
	}

	// TypeReference typeref = new TypeReference<List<CustomCode>>() {};
	// List<CustomCode> codes = mapper.readValue(json, typeref); return codes;

}
