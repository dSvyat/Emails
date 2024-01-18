package ua.vozniuk.demo;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a utility class for handling Excel files, providing methods for reading, writing, and modifying data.
 */
public class Excel {
    private final String filePath;
    private static FileInputStream file;
    private static Workbook workbook;
    private static Sheet sheet;

    /**
     * Constructs an instance of the Excel class with the specified file path.
     *
     * @param filePath The path to the Excel file.
     * @throws FileNotFoundException If the specified Excel file is not found.
     */
    public Excel(String filePath) throws FileNotFoundException {
        this.filePath = filePath;
        file = new FileInputStream(filePath);
        try {
            workbook = new XSSFWorkbook(file);
        } catch (IOException e){
            e.printStackTrace();
        }
        sheet = workbook.getSheetAt(0);
    }

    /**
     * Retrieves data from the Excel file and returns it as a formatted string.
     *
     * @return A string containing data from the Excel file in the format "Column:x, Row:y, Value:z".
     */
    public String getExcelData() {
        StringBuilder result = new StringBuilder();
        Map<Integer, List<String>> data = new HashMap<>();
        int i = 0;
        for (Row row : sheet) {
            data.put(i, new ArrayList<>());
            for (Cell cell : row) {
                if (Objects.requireNonNull(cell.getCellType()) == CellType.STRING) {
                    result.append("Column:").append(cell.getColumnIndex()).append(", Row:").append(cell.getRowIndex()).append(", Value: ").append(cell.toString()).append("\n");
                }
            }
            i++;
        }
        return result.toString();
    }

    /**
     * Performs Excel-related operations based on the provided answer, such as updating cell values and styles.
     * Additionally, handles additional information if present in the answer.
     *
     * @param answer The answer received from a request.
     * @return {@code true} if the Excel operations were successful, {@code false} otherwise.
     */
    public boolean doExcelWork(String answer) {
        CellStyle style = workbook.createCellStyle();
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        String value = getValue(answer);
        switch (value) {
            case "Rejected" -> style.setFillForegroundColor(IndexedColors.DARK_RED.getIndex());
            case "Scheduled interview" -> style.setFillForegroundColor(IndexedColors.DARK_YELLOW.getIndex());
            case "Confirmed" -> style.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            default -> style.setFillForegroundColor(IndexedColors.BLUE.getIndex());
        }
        Row row = sheet.getRow(getRow(answer));
        Cell cell = row.createCell(getColumn(answer));
        cell.setCellValue(value);
        cell.setCellStyle(style);
        if(checkForAdditionalInfo(answer)){
            Row row1 = sheet.getRow(getRow(answer));
            Cell cell1 = row1.createCell(5);
            cell1.setCellValue(getAdditionalInfo(answer));
        }
        try {
            workbook.write(new FileOutputStream(filePath));
            return true;
        } catch (IOException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Changes the value of a specific cell in the Excel sheet.
     *
     * @param row    The row index of the cell.
     * @param column The column index of the cell.
     * @param value  The new value for the cell.
     * @return {@code true} if the cell value was successfully changed, {@code false} otherwise.
     */
    public boolean changeExcelCell(int row, int column, String value){
        CellStyle style = workbook.createCellStyle();
        Row row1 = sheet.getRow(row);
        Cell cell = row1.createCell(column);
        cell.setCellValue(value);
        cell.setCellStyle(style);
        try {
            workbook.write(new FileOutputStream(filePath));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Extracts the row index from the provided answer.
     *
     * @param answer The answer containing information about the row.
     * @return The row index parsed from the answer, or -1 if not found.
     */
    public int getRow(String answer){
        String regex = "ROW:(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(answer);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return -1;
        }
    }

    /**
     * Extracts the column index from the provided answer.
     *
     * @param answer The answer containing information about the column.
     * @return The column index parsed from the answer, or -1 if not found.
     */
    public int getColumn(String answer){
        String regex = "COLUMN:(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(answer);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return -1;
        }
    }

    /**
     * Extracts the value from the provided text.
     *
     * @param text The text containing information about the value.
     * @return The value parsed from the text.
     * @throws IllegalArgumentException If the value cannot be extracted.
     */
    public String getValue(String text){
        String regex = "VALUE: (Rejected|Confirmed|Scheduled interview)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks if the provided text contains additional information.
     *
     * @param text The text to check for additional information.
     * @return {@code true} if additional information is present, {@code false} otherwise.
     */
    public boolean checkForAdditionalInfo(String text){
        String regex = "ADDITIONAL:";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

    /**
     * Extracts additional information from the provided text.
     *
     * @param text The text containing additional information.
     * @return The additional information parsed from the text.
     * @throws IllegalArgumentException If additional information cannot be extracted.
     */
    public String getAdditionalInfo(String text){
        String regex = "ADDITIONAL:(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if(matcher.find()){
            return matcher.group(1).trim();
        } else{
            throw new IllegalArgumentException();
        }
    }
}
