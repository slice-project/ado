package org.etri.ado;

import org.etri.ado.actor.ADOActor;
import org.etri.ado.actor.AgentRegistryActor;
import org.etri.ado.actor.AgentRemoverActor;
import org.etri.ado.agent.AbstractAgent;
import org.etri.ado.agent.Agent;
import org.etri.ado.agent.cp.CollaborationPolicy;
import org.etri.ado.agent.cp.KerasModelImporter;
import org.etri.ado.config.Configuration;
import org.etri.ado.config.Settings;
import org.etri.ado.device.Device;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigList;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class AgentSystem {
	
	private Agent m_agent;
	private final CollaborationPolicy m_policy;
	private final AgentScheduler m_scheduler;
	private final TupleSpace m_tuples;
	private final ActionStream m_actions;		
	private final ActorSystem m_system;
	
	private final ActorRef m_registry;
	
	public static AgentSystem create(String fileName) throws Exception {
		Config config = configure(fileName);
		ActorSystem system = ActorSystem.create(config.getString("ado.name"), config);
		
		return new AgentSystem(system, config);
	}	
	
	private AgentSystem(ActorSystem system, Config config) throws Exception {
		m_system = system;
		
		m_registry = system.actorOf(AgentRegistryActor.props());
		system.actorOf(AgentRemoverActor.props(this));
		system.actorOf(ADOActor.prop(this), "ado");
		
		m_tuples = new ContextStore(this);
		m_actions = new ActionStreamImpl(system.eventStream());
		
		m_policy = new KerasModelImporter().load(config.getString("ado.agent.policy"));
		m_scheduler = new AgentScheduler(this);
	}
	
	public LoggingAdapter getLogger(Object logSource) {
		return Logging.getLogger(m_system, logSource);
	}
	
	public Configuration getConfiguration() {
		return Settings.SettingsProvider.get(m_system);
	}
	
	public ActorRef getAgentRegistryActor() {
		return m_registry;
	}

	public ActorRef getTupleSpaceActor() {
		return ((ActorHolder)m_tuples).getActor();
	}
	
	public ActorSystem getActorSystem() {
		return m_system;
	}
	
	public CollaborationPolicy getCP() {
		return m_policy;
	}
	
	public Agent getAgent() {
		return m_agent;
	}
	
	public void setAgent(Agent agent) {
		((AbstractAgent)agent).setAgentSystem(this);
		m_agent = agent;		
		m_scheduler.start();
	}
	
	public TupleSpace tupleSpace() {
		return m_tuples;
	}
	
	public ActionStream actionStream() {
		return m_actions;
	}

	private static Config configure(String path) {
		Config config = ConfigFactory.load(path);
		Config parsed = ConfigFactory.parseString("akka {\n" + 
				"  actor {\n" + 
				"    provider = \"cluster\"\n" + 
				"\n" + 
				"    allow-java-serialization = on\n" + 
				"\n" + 
				"    warn-about-java-serializer-usage = off\n" + 
				" \n" + 
				"    # which serializers are available under which key\n" + 
				"    serializers {\n" + 
				"      proto = \"akka.remote.serialization.ProtobufSerializer\"\n" + 
				"      agentInfo = \"org.etri.ado.agent.AgentInfoSerializer\"\n" + 
				"    }\n" + 
				" \n" + 
				"    # which interfaces / traits / classes should be handled by which serializer\n" + 
				"    serialization-bindings {\n" + 
				"      \"com.google.protobuf.Message\" = proto\n" + 
				"      \"com.google.protobuf.GeneratedMessageLite\" = proto\n" + 
				"      \"com.google.protobuf.GeneratedMessageV3\" = proto\n" + 
				"      \"org.etri.ado.agent.AgentInfo\" = agentInfo\n" + 
				"    }    \n" + 
				"  }\n" + 
				"  \n" + 
				"  extensions = [\"akka.cluster.client.ClusterClientReceptionist\"]\n" + 
				"}\n" + 
				"");
		config = parsed.withFallback(config);
		
		String host = config.getString("ado.remote.hostname");
		parsed = ConfigFactory.parseString("akka.remote.netty.tcp.hostname = " + host);		
		config = parsed.withFallback(config);	
		
		String port = config.getString("ado.remote.port");
		parsed = ConfigFactory.parseString("akka.remote.netty.tcp.port = " + port);
		config = parsed.withFallback(config);
		
		String id = config.getString("ado.agent.id");
		parsed = ConfigFactory.parseString("akka.cluster.roles = [" + "id-".concat(id) + "]");
		config = parsed.withFallback(config);
		
		String ado = config.getString("ado.name");
		ConfigList seeds = config.getList("ado.seed-nodes");
		String[] seed_nodes = seeds.unwrapped().toArray(new String[seeds.size()]);
		StringBuilder buff = new StringBuilder();
		buff.append("[");
		for ( String node : seed_nodes ) {
			buff.append("\"akka.tcp://");
			buff.append(ado);
			buff.append("@");
			buff.append(node);
			buff.append("\", ");
		}
		buff.delete(buff.length()-2, buff.length());
		buff.append("]");
		
		parsed = ConfigFactory.parseString("akka.cluster.seed-nodes = " + buff.toString());
		config = parsed.withFallback(config);
		
		return config;
	}
}
