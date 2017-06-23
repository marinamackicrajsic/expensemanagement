package dao;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import com.mysql.jdbc.Driver;

import dto.Expense;

public class ExpenseJDBCDAO implements ExpenseDAO {

	private static final String USER = "user";
	private static final String PASSWORD = "password";
	private static final String DB_URL = "db.url";
	private static final String PATH_CONFFILE = "config" + File.separator + "config.properties";
	private static final String FIND_ALL = "SELECT * FROM Expense";
	private static final String SAVE = "INSERT INTO Expense (`amount`, `reason`,`date`) "
			+ "VALUES (?, ?, ?)";
	// consider using log4j
	private static final Logger LOG = Logger.getLogger(ExpenseJDBCDAO.class.getName());

	@Override
	public List<Expense> findAll() {

		Connection conn = openConnection();
		Statement stmt = null;
		List<Expense> expenses = new ArrayList<>();

		try {
			stmt = conn.createStatement();
			// max number of fetched rows is set to 5000.This should be replaced
			// when paging policy is determined - client side or server side
			stmt.setMaxRows(5000);
			ResultSet rs = stmt.executeQuery(FIND_ALL);

			while (rs.next()) {
				expenses.add(mapToExpense(rs));
			}
			rs.close();

		} catch (SQLException e) {
			LOG.log(LOG.getLevel(), e.getLocalizedMessage(), e);
		} finally {
			closeConnection(conn, stmt);
		}
		return expenses;
	}

	@Override
	public void save(Expense expense) {

		Connection conn = openConnection();
		PreparedStatement stmt = null;

		try {
			stmt = conn.prepareStatement(SAVE, Statement.RETURN_GENERATED_KEYS);
			stmt.setBigDecimal(1, expense.getAmount());
			stmt.setString(2, expense.getReason());
			stmt.setDate(3, Date.valueOf(expense.getDate()));
			stmt.executeUpdate();
		} catch (SQLException e) {
			LOG.log(LOG.getLevel(), e.getLocalizedMessage(), e);
		} finally {
			closeConnection(conn, stmt);
		}
	}

	private Connection openConnection() {
		try {
			Properties props = new Properties();
			props.load(this.getClass().getClassLoader().getResourceAsStream(PATH_CONFFILE));

			DriverManager.registerDriver(new Driver());
			return DriverManager.getConnection(props.getProperty(DB_URL), props.getProperty(USER),
					props.getProperty(PASSWORD));
		} catch (SQLException | IOException e) {
			LOG.log(LOG.getLevel(), e.getLocalizedMessage(), e);
		}
		return null;
	}

	private void closeConnection(Connection conn, Statement stmt) {
		try {
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			LOG.log(LOG.getLevel(), e.getLocalizedMessage(), e);
		}
	}

	private Expense mapToExpense(ResultSet rs) throws SQLException {
		Long id = rs.getLong("id");
		BigDecimal amount = rs.getBigDecimal("amount");
		String reason = rs.getString("reason");
		LocalDate date = rs.getDate("date").toLocalDate();
		return new Expense(id, reason, date, amount);
	}

}
