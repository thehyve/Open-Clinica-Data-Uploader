/**
 * Created by jacob on 6/28/16.
 */

var loading_html;

$(document).ready(function () {
    loading_html = '<div id="loading_div"><div class="loader"></div><hr></div>';
    $('#subject-registration-div').append(loading_html);
    next_btn();
    check_new_patients(true);
});

function next_btn() {
    var html = '<button type="button" class="btn btn-primary" id="odm-upload-back-btn">Back</button>&nbsp;' +
        '<button type="button" class="btn btn-primary" id="odm-upload-next-btn">Next</button>';
    $('#subject-registration-div').append(html);
    $('#subject-back-btn').click(function () {
        window.location.href = baseApp + "/views/feedback-data";
    });

    $('#subject-next-btn').click(function () {
        performODMUpload();
    });

}

function provide_filled_template_upload() {
    var html = '<h4>&#9755; Upload options</h4>' +
                '<div>Please select the status after upload and the status of existing CRF\'s which can be overwritten.</div>'
                '<form id="upload-odm-template-form" class="form-horizontal">' +
                '   <div>' +
                '       <span>CRF status after upload</span>' +
                '       <input id="statusAfterUpload_1" type="radio" name="statusAfterUpload" value="initial data entry" checked>Initial Data Entry</br>' +
                '       <input id="statusAfterUpload_2" type="radio" name="statusAfterUpload" value="initial data entry">Data Entry Started</br>' +
                '       <input id="statusAfterUpload_3" type="radio" name="statusAfterUpload" value="initial data entry">Data Entry Complete</br>' +
                '   </div>' +
                '   <div>' +
                '       <span>Upload to CRF with status ( existing CRF\'s will be overwritten)</span>' +
                '       <input id="overwriteStatus_1" type="checkbox" name="overwriteStatus" value="overwriteStatus_notStarted" checked>Not started</br>' +
                '       <input id="overwriteStatus_2" type="checkbox" name="overwriteStatus" value="overwriteStatus_initialDataEntry">Data Entry Started</br>' +
                '       <input id="overwriteStatus_3" type="checkbox" name="overwriteStatus" value="overwriteStatus_dataEntryComplete">Data Entry Complete</br>' +
                '   </div>' +
                '</form>' +
                '<span id="message-board"></span> <hr>';
    $(html).insertBefore('#odm-upload-back-btn');
}

function performODMUpload() {
    $('#loading_div').remove();
    $(loading_html).insertAfter('#message-board');
    $('#message-board').empty();
    $.ajax({
        url: baseApp + "/odm/upload",
        type: "POST",
        data: new FormData($("#upload-odm-template-form")[0]),
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        success: function (fileFormatErrors) {
            if(fileFormatErrors.length == 0) {
                update_submission();
            }
            else{
                log_errors(fileFormatErrors);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $('#loading_div').remove();
            console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
            $('#message-board').append('<div class="alert-danger">Subject upload fails.</div>')
        }

    });
}


function update_submission() {
    $.ajax({
        url: baseApp + "/submission/update",
        type: "POST",
        data: {step: "feedback-subjects"},
        success: function () {
            $('#loading_div').remove();
            //handle subject file upload
            window.location.href = baseApp + "/views/";
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $('#loading_div').remove();
            console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
            window.location.href = baseApp + "/views/subjects";
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
