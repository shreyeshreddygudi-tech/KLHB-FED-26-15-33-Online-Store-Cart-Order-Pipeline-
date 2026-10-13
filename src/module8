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
