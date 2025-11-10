package design;

// Abstract Factory: Declares interfaces for creating abstract products.
// Concrete Factories: Implement creation methods for specific product variants.
// Abstract Products: Define interfaces for product types.
// Concrete Products: Implement the abstract product interfaces.
// Client: Uses only abstract interfaces, not concrete classes.

//1. Abstract Products
interface Button {
    void paint();
}

interface Checkbox {
    void paint();
}

// 2. Concrete Products
class WindowsButton implements Button {
    public void paint() {
        System.out.println("Rendering a Windows-style button");
    }
}

class MacButton implements Button {
    public void paint() {
        System.out.println("Rendering a Mac-style button");
    }
}

class WindowsCheckbox implements Checkbox {
    public void paint() {
        System.out.println("Rendering a Windows-style checkbox");
    }
}

class MacCheckbox implements Checkbox {
    public void paint() {
        System.out.println("Rendering a Mac-style checkbox");
    }
}

// 3. Abstract Factory
interface GUIFactory {
    Button createButton();

    Checkbox createCheckbox();
}

// 4. Concrete Factories
class WindowsFactory implements GUIFactory {
    public Button createButton() {
        return new WindowsButton();
    }

    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

class MacFactory implements GUIFactory {
    public Button createButton() {
        return new MacButton();
    }

    public Checkbox createCheckbox() {
        return new MacCheckbox();
    }
}

// 5. Client Code
class Application {
    private Button button;
    private Checkbox checkbox;

    public Application(GUIFactory factory) {
        button = factory.createButton();
        checkbox = factory.createCheckbox();
    }

    public void renderUI() {
        button.paint();
        checkbox.paint();
    }
}

public class abstractFactoryPattern {
    public static void main(String[] args) {
        GUIFactory factory;

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            factory = new MacFactory();
        } else {
            factory = new WindowsFactory();
        }

        Application app = new Application(factory);
        app.renderUI();
    }
}
