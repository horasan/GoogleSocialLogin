package com.horasan.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import com.nimbusds.oauth2.sdk.util.StringUtils;

@Component
public class AppUtil {
	
	@Value("${customlog-note}")
	private boolean logNote;
	
	@Value("${customlog-frontend}")
	private boolean logFrontend;
	
	@Value("${customlog-oauth2}")
	private boolean logOauth2;
	
	@Value("${customlog-googlestep}")
	private boolean logGoogleStep;
	
	@Value("${customlog-logFileFolder}")
	private String logFileFolder;
	
	@Value("${customlog-logFileName}")
	private String logFileName;
	
	@Value("${clientagent-frontend-home}")
	private String clientAgentFrontEndHome;
	
	public static String OAUTH2_AUTHENTICATION_TOKEN_ATTRIBUTE_EMAIL = "email";
	public static String SINGLE_TAB = "\t";
	public static String DOUBLE_TAB = SINGLE_TAB + SINGLE_TAB;
	public static String TRIPLE_TAB = SINGLE_TAB + SINGLE_TAB + SINGLE_TAB;
	final static Level CUSTOM_LOG_LEVEL = Level.forName("CUSTOM_LOG_LEVEL", 550);
	final static Logger logger = LogManager.getLogger();
	
	public AppUtil() {
		
		Resource resource = new ClassPathResource("/application.yml");
	    Properties props = null;
		try {
			props = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    logNote=Boolean.valueOf(props.getProperty("customlog-note"));
	    
	    logFrontend=Boolean.valueOf(props.getProperty("customlog-frontend"));
	    logOauth2=Boolean.valueOf(props.getProperty("customlog-oauth2"));
	    logGoogleStep=Boolean.valueOf(props.getProperty("customlog-googlestep"));
	    
	    logFileFolder=String.valueOf(props.getProperty("customlog-logFileFolder"));
	    logFileName=String.valueOf(props.getProperty("customlog-logFileName"));
	    clientAgentFrontEndHome = String.valueOf(props.getProperty("clientagent-frontend-home")); 
		
	}
	
	public String getClientAgentFrontEndHome() {
		return clientAgentFrontEndHome;
	}
	
	public void deleteLogFile() {
		File existingFile = new File(logFileFolder + logFileName);

		if (existingFile.exists()) {
			existingFile.delete();
		}

	}
	
	public String getJSESSIONIDValueFrom(HttpServletRequest request) {
		Cookie jsessionId = Arrays.asList(request.getCookies()).stream().filter(c -> c.getName().equals("JSESSIONID")).findFirst().get();
		return jsessionId.getValue();
	}
	
	public  void printToConsoleAndLog(LogType logType, String indent, String message) {
		printToConsoleAndLog(logType, indent, message, null, null);
	}
	
	public  void printToConsoleAndLog(LogType logType, String indent, String message, String reference, String fromClass) {

		String logTypeDescOpening = "[";
		String logTypeDescClosing = "]";
		String newLine = "";
		if (logGoogleStep && logType.equals(LogType.GOOGLE_STEP)) {
			logTypeDescOpening = "[[";
			logTypeDescClosing = "]]";
			newLine = System.lineSeparator();
		}
				
		String msg = newLine + indent + "---> " + logTypeDescOpening + logType.name() + logTypeDescClosing + " "  + message + System.lineSeparator();
		
		String referenceMessage = "";
		if(StringUtils.isNotBlank(reference)) {
			referenceMessage = indent + DOUBLE_TAB + " [Ref: " + reference + "]" + System.lineSeparator();
		}
		
		String loggingClassName = "";
		if(StringUtils.isNotBlank(fromClass)) {
			loggingClassName = indent + DOUBLE_TAB + " [Logging class: " + fromClass + "]" + System.lineSeparator();
		}
		
		String customLogMessage = msg + referenceMessage + loggingClassName;
		
		if (	(logNote && logType.equals(LogType.NOTE)) 
				|| (logFrontend && logType.equals(LogType.FRONTEND))
				|| (logOauth2 && logType.equals(LogType.OAUTH2))
				|| (logGoogleStep && logType.equals(LogType.GOOGLE_STEP))

		) {

			String newLogFileName = logFileFolder + logFileName; 
			
			try (FileOutputStream fileOutputStream = new FileOutputStream(newLogFileName, true)) {

				fileOutputStream.write(customLogMessage.getBytes());

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
				
	}
	
}
