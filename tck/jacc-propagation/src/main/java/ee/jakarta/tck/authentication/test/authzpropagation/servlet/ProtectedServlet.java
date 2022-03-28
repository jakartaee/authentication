package ee.jakarta.tck.authentication.test.authzpropagation.servlet;

import static ee.jakarta.tck.authentication.test.authzpropagation.jacc.JakartaAuthorization.getSubject;
import static ee.jakarta.tck.authentication.test.authzpropagation.jacc.JakartaAuthorization.hasAccess;

import java.io.IOException;

import javax.security.auth.Subject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 
 * @author Arjan Tijms
 * 
 */
@WebServlet(urlPatterns = "/protected/servlet")
public class ProtectedServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // Obtain the active subject via a JACC policy handler
        Subject subject = getSubject();
       
        if (subject == null) {
            response.getWriter().write("Can't get Subject. JACC doesn't seem to be available.");
            return;
        }

        // Check with JACC if the caller has access to this Servlet. As we're
        // currently in this very Servlet the answer can't be anything than "true" if
        // JASPIC, JACC and role propagation all work correctly.
        response.getWriter().write("Has access to /protected/servlet: " + hasAccess("/protected/servlet", subject));
    }

}
