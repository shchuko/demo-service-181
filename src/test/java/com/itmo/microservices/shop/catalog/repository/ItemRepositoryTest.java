package com.itmo.microservices.shop.catalog.repository;

import com.itmo.microservices.shop.catalog.common.HardcodedValues;
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository;
import com.itmo.microservices.shop.common.test.DataJpaTestCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.stream.Collectors;

@ActiveProfiles("dev")
public class ItemRepositoryTest extends DataJpaTestCase {

    private final HardcodedValues hardcodedValues = new HardcodedValues();

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void injectedComponentsAreNotNull() {
        Assertions.assertNotNull(dataSource);
        Assertions.assertNotNull(jdbcTemplate);
        Assertions.assertNotNull(entityManager);
        Assertions.assertNotNull(itemRepository);
    }

    @Disabled
    @Test
    public void returnAvailableItems() {
        itemRepository.saveAllAndFlush(hardcodedValues.mockedItems);

        var test = itemRepository.findAllByAmountGreaterThan(0);
        var correct = hardcodedValues.mockedItems.stream().filter(item -> item.getAmount() > 0).collect(Collectors.toList());

        test.forEach(t -> t.setId(null));
        correct.forEach(t -> t.setId(null));

        Assertions.assertEquals(correct, test);
    }

    @Test
    public void whenSave_thenGenerateNewUUID() {
        itemRepository.save(hardcodedValues.mockedItem);

        var item = itemRepository.findAll().stream().findFirst();
        var test = item.get().getId();

        var correct = hardcodedValues.mockedItem.getId();

        Assertions.assertNotEquals(correct, test);
    }
}
