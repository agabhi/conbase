function Navigation($scope, screen) {
	this.screens = [];
	
	$scope.back = function() {
		$scope.navController.pop();
	};
	
	if (screen) {
		this.screens.push({screen: screen});
	}
	this.push = function(screen, handler) {
		var lastScreen = this.screens[this.screens.length -1];
		lastScreen.handler = handler;
		this.screens.push({screen: screen});
	};
	this.pop = function(result) {
		if (this.screens && this.screens.length > 1) {
			this.screens.pop();
			var lastScreen = this.screens[this.screens.length -1];
			if (lastScreen.handler) {
				lastScreen.handler(result);
			}
		}
	};
	this.popToRootView = function() {
		this.screens = this.screens[this.screens[0]];
	};
	
	this.currentScreen = function() {
		if (this.screens.length > 0) {
			return this.screens[this.screens.length -1].screen;
		}
	};
	
	this.contains = function(value) {
		var found = false;
		$.each(this.screens, function( index, s ) {
			if (value == s.screen) {
				console.log("return true");
				found = true;
			}
		});
		return found;
	}
};

function showErrorMessage(message) {
	$("#appErrorMsg").html(message);
	$("#applicationError").show();
	setTimeout(function(){$("#applicationError").fadeOut();}, 5000);
}

function showSuccessMessage(message) {
	$("#appSuccessMsg").html(message);
	$("#applicationSuccess").show();
	setTimeout(function(){$("#applicationSuccess").fadeOut();}, 5000);
}

var userHomeApp = angular.module('UserHomeApp', ['ui.bootstrap']);

userHomeApp.factory('parameter', function() {
    var value;
    var parameterService = {};

    parameterService.setValue = function(item) {
        value = item;
    };
    parameterService.getValue = function() {
        return value;
    };

    return parameterService;
});

userHomeApp.controller('UserHomeController', function ($scope, parameter) {
	$scope.navController = new Navigation($scope, "Home");
	$scope.getAllOwnedProjects = function() {
		console.log($scope.layer);
		var url = "getAllOwnedProjects?userId="+userId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.ownedProjects = data.output;
					  } else {
						  //showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	$scope.getAllOwnedProjects();
	
	$scope.getAllInvitedProjects = function() {
		console.log($scope.layer);
		var url = "getAllInvitedProjects?userId="+userId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.invitedProjects = data.output;
					  } else {
						  //showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	$scope.getAllInvitedProjects();
	//$scope.getLayer();
	
	$scope.createProject = function() {
		$scope.navController.push("Create Project", function() {
			$scope.getAllOwnedProjects();
		});
	};
	
	$scope.viewProjectDetails = function(projectId) {
		parameter.setValue(projectId);
		$scope.navController.push("View Project Details", function() {
			//refresh layers
		});
	};
	
	$scope.changeCurrentProject = function(projectId) {
		$.ajax({
			  type: "POST",
			  url: "changeCurrentProject?projectId="+projectId,
			  async: false,
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  document.location.href = 'recordTypes';
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
});

userHomeApp.controller('CreateProjectController', function ($scope, parameter) {
	$scope.addProject = function() {
		$scope.showValidationMessages = true;
		if (!$scope.addProjectForm.$invalid) {
			var url = "addProject";
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.project),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  $scope.navController.pop(data.output);
						  showSuccessMessage("Success! Project created successfully..");
					  } else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		}
	};
});

userHomeApp.controller('EditProjectDetailsController', function ($scope, $modal, parameter) {
	$scope.project = parameter.getValue();
});

userHomeApp.controller('ViewProjectDetailsController', function ($scope, $modal, parameter) {
	$scope.projectId = parameter.getValue();
	
	$scope.isProjectOwner = function() {
		var isOwner = false;
		if ($scope.project.user.id == userId) {
			isOwner = true;
		}
		return isOwner;
	};
	
	$scope.editProject = function() {
		parameter.setValue($scope.project);
		$scope.navController.push("Edit Project Details", function() {
			//refresh layers
		});
	}
	
	$scope.removeUserFromProject = function(user) {
		var modalInstance = $modal.open({
			templateUrl : 'RemoveUserTemplate',
			controller : 'RemoveUserController',
			resolve : {
				user : function() {
					return user;
				},
				projectId : function() {
					return $scope.projectId;
				}
			}
		});
		modalInstance.result.then(function(status) {
			if(status && status == "removed") {
				$scope.getProject();
			}
		});
	};
	
    $scope.getProject = function() {
		var url = "getProjectById?projectId="+$scope.projectId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.project = data.output;
					  } else {
						  showErrorMessage("No project found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	$scope.getProject();
	
	$scope.inviteUsers = function() {
		var allowedUsers = 1;
		var noOfUsers = 1;
		if ($scope.project.allowedUsers && $scope.project.allowedUsers > 1) {
			allowedUsers = $scope.project.allowedUsers;
		}
		if ($scope.project.invitedUsers && $scope.project.invitedUsers.length > 0) {
			noOfUsers += $scope.project.invitedUsers.length;
		}
		if (noOfUsers < allowedUsers) {
			var modalInstance = $modal.open({
				templateUrl : 'InviteUserTemplate',
				controller : 'InviteUserController',
				resolve : {
					projectId : function() {
						return $scope.projectId;
					}
				}
			});
			modalInstance.result.then(function(status) {
				if(status && status == "added") {
					$scope.getProject();
				}
			});
		} else {
			var modalInstance = $modal.open({
				templateUrl : 'CannotAddUsersTemplate',
				controller : 'CannotAddUsersController'
				
			});
		}
	};
});

userHomeApp.controller('CannotAddUsersController', function ($scope, $modalInstance, parameter) {
	$scope.close = function() {
		$modalInstance.dismiss();
	};
}); 

userHomeApp.controller('InviteUserController', function ($scope, $modalInstance, projectId, parameter) {
	$scope.invitedUser = {};
	$scope.inviteUser = function() {
		var url = "inviteUser?projectId="+projectId+"&email="+$scope.invitedUser.email;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  if (data.output == "complete") {
							  showSuccessMessage("Success! User is added to the project");
							  $modalInstance.close("added");
						  }
						  if (data.output == "pending") {
							  showSuccessMessage("Success! This user is not yet registered in Conbase. An email invite has been sent to the user. User will be added to the project automatically upon registration.");
							  $modalInstance.close("pending");
						  }
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	
	$scope.close = function() {
		$modalInstance.dismiss();
	};
});

userHomeApp.controller('RemoveUserController', function ($scope, $modalInstance, projectId, user, parameter) {
	$scope.user = user;
	$scope.removeUser = function() {
		var url = "removeUserFromProject?projectId="+projectId+"&userId="+$scope.user.id;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  showSuccessMessage("Success! User removed from the project.");
					  $modalInstance.close("removed");
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	
	$scope.close = function() {
		$modalInstance.dismiss();
	};
});


















var layersApp = angular.module('layersApp', ['ui.bootstrap']);

layersApp.directive('mandatory', function ($compile) {
    return {
        restrict: 'A',
        scope: {'mandatory' : "="},
        link: function (scope,element, attrs) {
        	if (!element.attr('required') && scope.mandatory){
            element.attr("required", true);
            $compile(element)(scope.$parent);
          }
        }
    };
});

layersApp.factory('parameter', function() {
    var value;
    var parameterService = {};

    parameterService.setValue = function(item) {
        value = item;
    };
    parameterService.getValue = function() {
        return value;
    };

    return parameterService;
});


layersApp.controller('LayersController', function ($scope, parameter) {
	$scope.selectedMenu = 'layers';
	$scope.navController = new Navigation($scope, "Layers");
	
	$scope.showNewLayerScreen = function() {
		$scope.screen = $scope.navController.push("New Layer", function() {
			jQuery("#list4").trigger( 'reloadGrid' );
		});
	};
	
	
	$(document).ready(function() {
	    jQuery("#list4").jqGrid({
	        url:'getJqItems?type=1',
	    	datatype: "json",
	        width: '100%',
	        height: '100%',
	        autowidth:true,
	        scrollOffset: 0,
	        altRows:true,
	        altclass:'myAltRowClass',
	        colNames:['Id', 'Layer Name'],
	        colModel:[
	            {name:'id',index:'id', hidden:true, align:"left"},
	            {name:'name',index:'name', align:"left"}
	        ],
	        onSelectRow: function(id){
	        	var layerId = $("#list4").jqGrid ('getCell', id, 'id');
	        	parameter.setValue(layerId);
	        	$scope.$apply(function () {
	        		 $scope.navController.push("View Layer", function(result) {
	        			 if (result == "deleted" || result == "edited") {
	        				 jQuery("#list4").trigger( 'reloadGrid' );
	        			 }
	        		 });
	        	});
	        }
	    });
	   });

	   function getTagCellContents (a) {
	       return '<a data-toggle="modal" href="#myModal">Upload <img src="<%=request.getContextPath()%>/assets/icons/excel-icon.png"></a>&nbsp;&nbsp;&nbsp;<a data-toggle="modal" href="#layerChartModal">View Layer Chart <img src="<%=request.getContextPath()%>/assets/icons/excel-icon.png"></a>';
	   }
});

layersApp.controller('ViewLayerChartController', function ($scope, parameter) {
	$scope.layerAttributeConfig= parameter.getValue();
	$scope.from = 0;
	$scope.interval = 10;
	$scope.noOfBlocks = 50;
	
	$scope.initialize = function() {
		$('.carousel').carousel({
            interval:false // remove interval for manual sliding
        });
        //$('.carousel').on('slid', function (e) {
        //	alert("check123");
        //});
	}
	
	$scope.refreshChart = function() {
		$(".item.layer-chart-div.active").html("Loading. Please wait..");
		$scope.getLayerChartEntries($(".item.layer-chart-div.active"),$scope.from, $scope.interval, $scope.noOfBlocks);
	}
	$scope.nextLayerChart = function() {
		var length = +$scope.interval *  +$scope.noOfBlocks;
		$scope.from = +$scope.from + length; 
		$(".item.layer-chart-div:not(.active)").html("Loading. Please wait..");
		$('.carousel').carousel('next');
		$scope.getLayerChartEntries($(".item.layer-chart-div:not(.active)"),$scope.from, $scope.interval, $scope.noOfBlocks);
	};
	
	$scope.prevLayerChart = function() {
		var length = +$scope.interval *  +$scope.noOfBlocks;
		$scope.from = +$scope.from - length;
		$(".item.layer-chart-div:not(.active)").html("Loading. Please wait..");
		$('.carousel').carousel('prev');
		$scope.getLayerChartEntries($(".item.layer-chart-div:not(.active)"),$scope.from, $scope.interval, $scope.noOfBlocks);
	};
	
	$scope.getLayerChartEntries = function(obj, from, interval, noOfBlocks) {
		console.log($scope.layer);
		var url = "getLayerChartEntriesByConfigIdByFromByTo?layerAttributeConfigId="+$scope.layerAttributeConfig.id;
		var to = from + (interval * noOfBlocks);
		$.ajax({
			  type: "GET",
			  url: url,
			  async: true,
			  data: {from :from, to: to},
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  obj.layerchart({from:+from, noOfBlocks: +noOfBlocks, interval:+interval, data:{layerAttributeConfig : $scope.layerAttributeConfig, entries : data.output}});
					  } else {
						  showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	$scope.getLayerChartEntries($(".item.layer-chart-div.active"),$scope.from, $scope.interval, $scope.noOfBlocks);
});

layersApp.controller('ViewLayerController', function ($scope, $modal, parameter) {
	$scope.layerId = parameter.getValue();
	
    $scope.getLayer = function() {
		console.log($scope.layer);
		var url = "getLayerById?layerId="+$scope.layerId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.layer = data.output;
					  } else {
						  showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	$scope.getLayer();
	
	$scope.viewLayerChart = function(lac) {
		lac.layer = $scope.layer;
		parameter.setValue(lac);
		$scope.navController.push("View Layer Chart");
	}
	
	$scope.deleteLayerChart = function(lac) {
		lac.layer = $scope.layer;
		
		var modalInstance = $modal.open({
			templateUrl : 'DeleteLayerChartTemplate',
			controller : 'DeleteLayerChartController',
			resolve : {
				layerAttributeConfig : function() {
					return lac;
				}
			}
		});
	}
	
	$scope.edited = false;
	$scope.editLayer = function() {
		parameter.setValue($scope.layerId);
		$scope.navController.push("Edit Layer", function(result) {
				if(result == "deleted"){
					$scope.navController.pop("deleted");
				} else if (result == "edited") {
					$scope.edited = true;
					$scope.getLayer();
				}
			});
	};
	
	$scope.back = function() {
		if ($scope.edited) {
			$scope.navController.pop("edited");
		} else {
			$scope.navController.pop();
		}
	};
});

layersApp.controller('AddLayerChartController', function ($scope, parameter) {
	$scope.layerAttributeConfig = {};
	$scope.layerAttributeConfig.layer = parameter.getValue();
	$scope.layerAttributeConfig.attributeValueMap = {};
	$scope.submitLayerChart = function(){
		$scope.showValidationMessages = true;
		if (!$scope.addLayerChartForm.$invalid) {
			$("#layerChartRequestString").val(JSON.stringify($scope.layerAttributeConfig));
			var form = document.getElementById("addLayerChartForm");
			// Create the iframe...
		    var iframe = document.createElement("iframe");
		    iframe.setAttribute("id", "upload_iframe");
		    iframe.setAttribute("name", "upload_iframe");
		    iframe.setAttribute("width", "0");
		    iframe.setAttribute("height", "0");
		    iframe.setAttribute("border", "0");
		    iframe.setAttribute("style", "width: 0; height: 0; border: none;");
		 
		    // Add to document...
		    form.parentNode.appendChild(iframe);
		    window.frames['upload_iframe'].name = "upload_iframe";
		 
		    var iframeId = document.getElementById("upload_iframe");
		 
		    // Add event...
		    var eventHandler = function () {
		 
		            if (iframeId.detachEvent) iframeId.detachEvent("onload", eventHandler);
		            else iframeId.removeEventListener("load", eventHandler, false);
		            
		            var content;
		            // Message from server...
		            if (iframeId.contentDocument) {
		                content = iframeId.contentDocument.body.innerHTML;
		            } else if (iframeId.contentWindow) {
		                content = iframeId.contentWindow.document.body.innerHTML;
		            } else if (iframeId.document) {
		                content = iframeId.document.body.innerHTML;
		            }
		            
		            //document.getElementById(div_id).innerHTML = content;
		            if (content && !isEmpty(content)) {
			            setTimeout(function(){
			            	$('#layerChartModal').modal('hide');
			            	$scope.$apply(function () {
			            		$scope.navController.pop("added");
			            	});
			            	showSuccessMessage("Success! Layer chart uploaded.");
			            }, 2000);
		            } else {
		            	setTimeout(function(){
			            	$('#layerChartModal').modal('hide');
			            	showErrorMessage("Error occured while processing the file!");
			            }, 2000);
		            }
		            
		            // Del the iframe...
		            setTimeout('iframeId.parentNode.removeChild(iframeId)', 250);
		        }
		 
		    if (iframeId.addEventListener) iframeId.addEventListener("load", eventHandler, true);
		    if (iframeId.attachEvent) iframeId.attachEvent("onload", eventHandler);
		 
		    // Set properties of form...
		    form.setAttribute("target", "upload_iframe");
		    form.setAttribute("action", 'addLayerChart');
		    form.setAttribute("method", "post");
		    form.setAttribute("enctype", "multipart/form-data");
		    form.setAttribute("encoding", "multipart/form-data");
		 
		    // Submit the form...
		    form.submit();
		    
		    $('#layerChartModal').modal({keyboard:false});
			/*
			$http({
				method : 'POST',
				url : $("#formUrl").text(),
				data : JSON.stringify($scope.user),
				headers : {
					'Content-Type' : 'application/json',
					"Accept" : "application/json"
				}
			}).success(function(data, status) {
				if (data) {
					$rootScope.user = data;
					showSuccessMessage();
				} else
					alert("Cannot update your information");
			}).error(handleHttpError);
			*/
		}
	};
});

layersApp.controller('EditLayerController', function ($scope, $modal, parameter) {
	$scope.layerId = parameter.getValue();
	
	$scope.checkAttributes = function(layer) {
		if (layer && layer.layerAttributes && layer.layerAttributes.length > 0) {
			$.each(layer.layerAttributes, function( index, layerAttribute ) {
				if (layerAttribute.allowedValues == "All") {
					$("input[data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', true);
				} else {
					$.each(layerAttribute.options, function( index, option ) {
						$("input[data-attrId='"+layerAttribute.attribute.id+"'][value='"+option+"']").prop('checked', true);
					});
					if (layerAttribute.options && layerAttribute.attribute.options && layerAttribute.options.length == layerAttribute.attribute.options) {
						$("input[data-allAttr='all'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', true);
					}
				}
			});
		}
	}
	
	$scope.removeAttribute = function(la) {
		var modalInstance = $modal.open({
			templateUrl : 'RemoveLayerAttributeTemplate',
			controller : 'RemoveLayerAttributeController',
			resolve : {
				layerAttribute : function() {
					return la;
				}
			}
		});
		modalInstance.result.then(function(status) {
			if(status && status == "removed") {
				var newLayerAttributes = [];
				$.each($scope.layer.layerAttributes, function( index1, layerAttribute ) {
					if(la.attribute.id == layerAttribute.attribute.id) {
						
					} else {
						newLayerAttributes.push(layerAttribute);
					}
				});
				$scope.layer.layerAttributes = newLayerAttributes; 
			}
		});
		
	};
	
	$scope.getLayer = function() {
		console.log($scope.layer);
		var url = "getLayerById?layerId="+$scope.layerId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.layer = data.output;
						} else {
						  showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	$scope.getLayer();
	$scope.back = function() {
		$scope.navController.pop();
	};

	$scope.deactivateLayer = function(layer) {
		var modalInstance = $modal.open({
			templateUrl : 'DeactivateLayerTemplate',
			controller : 'DeactivateLayerController',
			resolve : {
				layer : function() {
					return $scope.layer;
				}
			}
		});
		modalInstance.result.then(function(status) {
			if(status && status == "removed") {
				$scope.navController.pop("deleted");
			}
		});
	};
	
	$scope.addLayerChart = function() {
		parameter.setValue($scope.layer);
		$scope.navController.push("Add Layer Chart", function(status) {
			if(status && status == "added"){
				$scope.getLayer();
			}
		});
	};
	
	$scope.showAddAttributeScreen = function() {
		parameter.setValue(1);
		$scope.screen = $scope.navController.push("Add Attribute", function(result) {
			if (result) {
				if (result.editedAttributes && result.editedAttributes.length > 0) {
					if ($scope.layer.layerAttributes && $scope.layer.layerAttributes.length > 0) {
						$.each(result.editedAttributes, function( index, editedAttribute ) {
							$.each($scope.layer.layerAttributes, function( index1, layerAttribute ) {
								if (editedAttribute.id == layerAttribute.attribute.id) {
									layerAttribute.allowedValues = "All";
									layerAttribute.attribute = editedAttribute;
								}
							});
						});
					}
				}
				if (result.deletedAttributes && result.deletedAttributes.length > 0) {
					if ($scope.layer.layerAttributes && $scope.layer.layerAttributes.length > 0) {
						var newLayerAttributes = [];
						$.each($scope.layer.layerAttributes, function(index, layerAttribute) {
							var found = false;
							$.each(result.deletedAttributes, function(index1, deletedAttribute) {
								if (deletedAttribute.id == layerAttribute.attribute.id) {
									found = true;
								}
							});
							if (!found) {
								newLayerAttributes.push(layerAttribute);
							}
						});
						$scope.layer.layerAttributes = newLayerAttributes;
					}
				}
				if (result.selectedAttribute) {
					if (!$scope.layer.layerAttributes) {
						$scope.layer.layerAttributes = [];
					}
					var layerAttribute = {};
					layerAttribute.attribute = result.selectedAttribute;
					layerAttribute.allowedValues = "All";
					layerAttribute.mandatory = true;
					$scope.layer.layerAttributes.push(layerAttribute);
					$scope.checkAttributes($scope.layer);
				}
			}
		});
	};
	
	$scope.allOptionsClicked = function(layerAttribute) {
		layerAttribute.options = [];
		if($("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").is(':checked')) {
			layerAttribute.allowedValues = "All";
			$("input[data-attrId='"+layerAttribute.attribute.id+"']").each(function() {
				$(this).prop('checked', true);
			});
		} else {
			layerAttribute.allowedValues = "None";
			layerAttribute.options = [];
			$("input[data-attrId='"+layerAttribute.attribute.id+"']").each(function() {
				$(this).prop('checked', false);
			});
		}
	}
	
	$scope.attributeValueClicked = function(layerAttribute) {
		layerAttribute.options = [];
		var areAllOptionsChecked = true;
		var selectedValuesCount = 0;
		$("input[data-attrId='"+layerAttribute.attribute.id+"']").each(function() {
			if($(this).attr("data-allAttr") != "All") {
				if (!$(this).is(":checked")) {
					areAllOptionsChecked = false;
				} else {
					++selectedValuesCount;
					layerAttribute.options.push($(this).val());
				}
			}
		});
		if (areAllOptionsChecked) {
			$("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', true);
			layerAttribute.allowedValues = "All";
		} else if (selectedValuesCount > 0){
			$("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', false);
			layerAttribute.allowedValues = "Selected ("+selectedValuesCount+")";
		} else {
			$("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', false);
			layerAttribute.allowedValues = "None";
		}
		
	}
	
	
	
	$scope.updateLayer = function() {
		$scope.showValidationMessages = true;
		$scope.attributesInvalid = false;
		if($scope.layer.layerAttributes && $scope.layer.layerAttributes.length > 0) {
			$.each($scope.layer.layerAttributes, function( index, layerAttribute ) {
				if (!layerAttribute.allowedValues || layerAttribute.allowedValues == 'None') {
					$scope.attributesInvalid = true;
				}
			});
		}
		if (!$scope.editLayerForm.$invalid && !$scope.attributesInvalid ) {
			var url = "updateItem";
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.layer),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  showSuccessMessage("Success! Layer updated.");
						  $scope.navController.pop("edited");
					  } else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		}
	}
	
});


layersApp.controller('DeactivateLayerController', function ($scope, $modalInstance, layer, parameter) {
	$scope.layer = layer;
	$scope.deactivateLayer = function() {
		var url = "deactivateLayer?layerId="+$scope.layer.id;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  showSuccessMessage("Success! Layer deleted.!");
					  $modalInstance.close("removed");
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	
	$scope.close = function() {
		$modalInstance.dismiss();
	};
});

layersApp.controller('AddLayerController', function ($scope, $modal, parameter) {
	$scope.layer = {type:1};
	$scope.addLayer = function() {
		$scope.showValidationMessages = true;
		$scope.attributesInvalid = false;
		if($scope.layer.layerAttributes && $scope.layer.layerAttributes.length > 0) {
			$.each($scope.layer.layerAttributes, function( index, layerAttribute ) {
				if (!layerAttribute.allowedValues || layerAttribute.allowedValues == 'None') {
					$scope.attributesInvalid = true;
				}
			});
		}
		if (!$scope.addLayerForm.$invalid && !$scope.attributesInvalid ) {
			var url = "addItem";
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.layer),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  showSuccessMessage("Success! Layer Added successfully");
						  $scope.navController.pop(data.output);
					  } else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		}
	};
	
	$scope.removeAttribute = function(la) {
		var modalInstance = $modal.open({
			templateUrl : 'RemoveLayerAttributeTemplate',
			controller : 'RemoveLayerAttributeController',
			resolve : {
				layerAttribute : function() {
					return la;
				}
			}
		});
		modalInstance.result.then(function(status) {
			if(status && status == "removed") {
				var newLayerAttributes = [];
				$.each($scope.layer.layerAttributes, function( index1, layerAttribute ) {
					if(la.attribute.id == layerAttribute.attribute.id) {
						
					} else {
						newLayerAttributes.push(layerAttribute);
					}
				});
				$scope.layer.layerAttributes = newLayerAttributes; 
			}
		});
		
	};
	
	$scope.checkAttributes = function(layer) {
		if (layer && layer.layerAttributes && layer.layerAttributes.length > 0) {
			$.each(layer.layerAttributes, function( index, layerAttribute ) {
				if (layerAttribute.allowedValues == "All") {
					$("input[data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', true);
				} else {
					$.each(layerAttribute.options, function( index, option ) {
						$("input[data-attrId='"+layerAttribute.attribute.id+"'][value='"+option+"']").prop('checked', true);
					});
					if (layerAttribute.options && layerAttribute.attribute.options && layerAttribute.options.length == layerAttribute.attribute.options) {
						$("input[data-allAttr='all'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', true);
					}
				}
			});
		}
	};
	
	$scope.showAddAttributeScreen = function() {
		parameter.setValue(1);
		$scope.screen = $scope.navController.push("Add Attribute",  function(result) {
			if (result) {
				if (result.editedAttributes && result.editedAttributes.length > 0) {
					if ($scope.layer.layerAttributes && $scope.layer.layerAttributes.length > 0) {
						$.each(result.editedAttributes, function( index, editedAttribute ) {
							$.each($scope.layer.layerAttributes, function( index1, layerAttribute ) {
								if (editedAttribute.id == layerAttribute.attribute.id) {
									layerAttribute.allowedValues = "All";
									layerAttribute.attribute = editedAttribute;
								}
							});
						});
					}
				}
				if (result.deletedAttributes && result.deletedAttributes.length > 0) {
					if ($scope.layer.layerAttributes && $scope.layer.layerAttributes.length > 0) {
						var newLayerAttributes = [];
						$.each($scope.layer.layerAttributes, function(index, layerAttribute) {
							var found = false;
							$.each(result.deletedAttributes, function(index1, deletedAttribute) {
								if (deletedAttribute.id == layerAttribute.attribute.id) {
									found = true;
								}
							});
							if (!found) {
								newLayerAttributes.push(layerAttribute);
							}
						});
						$scope.layer.layerAttributes = newLayerAttributes;
					}
				}
				if (result.selectedAttribute) {
					if (!$scope.layer.layerAttributes) {
						$scope.layer.layerAttributes = [];
					}
					var layerAttribute = {};
					layerAttribute.attribute = result.selectedAttribute;
					layerAttribute.allowedValues = "All";
					layerAttribute.mandatory = true;
					$scope.layer.layerAttributes.push(layerAttribute);
					$scope.checkAttributes($scope.layer);
				}
			}
		});
	};
	
	$scope.allOptionsClicked = function(layerAttribute) {
		layerAttribute.options = [];
		if($("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").is(':checked')) {
			layerAttribute.allowedValues = "All";
			$("input[data-attrId='"+layerAttribute.attribute.id+"']").each(function() {
				$(this).prop('checked', true);
			});
		} else {
			layerAttribute.allowedValues = "None";
			layerAttribute.options = [];
			$("input[data-attrId='"+layerAttribute.attribute.id+"']").each(function() {
				$(this).prop('checked', false);
			});
		}
	}
	
	$scope.attributeValueClicked = function(layerAttribute) {
		layerAttribute.options = [];
		var areAllOptionsChecked = true;
		var selectedValuesCount = 0;
		$("input[data-attrId='"+layerAttribute.attribute.id+"']").each(function() {
			if($(this).attr("data-allAttr") != "All") {
				if (!$(this).is(":checked")) {
					areAllOptionsChecked = false;
				} else {
					++selectedValuesCount;
					layerAttribute.options.push($(this).val());
				}
			}
		});
		if (areAllOptionsChecked) {
			$("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', true);
			layerAttribute.allowedValues = "All";
		} else if (selectedValuesCount > 0){
			$("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', false);
			layerAttribute.allowedValues = "Selected ("+selectedValuesCount+")";
		} else {
			$("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', false);
			layerAttribute.allowedValues = "None";
		}
		
	}
});

layersApp.controller('EditAttributeController', function ($scope, parameter) {
	$scope.attribute = parameter.getValue();
	
	$scope.addOption = function() {
		$scope.attribute.options.push("");
	};
	
	$scope.updateAttribute = function(editAttributeForm) {
		$scope.showValidationMessages = true;
		var allOptionsEmpty = true;
		$.each($scope.attribute.options, function( index, option ) {
			if (!(!option || option == "")) {
				allOptionsEmpty = false;
			}
		});
		$scope.allOptionsEmpty= allOptionsEmpty;
		if (!editAttributeForm.$invalid && !$scope.allOptionsEmpty) {
			var url = "updateAttribute";
			//$scope.attribute.type = type;
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.attribute),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  showSuccessMessage("Success! Attribute updated.");
						  $scope.navController.pop("edited");
					  } else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		}
	};
});

layersApp.controller('AddAttributeController', function ($scope, $modal, parameter) {
	var result = {};
	result.editedAttributes = [];
	result.deletedAttributes = [];
	
	var type = parameter.getValue();
	$scope.attribute = {};
	$scope.attribute.type = type;
	$scope.getAttributes = function() {
		var url = "getAttributesByType?type="+type;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  $scope.attributes = data.output; 
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  //$scope.$parent.navController.pop();
			  }
			  
			});
	};
	
	$scope.gotoedit = function(attribute) {
		var selectedAttributeFound = false;
		$.each($scope.attributes, function( index, attribute ) {
			if (attribute.id == $scope.selectedAttributeId) {
				selectedAttributeFound = true;
				parameter.setValue(attribute);
			}
		});
		var selAttribute = parameter.getValue();
		$scope.navController.push("Edit Attribute", function(status) {
			if (status && status == "edited") {
				$scope.getAttributes();
				$.each($scope.attributes, function( index, attribute ) {
					if (attribute.id == selAttribute.id) {
						result.editedAttributes.push(attribute); 
					}
				});
			}
		});
	};
	
	$scope.deleteAttribute = function() {
		var attributeSel;
		$.each($scope.attributes, function( index, attribute ) {
			if (attribute.id == $scope.selectedAttributeId) {
				attributeSel = attribute;
			}
		});
		
		var modalInstance = $modal.open({
			templateUrl : 'DeleteAttributeTemplate',
			controller : 'DeleteAttributeController',
			resolve : {
				attribute : function() {
					return attributeSel;
				}
			}
		});
		modalInstance.result.then(function(status) {
			if(status && status == "removed") {
				$scope.getAttributes();
				result.deletedAttributes.push(attributeSel); 
			}
		});
	};
	
	$scope.backk = function() {
		$scope.navController.pop(result);
	};
	
	$scope.saveAttribute = function(addAttributeForm) {
		$scope.showValidationMessages = true;
		var allOptionsEmpty = true;
		$.each($scope.attribute.options, function( index, option ) {
			
			if (!(!option || option == "")) {
				allOptionsEmpty = false;
			}
		});
		$scope.allOptionsEmpty= allOptionsEmpty;
		if (!addAttributeForm.$invalid && !$scope.allOptionsEmpty) {
			var url = "addAttribute";
			$scope.attribute.type = type;
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.attribute),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  result.selectedAttribute = data.output;
						  $scope.navController.pop(result);
						  showSuccessMessage("Success! Attribute added successfully..");
						} else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		}
	};
	
	$scope.attribute = {};
	$scope.attribute.options = ["", "", ""];
	
	$scope.addOption = function() {
		$scope.attribute.options.push("");
	};
	$scope.back = function() {
		$scope.navController.pop();
	};
	
	$scope.markAttribute = function(attributeId) {
		$scope.selectedAttributeId = attributeId;
	}
	
	$scope.select = function() {
		$.each($scope.attributes, function( index, attribute ) {
			if (attribute.id == $scope.selectedAttributeId) {
				result.selectedAttribute = attribute;
				$scope.navController.pop(result);
			}
		});
		
	};
	$scope.getAttributes();
	
});


layersApp.controller('DeleteAttributeController', function ($scope, $modalInstance, attribute, parameter) {
	$scope.attribute = attribute;
	$scope.deleteAttribute = function() {
		var url = "deleteAttribute?attributeId="+$scope.attribute.id;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  showSuccessMessage("Success! Attribute deleted.!");
					  $modalInstance.close("removed");
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	
	$scope.close = function() {
		$modalInstance.dismiss();
	};
});


layersApp.controller('RemoveLayerAttributeController', function ($scope, $modalInstance, layerAttribute, parameter) {
	$scope.layerAttribute = layerAttribute;
	$scope.removeLayerAttribute = function() {
		$modalInstance.close("removed");
	};
	
	$scope.close = function() {
		$modalInstance.dismiss();
	};
});

layersApp.controller('DeleteLayerChartController', function ($scope, $modalInstance, layerAttributeConfig, parameter) {
	$scope.deleteLayerChart = function() {
		var url = "deleteLayerChart?layerAttributeConfigId="+layerAttributeConfig.id;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  showSuccessMessage("Success! Layer Chart deleted.!");
					  $modalInstance.close("removed");
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	
	$scope.close = function() {
		$modalInstance.dismiss();
	};
});














var recordTypesApp = angular.module('recordTypesApp', ['ui.bootstrap']);

recordTypesApp.factory('parameter', function() {
    var value;
    var parameterService = {};

    parameterService.setValue = function(item) {
        value = item;
    };
    parameterService.getValue = function() {
        return value;
    };

    return parameterService;
});

recordTypesApp.factory('dataService', function() {
	
	var layers;
	var structures;
	var structureItems;
	var structuresMap;
	var layersMap;
	var structureItemsMap;
	return {
		getLayers : function() {
			var url = "getItems?type=1";
			var success = true;
			var message;
			if (!layers) {
				$.ajax({
					  type: "POST",
					  url: url,
					  async: false,
					  //data: JSON.stringify($scope.layer),
					  contentType: "application/json",
					  dataType: "json",
					  success: function(data) {
						  if (data.success) {
							  if (data.output) {
								  layers = data.output;
							  } else {
								  //showErrorMessage("No layer found for your selection!");
							  }
						  } else {
							  //if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
							  success = false;
							  message = data.message;
						  }
					  },
					  error: function() {
						  //showErrorMessage("Sorry! An error has occured. Please try again or report an issue.");
						  success = false;
						  message = "Sorry! An error has occured. Please try again or report an issue.";
					  }
					  
					});
		  }
			return {message: message, output: layers, success : success};
	},
	
	getStructures : function() {
		var url = "getStructures";
		var success = true;
		var message;
		if (!structures) {
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  //data: JSON.stringify($scope.layer),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  structures = [];
						  if (data.output && data.output.length > 0) {
							  structures = data.output;
						  } else {
							  //showErrorMessage("No layer found for your selection!");
							}
					  } else {
						  //if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
						  success = false;
						  message = data.message;
					  }
				  },
				  error: function() {
					  //showErrorMessage("Sorry! An error has occured. Please try again or report an issue.");
					  success = false;
					  message = "Sorry! An error has occured. Please try again or report an issue."
				  }
				  
				});
		}
		return {message: message, output: structures, success : success};
	},
	
	getStructureItems : function() {
		var url = "getItems?type=2";
		var success = true;
		var message;
		if (!structureItems) {
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  //data: JSON.stringify($scope.layer),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  if (data.output) {
							  structureItems = data.output;
						  } else {
							  //showErrorMessage("No layer found for your selection!");
						  }
					  } else {
						  //if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
						  success = false;
						  message = data.message;
					  }
				  },
				  error: function() {
					  //showErrorMessage("Sorry! An error has occured. Please try again or report an issue.");
					  success = false;
					  message = "Sorry! An error has occured. Please try again or report an issue.";
				  }
				  
				});
	  }
		return {message: message, output: structureItems, success : success};
	},
	
	getLayersMap : function() {
		if (!layersMap) {
			var data = this.getLayers();
			if (data.success) {
				layersMap = {}
				$.each(data.output, function( index, layer ) {
					layersMap[layer.id] = layer;
				});
				return {success:true, output:layersMap};
			} else {
				return data;
			}
		} else {
			return {success:true, output:layersMap};
		}
	},
	
	getStructuresMap : function() {
		if (!structuresMap) {
			var data = this.getStructures();
			if (data.success) {
				structuresMap = {}
				$.each(data.output, function( index, structure ) {
					structuresMap[structure.id] = structure;
				});
				return {success:true, output:structuresMap};
			} else {
				return data;
			}
		} else {
			return {success:true, output:structuresMap};
		}
	},
	
	getStructureItemsMap : function() {
		if (!structureItemsMap) {
			var data = this.getStructureItems();
			if (data.success) {
				structureItemsMap = {}
				$.each(data.output, function( index, structureItem ) {
					structureItemsMap[structureItem.id] = structureItem;
				});
				return {success:true, output:structureItemsMap};
			} else {
				return data;
			}
		} else {
			return {success:true, output:structureItemsMap};
		}
	}
	
	
};
	
	
});
recordTypesApp.directive('mandatory', function ($compile) {
    return {
        restrict: 'A',
        scope: {'mandatory' : "="},
        link: function (scope,element, attrs) {
        	if (!element.attr('required') && scope.mandatory){
            element.attr("required", true);
            $compile(element)(scope.$parent);
          }
        }
    };
});

recordTypesApp.directive('minLength', function ($compile) {
    return {
        restrict: 'A',
        scope: {'minLength' : "="},
        link: function (scope,element, attrs) {
        	if (!element.attr('minlength') && scope.minLength && scope.minLength > 0){
            element.attr("minlength", scope.minLength);
            $compile(element)(scope.$parent);
          }
        }
    };
});

recordTypesApp.directive('maxLength', function ($compile) {
    return {
        restrict: 'A',
        scope: {'maxLength' : "="},
        link: function (scope,element, attrs) {
        	if (!element.attr('maxlength') && scope.maxLength && scope.maxLength > 0){
            element.attr("maxlength", scope.maxLength);
            $compile(element)(scope.$parent);
          }
        }
    };
});

recordTypesApp.controller('RecordTypesController', function ($scope, parameter) {
	$scope.selectedMenu = 'records';
	$scope.navController = new Navigation($scope, "Record Types");
	
	$scope.showNewRecordTypeScreen = function() {
		$scope.navController.push("New Record Type", function() {
			jQuery("#recordTypesList").trigger( 'reloadGrid' );
		});
	};
	
	
	$(document).ready(function() {
	    jQuery("#recordTypesList").jqGrid({
	        url:'getJqRecordTypes',
	    	datatype: "json",
	        width: '100%',
	        height: '100%',
	        autowidth:true,
	        scrollOffset: 0,
	        altRows:true,
	        altclass:'myAltRowClass',
	        colNames:['Id', 'Record Types', ''],
	        colModel:[
	            {name:'id',index:'id', hidden:true, align:"left"},
	            {name:'name',index:'name', align:"left"},
	            {name:'action',index:'action', align:"left", title:false, formatter: getTagCellContents}
	        ],
	        onSelectRow: function(id){
	        	var recordTypeId = $("#recordTypesList").jqGrid ('getCell', id, 'id');
	        	parameter.setValue(recordTypeId);
	        	$scope.$apply(function () {
	        		 $scope.navController.push("View Record Type", function(result) {
	        			 if (result == "deleted" || result == "edited") {
	        				 jQuery("#recordTypesList").trigger( 'reloadGrid' );
	        			 }
	        		 });
	        	});
	        }
	    });
	   });

	   function getTagCellContents (cellvalue, options, rowObject) {
	       console.log(rowObject);
	       if (rowObject.type == 1) {
	    	   return '<a class="viewRecordsLink" href="javascript:void(0)" data-recordTypeId="'+rowObject.id+'" style="color:blue">View Records</a>&nbsp;&nbsp;&nbsp;<a class="addRecordLink" href="javascript:void(0)" data-recordTypeId="'+rowObject.id+'" style="color:blue">Add record</a>&nbsp;&nbsp;&nbsp;<a class="viewBarchartsLink" href="javascript:void(0)" data-recordTypeId="'+rowObject.id+'" style="color:blue">Barcharts</a>';
	    	} else {
	    		return '<a class="viewRecordsLink" href="javascript:void(0)" data-recordTypeId="'+rowObject.id+'" style="color:blue">View Records</a>&nbsp;&nbsp;&nbsp;<a class="addRecordLink" href="javascript:void(0)" data-recordTypeId="'+rowObject.id+'" style="color:blue">Add record</a>';
	       }
		   
	   }
	   
	   $(document).on("click", "a.addRecordLink", function() {
		  var id = $(this).attr("data-recordTypeId");
		  $scope.$apply(function () {
			  parameter.setValue(id);
			  $scope.navController.push("Add Record", function(result) {
	  			 
			  });
		  });
	   });
	   
	   $(document).on("click", "a.viewRecordsLink", function() {
			  var id = $(this).attr("data-recordTypeId");
			  $scope.$apply(function () {
				  parameter.setValue(id);
				  $scope.navController.push("View Records", function(result) {
		  			 
				  });
			  });
		   });
	   
	   $(document).on("click", "a.viewBarchartsLink", function() {
			  var id = $(this).attr("data-recordTypeId");
			  $scope.$apply(function () {
				  parameter.setValue(id);
				  $scope.navController.push("Barcharts", function(result) {
		  			 
				  });
			  });
		   });
});



recordTypesApp.controller('ViewRecordTypeController', function ($scope, parameter) {
	$scope.recordTypeId = parameter.getValue();
	
    $scope.getRecordType = function() {
		var url = "getRecordTypeById?recordTypeId="+$scope.recordTypeId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.recordType = data.output;
						  checkAndAddRecordTypeLayerAttributes($scope.recordType);
					  } else {
						  showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	$scope.getRecordType();
	$scope.edited = false;
	$scope.editRecordType = function() {
		parameter.setValue($scope.recordTypeId);
		$scope.navController.push("Edit Record Type", function(result) {
				if(result == "deleted"){
					$scope.navController.pop("deleted");
				} else if (result == "edited") {
					$scope.edited = true;
					$scope.getRecordType();
				}
			});
	};
	
	$scope.back = function() {
		if ($scope.edited) {
			$scope.navController.pop("edited");
		} else {
			$scope.navController.pop();
		}
	};
});

recordTypesApp.controller('BarchartController', function ($scope, parameter) {
	$scope.recordTypeId = parameter.getValue();
	
	$scope.getRecordType = function() {
		var url = "getRecordTypeById?recordTypeId="+$scope.recordTypeId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.recordType = data.output;
					  } else {
						  showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	$scope.getRecordType();
	
	$(document).ready(function() {
	    jQuery("#barchartList").jqGrid({
	        url:'getJqBarcharts?recordTypeId='+$scope.recordTypeId,
	    	datatype: "json",
	        width: '100%',
	        height: '100%',
	        autowidth:true,
	        scrollOffset: 0,
	        altRows:true,
	        altclass:'myAltRowClass',
	        colNames:['Id', 'Name', ''],
	        colModel:[
	            {name:'id',index:'id', hidden:true, align:"left"},
	            {name:'name',index:'name', align:"left"},
	            {name:'action',index:'action', align:"left", title:false, formatter: getTagCellContents}
	        ],
	        onSelectRow: function(id){
	        	var barchartId = $("#barchartList").jqGrid ('getCell', id, 'id');
	        	parameter.setValue({barchartId : barchartId, recordType : $scope.recordType});
	        	$scope.$apply(function () {
	        		 $scope.navController.push("View Barchart", function(result) {
	        			 
	        		 });
	        	});
	        }
	    });
	    
	    
	   });
	
	function getTagCellContents (cellvalue, options, rowObject) {
	       return '<a class="executeBarchartLink" href="javascript:void(0)" data-barchartId="'+rowObject.id+'" style="color:blue">Execute</a>';
	}
	
	$(document).on("click", "a.executeBarchartLink", function() {
		  var id = $(this).attr("data-barchartId");
		  $scope.$apply(function () {
			  parameter.setValue({barchartId : id, recordType : $scope.recordType});
			  $scope.navController.push("Execute Barchart", function(result) {
	  			 
			  });
		  });
	   });
	
	$scope.getBarcharts = function() {
		var url = "getBarchartsByRecordTypeId?recordTypeId="+$scope.recordType.id;
		$.ajax({
			  type: "GET",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.recordType = data.output;
					  } else {
						  showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	
	$scope.createBarchart = function() {
		parameter.setValue($scope.recordType);
		$scope.navController.push("Create Barchart", function() {
			jQuery("#barchartList").trigger( 'reloadGrid' );
		});
	}
});

recordTypesApp.controller('CreateBarchartController', function ($scope, $http, parameter) {
	$scope.barchart = {};
	$scope.barchart.recordType = parameter.getValue();
	$scope.barchart.layerAttributeConfigs = [];
	$scope.addLayer = function() {
		$scope.navController.push("Select Layer", function(layer) {
			if (layer) {
				var layerAttributeConfig = layer.layerAttributeConfig;
				layer.layerAttributeConfig = undefined;
				layerAttributeConfig.layer = layer;
				if (layer.layerAttributes && layer.layerAttributes.length > 0) {
					var layerAttributeMap = {};
					$.each(layer.layerAttributes, function(index, layerAttribute) {
						layerAttributeMap[layerAttribute.attribute.id] = layerAttribute.attribute;
					});
					layer.layerAttributeMap = layerAttributeMap;
				}
				$scope.barchart.layerAttributeConfigs.push(layerAttributeConfig);
				
			}
		});
	};
	
	$scope.createBarchart = function() {
		var url = "createBarchart";
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  data: JSON.stringify($scope.barchart),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  showSuccessMessage("Success! Barchart created successfully.");
					  $scope.navController.pop("created");
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	
});

recordTypesApp.controller('ViewBarchartController', function ($scope, $http, dataService, parameter) {
	var obj = parameter.getValue();
	$scope.barchartId = obj.barchartId;
	
	var layersMapData = dataService.getLayersMap();
	if (layersMapData.success) {
		$scope.layersMap = layersMapData.output; 
	}
	
	$scope.getBarchartInfo = function() {
		console.log($scope.layer);
		var url = "getBarchartInfoById?barchartId="+$scope.barchartId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.barchart = data.output;
						  $scope.barchart.recordType = obj.recordType;
					  } else {
						  showErrorMessage("No barchart found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	$scope.getBarchartInfo();
	
});

recordTypesApp.controller('ExecuteBarchartController', function ($scope, $http, dataService, parameter) {
	var obj = parameter.getValue();
	$scope.barchartId = obj.barchartId;
	
	$scope.from = 0;
	$scope.interval = 10;
	$scope.noOfBlocks = 50;
	
	$scope.initialize = function() {
		$('.carousel').carousel({
            interval:false // remove interval for manual sliding
        });
    }
	
	var layersMapData = dataService.getLayersMap();
	if (layersMapData.success) {
		$scope.layersMap = layersMapData.output; 
	}
	
	$scope.getBarchartInfo = function() {
		console.log($scope.layer);
		var url = "getBarchartInfoById?barchartId="+$scope.barchartId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.barchart = data.output;
						  $scope.barchart.recordType = obj.recordType;
					  } else {
						  showErrorMessage("No barchart found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	$scope.getBarchartInfo();
	
	$scope.refreshChart = function() {
		$(".item.layer-chart-div.active").html("Loading. Please wait..");
		$scope.getBarchartData($(".item.layer-chart-div.active"),$scope.from, $scope.interval, $scope.noOfBlocks);
	}
	$scope.nextLayerChart = function() {
		var length = +$scope.interval *  +$scope.noOfBlocks;
		$scope.from = +$scope.from + length; 
		$(".item.layer-chart-div:not(.active)").html("Loading. Please wait..");
		$('.carousel').carousel('next');
		$scope.getBarchartData($(".item.layer-chart-div:not(.active)"),$scope.from, $scope.interval, $scope.noOfBlocks);
	};
	
	$scope.prevLayerChart = function() {
		var length = +$scope.interval *  +$scope.noOfBlocks;
		$scope.from = +$scope.from - length;
		$(".item.layer-chart-div:not(.active)").html("Loading. Please wait..");
		$('.carousel').carousel('prev');
		$scope.getBarchartData($(".item.layer-chart-div:not(.active)"),$scope.from, $scope.interval, $scope.noOfBlocks);
	};
	
	$scope.getBarchartData = function(obj, from, interval, noOfBlocks) {
		console.log($scope.layer);
		var to = from + (interval * noOfBlocks);
		var url = "getBarchartDataByFromByTo?from="+from+"&to="+to+"&barchartId="+$scope.barchartId;
		$.ajax({
			  type: "GET",
			  url: url,
			  async: true,
			  //data: JSON.stringify($scope.barchart),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  obj.barchart({from:+from, noOfBlocks: +noOfBlocks, interval:+interval, barchart: $scope.barchart, data : data.output, layersMap: $scope.layersMap});
					  } else {
						  showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	$scope.getBarchartData($(".item.layer-chart-div.active"),$scope.from, $scope.interval, $scope.noOfBlocks);
});

recordTypesApp.controller('SelectLayerController', function ($scope, $http, parameter) {
	$scope.getLayer = function(layerId) {
		var layer;
		var url = "getLayerById?layerId="+layerId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  layer = data.output;
						  layer.layerAttributeConfig = {};
						  layer.layerAttributeConfig.attributeValueMap = {};
					  } else {
						  showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
		return layer;
	}
	$scope.getLayers = function(val) {
	    return $http.get('getLayersByPartialName', {
	      params: {
	        name: val
	      }
	    }).then(function(result){
	      var layers = [];
	      angular.forEach(result.data.output, function(layer){
	        layers.push(layer);
	      });
	      return layers;
	    });
	  };
	$scope.selectLayer = function($item, $model, $label) {
		$scope.layer = $scope.getLayer($item.id);
	};
	
	$scope.select = function() {
		$scope.showValidationMessages = true;
		if ($scope.layer) {
			if (!$scope.selectLayerForm.$invalid) {
				$scope.navController.pop($scope.layer);
			}
		} else {
			alert("Please select a layer first");
		}
	};
	
});

recordTypesApp.controller('CreateRecordLayoutController', function ($scope, parameter) {
	$scope.recordType = parameter.getValue();
	console.log($scope.recordType);
	$scope.initialize = function() {
		
	        $( ".rfi-report .report-widget" ).draggable({ containment: "parent", snap: true, snapTolerance: 5 }).resizable({
	            containment: "parent"
	        });
	        /*
	        tinymce.init({
	            selector: "textarea",
	            setup : function(ed) {
	                ed.on("change", function(ed) {
	                    $(".report-widget").html(tinymce.activeEditor.getContent());
	                });
	            }
	        });
	        */
	        //CKEDITOR.replace( 'widget-text-editor' );
	        //setTimeout(function(){$('#slidecontent').hide();},0);
	        
	        $( "ul.report-component > li").draggable({
	            cancel: "a.ui-icon", // clicking an icon won't initiate dragging
	            revert: "invalid", // when not dropped, the item will revert back to its initial position
	            containment: "document",
	            helper: "clone",
	            cursor: "move"
	        });

	        $(".rfi-report").droppable({
	            accept: "ul.report-component > li",
	            activeClass: "ui-state-highlight",
	            drop: function( event, ui ) {
	                //alert( ui.draggable );
	                $( '<div class="report-widget draggable ui-widget-content">&nbsp</div>' ).appendTo( this).draggable({ containment: "parent" }).resizable({
	                    containment: "parent"
	                });;
	            }
	        });

	        $(".report-widget").click(function () {
	            // Set the effect type
	            var effect = 'slide';

	            // Set the options for the effect type chosen
	            var options = { direction: 'right' };

	            // Set the duration (default: 400 milliseconds)
	            var duration = 700;
	            //var height = $('#slidecontent').height();
	            //alert(height);
	            //if (height == 0) {
	            //	$('#slidecontent').hide();
	            //	$('#slidecontent').height(100);
	            //}
	            //console.log(CKEDITOR.instances['widget-text-editor' ]);
	            //if (CKEDITOR.instances['widget-text-editor' ]) CKEDITOR.instances['widget-text-editor' ].destroy();
	            if( $('#slidecontent').css('display') == 'block') {
	            	if (CKEDITOR.instances['widget-text-editor' ]) CKEDITOR.instances['widget-text-editor' ].destroy();
	            } else {
	            	setTimeout(function(){
	            		
	            		CKEDITOR.replace( 'widget-text-editor' );
	            		CKEDITOR.instances['widget-text-editor'].on('change', function() {$(".report-widget").html(CKEDITOR.instances['widget-text-editor'].getData());});
	            	},2000);
	            }
	            $('#slidecontent').toggle(effect, options, duration);
	            
	            
	            
	            //$("#slidecontent").slideToggle({queue: false, duration: 500});
	        });
	    
	}
});

recordTypesApp.controller('EditRecordTypeController', function ($scope, parameter) {
	
	$scope.getRecordType = function() {
		var url = "getRecordTypeById?recordTypeId="+$scope.recordTypeId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.recordType = data.output;
						} else {
						  showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	$scope.getRecordType();
	if (!$scope.recordType.customAttributes) {
		$scope.recordType.customAttributes = [];
	}
	if (!$scope.recordType.layerAttributes) {
		$scope.recordType.layerAttributes = [];
	}
	
	$scope.showCreateRecordLayoutScreen = function() {
		parameter.setValue($scope.recordType);
		$scope.navController.push("Create Record Layout", function() {
			
		});
	};
	
	$scope.updateRecordType = function() {
		console.log($scope.record);
		var url = "updateRecordType";
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  data: JSON.stringify($scope.recordType),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  $scope.navController.pop("edited");
					  showSuccessMessage("Success! Record type updated successfully..");
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	
	$scope.getSerials = function() {
		console.log($scope.layer);
		var url = "getSerials";
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.serials = data.output;
					  } 
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	$scope.getSerials();
	
	$scope.createNewSerial = function() {
		$scope.screen = $scope.navController.push("serial", function(serial) {
			if (serial) {
				$scope.getSerials();
			}
		});
	};
	
	$scope.showAddAttributeScreen = function() {
		parameter.setValue();
		$scope.screen = $scope.navController.push("Record Type Attribute", function(recordTypeAttribute) {
			if (recordTypeAttribute) {
				if (!$scope.recordType.customAttributes) {
					$scope.recordType.customAttributes = [];
				}
				$scope.recordType.customAttributes.push(recordTypeAttribute);
			}
		});
	};
	
	$scope.editRecordTypeAttribute = function(recordTypeAttribute) {
		parameter.setValue({recordTypeAttribute: angular.copy(recordTypeAttribute)});
		$scope.screen = $scope.navController.push("Record Type Attribute", function(editedRecordTypeAttribute) {
			if (editedRecordTypeAttribute) {
				for (var key in editedRecordTypeAttribute) {
					if (editedRecordTypeAttribute.hasOwnProperty(key)) {
						recordTypeAttribute[key] = editedRecordTypeAttribute[key];
					}
				}
				
			}
		});
	};
	
	$scope.deleteRecordTypeAttribute = function(recordTypeAttribute) {
		var newCustomAttributes = [];
		if ($scope.recordType.customAttributes) {
			$.each($scope.recordType.customAttributes, function( index, r ) {
				if (recordTypeAttribute.name != r.name) {
					newCustomAttributes.push(r);
				}
			});
			$scope.recordType.customAttributes = newCustomAttributes;
		}
	};
	
	$scope.changeType = function() {
		var layerFromAttribute = undefined;
		var layerToAttribute = undefined;
		var levelAttribute = undefined;
		if ($scope.recordType.type == 1) {
			if ($scope.recordType.layerAttributes) {
				$.each($scope.recordType.layerAttributes, function( index, layerAttribute ) {
					if (layerAttribute && layerAttribute.type == 6) {
						layerFromAttribute = layerAttribute;
					} else if (layerAttribute && layerAttribute.type == 7) {
						layerToAttribute = layerAttribute;
					} else if (layerAttribute && layerAttribute.type == 8) {
						levelAttribute = layerAttribute;
					}
				});
			}
			if (!layerFromAttribute) {
				layerFromAttribute = {name:"From", type:6};
				$scope.recordType.layerAttributes.push(layerFromAttribute);
			}
			if (!layerToAttribute) {
				layerToAttribute = {name:"To", type:7};
				$scope.recordType.layerAttributes.push(layerToAttribute);
			}
			if (!levelAttribute) {
				levelAttribute = {name:"Level", type:8};
				$scope.recordType.layerAttributes.push(levelAttribute);
			}
		}
		
	};
	
	
	
	$scope.showSpinner = function() {
		$( ".spinner" ).spinner();
	}
});



recordTypesApp.controller('AddRecordTypeController', function ($scope, parameter) {
	$scope.recordType = {};
	$scope.recordType.customAttributes = [];
	$scope.recordType.layerAttributes = [];
	$scope.addRecordType = function() {
		$scope.showValidationMessages = true;
		if (!$scope.addRecordTypeForm.$invalid && $scope.recordType.type && !isEmpty($scope.recordType.type)) {
			var url = "addRecordType";
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.recordType),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  $scope.navController.pop(data.output);
					  } else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		}
	}
	
	$scope.getSerials = function() {
		console.log($scope.layer);
		var url = "getSerials";
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.serials = data.output;
					  } 
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	$scope.getSerials();
	
	$scope.back = function() {
		$scope.navController.pop();
	};
	
	$scope.createNewSerial = function() {
		$scope.screen = $scope.navController.push("serial", function(serial) {
			if (serial) {
				$scope.getSerials();
			}
		});
	};
	
	$scope.showAddAttributeScreen = function() {
		parameter.setValue();
		$scope.screen = $scope.navController.push("Record Type Attribute", function(recordTypeAttribute) {
			if (recordTypeAttribute) {
				if (!$scope.recordType.customAttributes) {
					$scope.recordType.customAttributes = [];
				}
				$scope.recordType.customAttributes.push(recordTypeAttribute);
			}
		});
	};
	
	$scope.editRecordTypeAttribute = function(recordTypeAttribute) {
		parameter.setValue({recordTypeAttribute: angular.copy(recordTypeAttribute)});
		$scope.screen = $scope.navController.push("Record Type Attribute", function(editedRecordTypeAttribute) {
			if (editedRecordTypeAttribute) {
				for (var key in editedRecordTypeAttribute) {
					if (editedRecordTypeAttribute.hasOwnProperty(key)) {
						recordTypeAttribute[key] = editedRecordTypeAttribute[key];
					}
				}
				
			}
		});
	};
	

	$scope.deleteRecordTypeAttribute = function(recordTypeAttribute) {
		var newCustomAttributes = [];
		if ($scope.recordType.customAttributes) {
			$.each($scope.recordType.customAttributes, function( index, r ) {
				if (recordTypeAttribute.name != r.name) {
					newCustomAttributes.push(r);
				}
			});
			$scope.recordType.customAttributes = newCustomAttributes;
		}
	};
	
	$scope.changeType = function() {
		checkAndAddRecordTypeLayerAttributes($scope.recordType);
	};
	
	
	
	$scope.showSpinner = function() {
		$( ".spinner" ).spinner();
	}
});

recordTypesApp.controller('SerialController', function ($scope, parameter) {
	$scope.serial = {};
	$scope.addSerial = function() {
		var url = "addSerial";
		console.log($scope.serial);
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  data: JSON.stringify($scope.serial),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  $scope.navController.pop(data.output);
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	
	$scope.getSerials = function() {
		console.log($scope.layer);
		var url = "getSerials";
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.serials = data.output;
					  } 
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	$scope.getSerials();
});

recordTypesApp.controller('RecordTypeAttributeController', function ($scope, parameter) {
	
	if (parameter.getValue() && parameter.getValue().recordTypeAttribute) {
		$scope.action = "edit";
		$scope.recordTypeAttribute = parameter.getValue().recordTypeAttribute;
		if (!$scope.recordTypeAttribute.options) {
			$scope.recordTypeAttribute.options = ["", "", ""];
		}
	} else {
		$scope.action = "add";
		$scope.recordTypeAttribute = {};
		$scope.recordTypeAttribute.options = ["", "", ""];
	}
	$scope.saveAttribute = function() {
		$scope.showValidationMessages = true;
		if (!$scope.recordTypeAttributeForm.$invalid) {
			$scope.navController.pop($scope.recordTypeAttribute);
		}
	};
	
	$scope.addOption = function() {
		$scope.recordTypeAttribute.options.push("");
	};
	$scope.back = function() {
		$scope.navController.pop();
	};
	
	$scope.markAttribute = function(attributeId) {
		$scope.selectedAttributeId = attributeId;
	}
});


recordTypesApp.controller('ViewRecordController', function ($scope, $http, dataService, parameter) {
	var object = parameter.getValue();
	$scope.recordType = object.recordType;
	$scope.recordId = object.record.id;
	
	$scope.getLayer = function() {
		var url = "getLayerById?layerId="+$scope.record.layerAttributeConfig.layer.id;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.record.layerAttributeConfig.layer = data.output;
					  } else {
						  showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	
	$scope.getRecord = function() {
		var url = "getRecordById?recordId="+$scope.recordId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.record = data.output;
						  if (!$scope.record.attributeValuesMap) {
							  $scope.record.attributeValuesMap = {};
						  }
						  if ($scope.record.recordAttributes && $scope.record.recordAttributes.length > 0) {
							  $.each($scope.record.recordAttributes, function(i, recordAttribute) {
								  $scope.record.attributeValuesMap[recordAttribute.recordTypeAttributeId] = recordAttribute.value;
							  });
					  	  }
						  if ($scope.record.layerAttributeConfig && $scope.record.layerAttributeConfig.layer && $scope.record.layerAttributeConfig.layer.id) {
								$scope.getLayer();
						  }
						} else {
						  showErrorMessage("No record found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	$scope.getRecord();
	
	if ($scope.recordType.id == 1) {
		var layersMapData = dataService.getLayersMap();
		if (layersMapData.success) {
			$scope.layersMap = layersMapData.output; 
		}
	} else if ($scope.recordType.id == 2) {
		var structuresMapData = dataService.getStructuresMap();
		if (structuresMapData.success) {
			$scope.structuresMap = structuresMapData.output; 
		}
	
		var structureItemsMapData = dataService.getStructureItemsMap();
		if (structureItemsMapData.success) {
			$scope.structureItemsMap = structureItemsMapData.output; 
		}
	}
	
	
	
	var edited = false;
	$scope.editRecord = function() {
		parameter.setValue({recordType: $scope.recordType, record: angular.copy($scope.record)});
		$scope.navController.push("Edit Record", function(status) {
			if (status && status == "edited") {
				edited=true;
				$scope.getRecord();
			}
		});
	};
	
	$scope.back = function() {
		if (edited) {
			$scope.navController.pop("edited");
		} else {
			$scope.navController.pop();
		}
	};
	
});











recordTypesApp.controller('ViewRecordsController', function ($scope, $http, dataService, parameter) {
	$scope.recordTypeId = parameter.getValue();
	$scope.showSearch = false;
	$scope.search = {};
	$scope.search.conditions = [];
	$scope.search.conditions.push({});
	
    

	$scope.toggleSearch = function() {
		if ($scope.showSearch) {
			$scope.showSearch = false;
		} else {
			$scope.showSearch = true;
		}
	};
	
	$scope.resetSearch = function() {
		$scope.search = {};
		$scope.search.conditions = [];
		$scope.search.conditions.push({});
	};
	
	$scope.addCondition = function() {
		$scope.search.conditions.push({});
	};
	
	$scope.removeCondition = function(index) {
		if (index == 0 && $scope.search.conditions.length == 1) {
			
		} else {
			$scope.search.conditions.splice(index, 1);
		}
	};
	
	$scope.changeConditionColumn = function(index) {
		var condition = $scope.search.conditions[index];
		condition.columntype = 0;
		condition.options = [];
		var col = condition.column;
		if (col && col.substring(0,2) == 'l_') {
			condition.columntype = 1;
			var layerAttributeId = col.split('l_')[1];
			$.each($scope.grid.layerAttributes, function(i, layerAttribute) {
				if (layerAttributeId == layerAttribute.id) {
					condition.options = [];
					if (layerAttribute.options) {
						$.each(layerAttribute.options, function(i, option) {
							condition.options.push({name:option, value:option});
						});
					}
				}
			});
		} else if (col && col == 'layerId') {
			condition.columntype = 9;
			if (!$scope.layers) {
				$scope.layers = [];
				var url = "getLayers";
				$.ajax({
					  type: "POST",
					  url: url,
					  async: false,
					  //data: JSON.stringify($scope.layer),
					  contentType: "application/json",
					  dataType: "json",
					  success: function(data) {
						  if (data.success) {
							  if (data.output) {
								  $scope.layers = data.output;
							  } else {
								  //showErrorMessage("No layer found for your selection!");
							  }
						  } else {
							  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
						  }
					  },
					  error: function() {
						  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
					  }
					  
					});
			};
			condition.options = [];
			if ($scope.layers && $scope.layers.length > 0) {
				$.each($scope.layers, function(i, layer) {
					condition.options.push({name:layer.name, value:layer.id});
				});
			}
		} else if (col && col == 'structureId') {
			condition.columntype = 10;
			if (!$scope.structures) {
				var url = "getStructures";
				$.ajax({
					  type: "POST",
					  url: url,
					  async: false,
					  //data: JSON.stringify($scope.layer),
					  contentType: "application/json",
					  dataType: "json",
					  success: function(data) {
						  if (data.success) {
							  if (data.output) {
								  $scope.structures = data.output;
							  } else {
								  //showErrorMessage("No layer found for your selection!");
							  }
						  } else {
							  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
						  }
					  },
					  error: function() {
						  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
					  }
					  
					});
			};
			condition.options = [];
			if ($scope.structures && $scope.structures.length > 0) {
				$.each($scope.structures, function(i, structure) {
					condition.options.push({name:structure.name, value:structure.id});
				});
			}
		} else if (col && col == 'serial') {
			condition.columntype = 4;
		} else if (col && col == 'structureItemId') {
			condition.columntype = 11;
			if (!$scope.structureItems) {
				$scope.structureItems = [];
				var url = "getItems?type=2";
				$.ajax({
					  type: "POST",
					  url: url,
					  async: false,
					  //data: JSON.stringify($scope.layer),
					  contentType: "application/json",
					  dataType: "json",
					  success: function(data) {
						  if (data.success) {
							  if (data.output) {
								  $scope.structureItems = data.output;
							  } else {
								  //showErrorMessage("No layer found for your selection!");
							  }
						  } else {
							  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
						  }
					  },
					  error: function() {
						  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
					  }
					  
					});
			};
			condition.options = [];
			if ($scope.structureItems && $scope.structureItems.length > 0) {
				$.each($scope.structureItems, function(i, structureItem) {
					condition.options.push({name:structureItem.name, value:structureItem.id});
				});
			}
		} else if (col && col.substring(0,3) == 'rl_') {
			condition.columntype = col.split('rl_')[1];
		} else if (col && col.substring(0,3) == 'rc_') {
			var recordTypeAttributeId = col.split('rc_')[1];
			$.each($scope.recordType.customAttributes, function(i, customAttribute ) {
				if (recordTypeAttributeId == customAttribute.id) {
					condition.columntype = customAttribute.type;
					if (condition.columntype == 1) {
						condition.options = [];
						if (customAttribute.options) {
							$.each(customAttribute.options, function(i, option) {
								condition.options.push({name:option, value:option});
							});
						}
					}
				}
			});
		}
	};
	
	$scope.getRecordType = function() {
		var url = "getRecordTypeById?recordTypeId="+$scope.recordTypeId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.recordType = data.output;
						  checkAndAddRecordTypeLayerAttributes($scope.recordType);
						  $scope.configureSearchColumns($scope.recordType);
					  } else {
						  showErrorMessage("No record type found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	
	$scope.configureSearchColumns = function(recordType) {
		$scope.searchColumns = [];
		$scope.searchColumns[0] = [];
		$scope.searchColumns[1] = [];
		$scope.searchColumns[2] = [];
		$.each(recordType.layerAttributes, function( index, layerAttribute ) {
			var i = index % 3;
			$scope.searchColumns[i].push({name:layerAttribute.name, value:"rl+"+layerAttribute.id});
		});
		$.each(recordType.customAttributes, function( index, customAttribute ) {
			var i = index % 3;
			$scope.searchColumns[i].push({name:customAttribute.name, value:"rc+"+customAttribute.id});
		});
	}
	
	$scope.getRecordType();
	
	$scope.searchRecords = function() {
		$scope.getRecordsGrid();
		$scope.showSearch = false;
	};
	
	var structuresMapData = dataService.getStructuresMap();
	if (structuresMapData.success) {
		$scope.structuresMap = structuresMapData.output; 
	}
	var layersMapData = dataService.getLayersMap();
	if (layersMapData.success) {
		$scope.layersMap = layersMapData.output; 
	}
	var structureItemsMapData = dataService.getStructureItemsMap();
	if (structureItemsMapData.success) {
		$scope.structureItemsMap = structureItemsMapData.output; 
	}
	
	$scope.showRecord = function(record) {
		parameter.setValue({recordType: $scope.recordType, record: record});
		$scope.navController.push("View Record", function(status) {
			if (status && status == "edited") {
				$scope.getRecordsGrid($scope.currentPage);
			}
		});
	};
	
	$scope.getRecordsGrid = function(page) {
			console.log($scope.layer);
			var url = "getRecordsGrid?recordTypeId="+$scope.recordTypeId;
			if (page) {
				url += "&pageNumber="+page;
			}
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.search),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  if (data.output) {
							  //convert record attributes to key value
							  var recordAttributes = data.output.recordAttributes;
							  var records = data.output.records;
							  var recordsMap = {};
							  var layerAttributeConfigMap = {};
							  if (records && records.length > 0) {
								  $.each(records, function( index, record) {
									  recordsMap[record.id] = record;
									  record.attributeValuesMap = {};
									  record.layerAttributes = {};
								  });
								  if (recordAttributes && recordAttributes.length > 0) {
									  $.each(recordAttributes, function( index, recordAttribute ) {
											recordsMap[recordAttribute.recordId].attributeValuesMap[recordAttribute.recordTypeAttributeId] = recordAttribute.value;
									  });
								  }
								  if (data.output.layerAttributeConfigs && data.output.layerAttributeConfigs.length > 0) {
									  $.each(data.output.layerAttributeConfigs, function( index, layerAttributeConfig ) {
										  layerAttributeConfigMap[layerAttributeConfig.id] = layerAttributeConfig;
									  });
								  }
								  $.each(records, function( index, record) {
									  record.layerAttributeConfig = layerAttributeConfigMap[record.layerAttributeConfig.id];
								  });
							  }
							  $scope.grid = data.output;
							  if ($scope.grid) {
								  $scope.currentPage = $scope.grid.currentPage;
								  $scope.totalRecords = $scope.grid.totalRecords;
							  }
							  
						  } else {
							  showErrorMessage("No layer found for your selection!");
						  }
					  } else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		};
		
		$scope.getRecordsGrid();
		
		$scope.changePage = function(page) {
			$scope.getRecordsGrid(page);
		}
	  
});

recordTypesApp.controller('EditRecordController', function ($scope, $http, dataService, parameter) {
	var object = parameter.getValue();
	$scope.record = object.record;
	$scope.record.recordType = object.recordType;
	
	
	
	if ($scope.record.recordType.type == 1) {
		  var layersData = dataService.getLayers();
			if (layersData.success) {
				$scope.layers = layersData.output; 
			}
			
			var layersMapData = dataService.getLayersMap();
			if (layersMapData.success) {
				$scope.layersMap = layersMapData.output; 
			}
	  } else if ($scope.record.recordType.type == 2) {
		  	var structuresData = dataService.getStructures();
			if (structuresData.success) {
				$scope.structures = structuresData.output; 
			}
			var structureItemsData = dataService.getStructureItems();
			if (structureItemsData.success) {
				$scope.structureItems = structureItemsData.output; 
			}
			var structuresMapData = dataService.getStructuresMap();
			if (structuresMapData.success) {
				$scope.structuresMap = structuresMapData.output; 
			}
			
			var structureItemsMapData = dataService.getStructureItemsMap();
			if (structureItemsMapData.success) {
				$scope.structureItemsMap = structureItemsMapData.output; 
			}
	  } 
	
	$scope.getLayer = function() {
		var layer;
		var url = "getLayerById?layerId="+$scope.record.layerAttributeConfig.layer.id;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  layer = data.output;
					  } else {
						  showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
		return layer;
	}
	//$scope.getLayer();
	
	$scope.selectLayer = function() {
		if ($scope.record.layerAttributeConfig && $scope.record.layerAttributeConfig.layer && $scope.record.layerAttributeConfig.layer.id && $scope.record.layerAttributeConfig.layer.id != '') {
			var layer = $scope.getLayer($scope.record.layerAttributeConfig.layer.id);
		}
		$scope.record.layerAttributeConfig = {};
		$scope.record.layerAttributeConfig.attributeValueMap = {};
		$scope.record.layerAttributeConfig.layer =  layer;
		
	};
	
	$scope.validateRecordTypeAtt = function(recordTypeAttribute) {
		if (recordTypeAttribute && recordTypeAttribute.validations && recordTypeAttribute.validations) {
			var value;
			var type = "layer";
			if (recordTypeAttribute.type == 6) {
				value = $scope.record.from;
			} else if (recordTypeAttribute.type == 7) {
				value = $scope.record.to;
			} else if (recordTypeAttribute.type == 8) {
				value = $scope.record.level;
			} else {
				var type = "custom";
				if ($scope.record.attributeValuesMap) {
					value = $scope.record.attributeValuesMap[recordTypeAttribute.id];
				}
			}
			if (recordTypeAttribute.validations.minLength && recordTypeAttribute.validations.minLength > 0) {
				if (value) {
					var strValue = (value + "").trim();
					if (value.length < recordTypeAttribute.validations.minLength) {
							if (type == "layer") {
								$scope.recordTypeLayerErrors[recordTypeAttribute.type] = "Minimum length "+recordTypeAttribute.validations.minLength+" characters";
							} else {
								$scope.recordTypeCustomErrors[recordTypeAttribute.id] = "Minimum length "+recordTypeAttribute.validations.minLength+" characters";
							}
						errorsFound = true;
					}
				}
			}
			if (recordTypeAttribute.validations.maxLength && recordTypeAttribute.validations.maxLength > 0) {
				if (value) {
					var strValue = (value + "").trim();
					if (value.length > recordTypeAttribute.validations.maxLength) {
						if (type == "layer") {
							$scope.recordTypeLayerErrors[recordTypeAttribute.type] = "Maximum length "+recordTypeAttribute.validations.maxLength+" characters";
						} else {
							$scope.recordTypeCustomErrors[recordTypeAttribute.id] = "Maximum length "+recordTypeAttribute.validations.maxLength+" characters";
						}
						errorsFound = true;
					}
				}
			}
			if (recordTypeAttribute.validations.minValue || recordTypeAttribute.validations.minValue == 0) {
				if (value &&isNumeric(value)) {
					var numericValue = +value;
					if (numericValue < recordTypeAttribute.validations.minValue) {
						if (type == "layer") {
							$scope.recordTypeLayerErrors[recordTypeAttribute.type] = "Minimum value "+recordTypeAttribute.validations.minValue;
						} else {
							$scope.recordTypeCustomErrors[recordTypeAttribute.id] = "Minimum value "+recordTypeAttribute.validations.minValue;
						}
						errorsFound = true;
					}
				}
			}
			if (recordTypeAttribute.validations.maxValue || recordTypeAttribute.validations.maxValue == 0) {
				if (value && isNumeric(value)) {
					var numericValue = +value;
					if (numericValue > recordTypeAttribute.validations.maxValue) {
						if (type == "layer") {
							$scope.recordTypeLayerErrors[recordTypeAttribute.type] = "Maximum value "+recordTypeAttribute.validations.maxValue;
						} else {
							$scope.recordTypeCustomErrors[recordTypeAttribute.id] = "Maximum value "+recordTypeAttribute.validations.maxValue;
						}
						errorsFound = true;
					}
				}
			}
		}
	};
	
	$scope.validateRecordTypeAttributes = function() {	
		$scope.recordTypeCustomErrors = {};
		$scope.recordTypeLayerErrors = {};
		var errorsFound = false;
		if ($scope.record.recordType.customAttributes && $scope.record.recordType.customAttributes.length > 0) {
			$.each($scope.record.recordType.customAttributes, function( index, recordTypeAttribute ) {
				$scope.validateRecordTypeAtt(recordTypeAttribute);
			});
		}
		if ($scope.record.recordType.layerAttributes && $scope.record.recordType.layerAttributes.length > 0) {
			$.each($scope.record.recordType.layerAttributes, function( index, recordTypeAttribute ) {
				$scope.validateRecordTypeAtt(recordTypeAttribute);
			});
		}
		
		return errorsFound;
	};
	
	$scope.updateRecord = function() {
		$scope.showValidationMessages = true;
		var errorsFound = $scope.validateRecordTypeAttributes();
		if (!$scope.editRecordForm.$invalid && !errorsFound) {
			var url = "updateRecord";
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.record),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  $scope.navController.pop("edited");
						  showSuccessMessage("Success! Record updated successfully..");
					  } else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		}
	}
});

recordTypesApp.controller('AddRecordController', function ($scope, $http, dataService, parameter) {
	$scope.recordTypeId = parameter.getValue();
	$scope.record = {};
	$scope.record.layerAttributeConfig = {};
	$scope.record.layerAttributeConfig.layer = {};
	$scope.record.attributeValuesMap = {};
	
	$scope.getLayers = function(val) {
	    return $http.get('getLayersByPartialName', {
	      params: {
	        name: val
	      }
	    }).then(function(result){
	      var layers = [];
	      angular.forEach(result.data.output, function(layer){
	        layers.push(layer);
	      });
	      return layers;
	    });
	  };
	
	$scope.selectLayer = function($item, $model, $label) {
		if ($scope.record.layerAttributeConfig && $scope.record.layerAttributeConfig.layer && $scope.record.layerAttributeConfig.layer.id && $scope.record.layerAttributeConfig.layer.id != '') {
			var layer = $scope.getLayer($scope.record.layerAttributeConfig.layer.id);
		}
		$scope.record.layerAttributeConfig = {};
		$scope.record.layerAttributeConfig.attributeValueMap = {};
		$scope.record.layerAttributeConfig.layer =  layer;
	};
	
	$scope.getRecordType = function() {
		var url = "getRecordTypeById?recordTypeId="+$scope.recordTypeId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.record.recordType = data.output;
						  if ($scope.record.recordType.type == 1) {
							  checkAndAddRecordTypeLayerAttributes($scope.record.recordType);
							  var layersData = dataService.getLayers();
								if (layersData.success) {
									$scope.layers = layersData.output; 
								}
						  } else if ($scope.record.recordType.type == 2) {
							  	var structuresData = dataService.getStructures();
								if (structuresData.success) {
									$scope.structures = structuresData.output; 
								}
								var structureItemsData = dataService.getStructureItems();
								if (structureItemsData.success) {
									$scope.structureItems = structureItemsData.output; 
								}
						  } 
						  
					  } else {
						  showErrorMessage("No record type found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	
	$scope.getLayer = function(layerId) {
		var layer;
		var url = "getLayerById?layerId="+layerId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  layer = data.output;
					  } else {
						  showErrorMessage("No layer found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
		return layer;
	}
	$scope.getRecordType();
	
	$scope.showSelectLayerScreen = function() {
		$scope.screen = $scope.navController.push("selectLayer", function() {
			//refresh records
		});
	};
	
	$scope.validateRecordTypeAtt = function(recordTypeAttribute) {
		if (recordTypeAttribute && recordTypeAttribute.validations && recordTypeAttribute.validations) {
			var value;
			var type = "layer";
			if (recordTypeAttribute.type == 6) {
				value = $scope.record.from;
			} else if (recordTypeAttribute.type == 7) {
				value = $scope.record.to;
			} else if (recordTypeAttribute.type == 8) {
				value = $scope.record.level;
			} else {
				var type = "custom";
				if ($scope.record.attributeValuesMap) {
					value = $scope.record.attributeValuesMap[recordTypeAttribute.id];
				}
			}
			if (recordTypeAttribute.validations.minLength && recordTypeAttribute.validations.minLength > 0) {
				if (value) {
					var strValue = (value + "").trim();
					if (value.length < recordTypeAttribute.validations.minLength) {
							if (type == "layer") {
								$scope.recordTypeLayerErrors[recordTypeAttribute.type] = "Minimum length "+recordTypeAttribute.validations.minLength+" characters";
							} else {
								$scope.recordTypeCustomErrors[recordTypeAttribute.id] = "Minimum length "+recordTypeAttribute.validations.minLength+" characters";
							}
						errorsFound = true;
					}
				}
			}
			if (recordTypeAttribute.validations.maxLength && recordTypeAttribute.validations.maxLength > 0) {
				if (value) {
					var strValue = (value + "").trim();
					if (value.length > recordTypeAttribute.validations.maxLength) {
						if (type == "layer") {
							$scope.recordTypeLayerErrors[recordTypeAttribute.type] = "Maximum length "+recordTypeAttribute.validations.maxLength+" characters";
						} else {
							$scope.recordTypeCustomErrors[recordTypeAttribute.id] = "Maximum length "+recordTypeAttribute.validations.maxLength+" characters";
						}
						errorsFound = true;
					}
				}
			}
			if (recordTypeAttribute.validations.minValue || recordTypeAttribute.validations.minValue == 0) {
				if (value &&isNumeric(value)) {
					var numericValue = +value;
					if (numericValue < recordTypeAttribute.validations.minValue) {
						if (type == "layer") {
							$scope.recordTypeLayerErrors[recordTypeAttribute.type] = "Minimum value "+recordTypeAttribute.validations.minValue;
						} else {
							$scope.recordTypeCustomErrors[recordTypeAttribute.id] = "Minimum value "+recordTypeAttribute.validations.minValue;
						}
						errorsFound = true;
					}
				}
			}
			if (recordTypeAttribute.validations.maxValue || recordTypeAttribute.validations.maxValue == 0) {
				if (value && isNumeric(value)) {
					var numericValue = +value;
					if (numericValue > recordTypeAttribute.validations.maxValue) {
						if (type == "layer") {
							$scope.recordTypeLayerErrors[recordTypeAttribute.type] = "Maximum value "+recordTypeAttribute.validations.maxValue;
						} else {
							$scope.recordTypeCustomErrors[recordTypeAttribute.id] = "Maximum value "+recordTypeAttribute.validations.maxValue;
						}
						errorsFound = true;
					}
				}
			}
		}
	};
	
	$scope.validateRecordTypeAttributes = function() {	
		$scope.recordTypeCustomErrors = {};
		$scope.recordTypeLayerErrors = {};
		var errorsFound = false;
		if ($scope.record.recordType.customAttributes && $scope.record.recordType.customAttributes.length > 0) {
			$.each($scope.record.recordType.customAttributes, function( index, recordTypeAttribute ) {
				$scope.validateRecordTypeAtt(recordTypeAttribute);
			});
		}
		if ($scope.record.recordType.layerAttributes && $scope.record.recordType.layerAttributes.length > 0) {
			$.each($scope.record.recordType.layerAttributes, function( index, recordTypeAttribute ) {
				$scope.validateRecordTypeAtt(recordTypeAttribute);
			});
		}
		
		return errorsFound;
	};
	
	$scope.addRecord = function() {
		$scope.showValidationMessages = true;
		var errorsFound = $scope.validateRecordTypeAttributes();
		if (!$scope.addRecordForm.$invalid && !errorsFound) {
			var url = "addRecord";
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.record),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  $scope.navController.pop();
						  showSuccessMessage("Success! Record added successfully..");
					  } else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		}
	}
});



layersApp.controller('StructuresController', function ($scope, parameter) {
	$scope.selectedMenu = 'structures';
	$scope.navController = new Navigation($scope, "Structures");
	
	$scope.showNewStructureScreen = function() {
		$scope.screen = $scope.navController.push("New Structure", function() {
			jQuery("#structuresList").trigger( 'reloadGrid' );
		});
	};
	
	$scope.goToStructureItems = function() {
		$scope.screen = $scope.navController.push("Structure Items", function() {
			//jQuery("#structuresList").trigger( 'reloadGrid' );
		});
	};
	
	
	$(document).ready(function() {
	    jQuery("#structuresList").jqGrid({
	        url:'getJqStructures',
	    	datatype: "json",
	        width: '100%',
	        height: '100%',
	        autowidth:true,
	        scrollOffset: 0,
	        altRows:true,
	        altclass:'myAltRowClass',
	        colNames:['Id', 'Structure Name'],
	        colModel:[
	            {name:'id',index:'id', hidden:true, align:"left"},
	            {name:'name',index:'name', align:"left"}
	        ],
	        onSelectRow: function(id){
	        	var structureId = $("#structuresList").jqGrid ('getCell', id, 'id');
	        	parameter.setValue(structureId);
	        	$scope.$apply(function () {
	        		 $scope.navController.push("View Structure", function(result) {
	        			 if (result == "deleted" || result == "edited") {
	        				 jQuery("#structuresList").trigger( 'reloadGrid' );
	        			 }
	        		 });
	        	});
	        }
	    });
	   });
});

layersApp.controller('ViewStructureController', function ($scope, parameter) {
	$scope.structureId = parameter.getValue();
	
    $scope.getStructure = function() {
		console.log($scope.layer);
		var url = "getStructureById?structureId="+$scope.structureId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.structure = data.output;
					  } else {
						  showErrorMessage("No structure found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	$scope.getStructure();
	
	$scope.edited = false;
	$scope.editStructure = function() {
		parameter.setValue($scope.structureId);
		$scope.navController.push("Edit Structure", function(result) {
				if(result == "deleted"){
					$scope.navController.pop("deleted");
				} else if (result == "edited") {
					$scope.edited = true;
					$scope.getStructure();
				}
			});
	};
	
	$scope.back = function() {
		if ($scope.edited) {
			$scope.navController.pop("edited");
		} else {
			$scope.navController.pop();
		}
	};
});


layersApp.controller('EditStructureController', function ($scope, $modal, parameter) {
	$scope.structureId = parameter.getValue();
	
	$scope.getStructure = function() {
		console.log($scope.layer);
		var url = "getStructureById?structureId="+$scope.structureId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.structure = data.output;
						} else {
						  showErrorMessage("No structure found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	$scope.getStructure();
	$scope.back = function() {
		$scope.navController.pop();
	};
	
	$scope.deactivateStructure = function() {
		var modalInstance = $modal.open({
			templateUrl : 'DeactivateStructureTemplate',
			controller : 'DeactivateStructureController',
			resolve : {
				structure : function() {
					return $scope.structure;
				}
			}
		});
		modalInstance.result.then(function(status) {
			if(status && status == "removed") {
				$scope.navController.pop("deleted");
			}
		});
	};
	
	$scope.updateStructure = function() {
		$scope.showValidationMessages = true;
		if (!$scope.editStructureForm.$invalid) {
			var url = "updateStructure";
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.structure),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  showSuccessMessage("Success! Structure updated.!");
						  $scope.navController.pop("edited");
					  } else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		}
	}
	
});

layersApp.controller('AddStructureController', function ($scope) {
	$scope.structure = {};
	$scope.addStructure = function() {
		$scope.showValidationMessages = true;
		if (!$scope.addStructureForm.$invalid) {
			var url = "addStructure";
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.structure),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  showSuccessMessage("Success! Structure added.!");
						  $scope.navController.pop(data.output);
					  } else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		}
	};
});

layersApp.controller('StructureItemController', function ($scope, parameter) {
	$scope.showNewStructureItemScreen = function() {
		$scope.screen = $scope.navController.push("New Structure Item", function() {
			jQuery("#strItemsList").trigger( 'reloadGrid' );
		});
	};
	
	
	$(document).ready(function() {
	    jQuery("#strItemsList").jqGrid({
	        url:'getJqItems?type=2',
	    	datatype: "json",
	        width: '100%',
	        height: '100%',
	        autowidth:true,
	        scrollOffset: 0,
	        altRows:true,
	        altclass:'myAltRowClass',
	        colNames:['Id', 'Item Name'],
	        colModel:[
	            {name:'id',index:'id', hidden:true, align:"left"},
	            {name:'name',index:'name', align:"left"}
	        ],
	        onSelectRow: function(id){
	        	var layerId = $("#strItemsList").jqGrid ('getCell', id, 'id');
	        	parameter.setValue(layerId);
	        	$scope.$apply(function () {
	        		 $scope.navController.push("View Structure Item", function(result) {
	        			 if (result == "deleted" || result == "edited") {
	        				 jQuery("#strItemsList").trigger( 'reloadGrid' );
	        			 }
	        		 });
	        	});
	        }
	    });
	   });
});

layersApp.controller('ViewStructureItemController', function ($scope, parameter) {
	$scope.itemId = parameter.getValue();
	
    $scope.getItem = function() {
		var url = "getItemById?itemId="+$scope.itemId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.item = data.output;
					  } else {
						  showErrorMessage("No item found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	$scope.getItem();
	
	$scope.edited = false;
	$scope.editItem = function() {
		parameter.setValue($scope.itemId);
		$scope.navController.push("Edit Structure Item", function(result) {
				if(result == "deleted"){
					$scope.navController.pop("deleted");
				} else if (result == "edited") {
					$scope.edited = true;
					$scope.getItem();
				}
			});
	};
	
	$scope.back = function() {
		if ($scope.edited) {
			$scope.navController.pop("edited");
		} else {
			$scope.navController.pop();
		}
	};
});

layersApp.controller('EditStructureItemController', function ($scope, $modal, parameter) {
	$scope.itemId = parameter.getValue();
	
	$scope.checkAttributes = function(item) {
		if (item && item.layerAttributes && item.layerAttributes.length > 0) {
			$.each(item.layerAttributes, function( index, layerAttribute ) {
				if (layerAttribute.allowedValues == "All") {
					$("input[data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', true);
				} else {
					$.each(layerAttribute.options, function( index, option ) {
						$("input[data-attrId='"+layerAttribute.attribute.id+"'][value='"+option+"']").prop('checked', true);
					});
					if (layerAttribute.options && layerAttribute.attribute.options && layerAttribute.options.length == layerAttribute.attribute.options) {
						$("input[data-allAttr='all'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', true);
					}
				}
			});
		}
	};
	

	$scope.removeAttribute = function(la) {
		var modalInstance = $modal.open({
			templateUrl : 'RemoveLayerAttributeTemplate',
			controller : 'RemoveLayerAttributeController',
			resolve : {
				layerAttribute : function() {
					return la;
				}
			}
		});
		modalInstance.result.then(function(status) {
			if(status && status == "removed") {
				var newLayerAttributes = [];
				$.each($scope.item.layerAttributes, function( index1, layerAttribute ) {
					if(la.attribute.id == layerAttribute.attribute.id) {
						
					} else {
						newLayerAttributes.push(layerAttribute);
					}
				});
				$scope.item.layerAttributes = newLayerAttributes; 
			}
		});
		
	};
	
	
	
	$scope.getItem = function() {
		var url = "getItemById?itemId="+$scope.itemId;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  if (data.output) {
						  $scope.item = data.output;
						} else {
						  showErrorMessage("No item found for your selection!");
					  }
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	}
	$scope.getItem();
	
	$scope.deactivateItem = function(layer) {
		var modalInstance = $modal.open({
			templateUrl : 'DeactivateItemTemplate',
			controller : 'DeactivateStructureItemController',
			resolve : {
				item : function() {
					return $scope.item;
				}
			}
		});
		modalInstance.result.then(function(status) {
			if(status && status == "removed") {
				$scope.navController.pop("deleted");
			}
		});
	};
	
	$scope.showAddAttributeScreen = function() {
		parameter.setValue(2);
		$scope.screen = $scope.navController.push("Add Attribute", function(result) {
			if (result) {
				if (result.editedAttributes && result.editedAttributes.length > 0) {
					if ($scope.item.layerAttributes && $scope.item.layerAttributes.length > 0) {
						$.each(result.editedAttributes, function( index, editedAttribute ) {
							$.each($scope.item.layerAttributes, function( index1, layerAttribute ) {
								if (editedAttribute.id == layerAttribute.attribute.id) {
									layerAttribute.allowedValues = "All";
									layerAttribute.attribute = editedAttribute;
								}
							});
						});
					}
				}
				if (result.deletedAttributes && result.deletedAttributes.length > 0) {
					if ($scope.item.layerAttributes && $scope.item.layerAttributes.length > 0) {
						var newLayerAttributes = [];
						$.each($scope.item.layerAttributes, function(index, layerAttribute) {
							var found = false;
							$.each(result.deletedAttributes, function(index1, deletedAttribute) {
								if (deletedAttribute.id == layerAttribute.attribute.id) {
									found = true;
								}
							});
							if (!found) {
								newLayerAttributes.push(layerAttribute);
							}
						});
						$scope.item.layerAttributes = newLayerAttributes;
					}
				}
				if (result.selectedAttribute) {
					if (!$scope.item.layerAttributes) {
						$scope.item.layerAttributes = [];
					}
					var layerAttribute = {};
					layerAttribute.attribute = result.selectedAttribute;
					layerAttribute.allowedValues = "All";
					layerAttribute.mandatory = true;
					$scope.item.layerAttributes.push(layerAttribute);
					$scope.checkAttributes($scope.item);
				}
			}
		});
	};
	
	$scope.allOptionsClicked = function(layerAttribute) {
		layerAttribute.options = [];
		if($("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").is(':checked')) {
			layerAttribute.allowedValues = "All";
			$("input[data-attrId='"+layerAttribute.attribute.id+"']").each(function() {
				$(this).prop('checked', true);
			});
		} else {
			layerAttribute.allowedValues = "None";
			layerAttribute.options = [];
			$("input[data-attrId='"+layerAttribute.attribute.id+"']").each(function() {
				$(this).prop('checked', false);
			});
		}
	}
	
	$scope.attributeValueClicked = function(layerAttribute) {
		layerAttribute.options = [];
		var areAllOptionsChecked = true;
		var selectedValuesCount = 0;
		$("input[data-attrId='"+layerAttribute.attribute.id+"']").each(function() {
			if($(this).attr("data-allAttr") != "All") {
				if (!$(this).is(":checked")) {
					areAllOptionsChecked = false;
				} else {
					++selectedValuesCount;
					layerAttribute.options.push($(this).val());
				}
			}
		});
		if (areAllOptionsChecked) {
			$("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', true);
			layerAttribute.allowedValues = "All";
		} else if (selectedValuesCount > 0){
			$("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', false);
			layerAttribute.allowedValues = "Selected ("+selectedValuesCount+")";
		} else {
			$("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', false);
			layerAttribute.allowedValues = "None";
		}
		
	}
	
	
	
	$scope.updateItem = function() {
		$scope.showValidationMessages = true;
		$scope.attributesInvalid = false;
		if($scope.item.layerAttributes && $scope.item.layerAttributes.length > 0) {
			$.each($scope.item.layerAttributes, function( index, layerAttribute ) {
				if (!layerAttribute.allowedValues || layerAttribute.allowedValues == 'None') {
					$scope.attributesInvalid = true;
				}
			});
		}
		if (!$scope.editItemForm.$invalid && !$scope.attributesInvalid ) {
			var url = "updateItem";
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.item),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  $scope.navController.pop("edited");
						  showSuccessMessage("Success! Item updated successfully..");
					  } else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		}
	}
	
});

layersApp.controller('AddStructureItemController', function ($scope, parameter) {
	$scope.item = {type:2};
	$scope.showValidationMessages = false;
	$scope.addItem = function() {
		$scope.showValidationMessages = true;
		$scope.attributesInvalid = false;
		if($scope.item.layerAttributes && $scope.item.layerAttributes.length > 0) {
			$.each($scope.item.layerAttributes, function( index, layerAttribute ) {
				if (!layerAttribute.allowedValues || layerAttribute.allowedValues == 'None') {
					$scope.attributesInvalid = true;
				}
			});
		}
		if (!$scope.addItemForm.$invalid && !$scope.attributesInvalid ) {
			var url = "addItem";
			$.ajax({
				  type: "POST",
				  url: url,
				  async: false,
				  data: JSON.stringify($scope.item),
				  contentType: "application/json",
				  dataType: "json",
				  success: function(data) {
					  if (data.success) {
						  $scope.navController.pop(data.output);
						  showSuccessMessage("Success! Item added successfully..");
					  } else {
						  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
					  }
				  },
				  error: function() {
					  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
				  }
				  
				});
		}
	}
	
	$scope.showAddAttributeScreen = function() {
		parameter.setValue(2);
		$scope.screen = $scope.navController.push("Add Attribute", function(result) {
			if (result) {
				if (result.editedAttributes && result.editedAttributes.length > 0) {
					if ($scope.item.layerAttributes && $scope.item.layerAttributes.length > 0) {
						$.each(result.editedAttributes, function( index, editedAttribute ) {
							$.each($scope.item.layerAttributes, function( index1, layerAttribute ) {
								if (editedAttribute.id == layerAttribute.attribute.id) {
									layerAttribute.allowedValues = "All";
									layerAttribute.attribute = editedAttribute;
								}
							});
						});
					}
				}
				if (result.deletedAttributes && result.deletedAttributes.length > 0) {
					if ($scope.item.layerAttributes && $scope.item.layerAttributes.length > 0) {
						var newLayerAttributes = [];
						$.each($scope.item.layerAttributes, function(index, layerAttribute) {
							var found = false;
							$.each(result.deletedAttributes, function(index1, deletedAttribute) {
								if (deletedAttribute.id == layerAttribute.attribute.id) {
									found = true;
								}
							});
							if (!found) {
								newLayerAttributes.push(layerAttribute);
							}
						});
						$scope.item.layerAttributes = newLayerAttributes;
					}
				}
				if (result.selectedAttribute) {
					if (!$scope.item.layerAttributes) {
						$scope.item.layerAttributes = [];
					}
					var layerAttribute = {};
					layerAttribute.attribute = result.selectedAttribute;
					layerAttribute.allowedValues = "All";
					layerAttribute.mandatory = true;
					$scope.item.layerAttributes.push(layerAttribute);
					$scope.checkAttributes($scope.item);
				}
			}
		});
	};
	
	$scope.allOptionsClicked = function(layerAttribute) {
		layerAttribute.options = [];
		if($("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").is(':checked')) {
			layerAttribute.allowedValues = "All";
			$("input[data-attrId='"+layerAttribute.attribute.id+"']").each(function() {
				$(this).prop('checked', true);
			});
		} else {
			layerAttribute.allowedValues = "None";
			layerAttribute.options = [];
			$("input[data-attrId='"+layerAttribute.attribute.id+"']").each(function() {
				$(this).prop('checked', false);
			});
		}
	}
	
	$scope.attributeValueClicked = function(layerAttribute) {
		layerAttribute.options = [];
		var areAllOptionsChecked = true;
		var selectedValuesCount = 0;
		$("input[data-attrId='"+layerAttribute.attribute.id+"']").each(function() {
			if($(this).attr("data-allAttr") != "All") {
				if (!$(this).is(":checked")) {
					areAllOptionsChecked = false;
				} else {
					++selectedValuesCount;
					layerAttribute.options.push($(this).val());
				}
			}
		});
		if (areAllOptionsChecked) {
			$("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', true);
			layerAttribute.allowedValues = "All";
		} else if (selectedValuesCount > 0){
			$("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', false);
			layerAttribute.allowedValues = "Selected ("+selectedValuesCount+")";
		} else {
			$("input[data-allAttr='All'][data-attrId='"+layerAttribute.attribute.id+"']").prop('checked', false);
			layerAttribute.allowedValues = "None";
		}
		
	}
});

layersApp.controller('DeactivateStructureController', function ($scope, $modalInstance, structure, parameter) {
	$scope.structure = structure;
	$scope.deactivateStructure = function() {
		var url = "deactivateStructure?structureId="+$scope.structure.id;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  showSuccessMessage("Success! Structure deleted.!");
					  $modalInstance.close("removed");
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	
	$scope.close = function() {
		$modalInstance.dismiss();
	};
});

layersApp.controller('DeactivateStructureItemController', function ($scope, $modalInstance, item, parameter) {
	$scope.item = item;
	$scope.deactivateItem = function() {
		var url = "deactivateLayer?layerId="+$scope.item.id;
		$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  //data: JSON.stringify($scope.layer),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
					  showSuccessMessage("Success! Item deleted.!");
					  $modalInstance.close("removed");
				  } else {
					  if (data.messages && data.messages.length > 0) {showErrorMessage(data.messages[0]);}
				  }
			  },
			  error: function() {
				  showErrorMessage("Sorry! An error has occured. Please try again or report an issue.")
			  }
			  
			});
	};
	
	$scope.close = function() {
		$modalInstance.dismiss();
	};
});



function checkAndAddRecordTypeLayerAttributes(recordType) {
	var layerFromAttribute = undefined;
	var layerToAttribute = undefined;
	var levelAttribute = undefined;
	if (recordType && recordType.type == 1) {
		if (recordType.layerAttributes) {
			$.each(recordType.layerAttributes, function( index, layerAttribute ) {
				if (layerAttribute && layerAttribute.type == 6) {
					layerFromAttribute = layerAttribute;
				} else if (layerAttribute && layerAttribute.type == 7) {
					layerToAttribute = layerAttribute;
				} else if (layerAttribute && layerAttribute.type == 8) {
					levelAttribute = layerAttribute;
				}
			});
		} else {
			recordType.layerAttributes = [];
		}
		if (!layerFromAttribute) {
			layerFromAttribute = {name:"From", type:6};
			recordType.layerAttributes.push(layerFromAttribute);
		}
		if (!layerToAttribute) {
			layerToAttribute = {name:"To", type:7};
			recordType.layerAttributes.push(layerToAttribute);
		}
		if (!levelAttribute) {
			levelAttribute = {name:"Level", type:8};
			recordType.layerAttributes.push(levelAttribute);
		}
	}
}

function isNumeric(value) {
	  if(!(isEmpty(value))){
		   if (value != null && !value.toString().match(/^[-]?\d*\.?\d*$/)) return false;
		   return true;
	  }
	  else {
		  return false;
	  }
}

function isEmpty(value){
  if (value === undefined || value == null) {
	  return true;
  } else {
	  if (typeof value === "number" || typeof value === "boolean") {
		  return false;
	  } else {
		  value= value.replace(/^\s+|\s+$/, '');
		  return (value === undefined || value == null || value.length <= 0) ? true : false;
	  }
  }
}