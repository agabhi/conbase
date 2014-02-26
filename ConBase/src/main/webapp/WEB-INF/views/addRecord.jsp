<body ng-app="addRecordApp" ng-controller="AddRecordController">

<%@ include file="../layout/includeHomepageHeader.jsp"%>
<%@ include file="../layout/navigationMenu.jsp"%>

<section id="content" class="content-sidebar no-border"><span ng-repeat="screen in navController.screens">{{screen.screen}}-></span>
    <div class="row row-padder" style="margin:40px 0px;">
        <div ng-show="navController.currentScreen() == 'addRecord'">
        		<h1>Add Record<span class="badge" ng-bind="record.recordType.name"></span></h1>
        		<input type="text" ng-model="asyncSelected" placeholder="Start typing..." typeahead-on-select="selectLayer($item, $model, $label)" typeahead="layer as layer.name for layer in getLayers($viewValue) | filter:$viewValue" typeahead-loading="loadingLayers" class="form-control">
    			<i ng-show="loadingLayers" class="glyphicon glyphicon-refresh"></i>
				<button type="button" class="btn btn-link">Advanced Search</button>
				<span class="layer-name"></span>
        		<div class="layer-properties">
        			<h6>Layer Attributes</h6>
					<div class="form-group" ng-repeat="attribute in record.layer.attributes">
						<label>{{attribute.name}}</label>
		    			<select class="form-control" ng-model="record.layerProperties[attribute.id]">
							<option ng-repeat="option in attribute.options" value="{{option}}">{{option}}</option>
						</select>
					</div>
        		</div>
        		<div class="record-attributes">
        			<div class="form-group" ng-repeat="recordTypeAttribute in layerAttributes">
        				<label>{{recordTypeAttribute.name}}</label>
		    			<input type="number" class="form-control" ng-model="record.layerAttributes[recordTypeAttribute.id]">
        			</div>
        			<div class="form-group" ng-repeat="recordTypeAttribute in record.recordType.customAttributes">
        				<div ng-switch on="recordTypeAttribute.type">
	        				<div ng-switch-when="1">
		        				<label for="exampleInputEmail1">{{recordTypeAttribute.name}}</label>
		    					<select class="form-control" ng-model="record.customAttributes[recordTypeAttribute.id]">
		    						<option value="">-Select-</option>
		    						<option ng-repeat="option in recordTypeAttribute.options track by $index" value="{{option}}">{{option}}</option>
		    					</select>
	    					</div>
	    					<div ng-switch-when="2">
		        				<label for="exampleInputEmail1">{{recordTypeAttribute.name}}</label>
		    					<input type="text" class="form-control" ng-model="record.customAttributes[recordTypeAttribute.id]">
	    					</div>
	    					<div ng-switch-when="3">
		        				<label for="exampleInputEmail1">{{recordTypeAttribute.name}}</label>
		    					<textarea class="form-control" ng-model="record.customAttributes[recordTypeAttribute.id]"></textarea>
	    					</div>
	    					<div ng-switch-when="4">
		        				<label for="exampleInputEmail1">{{recordTypeAttribute.name}}</label>
		    					<input type="number" class="form-control" ng-model="record.customAttributes[recordTypeAttribute.id]">
	    					</div>
	    					<div ng-switch-when="5">
		        				<label for="exampleInputEmail1">{{recordTypeAttribute.name}}</label>
		    					<input type="number" class="form-control" ng-model="record.customAttributes[recordTypeAttribute.id]">
	    					</div>
    					</div>
        			</div>
        		</div>
        		<button type="submit" ng-click="addRecord()" class="btn btn-danger">Add</button>
        </div>
        <div ng-if="navController.contains('selectLayer')"><div ng-include="'selectLayerTemplate'"></div></div>
    </div>
</section>

<%@ include file="../layout/includeScripts.jsp"%>

<script>
	$( ".spinner" ).spinner();
</script>
<script type="text/ng-template" class="template" id="selectLayerTemplate">
	<div ng-controller="SelectLayerController">
		<div ng-show="navController.currentScreen() == 'selectLayer'">
			<h1>Search a layer</h1>
			<div>
				<input name="layerName">&nbsp;<button type="button" class="btn btn-link">Advanced Search</button>
				<span class="layer-name"></span>
				<div>
					<h6>Layer Attributes</h6>
					<div class="form-group" ng-repeat="attribute in selectedLayer.attributes">
						<label for="exampleInputEmail1">{{attribute.name}}</label>
		    			<select class="form-control">
							<option ng-repeat="option in attribute.options" value="option">{{option}}</option>
						</select>
					</div>
				</div>
				<di
			</div>
		</div>
	</div>
</script>