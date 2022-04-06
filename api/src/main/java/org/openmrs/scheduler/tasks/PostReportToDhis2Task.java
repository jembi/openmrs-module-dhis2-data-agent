package org.openmrs.scheduler.tasks;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.openmrs.api.context.Context;

import java.io.File;
import java.util.List;

public class PostReportToDhis2Task extends AbstractTask {
	
	private static final Logger LOGGER = LogManager.getLogger(PostReportToDhis2Task.class);
	
	@Override
	public void execute() {
		if (!isExecuting) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Fetch Reports...");
			}
			startExecuting();
			
			try {
				LOGGER.info("Post to Dhis2");
				String reportFolder = Context.getAdministrationService().getGlobalProperty(
					"dhis2.data.agent.report_folder");
				String archiveFolder = Context.getAdministrationService().getGlobalProperty(
				    "dhis2.data.agent.archive_folder");
				List<File> listOfReports = PostReportToDhis2Util.getReportFiles(reportFolder);
				PostReportToDhis2Util.readReportAndPostTask(listOfReports, archiveFolder);
			}
			catch (Exception e) {
				LOGGER.error("Unexpected error occur: ", e);
			}
			finally {
				stopExecuting();
			}
			
		}
	}
	
}
