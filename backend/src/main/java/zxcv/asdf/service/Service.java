package zxcv.asdf.service;


import lombok.RequiredArgsConstructor;
import zxcv.asdf.domain.Assignment;
import zxcv.asdf.domain.Lecture;
import zxcv.asdf.repository.UserRepository;
import zxcv.asdf.repository.LectureRepository;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@org.springframework.stereotype.Service
public class Service {

    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;

    /*public User login(User user){
        if (userRepository.getById(user.getId()) != null) {
            return userRepository.getById(user.getId());
        }
        userRepository.save(user);
        return user;
    }*/
    public Lecture create_lecture(Lecture lecture){
        lectureRepository.save(lecture);
        return lecture;
    }
    public Assignment addAssignments(Long id, Assignment assignment){
        Lecture lecture = lectureRepository.getById(id);
        lecture.getAssignments().add(assignment);
        return assignment;
    }
    public List<Assignment> getAllAssignments(Long id){
       return lectureRepository.getById(id).getAssignments();
    }

    public boolean compile(String sourceCode,Path outputpath){
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<String> options = Arrays.asList("-d", outputpath.toString());
        JavaFileObject file = new InMemoryJavaFileObject("Hello", sourceCode);   //public class 이름
        Iterable<? extends JavaFileObject> compilationUnits = Arrays.asList(file);

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, options, null, compilationUnits);
        return task.call();
    }
    static class InMemoryJavaFileObject extends SimpleJavaFileObject {
        final String code;

        InMemoryJavaFileObject(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
    public String test(Path tempDir,String[] args) throws Exception {
        URLClassLoader classLoader = URLClassLoader.newInstance(
                new URL[] {tempDir.toUri().toURL()},
                this.getClass().getClassLoader()
        );

        Class<?> clazz = Class.forName("Hello", true, classLoader);
        Method method = clazz.getMethod("main", String[].class);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(bos));

        try {
            method.invoke(null, (Object) args);
            System.setOut(originalOut);
            return bos.toString(StandardCharsets.UTF_8);
        } finally {
            System.setOut(originalOut);
        }
    }
}



