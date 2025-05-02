package com.pluralsight;

import java.util.Scanner;

public class MyUtils {
    static Scanner input=new Scanner(System.in);
    //prints n character long "-" text divider
    public static void printDivider(int n){
        System.out.println("-".repeat(Math.max(0, n)));
    }
    public static void printCustomDivider(String divider,int n){
        System.out.println(divider.repeat(Math.max(0, n)));
    }
    //input the question you want answered, returns answer, expects string
    public static String askQuestionGetString(String q){
        System.out.print(q);
        return input.nextLine();
    }
    //input the question you want answered, returns answer, expects integer
    public static int askQuestionGetInt(String q){
        boolean asking=true;
        int answer=-1;
        while(asking){
            try{
                System.out.print(q);
                answer = input.nextInt();
                input.nextLine();
            } catch (Exception e) {
                input.nextLine();
                printDivider(30);
                System.out.println("Error: must enter a number.");
                printDivider(30);
                continue;
            }
            asking=false;
        }
        return answer;
    }
    public static float askQuestionGetFloat(String q){
        boolean asking=true;
        float answer=-1;
        while(asking){
            try{
                System.out.print(q);
                answer = input.nextFloat();
                input.nextLine();
            } catch (Exception e) {
                input.nextLine();
                printDivider(30);
                System.out.println("Error: must enter a number.");
                printDivider(30);
                continue;
            }
            asking=false;
        }
        return answer;
    }
    public static double askQuestionGetDouble(String q){
        boolean asking=true;
        double answer=-1;
        while(asking){
            try{
                System.out.print(q);
                answer = input.nextDouble();
                input.nextLine();
            } catch (Exception e) {
                input.nextLine();
                printDivider(30);
                System.out.println("Error: must enter a number.");
                printDivider(30);
                continue;
            }
            asking=false;
        }
        return answer;
    }

    public static void clearTerminal(){
        for (int i = 0; i < 50; i++) {
            System.out.println();
        }
    }
}
