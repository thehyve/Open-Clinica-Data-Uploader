/**
 * Created by bo on 4/17/16.
 */
/**
 * Upload the file sending it via Ajax at the Spring Boot server.
 */
var isDataSelected = false;
$('#upload-file-input').change(function () {
    isDataSelected = true;
});
var isMappingSelected = false;
$('#upload-mapping-input').change(function () {
    isMappingSelected = true;
});

function uploadFile() {
    var isDataUploaded = false;
    var isMappingUploaded = false;
    var isDirected = false;

    $("#message-board").empty();
    if(isDataSelected) {
        $.ajax({
            url: "/uploadFile",
            type: "POST",
            data: new FormData($("#upload-file-form")[0]),
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            cache: false,
            success: function () {
                // Handle upload success
                var info = '<span id="data-alert" class="alert alert-success">Data succesfully uploaded</span>';
                $("#message-board").append(info);
                isDataUploaded = true;
                if(!isDirected && ((isMappingSelected && isMappingUploaded) || !isMappingSelected)) {
                    window.location.replace("/mapping");
                    isDirected = true;
                }
            },
            error: function () {
                // Handle upload error
                var info = '<span id="data-alert" class="alert alert-danger">Data not uploaded</span>';
                $("#message-board").append(info);
                isDataUploaded = false;
            }
        });
        window.setTimeout(function() {
            $("#data-alert").fadeTo(500, 0).slideUp(500, function(){
                $(this).remove();
            });
        }, 3000);

        if(isMappingSelected) {
            $.ajax({
                url: "/uploadMapping",
                type: "POST",
                data: new FormData($("#upload-mapping-form")[0]),
                enctype: 'multipart/form-data',
                processData: false,
                contentType: false,
                cache: false,
                success: function () {
                    // Handle upload success
                    var info = '<span id="mapping-alert" class="alert alert-success">Mapping succesfully uploaded</span>';
                    $("#message-board").append(info);
                    isMappingUploaded = true;
                    if(!isDirected && isDataUploaded) {
                        window.location.replace("/mapping");
                        isDirected = true;
                    }
                },
                error: function () {
                    // Handle upload error
                    var info = '<span id="mapping-alert" class="alert alert-danger">Mapping not uploaded</span>';
                    $("#message-board").append(info);
                    isMappingUploaded = false;
                }
            });
            window.setTimeout(function() {
                $("#mapping-alert").fadeTo(500, 0).slideUp(500, function(){
                    $(this).remove();
                });
            }, 3000);
        }

    }
    else{
        var info = '<span id="message-alert" class="alert alert-danger">Data file needs to be specified</span>';
        $("#message-board").append(info);
        window.setTimeout(function() {
            $("#message-alert").fadeTo(500, 0).slideUp(500, function(){
                $(this).remove();
            });
        }, 3000);
    }

} // function uploadFile

var _USERNAME = "";

function retrieveSessions() {
    $.ajax({
        url: "/username",
        type: "get",
        success: function (data) {
            _USERNAME = data;
            console.log("username is " + _USERNAME);
        }
    });

    //this commented code is for retrieving the sessions in real situations
    // $.ajax({
    //     url: "/unfinished-sessions",
    //     type: "get",
    //     success: handle_retrieval,
    //     error: handle_error
    // });

    //the code below is only for testing purpose
    d3.json('data/test-sessions.json', function(sessions) {
        handle_retrieval(sessions);
    });

}

function handle_retrieval(sessions) {
    var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    $('#old_upload_section').append('<div id="session_container" class="row-fluid"></div>');
    for(var i=0; i<sessions.length; i++) {
        var s = sessions[i];
        var btnid = "s"+(i+1);
        var d = new Date(s.savedDate);
        var sessionHTML = '<div class="well">' +
            '<button type="button" class="btn btn-primary" id="'+btnid+'" session_index='+i+'>'+s.name+'</button>'+
            '<p><small>saved on: '+monthNames[d.getMonth()] +' '+ d.getDate()+', ' + d.getFullYear()+'</small></p></div>';
        // $(sessionHTML).insertAfter("#old_upload_section_anchor");
        $('#session_container').append(sessionHTML);
        $('#'+btnid).click(function(){
            var ind = $(this).attr('session_index');
            console.log(sessions[ind]);
        });
        // console.log(s.name + ", " + s.step + ", " + d);
    }//for
}

function handle_error() {
    console.log('Fail to load sessions.');
}

$(document).ready(function() {
    retrieveSessions();
});