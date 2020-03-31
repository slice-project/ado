package org.etri.ado.agent.registry;

import java.util.Optional;

import org.etri.ado.actor.AgentRegistryActor;
import org.etri.ado.agent.AgentInfo;
import org.etri.ado.agent.registry.message.GetById;

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
import scala.Option;

public class GetByIdMatcher extends AbstractActor {
	
	private final ActorRef m_replicator;
	
	public static Props props(ActorRef replicator) {
		return Props.create(GetByIdMatcher.class, replicator);
	}	
	
	public GetByIdMatcher(ActorRef replicator) {
		m_replicator = replicator;
	}
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();
		
		builder.match(GetById.class, this::receiveGetById);
		builder.match(GetSuccess.class, this::isResponseToGetById, this::receiveGetByIdSuccess);
		builder.match(NotFound.class, this::isResponseToGetById, this::receiveByIdNotFound);
		builder.match(GetFailure.class, this::isResponseToGetById, this::receiveGetByIdFailure);
	
		return builder.build();
	}

	private void receiveGetById(GetById g) {
		Optional<Object> ctx = Optional.of(new GetById.Context(sender(), g.id));
		m_replicator.tell(new Replicator.Get<>(AgentRegistryActor.idToAgentKey, AgentRegistryActor.readMajority, ctx), self());
	}

	private boolean isResponseToGetById(GetResponse<?> response) {
		return response.key().equals(AgentRegistryActor.idToAgentKey) && (response.getRequest().orElse(null) instanceof GetById.Context);
	}

	private void receiveGetByIdSuccess(GetSuccess<LWWMap<String, AgentInfo>> g) {
		GetById.Context ctx = (GetById.Context) g.getRequest().get();
		Option<AgentInfo> option = g.dataValue().get(ctx.key);
		Optional<AgentInfo> optional = Optional.<AgentInfo>ofNullable(option.getOrElse(() -> null));

		ctx.sender.tell(optional, self());
	}

	private void receiveByIdNotFound(NotFound<LWWMap<String, AgentInfo>> n) {
		GetById.Context ctx = (GetById.Context) n.getRequest().get();
		ctx.sender.tell(Optional.empty(), self());
	}

	private void receiveGetByIdFailure(GetFailure<LWWMap<String, AgentInfo>> f) {
		// ReadMajority failure, try again with local read
		Optional<Object> ctx = Optional.of(sender());
		m_replicator.tell(new Replicator.Get<>(AgentRegistryActor.idToAgentKey, Replicator.readLocal(), ctx), self());
	}
}
