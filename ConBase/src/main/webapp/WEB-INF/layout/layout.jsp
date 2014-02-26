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
	    <link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/bootstrap.css">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/font-awesome.min.css">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/custom-theme/jquery-ui-1.10.0.custom.css">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/ui.jqgrid.css">
		<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/main.css">
		<link rel="stylesheet" media="screen" href="<%=request.getContextPath()%>/assets/css/jquery.handsontable.full.css">
	</head>
	
	
	<tiles:insertAttribute name="content" />
	<div id="applicationError" style="display:none;position:absolute;top:0px;width:100%;z-index:99999">
		<div id="appErrorMsg" class="alert alert-danger" style="display:table;margin:auto;"></div>
	</div>
	<div id="applicationSuccess" style="display:none;position:absolute;top:0px;width:100%;z-index:99999">
		<div id="appSuccessMsg" class="alert alert-success" style="display:table;margin:auto;"></div>
	</div>
</html>