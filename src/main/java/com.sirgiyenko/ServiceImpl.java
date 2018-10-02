package com.sirgiyenko;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ServiceImpl implements Service {

    private final TimeService timeService;
    private final IdGenerator idGenerator;
    private Dao dao;

    public ServiceImpl(TimeService timeService, IdGenerator generator, Dao dao) {
        this.timeService = timeService;
        this.idGenerator = generator;
        this.dao = dao;
    }

    @Override
    public void addNewItem(String title, BigDecimal price) {
        validateIsEmpty(title, "Title is mandatory!");
        validateTitleLength(title, "Title's length isn't between 3-20 letters!");
        validateTitleUniqueness(title, "Title isn't unique!");


    try {
        dao.store(new Entity(idGenerator.nextId(), title, timeService.now(), price));
    } catch (NetworkException e) {
        e.getMessage();
    }
    }

    private static void validateIsEmpty(String value, String errorMessage) {
        if (Objects.isNull(value) || value.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private static void validateTitleLength(String value, String errorMessage) {
        if (value.length() < 3 || value.length() > 20) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validateTitleUniqueness(String value, String errorMessage) {
        List<Entity> entityList = dao.findAll();
        for (Entity entity : entityList) {
            if (entity.getTitle().equals(value)) {
                throw new IllegalArgumentException(errorMessage);
            }
        }
    }

    @Override
    public Map<LocalDate, BigDecimal> getStatistic() {
        return null;
    }
}
