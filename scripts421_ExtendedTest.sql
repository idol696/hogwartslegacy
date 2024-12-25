-------- СКРИПТ УПРАВЛЕНИЯ ОЛЕГАМИ -------------
-- Комментируем скрипт для большего понимания ;)
------------------------------------------------
-- Основная схема внизу - это подготовка к
-- реструктуризации и формирование БД для теста
-- Так как основную БД жалко... :(
------------------------------------------------

-- Удаляем тестовую схему, если она существует
DROP SCHEMA IF EXISTS test1 CASCADE;

-- Создаем тестовую схему
CREATE SCHEMA test1;

-- Копируем структуру таблиц из public в test1
DO $$
DECLARE
    obj RECORD;
BEGIN
    FOR obj IN
        SELECT tablename FROM pg_tables WHERE schemaname = 'public'
    LOOP
        EXECUTE format('CREATE TABLE test1.%I (LIKE public.%I INCLUDING ALL)', obj.tablename, obj.tablename);
    END LOOP;
END $$;

-- Копируем данные из public в test1
DO $$
DECLARE
    obj RECORD;
BEGIN
    FOR obj IN
        SELECT tablename FROM pg_tables WHERE schemaname = 'public'
    LOOP
        EXECUTE format('INSERT INTO test1.%I SELECT * FROM public.%I', obj.tablename, obj.tablename);
    END LOOP;
END $$;

-- Удаляем записи из test1.student с возрастом < 16 или NULL
DELETE FROM test1.student WHERE age < 16 OR age IS NULL;

-- Удаляем записи с дублирующимися именами (student)
DELETE FROM test1.student
WHERE id NOT IN (
    SELECT MIN(id) FROM test1.student WHERE name IS NOT NULL GROUP BY name
);

-- Удаляем записи с дублирующимися комбинациями name и color (faculty)
DELETE FROM test1.faculty
WHERE id NOT IN (
    SELECT MIN(id) FROM test1.faculty GROUP BY name, color
);

-- Добавляем ограничения (основное задание 4.2.1)

-- 1. Добавляем ограничение NOT NULL и уникальность на name в student
ALTER TABLE test1.student
ALTER COLUMN name SET NOT NULL;

ALTER TABLE test1.student
ADD CONSTRAINT student_name_unique UNIQUE (name);

-- 2. Добавляем ограничение на возраст (age >= 16) с CHECK
ALTER TABLE test1.student
ADD CONSTRAINT student_age_check CHECK (age >= 16);

-- 3. Устанавливаем значение по умолчанию для возраста (20)
ALTER TABLE test1.student
ALTER COLUMN age SET DEFAULT 20;

-- 4. Добавляем уникальность для комбинации name и color в faculty
ALTER TABLE test1.faculty
ADD CONSTRAINT faculty_name_color_unique UNIQUE (name, color);
