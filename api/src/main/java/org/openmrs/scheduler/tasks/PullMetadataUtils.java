package org.openmrs.scheduler.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import org.apache.commons.logging.Log;

public class PullMetadataUtils {
	
	public static String getMetadata(String urlString) throws IOException {
		URL url = new URL(urlString);
		URLConnection conn = url.openConnection();
		InputStream is = conn.getInputStream();
		// is.readAllBytes();
		
		String text = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(
		    Collectors.joining("\n"));
		
		return text;
	}
	
	public static void saveTextToFile(String text, String fileName) throws IOException {
		OutputStream os = new FileOutputStream(fileName);
		PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
		out.println(text);
		out.close();
	}
	
	public static boolean taskPropertiesAreValid(String metadataUrl, String metadataFolder, Log log) {
		// verify that the property "metadata_url" is defined
		if (metadataUrl == null || metadataUrl.trim() == "") {
			log.error("Please configure the global property 'dhis2.data.agent.metadata_url'");
			return false;
		}
		
		// verify that the property "metadata_folder" is defined
		if (metadataFolder == null || metadataFolder.trim() == "") {
			log.error("Please configure the global property 'dhis2.data.agent.metadata_folder'");
			return false;
		}
		
		// verify that the property "metadata_url" is a correct url
		try {
			URL url = new URL(metadataUrl);
			url.toURI();
		}
		catch (Exception e) {
			log.error("The global property 'dhis2.data.agent.metadata_url' is not a valid URL");
			return false;
		}
		
		// verify that the folder "metadata_folder" exists and it is accessible
		File file = new File(metadataFolder);
		if (!file.exists() || !file.isDirectory()) {
			log.error("The global property 'dhis2.data.agent.metadata_folder' is either not a folder or the folder referenced does not exist");
			return false;
		}
		
		return true;
	}
	
}
