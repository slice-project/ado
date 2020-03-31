package org.etri.ado.device;

import java.util.function.Consumer;

import org.etri.ado.AgentSystem;
import org.etri.ado.TupleSpace;

import akka.event.LoggingAdapter;

public abstract class ContextUpdater<T> implements Consumer<T> {

	protected AgentSystem m_system;
	
	protected ContextUpdater() {
		
	}

	protected LoggingAdapter getLogger() {
		return m_system.getLogger(this);
	}
	
	protected TupleSpace tupleSpace() {
		return m_system.tupleSpace();
	}
		
	public void setAgentSystem(AgentSystem system) {
		m_system = system;
	}
}
