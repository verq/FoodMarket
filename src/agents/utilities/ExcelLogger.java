package utilities;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.management.RuntimeErrorException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import constants.Products;
import agents.MarketAgent;

public class ExcelLogger {

	private FileWriter tempFile;
	private String tempFileName;
	private int numberOfLastWroteRow;
	private final String DELIMITER = "||";
	private final String EXCEL_EXTENSION = "xlsx";

	public ExcelLogger() {
		if (tempFile == null) {
			generateNewLogFile();
		}
	}

	public synchronized void writeAgent(MarketAgent agent) {
		try {
			tempFile.write(createAgentString(agent));

			if (numberOfLastWroteRow > 5000) {
				writeToFile();
			}

			numberOfLastWroteRow++;

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void generateNewLogFile() {
		tempFileName = generateTempFileName();
			numberOfLastWroteRow = 0;
		try {
			tempFile = new FileWriter(tempFileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private String generateTempFileName() {
		return "logs" + System.getProperty("file.separator") + "agentlog_" + (new Date()).getTime() + "_" + AgentsUtilities.randomInt(0, 1000);
	}

	private void writeToFile() {
		try {
			closeTempFile();

			FileReader fileReader = new FileReader(tempFileName);
			BufferedReader reader = new BufferedReader(fileReader);
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet();
			writeTitles(sheet);
			int lastWroteRow = 1;
			setColumnsStyle(sheet);
			while (reader.ready()) {
				String line = reader.readLine();
				writeLineToRow(sheet, line, lastWroteRow);
				lastWroteRow++;
			}
			reader.close();
			fileReader.close();
			FileOutputStream fileOut = new FileOutputStream(tempFileName + "." + EXCEL_EXTENSION);
			workbook.write(fileOut);
			fileOut.close();
			generateNewLogFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void closeTempFile() throws IOException {
		tempFile.flush();
		tempFile.close();
		tempFile = null;
	}

	private void writeTitles(XSSFSheet sheet) {

	}

	private void writeLineToRow(XSSFSheet sheet, String line, int numberOfRow) {
		Pattern p = Pattern.compile(DELIMITER, Pattern.LITERAL);
		String[] tokens = p.split(line);
		XSSFRow row = sheet.createRow(numberOfRow);
		for (int i = 0; i < tokens.length; i++) {
			XSSFCell cell = row.createCell(i);
			cell.setCellValue(tokens[i]);
		}
	}

	private void setColumnsStyle(Sheet sheet) {
		for (int i = 0; i < 3; i++) { // TODO
			sheet.setColumnWidth(i, 3000);
		}
	}

	private String createAgentString(MarketAgent agent) {
		return addDelimitersToAgentString(agent.getName(), agent.getMyStrategy().toString(), agent.getAgentState()
				.toString(),
				((Double) agent.getMoney()).toString(), writeMap(agent.getHave()), writeMap(agent.getBuy()),
				writeMap(agent.getSell()))
				+ "\n";
	}

	private String addDelimitersToAgentString(String... strings) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < strings.length; i++) {
			sb.append(strings[i] + DELIMITER);
		}
		return sb.toString();
	}

	private String writeMap(EnumMap<Products, Double> map) {
		if (map != null) {
		StringBuffer sb = new StringBuffer();
		for (Products product : Products.values()) {
			if (map.containsKey(product)) {
				sb.append(map.get(product));
			}
			sb.append(DELIMITER);
		}
		return sb.toString();
		} else {
			return "";
		}
	}
}
