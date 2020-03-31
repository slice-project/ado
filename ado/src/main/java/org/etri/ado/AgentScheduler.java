package org.etri.ado;

import org.etri.ado.actor.AgentSchedulerActor;

import akka.actor.ActorRef;
import akka.camel.Camel;
import akka.camel.CamelExtension;
import akka.event.LoggingAdapter;

public class AgentScheduler extends ActorHolderImpl {

	private final LoggingAdapter m_logger;
	private final AgentSystem m_system;
		
	public AgentScheduler(AgentSystem system) {
		super(null);
		
		m_system = system;
		m_logger = m_system.getLogger(this);
	}
	
	public void start() {

		Camel camel = CamelExtension.get(m_system.getActorSystem());
		ActorRef scheduler = m_system.getActorSystem().actorOf(AgentSchedulerActor.prop(m_system), "scheduler");
		try {
			camel.context().addRoutes(new ConsoleInRouteBuilder(scheduler));
		} 
		catch ( Exception e ) {
			m_logger.error("Error:" + e.getMessage());
		}		
		setActor(scheduler);
	}

}
