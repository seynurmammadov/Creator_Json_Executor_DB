import Models.Grade;
import Models.Student;
import Models.Subject;
import Models.Teacher;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Main {
    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        properties.load(Files.newBufferedReader(Path.of("src/main/resources/creator.config")));
        List<Teacher> teachers = generateDataTeachers(20);
        List<Grade> grades = generateDataGrades();
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(Integer.parseInt(properties.getProperty("creator.thread_count")));
               executorService.scheduleAtFixedRate(()->{
                   List<Student> st =generateDataStudent(properties,teachers,grades);
                   try {
                       objectTojson(st,properties);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               },0,200, TimeUnit.MILLISECONDS);

    }
    public static List<Student> generateDataStudent(Properties properties,List<Teacher> teachers,List<Grade> grades){
        int count = Integer.parseInt(properties.getProperty("creator.rows_in_each_file"));

        return LongStream.range(1, count).mapToObj(z -> {
            Random rand = new Random();
            List<Teacher> studentTeacher = new ArrayList<>();
            Grade randomGrade = grades.get(rand.nextInt(grades.size()));
            int numb =(int)(3+Math.random()*3);
            for (int i = 0; i < numb; i++) {
                Random randT = new Random();
                Teacher randomTeacher = teachers.get(randT.nextInt(teachers.size()));
                if(studentTeacher.indexOf(randomTeacher)==-1){
                    studentTeacher.add(randomTeacher);
                }else {
                    numb++;
                }
            }
            return Student.builder().name("Student"+z).surname("Surname"+1).grade(randomGrade).teachers(studentTeacher).build();
        }).collect(Collectors.toList());


    }
    public static List<Teacher> generateDataTeachers(int count) {
        return LongStream.range(1, count+1).mapToObj(i -> {
            Subject sb = Subject.builder().name("Subject" + i).id(i).build();
            return Teacher.builder().name("Teacher" + i).surname("Surname" + i).id(i).subject(sb).build();
        }).collect(Collectors.toList());
    }

    public static List<Grade> generateDataGrades() {
        String[] grades = {"A","B","C","D","E"};
        return LongStream.range(1, 6).mapToObj(i ->Grade.builder().name(grades[Integer.parseInt((i-1)+"")]).id(i).build()).collect(Collectors.toList());
    }
    public static void objectTojson(List<Student> students,Properties properties) throws IOException {
        Gson gson = new GsonBuilder().create();
        String path =properties.getProperty("creator.folder_path")
                + File.separator
                +"temp_"
                +properties.getProperty("creator.file_name_regex")
                +LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy-HH-mm-ss-SSS"))+"_"
                +Thread.currentThread().getName()+".txt";

        BufferedWriter bwjson= Files.newBufferedWriter(Path.of(path));
        for (Student student: students) {
            String json = gson.toJson(student,Student.class);
            bwjson.write(json);
            bwjson.newLine();
        }
        File f1 = new File(path);
        path =properties.getProperty("creator.folder_path")
                + File.separator
                +properties.getProperty("creator.file_name_regex")
                +LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy-HH-mm-ss-SSS"))+"_"
                +Thread.currentThread().getName()+".txt";
        File f2 = new File(path);
        f1.renameTo(f2);
        bwjson.close();
    }
}
