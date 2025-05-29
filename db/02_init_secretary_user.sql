-- Crée un compte "secrétaire"
-- > Mot de passe en clair : secretary123
--   (haché avec BCrypt 12 rounds, même algo que dans PasswordHasher)

BEGIN;

INSERT INTO users (
    id,
    firstname,
    lastname,
    email,
    password_hash,
    role,
    session_token,
    is_hybrid_or_electric,
    created_at
) VALUES (
             '0327bd46-de09-47eb-ad01-9e39e7d15f0d',
             'Jane',
             'Doe',
             'secretary@company.com',
             '$2a$12$Wt0SA8UbC8CJMMhxprnvc.Q9q0ML361QSWMb4XJrseIO6B8rCb2Mq',
             'SECRETARY',
             NULL,
             false,
             NOW()
         );
COMMIT;
