
import dto.*;
import factory.SplitCalculatorFactory;
import model.*;
import repository.*;
import repository.impl.*;
import service.*;
import util.BalancePrinter;
import validator.ExpenseRequestValidator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class Main {

    private final UserRepository userRepository = new InMemoryUserRepository();
    private final GroupRepository groupRepository = new InMemoryGroupRepository();
    private final ExpenseRepository expenseRepository = new InMemoryExpenseRepository();
    private final SettlementRepository settlementRepository = new InMemorySettlementRepository();
    private final BalanceLedgerRepository balanceLedgerRepository = new InMemoryBalanceLedgerRepository();

    private final LockManager lockManager = new LockManager();
    private final SplitCalculatorFactory splitCalculatorFactory = new SplitCalculatorFactory();
    private final ExpenseRequestValidator expenseRequestValidator = new ExpenseRequestValidator();

    private final UserService userService = new UserService(userRepository);
    private final GroupService groupService = new GroupService(groupRepository, userRepository);
    private final ExpenseService expenseService = new ExpenseService(
            expenseRepository,
            userRepository,
            groupRepository,
            balanceLedgerRepository,
            splitCalculatorFactory,
            expenseRequestValidator,
            lockManager
    );
    private final SettlementService settlementService = new SettlementService(
            settlementRepository,
            userRepository,
            groupRepository,
            balanceLedgerRepository,
            lockManager
    );
    private final BalanceQueryService balanceQueryService = new BalanceQueryService(balanceLedgerRepository);
    private final BalancePrinter balancePrinter = new BalancePrinter(userRepository);

    public static void main(String[] args) throws Exception {
        Main runner = new Main();
        runner.run();
    }

    private void run() throws Exception {
        seedUsers();
        seedGroups();

        testEqualExpenseInGroup();
        testExactExpenseInGroup();
        testPercentExpenseInGroup();
        testDirectExpense();
        testShowBalancesForUser();
        testShowBalancesForGroup();
        testOutstandingBetweenTwoUsers();
        testPartialSettlement();
        testFullSettlement();
        testInvalidOverSettlement();
        testInvalidGroupMemberExpense();
        testConcurrentExpenses();
        testFinalAllBalances();
    }

    private void seedUsers() {
        printHeader("SEED USERS");

        userService.createUser(new CreateUserRequest("u1", "Lava", "lava@gmail.com", "9999999991"));
        userService.createUser(new CreateUserRequest("u2", "Sachin", "sachin@gmail.com", "9999999992"));
        userService.createUser(new CreateUserRequest("u3", "ntr", "ntr@gmail.com", "9999999993"));
        userService.createUser(new CreateUserRequest("u4", "Rohit", "rohit@gmail.com", "9999999994"));
        userService.createUser(new CreateUserRequest("u5", "Sara", "sara@gmail.com", "9999999995"));

        System.out.println("Users created: " + userService.getAllUsers().size());
    }

    private void seedGroups() {
        printHeader("SEED GROUPS");

        groupService.createGroup(new CreateGroupRequest(
                "g1",
                "Goa Trip",
                Set.of("u1", "u2", "u3")
        ));

        groupService.createGroup(new CreateGroupRequest(
                "g2",
                "Flatmates",
                Set.of("u2", "u3", "u4")
        ));

        groupService.addMember(new AddMemberToGroupRequest("g1", "u4"));

        System.out.println("Groups created: " + groupService.getAllGroups().size());
    }

    private void testEqualExpenseInGroup() {
        printHeader("TEST 1: EQUAL EXPENSE IN GROUP");

        expenseService.addExpense(new AddExpenseRequest(
                "e1",
                "Goa Hotel",
                new BigDecimal("3000.00"),
                "u1",
                "g1",
                SplitType.EQUAL,
                List.of(
                        new SplitInput("u1"),
                        new SplitInput("u2"),
                        new SplitInput("u3")
                )
        ));

        balancePrinter.printBalances(
                "Balances in g1 after equal expense",
                balanceQueryService.getBalancesForGroup("g1")
        );
    }

    private void testExactExpenseInGroup() {
        printHeader("TEST 2: EXACT EXPENSE IN GROUP");

        expenseService.addExpense(new AddExpenseRequest(
                "e2",
                "Goa Cab",
                new BigDecimal("900.00"),
                "u2",
                "g1",
                SplitType.EXACT,
                List.of(
                        new SplitInput("u1", new BigDecimal("300.00")),
                        new SplitInput("u2", new BigDecimal("200.00")),
                        new SplitInput("u3", new BigDecimal("400.00"))
                )
        ));

        balancePrinter.printBalances(
                "Balances in g1 after exact expense",
                balanceQueryService.getBalancesForGroup("g1")
        );
    }

    private void testPercentExpenseInGroup() {
        printHeader("TEST 3: PERCENT EXPENSE IN GROUP");

        expenseService.addExpense(new AddExpenseRequest(
                "e3",
                "Goa Dinner",
                new BigDecimal("1000.00"),
                "u3",
                "g1",
                SplitType.PERCENT,
                List.of(
                        new SplitInput("u1", new BigDecimal("20")),
                        new SplitInput("u2", new BigDecimal("30")),
                        new SplitInput("u3", new BigDecimal("50"))
                )
        ));

        balancePrinter.printBalances(
                "Balances in g1 after percent expense",
                balanceQueryService.getBalancesForGroup("g1")
        );
    }

    private void testDirectExpense() {
        printHeader("TEST 4: DIRECT / NON-GROUP EXPENSE");

        expenseService.addExpense(new AddExpenseRequest(
                "e4",
                "Coffee Bill",
                new BigDecimal("600.00"),
                "u4",
                null,
                SplitType.EQUAL,
                List.of(
                        new SplitInput("u4"),
                        new SplitInput("u5")
                )
        ));

        balancePrinter.printBalances(
                "All balances after direct expense",
                balanceQueryService.getAllBalances()
        );
    }

    private void testShowBalancesForUser() {
        printHeader("TEST 5: BALANCES FOR USER u2");

        balancePrinter.printBalances(
                "Balances involving u2",
                balanceQueryService.getBalancesForUser("u2")
        );
    }

    private void testShowBalancesForGroup() {
        printHeader("TEST 6: BALANCES FOR GROUP g1");

        balancePrinter.printBalances(
                "Balances inside group g1",
                balanceQueryService.getBalancesForGroup("g1")
        );
    }

    private void testOutstandingBetweenTwoUsers() {
        printHeader("TEST 7: OUTSTANDING BETWEEN TWO USERS");

        Balance balance = balanceQueryService.getBalanceBetween("g1", "u2", "u1");
        balancePrinter.printBalance("Outstanding between u2 and u1 in g1", balance);
    }

    private void testPartialSettlement() {
        printHeader("TEST 8: PARTIAL SETTLEMENT");

        Balance before = balanceQueryService.getBalanceBetween("g1", "u2", "u1");
        balancePrinter.printBalance("Before settlement", before);

        settlementService.settleUp(new SettleUpRequest(
                "s1",
                "u2",
                "u1",
                new BigDecimal("200.00"),
                "g1"
        ));

        Balance after = balanceQueryService.getBalanceBetween("g1", "u2", "u1");
        balancePrinter.printBalance("After partial settlement", after);
    }

    private void testFullSettlement() {
        printHeader("TEST 9: FULL SETTLEMENT");

        Balance balance = balanceQueryService.getBalanceBetween(null, "u5", "u4");
        balancePrinter.printBalance("Before full settlement (direct)", balance);

        settlementService.settleUp(new SettleUpRequest(
                "s2",
                "u5",
                "u4",
                new BigDecimal("300.00"),
                null
        ));

        Balance after = balanceQueryService.getBalanceBetween(null, "u5", "u4");
        balancePrinter.printBalance("After full settlement (direct)", after);
    }

    private void testInvalidOverSettlement() {
        printHeader("TEST 10: INVALID OVER-SETTLEMENT");

        try {
            settlementService.settleUp(new SettleUpRequest(
                    "s3",
                    "u2",
                    "u1",
                    new BigDecimal("99999.00"),
                    "g1"
            ));
            System.out.println("FAILED: Expected over-settlement to be rejected");
        } catch (Exception ex) {
            System.out.println("PASSED: " + ex.getMessage());
        }
    }

    private void testInvalidGroupMemberExpense() {
        printHeader("TEST 11: INVALID GROUP MEMBER EXPENSE");

        try {
            expenseService.addExpense(new AddExpenseRequest(
                    "e5",
                    "Invalid Expense",
                    new BigDecimal("500.00"),
                    "u1",
                    "g2",
                    SplitType.EQUAL,
                    List.of(
                            new SplitInput("u1"),
                            new SplitInput("u2")
                    )
            ));
            System.out.println("FAILED: Expected invalid group membership to be rejected");
        } catch (Exception ex) {
            System.out.println("PASSED: " + ex.getMessage());
        }
    }

    private void testConcurrentExpenses() throws Exception {
        printHeader("TEST 12: CONCURRENT EXPENSES");

        int threads = 5;
        CountDownLatch ready = new CountDownLatch(threads);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threads);

        for (int i = 1; i <= threads; i++) {
            final int index = i;
            Thread thread = new Thread(() -> {
                ready.countDown();
                try {
                    start.await();

                    expenseService.addExpense(new AddExpenseRequest(
                            "ce" + index,
                            "Concurrent Dinner " + index,
                            new BigDecimal("300.00"),
                            "u1",
                            "g1",
                            SplitType.EQUAL,
                            List.of(
                                    new SplitInput("u1"),
                                    new SplitInput("u2"),
                                    new SplitInput("u3")
                            )
                    ));
                } catch (Exception ex) {
                    System.out.println("Concurrent thread failure: " + ex.getMessage());
                } finally {
                    done.countDown();
                }
            });
            thread.start();
        }

        ready.await();
        start.countDown();
        done.await();

        balancePrinter.printBalances(
                "Balances in g1 after concurrent expenses",
                balanceQueryService.getBalancesForGroup("g1")
        );
    }

    private void testFinalAllBalances() {
        printHeader("TEST 13: FINAL ALL BALANCES");

        balancePrinter.printBalances(
                "All balances across all scopes",
                balanceQueryService.getAllBalances()
        );
    }

    private void printHeader(String title) {
        System.out.println();
        System.out.println();
        System.out.println("##################################################");
        System.out.println(title);
        System.out.println("##################################################");
    }
}