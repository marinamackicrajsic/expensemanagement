package service;

import java.util.List;

import dao.ExpenseDAO;
import dao.ExpenseJDBCDAO;
import dto.Expense;

public class ExpenseServiceImpl implements ExpenseService {

	ExpenseDAO expenseDAO = new ExpenseJDBCDAO();

	@Override
	public void createExpense(Expense expense) {
		expenseDAO.save(expense);
	}

	@Override
	public List<Expense> findExpenses() {
		List<Expense> expenses = expenseDAO.findAll();
		return expenses;
	}

}
