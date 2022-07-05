let reportData = [];

$( "#report-menu" ).on('click', function() {
  document.location = 'dhis2dataagent.form';
});

$( "#config-menu" ).on('click', function() {
    document.location = 'configuration.form';
});

const saveConfig = function(baseUrl) {
    $('#save-config').hide();

    let config = {
        dhis2OrgUnit: $('#org-unit').val(), 
        pathMetadataFolder: $('#path-metadata-folder').val(), 
        pathReportFolder: $('#path-report-folder').val(), 
        pathArchiveFolder: $('#path-archive-folder').val(),
        urlIOL: $('#url-iol').val(),
        metadataUrl: $('#metadata-url').val()
    };

    $.ajax(`${baseUrl}/config.form`, {
        data : JSON.stringify(config),
        contentType : 'application/json',
        type : 'POST',
    }).done(response => {
        $('#config-saved-alert').show();
    });

}

const updatePeriod = function() {
    $('[id^="report-data-"]').html('');
    $('[id^="generate-report-"]').show();
}

const prePopulateDateField = function () {
    let month = new Date().getMonth() < 9 ? `0${new Date().getMonth() + 1}` : `${new Date().getMonth() + 1}`;
    $('#month').val(month);
    $('#year').val(new Date().getFullYear());
}

const generateReport = function(baseUrl, reportIndex) {
    $(`#generate-report-${reportIndex}`).hide();

    let date1 = new Date($('#year').val(), parseInt($('#month').val()) -1, 1, 10).toISOString().slice(0, 10);
    let date2 = new Date($('#year').val(), parseInt($('#month').val()), 0, 10).toISOString().slice(0, 10);

    $.get(`${baseUrl}/generateReport.form?reportIndex=${reportIndex}&startDate=${date1}&endDate=${date2}`, function (data, status) {
        $('#report-data-' + reportIndex).html(renderReportData(baseUrl, data));
    });
}

const renderReportData = function(baseUrl, data) {
    data = JSON.parse(data);
    reportData[data.reportName] = data;
    let result = '<div>';

    data.dataElements.forEach(dataElement => {
        result += `<hr><div class="data-element">${dataElement.dataElementName}</div>`;
        result += '<table>';
        dataElement.values.forEach(value => {
            result +=
            `<tr>
                <td><div class="label">${value.label}</div></td>        
                <td><div class="report-value">${value.value}</div></td>        
            </tr>`;
        });
        result += '</table>';
    });

    result += '<hr>';
    result += `<button type="button" class="btn button" id="button-${data.reportName.replace(' ','')}" onclick="pushToDhis2('${baseUrl}', '${data.reportName}',this)">Validate and push to DHIS 2</button>`;
    result += `<div id="message-${data.reportName.replace(' ','')}"></div>`;

    result += '</div>';

    return result;
}

const pushToDhis2 = function(baseUrl, reportName) {
    $(`#button-${reportName.replace(' ','')}`).hide();

    $.ajax(`${baseUrl}/save-report.form`, {
        data : JSON.stringify(reportData[reportName]),
        contentType : 'application/json',
        type : 'POST',
    });

    $(`#message-${reportName.replace(' ','')}`).html(`
    <div class="alert alert-success" role="alert">
        Done
    </div>
    `);

}

const displayConfiguration = function(baseUrl) {
    $('#config-saved-alert').hide();
    $.get(`${baseUrl}/config.form`, function (data, status) {
        data = JSON.parse(data);
        $( "#org-unit" ).val(data.dhis2OrgUnit);
        $( "#path-metadata-folder" ).val(data.pathMetadataFolder);
        $( "#path-report-folder" ).val(data.pathReportFolder);
        $( "#path-archive-folder" ).val(data.pathArchiveFolder);
        $( "#url-iol" ).val(data.urlIOL);
        $( "#metadata-url" ).val(data.metadataUrl);
    });
}

const getListOfReports = function (baseUrl, imagePath) {
    return new Promise((resolve => {
        $.get(baseUrl + '/getReportList.form', function (data, status) {
            let result = '<div class="report-panel">';
            let i = 0;
            data = JSON.parse(data);
            data.forEach(reportName => {
                result += `
                    <div class="row"><div class="col-9 section-title">&nbsp;&nbsp;&nbsp;<img src="${imagePath}/report.png"> &nbsp;&nbsp;&nbsp;${reportName}</div> <div class="col-3"><button type="button" id="generate-report-${i}" class="btn button" onclick="generateReport('${baseUrl}', ${i})">Generate this report</button></div></div>
                    <div class="row" id="report-data-${i}"></div>
                `;
                i++;
            });
            result += '</div>';
            resolve(result);
        });
    }));
}
