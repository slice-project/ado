package org.etri.ado.util;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;
import org.etri.ado.message.DataModel;
import org.javatuples.Pair;
import org.javatuples.Tuple;
import org.javatuples.Unit;

import com.google.protobuf.Any;
import com.google.protobuf.ByteString;

public class MessageConverter {

	
	public static <A> Unit<A> toJava(DataModel.Unit proto) {		
		Any anyValue0 =  proto.getElement0();
		A value0 = SerializationUtils.<A> deserialize(anyValue0.getValue().toByteArray());		
		
		return new org.javatuples.Unit<A>(value0);
	}
	
	public static <A,B> Pair<A,B> toJava(DataModel.Pair proto) {		
		Any any0 =  proto.getElement0();
		A value0 = SerializationUtils.<A> deserialize(any0.getValue().toByteArray());
		
		Any any1 =  proto.getElement1();
		B value1 = SerializationUtils.<B> deserialize(any1.getValue().toByteArray());			
		
		return new Pair<A,B>(value0, value1);
	}
	
	public static <A> DataModel.Unit toProto(Unit<A> unit) {		
		ByteString byteString0 = ByteString.copyFrom(SerializationUtils.serialize((Serializable) unit.getValue0()));
		Any any0 = Any.newBuilder().setValue(byteString0).build();
		
		return DataModel.Unit.newBuilder().setElement0(any0).build();
	}
	
	public static <A,B> DataModel.Pair toProto(Pair<A,B> pair) {		
		ByteString byteString0 = ByteString.copyFrom(SerializationUtils.serialize((Serializable) pair.getValue0()));
		Any any0 = Any.newBuilder().setValue(byteString0).build();
		
		ByteString byteString1 = ByteString.copyFrom(SerializationUtils.serialize((Serializable) pair.getValue1()));
		Any any1 = Any.newBuilder().setValue(byteString1).build();		
		
		return  DataModel.Pair.newBuilder().setElement0(any0).setElement1(any1).build();
	}

	public static DataModel.Pair toProto(Tuple tuple) {
		
		return null; 		
	}
}
