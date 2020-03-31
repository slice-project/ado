package org.etri.ado.device.emulator;

import org.javatuples.Pair;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class LandmarkEmulator extends AbstractActor {
		
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private float m_locX = 0f;
	private float m_locY = 0f;

	public static Props prop(Pair<Float, Float> loc) {
		return Props.create(LandmarkEmulator.class, loc);
	}
	
	public LandmarkEmulator(Pair<Float, Float> loc) {
		m_locX = loc.getValue0();
		m_locY = loc.getValue1();
		
//		getContext().system().eventStream().publish(new AddLocation(Pair.with(m_locX, m_locY)));
		log.info("publish[AddLocation({}, {}]", m_locX, m_locY);
	}

	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();
//		builder.match(MoveToXY.class, this::receiveMoveToXY);
		builder.matchAny(this::unhandled);
		
		return builder.build();
	}
	
//	private void receiveMoveToXY(MoveToXY target) {
//		m_locX = target.loc.getValue0();
//		m_locY = target.loc.getValue1();
//		getContext().system().eventStream().publish(new AddLocation(Pair.with(m_locX, m_locY)));
//		log.info("publish[AddLocation({}, {}]", m_locX, m_locY);		
//	}
}
