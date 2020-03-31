package org.etri.ado.device.ros;

import java.util.function.Consumer;

import org.etri.ado.device.DeviceBinding;

import com.fasterxml.jackson.databind.JsonNode;

import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.tools.MessageUnpacker;

public class RosDevice implements DeviceBinding {
	
	private RosBridge m_ros = new RosBridge();	

	@Override
	public void connect(String uri) {
		m_ros.connect(uri, true);
	}

	@Override
	public <T> void publish(String topic, String type, T msg) {
		m_ros.publish(topic, type, msg);
	}

	@Override
	public <T> void subscribe(String topic, String type, Class<?> msgType, Consumer<T> subscriber) {
		
		SubscriptionRequestMsg msg 
			= SubscriptionRequestMsg.generate(topic).setType(type).setThrottleRate(1).setQueueLength(1);
		
		RosListenDelegate delegate = new RosListenDelegate() {
			@Override
			public void receive(JsonNode data, String stringRep) {
				MessageUnpacker<T> unpacker = new MessageUnpacker<T>(msgType);
				subscriber.accept(unpacker.unpackRosMessage(data));
			}			
		};
		
		m_ros.subscribe(msg, delegate);
	}
}
