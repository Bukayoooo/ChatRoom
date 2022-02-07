package com.tencent.server.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserServiceMemoryImpl implements UserService {
    private Map<String, String> allUserMap = new ConcurrentHashMap<>();

    {
        allUserMap.put("Bukayo", "123");
        allUserMap.put("Smith", "123");
        allUserMap.put("Tomiyasu", "123");
        allUserMap.put("Kerrin", "123");
        allUserMap.put("Martin", "123");
    }

    @Override
    public boolean login(String username, String password) {
        String pass = allUserMap.get(username);
        if (pass == null) {
            return false;
        }
        return pass.equals(password);
    }
}
