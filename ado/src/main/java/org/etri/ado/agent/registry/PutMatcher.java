package org.etri.ado.agent.registry;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.etri.ado.actor.AgentRegistryActor;
import org.etri.ado.agent.AgentInfo;
import org.etri.ado.agent.registry.message.Put;
import org.etri.ado.agent.registry.message.UpdateContext;

import com.google.common.collect.Sets;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.ddata.LWWMap;
import akka.cluster.ddata.ORMultiMap;
import akka.cluster.ddata.Replicator.Update;
import akka.cluster.ddata.Replicator.UpdateFailure;
import akka.cluster.ddata.Replicator.UpdateSuccess;
import akka.cluster.ddata.Replicator.UpdateTimeout;
import akka.cluster.ddata.SelfUniqueAddress;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class PutMatcher extends AbstractActor {
	private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);	
	
		
	private final ActorRef m_replicator;
	private final SelfUniqueAddress m_node;
	
	public static Props props(ActorRef replicator, SelfUniqueAddress node) {
		return Props.create(PutMatcher.class, replicator, node);
	}	
	
	public PutMatcher(ActorRef replicator, SelfUniqueAddress node) {
		m_replicator = replicator;
		m_node = node;
	}
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();		
		builder.match(Put.class, this::receivePut);
		
		return builder.build().orElse(matchOther());		
	}
	
	private void receivePut(Put cmd) {
		Optional<Object> ctx = Optional.of(new UpdateContext(UpdateContext.BY_ID, cmd.agent));
		Update<LWWMap<String, AgentInfo>> update1 = new Update<>(AgentRegistryActor.idToAgentKey, LWWMap.create(), 
				AgentRegistryActor.writeMajority, ctx, map -> updateIdToAgents(map, cmd.agent));
		m_replicator.tell(update1, self());
		
		ctx = Optional.of(new UpdateContext(UpdateContext.BY_ROLE, cmd.agent));
		Update<ORMultiMap<String, AgentInfo>> update2 = new Update<>(AgentRegistryActor.roleToAgentKey, ORMultiMap.create(),
				AgentRegistryActor.writeMajority, ctx, map -> updateRoleToAgents(map, cmd.agent));
		m_replicator.tell(update2, self());
		
		ctx = Optional.of(new UpdateContext(UpdateContext.BY_CAP, cmd.agent));
		Update<ORMultiMap<String, AgentInfo>> update3 = new Update<>(AgentRegistryActor.capaToAgentKey, ORMultiMap.create(),
				AgentRegistryActor.writeMajority, ctx, map -> updateCapaToAgents(map, cmd.agent));
		m_replicator.tell(update3, self());
	}

	private LWWMap<String, AgentInfo> updateIdToAgents(LWWMap<String, AgentInfo> map, AgentInfo info) {
		return map.put(m_node, info.id, info);
	}
	
	private ORMultiMap<String, AgentInfo> updateRoleToAgents(ORMultiMap<String, AgentInfo> map, AgentInfo info) {
		return map.addBinding(m_node, info.role, info);
	}

	private ORMultiMap<String, AgentInfo> updateCapaToAgents(ORMultiMap<String, AgentInfo> map, AgentInfo info) {
		SortedSet<String> capabilities = new TreeSet<>(Arrays.asList(info.capabilities));

		for (int i = 1; i <= capabilities.size(); ++i) {
			Set<Set<String>> combinations = Sets.combinations(capabilities, i);
			Iterator<Set<String>> iter = combinations.iterator();

			while (iter.hasNext()) {
				Set<String> combination = iter.next();
				map = map.addBinding(m_node, combination.toString(), info);
			}
		}

		return map;
	}
	
	private Receive matchOther() {
		return receiveBuilder().match(UpdateSuccess.class, u -> {
			UpdateContext ctx = (UpdateContext) u.getRequest().get();
			logger.info("registered " + ctx.type + " : " + ctx.info);

		}).match(UpdateTimeout.class, t -> {
			// will eventually be replicated
		}).match(UpdateFailure.class, f -> {
			throw new IllegalStateException("Unexpected failure: " + f);
		}).build();
	}	
}
