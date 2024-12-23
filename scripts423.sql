SELECT student.id, student.name, age, faculty.name as faculty_name,
color as faculty_color, file_path as avatar_path
	FROM public.student
	LEFT JOIN public.faculty ON faculty = faculty.id
	INNER JOIN public.avatar ON student.id = avatar.student_id
	ORDER BY student.name;