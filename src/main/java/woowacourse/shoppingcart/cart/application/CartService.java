package woowacourse.shoppingcart.cart.application;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowacourse.shoppingcart.cart.dao.CartItemDao;
import woowacourse.shoppingcart.cart.domain.Cart;
import woowacourse.shoppingcart.cart.dto.QuantityChangingRequest;
import woowacourse.shoppingcart.cart.exception.badrequest.DuplicateCartItemException;
import woowacourse.shoppingcart.customer.domain.Customer;
import woowacourse.shoppingcart.product.application.ProductService;

@Service
@Transactional
public class CartService {

    private final CartItemDao cartItemDao;
    private final ProductService productService;

    public CartService(final CartItemDao cartItemDao, final ProductService productService) {
        this.cartItemDao = cartItemDao;
        this.productService = productService;
    }

    @Transactional(readOnly = true)
    public List<Cart> findCartsBy(final Customer customer) {
        return cartItemDao.findAllByCustomerId(customer.getId());
    }

    public Long addCart(final Long productId, final Customer customer) {
        productService.findProductById(productId);
        final boolean existProduct = cartItemDao.existProduct(customer.getId(), productId);
        if (existProduct) {
            throw new DuplicateCartItemException();
        }
        return cartItemDao.addCartItem(customer.getId(), productId);
    }

    public Cart changeQuantity(final Customer customer, final Long productId, final QuantityChangingRequest request) {
        final Cart cart = cartItemDao.findByProductAndCustomerId(productId, customer.getId());
        final Cart updatedCart = cart.changeQuantity(request.getQuantity());
        cartItemDao.updateQuantity(updatedCart);
        return updatedCart;
    }

    public void deleteCartBy(final Customer customer, final Long productId) {
        final Cart cart = cartItemDao.findByProductAndCustomerId(productId, customer.getId());
        cartItemDao.deleteCartItem(cart.getId());
    }
}
