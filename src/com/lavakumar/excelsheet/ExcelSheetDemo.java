package com.lavakumar.excelsheet;

import com.lavakumar.excelsheet.service.SpreadsheetService;


public class ExcelSheetDemo {

    public static void main(String[] args) {
        SpreadsheetService sheet = new SpreadsheetService();

        runHappyPath(sheet);
        runResetScenario(sheet);
        runCycleScenario(sheet);
    }

    private static void runHappyPath(SpreadsheetService sheet) {
        System.out.println("========== HAPPY PATH ==========");

        sheet.setCell("A1", "10");
        sheet.setCell("B1", "20");
        sheet.setCell("C1", "=A1+B1");
        sheet.setCell("D1", "=C1*2");
        sheet.setCell("A2", "=D1/5 + 7");
        sheet.setCell("B2", "=(A1+B1)*3");
        sheet.setCell("C2", "=B2-A2");

        System.out.println("A1 = " + sheet.getCellDisplayValue("A1"));
        System.out.println("B1 = " + sheet.getCellDisplayValue("B1"));
        System.out.println("C1 = " + sheet.getCellDisplayValue("C1"));
        System.out.println("D1 = " + sheet.getCellDisplayValue("D1"));
        System.out.println("A2 = " + sheet.getCellDisplayValue("A2"));
        System.out.println("B2 = " + sheet.getCellDisplayValue("B2"));
        System.out.println("C2 = " + sheet.getCellDisplayValue("C2"));

        System.out.println();
        System.out.println(sheet.printSheet(3, 4));
        System.out.println();
    }

    private static void runResetScenario(SpreadsheetService sheet) {
        System.out.println("========== RESET SCENARIO ==========");

        sheet.resetCell("B1"); // now B1 becomes empty => treated as 0 in formulas

        System.out.println("After reset B1:");
        System.out.println("B1 = " + sheet.getCellDisplayValue("B1"));
        System.out.println("C1 = " + sheet.getCellDisplayValue("C1"));
        System.out.println("D1 = " + sheet.getCellDisplayValue("D1"));
        System.out.println("A2 = " + sheet.getCellDisplayValue("A2"));

        System.out.println();
        System.out.println(sheet.printSheet(3, 4));
        System.out.println();
    }

    private static void runCycleScenario(SpreadsheetService sheet) {
        System.out.println("========== CYCLE SCENARIO ==========");

        SpreadsheetService cycleSheet = new SpreadsheetService();
        cycleSheet.setCell("X1", "=Y1+1");
        cycleSheet.setCell("Y1", "=X1+1");

        try {
            System.out.println(cycleSheet.getCellDisplayValue("X1"));
            throw new AssertionError("Expected circular reference exception");
        } catch (CircularReferenceException ex) {
            System.out.println("Caught expected exception: " + ex.getMessage());
        }
    }
}
