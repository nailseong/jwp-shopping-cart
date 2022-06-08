package woowacourse.shoppingcart.integration.product.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.transaction.annotation.Transactional;
import woowacourse.shoppingcart.product.application.ProductService;
import woowacourse.shoppingcart.product.domain.Product;
import woowacourse.shoppingcart.product.exception.notfound.NotFoundProductException;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@Sql(scripts = {"classpath:schema.sql", "classpath:data.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService productService;

    @Test
    @DisplayName("모든 상품 목록을 조회한다.")
    void findProducts() {
        // when
        final List<Product> actual = productService.findProducts();

        final List<Long> actualIds = actual.stream()
                .map(Product::getId)
                .collect(Collectors.toList());

        // then
        assertThat(actualIds).containsExactly(1L, 2L, 3L, 4L, 5L);
    }

    @Test
    @DisplayName("id에 해당하는 상품을 조회한다.")
    void findProductById() {
        // given
        final Long id = 5L;
        final String name = "오렌지";
        final int price = 540;
        final String imageUrl = "orange.org";

        // when
        final Product actual = productService.findProductById(id);

        // then
        assertThat(actual.getId()).isEqualTo(id);
        assertThat(actual.getName()).isEqualTo(name);
        assertThat(actual.getPrice()).isEqualTo(price);
        assertThat(actual.getImageUrl()).isEqualTo(imageUrl);
    }

    @Test
    @DisplayName("조회하려는 id에 해당하는 상품이 존재하지 않으면 예외를 던진다.")
    void findProductById_notExistProduct_exceptionThrown() {
        // given
        final Long id = 999L;

        // when, then
        assertThatThrownBy(() -> productService.findProductById(id))
                .isInstanceOf(NotFoundProductException.class);
    }
}