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
		aNavElementoCuentaReg = "<a href=\"cuenta.jsp\" style=\"color: #fe6c00;\"> Mi Cuenta </a>";
	} else {
		aNavElementoCuentaReg = "<a href=\"registro.jsp\" style=\"color: #fe6c00;\"> Ingresar a tu cuenta </a>";
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
						<li><a href="inicio.jsp"> Principal </a></li>
						<li><%= aNavElementoCuentaReg %></li>
						<li><a href="acerca.jsp"> Acerca del Sitio </a></li>
					</ul>
				</nav>
			</section>
		</header>
		<div align="center">
			<div style="display: inline-flex;">
				<div id="contenedorIngresar" align="left" class="login">
					<form  action="#" onsubmit="return enviar_formaIngresar(this);">
						<h2><span>Proporcione el tipo de  <br/> Matrícula:</span></h2>
						<input id="campoTipo1" type="radio" name="tipoDeCuenta" value="Ms" style="min-height: 20px; max-width: 20px;" required/>Maestro
						<input id="campoTipo2" type="radio" name="tipoDeCuenta" value="Al" style="min-height: 20px; max-width: 20px;" required/>Alumno
						<p>
							<span>Matrícula:</span>
							<input id="campoMatricula1" type="number" style="min-width: 100%;" required value="201336828" name="matricula" required min="190000000" max="300000000"/>
							<span id="errorMat1" class="error"></span>
						</p>
						<p>
							<span>Vuelva a ingresar su Matrícula:</span>
							<input id="campoMatricula2" type="number" style="min-width: 100%;" required value="201336828" min="190000000" max="300000000"/>
							<span id="errorMat2" class="error"></span>
						</p>
						<input id="ingresar" type="submit" value="Ingresar" />
					</form>
				</div>
				<div id="contenedorRegistrar" align="left">
					<form id="formaRegistro"  action="#" onsubmit="return enviar_formaRegistro(this);">
						<h1><span>¡Aún NO estas registrado!</span></h1>
						<h2><span>Proporciona tus datos para <br> registrarte:</span></h2>
						<p>
							<span>Nombre:</span>
							<input id="campoNombre" type="text" style="min-width: 100%;" name="nombre" required pattern="[A-Za-z]{3,}">
							<span id="errorNom" class="error"></span>
						</p>
						<p>
							<span>Apellido Paterno:</span>
							<input id="campoPaterno" type="text" style="min-width: 100%;" name="paterno" required  pattern="[A-Za-z]{3,}">
							<span id="errorPat" class="error"></span>
						</p>
						<p>
							<span>Apellido Materno:</span>
							<input id="campoMaterno" type="text" style="min-width: 100%;" name="materno" pattern="[A-Za-z]{3,}">
							<span id="errorMat" class="error"></span>
						</p>
						<p>
                            <span>Programa Educativo:</span>
							<select id="campoProgEdu" name="progEdu" required style="width: 100%;">
    							<option value="">Selecciona su Programa Educativo</option>
								<option value="0">LCC</option>
								<option value="1">ICC</option>
								<option value="2">ITI</option>
								<option value="3">Otro</option>
							</select><span id="errorEdu" class="error"></span>
						</p>
						<input id="registrar" type="submit" value="Registrar" style="margin-top: 10px;">
					</form>
				</div>
			</div>
		</div>
		
		<script>
			var campoTipo1 = document.getElementById("campoTipo1");
			var campoTipo2 = document.getElementById("campoTipo2");
			var campoMatricula1 = document.getElementById("campoMatricula1");
			var errorMat1 = document.getElementById("errorMat1");
			var campoMatricula2 = document.getElementById("campoMatricula2");
			var errorMat2 = document.getElementById("errorMat2");
			
			var contenedorRegistrar = document.getElementById("contenedorRegistrar");
			var valorMatricula;
			var valorTipo;
			
			var handlerCampoMatricula1 = function (event) {
				if (campoMatricula1.validity.valid) {
					errorMat1.innerHTML = "";
					errorMat1.className = "error"; // Reset the visual state of the message
				} else {
					errorMat1.innerHTML = "¡Proporcione una Matrícula valida, mayor a 190'000'000, sin comillas(') ni caracteres extra!";
					errorMat1.className = "error active";
				}
				if(campoMatricula1.value != campoMatricula2.value) {
					errorMat2.innerHTML = "¡Las Matrículas no coinciden; Revise su Matrícula!";
					errorMat2.className = "error active";
				} else {
					errorMat2.innerHTML = "";
					errorMat2.className = "error";
				}
			};
			campoMatricula1.addEventListener("keyup", handlerCampoMatricula1, false);
			campoMatricula1.addEventListener("mouseup", handlerCampoMatricula1, false);
			
			var handlerCampoMatricula2 = function (event) {
				var ban = false;
				if (!campoMatricula2.validity.valid) {
					errorMat2.innerHTML = "¡Proporcione una Matrícula valida, mayor a 190'000'000, sin comillas(') ni caracteres extra!";
					ban = true;
				} 
				if(campoMatricula1.value != campoMatricula2.value) {
					errorMat2.innerHTML = "¡Las Matrículas no coinciden; Revise su Matrícula!";
					ban = true;
				}
				if( ban ) {
					errorMat2.className = "error active";
				} else {
					errorMat2.innerHTML = "";
					errorMat2.className = "error"; // Reset the visual state of the message
				}
			}
			campoMatricula2.addEventListener("keyup", handlerCampoMatricula2, false);
			campoMatricula2.addEventListener("mouseup", handlerCampoMatricula2, false);
			
			function enviar_formaIngresar(formaIngresar) {
				var campoSiEsMaestro = document.getElementById("campoTipo1");
				var mat1 = document.getElementById("campoMatricula1");
				var mat2 = document.getElementById("campoMatricula2");
				
				if(  !(!campoSiEsMaestro.validity.valid || !mat1.validity.valid || !mat2.validity.valid 
						|| mat2.value != mat1.value ) ) {
					document.getElementById("ingresar").disabled = true;
					
			        valorMatricula = campoMatricula1.value;
			        valorTipo = tipoDeCuenta();

			        var xhr = new XMLHttpRequest();
			        xhr.onreadystatechange = function () {
					    if(xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200){
					        var res = xhr.responseText;
					        if( res.substring(0, 8) == "noExiste" ) {
					        	if( campoTipo1.checked ) {
					        		
									inicioRegistro("Ms");
									limpiezaPostInicio();
					        	} else if(campoTipo2.checked) {
									
									inicioRegistro("Al");
									limpiezaPostInicio();
					        	} else {
						        	window.alert( "Existe un error al selecciona el tipo de Session: "+ campoTipo1.value );
					        	}
					        } else if( res.substring(0, 6) == "inicio" ) {
					        	window.alert( "¡En teoria no puedes ver este mensaje ya que Iniciaste Session! Y JSP tenia que ejecutarse." );
					        } else if( res.substring(0, 5) == "fallo") {
					        	window.alert( res );
					        } else {
					        	sobreEscribirPag( xhr.responseText );
					        }
					    }
					};
			        xhr.open("POST", "IniciarSesion?matricula=" + encodeURIComponent(valorMatricula) +
			        		"&tipoDeCuenta=" + encodeURIComponent(valorTipo) , true);
					xhr.send(null);				
				}
				return false;
			}
			function limpiezaPostInicio() {
				document.getElementById("ingresar").style.display = "none";
				
				campoMatricula1.disabled  = true;
				campoMatricula2.disabled  = true;
				campoTipo1.disabled  = true;
				campoTipo2.disabled  = true;
				campoMatricula1.removeEventListener("mouseup", handlerCampoMatricula1);
				campoMatricula2.removeEventListener("mouseup", handlerCampoMatricula2); 
				campoMatricula1.removeEventListener("keyup", handlerCampoMatricula1);
				campoMatricula2.removeEventListener("keyup", handlerCampoMatricula2);
			}
			function inicioRegistro(tipo) {
				if(tipo == 'Ms') {
					var campoProgEdu = document.getElementById("campoProgEdu" );
					campoProgEdu.removeAttribute("required");
					campoProgEdu.value = "relleno";
					campoProgEdu.style.display = "none";
				}
				contenedorRegistrar.style.display = "inline";
			}
			function tipoDeCuenta() {
				if( campoTipo1.checked ) {
					valorTipo = "Ms";
					return "Ms";
				} else {
					valorTipo = "Al";
					return "Al";
				}
			}
			function enviar_formaRegistro(formaRegistro) {
				if( formaRegistro.checkValidity ) {
					//debugger;
					ingresar.disabled = true;
			        var xhr = new XMLHttpRequest();
			        xhr.onreadystatechange = function () {
					    if(xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200){
					        var res = xhr.responseText;
					        if( res.substring(0,5) == "fallo" ) {
					        	window.alert( res );
					        } else {
					        	sobreEscribirPag( xhr.responseText );
					        }
					    }
					};
			        xhr.open("POST", "RegistrarSesion?matricula=" + encodeURIComponent( valorMatricula ) +
			        		"&tipoDeCuenta=" + encodeURIComponent( valorTipo ) +
			        		"&nombre=" + encodeURIComponent(document.getElementById("campoNombre").value) +
			        		"&paterno=" + encodeURIComponent(document.getElementById("campoPaterno").value) +
			        		"&materno=" + encodeURIComponent(document.getElementById("campoMaterno").value) +
			        		"&progEdu=" + encodeURIComponent(document.getElementById("campoProgEdu").value), true);
					xhr.send(null);
				}
				return false;
			}
			function sobreEscribirPag(pagina) {
				window.document.open('text/html');
    		    window.document.write(pagina);
    		    window.document.close();
			}
			
		</script>
	</body>
</html>