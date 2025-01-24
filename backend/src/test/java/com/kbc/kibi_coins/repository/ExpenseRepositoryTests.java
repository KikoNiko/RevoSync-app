package com.kbc.kibi_coins.repository;

import com.kbc.kibi_coins.model.Category;
import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.Expense;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ExpenseRepositoryTests {
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer
            = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    ExpenseRepository underTest;

    private Expense test1;
    private Expense test2;
    private Expense test3;

    @BeforeEach
    void setUp() {
        Category education = new Category(CategoryEnum.EDUCATION);
        Category others = new Category(CategoryEnum.OTHERS);
        categoryRepository.save(education);
        categoryRepository.save(others);

        test1 = Expense.builder()
                .amount(BigDecimal.ONE)
                .category(education)
                .date(LocalDate.of(2024, 9 ,12))
                .build();
        test2 = Expense.builder()
                .amount(BigDecimal.TWO)
                .category(education)
                .date(LocalDate.of(2024, 2 ,21))
                .build();
        test3 = Expense.builder()
                .amount(BigDecimal.TEN)
                .category(others)
                .date(LocalDate.of(2024, 5 ,2))
                .build();

        underTest.saveAll(List.of(test1, test2, test3));
    }

    @Test
    void isEstablishedConnection() {
        assertThat(postgreSQLContainer.isCreated()).isTrue();
        assertThat(postgreSQLContainer.isRunning()).isTrue();
    }

    @Test
    void getAllByCategory_NameOrderByDateDesc_ReturnsCorrectList() {
        Category category = categoryRepository.findByName(CategoryEnum.EDUCATION);

        List<Expense> allByCategoryName = underTest.getAllByCategory_NameOrderByDateDesc(CategoryEnum.EDUCATION);

        assertThat(allByCategoryName).isNotEmpty();
        assertThat(allByCategoryName).contains(test1);
        assertThat(allByCategoryName).contains(test2);
        assertThat(allByCategoryName).doesNotContain(test3);
        assertEquals(allByCategoryName.getFirst().getCategory(), category);
        assertThat(allByCategoryName.getLast().equals(test2)).isTrue();
    }

    @Test
    void findAllByDate_Month_ReturnsCorrectList() {
        List<Expense> allByDateMonth = underTest.findAllByDate_Month(2024, (short) 9);

        assertThat(allByDateMonth).isNotEmpty();
        assertThat(allByDateMonth.size() == 1).isTrue();
        assertEquals(allByDateMonth.getFirst().getAmount(), BigDecimal.ONE);
    }

    @Test
    void findAllByDate_Year_ReturnsCorrectList() {
        List<Expense> allByDateYear = underTest.findAllByDate_Year(2024);

        assertThat(allByDateYear).isNotEmpty();
        assertEquals(allByDateYear.size(), 3);
        assertThat(allByDateYear.stream().filter(e -> e.getDate().getYear() != 2024)).isEmpty();
    }

    @Test
    void findAllByDate_Year_WithFutureYear_ReturnsEmpty() {
        List<Expense> allByDateYear = underTest.findAllByDate_Year(2025);
        assertThat(allByDateYear).isEmpty();

    }

}