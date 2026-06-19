package com.dozycoffee.domain.admin;

import java.util.Objects;
import java.util.regex.Pattern;

public class AdminProfile {

    private AdminId adminId;
    private String employeeNo;
    private String name;
    private String phone;
    private String email;

    private AdminProfile(
            AdminId adminId,
            String employeeNo,
            String name,
            String phone,
            String email
    ) {
        setAdminId(adminId);
        setEmployeeNo(employeeNo);
        setName(name);
        setPhone(phone);
        setEmail(email);
    }

    public static AdminProfile of(AdminId adminId, String employeeNo, String name, String phone, String email) {
        return new AdminProfile(adminId, employeeNo, name, phone, email);
    }

    public static AdminProfile create(AdminId adminId, String employeeNo, String name, String phone, String email) {
        return new AdminProfile(adminId, employeeNo, name, phone, email);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof AdminProfile profile)) return false;
        return adminId.equals(profile.adminId);
    }

    @Override
    public int hashCode() {
        return adminId.hashCode();
    }

    public AdminId getAdminId() {
        return adminId;
    }

    private void setAdminId(AdminId adminId) {
        if (adminId == null) throw new AdminException("adminId cannot be null");
        this.adminId = adminId;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    private static final Pattern EMPLOYEE_NO_PATTERN =
            Pattern.compile("^[A-Z0-9]{1,20}$");

    private static void validateEmployeeNo(String employeeNo) {
        if (employeeNo == null || !EMPLOYEE_NO_PATTERN.matcher(employeeNo).matches()) {
            throw new AdminException("Invalid employee number");
        }
    }

    private void setEmployeeNo(String employeeNo) {
        validateEmployeeNo(employeeNo);
        this.employeeNo = employeeNo;
    }

    public String getName() {
        return name;
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

    private void setName(String name) {
        validateName(name);
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+[1-9]\\d{6,14}$");

    private static void validatePhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new AdminException("Invalid phone number");
        }
    }

    private void setPhone(String phone) {
        validatePhone(phone);
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$");

    private static void validateEmail(String email) {
        if (email == null || email.length() > 255 || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new AdminException("Invalid email");
        }
    }

    private void setEmail(String email) {
        validateEmail(email);
        this.email = email;
    }

    public void changePhoneNumber(String newPhone) {
        setPhone(newPhone);
    }


    public void changeEmail(String newEmail) {
        setEmail(newEmail);
    }

}
