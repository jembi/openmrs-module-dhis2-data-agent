package org.openmrs.scheduler.tasks;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openmrs.api.context.Context;

public class PostReportToDhis2Util {
	
	private static final Logger LOGGER = LogManager.getLogger(PostReportToDhis2Task.class);
	
	public static List<File> getReportFiles(String pathname) {
		final File folder = new File(pathname);
		final List<File> fileList = Arrays.asList(folder.listFiles());
		
		if (fileList.size() == 0) {
			try {
				LOGGER.error("Report folder is empty");
				throw new Exception("Report folder is empty");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return fileList;
	}
	
	public static void moveFileToArchive(File filename, String dest) {
		try {
			String sourcePath = filename.toString();
			String jsonFile = sourcePath.substring(sourcePath.lastIndexOf("/") + 1);
			String targetPath = dest + "/" + jsonFile;
			LOGGER.error("source path: " + sourcePath);
			LOGGER.error("target path: " + targetPath);
			Files.move(Paths.get(sourcePath), Paths.get(targetPath), StandardCopyOption.REPLACE_EXISTING);
			LOGGER.info("File has been moved to Archive ");
			
		}
		catch (Exception e) {
			LOGGER.error("File couldn't be moved to archive");
			e.printStackTrace();
		}
		
	}
	
	public static void readReportAndPostTask(List<File> reportFiles, String dest) {
		String POST_URL = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.iol_endpoint_url");
		String jsonData = "";
		
		for (File files : reportFiles) {
			LOGGER.info("files: " + files);
			try {
				BufferedReader bufferedReader = new BufferedReader(
				        new InputStreamReader(new FileInputStream(files), "UTF-8"));
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
				LOGGER.error("json: " + input);
				
				OutputStream os = conn.getOutputStream();
				PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
				out.println(input);
				out.close();
				
				// os.write(input.getBytes());
				// os.flush();
				
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					LOGGER.error("Failed to Post to Dhis2: HTTP error code: " + conn.getResponseCode());
					throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
				}
				
				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
				Thread.sleep(5000);
				
				jsonData = "";
				String output;
				LOGGER.info("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					System.out.println(output);
				}
				conn.disconnect();
				
				moveFileToArchive(files, dest);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
	
}
