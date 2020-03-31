package org.etri.ado.agent.registry;

import java.util.Map;
import java.util.Optional;

import org.etri.ado.actor.AgentRegistryActor;
import org.etri.ado.agent.AgentInfo;
import org.etri.ado.agent.registry.message.GetAll;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.ddata.LWWMap;
import akka.cluster.ddata.Replicator;
import akka.cluster.ddata.Replicator.GetFailure;
import akka.cluster.ddata.Replicator.GetResponse;
import akka.cluster.ddata.Replicator.GetSuccess;
import akka.cluster.ddata.Replicator.NotFound;
import akka.japi.pf.ReceiveBuilder;

public class GetAllMatcher extends AbstractActor {
	
	private final ActorRef m_replicator;
	
	public static Props props(ActorRef replicator) {
		return Props.create(GetAllMatcher.class, replicator);
	}	
	
	public GetAllMatcher(ActorRef replicator) {
		m_replicator = replicator;
	}
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();
		
		builder.match(GetAll.class, this::receiveGetAll);
		builder.match(GetSuccess.class, this::isResponseToGetAll, this::receiveGetAllSuccess);
		builder.match(NotFound.class, this::isResponseToGetAll, this::receiveByAllNotFound);
		builder.match(GetFailure.class, this::isResponseToGetAll, this::receiveGetAllFailure);
	
		return builder.build();
	}
	
	private void receiveGetAll(GetAll g) {
		Optional<Object> ctx = Optional.of(new GetAll.Context(sender()));
		m_replicator.tell(new Replicator.Get<>(AgentRegistryActor.idToAgentKey, AgentRegistryActor.readMajority, ctx), self());
	}

	private boolean isResponseToGetAll(GetResponse<?> response) {
		return response.key().equals(AgentRegistryActor.idToAgentKey) && (response.getRequest().orElse(null) instanceof GetAll.Context);
	}

	private void receiveGetAllSuccess(GetSuccess<LWWMap<String, AgentInfo>> g) {
		GetAll.Context ctx = (GetAll.Context) g.getRequest().get();
		Map<String, AgentInfo> entries = g.dataValue().getEntries();

		ctx.sender.tell(entries.values().toArray(new AgentInfo[entries.size()]), self());
	}

	private void receiveByAllNotFound(NotFound<LWWMap<String, AgentInfo>> n) {
		GetAll.Context ctx = (GetAll.Context) n.getRequest().get();
		ctx.sender.tell(Optional.empty(), self());
	}

	private void receiveGetAllFailure(GetFailure<LWWMap<String, AgentInfo>> f) {
		// ReadMajority failure, try again with local read
		Optional<Object> ctx = Optional.of(sender());
		m_replicator.tell(new Replicator.Get<>(AgentRegistryActor.idToAgentKey, Replicator.readLocal(), ctx), self());
	}
}
