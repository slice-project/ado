package org.etri.ado.actor;

import org.etri.ado.AgentSystem;
import org.etri.ado.agent.AgentInfo;
import org.etri.ado.agent.registry.message.GetAll;
import org.etri.ado.agent.registry.message.GetByCapabilities;
import org.etri.ado.agent.registry.message.GetById;
import org.etri.ado.agent.registry.message.GetByRole;
import org.etri.ado.agent.registry.message.Put;
import org.etri.ado.agent.tuplespace.Get;
import org.etri.ado.config.Settings;
import org.etri.ado.config.Configuration;

import akka.actor.AbstractActor;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.cluster.client.ClusterClientReceptionist;
import akka.cluster.singleton.ClusterSingletonManager;
import akka.cluster.singleton.ClusterSingletonManagerSettings;
import akka.japi.pf.ReceiveBuilder;

public class ADOActor extends AbstractActor {

	public static Props prop(AgentSystem system) {
		return ClusterSingletonManager.props(Props.create(ADOActor.class,system), PoisonPill.getInstance(),
				ClusterSingletonManagerSettings.create(system.getActorSystem()));
	}
	
	private final AgentSystem m_system;
			
	public ADOActor(AgentSystem system) {
		m_system = system;
	}
	
	@Override
	public void preStart() {
		ClusterClientReceptionist.get(getContext().system()).registerService(getSelf());
	}	
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();
		builder.match(GetAll.class, this::receiveGetAll);
		builder.match(GetById.class, this::receiveGetById);
		builder.match(GetByRole.class, this::receiveGetByRole);
		builder.match(GetByCapabilities.class, this::receiveGetByCapabilities);
		builder.match(Get.class, this::receiveGetTuple);
		
		builder.matchAny(this::unhandled);
		
		return builder.build();		
	}
	
	private void receiveGetAll(GetAll cmd) {
		m_system.getAgentRegistryActor().forward(cmd, getContext());
	}	

	private void receiveGetById(GetById cmd) {
		m_system.getAgentRegistryActor().forward(cmd, getContext());
	}
	
	private void receiveGetByRole(GetByRole cmd) {
		m_system.getAgentRegistryActor().forward(cmd, getContext());
	}	
		
	private void receiveGetByCapabilities(GetByCapabilities cmd) {
		m_system.getAgentRegistryActor().forward(cmd, getContext());
	}
	
	private void receiveGetTuple(Get cmd) {
		m_system.getTupleSpaceActor().forward(cmd, getContext());
	}

	
	
	
}
