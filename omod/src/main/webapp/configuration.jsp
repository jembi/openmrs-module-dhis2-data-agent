<%@ include file="/WEB-INF/template/include.jsp"%>

<%@ include file="/WEB-INF/template/header.jsp"%>

<!-- <h2><spring:message code="dhis2dataagent.generateReport" /></h2> -->

<!-- <!doctype html>
<html lang="en"> -->

<head>
  <!-- Required meta tags -->
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">

  <!-- Bootstrap CSS -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet"
    integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/moduleResources/dhis2dataagent/styles/style.css">
  <title>DHIS 2 Data Agent</title>
</head>

<body>
  <div >
    <div class="row">
      <div class="col-3 left-panel" >
        <br>
        <center><img src="${pageContext.request.contextPath}/moduleResources/dhis2dataagent/images/data_agent_logo.png"></center>
        <br>
        <div id="report-menu" class="p-3 unselected-menu">GENERATE, VALIDATE AND PUSH REPORTS</div>
        <div id="config-menu" class="p-3 selected-menu">CONFIGURATION</div>
      </div>
      <div class="col-9">
        <div class="page-title">CONFIGURATION</div>
        <hr>
        <div class="form-field ">
            <div class="section-title form-label">DHIS2 Org Unit ID:</div>
            <input id="org-unit" type="text" class="period">
        </div>

        <div class="form-field ">
            <div class="section-title form-label">Path to metadata folder:</div>
            <input id="path-metadata-folder" type="text" class="period">
        </div>

        <div class="form-field ">
            <div class="section-title form-label">Path to metadata folder:</div>
            <input id="path-metadata-folder" type="text" class="period">
        </div>

        <div class="form-field ">
            <div class="section-title form-label">URL to pull metadata:</div>
            <input id="metadata-url" type="text" class="period">
        </div>

        <div class="form-field ">
            <div class="section-title form-label">Path to archive folder:</div>
            <input id="path-archive-folder" type="text" class="period">
        </div>

        <div class="form-field ">
            <div class="section-title form-label">URL to post report to IOL :</div>
            <input id="url-iol" type="text" class="period">
        </div>

        <hr>

        <button type="button" class="btn button" id="save-config" onclick="saveConfig('${pageContext.request.contextPath}/module/dhis2dataagent')">Save</button>
        <div id="config-saved-alert" class="alert alert-success" role="alert">
            Configuration saved successfully
        </div>

        <br>
      </div>
    </div>
  </div>

  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
  <script src="${pageContext.request.contextPath}/moduleResources/dhis2dataagent/js/client-utils.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
    integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
    crossorigin="anonymous"></script>
  <script>
    displayConfiguration("${pageContext.request.contextPath}/module/dhis2dataagent");
  </script>
</body>

<!-- </html> -->
<%@ include file="/WEB-INF/template/footer.jsp"%>
