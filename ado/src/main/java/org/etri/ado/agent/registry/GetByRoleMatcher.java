package org.etri.ado.agent.registry;

import java.util.Optional;

import org.etri.ado.actor.AgentRegistryActor;
import org.etri.ado.agent.AgentInfo;
import org.etri.ado.agent.registry.message.GetByRole;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.ddata.ORMultiMap;
import akka.cluster.ddata.Replicator;
import akka.cluster.ddata.Replicator.GetFailure;
import akka.cluster.ddata.Replicator.GetResponse;
import akka.cluster.ddata.Replicator.GetSuccess;
import akka.cluster.ddata.Replicator.NotFound;
import akka.japi.pf.ReceiveBuilder;
import scala.Option;

public class GetByRoleMatcher extends AbstractActor {
	
	private final ActorRef m_replicator;
	
	public static Props props(ActorRef replicator) {
		return Props.create(GetByRoleMatcher.class, replicator);
	}	
	
	public GetByRoleMatcher(ActorRef replicator) {
		m_replicator = replicator;
	}
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();
		
		builder.match(GetByRole.class, this::receiveGetByRole);
		builder.match(GetSuccess.class, this::isResponseToGetByRole, this::receiveGetByRoleSuccess);
		builder.match(NotFound.class, this::isResponseToGetByRole, this::receiveByRoleNotFound);
		builder.match(GetFailure.class, this::isResponseToGetByRole, this::receiveGetByRoleFailure);
	
		return builder.build();
	}

	private void receiveGetByRole(GetByRole g) {
		Optional<Object> ctx = Optional.of(new GetByRole.Context(sender(), g.role));
		m_replicator.tell(new Replicator.Get<>(AgentRegistryActor.roleToAgentKey, AgentRegistryActor.readMajority, ctx), self());
	}

	private boolean isResponseToGetByRole(GetResponse<?> response) {
		return response.key().equals(AgentRegistryActor.roleToAgentKey)
				&& (response.getRequest().orElse(null) instanceof GetByRole.Context);
	}

	private void receiveGetByRoleSuccess(GetSuccess<ORMultiMap<String, AgentInfo>> g) {
		GetByRole.Context ctx = (GetByRole.Context) g.getRequest().get();;

		Option<scala.collection.immutable.Set<AgentInfo>> option = g.dataValue().get(ctx.key);
		Optional<scala.collection.immutable.Set<AgentInfo>> optional = Optional.<scala.collection.immutable.Set<AgentInfo>>ofNullable(
				option.getOrElse(() -> null));

		ctx.sender.tell(optional, self());
	}

	private void receiveByRoleNotFound(NotFound<ORMultiMap<String, AgentInfo>> n) {
		GetByRole.Context ctx = (GetByRole.Context) n.getRequest().get();
		ctx.sender.tell(Optional.empty(), self());
	}

	private void receiveGetByRoleFailure(GetFailure<ORMultiMap<String, AgentInfo>> f) {
		// ReadMajority failure, try again with local read
		Optional<Object> ctx = Optional.of(sender());
		m_replicator.tell(new Replicator.Get<>(AgentRegistryActor.roleToAgentKey, Replicator.readLocal(), ctx), self());
	}	
}
