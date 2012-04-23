import java.security.InvalidParameterException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class JSONUtil {
	//Get the key from a json array or json object
		//Key: index0.key0
		public static Object getValueFromJson(String key, Object obj) {
			String checkIndex = JSONUtil.getFirstKey(key);
			if(checkIndex.matches("^\\d+$")) {
				return getIndexFromJson(key,obj);
			} else {
				return getKeyFromJson(key,obj);
			} 
		}

		private static String getRightKey(String key) {
			int index = key.indexOf(".");
			String right = key.substring(index+1,key.length());
			return right;
		}

		private static boolean isLastKey(String key) {
			int index = key.indexOf(".");
			return index==-1;
		}

		public static String getFirstKey(String key) {
			int index = key.indexOf('.');
			return index==-1?key:key.substring(0, index);		
		}		
		
		private static Object getKeyFromJson(String key, Object obj) {
			if(!(obj instanceof JSONObject) )
				throw new InvalidParameterException("Not a json object");
			
			JSONObject jobj = (JSONObject)obj;
			if(isLastKey(key)) {
				return jobj.get(key);
			}else {
				String pos = getFirstKey(key);
				String right = getRightKey(key);			
				return getValueFromJson(right,jobj.get(pos));
			}			
		}

		private static Object getIndexFromJson(String key, Object obj) {
			if(!(obj instanceof JSONArray) )
				throw new InvalidParameterException("Not a json array");
			
			JSONArray jarray = (JSONArray)obj;
			
			String pos = getFirstKey(key);
			int keyPos = Integer.valueOf(pos);
			
			if(isLastKey(key)) {
				return jarray.get(keyPos);
			} else {
				String right = getRightKey(key);			
				return getValueFromJson(right,jarray.get(keyPos));
			}
		}
}
