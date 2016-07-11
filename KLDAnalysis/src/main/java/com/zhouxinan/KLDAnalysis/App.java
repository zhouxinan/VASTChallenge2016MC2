package com.zhouxinan.KLDAnalysis;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class App {
	public static void main(String[] args) {
		JSONParser parser = new JSONParser();
		try {
			Object object = parser.parse(new FileReader("json/proxOut-MC2.json"));
			JSONArray array = (JSONArray) object;
			for (Object o : array) {
				JSONObject record = (JSONObject) o;
				Double offset = (Double) record.get("offset");
				System.out.println(offset);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
