package parser;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Calendar;

import gui.GUIController;
import parser.ParsedCommand.ParsedCommandBuilder;
import parser.DateTime.DateTimeBuilder;
import parser.ParsedCommand.ConfigType;
import parser.ParsedCommand.TaskType;

public class DateTime {
    public enum ParserType {
        FORMATTED_PARSER, FLEXIBLE_PARSER, NATTY_PARSER;
    }
    private static final String DEFAULT_TWENTYFOUR_HOUR_TIME = "23:59";
	private static final String DEFAULT_TWELVE_HR__TIME = "11:59pm";
	private static final int INDEX_FOR_END = 1;
	private static final int INDEX_FOR_START = 0;
	private static final int INDEX_FOR_TIME_FORMAT = 2;
	private static final int INDEX_FOR_DATE_FORMAT = 2;
	static LocalDate today = LocalDate.now();
	static String currYear = String.valueOf(today.getYear());
	
	
	private static final int INDEX_FOR_DATE_ARR = 0;
	private static final int INDEX_FOR_YEAR_ARR = 2;
	private static final int INDEX_FOR_MONTH_ARR = 1;
	private String[] startDate;
    private String[] endDate;
    private String startTime;
    private String endTime;
    private Boolean hasStartDate;
    private Boolean hasEndDate;
    private Boolean hasStartTime;
    private Boolean hasEndTime;
    private Calendar startDateTime;
    private Calendar endDateTime;
    private String unparsed;
	private Boolean isValid;
    
    public DateTime(DateTimeBuilder builder) {
    	 this.startDate = builder.startDate;
    	 this.endDate = builder.endDate;
    	 this.startTime = builder.startTime;
    	 this.endTime = builder.endTime;
    	 this.hasStartDate = builder.hasStartDate;
    	 this.hasEndDate = builder.hasEndDate;
    	 this.hasStartTime = builder.hasStartDate;
    	 this.hasEndTime = builder.hasEndTime;
    	 this.startDateTime = builder.startDateTime;
    	 this.endDateTime = builder.endDateTime;
    	 this.unparsed = builder.unparsed;
 		 this.isValid = builder.isValid;
	}

	Calendar[] getSearchDatesTimes() {
		standardizeDatesForSearch();
		return getCalendarDates();
	}
	
	Calendar[] getStdDatesTimes() {
//		if (this.isValid == false) {
//			return null;
//		} else if (this.isValid == null) {
//			Calendar[] cal = {null, null};
//			return cal;
//		}
		System.out.println("VALID? " + isValid);
		standardizeDatesForStd();
		System.out.print("After standardizeDates: ");
		if (startDate != null) {
			for (int i = 0; i < 3; i++) {
				System.out.print(startDate[i] + "/");
			}
		} else {
			System.out.print(startDate);
		}
		System.out.print(" || ");
		if (endDate != null) {
			for (int i = 0; i < 3; i++) {
				System.out.print(endDate[i] + "/");
			}
		} else {
			System.out.print(endDate);
		}
		System.out.println(" << ");
		return getCalendarDates();
	}
	
	private void standardizeDatesForSearch() {
		setYears(true);
		setTimesForSearch();
	}

	private void standardizeDatesForStd() {
		if (startDate == null) {
			return;
		}
		setYears(false);
		setTimesForStandard();
	}
	
	private void setYears(boolean isSearch) { 
		System.out.println(currYear);
		
		if (startDate == null) { // no date detected
			return;
		} else {
			if (endDate == null) {
				if (startDate[INDEX_FOR_YEAR_ARR] == null) { // no year
					if (isSearch) {
						setCurrentYear(startDate);
						return;
					} else {
						setNextValidYear(startDate, false);
					}
				}
			} else { // has start and end date
				if (endDate[INDEX_FOR_YEAR_ARR] != null) { // has end year
					if (startDate[INDEX_FOR_YEAR_ARR] == null) { // no start year
						startDate[INDEX_FOR_YEAR_ARR] = endDate[INDEX_FOR_YEAR_ARR];
					} 
				} else { // no end year
					if (isSearch) {
						if(startDate[INDEX_FOR_YEAR_ARR] == null) {
							setCurrentYear(startDate);
						}
						if (isAfter(endDate, startDate)) {
							setCurrentYear(endDate);
						} else {
							setYearAfter(endDate, startDate);
						}
					} else { // is add, edit etc.
						if (startDate[INDEX_FOR_YEAR_ARR] == null) {
						    setNextValidYear(startDate, false);
					    }
					    if (endDate[INDEX_FOR_DATE_ARR] != null) {
						    setYearAfter(endDate, startDate);
					    }
					}
				}
			}
		}
	}
	
	private static void setYearAfter(String[] secondDate, String[] firstDate) {
		secondDate[INDEX_FOR_YEAR_ARR] = String.valueOf(firstDate[INDEX_FOR_YEAR_ARR] + 1);	
	}

	private static Boolean isAfter(String[] secondDate, String[] firstDate) {
		String second = currYear + "-" + secondDate[INDEX_FOR_MONTH_ARR] + "-" + secondDate[INDEX_FOR_DATE_ARR];
		String first = currYear + "-" + firstDate[INDEX_FOR_MONTH_ARR] + "-" + firstDate[INDEX_FOR_DATE_ARR];
		//System.out.println(newDateString);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
		//System.out.println(today.toString());
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
		date[INDEX_FOR_YEAR_ARR] = currYear;
	}

	private static void setNextValidYear(String[] date, boolean isSearch) { //if is not search
		LocalDate today = LocalDate.now();
		
		String currYear = String.valueOf(today.getYear());
		System.out.println(currYear);
		String newDateString = currYear + "-" + date[INDEX_FOR_MONTH_ARR] + "-" + date[INDEX_FOR_DATE_ARR];
		System.out.println(newDateString);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
		System.out.println(today.toString());
		try {
			LocalDate newDate = LocalDate.parse(newDateString, formatter);
			if (newDate.isAfter(today) || newDate.isEqual(today)) {
				System.out.println("IS AFTER");
				date[INDEX_FOR_YEAR_ARR] = currYear;
			} else {
				date[INDEX_FOR_YEAR_ARR] = String.valueOf(today.getYear() + 1);
			}
		} catch (DateTimeParseException e){
			date = null;
		}
	}


	private void setTimesForSearch() {
        if (startDate == null) {
        	return;
        // has dates
        } else if (endDate == null) { // has one date
        	endDate = startDate;
        	if (startTime == null) { // date without time
        		startTime = "00:00";
        		endTime = "23:59";
        	} else { // date with time
        		if (endTime == null) { // no end time
        			endTime = "23:59";
        		} 
        	}
        	return;
        } else { // has end date
        	if (startTime == null) { // only start & end dates
        		// does not support date to date time e.g. 2 jan to 4 jan 3pm
        		startTime = "00:00";
        		endTime = "23:59";
        	} else { //has start time
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
        // has dates
        } else if (endDate == null) { // has one date
        	if (startTime == null) { // date without time
        		startTime = "23:59";
        	} else { // one date with start time
        		if (endTime != null) { // is event
        			endDate = startDate;
        		}
        	}
        	return;
        } else { // has end date
        	if (startTime == null) { // only start & end dates
        		// does not support date to date time e.g. 2 jan to 4 jan 3pm
        		startTime = "00:00";
        		endTime = "23:59";
        	} else { //has start time
        		if (endTime == null) {
        			endTime = "23:59";
        		}
        	}
        }
	} 

	private static String convertDateTimeToStandardFormat(String[] dateArr, String time) {
		String date = dateArr[INDEX_FOR_DATE_ARR];
		if (date == null) {
			return null;
		}
		String month = dateArr[INDEX_FOR_MONTH_ARR];
		String year = dateArr[INDEX_FOR_YEAR_ARR];
		return date + "/" + month + "/" + year + " " + time;
	}

	
	
	public Calendar[] getCalendarDates() {
		Calendar[] calTimes = new Calendar[2];
		
		Calendar startCal = Calendar.getInstance();
		Calendar endCal = Calendar.getInstance();
		SimpleDateFormat dateTimeFormat = new SimpleDateFormat("d/M/yy HH:mm");
		if (isValid != null && isValid == false) {
			return null;
		}
		try {
			if (startDate == null) { // no date and time
				startCal = null;
				endCal = null;
			} else { // has start date
				dateTimeFormat.setLenient(false);
				startCal.setTime(dateTimeFormat.parse(convertDateTimeToStandardFormat(startDate, startTime)));
				if (endDate != null) {
					endCal.setTime(dateTimeFormat.parse(convertDateTimeToStandardFormat(endDate, endTime)));
				} else {
					endCal = null;
				}
			}
		} catch (java.text.ParseException e) {
			e.printStackTrace();
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
        private Boolean hasStartDate;
        private Boolean hasEndDate;
        private Boolean hasStartTime;
        private Boolean hasEndTime;
        private Calendar startDateTime;
        private Calendar endDateTime;
		private String unparsed;
		private Boolean isValid = null;

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
		
		public DateTimeBuilder dates(String[][] parsedDates) {
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
				int startD = calStart.get(Calendar.DAY_OF_MONTH);
				int startM = calStart.get(Calendar.MONTH) + 1;
				int startY = calStart.get(Calendar.YEAR);
				System.out.println("Natty start: " + startD + "." + startM + "." + startY);
				String[] start = {String.valueOf(startD), String.valueOf(startM), String.valueOf(startY)};
				this.startDate = start;
				this.isValid = true;
			} else {
				this.startDate = null;
			}
			if (calEnd != null) {
				int endD = calEnd.get(Calendar.DAY_OF_MONTH);
				int endM = calEnd.get(Calendar.MONTH) + 1;
				int endY = calEnd.get(Calendar.YEAR);
				System.out.println("Natty end: " + endD + "." + endM + "." + endY);
				String[] end = {String.valueOf(endD), String.valueOf(endM), String.valueOf(endY)};
				this.endDate = end;
			} else {
				this.endDate = null;
			}
			return this;
		}
		
		public DateTime build() {
			return new DateTime(this);
		}

		String getUnparsedInput() {
			return this.unparsed;
		}

		String getStartTime() {
			return startTime;
		}
		
		String getEndTime() {
			return endTime;
		}

		boolean hasEndTime() {
			return endTime != null;
		}	
		
		boolean hasStartTime() {
			return startTime != null;
		}

		public Boolean isDoneParsing() {
			if (isValid == null) {
				return false;
			} else {
				return true;
			}
		}

		

		public String[] getStartDate() {
			return startDate;
		}

    }
}
