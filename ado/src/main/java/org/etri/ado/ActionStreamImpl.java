package org.etri.ado;

import akka.event.EventStream;

public class ActionStreamImpl implements ActionStream {

	private final EventStream m_stream;
	
	public ActionStreamImpl(EventStream stream) {
		m_stream = stream;
	}
	
	@Override
	public void publish(Object action) {
		m_stream.publish(action);
	}
}
