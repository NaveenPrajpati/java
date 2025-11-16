package libraryManagement4;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

//uses Repository + Factory + Observer design pattern
// Enums
enum BookStatus {
    AVAILABLE, BORROWED, RESERVED, LOST
}

enum MembershipType {
    STUDENT, FACULTY, PUBLIC
}

// Core Domain Models
class Book {
    private final String isbn;
    private String title;
    private String author;
    private String category;
    private String publisher;
    private int publicationYear;

    public Book(String isbn, String title, String author, String category) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN cannot be null or empty");
        }
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.category = category;
    }

    // Getters
    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getCategory() {
        return category;
    }

    public String getPublisher() {
        return publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    // Setters (excluding ISBN as it's immutable)
    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setPublicationYear(int year) {
        this.publicationYear = year;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Book))
            return false;
        Book book = (Book) o;
        return isbn.equals(book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }
}

class BookItem {
    private final String barcode;
    private final Book book;
    private BookStatus status;
    private String rack;
    private LocalDate borrowedDate;
    private LocalDate dueDate;

    public BookItem(String barcode, Book book, String rack) {
        if (barcode == null || barcode.trim().isEmpty()) {
            throw new IllegalArgumentException("Barcode cannot be null or empty");
        }
        this.barcode = barcode;
        this.book = book;
        this.rack = rack;
        this.status = BookStatus.AVAILABLE;
    }

    public String getBarcode() {
        return barcode;
    }

    public Book getBook() {
        return book;
    }

    public BookStatus getStatus() {
        return status;
    }

    public String getRack() {
        return rack;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }

    public void setRack(String rack) {
        this.rack = rack;
    }

    public void borrow(LocalDate dueDate) {
        this.status = BookStatus.BORROWED;
        this.borrowedDate = LocalDate.now();
        this.dueDate = dueDate;
    }

    public void returnItem() {
        this.status = BookStatus.AVAILABLE;
        this.borrowedDate = null;
        this.dueDate = null;
    }

    public boolean isOverdue() {
        return dueDate != null && LocalDate.now().isAfter(dueDate);
    }
}

class User {
    private final String id;
    private String name;
    private String email;
    private MembershipType membershipType;
    private final List<BorrowRecord> borrowRecords;
    private int maxBorrowLimit;

    public User(String id, String name, String email, MembershipType type) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.membershipType = type;
        this.borrowRecords = new ArrayList<>();
        this.maxBorrowLimit = calculateBorrowLimit(type);
    }

    private int calculateBorrowLimit(MembershipType type) {
        switch (type) {
            case FACULTY:
                return 10;
            case STUDENT:
                return 5;
            case PUBLIC:
                return 3;
            default:
                return 3;
        }
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

    public MembershipType getMembershipType() {
        return membershipType;
    }

    public List<BorrowRecord> getBorrowRecords() {
        return new ArrayList<>(borrowRecords);
    }

    public int getMaxBorrowLimit() {
        return maxBorrowLimit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<BorrowRecord> getActiveBorrows() {
        return borrowRecords.stream()
                .filter(r -> r.getReturnDate() == null)
                .collect(Collectors.toList());
    }

    public boolean canBorrow() {
        return getActiveBorrows().size() < maxBorrowLimit;
    }

    public void addBorrowRecord(BorrowRecord record) {
        borrowRecords.add(record);
    }
}

class BorrowRecord {
    private final String id;
    private final BookItem bookItem;
    private final User user;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private double fine;

    public BorrowRecord(BookItem bookItem, User user, LocalDate dueDate) {
        this.id = UUID.randomUUID().toString();
        this.bookItem = bookItem;
        this.user = user;
        this.borrowDate = LocalDate.now();
        this.dueDate = dueDate;
        this.fine = 0.0;
    }

    public String getId() {
        return id;
    }

    public BookItem getBookItem() {
        return bookItem;
    }

    public User getUser() {
        return user;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public double getFine() {
        return fine;
    }

    public void setReturnDate(LocalDate date) {
        this.returnDate = date;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public boolean isOverdue() {
        return returnDate == null && LocalDate.now().isAfter(dueDate);
    }
}

// Strategy Pattern for Search
interface SearchStrategy {
    List<BookItem> search(List<BookItem> items, String query);
}

class TitleSearchStrategy implements SearchStrategy {
    @Override
    public List<BookItem> search(List<BookItem> items, String query) {
        return items.stream()
                .filter(item -> item.getBook().getTitle().toLowerCase()
                        .contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}

class AuthorSearchStrategy implements SearchStrategy {
    @Override
    public List<BookItem> search(List<BookItem> items, String query) {
        return items.stream()
                .filter(item -> item.getBook().getAuthor().toLowerCase()
                        .contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}

class IsbnSearchStrategy implements SearchStrategy {
    @Override
    public List<BookItem> search(List<BookItem> items, String query) {
        return items.stream()
                .filter(item -> item.getBook().getIsbn().equalsIgnoreCase(query))
                .collect(Collectors.toList());
    }
}

class CategorySearchStrategy implements SearchStrategy {
    @Override
    public List<BookItem> search(List<BookItem> items, String query) {
        return items.stream()
                .filter(item -> item.getBook().getCategory().toLowerCase()
                        .contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }
}

// Repository Pattern
interface LibraryRepository {
    void addBookItem(BookItem item);

    BookItem getBookItemByBarcode(String barcode);

    List<BookItem> getAllItems();

    List<BookItem> getAvailableItems();

    void updateBookItem(BookItem item);

    boolean removeBookItem(String barcode);
}

class InMemoryLibraryRepository implements LibraryRepository {
    private final Map<String, BookItem> items = new HashMap<>();

    @Override
    public void addBookItem(BookItem item) {
        items.put(item.getBarcode(), item);
    }

    @Override
    public BookItem getBookItemByBarcode(String barcode) {
        return items.get(barcode);
    }

    @Override
    public List<BookItem> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<BookItem> getAvailableItems() {
        return items.values().stream()
                .filter(item -> item.getStatus() == BookStatus.AVAILABLE)
                .collect(Collectors.toList());
    }

    @Override
    public void updateBookItem(BookItem item) {
        items.put(item.getBarcode(), item);
    }

    @Override
    public boolean removeBookItem(String barcode) {
        return items.remove(barcode) != null;
    }
}

// User Repository
interface UserRepository {
    void addUser(User user);

    User getUserById(String id);

    List<User> getAllUsers();

    boolean removeUser(String id);
}

class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    @Override
    public User getUserById(String id) {
        return users.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean removeUser(String id) {
        return users.remove(id) != null;
    }
}

// Fine Calculator Strategy
interface FineCalculator {
    double calculateFine(BorrowRecord record);
}

class StandardFineCalculator implements FineCalculator {
    private static final double DAILY_FINE = 5.0;
    private static final double MAX_FINE = 500.0;

    @Override
    public double calculateFine(BorrowRecord record) {
        if (!record.isOverdue())
            return 0.0;

        long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(
                record.getDueDate(),
                record.getReturnDate() != null ? record.getReturnDate() : LocalDate.now());

        double fine = daysOverdue * DAILY_FINE;
        return Math.min(fine, MAX_FINE);
    }
}

// Main Library Service
class LibraryService {
    private final LibraryRepository bookRepository;
    private final UserRepository userRepository;
    private final FineCalculator fineCalculator;
    private SearchStrategy searchStrategy;

    public LibraryService(LibraryRepository bookRepository,
            UserRepository userRepository,
            FineCalculator fineCalculator) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.fineCalculator = fineCalculator;
    }

    public void setSearchStrategy(SearchStrategy strategy) {
        this.searchStrategy = strategy;
    }

    public List<BookItem> search(String query) {
        if (searchStrategy == null) {
            throw new IllegalStateException("Search strategy not set");
        }
        return searchStrategy.search(bookRepository.getAllItems(), query);
    }

    public void addBookItem(BookItem item) {
        bookRepository.addBookItem(item);
    }

    public void addUser(User user) {
        userRepository.addUser(user);
    }

    public BorrowRecord borrowBook(String barcode, String userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (!user.canBorrow()) {
            throw new IllegalStateException("User has reached borrow limit");
        }

        BookItem item = bookRepository.getBookItemByBarcode(barcode);
        if (item == null) {
            throw new IllegalArgumentException("Book item not found");
        }

        if (item.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available");
        }

        // Calculate due date based on membership type
        int loanPeriod = user.getMembershipType() == MembershipType.FACULTY ? 30 : 14;
        LocalDate dueDate = LocalDate.now().plusDays(loanPeriod);

        item.borrow(dueDate);
        bookRepository.updateBookItem(item);

        BorrowRecord record = new BorrowRecord(item, user, dueDate);
        user.addBorrowRecord(record);

        System.out.println(user.getName() + " borrowed " +
                item.getBook().getTitle() + " (Due: " + dueDate + ")");
        return record;
    }

    public double returnBook(String barcode, String userId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        BookItem item = bookRepository.getBookItemByBarcode(barcode);
        if (item == null) {
            throw new IllegalArgumentException("Book item not found");
        }

        if (item.getStatus() != BookStatus.BORROWED) {
            throw new IllegalStateException("Book is not currently borrowed");
        }

        // Find the active borrow record
        BorrowRecord record = user.getActiveBorrows().stream()
                .filter(r -> r.getBookItem().getBarcode().equals(barcode))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No active borrow record found"));

        LocalDate returnDate = LocalDate.now();
        record.setReturnDate(returnDate);

        double fine = fineCalculator.calculateFine(record);
        record.setFine(fine);

        item.returnItem();
        bookRepository.updateBookItem(item);

        System.out.println(user.getName() + " returned " + item.getBook().getTitle());
        if (fine > 0) {
            System.out.println("Fine charged: ₹" + fine);
        }

        return fine;
    }

    public List<BookItem> getAvailableBooks() {
        return bookRepository.getAvailableItems();
    }

    public List<BorrowRecord> getUserBorrowHistory(String userId) {
        User user = userRepository.getUserById(userId);
        return user != null ? user.getBorrowRecords() : Collections.emptyList();
    }

    public List<BorrowRecord> getOverdueBooks() {
        return userRepository.getAllUsers().stream()
                .flatMap(user -> user.getActiveBorrows().stream())
                .filter(BorrowRecord::isOverdue)
                .collect(Collectors.toList());
    }
}

// Factory Pattern
class BookFactory {
    public static Book createBook(String isbn, String title, String author, String category) {
        return new Book(isbn, title, author, category);
    }
}

class BookItemFactory {
    public static BookItem createBookItem(Book book, String rack) {
        String barcode = generateBarcode(book);
        return new BookItem(barcode, book, rack);
    }

    private static String generateBarcode(Book book) {
        return book.getIsbn() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}

// Demo/Manager Class
public class Manager {
    public static void main(String[] args) {
        // Initialize repositories and service
        LibraryRepository bookRepo = new InMemoryLibraryRepository();
        UserRepository userRepo = new InMemoryUserRepository();
        FineCalculator fineCalc = new StandardFineCalculator();
        LibraryService library = new LibraryService(bookRepo, userRepo, fineCalc);

        // Create books
        Book book1 = BookFactory.createBook("978-0-13-468599-1",
                "Clean Code",
                "Robert Martin",
                "Programming");
        Book book2 = BookFactory.createBook("978-0-201-63361-0",
                "Design Patterns",
                "Gang of Four",
                "Programming");

        // Create book items (physical copies)
        BookItem item1 = BookItemFactory.createBookItem(book1, "A-101");
        BookItem item2 = BookItemFactory.createBookItem(book1, "A-102");
        BookItem item3 = BookItemFactory.createBookItem(book2, "A-201");

        library.addBookItem(item1);
        library.addBookItem(item2);
        library.addBookItem(item3);

        // Create users
        User user1 = new User("U001", "Alice", "alice@example.com", MembershipType.STUDENT);
        User user2 = new User("U002", "Bob", "bob@example.com", MembershipType.FACULTY);

        library.addUser(user1);
        library.addUser(user2);

        // Search for books
        library.setSearchStrategy(new TitleSearchStrategy());
        List<BookItem> results = library.search("Clean Code");
        System.out.println("Search results: " + results.size() + " items found");

        // Borrow books
        try {
            library.borrowBook(item1.getBarcode(), user1.getId());
            library.borrowBook(item3.getBarcode(), user2.getId());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        // Return books
        try {
            double fine = library.returnBook(item1.getBarcode(), user1.getId());
            System.out.println("Return successful. Fine: ₹" + fine);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }

        // Check overdue books
        System.out.println("\nOverdue books: " + library.getOverdueBooks().size());
    }
}