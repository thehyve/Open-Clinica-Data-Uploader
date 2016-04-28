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

    $.ajax({
        url: "/unfinished-sessions",
        type: "get",
        success: handle_retrieval,
        error: handle_error
    });
}

function handle_retrieval(sessions) {
    console.log("unfinished sessions: ");
    console.log(sessions);
    console.log(JSON.stringify(sessions));
}

function handle_error() {
    console.log('Fail to load sessions.');
}

$(document).ready(function() {
    retrieveSessions();
});