package model;

public class AccountOperationsLog {
    private Long id;
    private AccountOperationType type;
    private Integer amount;
    private Long sourceAccountId;
    private Long targetAccountId;

    public AccountOperationsLog() {
    }

    public AccountOperationsLog(final AccountOperationType type, final Integer amount,
                                final Long sourceAccountId, final Long targetAccountId) {
        this.type = type;
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
    }

    public AccountOperationsLog(final Long id, final AccountOperationType type, final Integer amount,
                                final Long sourceAccountId, final Long targetAccountId) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.sourceAccountId = sourceAccountId;
        this.targetAccountId = targetAccountId;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public AccountOperationType getType() {
        return type;
    }

    public void setType(final AccountOperationType type) {
        this.type = type;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(final Integer amount) {
        this.amount = amount;
    }

    public Long getSourceAccountId() {
        return sourceAccountId;
    }

    public void setSourceAccountId(final Long sourceAccountId) {
        this.sourceAccountId = sourceAccountId;
    }

    public Long getTargetAccountId() {
        return targetAccountId;
    }

    public void setTargetAccountId(final Long targetAccountId) {
        this.targetAccountId = targetAccountId;
    }

    @Override
    public String toString() {
        String result = type +
                " в размере " + amount +
                " со счета " + sourceAccountId;
        if (targetAccountId != null) {
            result = result + " на счет " + targetAccountId;
        }
        return result;
    }
}
