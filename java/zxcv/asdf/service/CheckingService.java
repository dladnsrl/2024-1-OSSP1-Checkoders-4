package zxcv.asdf.service;

import org.springframework.stereotype.Service;

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

@Service
public class CheckingService {
    public boolean compile(String sourceCode, Path outputpath){
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

        Iterable<String> options = Arrays.asList("-d", outputpath.toString());
        JavaFileObject file = new CheckingService.InMemoryJavaFileObject("Hello", sourceCode);   //public class 이름
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
