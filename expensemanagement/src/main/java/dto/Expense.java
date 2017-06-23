package dto;

/* Class Expense holds data to a single expense.
 * Value of the vat is calculated based on set amount and cannot be changed.
 * Suggestion : model should be extended to hold a timestamp for the entry, 
 * user that entered the expense and the user/client for whom the expense 
 * is entered. This was not implemented as it was not part of the test
 * */
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

public class Expense {

	private Long id;
	private String reason;
	private LocalDate date;
	private BigDecimal amount;
	private BigDecimal vat;

	public Expense(Long id, String reason, LocalDate date, BigDecimal amount) {
		this.id = id;
		this.reason = reason;
		this.date = date;
		this.amount = amount;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null) {
			return false;
		}
		
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		Expense other = (Expense) obj;
		
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
		this.vat = amount.divide(new BigDecimal(6), 4, RoundingMode.HALF_EVEN);
	}

	public BigDecimal getVat() {
		return vat;
	}
}
