package entity.dao;
import entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {

    void createProduct(Product product);
    void createProduct(List<Product> productList);

    List<Product> findAllProducts();

    Product getProductById(int productId);

    Optional<Product> findProductById();

    void updateProductById(Product Product);

    void deleteProductById();
}
