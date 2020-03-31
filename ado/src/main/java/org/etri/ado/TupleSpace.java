package org.etri.ado;

import java.util.Optional;

import org.javatuples.Tuple;

public interface TupleSpace {

	Optional<? extends Tuple> get(String key);
	
	void put(String key, Tuple value);
}
