package eu.eyan.util.collection;

import java.util.HashMap;
import java.util.Map;

public class MapsPlus {

	@SuppressWarnings("unchecked")
	public static <KEY, VALUE> Map<KEY, VALUE> newMap(Object... objects) {
		Map<KEY, VALUE> map = new HashMap<>();
		for (int i = 0; i < objects.length/2; i++) map.put((KEY)objects[i*2], (VALUE)objects[i*2+1]);
		return map;
	}

	public static <T1,T2> Map<T1, T2> newMaxSizeHashMap(int limit) {
		return new MaxSizeHashMap<T1, T2>(limit);
	}
}