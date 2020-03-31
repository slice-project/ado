package org.etri.ado.gateway.openai;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

import org.etri.ado.agent.AgentInfo;
import org.etri.ado.agent.registry.message.GetAll;
import org.etri.ado.gateway.openai.OpenAI.AgentRef;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.client.ClusterClient;
import akka.cluster.client.ClusterClient.SendToAll;
import akka.cluster.client.ClusterClientSettings;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.UseHttp2;
import akka.pattern.Patterns;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;

public class OpenAIGateway {

	public static void main(String[] args) throws Exception {
		
		final String host = "127.0.0.1";
		int port = 7090;
		
		Config conf = ConfigFactory.parseString("akka.remote.netty.tcp.port=" + 1558)
				.withFallback(ConfigFactory.load("gateway"));

		ActorSystem system = ActorSystem.create("OpenAIGateWay", conf);

		ActorRef clusterClient = system.actorOf(ClusterClient.props(ClusterClientSettings.create(system)),
				"openAIGateway");
		
		SendToAll msg = new SendToAll("/user/ado/singleton", new GetAll());
		CompletionStage<Object> getAll = Patterns.ask(clusterClient, msg, Duration.ofSeconds(1000));
		AgentInfo[] agentInfos = (AgentInfo[])getAll.toCompletableFuture().get();
		
		Map<AgentInfo, AgentRef> agents = new HashMap<AgentInfo, AgentRef>();
		for ( AgentInfo agent : agentInfos ) {
			AgentRef ref = AgentRef.newBuilder().setHost(host).setPort(port++).build();
			agents.put(agent, ref);
			
			runAgent(new AgentImpl(agent), ref, system).thenAccept(binding -> {
				System.out.println("Agent[id=" + agent.id + "] bound to: " + binding.localAddress());
			});			
		}		
		
		ADOImpl impl = new ADOImpl(clusterClient, agents);		
		runADO(impl, system).thenAccept(binding -> {
			System.out.println("ADO gateway bound to: " + binding.localAddress());
		});
	}

	public static CompletionStage<ServerBinding> runADO(ADOImpl impl, ActorSystem sys) throws Exception {
		Materializer mat = ActorMaterializer.create(sys);

		return Http.get(sys).bindAndHandleAsync(ADOHandlerFactory.create(impl, mat, sys),
				ConnectHttp.toHost("127.0.0.1", 7080, UseHttp2.always()), mat);
	}
	
	public static CompletionStage<ServerBinding> runAgent(AgentImpl impl, AgentRef ref, ActorSystem sys) throws Exception {
		Materializer mat = ActorMaterializer.create(sys);

		return Http.get(sys).bindAndHandleAsync(AgentHandlerFactory.create(impl, mat, sys),
				ConnectHttp.toHost(ref.getHost(), ref.getPort(), UseHttp2.always()), mat);
	}	
}
