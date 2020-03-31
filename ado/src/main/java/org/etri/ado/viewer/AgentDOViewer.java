package org.etri.ado.viewer;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClientSettings;

public class AgentDOViewer {

	public static void main(String[] args) throws Exception {
		
		Config conf = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 1485)
				.withFallback(ConfigFactory.load("viewer"));

		ActorSystem system = ActorSystem.create("AgentDOViewer", conf);
		ActorRef clusterClient = system.actorOf(ClusterClient.props(ClusterClientSettings.create(system)),
				"AgentDOViewer");

		AgentDO ado = new AgentDO(clusterClient);
		system.actorOf(ADORenderer.props(ado));
	}
}
