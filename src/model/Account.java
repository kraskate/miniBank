package model;

public class Account {
    private Long id;
    private Integer amount;
    private Long userId;

    public Account() {
    }

    public Account(Long id, Integer amount, Long userId) {
        this.id = id;
        this.amount = amount;
        this.userId = userId;
    }

    public Account(Integer amount, Long userId) {
        this.amount = amount;
        this.userId = userId;
    }

    public  boolean isNew() {
        return id == null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
