<body ng-app="layersApp" ng-controller="LayersController">

<%@ include file="../layout/includeHomepageHeader.jsp"%>
<%@ include file="../layout/navigationMenu.jsp"%>
<div class="sub-header">
	<ol class="breadcrumb">
  		<li class="active" ng-repeat="screen in navController.screens" ng-bind="screen.screen"></li>
	</ol>
	<h2 title="Layers" ng-bind="navController.currentScreen()"></h2>
</div>
<section id="content" class="content-sidebar no-border"><span style="display:none" ng-repeat="screen in navController.screens">{{screen.screen}}-></span>
    <div class="row row-padder" style="margin:20px 0px;">
        <div ng-show="navController.currentScreen() == 'Layers'">
        		<button type="button" ng-click="showNewLayerScreen()" class="btn btn-danger">Add</button>
        		<div style="margin-top:20px;">
        			<table id="list4"></table>
        		</div>
        </div>
        <div ng-if="navController.contains('New Layer')"><div ng-include="'addLayerTemplate'"></div></div>
        <div ng-if="navController.contains('View Layer')"><div ng-include="'viewLayerTemplate'"></div></div>
    </div>
</section>

<%@ include file="../layout/includeScripts.jsp"%>

<script>
    $('.carousel').carousel({
        interval:false // remove interval for manual sliding
    });

    // when the carousel slides, load the ajax content
    $('.carousel').on('slid', function (e) {

        // get index of currently active item
        var idx = $('#myCarousel .item.active').index();

        // ajax load from data-url
        $('.item').html($("#grid-box").html());


    });

    // load first slide
    $('[data-slide-number=0]').load($('[data-slide-number=0]').data('url'),function(result){
        $('.carousel').carousel(0);
    });

</script>

<script type="text/ng-template" class="template" id="editAttributeTemplate">
<div ng-controller="EditAttributeController">
	<div ng-show="navController.currentScreen() == 'Edit Attribute'">
		<form role="form" name="editAttributeForm">
				                    <div class="marginbottom10"><span class="badge">1</span> Basic details &nbsp;&nbsp;</div>
				                    <div class="form-group">
				                        <input type="text" class="form-control" placeholder="Name of the attribute" ng-model="attribute.name" name="nameAttribute" required>
										<div class="error"ng-show="showValidationMessages && editAttributeForm.nameAttribute.$error.required">Name is required.</div>
				                    </div>
				                    <br/>
				                    <div class="marginbottom10"><span class="badge">2</span> Options &nbsp;&nbsp;<a href="javascript:void(0)" ng-click="addOption()" >Add more options</a></div>
				                    <div class="form-group input-group" ng-repeat="option in attribute.options  track by $index">
				                        <span class="input-group-addon">{{$index+1}}.</span>
				                        <input type="text" class="form-control" placeholder="" ng-model="attribute.options[$index]">
									</div>
				                    <div class="error" ng-show="showValidationMessages && allOptionsEmpty">At least one option should be filled.</div>
				                </form>
								<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
				                <button type="button" class="btn btn-danger" ng-click="updateAttribute(editAttributeForm)">Update</button>
	</div>
</div>
</script>

<script type="text/ng-template" class="template" id="addAttributeTemplate">
    <div ng-controller="AddAttributeController">
	<div ng-show="navController.currentScreen() == 'Add Attribute'">
        		<div>
        			<tabset>
					    <tab heading="Existing">
					    	<table class="table table-striped">
						        <thead>
						          <tr>
						            <th></th>
						            <th>Name</th>
						            <th>Values</th>
						          </tr>
						        </thead>
						        <tbody>
						          <tr ng-repeat="attribute in attributes">
						            <td><input type="radio" name="selectedAttribute" value="{{attribute.id}}" ng-click="markAttribute(attribute.id)" ng-model="selectedAttributeId" /></td>
						            <td>{{attribute.name}}</td>
						            <td>
										<span ng-repeat="option in attribute.options track by $index" ng-bind="$index == 0 && option || (', '+option)"></span> 
									</td>
						          </tr>
						        </tbody>
						      </table>
						      <div>
						      	<button type="button" ng-click="backk()" class="btn btn-default">Go back</button>
								<button type="button" ng-if="selectedAttributeId == undefined" class="btn btn-default" disabled>Go to Edit</button>
								<button type="button" ng-if="selectedAttributeId && selectedAttributeId != ''" ng-click="gotoedit(attribute)" class="btn btn-default">Go to Edit</button>
						      	<button type="button" ng-if="selectedAttributeId == undefined" class="btn btn-danger" disabled>Select</button>
								<button type="button" ng-if="selectedAttributeId && selectedAttributeId != ''" ng-click="select()" class="btn btn-danger">Select</button>
								<button type="button" ng-if="selectedAttributeId == undefined" class="btn btn-warning pull-right" disabled>Delete</button>
								<button type="button" ng-if="selectedAttributeId && selectedAttributeId != ''" ng-click="deleteAttribute(attribute)" class="btn btn-warning pull-right">Delete</button>
						      </div>
					    </tab>
					    <tab heading="Add new">
					    	<div class="marginall20">
						    	<form role="form" name="addAttributeForm">
				                    <div class="marginbottom10"><span class="badge">1</span> Basic details &nbsp;&nbsp;</div>
				                    <div class="form-group">
				                        <input type="text" class="form-control" placeholder="Name of the attribute" ng-model="attribute.name" name="nameAttribute" required>
										<div class="error"ng-show="showValidationMessages && addAttributeForm.nameAttribute.$error.required">Name is required.</div>
				                    </div>
				                    <br/>
				                    <div class="marginbottom10"><span class="badge">2</span> Options &nbsp;&nbsp;<a href="javascript:void(0)" ng-click="addOption()" >Add more options</a></div>
				                    <div class="form-group input-group" ng-repeat="option in attribute.options  track by $index">
				                        <span class="input-group-addon">{{$index+1}}.</span>
				                        <input type="text" class="form-control" placeholder="" ng-model="attribute.options[$index]">
									</div>
				                    <div class="error" ng-show="showValidationMessages && allOptionsEmpty">At least one option should be filled.</div>
				                </form>
								<button type="button" ng-click="backk()" class="btn btn-default">Go back</button>
				                <button type="button" class="btn btn-danger" ng-click="saveAttribute(addAttributeForm)">Add</button>
				            </div>
					    </tab>
					</tabset>
        		</div>
        	</div>
			<div ng-if="navController.contains('Edit Attribute')"><div ng-include="'editAttributeTemplate'"></div></div>
			</div>
</script>

<script type="text/ng-template" class="template" id="viewLayerTemplate">
	<div ng-controller="ViewLayerController">
		<div ng-show="navController.currentScreen() == 'View Layer'">
			<div class="form-group">
	    		<label for="exampleInputEmail1">Layer Name</label>
	    		<div ng-bind="layer.name" class="form-display-val" ></div>
	  		</div>
			<div class="form-group">
	    			<label for="exampleInputPassword1">Attributes</label>
	    			<table class="table table-striped table-bordered innertable">
		        		<thead>
		          			<tr>
		            			<th>#</th>
		            			<th>Name</th>
		            			<th>Allowed Values</th>
								<th>Mandatory</th>
		          			</tr>
		        		</thead>
		        		<tbody>
		          			<tr ng-repeat="layerAttribute in layer.layerAttributes">
		            			<td>{{$index+1}}</td>
		            			<td>{{layerAttribute.attribute.name}}</td>
		            			<td>
									<span ng-repeat="option in layerAttribute.options track by $index" ng-bind="$index == 0 && option || (', '+option)"></span> 
								</td>
								<td>
									<span ng-bind="layerAttribute.mandatory"></span>
								</td>
		          			</tr>
		        		</tbody>
		      		</table>
	  		</div>
			<div class="form-group">
				<label for="exampleInputPassword1">Layer Charts</label>
				<table class="table table-striped table-bordered innertable">
		        		<thead>
		          			<tr>
		            			<th>Configuration</th>
								<th>View</th>
		            		</tr>
		        		</thead>
		        		<tbody>
		          			<tr ng-repeat="lac in layer.layerAttributeConfigs">
		            			<td>{{lac.valueString}}</td>
								<td><a href="javascript:void(0)" ng-click="viewLayerChart(lac)">View</a></td>
		            		</tr>
		        		</tbody>
		      		</table>
			</div>
			<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
	  		<button type="submit" ng-click="editLayer()" class="btn btn-danger">Edit</button>
		</div>	
		<div ng-if="navController.contains('Edit Layer')"><div ng-include="'editLayerTemplate'"></div></div>
		<div ng-if="navController.contains('View Layer Chart')"><div ng-include="'viewLayerChartTemplate'"></div></div>
	</div>
</script>

<script type="text/ng-template" class="template" id="viewLayerChartTemplate">
	<div ng-controller="ViewLayerChartController">
		<div ng-show="navController.currentScreen() == 'View Layer Chart'"  ng-init="initialize()">
			<form id="viewLayerChartForm" name="viewLayerChartForm" >
				<div class="form-group">
					<label>Configure your view</label>
					<div class="paddingall10 clearfix" style="border:1px solid gray;border-radius:4px;">
						<div class="form-group">
    						<label>From</label>
							<input type="text" name="from" ng-model="from"/>
						</div>
						<div class="form-group">
    						<label>Interval</label>
							<input type="text" name="interval" ng-model="interval"/>
						</div>
						<div class="form-group">
    						<label>No. of blocks</label>
							<input type="text" name="blockCount" ng-model="noOfBlocks"/>
						</div>
						<div class="form-group">
    						<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
							<button type="submit" ng-click="refreshChart()" class="btn btn-danger">View</button>
						</div>
					</div>
				</div>
			<form>
			<div>
				<div class="btn-group" style="margin-bottom:10px;">
					<button type="button" class="btn btn-default" ng-click="prevLayerChart()"><span class="glyphicon glyphicon-backward"></span>&nbsp;Prev</button>
					<button type="button" class="btn btn-default" ng-click="nextLayerChart()">Next&nbsp;<span class="glyphicon glyphicon-forward"></span></button>
				</div>			
			</div>
			<div id="carousel-example-generic" class="carousel slide">
                    <!-- Wrapper for slides -->
                    <div class="carousel-inner">
                        <div class="item layer-chart-div active">
                        	Loading. Please wait..
						</div>
						<div class="item layer-chart-div">
                        	Loading. Please wait..
						</div>
					</div>
			</div>
		</div>
	</div>
</script>

<script type="text/ng-template" class="template" id="addLayerChartTemplate">
	<div ng-controller="AddLayerChartController">
		<div ng-show="navController.currentScreen() == 'Add Layer Chart'">
			<form id="addLayerChartForm" name="addLayerChartForm" >
			<input type="hidden" name="requestString" id="layerChartRequestString" />
			<div class="form-group">
				<label>Layer Configuration</label>
				<div class="paddingall10 clearfix" style="border:1px solid gray;border-radius:4px;">
					<div class="form-group" ng-repeat="layerAttribute in layer.layerAttributes">
    					<label>{{layerAttribute.attribute.name}}</label>
    					<ng-form name="validationForm">
						<select class="form-control" ng-model="layerAttributeConfig.attributeValueMap[layerAttribute.attribute.id]" mandatory="layerAttribute.mandatory" name="layerAttributeSelect">
							<option value="">-Select-</option>
							<option ng-repeat="option in layerAttribute.options track by $index" value="{{option}}">{{option}}</option>
						</select>
						<div class="error" ng-show="showValidationMessages && validationForm.layerAttributeSelect.$error.required">Required.</div>
						</ng-form>
  					</div>		
				</div>		
			</div>
			<div class="form-group">
				<label>Upload file</label>&nbsp;<a href="<%=request.getContextPath()%>/assets/LayerChart.xls" target="_blank">Download sample file</a>
				<input name="layerChartFile" type="file"/>
			</div>
			<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
			<button type="submit" ng-click="submitLayerChart()" class="btn btn-danger">Add layer chart</button>
			</form>
		</div>	
	</div>
</script>

<script type="text/ng-template" class="template" id="editLayerTemplate">
	<div ng-controller="EditLayerController">
		<div ng-show="navController.currentScreen() == 'Edit Layer'">
			<form role="form" name="editLayerForm">
			<div class="form-group">
	    		<label for="exampleInputEmail1">Layer Name</label>
	    		<input type="text" class="form-control" id="exampleInputEmail1" placeholder="Enter layer name" ng-model="layer.name" name="layerName" autocomplete="off">
				<div class="error"ng-show="showValidationMessages && editLayerForm.layerName.$error.required">Name is required.</div>
	  		</div>
			<div class="form-group">
	    			<label>Attributes</label>&nbsp;&nbsp;&nbsp;<a href="javascript:void(0)" ng-click="showAddAttributeScreen()">Add/Edit an attribute</a>
					<div class="error"ng-show="showValidationMessages && attributesInvalid">All attributes should have at least one value allowed.</div>
	    			<table class="table table-striped table-bordered innertable">
		        		<thead>
		          			<tr>
		            			<th></th>
		            			<th>Name</th>
		            			<th>Allowed Values</th>
								<th>Mandatory</th>
								<th></th>
		          			</tr>
		        		</thead>
		        		<tbody>
		          			<tr ng-repeat="layerAttribute in layer.layerAttributes">
		            			<td>{{$index+1}}</td>
		            			<td>{{layerAttribute.attribute.name}}</td>
		            			<td>
		            					<span style="display:none" ng-bind="checkAttributes(layer)"></span>
										<div class="btn-group">
  										<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" style="min-width:200px;">
    										{{layerAttribute.allowedValues}} <span class="caret"></span>
  										</button>
  										<ul class="dropdown-menu" role="menu">
    										<li style="font-size:11px;padding-left:10px;"><input data-allAttr="All" abc data-attrId="{{layerAttribute.attribute.id}}" ng-click="allOptionsClicked(layerAttribute)" style="margin-right:10px;" type="checkbox" />All</li>
											<li class="divider"></li>
    										<li style="font-size:11px;padding-left:10px;" ng-repeat="option in layerAttribute.attribute.options track by $index"><input ng-click="attributeValueClicked(layerAttribute)" data-attrId="{{layerAttribute.attribute.id}}" data-attr="{{option}}" style="margin-right:10px;" type="checkbox"  value="{{option}}"/>{{option}}</li>
    									</ul>
									</div>
		            			</td>
								<td>
									<input type="radio" ng-model="layerAttribute.mandatory" ng-value="{'true':true}.true" name="mandatory_{{layerAttribute.attribute.id}}">&nbsp;Yes&nbsp;&nbsp;&nbsp;<input  name="mandatory_{{layerAttribute.attribute.id}}" type="radio" ng-value="{'false':false}.false" ng-model="layerAttribute.mandatory" >&nbsp;No
								</td>
								<td><a href="javascript:void(0)" ng-click="removeAttribute(layerAttribute)">Remove</a></td>
		          			</tr>
		        		</tbody>
		      		</table>
	  		</div>
			<div class="form-group">
				<label for="exampleInputPassword1">Layer Charts</label>
				<table class="table table-striped table-bordered innertable">
		        		<thead>
		          			<tr>
		            			<th>Configuration</th>
								<th>View</th>
		            		</tr>
		        		</thead>
		        		<tbody>
		          			<tr ng-repeat="lac in layer.layerAttributeConfigs">
		            			<td>{{lac.valueString}}</td>
								<td><a href="javascript:void(0)" ng-click="viewLayerChart(lac)">View</a>&nbsp;&nbsp;<a href="javascript:void(0)" ng-click="deleteLayerChart(lac)">Delete</a></td>
		            		</tr>
		        		</tbody>
		      		</table>
			</div>
			<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
	  		<button type="button" ng-click="updateLayer()" class="btn btn-danger">Update</button>
			<button type="button" ng-click="addLayerChart()" class="btn btn-danger">Add a layer chart configuration</button>
			<button type="button" class="btn btn-warning pull-right" ng-click="deactivateLayer()">Delete</button>
		</form>
		</div>	
		<div ng-if="navController.contains('Add Attribute')"><div ng-include="'addAttributeTemplate'"></div></div>
		<div ng-if="navController.contains('Add Layer Chart')"><div ng-include="'addLayerChartTemplate'"></div></div>
	</div>
</script>

<script type="text/ng-template" class="template" id="addLayerTemplate">
<div ng-controller="AddLayerController">
<div ng-show="navController.currentScreen() == 'New Layer'">
<div>
	<form role="form" name="addLayerForm">
	  <div class="form-group">
	    <label for="exampleInputEmail1">Layer Name</label>
	    <input type="text" class="form-control" id="exampleInputEmail1" placeholder="Enter layer name" ng-model="layer.name" name="layerName" autocomplete="off" required>
		<div class="error"ng-show="showValidationMessages && addLayerForm.layerName.$error.required">Name is required.</div>
	  </div>
	  <div class="form-group">
	    <label for="exampleInputPassword1">Attributes</label>&nbsp;&nbsp;&nbsp;<a href="javascript:void(0)" ng-click="showAddAttributeScreen()">Add a attribute</a>
		<div class="error"ng-show="showValidationMessages && attributesInvalid">All attributes should have at least one value allowed.</div>
	    <table class="table table-striped table-bordered innertable">
		        <thead>
		          <tr>
		            <th></th>
					<th>Name</th>
					<th>Allowed Values</th>
					<th>Mandatory</th>
					<th></th>
		          </tr>
		        </thead>
		        <tbody>
		          <tr ng-repeat="layerAttribute in layer.layerAttributes">
		            <td>{{$index+1}}</td>
					<td>{{layerAttribute.attribute.name}}</td>
					<td>
						<span style="display:none" ng-bind="checkAttributes(layer)"></span>
						<div class="btn-group">
							<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" style="min-width:200px;">
								{{layerAttribute.allowedValues}} <span class="caret"></span>
							</button>
							<ul class="dropdown-menu" role="menu">
								<li style="font-size:11px;padding-left:10px;"><input data-allAttr="All" abc data-attrId="{{layerAttribute.attribute.id}}" ng-click="allOptionsClicked(layerAttribute)" style="margin-right:10px;" type="checkbox" />All</li>
								<li class="divider"></li>
								<li style="font-size:11px;padding-left:10px;" ng-repeat="option in layerAttribute.attribute.options track by $index"><input ng-click="attributeValueClicked(layerAttribute)" data-attrId="{{layerAttribute.attribute.id}}" data-attr="{{option}}" style="margin-right:10px;" type="checkbox"  value="{{option}}"/>{{option}}</li>
							</ul>
						</div>
					</td>
					<td>
						<input type="radio" ng-model="layerAttribute.mandatory" ng-value="{'true':true}.true" name="mandatory_{{layerAttribute.attribute.id}}">&nbsp;Yes&nbsp;&nbsp;&nbsp;<input  name="mandatory_{{layerAttribute.attribute.id}}" type="radio" ng-value="{'false':false}.false" ng-model="layerAttribute.mandatory" >&nbsp;No
					</td>
					<td><a href="javascript:void(0)" ng-click="removeAttribute(layerAttribute)">Remove</a></td>
		          </tr>
		        </tbody>
		      </table>
	  </div>
	  
	  <button type="button" ng-click="back()" class="btn btn-default">Go back</button>
	  <button type="submit" ng-click="addLayer()" class="btn btn-danger">Submit</button>
		
	</form>
</div>
</div>
<div ng-if="navController.contains('Add Attribute')"><div ng-include="'addAttributeTemplate'"></div></div>
</div>
</script>

<script type="text/ng-template" class="template" id="DeactivateLayerTemplate">
<!-- Modal -->
<div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" ng-click="close()">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Delete layer</h4>
      </div>
      <div class="modal-body">
        Are you sure you want to delete layer <strong><span ng-bind="layer.name"></span></strong>?
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal" ng-click="close()" >Close</button>
        <button type="button" class="btn btn-primary" ng-click="deactivateLayer()">Yes</button>
      </div>
    </div>
</script>

<div class="modal fade" id="layerChartModal">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title">Processing</h4>
      </div>
      <div class="modal-body">
        <div style="margin-bottom:20px;">This may take a few minutes to process. Please wait...</div>
        <div class="progress progress-striped active">
  			<div class="progress-bar"  role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%">
    		</div>
		</div>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

</body>


<script type="text/ng-template" class="template" id="DeleteAttributeTemplate">
<!-- Modal -->
<div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" ng-click="close()">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Delete Attribute</h4>
      </div>
      <div class="modal-body">
        By deleting this attribute, it will get removed from all associated layers and records. Are you sure you want to delete attribute <strong><span ng-bind="attribute.name"></span></strong>? 
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal" ng-click="close()" >Close</button>
        <button type="button" class="btn btn-primary" ng-click="deleteAttribute()">Yes</button>
      </div>
    </div>

</script>

<script type="text/ng-template" class="template" id="RemoveLayerAttributeTemplate">
<!-- Modal -->
<div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" ng-click="close()">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Remove Attribute from Layer</h4>
      </div>
      <div class="modal-body">
        <span ng-if="layerAttribute.id && layerAttribute.id != 0">By removing this attribute from layer, it will get removed from all records created for this layer. </span>Are you sure you want to remove this attribute <strong><span ng-bind="layerAttribute.attribute.name"></span></strong>? 
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal" ng-click="close()" >Close</button>
        <button type="button" class="btn btn-primary" ng-click="removeLayerAttribute()">Yes</button>
      </div>
    </div>

</script>

<script type="text/ng-template" class="template" id="DeleteLayerChartTemplate">
<!-- Modal -->
<div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" ng-click="close()">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Delete Layer Chart</h4>
      </div>
      <div class="modal-body">
        Are you sure you want to delete the selected layer chart? 
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal" ng-click="close()" >Close</button>
        <button type="button" class="btn btn-primary" ng-click="deleteLayerChart()">Yes</button>
      </div>
    </div>

</script>
