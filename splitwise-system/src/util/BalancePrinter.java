package util;

import model.Balance;
import model.User;
import repository.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class BalancePrinter {
    private final UserRepository userRepository;

    public BalancePrinter(UserRepository userRepository) {
        this.userRepository = Objects.requireNonNull(userRepository, "UserRepository cannot be null");
    }

    public void printBalances(String title, List<Balance> balances) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println(title);
        System.out.println("==================================================");

        if (balances == null || balances.isEmpty()) {
            System.out.println("No balances");
            return;
        }

        balances.stream()
                .sorted(Comparator
                        .comparing(Balance::groupId, Comparator.nullsFirst(String::compareTo))
                        .thenComparing(Balance::debtorUserId)
                        .thenComparing(Balance::creditorUserId))
                .forEach(balance -> System.out.println(format(balance)));
    }

    public void printBalance(String title, Balance balance) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println(title);
        System.out.println("==================================================");

        if (balance == null) {
            System.out.println("No balance");
            return;
        }

        System.out.println(format(balance));
    }

    private String format(Balance balance) {
        String debtorName = getUserName(balance.debtorUserId());
        String creditorName = getUserName(balance.creditorUserId());

        if (balance.groupId() == null) {
            return debtorName + " owes " + creditorName + ": " + balance.amount();
        }

        return "[Group: " + balance.groupId() + "] "
                + debtorName + " owes " + creditorName + ": " + balance.amount();
    }

    private String getUserName(String userId) {
        return userRepository.findById(userId)
                .map(User::name)
                .orElse(userId);
    }
}
