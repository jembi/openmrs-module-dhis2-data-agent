package org.openmrs.module.dhis2dataagent.models;

import java.util.ArrayList;

public class ReportDataElement {
	
	public String dataElementName;
	
	public ArrayList<ReportDataValue> values = new ArrayList<ReportDataValue>();
}
