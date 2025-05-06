package com.example.splitwallet.utils;

import com.example.splitwallet.models.User;
import com.example.splitwallet.models.UserResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataUtils {
    public static Map<String, User> convertMembersToMap(List<UserResponse> members) {
        Map<String, User> membersMap = new HashMap<>();
        if (members != null) {
            for (User user : members) {
                membersMap.put(user.getId(), user);
            }
        }
        return membersMap;
    }
}