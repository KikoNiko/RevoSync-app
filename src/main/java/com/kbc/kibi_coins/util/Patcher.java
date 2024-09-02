package com.kbc.kibi_coins.util;

import com.kbc.kibi_coins.model.Expense;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class Patcher {
    public static void expensePatcher(Expense existingExpense, Expense incompleteExpense) throws IllegalAccessException {

        Class<?> expenseClass= Expense.class;
        Field[] expenseFields=expenseClass.getDeclaredFields();

        for(Field field : expenseFields){
            field.setAccessible(true);

            //Check if the value of the field is not null, if not update the existing expense
            Object value=field.get(incompleteExpense);
            if(value!=null){
                field.set(existingExpense,value);
            }

            field.setAccessible(false);
        }

    }
}
