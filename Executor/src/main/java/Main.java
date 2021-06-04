import Models.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

    static EntityManagerFactory emf= Persistence.createEntityManagerFactory("thread_task");

    public static void main(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(Files.newBufferedReader(Path.of("src/main/resources/executor.config")));

        ScheduledExecutorService scheduledexecutorService = Executors.newScheduledThreadPool(1);
        ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(properties.getProperty("executor.thread_lines_count")));

        scheduledexecutorService.scheduleAtFixedRate(()->{
            File dir = new File(properties.getProperty("executor.files_folder_path"));
            List<File> matchingFiles = Arrays.asList(dir.listFiles(pathname -> pathname.getName().matches(properties.getProperty("executor.file_name_regex") + ".+" + "\\.txt")));
            matchingFiles.stream().forEach(file -> {
                executorService.submit(()-> {
                    try {
                        fileExecute(file,properties);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        },0,1, TimeUnit.SECONDS);

    }
        public static void fileExecute(File movedFile,Properties properties) throws IOException {
            File file = new File(properties.getProperty("executor.processing_folder_path")+File.separator+movedFile.getName());
            movedFile.renameTo(file);

            BufferedReader br = Files.newBufferedReader(file.toPath());
            List<String> studentString = new ArrayList<>();
            int counter = 0;
            int linesInThread = Integer.parseInt(properties.getProperty("executor.lines_count"));
            ExecutorService executorService = Executors.newFixedThreadPool(Integer.parseInt(properties.getProperty("executor.thread_db_count")));

            for (String line : br.lines().collect(Collectors.toList())) {
                studentString.add(line.trim());
                counter++;
                if (counter == linesInThread) {
                    executorService.submit(() -> processLines(studentString));
                    studentString.clear();
                    counter = 0;
                }
            }
            if (counter > 0) {
                executorService.submit(() -> processLines(studentString));
            }
            File processed = new File(properties.getProperty("executor.processed_folder_path")+File.separator+movedFile.getName());
            file.renameTo(processed);
    }
    public static void processLines(List<String> studentString){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        EntityManager entityManager= emf.createEntityManager();
        for ( String st:studentString) {
            Student student =gson.fromJson(st,Student.class);
            entityManager.getTransaction().begin();
            entityManager.merge(student);
            entityManager.getTransaction().commit();

        }
    }
}
  /*
             entityManager.merge(student.getGrade());

            entityManager.getTransaction().begin();
            student.getTeachers().forEach(th->{
                entityManager.merge(th.getSubject());
            });
            entityManager.getTransaction().commit();
            student.getTeachers().forEach(th->{
                entityManager.getTransaction().begin();
                entityManager.merge(th);
                entityManager.getTransaction().commit();
            });
            entityManager.getTransaction().begin();
            entityManager.merge(student);
            entityManager.getTransaction().commit();*/