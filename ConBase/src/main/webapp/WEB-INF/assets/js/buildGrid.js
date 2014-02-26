/**
 * Taken from -
 * http://talkaboutstuff.hubpages.com/hub/HTML5-Drawing-grids-on-your-Canvas-as-guides-to-help-you-draw
 */

function buildGrids(gridPixelSize, color, gap, canvas)
{
    var gridlines = canvas.gridlines;
    if (gridlines)
    {
        $.each(gridlines, function (index, gridline) {
            canvas.remove(gridline);
        });
    }
    gridlines = [];
    canvas.gridlines = gridlines;
    //var canvas = $('#'+div+'').get(0); Commented by Abhishek
    //var ctx = canvas.getContext("2d");

    //ctx.lineWidth = 0.5;
    //ctx.strokeStyle = color;


// horizontal grid lines
    for(var i = 0; i <= canvas.getHeight(); i = i + gridPixelSize)
    {
        //ctx.beginPath();
        //ctx.moveTo(0, i);
        //ctx.lineTo(canvas.width, i);
        if(i % parseInt(gap) == 0) {
            //ctx.lineWidth = 2;
            var line = new fabric.Line([0, i, canvas.getWidth(), i],{ stroke: color, strokeWidth: 2, selectable:false, left:0});
            canvas.add(line);
            gridlines.push(line);
        } else {
            //canvas.add(new fabric.Line([0, i, canvas.getWidth(), i],{ stroke: color, strokeWidth: 0.5, selectable:false, left:0}));
            var line = new fabric.Line([0, i, canvas.getWidth(), i],{ stroke: color, strokeWidth: 0.5, selectable:false, left:0});
            canvas.add(line);
            gridlines.push(line);
            //ctx.lineWidth = 0.5;
        }
        //ctx.closePath();
        //ctx.stroke();
    }

// vertical grid lines
    for(var j = 0; j <= canvas.getWidth(); j = j + gridPixelSize)
    {
        //ctx.beginPath();
        //ctx.moveTo(j, 0);
        //ctx.lineTo(j, canvas.height);
        if(j % parseInt(gap) == 0) {
            //ctx.lineWidth = 2;
            //canvas.add(new fabric.Line([j, 0, j, canvas.getHeight()],{ stroke: color, strokeWidth: 2, selectable:false, top:0}));
            var line = new fabric.Line([j, 0, j, canvas.getHeight()],{ stroke: color, strokeWidth: 2, selectable:false, top:0});
            canvas.add(line);
            gridlines.push(line);
        } else {
            //ctx.lineWidth = 0.5;
            //canvas.add(new fabric.Line([j, 0, j, canvas.getHeight()],{ stroke: color, strokeWidth: 0.5, selectable:false, top:0}));
            var line = new fabric.Line([j, 0, j, canvas.getHeight()],{ stroke: color, strokeWidth: 0.5, selectable:false, top:0});
            canvas.add(line);
            gridlines.push(line);
        }
        //ctx.closePath();
        //ctx.stroke();
    }

}
