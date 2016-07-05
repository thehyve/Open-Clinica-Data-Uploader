/**
 * Javascript to control the ODM-upload
 * Created by jacob on 6/28/16.
 */

var loading_html;

$(document).ready(function () {
    loading_html = '<div id="loading_div"><div class="loader"></div><hr></div>';
    $('#odm-upload-div').append(loading_html);
    next_btn();
    generateUploadSettingsForm();
    $('#loading_div').remove();
});

function next_btn() {
    var html = '<button type="button" class="btn btn-primary" id="odm-upload-back-btn">Back</button>&nbsp;' +
        '<button type="button" class="btn btn-primary" id="odm-upload-next-btn">Next</button>';
    $('#odm-upload-div').append(html);
    $('#odm-upload-back-btn').click(function () {
        window.location.href = baseApp + "/views/feedback-events";
    });

    $('#odm-upload-next-btn').click(function () {
        submitUploadSettings();
    });

}

function generateUploadSettingsForm() {
    var html = '<h4>&#9755; Upload options</h4>' +
                '<div>Please select the status after upload and the status of existing CRF\'s which can be overwritten.</div>' +
                '<form id="upload-odm-template-form" class="form-horizontal">' +
                '   <div class="radio-button">' +
                '       <div>' +
                '           CRF status after upload' +
                '       </div>' +
                '       </br>' +
                '       <input id="statusAfterUpload_1" type="radio" name="statusAfterUpload" value="initial data entry" checked>Data Entry Started</br>' +
                '       <input id="statusAfterUpload_2" type="radio" name="statusAfterUpload" value="complete">Data Entry Complete</br>' +
                '   </div>' +
                '   <div class="filler"/>' +
                '   <div class="radio-button">' +
                '       <div>' +
                '           Upload to CRF with status (existing CRF\'s will be overwritten)' +
                '       </div>' +
                '       </br>' +
                '       <input id="overwriteStatus_1" type="checkbox" name="overwriteStatus" value="overwriteStatus_notStarted" checked>Not started</br>' +
                '       <input id="overwriteStatus_2" type="checkbox" name="overwriteStatus" value="overwriteStatus_initialDataEntry">Data Entry Started</br>' +
                '       <input id="overwriteStatus_3" type="checkbox" name="overwriteStatus" value="overwriteStatus_dataEntryComplete">Data Entry Complete</br>' +
                '   </div>' +
                '</form>' +
                '<span id="message-board"></span> <hr>';
    $(html).insertBefore('#odm-upload-back-btn');
}

function submitUploadSettings() {
    $('#loading_div').remove();
    $(loading_html).insertAfter('#message-board');
    $('#message-board').empty();
    $.ajax({
        url: baseApp + "/submission/upload-settings",
        type: "POST",
        data: new FormData($("#upload-odm-template-form")[0]),
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        success: function () {
            console.log("Update of update called successfully");
            update_submission();
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $('#loading_div').remove();
            console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
            $('#message-board').append('<div class="alert-danger">ODM upload failed.</div>')
        }

    });
}

function update_submission() {
    $.ajax({
        url: baseApp + "/submission/update",
        type: "POST",
        data: {step: "odm-upload"},
        success: function () {
            window.location.href = baseApp + "/views/odm-upload";
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
        }
    });
}


function log_errors(errors) {
    var info = '<div class="alert alert-danger"><ul>';
    errors.forEach(function (error) {
        var errDiv = '<li><span>' + error.message + '</span></li>';
        info += errDiv;
    });
    info += '</div></ul>';
    $("#message-board").append(info);
}
