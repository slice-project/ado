package org.etri.ado.device.emulator;

import org.javatuples.Pair;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class SpeakerEmulator extends AbstractActor {
		
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private float m_locX = 0f;
	private float m_locY = 0f;
	private Color m_previous = Color.Init;

	public static Props prop(Pair<Float, Float> loc) {
		return Props.create(SpeakerEmulator.class, loc);
	}
	
	public SpeakerEmulator(Pair<Float, Float> loc) {
		m_locX = loc.getValue0();
		m_locY = loc.getValue1();
		
//		getContext().system().eventStream().publish(new AddLocation(Pair.with(m_locX, m_locY)));
		log.info("publish[AddLocation({}, {}]", m_locX, m_locY);	
	}

	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();
//		builder.match(MoveToXY.class, this::receiveMoveToXY);
//		builder.match(Speak.class, this::receiveSpeak);
		builder.matchAny(this::unhandled);
		
		return builder.build();
	}
		
//	private void receiveMoveToXY(MoveToXY target) {
//		m_locX = target.loc.getValue0();
//		m_locY = target.loc.getValue1();
//		getContext().system().eventStream().publish(new AddLocation(Pair.with(m_locX, m_locY)));
//		log.info("publish[AddLocation({}, {}]", m_locX, m_locY);		
//	}
	
	private static enum Color {
		Init,
		Red,
		Green,
		Blue
	}
	
//	private void receiveSpeak(Speak word) {				
//		if ( word.target.getValue0() > 0.5 ) {
//			if ( m_previous == Color.Init || m_previous != Color.Red ) {
//				System.out.println("Go to the red Landmark!");
//				getContext().system().eventStream().publish(new AddAction(word.target));
//				log.info("publish[AddAction({}, {}, {}]", word.target.getValue0(), word.target.getValue1(), word.target.getValue2());
//			}
//			m_previous = Color.Red;
//		}
//		else if ( word.target.getValue1() > 0.5 ) {
//			if ( m_previous == Color.Init || m_previous != Color.Green ) {
//				System.out.println("Go to the green landmark!");
//				getContext().system().eventStream().publish(new AddAction(word.target));
//				log.info("publish[AddAction({}, {}, {}]", word.target.getValue0(), word.target.getValue1(), word.target.getValue2());				
//			}
//			m_previous = Color.Green;
//		}
//		else {
//			if ( m_previous == Color.Init || m_previous != Color.Blue ) {
//				System.out.println("Go to the blue landmark!");
//				getContext().system().eventStream().publish(new AddAction(word.target));
//				log.info("publish[AddAction({}, {}, {}]", word.target.getValue0(), word.target.getValue1(), word.target.getValue2());				
//			}
//			m_previous = Color.Blue;
//		}
//	}
}
