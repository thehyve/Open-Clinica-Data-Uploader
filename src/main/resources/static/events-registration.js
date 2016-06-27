/**
 * Created by bo on 5/12/16.
 */
var contains_events_unscheduled;


$(document).ready(function () {
    //TODO: connect to backend
    contains_events_unscheduled = true;

    if (contains_events_unscheduled) {
        notify_that_additional_info_required();
        provide_event_template_download();
        provide_event_template_upload();
        next_btn();
    }
    else {
        window.location.href = baseApp + "/views/feedback-events";
    }
});

function notify_that_additional_info_required() {
    var html = '<span class="alert-danger"><h3>Additional event information is required.</h3></span><hr>';
    $('#event-registration-div').append(html);
}

function provide_event_template_download() {
    var html = '<button id="download-event-template-btn" type="button" class="btn btn-success">Download Event Template</button><hr>';
    $('#event-registration-div').append(html);
}

function provide_event_template_upload() {
    var html = '<form id="upload-event-template-form" class="form-horizontal"><div class="form-group"><label for="upload-subject-template-input">Upload Event Template:</label><input id="upload-event-template-input" type="file" name="upload-event-template" accept="*" /></div></form><hr>';
    $('#event-registration-div').append(html);
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
/*
function next_btn() {
    var html = '<button type="button" class="btn btn-primary" id="event-back-btn">Back</button>&nbsp;' +
        '<button type="button" class="btn btn-primary" id="event-next-btn">Next</button>';
    $('#event-registration-div').append(html);
    $('#event-back-btn').click(function () {
        window.history.back();
    });
    $('#event-next-btn').click(function () {
        $.ajax({
            url: baseApp + "/submission/update",
            type: "POST",
            data: {step: "feedback-events"},
            success: function () {
                //handle subject file upload
                window.location.href = baseApp + "/views/feedback-events";
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
                window.location.href = baseApp + "/views/events";
            }
        });

    });
}
*/
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
