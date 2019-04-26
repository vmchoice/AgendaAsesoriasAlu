package com.asesoriasWA.servlets;
///CopyRight @ acerca.jsp (vmchoice@gmail.com)
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.asesoriasWA.jsps.Utilidades;


@WebServlet("/ActualizarSesion")
public class ActualizarSesion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static boolean driverSet = false;
      
  public ActualizarSesion() {
  	super();
    try {
    	if( !driverSet)
    		driverSet = Utilidades.setDriver();
    } catch(Exception ex) {
    System.out.println("driverDet: " + ex.getMessage() );
    }
  }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		StringBuilder respuesta = new StringBuilder("fallo: ");
		String 	matricula;
		char tipoDeConsulta;
		StringBuilder consulta = null;
		HttpSession session = request.getSession(false);
		Connection conn = null;
		Statement stmt =null;
		
		
		if( Utilidades.validarURL( request ) ) {
			respuesta.append( "Uno de tus campos contien ' o \\ favor de retirarlo!" );
		} else if( session != null) {
			tipoDeConsulta = request.getParameter("tipoConsulta").charAt(0);
			matricula = session.getAttribute("matricula").toString();
			
			respuesta.append("Session Existe: " );
			switch( tipoDeConsulta ) {
				case 'u': //Maestro Actualiza SOLO si le pertenese
					consulta = actualizarAs( request, matricula );
				break;
				case 'i': //Alumno Inserta TRIGGER no permite fechas pasadas
					consulta = insertarAs( request, matricula );
				break;
				case 'b': //Alumno Borra SOLO Dueño y fecha inmediata
					consulta = borrarAs( request, matricula );
				break;
				default:
					if( Utilidades.DEB ) respuesta.append("Tipo De Consulta inexistente '"+ request.getParameter("tipoConsulta") +"': " );
				break;
			}
			try {
				if( consulta != null ) {
					if( Utilidades.DEB )  System.out.println(consulta.toString());
					conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app_web_pf_rossains?" +
			               "user=client_ross&password=client_ross&useLegacyDatetimeCode=false&serverTimezone=America/Mexico_City"
			               + "&allowMultiQueries=true");
					conn.setAutoCommit(false);
					stmt = conn.createStatement();
		    	stmt.execute( consulta.toString() );
		    	conn.commit();
		    	
		    	respuesta = new StringBuilder("0");
				}
			} catch (SQLException ex) {
					try {  conn.rollback();  } catch (SQLException e) {}
					
					if( Utilidades.DEB )  respuesta.append(consulta);
			    respuesta.append( "SQLException: " + ex.getMessage());
			    System.out.println("SQLException: " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());
			} finally {
			    if (stmt != null) {  try {  stmt.close();  } catch (SQLException sqlEx) { }  stmt = null;   }
			    if (conn != null) {  try {  conn.close();  } catch (SQLException sqlEx) { }  conn = null;   }
			}
		} else {
			respuesta = new StringBuilder("expiro su Sesion");
		}
		response.getWriter().append( respuesta );
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	private StringBuilder actualizarAs( HttpServletRequest request, String matricula ) {
		StringBuilder consulta = new StringBuilder("UPDATE asesoria SET estatus='").append( request.getParameter("estatus") );
		
		
		if(  request.getParameter("comentario").length() > 0 ) {
			consulta.append( "',estatus_comentario='" ).append( request.getParameter("comentario") ).append( "'" );
		} else {
			consulta.append( "',estatus_comentario=NULL" );
		}
		consulta.append( " WHERE id_asesoria='" ).append( request.getParameter("asesoria") ).append( "' AND fk_ms_matricula='" );
		consulta.append( matricula ).append( "' AND tiempo_asesoria_unix > UNIX_TIMESTAMP(NOW());" );
		return consulta;
	}
	private StringBuilder insertarAs( HttpServletRequest request, String matricula ) {
		String	nombre_completo = request.getParameter( "nombre_completo" ),
						asignatura = request.getParameter( "asignatura" );
		StringBuilder consulta = new StringBuilder("INSERT IGNORE INTO asignatura (asignatura) VALUES ('" ).append( asignatura );
		
		consulta.append( "'); INSERT INTO asesoria (fk_al_matricula, asunto_asesoria, es_tu_maestro, tiempo_asesoria_unix, "
				+ "fk_asignatura, fk_ms_matricula, estatus_comentario ) VALUES ('" ).append( matricula ).append( "','" );
		consulta.append( request.getParameter( "asunto_asesoria" ) ).append( "','" );
		consulta.append( request.getParameter( "es_tu_maestro" ) ).append( "','" );
		consulta.append( request.getParameter( "tiempo_asesoria" ) ).append( "',( SELECT id_asignatura FROM asignatura WHERE asignatura='" );
		consulta.append( asignatura ).append( "'),(SELECT id_ms_matricula FROM maestro WHERE '" ).append( nombre_completo  );
		consulta.append( "' LIKE CONCAT('%',ms_paterno) AND '" ).append( nombre_completo  ).append( "' LIKE CONCAT( ms_nombre, '%')), NULL)" );
		return consulta;
	}
	private StringBuilder borrarAs( HttpServletRequest request, String matricula ) {
		StringBuilder consulta = new StringBuilder("DELETE FROM asesoria WHERE id_asesoria = '").append( request.getParameter("asesoria") );
		
		
		consulta.append( "' AND fk_al_matricula = '").append( matricula ).append( "';" );
		return consulta;
	}
}
