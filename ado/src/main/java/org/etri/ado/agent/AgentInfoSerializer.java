package org.etri.ado.agent;

import java.io.NotSerializableException;

import akka.actor.ExtendedActorSystem;
import akka.cluster.ddata.protobuf.AbstractSerializationSupport;
import akka.serialization.JavaSerializer;

public class AgentInfoSerializer extends AbstractSerializationSupport {

	private final ExtendedActorSystem m_system;
	private final JavaSerializer m_serializer;

	public AgentInfoSerializer(ExtendedActorSystem system) {
		m_system = system;
		m_serializer = new JavaSerializer(system);
	}

	@Override
	public ExtendedActorSystem system() {
		return m_system;
	}

	@Override
	public int identifier() {
		return 1485;
	}

	@Override
	public boolean includeManifest() {
		return false;
	}

	@Override
	public byte[] toBinary(Object obj) {
		if ( obj instanceof AgentInfo ) {
			return m_serializer.toBinary(obj);
		}
		else {
			throw new IllegalArgumentException("Can't serialize object of type " + obj.getClass());
		}
	}

	@Override
	public Object fromBinaryJava(byte[] bytes, Class<?> manifest) {
		Object info;
		try {
			info = m_serializer.fromBinary(bytes, AgentInfo.class);
		} 
		catch (NotSerializableException e) {
			throw new IllegalArgumentException("Can't dserialize object of AgentInfo " + e.getMessage());
		}
		
		return info;
	}

}
