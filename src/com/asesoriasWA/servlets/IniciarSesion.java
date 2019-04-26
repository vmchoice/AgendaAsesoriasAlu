package com.asesoriasWA.servlets;
///CopyRight @ acerca.jsp (vmchoice@gmail.com)
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.asesoriasWA.jsps.Utilidades;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;


@WebServlet("/IniciarSesion")
public class IniciarSesion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static boolean driverSet = false;
       
    public IniciarSesion() {
        super();
        try {
        	if( !driverSet)
        		driverSet = Utilidades.setDriver();
        } catch(Exception ex) {
		    System.out.println("driverDet: " + ex.getMessage() );
        }
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String 	matricula,
						tipo;
		StringBuilder respuesta = new StringBuilder("fallo: "),
									consulta = null;
		boolean inicio = false,
						esAlumno = false;
		HttpSession session;
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		
		
		if( Utilidades.validarURL( request ) ) {
			respuesta.append( "Uno de tus campos contien ' o \\ favor de retirarlo!" );
		} else {
			matricula = request.getParameter("matricula");
			tipo = request.getParameter("tipoDeCuenta");
			try {
				switch( tipo.toUpperCase().charAt(0) ) {
					case 'A': esAlumno = true;
					case 'M':
						consulta = inicioSesion( matricula, esAlumno );
						if( Utilidades.DEB )  System.out.println( consulta.toString() );
						conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app_web_pf_rossains?" +
				               "user=client_ross&password=client_ross&useLegacyDatetimeCode=false&serverTimezone=America/Mexico_City");
						conn.setAutoCommit(false);
								stmt = conn.createStatement();
								rs = stmt.executeQuery( consulta.toString() );
						conn.commit();
						
						System.out.println("Inicio: analizando respuesta ");
			    	if( rs.next() && rs.getString( 1 ).equals( matricula ) ) {
			    		session = request.getSession(true); //Inicio session si no existe
			    		session.setAttribute( "matricula", matricula ); //Respaldo quien es en esta session
			    		session.setAttribute( "nombre", rs.getString( 2 ) );
			    		session.setAttribute( "tipo", tipo );
			    		inicio = true;
			    		System.out.println("Inicio: resp x ");
			    	} else {
			    		respuesta = new StringBuilder( "noExiste" );
			    	}
			    break;
			    default:
						respuesta.append( "tipoDeCuenta: ").append( tipo );
					break;
				}
			} catch (SQLException ex) {
					try {  conn.rollback();  } catch (SQLException e) {}
					
			    System.out.println("SQLException: " + ex.getMessage());
			    respuesta.append(", SQLException: " + ex.getMessage());
			    System.out.println("SQLState: " + ex.getSQLState());
			    System.out.println("VendorError: " + ex.getErrorCode());
			} finally {
		    if (stmt != null) {  try {  stmt.close();  } catch (SQLException sqlEx) { }  stmt = null;   }
		    if (conn != null) {  try {  conn.close();  } catch (SQLException sqlEx) { }  conn = null;   }
			}
		}
		if( inicio ) {
      request.getRequestDispatcher("/cuenta.jsp").forward(request, response);
		} else {
			response.getWriter().append( respuesta );
		}
		if( Utilidades.DEB )  System.out.println("Mensaje Inicio: " + respuesta);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private StringBuilder inicioSesion(String matricula, boolean esAlumno) {
		StringBuilder consulta;
		
		
		if( esAlumno )
			consulta = new StringBuilder("SELECT id_al_matricula, CONCAT(al_nombre, ' ', al_paterno) " + 
				"FROM alumno WHERE id_al_matricula = '").append( matricula ).append( "';" );
		else
			consulta = new StringBuilder("SELECT id_ms_matricula, CONCAT(ms_nombre, ' ', ms_paterno) " + 
					"FROM maestro WHERE id_ms_matricula = '").append( matricula ).append( "';" );
		return consulta;
	}
}
