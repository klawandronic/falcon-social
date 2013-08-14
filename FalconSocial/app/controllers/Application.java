package controllers;

import java.util.Set;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.libs.F.Callback;
import play.libs.F.Callback0;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import views.html.index;

import com.falconsocial.jms.FalconSocialTopicMessageProducer;
import com.falconsocial.jms.FalconSocialTopicMessageProducerFactory;
import com.falconsocial.monitor.FalconSocialMonitoringManager;
import com.falconsocial.persistence.FalconSocialPersistenceManager;

public class Application extends Controller {

	public static Result index() {
		return ok(index.render("Your Falcon Social Application is ready!"));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public static Result publish() {
		JsonNode json = request().body().asJson();
		ObjectNode result = Json.newObject();

		try {
			FalconSocialTopicMessageProducer messageProducer = FalconSocialTopicMessageProducerFactory.createMessageProducer();
			messageProducer.send(json);
			messageProducer.close();

			result.put("status", "OK");
			result.put("message", "Processed message " + Json.stringify(json));
			return ok(result);
		} catch (Exception e) {
			Logger.error("Could not process request.", e);

			result.put("status", "NOK");
			result.put("message", "Internal server error.");
			return badRequest(result);
		}
	}

	public static Result list() {
		ObjectNode result = Json.newObject();

		try {
			Set<JsonNode> jsons = FalconSocialPersistenceManager.retrieve();

			result.putArray("items").addAll(jsons);
			return ok(result);
		} catch (Exception e) {
			Logger.error("Could not process request.", e);

			result.put("status", "NOK");
			result.put("message", "Internal server error.");
			return badRequest(result);
		}
	}

	public static WebSocket<String> monitor() {
		return new WebSocket<String>() {
			public void onReady(final WebSocket.In<String> in, final WebSocket.Out<String> out) {

				// register socket for monitoring
				Logger.info("WebSocket connected.");
				FalconSocialMonitoringManager.addMonitor(in, out);

				// register request handler
				in.onMessage(new Callback<String>() {
					public void invoke(String event) {
						Logger.info("Received: " + event);
					}
				});

				// register disconnection handler
				in.onClose(new Callback0() {
					public void invoke() {
						Logger.info("WebSocket disconnected.");
						FalconSocialMonitoringManager.removeMonitor(in);
					}
				});
			}
		};
	}

}
