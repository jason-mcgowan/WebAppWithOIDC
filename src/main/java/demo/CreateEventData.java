package demo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.Map;

public class CreateEventData {

  private final int creatorId;
  private String name;
  private Date startDate;
  private Date endDate;
  private String description;
  private int quantity;
  private BigDecimal price;

  public CreateEventData(Map<String, String> pairs, int creatorId) throws IllegalArgumentException {
    this.creatorId = creatorId;
    initFields(pairs);
  }

  private void initFields(Map<String, String> pairs) {
    name = pairs.get("name");
    String startDateString = pairs.get("startDate");
    String endDateString = pairs.get("endDate");
    description = pairs.get("description");
    String quantityString = pairs.get("quantity");
    String priceString = pairs.get("price");
    if (description == null) {
      description = "";
    }
    if (name == null || startDateString == null || endDateString == null || quantityString == null
        || priceString == null) {
      throw new IllegalArgumentException("Not all required arguments provided");
    }
    name = name.trim().replaceAll("\\s+", " ");
    if (name.isBlank()) {
      throw new IllegalArgumentException("Name is invalid");
    }
    try {
      startDate = Date.valueOf(startDateString);
      endDate = Date.valueOf(endDateString);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Date format incorrect");
    }
    if (startDate.compareTo(endDate) > 0) {
      throw new IllegalArgumentException("Start date is after End Date");
    }
    double priceDouble;
    try {
      quantity = Integer.parseInt(quantityString);
      priceDouble = Double.parseDouble(priceString);
      price = BigDecimal.valueOf(priceDouble);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Number format incorrect");
    }
    price = price.setScale(2, RoundingMode.CEILING);
    boolean priceIsNegative = price.compareTo(BigDecimal.valueOf(0.0)) < 0;
    boolean priceIsHuge = price.compareTo(BigDecimal.valueOf(999999999.99)) > 0;
    if (quantity < 0 || priceIsNegative || priceIsHuge) {
      throw new IllegalArgumentException("Numeric value out of range");
    }
  }
}
