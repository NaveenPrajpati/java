package inheritance;

// stage 2 Inheritance
class Person {
    protected String name;
    protected int age;

    public Person(String name, int age) {
        this.age = age;
        this.name = name;
    }

    public void displayInfo() {
        System.out.println("Name - " + this.name);
        System.out.println("Age - " + this.age);
    }

}

class Student extends Person {
    private String grade;

    public Student(String name, int age, String grade) {
        super(name, age);
        this.grade = grade;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Grade - " + this.grade);
    }

}

class Teacher extends Person {
    private String subject;

    public Teacher(String name, int age, String subject) {
        super(name, age);
        this.subject = subject;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Subject - " + this.subject);
    }

}

public class Stage2 {

    public static void main(String[] args) {

        Student student = new Student("naveen", 20, "A");
        student.displayInfo();
        Teacher teacher = new Teacher("jace", 40, "Maths");
        teacher.displayInfo();

    }

}
