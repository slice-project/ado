package org.etri.ado.agent.tuplespace;

import java.util.Optional;

import org.etri.ado.actor.TupleSpaceActor;
import org.javatuples.KeyValue;
import org.javatuples.Tuple;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.AbstractActor.Receive;
import akka.cluster.ddata.Key;
import akka.cluster.ddata.LWWMap;
import akka.cluster.ddata.LWWMapKey;
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

@SuppressWarnings("unchecked")
public class RemoveMatcher <T extends Tuple> extends AbstractActor {
	private final LoggingAdapter logger = Logging.getLogger(getContext().system(), this);	
	
		
	private final ActorRef m_replicator;
	private final SelfUniqueAddress m_node;
	private final Key<LWWMap<String,T>> m_dataKey;
	
	public static Props props(ActorRef replicator, SelfUniqueAddress node, String key) {
		return Props.create(RemoveMatcher.class, replicator, node, key);
	}	
	
	public RemoveMatcher(ActorRef replicator, SelfUniqueAddress node, String key) {
		m_replicator = replicator;
		m_node = node;
		m_dataKey = LWWMapKey.create(key);
	}
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();		
		
		builder.match(Remove.class, this::receiveRemove);
		builder.match(GetSuccess.class, this::isResponseToRemove, this::receiveRemoveSuccess);
		builder.match(NotFound.class, this::isResponseToRemove, n -> {});
		builder.match(GetFailure.class, this::isResponseToRemove, this::receiveRemoveGetFailure);
		
		return builder.build().orElse(matchOther());
	}
	
	private void receiveRemove(Remove rm) {
		// Try to fetch latest from a majority of nodes first, since ORMap
		// remove must have seen the item to be able to remove it.
		Optional<Object> ctx = Optional.of(rm);
		m_replicator.tell(new Replicator.Get<>(m_dataKey, TupleSpaceActor.readMajority, ctx), self());
	}

	private void receiveRemoveSuccess(GetSuccess<LWWMap<String,T>> g) {
		Remove rm = (Remove) g.getRequest().get();
		removeTuple(rm.key);
	}

	private void receiveRemoveGetFailure(GetFailure<LWWMap<String,T>> f) {
		// ReadMajority failed, fall back to best effort local value
		Remove rm = (Remove) f.getRequest().get();
		removeTuple(rm.key);
	}

	private void removeTuple(String key) {
		Update<LWWMap<String,T>> update = new Update<>(m_dataKey, LWWMap.create(), TupleSpaceActor.writeMajority,
				space -> space.remove(m_node, key));
		m_replicator.tell(update, self());
	}

	private boolean isResponseToRemove(GetResponse<?> response) {
		return response.key().equals(m_dataKey) && (response.getRequest().orElse(null) instanceof Remove);		
	}
	
	private Receive matchOther() {
		return receiveBuilder().match(UpdateSuccess.class, u -> {
			 Put.Context<Tuple> ctx = (Put.Context<Tuple>) u.getRequest().get();
			 logger.info("removed : " + m_dataKey.id()  + "-" + ctx.tuple.getKey() + " : " + ctx.tuple.getValue());
		}).match(UpdateTimeout.class, t -> {
			// will eventually be replicated
		}).match(UpdateFailure.class, f -> {
			throw new IllegalStateException("Unexpected failure: " + f);
		}).build();
	}
}
