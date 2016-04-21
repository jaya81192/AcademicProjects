package ir.yelp.jsonparser;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Shruti, Bhavik, Jaya
 */
public class DatasetReader {

	private DatasetReader() {
		// TODO Auto-generated constructor stub
	}

	//Reads a given line and returns a json object
	public static JSONObject JSONReader(String jsonContent) {
		JSONParser parser = new JSONParser();

		try {
			JSONObject jsonObject = (JSONObject) parser.parse(jsonContent);
			return jsonObject;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
}
