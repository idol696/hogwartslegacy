DROP SCHEMA IF EXISTS test2 CASCADE;
CREATE SCHEMA IF NOT EXISTS test2;

-- Таблица машин
CREATE TABLE test2.cars
(
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    brand VARCHAR(100) NOT NULL,
    model VARCHAR(100) NOT NULL,
    cost NUMERIC(10, 2) NOT NULL,
    CONSTRAINT cars_brand_model_unique UNIQUE (brand, model)
);

-- Таблица водителей
CREATE TABLE test2.drivers
(
    id BIGINT NOT NULL GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    age INTEGER DEFAULT 20 CHECK (age >= 16),
    has_license BOOLEAN NOT NULL,
    car_id BIGINT,
    CONSTRAINT fk_car FOREIGN KEY (car_id) REFERENCES test2.cars (id) ON DELETE SET NULL
);

-- Мудрёное тестовое заполнение данных, ибо не нашел как делать инсерты на Postgree если
-- данных нет, кроме как этого примера из интернета

INSERT INTO test2.cars (brand, model, cost)
SELECT * FROM (
    SELECT 'Toyota' AS brand, 'Corolla' AS model, 20000.00 AS cost
    UNION ALL
    SELECT 'Ford', 'Focus', 18000.50
) AS new_data
WHERE NOT EXISTS (SELECT 1 FROM test2.cars);

INSERT INTO test2.drivers (name, age, has_license, car_id)
SELECT * FROM (
    SELECT 'Alice' AS name, 25 AS age, TRUE AS has_license, 1 AS car_id
    UNION ALL
    SELECT 'Bob', 17, TRUE, 2
    UNION ALL
    SELECT 'Charlie', 30, FALSE, NULL
) AS new_data
WHERE NOT EXISTS (SELECT 1 FROM test2.drivers);