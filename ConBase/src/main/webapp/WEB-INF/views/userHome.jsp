<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.derive.conbase.security.User, org.springframework.security.core.context.SecurityContextHolder" %>
<body ng-app="UserHomeApp" ng-controller="UserHomeController">

<header id="header" class="navbar">
       <div class="clearfix" id="top-user-header">
           <ul class="pull-left clearfix" style="margin: 0px;">
               <li class="pull-right" style="padding-right: 10px;"><a style="color:white" href="<%=request.getContextPath() %>/userHome">Home</a></li>
           </ul>
           <ul class="pull-right clearfix" style="margin: 0px;">
               <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
               <li class="pull-right" style="padding-left: 10px;"><a style="color:white" href="<%=request.getContextPath() %>/j_spring_security_logout">Logout</a></li>
               <li class="pull-right" style="padding-left: 10px;"><span class="glyphicon glyphicon-user"></span>
                   <%=((com.derive.conbase.security.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getFullName()%>
               </li>
               </sec:authorize>
           </ul>
       </div>
       <h1>Welcome <%=((com.derive.conbase.security.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getFullName()%>!</h1>
</header>
<section id="content" class="content-sidebar no-border" ng-cloak>
    <div class="row row-padder" style="margin:20px 0px;">
        <div ng-show="navController.currentScreen() == 'Home'">
        		<div class="panel panel-default">
				  <div class="panel-heading">
				  	<strong>Following projects are owned by you</strong>
				  	<button type="button" class="btn btn-info btn-xs" ng-click="createProject()"><span class="glyphicon glyphicon-plus"></span> Create a new project</button>
				  </div>
				  <div class="panel-body">
				    <div ng-if="ownedProjects.length > 0">
					    <table class="table table-bordered">
					        <thead>
					          <tr>
					            <th>#</th>
					            <th>Project Name</th>
					            <th>Status</th>
					            <th>Created On</th>
					            <th></th>
					          </tr>
					        </thead>
					        <tbody>
					          <tr ng-repeat="project in ownedProjects">
					            <td ng-bind="$index + 1"></td>
					            <td><a href="javascript:void(0)"><span ng-bind="project.name" ng-click="viewProjectDetails(project.id)"></span></a></td>
					            <td ng-bind="(project.active && 'Active') || 'Inactive'"></td>
					            <td ng-bind="project.createdOn"></td>
					            <td><button type="button" ng-if="project.active" ng-click="changeCurrentProject(project.id)" class="btn btn-default btn-xs"><span class="glyphicon glyphicon-log-in"></span> Go</button></td>
					          </tr>
					        </tbody>
					      </table>
				    </div>
				    <div ng-if="!ownedProjects || ownedProjects.length == 0">
				    	You have not created any projects yet!
				    </div>
				  </div>
				  </div>
				  
				  <div class="panel panel-default" style="margin-top:20px;">
				  <div class="panel-heading">
				  	<strong>You are invited to following projects</strong>
				  </div>
				  <div class="panel-body">
				    <div ng-if="invitedProjects.length > 0">
					    <table class="table table-bordered">
					        <thead>
					          <tr>
					            <th>#</th>
					            <th>Project Name</th>
					            <th>Status</th>
					            <th>Created On</th>
					            <th></th>
					          </tr>
					        </thead>
					        <tbody>
					          <tr ng-repeat="project in invitedProjects">
					            <td ng-bind="$index + 1"></td>
					            <td><a href="javascript:void(0)"><span ng-bind="project.name" ng-click="viewProjectDetails(project.id)"></span></a></td>
					            <td ng-bind="(project.active && 'Active') || 'Inactive'"></td>
					            <td ng-bind="project.createdOn"></td>
					            <td><button type="button" ng-if="project.active" ng-click="changeCurrentProject(project.id)" class="btn btn-default btn-xs"><span class="glyphicon glyphicon-log-in"></span> Go</button></td>
					          </tr>
					        </tbody>
					      </table>
				    </div>
				    <div ng-if="!ownedProjects || ownedProjects.length == 0">
				    	You are not invited to any projects!
				    </div>
				  </div>
				</div>
        </div>
    </div>
    
    <div ng-if="navController.contains('Create Project')"><div ng-include="'CreateProjectTemplate'"></div></div>
    <div ng-if="navController.contains('View Project Details')"><div ng-include="'ViewProjectDetailsTemplate'"></div></div>
</section>

<%@ include file="../layout/includeScripts.jsp"%>

<script>var userId = '<%=((User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId() %>';</script>

<script type="text/ng-template" class="template" id="CreateProjectTemplate">
<div ng-controller="CreateProjectController" style="margin-top:-40px;">
	<div class="sub-header">
		<ol class="breadcrumb">
  			<li class="active" ng-repeat="screen in navController.screens" ng-bind="screen.screen"></li>
		</ol>
		<h2 title="Records" ng-bind="navController.currentScreen()"></h2>
	</div>
	<div class="row row-padder" style="margin:20px 0px;">
	<div ng-show="navController.currentScreen() == 'Create Project'">
		<div>
		<form role="form" name="addProjectForm">
			<div class="form-group">
				<label>Project name</label>
				<input type="text" class="form-control" placeholder="Name of the project" ng-model="project.name" required>
			</div>
			<div class="form-group">
				<label>Description</label>
				<textarea class="form-control" rows="3" ng-model="project.description"></textarea>
			</div>
	  		<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
			<button type="button" ng-click="addProject()" class="btn btn-danger">Create</button>
		</form>
		</div>
	</div>
	</div>
</div>
</script>

<script type="text/ng-template" class="template" id="EditProjectDetailsTemplate">
<div ng-controller="EditProjectDetailsController">
	<div class="row row-padder" style="margin:20px 0px;">
	<div ng-show="navController.currentScreen() == 'Edit Project Details'">
		<div>
		<form role="form" name="editProjectForm">
			<div class="form-group">
				<label>Project name</label>
				<input type="text" class="form-control" placeholder="Name of the project" ng-model="project.name" required>
			</div>
			<div class="form-group">
				<label>Description</label>
				<textarea class="form-control" rows="3" ng-model="project.description"></textarea>
			</div>
	  		<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
			<button type="button" ng-click="addProject()" class="btn btn-danger">Update</button>
		</form>
		</div>
	</div>
	</div>
</div>
</script>


<script type="text/ng-template" class="template" id="ViewProjectDetailsTemplate">
<div ng-controller="ViewProjectDetailsController" style="margin-top:-40px;">
	<div class="sub-header">
		<ol class="breadcrumb">
  			<li class="active" ng-repeat="screen in navController.screens" ng-bind="screen.screen"></li>
		</ol>
		<h2 title="View Project Details" ng-bind="navController.currentScreen()"></h2>
	</div>
	<div class="row row-padder" style="margin:20px 0px;">
	<div ng-show="navController.currentScreen() == 'View Project Details'">
		<div>
		<form role="form" name="viewProjectForm">
			<div class="form-group">
				<label>Project Name</label>
				<div ng-bind="project.name" class="form-display-val" ></div>
			</div>
			<div class="form-group">
				<label>Description</label>
				<div ng-bind="project.description" class="form-display-val" ></div>
			</div>
			<div class="form-group">
				<label>Owned by</label>
				<div ng-bind="project.user.fullName" class="form-display-val" ></div>
			</div>
			<div class="form-group">
				<label>Invited Users</label>
				<table class="table table-striped">
					<thead>
						<tr>
							<th>Name</th>
							<th>Email</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<tr ng-repeat="user in project.invitedUsers">
							<td>{{user.fullName}}</td>
							<td>{{user.email}}</td>
							<td><a href="javascript:void(0)" ng-click="removeUserFromProject(user)"><span class="glyphicon glyphicon-trash">Remove</span></a></td>
						</tr>
					</tbody>
		      </table>
			</div>
	  		<button type="button" ng-click="back()" class="btn btn-default">Go back</button>
			<button ng-if="isProjectOwner()" type="button" class="btn btn-default" ng-click="inviteUsers()" >Invite Users</button>
			<button type="button" ng-click="editProject()" class="btn btn-danger">Edit</button>
		</form>
		</div>
	</div>
	</div>
	<div ng-if="navController.contains('Edit Project Details')"><div ng-include="'EditProjectDetailsTemplate'"></div></div>

</div>
</script>

<script type="text/ng-template" class="template" id="InviteUserTemplate">
<!-- Modal -->
<div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" ng-click="close()">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Invite users</h4>
      </div>
      <div class="modal-body">
        <form role="form" name="InviteUsersForm">
			<div class="form-group">
				<label>Email</label>
				<input type="email" class="form-control" placeholder="Enter email id" ng-model="invitedUser.email" required>
			</div>
		</form>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal" ng-click="close()">Close</button>
        <button type="button" class="btn btn-primary" ng-click="inviteUser()">Invite</button>
      </div>
    </div>

</script>

<script type="text/ng-template" class="template" id="RemoveUserTemplate">
<!-- Modal -->
<div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true" ng-click="close()">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Remove user</h4>
      </div>
      <div class="modal-body">
        Are you sure you want to remove user <span ng-bind="user.fullName"></span> from the project?
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal" ng-click="close()" >Close</button>
        <button type="button" class="btn btn-primary" ng-click="removeUser()">Yes</button>
      </div>
    </div>

</script>