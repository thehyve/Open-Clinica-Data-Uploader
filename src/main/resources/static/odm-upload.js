/**
 * Javascript to control the ODM-upload
 * Created by jacob on 6/28/16.
 */

var loading_html;

var displayMessages = function displayMessages(data) {
    $('#loading_div').remove();
    if(data.length == 0) {
        var html = '<div class="alert alert-success"> <strong>Data validation is successful!</strong></div>';
        $('#feedback-tables').append(html);

    }//if
    else {
        $('#feedback-tables').empty();
        var error_word = 'errors'; if(data.length == 1) error_word = 'error';
        var html_title = '<h3><span> <strong>'+data.length +' '+error_word+' found... </strong> </span></h3>';
        $('#feedback-tables').append(html_title);

        for (var i = 0; i < data.length; i++) {
            var fb = data[i];
            var msg = fb['message'];
            var vals = fb['offendingValues'];
            var errorid = "error"+i;
            var middlepart = '<div class="panel-heading"><h4 class="panel-title"><a data-toggle="collapse" href="#'+errorid+'"> '+msg+'</a></h4></div>';
            var listpart = '<ul class="list-group">';

            for (var j = 0; j < vals.length; j++) {
                listpart += '<li class="list-group-item">'+vals[j]+'</li>'
            }
            listpart += '</ul>';
            middlepart += '<div id="'+errorid+'" class="panel-collapse collapse in">'+listpart+'</div>';
            var html = '<div class="panel-group"><div class="panel panel-default">'+middlepart+'</div></div>'
            $('#feedback-tables').append(html);
        }//for

    }//else
};

$(document).ready(function () {
    loading_html = '<div id="loading_div"><div class="loader"></div><hr></div>';
    $('#odm-upload-div').append(loading_html);
    provide_odm_upload_template();
    performODMUpload();
    $('#loading_div').remove();
});

function provide_odm_upload_template() {
    $(html).insertBefore('#odm-upload-div');
}
var html = '<h4>&#9755; Patience please...</h4> <form id="upload-odm-template-form" class="form-horizontal"></form> <span id="message-board"></span> <hr>';


function performODMUpload() {
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
            console.log("Upload ODM successfully");
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
        data: {step: "final"},
        success: function () {
            window.location.href = baseApp + "/views/final";
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
