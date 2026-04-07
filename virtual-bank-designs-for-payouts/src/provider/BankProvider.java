package provider;

import domain.BankResult;
import domain.PayoutRequest;

public interface BankProvider {
    String getName();
    BankResult transfer(PayoutRequest request);
}
