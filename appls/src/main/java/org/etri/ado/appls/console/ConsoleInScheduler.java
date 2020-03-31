package org.etri.ado.appls.console;

import java.util.function.Function;

import org.etri.ado.appls.MoveDeltaXYCommander.MoveDeltaXY;
import org.etri.ado.appls.MoveToXYCommander.MoveToXY;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.camel.CamelMessage;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;

@SuppressWarnings("deprecation")
public class ConsoleInScheduler extends AbstractActor {

	private final LoggingAdapter m_log = Logging.getLogger(getContext().system(), this);	
	
	public static Props prop() {
		return Props.create(ConsoleInScheduler.class);
	}
	
	private Function<CamelMessage,MoveToXY> m_moveToXY = new MoveToXYActionBuilder();
	private Function<CamelMessage,MoveDeltaXY> m_deltaXY = new MoveDeltaXYActionBuilder();
		
	public ConsoleInScheduler() {
	}
	
	@Override
	public void postStop() {
	}	
	
	@Override
	public Receive createReceive() {
		ReceiveBuilder builder = ReceiveBuilder.create();
		builder.match(CamelMessage.class, this::receiveCommand);
		builder.matchAny(this::unhandled);
		
		return builder.build();
	}	

	private void receiveCommand( CamelMessage msg) {
		String cmd = (String)msg.body();
		if ( cmd.contains("delta") ) {
			MoveDeltaXY action = m_deltaXY.apply(msg);
			getContext().system().eventStream().publish(action);
		}
		else if ( cmd.contains("goto" ) ) {
			MoveToXY action = m_moveToXY.apply(msg);
			getContext().system().eventStream().publish(action);			
		}
	}
}
