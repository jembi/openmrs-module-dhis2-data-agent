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
        <div id="report-menu" class="p-3 selected-menu">GENERATE, VALIDATE AND PUSH REPORTS</div>
        <div id="config-menu" class="p-3 unselected-menu">CONFIGURATION</div>
      </div>
      <div class="col-9">
        <div class="page-title">GENERATE, VALIDATE AND PUSH REPORTS</div>
        <hr>

        <span class="section-title">Report period :</span>
        <select id="month" class="period" onchange="updatePeriod()">
          <option value="01">January</option>
          <option value="02">February</option>
          <option value="03">March</option>
          <option value="04">April</option>
          <option value="05">May</option>
          <option value="06">June</option>
          <option value="07">July</option>
          <option value="08">August</option>
          <option value="09">September</option>
          <option value="10">October</option>
          <option value="11">November</option>
          <option value="12">December</option>
        </select>
        <input id="year" type="number" maxlength="4" class="period" onchange="updatePeriod()">

        <br>
        <br>
        <div class="section-title">Reports available :</div>

        <br>
        <div id="report_list"></div>
      </div>
    </div>
  </div>

  <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
  <script src="${pageContext.request.contextPath}/moduleResources/dhis2dataagent/js/client-utils.js"></script>
  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"
    integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p"
    crossorigin="anonymous"></script>
  <script>
    prePopulateDateField();

    getListOfReports("${pageContext.request.contextPath}/module/dhis2dataagent", "${pageContext.request.contextPath}/moduleResources/dhis2dataagent/images").then(reportNames => {
      $('#report_list').html(reportNames);

      for(let i=0;i<1000;i++) {
        $(`#loading-icon-${i}`).hide();
      }
    });

  </script>
</body>

<!-- </html> -->
<%@ include file="/WEB-INF/template/footer.jsp"%>
