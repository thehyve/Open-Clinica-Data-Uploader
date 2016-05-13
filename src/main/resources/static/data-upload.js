/**
 * Created by bo on 4/17/16.
 */
/**
 * Upload the file sending it via Ajax at the Spring Boot server.
 */
var sessions = [];

function uploadFile() {

    var isSessionNameDefined = ($('#upload-session-input').val() !== "");
    var isDataSelected = ($('#upload-file-input').val() !== "");
    var isMappingSelected = ($('#upload-mapping-input').val() !== "");

    var isDataUploaded = false;
    var isMappingUploaded = false;
    var isDirected = false;

    SESSIONNAME = $('#upload-session-input').val();
    var sessionnames = [];
    for(var i=0; i<sessions.length; i++) {
        sessionnames.push(sessions[i].name);
    }

    if(sessionnames.indexOf(SESSIONNAME) !== -1) isSessionNameDefined = false;


    $("#message-board").empty();
    if (isSessionNameDefined && isDataSelected) {

        var dataFileUpload = function () {
            $.ajax({
                url: baseApp+"/upload/data",
                type: "POST",
                data: new FormData($("#upload-file-form")[0]),
                enctype: 'multipart/form-data',
                processData: false,
                contentType: false,
                success: function (fileFormatErrors) {
                    if (fileFormatErrors.length === 0) {
                        // Handle upload success
                        var info = '<span id="data-alert" class="alert alert-success">Data succesfully uploaded</span>';
                        $("#message-board").append(info);
                        isDataUploaded = true;
                        if (!isDirected && ((isMappingSelected && isMappingUploaded) || !isMappingSelected)) {
                            window.location.href = baseApp+"/views/mapping";
                            isDirected = true;
                        }
                    } else {
                        var info = '<div class="alert alert-danger"><ul>';
                        fileFormatErrors.forEach(function (error) {
                            var errDiv = '<li><span>' + error.message + '</span></li>';
                            // console.log(error);
                            info += errDiv;
                        });
                        info += '</div></ul>';
                        $("#message-board").append(info);
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    // Handle upload error
                    var info = '<span id="data-alert" class="alert alert-danger">Data not uploaded, either due to exceeding file size limit or server error</span>';
                    $("#message-board").append(info);
                    isDataUploaded = false;
                    console.log(jqXHR.status+" "+textStatus+" "+errorThrown);
                }
            });
        };
        $.ajax({
            url: baseApp+"/submission/create",
            type: "post",
            data: {name: SESSIONNAME},
            success: dataFileUpload,
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR.status+" "+textStatus+" "+errorThrown);
            }
        });

        window.setTimeout(function () {
            $("#data-alert").fadeTo(500, 0).slideUp(500, function () {
                $(this).empty();
            });
        }, 3000);

        if (isMappingSelected) {
            // var upload_mapping_data = new FormData($("#upload-mapping-form")[0]);
            // $.ajax({
            //     headers: {
            //         'Accept': 'application/json',
            //         'Content-Type': 'application/json'
            //     },
            //     url: baseApp+"/upload/mapping",
            //     type: "POST",
            //     data: upload_mapping_data,
            //     processData: false, // Don't process the files
            //     // contentType: false,
            //     cache: false,
            //     dataType: 'json',
            //     success: function (feedback) {
            //         // Handle upload success
            //         var info = '<span id="mapping-alert" class="alert alert-success">Mapping succesfully uploaded</span>';
            //         $("#message-board").append(info);
            //         isMappingUploaded = true;
            //         if (!isDirected && isDataUploaded) {
            //             // window.location.href = "/mapping");
            //             console.log(feedback);
            //             isDirected = true;
            //         }
            //     },
            //     error: function (jqXHR, textStatus, errorThrown) {
            //         console.log("Mapping upload to the server failed. HTTP status code:" + jqXHR.status + " " + errorThrown);
            //         // Handle upload error
            //         var info = '<span id="mapping-alert" class="alert alert-danger">Mapping not uploaded</span>';
            //         $("#message-board").append(info);
            //         isMappingUploaded = false;
            //     }
            // });
            // window.setTimeout(function () {
            //     $("#mapping-alert").fadeTo(500, 0).slideUp(500, function () {
            //         $(this).empty();
            //     });
            // }, 3000);
        }//if isMappingSelected

    }

    if (!isSessionNameDefined || !isDataSelected) {
        $("#message-board").empty();
        if (!isSessionNameDefined) {
            var info = '<span id="message-alert" class="alert alert-danger">Pleaes give your new submission a unique name. </span>';
            $("#message-board").append(info);
        }
        if (!isDataSelected) {
            var info = '<span id="message-alert" class="alert alert-danger">Please select a data file to upload. </span>';
            $("#message-board").append(info);
        }
    }

} //function uploadFile

function retrieveSessions() {
    $.ajax({
        url: baseApp+"/submission/all",
        type: "get",
        success: handle_session_retrieval_all,
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR.status+" "+textStatus+" "+errorThrown);
            // if(jqXHR.status == 401) {
            //     window.location.href = baseApp+"/views/data";
            // }
        }
    });

}

function handle_session_retrieval_all(_sessions) {
    sessions = _sessions;
    $("#data-proceed-btn").attr("disabled", false);
    var monthNames = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"];
    $('#old_upload_section').append('<div id="session_container" class="row-fluid"></div>');
    for (var i = 0; i < _sessions.length; i++) {
        var s = _sessions[i];
        var btnid = "s" + (i + 1);
        var d = new Date(s.savedDate);
        var sessionHTML = '<div class="well">' +
            '<button type="button" class="btn btn-primary" id="' + btnid + '" session_index=' + i + '>' + s.name + '</button>' +
            '<p><small>saved on: ' + monthNames[d.getMonth()] + ' ' + d.getDate() + ', ' + d.getFullYear() + '</small></p></div>';
        // $(sessionHTML).insertAfter("#old_upload_section_anchor");
        $('#session_container').append(sessionHTML);
        $('#' + btnid).click(handle_session_retrieval);
    }//for
}//function handle_session_retrieval_all

function handle_session_retrieval() {
    var ind = $(this).attr('session_index');
    var session = sessions[ind];
    var sid = session.id;
    //set the current session
    $.ajax({
        url: baseApp+"/submission/select",
        type: "get",
        data:{sessionId:sid},
        success: function (data) {
            //direct user to the selected session
            if(session.step == "MAPPING") {
                window.location.href = baseApp+"/views/mapping";
            }
            else if(session.step == "PATIENTS") {
                window.location.href = baseApp+"/views/subjects";
            }
            else if(session.step == "EVENTS") {
                window.location.href = baseApp+"/views/events";
            }
            else if(session.step = "OVERVIEW") {
                window.location.href = baseApp+"/views/overview";
            }
            else {
                console.log('Session step is not recognized.');
            }
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR.status+" "+textStatus+" "+errorThrown);
        }
    });
    console.log(sessions[ind]);
}//function handle_session_retrieval

function backBtnHandler() {
    window.history.back();
}

$(document).ready(function () {
    $("#data-proceed-btn").attr("disabled", "disabled");

    //retrieve user name
    $.ajax({
        url: baseApp+"/submission/username",
        type: "get",
        success: function (data) {
            USERNAME = data;
        },
        error: function(jqXHR, textStatus, errorThrown) {
            console.log(jqXHR.status+" "+textStatus+" "+errorThrown);
        }
    });

    retrieveSessions();
});
