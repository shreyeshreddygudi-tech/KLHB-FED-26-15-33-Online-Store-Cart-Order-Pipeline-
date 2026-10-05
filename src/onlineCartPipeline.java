import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;


class Product {
    int id;
    String name;
    double price;
    String description;
    String image;
    int stock;

    Product(int id, String name, double price, String description, String image, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.image = image;
        this.stock = stock;
    }

    void display() {
        System.out.println("ID: " + id + " | " + name + " | " + Utility.formatPrice(price) + " | Stock: " + stock);
        System.out.println("Desc : " + description);
        System.out.println("Image: " + image);
        System.out.println("---------------------------------");
    }
}

class ProductCatalog {
    static Product[] products = new Product[5];
    static int count = 0;

    static void loadProducts() {
        products[0] = new Product(101, "Laptop", 45000, "15-inch gaming laptop", "laptop.jpg", 10);
        products[1] = new Product(102, "Mouse", 500, "Wireless mouse", "mouse.jpg", 50);
        products[2] = new Product(103, "Keyboard", 1200, "Mechanical keyboard", "keyboard.jpg", 30);
        products[3] = new Product(104, "Headphones", 2500, "Noise cancelling", "headphones.jpg", 20);
        products[4] = new Product(105, "USB Drive", 400, "64GB pen drive", "usb.jpg", 100);
        count = 5;
    }

    static void listAllProducts() {
        System.out.println("\n===== PRODUCT CATALOG =====");
        for (int i = 0; i < count; i++) products[i].display();
    }

    static Product getProductById(int id) {
        for (int i = 0; i < count; i++) if (products[i].id == id) return products[i];
        return null;
    }

    static void searchProduct(String keyword) {
        System.out.println("\nSearch results for: " + keyword);
        boolean found = false;
        for (int i = 0; i < count; i++) {
            if (products[i].name.toLowerCase().contains(keyword.toLowerCase())) {
                products[i].display();
                found = true;
            }
        }
        if (!found) System.out.println("No product found.");
    }
}

class Inventory {
    static boolean checkStock(int productId, int qty) {
        Product p = ProductCatalog.getProductById(productId);
        if (p == null) {
            System.out.println("Product not found in inventory.");
            return false;
        }
        if (p.stock >= qty) return true;
        System.out.println("Only " + p.stock + " left in stock!");
        return false;
    }

    static void decreaseStock(int productId, int qty) {
        Product p = ProductCatalog.getProductById(productId);
        if (p != null && p.stock >= qty) {
            p.stock -= qty;
            System.out.println("Stock updated. Remaining: " + p.stock);
        }
    }

    static void showStock() {
        System.out.println("\n===== CURRENT STOCK =====");
        for (int i = 0; i < ProductCatalog.count; i++) {
            Product p = ProductCatalog.products[i];
            System.out.println("Product ID: " + p.id + " | Stock: " + p.stock);
        }
    }
}

class CartItem {
    int productId;
    String name;
    double price;
    int quantity;

    CartItem(int productId, String name, double price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    double getSubtotal() { return price * quantity; }
}

class ShoppingCart {
    static CartItem[] cart = new CartItem[20];
    static int itemCount = 0;

    static void addItem(int productId, String name, double price, int qty) {
        for (int i = 0; i < itemCount; i++) {
            if (cart[i].productId == productId) {
                cart[i].quantity += qty;
                System.out.println("Quantity updated for " + name);
                return;
            }
        }
        if (itemCount < cart.length) {
            cart[itemCount] = new CartItem(productId, name, price, qty);
            itemCount++;
            System.out.println(name + " added to cart.");
        } else {
            System.out.println("Cart is full!");
        }
    }

    static void removeItem(int productId) {
        for (int i = 0; i < itemCount; i++) {
            if (cart[i].productId == productId) {
                for (int j = i; j < itemCount - 1; j++) cart[j] = cart[j + 1];
                itemCount--;
                System.out.println("Item removed from cart.");
                return;
            }
        }
        System.out.println("Item not found in cart.");
    }

    static void updateQuantity(int productId, int newQty) {
        for (int i = 0; i < itemCount; i++) {
            if (cart[i].productId == productId) {
                if (newQty <= 0) removeItem(productId);
                else {
                    cart[i].quantity = newQty;
                    System.out.println("Quantity updated.");
                }
                return;
            }
        }
        System.out.println("Item not found.");
    }

    static double calculateTotal() {
        double total = 0;
        for (int i = 0; i < itemCount; i++) total += cart[i].getSubtotal();
        return total;
    }

    static void clearCart() {
        itemCount = 0;
        System.out.println("Cart cleared.");
    }

    static void displayCart() {
        if (itemCount == 0) {
            System.out.println("Cart is empty.");
            return;
        }
        System.out.println("\n===== YOUR CART =====");
        for (int i = 0; i < itemCount; i++) {
            System.out.println((i + 1) + ". " + cart[i].name +
                    " | Qty: " + cart[i].quantity +
                    " | Price: " + Utility.formatPrice(cart[i].price) +
                    " | Subtotal: " + Utility.formatPrice(cart[i].getSubtotal()));
        }
        System.out.println("TOTAL: " + Utility.formatPrice(calculateTotal()));
        System.out.println("=====================");
    }
}

class User {
    String username;
    String password;

    User(String username, String password) {
        this.username = username;
        this.password = password;
    }
}

class UserSession {
    static User[] users = new User[10];
    static int userCount = 0;
    static String currentUser = null;

    static void register(String uname, String pass) {
        if (userCount < users.length) {
            users[userCount] = new User(uname, pass);
            userCount++;
            System.out.println("Registration successful!");
        } else {
            System.out.println("User limit reached.");
        }
    }

    static boolean login(String uname, String pass) {
        for (int i = 0; i < userCount; i++) {
            if (users[i].username.equals(uname) && users[i].password.equals(pass)) {
                currentUser = uname;
                System.out.println("Login successful. Welcome " + uname);
                return true;
            }
        }
        System.out.println("Invalid username or password.");
        return false;
    }

    static void guestLogin() {
        currentUser = "Guest";
    }

    static void logout() {
        currentUser = null;
        System.out.println("Logged out.");
    }

    static void showSession() {
        System.out.println(currentUser == null ? "No active session." : "Current user: " + currentUser);
    }
}

class Validator {
    static boolean validateQuantity(int qty) {
        if (qty <= 0) {
            System.out.println("Error: Quantity must be greater than 0.");
            return false;
        }
        return true;
    }

    static boolean validateRequired(String field, String value) {
        if (value == null || value.trim().isEmpty()) {
            System.out.println("Error: " + field + " is required.");
            return false;
        }
        return true;
    }

    static boolean validateCartNotEmpty(int itemCount) {
        if (itemCount <= 0) {
            System.out.println("Error: Cart is empty. Add items first.");
            return false;
        }
        return true;
    }

    static void handlePaymentResult(boolean success) {
        System.out.println(success ? "Payment successful." : "Error: Payment failed. Please try another method.");
    }
}

class Checkout {
    String name;
    String address;
    String phone;
    String city;
    String pincode;

    void collectDetails(Scanner sc) {
        System.out.println("\n===== CHECKOUT - Shipping Details =====");
        System.out.print("Full Name: ");
        name = sc.nextLine();

        System.out.print("Address: ");
        address = sc.nextLine();

        System.out.print("Mobile Number: ");
        phone = sc.nextLine();

        System.out.print("City: ");
        city = sc.nextLine();

        System.out.print("Pincode: ");
        pincode = sc.nextLine();
    }

    boolean validate() {
        if (!Validator.validateRequired("Name", name)) return false;
        if (!Validator.validateRequired("Address", address)) return false;
        if (phone == null || phone.trim().length() < 10) {
            System.out.println("Error: Valid mobile number (at least 10 digits) required!");
            return false;
        }
        if (!Validator.validateRequired("City", city)) return false;
        if (pincode == null || pincode.trim().length() != 6) {
            System.out.println("Error: Valid 6-digit pincode required!");
            return false;
        }
        return true;
    }

    void reviewOrder(double total) {
        System.out.println("\n===== ORDER SUMMARY =====");
        System.out.println("Customer : " + name);
        System.out.println("Address  : " + address + ", " + city + " - " + pincode);
        System.out.println("Phone    : " + phone);
        System.out.println("Total    : " + Utility.formatPrice(total));
        System.out.println("=========================");
    }
}

class OrderItem {
    int productId;
    String name;
    double price;
    int quantity;

    OrderItem(int productId, String name, double price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
}

class Order {
    String orderId;
    OrderItem[] items;
    int itemCount;
    double total;
    String status;
    Date timestamp;
    String customerName;
    String address;
    String phone;

    Order(String customerName, String address, String phone) {
        this.orderId = Utility.generateUniqueId("ORD");
        this.items = new OrderItem[20];
        this.itemCount = 0;
        this.total = 0;
        this.status = "Pending";
        this.timestamp = new Date();
        this.customerName = customerName;
        this.address = address;
        this.phone = phone;
    }

    void addItem(int pid, String name, double price, int qty) {
        items[itemCount] = new OrderItem(pid, name, price, qty);
        total += price * qty;
        itemCount++;
    }

    void displayOrder() {
        System.out.println("\n===== ORDER DETAILS =====");
        System.out.println("Order ID   : " + orderId);
        System.out.println("Customer   : " + customerName);
        System.out.println("Address    : " + address);
        System.out.println("Phone      : " + phone);
        System.out.println("Status     : " + status);
        System.out.println("Date       : " + timestamp);
        System.out.println("Items:");
        for (int i = 0; i < itemCount; i++) {
            System.out.println("  - " + items[i].name + " x" + items[i].quantity +
                    " = " + Utility.formatPrice(items[i].price * items[i].quantity));
        }
        System.out.println("TOTAL      : " + Utility.formatPrice(total));
        System.out.println("=========================");
    }
}

class OrderModule {
    static Order[] orders = new Order[50];
    static int orderCount = 0;

    static void saveOrder(Order o) {
        if (orderCount < orders.length) {
            orders[orderCount] = o;
            orderCount++;
            System.out.println("Order saved successfully! ID: " + o.orderId);
        }
    }
}

class Payment {
    static String[] methods = {"Card", "COD", "UPI"};

    static boolean processPayment(String method, double amount) {
        System.out.println("\nProcessing payment of " + Utility.formatPrice(amount) + " via " + method + "...");
        try {
            Thread.sleep(500);
        } catch (Exception e) {}

        Random rand = new Random();
        int chance = rand.nextInt(10);   // 0-9, 80% success chance
        if (chance < 8) {
            System.out.println("Payment SUCCESSFUL!");
            return true;
        } else {
            System.out.println("Payment FAILED! Please try again.");
            return false;
        }
    }
}

class OrderConfirmation {
    static void generateConfirmation(String orderId, String customerName, String phone, double total, String paymentStatus) {
        System.out.println("\n########################################");
        System.out.println("       ORDER CONFIRMATION");
        System.out.println("########################################");
        System.out.println("Dear " + customerName + ",");
        System.out.println();
        System.out.println("Your order has been placed successfully!");
        System.out.println();
        System.out.println("Order ID       : " + orderId);
        System.out.println("Total Amount   : " + Utility.formatPrice(total));
        System.out.println("Payment Status : " + paymentStatus);
        System.out.println();
        System.out.println("Thank you for shopping with us!");
        System.out.println("You will receive updates on: " + maskPhone(phone));
        System.out.println("[Email Simulation] Confirmation mail sent to customer@email.com");
    }

    static String maskPhone(String phone) {
        if (phone == null || phone.trim().length() < 4) return "**********";
        String p = phone.trim();
        return p.substring(0, 3) + "*****" + p.substring(p.length() - 2);
    }
}

class DataStorage {
    static String[] productData = new String[50];
    static int productCount = 0;
    static String[] orderData = new String[50];
    static int orderCount = 0;

    static void saveProduct(String data) {
        if (productCount < productData.length) {
            productData[productCount] = data;
            productCount++;
        } else {
            System.out.println("Product storage is full!");
        }
    }

    static void loadProducts() {
        System.out.println("\n===== Stored Product Snapshots =====");
        if (productCount == 0) System.out.println("No products found.");
        else for (int i = 0; i < productCount; i++) System.out.println(productData[i]);
    }

    static void saveOrder(String data) {
        if (orderCount < orderData.length) {
            orderData[orderCount] = data;
            orderCount++;
            System.out.println("Order saved in memory.");
        } else {
            System.out.println("Order storage is full!");
        }
    }

    static void loadOrders() {
        System.out.println("\n===== Stored Orders =====");
        if (orderCount == 0) System.out.println("No orders found.");
        else for (int i = 0; i < orderCount; i++) System.out.println(orderData[i]);
    }

    static void saveToFile(String filename, String content) {
        System.out.println("[File Simulation] Writing to " + filename + ":");
        System.out.println(content);
        System.out.println("Data written successfully (simulated).");
    }
}

class Utility {
    static String formatPrice(double price) {
        return "Rs." + String.format("%.2f", price);
    }

    static String generateUniqueId(String prefix) {
        Random rand = new Random();
        int num = 10000 + rand.nextInt(90000);
        return prefix + num;
    }

    static String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sdf.format(new Date());
    }

    static double calculateTax(double amount) {
        return amount * 0.05;
    }

    static double calculateShipping(double amount) {
        return (amount >= 1000) ? 0 : 50;
    }

    static double calculateFinalTotal(double subtotal) {
        return subtotal + calculateTax(subtotal) + calculateShipping(subtotal);
    }
}

public class OnlineStore {
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        ProductCatalog.loadProducts();
        UserSession.guestLogin();

        System.out.println("========================================");
        System.out.println("   WELCOME TO THE ONLINE STORE");
        System.out.println("   Cart & Order Pipeline");
        System.out.println("========================================");

        boolean running = true;
        while (running) {
            printMenu();
            int ch = readInt("Choice: ");

            switch (ch) {
                case 1:
                    ProductCatalog.listAllProducts();
                    break;
                case 2:
                    System.out.print("Enter keyword: ");
                    ProductCatalog.searchProduct(sc.nextLine());
                    break;
                case 3:
                    addToCartFlow();
                    break;
                case 4:
                    ShoppingCart.displayCart();
                    break;
                case 5:
                    removeFromCartFlow();
                    break;
                case 6:
                    updateQuantityFlow();
                    break;
                case 7:
                    Inventory.showStock();
                    break;
                case 8:
                    checkoutFlow();
                    break;
                case 9:
                    System.out.println("Thank you for visiting. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
        sc.close();
    }

    static void printMenu() {
        System.out.println("\n================ MAIN MENU ================");
        System.out.println("1. Browse Products");
        System.out.println("2. Search Products");
        System.out.println("3. Add Item to Cart");
        System.out.println("4. View Cart");
        System.out.println("5. Remove Item from Cart");
        System.out.println("6. Update Cart Quantity");
        System.out.println("7. Check Stock");
        System.out.println("8. Checkout");
        System.out.println("9. Exit");
        System.out.println("=============================================");
    }

    static void addToCartFlow() {
        ProductCatalog.listAllProducts();
        int id = readInt("Enter Product ID to add: ");
        Product p = ProductCatalog.getProductById(id);
        if (p == null) {
            System.out.println("Product not found!");
            return;
        }

        int qty = readInt("Enter quantity: ");
        if (!Validator.validateQuantity(qty)) return;
        if (!Inventory.checkStock(id, qty)) return;

        ShoppingCart.addItem(p.id, p.name, p.price, qty);
    }

    static void removeFromCartFlow() {
        int id = readInt("Enter Product ID to remove: ");
        ShoppingCart.removeItem(id);
    }

    static void updateQuantityFlow() {
        int id = readInt("Enter Product ID: ");
        int qty = readInt("Enter new quantity: ");
        ShoppingCart.updateQuantity(id, qty);
    }

    static void checkoutFlow() {
        if (!Validator.validateCartNotEmpty(ShoppingCart.itemCount)) return;

        ShoppingCart.displayCart();

        // Collect customer name & mobile number via Scanner (no fixed/hardcoded values)
        Checkout checkout = new Checkout();
        checkout.collectDetails(sc);

        if (!checkout.validate()) {
            System.out.println("Please correct the errors and try checkout again.");
            return;
        }

        double subtotal = ShoppingCart.calculateTotal();
        double tax = Utility.calculateTax(subtotal);
        double shipping = Utility.calculateShipping(subtotal);
        double finalTotal = Utility.calculateFinalTotal(subtotal);

        checkout.reviewOrder(finalTotal);
        System.out.println("Tax (5%)   : " + Utility.formatPrice(tax));
        System.out.println("Shipping   : " + Utility.formatPrice(shipping));

        System.out.println("\nSelect Payment Method:");
        for (int i = 0; i < Payment.methods.length; i++) {
            System.out.println((i + 1) + ". " + Payment.methods[i]);
        }
        int pch = readInt("Choice: ");
        String method;
        if (pch >= 1 && pch <= Payment.methods.length) {
            method = Payment.methods[pch - 1];
        } else {
            System.out.println("Invalid method. Defaulting to COD.");
            method = "COD";
        }

        boolean paid = Payment.processPayment(method, finalTotal);
        Validator.handlePaymentResult(paid);

        Order order = new Order(checkout.name,
                checkout.address + ", " + checkout.city + " - " + checkout.pincode,
                checkout.phone);
        for (int i = 0; i < ShoppingCart.itemCount; i++) {
            CartItem ci = ShoppingCart.cart[i];
            order.addItem(ci.productId, ci.name, ci.price, ci.quantity);
            if (paid) Inventory.decreaseStock(ci.productId, ci.quantity);
        }
        order.status = paid ? "Confirmed" : "Payment Pending";

        order.displayOrder();
        OrderModule.saveOrder(order);
        DataStorage.saveOrder(order.orderId + "," + order.customerName + "," + finalTotal + "," + order.status);
        DataStorage.saveToFile("order_" + order.orderId + ".txt",
                order.orderId + " | " + order.customerName + " | " + order.phone + " | " + Utility.formatPrice(finalTotal));

        if (paid) {
            OrderConfirmation.generateConfirmation(order.orderId, order.customerName, order.phone, finalTotal, "SUCCESS");
            ShoppingCart.clearCart();
        } else {
            OrderConfirmation.generateConfirmation(order.orderId, order.customerName, order.phone, finalTotal, "FAILED");
            System.out.println("Your cart has been kept so you can retry payment.");
        }

        System.out.println("Order timestamp: " + Utility.getCurrentDateTime());
    }

    static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a whole number.");
            }
        }
    }
}
