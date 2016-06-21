/**
 * Created by bo on 5/12/16.
 */

var contains_site_info_for_missing_paitents = false;
var to_register_at_site = true;
var template_str;
var loading_html;

$(document).ready(function () {
    loading_html = '<div id="loading_div" class="loader"><br></div>';
    $('#subject-registration-div').append(loading_html);

    check_new_patients(true);
});

function check_new_patients(toRegisterSite) {

    $.ajax({
        url: baseApp + "/template/get-subject-template",
        type: "GET",
        data: {registerSite: toRegisterSite},
        success: function (template) {
            $('#loading_div').remove();
            template_str = template;
            if (template.length > 1) {
                notify_user_that_additional_patient_info_is_required();
                if (!contains_site_info_for_missing_paitents) {
                    ask_whether_patients_should_be_registered_at_sites();
                }
                provide_template_download();
                provide_filled_template_upload();
                next_btn();
            }
            else {
                window.location.href = baseApp + "/views/feedback-subjects";
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
            window.location.href = baseApp + "/views/feedback-subjects";
        }
    });
}


function notify_user_that_additional_patient_info_is_required() {
    var html = '<span class="alert-danger"><h3>Additional subject information is required.</h3></span><hr>';
    $('#subject-registration-div').append(html);
}

function ask_whether_patients_should_be_registered_at_sites() {
    var html = '<h4>Choose if subjects should be registered at sites:</h4>';
    html += '<form role="form">' +
        '<label class="radio-inline"><input id="userCheckSiteYes" type="radio" name="optradio" checked>Yes</label>' +
        '<label class="radio-inline"><input id="userCheckSiteNo" type="radio" name="optradio">No</label>' +
        '</form><hr>';
    $('#subject-registration-div').append(html);
    $('#userCheckSiteYes').change(function () {
        to_register_at_site = true;
    });
    $('#userCheckSiteNo').change(function () {
        to_register_at_site = false;
    });
}

function provide_template_download() {
    var html = '<button id="download-subject-template-btn" type="button" class="btn btn-success">Download Subject Template</button><div id="template-download-anchor"></div><hr>';
    $('#subject-registration-div').append(html);
    $('#download-subject-template-btn').click(function () {
        $(loading_html).insertAfter('#template-download-anchor');
        $.ajax({
            url: baseApp + "/template/get-subject-template",
            type: "GET",
            data: {registerSite: to_register_at_site},
            success: function (template) {
                template_str = template;
                $('#loading_div').remove();
                var blob = new Blob(template_str, {type: "text/plain;charset=utf-8"});
                saveAs(blob, "subject-registration-template.tsv");
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
                // window.location.href = baseApp + "/views/subjects";
            }
        });
    });
}

function provide_filled_template_upload() {
    var html = '<form id="upload-subject-template-form" class="form-horizontal"><div class="form-group"><label for="upload-subject-template-input">Upload Subject Template:</label><input id="upload-subject-template-input" type="file" name="uploadPatientData" accept="*" /></div></form><span id="message-board"></span><hr>';
    $('#subject-registration-div').append(html);
}

function next_btn() {
    var html = '<button type="button" class="btn btn-primary" id="subject-back-btn">Back</button>&nbsp;' +
        '<button type="button" class="btn btn-primary" id="subject-next-btn">Next</button>';
    $('#subject-registration-div').append(html);
    $('#subject-back-btn').click(function () {
        window.location.href = baseApp + "/views/feedback-data";
    });

    $('#subject-next-btn').click(function () {
        upload_subjects();
    });

}

function upload_subjects() {
    $('#loading_div').remove();
    $(loading_html).insertAfter('#template-download-anchor');
    $('#message-board').empty();
    $.ajax({
        url: baseApp + "/upload/subjects",
        type: "POST",
        data: new FormData($("#upload-subject-template-form")[0]),
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        success: function (fileFormatErrors) {
            if(fileFormatErrors.length == 0) {
                validate_subjects();
            }
            else{
                log_errors(fileFormatErrors);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
        }

    });
}

function validate_subjects() {
    $.ajax({
        url: baseApp + "/validate/patients",
        type: "GET",
        success: function (validationErrors) {
            if(validationErrors.length == 0) {
                console.log("patient validation ok");
                update_submission();
            }
            else{
                log_errors(validationErrors);
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
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
            window.location.href = baseApp + "/views/feedback-subjects";
        },
        error: function (jqXHR, textStatus, errorThrown) {
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
