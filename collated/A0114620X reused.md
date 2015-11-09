# A0114620X reused
###### test\MyParserTest.java
``` java
	private void initLogging(){
		String config = "\nhandlers = java.util.logging.ConsoleHandler\n.level = ALL\n" +
						"java.util.logging.ConsoleHandler.level = FINE\n" +
						"com.sun.level = INFO\n" +
						"javax.level = INFO\n" +
						"sun.level = INFO\n";
 
		InputStream ins = new ByteArrayInputStream(config.getBytes());
 
		Logger logger = Logger.getLogger(MyParserTest.class.getName());
		try {
			LogManager.getLogManager().readConfiguration(ins);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Log manager configuration failed: " + e.getMessage(),e);
		}
	}
	
```
