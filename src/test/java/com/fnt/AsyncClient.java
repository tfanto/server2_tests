package com.fnt;

import java.io.IOException;
import java.net.URI;

import javax.websocket.ClientEndpoint;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.glassfish.tyrus.client.ClientManager;

@ClientEndpoint
public class AsyncClient {

	private static final String asyncServer = "ws://localhost:8080/server2/rest/ws/";

	public static void main(String[] args) {

		try {

			final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
			ClientManager client = ClientManager.createClient();
			client.connectToServer(new Endpoint() {

				@Override
				public void onOpen(Session session, EndpointConfig config) {
					session.addMessageHandler(new MessageHandler() {
						

						

					});
					try {
						session.getBasicRemote().sendText("HEPP");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}, cec, new URI(asyncServer));
		} catch (

		Exception e) {
			e.printStackTrace();
		}

	}
}
