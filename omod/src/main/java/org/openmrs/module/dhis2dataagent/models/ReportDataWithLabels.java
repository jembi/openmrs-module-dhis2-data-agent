package org.openmrs.module.dhis2dataagent.models;

import java.util.ArrayList;

public class ReportDataWithLabels {
	
	public String metadataVersion;
	
	public String period;
	
	public String orgUnitId;
	
	public String reportName;
	
	public ArrayList<ReportDataElementWithLabels> dataElements = new ArrayList<ReportDataElementWithLabels>();
}
