package strategy;

import domain.PayoutRequest;
import provider.VirtualBank;

import java.util.List;

public interface BankSelectionStrategy {
    List<VirtualBank> selectCandidates(PayoutRequest request, List<VirtualBank> banks);
}
