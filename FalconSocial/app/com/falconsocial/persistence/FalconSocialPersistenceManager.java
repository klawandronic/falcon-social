package com.falconsocial.persistence;

import java.util.LinkedHashSet;
import java.util.Set;

import org.codehaus.jackson.JsonNode;

import play.Logger;
import play.Play;
import play.libs.Json;
import redis.clients.jedis.Jedis;

public class FalconSocialPersistenceManager {

	private static final String REDIS_HOST_PROPERTY = "redis.host";
	private static final String REDIS_PORT_PROPERTY = "redis.port";

	private static final String SET_KEY = "fs";

	private static final FalconSocialPersistenceManager persistenceManager = new FalconSocialPersistenceManager();

	private final Jedis jedis;

	private FalconSocialPersistenceManager() {
		jedis = new Jedis(Play.application().configuration().getString(REDIS_HOST_PROPERTY), Integer.parseInt(Play.application().configuration().getString(REDIS_PORT_PROPERTY)));

		// register resource cleanup
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					jedis.disconnect();
				} catch (Exception e) {
					Logger.error("Could not close Falcon Social persistence manager.", e);
				}
			}
		}));

		// open connection
		jedis.connect();
	}

	public static final void store(JsonNode json) {
		persistenceManager.jedis.sadd(SET_KEY, Json.stringify(json));
	}

	public static final Set<JsonNode> retrieve() {
		Set<String> strings = persistenceManager.jedis.smembers(SET_KEY);
		Set<JsonNode> result = new LinkedHashSet<JsonNode>();
		for (String string : strings) {
			result.add(Json.parse(string));
		}
		return result;
	}

}
