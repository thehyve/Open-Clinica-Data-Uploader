/**
 * Created by bo on 5/7/16.
 */

d3.json("data/test-feedback-data.json", function (data) {
    console.log(data);
    for(var i=0; i<data.length; i++) {
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
            '<td>'+msg+'</td>' +
            '</tr>';
        var valRows = '<tr><th scope="row">offendingValues: </th><td></td></tr>';

        for(var j=0; j<vals.length; j++) {
            valRows +=
                '<tr>' +
                '<th scope="row"> </th>' +
                '<td>'+vals[j]+'</td>'+
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
});

function feedbackDataNext() {
    console.log("next");
}
