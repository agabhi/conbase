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
    <title>infraCMS - Record Management</title>
</head>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/bootstrap.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/font-awesome.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/custom-theme/jquery-ui-1.10.0.custom.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/main.css">
<body>
<div id="wrap" style="background: transparent url(https://www.assembla.com/assets/home_featured_bg-aceeaee1e58c9c6302ada1673384cf7d.png) top left;">
    <section id="content" class="no-border">
        <div class="row row-padder" style="padding-top: 40px;background: #F1F0E8;height: 200px;">
            <div class="container">
            	<div class="row">
                <ul style="margin-left:20px;">
                            <li class="nav-link"><a  style="color:#b27503;" href="<%=request.getContextPath()%>/">Home</a></li>
                            <li class="nav-link"><a style="color:#b27503;" href="<%=request.getContextPath()%>/pricing">Pricing</a></li>
                </ul>
                </div>
                <div class="row" style="position: relative;">
                    <div class="col-xs-12 text-center" style="">
                        <img src="<%=request.getContextPath()%>/assets/img/logodark.png"/>
                    </div>
                    <div class="col-xs-12" style="position: absolute;top:110px">
                        <div class="col-sm-5 signup-box" style="margin: auto;float:none;padding: 20px;">
                            <h2 class="text-center" style="margin-bottom: 20px">Sign In</h2>

                            <form role="form" name="loginForm" action="<c:url value='j_spring_security_check'/>" method="POST">
                                <div class="form-group input-group" style="width:100%">
                                    <span class="input-group-addon glyphicon glyphicon-user" style="top:0px;"></span>
                                    <input name="j_username" type="text" class="form-control" placeholder="Username">
                                </div>
                                <div class="form-group input-group"  style="width:100%">
                                    <span class="input-group-addon glyphicon glyphicon-asterisk" style="top:0px;"></span>
                                    <input type="password" name="j_password" class="form-control" id="exampleInputPassword1" placeholder="Password">
                                </div>
                                <div class="checkbox" style="display:none;">
                                    <label style="line-height: 21px;">
                                        <input type="checkbox"> Remember me
                                    </label>
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
        <img src="<%=request.getContextPath()%>/assets/img/logodark.png" height="40"/>
        &copy; infraCMS 2014 | All Rights Reserved
    </div>
</footer>
</body>
</html>