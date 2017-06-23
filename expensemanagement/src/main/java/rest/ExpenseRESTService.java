package rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.ClientResponse;

import dto.Expense;
import service.ExpenseService;
import service.ExpenseServiceImpl;

@Path("/expenses")
public class ExpenseRESTService {

	public static final Logger LOG = Logger.getLogger(ExpenseRESTService.class.getName());

	RestClient restClient = new RestClient();
	ExpenseService expenseService = new ExpenseServiceImpl();
	ObjectMapper objectMapper = new ObjectMapper();

	@GET
	@Path("/")
	public Response getExpenses() {
		List<Expense> expenses = expenseService.findExpenses();

		if (!expenses.isEmpty()) {
			try {
				return Response.status(200).entity(objectMapper.writeValueAsString(expenses))
						.build();
			} catch (JsonProcessingException e) {
				LOG.log(LOG.getLevel(), e.getLocalizedMessage(), e);
			}
		}
		return Response.status(500).build();
	}

	@POST
	@Path("/")
	@Consumes("application/x-www-form-urlencoded")
	public Response createExpense(@FormParam("amount") String amount,
			@FormParam("date") String date, @FormParam("reason") String reason) {

		BigDecimal calculatedAmount = calculateAmount(amount);

		try {
			LocalDate lDate = LocalDate.parse(date);
			Expense expense = new Expense(null, reason, lDate, calculatedAmount);
			expenseService.createExpense(expense);
		} catch (DateTimeParseException | IllegalArgumentException | NullPointerException e) {
			LOG.log(LOG.getLevel(), e.getLocalizedMessage(), e);
			return Response.status(422).entity("Invalid input data").build();
		}

		return Response.status(201).build();

	}

	private BigDecimal calculateAmount(String amountString) {
		BigDecimal amount = null;

		if (amountString.matches("[0-9]+(.[0-9]+){0,1}[a-zA-Z]{3}")) {
			int amountLength = amountString.length();
			String currencyString = amountString.substring(amountLength - 3).toUpperCase();
			Currency currency = Currency.getInstance(currencyString);

			BigDecimal value = new BigDecimal(amountString.substring(0, amountLength - 3));

			BigDecimal rate = getConversionRate(Currency.getInstance(Locale.UK), currency);

			amount = value.multiply(rate);
		} else {
			amount = new BigDecimal(amountString);
		}
		return amount;

	}

	private BigDecimal getConversionRate(Currency base, Currency pair) {

		String url = "http://api.fixer.io/latest?base=" + pair.getCurrencyCode() + "&symbols="
				+ base.getCurrencyCode();

		ClientResponse response = restClient.getResponse(url);
		String output = response.getEntity(String.class);
		return extractRate(output);
	}

	private BigDecimal extractRate(String output) {
		BigDecimal rate = null;

		try {
			JsonNode rootNode = objectMapper.readTree(output);
			JsonNode idNode = rootNode.path("rates");
			Iterator<JsonNode> elements = idNode.elements();
			if (elements.hasNext()) {
				rate = new BigDecimal(elements.next().asText());
			}
		} catch (IOException e) {
			LOG.log(LOG.getLevel(), e.getLocalizedMessage(), e);
		}
		return rate;
	}

}
