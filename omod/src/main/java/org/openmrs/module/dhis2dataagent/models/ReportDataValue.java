package org.openmrs.module.dhis2dataagent.models;

import java.util.ArrayList;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportDataValue {
	
	public ArrayList<ReportDataDisaggregation> disaggregations = new ArrayList<ReportDataDisaggregation>();
	
	public double value;
}
