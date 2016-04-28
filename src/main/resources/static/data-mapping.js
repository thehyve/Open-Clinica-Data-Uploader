var uploaded_data;
var uploaded_mapping;
var usritems = [];
var ocitems = [];
var gridData = [];
var baseSvg;
var zoomListener;
var hoveredItem = null;
// update and store mouse position
var mousepos = {x:0, y:0};
$( document ).on( "mousemove", function( event ) {
    var offset = $('#tree-container').offset();
    mousepos.x = event.pageX - offset.left;
    mousepos.y = event.pageY - offset.top; //console.log(mousepos);
});
$( window ).on('resize', function() {
    //resize base svg
    baseSvg.attr('width', $( window ).width());
    //position the items in the matching area
    var w = 200, m = 5;
    var x0 = +d3.select('#baseSvg').attr('width') - w - 5*m;
    var y0 = +d3.select('.ocrect').attr('y');
    var items_height = positionMatchingItems(x0, y0);
    //if in the plain view, position the grid-items
    var is_tree_view = $('#tree-toggle').prop('checked');
    if(!is_tree_view) positionGridItems(5,5);
})

// initializing and button listeners
$(document).ready(function() {
    d3.tsv("test-data.tsv", function (data) {
        uploaded_data = data;
        var treeData = prepareTreeData(data);
        visualizeTree(treeData);
        visualizeMatchingArea();
    });//d3.tsv

    $('#tree-toggle').change(function() {
        // console.log('change ' + $(this).prop('checked'));
        //clear the div #tree-container
        $('#tree-container').empty();
        var is_tree_view = $(this).prop('checked');
        if(is_tree_view) {
            var treeData = prepareTreeData(uploaded_data);
            visualizeTree(treeData);
            visualizeMatchingArea();
        }
        else{
            gridData = prepareGridData(uploaded_data);
            visualizeGrid(gridData);
            visualizeMatchingArea();
        }
    });

    $('#auto-map-btn').click(function(){
        d3.tsv('test-mapping.tsv', function(mapping) {
            uploaded_mapping = mapping;
            for(var i=0; i<mapping.length; i++) {
                var inst = mapping[i];
                var usr_name = inst['USR'];
                var oc_name = inst['OC'];
                clearManualItems(usr_name);
                for(var j=0; j<ocitems.length; j++) {
                    var item = ocitems[j];
                    if(item.ocname == oc_name) {
                        item.connected = true;
                        item.usrname = usr_name;
                        break;
                    }
                }//for each mapping-pair in oc-items
            }//for each mapping instance
            updateMatchingItems();
            var is_tree_view = $('#tree-toggle').prop('checked');
            if(!is_tree_view) {
                d3.selectAll('.mitem').each(function(d,i){
                    if(d.connected) {
                        var m = 5;
                        d3.selectAll('.griditem').each(function(gridd, gridi) {
                            if(gridd.name == d.usrname) {
                                gridd.x = +d.x - 2*m - d.w;
                                gridd.y = +d.y;
                                gridd.mapped = true;
                                gridd.matchingItem = d;
                            }
                        });
                    }
                });
                updateGridItems(250);
                positionGridItems(5,5);
            }
        });//d3.tsv
    });//auto-map-btn click

    $('#clear-map-btn').click(function() {
        for(var j=0; j<ocitems.length; j++) {
            var item = ocitems[j];
            item.connected = false;
            item.usrname = '';
        }
        updateMatchingItems();
        var is_tree_view = $('#tree-toggle').prop('checked');
        if(!is_tree_view) {
            for(var i=0; i<gridData.length; i++) {
                gridData[i].mapped = false;
                gridData[i].matchingItem = null;
            }
            positionGridItems(5,5);
        }
    });

    $('#download-map-btn').click(function() {
        var output = "USR\tOC\n";
        for(var j=0; j<ocitems.length; j++) {
            var item = ocitems[j];
            if(item.connected) {
                output += item.usrname + "\t" + item.ocname + "\n";
            }
        }
        var zip = new JSZip();
        zip.file("my_mapping.tsv",  output);
        var content = zip.generate({type:"blob"});
        saveAs(content, "my_mapping.zip");
    });

    function clearManualItems(usr_name) {
        for(var j=0; j<ocitems.length; j++) {
            var item = ocitems[j];
            if(item.usrname == usr_name) {
                item.connected = false;
                item.usrname = "";
            }
        }
    }

    $('#map-proceed-btn').click(function() {
        var isValid = true;
        if(isValid) {
            window.location.replace(baseApp + "/patients");
        }
    })

});//end of the function $(document).ready...

function updateGridItems(duration) {
    d3.selectAll('.griditem').each(function(d, i) {
        var rect = d3.select(this).select('rect');
        var text = d3.select(this).select('text');
        rect.transition().duration(duration).attr('x', +d.x).attr('y', +d.y);
        text.transition().duration(duration).attr('x', +d.x + d.w / 2).attr('y', +d.y + d.h / 2);
    });
}

function updateMatchingItems() {
    var is_tree_view = $('#tree-toggle').prop('checked');
    if(is_tree_view) {
        d3.selectAll('.mitem').each(function(d,i){
            var mitem = d3.select(this);
            var usrtext = mitem.select('.usrtext');
            var connector = mitem.select('.connector');
            if(d.connected) {
                usrtext.text(d.usrname);
                usrtext.style('fill-opacity',0).transition().duration(500).style('fill-opacity','1');
                connector.transition().style('fill', 'dimgrey');
            }
            else{
                usrtext.text('');
                connector.transition().style('fill', 'lightgrey');
            }
        });
    }
    else {
        d3.selectAll('.mitem').each(function(d,i){
            var mitem = d3.select(this);
            var usrtext = mitem.select('.usrtext');
            var connector = mitem.select('.connector');
            usrtext.text('');
            if(d.connected) {  connector.transition().style('fill', 'dimgrey'); }
            else{ connector.transition().style('fill', 'lightgrey'); }
        });
    }
}//update matching items

function prepareTreeData(data) {
    ocitems = [];
    //convert csv/tsv to json hierarchy
    //------ assisting functions BEGIN ------
    function getChild(hierarchy, name) {
        for(var i=0; i<hierarchy['children'].length; i++) {
            var child = hierarchy['children'][i];
            if(child['name'] == name) {
                return child;
            }
        }
        var branch = {};
        branch.name = name;
        branch.children = [];
        hierarchy.children.push(branch);
        return branch;
    }

    function hasChild(hierarchy, name) {
        for(var i=0; i<hierarchy['children'].length; i++) {
            var child = hierarchy['children'][i];
            if(child['name'] == name) {
                return true;
            }
        }
        return false;
    }
    //------ assisting functions END ------

    var headernames = d3.keys(data[0]);
    var itemnames = [];
    var headername_dict = {};
    for (var i = 0; i<headernames.length; i++) {
        var upper = headernames[i].toUpperCase();
        if(upper == "EVENTNAME" || upper == "CRFNAME" || upper == "CRFVERSION") {
            headername_dict[upper] = headernames[i];
        }
        else if(upper !== "STUDYSUBJECTID" && upper !== "STUDY_SITE" && upper !== "EVENTREPEAT"){
            itemnames.push(headernames[i]);
        }
    }

    var combo_items = {};
    data.forEach(function(d){
        var eventname = d[headername_dict["EVENTNAME"]];
        var crfname = d[headername_dict["CRFNAME"]];
        var crfversion = d[headername_dict["CRFVERSION"]];
        var combo = eventname+"\t"+crfname+"\t"+crfversion;
        if(!(combo in combo_items)){
            combo_items[combo] = [];
        }
        for(var i=0; i<itemnames.length; i++) {
            if(d[itemnames[i]] !== "" && combo_items[combo].indexOf(itemnames[i]) == -1) {
                combo_items[combo].push(itemnames[i]);
            }
        }
    });
    // console.log(combo_items);

    var hierarchy = {};
    hierarchy.name = "Study";
    hierarchy.children = [];
    for(combo in combo_items) {
        var s = combo.split("\t");
        var eventname = s[0];
        var crfname = s[1];
        var crfversion = s[2];

        var branch_event = getChild(hierarchy, "Event: "+eventname);
        var branch_crf = getChild(branch_event, "CRF: "+crfname);
        var branch_crf_v = getChild(branch_crf, "CRF Version: "+crfversion);
        for(var k=0; k<combo_items[combo].length; k++) {
            var item = combo_items[combo][k];
            if(usritems.indexOf(item) == -1) {
                usritems.push(item);
            }
            if(!hasChild(branch_crf_v, item)) {
                var child = {};
                child.name = item;
                branch_crf_v.children.push(child);
            }
        }
    }//for

    // console.log(hierarchy);
    return hierarchy;
}

function visualizeTree(treeData) {
    // Calculate total nodes, max label length
    var totalNodes = 0;
    var maxLabelLength = 0;
    // Misc. variables
    var i = 0;
    var duration = 750;
    var root;

    // size of the diagram
    var viewerWidth = $( window ).width();
    // var viewerWidth = $(document).width()>1000?$(document).width():1000;
    var viewerHeight = 800; // $(document).height();

    var tree = d3.layout.tree()
        .size([viewerHeight, viewerWidth]);

    // define a d3 diagonal projection for use by the node paths later on.
    var diagonal = d3.svg.diagonal()
        .projection(function(d) {
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
    visit(treeData, function(d) {
        totalNodes++;
        maxLabelLength = Math.max(d.name.length, maxLabelLength);

    }, function(d) {
        return d.children && d.children.length > 0 ? d.children : null;
    });


    // sort the tree according to the node names

    function sortTree() {
        tree.sort(function(a, b) {
            return b.name.toLowerCase() < a.name.toLowerCase() ? 1 : -1;
        });
    }
    // Sort the tree initially incase the JSON isn't in a sorted order.
    sortTree();

    // Define the zoom function for the zoomable tree
    function zoom() {
        treeg.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
    }


    // define the zoomListener which calls the zoom function on the "zoom" event constrained within the scaleExtents
    zoomListener = d3.behavior.zoom().scaleExtent([0.1, 3]).on("zoom", zoom);

    // define the baseSvg, attaching a class for styling and the zoomListener
    baseSvg = d3.select("#tree-container").append("svg")
        .attr("width", viewerWidth)
        .attr("height", viewerHeight)
        .attr("class", "overlay")
        .attr('id','baseSvg')
        .call(zoomListener);

    // Helper functions for collapsing and expanding nodes.
    function collapse(d) {
        if (d.children) {
            d._children = d.children;
            d._children.forEach(collapse);
            d.children = null;
        }
    }

    function expand(d) {
        if (d._children) {
            d.children = d._children;
            d.children.forEach(expand);
            d._children = null;
        }
    }

    // Toggle children function
    function toggleChildren(d) {
        if (d.children) {
            d._children = d.children;
            d.children = null;
        } else if (d._children) {
            d.children = d._children;
            d._children = null;
        }
        return d;
    }

    // Toggle children on click.
    function click(d) {
        if (d3.event.defaultPrevented) return; // click suppressed
        d = toggleChildren(d);
        update(d);
    }

    function update(source) {
        // Compute the new height, function counts total children of root node and sets tree height accordingly.
        // This prevents the layout looking squashed when new nodes are made visible or looking sparse when nodes are removed
        // This makes the layout more consistent.
        var levelWidth = [1];
        var childCount = function(level, n) {

            if (n.children && n.children.length > 0) {
                if (levelWidth.length <= level + 1) levelWidth.push(0);

                levelWidth[level + 1] += n.children.length;
                n.children.forEach(function(d) {
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
        nodes.forEach(function(d) {
            d.y = (d.depth * (maxLabelLength * 10)); //maxLabelLength * 10px
            // alternatively to keep a fixed scale one can set a fixed depth per level
            // Normalize for fixed-depth by commenting out below line
            // d.y = (d.depth * 500); //500px per level.
        });

        // Update the nodes…
        node = treeg.selectAll("g.node")
            .data(nodes, function(d) {
                return d.id || (d.id = ++i);
            });

        // Enter any new nodes at the parent's previous position.
        var nodeEnter = node.enter().append("g")
            .attr("class", "node")
            .attr("transform", function(d) {
                return "translate(" + source.y0 + "," + source.x0 + ")";
            })
            .on('click', click);

        nodeEnter.append("circle")
            .attr('class', function(d) {
                if(d.children == null) return d._children ? "nodeCircle" : "itemCircle";
                else return d.children ? "nodeCircle" : "itemCircle";
            })
            .attr("r", 0);

        nodeEnter.append("text")
            .attr("x", function(d) {
                return d.children || d._children ? -10 : 10;
            })
            .attr("dy", ".35em")
            .attr('class', 'nodeText')
            .attr("text-anchor", function(d) {
                return d.children || d._children ? "end" : "start";
            })
            .text(function(d) {
                return d.name;
            })
            .style("fill-opacity", 0);


        // Update the text to reflect whether node has children or not.
        node.select('text')
            .attr("x", function(d) {
                return d.children || d._children ? -10 : 10;
            })
            .attr("text-anchor", function(d) {
                return d.children || d._children ? "end" : "start";
            })
            .text(function(d) {
                return d.name;
            });

        // Change the circle fill depending on whether it has children and is collapsed
        node.select("circle").attr("r", 5);

        node.select('.nodeCircle')
            .style("fill", function(d) {
                return d._children ? "lightsteelblue" : "#fff";
            });

        // Transition nodes to their new position.
        var nodeUpdate = node.transition()
            .duration(duration)
            .attr("transform", function(d) {
                d.y += 50;
                return "translate(" + d.y + "," + d.x + ")";
            });
        // .each('start', function(){
        //     console.log('start');
        // })
        // .each('end', function(){
        //     console.log('end');
        // });

        // Fade the text in
        nodeUpdate.select("text")
            .style("fill-opacity", 1);

        // Transition exiting nodes to the parent's new position.
        var nodeExit = node.exit().transition()
            .duration(duration)
            .attr("transform", function(d) {
                return "translate(" + source.y + "," + source.x + ")";
            })
            .remove();

        nodeExit.select("circle")
            .attr("r", 0);

        nodeExit.select("text")
            .style("fill-opacity", 0);

        // Update the links…
        var link = treeg.selectAll("path.link")
            .data(links, function(d) {
                return d.target.id;
            });

        // Enter any new links at the parent's previous position.
        link.enter().insert("path", "g")
            .attr("class", "link")
            .attr("d", function(d) {
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
            .duration(duration)
            .attr("d", diagonal);

        // Transition exiting nodes to the parent's new position.
        link.exit().transition()
            .duration(duration)
            .attr("d", function(d) {
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
        nodes.forEach(function(d) {
            d.x0 = d.x;
            d.y0 = d.y;
        });
    }

    function interactWithItems() {
        /*
         * ------ item drag behavior ------
         */
        function dragstart(d) {
            if(d.depth == 4) {
                d3.event.sourceEvent.stopPropagation();
            }
        }
        function dragging(d) {
            if(d.depth == 4) {
                var coord  = d3.mouse(this);
                var c = d3.select(this).select('circle');
                c.attr('cx', coord[0]).attr('cy', coord[1]);
                var t = d3.select(this).select('text');
                t.attr('x', (coord[0]+10)).attr('y', coord[1]);
                // update links
                d3.selectAll('.link').each(function(ld){
                    if(ld.target.id == d.id) {
                        d3.select(this).attr("d", function(d) {
                            var source = {
                                x: ld.source.x,
                                y: ld.source.y
                            };
                            var target = {
                                x: ld.target.x+coord[1],
                                y: ld.target.y+coord[0]
                            };
                            return diagonal({
                                source: source,
                                target: target
                            });
                        });//adjust the link's curve
                    }//if the link connects to the selected item node
                });
                //update item rectangles
                hoveredItem = null;
                d3.selectAll('.usrrect').each(function(d) {
                    var rect = d3.select(this);
                    var hovering = isHovering(rect, mousepos.x, mousepos.y);
                    if(hovering) {
                        hoveredItem = d3.select(this.parentNode);
                        rect.style('fill', 'orange');
                    }
                    else rect.style('fill', 'LightSteelBlue');
                });
            }
        }
        function dragend(d) {
            if(d.depth == 4) {
                var c = d3.select(this).select('circle');
                c.transition().attr('cx', 0).attr('cy', 0);
                var t = d3.select(this).select('text');
                t.transition().attr('x', 10).attr('y', 0);
                // update links
                d3.selectAll('.link').each(function(ld){
                    if(ld.target.id == d.id) {
                        d3.select(this).transition().attr("d", function(d) {
                            var source = {
                                x: ld.source.x,
                                y: ld.source.y
                            };
                            var target = {
                                x: ld.target.x,
                                y: ld.target.y
                            };
                            return diagonal({
                                source: source,
                                target: target
                            });
                        });//adjust the link's curve
                    }//if the link connects to the selected item node
                });
                // update usr-item's text in the matching area
                if(hoveredItem !== null) {
                    var msg = validateOCitems(d.name);
                    if(msg == null) {
                        var item_data = hoveredItem.datum();
                        item_data.connected = true;
                        item_data.usrname = d.name;
                        hoveredItem.select('.usrtext').text(d.name);
                        hoveredItem.select('.connector').style('fill', 'dimgrey');
                    }
                    else {
                        // alert('The item "' + d.name + '" has been mapped to "' + msg + '"!');
                        var message = 'The item "<strong>' + d.name + '</strong>" has been mapped to "<strong>' + msg + '</strong>"!';
                        var alert_html = '<span id="duplicate_alert" class="alert alert-danger" role="alert">'+message+'</span>';
                        $(alert_html).insertAfter('#empty_space');
                        // $("#duplicate_alert").fadeTo(2000, 0).slideUp("slow", function(){
                        //     // $("#duplicate_alert").alert('close');
                        // });

                        $("#duplicate_alert").fadeTo(6000, 0.3, function() {
                            $("#duplicate_alert").alert('close');
                        });
                    }
                }
            }//if depth is 4, i.e. it is a leaf node
        }

        function validateOCitems(new_usr_defined_item_name) {
            for(var i=0; i<ocitems.length; i++) {
                var item = ocitems[i];
                if(item.connected && item.usrname == new_usr_defined_item_name) {
                    return item.ocname;
                }//if
            }//for
            return null;
        }

        function isHovering(rect, mousex, mousey) {
            var x = +rect.attr('x'), y = +rect.attr('y');
            var w = +rect.attr('width'), h = +rect.attr('height');
            var hon = mousex > x && mousex < (x+w);
            var ver = mousey > y && mousey < (y+h);
            // console.log(x+","+y+","+w+","+h+" -- " + mousex + ", " + mousey + ": " + hon + " and " + ver);
            if(hon && ver) return true;
            else return false;
        }

        var itemdrag = d3.behavior.drag()
            .on('dragstart', dragstart)
            .on('drag', dragging)
            .on('dragend', dragend);
        d3.selectAll('.node').call(itemdrag);

        /*
         * ------ item mouse over/out behavior ------
         */
        function itemover(d) {
            adjustColors(d, true);
        }
        function itemout(d) {
            adjustColors(d, false);
        }
        function adjustColors(d, highlight) {
            traceBackward(d, highlight);
            traceForward(d, highlight);
            d3.selectAll('.node circle').each(function(d){
                d3.select(this).classed('highlightCircle', d.highlight);
            });
            // console.log(root);
        }
        function traceBackward(d, highlight) {
            d.highlight = highlight;
            if(d.depth > 0 && d.parent) {
                traceBackward(d.parent, highlight);
            }
        }
        function traceForward(d, highlight) {
            d.highlight = highlight;
            if(d.depth < 4 && d.children) {
                for(var i=0; i<d.children.length; i++) {
                    traceForward(d.children[i], highlight);
                }
            }
        }

        d3.selectAll('.node')
            .on('mouseover', itemover)
            .on('mouseout', itemout);
    }

    // Append a group which holds all nodes and which the zoom Listener can act upon.
    var matchg = baseSvg.append("g").attr('id', 'matchg').attr('class','matching_area');
    var treeg = baseSvg.append("g").attr('id', 'treeg');

    // Define the root
    root = treeData;
    root.y0 = 0;
    root.x0 = viewerHeight / 2;

    // Layout the tree initially and center on the root node.
    update(root);
    interactWithItems();
}

function prepareGridData(data) {
    ocitems = [];

    var headernames = d3.keys(data[0]);
    var itemnames = [];
    var headername_dict = {};
    for (var i = 0; i<headernames.length; i++) {
        var upper = headernames[i].toUpperCase();
        if(upper == "EVENTNAME" || upper == "CRFNAME" || upper == "CRFVERSION") {
            headername_dict[upper] = headernames[i];
        }
        else if(upper !== "STUDYSUBJECTID" && upper !== "STUDY_SITE" && upper !== "EVENTREPEAT"){
            itemnames.push(headernames[i]);
        }
    }

    gridData = [];
    for(var i=0; i<itemnames.length; i++) {
        var item = {};
        item.name = itemnames[i];
        item.mapped = false;
        item.matchingItem = null;
        gridData.push(item);
    }
    return gridData;
}

function visualizeGrid(gridData) {
    // size of the diagram
    var viewerWidth = $( window ).width();
    var viewerHeight = 800; // $(document).height();

    var grid_zoomListener = d3.behavior.zoom().scaleExtent([1, 1]).on("zoom", function(){});
    baseSvg = d3.select("#tree-container").append("svg")
        .attr("width", viewerWidth)
        .attr("height", viewerHeight)
        .attr("class", "overlay")
        .attr('id','baseSvg')
        .call(grid_zoomListener);

    var matchg = baseSvg.append("g").attr('id', 'matchg').attr('class','matching_area');
    var gridg = baseSvg.append("g").attr('id', 'gridg');

    var w = 200, h = 30;
    var griditem = d3.select('#gridg').selectAll('.griditem')
        .data(gridData).enter().append('g').attr('class', 'griditem');
    griditem.append('rect')
        .attr('class', 'griditem_rect')
        .attr('rx', 8).attr('ry', 8)
        .attr('width', w)
        .attr('height', h)
        .on('mouseover', function(d,i){
            d3.select(this).style('fill', 'red');
        })
        .on('mouseout', function(d,i){
            d3.select(this).style('fill', 'LightSteelBlue');
        });
    griditem.append('text')
        .attr('class', 'griditem_text')
        .text(function(d) {
            return d.name;
        })
        .on('mouseover', function(d,i){
            var rect = d3.select(this.parentNode).select('.griditem_rect');
            rect.style('fill', 'red');
        })
        .on('mouseout', function(d,i){
            var rect = d3.select(this.parentNode).select('.griditem_rect');
            rect.style('fill', 'LightSteelBlue');
        });

    var m = 5;
    positionGridItems(m,m);
    listenToGridItemDrag();

    function listenToGridItemDrag() {
        var was_dragged = false;
        function dragstart(d) {
            was_dragged = false;
            d.prev_x = +d3.select(this).select('rect').attr('x');
            d.prev_y = +d3.select(this).select('rect').attr('y');
        }
        function dragging(d) {
            d.x = +d.x + d3.event.dx;
            d.y = +d.y + d3.event.dy;
            updateGridItems(1);
            //update item rectangles
            hoveredItem = null;
            d3.selectAll('.usrrect').each(function(d) {
                var rect = d3.select(this);
                var hovering = isHovering(rect, mousepos.x, mousepos.y);
                if(hovering) {
                    hoveredItem = d3.select(this.parentNode);
                    rect.style('fill', 'orange');
                }
                else rect.style('fill', 'LightSteelBlue');
            });
            was_dragged = true;
        }
        function dragend(d) {
            if(was_dragged) {
                if(hoveredItem !== null) {
                    hoveredItem.select('.usrrect').style('fill', 'LightSteelBlue');
                    var item_data = hoveredItem.datum();
                    if(item_data.connected) {//already occupied
                        d.x = +d.prev_x; d.y = +d.prev_y;
                    }
                    else {
                        item_data.connected = true;
                        item_data.usrname = d.name;
                        d.x = +hoveredItem.select('.usrrect').attr('x');
                        d.y = +hoveredItem.select('.usrrect').attr('y');
                        if(d.matchingItem !== null) {
                            d.matchingItem.connected = false;
                            d.matchingItem.usrname = '';
                        }
                        d.mapped = true;
                        d.matchingItem = item_data;
                        updateMatchingItems();
                    }
                }//if a usr-rect in a matching item is hovered
                else {
                    // d.x = d.prev_x; d.y = d.prev_y;
                    d.mapped = false;
                    if(d.matchingItem !== null) {
                        d.matchingItem.connected = false;
                        d.matchingItem.usrname = '';
                        updateMatchingItems();
                    }
                }
                updateGridItems(250);
                positionGridItems(5,5);
            }//if the griditem was dragged

        }

        function isHovering(rect, mousex, mousey) {
            var x = +rect.attr('x'), y = +rect.attr('y');
            var w = +rect.attr('width'), h = +rect.attr('height');
            var hon = mousex > x && mousex < (x+w);
            var ver = mousey > y && mousey < (y+h);
            // console.log(x+","+y+","+w+","+h+" -- " + mousex + ", " + mousey + ": " + hon + " and " + ver);
            if(hon && ver) return true;
            else return false;
        }

        var itemdrag = d3.behavior.drag()
            .on('dragstart', dragstart)
            .on('drag', dragging)
            .on('dragend', dragend);
        d3.selectAll('.griditem').call(itemdrag);

    }
}

function positionGridItems(x0,y0) {
    var duration = 700;
    var w = +d3.select('.griditem_rect').attr('width');
    var h = +d3.select('.griditem_rect').attr('height');
    var m = 5;
    var _width = 2*w + 8*m;
    var xlimit = d3.select('#baseSvg').attr('width') - _width - w;

    var col_count = 0;
    var row_count = 0;

    for(var i=0; i<gridData.length; i++) {
        var d = gridData[i];
        if(!d.mapped) {
            d.w = w; d.h = h;
            var x1 = x0 + col_count*(w+m);
            if(x1 > xlimit) {
                x1 = x0; col_count = 1; row_count++;
            }
            else {
                col_count++;
            }
            d.x = x1;

            var y1 = y0 + row_count*(h+m);
            d.y = y1;
        }
    }

    d3.selectAll('.griditem').each(function(d,i) {
        var rect = d3.select(this).select('rect');
        var text = d3.select(this).select('text');
        rect.transition().duration(duration).attr('x', d.x).attr('y', d.y);
        text.transition().duration(duration).attr('x',d.x + d.w / 2).attr('y', d.y + d.h / 2);
    });
}

function visualizeMatchingArea() {
    //construct synthetic data items
    for(var i=1; i<30; i++) {
        var item = {};
        item.idx = i;
        item.usrname = '';
        item.ocname = 'ocitem'+i;
        item.connected = false;
        ocitems.push(item);
    }

    //construct items
    var mitem = d3.select('#matchg').selectAll('.mitem')
        .data(ocitems).enter().append('g').attr('class', 'mitem');

    var w = 200, h = 30, m = 5;
    mitem.append('rect')
        .attr('class', 'ocrect')
        .attr('rx', 6).attr('ry', 6)
        .attr('width', w)
        .attr('height', h)
        .on('mouseover', function(d,i){
            hoveredItem = d3.select(this.parentNode);
            d3.select(this).style('fill', 'orange');
        })
        .on('mouseout', function(d,i){
            hoveredItem = null;
            d3.select(this).style('fill', 'steelblue');
        });
    mitem.append('text')
        .attr('class', 'octext')
        .text(function(d) {
            return d.ocname;
        })
        .on('mouseover', function(d,i){
            hoveredItem = d3.select(this.parentNode);
            var rect = d3.select(this.parentNode).select('.ocrect');
            rect.style('fill', 'orange');
        })
        .on('mouseout', function(d,i){
            hoveredItem = null;
            var rect = d3.select(this.parentNode).select('.ocrect');
            rect.style('fill', 'steelblue');
        });
    mitem.append('rect')
        .attr('class', 'usrrect')
        .attr('rx', 6).attr('ry', 6)
        .attr('width', w)
        .attr('height', h)
        .on('mouseover', function(d,i){
            hoveredItem = d3.select(this.parentNode);
            d3.select(this).style('fill', 'orange');
        })
        .on('mouseout', function(d,i){
            hoveredItem = null;
            d3.select(this).style('fill', 'LightSteelBlue');
        });
    mitem.append('text')
        .attr('class', 'usrtext')
        .text(function(d) {
            return d.usrname;
        })
        .on('mouseover', function(d,i){
            hoveredItem = d3.select(this.parentNode);
            var rect = d3.select(this.parentNode).select('.usrrect');
            rect.style('fill', 'orange');
        })
        .on('mouseout', function(d,i){
            hoveredItem = null;
            var rect = d3.select(this.parentNode).select('.usrrect');
            rect.style('fill', 'LightSteelBlue');
        });
    mitem.append('rect')
        .attr('class', 'connector')
        .attr('width', 2*m)
        .attr('height', 2*m)
        .on('click', function(d) {
            if(d.connected) {
                var is_tree_view = $('#tree-toggle').prop('checked');
                if(!is_tree_view) {
                    d3.selectAll('.griditem').each(function(gridd){
                        if(gridd.name == d.usrname) {
                            gridd.mapped = false;
                            gridd.matchingItem = null;
                            positionGridItems(5,5);
                        }
                    });
                }//if
                d.connected = false;
                d.usrname = "";
                d3.select(this.parentNode).transition().select('.usrtext').text('');
                d3.select(this).transition().style('fill', 'lightgrey');
            }
        });

    var x0 = +d3.select('#baseSvg').attr('width') - w - 5*m;
    var y0 = m;
    var items_height = positionMatchingItems(x0, y0);

    baseSvg.on('click', function(){
        if(is_mouse_in_matching_area()) {
            d3.event.stopPropagation();
        }
    });

    function is_mouse_in_matching_area() {
        var m = 5;
        var mouseX = event.pageX, mouseY = event.pageY;
        var _width = 2*w + 8*m;
        var x = d3.select('#baseSvg').attr('width') - _width;
        var y = m;
        var is_within_width = mouseX > x && mouseX < d3.select('#baseSvg').attr('width');
        var is_within_height = mouseY > y && mouseY < d3.select('#baseSvg').attr('height');
        if(is_within_width && is_within_height) return true;
        else return false;
    }

    $(document).on('mousewheel', '#baseSvg', function(e) {

        if(is_mouse_in_matching_area()) {
            var s = zoomListener.scale();
            zoomListener.scaleExtent([s, s]);

            var v = e.originalEvent.wheelDelta / 8;
            y0 += v;
            if(y0 < -items_height+10*m) y0 = -items_height+10*m;
            if(y0 > d3.select('#baseSvg').attr('height')-10*m) {
                y0 = d3.select('#baseSvg').attr('height')-10*m;
            }

            var x0 = +d3.select('#baseSvg').attr('width') - w - 5*m;
            positionMatchingItems(x0, y0);
        }
        else {
            zoomListener.scaleExtent([0.1, 3]);
        }
        // console.log(is_mouse_in_matching_area());

    });
    // console.log(usritems);
}//visualizeMatchingArea

function positionMatchingItems(x0,y0) {
    var w = +d3.select('.usrrect').attr('width');
    var h = +d3.select('.usrrect').attr('height');
    var m = 5;
    var total_y = 0;
    d3.selectAll('.ocrect').transition()
        .attr('x', function(d) {
            d.w = w; d.h = h;
            d.x = x0;
            return x0;
        })
        .attr('y', function(d,i){
            var y1 = y0 + i*(h+m);
            d.y = y1;
            total_y += (h + m);
            return y1;
        });
    d3.selectAll('.octext').transition()
        .attr('x', function(d) {
            return d.x + d.w / 2;
        })
        .attr('y', function(d) {
            return d.y + d.h / 2;
        });
    d3.selectAll('.usrrect').transition()
        .attr('x', function(d) {
            return (d.x - w - 2*m);
        })
        .attr('y', function(d,i){
            return d.y;
        });
    d3.selectAll('.usrtext').transition()
        .attr('x', function(d) {
            return d.x - d.w / 2 - 2*m;
        })
        .attr('y', function(d) {
            return d.y + d.h / 2;
        });
    d3.selectAll('.connector').transition()
        .attr('x', function(d) {
            return d.x - 2*m;
        })
        .attr('y', function(d) {
            return d.y + d.h / 2 - m;
        });

    var is_tree_view = $('#tree-toggle').prop('checked');
    if(!is_tree_view) {//also position the grid items if in grid view
        d3.selectAll('.mitem').each(function(d,i){
            if(d.connected) {
                var m = 5;
                d3.selectAll('.griditem').each(function(gridd, gridi) {
                    if(gridd.name == d.usrname) {
                        gridd.x = d.x - 2*m - d.w;
                        gridd.y = d.y;
                    }
                });
            }
        });
        updateGridItems(250);
    }

    return total_y;
}  