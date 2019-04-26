<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="ISO-8859-1"%>
<%@ page session="false" %>
<%@page import="com.asesoriasWA.jsps.Utilidades"%>
<!--CopyRight @ acerca.jsp (vmchoice@gmail.com)-->
<%	
	Utilidades elementosDinamicos = new Utilidades(request, response);
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
	<body data-gr-c-s-loaded="true" id="cuerpo">
		<%= elementosDinamicos.elementoDeAsesorias %>
		<header>
			<section id="herramientas">
				<img id="logo" src="img/logo.png">
				<nav id="menu">
					<ul>
						<li><a href="inicio.jsp"> Principal </a></li>
						<li><%= elementosDinamicos.aNavElementoCuentaReg %></li>
						<li><a href="acerca.jsp"> Acerca del Sitio </a></li>
					</ul>
				</nav>
			</section>
		</header>
		<div style=" width: 100%; height: 100%;">		
			<table>
				<tbody>
					<tr>
						<td class="renglonReducido" width="auto" style="height: 100%;">
							<div id="sideContent">
				                <%= elementosDinamicos.mensajeBienV %>
								<nav id="menuAsesorias">
									<ul>
										<li><a onclick="abrirListaAsesorias('p');" href="#" name="elementosMA" id='menuOpcp' style="color: rgb(254, 108, 0);">Asesorías Pendientes (<%= elementosDinamicos.numPen%>)</a></li>
										<li><a onclick="abrirListaAsesorias('a');" href="#" name="elementosMA" id='menuOpca'>Asesorías Aceptadas (<%= elementosDinamicos.numAce %>)</a></li>
										<li><a onclick="abrirListaAsesorias('r');" href="#" name="elementosMA" id='menuOpcr'>Asesorías Rechazadas (<%= elementosDinamicos.numRech %>)</a></li>
										<li><a onclick="abrirListaAsesorias('h');" href="#" name="elementosMA" id='menuOpch'>Historial (<%= elementosDinamicos.numHis %>)</a></li>
									</ul>
								</nav>
								<button onclick="petisonAServidor('s')" style="width: 100%;">Salir de Sesión</button>
							</div>
						</td>
						<td width="100%" style="vertical-align: top;">
							<div id="mainContent">
							</div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		
		<script type="text/javascript">
		  	<%=elementosDinamicos.funcText%>
			function abrirListaAsesorias( tipo ) {
				var seleccionado = document.getElementById('menuOpc'+tipo);
				petisonAServidor( tipo );
				fijarParametro('opcionDelMenu', tipo);
				//Cambio de color
				var elements = document.getElementsByName("elementosMA");
				for(var i=0; i<elements.length; i++) {
					if( elements[i] != seleccionado ) {
						elements[i].removeAttribute("style");
					} else {
						elements[i].style.color = "#fe6c00";
					}
				}
			}
			function petisonAServidor( tipo ) {
		        var xhr = new XMLHttpRequest();
		        xhr.open("POST", "ConsultarSesion?tipoConsulta=" + tipo, true);
		        xhr.send(null);
		        xhr.onreadystatechange = function () {
				    if(xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200){
				        var res = xhr.responseText;
				        if ( confirmacionPorDefecto( res ) ) {
				        	if( tipo == 'm' || tipo == 'n') {
				        		cargarDataListaWithJSON( tipo, res);
				        	} else {
					            procesarListaJSON( tipo, res);
				        	}
				        }
				    }
				};
			}
			function confirmacionPorDefecto( res ) {
				if( res.substring(0,1) == '0' ) { 
					document.location = 'cuenta.jsp'; 
					elemento.innerHTML = '';
				} else if( res.substring(0,5) == "salio" ) {
					document.location = "registro.jsp";
					return false;
		        } else if( res.substring(0,6) == "expiro" ) {
		        	window.alert( 'Su sesion Expiro, Entre denuevo' );
					document.location = "registro.jsp";
					return false;
		        } else if( res.substring(0,5) == "fallo" ) {
		        	window.alert( res );
					return false;
		        }
				return true;
			}
			
			function procesarListaJSON( tipo, lista ) {
				var arreglo = eval( lista );
				var len = arreglo.length;
				var contenedorLista = document.getElementById( "mainContent" );
				
				var htmlLista=['<h1 style="margin-bottom: 5px;">Lista de Asesorías '];
				if( tipo == 'p') {
					htmlLista.push('PENDIENTES por fecha proxima, vigente:</h1>');
				} else if( tipo == 'a') {
					htmlLista.push('ACEPTADAS por fecha proxima, vigente:</h1>');
				} else if( tipo == 'r') {
					htmlLista.push('RECHAZADAS por fecha proxima, vigente:</h1>');
				} else if( tipo == 'h') {
					htmlLista.push('PASADAS por fecha inmediata anterior:</h1>');
				}
				if( lista.substring(0,2) != "[]" ) {
					for (var i=0; i<len; i++) {
						htmlLista.push("<article></article>");
					}
					contenedorLista.innerHTML = htmlLista.join("");
	
					var elementosDeLista = document.getElementsByTagName("article");
					for (var i=0; i<len; i++) {
						var elemento=[];
						var idAs = arreglo[i].id_asesoria;
						elemento.push('<header ');
						if( arreglo[i].estatus != '1' ) {
							if( arreglo[i].estatus != '0' ) {
								elemento.push('style="background: #FFB74D;"'); //Naranja no EValuado
							} else {
								elemento.push('style="background: #e57373;"'); //Rojo Rechazado
							}
						} else {
							elemento.push('style="background: #8BC34A;"'); //Verde Aceptado
						}
						elemento.push('><table><tbody><tr><td>');
						elemento.push(arreglo[i].asunto_asesoria);
						elemento.push('</td><td align="right" >FECHA ');
						elemento.push((new Date(arreglo[i].tiempo_asesoria * 1000)).toLocaleString().replace(',', ', HORA '));
						elemento.push('</td></tr><tr><td>');
						elemento.push(arreglo[i].nombre);
						elemento.push(' ');
						elemento.push(arreglo[i].paterno);
						elemento.push('</td><td align="right"><input type="checkbox" onclick="return false;" ');
						if( arreglo[i].es_tu_maestro ) {
							elemento.push('checked');
						}
						elemento.push('> Es tu <%=elementosDinamicos.deQuienSonLasAsesorias%></td></tr><tr><td>');
						elemento.push(arreglo[i].asignatura);
						elemento.push('</td><td align="right">');
						elemento.push(arreglo[i].programa_educativo);
						elemento.push('</td></tr></tbody></table></header><div style="position:relative; min-height:25px;">');
						if( tipo != 'p' ) {
							if(  arreglo[i].estatus_comentario ) {
								elemento.push( '<details><summary>Ver Comentario</summary>');
								<%= elementosDinamicos.scriptComentario%> /// Identificador del comentario para maestros
								elemento.push(arreglo[i].estatus_comentario);
								elemento.push('</p></details>');
							} else {
								elemento.push( '<p>SIN comentario.</p>');
							}
						}
						if( tipo != 'h') {
							<%= elementosDinamicos.scriptBotonesAs%> /// Botones correspondientes a cada tipo de usuario
						}
						elemento.push('</div>');
						elementosDeLista[i].innerHTML = elemento.join("");
					}var elemento=[];
				} else {
					htmlLista.push( '<article><h1 style="background-color: #FFD54F;color: #d50000;">UPSS ¡No tienes Asesorias en esta Categoria!</h1></article>');
					contenedorLista.innerHTML = htmlLista.join("");
				}
			}
			function obtenerParametro( key ) {
				local = window.localStorage;
				if(local.getItem(key) == null) {
			    	return 'p'
			    } else {
			    	return local.getItem(key);
			    }
			}
			function fijarParametro( key, val ) {
				local = window.localStorage;
				local.setItem(key, val)
			}
			abrirListaAsesorias( obtenerParametro( 'opcionDelMenu' ) );
		</script>
	</body>
</html>