package org.etri.ado.device;

import org.etri.ado.AgentSystem;
import org.etri.ado.TupleSpace;

import akka.event.LoggingAdapter;
import akka.japi.pf.FI.UnitApply;

public abstract class ActionCommander<T> implements UnitApply<T>{

	protected AgentSystem m_system;
	protected DeviceBinding m_device;
	
	protected ActionCommander() {
	}

	protected LoggingAdapter getLogger() {
		return m_system.getLogger(this);
	}
	
	protected DeviceBinding getDevice() {
		return m_device;
	}
	
	protected TupleSpace tupleSpace() {
		return m_system.tupleSpace();
	}
	
	public void setAgentSystem(AgentSystem system) {
		m_system = system;
	}	
	
	public void setDevice(DeviceBinding device) {
		m_device = device;
	}
	
}
