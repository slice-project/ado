package org.etri.ado.device.emulator;

import java.util.function.Consumer;

import org.etri.ado.device.DeviceBinding;

import ros.msgs.geometry_msgs.Vector3;

public class EmulatorDevice implements DeviceBinding {
	
	private float m_locX = 0f;
	private float m_locY = 0f;
	private float m_velX = 0f;
	private float m_velY = 0f;
	private final float m_mass = 1.0f;
	private final float m_dt = 0.1f;
	private final float m_damping = 0.25f;
	private final float m_max_speed = 1.3f;

	private Consumer<Vector3> m_locationUpdater;
	private Consumer<Vector3> m_velocityUpdater;
	
	private static final float s_border = 2f;
				
	public EmulatorDevice(float xPos, float yPos) {
		m_locX = xPos;
		m_locY = yPos;
	}

	@Override
	public void connect(String uri) {
		m_locationUpdater.accept(new Vector3(m_locX, m_locY, 0));
		m_velocityUpdater.accept(new Vector3(m_velX, m_velY, 0));
	}

	@Override
	public <T> void publish(String topic, String type, T msg) {
		if ( ! (msg instanceof Vector3) ) return;
		
		if ( topic.equals("/waffle/move_delta") ) {
			receiveActionForce((Vector3)msg);
		}
		else if ( topic.equals("/waffle/move_xy") ) {
			receiveMoveToXY((Vector3)msg);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> void subscribe(String topic, String type, Class<?> msgType, Consumer<T> subscriber) {
		
		if ( topic.equals("/waffle/current_loc") ) {
			m_locationUpdater = (Consumer<Vector3>) subscriber;
		}
		else if ( topic.equals("/waffle/velocity") ) {
			m_velocityUpdater = (Consumer<Vector3>) subscriber;
		}
	}

	private void receiveActionForce(Vector3 force) {		
		
//		log.info("received[MoveDeltaXY({}, {}]", force.x, force.y);
		
		m_velX = m_velX * ( 1 - m_damping );		
		m_velX += ( force.x / m_mass ) * m_dt;
		
		m_velY =  m_velY * ( 1 - m_damping );	
		m_velY += ( force.y / m_mass ) * m_dt;

		double speed = Math.sqrt(m_velX * m_velX  + m_velY * m_velY);
		if ( speed > m_max_speed ) {
			m_velX = m_velX / (float)speed * m_max_speed;
			m_velY = m_velY / (float)speed * m_max_speed;
		}		
		m_velocityUpdater.accept(new Vector3(m_velX, m_velY, 0));
		
		
//		getContext().system().eventStream().publish(new AddVelocity(Pair.with(m_velX, m_velY)));
//		log.info("publish[AddVelocity({}, {}]", m_velX, m_velY);
		
		
		m_locX = m_locX + ( m_velX * m_dt );		
		m_locY = m_locY + ( m_velY * m_dt );
		
		checkBorder();

		m_locationUpdater.accept(new Vector3(m_locX, m_locY, 0));
		
//		getContext().system().eventStream().publish(new AddLocation(Pair.with(m_locX, m_locY)));
//		log.info("publish[AddLocation({}, {}]", m_locX, m_locY);
			
	}
	
	private void receiveMoveToXY(Vector3 target) {
		m_locX = (float)target.x;
		m_locY = (float)target.y;
		
		m_locationUpdater.accept(new Vector3(m_locX, m_locY, 0));
		
//		getContext().system().eventStream().publish(new AddLocation(Pair.with(m_locX, m_locY)));
//		log.info("publish[AddLocation({}, {}]", m_locX, m_locY);		
	}

	private void checkBorder() {
		if ( m_locX > s_border ) {
			m_locX = s_border;
		}
		else if ( m_locX < -s_border ) {
			m_locX = -s_border;
		}
		
		if ( m_locY > s_border ) {
			m_locY = s_border;
		}
		else if ( m_locX < -s_border ) {
			m_locY = -s_border;
		}		
	}	
	
}
