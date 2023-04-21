package com.raf.si.Banka2Backend.requests;

import com.raf.si.Banka2Backend.models.mariadb.PermissionName;
import lombok.Data;

import java.util.List;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private List<PermissionName> permissions;
    private String jobPosition;
    private boolean active;
    private String jmbg;
    private Double dailyLimit;
    private String phone;
}
