package ee.jakarta.tck.authentication.test.registersession.servlet;

import java.io.IOException;
import java.security.Principal;

import ee.jakarta.tck.authentication.test.registersession.sam.MyPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


/**
 * 
 * @author Arjan Tijms
 * 
 */
@WebServlet(urlPatterns = "/public/servlet")
public class PublicServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.getWriter().write("This is a public servlet \n");

        String webName = null;
        boolean isCustomPrincipal = false;
        if (request.getUserPrincipal() != null) {
            Principal principal = request.getUserPrincipal();
            isCustomPrincipal = principal instanceof MyPrincipal; 
            webName = principal.getName();
        }
        
        boolean webHasRole = request.isUserInRole("architect");

        response.getWriter().write("isCustomPrincipal: " + isCustomPrincipal + "\n");
        response.getWriter().write("web username: " + webName + "\n");
        response.getWriter().write("web user has role \"architect\": " + webHasRole + "\n");
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            response.getWriter().write("Session ID: " + session.getId());
        } else {
            response.getWriter().write("No session");
        }

    }

}
