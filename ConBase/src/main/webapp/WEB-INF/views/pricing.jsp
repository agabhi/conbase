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
<html style="min-height:900px;">
<head>
    <title>infraCMS - Record Management</title>
</head>
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/bootstrap.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/font-awesome.min.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/custom-theme/jquery-ui-1.10.0.custom.css">
<link rel="stylesheet" href="<%=request.getContextPath()%>/assets/css/custom/main.css">
<body>

<!-- start Mixpanel --><script type="text/javascript">(function(e,b){if(!b.__SV){var a,f,i,g;window.mixpanel=b;a=e.createElement("script");a.type="text/javascript";a.async=!0;a.src=("https:"===e.location.protocol?"https:":"http:")+'//cdn.mxpnl.com/libs/mixpanel-2.2.min.js';f=e.getElementsByTagName("script")[0];f.parentNode.insertBefore(a,f);b._i=[];b.init=function(a,e,d){function f(b,h){var a=h.split(".");2==a.length&&(b=b[a[0]],h=a[1]);b[h]=function(){b.push([h].concat(Array.prototype.slice.call(arguments,0)))}}var c=b;"undefined"!==
typeof d?c=b[d]=[]:d="mixpanel";c.people=c.people||[];c.toString=function(b){var a="mixpanel";"mixpanel"!==d&&(a+="."+d);b||(a+=" (stub)");return a};c.people.toString=function(){return c.toString(1)+".people (stub)"};i="disable track track_pageview track_links track_forms register register_once alias unregister identify name_tag set_config people.set people.set_once people.increment people.append people.track_charge people.clear_charges people.delete_user".split(" ");for(g=0;g<i.length;g++)f(c,i[g]);
b._i.push([a,e,d])};b.__SV=1.2}})(document,window.mixpanel||[]);
mixpanel.init("6411320877c544bbe9300a582363f525");</script><!-- end Mixpanel -->


<div id="wrap" class="bluebkg">
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
                        <div class="col-xs-4" ><img src="<%=request.getContextPath()%>/assets/img/logodark.png"/></div>
                        <div class="col-xs-8" style="text-align:left;color:#326bb1;" >
                        	<h2 style="font-weight: bold;text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.3);letter-spacing: -1px;">Packages & Pricing</h2>
                        	<p>
                        		Excellent! You'd like to give infraCMS a try? Simply choose a package from the list below and get started!
							</p>
                        </div>
                        
                    </div>
                    <div class="col-xs-12" style="position: absolute;top:110px">
                        <div class="col-sm-12 signup-box" style="margin: auto;float:none;padding: 20px;">
                            <table class="pricing-table">
                            	<thead>
                            		<tr class="head">
                            			<th style="width:20%"></th>
                            			<th style="width:8%"></th>
                            			<th style="width:18%" class="plan plan-free">Free</th>
                            			<th style="width:18%">Small</th>
                            			<th style="width:18%">Medium</th>
                            			<th style="width:18%">Large</th>
                            		</tr>
                            	</thead>
                            	<tbody>
                            		<tr class="price">
                            			<th class="row-header">How much?</th>
                            			<th><img alt="Group24" class="icon" src="<%=request.getContextPath()%>/assets/img/price24.png"></th>
                            			<td class="plan-free">Free</td>
                            			<td>INR 6000/month</td>
                            			<td>INR 10000/month</td>
                            			<td>INR 15000/month</td>
                            		</tr>
                            		<tr>
                            			<th class="row-header">Users</th>
                            			<th><img alt="Group24" class="icon" src="<%=request.getContextPath()%>/assets/img/users.png"></th>
                            			<td class="plan-free">1</td>
                            			<td>5</td>
                            			<td>10</td>
                            			<td>20</td>
                            		</tr>
                            		<tr>
                            			<th class="row-header">Number of records</th>
                            			<th><img alt="Group24" class="icon" src="<%=request.getContextPath()%>/assets/img/repo24.png"></th>
                            			<td class="plan-free">10000</td>
                            			<td>25000</td>
                            			<td>50000</td>
                            			<td>100000</td>
                            		</tr>
                            		<tr>
                            			<th class="row-header">Data Export</th>
                            			<th><img alt="Group24" class="icon" src="<%=request.getContextPath()%>/assets/img/export24.png"></th>
                            			<td class="plan-free"><img alt="Included" class="icon" src="<%=request.getContextPath()%>/assets/img/tick24.png" title="Included"></td>
                            			<td><img alt="Included" class="icon" src="<%=request.getContextPath()%>/assets/img/tick24.png" title="Included"></td>
                            			<td><img alt="Included" class="icon" src="<%=request.getContextPath()%>/assets/img/tick24.png" title="Included"></td>
                            			<td><img alt="Included" class="icon" src="<%=request.getContextPath()%>/assets/img/tick24.png" title="Included"></td>
                            		</tr>
                            	</tbody>
                            </table>
                            
                            <div style="margin-top:40px;">
                    			<h4 style="font-size: 140%;font-weight: bold;">How do you make the payment?</h4>
                    	
                    	Currently, we are in process of integrating the payment gateways on our website. Meanwhile, you can credit the amount in the below account and call/email us on +91-7895988251/admin@infracms.com to activate your account.
                    	<br>
                    	<br>
                    	<div>
                    		<div class="textwidget">
                    			<font color="#903;"><strong>Name of Account</strong>:</font>
                    			DeriveSoft Technologies<br>
								<font color="#903;"><strong>Account Number:</strong></font> 628701020182<br>
								<font color="#903;"><strong>Bank Branch:</strong> </font> 0840<br>
								<font color="#903;"><strong>IFSC Code:</strong> </font>ORBC0100840<br>
									<br>
								Transfer through NEFT or RTGS<br><br>
								<font color="#903;"><strong>Bank Branch Address:</strong></font><br>
								ICICI Bank<br>
								Sanjay Place<br>
								AGRA<br>
								PIN 282005
								<br>
								<font color="#903;"><strong>Company Address:</strong></font><br>
								D-27 Kamla Nagar<br>
								Agra, Uttar Pradesh<br>
								PIN 282005<br>
				    	</div>
                    	
                    </div>
                        </div>
                    </div>
                    
                </div>
            </div>
        </div>
		</div>
    </section>

    <script src="<%=request.getContextPath()%>/assets/js/jquery-1.9.1.min.js"></script>
    <script src="<%=request.getContextPath()%>/assets/js/bootstrap.min.js"></script>

    <script src="<%=request.getContextPath()%>/assets/js/jquery.handsontable.full.js"></script>
    <script src="<%=request.getContextPath()%>/assets/js/custom/main.js"></script>
</div>
<footer id="footer">
    <div class="container">
        <img src="<%=request.getContextPath()%>/assets/img/logodark.png" height="40"/>
        &copy; infraCMS 2014 | All Rights Reserved
    </div>
</footer>

<script>mixpanel.track("Pricing page loaded");</script>
</body>
</html>