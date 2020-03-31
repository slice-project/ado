package org.etri.ado.agent.tuplespace;

import java.util.Optional;

import org.etri.ado.actor.TupleSpaceActor;
import org.javatuples.Tuple;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.ddata.Key;
import akka.cluster.ddata.LWWMap;
import akka.cluster.ddata.LWWMapKey;
import akka.cluster.ddata.Replicator;
import akka.cluster.ddata.Replicator.GetFailure;
import akka.cluster.ddata.Replicator.GetResponse;
import akka.cluster.ddata.Replicator.GetSuccess;
import akka.cluster.ddata.Replicator.NotFound;
import akka.japi.pf.ReceiveBuilder;
import scala.Option;

public class GetMatcher <T extends Tuple> extends AbstractActor {	
		
	private final ActorRef m_replicator;
	
	public static Props props(ActorRef replicator) {
		return Props.create(GetMatcher.class, replicator);
	}	
	
	public GetMatcher(ActorRef replicator) {
		m_replicator = replicator;
	}
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();		
		builder.match(Get.class, this::receiveGet);
		builder.match(GetSuccess.class, this::isResponseToGet, this::receiveGetSuccess);
		builder.match(NotFound.class, this::isResponseToGet, this::receiveNotFound);
		builder.match(GetFailure.class, this::isResponseToGet, this::receiveGetFailure);
		
		return builder.build();
	}
	
	private void receiveGet(Get g) {
		Key<LWWMap<String,T>> dataKey = LWWMapKey.create(g.agent);
		Optional<Object> ctx = Optional.of(new Get.Context(sender(), g.agent, g.key));
		m_replicator.tell(new Replicator.Get<>(dataKey, TupleSpaceActor.readMajority, ctx), self());
	}

	private boolean isResponseToGet(GetResponse<?> response) {
		return response.getRequest().orElse(null) instanceof Get.Context;
	}

	private void receiveGetSuccess(GetSuccess<LWWMap<String,T>> g) {
		Get.Context ctx = (Get.Context) g.getRequest().get();
		Option<T> option = g.dataValue().get(ctx.key);
		Optional<T> optional = Optional.<T> ofNullable(option.getOrElse(() -> null));
		
		ctx.sender.tell(optional, self());
	}

	private void receiveNotFound(NotFound<LWWMap<String,T>> n) {
		Get.Context ctx = (Get.Context) n.getRequest().get();
		ctx.sender.tell(Optional.empty(), self());
	}

	private void receiveGetFailure(GetFailure<LWWMap<String,T>> f) {
		// ReadMajority failure, try again with local read
		Optional<Object> request = f.getRequest();
		Get.Context ctx = (Get.Context)request.get();
				
		Key<LWWMap<String,T>> dataKey = LWWMapKey.create(ctx.agent);
		m_replicator.tell(new Replicator.Get<>(dataKey, Replicator.readLocal(), request), self());
	}
}
