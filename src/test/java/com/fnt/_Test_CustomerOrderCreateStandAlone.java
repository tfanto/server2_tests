package com.fnt;

import java.time.LocalDate;
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

public class _Test_CustomerOrderCreateStandAlone implements Runnable {

	private final String REST_CUSTOMER_END_POINT = "http://localhost:8080/server2/rest/customer";
	private final String REST_ITEM_END_POINT = "http://localhost:8080/server2/rest/item";
	private String REST_CUSTOMER_ORDER_END_POINT = "http://localhost:8080/server2/rest/customerorder";
	private final String LOGIN_END_POINT = "http://localhost:8080/auth/rest/login";
	private float NUMBER_OF_CUSTOMERORDERS;
	private int NUMBER_OF_LINES_PER_ORDER;

	private Random rnd = new Random();

	private int OK = 200;
	private int FORBIDDEN = 403;
	private int PRECONDITION_FAILED = 412;

	static String logins[] = { "thomas@fanto.se", "annalena@fanto.se", "madeleine@fanto.se", };
	static String pwds[] = { "myPassword", "myAnnaLenaPassword", "myMadeleinePassword", };

	private String uuu = "thomas@fanto.se";
	private String ppp = "myPassword";

	private Client client;
	private String jwe;
	private List<Long> customerIds;
	private List<ItemView1> itemIds;
	


	_Test_CustomerOrderCreateStandAlone(float NUMBER_OF_CUSTOMERORDERS, int NUMBER_OF_LINES_PER_ORDER) {

		this.NUMBER_OF_CUSTOMERORDERS = NUMBER_OF_CUSTOMERORDERS;
		this.NUMBER_OF_LINES_PER_ORDER = NUMBER_OF_LINES_PER_ORDER;

	}

	public void init() throws KeyLengthException, JsonProcessingException, JOSEException {

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

		// System.out.println("customers : " + customerIds.size() + " items : " +
		// itemIds.size());

	}

	public void createCustomerOrder() throws KeyLengthException, JsonProcessingException, JOSEException {

		// long then = System.currentTimeMillis();

		// for (int i = 0; i < NUMBER_OF_CUSTOMERORDERS; i++) {
		for (int i = 0; i < 1; i++) {

			CustomerOrder customerOrder = createCustomerOrderHelper();

			Response response = client.target(REST_CUSTOMER_ORDER_END_POINT).path("batch").request(MediaType.APPLICATION_JSON)
					.header("Authorization", jwe).post(Entity.json(customerOrder), Response.class);

			int status = response.getStatus();
			if (status == OK) {
			} else if (status == FORBIDDEN) {
				System.out.println("createCustomerOrder FORBIDDEN FAIL");
			} else if (status == PRECONDITION_FAILED) {
				System.out.println("createCustomerOrder PRECONDITION FAIL");
			} else {
				System.out.println("createCustomerOrder  OTHER " + status + "  " + response.getStatusInfo().toString());
			}
		}

	}

	private CustomerOrder createCustomerOrderHelper() {
		CustomerOrder co = new CustomerOrder();
		CustomerOrderHead coh = createOrderHeadHelper();

		List<CustomerOrderLine> lines = createOrderLinesHelper(coh);
		co.setHead(coh);
		co.setLines(lines);

		return co;

	}

	private CustomerOrderHead createOrderHeadHelper() {

		int n = rnd.nextInt(customerIds.size());

		CustomerOrderHead coh = new CustomerOrderHead();
		coh.setCustomerid(customerIds.get(n));
		coh.setDate(LocalDate.now());
		coh.setStatus(5);

		return coh;
	}

	private List<CustomerOrderLine> createOrderLinesHelper(CustomerOrderHead coh) {

		List<CustomerOrderLine> ret = new ArrayList<>();

		for (int line = 0; line < NUMBER_OF_LINES_PER_ORDER; line++) {
			int n = rnd.nextInt(itemIds.size());
			int numberOfItems = rnd.nextInt(1000) + 1;
			ItemView1 itno = itemIds.get(n);
			CustomerOrderLine col = new CustomerOrderLine();
			CustomerOrderLinePK primaryKey = new CustomerOrderLinePK();
			primaryKey.setLineNumber(1L);
			col.setItemid(itno.getId());
			col.setNumberofitems(numberOfItems);
			col.setPriceperitem(itno.getPrice());
			col.setDate(coh.getDate());
			col.setPrimarykey(primaryKey);
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

	private String getJWEFromSecurityServer(String login, String password)
			throws JsonProcessingException, KeyLengthException, JOSEException {

		Response response = client.target(LOGIN_END_POINT).path(login).path(password)
				.request(MediaType.APPLICATION_JSON).get(Response.class);
		if (response.getStatus() == 200) {
			String jwe = response.getHeaderString("Authorization");
			if (jwe == null) {
				System.out.println("FAIL");
			}
			return jwe;
		} else if (response.getStatus() == 404) {
			System.out.println("FAIL");
			return "";
		} else {
			System.out.println("FAIL");
			return "";
		}
	}

	public List<Long> getAllCustomerIDS() throws KeyLengthException, JsonProcessingException, JOSEException {

		Response response = client.target(REST_CUSTOMER_END_POINT).path("ids").request(MediaType.APPLICATION_JSON)
				.header("Authorization", jwe).get(Response.class);
		int status = response.getStatus();
		if (status == OK) {
			List<Long> theList = response.readEntity(new GenericType<List<Long>>() {
			});
			return theList;
		} else if (status == FORBIDDEN) {
		} else if (status == PRECONDITION_FAILED) {
		} else {
		}
		return new ArrayList<>();
	}

	public List<ItemView1> getAllItemIDS() throws KeyLengthException, JsonProcessingException, JOSEException {

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

	public static void main(String[] args)
			throws KeyLengthException, JsonProcessingException, JOSEException, InterruptedException {

		long then = System.currentTimeMillis();
		List<Thread> threads = new ArrayList<>();

		float number_of_customerOrders_per_thread = 1000;
		int number_of_threads = 10;
		int number_of_lines_per_order = 1000;

		for (int i = 0; i < number_of_threads; i++) {

			_Test_CustomerOrderCreateStandAlone pgm = new _Test_CustomerOrderCreateStandAlone(
					number_of_customerOrders_per_thread, number_of_lines_per_order);
			pgm.init();
			Thread t = new Thread(pgm);
			threads.add(t);
			t.start();

		}

		for (Thread thread : threads) {
			thread.join();
		}

		float orders = number_of_customerOrders_per_thread * number_of_threads;
		float lines = orders * number_of_lines_per_order;
		float totalrecs = lines + orders;

		long now = System.currentTimeMillis();
		float millisecs = now - then;
		float perRec = millisecs / orders;
		float perRec2 = millisecs / (orders + (orders * number_of_lines_per_order));

		System.out.println("KUNDORDER  ***********************************");
		System.out.println("Antal orders    " + orders);
		System.out.println("Antal millisecs " + millisecs);
		System.out.println("Rader per order " + number_of_lines_per_order);
		System.out.println("Snitt / trans   " + perRec);
		System.out.println("Snitt / record  " + perRec2);

	}

	@Override
	public void run() {
		try {
			createCustomerOrder();
		} catch (JsonProcessingException | JOSEException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
