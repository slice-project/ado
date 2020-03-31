package org.etri.ado.agent.registry;

import org.etri.ado.actor.AgentRegistryActor;
import org.etri.ado.agent.AgentInfo;
import org.etri.ado.agent.registry.message.SubscribeTo;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.ddata.LWWMap;
import akka.cluster.ddata.Replicator.Subscribe;
import akka.japi.pf.ReceiveBuilder;

public class SubscribeMatcher extends AbstractActor {	
		
	private final ActorRef m_replicator;
	
	public static Props props(ActorRef replicator) {
		return Props.create(SubscribeMatcher.class, replicator);
	}	
	
	public SubscribeMatcher(ActorRef replicator) {
		m_replicator = replicator;
	}
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();		
		builder.match(SubscribeTo.class, this::receiveSubscribe);
		builder.matchAny(this::unhandled);
		
		return builder.build();
	}
	
	private void receiveSubscribe(SubscribeTo cmd) {
		m_replicator.tell(new Subscribe<LWWMap<String,AgentInfo>>(AgentRegistryActor.idToAgentKey, getSender()), ActorRef.noSender());
	}
}
