package util;

import java.util.Collection;
import java.util.Map;

public class ObjectType {

	public static final String module = ObjectType.class.getName();

	@SuppressWarnings("unchecked")
	public static boolean isEmpty(Object value) {
		if (value == null)
			return true;

		if (value instanceof String)
			return UtilValidate.isEmpty((String) value);
		if (value instanceof Collection)
			return UtilValidate.isEmpty((Collection<? extends Object>) value);
		if (value instanceof Map)
			return UtilValidate.isEmpty((Map<? extends Object, ? extends Object>) value);
		if (value instanceof CharSequence)
			return UtilValidate.isEmpty((CharSequence) value);

		// These types would flood the log
		// Number covers: BigDecimal, BigInteger, Byte, Double, Float, Integer, Long, Short
		if (value instanceof Boolean)
			return false;
		if (value instanceof Number)
			return false;
		if (value instanceof Character)
			return false;
		if (value instanceof java.sql.Timestamp)
			return false;

		return false;
	}
	
	public static String trim(String query){
	    return query.replaceAll("[　  ]", "");
	}
}
