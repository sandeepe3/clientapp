package clientapp;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ServiceCaller {
	public static void main(String[] args) {
		URL url;
		try {
			url = new URL(args[0]);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("GET");

			int responseCode = httpURLConnection.getResponseCode();
			if (responseCode != 200) {
				System.out.println("Unable to get the response from service! Due to an internal error.");
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getErrorStream()));
				StringBuilder stringBuilder = new StringBuilder();
				String output;
				while (null != (output = bufferedReader.readLine())) {
					stringBuilder.append(output);
				}
				System.out.println("Error: " + output);
			} else {
				ObjectMapper mapper = new ObjectMapper();
				List<Map<String, ArrayList<Integer>>> documents = mapper.readValue(httpURLConnection.getInputStream(), ArrayList.class);
				int allDocumentsTotal = 0;
				int counter = 1;
				for (Map<String, ArrayList<Integer>> doc : documents) {
					for (Map.Entry<String, ArrayList<Integer>> docEntry : doc.entrySet()) {
						if ("numbers".equalsIgnoreCase(docEntry.getKey())) {
							int documentTotal = docEntry.getValue().stream().reduce(0, (total, number) -> total + number);
							System.out.println("Total for set " + counter + ": " + documentTotal);
							allDocumentsTotal += documentTotal;
						}
						counter++;
					}
				}
				System.out.println("Total for all integers in all documents : " + allDocumentsTotal);
			}
		} catch (Exception exception) {
			System.out.println("Unable to connect to the Service, reason could be: " + exception.getMessage());
		}
	}
}