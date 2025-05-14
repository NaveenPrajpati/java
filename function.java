import java.util.Scanner;

public class function {


    public static void printName(){
System.out.println("this is my name");
    }
    public static void printName(String name){
System.out.println("this is my name" + name);
    }

    public static void main(String[] args) {
        System.out.println("Enter name to print");
        Scanner sc=new Scanner(System.in);
        
        String nam=sc.nextLine();

        printName(nam);
    }
}