/**
 * Created by bo on 5/12/16.
 */

var to_register_at_site = true;
var template_str;
var loading_html;

$(document).ready(function () {
    loading_html = '<div id="loading_div"><div class="loader"></div><hr></div>';
    $('#subject-registration-div').append(loading_html);
    next_btn();
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
                provide_template_download();
                provide_filled_template_upload();
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


function provide_template_download() {
    var info = '<h4>&#9755; Unregistered subjects are found in the dataset. These subjects should be registered with a "subject registration template", which can be downloaded<button id="download-subject-template-btn" class="btn btn-link btn-lg text-left" style="text-align: left">here</button></h4><div id="template-download-anchor"></div><hr>';
    $(info).insertBefore('#subject-back-btn');

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
    var html = '<h4>&#9755; Once you have filled out the template, select it using the file chooser below.</h4> <form id="upload-subject-template-form" class="form-horizontal"><input id="upload-subject-template-input" type="file" name="uploadPatientData" accept="*" /></form> <span id="message-board"></span> <hr>';
    $(html).insertBefore('#subject-back-btn');
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
    $(loading_html).insertAfter('#message-board');
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
            window.location.href = baseApp + "/views/feedback-subjects";
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
