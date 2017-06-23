package dao;

import java.util.List;

import dto.Expense;

public interface ExpenseDAO {

	List<Expense> findAll();

	void save(Expense expense);

}
