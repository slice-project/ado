package org.etri.ado.appls;

import java.util.Optional;

import org.etri.ado.TupleSpace;
import org.etri.ado.agent.AbstractAgent;
import org.etri.ado.appls.MoveDeltaXYCommander.MoveDeltaXY;
import org.javatuples.Pair;
import org.javatuples.Tuple;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

public class Predator2Impl extends AbstractAgent {
	
	public Predator2Impl() {

	}

	@SuppressWarnings("unchecked")
	public INDArray getObservations() {
		
		TupleSpace tuples = getTupleSpace();
		
		float[][] obs = new float[1][12];
		
		Optional<? extends Tuple> agent_vel = tuples.get("predator2-velocity");
		if ( !agent_vel.isPresent() ) return null;
				
		obs[0][0] = ((Pair<Float, Float>) agent_vel.get()).getValue0();
		obs[0][1] = ((Pair<Float, Float>) agent_vel.get()).getValue1();
		
		Optional<? extends Tuple> agent_loc = tuples.get("predator2-loc");
		if ( !agent_loc.isPresent() ) return null;
		
		obs[0][2] = ((Pair<Float, Float>) agent_loc.get()).getValue0();
		obs[0][3] = ((Pair<Float, Float>) agent_loc.get()).getValue1();
				
		Optional<? extends Tuple> agent1_loc = tuples.get("predator1-loc");
		if ( !agent_loc.isPresent() ) return null;
		
		obs[0][4] = ((Pair<Float, Float>) agent1_loc.get()).getValue0() - obs[0][2];
		obs[0][5] = ((Pair<Float, Float>) agent1_loc.get()).getValue1() - obs[0][3];
		
		Optional<? extends Tuple> agent2_loc = tuples.get("predator3-loc");
		if ( !agent2_loc.isPresent() ) return null;
		
		obs[0][6] = ((Pair<Float, Float>) agent2_loc.get()).getValue0() - obs[0][2];
		obs[0][7] = ((Pair<Float, Float>) agent2_loc.get()).getValue1() - obs[0][3];
		
		Optional<? extends Tuple> agent3_loc = tuples .get("prey-loc");
		if ( !agent3_loc.isPresent() ) return null;
		
		obs[0][8] = ((Pair<Float, Float>) agent3_loc.get()).getValue0() - obs[0][2];
		obs[0][9] = ((Pair<Float, Float>) agent3_loc.get()).getValue1() - obs[0][3];

		Optional<? extends Tuple> agent3_vel = tuples.get("prey-velocity");
		if ( !agent3_vel.isPresent() ) return null;
		
		obs[0][10] = ((Pair<Float, Float>) agent3_vel.get()).getValue0();
		obs[0][11] = ((Pair<Float, Float>) agent3_vel.get()).getValue1();
		
		return Nd4j.create(obs);
	}

	public void setActions(INDArray[] actions) {
		float deltaX = actions[0].getFloat(1) - actions[0].getFloat(2);
		float deltaY = actions[0].getFloat(3) - actions[0].getFloat(4);
		
		getActionStream().publish(new MoveDeltaXY(deltaX, deltaY));
	}
}
