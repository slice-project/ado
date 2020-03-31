package org.etri.ado.device.emulator;

import org.javatuples.Pair;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

public class AdversaryEmulator extends AbstractActor {
		
	private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	
	private float m_locX = 0f;
	private float m_locY = 0f;
	private float m_velX = 0f;
	private float m_velY = 0f;
	private final float m_mass = 1.0f;
	private final float m_dt = 0.1f;
	private final float m_damping = 0.25f;
	private final float m_max_speed = 1.3f;
	
	private static final float s_border = 2f;
	
	public static Props prop(Pair<Float, Float> loc) {
		return Props.create(AdversaryEmulator.class, loc);
	}
	
	public AdversaryEmulator(Pair<Float, Float> loc) {
		m_locX = loc.getValue0();
		m_locY = loc.getValue1();
		
//		getContext().system().eventStream().publish(new AddLocation(Pair.with(m_locX, m_locY)));
		log.info("publish[AddLocation({}, {}]", m_locX, m_locY);	
		
//		getContext().system().eventStream().publish(new AddVelocity(Pair.with(m_velX, m_velY)));
		log.info("publish[AddVelocity({}, {}]", m_velX, m_velY);		
	}

	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();
//		builder.match(MoveDeltaXY.class, this::receiveActionForce);
//		builder.match(MoveToXY.class, this::receiveMoveToXY);
		builder.matchAny(this::unhandled);
		
		return builder.build();
	}
	
//	private void receiveActionForce(MoveDeltaXY force) {		
//		m_velX = m_velX * ( 1 - m_damping );		
//		m_velX += ( force.delta.getValue0() / m_mass ) * m_dt;
//		
//		m_velY =  m_velY * ( 1 - m_damping );	
//		m_velY += ( force.delta.getValue1() / m_mass ) * m_dt;
//
//		double speed = Math.sqrt(m_velX * m_velX  + m_velY * m_velY);
//		if ( speed > m_max_speed ) {
//			m_velX = m_velX / (float)speed * m_max_speed;
//			m_velY = m_velY / (float)speed * m_max_speed;
//		}		
//		
//		getContext().system().eventStream().publish(new AddVelocity(Pair.with(m_velX, m_velY)));
//		log.info("publish[AddVelocity({}, {}]", m_velX, m_velY);
//		
//		
//		m_locX = m_locX + ( m_velX * m_dt );		
//		m_locY = m_locY + ( m_velY * m_dt );
//		
//		checkBorder();		
//
//		getContext().system().eventStream().publish(new AddLocation(Pair.with(m_locX, m_locY)));
//		log.info("publish[AddLocation({}, {}]", m_locX, m_locY);
//	}
//	
//	private void receiveMoveToXY(MoveToXY target) {
//		m_locX = target.loc.getValue0();
//		m_locY = target.loc.getValue1();
//		getContext().system().eventStream().publish(new AddLocation(Pair.with(m_locX, m_locY)));
//		log.info("publish[AddLocation({}, {}]", m_locX, m_locY);		
//	}
//	
//	private void checkBorder() {
//		if ( m_locX > s_border ) {
//			m_locX = s_border;
//		}
//		else if ( m_locX < -s_border ) {
//			m_locX = -s_border;
//		}
//		
//		if ( m_locY > s_border ) {
//			m_locY = s_border;
//		}
//		else if ( m_locX < -s_border ) {
//			m_locY = -s_border;
//		}		
//	}	
}
