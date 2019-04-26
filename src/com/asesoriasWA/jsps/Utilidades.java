package com.asesoriasWA.jsps;
///CopyRight @ acerca.jsp (vmchoice@gmail.com)
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import com.mysql.jdbc.Driver;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Utilidades {
	private static boolean driverSet = true;
	public final static boolean DEB = true;
	public String 	matricula = null,
			deQuienSonLasAsesorias = null,
			scriptComentario = null,
			scriptBotonesAs = null,
			aNavElementoCuentaReg = null,
			elementoDeAsesorias = "",
			mensajeBienV = "Sin Cuenta",
			numPen = "0",
			numAce = "0",
			numRech = "0",
			numHis = "0",
			funcText = null;
	private static Pattern PatternValidQueryVal = Pattern.compile(".*(?:%5C|[\\']).*");
	char tipo = 'k';
	
	
	public Utilidades(HttpServletRequest request, HttpServletResponse response) {
    try {
    	if( !driverSet)
    		driverSet = setDriver();
    } catch(Exception ex) {
    	System.out.println("driverDet: " + ex.getMessage() );
    }
    
		StringBuilder respuesta = new StringBuilder("fallo: "),
									consulta = null;
		String tipoDeCuenta = null;
		boolean consultaLista = true;
		Connection conn = null;
		Statement stmt =null;
		HttpSession sesion = request.getSession(false);
		ResultSet rs = null;
		
		if( sesion != null ) {
			matricula = sesion.getAttribute("matricula").toString();
			tipoDeCuenta = sesion.getAttribute("tipo").toString();
			aNavElementoCuentaReg = "<a href=\"cuenta.jsp\" style=\"color: #fe6c00;\"> Mi Cuenta </a>";
			mensajeBienV = "<h1>BienVenido:<br>" +  sesion.getAttribute("nombre").toString();
			consulta = generarConsultaCantidad( tipoDeCuenta, matricula );
			try {
				if( consultaLista ) {
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app_web_pf_rossains?" +
			               "user=client_ross&password=client_ross&useLegacyDatetimeCode=false&serverTimezone=America/Mexico_City"
			               + "&allowMultiQueries=true");
					conn.setAutoCommit(false);
								stmt = conn.createStatement();
								rs = stmt.executeQuery( consulta.toString() );
					conn.commit();
					while (rs.next()) {
						switch(rs.getString(1).charAt(0)) {
						case 'p':
							numPen = rs.getString(2);
							break;
						case 'a':
							numAce = rs.getString(2);
							break;
						case 'r':
							numRech = rs.getString(2);
							break;
						case 'h':
							numHis = rs.getString(2);
							break;
						}
					}
				}
			} catch (SQLException ex) {
				try {  conn.rollback();  } catch (Exception e) {}
		    consultaLista = false;
	    
		    System.out.println("SQLException: " + ex.getMessage());
		    respuesta.append(", SQLException: " + ex.getMessage());
		    System.out.println("SQLState: " + ex.getSQLState());
		    System.out.println("VendorError: " + ex.getErrorCode());
				respuesta.append(consulta);
		    
				try {  response.sendRedirect( respuesta.toString() );  } catch (IOException e) { 	}
			} finally {
		    if (stmt != null) {  try {  stmt.close();  } catch (SQLException sqlEx) { }  stmt = null;   }
		    if (conn != null) {  try {  conn.close();  } catch (SQLException sqlEx) { }  conn = null;   }
			}
			if( consultaLista )
			if( tipoDeCuenta.equals("Ms")) {
				scriptComentario = "elemento.push( '<p id=\"com');\n elemento.push( idAs );\n elemento.push( '\">');";
				scriptBotonesAs = " elemento.push( '<div style=\"position:absolute; top:0; right:0;\">');\n" + 
						"if( tipo != 'a' ) {\r\n" + 
						"	elemento.push( '<button onclick=\"cambiaEstatusAs(this,');\r\n" + 
						"	elemento.push( idAs );\r\n" + 
						"		elemento.push( ',\\'1\\')\" >Aceptar</button>');\r\n" + 
						"}\r\n" + 
						"if( tipo != 'r' ) {\r\n" + 
						"	elemento.push( '<button onclick=\"cambiaEstatusAs( this,');\r\n" + 
						"	elemento.push( idAs );\r\n" + 
						"	elemento.push( ',\\'0\\')\">Rechazar</button>');\r\n" + 
						"}";
				deQuienSonLasAsesorias = "Alumno";
				funcText = funcMaestro;
				mensajeBienV += "</h1>\r\n";
								
			} else {
				scriptComentario = "elemento.push( '<p>');";
				scriptBotonesAs = "elemento.push( '<div style=\"position:absolute; top:0; right:0;\"><button onclick=\"borrarAs( this,');\r\n" + 
				"elemento.push( idAs );\r\n" + 
				"elemento.push(')\">Borrar</button></div>');";
				deQuienSonLasAsesorias = "Maestro";
				funcText = funcAlumno;
				mensajeBienV += "</h1>\r\n<button onclick=\"abrirAsesoria()\" style=\"width: 100%;\">Solicitar Asesoria</button>\r\n" + 
						"								<br>";
				elementoDeAsesorias = htmlAsesorias;
			}
		} else {
	    try {
				response.sendRedirect("registro.jsp"); ///cuenta.jsp
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Utilidades" + respuesta.toString());
	}
	public static StringBuilder generarConsultaCantidad(String tipo, String matricula ) {
		StringBuilder consulta;
		
		
		consulta = new StringBuilder(	"SELECT CASE WHEN tiempo_asesoria_unix < UNIX_TIMESTAMP(NOW())" + 
				"	THEN 'h' ELSE CASE WHEN estatus IS NULL THEN 'p' WHEN estatus=true THEN 'a' ELSE 'r' END END AS tipo, COUNT(*) "
				+ "FROM asesoria WHERE ");	
		if( tipo.equals("Ms") ) {
			consulta.append("fk_ms_matricula ='");
		} else {
			consulta.append("fk_al_matricula ='");
		}
		consulta.append( matricula).append("' GROUP BY (tipo);");
		return consulta;
	}
	
	public static boolean validarURL( HttpServletRequest request ) {
		return PatternValidQueryVal.matcher( request.getQueryString() ).matches();
	}
	public static boolean setDriver() {
		try {
			DriverManager.registerDriver(new Driver());
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String funcMaestro = 
			"function cambiaEstatusAs(elemento, asesoria, estatus ) {\r\n" + 
			"			let comentario = prompt(\"(Opcional:) Si dease agrugue un comentario justificando su Accion.\\nCanelar evita la accion tomada.\", '¡Sustituya este texto con su comentario!');\r\n" + 
			"		    if( comentario != null ) {\r\n" + 
			"		    	if( comentario == '¡Sustituya este texto con su comentario!') {\r\n" + 
			"		    		comentario = '';\r\n" + 
			"		    	}\r\n" + 
			"				var xhr = new XMLHttpRequest();\r\n" + 
			"			    elemento = elemento.parentNode.parentNode.parentNode;\r\n" + 
			"			    xhr.open(\"POST\", \"ActualizarSesion?tipoConsulta=u\" +\r\n" + 
			"			        \"&asesoria=\" + encodeURIComponent( asesoria) +\r\n" + 
			"			        \"&estatus=\"+ encodeURIComponent( estatus) +\r\n" + 
			"			        \"&comentario=\" + encodeURIComponent( comentario ), true);\r\n" + 
			"			    xhr.send(null);\r\n" + 
			"			    xhr.onreadystatechange = function () {\r\n" + 
			"			      if(xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200){\r\n" + 
			"			        var res = xhr.responseText;\r\n" + 
			"			        if( confirmacionPorDefecto( res ) ) {\r\n" + 
			"			          } else {\r\n" + 
			"			            window.alert( res );\r\n" + 
			"			          }\r\n" + 
			"			        }\r\n" + 
			"		    	 };\r\n" + 
			"		      }\r\n" + 
			"		    }";
	public static String funcAlumno = 
			"		  function borrarAs(elemento, asesoria ) {\r\n" + 
			"			elemento = elemento.parentNode.parentNode.parentNode;\r\n" + 
			"			table= elemento.childNodes[0];\r\n" + 
			"			asunto = table.getElementsByTagName('td')[0];\r\n" + 
			"            var continuar = confirm(\"Esta seguro de borrar:\\nAsunto: \"+asunto.innerText);\r\n" + 
			"            if( continuar ) {\r\n" + 
			"			    var xhr = new XMLHttpRequest();\r\n" + 
			"			    xhr.open(\"POST\", \"ActualizarSesion?tipoConsulta=b\" +\r\n" + 
			"			        \"&asesoria=\" + encodeURIComponent( asesoria), true);\r\n" + 
			"			    xhr.send(null);\r\n" + 
			"			    xhr.onreadystatechange = function () {\r\n" + 
			"			      if(xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200){\r\n" + 
			"			        var res = xhr.responseText;\r\n" + 
			"			        if( confirmacionPorDefecto( res ) ) {\r\n" + 
			"			        } else {\r\n" + 
			"			          window.alert( res );\r\n" + 
			"			        }\r\n" + 
			"			      }\r\n" + 
			"			    };\r\n" + 
			"          }\r\n" + 
			"		}\r\n" + 
			"			function salirAsesoria() {\r\n" + 
			"				 document.getElementById( \"capaBloqueo\" ).style.display = 'none';\r\n" + 
			"				 document.getElementById( \"solicitarAsesoria\" ).style.display = 'none';\r\n" + 
			"				 document.getElementById( \"cuerpo\" ).removeAttribute(\"style\");\r\n" + 
			"				 document.getElementById( \"cuerpo\" ).scroll = 'yes';\r\n" + 
			"			}\r\n" +
			"			function solicitarAsesoria() {\r\n" + 
			"				var asunto = document.getElementById(\"asunto\");\r\n" + 
			"				var profesor = document.getElementById(\"profesores\");\r\n" + 
			"				var asignatura = document.getElementById(\"asignatura\");\r\n" + 
			"				var campoSiEsMaestro = document.getElementById(\"campoTipo1\");\r\n" + 
			"				var fecha = document.getElementById(\"fecha\");\r\n" + 
			"				var hora = document.getElementById(\"hora\");\r\n" + 
			"				if(  !(!asunto.validity.valid || !profesor.validity.valid || !asignatura.validity.valid \r\n" + 
			"						|| !campoSiEsMaestro.validity.valid || !fecha.validity.valid || !hora.validity.valid) ) {\r\n" + 
			"			        var xhr = new XMLHttpRequest();\r\n" + 
			"			        var d = new Date(fecha.value+'T'+hora.value);\r\n" + 
			"			        xhr.open(\"POST\", \"ActualizarSesion?tipoConsulta=i\" +\r\n" + 
			"			        		\"&asunto_asesoria=\" + encodeURIComponent(asunto.value) +\r\n" + 
			"			        		\"&nombre_completo=\" + encodeURIComponent(profesor.value) +\r\n" + 
			"			        		\"&asignatura=\" + encodeURIComponent(asignatura.value) +\r\n" + 
			"			        		\"&es_tu_maestro=\" + encodeURIComponent( campoSiEsMaestro.checked ? '1': '0') +\r\n" + 
			"			        		\"&tiempo_asesoria=\" + encodeURIComponent( \r\n" + 
			"			        				parseInt((d.getTime() / 1000 ).toFixed(0))), true);\r\n" + 
			"			        xhr.send(null);\r\n" + 
			"			        xhr.onreadystatechange = function () {\r\n" + 
			"					    if(xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200){\r\n" + 
			"					        var res = xhr.responseText;\r\n" + 
			"					        if ( confirmacionPorDefecto( res ) ) {\r\n" + 
			"					        	window.alert( \"Satisfactorio!\" );\r\n" + 
			"					        }\r\n" + 
			"					        salirAsesoria();\r\n" + 
			"					    }\r\n" + 
			"			        };\r\n" + 
			"				} else {\r\n" + 
			"		        	window.alert( \"Campos invalidos\" );\r\n" + 
			"				}\r\n" + 
			"				return false;\r\n" + 
			"			}\r\n" + 
			"function abrirAsesoria() {\r\n" + 
			"				petisonAServidor( 'm' );\r\n" + 
			"				petisonAServidor( 'n' );\r\n" + 
			"				document.getElementById( \"capaBloqueo\" ).style.display = 'none';\r\n" + 
			"\r\n" + 
			"				document.getElementById( \"fecha\" ).min = new Date().toISOString().split(\"T\")[0];\r\n" + 
			"				document.getElementById( \"fecha\" ).value = new Date().toISOString().split(\"T\")[0];\r\n" + 
			"				 document.getElementById( \"capaBloqueo\" ).style.display = 'block';\r\n" + 
			"				 document.getElementById( \"solicitarAsesoria\" ).style.display = 'block';\r\n" + 
			"				 document.getElementById( \"cuerpo\" ).style.overflow = 'hidden';\r\n" + 
			"				 document.getElementById( \"cuerpo\" ).scroll = 'no';\r\n" + 
			"			}\r\n" + 
			"function cargarDataListaWithJSON( tipo, valores ) {\r\n" + 
			"				var arreglo = eval( valores );\r\n" + 
			"				var len = arreglo.length;\r\n" + 
			"				\r\n" + 
			"				if( tipo == 'm') {\r\n" + 
			"					dataList = document.getElementById( \"profesores\" );\r\n" + 
			"				} else {\r\n" + 
			"					dataList = document.getElementById( \"asignaturas\" );\r\n" + 
			"				}\r\n" + 
			"				\r\n" + 
			"				if( valores.substring(0,2) != \"[]\" ) {\r\n" + 
			"					var htmlLista=[];\r\n" + 
			"					htmlLista.push('<option value=\"\">Selecciona al maestro</option>');\r\n" + 
			"					for (var i=0; i<len; i++) {\r\n" + 
			"						htmlLista.push('<option value=\"');\r\n" + 
			"						htmlLista.push( arreglo[i].elemnto );\r\n" + 
			"						htmlLista.push('\">');" +
			"						htmlLista.push( arreglo[i].elemnto );\r\n" + 
			"						htmlLista.push('</option>');\r\n" + 
			"					}\r\n" + 
			"				} else {\r\n" + 
			"					var htmlLista=['<option value=\"No hay Resultados\"></option>'];\r\n" + 
			"				}\r\n" + 
			"				dataList.innerHTML = htmlLista.join(\"\");\r\n" + 
			"			}";
	private static String htmlAsesorias = 
			"<div id=\"capaBloqueo\">\r\n" + 
			"			<a href=\"#\"><img src=\"img/cargando.gif\" id=\"loadingImage\" style=\"position: absolute; z-index: 150; top: 50%; left: 50%; display: block;\"></a>\r\n" + 
			"		</div>\r\n" + 
			"    	<div id=\"solicitarAsesoria\" style=\"display: none;\">\r\n" + 
			"			<form id=\"formAsesoria\" action=\"#\" onsubmit=\"return solicitarAsesoria()\">\r\n" + 
			"				El asunto: \r\n" + 
			"				<br>\r\n" + 
			"				<textarea id=\"asunto\" style=\"width: 100%;height: auto;min-height: 20px;margin: 0px;resize: none;\" wrap=\"soft\" maxlength=\"80\" spellcheck=\"true\" required></textarea><br>	\r\n" + 
			"				Materia/Asignatura de la cual solicita asesoría, si no aparece escríbala: <br>\r\n" + 
			"				<input list=\"asignaturas\" id=\"asignatura\" style=\"min-width: 100%;\" placeholder=\"Escribe / Selecciona\" value=\"\" required pattern=\"[A-Za-z\\s]{6,}\">\r\n" + 
			"				<datalist id=\"asignaturas\">\r\n" + 
			"					<option value=\"Si ve este Elemento su\"></option>\r\n" + 
			"					<option value=\"navegador no cargio\"></option>\r\n" + 
			"					<option value=\"correctamente la lista\"></option>\r\n" + 
			"				</datalist>\r\n" + 
			"				<br>Elija al profesor registrado del cual solicita la asesoría: <br>\r\n" + 
			"				<select id=\"profesores\" style=\"min-width: 100%;\" list=\"profesores\" placeholder=\"Selecciona\" onkeydown=\"return false\" value=\"\" required >\r\n" + 
			"					<option value=\"Si ve este Elemento su\">< /option>\r\n" + 
			"					<option value=\"navegador no cargio\"></option>\r\n" + 
			"					<option value=\"correctamente la lista\"></option>\r\n" + 
			"				</select><br>Seleccione la opcion que le corresponde:<br>\r\n" + 
			"					<input id=\"campoTipo1\" type=\"radio\" name=\"grupo1\" value=\"1\" style=\"min-height: 20px; max-width: 20px;\" required>SÍ es mi maestro \r\n" + 
			"					<input id=\"campoTipo2\" type=\"radio\" name=\"grupo1\" value=\"0\" style=\"min-height: 20px; max-width: 20px;\" required>NO es mi maestro<span id=\"errorTipo\" class=\"error\" aria-live=\"polite\"></span>\r\n" + 
			"				Elija la fecha de la asesoría: \r\n" + 
			"				<br>\r\n" + 
			"					<input id=\"fecha\" type=\"date\" requiered style=\"width: 50%\">\r\n" + 
			"				<br>\r\n" + 
			"				Elija la hora de la asesoría: \r\n" + 
			"   				<br>\r\n" + 
			"   					<input id=\"hora\" type=\"time\" placeholder=\"hrs:mins\" min=\"07:00:00\" max=\"19:40:00\" step=\"1200\" value=\"07:00\" requiered style=\"width: 50%\" />\r\n" + 
			"   				<div style=\"bottom: 0;position:absolute;botom:50px;right:0;\">\r\n" + 
			"					<button class=\"button\" onclick=\"salirAsesoria()\" style=\"margin-bottom: 10px;\">Cancelar</button>\r\n" + 
			"					<input class=\"button\" type=\"submit\" name=\"Solicitar\" style=\"margin-bottom: 10px;margin-right: 10px;\"></input>\r\n" + 
			"				</div>\r\n" + 
			"			</form>\r\n" + 
			"			\r\n" + 
			"		</div>";
}
