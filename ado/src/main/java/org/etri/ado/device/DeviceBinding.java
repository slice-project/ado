package org.etri.ado.device;

import java.util.function.Consumer;

public interface DeviceBinding {

	void connect(String uri);
	<T> void publish(String topic, String type, T msg);
	<T> void subscribe(String topic, String type, Class<?> msgType, Consumer<T> subsriber);
}
