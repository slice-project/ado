package org.etri.ado.device;

import java.util.ArrayList;
import java.util.List;

import org.etri.ado.AgentSystem;

import akka.japi.pf.ReceiveBuilder;

public class DeviceBuilder {
	
	public static DeviceBuilder create(AgentSystem system) {
		return new DeviceBuilder(system);
	}	
	
	private AgentSystem m_system;
	private DeviceBinding m_device;	
	private String m_deviceUri;
	private ReceiveBuilder m_builder = ReceiveBuilder.create();	
	private List<Class<?>> m_commanders = new ArrayList<Class<?>>();	
	
	private DeviceBuilder(AgentSystem system) {
		m_system = system;
	}
	
	public DeviceBuilder setDeviceBinding(DeviceBinding device) {
		m_device = device;
		return this;
	}
	
	public DeviceBuilder setDeviceURI(String uri) {
		m_deviceUri = uri;
		return this;
	}
	
	public <T> DeviceBuilder addActionCommander(Class<T> actionType, ActionCommander<T> commander) {
		commander.setAgentSystem(m_system);
		commander.setDevice(m_device);
		
		m_commanders.add(actionType);
		m_builder = m_builder.match(actionType, commander);
		
		return this;
	}	
	
	public Device build() {
		return new Device(m_system, m_device, m_deviceUri, m_builder, m_commanders);
	}
}
