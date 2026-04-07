package provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VirtualBankPool {
    private final List<VirtualBank> banks;

    public VirtualBankPool(List<VirtualBank> banks) {
        this.banks = new ArrayList<>(banks);
    }

    public List<VirtualBank> getAllBanks() {
        return Collections.unmodifiableList(banks);
    }
}