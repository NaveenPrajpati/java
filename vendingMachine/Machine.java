package vendingMachine;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class Product {
    private String code;
    private String name;
    private int price;

    public Product(String code, String name, int price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}

class Inventory {
    private List<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        products.add(product);
    }

    public Product getProduct(String code) {
        for (Product p : products) {
            if (p.getCode().equals(code))
                return p;
        }
        return null;
    }

    public void removeProduct(String code) {
        products.removeIf(it -> it.getCode().equals(code));
    }
}

enum Coin {
    FIVE(5), TEN(10), TWENTY(20);

    int value;

    Coin(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

interface State {
    void insertCoin(Coin coin);

    void selectProduct(String code);

    void dispense();
}

class IdleState implements State {
    private VendingMachine vm;

    IdleState(VendingMachine m) {
        this.vm = m;
    }

    @Override
    public void insertCoin(Coin coin) {
        System.out.println("Money inserted: " + coin.getValue());
        vm.addBalance(coin.getValue());
        vm.setState(vm.getHasMoneyState());
    }

    @Override
    public void selectProduct(String code) {
        System.out.println("Please insert money");
    }

    @Override
    public void dispense() {
        System.out.println("Nothing to dispense");
    }

}

class HasMoneyState implements State {
    VendingMachine vm;

    HasMoneyState(VendingMachine m) {
        this.vm = m;
    }

    @Override
    public void insertCoin(Coin coin) {
        vm.addBalance(coin.getValue());
        System.out.println("Balance: " + vm.getBalance());
    }

    @Override
    public void selectProduct(String code) {
        Inventory in = vm.getInventory();
        Product product = in.getProduct(code);
        if (product == null) {
            System.out.println("Product is out of stock");
            return;
        }
        if (vm.getBalance() < product.getPrice()) {
            System.out.println("Please add more balance");
            return;

        }
        System.out.println("Product selected: " + product.getName());
        vm.setSelectedProduct(product);
        vm.setState(new DispenseState(vm));

    }

    @Override
    public void dispense() {
        System.out.println("Please Select product");
    }

}

class DispenseState implements State {
    VendingMachine vm;

    DispenseState(VendingMachine m) {
        this.vm = m;
    }

    @Override
    public void insertCoin(Coin coin) {
        System.out.println("Wait, dispensing...");
    }

    @Override
    public void selectProduct(String code) {
        System.out.println("Already dispensing");
    }

    @Override
    public void dispense() {
        Product p = vm.getSelectedProduct();
        System.out.println("Dispensing: " + p.getName());

        vm.getInventory().removeProduct(p.getCode()); // ✅ remove from stock
        vm.deductBalance(p.getPrice()); // ✅ deduct money

        System.out.println("Remaining Balance: " + vm.getBalance());

        vm.setSelectedProduct(null); // clear selection
        vm.setState(vm.getIdleState());
    }

}

class VendingMachine {

    private State idleState;
    private State hasMoneyState;
    private State dispenseState;

    private State currentState;
    private Inventory inventory;
    private int currentBalance = 0;
    private Product selectedProduct;

    public VendingMachine() {
        this.inventory = new Inventory();
        idleState = new IdleState(this);
        hasMoneyState = new HasMoneyState(this);
        dispenseState = new DispenseState(this);
        currentState = idleState;
    }

    // Delegate to state
    public void insertCoin(Coin coin) {
        currentState.insertCoin(coin);
    }

    public void selectProduct(String code) {
        currentState.selectProduct(code);
    }

    public void dispense() {
        currentState.dispense();
    }

    // Getters & Setters
    public void setState(State state) {
        this.currentState = state;
    }

    public State getIdleState() {
        return idleState;
    }

    public State getHasMoneyState() {
        return hasMoneyState;
    }

    public State getDispenseState() {
        return dispenseState;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public int getBalance() {
        return currentBalance;
    }

    public void addBalance(int amt) {
        currentBalance += amt;
    }

    public void deductBalance(int amt) {
        currentBalance -= amt;
    }

    public void resetBalance() {
        currentBalance = 0;
    }

    public void setSelectedProduct(Product p) {
        selectedProduct = p;
    }

    public Product getSelectedProduct() {
        return selectedProduct;
    }

}

public class Machine {
    public static void main(String[] args) {

    }
}
