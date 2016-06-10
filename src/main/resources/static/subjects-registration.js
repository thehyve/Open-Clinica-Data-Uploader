/**
 * Created by bo on 5/12/16.
 */

var contains_patients_without_data;
var contains_site_info_for_missing_paitents;

$(document).ready(function () {
    //TODO: backend checks
    //for testing:
    contains_patients_without_data = true;
    contains_site_info_for_missing_paitents = false;

    if(contains_patients_without_data) {
        notify_user_that_additional_patient_info_is_required();
        if(!contains_site_info_for_missing_paitents) {
            ask_whether_patients_should_be_registered_at_sites();
        }
        provide_template_download();
        provide_filled_template_upload();
        next_btn();
    }
    else{
        //go to the feedback view
        window.location.href = baseApp + "/views/feedback-subjects";
    }
});

function notify_user_that_additional_patient_info_is_required() {
    var html = '<span class="alert-danger"><h3>Additional subject information is required.</h3></span><hr>';
    $('#subject-registration-div').append(html);
}

function ask_whether_patients_should_be_registered_at_sites() {
    var html = '<h4>Choose if subjects should be registered at sites:</h4>';
    html += '<form role="form"><label class="radio-inline"><input type="radio" name="optradio" checked>Yes</label><label class="radio-inline"><input type="radio" name="optradio">No</label></form><hr>';
    $('#subject-registration-div').append(html);
}

function provide_template_download() {
    var html = '<button id="download-subject-template-btn" type="button" class="btn btn-success">Download Subject Template</button><hr>';
    $('#subject-registration-div').append(html);
}

function provide_filled_template_upload() {
    var html = '<form id="upload-subject-template-form" class="form-horizontal"><div class="form-group"><label for="upload-subject-template-input">Upload Subject Template:</label><input id="upload-subject-template-input" type="file" name="upload-subject-template" accept="*" /></div></form><hr>';
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

        $.ajax({
            url: baseApp + "/submission/update",
            type: "POST",
            data: {step: "feedback-subjects"},
            success: function () {
                //handle subject file upload
                window.location.href = baseApp + "/views/feedback-subjects";
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR.status+" "+textStatus+" "+errorThrown);
                window.location.href = baseApp + "/views/subjects";
            }
        });

    });

}

