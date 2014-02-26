

function drawLine() {

    // create a rectangle with angle=45
    var startPoints = [
        {x: 10, y: 42},
        {x: 155, y: 0},
        {x: 200, y: 200},
        {x: 10, y: 256}
    ];

    var polygon = new fabric.Polygon(startPoints, {
        left: 0,
        top: 0,
        fill: 'purple',
        selectable: true,
        pointDraggers:[],
        pointAdders:[]
    });
    canvas.add(polygon);
    console.log(polygon);
    createDraggerAndAdder(polygon);
    canvas.on('object:over', function(e) {
        var activeObjects = [];
        if (e.target._objects) {
            activeObjects = activeObjects.concat(e.target._objects);
        } else {
            activeObjects.push(e.target);
        }
        $.each(activeObjects, function( index, activeObject ) {
            if (activeObject.isPointDragger || activeObject.isPointAdder) {
                activeObject.selectable = true;
            }
        });
    });

    canvas.on('object:out', function(e) {
        var activeObjects = [];
        if (e.target._objects) {
            activeObjects = activeObjects.concat(e.target._objects);
        } else {
            activeObjects.push(e.target);
        }
        $.each(activeObjects, function( index, activeObject ) {
            if (activeObject.isPointDragger || activeObject.isPointAdder) {
                activeObject.selectable = false;
            }
        });
    })

    canvas.on('object:modified', function(e) {
        var activeObjects = [];
        if (e.target._objects) {
            activeObjects = activeObjects.concat(e.target._objects);
        } else {
            activeObjects.push(e.target);
        }
        $.each(activeObjects, function( index, activeObject ) {
            if (activeObject.isPointDragger || activeObject.isPointAdder) {
                activeObject.selectable = false;
            }
            if (activeObject.type === "polygon" && activeObject.pointDraggers) {
                polygon._calcDimensions();
                console.log(polygon);
                createDraggerAndAdder(polygon);
            }
        });
    });

    canvas.on('object:modified', function(e) {
        var activeObject = e.target;
        if (activeObject.isPointDragger || activeObject.isPointAdder) {
            var point = activeObject.point;
            //var point2 = activeObject.point2;
            //console.log("Point is");
            // console.log(point);
            var polyCenter = polygon.getCenterPoint();
            var newX = (activeObject.left - polyCenter.x)/polygon.scaleX+5;
            var newY =  (activeObject.top - polyCenter.y)/polygon.scaleY+5;


            var theta = fabric.util.degreesToRadians(polygon.angle);
            var hypotenuse = Math.sqrt(
                Math.pow(newX, 2) +
                    Math.pow(newY, 2));
            var _angle = Math.atan(isFinite((newY) / (newX)) ? (newY) / (newX) : 0);
            if (newX < 0) {
                _angle += Math.PI;
            } else if (newY < 0) {
                _angle += Math.PI*2;
            }
            newX = Math.cos(_angle - theta) * hypotenuse*1;
            newY = Math.sin(_angle - theta) * hypotenuse*1;

            if (activeObject.isPointDragger) {
               var newPoint = {x:newX, y: newY};
                resetControls(polygon, newPoint, activeObject.point);
                point.x= newX;
                point.y= newY;
                polygon._calcDimensions();
                polygon.setCoords();
                createDraggerAndAdder(polygon);
            }

            if (activeObject.isPointAdder) {
               // add a new point at that position
                var newPoint = {x:newX, y: newY};
                var point1 = activeObject.point1;
                resetControlsForAdder(polygon, newPoint, point1);
                polygon._calcDimensions();
                polygon.setCoords();
                createDraggerAndAdder(polygon);
            }
        }
    });
}

function resetControlsForAdder (obj, newPoint, oldPoint) {

    var oldMinX;
    var oldMinY;
    var newMinX;
    var newMinY;
    var newIndex = 0;
    //console.log(obj.get('points'));
    var oldPoints  = obj.get('points');
    $.each(oldPoints, function( index, p ) {
        if(oldMinX) {
            oldMinX = p.x < oldMinX ? p.x : oldMinX;
        } else {
            oldMinX = p.x;
        }
        if(oldMinY) {
            oldMinY = p.y < oldMinY ? p.y : oldMinY;
        } else {
            oldMinY = p.y;
        }
        if (p === oldPoint) {
            newIndex = index+1;
        }
    });
    oldPoints.splice(newIndex, 0, newPoint);
    if(newPoint.x < oldMinX) {
        newMinX = newPoint.x;
    } else {
        newMinX = oldMinX;
    }
    if(newPoint.y < oldMinY) {
        newMinY = newPoint.y;
    } else {
        newMinY = oldMinY;
    }
    //if (newMinX < oldMinX) {
    obj.setLeft(obj.getLeft() + (newMinX - oldMinX)*obj.scaleX);
    //}
    // if (newMinY < oldMinY) {
    obj.setTop(obj.getTop() + (newMinY - oldMinY)*obj.scaleY);
    // }

}

function resetControls (obj, newPoint, oldPoint) {

    var oldMinX;
    var oldMinY;
    var newMinX;
    var newMinY;
    //console.log(obj.get('points'));
    var oldPoints  = obj.get('points');
    $.each(oldPoints, function( index, p ) {
        if(oldMinX) {
            oldMinX = p.x < oldMinX ? p.x : oldMinX;
        } else {
            oldMinX = p.x;
        }
        if(oldMinY) {
            oldMinY = p.y < oldMinY ? p.y : oldMinY;
        } else {
            oldMinY = p.y;
        }
    });
    $.each(oldPoints, function( index, p ) {
        if (p === oldPoint) {
            p = newPoint;
        }
        if(newMinX) {
            newMinX = p.x < newMinX ? p.x : newMinX;
        } else {
            newMinX = p.x;
        }
        if(newMinY) {
            newMinY = p.y < newMinY ? p.y : newMinY;
        } else {
            newMinY = p.y;
        }
    });

    //if (newMinX < oldMinX) {
        obj.setLeft(obj.getLeft() + (newMinX - oldMinX)*obj.scaleX);
    //}
   // if (newMinY < oldMinY) {
        obj.setTop(obj.getTop() + (newMinY - oldMinY)*obj.scaleY);
   // }

}

function createDraggerAndAdder(polygon) {

    var canvas = polygon.canvas;
    $.each(polygon.pointAdders, function( index, p ) {
       canvas.remove(p);
    });
    polygon.pointAdders = [];
    $.each(polygon.pointDraggers, function( index, p ) {
        canvas.remove(p);
    });
    polygon.pointDraggers = [];

    var polygonCenter = polygon.getCenterPoint();
    var theta = fabric.util.degreesToRadians(polygon.angle);

    var polygonPoints = polygon.get('points');
    var translatedPoints = polygon.get('points').map(function(p) {
        var hypotenuse = Math.sqrt(
            Math.pow(p.x*polygon.scaleX, 2) +
                Math.pow(p.y*polygon.scaleY, 2));
        var _angle = Math.atan(isFinite((p.y*polygon.scaleY) / (p.x*polygon.scaleX)) ? (p.y*polygon.scaleY) / (p.x*polygon.scaleX) : 0);
        if (p.x < 0) {
          _angle += Math.PI;
        } else if (p.y < 0) {
            _angle += Math.PI*2;
        }
        var offsetX = Math.cos(_angle + theta) * hypotenuse*1,
            offsetY = Math.sin(_angle + theta) * hypotenuse*1;
        console.log(offsetX +" & "+(p.x)+", "+offsetY+" & "+ p.y);
        return {
            x: polygonCenter.x + offsetX,
            y: polygonCenter.y + offsetY
            //x: polygonCenter.x + p.x*polygon.scaleX,
           // y: polygonCenter.y + p.y*polygon.scaleY
        };
    });

    var firstPoint;
    var prevPoint;

    $.each(translatedPoints, function( index, p ) {
        //console.log(index);
        var circle1 = new fabric.Circle({
            radius: 5,
            fill: 'red',
            left: (p.x-5),
            top: (p.y-5),
            selectable: false,
            hasBorders : false,
            hasControls: false,
            isPointDragger : true,
            point : polygonPoints[index]
        });
        polygon.pointDraggers.push(circle1);
        //group.addWithUpdate(circle1)        ;
        canvas.add(circle1);

        if (!firstPoint) {
            firstPoint = p;
        }

        if (prevPoint) {
            var newX = (p.x + prevPoint.x)/2;
            var newY = (p.y + prevPoint.y)/2;

            var circle2 = new fabric.Circle({
                radius: 5,
                fill: 'blue',
                left: newX-5,
                top: newY-5,
                oldLeft: newX-5,
                oldTop: newY-5,
                hasBorders : false,
                hasControls: false,
                selectable: false,
                isPointAdder : true,
                point1 : polygon.get('points')[index-1],
                point2 : polygon.get('points')[index]
            });
            polygon.pointAdders.push(circle2);
            //group.addWithUpdate(circle1)        ;
            canvas.add(circle2);
        }

    prevPoint = p;
    //canvas.getContext().strokeRect(p.x-5, p.y-5, 10, 10);
    });

    var newX = (firstPoint.x + prevPoint.x)/2;
    var newY = (firstPoint.y + prevPoint.y)/2;

    var circle2 = new fabric.Circle({
        radius: 5,
        fill: 'blue',
        left: newX-5,
        top: newY-5,
        oldLeft: newX-5,
        oldTop: newY-5,
        selectable: false,
        hasBorders : false,
        hasControls: false,
        isPointAdder : true,
        point1 : polygon.get('points')[polygon.get('points').length-1],
        point2 : polygon.get('points')[0]
    });
    polygon.pointAdders.push(circle2);
    canvas.add(circle2);
}