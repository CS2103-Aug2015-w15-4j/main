//@@author A0114620X

package parser;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;

public class DateTime {
    public enum ParserType {
        FORMATTED_PARSER, FLEXIBLE_PARSER, NATTY_PARSER;
    }
    static LocalDate today = LocalDate.now();
	private static final String CURR_YEAR = String.valueOf(today.getYear());
	
	
	private static final int INDEX_FOR_DATE = 0;
	private static final int INDEX_FOR_MONTH = 1;
	private static final int INDEX_FOR_YEAR = 2;
	
	private String[] startDate;
    private String[] endDate;
    private String startTime;
    private String endTime;
	private Boolean isValid;
	private ParserType parser;
    
    public DateTime(DateTimeBuilder builder) {
    	 this.startDate = builder.startDate;
    	 this.endDate = builder.endDate;
    	 this.startTime = builder.startTime;
    	 this.endTime = builder.endTime;
 		 this.isValid = builder.isValid;
 		 this.parser = builder.parser;
	}

	Calendar[] getSearchDatesTimes() {
		standardizeDatesForSearch();
		return getCalendarDates();
	}
	
	Calendar[] getStdDatesTimes() {
		standardizeDatesForStd();
		return getCalendarDates();
	}
	
	private void standardizeDatesForSearch() {
		setYearsForSearch();
		setTimesForSearch();
	}

	private void standardizeDatesForStd() {
		if (startDate == null) {
			return;
		}
		setYearsForStd();
		setTimesForStandard();
	}
	
	private void setYearsForSearch() { 
		if (startDate == null) { // no date detected
			return;
		} else {
			if (endDate == null) {
				if (startDate[INDEX_FOR_YEAR] == null) { 
					setCurrentYear(startDate);
					return;
				}
			} else { // has start and end date
				if (endDate[INDEX_FOR_YEAR] != null) { 
					if (startDate[INDEX_FOR_YEAR] == null) { 
						startDate[INDEX_FOR_YEAR] = endDate[INDEX_FOR_YEAR];
					} 
				} else { // no end year
					if(startDate[INDEX_FOR_YEAR] == null) {
						setCurrentYear(startDate);
					}
					if (isAfter(endDate, startDate)) {
						setCurrentYear(endDate);
					} else {
						setYearAfter(endDate, startDate);
					}
				}
			}
		}
	}
	
	private void setYearsForStd() { 
		if (startDate == null) { 
			return;
		} else {
			if (endDate == null) {
				if (startDate[INDEX_FOR_YEAR] == null) { 
					setNextValidYear(startDate);
				}
			} else { 
				if (endDate[INDEX_FOR_YEAR] != null) { 
					if (startDate[INDEX_FOR_YEAR] == null) { 
						startDate[INDEX_FOR_YEAR] = endDate[INDEX_FOR_YEAR];
					} 
				} else { 
					if (startDate[INDEX_FOR_YEAR] == null) {
					    setNextValidYear(startDate);
				    }
					if (isAfter(endDate, startDate)) {
						endDate[INDEX_FOR_YEAR] = startDate[INDEX_FOR_YEAR];
					} else {
						setYearAfter(endDate, startDate);
				    }
				}
			}
		}
	}
	private static void setYearAfter(String[] secondDate, String[] firstDate) {
		secondDate[INDEX_FOR_YEAR] = String.valueOf(Integer.parseInt(firstDate[INDEX_FOR_YEAR]) + 1);	
	}

	private static Boolean isAfter(String[] secondDate, String[] firstDate) {
		String second = CURR_YEAR + "-" + secondDate[INDEX_FOR_MONTH] + "-" + secondDate[INDEX_FOR_DATE];
		String first = CURR_YEAR + "-" + firstDate[INDEX_FOR_MONTH] + "-" + firstDate[INDEX_FOR_DATE];
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
		try {
			LocalDate secDate = LocalDate.parse(second, formatter);
			LocalDate fstDate = LocalDate.parse(first, formatter);
			if (secDate.isAfter(fstDate) || secDate.isEqual(fstDate)) {
				return true;
			} else {
				return false;
			}
		} catch (DateTimeParseException e){
			return null;
		}
	}

	private static void setCurrentYear(String[] date) {
		date[INDEX_FOR_YEAR] = CURR_YEAR;
	}

	private static void setNextValidYear(String[] date) { //if is not search
		LocalDate today = LocalDate.now();
		String newDateString = CURR_YEAR + "-" + date[INDEX_FOR_MONTH] + "-" + date[INDEX_FOR_DATE];
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
		try {
			LocalDate newDate = LocalDate.parse(newDateString, formatter);
			if (newDate.isAfter(today) || newDate.isEqual(today)) {
				setCurrentYear(date);
			} else {
				date[INDEX_FOR_YEAR] = String.valueOf(today.getYear() + 1);
			}
		} catch (DateTimeParseException e){
			date = null;
		}
	}


	private void setTimesForSearch() {
        if (startDate == null) {
        	return;
        } else if (endDate == null) { 
        	endDate = startDate;
        	if (startTime == null) { 
        		startTime = "00:00";
        		endTime = "23:59";
        	} else { 
        		if (endTime == null) { 
        			endTime = "23:59";
        		} 
        	}
        	return;
        } else { 
        	if (startTime == null) { // only start & end dates
        		// note: does not support date to date time e.g. 2 jan to 4 jan 3pm
        		startTime = "00:00";
        		endTime = "23:59";
        	} else { 
        		if (endTime == null) {
        			endTime = "23:59";
        		}
        	}
        	return;
        }
	} 


	private void setTimesForStandard() {
        if (startDate == null) {
        	return;
        } else if (endDate == null) { 
        	if (startTime == null) { 
        		startTime = "23:59";
        	} else { 
        		if (endTime != null) { 
        			endDate = startDate;
        		}
        	}
        	return;
        } else { 
        	if (startTime == null) { 
        		// Note: does not support date to date time e.g. 2 jan to 4 jan 3pm
        		startTime = "00:00";
        		endTime = "23:59";
        	} else { //has start time
        		if (endTime == null) {
        			endTime = "23:59";
        		}
        	}
        }
	} 

	private static String convertDateTimeToString(String[] dateArr, String time) {
		String date = dateArr[INDEX_FOR_DATE];
		if (date == null) {
			return null;
		}
		String month = dateArr[INDEX_FOR_MONTH];
		String year = dateArr[INDEX_FOR_YEAR];
		String std = date + "/" + month + "/" + year + " " + time;
		return std;
	}

	
	
	public Calendar[] getCalendarDates() {
		Calendar[] calTimes;
		if (parser == ParserType.NATTY_PARSER) {
			calTimes = new Calendar[3];
		} else {
			calTimes = new Calendar[2];
		}
		
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("d/M/yy HH:mm");
		
		// Check for null first to avoid null pointer exception
		if (isValid != null && isValid == false) {
			return null;
		}
		
		try {
			if (startDate == null) { // no date and time
				startCal = null;
				endCal = null;
			} else { // has start date
				dateTimeFormat.setLenient(false);
				startCal.setTime(dateTimeFormat.parse(convertDateTimeToString(startDate, startTime)));
				if (endDate != null) {
					endCal.setTime(dateTimeFormat.parse(convertDateTimeToString(endDate, endTime)));
				} else {
					endCal = null;
				}
			}
		} catch (java.text.ParseException e) {
			return null; // failed to parse
		}
		
		calTimes[0] = startCal;
		calTimes[1] = endCal;

		return calTimes;
	}

    static class DateTimeBuilder {		
    	private String[] startDate;
        private String[] endDate;
        private String startTime;
        private String endTime;
        private String unparsed;
		private Boolean isValid = null;
		private ParserType parser;

		public DateTimeBuilder(String input) {
			this.unparsed = input;
		}
		
		DateTimeBuilder times(String[] parsedTimes) {
			if (parsedTimes == null) {
				this.isValid = false;
			} else {
				this.startTime = parsedTimes[0];
				this.endTime = parsedTimes[1];
			}
			return this;
		}
		
		DateTimeBuilder dates(String[][] parsedDates) {
			if (parsedDates == null) {
				this.isValid = false;
			} else {
				if (parsedDates[0][0] == null) {
					this.startDate = null;
				} else {
					this.isValid = true;
					this.startDate = parsedDates[0];
				}
				
				if (parsedDates[1][0] == null) {
					this.endDate = null;
				} else {
					this.endDate = parsedDates[1];
				}
			}
			return this;
		}
		
		public DateTimeBuilder calDates(Calendar[] dates) {
			Calendar calStart = dates[0];
			Calendar calEnd = dates[1];
			if (calStart != null) {
				String[] start = convertCalToArr(calStart);
				this.startDate = start;
				this.isValid = true;
			} 
			
			if (calEnd != null) {
				String[] end = convertCalToArr(calEnd);
				this.endDate = end;
			} 
			this.parser = ParserType.NATTY_PARSER;
			return this;
		}

		private String[] convertCalToArr(Calendar calStart) {
			int date = calStart.get(Calendar.DAY_OF_MONTH);
			int month = calStart.get(Calendar.MONTH) + 1;
			int year = calStart.get(Calendar.YEAR);
			String[] arr = {String.valueOf(date), String.valueOf(month), String.valueOf(year)};
			return arr;
		}
		
		public DateTime build() {
			return new DateTime(this);
		}

		String getUnparsedInput() {
			return this.unparsed;
		}

		public Boolean isDoneParsing() {
			if (isValid == null) {
				return false;
			} else {
				return true;
			}
		}
    }
}
