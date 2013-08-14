
import org.codehaus.jackson.JsonNode;

import com.falconsocial.jms.FalconSocialTopicMessageConsumerFactory;
import com.falconsocial.jms.FalconSocialTopicMessageListener;
import com.falconsocial.monitor.FalconSocialMonitoringManager;
import com.falconsocial.persistence.FalconSocialPersistenceManager;

import play.Application;
import play.GlobalSettings;
import play.Logger;
import play.libs.Json;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		registerMessagePersisterConsumer();
		registerMessagePublisherConsumer();
	}

	private void registerMessagePersisterConsumer() {
		try {
			Logger.info("Registering Falcon Social Topic message persister consumer...");
			FalconSocialTopicMessageConsumerFactory.createMessageConsumer(new FalconSocialTopicMessageListener() {
				@Override
				public void onMessage(JsonNode json) {
					Logger.info("Persisting message: " + Json.stringify(json));
					FalconSocialPersistenceManager.store(json);
				}
			});
		} catch (Exception e) {
			Logger.error("Some error in Falcon Social Topic message persister consumer.", e);
		}
	}

	private void registerMessagePublisherConsumer() {
		try {
			Logger.info("Registering Falcon Social Topic message publisher consumer...");
			FalconSocialTopicMessageConsumerFactory.createMessageConsumer(new FalconSocialTopicMessageListener() {
				@Override
				public void onMessage(JsonNode json) {
					Logger.info("Publishing message: " + Json.stringify(json));
					FalconSocialMonitoringManager.publish(json);
				}
			});
		} catch (Exception e) {
			Logger.error("Some error in Falcon Social Topic message publisher consumer.", e);
		}
	}

}