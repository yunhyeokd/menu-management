package com.dozycoffee.admin.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public class AdminProfile {

    private final String employeeNo;
    private final String name;
    private final String phone;
    private final String email;

    private static final Pattern EMPLOYEE_NO_PATTERN = Pattern.compile("^[A-Z0-9]{1,20}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z가-힣 ]{1,50}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+[1-9]\\d{6,14}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    private AdminProfile(String employeeNo, String name, String phone, String email) {
        if (employeeNo == null || !EMPLOYEE_NO_PATTERN.matcher(employeeNo).matches())
            throw new AdminException("Invalid employee number");
        if (name == null || !NAME_PATTERN.matcher(name).matches())
            throw new AdminException("Invalid admin name");
        if (!name.equals(name.strip()))
            throw new AdminException("Admin name must not have leading or trailing whitespace");
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches())
            throw new AdminException("Invalid phone number");
        if (email == null || email.length() > 255 || !EMAIL_PATTERN.matcher(email).matches())
            throw new AdminException("Invalid email");
        this.employeeNo = employeeNo;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public static AdminProfile of(String employeeNo, String name, String phone, String email) {
        return new AdminProfile(employeeNo, name, phone, email);
    }

    public static AdminProfile create(String employeeNo, String name, String phone, String email) {
        return new AdminProfile(employeeNo, name, phone, email);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AdminProfile profile)) return false;
        return Objects.equals(employeeNo, profile.employeeNo);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(employeeNo);
    }

    public String getEmployeeNo() { return employeeNo; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
}
