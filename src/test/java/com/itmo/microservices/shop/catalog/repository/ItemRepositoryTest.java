package com.itmo.microservices.shop.catalog.repository;

import com.itmo.microservices.shop.catalog.HardcodedValues;
import com.itmo.microservices.shop.catalog.impl.entity.Item;
import com.itmo.microservices.shop.catalog.impl.repository.ItemRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.sql.DataSource;

import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ItemRepositoryTest extends HardcodedValues {
    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ItemRepository<Item> itemRepository;

    @Test
    public void injectedComponentsAreNotNull(){
        Assert.assertNotNull(dataSource);
        Assert.assertNotNull(jdbcTemplate);
        Assert.assertNotNull(entityManager);
        Assert.assertNotNull(itemRepository);
    }

    @Test
    public void returnAvailableItems(){
        itemRepository.saveAllAndFlush(mockedItems);
        var test = itemRepository.returnAvailableItems();
        var correct = mockedItems.stream().filter(item -> item.getAmount() > 0).collect(Collectors.toList());
        test.forEach(t -> t.setId(null));
        correct.forEach(t -> t.setId(null));
        Assert.assertEquals(correct, test);
    }

    @Test
    public void getCount(){
        itemRepository.save(mockedItem);
        var item = itemRepository.findAll().stream().findFirst();
        var test = itemRepository.getCount(item.get().getId());
        var correct = mockedItem.getAmount();
        Assert.assertEquals(correct, test);
    }

    @Test
    public void whenSave_thenGenerateNewUUID(){
        itemRepository.save(mockedItem);
        var item = itemRepository.findAll().stream().findFirst();
        var test = item.get().getId();
        var correct = mockedItem.getId();
        Assert.assertNotEquals(correct, test);
    }
}
