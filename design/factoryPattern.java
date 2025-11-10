package design;

interface Shape {
    void draw();
}

class Circle implements Shape {
    public void draw() {
        System.out.println("Drawing a Circle");
    }
}

class Square implements Shape {
    public void draw() {
        System.out.println("Drawing a Square");
    }
}

class ShapeFactory {
    Shape getShape(String shapeType) {
        if (shapeType == null)
            return null;
        switch (shapeType.toLowerCase()) {
            case "circle":
                return new Circle();
            case "square":
                return new Square();
            default:
                return null;
        }
    }
}

class factoryPattern {
    static void main(String[] args) {

        ShapeFactory factory = new ShapeFactory();

        Shape shape1 = factory.getShape("circle");
        shape1.draw(); // Output: Drawing a Circle

        Shape shape2 = factory.getShape("square");
        shape2.draw(); // Output: Drawing a Square
    }
}
