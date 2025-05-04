package com.example.splitwallet.models;


import java.util.List;

import lombok.Getter;

@Getter
public class GroupBalancesResponse {
    private String groupName;
    private List<Balance> balances;
}