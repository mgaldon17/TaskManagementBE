-- Inserta datos en la tabla roles
INSERT INTO roles (name)
SELECT 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'ADMIN');

INSERT INTO roles (name)
SELECT 'USER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE name = 'USER');

