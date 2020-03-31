package org.etri.ado.misc;

import com.fasterxml.jackson.databind.JsonNode;

import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;
import ros.msgs.geometry_msgs.Twist;
import ros.msgs.geometry_msgs.Vector3;
import ros.tools.MessageUnpacker;

public class ROSClientTest {

	public static void main(String[] args) throws Exception {
		if(args.length != 1){
			System.out.println("Need the rosbridge websocket URI provided as argument. For example:\n\tws://localhost:9090");
			System.exit(0);
		}
		
				
		RosBridge bridge = new RosBridge();
		bridge.connect(args[0], true);

		bridge.subscribe(SubscriptionRequestMsg.generate("/turtle1/cmd_vel")
					.setType("geometry_msgs/Twist")
					.setThrottleRate(1)
					.setQueueLength(1),
				new RosListenDelegate() {
					@Override
					public void receive(JsonNode data, String stringRep) {
						MessageUnpacker<Twist> unpacker = new MessageUnpacker<Twist>(Twist.class);
						Twist msg = unpacker.unpackRosMessage(data);
						System.out.println("position(" + msg.linear.x + ", " + msg.linear.y + ")");
					}
				}
		);
		
		Vector3 linear = new Vector3(1.0, 0.0, 0.0);
		Vector3 angular = new Vector3(0.0, 0.0, 0.0);
		Twist cmd = new Twist(linear, angular);
		bridge.publish("turtle1/cmd_vel",  "geometry_msgs/Twist", cmd);
		bridge.publish("turtle1/cmd_vel",  "geometry_msgs/Twist", cmd);
		bridge.publish("turtle1/cmd_vel",  "geometry_msgs/Twist", cmd);
		bridge.publish("turtle1/cmd_vel",  "geometry_msgs/Twist", cmd);
		
		
//		bridge.subscribe(SubscriptionRequestMsg.generate("/odom")
//				.setType("nav_msgs/Odometry")
//				.setThrottleRate(1)
//				.setQueueLength(1),
//			new RosListenDelegate() {
//				@Override
//				public void receive(JsonNode data, String stringRep) {
//					JsonNode  position = data.get("msg").get("pose").get("pose").get("position");
//					double x = position.get("x").asDouble();
//					double y = position.get("y").asDouble();
//					System.out.println("POS[x = " + x + ", " + y + "]");
//				}
//			}
//	);		



//		Publisher pub = new Publisher("/java_to_ros", "std_msgs/String", bridge);
//
//		for(int i = 0; i < 100; i++) {
//			pub.publish(new PrimitiveMsg<String>("hello from java " + i));
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
	}
}
