package com.itmo.microservices.shop.catalog.repository;

import com.itmo.microservices.shop.catalog.CatalogTest;
import com.itmo.microservices.shop.catalog.impl.entity.Item;
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ItemRepositoryTest extends CatalogTest {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ItemRepository<Item> itemRepository;

    @Test
    void injectedComponentsAreNotNull(){
        assertThat(dataSource).isNotNull();
        assertThat(jdbcTemplate).isNotNull();
        assertThat(entityManager).isNotNull();
        assertThat(itemRepository).isNotNull();
    }

    @Test
    public void returnAvailableItems(){
        itemRepository.saveAllAndFlush(mockedItems);
        var test = itemRepository.returnAvailableItems();
        var correct = mockedItems.stream().filter(item -> item.getCount() > 0).collect(Collectors.toList());
        test.forEach(t -> t.setUuid(null));
        correct.forEach(t -> t.setUuid(null));
        Assertions.assertEquals(correct, test);
    }

    @Test
    public void getCount(){
        itemRepository.save(mockedItem);
        var item = itemRepository.findAll().stream().findFirst();
        var test = itemRepository.getCount(item.get().getUuid());
        var correct = mockedItem.getCount();
        Assertions.assertEquals(correct, test);
    }
}
