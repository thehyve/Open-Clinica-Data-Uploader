/**
 * Created by bo on 5/13/16.
 */


var displayMessages = function displayMessages(data) {
    $('#loading_div').remove();
    if(data.length == 0) {
        var html = '<div class="alert alert-success"> <strong>Event validation is successful!</strong></div>';
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

function feedbackNext() {
    $.ajax({
        url: baseApp + "/submission/update",
        type: "POST",
        data: {step: "pre-odm-upload"},
        success: function () {
            window.location.href = baseApp + "/views/pre-odm-upload";
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.log(jqXHR.status+" "+textStatus+" "+errorThrown);
            var html = "<div class='label label-danger'>Submission update has failed, please refresh the page.</div>";
            $(html).insertBefore('#data-feedback-back-btn');
        }
    });
}

function backBtnHandler() {
    window.location.href = baseApp + "/views/events";
}

//waiting for the ajax call
var loadinghtml = '<div id="loading_div" class="loader"></div>';
$('#feedback-tables').append(loadinghtml);

if(NEED_TO_VALIDATE_EVENTS) {
    $.ajax({
        url: baseApp+"/validate/events",
        type: "GET",
        success: displayMessages,
        cache: false,
        error: function (jqXHR, textStatus, errorThrown) {
            //window.location.href = baseApp + "/views/data";
        }
    });
}
else {
    var html = "<div class='label label-success'><strong>No event needs to be scheduled, click Next to proceed.</strong></div>";
    $(html).insertBefore('#data-feedback-back-btn');
}
