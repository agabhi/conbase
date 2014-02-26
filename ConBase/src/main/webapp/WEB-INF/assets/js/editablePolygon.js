(function (global) {
    "use strict";

    var fabric = global.fabric || (global.fabric = { }),
        extend = fabric.util.object.extend,
        min = fabric.util.array.min,
        max = fabric.util.array.max,
        toFixed = fabric.util.toFixed;

    if (fabric.EditablePolygon) {
        fabric.warn('fabric.EditablePolygon is already defined');
        return;
    }

    /**
     * EditablePolygon class
     * @class fabric.EditablePolygon
     * @extends fabric.Polygon
     * @see {@link fabric.EditablePolygon#initialize} for constructor definition
     */
    fabric.EditablePolygon = fabric.util.createClass(fabric.Polygon, /** @lends fabric.EditablePolygon.prototype */ {
        /**
         * Type of an object
         * @type String
         * @default
         */
        type: 'editable-polygon',

        /**
         * Constructor
         * @param {Array} points Array of points
         * @param {Object} [options] Options object
         * @param {Boolean} [skipOffset] Whether points offsetting should be skipped
         * @return {fabric.Polygon} thisArg
         */
        initialize: function (points, options, skipOffset) {
            options = options || { };
            this.points = points;
            this.pointAdders = [];
            this.pointDraggers = [];
            this.callSuper('initialize', points, options, skipOffset);
            this.createDraggerAndAdder();
        },

        /**
         * @private
         * @param {CanvasRenderingContext2D} ctx Context to render on
         */
        _render: function (ctx) {
            this.callSuper('_render', ctx);
            //this.createDraggerAndAdder();
        },

        createDraggerAndAdder: function () {
            $.each(this.pointAdders, function (index, p) {
                canvas.remove(p);
            });
            this.pointAdders = [];
            $.each(this.pointDraggers, function (index, p) {
                canvas.remove(p);
            });
            this.pointDraggers = [];

            var polygon = this;
            var polygonCenter = this.getCenterPoint();
            var theta = fabric.util.degreesToRadians(this.angle);

            var polygonPoints = this.get('points');
            var translatedPoints = this.get('points').map(function (p) {
                var hypotenuse = Math.sqrt(
                    Math.pow(p.x * polygon.scaleX, 2) +
                        Math.pow(p.y * polygon.scaleY, 2));
                var _angle = Math.atan(isFinite((p.y * polygon.scaleY) / (p.x * polygon.scaleX)) ? (p.y * polygon.scaleY) / (p.x * polygon.scaleX) : 0);
                if (p.x < 0) {
                    _angle += Math.PI;
                } else if (p.y < 0) {
                    _angle += Math.PI * 2;
                }
                var offsetX = Math.cos(_angle + theta) * hypotenuse * 1,
                    offsetY = Math.sin(_angle + theta) * hypotenuse * 1;
                //console.log(offsetX + " & " + (p.x) + ", " + offsetY + " & " + p.y);
                return {
                    x: polygonCenter.x + offsetX,
                    y: polygonCenter.y + offsetY
                };
            });

            var firstPoint;
            var prevPoint;

            $.each(translatedPoints, function (index, p) {
                //console.log(index);
                var circle1 = new fabric.Circle({
                    radius: 5,
                    fill: 'red',
                    left: (p.x - 5),
                    top: (p.y - 5),
                    selectable: false,
                    hasBorders: false,
                    hasControls: false,
                    isPointDragger: true,
                    point: polygonPoints[index],
                    polygon: polygon
                });
                polygon.pointDraggers.push(circle1);
                canvas.add(circle1);

                if (!firstPoint) {
                    firstPoint = p;
                }

                if (prevPoint) {
                    var newX = (p.x + prevPoint.x) / 2;
                    var newY = (p.y + prevPoint.y) / 2;

                    var circle2 = new fabric.Circle({
                        radius: 5,
                        fill: 'blue',
                        left: newX - 5,
                        top: newY - 5,
                        oldLeft: newX - 5,
                        oldTop: newY - 5,
                        hasBorders: false,
                        hasControls: false,
                        selectable: false,
                        isPointAdder: true,
                        point1: polygon.get('points')[index - 1],
                        point2: polygon.get('points')[index],
                        polygon: polygon
                    });
                    polygon.pointAdders.push(circle2);
                    //group.addWithUpdate(circle1)        ;
                    canvas.add(circle2);
                }

                prevPoint = p;
                //canvas.getContext().strokeRect(p.x-5, p.y-5, 10, 10);
            });

            var newX = (firstPoint.x + prevPoint.x) / 2;
            var newY = (firstPoint.y + prevPoint.y) / 2;

            var circle2 = new fabric.Circle({
                radius: 5,
                fill: 'blue',
                left: newX - 5,
                top: newY - 5,
                oldLeft: newX - 5,
                oldTop: newY - 5,
                selectable: false,
                hasBorders: false,
                hasControls: false,
                isPointAdder: true,
                point1: polygon.get('points')[this.get('points').length - 1],
                point2: polygon.get('points')[0],
                polygon: polygon
            });
            polygon.pointAdders.push(circle2);
            canvas.add(circle2);
        },

        resetControlsForDragger: function (newPoint, oldPoint) {

            var oldMinX;
            var oldMinY;
            var newMinX;
            var newMinY;
            var oldPoints = this.get('points');
            $.each(oldPoints, function (index, p) {
                if (oldMinX) {
                    oldMinX = p.x < oldMinX ? p.x : oldMinX;
                } else {
                    oldMinX = p.x;
                }
                if (oldMinY) {
                    oldMinY = p.y < oldMinY ? p.y : oldMinY;
                } else {
                    oldMinY = p.y;
                }
            });
            $.each(oldPoints, function (index, p) {
                if (p === oldPoint) {
                    p = newPoint;
                }
                if (newMinX) {
                    newMinX = p.x < newMinX ? p.x : newMinX;
                } else {
                    newMinX = p.x;
                }
                if (newMinY) {
                    newMinY = p.y < newMinY ? p.y : newMinY;
                } else {
                    newMinY = p.y;
                }
            });
            this.setLeft(this.getLeft() + (newMinX - oldMinX) * this.scaleX);
            this.setTop(this.getTop() + (newMinY - oldMinY) * this.scaleY);
        },

        resetControlsForAdder: function (newPoint, oldPoint) {

            var oldMinX;
            var oldMinY;
            var newMinX;
            var newMinY;
            var newIndex = 0;
            var oldPoints = this.get('points');
            $.each(oldPoints, function (index, p) {
                if (oldMinX) {
                    oldMinX = p.x < oldMinX ? p.x : oldMinX;
                } else {
                    oldMinX = p.x;
                }
                if (oldMinY) {
                    oldMinY = p.y < oldMinY ? p.y : oldMinY;
                } else {
                    oldMinY = p.y;
                }
                if (p === oldPoint) {
                    newIndex = index + 1;
                }
            });
            oldPoints.splice(newIndex, 0, newPoint);
            if (newPoint.x < oldMinX) {
                newMinX = newPoint.x;
            } else {
                newMinX = oldMinX;
            }
            if (newPoint.y < oldMinY) {
                newMinY = newPoint.y;
            } else {
                newMinY = oldMinY;
            }
            this.setLeft(this.getLeft() + (newMinX - oldMinX) * this.scaleX);
            this.setTop(this.getTop() + (newMinY - oldMinY) * this.scaleY);
        }
    });
    $(document).ready(function () {
        canvas.on('object:over', function (e) {
            var activeObjects = [];
            if (e.target._objects) {
                activeObjects = activeObjects.concat(e.target._objects);
            } else {
                activeObjects.push(e.target);
            }
            $.each(activeObjects, function (index, activeObject) {
                if (activeObject.isPointDragger || activeObject.isPointAdder) {
                    activeObject.selectable = true;
                }
            });
        });
        canvas.on('object:out', function (e) {
            var activeObjects = [];
            if (e.target._objects) {
                activeObjects = activeObjects.concat(e.target._objects);
            } else {
                activeObjects.push(e.target);
            }
            $.each(activeObjects, function (index, activeObject) {
                if (activeObject.isPointDragger || activeObject.isPointAdder) {
                    activeObject.selectable = false;
                }
            });
        })

        canvas.on('object:modified', function (e) {
            var activeObjects = [];
            if (e.target._objects) {
                activeObjects = activeObjects.concat(e.target._objects);
            } else {
                activeObjects.push(e.target);
            }
            $.each(activeObjects, function (index, activeObject) {
                activeObject.resizeToScale();
                console.log(activeObject);
                if (activeObject.isPointDragger || activeObject.isPointAdder) {
                    activeObject.selectable = false;
                }
                if (activeObject.type === "editable-polygon") {
                    activeObject._calcDimensions();
                    activeObject.setCoords();
                    activeObject.createDraggerAndAdder();
                }
            });
        });

        canvas.on('object:modified', function (e) {

            var activeObject = e.target;
            if (activeObject.isPointDragger || activeObject.isPointAdder) {
                var point = activeObject.point;
                var polygon = activeObject.polygon;
                var polyCenter = polygon.getCenterPoint();
                var newX = (activeObject.left - polyCenter.x) / polygon.scaleX + 5;
                var newY = (activeObject.top - polyCenter.y) / polygon.scaleY + 5;


                var theta = fabric.util.degreesToRadians(polygon.angle);
                var hypotenuse = Math.sqrt(
                    Math.pow(newX, 2) +
                        Math.pow(newY, 2));
                var _angle = Math.atan(isFinite((newY) / (newX)) ? (newY) / (newX) : 0);
                if (newX < 0) {
                    _angle += Math.PI;
                } else if (newY < 0) {
                    _angle += Math.PI * 2;
                }
                newX = Math.cos(_angle - theta) * hypotenuse * 1;
                newY = Math.sin(_angle - theta) * hypotenuse * 1;

                if (activeObject.isPointDragger) {
                    var newPoint = {x: newX, y: newY};
                    polygon.resetControlsForDragger(newPoint, activeObject.point);
                    point.x = newX;
                    point.y = newY;
                    polygon._calcDimensions();
                    polygon.setCoords();
                    polygon.createDraggerAndAdder();
                }

                if (activeObject.isPointAdder) {
                    // add a new point at that position
                    var newPoint = {x: newX, y: newY};
                    var point1 = activeObject.point1;
                    polygon.resetControlsForAdder(newPoint, point1);
                    polygon._calcDimensions();
                    polygon.setCoords();
                    polygon.createDraggerAndAdder(polygon);
                }
            }
        });

        canvas.on('object:moving', function(options) {
            options.target.set({
                left: Math.round(options.target.left / 10)* 10,
                top: Math.round(options.target.top / 10) * 10
                //width: Math.round(options.target.width / 10)* 10,
                //height: Math.round(options.target.height / 10) * 10
            });
        });

        // set up a listener for the event where the object has been modified
        canvas.observe('object:scaling', function (e) {
            console.log("object:scaling");
            var activeObjects = [];
            if (e.target._objects) {
                activeObjects = activeObjects.concat(e.target._objects);
            } else {
                activeObjects.push(e.target);
            }
            $.each(activeObjects, function (index, activeObject) {
                var scaleX = activeObject.scaleX;
                var scaleY = activeObject.scaleY;

                activeObject.resizeToScale();
                activeObject.set({
                    //left: Math.round(activeObject.left / 10)* 10,
                    //top: Math.round(activeObject.top / 10) * 10
                    //width: Math.round(activeObject.width / 10)* 10,
                    //height: Math.round(activeObject.height / 10) * 10
                });
                if (scaleX > 1) {
                    //activeObject.set({
                     //   width: Math.ceil(activeObject.width / 10) * 10});
                } else {
                   // activeObject.set({
                    //    width: Math.floor(activeObject.width / 10) * 10});
                }
                //console.log(scaleX);
                if (scaleY > 1) {
                    //activeObject.setHeight(Math.ceil(activeObject.height / 10) * 10);
                } else {
                    //activeObject.setHeight(Math.floor(activeObject.height / 10) * 10);
                }

                //activeObject.saveState();
            });
        });
         /*
        fabric.Canvas.prototype._transformObject = function(e) {
            var pointer = fabric.util.getPointer(e, this.upperCanvasEl);
            var adjustedPointer = {
                x: Math.round(pointer.x / 10)* 10,
                y: Math.round(pointer.y / 10)* 10
            };
            var pointerDiff;
            if (this.prevPointer && this.prevSnappedPointer) {
                pointerDiff = {x: pointer.x - this.prevPointer.x, y: pointer.y - this.prevPointer.y}
                var adjustedDiff = {x:pointerDiff.x, y:pointerDiff.y} ;
                if (this.uneffectedDiff) {
                    adjustedDiff.x = adjustedDiff.x + this.uneffectedDiff.x;
                    adjustedDiff.y = adjustedDiff.y + this.uneffectedDiff.y;
                }
                var adjustedSnappedDiff =
                {
                    x: Math.round(adjustedDiff.x / 10)* 10,
                    y: Math.round(adjustedDiff.y / 10)* 10
                };
                var adjustedPointer = {x: this.prevSnappedPointer.x + adjustedSnappedDiff.x, y: this.prevSnappedPointer.y + adjustedSnappedDiff.y};
                //console.log("{Prev pointer = ("+ this.prevPointer.x+", "+this.prevPointer.y + "), PrevSnappedPointer = "+ this.prevSnappedPointer.x+", "+this.prevSnappedPointer.y +")");

            }
            //var adjustedSnappedPointer = {
            //    x: Math.round(adjustedPointer.x / 10)* 10,
            //    y: Math.round(adjustedPointer.y / 10)* 10
            //};


            //console.log("Pointer = ("+ pointer.x+", "+pointer.y +  "), SnappedPointer = ("+ adjustedPointer.x+", "+adjustedPointer.y + ")}");
            if (!this.uneffectedDiff) {
                this.uneffectedDiff= {x: 0, y: 0};
            }
            if (pointerDiff && Math.abs(pointerDiff.x) > 0) {
                if (this.prevSnappedPointer && adjustedPointer.x === this.prevSnappedPointer.x) {
                    this.uneffectedDiff.x += pointerDiff.x;
                } else {
                    this.uneffectedDiff.x = 0;
                }
            }
            if (pointerDiff && Math.abs(pointerDiff.y) > 0) {
                if (this.prevSnappedPointer && adjustedPointer.y === this.prevSnappedPointer.y) {
                    this.uneffectedDiff.y += pointerDiff.y;
                } else {
                    this.uneffectedDiff.y = 0;
                }
            }

            //console.log("UneffectedDiff= ("+ this.uneffectedDiff.x+", "+this.uneffectedDiff.y + ")");
           // console.log(" ");

            this.prevPointer = {x:pointer.x, y: pointer.y};
            this.prevSnappedPointer = adjustedPointer;

            var transform = this._currentTransform;
            transform.reset = false,
                transform.target.isMoving = true;

            this._beforeScaleTransform(e, transform);
            this._performTransformAction(e, transform, adjustedPointer);

            this.renderAll();
        }

          */
        fabric.Canvas.prototype._setObjectScale =  function(localMouse, transform, lockScalingX, lockScalingY, by) {

            var pointer = {x:localMouse.x, y:localMouse.y};
            var adjustedPointer = {
                x: Math.round(pointer.x / 10)* 10,
                y: Math.round(pointer.y / 10)* 10
            };
            var pointerDiff;
            if (this.prevPointer && this.prevSnappedPointer) {
                pointerDiff = {x: pointer.x - this.prevPointer.x, y: pointer.y - this.prevPointer.y}
                var adjustedDiff = {x:pointerDiff.x, y:pointerDiff.y} ;
                if (this.uneffectedDiff) {
                    adjustedDiff.x = adjustedDiff.x + this.uneffectedDiff.x;
                    adjustedDiff.y = adjustedDiff.y + this.uneffectedDiff.y;
                }
                var adjustedSnappedDiff =
                {
                    x: Math.round(adjustedDiff.x / 10)* 10,
                    y: Math.round(adjustedDiff.y / 10)* 10
                };
                var adjustedPointer = {x: this.prevSnappedPointer.x + adjustedSnappedDiff.x, y: this.prevSnappedPointer.y + adjustedSnappedDiff.y};
                //console.log("{Prev pointer = ("+ this.prevPointer.x+", "+this.prevPointer.y + "), PrevSnappedPointer = "+ this.prevSnappedPointer.x+", "+this.prevSnappedPointer.y +")");

            }
            //var adjustedSnappedPointer = {
            //    x: Math.round(adjustedPointer.x / 10)* 10,
            //    y: Math.round(adjustedPointer.y / 10)* 10
            //};


            //console.log("Pointer = ("+ pointer.x+", "+pointer.y +  "), SnappedPointer = ("+ adjustedPointer.x+", "+adjustedPointer.y + ")}");
            if (!this.uneffectedDiff) {
                this.uneffectedDiff= {x: 0, y: 0};
            }
            if (pointerDiff && Math.abs(pointerDiff.x) > 0) {
                if (this.prevSnappedPointer && adjustedPointer.x === this.prevSnappedPointer.x) {
                    this.uneffectedDiff.x += pointerDiff.x;
                } else {
                    this.uneffectedDiff.x = 0;
                }
            }
            if (pointerDiff && Math.abs(pointerDiff.y) > 0) {
                if (this.prevSnappedPointer && adjustedPointer.y === this.prevSnappedPointer.y) {
                    this.uneffectedDiff.y += pointerDiff.y;
                } else {
                    this.uneffectedDiff.y = 0;
                }
            }

            //console.log("UneffectedDiff= ("+ this.uneffectedDiff.x+", "+this.uneffectedDiff.y + ")");
            // console.log(" ");

            this.prevPointer = {x:pointer.x, y: pointer.y};
            this.prevSnappedPointer = adjustedPointer;


             /*
            var target = transform.target;
            //console.log(localMouse);
            transform.newScaleX = target.scaleX;
            transform.newScaleY = target.scaleY;

            if (by === 'equally' && !lockScalingX && !lockScalingY) {
                //this._scaleObjectEqually(localMouse, target, transform);
                if ()
                lockScalingY || target.set('height', (adjustedPointer.x/target.width) * target.height);
                lockScalingX || target.set('width', adjustedPointer.x );

            }
            else if (!by) {
                //target.width = localMouse.x + target.width;
                //target.height = localMouse.y + target.height;

                lockScalingX || target.set('width', adjustedPointer.x);
                lockScalingY || target.set('height', adjustedPointer.y);
            }
            else if (by === 'x' && !target.get('lockUniScaling')) {
                //transform.newScaleX = localMouse.x / (target.width + target.strokeWidth);
                lockScalingX || target.set('width', adjustedPointer.x);
            }
            else if (by === 'y' && !target.get('lockUniScaling')) {
                //transform.newScaleY = localMouse.y / (target.height + target.strokeWidth);
                lockScalingY || target.set('height', adjustedPointer.y);
            }

            this._flipObject(transform);
            */
            var target = transform.target;

            transform.newScaleX = target.scaleX;
            transform.newScaleY = target.scaleY;

            if (by === 'equally' && !lockScalingX && !lockScalingY) {
                //this._scaleObjectEqually(localMouse, target, transform);
                transform.newScaleX = adjustedPointer.x / (target.width);
                transform.newScaleY = adjustedPointer.x / (target.width);
                lockScalingX || target.set('scaleX', transform.newScaleX);
                lockScalingY || target.set('scaleY', transform.newScaleY);
            }
            else if (!by) {
                transform.newScaleX = adjustedPointer.x / (target.width);
                transform.newScaleY = adjustedPointer.y / (target.height);

                lockScalingX || target.set('scaleX', transform.newScaleX);
                lockScalingY || target.set('scaleY', transform.newScaleY);
            }
            else if (by === 'x' && !target.get('lockUniScaling')) {
                transform.newScaleX = adjustedPointer.x / (target.width );
                lockScalingX || target.set('scaleX', transform.newScaleX);
            }
            else if (by === 'y' && !target.get('lockUniScaling')) {
                transform.newScaleY = adjustedPointer.y / (target.height);
                lockScalingY || target.set('scaleY', transform.newScaleY);
            }

            this._flipObject(transform);
        }

// customise fabric.Object with a method to resize rather than just scale after tranformation
        fabric.Object.prototype.resizeToScale = function () {
            // resizes an object that has been scaled (e.g. by manipulating the handles), setting scale to 1 and recalculating bounding box where necessary
            switch (this.type) {
                case "circle":
                    this.radius *= this.scaleX;
                    this.scaleX = 1;
                    this.scaleY = 1;
                    break;
                case "ellipse":
                    this.rx *= this.scaleX;
                    this.ry *= this.scaleY;
                    this.width = this.rx * 2;
                    this.height = this.ry * 2;
                    this.scaleX = 1;
                    this.scaleY = 1;
                    break;
                case "polygon":
                case "editable-polygon":
                    var points = this.get('points');
                    for (var i = 0; i < points.length; i++) {
                        var p = points[i];
                        p.x *= this.scaleX
                        p.y *= this.scaleY;
                    }
                    this.scaleX = 1;
                    this.scaleY = 1;
                    this._calcDimensions();
                    this.setCoords();
                    break;
                case "polyline":
                    var points = this.get('points');
                    for (var i = 0; i < points.length; i++) {
                        var p = points[i];
                        p.x *= this.scaleX
                        p.y *= this.scaleY;
                    }
                    this.scaleX = 1;
                    this.scaleY = 1;
                    this.width = this.getWidth();
                    this.height = this.getHeight();
                    break;
                case "triangle":
                case "line":
                case "rect":
                   // console.log(this.scaleX);
                    this.width *= this.scaleX;
                    this.height *= this.scaleY;
                    this.scaleX = 1;
                    this.scaleY = 1;
                default:
                    break;
            }
        }

        var current;
        var list = [];
        var state = [];
        var index = 0;
        var index2 = 0;
        var action = false;
        var refresh = true;

        canvas.on("object:added", function (e) {
            var object = e.target;
            //console.log('object:modified');

            if (action === true) {
                state = [state[index2]];
                list = [list[index2]];

                action = false;
                //console.log(state);
                index = 1;
            }
            object.saveState();

            //console.log(object.originalState);
            state[index] = JSON.stringify(object.originalState);
            list[index] = object;
            index++;
            index2 = index - 1;

            refresh = true;
        });

        canvas.on("object:modified", function (e) {
            var object = e.target;
            //console.log('object:modified');

            if (action === true) {
                state = [state[index2]];
                list = [list[index2]];

                action = false;
                //console.log(state);
                index = 1;
            }

            object.saveState();

            state[index] = JSON.stringify(object.originalState);
            list[index] = object;
            index++;
            index2 = index - 1;

            //console.log(state);
            refresh = true;
        });

        function undo() {

            if (index <= 0) {
                index = 0;
                return;
            }

            if (refresh === true) {
                index--;
                refresh = false;
            }

            //console.log('undo');

            index2 = index - 1;
            current = list[index2];
            current.setOptions(JSON.parse(state[index2]));

            index--;
            current.setCoords();
            canvas.renderAll();
            action = true;
        }

        function redo() {

            action = true;
            if (index >= state.length - 1) {
                return;
            }

            //console.log('redo');

            index2 = index + 1;
            current = list[index2];
            current.setOptions(JSON.parse(state[index2]));

            index++;
            current.setCoords();
            canvas.renderAll();
        }

        $('#undo').click(function () {
            undo();
        });
        $('#redo').click(function () {
            redo();
        });
    });
})(typeof exports !== 'undefined' ? exports : this);
