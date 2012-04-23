import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JsonProcessor {
	private static final String LEFT_BIG = "{";
	private static final String LEFT_MID = "[";
	private static final String RIGHT_BIG = "}";
	private static final String RIGHT_MID = "]";
	
	public Object process(InputStream dataStream) throws Exception   {

		BufferedReader br = new BufferedReader(
				new InputStreamReader(dataStream));
		StringBuilder sb = new StringBuilder();

		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		Object obj = process(sb.toString());
		return obj;
	        
	}	
	
	public Object process(String str) throws ParseException {
    	//Validate JSON
		String jsonStr = getJsonFromString(str);
    	return new JSONParser().parse(jsonStr);
	}
	
	private String getJsonFromString(String sb) {
		int big = sb.indexOf(LEFT_BIG);
		int mid = sb.indexOf(LEFT_MID);
		
		int begin = -1;
		int end = -1;
		
		if(big==-1&&mid==-1) {
			return sb;
		} else if(big==-1) {
			begin = mid;
			end = sb.lastIndexOf(RIGHT_MID);
		} else if(mid == -1) {
			begin = big;
			end = sb.lastIndexOf(RIGHT_BIG);
		} else if(big<mid){
			begin = big;
			end = sb.lastIndexOf(RIGHT_BIG);
		} else {
			begin = mid;
			end = sb.lastIndexOf(RIGHT_MID);
		}
		
		return sb.substring(begin, end+1);
	}
	
	

	public static final void main(String[] args) throws Exception {
		String url1 = "http://216.74.176.78:8185/ts?symbol=.SSEC&period=daily";
		String url2 = "http://flashquote.stock.hexun.com/Quotejs/DA/2_000159_DA.html?";
		
		testUrl(url2, "0.0");
		testUrl(url1, "0.values.OPEN");
		testUrl(url1,"0.ric");
	}
	
	public static void testUrl(String url,String key) throws Exception {
		MultiThreadedHttpConnectionManager connMgr = new MultiThreadedHttpConnectionManager();
		HttpClient http = new HttpClient();
		http.setHttpConnectionManager(connMgr);
		http.getHostConfiguration().setProxy("10.90.7.56", 3128);
		
		HttpMethod method = new GetMethod(url);

		http.executeMethod(method);
		Object obj = new JsonProcessor().process(method.getResponseBodyAsStream());
		if(obj instanceof JSONArray ) {
			JSONArray jarray = (JSONArray)obj;
			System.out.println("Got a json array of " + jarray.size());
			System.out.println("Sample data:");
			System.out.println(jarray.get(0));
		} else if (obj instanceof JSONObject ) {
			System.out.println(obj);
		}
		
		System.out.println(JSONUtil.getValueFromJson(key, obj));
		method.releaseConnection();
	}
}
