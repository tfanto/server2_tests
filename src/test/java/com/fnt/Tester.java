package com.fnt;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.SseEventSource;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class Tester {

	private Client client;
	private WebTarget webTarget;
	private SseEventSource eventSource;


	@Before
	public void setUp() {
		client = ClientBuilder.newBuilder().connectTimeout(5, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build();
		webTarget = client.target("http://localhost:8080/server2/rest/events");
		try {
			eventSource = SseEventSource.target(webTarget).build();
			eventSource.register((e) -> {
				System.out.println(e.readData());
			});
			eventSource.open();
		} catch (Throwable t) {
			System.out.println(t.toString());
		}
		finally {
		}

	}

	@After
	public void tearDown() {
		webTarget.request().delete();
		//eventSource.close(3, TimeUnit.SECONDS);
	}

	@Test
	public void test1() {
		
		
	//       executorService.scheduleWithFixedDelay(() -> {
	//            webTarget.request().post(Entity.entity("Hello SSE JAX-RS client.", MediaType.TEXT_PLAIN_TYPE));
	//        }, 250, 500, TimeUnit.MILLISECONDS);
		
		webTarget.request().post(Entity.entity("Hello SSE JAX-RS client.", MediaType.TEXT_PLAIN_TYPE));

		try {
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		


	}

}
