<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="ISO-8859-1"%>
<%@ page session="false" %>
<%@page import="javax.servlet.http.HttpSession"%>
<!--CopyRight @ acerca.jsp (vmchoice@gmail.com)-->
<%	String 	matricula = null,
			tipoDeCuenta = null,
			aNavElementoCuentaReg = null;
	HttpSession sesion = request.getSession(false);
	if( sesion != null ) {
		matricula = sesion.getAttribute("matricula").toString();
		tipoDeCuenta = sesion.getAttribute("tipo").toString();
		aNavElementoCuentaReg = "<a href=\"cuenta.jsp\"> Mi Cuenta </a>";
	} else {
		aNavElementoCuentaReg = "<a href=\"registro.jsp\"> Ingresar a tu cuenta </a>";
	}
%>

<!DOCTYPE html>
<html lang="es"><head>
		<meta charset="utf-8">
		<meta name="description" content="Ejemplo de HTML5">
		<meta name="keywords" content="HTML5, CSS3, JavaScript">
		<title>Asesorias PF AppWeb</title>
		<link rel="stylesheet" type="text/css" href="css/estiloprincipal.css">
		<link rel="icon" type="image/png" href="img/favicon-32x32.png" sizes="32x32" />
		<link rel="icon" type="image/png" href="img/favicon-16x16.png" sizes="16x16" />
	</head>
	<body data-gr-c-s-loaded="true">
		<header>
			<section id="herramientas">
				<img id="logo" src="img/logo.png">
				<nav id="menu">
					<ul>
						<li><a href="inicio.jsp" style="color: #fe6c00;"> Principal </a></li>
						<li><%= aNavElementoCuentaReg %></li>
						<li><a href="acerca.jsp"> Acerca del Sitio </a></li>
					</ul>
				</nav>
			</section>
		</header>
		<div>		
			<img src="https://ecuinc.biz/wp-content/uploads/2017/11/index.jpg">
		</div>
		<script>
			local = window.localStorage;
			if(local.getItem("matricula") != null) {
		    	y = document.getElementsByClassName("registro")[0];
		  		y.textContent="Mi Cuenta";
		        y.setAttribute("href", "cuenta.html");
		    }
		</script>
	</body>
</html>