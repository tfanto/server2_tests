package com.fnt;

import java.util.Random;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.entity.Customer;
import com.fnt.entity.Item;
import com.fnt.entity.NumberSerie;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;

public class _Test_SETUP_DATA {

	private static boolean createLogins = false;
	private static boolean createNumberSeries = false;

	private static final String REST_CUSTOMER_END_POINT = "http://localhost:8080/server2/rest/customer";
	private static final String REST_ITEM_END_POINT = "http://localhost:8080/server2/rest/item";
	private static final String USER_REGISTRATION_END_POINT = "http://localhost:8080/auth/rest/user";
	private static final String NUMBERSERIES_END_POINT = "http://localhost:8080/server2/rest/ns";

	private static final String LOGIN_END_POINT = "http://localhost:8080/auth/rest/login";
	private static final int NUMBER_OF_CUSTOMERS = 10000;
	private static final int NUMBER_OF_ITEMS = 250000;

	@SuppressWarnings("unused")
	private Random rnd = new Random();

	private int OK = 200;
	private int FORBIDDEN = 403;
	private int NOT_FOUND = 404;
	private int PRECONDITION_FAILED = 412;

	static String logins[] = { "thomas@fanto.se", "annalena@fanto.se", "madeleine@fanto.se", };
	static String pwds[] = { "myPassword", "myAnnaLenaPassword", "myMadeleinePassword", };
	static String roles[] = { "ADMIN", "USER", "GUEST", };

	private static String uuu = "thomas@fanto.se";
	private static String ppp = "myPassword";

	private static Client client;
	private static String jwe;
	
	static String items[] = { "skruv","spik","hammare","skruvmejsel","såg","yxa","pensel","skiftnyckel","tång","syl", };


	@BeforeClass
	public static void beforeClass() throws KeyLengthException, JsonProcessingException, JOSEException {

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

		int nLogins = logins.length;
		int nRoles = roles.length;

		if (createLogins) {

			for (int l = 0; l < nLogins; l++) {
				String login = logins[l];
				String password = pwds[l];

				client.target(USER_REGISTRATION_END_POINT).path(login).path(password)
						.request(MediaType.APPLICATION_JSON).post(null, Response.class);

				for (int r = 0; r < nRoles; r++) {

					String role = roles[r];
					client.target(USER_REGISTRATION_END_POINT).path(login).path(role).path("Description for " + role)
							.request(MediaType.APPLICATION_JSON).post(null, Response.class);
				}
			}
		}

		// login
		jwe = getJWEFromSecurityServer(uuu, ppp);

		if (createNumberSeries) {

			NumberSerie ns = new NumberSerie();
			ns.setName("CUSTOMER_ORDER");
			ns.setValue(0);

			client.target(NUMBERSERIES_END_POINT).request(MediaType.APPLICATION_JSON).header("Authorization", jwe)
					.post(Entity.json(ns), Response.class);

		}

	}

	@Test
	public void testCreateJWE() throws KeyLengthException, JsonProcessingException, JOSEException {

		@SuppressWarnings("unused")
		String jwe = getJWEFromSecurityServer(uuu, ppp);
	}

	@Test
	public void createCustomers() throws KeyLengthException, JsonProcessingException, JOSEException {

		client.target(REST_CUSTOMER_END_POINT).path("all").request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).delete(Response.class);

		long then = System.currentTimeMillis();

		for (int i = 0; i < NUMBER_OF_CUSTOMERS; i++) {

			Customer customer = new Customer();
			customer.setCustomernumber("CUNO_" + i);
			customer.setName("CustomerName_" + i);
			customer.setDescription("Description_" + i);

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

		long now = System.currentTimeMillis();
		float millisecs = now - then;
		float perRec = NUMBER_OF_CUSTOMERS / millisecs;

		System.out.println("KUNDER *************************************");
		System.out.println("Antal           " + NUMBER_OF_CUSTOMERS);
		System.out.println("Antal millisecs " + millisecs);
		System.out.println("Snitt           " + perRec);

	}

	@Test
	public void createItems() throws KeyLengthException, JsonProcessingException, JOSEException {

		client.target(REST_ITEM_END_POINT).path("all").request(MediaType.APPLICATION_JSON).header("Authorization", jwe)
				.delete(Response.class);

		long then = System.currentTimeMillis();

		for (int i = 0; i < NUMBER_OF_ITEMS; i++) {

			Item item = new Item();
			
			String itemName = items[rnd.nextInt(items.length)];

			String nbr = String.format("%05d", i);
			item.setItemnumber(itemName + "_" + nbr);
			item.setDescription( itemName + "artikelbeskrivning typ:" + nbr);
			int inStock = rnd.nextInt(2000) + 5;
			item.setInstock(inStock);
			item.setOrderingpoint(item.getInstock() / 3);

			double price = (rnd.nextDouble() * 1000) + 15;

			item.setPrice(price);
			item.setPurchaseprice(item.getPrice() / 2);

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

		long now = System.currentTimeMillis();
		float millisecs = now - then;
		float perRec = NUMBER_OF_ITEMS / millisecs;

		System.out.println("ARTIKLAR *************************************");
		System.out.println("Antal           " + NUMBER_OF_ITEMS);
		System.out.println("Antal millisecs " + millisecs);
		System.out.println("Snitt           " + perRec);

	}

	/**********************************************************************************
	 * 
	 * @param login
	 * @param password
	 * @return
	 * @throws JsonProcessingException
	 * @throws KeyLengthException
	 * @throws JOSEException
	 */

	private static String getJWEFromSecurityServer(String login, String password)
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

}
