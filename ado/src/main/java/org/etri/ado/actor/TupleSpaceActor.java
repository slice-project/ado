package org.etri.ado.actor;

import static java.util.concurrent.TimeUnit.SECONDS;

import org.etri.ado.agent.tuplespace.Get;
import org.etri.ado.agent.tuplespace.GetMatcher;
import org.etri.ado.agent.tuplespace.Put;
import org.etri.ado.agent.tuplespace.PutMatcher;
import org.etri.ado.agent.tuplespace.Remove;
import org.etri.ado.agent.tuplespace.RemoveMatcher;
import org.etri.ado.agent.tuplespace.SubscribeTo;
import org.etri.ado.agent.tuplespace.SubscribeToMatcher;
import org.javatuples.Tuple;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.ddata.DistributedData;
import akka.cluster.ddata.Replicator.ReadConsistency;
import akka.cluster.ddata.Replicator.ReadMajority;
import akka.cluster.ddata.Replicator.WriteAll;
import akka.cluster.ddata.Replicator.WriteConsistency;
import akka.cluster.ddata.Replicator.WriteMajority;
import akka.cluster.ddata.SelfUniqueAddress;
import akka.japi.pf.ReceiveBuilder;
import scala.concurrent.duration.Duration;

public class TupleSpaceActor <T extends Tuple> extends AbstractActor {
	
	public static final WriteConsistency writeMajority = new WriteMajority(Duration.create(3, SECONDS));
	public static final WriteAll writeAll = new WriteAll(Duration.create(3, SECONDS));
	public static final ReadConsistency readMajority = new ReadMajority(Duration.create(3, SECONDS));	
	
	public static <T> Props props(String agentId) {
		return Props.create(TupleSpaceActor.class, agentId);
	}

	private final ActorRef m_replicator = DistributedData.get(context().system()).replicator();
	private final SelfUniqueAddress m_node = DistributedData.get(getContext().getSystem()).selfUniqueAddress();
	
	private final ActorRef m_matchPut;
	private final ActorRef m_matchGet;
	private final ActorRef m_matchRemove;
	private final ActorRef m_matchSubscribeTo;
	
	public TupleSpaceActor(String spaceKey) {
		m_matchGet = context().actorOf(GetMatcher.props(m_replicator));
		m_matchPut = context().actorOf(PutMatcher.props(m_replicator, m_node, spaceKey));
		m_matchRemove = context().actorOf(RemoveMatcher.props(m_replicator, m_node, spaceKey));
		m_matchSubscribeTo = context().actorOf(SubscribeToMatcher.props(m_replicator));
	}

	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();
		
		builder.match(Get.class, this::receiveGet);
		builder.match(Put.class, this::receivePut);	
		builder.match(Remove.class, this::receiveRemove);
		builder.match(SubscribeTo.class, this::receiveSubscribeTo);
		builder.matchAny(this::unhandled);
		
		return builder.build();		
	}
	
	private void receiveSubscribeTo(SubscribeTo cmd) {
		m_matchSubscribeTo.forward(cmd, getContext());
	}

	private void receiveGet(Get cmd) {
		m_matchGet.forward(cmd, getContext());
	}

	private void receivePut(Put<T> cmd) {
		m_matchPut.forward(cmd, getContext());
	}
	
	private void receiveRemove(Remove cmd) {
		m_matchRemove.forward(cmd, getContext());
	}
}