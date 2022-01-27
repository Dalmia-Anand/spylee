package com.spylee.model;

public class Guardian {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String relation;
    private String personId;
    private String personId_email;
    private String personId_phone;

    public Guardian() {
    }

    public Guardian(String id, String name, String email, String phone, String relation, String personId, String personId_email, String personId_phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.relation = relation;
        this.personId = personId;
        this.personId_email = personId_email;
        this.personId_phone = personId_phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getPersonId_email() {
        return personId_email;
    }

    public void setPersonId_email(String personId_email) {
        this.personId_email = personId_email;
    }

    public String getPersonId_phone() {
        return personId_phone;
    }

    public void setPersonId_phone(String personId_phone) {
        this.personId_phone = personId_phone;
    }
}
