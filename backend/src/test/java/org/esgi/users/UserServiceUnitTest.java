package org.esgi.users;

import org.esgi.users.resources.UserMapper;
import org.esgi.users.resources.dto.in.CreateUserRequest;
import org.esgi.users.resources.dto.in.UserUpdateRequest;
import org.esgi.users.resources.dto.out.UserResponse;
import org.esgi.utils.PasswordHasher;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.WebApplicationException;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @InjectMocks
    UserService service;

    @Mock UserRepository repo;
    @Mock PasswordHasher hasher;
    @Mock Logger log;
    @Mock UserMapper mapper;
    @Mock UserMapper userMapper;


    @Nested class CreateUser {

        @Test
        void ok_when_email_free_and_password_long_enough() {
            CreateUserRequest req = new CreateUserRequest("John","Doe","john@test.com","password123", "EMPLOYEE", false);
            UserEntity entity = new UserEntity();
            entity.id = UUID.randomUUID();
            entity.email = req.email();
            entity.role= Role.EMPLOYEE;
            entity.isHybridOrElectric = false;
            UserResponse resp = new UserResponse(entity.id.toString(), entity.firstname, entity.lastname, entity.email, entity.role.name(), entity.isHybridOrElectric, null);

            try (MockedStatic<UserEntity> mockStatic = Mockito.mockStatic(UserEntity.class)) {
                mockStatic.when(() -> UserEntity.emailExists(req.email())).thenReturn(false);

                when(hasher.hash(req.password())).thenReturn("hashed");
                when(mapper.fromCreateRequest(eq(req), eq("hashed"))).thenReturn(entity);
                when(mapper.toResponse(entity)).thenReturn(resp);

                UserResponse out = service.create(req);

                assertSame(resp, out);
                verify(repo).persist(entity);
            }
        }

        @Test
        void fail_when_email_already_exists() {
            CreateUserRequest req = new CreateUserRequest("A","B","a@b.com","password123", "EMPLOYEE", false);
            try (MockedStatic<UserEntity> mockStatic = Mockito.mockStatic(UserEntity.class)) {
                mockStatic.when(() -> UserEntity.emailExists(req.email())).thenReturn(true);
                assertThrows(IllegalArgumentException.class, () -> service.create(req));
                verify(repo, never()).persist(any(UserEntity.class));
            }
        }

        @Test
        void fail_when_password_too_short() {
            CreateUserRequest req = new CreateUserRequest("A","B","a@b.com","short", "EMPLOYEE", false);
            try (MockedStatic<UserEntity> mockStatic = Mockito.mockStatic(UserEntity.class)) {
                mockStatic.when(() -> UserEntity.emailExists(any())).thenReturn(false);
                assertThrows(IllegalArgumentException.class, () -> service.create(req));
            }
        }
    }

    @Test
    void find_returns_mapped_user() {
        UUID id = UUID.randomUUID();
        UserEntity entity = new UserEntity(); entity.id = id; entity.email = "x@y.com";
        UserResponse resp = new UserResponse(id.toString(), null, null, entity.email, null, false, null);
        when(repo.findById(id)).thenReturn(entity);
        when(userMapper.toResponse(entity)).thenReturn(resp);

        assertSame(resp, service.find(id));
    }

    @Test
    void getAll_returns_list() {
        when(repo.listAll()).thenReturn(List.of());
        when(userMapper.toResponses(any())).thenReturn(List.of());
        assertTrue(service.getAll().isEmpty());
    }


    @Nested class UpdateUser {
        @Test
        void ok_update_valid_role() {
            UUID id = UUID.randomUUID();
            UserEntity entity = new UserEntity(); entity.id = id; entity.role = Role.EMPLOYEE;
            UserUpdateRequest upd = new UserUpdateRequest(); upd.role = "MANAGER";

            when(repo.findById(id)).thenReturn(entity);

            assertDoesNotThrow(() -> service.update(id, upd));
            verify(mapper).applyUpdate(entity, upd);
            verify(repo).persist(entity);
        }

        @Test
        void fail_when_user_not_found() {
            UUID id = UUID.randomUUID();
            when(repo.findById(id)).thenReturn(null);
            assertThrows(WebApplicationException.class, () -> service.update(id, new UserUpdateRequest()));
        }

        @Test
        void fail_when_invalid_role() {
            UUID id = UUID.randomUUID();
            UserEntity entity = new UserEntity(); entity.id = id;
            UserUpdateRequest upd = new UserUpdateRequest(); upd.role = "INVALID";
            when(repo.findById(id)).thenReturn(entity);
            assertThrows(WebApplicationException.class, () -> service.update(id, upd));
            verify(repo, never()).persist(any(UserEntity.class));
        }
    }


    @Nested class DeleteUser {
        @Test
        void ok_delete_user() {
            UUID id = UUID.randomUUID();
            UserEntity entity = new UserEntity(); entity.id = id;
            when(repo.findById(id)).thenReturn(entity);

            assertDoesNotThrow(() -> service.delete(id));
            verify(repo).delete(entity);
        }

        @Test
        void fail_when_not_found() {
            UUID id = UUID.randomUUID();
            when(repo.findById(id)).thenReturn(null);
            assertThrows(WebApplicationException.class, () -> service.delete(id));
        }
    }
}

