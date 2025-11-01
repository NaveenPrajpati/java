package polymorphism;

// Stage 3 Runtime polymorphism at work

abstract class Shape {
    // Abstract class: is a restricted class that cannot be used to create objects
    abstract void area();
}

class Circle extends Shape {
    private double radius;

    public Circle(double radius) {
        this.radius = radius;
    }

    @Override
    void area() {
        System.out.println("Circle area: " + (Math.PI * radius * radius));
    }
}

class Rectangle extends Shape {
    private double length;
    private double breadth;

    public Rectangle(double length, double breadth) {
        this.length = length;
        this.breadth = breadth;
    }

    @Override
    void area() {
        System.out.println("Rectangle area: " + (length * breadth));
    }
}

public class Stage3 {
    public static void main(String[] args) {
        Shape[] shapes = {
                new Circle(3),
                new Rectangle(4, 5),
                new Circle(2.5)
        };

        for (Shape s : shapes) {
            s.area();
        }
    }
}