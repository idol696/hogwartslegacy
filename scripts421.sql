-- Добавляем ограничения (основное задание 4.2.1)

-- 1. Добавляем ограничение NOT NULL и уникальность на name в student
ALTER TABLE public.student
ALTER COLUMN name SET NOT NULL;

ALTER TABLE public.student
ADD CONSTRAINT student_name_unique UNIQUE (name);

-- 2. Устанавливаем значение по умолчанию для возраста (20)
ALTER TABLE public.student
ALTER COLUMN age SET DEFAULT 20;

-- 3. Устанавливаем ограничение по возрасту (>=16)
ALTER TABLE public.student
ADD CONSTRAINT student_age_check CHECK (age >= 16);

-- 4. Добавляем уникальность для комбинации name и color в faculty
ALTER TABLE public.faculty
ADD CONSTRAINT faculty_name_color_unique UNIQUE (name, color);