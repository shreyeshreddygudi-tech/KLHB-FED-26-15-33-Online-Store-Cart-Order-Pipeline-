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
        System.out.println("==========================");
    }

    void reviewOrder(double total, String note) {
        reviewOrder(total);
        System.out.println("Note     : " + note);
    }
}
