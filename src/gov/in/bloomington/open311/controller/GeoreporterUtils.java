package gov.in.bloomington.open311.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class GeoreporterUtils {
	public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
          sb.append(line + "\n");
        }
        is.close();
        return sb.toString();
      }
}
