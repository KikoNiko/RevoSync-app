package com.kbc.kibi_coins.repository;

import com.kbc.kibi_coins.model.CategoryEnum;
import com.kbc.kibi_coins.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> getAllByCategory_NameOrderByDateDesc(CategoryEnum categoryEnum);

    @Query(value = "from Expense where extract(year from date) = ?1 and extract(month from date) = ?2")
    List<Expense> findAllByDate_Month(@Param(value = "dateMonth") int year, short month);
    @Query(value = "from Expense where extract(year from date) = ?1")
    List<Expense> findAllByDate_Year(@Param(value = "dateYear") int dateYear);
}
