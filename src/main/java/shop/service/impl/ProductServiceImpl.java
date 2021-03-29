package shop.service.impl;

import shop.dao.ProductDao;
import shop.lib.Inject;
import shop.lib.Service;
import shop.model.Product;
import shop.service.ProductService;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Inject
    private ProductDao productDao;

    @Override
    public Product create(Product product) {
        return productDao.create(product);
    }

    @Override
    public Product get(Long id) {
        return productDao.get(id).get();
    }

    @Override
    public List<Product> getAll() {
        return productDao.getAll();
    }

    @Override
    public Product update(Product product) {
        return productDao.update(product);
    }

    @Override
    public boolean deleteById(Long id) {
        return productDao.delete(id);
    }

    @Override
    public boolean delete(Product item) {
        return productDao.delete(item.getId());
    }
}
