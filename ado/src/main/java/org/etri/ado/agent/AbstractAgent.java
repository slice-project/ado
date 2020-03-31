package org.etri.ado.agent;

import org.etri.ado.ActionStream;
import org.etri.ado.AgentSystem;
import org.etri.ado.TupleSpace;

public abstract class AbstractAgent implements Agent {

	protected AgentSystem m_system;
	
	public AbstractAgent() {
		
	}	

	public void setAgentSystem(AgentSystem system) {
		m_system = system;
	}

	protected TupleSpace getTupleSpace() {
		return m_system.tupleSpace();
	}
	
	protected ActionStream getActionStream() {
		return m_system.actionStream();
	}
}
