package com.dozycoffee.domain.admin;

import java.util.Objects;
import java.util.regex.Pattern;

public class AdminProfile {

    private long adminId;
    private String employeeNo;
    private String name;
    private String phone;
    private String email;

    private AdminProfile(
            Long adminId,
            String employeeNo,
            String name,
            String phone,
            String email
    ) {
        validateRequiredFields(employeeNo, name, phone, email);
        this.adminId = adminId;
        this.employeeNo = employeeNo;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    public static AdminProfile of(long adminId, String employeeNo, String name, String phone, String email) {
        return new AdminProfile(adminId, employeeNo, name, phone, email);
    }

    public static AdminProfile create(long adminId, String employeeNo, String name, String phone, String email) {
        validateEmployeeNo(employeeNo);
        validateName(name);
        validatePhone(phone);
        validateEmail(email);
        return new AdminProfile(adminId, employeeNo, name, phone, email);
    }

    private static void validateRequiredFields(String employeeNo, String name, String phone, String email) {
        if (employeeNo == null) throw new AdminException("employeeNo cannot be null");
        if (name == null) throw new AdminException("name cannot be null");
        if (phone == null) throw new AdminException("phone cannot be null");
        if (email == null) throw new AdminException("email cannot be null");
    }

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+[1-9]\\d{6,14}$");

    private static void validatePhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new AdminException("Invalid phone number");
        }
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    private static void validateEmail(String email) {
        if (email == null || email.length() > 255 || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new AdminException("Invalid email");
        }
    }

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^[a-zA-Z가-힣 ]{1,50}$");

    private static void validateName(String name) {
        if (name == null || !NAME_PATTERN.matcher(name).matches()) {
            throw new AdminException("Invalid admin name");
        }
        if (!name.equals(name.strip())) {
            throw new AdminException("Admin name must not have leading or trailing whitespace");
        }
    }

    private static final Pattern EMPLOYEE_NO_PATTERN =
            Pattern.compile("^[A-Z0-9]{1,20}$");

    private static void validateEmployeeNo(String employeeNo) {
        if (employeeNo == null || !EMPLOYEE_NO_PATTERN.matcher(employeeNo).matches()) {
            throw new AdminException("Invalid employee number");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AdminProfile profile)) return false;
        return adminId == profile.adminId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(adminId);
    }

    public long getAdminId() {
        return adminId;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public void updatePhoneNumber(String newPhone) {
        validatePhone(newPhone);
        this.phone = newPhone;
    }

    public void updateEmail(String newEmail) {
        validateEmail(newEmail);
        this.email = newEmail;
    }

}
