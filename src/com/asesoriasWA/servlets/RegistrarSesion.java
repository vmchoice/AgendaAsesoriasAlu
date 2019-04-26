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

@WebServlet("/RegistrarSesion")
public class RegistrarSesion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static boolean driverSet = false;
       
    public RegistrarSesion() {
        super();
        try {
        	if( !driverSet)
        		driverSet = Utilidades.setDriver();
        } catch(Exception ex) {
		    System.out.println("driverDet: " + ex.getMessage() );
        }
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String 	tipo = null,
						matricula = null;
		StringBuilder consulta = null,
				respuesta = new StringBuilder("fallo: ");
		boolean esAlumno = false,
				registrado = false;
		HttpSession session;
		Statement stmt = null;
		Connection conn = null;
		
		
		if( Utilidades.validarURL( request ) ) {
			respuesta.append( "Uno de tus campos contien ' o \\ favor de retirarlo!" );
		} else {
			matricula = request.getParameter( "matricula" );
			tipo = request.getParameter("tipoDeCuenta");
			
			try {
				switch( tipo.toUpperCase().charAt(0) ) {
					case 'A': esAlumno = true;
					case 'M':
						consulta = registrarUsuario( request, matricula, esAlumno );
						if( Utilidades.DEB )  System.out.println(consulta.toString());
						conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app_web_pf_rossains?" +
				               "user=client_ross&password=client_ross&useLegacyDatetimeCode=false&serverTimezone=America/Mexico_City");
						conn.setAutoCommit(false);
									stmt = conn.createStatement();
						    	stmt.execute( consulta.toString() );
			    	conn.commit();
			    	
						session = request.getSession(true);
						session.setAttribute( "matricula", request.getParameter( "matricula" ) );
		    		session.setAttribute( "nombre", request.getParameter( "nombre" ) + ' '+ request.getParameter( "paterno" ) );
						session.setAttribute( "tipo", tipo );
						registrado = true;
					break;
					default:
						respuesta.append( "tipoDeCuenta: ").append( tipo );
					break;
				}
			} catch (SQLException ex) {
					try {  conn.rollback();  } catch (SQLException e) {}
					
					if( Utilidades.DEB )  respuesta.append(", SQLException: " + ex.getMessage());
			    System.out.println("SQLException: " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());
			} finally {
		    if (stmt != null) {  try {  stmt.close();  } catch (SQLException sqlEx) { }  stmt = null;   }
		    if (conn != null) {  try {  conn.close();  } catch (SQLException sqlEx) { }  conn = null;   }
			}
		}
		if( registrado ) {
      request.getRequestDispatcher("/cuenta.jsp").forward(request, response);
		} else {
			response.getWriter().append( respuesta );
		}
		System.out.println("Mensaje Registro: " + respuesta);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	private StringBuilder registrarUsuario(HttpServletRequest request, String matricula, boolean esAlumno) throws SQLException {
		StringBuilder consulta;
		
		
		if( esAlumno )
			consulta = new StringBuilder("INSERT INTO alumno (id_al_matricula,al_nombre,al_paterno,al_materno,programa_educativo) "+
				"VALUES ('" ).append( matricula );
		else
			consulta =  new StringBuilder("INSERT INTO maestro (id_ms_matricula,ms_nombre,ms_paterno,ms_materno) "+
				"VALUES ('" ).append( matricula );
		consulta.append( "','" ).append( request.getParameter( "nombre" ) );
		consulta.append( "','" ).append( request.getParameter( "paterno" ) );
		consulta.append( "','" ).append( request.getParameter( "materno" ) );
		if( esAlumno )
			consulta.append( "','" ).append( request.getParameter( "progEdu" ) );
		consulta.append( "');" );
		return consulta;
	}
}
