var baseApp = "";
var SESSIONNAME = "";
var USERNAME = "";
var MAPPING_FILE_ENABLED = false;
var NEED_TO_VALIDATE_SUBJECTS = true;
var NEED_TO_VALIDATE_EVENTS = true;

if(window.location.hostname !== "localhost") {
    baseApp = "/ocdu";
}
