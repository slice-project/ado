package org.etri.ado.agent.tuplespace;

import java.util.Optional;

import org.etri.ado.actor.TupleSpaceActor;
import org.javatuples.KeyValue;
import org.javatuples.Tuple;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.ddata.Key;
import akka.cluster.ddata.LWWMap;
import akka.cluster.ddata.LWWMapKey;
import akka.cluster.ddata.Replicator.Update;
import akka.cluster.ddata.Replicator.UpdateFailure;
import akka.cluster.ddata.Replicator.UpdateSuccess;
import akka.cluster.ddata.Replicator.UpdateTimeout;
import akka.cluster.ddata.SelfUniqueAddress;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

@SuppressWarnings("unchecked")
public class PutMatcher <T extends Tuple> extends AbstractActor {
	private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);	
	
		
	private final ActorRef m_replicator;
	private final SelfUniqueAddress m_node;
	private final Key<LWWMap<String,T>> m_dataKey;
	
	public static Props props(ActorRef replicator, SelfUniqueAddress node, String key) {
		return Props.create(PutMatcher.class, replicator, node, key);
	}	
	
	public PutMatcher(ActorRef replicator, SelfUniqueAddress node, String key) {
		m_replicator = replicator;
		m_node = node;
		m_dataKey = LWWMapKey.create(key);
	}
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();		
		builder.match(Put.class, this::receivePut);
		
		return builder.build().orElse(matchOther());
	}
	
	private void receivePut(Put<T> p) {
		Optional<Object> ctx = Optional.of(new Put.Context<T>(p.tuple));
		Update<LWWMap<String,T>> update = new Update<>(m_dataKey, LWWMap.create(), TupleSpaceActor.writeAll, ctx,
				space -> updateTuple(space, p.tuple));
		m_replicator.tell(update, self());
	}

	private LWWMap<String,T> updateTuple(LWWMap<String,T> space, KeyValue<String,T> tuple) {
		T value = tuple.getValue();
		return space.put(m_node, tuple.getKey(), value);
	}
	
	private Receive matchOther() {
		return receiveBuilder().match(UpdateSuccess.class, u -> {
			Put.Context<Tuple> ctx = (Put.Context<Tuple>) u.getRequest().get();
			logger.info("inserted : " + m_dataKey.id()  + "-" + ctx.tuple.getKey() + ctx.tuple.getValue());
		}).match(UpdateTimeout.class, t -> {
			logger.error("Failded to insert tuple: UpdateTimeout");
			// will eventually be replicated
		}).match(UpdateFailure.class, f -> {
			throw new IllegalStateException("Unexpected failure: " + f);
		}).build();
	}
}
