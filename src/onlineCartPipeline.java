import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
/*
 * ONLINE STORE - CART & ORDER PIPELINE  (Final Project, all 12 modules blended)
 *
 *  1  Product Catalog         -> Product, ElectronicProduct, ProductCatalog
 *  2  Shopping Cart           -> CartItem, ShoppingCart
 *  3  Inventory & Stock       -> Inventory
 *  4  User Account & Session  -> User, Customer, Guest, UserSession
 *  5  Checkout & Shipping     -> Checkout
 *  6  Order Management        -> OrderStatus, OrderItem, Order, OrderBook
 *  7  Payment Gateway         -> PaymentMethod (Card / UPI / COD), PaymentGateway
 *  8  Order Confirmation      -> Notifier, EmailNotifier, SmsNotifier, OrderConfirmation
 *  9  Data Storage / Files    -> DataStorage
 * 10  Storefront UI pages     -> UI
 * 11  Validation & Errors     -> Validator + custom exceptions
 * 12  Utility & Pricing       -> Utility
 */

// ===================== MODULE 1: product catalog =====================
class Product {
    protected int id;
    protected String name;
    protected double price;
    protected String description;
    protected String image;
    protected int stock;
    protected String category;
    Product(int id, String name, double price, String description, String image, int stock, String category) {
        this.id = id; this.name = name; this.price = price;
        this.description = description; this.image = image;
        this.stock = stock; this.category = category;
    }
    Product(int id, String name, double price, int stock) {
        this(id, name, price, "No description", name.toLowerCase() + ".jpg", stock, "General");
    }
    void display() {
        System.out.println("ID: " + id + " | " + name + " | " + Utility.formatPrice(price) + " | Stock: " + stock + " | " + category);
        System.out.println("Desc : " + description);
        System.out.println("--------------------------------");
    }
}
class ElectronicProduct extends Product {
    int warrantyMonths;
    ElectronicProduct(int id, String name, double price, String desc, String image, int stock, int warrantyMonths) {
        super(id, name, price, desc, image, stock, "Electronics");
        this.warrantyMonths = warrantyMonths;
    }
    @Override
    void display() {
        System.out.println("ID: " + id + " | " + name + " | " + Utility.formatPrice(price) + " | Stock: " + stock + " | " + category);
        System.out.println("Desc : " + description + " | Warranty: " + warrantyMonths + " months");
        System.out.println("--------------------------------");
    }
}
class ProductCatalog {
    private final List<Product> products = new ArrayList<>();
    void loadProducts() {
        products.add(new ElectronicProduct(101, "Laptop", 45000, "15-inch gaming laptop", "laptop.jpg", 10, 24));
        products.add(new Product(102, "Mouse", 500, "Wireless mouse", "mouse.jpg", 50, "Accessories"));
        products.add(new Product(103, "Keyboard", 1200, "Mechanical keyboard", "keyboard.jpg", 30, "Accessories"));
        products.add(new ElectronicProduct(104, "Headphones", 2500, "Noise cancelling", "headphones.jpg", 20, 12));
        products.add(new Product(105, "USB Drive", 400, "64GB pen drive", "usb.jpg", 100, "Storage"));
        products.add(new Product(106, "Notebook", 60, 200));
    }
    List<Product> all() { return products; }
    void listAll() {
        System.out.println("\n===== PRODUCT CATALOG =====");
        for (Product p : products) p.display();
    }
    Product getById(int id) throws ProductNotFoundException {
        for (Product p : products) if (p.id == id) return p;
        throw new ProductNotFoundException(id);
    }
    void search(String keyword) {
        System.out.println("\nSearch results for: " + keyword);
        boolean found = false;
        for (Product p : products) {
            if (p.name.toLowerCase().contains(keyword.trim().toLowerCase())) { p.display(); found = true; }
        }
        if (!found) System.out.println("No product found.");
    }
    void search(double min, double max) {
        System.out.println("\nProducts between " + Utility.formatPrice(min) + " and " + Utility.formatPrice(max));
        boolean found = false;
        for (Product p : products) {
            if (p.price >= min && p.price <= max) { p.display(); found = true; }
        }
        if (!found) System.out.println("Nothing in that range.");
    }
    void showByCategory() {
        Map<String, List<String>> groups = new TreeMap<>();
        for (Product p : products) groups.computeIfAbsent(p.category, k -> new ArrayList<>()).add(p.name);
        System.out.println("\n===== PRODUCTS BY CATEGORY =====");
        for (Map.Entry<String, List<String>> e : groups.entrySet()) {
            System.out.println(e.getKey().toUpperCase() + ": " + String.join(", ", e.getValue()));
        }
    }
}

// ===================== MODULE 2: shopping cart =====================
class CartItem {
    int productId; String name; double price; int quantity;
    CartItem(int productId, String name, double price, int quantity) {
        this.productId = productId; this.name = name; this.price = price; this.quantity = quantity;
    }
    double getSubtotal() { return price * quantity; }
    @Override
    public String toString() {
        return String.format("%-12s Qty: %-3d Price: Rs.%-9.2f Subtotal: Rs.%.2f", name, quantity, price, getSubtotal());
    }
}
class ShoppingCart {
    private static final int MAX_ITEMS = 20;
    private final List<CartItem> items = new ArrayList<>();
    List<CartItem> getItems() { return items; }
    int distinctItems() { return items.size(); }
    boolean isEmpty() { return items.isEmpty(); }
    private CartItem find(int id) {
        for (CartItem c : items) if (c.productId == id) return c;
        return null;
    }
    void addItem(int productId, String name, double price, int qty) {
        CartItem existing = find(productId);
        if (existing != null) {
            existing.quantity += qty;
            System.out.println("Quantity updated for " + name);
            return;
        }
        if (items.size() >= MAX_ITEMS) throw new CartFullException(MAX_ITEMS);
        items.add(new CartItem(productId, name, price, qty));
        System.out.println(name + " added to cart.");
    }
    void addItem(Product p, int qty) { addItem(p.id, p.name, p.price, qty); }
    int quantityOf(int productId) {
        CartItem c = find(productId);
        return c == null ? 0 : c.quantity;
    }
    void removeItem(int productId) throws ItemNotInCartException {
        CartItem c = find(productId);
        if (c == null) throw new ItemNotInCartException(productId);
        items.remove(c);
        System.out.println(c.name + " removed from cart.");
    }
    void updateQuantity(int productId, int newQty) throws ItemNotInCartException {
        CartItem c = find(productId);
        if (c == null) throw new ItemNotInCartException(productId);
        if (newQty <= 0) removeItem(productId);
        else { c.quantity = newQty; System.out.println("Quantity updated."); }
    }
    double calculateTotal() {
        double t = 0;
        for (CartItem c : items) t += c.getSubtotal();
        return t;
    }
    void clear() { items.clear(); System.out.println("Cart cleared."); }
    void display() {
        if (items.isEmpty()) { System.out.println("Cart is empty."); return; }
        System.out.println("\n===== YOUR CART =====");
        int i = 1;
        for (CartItem c : items) System.out.println((i++) + ". " + c);
        System.out.println("TOTAL: " + Utility.formatPrice(calculateTotal()));
        System.out.println("=====================");
    }
}

// ===================== MODULE 3: inventory =====================
class Inventory {
    private final ProductCatalog catalog;
    private static final int LOW_STOCK_LIMIT = 15;
    Inventory(ProductCatalog catalog) { this.catalog = catalog; }
    void checkStock(int productId, int qty) throws OutOfStockException, ProductNotFoundException {
        Product p = catalog.getById(productId);
        if (p.stock < qty) throw new OutOfStockException(p.name, p.stock);
    }
    void decreaseStock(int productId, int qty) throws OutOfStockException, ProductNotFoundException {
        checkStock(productId, qty);
        Product p = catalog.getById(productId);
        p.stock -= qty;
        System.out.println("Stock updated for " + p.name + ". Remaining: " + p.stock);
    }
    void showStock() {
        System.out.println("\n===== CURRENT STOCK =====");
        List<String> low = new ArrayList<>();
        for (Product p : catalog.all()) {
            System.out.println("Product ID: " + p.id + " | " + p.name + " | Stock: " + p.stock);
            if (p.stock <= LOW_STOCK_LIMIT) low.add(p.name);
        }
        System.out.println("Low stock (<= " + LOW_STOCK_LIMIT + "): " + (low.isEmpty() ? "none" : String.join(", ", low)));
    }
}

// ===================== MODULE 4: users & session =====================
abstract class User {
    protected String username, password;
    User(String username, String password) { this.username = username; this.password = password; }
    abstract String getRole();
    boolean matches(String u, String p) { return username.equals(u) && password.equals(p); }
    @Override public String toString() { return username + " (" + getRole() + ")"; }
}
class Customer extends User {
    Customer(String u, String p) { super(u, p); }
    @Override String getRole() { return "Customer"; }
}
class Guest extends User {
    Guest() { super("Guest", ""); }
    @Override String getRole() { return "Guest"; }
}
class UserSession {
    private static final int MAX_USERS = 10;
    private final List<User> users = new ArrayList<>();
    private User current = null;
    void register(String uname, String pass) throws AuthException {
        uname = uname.trim();
        if (uname.isEmpty()) throw new AuthException("Username cannot be empty");
        if (uname.contains(" ")) throw new AuthException("Username cannot contain spaces");
        if (pass.length() < 4) throw new AuthException("Password must be at least 4 characters");
        if (users.size() >= MAX_USERS) throw new AuthException("User limit reached");
        for (User u : users) if (u.username.equalsIgnoreCase(uname)) throw new AuthException("Username already taken");
        users.add(new Customer(uname, pass));
        System.out.println("Registration successful! Welcome, " + uname);
    }
    void login(String uname, String pass) throws AuthException {
        for (User u : users) {
            if (u.matches(uname.trim(), pass)) {
                current = u;
                System.out.println("Login successful. Welcome " + u);
                return;
            }
        }
        throw new AuthException("Invalid username or password");
    }
    void guestLogin() { current = new Guest(); }
    void logout() {
        System.out.println(current == null ? "Nobody is logged in." : "Logged out " + current.username);
        current = null;
        guestLogin();
    }
    boolean isGuest() { return current == null || current instanceof Guest; }
    String currentName() { return current == null ? "Guest" : current.username; }
    void show() { System.out.println(current == null ? "No active session." : "Current user: " + current); }
}

// ===================== MODULE 5: checkout =====================
class Checkout {
    String name, address, phone, city, pincode;
    void collectDetails(Scanner sc, String defaultName) {
        System.out.println("\n===== CHECKOUT - Shipping Details =====");
        System.out.print("Full Name" + (defaultName.equals("Guest") ? "" : " [" + defaultName + "]") + ": ");
        name = sc.nextLine().trim();
        if (name.isEmpty() && !defaultName.equals("Guest")) name = defaultName;
        System.out.print("Address: ");       address = sc.nextLine().trim();
        System.out.print("Mobile Number: "); phone = sc.nextLine().trim();
        System.out.print("City: ");          city = sc.nextLine().trim();
        System.out.print("Pincode: ");       pincode = sc.nextLine().trim();
    }
    void validate() throws InvalidDetailsException {
        List<String> errors = new ArrayList<>();
        if (name == null || name.isEmpty()) errors.add("Name is required");
        else if (!name.matches("[A-Za-z .]+")) errors.add("Name should contain letters only");
        if (address == null || address.isEmpty()) errors.add("Address is required");
        if (phone == null || !phone.matches("\\d{10}")) errors.add("Mobile must be exactly 10 digits");
        if (city == null || city.isEmpty()) errors.add("City is required");
        if (pincode == null || !pincode.matches("\\d{6}")) errors.add("Pincode must be exactly 6 digits");
        if (!errors.isEmpty()) throw new InvalidDetailsException(errors);
    }
    void reviewOrder(double total) {
        System.out.println("\n===== ORDER SUMMARY =====");
        System.out.println("Customer : " + name.toUpperCase());
        System.out.println("Address  : " + address + ", " + city + " - " + pincode);
        System.out.println("Phone    : " + OrderConfirmation.mask(phone));
        System.out.println("Total    : " + Utility.formatPrice(total));
        System.out.println("=========================");
    }
    void reviewOrder(double total, String note) {
        reviewOrder(total);
        System.out.println("Note     : " + note);
    }
}

// ===================== MODULE 6: orders =====================
enum OrderStatus { PENDING, CONFIRMED, PAYMENT_PENDING, SHIPPED, DELIVERED, CANCELLED }
class OrderItem {
    int productId; String name; double price; int quantity;
    OrderItem(int productId, String name, double price, int quantity) {
        this.productId = productId; this.name = name; this.price = price; this.quantity = quantity;
    }
    @Override public String toString() { return name + " x" + quantity + " = " + Utility.formatPrice(price * quantity); }
}
class Order {
    String orderId, customerName, address, phone;
    List<OrderItem> items = new ArrayList<>();
    double total;
    OrderStatus status = OrderStatus.PENDING;
    Date timestamp = new Date();
    Order(String customerName, String address, String phone) {
        this.orderId = Utility.generateUniqueId("ORD");
        this.customerName = customerName;
        this.address = address;
        this.phone = phone;
    }
    void addItem(int pid, String name, double price, int qty) { items.add(new OrderItem(pid, name, price, qty)); }
    void addItem(OrderItem i) { items.add(i); }
    void updateStatus(OrderStatus next) {
        if (status == OrderStatus.CANCELLED || status == OrderStatus.DELIVERED)
            throw new IllegalStateException("Order " + orderId + " is already " + status);
        status = next;
    }
    void display() {
        System.out.println("\n===== ORDER DETAILS =====");
        System.out.println("Order ID   : " + orderId);
        System.out.println("Customer   : " + customerName);
        System.out.println("Address    : " + address);
        System.out.println("Phone      : " + OrderConfirmation.mask(phone));
        System.out.println("Status     : " + status.toString().replace('_', ' '));
        System.out.println("Date       : " + new SimpleDateFormat("dd-MM-yyyy HH:mm").format(timestamp));
        System.out.println("Items:");
        for (OrderItem i : items) System.out.println("  - " + i);
        System.out.println("TOTAL PAID : " + Utility.formatPrice(total));
        System.out.println("=========================");
    }
    @Override public String toString() { return orderId + " | " + customerName + " | " + Utility.formatPrice(total) + " | " + status; }
}
class OrderBook {
    private static final int MAX_ORDERS = 50;
    private final List<Order> orders = new ArrayList<>();
    void save(Order o) {
        if (orders.size() >= MAX_ORDERS) throw new IllegalStateException("Order book is full");
        orders.add(o);
        System.out.println("Order saved successfully! ID: " + o.orderId);
    }
    void showHistory() {
        if (orders.isEmpty()) { System.out.println("No orders placed yet."); return; }
        Map<OrderStatus, List<Order>> groups = new EnumMap<>(OrderStatus.class);
        for (Order o : orders) groups.computeIfAbsent(o.status, k -> new ArrayList<>()).add(o);
        System.out.println("\n===== ORDER HISTORY (grouped by status) =====");
        for (Map.Entry<OrderStatus, List<Order>> e : groups.entrySet()) {
            System.out.println(e.getKey() + " (" + e.getValue().size() + ")");
            for (Order o : e.getValue()) System.out.println("   " + o);
        }
    }
}

// ===================== MODULE 7: payments =====================
abstract class PaymentMethod {
    protected String label;
    PaymentMethod(String label) { this.label = label; }
    abstract double fee(double amount);
    void validate(double amount) throws PaymentFailedException {
        if (amount <= 0) throw new PaymentFailedException("Amount must be positive");
    }
    boolean pay(double amount) throws PaymentFailedException {
        validate(amount);
        System.out.println("\nProcessing " + Utility.formatPrice(amount + fee(amount)) + " via " + label + "...");
        try { Thread.sleep(400); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        return new Random().nextInt(10) < 8;       // 80% success, mock gateway
    }
    @Override public String toString() { return label; }
}
class CardPayment extends PaymentMethod {
    CardPayment() { super("Card"); }
    @Override double fee(double a) { return a * 0.02; }
    @Override void validate(double a) throws PaymentFailedException {
        super.validate(a);
        if (a > 200000) throw new PaymentFailedException("Card limit of Rs.200000 exceeded");
    }
}
class UpiPayment extends PaymentMethod {
    UpiPayment() { super("UPI"); }
    @Override double fee(double a) { return 0; }
    @Override void validate(double a) throws PaymentFailedException {
        super.validate(a);
        if (a > 100000) throw new PaymentFailedException("UPI limit of Rs.100000 exceeded");
    }
}
class CashOnDelivery extends PaymentMethod {
    CashOnDelivery() { super("COD"); }
    @Override double fee(double a) { return 40; }
}
class PaymentGateway {
    static final List<PaymentMethod> METHODS = new ArrayList<>(List.of(new CardPayment(), new CashOnDelivery(), new UpiPayment()));
    static PaymentMethod pick(int choice) {
        if (choice >= 1 && choice <= METHODS.size()) return METHODS.get(choice - 1);
        System.out.println("Invalid method. Defaulting to COD.");
        return METHODS.get(1);
    }
    static boolean process(PaymentMethod m, double amount) {
        try {
            boolean ok = m.pay(amount);
            System.out.println(ok ? "Payment SUCCESSFUL!" : "Payment FAILED! Please try again.");
            return ok;
        } catch (PaymentFailedException e) {
            System.out.println("Payment rejected: " + e.getMessage());
            return false;
        }
    }
    static boolean process(PaymentMethod m, double amount, int maxTries) {
        for (int i = 1; i <= maxTries; i++) {
            System.out.println("Attempt " + i + " of " + maxTries);
            if (process(m, amount)) return true;
        }
        return false;
    }
}

// ===================== MODULE 8: confirmation & notifications =====================
interface Notifier { void send(String to, String message); }
class EmailNotifier implements Notifier {
    @Override public void send(String to, String msg) { System.out.println("[Email -> " + to + "] " + msg); }
}
class SmsNotifier implements Notifier {
    @Override public void send(String to, String msg) { System.out.println("[SMS -> " + OrderConfirmation.mask(to) + "] " + msg); }
}
class OrderConfirmation {
    private final List<Notifier> notifiers = new ArrayList<>(List.of(new EmailNotifier(), new SmsNotifier()));
    static String mask(String phone) {
        if (phone == null || phone.trim().length() < 6) return "**********";
        String p = phone.trim();
        return p.substring(0, 3) + "*****" + p.substring(p.length() - 2);
    }
    void generate(Order o, String paymentStatus) {
        String line = "#".repeat(40);
        System.out.println("\n" + line + "\n       ORDER CONFIRMATION\n" + line);
        System.out.println("Dear " + o.customerName + ",\n");
        System.out.println(paymentStatus.equals("SUCCESS")
                ? "Your order has been placed successfully!"
                : "Your order is saved, but payment is still pending.");
        System.out.println("\nOrder ID       : " + o.orderId);
        System.out.println("Total Amount   : " + Utility.formatPrice(o.total));
        System.out.println("Payment Status : " + paymentStatus);
        System.out.println("\nThank you for shopping with us!");
        String msg = "Order " + o.orderId + " (" + paymentStatus + ") for " + Utility.formatPrice(o.total);
        notifiers.get(0).send("customer@email.com", msg);
        notifiers.get(1).send(o.phone, msg);
    }
}

// ===================== MODULE 9: data storage =====================
class DataStorage {
    private final List<String> orderData = new ArrayList<>();
    private static final int LIMIT = 50;
    void saveOrder(String data) {
        if (orderData.size() >= LIMIT) { System.out.println("Order storage is full!"); return; }
        orderData.add(data);
    }
    void saveToFile(String filename, String content) throws StorageException {
        try (PrintWriter out = new PrintWriter(new FileWriter(filename, true))) {   // true = append
            out.println(content);
            System.out.println("Saved to file: " + filename);
        } catch (IOException e) {
            throw new StorageException("Could not write " + filename, e);
        }
    }
    List<String> readFromFile(String filename) throws StorageException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String l;
            while ((l = br.readLine()) != null) lines.add(l);
        } catch (IOException e) {
            throw new StorageException("Could not read " + filename, e);
        }
        return lines;
    }
    void showOrders() {
        System.out.println("\n===== Stored Orders (this session) =====");
        if (orderData.isEmpty()) System.out.println("No orders found.");
        for (String s : orderData) System.out.println(s);
    }
}

// ===================== MODULE 10: UI pages =====================
class UI {
    static void banner() {
        System.out.println("=".repeat(40));
        System.out.println("   WELCOME TO THE ONLINE STORE");
        System.out.println("   Cart & Order Pipeline");
        System.out.println("=".repeat(40));
    }
    static void menu(String user) {
        System.out.println("\n================ MAIN MENU ================  (user: " + user + ")");
        System.out.println(" 1. Browse Products          9. Register");
        System.out.println(" 2. Search by Name          10. Login");
        System.out.println(" 3. Search by Price Range   11. Logout");
        System.out.println(" 4. Products by Category    12. Order History");
        System.out.println(" 5. Add Item to Cart        13. Check Stock");
        System.out.println(" 6. View Cart               14. Exit");
        System.out.println(" 7. Remove Item from Cart");
        System.out.println(" 8. Update Cart Quantity     (Checkout = 15)");
        System.out.println("=============================================");
    }
    static void orderSuccess(String orderId) {
        System.out.println("\n" + "*".repeat(10) + " ORDER SUCCESS " + "*".repeat(10));
        System.out.println("Thank you! Your order is confirmed. ID: " + orderId);
        System.out.println("*".repeat(36));
    }
}

// ===================== MODULE 11: Validation & Errors =====================
class ValidationException extends Exception {
    ValidationException(String msg) { super(msg); }
}
class ProductNotFoundException extends Exception {
    ProductNotFoundException(int id) { super("No product found with ID " + id); }
}
class OutOfStockException extends Exception {
    OutOfStockException(String name, int left) { super("Only " + left + " of " + name + " left in stock"); }
}
class ItemNotInCartException extends Exception {
    ItemNotInCartException(int id) { super("Product " + id + " is not in your cart"); }
}
class CartFullException extends RuntimeException {
    CartFullException(int max) { super("Cart is full (max " + max + " different items)"); }
}
class AuthException extends Exception {
    AuthException(String msg) { super(msg); }
}
class PaymentFailedException extends Exception {
    PaymentFailedException(String msg) { super(msg); }
}
class InvalidCouponException extends Exception {
    InvalidCouponException(String code) { super("Coupon '" + code + "' is not valid"); }
}
class StorageException extends Exception {
    StorageException(String msg, Throwable cause) { super(msg, cause); }
}
class InvalidDetailsException extends Exception {
    final List<String> problems;
    InvalidDetailsException(List<String> problems) {
        super(problems.size() + " problem(s) found in shipping details");
        this.problems = problems;
    }
}
class Validator {
    static void quantity(int qty) throws ValidationException {
        if (qty <= 0) throw new ValidationException("Quantity must be greater than 0");
    }
    static void quantity(int qty, int max) throws ValidationException {
        quantity(qty);
        if (qty > max) throw new ValidationException("You can order at most " + max + " of one item at a time");
    }
    static void required(String field, String value) throws ValidationException {
        if (value == null || value.trim().isEmpty()) throw new ValidationException(field + " is required");
    }
    static void cartNotEmpty(boolean empty) throws ValidationException {
        if (empty) throw new ValidationException("Cart is empty. Add items first");
    }
}

// ===================== MODULE 12: utilities & pricing =====================
class Utility {
    private static final Random RANDOM = new Random();
    static final double TAX_RATE = 0.05;
    static final double FREE_SHIPPING_ABOVE = 1000;
    static final Map<String, Double> COUPONS = new HashMap<>();
    static {
        COUPONS.put("SAVE10", 0.10);
        COUPONS.put("FEST20", 0.20);
        COUPONS.put("NEW5", 0.05);
    }
    static String formatPrice(double p) { return "Rs." + String.format("%.2f", p); }
    static String formatPrice(double p, String symbol) { return symbol + String.format("%.2f", p); }
    static String generateUniqueId(String prefix) { return prefix.toUpperCase() + (10000 + RANDOM.nextInt(90000)); }
    static String getCurrentDateTime() { return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()); }
    static double calculateTax(double amount) { return amount * TAX_RATE; }
    static double calculateShipping(double amount) { return amount >= FREE_SHIPPING_ABOVE ? 0 : 50; }
    static double calculateShipping(double amount, boolean express) { return calculateShipping(amount) + (express ? 100 : 0); }
    static double applyCoupon(double amount, String code) throws InvalidCouponException {
        Double rate = COUPONS.get(code == null ? "" : code.trim().toUpperCase());
        if (rate == null) throw new InvalidCouponException(code);
        return amount * rate;
    }
    // Combining: buy more different things, save a little more
    static double bundleDiscount(int differentItems, double amount) {
        if (differentItems >= 3) return amount * 0.05;
        if (differentItems == 2) return amount * 0.02;
        return 0;
    }
    static double calculateFinalTotal(double subtotal) {
        return subtotal + calculateTax(subtotal) + calculateShipping(subtotal);
    }
    static double calculateFinalTotal(double subtotal, double discount, boolean express) {
        double net = subtotal - discount;
        return net + calculateTax(net) + calculateShipping(net, express);
    }
}

// ===================== MAIN APPLICATION =====================
public class OnlineStore {
    static final Scanner sc = new Scanner(System.in);
    static final ProductCatalog catalog = new ProductCatalog();
    static final Inventory inventory = new Inventory(catalog);
    static final ShoppingCart cart = new ShoppingCart();
    static final UserSession session = new UserSession();
    static final OrderBook orderBook = new OrderBook();
    static final OrderConfirmation confirmation = new OrderConfirmation();
    static final DataStorage storage = new DataStorage();
    public static void main(String[] args) {
        catalog.loadProducts();
        session.guestLogin();
        UI.banner();
        boolean running = true;
        while (running) {
            UI.menu(session.currentName());
            int choice = readInt("Choice: ");
            try {
                switch (choice) {
                    case 1: catalog.listAll(); break;
                    case 2:
                        System.out.print("Enter keyword: ");
                        catalog.search(sc.nextLine());
                        break;
                    case 3:
                        catalog.search(readDouble("Min price: "), readDouble("Max price: "));
                        break;
                    case 4: catalog.showByCategory(); break;
                    case 5: addToCartFlow(); break;
                    case 6: cart.display(); break;
                    case 7: cart.removeItem(readInt("Enter Product ID to remove: ")); break;
                    case 8:
                        int id = readInt("Enter Product ID: ");
                        cart.updateQuantity(id, readInt("Enter new quantity: "));
                        break;
                    case 9:
                        System.out.print("Username: ");
                        String u = sc.nextLine();
                        System.out.print("Password: ");
                        session.register(u, sc.nextLine());
                        break;
                    case 10:
                        System.out.print("Username: ");
                        String lu = sc.nextLine();
                        System.out.print("Password: ");
                        session.login(lu, sc.nextLine());
                        break;
                    case 11: session.logout(); break;
                    case 12: orderBook.showHistory(); break;
                    case 13: inventory.showStock(); break;
                    case 14:
                        System.out.println("Thank you for visiting. Goodbye!");
                        running = false;
                        break;
                    case 15: checkoutFlow(); break;
                    default: System.out.println("Invalid choice!");
                }
            } catch (ProductNotFoundException | ItemNotInCartException | AuthException | CartFullException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        sc.close();
    }
    static void addToCartFlow() throws ProductNotFoundException {
        catalog.listAll();
        int id = readInt("Enter Product ID to add: ");
        Product p = catalog.getById(id);
        int qty = readInt("Enter quantity: ");
        try {
            Validator.quantity(qty, 10);
            inventory.checkStock(id, qty + cart.quantityOf(id));    // stock must cover what is already in cart too
            cart.addItem(p, qty);
        } catch (ValidationException | OutOfStockException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    static void checkoutFlow() {
        try {
            Validator.cartNotEmpty(cart.isEmpty());
        } catch (ValidationException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
        cart.display();
        // Shipping details
        Checkout checkout = new Checkout();
        checkout.collectDetails(sc, session.currentName());
        try {
            checkout.validate();
        } catch (InvalidDetailsException e) {
            System.out.println("Please fix the following and try checkout again:");
            for (String p : e.problems) System.out.println("  - " + p);
            return;
        }
        // Pricing: bundle discount + optional coupon + shipping option
        double subtotal = cart.calculateTotal();
        double discount = Utility.bundleDiscount(cart.distinctItems(), subtotal);
        System.out.print("Coupon code (press Enter to skip): ");
        String code = sc.nextLine().trim();
        if (!code.isEmpty()) {
            try {
                double d = Utility.applyCoupon(subtotal, code);
                discount += d;
                System.out.println("Coupon applied! You save " + Utility.formatPrice(d));
            } catch (InvalidCouponException e) {
                System.out.println(e.getMessage() + " - continuing without it.");
            }
        }
        boolean express = readYesNo("Express delivery for Rs.100 extra? (y/n): ");
        double net = subtotal - discount;
        double finalTotal = Utility.calculateFinalTotal(subtotal, discount, express);
        checkout.reviewOrder(finalTotal, express ? "Express delivery" : "Standard delivery");
        System.out.println("Subtotal   : " + Utility.formatPrice(subtotal));
        System.out.println("Discount   : -" + Utility.formatPrice(discount));
        System.out.println("Tax (5%)   : " + Utility.formatPrice(Utility.calculateTax(net)));
        System.out.println("Shipping   : " + Utility.formatPrice(Utility.calculateShipping(net, express)));
        // Payment
        System.out.println("\nSelect Payment Method:");
        for (int i = 0; i < PaymentGateway.METHODS.size(); i++) System.out.println((i + 1) + ". " + PaymentGateway.METHODS.get(i));
        PaymentMethod method = PaymentGateway.pick(readInt("Choice: "));
        boolean paid = PaymentGateway.process(method, finalTotal, 2);
        // Build the order
        Order order = new Order(checkout.name, checkout.address + ", " + checkout.city + " - " + checkout.pincode, checkout.phone);
        order.total = finalTotal;
        for (CartItem ci : cart.getItems()) {
            order.addItem(ci.productId, ci.name, ci.price, ci.quantity);
            if (paid) {
                try {
                    inventory.decreaseStock(ci.productId, ci.quantity);
                } catch (OutOfStockException | ProductNotFoundException e) {
                    System.out.println("Stock warning: " + e.getMessage());
                }
            }
        }
        order.updateStatus(paid ? OrderStatus.CONFIRMED : OrderStatus.PAYMENT_PENDING);
        order.display();
        orderBook.save(order);
        // Save to storage
        String record = order.orderId + "," + order.customerName + "," + String.format("%.2f", finalTotal) + "," + order.status;
        storage.saveOrder(record);
        try {
            storage.saveToFile("orders.txt", record + "," + Utility.getCurrentDateTime());
        } catch (StorageException e) {
            System.out.println("Warning: " + e.getMessage());
        }
        // Confirmation
        confirmation.generate(order, paid ? "SUCCESS" : "FAILED");
        if (paid) {
            UI.orderSuccess(order.orderId);
            cart.clear();
        } else {
            System.out.println("Your cart has been kept so you can retry payment.");
        }
        System.out.println("Order timestamp: " + Utility.getCurrentDateTime());
    }
    // ---------- input helpers (Module 11) ----------
    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Invalid input. Please enter a whole number."); }
        }
    }
    static double readDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Double.parseDouble(sc.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Invalid input. Please enter a number."); }
        }
    }
    static boolean readYesNo(String prompt) {
        while (true) {
            System.out.print(prompt);
            String in = sc.nextLine().trim().toLowerCase();
            if (in.equals("y") || in.equals("yes")) return true;
            if (in.equals("n") || in.equals("no")) return false;
            System.out.println("Please enter 'y' or 'n'.");
        }
    }
}
