package org.etri.ado.actor;

import org.etri.ado.gateway.openai.OpenAI.Action;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;

public class DeviceActor extends AbstractActor {
		
	private final ReceiveBuilder m_builder;
	
	
	public static Props prop(ReceiveBuilder builder) {
		return Props.create(DeviceActor.class, builder);
	}
	
	public DeviceActor(ReceiveBuilder builder) {
		m_builder = builder;
	}
	
	@Override
	public void preStart() {	
//		getContext().system().getEventStream().subscribe(getContext().getSelf(), Action.class);
	}
	
	@Override
	public void postStop() {

	}	

	@Override
	public Receive createReceive() {		
		return m_builder.build();
	}
}