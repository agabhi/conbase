<body ng-app="recordTypesApp" ng-controller="RecordTypesController" id="recordTypesController">

<%@ include file="../layout/includeHomepageHeader.jsp"%>
<%@ include file="../layout/navigationMenu.jsp"%>
<div class="sub-header">
	<ol class="breadcrumb">
  		<li class="active" ng-repeat="screen in navController.screens" ng-bind="screen.screen"></li>
	</ol>
	<h2 title="Records" ng-bind="navController.currentScreen()"></h2>
</div>
<section id="content" class="content-sidebar no-border"><span style="display:none" ng-repeat="screen in navController.screens">{{screen.screen}}-></span>
    <div class="row row-padder" style="margin:20px 0px;">
        <div ng-show="navController.currentScreen() == 'Record Types'">
        		<button type="button" ng-click="showNewRecordTypeScreen()" class="btn btn-danger">Add a new record type</button>
        		<div style="margin-top:20px;">
        			<table id="recordTypesList"></table>
        		</div>
        </div>
        <div ng-if="navController.contains('Barcharts')"><div ng-include="'BarchartsTemplate'"></div></div>
        <div ng-if="navController.contains('View Records')"><div ng-include="'viewRecordsTemplate'"></div></div>
        <div ng-if="navController.contains('Add Record')"><div ng-include="'addRecordTemplate'"></div></div>
        <div ng-if="navController.contains('New Record Type')"><div ng-include="'addRecordTypeTemplate'"></div></div>
        <div ng-if="navController.contains('View Record Type')"><div ng-include="'viewRecordTypeTemplate'"></div></div>
    </div>
</section>

<%@ include file="../layout/includeScripts.jsp"%>

<script>
	$( ".spinner" ).spinner();
</script>
<script type="text/ng-template" class="template" id="viewRecordTypeTemplate">
<div ng-controller="ViewRecordTypeController">
	<div ng-show="navController.currentScreen() == 'View Record Type'">
		<div>
		<form role="form">
			<div class="form-group">
				<label>Record Type Name</label>
				<div ng-bind="recordType.name" class="form-display-val" ></div>
			</div>
			<div class="form-group">
				<label>Serial No.</label>
				<div ng-bind="recordType.serial.name" class="form-display-val" ></div>
			</div>
			<div class="form-group">
				<label>Type</label>
				<div ng-bind="recordType.type == 1 && 'Layer' || (recordType.type == 2 && 'Structure' || 'Other')" class="form-display-val" ></div>
			</div>
			<div class="form-group" ng-if="recordType.type == 1">
				<label>Layer Attributes</label>
				<table class="table table-striped">
					<thead>
						<tr>
							<th>Name</th>
							<th>Type</th>
							<th>Validations</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="recordTypeAttribute in recordType.layerAttributes">
							<td>{{recordTypeAttribute.name}}</td>
							<td>{{recordTypeAttribute.type == 6 && 'From (length)' || (recordTypeAttribute.type == 7 && 'To (length)' || 'Level') }}</td>
							<td><span ng-repeat="(key, value) in recordTypeAttribute.validations" ng-bind="$index == 0 && (key+ ' = '+value) || (', '+key+ ' = '+value)"></span> </td>
						</tr>
					</tbody>
		      </table>
			</div>
			<div class="form-group">
				<label>Custom Attributes</label>
				<table class="table table-striped">
					<thead>
						<tr>
							<th></th>
							<th>Name</th>
							<th>Type</th>
							<th>Options</th>
							<th>Validations</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="recordTypeAttribute in recordType.customAttributes">
							<td>{{$index+1}}</td>
							<td>{{recordTypeAttribute.name}}</td>
							<td ng-bind="(recordTypeAttribute.type == 1 && 'Option' || (recordTypeAttribute.type == 2 && 'String' || (recordTypeAttribute.type == 3 && 'Text' || (recordTypeAttribute.type == 4 && 'Integer' || (recordTypeAttribute.type == 5 && 'Decimal' || '')))))"></td>
							<td>
								<span ng-repeat="option in recordTypeAttribute.options track by $index" ng-bind="$index == 0 && option || (', '+option)"></span> 
							</td>
							<td>
								<span ng-repeat="(key, value) in recordTypeAttribute.validations" ng-bind="$index == 0 && (key+ ' = '+value) || (', '+key+ ' = '+value)"></span> 
							</td>
						</tr>
					</tbody>
				</table>
			</div>
	  		<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
			<button type="submit" ng-click="editRecordType()" class="btn btn-danger">Edit</button>
		</form>
		</div>
	</div>
	<div ng-if="navController.contains('Edit Record Type')"><div ng-include="'editRecordTypeTemplate'"></div></div>
</div>
</script>

<script type="text/ng-template" class="template" id="BarchartsTemplate">
<div ng-controller="BarchartController">
	<div ng-show="navController.currentScreen() == 'Barcharts'">
		<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
		<button type="submit" ng-click="createBarchart()" class="btn btn-danger">Create New</button>
		<div style="margin-top:20px;">
			<table id="barchartList"></table>
		</div>
	</div>
	<div ng-if="navController.contains('Create Barchart')"><div ng-include="'CreateBarchartTemplate'"></div></div>
	<div ng-if="navController.contains('Execute Barchart')"><div ng-include="'executeBarchartTemplate'"></div></div>
	<div ng-if="navController.contains('View Barchart')"><div ng-include="'viewBarchartTemplate'"></div></div>
</div>
</script>

<script type="text/ng-template" class="template" id="CreateBarchartTemplate">
<div ng-controller="CreateBarchartController">
	<div ng-show="navController.currentScreen() == 'Create Barchart'">
		<h3 style="margin-top:0px;margin-bottom:30px;"><span class="label label-default" ng-bind="recordType.name"></span></h3>
        			<div class="form-group">
						<label>Give your barchar a name</label>
		    			<input type="text" class="form-control" placeholder="Enter barchart name" ng-model="barchart.name" required>
					</div>
					<div class="form-group">
						<label>Attribute for which barchart should be filled</label>
		    			<select class="form-control" ng-model="barchart.recordTypeAttribute.id" required>
							<option value="">--Select--</option>
							<option ng-if="recordTypeAttribute.type == 1" ng-repeat="recordTypeAttribute in recordType.customAttributes" value="{{recordTypeAttribute.id}}">{{recordTypeAttribute.name}}</option>
						</select>
					</div>
				<div class="form-group">
				<label>Layers</label>&nbsp;&nbsp;<a href="javascript:void(0)" ng-click="addLayer()">Add a layer</a>
				<table class="table table-striped">
					<thead>
						<tr>
							<th>Name</th>
							<th>Attributes/Values</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="layerAttributeConfig in barchart.layerAttributeConfigs">
							<td>{{layerAttributeConfig.layer.name}}</td>
							<td><span ng-repeat="(attributeId, value) in layerAttributeConfig.attributeValueMap" ng-bind="$index == 0 && (layerAttributeConfig.layer.layerAttributeMap[attributeId].name + ' = ' + value) || (', '+layerAttributeConfig.layer.layerAttributeMap[attributeId].name + ' = ' + value) "></span> </td>
						</tr>
					</tbody>
		      </table>
			</div>
			<div class="form-group">
				<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
				<button type="button" ng-click="createBarchart()" class="btn btn-danger">Create</button>
			</div>
	</div>
	<div ng-if="navController.contains('Select Layer')"><div ng-include="'SelectLayerTemplate'"></div></div>
</div>
</script>

<script type="text/ng-template" class="template" id="viewBarchartTemplate">
<div ng-controller="ViewBarchartController">
	<div ng-show="navController.currentScreen() == 'View Barchart'">
		<h3 style="margin-top:0px;margin-bottom:30px;"><span class="label label-default" ng-bind="recordType.name"></span></h3>
        			<div class="form-group">
						<label>Name</label>
						<div ng-bind="barchart.name" class="form-display-val" ></div>
		    		</div>
					<div class="form-group">
						<label>Attribute for which barchart should be filled</label>
		    			<div ng-bind="barchart.recordTypeAttribute.name" class="form-display-val" ></div>
					</div>
				<div class="form-group">
				<label>Layers</label>
				<table class="table table-striped">
					<thead>
						<tr>
							<th>Name</th>
							<th>Attributes/Values</th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="layerAttributeConfig in barchart.layerAttributeConfigs">
							<td ng-bind="layersMap[layerAttributeConfig.layer.id].name"></td>
							<td><span ng-repeat="(attributeId, value) in layerAttributeConfig.attributeValueMap" ng-bind="$index == 0 && (value) || (', ' + value) "></span> </td>
						</tr>
					</tbody>
		      </table>
			</div>
			<div class="form-group">
				<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
			</div>
	</div>
	<div ng-if="navController.contains('Select Layer')"><div ng-include="'SelectLayerTemplate'"></div></div>
</div>
</script>

<script type="text/ng-template" class="template" id="SelectLayerTemplate">
<div ng-controller="SelectLayerController">
	<div ng-show="navController.currentScreen() == 'Select Layer'">
				<form name="selectLayerForm" >
				<div class="form-group">
					<label>Layer</label>
					<input type="text" ng-model="asyncSelected" placeholder="Start typing..." typeahead-on-select="selectLayer($item, $model, $label)" typeahead="layer as layer.name for layer in getLayers($viewValue) | filter:$viewValue" typeahead-loading="loadingLayers" class="form-control">
    				<i ng-show="loadingLayers" class="glyphicon glyphicon-refresh"></i>
					<button type="button" class="btn btn-link">Advanced Search</button>
					<span class="layer-name"></span>
				</div>
        		<div class="layer-properties">
        			<div class="form-group" ng-repeat="layerAttribute in layer.layerAttributes">
						<label>{{layerAttribute.attribute.name}}</label>
		    			<ng-form name="layerAttributeValidationForm">
						<select class="form-control" ng-model="layer.layerAttributeConfig.attributeValueMap[layerAttribute.attribute.id]" mandatory="layerAttribute.mandatory" name="layerAttributeSelect">
							<option value="">--Select--</option>
							<option ng-repeat="option in layerAttribute.options" value="{{option}}">{{option}}</option>
						</select>
						<div class="error" ng-show="showValidationMessages && layerAttributeValidationForm.layerAttributeSelect.$error.required">Required.</div>
						</ng-form>
					</div>
        		</div>
			<div class="form-group">
				<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
				<button type="button" ng-click="select()" class="btn btn-default">Select</button>
			</div>
			</div>
	</div>
</div>
</script>

<script type="text/ng-template" class="template" id="CreateRecordLayoutTemplate">
<div ng-controller="CreateRecordLayoutController" ng-init="initialize()" >
	<div ng-show="navController.currentScreen() == 'Create Record Layout'">
		<div class="record-design-page row-padder">
        <div class="row">
            <div class="col-md-9 no-padder">
                <div class="report-section">
                    <div class="blank-page rfi-report">
                        <div class="report-widget draggable ui-widget-content">&nbsp</div>
                        <div class="report-widget draggable ui-widget-content">&nbsp</div>
                        <div class="report-widget draggable ui-widget-content">&nbsp</div>
                        <div class="report-widget draggable ui-widget-content">&nbsp</div>
                        <div class="report-widget draggable ui-widget-content">&nbsp
                            <div class="widget-hover widget-hover-top widget-hover-right">
                                x
                            </div>
                            <div class="widget-hover widget-hover-top widget-hover-left">
                                x
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3" style="position: relative;">
                <div class="component-selection" style="position: relative;">
                    <div class="alert alert-info text-center">
                        <strong>Record Fields</strong>
                    </div>
                    <ul class="report-component clearfix">
                        <li class="report-field-component" ng-repeat="recordTypeAttribute in recordType.layerAttributes">
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(assets/img/box-border.png)">
                                &nbsp;
                            </div>
                            <strong ng-bind="recordTypeAttribute.name"></strong>
                        </li>
						<li class="report-field-component" ng-repeat="recordTypeAttribute in recordType.customAttributes">
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(assets/img/box-border.png)">
                                &nbsp;
                            </div>
                            <strong ng-bind="recordTypeAttribute.name"></strong>
                        </li>
                    </ul>
                    <hr style="border-width: 3px"/>
                    <div class="alert alert-info text-center">
                        <strong>Standard Components</strong>
                    </div>
                    <ul class="report-component clearfix">
                        <li class="report-field-component">
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(assets/img/box-border.png)">
                                &nbsp;
                            </div>
                            <strong>Bordered Box</strong>
                        </li>
                        <li class="report-field-component">
                            <div class="component-image component-image-border-box" style="width:74px;height:74px;background:url(assets/img/box-border.png)">
                                &nbsp;
                            </div>
                            <strong>Box w/o border</strong>
                        </li>
                    </ul>



                </div>

                <div id="slidecontent" style="display:none;position: absolute;top: 0px;right: 0px;width: 100%;">
                    <div>
                        <textarea id="widget-text-editor"></textarea>
                    </div>
                </div>


            </div>
        </div>


    </div>
	</div>
</div>
</script>

<script type="text/ng-template" class="template" id="editRecordTypeTemplate">
<div ng-controller="EditRecordTypeController">
	<div ng-show="navController.currentScreen() == 'Edit Record Type'">
	<div>
	<form role="form">
	  <div class="form-group">
	    <label>Record Type  Name</label>
	    <input type="text" class="form-control" placeholder="Enter record type name" ng-model="recordType.name">
	  </div>
		<div class="form-group">
			<label>Serial No.</label>&nbsp;&nbsp;<a href="javascript:void(0)" ng-click="createNewSerial()">Create New</a>
			<select class="form-control" ng-model="recordType.serial.id" ng-options="serial.id as serial.name for serial in serials">
				<option value="">--Select--</option>
			</select>
		</div>
		<div class="form-group">
			<label>Type</label>
			<input type="radio" value="1" ng-model="recordType.type" ng-change="changeType()" disabled>Layer&nbsp;&nbsp;
			<input type="radio" value="2" ng-model="recordType.type" ng-change="changeType()" disabled>Structure&nbsp;&nbsp;
			<input type="radio" value="3" ng-model="recordType.type" ng-change="changeType()" disabled>Other
		</div>
		<div class="form-group" ng-if="recordType.type == 1">
	    <label>Layer Attributes</label>
	    <table class="table table-striped">
		        <thead>
		          <tr>
		            <th>Name</th>
					<th>Type</th>
		            <th>Validations</th>
					<th></th>
		          </tr>
		        </thead>
		        <tbody>
		          <tr ng-repeat="recordTypeAttribute in recordType.layerAttributes">
		            <td ng-bind="recordTypeAttribute.name"></td>
					<td>{{recordTypeAttribute.type == 6 && 'From (length)' || (recordTypeAttribute.type == 7 && 'To (length)' || 'Level') }}</td>
		            <td><span ng-repeat="(key, value) in recordTypeAttribute.validations" ng-bind="$index == 0 && (key+ ' = '+value) || (', '+key+ ' = '+value)"></span> </td>
					<td><a href="javascript:void(0)" ng-click="editRecordTypeAttribute(recordTypeAttribute)">Edit</a></td>
		          </tr>
		        </tbody>
		      </table>
	  </div>
	  <div class="form-group">
	    <label>Custom Attributes</label>&nbsp;&nbsp;<a href="javascript:void(0)" ng-click="showAddAttributeScreen()">Add a attribute</a>
	    <table class="table table-striped">
		        <thead>
		          <tr>
		            <th></th>
		            <th>Name</th>
		            <th>Type</th>
		            <th>Options</th>
					<th>Validations</th>
					<th></th>
		          </tr>
		        </thead>
		        <tbody>
		          <tr ng-repeat="recordTypeAttribute in recordType.customAttributes">
		            <td>{{$index+1}}</td>
		            <td>{{recordTypeAttribute.name}}</td>
		            <td ng-bind="(recordTypeAttribute.type == 1 && 'Option' || (recordTypeAttribute.type == 2 && 'String' || (recordTypeAttribute.type == 3 && 'Text' || (recordTypeAttribute.type == 4 && 'Integer' || (recordTypeAttribute.type == 5 && 'Decimal' || '')))))"></td>
					<td>
		            	<span ng-repeat="option in recordTypeAttribute.options track by $index" ng-bind="$index == 0 && option || (', '+option)"></span> 
		            </td>
					<td>
		            	<span ng-repeat="(key, value) in recordTypeAttribute.validations" ng-bind="$index == 0 && (key+ ' = '+value) || (', '+key+ ' = '+value)"></span> 
		            </td>
					<td>
						<a href="javascript:void(0)" ng-click="editRecordTypeAttribute(recordTypeAttribute)">Edit</a>&nbsp;&nbsp;
						<a href="javascript:void(0)" ng-click="deleteRecordTypeAttribute(recordTypeAttribute)">Delete</a>&nbsp;&nbsp;
					</td>
		          </tr>
		        </tbody>
		      </table>
	  </div>
	  <div class="form-group" style="display:none">
			<label>Print Design</label>
			<a href="javascript:void(0)" ng-click="showCreateRecordLayoutScreen()">Create Print Design</a>
		</div>
	  <button type="button" ng-click="back()" class="btn btn-default">Go back</button>
	  <button type="submit" ng-click="updateRecordType()" class="btn btn-danger">Update</button>
	</form>
</div>
</div>
<div ng-if="navController.contains('Create Record Layout')"><div ng-include="'CreateRecordLayoutTemplate'"></div></div>
<div ng-if="navController.contains('Record Type Attribute')"><div ng-include="'RecordTypeAttributeTemplate'"></div></div>
<div ng-if="navController.contains('serial')"><div ng-include="'SerialTemplate'"></div></div>
</div>
</script>


<script type="text/ng-template" class="template" id="addRecordTypeTemplate">
<div ng-controller="AddRecordTypeController">
<div ng-show="navController.currentScreen() == 'New Record Type'">
<div>
	<form name="addRecordTypeForm">
	  <div class="form-group">
	    <label>Record Type  Name</label>
	    <input type="text" class="form-control" placeholder="Enter record type name" ng-model="recordType.name" name="recordTypeName" autocomplete="off" required>
		<div class="error" ng-show="showValidationMessages && addRecordTypeForm.recordTypeName.$error.required">Required.</div>
	  </div>
		<div class="form-group">
			<label>Serial No.</label>&nbsp;&nbsp;<a href="javascript:void(0)" ng-click="createNewSerial()">Create New</a>
			<select class="form-control" ng-model="recordType.serial.id" required name="serial">
				<option value="">--Select--</option>
				<option ng-repeat="serial in serials" value="{{serial.id}}">{{serial.name}}</option>
			</select>
			<div class="error" ng-show="showValidationMessages && addRecordTypeForm.serial.$error.required">Required.</div>
		</div>
		<div class="form-group">
			<label>Type</label>
			<input type="radio" value="1" ng-model="recordType.type" ng-change="changeType()" >Layer&nbsp;&nbsp;
			<input type="radio" value="2" ng-model="recordType.type" ng-change="changeType()" >Structure&nbsp;&nbsp;
			<input type="radio" value="3" ng-model="recordType.type" ng-change="changeType()" >Other
			<div class="error" ng-show="showValidationMessages && (!recordType.type || recordType.type == '')">Required.</div>
		</div>
		<div class="form-group" ng-if="recordType.type == 1">
	    <label>Layer Attributes</label>
	    <table class="table table-striped">
		        <thead>
		          <tr>
		            <th>Name</th>
					<th>Type</th>
		            <th>Validations</th>
					<th></th>
		          </tr>
		        </thead>
		        <tbody>
		          <tr ng-repeat="recordTypeAttribute in recordType.layerAttributes">
		            <td>{{recordTypeAttribute.name}}</td>
					<td>{{recordTypeAttribute.type == 6 && 'From (length)' || (recordTypeAttribute.type == 7 && 'To (length)' || 'Level') }}</td>
		            <td><span ng-repeat="(key, value) in recordTypeAttribute.validations" ng-bind="$index == 0 && (key+ ' = '+value) || (', '+key+ ' = '+value)"></span> </td>
					<td><a href="javascript:void(0)" ng-click="editRecordTypeAttribute(recordTypeAttribute)">Edit</a></td>
		          </tr>
		        </tbody>
		      </table>
	  </div>
	  <div class="form-group">
	    <label>Custom Attributes</label>&nbsp;&nbsp;<a href="javascript:void(0)" ng-click="showAddAttributeScreen()">Add a attribute</a>
	    <table class="table table-striped">
		        <thead>
		          <tr>
		            <th></th>
		            <th>Name</th>
		            <th>Type</th>
		            <th>Options</th>
					<th>Validations</th>
					<th></th>
		          </tr>
		        </thead>
		        <tbody>
		          <tr ng-repeat="recordTypeAttribute in recordType.customAttributes">
		            <td ng-bind="$index+1"></td>
		            <td ng-bind="recordTypeAttribute.name"></td>
		            <td ng-bind="(recordTypeAttribute.type == 1 && 'Option' || (recordTypeAttribute.type == 2 && 'String' || (recordTypeAttribute.type == 3 && 'Text' || (recordTypeAttribute.type == 4 && 'Integer' || (recordTypeAttribute.type == 5 && 'Decimal' || '')))))"></td>
					<td>
		            	<span ng-repeat="option in recordTypeAttribute.options track by $index" ng-bind="$index == 0 && option || (', '+option)"></span> 
		            </td>
					<td>
		            	<span ng-repeat="(key, value) in recordTypeAttribute.validations" ng-bind="$index == 0 && (key+ ' = '+value) || (', '+key+ ' = '+value)"></span> 
		            </td>
					<td>
						<a href="javascript:void(0)" ng-click="editRecordTypeAttribute(recordTypeAttribute)">Edit</a>&nbsp;&nbsp;
						<a href="javascript:void(0)" ng-click="deleteRecordTypeAttribute(recordTypeAttribute)">Delete</a>&nbsp;&nbsp;
					</td>
		          </tr>
		        </tbody>
		      </table>
	  </div>
	  <button type="button" ng-click="back()" class="btn btn-default">Go back</button>
	  <button type="submit" ng-click="addRecordType()" class="btn btn-danger">Submit</button>
	</form>
</div>
</div>
<div ng-if="navController.contains('recordTypeAttribute')"><div ng-include="'RecordTypeAttributeTemplate'"></div></div>
<div ng-if="navController.contains('Record Type Attribute')"><div ng-include="'RecordTypeAttributeTemplate'"></div></div>
<div ng-if="navController.contains('serial')"><div ng-include="'SerialTemplate'"></div></div>
</div>
</script>

<script type="text/ng-template" class="template" id="viewRecordsTemplate">
	<div ng-controller="ViewRecordsController">
		<div ng-show="navController.currentScreen() == 'View Records'">
			<div  style="margin-bottom:20px;">
				<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
				<a href="javascript:void(0)" class="pull-right" ng-click="toggleSearch()">Search</a>
			</div>
			
			<div class="panel clearfix" style="background: #f9f9f9;border: 1px solid #e8e8e8;" ng-show="showSearch">
				<div class="col-md-7">
					<div>
						<h6 style="font-weight:bold;">Search and filter criteria</h6>
						<div class="clearfix" ng-repeat="condition in search.conditions">
							<div class="col-md-3" style="margin-left:0px;padding-left:0px;">
								<div class="form-group">
    								<select class="form-control" ng-model="condition.column" ng-change="changeConditionColumn($index)">
  										<option value="">-Select-</option>
										<option ng-if="recordType.type == 1" ng-repeat = "recordTypeAttribute in recordType.layerAttributes" ng-bind="recordTypeAttribute.name" ng-value="'rl_'+recordTypeAttribute.type"></option>
										<option ng-repeat = "recordTypeAttribute in recordType.customAttributes" ng-bind="recordTypeAttribute.name" ng-value="'rc_'+recordTypeAttribute.id"></option>
										<option value="serial">Serial</option>
										<option value="layerId">Layer</option>
										<option value="structureId">Structure</option>
										<option value="structureItemId">Structure Item</option>
										<option ng-repeat = "layerAttribute in grid.layerAttributes" ng-bind="layerAttribute.name" ng-value="'l_'+layerAttribute.id"></option>
									</select>
  								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
    								<select class="form-control" ng-model="condition.operation">
  										<option value="">-Select-</option>
										<option ng-if="condition.columntype == 1 || condition.columntype == 4 || condition.columntype == 5 || condition.columntype == 6 ||condition.columntype == 7 || condition.columntype == 8 || condition.columntype == 9 || condition.columntype == 10 || condition.columntype == 11" value="equal to">equal to</option>
										<option ng-if="condition.columntype == 1 || condition.columntype == 4 || condition.columntype == 5 || condition.columntype == 6 ||condition.columntype == 7 || condition.columntype == 8 || condition.columntype == 9 || condition.columntype == 10 || condition.columntype == 11" value="not equal to">not equal to</option>
										<option ng-if="condition.columntype == 4 || condition.columntype == 5 || condition.columntype == 6 ||condition.columntype == 7 || condition.columntype == 8" value="greater than">greater than</option>
										<option ng-if="condition.columntype == 4 || condition.columntype == 5 || condition.columntype == 6 ||condition.columntype == 7 || condition.columntype == 8" value="less than">less than</option>
										<option ng-if="condition.columntype == 2 || condition.columntype == 3" value="starts with">starts with</option>
										<option ng-if="condition.columntype == 2 || condition.columntype == 3" value="ends with">ends with</option>
										<option ng-if="condition.columntype == 2 || condition.columntype == 3" value="contains">contains</option>
									</select>
  								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
	    							
									<select ng-if="condition.columntype == 1 || condition.columntype == 9 || condition.columntype == 10 || condition.columntype == 11" class="form-control" ng-model="condition.value">
  										<option value="">-Select-</option>
										<option ng-repeat="option in condition.options track by $index" ng-value="option.value" ng-bind="option.name"></option>
									</select>
									<input type="text" ng-if="condition.columntype != 1 && condition.columntype != 9 && condition.columntype != 10 && condition.columntype != 11" class="form-control"  ng-model="condition.value">
  								</div>
							</div>
							<div class="col-md-1">
								<button class="minus" ng-click="removeCondition($index)"></button>
								<button class="plus" ng-click="addCondition()"></button>
							</div>
						</div>
					</div>
					<div style="display:none">
						<h6 style="font-weight:bold;">Columns to display</h6>
						<div class="clearfix" >
							<div class="col-md-3" style="margin-left:0px;padding-left:0px;">
								<div class="form-group" ng-repeat="searchColumn in searchColumns[0]">
    								<input type="checkbox"> {{searchColumn.name}}
  								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group" ng-repeat="searchColumn in searchColumns[1]">
    								<input type="checkbox"> {{searchColumn.name}}
  								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group" ng-repeat="searchColumn in searchColumns[2]">
    								<input type="checkbox"> {{searchColumn.name}}
  								</div>
							</div>
						</div>
					</div>	
				</div>
				<div class="col-md-5">
					<div  style="display:none">
						<h6 style="font-weight:bold;">Result sorting & ordering</h6>
						<div class="clearfix">
							<div class="col-md-3" style="margin-left:0px;padding-left:0px;">
								<div class="form-group">
    								<select class="form-control" ng-model="search.sortColumn">
  										<option value="">-Select-</option>
										<option ng-if="recordType.type == 1" ng-repeat = "recordTypeAttribute in recordType.layerAttributes" ng-bind="recordTypeAttribute.name" ng-value="'rl_'+recordTypeAttribute.type"></option>
										<option ng-repeat = "recordTypeAttribute in recordType.customAttributes" ng-bind="recordTypeAttribute.name" ng-value="'rc_'+recordTypeAttribute.id"></option>
										<option value="layerId">Layer</option>
										<option value="structureId">Structure</option>
										<option value="structureItemId">Structure Item</option>
										<option ng-repeat = "layerAttribute in grid.layerAttributes" ng-bind="layerAttribute.name" ng-value="'l_'+layerAttribute.id"></option>
									</select>
  								</div>
							</div>
							<div class="col-md-3">
								<div class="form-group">
    								<select class="form-control" ng-model="search.sortOrder">
  										<option value="descending" selected>descending</option>
										<option value="ascending">ascending</option>
									</select>
  								</div>
							</div>
						</div>
					</div>
					<div style="margin-top:10px;margin-bottom:10px;">
						<button type="button" class="btn btn-primary" ng-click="searchRecords()"><span class="glyphicon glyphicon-search"></span> Search</button>
						<button type="button" class="btn btn-default" ng-click="resetSearch()">Reset</button>
					</div>
				</div>
			</div>
			 <pagination ng-hide="showSearch" total-items="totalRecords" page="currentPage" items-per-page="50" on-select-page="changePage(page)"></pagination>

			<table class="table table-bordered table-hover table-striped" ng-hide="showSearch">
				<thead>
					<tr style="background:rgb(218, 238, 245);">
						<th>Serial</th>
						<th ng-if="recordType.type == 1">Layer</th>
						<th ng-if="recordType.type == 1">From</th>
						<th ng-if="recordType.type == 1">To</th>
						<th ng-if="recordType.type == 1">Level</th>
						<th ng-if="recordType.type == 2">Structure</th>
						<th ng-if="recordType.type == 2">Item</th>
						<th ng-repeat = "recordTypeAttribute in grid.recordTypeAttributes" ng-bind="recordTypeAttribute.name"></th>
						<th  ng-if="recordType.type == 1 || recordType.type == 2" ng-repeat = "layerAttribute in grid.layerAttributes" ng-bind="layerAttribute.name"></th>
					</tr>
				</thead>
				<tbody>
					<tr ng-repeat="record in grid.records">
						<td><a href="javascript:void(0)" ng-click="showRecord(record)" ng-bind="recordType.serial.prefix + record.serial"></a></td>
						<td ng-if="recordType.type == 1" ng-bind="layersMap[record.layerAttributeConfig.layer.id].name"></td>
						<td ng-if="recordType.type == 1" ng-bind="record.from"></td>
						<td ng-if="recordType.type == 1"ng-bind="record.to"></td>
						<td ng-if="recordType.type == 1" ng-bind="record.level"></td>
						<td ng-if="recordType.type == 2" ng-bind="structuresMap[record.structure.id].name"></td>
						<td ng-if="recordType.type == 2" ng-bind="structureItemsMap[record.layerAttributeConfig.layer.id].name"></td>
						<td ng-repeat = "recordTypeAttribute in grid.recordTypeAttributes" ng-bind="record.attributeValuesMap[recordTypeAttribute.id]"></td>
						<td  ng-if="recordType.type == 1 || recordType.type == 2" ng-repeat = "layerAttribute in grid.layerAttributes" ng-bind="record.layerAttributeConfig.attributeValueMap[layerAttribute.id]"></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div ng-if="navController.contains('View Record')"><div ng-include="'viewRecordTemplate'"></div></div>
	</div>
</script>


<script type="text/ng-template" class="template" id="viewRecordTemplate">
	<div ng-controller="ViewRecordController">
	<div ng-show="navController.currentScreen() == 'View Record'">
        		<h3 style="margin-top:0px;margin-bottom:30px;"><span class="label label-default" ng-bind="recordType.name"></span></h3>
        		<div class="form-group" ng-if="recordType.type == 1">
					<label>Layer</label>
					<div ng-bind="layersMap[record.layerAttributeConfig.layer.id].name" class="form-display-val" ></div>
				</div>
				<div class="form-group" ng-if="recordType.type == 2">
					<label>Structure</label>
					<div ng-bind="structuresMap[record.structure.id].name" class="form-display-val" ></div>
				</div>
				<div class="form-group" ng-if="recordType.type == 2">
					<label>Item</label>
					<div ng-bind="record.layerAttributeConfig.layer.name" class="form-display-val" ></div>
				</div>
        		<div class="layer-properties">
        			<div class="form-group" ng-repeat="layerAttribute in record.layerAttributeConfig.layer.layerAttributes">
						<label>{{layerAttribute.attribute.name}}</label>
		    			<div ng-bind="record.layerAttributeConfig.attributeValueMap[layerAttribute.attribute.id]" class="form-display-val" ></div>
					</div>
        		</div>
        		<div class="record-attributes">
        			<div class="form-group" ng-repeat="recordTypeAttribute in recordType.layerAttributes">
        				<label>{{recordTypeAttribute.name}}</label>
		    			<div ng-bind="((recordTypeAttribute.type == 6 && record.from +'') ||(recordTypeAttribute.type == 7 && record.to+'')) || (recordTypeAttribute.type == 8 && record.level+'')" class="form-display-val" ></div>
        			</div>
        			<div class="form-group" ng-repeat="recordTypeAttribute in recordType.customAttributes">
        				<label >{{recordTypeAttribute.name}}</label>
						<div ng-bind="record.attributeValuesMap[recordTypeAttribute.id]" class="form-display-val" ></div>
		    		</div>
        		</div>
        		<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
				<button type="submit" ng-click="editRecord()" class="btn btn-danger">Edit</button>
        </div>
        <div ng-if="navController.contains('Edit Record')"><div ng-include="'editRecordTemplate'"></div></div>
	</div>
</script>

<script type="text/ng-template" class="template" id="editRecordTemplate">
	<div ng-controller="EditRecordController">
	<div ng-show="navController.currentScreen() == 'Edit Record'">
        		<h3 style="margin-top:0px;margin-bottom:30px;"><span class="label label-default" ng-bind="record.recordType.name"></span></h3>
        		<form name="editRecordForm" >
				<div class="form-group" ng-if="record.recordType.type == 1">
					<label>Layer</label>
					<select name="layer" class="form-control" ng-model="record.layerAttributeConfig.layer.id" ng-change="selectLayer()" ng-options="layer.id as layer.name for layer in layers" required>
							<option value="">--Select--</option>
					</select>
					<div class="error" ng-show="showValidationMessages && editRecordForm.layer.$error.required">Required.</div>
				</div>
				<div class="form-group" ng-if="record.recordType.type == 2">
					<label>Structure</label>
					<select name="structure" class="form-control" ng-model="record.structure.id" required ng-options="structure.id as structure.name for structure in structures">
							<option value="">--Select--</option>
					</select>
					<div class="error" ng-show="showValidationMessages && editRecordForm.structure.$error.required">Required.</div>
				</div>
				<div class="form-group" ng-if="record.recordType.type == 2">
					<label>Item</label>
					<select name="structureItem" class="form-control" ng-model="record.layerAttributeConfig.layer.id" required name="structureItem"  ng-options="structureItem.id as structureItem.name for structureItem in structureItems">
							<option value="">--Select--</option>
					</select>
					<div class="error" ng-show="showValidationMessages && editRecordForm.structureItem.$error.required">Required.</div>
				</div>
        		<div class="layer-properties">
        			<div class="form-group" ng-repeat="layerAttribute in record.layerAttributeConfig.layer.layerAttributes">
						<label>{{layerAttribute.attribute.name}}</label>
		    			<ng-form name="layerAttributeValidationForm">
						<select class="form-control" ng-model="record.layerAttributeConfig.attributeValueMap[layerAttribute.attribute.id]" ng-options="option for option in layerAttribute.options" name="layerAttributeSelect" mandatory="layerAttribute.mandatory">
							<option value="">--Select--</option>
						</select>
						<div class="error" ng-show="showValidationMessages && layerAttributeValidationForm.layerAttributeSelect.$error.required">Required.</div>
						</ng-form>
					</div>
        		</div>
        		<div class="record-attributes">
        			<div class="form-group" ng-repeat="recordTypeAttribute in record.recordType.layerAttributes">
        				<label>{{recordTypeAttribute.name}}</label>
		    			<ng-form name="recordLayerAttributeForm">
						<input ng-if="recordTypeAttribute.type == 6" type="text" class="form-control" ng-model="record.from" name="recordLayerAttribute" required ng-pattern="/^-?\d*\.?\d*$/">
						<input ng-if="recordTypeAttribute.type == 7" type="text" class="form-control" ng-model="record.to" name="recordLayerAttribute" required ng-pattern="/^-?\d*\.?\d*$/">
						<input ng-if="recordTypeAttribute.type == 8" type="text" class="form-control" ng-model="record.level" name="recordLayerAttribute" required ng-pattern="/^-?\d+$/" >
						<div class="error" ng-show="showValidationMessages && recordLayerAttributeForm.recordLayerAttribute.$error.required">Required.</div>
						<div class="error" ng-show="showValidationMessages && recordLayerAttributeForm.recordLayerAttribute.$error.pattern">Not valid number!</div>
						<div class="error" ng-show="showValidationMessages && recordLayerAttributeForm.recordLayerLevelAttribute.$error.required">Required.</div>
						<div class="error" ng-show="showValidationMessages && recordLayerAttributeForm.recordLayerLevelAttribute.$error.pattern">Should be a valid integer!</div>
						<div class="error" ng-show="showValidationMessages && $scope.recordTypeLayerErrors[recordTypeAttribute.type] != ''" ng-bind="recordTypeLayerErrors[recordTypeAttribute.type]"></div>
						</ng-form>
        			</div>
        			<div class="form-group" ng-repeat="recordTypeAttribute in record.recordType.customAttributes">
	        			<ng-form name="recordTypeCustomAttForm">	
						<div ng-switch on="recordTypeAttribute.type">
	        				<div ng-switch-when="1">
		        				<label>{{recordTypeAttribute.name}}</label>
		    					<select class="form-control" ng-model="record.attributeValuesMap[recordTypeAttribute.id]" ng-options="option for option in recordTypeAttribute.options"  required name="recTypeCustomAtt">
		    						<option value="">-Select-</option>
		    					</select>
	    					</div>
	    					<div ng-switch-when="2">
		        				<label>{{recordTypeAttribute.name}}</label>
		    					<input type="text" class="form-control" ng-model="record.attributeValuesMap[recordTypeAttribute.id]"  required name="recTypeCustomAtt">
	    					</div>
	    					<div ng-switch-when="3">
		        				<label>{{recordTypeAttribute.name}}</label>
		    					<textarea class="form-control" ng-model="record.attributeValuesMap[recordTypeAttribute.id]"  required name="recTypeCustomAtt"></textarea>
	    					</div>
	    					<div ng-switch-when="4">
		        				<label>{{recordTypeAttribute.name}}</label>
		    					<input type="text" class="form-control" ng-pattern="/^-?\d+$/" ng-model="record.attributeValuesMap[recordTypeAttribute.id]"  required name="recTypeCustomAtt">
								<div class="error" ng-show="showValidationMessages && recordTypeCustomAttForm.recTypeCustomAtt.$error.pattern">Only integer value allowed.</div>
	    					</div>
	    					<div ng-switch-when="5">
		        				<label>{{recordTypeAttribute.name}}</label>
		    					<input type="text" class="form-control"  ng-pattern="/^-?\d*\.?\d*$/"   ng-model="record.attributeValuesMap[recordTypeAttribute.id]"  required name="recTypeCustomAtt">
								<div class="error" ng-show="showValidationMessages && recordTypeCustomAttForm.recTypeCustomAtt.$error.pattern">Only numeric value allowed.</div>
	    					</div>
    					</div>
						<div class="error" ng-show="showValidationMessages && recordTypeCustomAttForm.recTypeCustomAtt.$error.required">Required.</div>
						<div class="error" ng-show="showValidationMessages && $scope.recordTypeCustomErrors[recordTypeAttribute.id] != ''" ng-bind="recordTypeCustomErrors[recordTypeAttribute.id]"></div>
						</ng-form>
        			</div>
					
        		</div>
        		<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
				<button type="submit" ng-click="updateRecord()" class="btn btn-danger">Update</button>
				</form>
        </div>
        <div ng-if="navController.contains('selectLayer')"><div ng-include="'selectLayerTemplate'"></div></div>
	</div>
</script>

<script type="text/ng-template" class="template" id="addRecordTemplate">
	<div ng-controller="AddRecordController">
	<div ng-show="navController.currentScreen() == 'Add Record'">
        		<h3 style="margin-top:0px;margin-bottom:30px;"><span class="label label-default" ng-bind="record.recordType.name"></span></h3>
        		<form name="addRecordForm" >
				<!--
				<div class="form-group" ng-if="record.recordType.type == 1">
					<label>Layer</label>
					<input type="text" ng-model="asyncSelected" placeholder="Start typing..." typeahead-on-select="selectLayer($item, $model, $label)" typeahead="layer as layer.name for layer in getLayers($viewValue) | filter:$viewValue" typeahead-loading="loadingLayers" class="form-control">
    				<i ng-show="loadingLayers" class="glyphicon glyphicon-refresh"></i>
					<span class="layer-name"></span>
				</div>
				-->
				<div class="form-group" ng-if="record.recordType.type == 1">
					<label>Layer</label>
					<select name="layer" class="form-control" ng-model="record.layerAttributeConfig.layer.id" ng-change="selectLayer()" required ng-options="layer.id as layer.name for layer in layers">
							<option value="">--Select--</option>
					</select>
					<div class="error" ng-show="showValidationMessages && addRecordForm.layer.$error.required">Required.</div>
				</div>
				<div class="form-group" ng-if="record.recordType.type == 2">
					<label>Structure</label>
					<select name="structure" class="form-control" ng-model="record.structure.id" required  ng-options="structure.id as structure.name for structure in structures">
							<option value="">--Select--</option>
					</select>
					<div class="error" ng-show="showValidationMessages && addRecordForm.structure.$error.required">Required.</div>
				</div>
				<div class="form-group" ng-if="record.recordType.type == 2">
					<label>Item</label>
					<select name="structureItem" class="form-control" ng-model="record.layerAttributeConfig.layer.id" ng-change="selectLayer()"  required ng-options="structureItem.id as structureItem.name for structureItem in structureItems">
							<option value="">--Select--</option>
					</select>
					<div class="error" ng-show="showValidationMessages && addRecordForm.structureItem.$error.required">Required.</div>
				</div>
        		<div class="layer-properties">
        			<div class="form-group" ng-repeat="layerAttribute in record.layerAttributeConfig.layer.layerAttributes">
						<label>{{layerAttribute.attribute.name}}</label>
		    			<ng-form name="layerAttributeValidationForm">
						<select class="form-control" ng-model="record.layerAttributeConfig.attributeValueMap[layerAttribute.attribute.id]"  name="layerAttributeSelect" mandatory="layerAttribute.mandatory">
							<option value="">--Select--</option>
							<option ng-repeat="option in layerAttribute.options" value="{{option}}">{{option}}</option>
						</select>
						<div class="error" ng-show="showValidationMessages && layerAttributeValidationForm.layerAttributeSelect.$error.required">Required.</div>
						</ng-form>
					</div>
        		</div>
        		<div class="record-attributes">
        			<div class="form-group" ng-repeat="recordTypeAttribute in record.recordType.layerAttributes">
        				<label>{{recordTypeAttribute.name}}</label>
		    			<ng-form name="recordLayerAttributeForm">
						<input ng-if="recordTypeAttribute.type == 6" type="text" class="form-control" ng-model="record.from" name="recordLayerAttribute" required ng-pattern="/^-?\d*\.?\d*$/">
						<input ng-if="recordTypeAttribute.type == 7" type="text" class="form-control" ng-model="record.to" name="recordLayerAttribute" required ng-pattern="/^-?\d*\.?\d*$/">
						<input ng-if="recordTypeAttribute.type == 8" type="text" class="form-control" ng-model="record.level" name="recordLayerAttribute" required ng-pattern="/^-?\d+$/" >
						<div class="error" ng-show="showValidationMessages && recordLayerAttributeForm.recordLayerAttribute.$error.required">Required.</div>
						<div class="error" ng-show="showValidationMessages && recordLayerAttributeForm.recordLayerAttribute.$error.pattern">Not valid number!</div>
						<div class="error" ng-show="showValidationMessages && recordLayerAttributeForm.recordLayerLevelAttribute.$error.required">Required.</div>
						<div class="error" ng-show="showValidationMessages && recordLayerAttributeForm.recordLayerLevelAttribute.$error.pattern">Should be a valid integer!</div>
						<div class="error" ng-show="showValidationMessages && $scope.recordTypeLayerErrors[recordTypeAttribute.type] != ''" ng-bind="recordTypeLayerErrors[recordTypeAttribute.type]"></div>
						</ng-form>
        			</div>
        			<div class="form-group" ng-repeat="recordTypeAttribute in record.recordType.customAttributes">
        				<ng-form name="recordTypeCustomAttForm">
						<div ng-switch on="recordTypeAttribute.type">
	        				<div ng-switch-when="1">
		        				<label >{{recordTypeAttribute.name}}</label>
		    					<select class="form-control" ng-model="record.attributeValuesMap[recordTypeAttribute.id]" required name="recTypeCustomAtt">
		    						<option value="">-Select-</option>
		    						<option ng-repeat="option in recordTypeAttribute.options track by $index" value="{{option}}">{{option}}</option>
		    					</select>
	    					</div>
	    					<div ng-switch-when="2">
		        				<label >{{recordTypeAttribute.name}}</label>
		    					<input type="text" class="form-control" ng-model="record.attributeValuesMap[recordTypeAttribute.id]" required name="recTypeCustomAtt">
							</div>
	    					<div ng-switch-when="3">
		        				<label >{{recordTypeAttribute.name}}</label>
		    					<textarea class="form-control" ng-model="record.attributeValuesMap[recordTypeAttribute.id]" required name="recTypeCustomAtt"></textarea>
	    					</div>
	    					<div ng-switch-when="4">
		        				<label >{{recordTypeAttribute.name}}</label>
		    					<input type="text" class="form-control" ng-pattern="/^-?\d+$/" ng-model="record.attributeValuesMap[recordTypeAttribute.id]" required name="recTypeCustomAtt">
								<div class="error" ng-show="showValidationMessages && recordTypeCustomAttForm.recTypeCustomAtt.$error.pattern">Only integer value allowed.</div>
	    					</div>
	    					<div ng-switch-when="5">
		        				<label >{{recordTypeAttribute.name}}</label>
		    					<input type="text" class="form-control" ng-pattern="/^-?\d*\.?\d*$/"  ng-model="record.attributeValuesMap[recordTypeAttribute.id]" required name="recTypeCustomAtt">
								<div class="error" ng-show="showValidationMessages && recordTypeCustomAttForm.recTypeCustomAtt.$error.pattern">Only numeric value allowed.</div>
	    					</div>
    					</div>
						<div class="error" ng-show="showValidationMessages && recordTypeCustomAttForm.recTypeCustomAtt.$error.required">Required.</div>
						<div class="error" ng-show="showValidationMessages && $scope.recordTypeCustomErrors[recordTypeAttribute.id] != ''" ng-bind="recordTypeCustomErrors[recordTypeAttribute.id]"></div>
						</ng-form>
        			</div>
        		</div>
				</form>
        		<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
				<button type="submit" ng-click="addRecord()" class="btn btn-danger">Add</button>
        </div>
        <div ng-if="navController.contains('selectLayer')"><div ng-include="'selectLayerTemplate'"></div></div>
	</div>
</script>

<script type="text/ng-template" class="template" id="SerialTemplate">
    <div ng-controller="SerialController">
	<div ng-show="navController.currentScreen() == 'serial'">
		<div>
			<tabset>
				<tab heading="Existing">
					<div class="marginall20">
						<table class="table table-striped">
							<thead>
								<tr>
									<th>Name</th>
									<th>Prefix</th>
									<th>Current Value</th>
									<th>Action</th>
								</tr>
							</thead>
							<tbody>
								<tr ng-repeat="serial in serials">
									<td>{{serial.name}}</td>
									<td>{{serial.prefix}}</td>
									<td>{{serial.value}}</td>
									<td><button type="button" class="btn btn-link btn-sm" ng-click="editSerial(serial)">Edit</button></td>
							</tbody>
						</table>
						<div class="form-group">							
							<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
						</div>
					</div>
				</tab>
				<tab heading="New">
					<div class="marginall20">
						<form role="form">
							<div class="form-group">
								<label>Name</label>
								<input type="text" class="form-control" placeholder="Name" ng-model="serial.name">
							</div>
							<div class="form-group">
								<label>Prefix</label>
								<input type="text" class="form-control" placeholder="This will be appended in front of record number" ng-model="serial.prefix">
							</div>
							<div class="form-group">							
								<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
								<button type="button" class="btn btn-danger" ng-click="addSerial()">Add</button>
							</div>
					</div>
				</tab>
			</tabset>
	</div>
	</div>
</script>


<script type="text/ng-template" class="template" id="RecordTypeAttributeTemplate">
    <div ng-controller="RecordTypeAttributeController">
	<div ng-show="navController.currentScreen() == 'Record Type Attribute'">
        		<div>
        			<form name="recordTypeAttributeForm">
					<tabset>
					    
					    <tab heading="Details">
					    	<div class="marginall20">
						    	<div class="marginbottom10"><span class="badge">1</span> Basic details &nbsp;&nbsp;</div>
				                    <div class="form-group">
				                        <input type="text" class="form-control" placeholder="Name of the attribute" ng-model="recordTypeAttribute.name" required autocomplete="off" name="recordTypeAttributeName">
										<div class="error" ng-show="showValidationMessages && recordTypeAttributeForm.recordTypeAttributeName.$error.required">Required.</div>
				                    </div>
									<div class="form-group" ng-if="recordTypeAttribute.type !=6 && recordTypeAttribute.type !=7 && recordTypeAttribute.type !=8">
				                        <select class="form-control" ng-model="recordTypeAttribute.type">
										  <option value="">--Select type--</option>
										  <option value="1" >Option</option>
										  <option value="2">String</option>
										  <option value="3">Text</option>
										  <option value="4">Integer</option>
										  <option value="5">Decimal</option>
										</select>
				                    </div>
				                    <br/>
									<div ng-if="recordTypeAttribute.type == '1'">
				                    <div class="marginbottom10"><span class="badge">2</span> Options &nbsp;&nbsp;<button type="button" class="btn btn-link" ng-click="addOption()" >Add more options</button></div>
				                    <div class="form-group input-group" ng-repeat="option in recordTypeAttribute.options  track by $index">
				                        <span class="input-group-addon">{{$index+1}}.</span>
				                        <input type="text" class="form-control" placeholder="" ng-model="recordTypeAttribute.options[$index]">
				                    </div>
									</div>
									<div ng-show="recordTypeAttribute.type && recordTypeAttribute.type != '' && recordTypeAttribute.type != '1' && recordTypeAttribute.type != 8">
				                    <div class="marginbottom10" ng-init="showSpinner()"><span class="badge">3</span> Validations</div>
				                    <div class="form-group input-group" ng-if="recordTypeAttribute.type == 2 || recordTypeAttribute.type == 3" >
                        				<span class="input-group-addon">Min Length</span>
                        				<input type="text" class="form-control spinner" ng-model="recordTypeAttribute.validations.minLength" ng-pattern="/^-?\d+$/" name="minLength">
										<div class="error" ng-show="showValidationMessages && recordTypeAttributeForm.minLength.$error.pattern">Only integer value allowed.</div>
                    				</div>
									<div class="form-group input-group" ng-if="recordTypeAttribute.type == 2 || recordTypeAttribute.type == 3">
                        				<span class="input-group-addon">Max Length</span>
                        				<input type="text" class="form-control spinner" ng-model="recordTypeAttribute.validations.maxLength" ng-pattern="/^-?\d+$/" name="maxLength">
										<div class="error" ng-show="showValidationMessages && recordTypeAttributeForm.maxLength.$error.pattern">Only integer value allowed.</div>
                    				</div>
									<div class="form-group input-group" ng-if="recordTypeAttribute.type == 4 || recordTypeAttribute.type == 5 || recordTypeAttribute.type == 6 || recordTypeAttribute.type ==7">
                        				<span class="input-group-addon">Min Value</span>
                        				<input type="text" class="form-control spinner" ng-model="recordTypeAttribute.validations.minValue" ng-pattern="/^-?\d+$/" name="minValue" >
										<div class="error" ng-show="showValidationMessages && recordTypeAttributeForm.minValue.$error.pattern">Only integer value allowed.</div>
                    				</div>
									<div class="form-group input-group" ng-if="recordTypeAttribute.type == 4 || recordTypeAttribute.type == 5 || recordTypeAttribute.type == 6 || recordTypeAttribute.type ==7">
                        				<span class="input-group-addon">Max Value</span>
                        				<input type="text" class="form-control spinner" ng-model="recordTypeAttribute.validations.maxValue" ng-pattern="/^-?\d+$/" name="maxValue">
										<div class="error" ng-show="showValidationMessages && recordTypeAttributeForm.maxValue.$error.pattern">Only integer value allowed.</div>
                    				</div>
									</div>
				                <button type="button" ng-click="back()" class="btn btn-default">Go back</button>
								<button type="button" class="btn btn-danger" ng-click="saveAttribute()" ng-bind="action == 'add' && 'Add' || 'Edit'"></button>
				            </div>
					    </tab>
					</tabset>
					</form>
        		</div>
			</div>
        	</div>
</script>

<script type="text/ng-template" class="template" id="executeBarchartTemplate">
	<div ng-controller="ExecuteBarchartController">
		<div ng-show="navController.currentScreen() == 'Execute Barchart'"  ng-init="initialize()">
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
