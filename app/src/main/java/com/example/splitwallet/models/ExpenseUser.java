package com.example.splitwallet.models;

import java.math.BigDecimal;

public class ExpenseUser {
    private Long id; // Уникальный идентификатор связи

    private User user; // Пользователь, который должен оплатить часть расхода

    private Expense expense; // Расход, к которому относится эта связь

    private BigDecimal amount; // Сколько монет должен пользователь
}