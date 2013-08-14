package com.falconsocial.jms;

import org.codehaus.jackson.JsonNode;

public interface FalconSocialTopicMessageListener {

	public void onMessage(JsonNode json) throws Exception;

}
