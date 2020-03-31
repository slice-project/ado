package org.etri.ado.agent.registry;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.etri.ado.actor.AgentRegistryActor;
import org.etri.ado.agent.AgentInfo;
import org.etri.ado.agent.registry.message.GetById;
import org.etri.ado.agent.registry.message.Remove;
import org.etri.ado.agent.registry.message.RemoveContext;
import org.etri.ado.agent.registry.message.UpdateContext;

import com.google.common.collect.Sets;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.ddata.LWWMap;
import akka.cluster.ddata.ORMultiMap;
import akka.cluster.ddata.Replicator;
import akka.cluster.ddata.Replicator.GetFailure;
import akka.cluster.ddata.Replicator.GetResponse;
import akka.cluster.ddata.Replicator.GetSuccess;
import akka.cluster.ddata.Replicator.NotFound;
import akka.cluster.ddata.Replicator.Update;
import akka.cluster.ddata.Replicator.UpdateFailure;
import akka.cluster.ddata.Replicator.UpdateSuccess;
import akka.cluster.ddata.Replicator.UpdateTimeout;
import akka.cluster.ddata.SelfUniqueAddress;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import scala.Option;

public class RemoveMatcher extends AbstractActor {
	private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);	
	
		
	private final ActorRef m_replicator;
	private final SelfUniqueAddress m_node;
	
	public static Props props(ActorRef replicator, SelfUniqueAddress node) {
		return Props.create(RemoveMatcher.class, replicator, node);
	}	
	
	public RemoveMatcher(ActorRef replicator, SelfUniqueAddress node) {
		m_replicator = replicator;
		m_node = node;
	}
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();		
		builder.match(Remove.class, this::receiveRemove);
		builder.match(GetSuccess.class, this::isResponseToRemoveItem,this::receiveRemoveSuccess);
		builder.match(GetFailure.class, this::isResponseToRemoveItem, this::receiveRemoveFailure);
		builder.match(NotFound.class, this::isResponseToRemoveItem, n -> {});
		
		return builder.build().orElse(matchOther());
	}
	
	private boolean isResponseToRemoveItem(GetResponse<?> response) {
		return response.key().equals(AgentRegistryActor.idToAgentKey) && (response.getRequest().orElse(null) instanceof Remove.Context);
	}	
	
	private void receiveRemove(Remove g) {
		Optional<Object> ctx = Optional.of(new Remove.Context(g.id, null));
		m_replicator.tell(new Replicator.Get<>(AgentRegistryActor.idToAgentKey, AgentRegistryActor.readMajority, ctx), self());		
	}

	private void receiveRemoveSuccess(GetSuccess<LWWMap<String, AgentInfo>> g) {
		Remove.Context ctx = (Remove.Context) g.getRequest().get();
		Option<AgentInfo> removed = g.dataValue().get(ctx.id);
		if ( removed.isEmpty() ) return;
		removeAgent(ctx.id, removed.get());
	}

	private void receiveRemoveFailure(GetFailure<LWWMap<String, AgentInfo>> f) {
		GetById.Context ctx = (GetById.Context) f.getRequest().get();
		removeAgent(ctx.key, null);
	}
	
	private void removeAgent(String id, AgentInfo info) {
		Optional<Object> ctx = Optional.of(new RemoveContext(AgentRegistryActor.idToAgentKey.id(), info));		
		Update<LWWMap<String, AgentInfo>> remove1 = new Update<>(AgentRegistryActor.idToAgentKey, LWWMap.create(), 
				AgentRegistryActor.writeMajority, ctx, map -> removeIdToAgents(map, info));
		m_replicator.tell(remove1, self());		
		
		ctx = Optional.of(new RemoveContext(AgentRegistryActor.roleToAgentKey.id(), info));		
		Update<ORMultiMap<String, AgentInfo>> remove2 = new Update<>(AgentRegistryActor.roleToAgentKey, ORMultiMap.create(),
				AgentRegistryActor.writeMajority, ctx, map -> removeRoleToAgents(map, info));
		m_replicator.tell(remove2, self());
		
		ctx = Optional.of(new RemoveContext(AgentRegistryActor.capaToAgentKey.id(), info));		
		Update<ORMultiMap<String, AgentInfo>> remove3 = new Update<>(AgentRegistryActor.capaToAgentKey, ORMultiMap.create(),
				AgentRegistryActor.writeMajority, ctx, map -> removeCapaToAgents(map, info));
		m_replicator.tell(remove3, self());		
		
	}
	
	private LWWMap<String, AgentInfo> removeIdToAgents(LWWMap<String, AgentInfo> map, AgentInfo info) {
		return map.remove(m_node,  info.id);
	}
	
	private ORMultiMap<String, AgentInfo> removeRoleToAgents(ORMultiMap<String, AgentInfo> map, AgentInfo info) {
		return map.remove(m_node, info.role);
	}

	private ORMultiMap<String, AgentInfo> removeCapaToAgents(ORMultiMap<String, AgentInfo> map, AgentInfo info) {
		SortedSet<String> capabilities = new TreeSet<>(Arrays.asList(info.capabilities));

		for (int i = 1; i <= capabilities.size(); ++i) {
			Set<Set<String>> combinations = Sets.combinations(capabilities, i);
			Iterator<Set<String>> iter = combinations.iterator();

			while (iter.hasNext()) {
				Set<String> combination = iter.next();
				map = map.removeBinding(m_node, combination.toString(), info);
			}
		}

		return map;
	}	
	
	
	private Receive matchOther() {
		return receiveBuilder().match(UpdateSuccess.class, u -> {
			RemoveContext ctx = (RemoveContext) u.getRequest().get();
			logger.info("removed from " + ctx.key + " : "+ ctx.info);
		}).match(UpdateTimeout.class, t -> {
			// will eventually be replicated
		}).match(UpdateFailure.class, f -> {
			throw new IllegalStateException("Unexpected failure: " + f);
		}).build();
	}	
}
