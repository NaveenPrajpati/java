package encapsulation;
//stage 1.1

class Book {
    String title;
    String author;
    int price;

    Book(String title, String author, int price) {
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public void displayInfo() {
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Price: " + String.valueOf(price));
    }
}

// stage 1.2 - Encapsulation
class Student {
    private String name;
    private int age;
    private String grade;

    Student(String name, String grade, int age) {
        this.age = age;
        this.name = name;
        this.grade = grade;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGrage(String grade) {
        this.grade = grade;
    }

    public String getName() {
        return this.name;
    }

    public String getGrade() {
        return this.grade;
    }

    public int getAge() {
        return this.age;
    }
}

public class Stage1 {

    public static void main(String[] args) {
        // Book book1 = new Book("Jungle book", "naveen", 100);
        // Book book2 = new Book("Horro book", "kumar", 150);
        // book1.displayInfo();
        // book2.displayInfo();

        Student student = new Student("naveen", "A", 20);

        student.setName("jack");
        student.setGrage("B");
        student.setAge(21);

        System.out.println(
                "Age-" + student.getAge() + ", Name-" +
                        student.getName() + ", Grade-" +
                        student.getGrade());

    }

}
