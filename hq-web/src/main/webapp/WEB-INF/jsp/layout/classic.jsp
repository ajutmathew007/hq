<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" %>
<%@ page errorPage="/common/Error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
		<title>
			<fmt:message>
				<tiles:insertAttribute name="title"/>
			</fmt:message>
		</title>
		<link rel="icon" href="<spring:url value="/static/images/favicon.ico" />" />
		<link rel="stylesheet" href="<spring:url value="/static/js/dojo/1.5/dojo/resources/dojo.css" />" type="text/css">
		<link rel="stylesheet" href="<spring:url value="/static/js/dojo/1.5/dijit/themes/tundra/tundra.css" />" type="text/css">
		<link rel="stylesheet" href="<spring:url value="/static/css/blueprint/screen.css" />" type="text/css" media="screen, projection">
		<link rel="stylesheet" href="<spring:url value="/static/css/blueprint/print.css" />" type="text/css" media="print">	
		<!--[if lt IE 8]><link rel="stylesheet" href="<spring:url value="/static/css/blueprint/ie.css" />" type="text/css" media="screen, projection"><![endif]-->
		<link rel="stylesheet" type="text/css" href="<spring:url value="/static/css/core/layout.css" />">
		<link rel="stylesheet" type="text/css" href="<spring:url value="/static/css/core/type.css" />">
		<link rel="stylesheet" type="text/css" href="<spring:url value="/static/css/core/theme.css" />">
		<!--[if lt IE 8]><link rel="stylesheet" href="<spring:url value="/static/css/core/ie.css" />"><![endif]-->
		<script src="<spring:url value="/static/js/dojo/1.5/dojo/dojo.js" />" type="text/javascript" djConfig="parseOnLoad: true"></script>
		<script src="<spring:url value="/static/js/html5.js" />" type="text/javascript"></script>
	</head>
	<body class="tundra">
   		<div id="header">
   			<tiles:insertAttribute name="header" />
   		</div>
   		<div id="content">
			<tiles:insertAttribute name="content" />
		</div>
		<sec:authorize access="hasRole('ROLE_USER')">
			<div id="footer">
				<tiles:insertAttribute name="footer" />
			</div>
		</sec:authorize>
	</body>
</html>