package com.fnt;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;

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
import com.fnt.entity.Item;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;

public class TesterItem {

	private static final String REST_ITEM_END_POINT = "http://localhost:8080/server2/rest/item";
	private static final String LOGIN_END_POINT = "http://localhost:8080/auth/rest/login";

	private Random rnd = new Random();

	private int OK = 200;
	private int FORBIDDEN = 403;
	private int NOT_FOUND = 404;
	private int PRECONDITION_FAILED = 412;

	String uuu = "thomas@fanto.se";
	String ppp = "myPassword";

	private Client client;

	public TesterItem() {
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

		String jwe = getJWEFromSecurityServer(uuu, ppp);
		System.out.println(jwe);
	}

	@Test
	public void create5000() throws KeyLengthException, JsonProcessingException, JOSEException {

		String jwe = getJWEFromSecurityServer(uuu, ppp);

		Response responseDelete = client.target(REST_ITEM_END_POINT).path("all").request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).delete(Response.class);

		for (int i = 0; i < 500; i++) {

			Item item = new Item();

			String nbr = String.format("%05d", i);
			item.setItemnumber("MUT_" + nbr);
			item.setDescription("Mutter");
			int inStock = rnd.nextInt(2000) + 5;
			item.setInStock(inStock);
			item.setOrderingPoint(item.getInStock() / 3);

			double price = (rnd.nextDouble() * 1000) + 15;

			item.setPrice(price);
			item.setPurchasePrice(item.getPrice() / 2);

			Response response = client.target(REST_ITEM_END_POINT).request(MediaType.APPLICATION_JSON)
					.header("Authorization", jwe).post(Entity.json(item), Response.class);

			int status = response.getStatus();
			if (status == OK) {
				Item aCreatedItem = response.readEntity(new GenericType<Item>() {
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

	private Item createItemHelper() {
		Item item = new Item();
		item.setItemnumber(itemNumber);
		item.setDescription("Mutter");
		int inStock = 100;
		item.setInStock(inStock);
		item.setOrderingPoint(15);
		double price = 25.50;
		item.setPrice(price);
		item.setPurchasePrice(item.getPrice() / 2);
		return item;

	}

	@Test
	public void create() throws KeyLengthException, JsonProcessingException, JOSEException {

		String jwe = getJWEFromSecurityServer(uuu, ppp);

		Item item = createItemHelper();

		Response response = client.target(REST_ITEM_END_POINT).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).post(Entity.json(item), Response.class);

		int status = response.getStatus();
		if (status == OK) {
			Item aCreatedItem = response.readEntity(new GenericType<Item>() {
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
		client.target(REST_ITEM_END_POINT).path(itemNumber).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).delete(Response.class);

		// and add so we can update
		Item item = createItemHelper();

		client.target(REST_ITEM_END_POINT).request(MediaType.APPLICATION_JSON).header("Authorization", jwe)
				.post(Entity.json(item), Response.class);

		Item itemWithNewData = createItemHelper();
		itemWithNewData.setDescription("CHANGED DESCRIPTION");

		Response response = client.target(REST_ITEM_END_POINT).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).put(Entity.json(itemWithNewData), Response.class);

		int status = response.getStatus();
		if (status == OK) {

			if (status == OK) {
				Item aCreatedItem = response.readEntity(new GenericType<Item>() {
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
		client.target(REST_ITEM_END_POINT).path(itemNumber).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).delete(Response.class);

		// and add so we can update
		Item item = createItemHelper();

		client.target(REST_ITEM_END_POINT).request(MediaType.APPLICATION_JSON).header("Authorization", jwe)
				.post(Entity.json(item), Response.class);

		Response response = client.target(REST_ITEM_END_POINT).path(itemNumber).request(MediaType.APPLICATION_JSON)
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

		Response response = client.target(REST_ITEM_END_POINT).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).get(Response.class);

		int status = response.getStatus();
		if (status == OK) {
			List<Item> theList = response.readEntity(new GenericType<List<Item>>() {
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
		client.target(REST_ITEM_END_POINT).path(itemNumber).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).delete(Response.class);

		// and add so we can update
		Item item = createItemHelper();
		item.setId(1004L);

		client.target(REST_ITEM_END_POINT).request(MediaType.APPLICATION_JSON).header("Authorization", jwe)
				.post(Entity.json(item), Response.class);

		String theId = String.valueOf(item.getId().intValue());
		Response response = client.target(REST_ITEM_END_POINT).path(theId).request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).get(Response.class);

		int status = response.getStatus();
		if (status == OK) {
			Item aService = response.readEntity(new GenericType<Item>() {
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

	@Test
	public void getAllItemIDS() throws KeyLengthException, JsonProcessingException, JOSEException {

		String jwe = getJWEFromSecurityServer(uuu, ppp);

		Response response = client.target(REST_ITEM_END_POINT).path("ids").request(MediaType.APPLICATION_JSON)
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

	// TypeReference typeref = new TypeReference<List<CustomCode>>() {};
	// List<CustomCode> codes = mapper.readValue(json, typeref); return codes;

}
