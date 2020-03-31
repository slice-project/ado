package org.etri.ado.device;

import java.util.ArrayList;
import java.util.List;

import org.etri.ado.ActorHolderImpl;
import org.etri.ado.AgentSystem;
import org.etri.ado.actor.DeviceActor;

import akka.actor.ActorRef;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class Device extends ActorHolderImpl {
	
	public static DeviceBuilder builder(AgentSystem system) {
		return new DeviceBuilder(system);
	}

	private LoggingAdapter m_logger;
	private AgentSystem m_system;
	private DeviceBinding m_device;	
	private String m_deviceUri;
	private ReceiveBuilder m_builder = ReceiveBuilder.create();	
	private List<Class<?>> m_commanders = new ArrayList<Class<?>>();
	
	public Device(AgentSystem system) {
		super(null);
		setAgentSystem(system);
	}
	
	public DeviceBinding getDeviceBinding() {
		return m_device;
	}

	private void setAgentSystem(AgentSystem system) {
		m_system = system;
		m_logger = system.getLogger(this);
	}
	
	public void setDeviceBinding(DeviceBinding device) {
		m_device = device;
	}
	
	public void setDeviceURI(String uri) {
		m_deviceUri = uri;
	}
	
	public <T> void addActionCommander(Class<T> actionType, ActionCommander<T> commander) {
		commander.setAgentSystem(m_system);
		commander.setDevice(m_device);
		
		m_commanders.add(actionType);
		m_builder = m_builder.match(actionType, commander);
	}	
	
	public <T> void addContextUpdater(String topic, String type, Class<?> msgType, ContextUpdater<T> updater) {
		updater.setAgentSystem(m_system);
		m_device.subscribe(topic, type, msgType, updater);
	}
	
	public void connect() {
		ActorRef actor = m_system.getActorSystem().actorOf(DeviceActor.prop(m_builder));
		setActor(actor);	
		
		for ( Class<?> commander : m_commanders ) {
			m_system.getActorSystem().getEventStream().subscribe(getActor(), commander);
		}
		
		m_device.connect(m_deviceUri);
		m_logger.info("connection established to ({}]", m_deviceUri);			
	}
}
