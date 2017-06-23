package service;

import java.util.List;

import dto.Expense;

public interface ExpenseService {

	void createExpense(Expense expense);

	List<Expense> findExpenses();
}
