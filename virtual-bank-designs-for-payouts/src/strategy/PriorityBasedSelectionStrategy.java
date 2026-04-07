package strategy;

import domain.PayoutRequest;
import provider.VirtualBank;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/*
Later we can add:

least loaded strategy
weighted strategy
merchant-specific strategy
 */
public class PriorityBasedSelectionStrategy implements BankSelectionStrategy {

    private final List<String> priorityOrder;

    public PriorityBasedSelectionStrategy(List<String> priorityOrder) {
        this.priorityOrder = priorityOrder;
    }

    @Override
    public List<VirtualBank> selectCandidates(PayoutRequest request, List<VirtualBank> banks) {
        List<VirtualBank> result = new ArrayList<>(banks);
        result.sort(Comparator.comparingInt(bank -> indexOf(bank.name())));
        return result;
    }

    private int indexOf(String bankName) {
        int index = priorityOrder.indexOf(bankName);
        return index == -1 ? Integer.MAX_VALUE : index;
    }
}
