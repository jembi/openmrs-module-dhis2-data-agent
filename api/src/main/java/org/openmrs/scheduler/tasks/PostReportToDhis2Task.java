package org.openmrs.scheduler.tasks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;

import java.io.File;
import java.util.List;

public class PostReportToDhis2Task extends AbstractTask {
	
	private static final Log log = LogFactory.getLog(PullMetadataTask.class);
	
	@Override
	public void execute() {
		if (!isExecuting) {
			if (log.isDebugEnabled()) {
				log.debug("Fetch Reports...");
			}
			startExecuting();
			
			try {
				log.error("Post to Dhis2");
				String reportFolder = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.report_folder");
				String archiveFolder = Context.getAdministrationService().getGlobalProperty(
				    "dhis2.data.agent.archive_folder");
				
				List<File> listOfReports = PostReportToDhis2Util.getReportFiles(reportFolder, log);
				PostReportToDhis2Util.readReportAndPostTask(listOfReports, archiveFolder, log);
			}
			catch (Exception e) {
				log.error("Unexpected error occur: ", e);
			}
			finally {
				stopExecuting();
			}
			
		}
	}
	
}
