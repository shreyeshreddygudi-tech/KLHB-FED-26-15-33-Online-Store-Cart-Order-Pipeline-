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
