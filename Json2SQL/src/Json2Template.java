import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;


public class Json2Template {
	
	private static final String VIRABLE_PATTERN="#\\{(.+?)\\}";	
	private static final Pattern KEY_PATTERN = Pattern.compile(VIRABLE_PATTERN);
	
	
	public String json2Template(String input, String template) throws FileNotFoundException, Exception {
		//read json ojbect from input path. 
		File inputFile = new File(input);
		FileInputStream fip = new FileInputStream(inputFile);
		JsonProcessor jprocessor = new JsonProcessor();
		StringBuilder sb = new StringBuilder();

		try {
			Object obj = jprocessor.process(fip);

			String templateStr = readFile(template);

			List<String> keys = pullAllKeysFromTemplate(templateStr);

			// first, let's assume the input source is a json array.

			try {
				if (obj instanceof JSONArray) {
					JSONArray jarray = (JSONArray) obj;
					for (int i = 0; i < jarray.size(); i++) {
						Object item = jarray.get(i);
						sb.append(processSingle(templateStr, keys, item));
						sb.append("\n");
					}
				}
			} catch (Exception e) {
				// too bad, input is not an array
				sb.append(processSingle(templateStr, keys, obj));
			}
		} finally {
			if (fip != null)
				fip.close();
		}
		return sb.toString();
	}


	private String processSingle(String templateStr, List<String> keys,
			Object item) {
		try {
			for (String key : keys) {
				String value = ""+ JSONUtil.getValueFromJson(key, item);
				templateStr = replacekeyInTemplate(templateStr, key, value);
			}
		} catch (Exception e) {
			System.err.println("line: " + item
					+ " is not suitable for template");
			System.err.println(": " + templateStr);

		}
		return templateStr;
	}
	

	private String readFile(String template) throws IOException {
		StringBuilder sb = new StringBuilder();
		FileInputStream fip = null;
		try {
			File file = new File(template);
			fip = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fip));
			String line;
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		}
		finally {
			if(fip != null) fip.close();
		}
		return sb.toString();
	}


	private String replacekeyInTemplate(String template, String key, String value) {
		return template.replaceAll("#\\{"+key+"\\}", value);		
	}

	private List<String> pullAllKeysFromTemplate(String template) {
		List<String> list = new ArrayList<String>();
		Matcher m = KEY_PATTERN.matcher(template);
		while(m.find()) {
			String key = m.group(1);
			list.add(key);
		}
		return list;
	}


	/**
	 * @param args
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, Exception {
		if(args.length!=2) {
			System.out.println("Need 2!");
		}
		
		String input = args[0];
		String template = args[1];
		
		System.out.println(new Json2Template().json2Template(input,template));
//		test();
	}
	
	
	public static void test() throws FileNotFoundException, Exception {
		String input="/home/test/tmp/data";
		String template="/home/test/tmp/template";
		
		System.out.println(new Json2Template().json2Template(input,template));

	}

}
