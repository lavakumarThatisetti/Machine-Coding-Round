package domain;

public class PayoutRequest {
    private final String merchantId;
    private final String beneficiaryId;
    private final long amount;
    private final String currency;
    private final String idempotencyKey;

    public PayoutRequest(String merchantId, String beneficiaryId, long amount, String currency, String idempotencyKey) {
        this.merchantId = merchantId;
        this.beneficiaryId = beneficiaryId;
        this.amount = amount;
        this.currency = currency;
        this.idempotencyKey = idempotencyKey;
    }

    public String merchantId() { return merchantId; }
    public String beneficiaryId() { return beneficiaryId; }
    public long amount() { return amount; }
    public String currency() { return currency; }
    public String idempotencyKey() { return idempotencyKey; }
}
