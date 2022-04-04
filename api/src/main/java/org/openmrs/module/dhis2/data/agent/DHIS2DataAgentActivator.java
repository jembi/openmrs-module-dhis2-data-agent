/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.dhis2.data.agent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.module.BaseModuleActivator;
import org.openmrs.scheduler.TaskDefinition;
import org.openmrs.api.context.Context;

/**
 * This class contains the logic that is run every time this module is either started or shutdown
 */
public class DHIS2DataAgentActivator extends BaseModuleActivator {
	
	private Log log = LogFactory.getLog(this.getClass());
	
	/**
	 * @see #started()
	 */
	public void started() {
		log.info("Started DHIS2 Data Agent");
		
		// set up initial config
		String metadataUrl = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.metadata_url");
		String metadataFolder = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.metadata_folder");
		
		if (metadataFolder == null) {
			Context.getAdministrationService().setGlobalProperty("dhis2.data.agent.metadata_folder", "/opt/openmrs/etc");
		}
		
		if (metadataUrl == null) {
			Context.getAdministrationService().setGlobalProperty("dhis2.data.agent.metadata_url", "");
		}
		
	}
	
	/**
	 * @see #shutdown()
	 */
	public void shutdown() {
		log.info("Shutdown DHIS2 Data Agent");
	}
	
}
