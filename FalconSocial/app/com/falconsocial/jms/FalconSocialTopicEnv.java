package com.falconsocial.jms;

import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;

import play.Logger;
import play.Play;

final class FalconSocialTopicEnv {

	private static final String PROVIDER_URL_PROPERTY = "falcon.social.topic.provider.url";
	private static final String CONNECTION_FACTORY_NAME_PROPERTY = "falcon.social.topic.connection.factory.name";
	private static final String TOPIC_NAME_PROPERTY = "falcon.social.topic.name";

	private static Context context = null;
	private static ConnectionFactory connectionFactory = null;
	private static Topic topic = null;

	private FalconSocialTopicEnv() {
	}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					getContext().close();
				} catch (Exception e) {
					Logger.error("Could not close JNDI context.", e);
				}
			}
		}));
	}

	private static final Context getContext() throws Exception {
		if (context == null) {
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.PROVIDER_URL, Play.application().configuration().getString(PROVIDER_URL_PROPERTY));
			env.put(Context.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
			env.put(Context.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces  ");

			context = new InitialContext(env);
		}
		return context;
	}

	static final ConnectionFactory getConnectionFactory() throws Exception {
		if (connectionFactory == null) {
			connectionFactory = (ConnectionFactory) getContext().lookup(Play.application().configuration().getString(CONNECTION_FACTORY_NAME_PROPERTY));
		}
		return connectionFactory;
	}

	static final Topic getTopic() throws Exception {
		if (topic == null) {
			topic = (Topic) getContext().lookup(Play.application().configuration().getString(TOPIC_NAME_PROPERTY));
		}
		return topic;
	}

}