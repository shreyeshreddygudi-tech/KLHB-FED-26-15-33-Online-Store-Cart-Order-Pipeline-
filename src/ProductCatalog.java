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
        System.out.println("-------------------------------");
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
