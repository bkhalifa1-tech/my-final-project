package com.example.myfinalproject;

public class EmergencyContact{
    private long id,userId;
    private String contactName,phoneNumber;

    public EmergencyContact(){}

    public EmergencyContact(long userId,String contactName,String phoneNumber){
        this.userId=userId;
        this.contactName=contactName;
        this.phoneNumber=phoneNumber;
    }

    public EmergencyContact(long id,long userId,String contactName,String phoneNumber){
        this.id=id;
        this.userId=userId;
        this.contactName=contactName;
        this.phoneNumber=phoneNumber;
    }

    public long getId(){return id;}
    public void setId(long id){this.id=id;}
    public long getUserId(){return userId;}
    public void setUserId(long userId){this.userId=userId;}
    public String getContactName(){return contactName;}
    public void setContactName(String contactName){this.contactName=contactName;}
    public String getPhoneNumber(){return phoneNumber;}
    public void setPhoneNumber(String phoneNumber){this.phoneNumber=phoneNumber;}
}
