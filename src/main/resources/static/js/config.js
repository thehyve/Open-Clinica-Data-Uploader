var baseApp = "";
var USERNAME = "";
var _SESSIONS = [];
var _CURRENT_SESSION_NAME = "";
var _SESSION_CONFIG = {};

if(window.location.hostname !== "localhost") {
    baseApp = "/ocdu";
}

function init_session_config(session_name) {
    if(!(session_name in _SESSION_CONFIG)) {
        var obj = {};
        obj['MAPPING_FILE_ENABLED'] = false;//default
        obj['NEED_TO_VALIDATE_SUBJECTS'] = true;//default
        obj['NEED_TO_VALIDATE_EVENTS'] = true;//default

        _SESSION_CONFIG[session_name] = obj;
        localStorage.setItem("session_config", JSON.stringify(_SESSION_CONFIG));
    }
}


function update_session_config(session_config) {
    localStorage.setItem("session_config", JSON.stringify(session_config));
}
