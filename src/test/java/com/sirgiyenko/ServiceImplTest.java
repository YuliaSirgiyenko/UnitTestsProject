package com.sirgiyenko;

import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ServiceImplTest {

    @Mock
    private TimeService timeService;
    @Mock
    private IdGenerator idGenerator;
    @Mock
    private Dao mockedDao;

    private ServiceImpl service;
    List<Entity> mockedEntityList;

    @BeforeEach
    public void initEntityList() {
        this.mockedEntityList = new ArrayList<>();
        mockedEntityList.add(new Entity(1L, "phone Lenovo", Instant.now(), new BigDecimal(20.00)));
        mockedEntityList.add(new Entity(2L, "phone SamsungJ7", Instant.now(), new BigDecimal(20.00)));
        mockedEntityList.add(new Entity(3L, "phone SamsungA7", Instant.now(), new BigDecimal(20.00)));
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.service = new ServiceImpl(timeService, idGenerator, mockedDao);
    }

    @DisplayName("Happy path - Entity created and stored successfully")
    @Test
    public void testCase1() {
        //Given
        final long expectedId = idGenerator.nextId();
        final String expectedTitle = "Item name";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = new BigDecimal(25.25);
        final Entity expectedEntity = new Entity(expectedId, expectedTitle, expectedDateIn, expectedPrice);

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);

        //When
        service.addNewItem(expectedTitle, expectedPrice);

        //Then
        Mockito.verify(mockedDao, times(1)).store(expectedEntity);
    }

    @DisplayName("Entity created but failed during storage")
    @Test
    public void testCase2() {
        //Given
        final long expectedId = idGenerator.nextId();
        final String expectedTitle = "Item name";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = new BigDecimal(25.25);
        final Entity expectedEntity = new Entity(expectedId, expectedTitle, expectedDateIn, expectedPrice);

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);

        NetworkException exception = new NetworkException();
        when(mockedDao.store(expectedEntity)).thenThrow(exception);

        //When
        service.addNewItem(expectedTitle, expectedPrice);
    }

    @DisplayName("Fail on empty title")
    @Test
    public void testCase3() {
        //Given
        final long expectedId = 10;
        final String expectedTitle = "  ";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = new BigDecimal(25.25);
        final Entity expectedEntity = new Entity(expectedId, expectedTitle, expectedDateIn, expectedPrice);

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);

        //When
        assertThrows(IllegalArgumentException.class, () -> service.addNewItem(expectedTitle, expectedPrice));
    }

    @DisplayName("Fail on title length (short)")
    @Test
    public void testCase4() {
        //Given
        final long expectedId = 10;
        final String expectedTitle = "ti";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = new BigDecimal(25.25);
        final Entity expectedEntity = new Entity(expectedId, expectedTitle, expectedDateIn, expectedPrice);

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);

        //When
        assertThrows(IllegalArgumentException.class, () -> service.addNewItem(expectedTitle, expectedPrice));
    }

    @DisplayName("Fail on title length (long)")
    @Test
    public void testCase5() {
        //Given
        final long expectedId = 10;
        final String expectedTitle = "title is longer than 20 letters";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = new BigDecimal(25.25);
        final Entity expectedEntity = new Entity(expectedId, expectedTitle, expectedDateIn, expectedPrice);

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);

        //When
        assertThrows(IllegalArgumentException.class, () -> service.addNewItem(expectedTitle, expectedPrice));
    }

    @DisplayName("Fail on title uniqueness.")
    @Test
    public void testCase6() {
        //Given
        final long expectedId = 10;
        final String expectedTitle = "phone SamsungA7";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = new BigDecimal(25.25);
        final Entity expectedEntity = new Entity(expectedId, expectedTitle, expectedDateIn, expectedPrice);

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);
        when(mockedDao.findAll()).thenReturn(mockedEntityList);

        //When
        assertThrows(IllegalArgumentException.class, () -> service.addNewItem(expectedTitle, expectedPrice));
    }



}