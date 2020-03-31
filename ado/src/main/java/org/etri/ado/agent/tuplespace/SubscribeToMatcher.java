package org.etri.ado.agent.tuplespace;

import org.javatuples.Tuple;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.ddata.Key;
import akka.cluster.ddata.LWWMap;
import akka.cluster.ddata.LWWMapKey;
import akka.cluster.ddata.Replicator.Subscribe;
import akka.japi.pf.ReceiveBuilder;

public class SubscribeToMatcher <T extends Tuple> extends AbstractActor {	
		
	private final ActorRef m_replicator;
	
	public static Props props(ActorRef replicator) {
		return Props.create(SubscribeToMatcher.class, replicator);
	}	
	
	public SubscribeToMatcher(ActorRef replicator) {
		m_replicator = replicator;
	}
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();		
		builder.match(SubscribeTo.class, this::receiveSubscribeTo);
		builder.matchAny(this::unhandled);
		
		return builder.build();
	}
	
	private void receiveSubscribeTo(SubscribeTo cmd) {
		Key<LWWMap<String,T>> dataKey = LWWMapKey.create(cmd.agent);
		m_replicator.tell(new Subscribe<LWWMap<String,T>>(dataKey, getSender()), ActorRef.noSender());
	}
}
