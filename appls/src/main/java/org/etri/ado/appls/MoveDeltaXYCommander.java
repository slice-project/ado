package org.etri.ado.appls;

import java.util.Optional;

import org.etri.ado.appls.MoveDeltaXYCommander.MoveDeltaXY;
import org.etri.ado.device.ActionCommander;
import org.javatuples.Pair;
import org.javatuples.Tuple;

import ros.msgs.geometry_msgs.Vector3;

public class MoveDeltaXYCommander extends ActionCommander<MoveDeltaXY> {
		
	public static class MoveDeltaXY {
		public float deltaX;
		public float deltaY;
		
		public MoveDeltaXY(float deltaX, float deltaY) {
			this.deltaX = deltaX;
			this.deltaY = deltaY;
		}
	}	
	
	private static final float SCALE = 1.0f / 6.0f ;
	private static final float BORDER = 2.0f;
		
	public MoveDeltaXYCommander() {
		
	}

	@SuppressWarnings("unchecked")
	public void apply(MoveDeltaXY action) throws Exception {
		
		Optional<? extends Tuple> agent_loc = tupleSpace().get("predator1-loc");
		if ( !agent_loc.isPresent() ) return;
		
		float xPos = ((Pair<Float, Float>) agent_loc.get()).getValue0();
		float yPos = ((Pair<Float, Float>) agent_loc.get()).getValue1();
		
		float deltaX = action.deltaX * SCALE;
		float deltaY = action.deltaY * SCALE;
				
		if ( Math.abs(xPos + deltaX) > BORDER ) {
			deltaX =  -deltaX;
		}
		
		if ( Math.abs(yPos + deltaY) > BORDER ) {
			deltaY = -deltaY;
		}
		
		Vector3 delta = new Vector3(deltaX, deltaY, 0);
		getDevice().publish("/waffle/move_delta",  "geometry_msgs/Vector3", delta);
		getLogger().info("MOVE : ({},{}) => ({},{})", xPos, yPos, xPos + deltaX, yPos + deltaY);		
	}
}
