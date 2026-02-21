package ru.cs.vsu.yachnyy_m_a;

import com.google.gson.Gson;
import ru.cs.vsu.yachnyy_m_a.util.JTableUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

public class Student {
    public String name;
    public Sex sex;
    public int course;
    public double mark;

    public Student(String name, Sex sex, int course, double mark) {
        this.name = name;
        this.sex = sex;
        this.course = course;
        this.mark = mark;
    }

    public Student(String name, String sex, int course, double mark) {
        this.name = name;
        this.sex = Sex.valueOf(sex);
        this.course = course;
        this.mark = mark;
    }

    public static Student[] readStudentArrayFromFile(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner scanner = new Scanner(file);
        StringBuilder json = new StringBuilder();
        while (scanner.hasNext()) {
            json.append(scanner.nextLine());
        }
        return new Gson().fromJson(json.toString(), Student[].class);
    }

    public static void writeStudentArrayToJTable(JTable jTable, Student[] array) {
        String[][] matrix = new String[array.length][4];
        for (int i = 0; i < matrix.length; i++) {
            Student student = array[i];
            matrix[i][0] = student.name;
            matrix[i][1] = student.sex.toString();
            matrix[i][2] = Integer.toString(student.course);
            matrix[i][3] = Double.toString(student.mark);
        }
        JTableUtils.writeArrayToJTable(jTable, matrix);
    }

    public int compareTo(Student student) {
        return Double.compare(this.mark, student.mark);
    }

    public static Student[] randomStudentArray(int length) {
        Random RND = new Random();
        Student[] result = new Student[length];
        for (int i = 0; i < length; i++) {
            StringBuilder name = new StringBuilder();
            name.append((char) (65 + RND.nextInt(26)));

            for (int j = 0; j < 5 + RND.nextInt(6); j++) {
                char c = (char) (97 + RND.nextInt(26));
                name.append(c);
            }
            name.append(" ");
            name.append((char)(65 + RND.nextInt(26)));
            name.append(".");
            name.append((char)(65 + RND.nextInt(26)));
            name.append(".");
            Sex sex = (RND.nextInt(2) == 0) ? Sex.MALE : Sex.FEMALE;
            int course = 1 + RND.nextInt(6);
            double mark = RND.nextInt(5000) / 100.0;
            result[i] = new Student(name.toString(), sex, course, mark);
        }
        return result;
    }

    public static Student[] readStudentArrayFromJTable(JTable table) {
        String[][] matrix = JTableUtils.readStringMatrixFromJTable(table);
        Student[] result = new Student[matrix.length];
        for (int i = 0; i < result.length; i++) {
            String[] strings = matrix[i];
            String name = strings[0];
            Sex sex = Sex.valueOf(strings[1]);
            int course = Integer.parseInt(strings[2]);
            double mark = Double.parseDouble(strings[3]);
            result[i] = new Student(name, sex, course, mark);
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("Имя: %s\nПол: %s\nКурс: %s\nСредний балл: %.2f", name, sex, course, mark);
    }
}
