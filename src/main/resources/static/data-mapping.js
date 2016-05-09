var baseApp = "";

var usr_data;//store user-uploaded tabular data in JSON
var oc_data;//store oc tree data in JSON
var map_data;//store user-uploaded mapping data
var usr_item_data;//store user-edited mapping data
var selectedOCItem = null;//the selected OC item in the tree
var selectedUsrItem = null;//the selected Usr item in the list
var mapped_ocitems = [];//an array containing the paths of mapped oc items

var baseSvg;
var treeg, listg;

var tipDiv;
var leaf_depth = 5;
var zoomListener;
var rect_w = 100, rect_h = 15;//default rect size
// update and store mouse position
var mousepos = {x: 0, y: 0};
$(document).on("mousemove", function (event) {
    var offset = $('#tree-container').offset();
    mousepos.x = event.pageX - offset.left;
    mousepos.y = event.pageY - offset.top; //console.log(mousepos);
});
$(window).on('resize', function () {
    //resize base svg
    baseSvg.attr('width', $(window).width());
})

// initializing and button listeners
$(document).ready(function () {

    d3.json('data/test-usr-data.json', function (data) {
        initialize();
        usr_data = data;
        visualizeUsrList(usr_data);

        var metadataCallSuccess = function (data) {
            console.log('metadataTree call successful');
            oc_data = data;
            visualizeOCTree(data);
        };

        $.ajax({
            url: "/metadata-tree",
            type: "GET",
            cache: false,
            success: metadataCallSuccess,
            error: function () {
                console.log("Fetching metadata from the server failed.");
            }
        });


        d3.json('data/test-map-data.json', function (data) {
            map_data = data;
        });
    });

    //for testing: mapping data
    $('#auto-map-btn').click(function () {
        clearMapping();
        for (var i = 0; i < map_data.length; i++) {
            var pair = map_data[i];
            var ocd = findOCitem(pair['study'], pair['eventName'], pair['crfName'], pair['crfVersion'], pair['ocItemName']);
            var usritem_obj = findUsrItem(pair['usrItemName']);
            //console.log(pair); console.log(ocd);

            if (ocd !== null && usritem_obj !== null) {
                var ocname = ocd.name;
                var ocCRFv = ocd.parent.name;
                var ocCRF = ocd.parent.parent.name;
                var ocEventName = ocd.parent.parent.parent.name;
                var ocStudy = ocd.parent.parent.parent.parent.name;
                var path = ocStudy + "\t" + ocEventName + "\t" + ocCRF + "\t" + ocCRFv + "\t" + ocname;
                var d = usritem_obj.datum();
                if (mapped_ocitems.indexOf(path) == -1) {
                    mapped_ocitems.push(path);
                    d.mapped = true;
                    d.ocItemName = ocname;
                    d.ocCRFv = ocCRFv;
                    d.ocCRF = ocCRF;
                    d.ocEventName = ocEventName;
                    d.ocStudy = ocStudy;
                    d.ocItemData = ocd;
                    d.ocPath = path;
                }
                positionUsrList(usr_item_data, 800);
            }
        }

        function findOCitem(study, event, crf, crfv, itemname) {
            for (var _study in oc_data.children) {
                _study = oc_data.children[_study]; //console.log(_study);
                if (_study.name == study) {
                    for (var _event in _study.children) {
                        _event = _study.children[_event]; //console.log(_event);
                        if (_event.name == event) {
                            for (var _crf in _event.children) {
                                _crf = _event.children[_crf]; //console.log(_crf);
                                if (_crf.name == crf) {
                                    for (var _crfv in _crf.children) {
                                        _crfv = _crf.children[_crfv]; //console.log(_crfv);
                                        if (_crfv.name == crfv) {
                                            for (var _item in _crfv.children) {
                                                _item = _crfv.children[_item]; //console.log(_item);
                                                if (_item.name == itemname) {
                                                    return _item;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return null;
        }

        function findUsrItem(itemname) {
            var item = null;
            d3.selectAll('.usritem').each(function (d) {
                if (d.usrItemName == itemname) {
                    item = d3.select(this);
                }
            });
            return item;
        }
    });//auto-map-btn click

    function clearMapping() {
        for (var i = 0; i < usr_item_data.length; i++) {
            var d = usr_item_data[i];
            if (d.mapped) {
                var idx = mapped_ocitems.indexOf(d.ocPath);
                mapped_ocitems.splice(idx, 1);
                d.mapped = false;
                d.ocItemName = "";
                d.ocCRFv = "";
                d.ocCRF = "";
                d.ocEventName = "";
                d.ocStudy = "";
                d.ocItemData = null;
                d.ocPath = "";
            }
        }
        positionUsrList(usr_item_data);
    }

    $('#clear-map-btn').click(function () {
        clearMapping();
    });

    $('#download-map-btn').click(function () {
        var output = [];

        for (var i = 0; i < usr_item_data.length; i++) {
            var d = usr_item_data[i];
            if (d.mapped) {
                var item = {};
                item['study'] = d.ocStudy;
                item['eventName'] = d.ocEventName;
                item['crfName'] = d.ocCRF;
                item['crfVersion'] = d.ocCRFv;
                item['ocItemName'] = d.ocItemName;
                item['usrItemName'] = d.usrItemName;
                output.push(item);
            }
        }

        var zip = new JSZip();
        zip.file("my_mapping.json", JSON.stringify(output));
        var content = zip.generate({type: "blob"});
        saveAs(content, "my_mapping.zip");
    });

    $('#map-proceed-btn').click(function () {
        var isValid = true;
        if (isValid) {
            window.location.replace(baseApp + "/patients");
        }
    })

});//end of the function $(document).ready...


function initialize() {
    var viewerWidth = $(window).width();
    var viewerHeight = 800; // $(document).height();
    // Define the zoom function for the zoomable tree
    function zoom() {
        treeg.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
        listg.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
    }

    // define the zoomListener which calls the zoom function on the "zoom" event constrained within the scaleExtents
    zoomListener = d3.behavior.zoom().scaleExtent([0.1, 3]).on("zoom", zoom);

    // define the baseSvg, attaching a class for styling and the zoomListener
    baseSvg = d3.select("#tree-container")
        .append("svg")
        .attr("class", "overlay")
        .attr('id', 'baseSvg')
        .attr("width", viewerWidth)
        .attr("height", viewerHeight)
        .call(zoomListener)
        .on("dblclick.zoom", null);

    tipDiv = d3.select("body").append("div")
        .attr("class", "tooltip")
        .style("opacity", 0);
}//initialize


var root;
var _duration = 750;
var tree;
var viewerWidth, viewerHeight;
var maxLabelLength = 0;
var _i = 0;// Misc. variables
var diagonal;

function visualizeOCTree(treeData) {
    // Calculate total nodes, max label length
    var totalNodes = 0;

    // size of the diagram
    viewerWidth = $(window).width();
    viewerHeight = 800; // $(document).height();
    tree = d3.layout.tree()
        .size([viewerHeight, viewerWidth]);

    // define a d3 diagonal projection for use by the node paths later on.
    diagonal = d3.svg.diagonal()
        .projection(function (d) {
            return [d.y, d.x];
        });

    // A recursive helper function for performing some setup by walking through all nodes
    function visit(parent, visitFn, childrenFn) {
        if (!parent) return;

        visitFn(parent);

        var children = childrenFn(parent);
        if (children) {
            var count = children.length;
            for (var i = 0; i < count; i++) {
                visit(children[i], visitFn, childrenFn);
            }
        }
    }

    // Call visit function to establish maxLabelLength
    visit(treeData, function (d) {
        totalNodes++;
        maxLabelLength = Math.max(d.name.length, maxLabelLength);

    }, function (d) {
        return d.children && d.children.length > 0 ? d.children : null;
    });

    // sort the tree according to the node names

    function sortTree() {
        tree.sort(function (a, b) {
            return b.name.toLowerCase() < a.name.toLowerCase() ? 1 : -1;
        });
    }

    // Sort the tree initially incase the JSON isn't in a sorted order.
    sortTree();



    treeg = baseSvg.append("g").attr('id', 'treeg');

    // Define the root
    root = treeData;
    root.y0 = 0;
    root.x0 = viewerHeight / 2;

    // Layout the tree initially and center on the root node.
    updateOCTree(root);
    handleOCItemInteraction();
}//visualizeOCTree

function updateOCTree(source) {
    // Compute the new height, function counts total children of root node and sets tree height accordingly.
    // This prevents the layout appearing squashed when new nodes are made visible or appearing sparse when nodes are removed
    // This makes the layout more consistent.
    var levelWidth = [1];
    var childCount = function (level, n) {

        if (n.children && n.children.length > 0) {
            if (levelWidth.length <= level + 1) levelWidth.push(0);

            levelWidth[level + 1] += n.children.length;
            n.children.forEach(function (d) {
                childCount(level + 1, d);
            });
        }
    };
    childCount(0, root);
    var newHeight = d3.max(levelWidth) * 25; // 25 pixels per line
    tree = tree.size([newHeight, viewerWidth]);

    // Compute the new tree layout.
    var nodes = tree.nodes(root).reverse(),
        links = tree.links(nodes);

    // Set widths between levels based on maxLabelLength.
    nodes.forEach(function (d) {
        // d.y = (d.depth * (maxLabelLength * 10)); //maxLabelLength * 10px
        // alternatively to keep a fixed scale one can set a fixed depth per level
        // Normalize for fixed-depth by commenting out below line
        d.y = (d.depth * 150); //150px per level.
    });

    // Update the nodes…
    node = treeg.selectAll("g.node")
        .data(nodes, function (d) {
            return d.id || (d.id = ++_i);
        });

    // Enter any new nodes at the parent's previous position.
    var nodeEnter = node.enter().append("g")
        .attr("class", "node")
        .attr("transform", function (d) {
            return "translate(" + source.y0 + "," + source.x0 + ")";
        });


    nodeEnter.each(function (d, i) {
        var node = d3.select(this);
        if ((d.children == null && d._children) || d.children) {
            node.append('circle').attr('class', 'nodeCircle').attr('r', 0);
        }
        else {
            node.append('rect').attr('class', 'itemRect')
                .attr('rx', 4).attr('ry', 4)
                .attr('width', rect_w).attr('height', rect_h);
            // node.append('rect').attr('class', 'itemRect')
            //     .attr('rx', 4).attr('ry', 4)
            //     .attr('width', rect_w).attr('height', rect_h);
        }
    });

    nodeEnter.append("text")
        .attr("x", function (d) {
            return d.children || d._children ? -10 : 10;
        })
        .attr("dy", ".35em")
        .attr('class', 'nodeText')
        .attr("text-anchor", function (d) {
            return d.children || d._children ? "end" : "start";
        })
        .text(function (d) {
            return d.name;
        });

    node.select('text').text(function (d) {
        return d.name;
    });


    // Update the text to reflect whether node has children or not.
    node.select('text')
        .attr("x", function (d) {
            return d.children || d._children ? -10 : 10;
        })
        .attr('dy', function (d) {
            return d.children || d._children ? 5 : rect_h / 1.2;
        })
        .attr("text-anchor", function (d) {
            return d.children || d._children ? "end" : "start";
        })
        .text(function (d) {
            d.shortTexted = false;
            var len = this.getComputedTextLength();
            if (len > rect_w) {
                d.shortTexted = true;
                return d.name.substring(0, 9) + "...";
            }
            return d.name;
        });
    tree = tree.size([newHeight, 10]);

    // Change the circle fill depending on whether it has children and is collapsed
    node.select("circle").attr("r", 5);

    node.select('.nodeCircle')
        .style("fill", function (d) {
            return d._children ? "lightsteelblue" : "#fff";
        });

    // Transition nodes to their new position.
    var nodeUpdate = node.transition()
        .duration(_duration)
        .attr("transform", function (d) {
            d.y -= 50;
            if (d.depth == leaf_depth) {
                return "translate(" + d.y + "," + (d.x - rect_h / 2) + ")";
            }
            else return "translate(" + d.y + "," + d.x + ")";
        });

    // Fade the text in
    nodeUpdate.select("text").style("fill-opacity", 1);

    // Transition exiting nodes to the parent's new position.
    var nodeExit = node.exit().transition()
        .duration(_duration)
        .attr("transform", function (d) {
            return "translate(" + source.y + "," + source.x + ")";
        })
        .remove();

    nodeExit.select("circle")
        .attr("r", 0);

    nodeExit.select("text")
        .style("fill-opacity", 0);

    // Update the links…
    var link = treeg.selectAll("path.link")
        .data(links, function (d) {
            return d.target.id;
        });

    // Enter any new links at the parent's previous position.
    link.enter().insert("path", "g")
        .attr("class", "link")
        .attr("d", function (d) {
            var o = {
                x: source.x0,
                y: source.y0
            };
            return diagonal({
                source: o,
                target: o
            });
        });

    // Transition links to their new position.
    link.transition()
        .duration(_duration)
        .attr("d", diagonal);

    // Transition exiting nodes to the parent's new position.
    link.exit().transition()
        .duration(_duration)
        .attr("d", function (d) {
            var o = {
                x: source.x,
                y: source.y
            };
            return diagonal({
                source: o,
                target: o
            });
        })
        .remove();

    // Stash the old positions for transition.
    nodes.forEach(function (d) {
        d.x0 = d.x;
        d.y0 = d.y;
    });
}

// Helper functions for collapsing and expanding nodes.
function collapse(d) {
    d.collapsed = true;
    if (d.children) {
        d._children = d.children;
        d._children.forEach(collapse);
        d.children = null;
    }
}

function expand(d) {
    d.collapsed = false;
    if (d._children) {
        d.children = d._children;
        d.children.forEach(expand);
        d._children = null;
    }
}

// Toggle children function
function toggleChildren(d) {
    if (d.children) {
        collapse(d);
    } else if (d._children) {
        expand(d);
    }
    return d;
}

function handleOCItemInteraction() {
    // Toggle children on click.
    var ready_for_second_click = true;

    function click(d) {
        var event = d3.event;
        window.setTimeout(function () {
            ready_for_second_click = true;
        }, 1000);
        if (d3.event.defaultPrevented) return; // click suppressed

        if (ready_for_second_click) {
            d = toggleChildren(d);
            updateOCTree(d);
            handleOCItemInteraction();
            ready_for_second_click = false;
            positionUsrList(usr_item_data, _duration);
        }
        tipDiv.style("opacity", 0);
    }

    /*
     * ------ item mouse over/out behavior ------
     */
    function itemover(d) {
        if (d.shortTexted) {
            tipDiv.transition().duration(200).style("opacity", .95);
            tipDiv.html('<span style="font-size:18px;">' + d.name + '</span>')
                .style("left", (d3.event.pageX) + "px")
                .style("top", (d3.event.pageY - 28) + "px");
        }
        adjustColors(d, true);
        if(d.depth == leaf_depth) selectedOCItem = d3.select(this);
    }

    function itemout(d) {
        tipDiv.transition().style("opacity", 0);
        adjustColors(d, false);
        if(d.depth == leaf_depth) selectedOCItem = null;
    }

    function adjustColors(d, highlight) {
        traceBackward(d, highlight);
        traceForward(d, highlight);
        d3.selectAll('.node').each(function (d) {
            d3.select(this).select('circle')
                .classed('highlighted', d.highlight);
            d3.select(this).select('rect')
                .classed('highlighted', d.highlight);
        });
    }

    function traceBackward(d, highlight) {
        d.highlight = highlight;
        if (d.depth > 0 && d.parent) {
            traceBackward(d.parent, highlight);
        }
    }

    function traceForward(d, highlight) {
        d.highlight = highlight;
        if (d.depth < leaf_depth && d.children) {
            for (var i = 0; i < d.children.length; i++) {
                traceForward(d.children[i], highlight);
            }
        }
    }

    d3.selectAll('.node')
        .on('mouseover', itemover)
        .on('mouseout', itemout)
        .on('click', click);
}//function handleOCItemInteraction

function visualizeUsrList(usrData) {
    listg = d3.select('#baseSvg').append('g').attr('id', 'listg');
    usr_item_data = [];
    var usr_path_names = ['CRFNAME', 'CRFVERSION', 'EVENTNAME', 'EVENTREPEAT', 'STUDYSUBJECTID', 'STUDY'];
    var usr_item_names = [];
    for (var i = 0; i < usrData.length; i++) {
        for (var key in usrData[i]) {
            var upper_key = key.toUpperCase();
            if (usr_path_names.indexOf(upper_key) == -1 && usr_item_names.indexOf(key) == -1) {
                var listitem = {};
                listitem.usrItemName = key;
                listitem.mapped = false;
                listitem.ocStudy = "";
                listitem.ocEventName = "";
                listitem.ocCRF = "";
                listitem.ocCRFv = "";
                listitem.ocItemName = "";
                listitem.ocItemData = null;
                usr_item_data.push(listitem);
                usr_item_names.push(key);
            }
        }
    }

    var usritem = listg.selectAll('.listitem').data(usr_item_data);
    usritem.enter().append('g').attr('class', 'usritem');

    usritem.append('rect')
        .attr('rx', 4).attr('ry', 4)
        .attr('width', rect_w).attr('height', rect_h);
    usritem.append('text')
        .attr('dx', 10)
        .attr('dy', rect_h / 1.2)
        .attr('text-anchor', 'start')
        .text(function (d) {
            return d.usrItemName;
        });
    usritem.select('text')
        .text(function (d) {
            d.shortTexted = false;
            var len = this.getComputedTextLength();
            if (len > rect_w) {
                d.shortTexted = true;
                return d.usrItemName.substring(0, 9) + "...";
            }
            return d.usrItemName;
        });

    usritem.on('mouseover', usrItemMouseOver)
        .on('mouseout', usrItemMouseOut)
        .on('click', usrItemClick);

    var usrItemDrag = d3.behavior.drag()
        .on('dragstart', usrItemDragstart)
        .on('drag', usrItemDragging)
        .on('dragend', usrItemDragend);
    usritem.call(usrItemDrag);
    positionUsrList(usr_item_data);

    function usrItemMouseOver(d) {
        d3.select(this).select('rect').style('fill', 'Orange');
        if (d.shortTexted) {
            var len = computeTextLength(d3.select(this).select('text'));
            tipDiv.transition()
                .duration(200)
                .style("opacity", .95);
            tipDiv.html('<span style="font-size:18px;">' + d.usrItemName + '</span>')
                .style("left", (d3.event.pageX - len) + "px")
                .style("top", (d3.event.pageY - 30) + "px");
        }

        selectedUsrItem = d3.select(this);
    }

    function usrItemMouseOut() {
        tipDiv.transition().style("opacity", 0);
        d3.select(this).select('rect').style('fill', 'LightSteelBlue');
        selectedUsrItem = null;
    }

    function computeTextLength(selection) {
        selection.text(selection.datum().usrItemName);
        var len = selection[0][0].getComputedTextLength();
        selection.text(function (d) {
            d.shortTexted = false;
            if (len > rect_w) {
                d.shortTexted = true;
                return d.usrItemName.substring(0, 9) + "...";
            }
            return d.usrItemName;
        });
        return len;
    }

    function usrItemClick(d) {
        if (d.mapped) {
            var idx = mapped_ocitems.indexOf(d.ocPath);
            mapped_ocitems.splice(idx, 1);
            d.mapped = false;
            d.ocItemName = "";
            d.ocCRFv = "";
            d.ocCRF = "";
            d.ocEventName = "";
            d.ocStudy = "";
            d.ocItemData = null;
            d.ocPath = "";
            positionUsrList(usr_item_data);
        }
    }

    function usrItemDragstart(d) {
        d3.event.sourceEvent.stopPropagation();
        d.offsetX = d3.mouse(this)[0] - d3.select(this).select('rect').attr('x');
        d.offsetY = d3.mouse(this)[1] - d3.select(this).select('rect').attr('y');
    }

    function usrItemDragging(d) {
        var coord = d3.mouse(this);
        var x = coord[0] - d.offsetX, y = coord[1] - d.offsetY;
        d3.select(this).select('rect').attr('x', x).attr('y', y);
        d3.select(this).select('text').attr('x', x).attr('y', y);
    }

    function usrItemDragend(d) {
        if (selectedOCItem !== null) {
            usrItemClick(d);
            var ocd = selectedOCItem.datum();
            var ocname = ocd.name;
            var ocCRFv = ocd.parent.name;
            var ocCRF = ocd.parent.parent.name;
            var ocEventName = ocd.parent.parent.parent.name;
            var ocStudy = ocd.parent.parent.parent.parent.name;
            var path = ocStudy + "\t" + ocEventName + "\t" + ocCRF + "\t" + ocCRFv + "\t" + ocname;

            if (mapped_ocitems.indexOf(path) == -1) {
                mapped_ocitems.push(path);
                d.mapped = true;
                d.ocItemName = ocname;
                d.ocCRFv = ocCRFv;
                d.ocCRF = ocCRF;
                d.ocEventName = ocEventName;
                d.ocStudy = ocStudy;
                d.ocItemData = selectedOCItem.datum();
                d.ocPath = path;
            }
        }
        positionUsrList(usr_item_data);
    }

}//function visualizeUsrList

function positionUsrList(usr_item_data, time) {
    if (!time) time = 250;
    for (var i = 0; i < usr_item_data.length; i++) {
        var uitem = usr_item_data[i];
        //position the usr items when they are not mapped
        if (!uitem.mapped) {
            uitem.x = 1000;
            uitem.y = 15 + i * (rect_h + 5);
        }
        //position the usr items when they are mapped
        else {
            var head = uitem.ocItemData;
            uitem.x = head.y + rect_w + 2;
            uitem.y = head.x - rect_h / 2;

            if (head.parent.collapsed) {
                head = head.parent;
                if (head.parent.collapsed) head = head.parent;
                if (head.parent.collapsed) head = head.parent;
                if (head.parent.collapsed) head = head.parent;
                uitem.x = head.y + 9;
                uitem.y = head.x - 7;
            }
        }
    }

    d3.selectAll('.usritem').each(function (d, i) {
        var rect = d3.select(this).select('rect');
        var text = d3.select(this).select('text');
        rect.transition().duration(time).attr('x', d.x).attr('y', d.y);
        text.transition().duration(time).attr('x', d.x).attr('y', d.y);
    });
}//function positionUsrList()
