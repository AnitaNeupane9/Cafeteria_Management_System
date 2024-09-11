import java.util.Date;

public class Order {
    private int orderId;
    private String username;
    private String item;
    private double price;
    private Date date;
    private int quantity;
    private double total;

    public Order(int orderId, String username, String item, double price, Date date, int quantity) {
        this.orderId = orderId;
        this.username = username;
        this.item = item;
        this.price = price;
        this.date = date;
        this.quantity = quantity;
        this.total = price * quantity;
    }

    public Order(int orderId, String username, String item, double price, Date date, int quantity, double total) {
        this.orderId = orderId;
        this.username = username;
        this.item = item;
        this.price = price;
        this.date = date;
        this.quantity = quantity;
        this.total = total;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getUsername() {
        return username;
    }

    public String getItem() {
        return item;
    }

    public double getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getTotal() {
        return total;
    }
}
