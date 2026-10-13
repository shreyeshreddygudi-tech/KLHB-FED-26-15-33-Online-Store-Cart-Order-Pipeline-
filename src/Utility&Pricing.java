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

    // Combining: buy more different things, save a little  more
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
