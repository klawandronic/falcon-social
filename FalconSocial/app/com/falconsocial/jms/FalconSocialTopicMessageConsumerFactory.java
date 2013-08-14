package com.falconsocial.jms;

import javax.jms.Connection;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import play.Logger;
import play.libs.Json;

public class FalconSocialTopicMessageConsumerFactory {

	private FalconSocialTopicMessageConsumerFactory() {
	}

	public static final void createMessageConsumer(final FalconSocialTopicMessageListener messageListener) throws Exception {

		// create the JMS prerequisites
		final Connection connection = FalconSocialTopicEnv.getConnectionFactory().createConnection();
		final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		final MessageConsumer messageConsumer = session.createConsumer(FalconSocialTopicEnv.getTopic());

		// add the message listener for this consumer
		messageConsumer.setMessageListener(new MessageListener() {
			@Override
			public void onMessage(Message message) {
				if (message == null) {
					Logger.warn("Null message received from Falcon Social Topic.");
				} else if (!(message instanceof TextMessage)) {
					Logger.warn("Unexpected message type received from Falcon Social Topic: " + message.getClass().getCanonicalName());
				} else {
					try {
						String serializedJson = ((TextMessage) message).getText();
						Logger.info("Received message from Falcon Social Topic: " + serializedJson);
						messageListener.onMessage(Json.parse(serializedJson));
					} catch (Exception e) {
						Logger.error("Could not receive message from Falcon Social Topic.");
					}
				}
			}
		});

		// register resource cleanup
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					messageConsumer.close();
					session.close();
					connection.close();
				} catch (Exception e) {
					Logger.error("Could not close Falcon Social Topic message consumer.", e);
				}
			}
		}));

		// start the connection
		connection.start();

		Logger.info("Falcon Social Topic message consumer was started.");

	}
}
