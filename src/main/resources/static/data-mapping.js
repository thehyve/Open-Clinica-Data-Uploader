$(function () {

    //switches and thresholds
    var TESTING = false;
    var MAX_NUM_LABEL_CHARS = 30;


    //data obtained from backend
    var oc_list;
    var oc_path = [];
    var oc_labels = [];
    var usr_list;

    //mapping data
    var mapping = {};//oc_label --> usr_label
    var oc_usr_id_mapping = {};// #droppable_id --> draggable_id
    var usr_label_mapping = {};// full-usr-label --> shortened-usr-label
    var oc_label_mapping = {}; //full-oc-label --> shortened-oc-label

    //tips
    var oc_tip;
    var usr_tip;

    
    function build_filters() {
        $('#oc-filter-input').keyup(function () {
            var query = $(this).val();
            if(query == '') {
                $('.oc-label-item').each(function (index, obj) {
                    $(obj).show();
                    $('#oc_'+index).show();
                });
            }
            else {
                $('.oc-label-item').each(function (index, obj) {
                    var item = $(obj);
                    var label = item.html();
                    if(label.indexOf(query) > -1) {
                        item.show(); $('#oc_'+index).show();
                    }
                    else{
                        item.hide(); $('#oc_'+index).hide();
                    }
                });
            }
        });

        $('#usr-filter-input').keyup(function () {
            var query = $(this).val();
            if(query == '') {
                $('.usr-item').each(function (index, obj) {
                    $(obj).show();
                });
            }
            else {
                $('.usr-item').each(function (index, obj) {
                    var item = $(obj);
                    var label = item.html();
                    if(label.indexOf(query) > -1) {
                        item.show();
                    }
                    else{
                        item.hide();
                    }
                });
            }
        });

    }

    function build_tips() {
        oc_tip = d3.select("body").append("div")
            .attr("class", "tooltip")
            .style("opacity", 0);
        usr_tip = d3.select("body").append("div")
            .attr("class", "tooltip")
            .style("opacity", 0);

        var tip_style = 'font-size:18px; font-family: Georgia; font-weight: bold;';

        d3.selectAll('.oc-label-item').each(function () {
            var div = d3.select(this);
            div.on('mouseover', function () {
                var content = div.html();
                var oc_label = div.attr('data-oc-label');

                if (content !== '' && oc_label.length > MAX_NUM_LABEL_CHARS) {
                    var box = div[0][0].getBoundingClientRect();
                    oc_tip
                        .transition().duration(200)
                        .style('opacity', .95);
                    oc_tip
                        .html('<span style=\"'+tip_style+'\">' + oc_label + '</span>');

                    var tipBox = oc_tip[0][0].getBoundingClientRect();
                    var top = +box.top - 3;
                    var left = +box.right - tipBox.width - 6;

                    oc_tip
                        .style('top', top + 'px')
                        .style('left', left + 'px');
                }
            });
            div.on('mouseout', function () {
                oc_tip
                    .transition().duration(200)
                    .style('opacity', 0);
            });
        });

        d3.selectAll('.oc-item').each(function () {
            var div = d3.select(this);
            div.on('mouseover', function () {
                var content = div.html();
                var oc_label = div.attr('data-oc-label');
                var usr_label = mapping[oc_label];

                if (content !== '' && usr_label.length > MAX_NUM_LABEL_CHARS) {
                    var box = div[0][0].getBoundingClientRect();
                    var top = +box.top - 3;
                    var left = +box.left;
                    usr_tip
                        .transition().duration(200)
                        .style('opacity', .95);

                    usr_tip
                        .html('<span style=\"'+tip_style+'\">' + usr_label + '</span>')
                        .style('top', top + 'px')
                        .style('left', left + 'px');
                }
            });
            div.on('mouseout', function () {
                usr_tip
                    .transition().duration(200)
                    .style('opacity', 0);
            });
        });

        d3.selectAll('.usr-item').each(function () {
            var div = d3.select(this);
            div.on('mouseover', function () {
                var content = div.html();
                var usr_label = div.attr('data-usr-label');

                if (content !== '' && usr_label.length > MAX_NUM_LABEL_CHARS) {
                    var box = div[0][0].getBoundingClientRect();
                    var top = +box.top - 3;
                    var left = +box.left - 10;
                    usr_tip
                        .transition().duration(200)
                        .style('opacity', .95);

                    usr_tip
                        .html('<span style=\"'+tip_style+'\"\>' + usr_label + '</span>')
                        .style('top', top + 'px')
                        .style('left', left + 'px');
                }
            });
            div.on('mouseout', function () {
                usr_tip
                    .transition().duration(200)
                    .style('opacity', 0);
            });
        });
    }

    function clear_mapping_item(oc_id) {
        if (oc_usr_id_mapping[oc_id] !== null) {
            $('#' + oc_id).html('');
            var oc_label = $('#' + oc_id).attr('data-oc-label');
            mapping[oc_label] = null;
            var usr_id = oc_usr_id_mapping[oc_id];
            $('#' + usr_id).show();
            oc_usr_id_mapping[oc_id] = null;
        }
    }

    function clear_mapping() {
        for (var oc_id in oc_usr_id_mapping) {
            clear_mapping_item(oc_id);
        }
    }

    function match_items() {
        clear_mapping();
        $('.usr-item').each(function () {
            var usr_obj = $(this);
            var usr_label = usr_obj.attr('data-usr-label');
            var usr_id = usr_obj.attr('id');
            $('.oc-item').each(function () {
                var oc_obj = $(this);
                var oc_label = oc_obj.attr('data-oc-label');
                var oc_id = oc_obj.attr('id');
                if(usr_label == oc_label) {
                    mapping[oc_label] = usr_label;
                    oc_usr_id_mapping[oc_id] = usr_id;
                    var short_usr_label = usr_label_mapping[usr_label];
                    oc_obj.html(short_usr_label);
                    usr_obj.hide();
                }
            });
        });
    }

    function generate_mapping() {
        var output = [];
        for (var oc_label in mapping) {
            var usr_label = mapping[oc_label];
            if (usr_label !== null) {
                var matching = {};
                matching['Open Clinica Item'] = oc_label;
                matching['User Defined Item'] = usr_label;
                output.push(matching);
            }
        }
        return output;
    }

    function export_mapping() {
        var output = generate_mapping();
        if (output.length > 0) {
            var str = JSON.stringify(output);
            var blob = new Blob([str], {type: "application/json"});
            saveAs(blob, "mapping.json");
        }
    }

    function update_submission() {
        $.ajax({
            url: baseApp + "/submission/update",
            type: "POST",
            data: {step: "feedback-data"},
            success: function () {
                window.location.href = baseApp + "/views/feedback-data";
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
            }
        });
    }

    function upload_mapping() {
        var output = generate_mapping();
        if (output.length > 0) {
            $.ajax({
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                url: baseApp + "/upload/mapping",
                type: "POST",
                dataType: 'json',
                data: JSON.stringify(output),
                success: function () {
                    update_submission();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
                }
            });
        }
        else {
            update_submission();
        }
    }

    function shorten_label(label) {
        var slabel = label;
        if(slabel.length > MAX_NUM_LABEL_CHARS) {
            slabel = slabel.substring(0,MAX_NUM_LABEL_CHARS-5);
            slabel += '...';
        }
        return slabel;
    }

    function callback_build_usr_list(data) {
        usr_list = data;

        for (var i = 0; i < usr_list.length; i++) {
            var usr_label = usr_list[i];
            var short_usr_label = shorten_label(usr_label);
            usr_label_mapping[usr_label] = short_usr_label;
            var usr_id = 'usr_' + i;
            var html = '<div id=\"' + usr_id + '\" class="label label-info usr-item ui-widget-content" data-usr-label=\"' + usr_label + '\">' + short_usr_label + '</div>';
            $('#right-col-area').append(html);
            $('#' + usr_id).draggable({
                revert: true,
                helper: 'clone',
                appendTo: 'body'
            });
        }

        //initialize mapping
        for (var i = 0; i < oc_labels.length; i++) {
            var oc_item_label = oc_labels[i];
            mapping[oc_item_label] = null;
            var oc_id = 'oc_' + i;
            oc_usr_id_mapping[oc_id] = null;
        }

        //build tips and filters
        build_tips();
        build_filters();
    }

    function build_usr_list() {
        if(!TESTING) {
            $.ajax({
                url: baseApp + "/submission/user-items",
                type: "GET",
                success: callback_build_usr_list,
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
                }
            });
        }
        else {
            callback_build_usr_list(usr_list);
        }
    }

    function handle_usr_item_drop(event, ui) {

        var oc_label = $(this).attr('data-oc-label');
        var oc_id = $(this).attr('id');
        var usr_label = ui.draggable.attr('data-usr-label');
        if (mapping[oc_label] == null) { // can be mapped
            mapping[oc_label] = usr_label;
            oc_usr_id_mapping[oc_id] = ui.draggable.attr('id');
            var html = usr_label_mapping[usr_label];
            $(this).html(html);
            ui.draggable.hide();
        }
    }

    function handle_oc_item_click() {
        var oc_id = $(this).attr('id');
        clear_mapping_item(oc_id);
    }

    function callback_build_oc_list(data) {
        oc_list = data;

        var path_items = data[0].split('\\');
        oc_path.push(path_items[0]);//event
        oc_path.push(path_items[1]);//crf
        oc_path.push(path_items[2]);//crf version
        //<span class="label label-warning">Warning</span>
        var event_span = '<span class="label label-warning">'+path_items[0]+'</span>';
        var crf_span = '<span class="label label-warning">'+path_items[1]+'</span>';
        var crfv_span = '<span class="label label-warning">'+path_items[2]+'</span>';

        var path_html = 'Event: ' + event_span + ' &#8594; CRF: ' + crf_span + ' &#8594; CRF version: ' + crfv_span;
        $('#path-area').html('<h4><div>'+path_html+'</div></h4><br>');

        for (var i = 0; i < oc_list.length; i++) {
            var arr = oc_list[i].split('\\');
            var oc_label = arr[3];
            var short_oc_label = shorten_label(oc_label);
            oc_label_mapping[oc_label] = short_oc_label;
            oc_labels.push(oc_label);

            //append html
            var html = '<div class="label label-primary oc-label-item" data-oc-label=\"' + oc_label + '\">' + short_oc_label + '</div><hr>';
            $('#oc-label-col-area').append(html);

            var oc_id = 'oc_' + i;
            html = '<div id=\"' + oc_id + '\" class="oc-item ui-widget-header" data-oc-label=\"' + oc_label + '\"></div><hr>';
            $('#oc-col-area').append(html);

            //add droppable behavior
            $('#' + oc_id).droppable({
                tolerance: 'pointer',
                hoverClass: "label-warning",
                drop: handle_usr_item_drop
            });
            $('#' + oc_id).click(handle_oc_item_click);
        }

        build_usr_list();
    }


    function build_oc_list() {
        if(!TESTING) {
            $.ajax({
                url: baseApp + "/metadata/targetedCrf",
                type: "GET",
                success: callback_build_oc_list,
                error: function (jqXHR, textStatus, errorThrown) {
                    console.log(jqXHR.status + " " + textStatus + " " + errorThrown);
                }
            });
        }
        else{
            callback_build_oc_list(oc_list);
        }
    }

    function test() {
        if(TESTING) {
            oc_list = [];
            usr_list = [];
            var delim = '\\';
            var head = 'event' + delim + 'crf' + delim + 'version' + delim;

            oc_list.push(head+'long_long_long_long_long_long_long_long_long_oc_label');
            for(var i=0; i<100; i++) {
                var oclabel = head+'oc_label_'+i;
                oc_list.push(oclabel);
            }

            usr_list.push('oc_label_3');
            usr_list.push('oc_label_5');
            usr_list.push('a_very_loooooooooooooooooooooooooooong_label');

            for(var i=0; i<50; i++) {
                var usrlabel = 'usr_label_'+i;
                usr_list.push(usrlabel);
            }
        }
    }

    function init() {
        test();
        build_oc_list();
    }

    function stepback() {
        window.location.href = baseApp + "/views/data";
    }

    $(document).ready(function () {
        init();

        $('#auto-map-btn').click(match_items);
        $('#clear-map-btn').click(clear_mapping);
        $('#export-map-btn').click(export_mapping);
        $('#map-proceed-btn').click(upload_mapping);
        $('#map-back-btn').click(stepback);
    });
});
