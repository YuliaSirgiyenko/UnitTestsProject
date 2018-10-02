package com.sirgiyenko;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
        validateTitleIsEmpty(title, "Title is mandatory!");
        validateTitleLength(title, "Title's length isn't between 3-20 letters!");
        validateTitleUniqueness(title, "Title isn't unique!");
        validatePriceIsEmpty(price, "Price is mandatory!");
        validatePriceLowerLewel(price, "Price is lower than 15.00!");

    try {
        dao.store(new Entity(idGenerator.nextId(), title, timeService.now(), scalePriceTwoDecimalPoints(price)));
    } catch (NetworkException e) {
        e.getMessage();
    }
    }

    private static void validateTitleIsEmpty(String value, String errorMessage) {
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

    private static void validatePriceIsEmpty(BigDecimal value, String errorMessage) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private static void validatePriceLowerLewel(BigDecimal value, String errorMessage) {
        if (value.compareTo(new BigDecimal(15.00)) == -1) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private static BigDecimal scalePriceTwoDecimalPoints(BigDecimal value){
        if (value.scale() > 2) {
            return value.setScale(2, RoundingMode.HALF_UP);
        } else {
            return value;
        }
    }

    @Override
    public Map<LocalDate, BigDecimal> getStatistic() {
        return null;
    }
}
