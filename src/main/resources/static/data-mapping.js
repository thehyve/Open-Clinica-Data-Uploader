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
})

// initializing and button listeners
$(document).ready(function() {
    d3.tsv("test-data.tsv", function (data) {
        uploaded_data = data;
        var treeData = prepareTreeData(data);
        visualizeTree(treeData);
    });//d3.tsv

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
        });//d3.tsv
    });//auto-map-btn click

    $('#clear-map-btn').click(function() {
        for(var j=0; j<ocitems.length; j++) {
            var item = ocitems[j];
            item.connected = false;
            item.usrname = '';
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
    baseSvg.on("dblclick.zoom", null);

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

        // d3.selectAll('.itemCircle').each(function (d) { console.log(d);
        //     var r = d3.select(this).attr('r'); console.log(r);
        //     if(r < 5) d3.select(this).transition().attr('r', 5);
        // });
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
            }//if depth is 4, i.e. it is a leaf node
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
