package com.sirgiyenko;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    Map<LocalDate, BigDecimal> expectedStatMap;

    @BeforeEach
    public void initEntityList() {
        this.mockedEntityList = new ArrayList<>();
        mockedEntityList.add(new Entity(1L, "phone Lenovo",
                Instant.now(), new BigDecimal(20.00)));
        mockedEntityList.add(new Entity(2L, "phone SamsungJ7",
                Instant.now().plusSeconds(175000), new BigDecimal(100.00)));
        mockedEntityList.add(new Entity(3L, "phone SamsungA7",
                Instant.now().plusSeconds(175000), new BigDecimal(300.00)));
        mockedEntityList.add(new Entity(4L, "phone Apple",
                Instant.now().plusSeconds(500000), new BigDecimal(1000.00)));
    }

    @BeforeEach
    public void initExpectedStatMap() {
        this.expectedStatMap = new HashMap<>();
        expectedStatMap.put(LocalDateTime.ofInstant(Instant.now(),
                ZoneId.systemDefault()).toLocalDate(), new BigDecimal(20).setScale(2));
        expectedStatMap.put(LocalDateTime.ofInstant(Instant.now().plusSeconds(175000),
                ZoneId.systemDefault()).toLocalDate(), new BigDecimal(200).setScale(2));
        expectedStatMap.put(LocalDateTime.ofInstant(Instant.now().plusSeconds(500000),
                ZoneId.systemDefault()).toLocalDate(), new BigDecimal(1000).setScale(2));
    }

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        this.service = new ServiceImpl(timeService, idGenerator, mockedDao);
    }

    @DisplayName("Method addNewItem - Happy path - Entity created and stored successfully")
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

    @DisplayName("Method addNewItem - Entity created but failed during storage")
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

    @DisplayName("Method addNewItem - Fail on empty title")
    @Test
    public void testCase3() {
        //Given
        final long expectedId = 10;
        final String expectedTitle = "  ";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = new BigDecimal(25.25);

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);

        //When
        assertThrows(IllegalArgumentException.class, () -> service.addNewItem(expectedTitle, expectedPrice));
    }

    @DisplayName("Method addNewItem - Fail on title length (short)")
    @Test
    public void testCase4() {
        //Given
        final long expectedId = 10;
        final String expectedTitle = "ti";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = new BigDecimal(25.25);

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);

        //When
        assertThrows(IllegalArgumentException.class, () -> service.addNewItem(expectedTitle, expectedPrice));
    }

    @DisplayName("Method addNewItem - Fail on title length (long)")
    @Test
    public void testCase5() {
        //Given
        final long expectedId = 10;
        final String expectedTitle = "title is longer than 20 letters";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = new BigDecimal(25.25);

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);

        //When
        assertThrows(IllegalArgumentException.class, () -> service.addNewItem(expectedTitle, expectedPrice));
    }

    @DisplayName("Method addNewItem - Fail on title uniqueness.")
    @Test
    public void testCase6() {
        //Given
        final long expectedId = 10;
        final String expectedTitle = "phone SamsungA7";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = new BigDecimal(25.25);

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);
        when(mockedDao.findAll()).thenReturn(mockedEntityList);

        //When
        assertThrows(IllegalArgumentException.class, () -> service.addNewItem(expectedTitle, expectedPrice));
    }

    @DisplayName("Method addNewItem - Fail on empty price")
    @Test
    public void testCase7() {
        //Given
        final long expectedId = 10;
        final String expectedTitle = "Item name";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = null;

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);

        //When
        assertThrows(IllegalArgumentException.class, () -> service.addNewItem(expectedTitle, expectedPrice));
    }

    @DisplayName("Method addNewItem - Fail on price lower limit")
    @Test
    public void testCase8() {
        //Given
        final long expectedId = 10;
        final String expectedTitle = "Item name";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = new BigDecimal(14.99);

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);

        //When
        assertThrows(IllegalArgumentException.class, () -> service.addNewItem(expectedTitle, expectedPrice));
    }

    @DisplayName("Method addNewItem - Fail on price scale more than 2 decimal points")
    @Test
    public void testCase9() {
        //Given
        final long expectedId = 10;
        final String expectedTitle = "Item name";
        final Instant expectedDateIn = Instant.now();
        final BigDecimal expectedPrice = new BigDecimal(20.118765);
        final Entity expectedEntity = new Entity(expectedId, expectedTitle, expectedDateIn, expectedPrice);

        when(timeService.now()).thenReturn(expectedDateIn);
        when(idGenerator.nextId()).thenReturn(expectedId);

        //When
        service.addNewItem(expectedTitle, expectedPrice);

        //Then
        ArgumentCaptor<Entity> argument = ArgumentCaptor.forClass(Entity.class);
        verify(mockedDao).store(argument.capture());
        assertEquals(new BigDecimal(20.12).setScale(2, RoundingMode.HALF_UP), argument.getValue().getPrice());
    }

    @DisplayName("Method getStatistic - Happy case - Return Map <Date, " +
            "average price of items added during Date>")
    @Test
    public void testCase10() {
        //Given
        when(mockedDao.findAll()).thenReturn(mockedEntityList);

        //When
        Map<LocalDate, BigDecimal> actual = service.getStatistic();

        //Then
        assertThat(actual).isEqualTo(expectedStatMap);
    }

    @DisplayName("Method getStatistic - NetworkException received")
    @Test
    public void testCase11() {
        //Given
        NetworkException exception = new NetworkException();
        when(mockedDao.findAll()).thenThrow(exception);

        //When
        Map<LocalDate, BigDecimal> actual = service.getStatistic();
    }

}