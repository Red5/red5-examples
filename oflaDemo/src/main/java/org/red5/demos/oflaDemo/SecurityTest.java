package org.red5.demos.oflaDemo;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SecurityTest
 */
public class SecurityTest extends HttpServlet {

	private static final long serialVersionUID = 17227839L;

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		
		try {
			File rootDir = new File("/");
			out.print("\nPath: " + rootDir.getAbsolutePath());
			if (rootDir.exists()) {
				out.print("\nRoot '/' exists");
				if (rootDir.canRead()) {
					out.print("\nCan read from Root");
					if (rootDir.canWrite()) {
						out.print("\nCan write to Root");
					}
				}
			}
		} catch (Exception e) {
			out.print("\nException with Root: " + e.getMessage());
		}

		try {
			File cDir = new File("file://C:/");
			out.print("\nPath: " + cDir.getAbsolutePath());			
			if (cDir.exists()) {
				out.print("\nC drive exists");
				if (cDir.canRead()) {
					out.print("\nCan read from C drive");
					if (cDir.canWrite()) {
						out.print("\nCan write to C drive");
					}
				}
			}
		} catch (Exception e) {
			out.print("\nException with C drive: " + e.getMessage());
		}

		try {
			File servDir = new File(getServletContext().getRealPath("/"));
			out.print("\nPath: " + servDir.getAbsolutePath());
			if (servDir.exists()) {
				out.print("\nServlet directory exists");
				if (servDir.canRead()) {
					out.print("\nCan read from Servlet directory");
					if (servDir.canWrite()) {
						out.print("\nCan write to Servlet directory");
					}
				}
			}
		} catch (Exception e) {
			out.print("\nException with servlet directory: " + e.getMessage());
		}
		
	}

}
