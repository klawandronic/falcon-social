package com.falconsocial.jms;

import org.codehaus.jackson.JsonNode;

public interface FalconSocialTopicMessageProducer {

	public void send(JsonNode json) throws Exception;

	public void close();

}
