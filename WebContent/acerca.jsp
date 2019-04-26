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
	}%>
	
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
						<li><a href="inicio.jsp"> Principal </a></li>
						<li><%= aNavElementoCuentaReg %></li>
						<li><a href="acerca.jsp" style="color: #fe6c00;"> Acerca del Sitio </a></li>
					</ul>
				</nav>
			</section>
		</header>
		<div style="background: #546E7A; padding: 10px; margin: 5px;">
			<table>
				<tr>
					<td width=auto align="right"> Alumno </td>
					<td width=100%>: Vladimir Andriyenko</td>
				</tr>			
				<tr>
					<td width=auto align="right"> Matrícula </td>
					<td width=100%>: 201336828</td>
				</tr>
				<tr>
					<td width=auto align="right">Build</td>
					<td width=100%>: 1.0</td>
				</tr>
				<tr>
					<td width=auto align="right"> Universidad </td>
					<td width=100%>: Benemérita Universidad Autónoma de Puebla. </td>
				</tr>
				<tr>
					<td width=auto align="right"> Asignatura </td>
					<td width=100%>: Aplicaciones Web  </td>
				</tr>
				<tr>
					<td width=auto align="right"> Maestro </td>
					<td width=100%>: DR. MARIO ROSSAINZ LÓPEZ</td>
				</tr>
			</table>
		</div>
		<div align="center" style=" width:100%;">
			<div align="left" style="background: #546E7A;width:800px;padding: 10px;">
				<h1>Copyright 2019 Vladimir Andriyenko (vmchoice@gmail.com)
				</h1><br>
				<p>Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
				</p><br>
				<p>The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
				</p><br>
				<p>THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
				</p>
			</div>
		</div>
	</body>
</html>