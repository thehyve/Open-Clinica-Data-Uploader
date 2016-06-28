/**
 * Created by bo on 5/12/16.
 */
var contains_events_unscheduled;
var template_arr;
var loading_html;


$(document).ready(function () {
    contains_events_unscheduled = true;

    loading_html = '<div id="loading_div"><div class="loader"></div><hr></div>';
    $('#event-registration-div').append(loading_html);
    next_btn();
    check_new_events();
});

function check_new_events() {

    $.ajax({
        url: baseApp + "/template/get-event-template",
        type: "GET",
        success: function (template) { console.log(template);
            $('#loading_div').remove();
            template_arr = template;
            if (template.length > 1) {
                provide_event_template_download();
                provide_event_template_upload();
            }
            else {
                window.location.href = baseApp + "/views/feedback-events";
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
            window.location.href = baseApp + "/views/feedback-events";
        }
    });
}


function provide_event_template_download() {
    var info = '<h4>&#9755; Unscheduled events are found in the dataset. These events should be scheduled with an "event scheduling template", which can be downloaded<button id="download-event-template-btn" class="btn btn-link btn-lg text-left" style="text-align: left">here</button></h4><div id="template-download-anchor"></div><hr>';
    $(info).insertBefore('#event-back-btn');

    $('#download-event-template-btn').click(function () {
        var blob = new Blob(template_arr, {type: "text/plain;charset=utf-8"});
        saveAs(blob, "event-scheduling-template.txt");
    });
}

function provide_event_template_upload() {
    var html = '<h4>&#9755; Once you have filled out the template, select it using the file chooser below.</h4> <form id="upload-event-template-form" class="form-horizontal"><input id="upload-event-template-input" type="file" name="uploadEventData" accept="*" /></form> <span id="message-board"></span> <hr>';
    $(html).insertBefore('#event-back-btn');
}


function next_btn() {
    var html = '<button type="button" class="btn btn-primary" id="subject-back-btn">Back</button>&nbsp;' +
        '<button type="button" class="btn btn-primary" id="subject-next-btn">Next</button>';
    $('#event-registration-div').append(html);
    $('#event-back-btn').click(function () {
        window.location.href = baseApp + "/views/feedback-subjects";
    });

    $('#event-next-btn').click(function () {
        upload_event_data();
    });

}


function update_submission() {
    $.ajax({
        url: baseApp + "/submission/update",
        type: "POST",
        data: {step: "feedback-events"},
        success: function () {
            console.log("Update submission called successfully");
            $('#loading_div').remove();
            window.location.href = baseApp + "/views/feedback-events";
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $('#loading_div').remove();
            console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
            window.location.href = baseApp + "/views/events";
        }
    });
}

function upload_event_data() {
    $('#loading_div').remove();
    $(loading_html).insertAfter('#message-board');
    $('#message-board').empty();
    $.ajax({
        url: baseApp + "/upload/events",
        type: "POST",
        data: new FormData($("#upload-event-template-form")[0]),
        enctype: 'multipart/form-data',
        processData: false,
        contentType: false,
        success: function (fileFormatErrors) {
            console.log(fileFormatErrors);
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
