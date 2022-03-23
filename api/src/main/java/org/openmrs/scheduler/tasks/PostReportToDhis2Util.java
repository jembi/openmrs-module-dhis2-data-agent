package org.openmrs.scheduler.tasks;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.logging.Log;
import org.openmrs.api.context.Context;

public class PostReportToDhis2Util {
	
	public static List<File> getReportFiles(String pathname, Log log) {
		final File folder = new File(pathname);
		final List<File> fileList = Arrays.asList(folder.listFiles());
		
		if (fileList.size() == 0) {
			log.error("Report folder is empty");
			try {
				throw new Exception("Report folder is empty");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fileList;
	}
	
	public static void moveFileToArchive(File filename, String dest, Log log) {
		try {
			Path sourcePath = filename.toPath();
			String targetPath = filename.toString().replace("reportDataBox", dest);
			Files.move(sourcePath, Paths.get(targetPath));
			log.info("File has been moved to Archive ");
			
		}
		catch (Exception e) {
			log.error("File couldn't be moved to archive");
			e.printStackTrace();
		}
		
	}
	
	public static void readReportAndPostTask(List<File> reportFiles, String dest, Log log) {
		String POST_URL = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.iol_endpoint_url");
		String jsonData = "";
		
		for (File files : reportFiles) {
			log.info("files: " + files);
			try {
				BufferedReader bufferedReader = new BufferedReader(new FileReader(files));
				String line;
				while ((line = bufferedReader.readLine()) != null) {
					jsonData += line + "\n";
				}
				bufferedReader.close();
				
				URL url = new URL(POST_URL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setDoOutput(true);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "application/json");
				
				String input = jsonData;
				
				OutputStream os = conn.getOutputStream();
				os.write(input.getBytes());
				os.flush();
				
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}
				
				BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
				
				jsonData = "";
				String output;
				log.info("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					System.out.println(output);
				}
				conn.disconnect();
				moveFileToArchive(files, dest, log);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
