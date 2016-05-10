/**
 * Created by bo on 5/7/16.
 */


var displayMessages = function displayMessages(data) {
    if(data.length == 0) {
        var html = '<div class="alert alert-success"> <strong>Data validation is successful!</strong></div>';
        $('#feedback-tables').append(html);
    }//if
    else {
        for (var i = 0; i < data.length; i++) {
            var fb = data[i];
            var msg = fb['message'];
            var vals = fb['offendingValues'];

            var msgRow = '<span><strong>Validation Error --- '+msg+'</strong></span>';
            var errorid = "error"+i;

            var valRows = '<div class="container"><a href="#'+errorid+'" data-toggle="collapse"><strong>Erroneous Values:</strong></a>';
            valRows += '<div class="collapse in" id='+errorid+'>';

            for (var j = 0; j < vals.length; j++) {
                valRows +=
                    '<tr>' +
                    vals[j] +
                    '</tr>';
            }
            valRows += "</div></div>";
            var html =
                '<div class="table-responsive well">' +
                '<table class="table table-striped table-hover">' +
                '<tbody>' +
                msgRow +
                valRows +
                '</tbody>' +
                '</table>' +
                '</div>';
            $('#feedback-tables').append(html);
        }
    }//else
};

function feedbackDataNext() {
    window.location.replace(baseApp + "/views/mapping");
}

$.ajax({
    url: baseApp+"/validate/data",
    type: "GET",
    cache: false,
    success: displayMessages,
    error: function () {
        console.log("Fetching validation errors from the server failed.");
    }
});
