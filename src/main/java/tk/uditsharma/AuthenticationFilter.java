package tk.uditsharma;

import java.io.IOException;
import java.security.Principal;
import java.util.Base64;

import javax.annotation.Priority;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

    private static final String REALM = "example";
    private static final String AUTHENTICATION_SCHEME = "Bearer";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        // Get the Authorization header from the request
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        // Validate the Authorization header
        if (!isTokenBasedAuthentication(authorizationHeader)) {
            abortWithUnauthorized(requestContext);
            return;
        }

        // Extract the token from the Authorization header
        String token = authorizationHeader
                            .substring(AUTHENTICATION_SCHEME.length()).trim();

        try {

            // Validate the token
            validateToken(token, requestContext);
            //System.out.println("after validate");

        } catch (Exception e) {
            abortWithUnauthorized(requestContext);
        }
    }

    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // The authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                    .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private void abortWithUnauthorized(ContainerRequestContext requestContext) {

        // Abort the filter chain with a 401 status code response
        // The WWW-Authenticate header is sent along with the response
        requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                        .header(HttpHeaders.WWW_AUTHENTICATE, 
                                AUTHENTICATION_SCHEME + " realm=\"" + REALM + "\"")
                        .build());
    }

    private void validateToken(String encodedToken, ContainerRequestContext requestContext) throws Exception {
        // Check if the token was issued by the server and if it's not expired
        // Throw an Exception if the token is invalid
    	String token = null;
    	try {
    		//System.out.println(encodedToken);
			byte[] decodedBytes = Base64.getDecoder().decode(
					encodedToken);
			token = new String(decodedBytes, "UTF-8");
		} catch (IOException e) {
			throw e;
		}
    	
    	try {
    		//System.out.println("inside validate token");
    		//System.out.println(token);
    		DBConnection.checkToken(token);
    		String uname = token.substring(26);
    		final SecurityContext currentSecurityContext = requestContext.getSecurityContext();
    		requestContext.setSecurityContext(new SecurityContext() {

		        @Override
		        public Principal getUserPrincipal() {
		            return () -> uname;
		        }

    		    @Override
    		    public boolean isUserInRole(String role) {
    		        return true;
    		    }

    		    @Override
    		    public boolean isSecure() {
    		        return currentSecurityContext.isSecure();
    		    }

    		    @Override
    		    public String getAuthenticationScheme() {
    		        return AUTHENTICATION_SCHEME;
    		    }
    		});

    	} catch (Exception e) {
    		System.out.println("inside validatToken catch");
    		throw e;
		}
    	
    	
    }
}