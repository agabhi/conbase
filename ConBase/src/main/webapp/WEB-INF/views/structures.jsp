<body ng-app="layersApp" ng-controller="StructuresController">

<%@ include file="../layout/includeHomepageHeader.jsp"%>
<%@ include file="../layout/navigationMenu.jsp"%>
<div class="sub-header">
	<ol class="breadcrumb">
  		<li class="active" ng-repeat="screen in navController.screens" ng-bind="screen.screen"></li>
	</ol>
	<h2 title="Layers" ng-bind="navController.currentScreen()"></h2>
</div>
<section id="content" class="content-sidebar no-border">
    <div class="row row-padder" style="margin:20px 0px;">
        <div ng-show="navController.currentScreen() == 'Structures'">
        		<button type="button" ng-click="showNewStructureScreen()" class="btn btn-danger">Add</button>
        		<button type="button" ng-click="goToStructureItems()" class="btn btn-danger">Structure Items</button>
        		<div style="margin-top:20px;">
        			<table id="structuresList"></table>
        		</div>
        </div>
        <div ng-if="navController.contains('New Structure')"><div ng-include="'addStructureTemplate'"></div></div>
        <div ng-if="navController.contains('View Structure')"><div ng-include="'viewStructureTemplate'"></div></div>
        <div ng-if="navController.contains('Structure Items')"><div ng-include="'StructureItemsTemplate'"></div></div>
    </div>
</section>

<%@ include file="../layout/includeScripts.jsp"%>


<script type="text/ng-template" class="template" id="viewStructureTemplate">
	<div ng-controller="ViewStructureController">
		<div ng-show="navController.currentScreen() == 'View Structure'">
			<div class="form-group">
	    		<label>Structure Name</label>
	    		<div ng-bind="structure.name" class="form-display-val" ></div>
	  		</div>
			<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
	  		<button type="submit" ng-click="editStructure()" class="btn btn-danger">Edit</button>
		</div>	
		<div ng-if="navController.contains('Edit Structure')"><div ng-include="'editStructureTemplate'"></div></div>
	</div>
</script>

<script type="text/ng-template" class="template" id="editStructureTemplate">
	<div ng-controller="EditStructureController">
		<div ng-show="navController.currentScreen() == 'Edit Structure'">
			<form role="form" name="editStructureForm">
			<div class="form-group">
	    		<label>Structure Name</label>
	    		<input type="text" class="form-control" placeholder="Enter structure name" ng-model="structure.name" name="structureName" autocomplete="off">
				<div class="error"ng-show="showValidationMessages && editStructureForm.structureName.$error.required">Name is required.</div>
	  		</div>
			<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
	  		<button type="button" ng-click="updateStructure()" class="btn btn-danger">Update</button>
			<button type="button" class="btn btn-warning pull-right" ng-click="deactivateStructure()">Delete</button>
		</form>
		</div>	
	</div>
</script>

<script type="text/ng-template" class="template" id="addStructureTemplate">
<div ng-controller="AddStructureController">
<div ng-show="navController.currentScreen() == 'New Structure'">
<div>
	<form role="form" name="addStructureForm">
	  <div class="form-group">
	    <label>Structure Name</label>
	    <input type="text" class="form-control" placeholder="Enter structure name" ng-model="structure.name" name="structureName" autocomplete="off" required>
		<div class="error"ng-show="showValidationMessages && addStructureForm.structureName.$error.required">Name is required.</div>
	  </div>
	  <button type="button" ng-click="back()" class="btn btn-default">Go back</button>
	  <button type="submit" ng-click="addStructure()" class="btn btn-danger">Submit</button>
		
	</form>
</div>
</div>
</div>
</script>

<script type="text/ng-template" class="template" id="StructureItemsTemplate">
<div ng-controller="StructureItemController">
	<div ng-show="navController.currentScreen() == 'Structure Items'">
		<div>
			<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
			<button type="button" ng-click="showNewStructureItemScreen()" class="btn btn-danger">Add</button>
			<div style="margin-top:20px;">
				<table id="strItemsList"></table>
			</div>
		</div>
	</div>
	<div ng-if="navController.contains('New Structure Item')"><div ng-include="'addStructureItemTemplate'"></div></div>
	<div ng-if="navController.contains('View Structure Item')"><div ng-include="'viewStructureItemTemplate'"></div></div>
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

<script type="text/ng-template" class="template" id="viewStructureItemTemplate">
	<div ng-controller="ViewStructureItemController">
		<div ng-show="navController.currentScreen() == 'View Structure Item'">
			<div class="form-group">
	    		<label>Item Name</label>
	    		<div ng-bind="item.name" class="form-display-val" ></div>
	  		</div>
			<div class="form-group">
	    			<label>Attributes</label>
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
		          			<tr ng-repeat="layerAttribute in item.layerAttributes">
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
			<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
	  		<button type="submit" ng-click="editItem()" class="btn btn-danger">Edit</button>
		</div>	
		<div ng-if="navController.contains('Edit Structure Item')"><div ng-include="'editStructureItemTemplate'"></div></div>
	</div>
</script>


<script type="text/ng-template" class="template" id="editStructureItemTemplate">
	<div ng-controller="EditStructureItemController">
		<div ng-show="navController.currentScreen() == 'Edit Structure Item'">
			<form role="form" name="editItemForm">
			<div class="form-group">
	    		<label>Item Name</label>
	    		<input type="text" class="form-control" placeholder="Enter item name" ng-model="item.name" name="itemName" autocomplete="off">
				<div class="error"ng-show="showValidationMessages && editLayerForm.itemName.$error.required">Name is required.</div>
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
		          			<tr ng-repeat="layerAttribute in item.layerAttributes">
		            			<td>{{$index+1}}</td>
		            			<td>{{layerAttribute.attribute.name}}</td>
		            			<td>
		            					<span style="display:none" ng-bind="checkAttributes(item)"></span>
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
	  		<button type="button" ng-click="updateItem()" class="btn btn-danger">Edit</button>
			<button type="button" class="btn btn-warning pull-right" ng-click="deactivateItem()">Delete</button>
		</form>
		</div>	
		<div ng-if="navController.contains('Add Attribute')"><div ng-include="'addAttributeTemplate'"></div></div>
	</div>
</script>

<script type="text/ng-template" class="template" id="addStructureItemTemplate">
<div ng-controller="AddStructureItemController">
<div ng-show="navController.currentScreen() == 'New Structure Item'">
<div>
	<form role="form" name="addItemForm">
	  <div class="form-group">
	    <label>Item Name</label>
	    <input type="text" class="form-control" placeholder="Enter item name" ng-model="item.name" name="itemName" autocomplete="off" required>
		<div class="error" ng-show="showValidationMessages && addItemForm.itemName.$error.required">Name is required.</div>
	  </div>
	  <div class="form-group">
	    <label>Attributes</label>&nbsp;&nbsp;&nbsp;<a href="javascript:void(0)" ng-click="showAddAttributeScreen()">Add a attribute</a>
		<div class="error"ng-show="showValidationMessages && attributesInvalid">All attributes should have at least one value allowed.</div>
	    <table class="table table-striped table-bordered innertable">
		        <thead>
		          <tr>
		            <th></th>
		            <th>Name</th>
		            <th>Allowed Values</th>
					<th>Mandatory</th>
		          </tr>
		        </thead>
		        <tbody>
		          <tr ng-repeat="layerAttribute in item.layerAttributes">
		            <td>{{$index+1}}</td>
		            <td>{{layerAttribute.attribute.name}}</td>
		            <td>
		            	<div class="btn-group">
  							<button type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" style="min-width:200px;">
    							{{layerAttribute.allowedValues}} <span class="caret"></span>
  							</button>
  							<ul class="dropdown-menu" role="menu">
    							<li style="font-size:11px;padding-left:10px;"><input data-allAttr="All" data-attrId="{{layerAttribute.attribute.id}}" ng-click="allOptionsClicked(layerAttribute)" style="margin-right:10px;" type="checkbox" checked/>All</li>
								<li class="divider"></li>
    							<li style="font-size:11px;padding-left:10px;" ng-repeat="option in layerAttribute.attribute.options track by $index"><input ng-click="attributeValueClicked(layerAttribute)" data-attrId="{{layerAttribute.attribute.id}}" data-attr="{{option}}" style="margin-right:10px;" type="checkbox" checked/ value="{{option}}">{{option}}</li>
    						</ul>
						</div>
		            </td>
					<td>
						<input type="radio" ng-model="layerAttribute.mandatory" ng-value="{'true':true}.true" name="mandatory_{{layerAttribute.attribute.id}}">&nbsp;Yes&nbsp;&nbsp;&nbsp;<input  name="mandatory_{{layerAttribute.attribute.id}}" type="radio" ng-value="{'false':false}.false" ng-model="layerAttribute.mandatory" >&nbsp;No
					</td>
		          </tr>
		        </tbody>
		      </table>
	  </div>
	  
	  <button type="button" ng-click="back()" class="btn btn-default">Go back</button>
	  <button type="submit" ng-click="addItem()" class="btn btn-danger">Submit</button>
		
	</form>
</div>
</div>
<div ng-if="navController.contains('Add Attribute')"><div ng-include="'addAttributeTemplate'"></div></div>
</div>
</script>

<script type="text/ng-template" class="template" id="DeactivateStructureTemplate">
<!-- Modal -->
<div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" ng-click="close()">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Delete Structure</h4>
      </div>
      <div class="modal-body">
        Are you sure you want to delete structure <strong><span ng-bind="structure.name"></span></strong>?
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal" ng-click="close()" >Close</button>
        <button type="button" class="btn btn-primary" ng-click="deactivateStructure()">Yes</button>
      </div>
    </div>
</script>

<script type="text/ng-template" class="template" id="DeactivateItemTemplate">
<!-- Modal -->
<div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" ng-click="close()">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Delete Structure Item</h4>
      </div>
      <div class="modal-body">
        Are you sure you want to delete structure item <strong><span ng-bind="item.name"></span></strong>?
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal" ng-click="close()" >Close</button>
        <button type="button" class="btn btn-primary" ng-click="deactivateItem()">Yes</button>
      </div>
    </div>
</script>

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

<script type="text/ng-template" class="template" id="RemoveItemAttributeTemplate">
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

<script type="text/ng-template" class="template" id="RemoveLayerAttributeTemplate">
<!-- Modal -->
<div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" ng-click="close()">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Remove Attribute from Item</h4>
      </div>
      <div class="modal-body">
        <span ng-if="layerAttribute.id && layerAttribute.id != 0">By removing this attribute from item, it will get removed from all records created for this item. </span>Are you sure you want to remove this attribute <strong><span ng-bind="layerAttribute.attribute.name"></span></strong>? 
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal" ng-click="close()" >Close</button>
        <button type="button" class="btn btn-primary" ng-click="removeLayerAttribute()">Yes</button>
      </div>
    </div>

</script>

</body>