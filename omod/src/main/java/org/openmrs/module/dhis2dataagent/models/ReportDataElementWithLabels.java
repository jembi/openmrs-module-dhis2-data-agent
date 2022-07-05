package org.openmrs.module.dhis2dataagent.models;

import java.util.ArrayList;

public class ReportDataElementWithLabels {
	
	public String dataElementName;
	
	public ArrayList<ReportDataValueWithLabels> values = new ArrayList<ReportDataValueWithLabels>();
}
