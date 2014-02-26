<body ng-app="indexApp" ng-controller="IndexController">
<div id="wrap">
    <%@ include file="../layout/includeHomepageHeader.jsp"%>
    <section id="content" class="no-border">
        <div class="row row-padder home-center-box" style="padding-top: 40px;">
            <div class="container">
                <div class="row">
                    <div class="col-md-7" style="color: white">
                        <img src="<%=request.getContextPath()%>/assets/img/logo2.png"/>

                        <h1 style="margin-bottom: 20px;margin-top: 40px;">Management and monitoring system for construction projects</h1>
                        <ul>
                            <li style="font-size: 2.5em;font-weight: 200;list-style:disc inside ;margin-bottom: 15px;">Roads & Canals</li>
                            <li style="font-size: 2.5em;font-weight: 200;list-style:disc inside ;margin-bottom: 15px;">Bridges</li>
                            <li style="font-size: 2.5em;font-weight: 200;list-style:disc inside ;margin-bottom: 15px;">Buildings</li>
                            <li style="font-size: 2.5em;font-weight: 200;list-style:disc inside ;margin-bottom: 15px;">Hangers</li>
                        </ul>
                    </div>
                    <div class="col-md-5" style="color: white">
                        <ul style="margin-bottom: 110px;">
                            <li class="nav-link"><a href="#">Home</a></li>
                            <li class="nav-link"><a href="#">Features</a></li>
                            <li class="nav-link"><a href="#">Pricing</a></li>
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
                            <h4>By signing up you agree to our Terms & Service</h4>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <div>
            <div class="container">
                <div class="row">
                    <div class="col-md-6" style="font-size: 14px;">
                        <h3 style="font-weight: bold">Monitor layers</h3>
                        <p>ConBase creates layer charts for length based constructions such as roads & canals. The charts gets auto generated from the input data.</p>
                        <p>With the help of these charts you can easily locate gaps in the construction and monitor the work done on any given stretch.</p>
                    </div>
                    <div class="col-md-6" style="font-size: 14px;">
                        <h3 style="font-weight: bold">Monitor structures</h3>
                        <p>Easy to mock the structural drawings, which can be easily associated with input data. It enables you to quick assess the development status of the structure.</p>
                    </div>
                </div>
            </div>

        </div>
    </section>
    <footer id="footer">
        <div class="container">
            <img src="<%=request.getContextPath()%>/assets/img/logo2.png" height="40"/>
            &copy; Copyright ConBase 2013 | All Rights Reserved
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

</body>
<script src="<%=request.getContextPath()%>/assets/js/jquery-1.9.1.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/bootstrap.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/angular.min.js"></script>
<script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
<script>
var indexApp = angular.module('indexApp', []);

indexApp.controller('IndexController', function ($scope) {
	$scope.register = function() {
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
