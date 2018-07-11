package com.fnt;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fnt.dto.CustomerOrder;
import com.fnt.entity.CustomerOrderHead;
import com.fnt.entity.CustomerOrderLine;
import com.fnt.entity.CustomerOrderLinePK;
import com.fnt.entity.ItemView1;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeyLengthException;

public class _Test_CustomerOrderCreate {

	private static final String REST_CUSTOMER_END_POINT = "http://localhost:8080/server2/rest/customer";
	private static final String REST_ITEM_END_POINT = "http://localhost:8080/server2/rest/item";
	private static final String REST_CUSTOMER_ORDER_END_POINT = "http://localhost:8080/server2/rest/customerorder";
	private static final String REST_QUEUE_END_POINT = "http://localhost:8080/server2/rest/queue";
	private static final String LOGIN_END_POINT = "http://localhost:8080/auth/rest/login";
	private static final float NUMBER_OF_CUSTOMERORDERS = 1000;

	private Random rnd = new Random();

	private static int OK = 200;
	private static int FORBIDDEN = 403;
	private static int PRECONDITION_FAILED = 412;

	static String logins[] = { "thomas@fanto.se", "annalena@fanto.se", "madeleine@fanto.se", };
	static String pwds[] = { "myPassword", "myAnnaLenaPassword", "myMadeleinePassword", };

	private static String uuu = "thomas@fanto.se";
	private static String ppp = "myPassword";

	private static Client client;
	private static String jwe;
	private static List<String> customerIds;
	private static List<ItemView1> itemIds;

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

		// login
		jwe = getJWEFromSecurityServer(uuu, ppp);

		// get valid data
		customerIds = getAllCustomerIDS();
		itemIds = getAllItemIDS();

		System.out.println("customers : " + customerIds.size() + "  items : " + itemIds.size());

	}

	@Test
	public void createCustomerOrder() throws KeyLengthException, JsonProcessingException, JOSEException {

		long then = System.currentTimeMillis();

		for (int i = 0; i < NUMBER_OF_CUSTOMERORDERS; i++) {

			CustomerOrder customerOrder = createCustomerOrderHelper();

			Response response = client.target(REST_CUSTOMER_ORDER_END_POINT).request(MediaType.APPLICATION_JSON)
					.header("Authorization", jwe).post(Entity.json(customerOrder), Response.class);

			int status = response.getStatus();
			if (status == OK) {
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
		float perRec = millisecs / NUMBER_OF_CUSTOMERORDERS;
		float perRec2 = millisecs / (NUMBER_OF_CUSTOMERORDERS + (NUMBER_OF_CUSTOMERORDERS * 1000));

		System.out.println("KUNDORDER  ***********************************");
		System.out.println("Antal           " + NUMBER_OF_CUSTOMERORDERS);
		System.out.println("Antal millisecs " + millisecs);
		System.out.println("Rader per order " + 1000);
		System.out.println("Snitt / trans   " + perRec);
		System.out.println("Snitt / record  " + perRec2);

	}

	private CustomerOrder createCustomerOrderHelper() {
		CustomerOrder co = new CustomerOrder();
		CustomerOrderHead coh = createOrderHeadHelper();

		List<CustomerOrderLine> lines = createOrderLinesHelper(1000, coh);
		co.setHead(coh);
		co.setLines(lines);

		return co;

	}

	private CustomerOrderHead createOrderHeadHelper() {

		int n = rnd.nextInt(customerIds.size());

		CustomerOrderHead coh = new CustomerOrderHead();
		coh.setCustomerId(customerIds.get(n));
		coh.setDate(LocalDateTime.now());
		coh.setStatus(5);

		return coh;
	}

	private List<CustomerOrderLine> createOrderLinesHelper(int numberOfLines, CustomerOrderHead coh) {

		List<CustomerOrderLine> ret = new ArrayList<>();

		for (int line = 0; line < numberOfLines; line++) {
			int n = rnd.nextInt(itemIds.size());
			int numberOfItems = rnd.nextInt(1000) + 1;
			ItemView1 itno = itemIds.get(n);
			CustomerOrderLine col = new CustomerOrderLine();
			CustomerOrderLinePK primaryKey = new CustomerOrderLinePK();
			primaryKey.setLineNumber(1);
			col.setItemId(itno.getId());
			col.setNumberOfItems(numberOfItems);
			col.setPricePerItem(itno.getPrice());
			col.setDate(coh.getDate());
			col.setPrimaryKey(primaryKey);
			ret.add(col);
		}
		return ret;
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

	public static List<String> getAllCustomerIDS() throws KeyLengthException, JsonProcessingException, JOSEException {

		Response response = client.target(REST_CUSTOMER_END_POINT).path("ids").request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).get(Response.class);
		int status = response.getStatus();
		if (status == OK) {
			List<String> theList = response.readEntity(new GenericType<List<String>>() {
			});
			return theList;
		} else if (status == FORBIDDEN) {
		} else if (status == PRECONDITION_FAILED) {
		} else {
		}
		return new ArrayList<>();
	}

	public static List<ItemView1> getAllItemIDS() throws KeyLengthException, JsonProcessingException, JOSEException {

		Response response = client.target(REST_ITEM_END_POINT).path("orderinginfo").request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).get(Response.class);

		int status = response.getStatus();
		if (status == OK) {
			List<ItemView1> theList = response.readEntity(new GenericType<List<ItemView1>>() {
			});
			return theList;
		} else if (status == FORBIDDEN) {
		} else if (status == PRECONDITION_FAILED) {
		} else {
		}
		return new ArrayList<>();

	}

	@Test
	public void browseQueue() {

		Response response = client.target(REST_QUEUE_END_POINT).path("browse").request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).get(Response.class);
		System.out.println("status " + response.getStatus());

	}

}
