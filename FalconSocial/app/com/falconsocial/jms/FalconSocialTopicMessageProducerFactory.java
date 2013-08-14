package com.falconsocial.jms;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.libs.Json;

public class FalconSocialTopicMessageProducerFactory {

	private FalconSocialTopicMessageProducerFactory() {
	}

	public static final FalconSocialTopicMessageProducer createMessageProducer() throws Exception {

		// create the JMS prerequisites
		final Connection connection = FalconSocialTopicEnv.getConnectionFactory().createConnection();
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final MessageProducer messageProducer = session.createProducer(FalconSocialTopicEnv.getTopic());

		// register resource cleanup
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					messageProducer.close();
					session.close();
					connection.close();
				} catch (Exception e) {
					Logger.error("Could not close Falcon Social Topic message producer.", e);
				}
			}
		}));

		return new FalconSocialTopicMessageProducer() {
			@Override
			public void send(JsonNode json) throws Exception {
				String serializedJson = Json.stringify(json);
				Logger.info("Sending message to Falcon Social Topic: " + serializedJson);
				messageProducer.send(session.createTextMessage(serializedJson));
			}

			@Override
			public void close() {
				try {
					messageProducer.close();
					session.close();
					connection.close();
				} catch (Exception e) {
					Logger.error("Could not close Falcon Social Topic message producer.", e);
				}
			}
		};
	}

}
