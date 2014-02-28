<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.derive.conbase.security.User, org.springframework.security.core.context.SecurityContextHolder" %>

<body ng-app="indexApp" ng-controller="IndexController">

<!-- start Mixpanel --><script type="text/javascript">(function(e,b){if(!b.__SV){var a,f,i,g;window.mixpanel=b;a=e.createElement("script");a.type="text/javascript";a.async=!0;a.src=("https:"===e.location.protocol?"https:":"http:")+'//cdn.mxpnl.com/libs/mixpanel-2.2.min.js';f=e.getElementsByTagName("script")[0];f.parentNode.insertBefore(a,f);b._i=[];b.init=function(a,e,d){function f(b,h){var a=h.split(".");2==a.length&&(b=b[a[0]],h=a[1]);b[h]=function(){b.push([h].concat(Array.prototype.slice.call(arguments,0)))}}var c=b;"undefined"!==
typeof d?c=b[d]=[]:d="mixpanel";c.people=c.people||[];c.toString=function(b){var a="mixpanel";"mixpanel"!==d&&(a+="."+d);b||(a+=" (stub)");return a};c.people.toString=function(){return c.toString(1)+".people (stub)"};i="disable track track_pageview track_links track_forms register register_once alias unregister identify name_tag set_config people.set people.set_once people.increment people.append people.track_charge people.clear_charges people.delete_user".split(" ");for(g=0;g<i.length;g++)f(c,i[g]);
b._i.push([a,e,d])};b.__SV=1.2}})(document,window.mixpanel||[]);
mixpanel.init("6411320877c544bbe9300a582363f525");</script><!-- end Mixpanel -->


<div id="wrap">
    <header id="header" class="navbar home-page-header">
       <div class="clearfix">
           <ul class="pull-left clearfix" style="margin: 0px;">
               <li class="pull-right" style="padding-right: 10px;"><a href="<%=request.getContextPath() %>/userHome">Home</a></li>
           </ul>
           <ul class="pull-right clearfix" style="margin: 0px;">
               <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
               		<li class="pull-right" style="padding-left: 10px;"><a  href="<%=request.getContextPath() %>/login">Login</a></li>
               </sec:authorize>
               <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
               <li class="pull-right" style="padding-left: 10px;"><a  href="<%=request.getContextPath() %>/j_spring_security_logout">Logout</a></li>
               <li class="pull-right" style="padding-left: 10px;">
               <div style="padding:3px 0px;">
               	<span class="glyphicon glyphicon-user"></span>
                   <%=((com.derive.conbase.security.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getFullName()%>
                </div>
               </li>
               </sec:authorize>
           </ul>
       </div>
    </header>
    <section id="content" class="no-border">
        <div class="row row-padder home-center-box" style="padding-top: 40px;">
            <div class="container">
                <div class="row">
                    <div class="col-md-7" style="color: white">
                        <img src="<%=request.getContextPath()%>/assets/img/infraCMSlogo.png"/>

                        <h1 style="margin-bottom: 30px;margin-top: 40px;">Record management for construction projects</h1>
                        <ul>
                            <li style="font-size: 2.0em;font-weight: 200;list-style:none ;margin-bottom: 15px;color:#B3D8FF"><span class="glyphicon glyphicon-ok"></span><div style="display:inline-block;margin-left:10px;">Get free from scattered excel spreadsheets.</div></li>
                            <li style="font-size: 2.0em;font-weight: 200;list-style:none ;margin-bottom: 15px;color:#B3D8FF"><span class="glyphicon glyphicon-ok"></span><div style="display:inline-block;margin-left:10px;">Collaborate with multiple users.</div></li>
                            <li style="font-size: 2.0em;font-weight: 200;list-style:none ;margin-bottom: 15px;color:#B3D8FF"><span class="glyphicon glyphicon-ok"></span><div style="display:inline-block;margin-left:10px;">Store you records in the cloud with ensured backup.</div></li>
                            <li style="font-size: 2.0em;font-weight: 200;list-style:none ;margin-bottom: 15px;color:#B3D8FF"><span class="glyphicon glyphicon-ok"></span><div style="display:inline-block;margin-left:10px;">Monitor data with layer charts.</div></li>
                        </ul>
                    </div>
                    <div class="col-md-5" style="color: white">
                        <ul style="margin-bottom: 110px;">
                            <li class="nav-link"><a href="<%=request.getContextPath()%>/">Home</a></li>
                            <li class="nav-link"><a href="<%=request.getContextPath()%>/pricing">Pricing</a></li>
                            <li class="btn btn-warning">Signup</li>
                        </ul>
                        <form role="form" name="registrationForm" method="POST">
                            <div class="form-group">
                                <input type="text" class="form-control" id="fullName" name="fullName"
                                       placeholder="Your Fullname" ng-model="user.fullName" style="height:60px;font-size: 16px;" required>
                                <div class="error" ng-show="showValidationErrors && registrationForm.fullName.$error.required">Fullname is required.</div>
                            </div>
                            <div class="form-group">
                                <input type="email" ng-model="user.email" class="form-control" name="email"
                                       placeholder="Enter email" style="height:60px;font-size: 16px;" required>
                                <div class="error" ng-show="showValidationErrors && registrationForm.email.$error.required">Email is required.</div>
                                <div class="error" ng-show="showValidationErrors && registrationForm.email.$error.email">Not a valid email.</div>
                            </div>
                            <div class="form-group">
                                <input type="password" class="form-control" name="password"
                                       placeholder="Password" ng-model="user.password" style="height:60px;font-size: 16px;" ng-minlength="6" required>
                                <div class="error" ng-show="showValidationErrors && registrationForm.password.$error.required">Password is required.</div>
                                <div class="error" ng-show="showValidationErrors && registrationForm.password.$error.minlength">Password should have atleast 6 characters.</div>
                            </div>
                            <button type="button" ng-click="register()" class="btn btn-warning btn-lg btn-block" style="height:60px;font-size: 18px;">Get Started For Free</button>
                            <h6>Works on IE9 & above, Mozilla, Chrome, Safari & Opera</h6>
                            <h5 style="display:none">By signing up you agree to our <a href="javascript:void(0)" style="color:#B3D8FF">Terms & Service</a></h5>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div>
            <div class="container">
                <div class="row">
                    <div class="col-md-6" style="font-size: 14px;">
                        <h3 style="font-weight: bold;color:#326bb1;;"><span class="glyphicon glyphicon-list-alt"></span>&nbsp;&nbsp;Get free from scattered excel</h3>
                        <div style="color:#4b4b4b">
	                        <p>InfraCMS allows you to allows you to create custom records online. You will now never need to maintain your data in the excel. However, you can export the records in excel format at any point of time.</p>
	                    </div>
                    </div>
                    <div class="col-md-6" style="font-size: 14px;">
                        <h3 style="font-weight: bold;color:#326bb1;;"><span class="glyphicon glyphicon-road"></span>&nbsp;&nbsp;Monitor Layers</h3>
                        <div style="color:#4b4b4b">
	                        <p>InfraCMS creates layer charts for length based constructions such as roads & canals. The charts gets auto generated from the input data.</p>
	                        <p>With the help of these charts you can easily locate gaps in the construction and monitor the work done on any given stretch.</p>
	                    </div>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6" style="font-size: 14px;">
                        <h3 style="font-weight: bold;color:#326bb1;;"><span class="glyphicon glyphicon-cloud"></span>&nbsp;&nbsp;Web based in the cloud</h3>
                        <div style="color:#4b4b4b">
	                        <p>With InfraCMS, you get web based access to your records. You can access them just from anywhere in the world where you have a PC or mobile with the internet connection.</p>
	                    </div>
                    </div>
                    <div class="col-md-6" style="font-size: 14px;">
                        <h3 style="font-weight: bold;color:#326bb1;;"><span class="glyphicon glyphicon-user"></span>&nbsp;&nbsp;Collaborate</h3>
                        <div style="color:#4b4b4b">
                        	<p>Add multiple users to your project, so that they can work along with you on the project.</p>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </section>
    <footer id="footer" >
        <div class="container">
            <img src="<%=request.getContextPath()%>/assets/img/logodark.png" height="40"/>
            &copy; infraCMS 2014 | All Rights Reserved
        </div>
    </footer>
</div> 

<div class="modal fade" id="myModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <h4 class="modal-title" id="myModalLabel">Message</h4>
      </div>
      <div class="modal-body">
        	<div ng-show="modal.error" class="alert alert-danger">
		      <strong>Error!</strong>
		    </div>
		    <div ng-show="modal.success" class="alert alert-success">
		      <strong>Success!</strong>
		    </div>
		    <div ng-bind="modal.message"></div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal">Ok</button>
      </div>
    </div>
  </div>
</div>
<script>mixpanel.track("Index page loaded");</script>
</body>
<script src="<%=request.getContextPath()%>/assets/js/jquery-1.9.1.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/bootstrap.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/angular.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
<script>
var indexApp = angular.module('indexApp', []);

indexApp.controller('IndexController', function ($scope) {
	$scope.register = function() {
		mixpanel.track("Register click");
		$scope.modal = {};
		$scope.modal.success = false;
		$scope.modal.message = "Try again later. Sorry for the inconvinence caused.";
		$scope.showValidationErrors = true;
		var url = "register";
		if (!$scope.registrationForm.$invalid) {
			$scope.showValidationErrors = false;
			$.ajax({
			  type: "POST",
			  url: url,
			  async: false,
			  data: JSON.stringify($scope.user),
			  contentType: "application/json",
			  dataType: "json",
			  success: function(data) {
				  if (data.success) {
				  		$scope.modal.success = true;
				  		$scope.modal.message = "Congratulations! You have been successfully registered. Please check your email to activate your account.";
				  		$('#myModal').modal();
				  } else {
					  $scope.modal.error = true;
					  if (data.messages && data.messages.length > 0) {
						  $scope.modal.message = data.messages[0];
						  $('#myModal').modal();
					  }
				  }
			  },
			  error: function() {
				  $scope.modal.error = true;
				  $('#myModal').modal();
			  }
			});
		}
	};
});
</script>
