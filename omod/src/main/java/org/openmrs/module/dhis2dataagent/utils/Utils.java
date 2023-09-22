package org.openmrs.module.dhis2dataagent.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhis2dataagent.models.ReportDataWithLabels;
import org.openmrs.module.dhis2dataagent.models.ReportDataDisaggregation;
import org.openmrs.module.dhis2dataagent.models.ReportDataElementWithLabels;
import org.openmrs.module.dhis2dataagent.models.ReportDataValueWithLabels;

public class Utils {
	
	private static final Logger LOGGER = LogManager.getLogger(Utils.class);
	
	public static Reader getMetadataReader(String metadataFilePath) throws Exception {
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(metadataFilePath),
		        "UTF-8"));//new FileReader(file);
		return bufferedReader;
	}
	
	public static JSONObject getMetadataConfig() throws Exception {
		String metadataFolder = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.metadata_folder");
		JSONParser parser = new JSONParser();
		
		Reader reader = Utils.getMetadataReader(metadataFolder + "/metadata.json");
		Object configObject = parser.parse(reader);
		reader.close();
		return (JSONObject) configObject;
	}
	
	public static JSONObject getReportConfiguration(int reportIndex) throws Exception {
		JSONObject metadataConfig = getMetadataConfig();
		JSONArray reports = (JSONArray) metadataConfig.get("reports");
		return (JSONObject) reports.get(reportIndex);
	}
	
	public static Tree generateParameterTree(JSONObject reportConfig) {
		Tree parameterTree = new Tree(UUID.randomUUID().toString(), new ReportParameter[0]);
		
		JSONArray parameters = (JSONArray) reportConfig.get("parameters");
		for (Object _parameter : parameters.toArray()) {
			JSONObject parameter = (JSONObject) _parameter;
			ArrayList<TreeNode> leaves = parameterTree.getLeaves();
			
			if (parameter.containsKey("options")) {
				JSONArray options = (JSONArray) parameter.get("options");
				for (Object _option : options.toArray()) {
					String option = (String) _option;
					ReportParameter[] params = new ReportParameter[1];
					params[0] = new ReportParameter(parameter.get("key").toString(), option);
					for (TreeNode leaf : leaves) {
						parameterTree.insert(leaf.key, UUID.randomUUID().toString(), params);
					}
				}
			} else if (parameter.containsKey("ranges")) {
				JSONArray ranges = (JSONArray) parameter.get("ranges");
				for (Object _range : ranges.toArray()) {
					JSONObject range = (JSONObject) _range;
					ReportParameter[] params = new ReportParameter[2];
					params[0] = new ReportParameter(parameter.get("key").toString() + ".min", range.get("min").toString());
					params[1] = new ReportParameter(parameter.get("key").toString() + ".max", range.get("max").toString());
					
					for (TreeNode leaf : leaves) {
						parameterTree.insert(leaf.key, UUID.randomUUID().toString(), params);
					}
				}
			}
		}
		
		return parameterTree;
	}
	
	public static ArrayList<ArrayList<ReportParameter>> generateDisaggregationMatrix(Tree parameterTree) {
		ArrayList<ArrayList<ReportParameter>> disaggregations = new ArrayList<ArrayList<ReportParameter>>();
		
		ArrayList<TreeNode> leaves = parameterTree.getLeaves();
		
		for (TreeNode leaf : leaves) {
			ArrayList<TreeNode> path = new ArrayList<TreeNode>();
			parameterTree.getPath(leaf, path);
			
			ArrayList<ReportParameter> disaggregation = new ArrayList<ReportParameter>();
			for (TreeNode node : path) {
				disaggregation.addAll(Arrays.asList((ReportParameter[]) node.value));
			}
			
			disaggregations.add(disaggregation);
		}
		
		return disaggregations;
	}
	
	public static ReportDataWithLabels generateReportData(LocalDate startDate, LocalDate endDate,
	        ArrayList<ArrayList<ReportParameter>> disaggregationMatrix, JSONObject metadata, JSONObject reportConfig)
	        throws Exception {
		ReportDataWithLabels reportData = new ReportDataWithLabels();
		
		reportData.metadataVersion = ((JSONObject) metadata.get("metadata")).get("version").toString();
		reportData.period = startDate.getYear() + ""
		        + (startDate.getMonthValue() < 10 ? "0" + startDate.getMonthValue() : startDate.getMonthValue());
		reportData.orgUnitId = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.org_unit_id");
		reportData.reportName = reportConfig.get("reportName").toString();
		
		JSONArray dataElements = (JSONArray) reportConfig.get("dataElements");
		
		for (int k = 0; k < dataElements.size(); k++) {
			JSONObject dataElement = (JSONObject) dataElements.get(k);
			
			ReportDataElementWithLabels dataElementRecord = new ReportDataElementWithLabels();
			dataElementRecord.dataElementName = dataElement.get("dataElementName").toString();
			
			for (int i = 0; i < disaggregationMatrix.size(); i++) {
				ArrayList<ReportParameter> d = disaggregationMatrix.get(i);
				String query = dataElement.get("query").toString();
				query = query.replace("#startDate#", startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				query = query.replace("#endDate#", endDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
				
				for (ReportParameter template : d) {
					if (template.value.equals("H")) {
						query = query.replace("#gender#", "M");
					}
					query = query.replace("#" + template.key + "#", template.value);
					LOGGER.error("Sql query 1:" + query);
					// LOGGER.error("values:" + template.value);
					
				}
				
				double value = evaluateQuery(query);
				
				dataElementRecord.values.add(buildDataElementValue(value, d, reportConfig));
			}
			
			reportData.dataElements.add(dataElementRecord);
		}
		
		return reportData;
	}
	
	static ReportDataValueWithLabels buildDataElementValue(double value, ArrayList<ReportParameter> mappingTable,
	        JSONObject reportConfig) {
		ReportDataValueWithLabels reportDataValue = new ReportDataValueWithLabels();
		
		reportDataValue.value = value;
		reportDataValue.label = getAggregationLabels(mappingTable);
		
		for (int i = 0; i < mappingTable.size(); i++) {
			ReportParameter template = mappingTable.get(i);
			if (!template.key.contains(".")) {
				ReportDataDisaggregation rdd = new ReportDataDisaggregation();
				rdd.key = template.key;
				rdd.index = findIndexOfOption(template.key, template.value, reportConfig);
				reportDataValue.disaggregations.add(rdd);
			} else {
				String key = template.key.split("\\.")[0];
				
				ReportDataDisaggregation rdd = new ReportDataDisaggregation();
				rdd.key = key;
				rdd.index = findIndexOfRange(key, template.value, mappingTable.get(i + 1).value, reportConfig);
				reportDataValue.disaggregations.add(rdd);
				i++;
			}
		}
		
		return reportDataValue;
	}
	
	static double evaluateQuery(String query) throws Exception {
		List<List<Object>> result = Context.getAdministrationService().executeSQL(query, true);
		return Double.parseDouble(result.get(0).get(0).toString());
	}
	
	static String getAggregationLabels(ArrayList<ReportParameter> mappingTable) {
		String result = "";
		
		for (int i = 0; i < mappingTable.size(); i++) {
			ReportParameter template = mappingTable.get(i);
			if (!template.key.contains(".")) {
				result += template.key + " : " + template.value + " | ";
			} else {
				String key = template.key.split("\\.")[0];
				result += key + " : " + template.value + " - " + mappingTable.get(i + 1).value + " | ";
				i++;
			}
		}
		
		return result.substring(0, result.length() - 2);
	}
	
	static int findIndexOfOption(String key, String value, JSONObject reportConfig) {
		JSONArray parameters = (JSONArray) reportConfig.get("parameters");
		JSONObject parameter = null;
		for (Object _parameter : parameters.toArray()) {
			parameter = (JSONObject) _parameter;
			if (parameter.get("key").toString().equals(key)) {
				break;
			}
		}
		
		if (parameter != null) {
			JSONArray options = (JSONArray) parameter.get("options");
			return options.indexOf(value);
		} else {
			return -2;
		}
	}
	
	static int findIndexOfRange(String key, String value1, String value2, JSONObject reportConfig) {
		JSONArray parameters = (JSONArray) reportConfig.get("parameters");
		JSONObject parameter = null;
		for (Object _parameter : parameters.toArray()) {
			parameter = (JSONObject) _parameter;
			String _key = parameter.get("key").toString();
			if (_key.equals(key)) {
				break;
			}
		}
		
		if (parameter != null) {
			JSONArray ranges = (JSONArray) parameter.get("ranges");
			
			for (int i = 0; i < ranges.size(); i++) {
				JSONObject range = (JSONObject) ranges.get(i);
				
				if (range.get("min").toString().equals(value1) && range.get("max").toString().equals(value2)) {
					return i;
				}
			}
			return -2;
		} else {
			return -2;
		}
	}
}
