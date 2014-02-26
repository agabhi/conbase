<body>

<%@ include file="../layout/includeHomepageHeader.jsp"%>


<!-- nav -->
<nav id="nav" class="nav-primary visible-lg nav-vertical nav-icon">
    <ul class="nav" data-spy="affix" data-offset-top="50">
        <li>
            <a href="dashboard.html"><img src="<%=request.getContextPath()%>/assets/icons/dashboard.png" alt="Dashboard"><span>Dashboard</span></a>
        </li>
        <li class="active dropdown-submenu">
            <a href="#"><img src="<%=request.getContextPath()%>/assets/icons/shop.png" alt="Dashboard"><span>Shop</span></a>
            <ul class="dropdown-menu">
                <li><a href="shop-regular.html">Regular Shop</a></li>
                <li><a href="shop-manager.html">Manager Shop</a></li>
            </ul>
        </li>
        <li>
            <a href="wallet.html"><img src="<%=request.getContextPath()%>/assets/icons/wallet.png" alt="Dashboard"><span>Wallet</span></a>
        </li>
        <li>
            <a href="calendar.html"><img src="<%=request.getContextPath()%>/assets/icons/calendar.png" alt="Dashboard"><span>Calendar</span></a>
        </li>
        <li>
            <a href="network.html"><img src="<%=request.getContextPath()%>/assets/icons/network.png" alt="Dashboard"><span>Network</span></a>
        </li>
        <li>
            <a href="support.html"><img src="<%=request.getContextPath()%>/assets/icons/support.png" alt="Dashboard"><span>Support</span></a>
        </li>
        <li>
            <a href="upgrade.html"><img src="<%=request.getContextPath()%>/assets/icons/upgrade.png" alt="Dashboard"><span>Upgrade</span></a>
        </li>
    </ul>
</nav>
<section id="content" class="content-sidebar no-border" style="position: relative">
    <div class="record-design-page row-padder">
        <div class="row">
            <div class="col-md-9 no-padder">
                <div class="report-section" style="position: relative;">
                    <div style="padding: 5px; background-color:rgba(0, 0, 0, 0.51);position: absolute;width: 100%;z-index: 2">
                        <button type="button" class="btn btn-primary btn-sm">Group</button>
                        <button type="button" class="btn btn-primary btn-sm">Delete</button>
                        <button type="button" class="btn btn-primary btn-sm">Save</button>
                        <button type="button" class="btn btn-primary btn-sm">Undo</button>
                        <button type="button" class="btn btn-primary btn-sm">Redo</button>
                        <button type="button" class="btn btn-primary btn-sm">View Item</button>
                        <button type="button" class="btn btn-danger btn-sm" style="float:right">Assign Item</button>
                        <span style="color:white;font-weight: bold;font-size: 16px;margin-left: 40px;line-height: 30px;vertical-align: middle">Bridge 23+500</span>
                    </div>
                    <div class="blank-page rfi-report" style="position: relative" >
                         <canvas id="rc-canvas"></canvas>
                    </div>
                </div>
            </div>
            <div class="col-md-3" style="position: relative;">
                <div class="component-selection" style="position: relative;">
                    <div class="alert alert-info text-center">
                        <strong>Standard Shapes</strong>
                    </div>
                    <a href="#" id="undo">Undo</a>
                    <a href="#" id="redo">Redo</a>
                    <ul class="report-component clearfix">
                        <li class="report-field-component" data-shape-type="rect">
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(<%=request.getContextPath()%>/assets/img/box-rect.png)">
                             &nbsp;
                            </div>
                            <strong>Rectangle</strong>
                        </li>
                        <li class="report-field-component" data-shape-type="sq" >
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(<%=request.getContextPath()%>/assets/img/box-sq.png)">
                                &nbsp;
                            </div>
                            <strong>Square</strong>
                        </li>
                        <li class="report-field-component" data-shape-type="circle" >
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(<%=request.getContextPath()%>/assets/img/circle.png)">
                                &nbsp;
                            </div>
                            <strong>Circle</strong>
                        </li>
                        <li class="report-field-component" data-shape-type="wedge" >
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(<%=request.getContextPath()%>/assets/img/wedge.png)">
                                &nbsp;
                            </div>
                            <strong>Wedge</strong>
                        </li>
                        <li class="report-field-component" data-shape-type="polygon" >
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(<%=request.getContextPath()%>/assets/img/polygon.png)">
                                &nbsp;
                            </div>
                            <strong>Polygon</strong>
                        </li>
                    </ul>
                    <hr style="border-width: 3px"/>
                    <div class="alert alert-info text-center">
                        <strong>Custom Shapes</strong>
                    </div>
                    <ul class="report-component clearfix">
                        <li class="report-field-component">
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(<%=request.getContextPath()%>/assets/img/box-border.png)">
                                &nbsp;
                            </div>
                            <strong>S.No.</strong>
                        </li>
                        <li class="report-field-component">
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(<%=request.getContextPath()%>/assets/img/box-border.png)">
                                &nbsp;
                            </div>
                            <strong>S.No.</strong>
                        </li>
                        <li class="report-field-component">
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(<%=request.getContextPath()%>/assets/img/box-border.png)">
                                &nbsp;
                            </div>
                            <strong>S.No.</strong>
                        </li>
                        <li class="report-field-component">
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(<%=request.getContextPath()%>/assets/img/box-border.png)">
                                &nbsp;
                            </div>
                            <strong>S.No.</strong>
                        </li>
                    </ul>



                </div>

                <div id="slidecontent" style="display:none;position: absolute;top: 0px;right: 0px;width: 100%;height:100%;background-color: white;padding:10px;">
                    <div>
                        <form role="form">
                            <div class="form-group">
                                <label for="exampleInputEmail1">Item</label>
                                <input type="email" class="form-control" id="exampleInputEmail1" placeholder="Type first 3 letters">
                            </div>
                            <div>
                                <a href="#">Advanced Search</a>
                            </div>
                            <button type="submit" class="btn btn-default">Submit</button>
                        </form>
                    </div>
                </div>


            </div>
        </div>


    </div>
</section>
<footer id="footer">
    <div class="text-center padder clearfix">
        <p>
            <small>&copy; Copyright RoadsCRM 2013 | All Rights Reserved</small>
            <br><br>
            <a href="#" class="btn btn-mini btn-circle btn-twitter"><i class="icon-twitter"></i></a>
            <a href="#" class="btn btn-mini btn-circle btn-facebook"><i class="icon-facebook"></i></a>
            <a href="#" class="btn btn-mini btn-circle btn-linkedin"><i class="icon-linkedin"></i></a>
        </p>
    </div>
</footer>


<script src="js/jquery-1.9.1.min.js"></script>
<script src="js/bootstrap.min.js"></script>

<script src="js/jquery.handsontable.full.js"></script>
<script src="js/main.js"></script>
<script type="text/javascript" src="js/bootstrap-filestyle.min.js"></script>
<script src="js/jqgrid/grid.locale-en.js"></script>
<script src="js/jqgrid/jquery.jqGrid.min.js"></script>
<script src="js/jquery-ui-1.10.3.custom.min.js"></script>


<script type="text/javascript" src="plugins/tinymce/tinymce.min.js"></script>
<script src="http://d3lp1msu2r81bx.cloudfront.net/kjs/js/lib/kinetic-v4.7.4.min.js"></script>
<script src="http://cdnjs.cloudflare.com/ajax/libs/fabric.js/1.4.0/fabric.min.js"></script>
<script src="js/buildGrid.js"></script>
<script src="js/editablePolygon.js"></script>
<script defer="defer">
    var stage;
    $( ".rfi-report .report-widget" ).draggable({ containment: "parent", snap: true, snapTolerance: 5 }).resizable({
        containment: "parent"
    });



    $(".rfi-report").droppable({
        accept: "ul.report-component > li",
        activeClass: "ui-state-highlight",
        drop: function( event, ui ) {
            var draggable = ui.draggable;
            console.log(draggable);
            var shapeType = draggable.attr("data-shape-type");
            console.log(shapeType);
            var pos = ui.offset;
            var reportOffset  = $(".rfi-report").offset();
            var left = (pos.left - reportOffset.left);
            var top = (pos.top - reportOffset.top);
            var shape;
            if (shapeType === "rect") {
               shape = new fabric.Rect({
                    left: left,
                    top: top,
                    fill: '#e8e8e8',
                    width: 40,
                    height: 20,
                   stroke:"#000",
                   strokeWidth: 2
                });
            } else if (shapeType === "sq") {
                shape = new fabric.Rect({
                    left: left,
                    top: top,
                    fill: '#e8e8e8',
                    stroke:"#000",
                    strokeWidth: 2,
                    width: 20,
                    height: 20
                });
            } else if (shapeType === "circle") {
                shape = new fabric.Circle({
                    radius: 20,
                    fill: '#e8e8e8',
                    stroke:"#000",
                    strokeWidth: 2,
                    left: left, top: top
                });
            } else if (shapeType === "wedge") {
                shape = new fabric.Polygon([
                    {x: 0, y: 0},
                    {x: 0, y: 40},
                    {x: 40, y: 40}
                ], {
                    left: left,
                    top: top,
                    fill: '#e8e8e8',
                    stroke:"#000",
                    strokeWidth: 2,
                    selectable: true
                });
            } else if (shapeType === "polygon") {
                shape = new fabric.EditablePolygon([
                    {x: 20, y: 0},
                    {x: 0, y: 60},
                    {x: 80, y: 60},
                    {x: 60, y: 0}
                ], {
                    left: left,
                    top: top,
                    fill: '#e8e8e8',
                    stroke:"#000",
                    strokeWidth: 2,
                    selectable: true
                });
            }
            canvas.add(shape);
        }
    });

    $( "ul.report-component > li").draggable({
        cancel: "a.ui-icon", // clicking an icon won't initiate dragging
        revert: "invalid", // when not dropped, the item will revert back to its initial position
        containment: "document",
        helper: "clone",
        cursor: "move"
    });

    var canvas = new fabric.Canvas('rc-canvas');
    canvas.setHeight($(".blank-page").height());
    canvas.setWidth($(".blank-page").width());
    //canvas.renderAll();
    canvas.findTarget = (function(originalFn) {
        return function() {
            var target = originalFn.apply(this, arguments);
            if (target) {
                if (this._hoveredTarget !== target) {
                    canvas.fire('object:over', { target: target });
                    if (this._hoveredTarget) {
                        canvas.fire('object:out', { target: this._hoveredTarget });
                    }
                    this._hoveredTarget = target;
                }
            }
            else if (this._hoveredTarget) {
                canvas.fire('object:out', { target: this._hoveredTarget });
                this._hoveredTarget = null;
            }
            return target;
        };
    })(canvas.findTarget);

    canvas.__onMouseDown = (function(originalFn) {
        return function() {
            originalFn.apply(this, arguments);
            this.prevPointer = undefined;
            this.prevSnappedPointer = undefined;
            this.uneffectedDiff = undefined;
        };
    })(canvas.__onMouseDown);
    //draw_grid(5);
    buildGrids(10, "#EEEEEE", 50, canvas);

    canvas.on("object:selected", function (e) {
        // Set the effect type
        var effect = 'slide';

        // Set the options for the effect type chosen
        var options = { direction: 'right' };

        // Set the duration (default: 400 milliseconds)
        var duration = 700;

        //$('#slidecontent').toggle(effect, options, duration);
        //$("#slidecontent").slideToggle({queue: false, duration: 500});
    });

    $( ".blank-page" ).resizable({
        stop: function( event, ui ) {
            console.log(ui.size.width);
            canvas.setHeight(ui.size.height);
            canvas.setWidth(ui.size.width);
            buildGrids(10, "#EEEEEE", 50, canvas);
            canvas.renderAll();
        }
    });
</script>
</body>