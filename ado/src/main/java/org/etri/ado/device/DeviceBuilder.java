package org.etri.ado.device;

import org.etri.ado.AgentSystem;

public class DeviceBuilder {

	private AgentSystem m_system;
	private DeviceBinding m_device;	
	private String m_deviceUri;
	
	public DeviceBuilder(AgentSystem system) {
		m_system = system;
	}

	public DeviceBuilder setAgentSystem(AgentSystem system) {
		m_system = system;
		return this;
	}
	
	public DeviceBuilder setDeviceBinding(DeviceBinding device) {
		m_device = device;
		return this;
	}
	
	public DeviceBuilder setDeviceURI(String uri) {
		m_deviceUri = uri;
		return this;
	}
	
	public Device build() {
		Device device = new Device(m_system);
		device.setDeviceBinding(m_device);
		device.setDeviceURI(m_deviceUri);
		
		return device;
	}
}
