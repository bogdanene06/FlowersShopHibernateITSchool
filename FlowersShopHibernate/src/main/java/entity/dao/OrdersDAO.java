package entity.dao;

import entity.Orders;
import entity.Product;

import java.util.List;
import java.util.Optional;

public interface OrdersDAO {
    void createOrders(List<Product> productList, String clientName);
    List<Orders> findAllOrdersWithProducts();
    Optional<Orders> findOrdersById();
    List<Orders> findAllOrdersByClientId(int clientId);
    void updateOrdersById();
    List<Product> generateRandomProducts(int numberOfProducts);
    void deleteOrdersById();
}
