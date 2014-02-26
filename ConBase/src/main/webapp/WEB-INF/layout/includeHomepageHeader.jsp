<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page import="com.derive.conbase.security.User, org.springframework.security.core.context.SecurityContextHolder" %>

<header id="header" class="navbar">
       <div class="clearfix" id="top-user-header">
           <ul class="pull-left clearfix" style="margin: 0px;">
               <li class="pull-right" style="padding-right: 10px;"><a style="color:white" href="<%=request.getContextPath() %>/userHome">Home</a></li>
           </ul>
           <ul class="pull-right clearfix" style="margin: 0px;">
               <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
               		<li class="pull-right" style="padding-left: 10px;"><a style="color:white" href="<%=request.getContextPath() %>/login">Login</a></li>
               </sec:authorize>
               <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
               <li class="pull-right" style="padding-left: 10px;"><a style="color:white" href="<%=request.getContextPath() %>/j_spring_security_logout">Logout</a></li>
               <li class="pull-right" style="padding-left: 10px;"><span class="glyphicon glyphicon-user"></span>
                   <%=((com.derive.conbase.security.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getFullName()%>
               </li>
               </sec:authorize>
           </ul>
       </div>
       	<sec:authorize ifNotGranted="ROLE_ANONYMOUS">
       		<h1>
       		<%
       		com.derive.conbase.security.User securityUser = (com.derive.conbase.security.User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
       		if (securityUser != null && securityUser.getCurrentProject() != null && securityUser.getCurrentProject().getName() != null) {
       			out.println(securityUser.getCurrentProject().getName());
       		}
       		%>
       		</h1>
       	</sec:authorize>
       <ul class="project-nav pull-right clearfix">
       	<li><a ng-class="{active : selectedMenu == 'layers'}" href="<%=request.getContextPath() %>/layers">Layers</a></li>
       	<li><a ng-class="{active : selectedMenu == 'structures'}" href="<%=request.getContextPath() %>/structures">Structures</a></li>
       	<li><a ng-class="{active : selectedMenu == 'records'}" href="<%=request.getContextPath() %>/recordTypes">Records</a></li>
       </ul>
</header>