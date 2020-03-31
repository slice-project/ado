package org.etri.ado.agent.registry;

import java.util.Arrays;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import org.etri.ado.actor.AgentRegistryActor;
import org.etri.ado.agent.AgentInfo;
import org.etri.ado.agent.registry.message.GetByCapabilities;

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

public class GetByCapabilitiesMatcher extends AbstractActor {
	
	private final ActorRef m_replicator;
	
	public static Props props(ActorRef replicator) {
		return Props.create(GetByCapabilitiesMatcher.class, replicator);
	}	
	
	public GetByCapabilitiesMatcher(ActorRef replicator) {
		m_replicator = replicator;
	}
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();
		
		builder.match(GetByCapabilities.class, this::receiveGetByCapabilities);
		builder.match(GetSuccess.class, this::isResponseToGetByCapabilities, this::receiveGetByCapabilitiesSuccess);
		builder.match(NotFound.class, this::isResponseToGetByCapabilities, this::receiveByCapabilitiesNotFound);
		builder.match(GetFailure.class, this::isResponseToGetByCapabilities, this::receiveGetByCapabilitiesFailure);
	
		return builder.build();
	}

	private void receiveGetByCapabilities(GetByCapabilities g) {
		Optional<Object> ctx = Optional.of(new GetByCapabilities.Context(sender(), g.capabilities));
		m_replicator.tell(new Replicator.Get<>(AgentRegistryActor.capaToAgentKey, AgentRegistryActor.readMajority, ctx), self());
	}

	private boolean isResponseToGetByCapabilities(GetResponse<?> response) {
		return response.key().equals(AgentRegistryActor.capaToAgentKey)
				&& (response.getRequest().orElse(null) instanceof GetByCapabilities.Context);
	}

	private void receiveGetByCapabilitiesSuccess(GetSuccess<ORMultiMap<String, AgentInfo>> g) {
		GetByCapabilities.Context ctx = (GetByCapabilities.Context) g.getRequest().get();
		SortedSet<String> capabilities = new TreeSet<>(Arrays.asList(ctx.capabilities));

		Option<scala.collection.immutable.Set<AgentInfo>> option = g.dataValue().get(capabilities.toString());
		Optional<scala.collection.immutable.Set<AgentInfo>> optional = Optional.<scala.collection.immutable.Set<AgentInfo>>ofNullable(
				option.getOrElse(() -> null));

		ctx.sender.tell(optional, self());
	}

	private void receiveByCapabilitiesNotFound(NotFound<ORMultiMap<String, AgentInfo>> n) {
		GetByCapabilities.Context ctx = (GetByCapabilities.Context) n.getRequest().get();
		ctx.sender.tell(Optional.empty(), self());
	}

	private void receiveGetByCapabilitiesFailure(GetFailure<ORMultiMap<String, AgentInfo>> f) {
		// ReadMajority failure, try again with local read
		Optional<Object> ctx = Optional.of(sender());
		m_replicator.tell(new Replicator.Get<>(AgentRegistryActor.capaToAgentKey, Replicator.readLocal(), ctx), self());
	}
}
