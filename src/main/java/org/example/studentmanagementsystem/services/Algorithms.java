package org.example.studentmanagementsystem.services;

import org.example.studentmanagementsystem.models.Student;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Algorithms {


    public int[] getPerformance(List<Student> students, boolean isSorted){
        int[] results = new int[5];

        if (!isSorted) students = sortByGrade(students, 0, students.size()-1);

        for (Student std: students){
            if (std.getPerformance().equals("Below Average")) results[0]++;
            else if (std.getPerformance().equals("Average")) results[1]++;
            else if (std.getPerformance().equals("Good")) results[2]++;
            else if (std.getPerformance().equals("Very Good")) results[3]++;
            else results[4]++;
        }
        return results;
    }
    public double[] getStatistics(List<Student> students, boolean isSorted){
        // Median - Mean - Mode - Smallest - Largest
        double[] stats = new double[5];

        if (students == null || students.isEmpty()) return stats;
        Map<Double, Integer> freq = new HashMap<>();

        if (!isSorted) students = sortByGrade(students, 0, students.size()-1);

        stats[3] = students.get(0).getGrade();
        stats[4] = students.get(students.size()-1).getGrade();

        if (students.size() % 2 != 0) // ODD
            stats[0] = students.get((students.size()-1)/2).getGrade();
        else{ // Even
            double med1 = students.get((students.size()-1)/2).getGrade();
            double med2 = students.get((students.size()-1)/2 +1).getGrade();
            stats[0] = (med1+med2)/2;
        }
        double sum = 0;
        for (Student std: students){
            sum += std.getGrade();
            freq.put(std.getGrade(), freq.getOrDefault(std.getGrade(),0)+1);
        }
        stats[1] = sum/students.size();

        int max = 0;
        for(var entry: freq.entrySet()){
            if(entry.getValue() > max){
                max = entry.getValue();
                stats[2] = entry.getKey();
            }
        }

        return stats;
    }

    // Quick Sort Algorithm
    public List<Student> sortByGrade(List<Student> students, int start, int end){
        if (start < end){
            int partitionInd = partition(students, start, end);
            sortByGrade(students, start, partitionInd-1);
            sortByGrade(students, partitionInd+1, end);
        }
        return students;
    }
    public int partition (List<Student> students, int i, int j){
        int pivot = i;
        while (i < j){
            while (i <= j && students.get(pivot).getGrade() >= students.get(i).getGrade()) i++;
            while (i <= j && students.get(pivot).getGrade() < students.get(j).getGrade()) j--;
            if (i < j) swap(students, i, j);
        }
        swap(students, pivot, j);
        return j;
    }
    public static void swap (List<Student> students, int i, int j){
        Student temp = students.get(i);
        students.set(i, students.get(j));
        students.set(j, temp);
    }

    // Merge Sort
    public List<Student> sortByName(List<Student> students, int start, int end){
        if (start < end){
            int mid = (start+end)/2;
            sortByName(students, start, mid);
            sortByName(students, mid+1, end);
            merge(students, start, mid, mid+1, end);
        }
        return students;
    }
    public void merge(List<Student> students,int aStart,int aEnd, int bStart, int bEnd){
        Student[] holder = new Student[aEnd-aStart+bEnd-bStart+2];
        int i = 0, a = aStart, b = bStart;

        while (a <= aEnd && b <= bEnd){
            if (students.get(a).getName().compareToIgnoreCase(students.get(b).getName()) < 0 ) holder[i++] = students.get(a++);
            else holder[i++] = students.get(b++);
        }
        while (a <= aEnd ) holder[i++] = students.get(a++);
        while (b <= bEnd ) holder[i++] = students.get(b++);

        i = 0;
        while (i < holder.length)
            students.set(aStart++, holder[i++]);
    }
}
