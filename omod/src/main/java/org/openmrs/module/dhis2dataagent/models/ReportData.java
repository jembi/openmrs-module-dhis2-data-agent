package org.openmrs.module.dhis2dataagent.models;

import java.util.ArrayList;

public class ReportData {
	
	public String metadataVersion;
	
	public String period;
	
	public String orgUnitId;
	
	public String reportName;
	
	public ArrayList<ReportDataElement> dataElements = new ArrayList<ReportDataElement>();
}
