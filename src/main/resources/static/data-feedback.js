/**
 * Created by bo on 5/7/16.
 */



var displayMessages = function displayMessages(data) {
    if(data.length == 0) {
<<<<<<< HEAD
        var html = '<h2>Data validation is successful!</h2>';
=======
        var html = '<h2>Data valiation is successful!</h2>';
>>>>>>> e68aaa5200782be0b42f7472fb3d1a049b6bd6b2
        $('#feedback-tables').append(html);
    }//if
    else {
        for (var i = 0; i < data.length; i++) {
            var fb = data[i];
            var msg = fb['message'];
            var vals = fb['offendingValues'];
            var msgRow =
                '<tr>' +
                '<th scope="row">Message: </th>' +
                '<td>  </td>' +
                '</tr>' +
                '<tr>' +
                '<th scope="row">  </th>' +
                '<td>' + msg + '</td>' +
                '</tr>';
            var valRows = '<tr><th scope="row">Offending Values: </th><td></td></tr>';

            for (var j = 0; j < vals.length; j++) {
                valRows +=
                    '<tr>' +
                    '<th scope="row"> </th>' +
                    '<td>' + vals[j] + '</td>' +
                    '</tr>';
            }
            var html =
                '<div class="table-responsive">' +
                '<table class="table table-striped table-hover">' +
                '<tbody>' +
                msgRow + '<hr>' +
                valRows +
                '</tbody>' +
                '</table>' +
                '</div>';
            $('#feedback-tables').append(html);
        }
    }//else
};

function feedbackDataNext() {
    window.location.replace(baseApp + "/mapping");
}

$.ajax({
    url: "/validate-data",
    type: "GET",
    cache: false,
    success: displayMessages,
    error: function () {
        console.log("Fetching validation errors from the server failed.");
    }
});
