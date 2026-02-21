package ru.cs.vsu.yachnyy_m_a;

import sun.awt.Mutex;

import java.util.*;

public class Task10 {
    public static TreeMap<Integer, StudentPair> process(List<Student> students) {
        Random RND = new Random();

        HashMap<Integer, ArrayList<Student>> bestMales = new HashMap<>();
        HashMap<Integer, ArrayList<Student>> bestFemales = new HashMap<>();

        for (Student student : students) {
            if (student.sex == Sex.MALE) {

                int course = student.course;
                ArrayList<Student> thisCourseBest = bestMales.get(course);

                if (thisCourseBest != null) {
                    if (student.compareTo(thisCourseBest.get(0)) > 0) {
                        thisCourseBest.clear();
                        thisCourseBest.add(student);
                    } else if (student.compareTo(thisCourseBest.get(0)) == 0) {
                        thisCourseBest.add(student);
                    }
                } else {
                    bestMales.put(course, new ArrayList<>());
                    bestMales.get(course).add(student);
                }
            } else {

                int course = student.course;
                ArrayList<Student> thisCourseBest = bestFemales.get(course);

                if (thisCourseBest != null) {
                    if (student.compareTo(thisCourseBest.get(0)) > 0) {
                        thisCourseBest.clear();
                        thisCourseBest.add(student);
                    } else if (student.compareTo(thisCourseBest.get(0)) == 0) {
                        thisCourseBest.add(student);
                    }
                } else {
                    bestFemales.put(course, new ArrayList<>());
                    bestFemales.get(course).add(student);
                }
            }
        }

        HashSet<Integer> coursesAvailable = new HashSet<>(bestMales.keySet());
        coursesAvailable.retainAll(bestFemales.keySet());
        TreeMap<Integer, StudentPair> result = new TreeMap<>();

        for (Integer course : coursesAvailable) {
            ArrayList<Student> males = bestMales.get(course);
            ArrayList<Student> females = bestFemales.get(course);
            Student male = males.get(RND.nextInt(males.size()));
            Student female = females.get(RND.nextInt(females.size()));
            result.put(course, new StudentPair(male, female));
        }

        return result;
    }

    public static String processToString(List<Student> list) {
        StringBuilder result = new StringBuilder();
        TreeMap<Integer, StudentPair> treeMap = process(list);
        for (Map.Entry<Integer, StudentPair> entry : treeMap.entrySet()) {
            StudentPair pair = entry.getValue();
            result.append(String.format("С %s курса на бал будут отправлены:\n%s\n\n%s\n------------------------------------\n", entry.getKey(), pair.male.toString(), pair.female.toString()));
        }
        if(!result.toString().equals("")){
            return result.toString();
        }
        return "На бал никто не будет отправлен";
    }
}
