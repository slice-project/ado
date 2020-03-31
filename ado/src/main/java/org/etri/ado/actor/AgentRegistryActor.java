package org.etri.ado.actor;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.etri.ado.agent.AgentInfo;
import org.etri.ado.agent.registry.GetAllMatcher;
import org.etri.ado.agent.registry.GetByCapabilitiesMatcher;
import org.etri.ado.agent.registry.GetByIdMatcher;
import org.etri.ado.agent.registry.GetByRoleMatcher;
import org.etri.ado.agent.registry.PutMatcher;
import org.etri.ado.agent.registry.RemoveMatcher;
import org.etri.ado.agent.registry.message.GetAll;
import org.etri.ado.agent.registry.message.GetByCapabilities;
import org.etri.ado.agent.registry.message.GetById;
import org.etri.ado.agent.registry.message.GetByRole;
import org.etri.ado.agent.registry.message.Put;
import org.etri.ado.agent.registry.message.Remove;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.ddata.DistributedData;
import akka.cluster.ddata.Key;
import akka.cluster.ddata.LWWMap;
import akka.cluster.ddata.LWWMapKey;
import akka.cluster.ddata.ORMultiMap;
import akka.cluster.ddata.ORMultiMapKey;
import akka.cluster.ddata.Replicator.ReadAll;
import akka.cluster.ddata.Replicator.ReadConsistency;
import akka.cluster.ddata.Replicator.WriteAll;
import akka.cluster.ddata.Replicator.WriteConsistency;
import akka.cluster.ddata.SelfUniqueAddress;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.Duration;


public class AgentRegistryActor extends AbstractActor {

	public final static WriteConsistency writeMajority = new WriteAll(Duration.create(3, SECONDS));
	public final static ReadConsistency readMajority = new ReadAll(Duration.create(3, SECONDS));
		
	public final static Key<LWWMap<String, AgentInfo>> idToAgentKey = LWWMapKey.create("id-to-agent-mapping");
	public final static Key<ORMultiMap<String, AgentInfo>> roleToAgentKey = ORMultiMapKey.create("role-to-agent-mapping");
	public final static Key<ORMultiMap<String, AgentInfo>> capaToAgentKey = ORMultiMapKey.create("capa-to-agent-mapping");
	
	public static Props props() {
		return Props.create(AgentRegistryActor.class);
	}

	private final ActorRef m_replicator = DistributedData.get(context().system()).replicator();
	private final SelfUniqueAddress m_node = DistributedData.get(getContext().getSystem()).selfUniqueAddress();
	
	private final ActorRef m_matchPut;
	private final ActorRef m_matchRemove;
	private final ActorRef m_matchGetAll;
	private final ActorRef m_matchGetById;
	private final ActorRef m_matchGetByRole;
	private final ActorRef m_matchGetByCapabilities;


	public AgentRegistryActor() {
		m_matchPut = context().actorOf(PutMatcher.props(m_replicator, m_node));
		m_matchGetAll = context().actorOf(GetAllMatcher.props(m_replicator));
		m_matchRemove = context().actorOf(RemoveMatcher.props(m_replicator, m_node));
		m_matchGetById = context().actorOf(GetByIdMatcher.props(m_replicator));
		m_matchGetByRole = context().actorOf(GetByRoleMatcher.props(m_replicator));
		m_matchGetByCapabilities = context().actorOf(GetByCapabilitiesMatcher.props(m_replicator));
	}

	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();
		
		builder.match(GetAll.class, this::receiveGetAll);
		builder.match(Put.class, this::receivePut);
		builder.match(Remove.class, this::receiveRemove);
		builder.match(GetById.class, this::receiveGetById);
		builder.match(GetByRole.class, this::receiveGetByRole);
		builder.match(GetByCapabilities.class, this::receiveGetByCapabilities);
		builder.matchAny(this::unhandled);		
		
		return builder.build();
	}
		
	private void receiveGetAll(GetAll cmd) {
		m_matchGetAll.forward(cmd, getContext());
	}

	private void receivePut(Put cmd) {
		m_matchPut.forward(cmd, getContext());
	}

	private void receiveRemove(Remove cmd) {
		m_matchRemove.forward(cmd, getContext());
	}	

	private void receiveGetById(GetById cmd) {
		m_matchGetById.forward(cmd, getContext());
	}

	private void receiveGetByRole(GetByRole cmd) {
		m_matchGetByRole.forward(cmd, getContext());
	}

	private void receiveGetByCapabilities(GetByCapabilities cmd) {
		m_matchGetByCapabilities.forward(cmd, getContext());
	}	
}