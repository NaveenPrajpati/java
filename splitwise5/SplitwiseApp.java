package splitwise;

import java.time.LocalDateTime;
import java.util.*;

// ============================================================================
// DOMAIN MODELS
// ============================================================================

class User {
    private String id;
    private String name;
    private String email;
    private String mobile;

    public User(String id, String name, String email, String mobile) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

// ============================================================================
// STRATEGY PATTERN - Split Types
// WHY: Different algorithms for validating and calculating splits
// BENEFIT: Easy to add new split types without modifying existing code (OCP)
// ============================================================================

abstract class Split {
    protected User user;
    protected double amount;

    public Split(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}

class EqualSplit extends Split {
    public EqualSplit(User user) {
        super(user);
    }
}

class ExactSplit extends Split {
    public ExactSplit(User user, double amount) {
        super(user);
        this.amount = amount;
    }
}

class PercentSplit extends Split {
    private double percent;

    public PercentSplit(User user, double percent) {
        super(user);
        this.percent = percent;
    }

    public double getPercent() {
        return percent;
    }
}

enum SplitType {
    EQUAL, EXACT, PERCENT
}

enum ExpenseCategory {
    FOOD, TRAVEL, ENTERTAINMENT, UTILITIES, SHOPPING, OTHER
}

// ============================================================================
// Expense Domain Model
// ============================================================================

class Expense {
    private String id;
    private User paidBy;
    private double amount;
    private List<Split> splits;
    private SplitType splitType;
    private String description;
    private ExpenseCategory category;
    private LocalDateTime createdAt;

    public Expense(String id, User paidBy, double amount, List<Split> splits,
            SplitType splitType, String description, ExpenseCategory category) {
        this.id = id;
        this.paidBy = paidBy;
        this.amount = amount;
        this.splits = splits;
        this.splitType = splitType;
        this.description = description;
        this.category = category;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public List<Split> getSplits() {
        return splits;
    }

    public double getAmount() {
        return amount;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public String getDescription() {
        return description;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

// ============================================================================
// STRATEGY PATTERN - Validation Strategies
// WHY: Each split type has different validation rules
// BENEFIT: Separates validation logic, makes testing easier
// ============================================================================

interface SplitStrategy {
    void validate(double amount, List<Split> splits);
}

class EqualSplitStrategy implements SplitStrategy {
    // STRATEGY PATTERN: Validates equal split - all users get same amount
    @Override
    public void validate(double amount, List<Split> splits) {
        if (splits == null || splits.isEmpty()) {
            throw new IllegalArgumentException("No splits provided");
        }
        double splitAmount = amount / splits.size();
        if (splitAmount <= 0) {
            throw new IllegalArgumentException("Invalid split amount");
        }
    }
}

class ExactSplitStrategy implements SplitStrategy {
    // STRATEGY PATTERN: Validates exact split - sum must equal total
    @Override
    public void validate(double amount, List<Split> splits) {
        double totalAmount = splits.stream()
                .mapToDouble(Split::getAmount)
                .sum();

        // Use tolerance for floating point comparison
        if (Math.abs(totalAmount - amount) > 0.01) {
            throw new IllegalArgumentException(
                    String.format("Amount mismatch: expected %.2f, got %.2f",
                            amount, totalAmount));
        }
    }
}

class PercentSplitStrategy implements SplitStrategy {
    // STRATEGY PATTERN: Validates percent split - must sum to 100%
    @Override
    public void validate(double amount, List<Split> splits) {
        double totalPercent = 0;
        for (Split s : splits) {
            PercentSplit ps = (PercentSplit) s;
            totalPercent += ps.getPercent();
        }

        if (Math.abs(totalPercent - 100.0) > 0.01) {
            throw new IllegalArgumentException(
                    String.format("Total percent must be 100, got %.2f", totalPercent));
        }
    }
}

// ============================================================================
// FACTORY PATTERN - Expense Creation
// WHY: Centralizes complex expense creation logic
// BENEFIT: Hides complexity, ensures validation, calculates amounts
// ============================================================================

class ExpenseFactory {
    private static int expenseCounter = 1;

    // FACTORY PATTERN: Creates expense with proper validation and calculation
    // WHY: Client doesn't need to know about validation and amount calculation
    public static Expense createExpense(
            SplitType type,
            User paidBy,
            double amount,
            List<Split> splits,
            String description,
            ExpenseCategory category) {

        // Step 1: Get appropriate validation strategy based on split type
        SplitStrategy strategy = getStrategy(type);

        // Step 2: Validate splits using selected strategy
        strategy.validate(amount, splits);

        // Step 3: Calculate individual amounts based on split type
        switch (type) {
            case EQUAL:
                // EQUAL: Divide amount equally among all participants
                double equalAmount = amount / splits.size();
                for (Split s : splits) {
                    s.setAmount(equalAmount);
                }
                break;

            case PERCENT:
                // PERCENT: Calculate amount based on percentage
                for (Split s : splits) {
                    PercentSplit ps = (PercentSplit) s;
                    s.setAmount((ps.getPercent() * amount) / 100.0);
                }
                break;

            case EXACT:
                // EXACT: Amounts already provided, just validate
                break;
        }

        // Step 4: Create and return the expense object
        String expenseId = "EXP" + (expenseCounter++);
        return new Expense(expenseId, paidBy, amount, splits, type, description, category);
    }

    // FACTORY PATTERN: Helper method to get strategy
    // WHY: Encapsulates strategy selection logic
    private static SplitStrategy getStrategy(SplitType type) {
        switch (type) {
            case EQUAL:
                return new EqualSplitStrategy();
            case EXACT:
                return new ExactSplitStrategy();
            case PERCENT:
                return new PercentSplitStrategy();
            default:
                throw new IllegalArgumentException("Invalid SplitType");
        }
    }
}

// ============================================================================
// BUILDER PATTERN - Expense Builder
// WHY: Simplifies creation of complex Expense objects with many parameters
// BENEFIT: More readable, optional parameters, immutable object creation
// ============================================================================

class ExpenseBuilder {
    // BUILDER PATTERN: Allows step-by-step construction of Expense
    // WHY: Expense has many parameters, some optional - builder makes it cleaner

    private SplitType type;
    private User paidBy;
    private double amount;
    private List<Split> splits;
    private String description = "No description";
    private ExpenseCategory category = ExpenseCategory.OTHER;

    public ExpenseBuilder setType(SplitType type) {
        this.type = type;
        return this;
    }

    public ExpenseBuilder setPaidBy(User paidBy) {
        this.paidBy = paidBy;
        return this;
    }

    public ExpenseBuilder setAmount(double amount) {
        this.amount = amount;
        return this;
    }

    public ExpenseBuilder setSplits(List<Split> splits) {
        this.splits = splits;
        return this;
    }

    public ExpenseBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ExpenseBuilder setCategory(ExpenseCategory category) {
        this.category = category;
        return this;
    }

    // BUILDER PATTERN: Final build method delegates to Factory
    // WHY: Combines Builder's convenience with Factory's validation
    public Expense build() {
        if (type == null || paidBy == null || splits == null) {
            throw new IllegalArgumentException("Required fields missing");
        }
        return ExpenseFactory.createExpense(type, paidBy, amount, splits,
                description, category);
    }
}

// ============================================================================
// OBSERVER PATTERN - Event Notifications
// WHY: Users need to be notified when they're added to expenses
// BENEFIT: Loose coupling between expense creation and notifications
// ============================================================================

interface ExpenseObserver {
    void onExpenseAdded(Expense expense, User affectedUser, double amountOwed);
}

// OBSERVER PATTERN: Concrete observer for email notifications
class EmailNotificationObserver implements ExpenseObserver {
    @Override
    public void onExpenseAdded(Expense expense, User affectedUser, double amountOwed) {
        System.out.println("📧 EMAIL to " + affectedUser.getEmail() + ": " +
                "You were added to expense '" + expense.getDescription() +
                "' for ₹" + String.format("%.2f", amountOwed));
    }
}

// OBSERVER PATTERN: Concrete observer for SMS notifications
class SMSNotificationObserver implements ExpenseObserver {
    @Override
    public void onExpenseAdded(Expense expense, User affectedUser, double amountOwed) {
        System.out.println("📱 SMS: You owe ₹" + String.format("%.2f", amountOwed) +
                " for " + expense.getDescription());
    }
}

// ============================================================================
// COMMAND PATTERN - Undo/Redo Support
// WHY: Need ability to undo expense additions
// BENEFIT: Encapsulates operations, supports undo/redo, maintains history
// ============================================================================

interface Command {
    void execute();

    void undo();

    String getDescription();
}

// COMMAND PATTERN: Command to add expense (with undo capability)
class AddExpenseCommand implements Command {
    private SplitwiseManager manager;
    private Expense expense;
    private boolean executed = false;

    public AddExpenseCommand(SplitwiseManager manager, Expense expense) {
        this.manager = manager;
        this.expense = expense;
    }

    // COMMAND PATTERN: Execute adds the expense and updates balances
    @Override
    public void execute() {
        if (!executed) {
            manager.executeAddExpense(expense);
            executed = true;
            System.out.println("✅ Executed: Added expense '" + expense.getDescription() + "'");
        }
    }

    // COMMAND PATTERN: Undo removes the expense and reverses balances
    @Override
    public void undo() {
        if (executed) {
            manager.executeRemoveExpense(expense);
            executed = false;
            System.out.println("↩️  Undone: Removed expense '" + expense.getDescription() + "'");
        }
    }

    @Override
    public String getDescription() {
        return "Add expense: " + expense.getDescription() + " (₹" + expense.getAmount() + ")";
    }
}

// COMMAND PATTERN: Manages command history for undo/redo
class CommandManager {
    private Stack<Command> executedCommands = new Stack<>();
    private Stack<Command> undoneCommands = new Stack<>();

    // COMMAND PATTERN: Execute and save to history
    public void executeCommand(Command command) {
        command.execute();
        executedCommands.push(command);
        undoneCommands.clear(); // Clear redo stack when new command is executed
    }

    // COMMAND PATTERN: Undo last command
    public void undo() {
        if (!executedCommands.isEmpty()) {
            Command command = executedCommands.pop();
            command.undo();
            undoneCommands.push(command);
        } else {
            System.out.println("❌ Nothing to undo");
        }
    }

    // COMMAND PATTERN: Redo last undone command
    public void redo() {
        if (!undoneCommands.isEmpty()) {
            Command command = undoneCommands.pop();
            command.execute();
            executedCommands.push(command);
        } else {
            System.out.println("❌ Nothing to redo");
        }
    }

    public void showHistory() {
        System.out.println("\n📜 Command History:");
        if (executedCommands.isEmpty()) {
            System.out.println("No commands executed yet");
            return;
        }
        for (Command cmd : executedCommands) {
            System.out.println("  - " + cmd.getDescription());
        }
    }
}

// ============================================================================
// Balance Sheet - Tracks who owes whom
// ============================================================================

class BalanceSheet {
    private Map<String, Map<String, Double>> sheet = new HashMap<>();

    public void addTransaction(String paidBy, String owedBy, double amount) {
        if (!sheet.containsKey(paidBy))
            sheet.put(paidBy, new HashMap<>());
        if (!sheet.containsKey(owedBy))
            sheet.put(owedBy, new HashMap<>());

        // owedBy owes to paidBy
        sheet.get(paidBy).put(owedBy, sheet.get(paidBy).getOrDefault(owedBy, 0.0) + amount);

        // Reverse entry
        sheet.get(owedBy).put(paidBy, sheet.get(owedBy).getOrDefault(paidBy, 0.0) - amount);
    }

    public void removeTransaction(String paidBy, String owedBy, double amount) {
        if (sheet.containsKey(paidBy) && sheet.get(paidBy).containsKey(owedBy)) {
            double current = sheet.get(paidBy).get(owedBy);
            sheet.get(paidBy).put(owedBy, current - amount);
        }

        if (sheet.containsKey(owedBy) && sheet.get(owedBy).containsKey(paidBy)) {
            double current = sheet.get(owedBy).get(paidBy);
            sheet.get(owedBy).put(paidBy, current + amount);
        }
    }

    public void simplifyBalances() {
        // Simplify: If A owes B $50 and B owes A $30, net to A owes B $20
        for (String user1 : sheet.keySet()) {
            for (String user2 : new ArrayList<>(sheet.get(user1).keySet())) {
                if (sheet.containsKey(user2) && sheet.get(user2).containsKey(user1)) {
                    double amt1 = sheet.get(user1).get(user2);
                    double amt2 = sheet.get(user2).get(user1);

                    if (amt1 > 0 && amt2 < 0) {
                        double netAmount = amt1 + amt2;
                        if (netAmount > 0) {
                            sheet.get(user1).put(user2, netAmount);
                            sheet.get(user2).put(user1, -netAmount);
                        } else {
                            sheet.get(user1).put(user2, 0.0);
                            sheet.get(user2).put(user1, netAmount);
                        }
                    }
                }
            }
        }
    }

    public void showBalances() {
        System.out.println("\n💰 All Balances:");
        boolean hasBalances = false;
        for (String user : sheet.keySet()) {
            for (String other : sheet.get(user).keySet()) {
                double amt = sheet.get(user).get(other);
                if (amt > 0.01) { // Only show positive balances
                    System.out.println("  " + other + " owes " + user + ": ₹" +
                            String.format("%.2f", amt));
                    hasBalances = true;
                }
            }
        }
        if (!hasBalances) {
            System.out.println("  All settled up! 🎉");
        }
    }

    public void showBalance(String userId) {
        System.out.println("\n💰 Balance for " + userId + ":");
        if (!sheet.containsKey(userId)) {
            System.out.println("  No balances");
            return;
        }

        boolean hasBalances = false;
        for (String other : sheet.get(userId).keySet()) {
            double amt = sheet.get(userId).get(other);
            if (amt > 0.01) {
                System.out.println("  " + other + " owes you: ₹" +
                        String.format("%.2f", amt));
                hasBalances = true;
            } else if (amt < -0.01) {
                System.out.println("  You owe " + other + ": ₹" +
                        String.format("%.2f", Math.abs(amt)));
                hasBalances = true;
            }
        }
        if (!hasBalances) {
            System.out.println("  All settled up! 🎉");
        }
    }
}

// ============================================================================
// SINGLETON PATTERN - Splitwise Manager
// WHY: Should have only one instance managing all expenses and balances
// BENEFIT: Global access point, controlled instantiation, shared state
// ============================================================================

class SplitwiseManager {
    // SINGLETON PATTERN: Single instance for entire application
    private static SplitwiseManager instance;

    private Map<String, User> users = new HashMap<>();
    private BalanceSheet balanceSheet = new BalanceSheet();

    // OBSERVER PATTERN: List of observers to notify
    private List<ExpenseObserver> observers = new ArrayList<>();

    // COMMAND PATTERN: Command manager for undo/redo
    private CommandManager commandManager = new CommandManager();

    // SINGLETON PATTERN: Private constructor prevents direct instantiation
    private SplitwiseManager() {
        System.out.println("🏦 Splitwise Manager initialized (Singleton)");
    }

    // SINGLETON PATTERN: Thread-safe getInstance method
    public static synchronized SplitwiseManager getInstance() {
        if (instance == null) {
            instance = new SplitwiseManager();
        }
        return instance;
    }

    // OBSERVER PATTERN: Register observers for notifications
    public void addObserver(ExpenseObserver observer) {
        observers.add(observer);
        System.out.println("📢 Observer registered: " + observer.getClass().getSimpleName());
    }

    // OBSERVER PATTERN: Notify all observers about new expense
    private void notifyObservers(Expense expense) {
        for (Split split : expense.getSplits()) {
            User user = split.getUser();
            if (!user.getId().equals(expense.getPaidBy().getId())) {
                for (ExpenseObserver observer : observers) {
                    observer.onExpenseAdded(expense, user, split.getAmount());
                }
            }
        }
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
        System.out.println("👤 User added: " + user.getName());
    }

    // COMMAND PATTERN: Add expense using command for undo support
    public void addExpenseWithUndo(Expense expense) {
        Command command = new AddExpenseCommand(this, expense);
        commandManager.executeCommand(command);
    }

    // Called by AddExpenseCommand
    public void executeAddExpense(Expense expense) {
        // Update balances
        for (Split s : expense.getSplits()) {
            if (!s.getUser().getId().equals(expense.getPaidBy().getId())) {
                balanceSheet.addTransaction(
                        expense.getPaidBy().getId(),
                        s.getUser().getId(),
                        s.getAmount());
            }
        }

        // OBSERVER PATTERN: Notify observers
        notifyObservers(expense);
    }

    // Called by undo
    public void executeRemoveExpense(Expense expense) {
        // Reverse balances
        for (Split s : expense.getSplits()) {
            if (!s.getUser().getId().equals(expense.getPaidBy().getId())) {
                balanceSheet.removeTransaction(
                        expense.getPaidBy().getId(),
                        s.getUser().getId(),
                        s.getAmount());
            }
        }
    }

    public void undo() {
        commandManager.undo();
    }

    public void redo() {
        commandManager.redo();
    }

    public void showHistory() {
        commandManager.showHistory();
    }

    public void simplifyBalances() {
        balanceSheet.simplifyBalances();
        System.out.println("✨ Balances simplified");
    }

    public void showBalances() {
        balanceSheet.showBalances();
    }

    public void showBalance(String userId) {
        balanceSheet.showBalance(userId);
    }

    public User getUser(String userId) {
        return users.get(userId);
    }
}

// ============================================================================
// MAIN APPLICATION
// ============================================================================

public class SplitwiseApp {
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("🎯 SPLITWISE - Complete Design Patterns Demo");
        System.out.println("=".repeat(60));

        // SINGLETON PATTERN: Get single instance
        SplitwiseManager manager = SplitwiseManager.getInstance();

        // OBSERVER PATTERN: Register notification observers
        manager.addObserver(new EmailNotificationObserver());
        manager.addObserver(new SMSNotificationObserver());

        // Create users
        User u1 = new User("U1", "Naveen", "naveen@mail.com", "99999");
        User u2 = new User("U2", "Rahul", "rahul@mail.com", "88888");
        User u3 = new User("U3", "Arjun", "arjun@mail.com", "77777");

        manager.addUser(u1);
        manager.addUser(u2);
        manager.addUser(u3);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("📝 SCENARIO 1: Equal Split - Dinner");
        System.out.println("=".repeat(60));

        // BUILDER PATTERN: Create expense using builder
        List<Split> equalSplits = new ArrayList<>();
        equalSplits.add(new EqualSplit(u1));
        equalSplits.add(new EqualSplit(u2));
        equalSplits.add(new EqualSplit(u3));

        Expense expense1 = new ExpenseBuilder()
                .setType(SplitType.EQUAL)
                .setPaidBy(u1)
                .setAmount(300)
                .setSplits(equalSplits)
                .setDescription("Dinner at Restaurant")
                .setCategory(ExpenseCategory.FOOD)
                .build();

        // COMMAND PATTERN: Add with undo support
        manager.addExpenseWithUndo(expense1);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("📝 SCENARIO 2: Exact Split - Shopping");
        System.out.println("=".repeat(60));

        List<Split> exactSplits = new ArrayList<>();
        exactSplits.add(new ExactSplit(u1, 200));
        exactSplits.add(new ExactSplit(u2, 200));
        exactSplits.add(new ExactSplit(u3, 200));

        Expense expense2 = new ExpenseBuilder()
                .setType(SplitType.EXACT)
                .setPaidBy(u2)
                .setAmount(600)
                .setSplits(exactSplits)
                .setDescription("Grocery Shopping")
                .setCategory(ExpenseCategory.SHOPPING)
                .build();

        manager.addExpenseWithUndo(expense2);

        System.out.println("\n" + "=".repeat(60));
        System.out.println("📝 SCENARIO 3: Percent Split - Trip");
        System.out.println("=".repeat(60));

        List<Split> percentSplits = new ArrayList<>();
        percentSplits.add(new PercentSplit(u1, 50));
        percentSplits.add(new PercentSplit(u2, 30));
        percentSplits.add(new PercentSplit(u3, 20));

        Expense expense3 = new ExpenseBuilder()
                .setType(SplitType.PERCENT)
                .setPaidBy(u3)
                .setAmount(1000)
                .setSplits(percentSplits)
                .setDescription("Weekend Trip to Goa")
                .setCategory(ExpenseCategory.TRAVEL)
                .build();

        manager.addExpenseWithUndo(expense3);

        // Show balances
        manager.showBalances();

        // COMMAND PATTERN: Show history
        manager.showHistory();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("↩️  DEMO: Undo Last Expense");
        System.out.println("=".repeat(60));

        // COMMAND PATTERN: Undo
        manager.undo();
        manager.showBalances();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("↪️  DEMO: Redo Last Expense");
        System.out.println("=".repeat(60));

        // COMMAND PATTERN: Redo
        manager.redo();
        manager.showBalances();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✨ DEMO: Simplify Balances");
        System.out.println("=".repeat(60));

        manager.simplifyBalances();
        manager.showBalances();

        System.out.println("\n" + "=".repeat(60));
        System.out.println("👤 Individual Balance Check");
        System.out.println("=".repeat(60));

        manager.showBalance("U1");

        System.out.println("\n" + "=".repeat(60));
        System.out.println("✅ DESIGN PATTERNS DEMONSTRATED:");
        System.out.println("=".repeat(60));
        System.out.println("1. ✅ STRATEGY PATTERN - Split validation strategies");
        System.out.println("2. ✅ FACTORY PATTERN - Expense creation");
        System.out.println("3. ✅ BUILDER PATTERN - Expense builder for complex objects");
        System.out.println("4. ✅ OBSERVER PATTERN - Notification system");
        System.out.println("5. ✅ COMMAND PATTERN - Undo/Redo support");
        System.out.println("6. ✅ SINGLETON PATTERN - Single manager instance");
        System.out.println("=".repeat(60));
    }
}