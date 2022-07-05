package org.openmrs.module.dhis2dataagent.web.controller;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openmrs.api.context.Context;
import org.openmrs.module.dhis2dataagent.models.ReportDataWithLabels;
import org.openmrs.module.dhis2dataagent.models.ReportData;
import org.openmrs.module.dhis2dataagent.models.UiConfig;
import org.openmrs.module.dhis2dataagent.utils.ReportParameter;
import org.openmrs.module.dhis2dataagent.utils.Tree;
import org.openmrs.module.dhis2dataagent.utils.Utils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ApiController {
	
	@RequestMapping(value = "module/dhis2dataagent/getReportList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody()
	public String getReportList() throws Exception {
		ArrayList<String> reportNames = new ArrayList<String>();
		JSONObject metadataConfig = Utils.getMetadataConfig();
		
		JSONArray reports = (JSONArray) metadataConfig.get("reports");
		Iterator<JSONObject> iterator = reports.iterator();
		while (iterator.hasNext()) {
			reportNames.add(iterator.next().get("reportName").toString());
		}
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(reportNames);
	}
	
	@RequestMapping(value = "module/dhis2dataagent/generateReport", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody()
	public String getGenerateReport(@RequestParam int reportIndex, @RequestParam String startDate,
	        @RequestParam String endDate) throws Exception {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date1 = LocalDate.parse(startDate, dateFormatter);
		LocalDate date2 = LocalDate.parse(endDate, dateFormatter);
		
		JSONObject metadata = Utils.getMetadataConfig();
		JSONObject reportConfigJson = Utils.getReportConfiguration(reportIndex);
		
		Tree parameterTree = Utils.generateParameterTree(reportConfigJson);
		ArrayList<ArrayList<ReportParameter>> disaggregationMatrix = Utils.generateDisaggregationMatrix(parameterTree);
		
		ReportDataWithLabels reportData = Utils.generateReportData(date1, date2, disaggregationMatrix, metadata,
		    reportConfigJson);
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(reportData);
	}
	
	@RequestMapping(value = "module/dhis2dataagent/config", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody()
	public String getConfig() throws Exception {
		UiConfig uiConfig = new UiConfig();
		
		uiConfig.dhis2OrgUnit = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.org_unit_id");
		uiConfig.pathArchiveFolder = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.archive_folder");
		uiConfig.pathMetadataFolder = Context.getAdministrationService().getGlobalProperty(
		    "dhis2.data.agent.metadata_folder");
		uiConfig.metadataUrl = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.metadata_url");
		uiConfig.urlIOL = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.iol_endpoint_url");
		uiConfig.pathReportFolder = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.report_folder");
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(uiConfig);
	}
	
	@RequestMapping(value = "module/dhis2dataagent/config", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody()
	public String postConfig(@RequestBody UiConfig uiConfig) throws Exception {
		
		Context.getAdministrationService().setGlobalProperty("dhis2.data.agent.org_unit_id", uiConfig.dhis2OrgUnit);
		Context.getAdministrationService().setGlobalProperty("dhis2.data.agent.archive_folder", uiConfig.pathArchiveFolder);
		Context.getAdministrationService()
		        .setGlobalProperty("dhis2.data.agent.metadata_folder", uiConfig.pathMetadataFolder);
		Context.getAdministrationService().setGlobalProperty("dhis2.data.agent.metadata_url", uiConfig.metadataUrl);
		Context.getAdministrationService().setGlobalProperty("dhis2.data.agent.iol_endpoint_url", uiConfig.urlIOL);
		Context.getAdministrationService().setGlobalProperty("dhis2.data.agent.report_folder", uiConfig.pathReportFolder);
		
		ObjectMapper mapper = new ObjectMapper();
		return mapper.writeValueAsString(uiConfig);
	}
	
	@RequestMapping(value = "module/dhis2dataagent/save-report", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody()
	public String saveReport(@RequestBody ReportData reportData) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		
		String reportFolder = Context.getAdministrationService().getGlobalProperty("dhis2.data.agent.report_folder");
		
		String reportString = mapper.writeValueAsString(reportData);
		
		try {
			PrintWriter out = new PrintWriter(reportFolder + "/" + reportData.reportName + "-" + reportData.period + ".json");
			out.println(reportString);
			out.close();
			return reportFolder + "/" + reportData.reportName + "-" + reportData.period + ".json" + "|" + reportString;
		}
		catch (Exception e) {
			return e.getMessage() + " " + e.getCause().getMessage();
		}
	}
}
