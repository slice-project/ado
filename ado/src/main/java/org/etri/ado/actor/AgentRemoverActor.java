package org.etri.ado.actor;

import java.util.Set;

import org.etri.ado.AgentSystem;
import org.etri.ado.agent.registry.message.Remove;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.cluster.ClusterEvent.MemberEvent;
import akka.cluster.ClusterEvent.MemberRemoved;
import akka.cluster.ClusterEvent.UnreachableMember;

public class AgentRemoverActor extends AbstractActor {

	public static Props props(AgentSystem system) {
		return Props.create(AgentRemoverActor.class, system);
	}
	
	
	private final Cluster m_cluster = Cluster.get(getContext().getSystem());	
	private final AgentSystem m_system;
	
	public AgentRemoverActor(AgentSystem system) {
		m_system = system;
	}
	
	@Override
	public void preStart() {
		m_cluster.subscribe(getSelf(), ClusterEvent.initialStateAsEvents(), MemberEvent.class, UnreachableMember.class);
	}

	@Override
	public void postStop() {
		m_cluster.unsubscribe(getSelf());
	}	
	
	@Override
	public Receive createReceive() {		
		return receiveBuilder().match(MemberRemoved.class, this::receiveMemberRemoved).build();
	}

	private void receiveMemberRemoved(MemberRemoved event) {
		Set<String> roles = event.member().getRoles();
		String removedId = null;
		roles.stream().forEach(role -> {
			if ( role.contains("id-" )) {
				m_system.getAgentRegistryActor().tell(new Remove(role.substring(3)), getSelf());
			}
		});
		
		System.out.println("Removed Agent : " + event.member().getRoles());
	}	
	
}
