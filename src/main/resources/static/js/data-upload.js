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
            },
            error: function () {
                // Handle upload error
                var info = '<span id="data-alert" class="alert alert-danger">Data not uploaded</span>';
                $("#message-board").append(info);
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
                },
                error: function () {
                    // Handle upload error
                    var info = '<span id="mapping-alert" class="alert alert-danger">Mapping not uploaded</span>';
                    $("#message-board").append(info);
                }
            });
            window.setTimeout(function() {
                $("#mapping-alert").fadeTo(500, 0).slideUp(500, function(){
                    $(this).remove();
                });
            }, 3000);
        }
        /**
         * redirect to the mapping view
         */
        window.location.replace("/mapping");
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