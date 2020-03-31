package org.etri.ado.appls;

import org.etri.ado.appls.MoveToXYCommander.MoveToXY;
import org.etri.ado.device.ActionCommander;

import ros.msgs.geometry_msgs.Vector3;

public class MoveToXYCommander extends ActionCommander<MoveToXY> {

	public static class MoveToXY {				
		public float xPos;
		public float yPos;
		
		public MoveToXY(float xPos, float yPos) {
			this.xPos = xPos;
			this.yPos = yPos;
		}
	}	
	
	private static final float SCALE = 1.0f / 6.0f ;
	private static final float BORDER = 2.0f;
	
	public MoveToXYCommander() {
		
	}

	public void apply(MoveToXY target) throws Exception {
		float xPos = target.xPos * SCALE;
		float yPos = target.yPos * SCALE;
		
		if ( Math.abs(xPos) > BORDER ) {
			xPos =  -xPos;
		}
		
		if ( Math.abs(yPos) > BORDER ) {
			yPos = -yPos;
		}		
		
		Vector3 position = new Vector3(xPos, yPos, 0);
		getDevice().publish("/waffle/move_xy",  "geometry_msgs/Vector3", position);
		getLogger().info("MOVE To: ({},{})", xPos, yPos);	
		
	}
	
}
