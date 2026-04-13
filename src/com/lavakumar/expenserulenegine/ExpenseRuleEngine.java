package com.lavakumar.expenserulenegine;

import com.lavakumar.expenserulenegine.model.*;
import com.lavakumar.expenserulenegine.repository.ExpenseRepository;
import com.lavakumar.expenserulenegine.rule.*;
import com.lavakumar.expenserulenegine.service.CostExplorerService;

import java.math.BigDecimal;
import java.time.Instant;

public class ExpenseRuleEngine {

    public static void main(String[] args) {
        ExpenseRepository expenseRepository = new ExpenseRepository();
        CostExplorerService costExplorerService = new CostExplorerService(expenseRepository);

        registerSampleRules(costExplorerService);
        runSampleScenario(costExplorerService);
    }

    private static void registerSampleRules(CostExplorerService service) {
        service.addRule(new CategoryNotAllowedRule(
                "R1",
                "Entertainment expenses are not reimbursable",
                ExpenseCategory.ENTERTAINMENT
        ));

        service.addRule(new MaxAmountPerExpenseRule(
                "R2",
                "Each meal expense must be <= 100.00",
                ExpenseCategory.MEAL,
                new BigDecimal("100.00")
        ));

        service.addRule(new TripTotalLimitRule(
                "R3",
                "Total approved spend per trip must be <= 1000.00",
                new BigDecimal("1000.00")
        ));

        service.addRule(new TripCategoryTotalLimitRule(
                "R4",
                "Total approved meal spend per trip must be <= 150.00",
                ExpenseCategory.MEAL,
                new BigDecimal("150.00")
        ));
    }

    private static void runSampleScenario(CostExplorerService service) {
        Instant now = Instant.parse("2026-04-13T09:00:00Z");

        printSubmission(service.submitExpense(new ExpenseItem(
                "E1", "EMP1", "TRIP1", ExpenseCategory.MEAL,
                "Breakfast", new BigDecimal("60.00"), now
        )));

        printSubmission(service.submitExpense(new ExpenseItem(
                "E2", "EMP1", "TRIP1", ExpenseCategory.HOTEL,
                "Hotel stay", new BigDecimal("300.00"), now.plusSeconds(60)
        )));

        printSubmission(service.submitExpense(new ExpenseItem(
                "E3", "EMP1", "TRIP1", ExpenseCategory.ENTERTAINMENT,
                "Movie ticket", new BigDecimal("40.00"), now.plusSeconds(120)
        )));

        printSubmission(service.submitExpense(new ExpenseItem(
                "E4", "EMP1", "TRIP1", ExpenseCategory.MEAL,
                "Dinner", new BigDecimal("50.00"), now.plusSeconds(180)
        )));

        printSubmission(service.submitExpense(new ExpenseItem(
                "E5", "EMP1", "TRIP1", ExpenseCategory.MEAL,
                "Late snack", new BigDecimal("45.00"), now.plusSeconds(240)
        )));

        printSubmission(service.submitExpense(new ExpenseItem(
                "E6", "EMP1", "TRIP1", ExpenseCategory.FLIGHT,
                "Return flight", new BigDecimal("700.00"), now.plusSeconds(300)
        )));

        printSubmission(service.submitExpense(new ExpenseItem(
                "E7", "EMP2", "TRIP2", ExpenseCategory.TAXI,
                "Airport cab", new BigDecimal("80.00"), now.plusSeconds(360)
        )));

        System.out.println();
        System.out.println("========== COST EXPLORER QUERIES ==========");
        System.out.println("Approved total by employee EMP1     : " + service.getApprovedTotalByEmployee("EMP1"));
        System.out.println("Approved total by employee EMP2     : " + service.getApprovedTotalByEmployee("EMP2"));
        System.out.println("Approved total by trip TRIP1        : " + service.getApprovedTotalByTrip("TRIP1"));
        System.out.println("Approved meal total by trip TRIP1   : " + service.getApprovedTotalByTripAndCategory("TRIP1", ExpenseCategory.MEAL));
        System.out.println("Approved total for category HOTEL   : " + service.getApprovedTotalByCategory(ExpenseCategory.HOTEL));
        System.out.println();

        System.out.println("========== TRIP SUMMARY ==========");
        System.out.println(service.getTripSummary("TRIP1"));
        System.out.println();

        System.out.println("========== EMPLOYEE SUMMARY ==========");
        System.out.println(service.getEmployeeSummary("EMP1"));
        System.out.println();

        System.out.println("========== ALL SUBMISSIONS ==========");
        for (SubmittedExpense submittedExpense : service.getAllSubmittedExpenses()) {
            System.out.println(submittedExpense);
        }
    }

    private static void printSubmission(SubmissionResult result) {
        System.out.println("Expense " + result.expenseId() + " -> " + result.status());
        if (!result.validationReport().approved()) {
            for (RuleViolation violation : result.validationReport().violations()) {
                System.out.println("   violation: " + violation.ruleId() + " | " + violation.message());
            }
        }
    }
}
