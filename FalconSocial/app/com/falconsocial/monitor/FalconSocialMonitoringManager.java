package com.falconsocial.monitor;

import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.JsonNode;

import play.libs.Json;
import play.mvc.WebSocket;

public class FalconSocialMonitoringManager {

	private static final FalconSocialMonitoringManager monitoringManager = new FalconSocialMonitoringManager();

	private final Map<WebSocket.In<String>, WebSocket.Out<String>> monitors = new HashMap<WebSocket.In<String>, WebSocket.Out<String>>();

	private FalconSocialMonitoringManager() {
	}

	synchronized public static final void addMonitor(WebSocket.In<String> in, WebSocket.Out<String> out) {
		monitoringManager.monitors.put(in, out);
	}

	synchronized public static final void removeMonitor(WebSocket.In<String> in) {
		monitoringManager.monitors.remove(in);
	}

	synchronized public static final void publish(JsonNode json) {
		for (WebSocket.Out<String> out : monitoringManager.monitors.values()) {
			out.write(Json.stringify(json));
		}
	}

}
