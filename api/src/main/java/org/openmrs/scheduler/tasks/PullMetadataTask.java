package org.openmrs.scheduler.tasks;

import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.api.context.Context;

public class PullMetadataTask extends AbstractTask {
	
	private static final Log log = LogFactory.getLog(PullMetadataTask.class);
	
	/**
	 * @see org.openmrs.scheduler.tasks.AbstractTask#execute()
	 */
	@Override
	public void execute() {
		if (!isExecuting) {
			if (log.isDebugEnabled()) {
				log.debug("Starting Pull Metadata Task...");
			}
			
			startExecuting();
			
			try {
				String metadataUrl = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.metadata_url");
				String metadataFolder = Context.getAdministrationService().getGlobalProperty(
				    "dhis2.data.agent.metadata_folder");
				
				if (PullMetadataUtils.taskPropertiesAreValid(metadataUrl, metadataFolder, log)) {
					// Pull the metadata
					String metadata;
					try {
						metadata = PullMetadataUtils.getMetadata(metadataUrl);
					}
					catch (Exception e) {
						log.error("Error while pulling the metadata: ", e);
						return;
					}
					
					// Store locally the metadata pulled
					try {
						PullMetadataUtils.saveTextToFile(metadata, metadataFolder + "/metadata.json");
					}
					catch (Exception e) {
						log.error("Error while saving the metadata in the local file: ", e);
						return;
					}
					
					log.debug("Metadata pulled and stored successfully at " + new Date().toString());
				}
			}
			catch (Exception e) {
				log.error("Unexpected error on the pull metadata task: ", e);
			}
			finally {
				stopExecuting();
			}
		}
	}
	
}
