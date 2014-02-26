<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles"%>
<%@ page session="true"%>


<!DOCTYPE html>
<html>
<head>
    <title>ConBase</title>
</head>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/bootstrap.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/font-awesome.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/custom-theme/jquery-ui-1.10.0.custom.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/main.css">
<body>
<div id="wrap" style="background: transparent url(https://www.assembla.com/images/home_featured_bg.png?1388086700) top left;">
    <section id="content" class="no-border">
        <div class="row row-padder" style="padding-top: 40px;background: #F1F0E8;height: 200px;">
            <div class="container">
                <div class="row" style="position: relative;">
                    <div class="col-xs-12 text-center" style="">
                        <img src="<%=request.getContextPath()%>/assets/img/logo2.png"/>
                    </div>
                    <div class="col-xs-12" style="position: absolute;top:110px">
                        <div class="col-sm-5 signup-box" style="margin: auto;float:none;padding: 20px;">
                            <h2 class="text-center" style="margin-bottom: 20px">Sign In</h2>
							Your account has been activated.
                            <form role="form" name="loginForm" action="<c:url value='j_spring_security_check'/>" method="POST">
                                <div class="form-group input-group" style="width:100%">
                                    <span class="input-group-addon glyphicon glyphicon-user" style="top:0px;"></span>
                                    <input name="j_username" type="text" class="form-control" placeholder="Username">
                                </div>
                                <div class="form-group input-group"  style="width:100%">
                                    <span class="input-group-addon glyphicon glyphicon-asterisk" style="top:0px;"></span>
                                    <input type="password" name="j_password" class="form-control" id="exampleInputPassword1" placeholder="Password">
                                </div>
                                <button type="submit" class="btn btn-warning btn-lg btn-block">Log In</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </section>

    <script src="<%=request.getContextPath()%>/assets/js/jquery-1.9.1.min.js"></script>
    <script src="<%=request.getContextPath()%>/assets/js/bootstrap.min.js"></script>

    <script src="<%=request.getContextPath()%>/assets/js/jquery.handsontable.full.js"></script>
    <script src="<%=request.getContextPath()%>/assets/js/main.js"></script>
</div>
<footer id="footer">
    <div class="container">
        <img src="<%=request.getContextPath()%>/assets/img/logo2.png" height="40"/>
        &copy; Copyright ConBase 2013 | All Rights Reserved
    </div>
</footer>
</body>
</html>