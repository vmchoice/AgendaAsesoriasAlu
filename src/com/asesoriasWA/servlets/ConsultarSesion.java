package com.asesoriasWA.servlets;
///CopyRight @ acerca.jsp (vmchoice@gmail.com)
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.asesoriasWA.jsps.Utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/ConsultarSesion")
public class ConsultarSesion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static boolean driverSet = false;
    
    public ConsultarSesion() {
        super();
        try {
        	if( !driverSet)
        		driverSet = Utilidades.setDriver();
        } catch(Exception ex) {
		    System.out.println("driverDet: " + ex.getMessage() );
        }
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String	matricula,
						tipo;
		StringBuilder	respuesta = new StringBuilder("fallo: "),
									consulta = null;
		HttpSession session;
		ResultSet rs = null;
		Connection conn = null;
		Statement stmt =null;
		boolean consultaLista = true;
		
		
		if( Utilidades.validarURL( request ) ) {
			respuesta.append( "Uno de tus campos contien ' o \\ favor de retirarlo!" );
		} else {
			session = request.getSession(false);
			if( session != null) {
				respuesta.append("Session Existe: " );
				
				matricula = session.getAttribute("matricula").toString();
				tipo = session.getAttribute("tipo").toString();
				switch( request.getParameter("tipoConsulta").charAt(0) ) {
					case 'p':
						consulta = generarConsultaAP(tipo, matricula);
					break;
					case 'a':
						consulta = generarConsultaAA(tipo, matricula);
					break;
					case 'r':
						consulta = generarConsultaAR(tipo, matricula);
					break;
					case 'h':
						consulta = generarConsultaAH(tipo, matricula);
					break;
					case 'm':
						consulta = generarConsultaMat( );
					break;
					case 'n':
						consulta = generarConsultaAsig( );
					break;
					case 's': ///Salir de Session
						session.invalidate();
						respuesta = new StringBuilder("salio");
						consultaLista = false;
					break;
					default:
						respuesta.append("Tipo De Consulta inexistente '"+ request.getParameter("tipoConsulta") +"': " );
						consultaLista = false;
					break;
				}
				try {
					if( consultaLista ) {
						if( Utilidades.DEB )  System.out.println(consulta.toString());
						conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app_web_pf_rossains?" +
				               "user=client_ross&password=client_ross&useLegacyDatetimeCode=false&serverTimezone=America/Mexico_City");
				    conn.setAutoCommit(false);
								stmt = conn.createStatement();
								rs = stmt.executeQuery( consulta.toString() );
						conn.commit();
						
				    response.setContentType("application/json; charset=UTF-8");
						response.setCharacterEncoding("UTF-8");
						JSONArray jsonArray = new JSONArray();
						while (rs.next()) {
							int total_columns = rs.getMetaData().getColumnCount();
							JSONObject obj = new JSONObject();
							for (int i = 1; i <= total_columns; i++) {
							    obj.put(rs.getMetaData().getColumnLabel(i).toLowerCase(), rs.getObject(i));
							}
						    jsonArray.put(obj);
						}
						jsonArray.write(response.getWriter());
					}
				} catch (SQLException ex) {
						try {  conn.rollback();  } catch (SQLException e) {}
						consultaLista = false;
						
				    System.out.println("SQLException: " + ex.getMessage());
				    respuesta.append(", SQLException: " + ex.getMessage());
				    System.out.println("SQLState: " + ex.getSQLState());
				    System.out.println("VendorError: " + ex.getErrorCode());
				} catch (JSONException ex) {
						consultaLista = false;
						
				    System.out.println("JSONException: " + ex.getMessage());
				    respuesta.append(", SQLException: " + ex.getMessage());
				} finally {
			    if (stmt != null) {  try {  stmt.close();  } catch (SQLException sqlEx) { }  stmt = null;   }
			    if (conn != null) {  try {  conn.close();  } catch (SQLException sqlEx) { }  conn = null;   }
				}
			} else {
				respuesta = new StringBuilder("expiro su Sesion");
				consultaLista = false;
			}

		}
		if( !consultaLista ) {
			response.getWriter().append( respuesta );
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private StringBuilder generarConsultaAP(String tipo, String matricula) {
		StringBuilder consulta  = null;
		if(tipo.equals("Ms")) {
			consulta = new StringBuilder( "SELECT id_asesoria, tiempo_asesoria_unix AS tiempo_asesoria, estatus_comentario," +
					"asunto_asesoria, estatus, asignatura, es_tu_maestro,al_nombre AS nombre, al_paterno AS paterno, programa_educativo " + 
					"FROM asesoria LEFT OUTER JOIN asignatura ON (fk_asignatura=id_asignatura) LEFT OUTER JOIN alumno " + 
					"ON (fk_al_matricula=id_al_matricula) LEFT OUTER JOIN maestro ON (fk_ms_matricula=id_ms_matricula) " + 
					"WHERE fk_ms_matricula = '" ); //Lo unico que cambia en las consultas es el id maestro/alumno.
		} else {
			consulta = new StringBuilder( "SELECT id_asesoria, tiempo_asesoria_unix AS tiempo_asesoria, estatus_comentario," +
					"asunto_asesoria, estatus, asignatura, es_tu_maestro, programa_educativo, ms_nombre AS nombre, ms_paterno AS paterno, programa_educativo " + 
					"FROM asesoria LEFT OUTER JOIN asignatura ON (fk_asignatura=id_asignatura) LEFT OUTER JOIN alumno " + 
					"ON (fk_al_matricula=id_al_matricula) LEFT OUTER JOIN maestro ON (fk_ms_matricula=id_ms_matricula) " + 
					"WHERE fk_al_matricula = '" ); //Lo unico que cambia en las consultas es el id maestro/alumno.
		}
		consulta.append( matricula );
		consulta.append( "' AND tiempo_asesoria_unix >= UNIX_TIMESTAMP(NOW()) AND estatus is NULL " + 
				"ORDER BY tiempo_asesoria_unix DESC;");
		return consulta;
	}
	private StringBuilder generarConsultaAA(String tipo, String matricula) {
		StringBuilder consulta  = null;
		if(tipo.equals("Ms")) {
			consulta = new StringBuilder( "SELECT id_asesoria, tiempo_asesoria_unix AS tiempo_asesoria, estatus_comentario," +
					"asunto_asesoria, estatus, asignatura, es_tu_maestro,al_nombre AS nombre, al_paterno AS paterno, programa_educativo " + 
					"FROM asesoria LEFT OUTER JOIN asignatura ON (fk_asignatura=id_asignatura) LEFT OUTER JOIN alumno " + 
					"ON (fk_al_matricula=id_al_matricula) LEFT OUTER JOIN maestro ON (fk_ms_matricula=id_ms_matricula) " + 
					"WHERE fk_ms_matricula = '" ); //Lo unico que cambia en las consultas es el id maestro/alumno.
		} else {
			consulta = new StringBuilder( "SELECT id_asesoria, tiempo_asesoria_unix AS tiempo_asesoria, estatus_comentario," +
					"asunto_asesoria, estatus, asignatura, es_tu_maestro, programa_educativo, ms_nombre AS nombre, ms_paterno AS paterno, programa_educativo " + 
					"FROM asesoria LEFT OUTER JOIN asignatura ON (fk_asignatura=id_asignatura) LEFT OUTER JOIN alumno " + 
					"ON (fk_al_matricula=id_al_matricula) LEFT OUTER JOIN maestro ON (fk_ms_matricula=id_ms_matricula) " + 
					"WHERE fk_al_matricula = '" ); //Lo unico que cambia en las consultas es el id maestro/alumno.
		}
		consulta.append( matricula );
		consulta.append( "' AND tiempo_asesoria_unix >= UNIX_TIMESTAMP(NOW()) AND estatus " + 
				"ORDER BY tiempo_asesoria_unix DESC;");
		return consulta;
	}
	private StringBuilder generarConsultaAR(String tipo, String matricula) {
		StringBuilder consulta  = null;
		if(tipo.equals("Ms")) {
			consulta = new StringBuilder( "SELECT id_asesoria, tiempo_asesoria_unix AS tiempo_asesoria, estatus_comentario," +
					"asunto_asesoria, estatus, asignatura, es_tu_maestro,al_nombre AS nombre, al_paterno AS paterno, programa_educativo " + 
					"FROM asesoria LEFT OUTER JOIN asignatura ON (fk_asignatura=id_asignatura) LEFT OUTER JOIN alumno " + 
					"ON (fk_al_matricula=id_al_matricula) LEFT OUTER JOIN maestro ON (fk_ms_matricula=id_ms_matricula) " + 
					"WHERE fk_ms_matricula = '" ); //Lo unico que cambia en las consultas es el id maestro/alumno.
		} else {
			consulta = new StringBuilder( "SELECT id_asesoria, tiempo_asesoria_unix AS tiempo_asesoria, estatus_comentario," +
					"asunto_asesoria, estatus, asignatura, es_tu_maestro, programa_educativo, ms_nombre AS nombre, ms_paterno AS paterno, programa_educativo " + 
					"FROM asesoria LEFT OUTER JOIN asignatura ON (fk_asignatura=id_asignatura) LEFT OUTER JOIN alumno " + 
					"ON (fk_al_matricula=id_al_matricula) LEFT OUTER JOIN maestro ON (fk_ms_matricula=id_ms_matricula) " + 
					"WHERE fk_al_matricula = '" ); //Lo unico que cambia en las consultas es el id maestro/alumno.
		} 
		consulta.append( matricula ).append( "' AND tiempo_asesoria_unix >= UNIX_TIMESTAMP(NOW()) AND NOT estatus " + 
				"ORDER BY tiempo_asesoria_unix DESC;");
		return consulta;
	}
	private StringBuilder generarConsultaAH(String tipo, String matricula) {
		StringBuilder consulta  = null;
		if(tipo.equals("Ms")) {
			consulta = new StringBuilder( "SELECT id_asesoria, tiempo_asesoria_unix AS tiempo_asesoria, estatus_comentario," +
					"asunto_asesoria, estatus, asignatura, es_tu_maestro,al_nombre AS nombre, al_paterno AS paterno, programa_educativo " + 
					"FROM asesoria LEFT OUTER JOIN asignatura ON (fk_asignatura=id_asignatura) LEFT OUTER JOIN alumno " + 
					"ON (fk_al_matricula=id_al_matricula) LEFT OUTER JOIN maestro ON (fk_ms_matricula=id_ms_matricula) " + 
					"WHERE fk_ms_matricula = '" ); //Lo unico que cambia en las consultas es el id maestro/alumno.
		} else {
			consulta = new StringBuilder( "SELECT id_asesoria, tiempo_asesoria_unix AS tiempo_asesoria, estatus_comentario," +
					"asunto_asesoria, estatus, asignatura, es_tu_maestro, programa_educativo, ms_nombre AS nombre, ms_paterno AS paterno, programa_educativo " + 
					"FROM asesoria LEFT OUTER JOIN asignatura ON (fk_asignatura=id_asignatura) LEFT OUTER JOIN alumno " + 
					"ON (fk_al_matricula=id_al_matricula) LEFT OUTER JOIN maestro ON (fk_ms_matricula=id_ms_matricula) " + 
					"WHERE fk_al_matricula = '" ); //Lo unico que cambia en las consultas es el id maestro/alumno.
		}
		consulta.append( matricula ).append( "' AND tiempo_asesoria_unix < UNIX_TIMESTAMP(NOW()) " + 
				"ORDER BY tiempo_asesoria_unix DESC;");
		return consulta;
	}
	private StringBuilder generarConsultaAsig() {
		return new StringBuilder( "SELECT asignatura AS elemnto FROM asignatura ORDER BY elemnto ASC;" );
	}
	private StringBuilder generarConsultaMat() {
		return new StringBuilder( "SELECT CONCAT( ms_nombre, ' ', ms_paterno ) elemnto FROM maestro ORDER BY elemnto ASC;" );
	}
}
