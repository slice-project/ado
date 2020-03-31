package org.etri.ado.appls;

import org.etri.ado.device.ContextUpdater;
import org.javatuples.Pair;

import ros.msgs.geometry_msgs.Vector3;

public class LocationUpdater extends ContextUpdater<Vector3> {

	public void accept(Vector3 msg) {
		tupleSpace().put("loc", Pair.with((float)msg.x, (float)msg.y));		
	}
}
