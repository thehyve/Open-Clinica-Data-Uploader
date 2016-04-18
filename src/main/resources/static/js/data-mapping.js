/**
 * Created by bo on 4/17/16.
 */

d3.tsv("/js/test-data.tsv", function(data) {

    data.forEach(function (d) {
        console.log(d);
    });
});