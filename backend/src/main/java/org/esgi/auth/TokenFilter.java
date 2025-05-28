package org.esgi.auth;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.esgi.users.UserEntity;
import org.esgi.users.UserRepository;

import java.io.IOException;
import java.util.Optional;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class TokenFilter implements ContainerRequestFilter {

    @Inject
    UserRepository userRepository;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        if (path.startsWith("/auth/login") || path.startsWith("/auth/logout") || path.startsWith("/users")){
            return;
        }


        String authHeader = requestContext.getHeaderString("Authorization");
        System.out.println("hello world 1");
        if (authHeader == null || !authHeader.startsWith("Token ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }

        String token = authHeader.substring("Token ".length());
        Optional<UserEntity> userOpt = userRepository.find("sessionToken", token).firstResultOptional();
        if (userOpt.isEmpty()) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }
}
