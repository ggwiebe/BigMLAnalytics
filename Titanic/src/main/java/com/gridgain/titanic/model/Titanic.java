package com.gridgain.titanic.model;

import java.io.Serializable;

/**
 * Titanic definition.
 * 
 * This file was generated by Ignite Web Console (03/23/2020, 18:23)
 **/
public class Titanic implements Serializable {
    /** */
    private static final long serialVersionUID = 0L;

    /** Value for passengerid. */
    private Integer passengerid;

    /** Value for survived. */
    private Integer survived;

    /** Value for pclass. */
    private Integer pclass;

    /** Value for name. */
    private String name;

    /** Value for sex. */
    private String sex;

    /** Value for age. */
    private Double age;

    /** Value for sibsp. */
    private Integer sibsp;

    /** Value for parch. */
    private Integer parch;

    /** Value for ticket. */
    private String ticket;

    /** Value for fare. */
    private Double fare;

    /** Value for cabin. */
    private String cabin;

    /** Value for embarked. */
    private String embarked;

    /** Empty constructor. **/
    public Titanic() {
        // No-op.
    }

    /** Full constructor. **/
    public Titanic(
    	Integer survived,
    	Integer pclass,
        String name,
        String sex,
        Double age,
        Integer sibsp,
        Integer parch,
        String ticket,
        Double fare,
        String cabin,
        String embarked) {
        this.passengerid = passengerid;
        this.survived = survived;
        this.pclass = pclass;
        this.name = name;
        this.sex = sex;
        this.age = age;
        this.sibsp = sibsp;
        this.parch = parch;
        this.ticket = ticket;
        this.fare = fare;
        this.cabin = cabin;
        this.embarked = embarked;
    }

    /**
     * Gets survived
     * 
     * @return Value for survived.
     **/
    public Integer getSurvived() {
        return survived;
    }

    /**
     * Sets survived
     * 
     * @param survived New value for survived.
     **/
    public void setSurvived(Integer survived) {
        this.survived = survived;
    }

    /**
     * Gets pclass
     * 
     * @return Value for pclass.
     **/
    public Integer getPclass() {
        return pclass;
    }

    /**
     * Sets pclass
     * 
     * @param pclass New value for pclass.
     **/
    public void setPclass(Integer pclass) {
        this.pclass = pclass;
    }

    /**
     * Gets name
     * 
     * @return Value for name.
     **/
    public String getName() {
        return name;
    }

    /**
     * Sets name
     * 
     * @param name New value for name.
     **/
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets sex
     * 
     * @return Value for sex.
     **/
    public String getSex() {
        return sex;
    }

    /**
     * Sets sex
     * 
     * @param sex New value for sex.
     **/
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * Gets age
     * 
     * @return Value for age.
     **/
    public Double getAge() {
        return age;
    }

    /**
     * Sets age
     * 
     * @param age New value for age.
     **/
    public void setAge(Double age) {
        this.age = age;
    }

    /**
     * Gets sibsp
     * 
     * @return Value for sibsp.
     **/
    public Integer getSibsp() {
        return sibsp;
    }

    /**
     * Sets sibsp
     * 
     * @param sibsp New value for sibsp.
     **/
    public void setSibsp(Integer sibsp) {
        this.sibsp = sibsp;
    }

    /**
     * Gets parch
     * 
     * @return Value for parch.
     **/
    public Integer getParch() {
        return parch;
    }

    /**
     * Sets parch
     * 
     * @param parch New value for parch.
     **/
    public void setParch(Integer parch) {
        this.parch = parch;
    }

    /**
     * Gets ticket
     * 
     * @return Value for ticket.
     **/
    public String getTicket() {
        return ticket;
    }

    /**
     * Sets ticket
     * 
     * @param ticket New value for ticket.
     **/
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    /**
     * Gets fare
     * 
     * @return Value for fare.
     **/
    public Double getFare() {
        return fare;
    }

    /**
     * Sets fare
     * 
     * @param fare New value for fare.
     **/
    public void setFare(Double fare) {
        this.fare = fare;
    }

    /**
     * Gets cabin
     * 
     * @return Value for cabin.
     **/
    public String getCabin() {
        return cabin;
    }

    /**
     * Sets cabin
     * 
     * @param cabin New value for cabin.
     **/
    public void setCabin(String cabin) {
        this.cabin = cabin;
    }

    /**
     * Gets embarked
     * 
     * @return Value for embarked.
     **/
    public String getEmbarked() {
        return embarked;
    }

    /**
     * Sets embarked
     * 
     * @param embarked New value for embarked.
     **/
    public void setEmbarked(String embarked) {
        this.embarked = embarked;
    }

    /** {@inheritDoc} **/
    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        
        if (!(o instanceof Titanic))
            return false;
        
        Titanic that = (Titanic)o;

        if (passengerid != null ? !passengerid.equals(that.passengerid) : that.passengerid != null)
            return false;
        

        if (survived != null ? !survived.equals(that.survived) : that.survived != null)
            return false;
        

        if (pclass != null ? !pclass.equals(that.pclass) : that.pclass != null)
            return false;
        

        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        

        if (sex != null ? !sex.equals(that.sex) : that.sex != null)
            return false;
        

        if (age != null ? !age.equals(that.age) : that.age != null)
            return false;
        

        if (sibsp != null ? !sibsp.equals(that.sibsp) : that.sibsp != null)
            return false;
        

        if (parch != null ? !parch.equals(that.parch) : that.parch != null)
            return false;
        

        if (ticket != null ? !ticket.equals(that.ticket) : that.ticket != null)
            return false;
        

        if (fare != null ? !fare.equals(that.fare) : that.fare != null)
            return false;
        

        if (cabin != null ? !cabin.equals(that.cabin) : that.cabin != null)
            return false;
        

        if (embarked != null ? !embarked.equals(that.embarked) : that.embarked != null)
            return false;
        
        return true;
    }

    /** {@inheritDoc} **/
    @Override public int hashCode() {
        int res = passengerid != null ? passengerid.hashCode() : 0;

        res = 31 * res + (survived != null ? survived.hashCode() : 0);

        res = 31 * res + (pclass != null ? pclass.hashCode() : 0);

        res = 31 * res + (name != null ? name.hashCode() : 0);

        res = 31 * res + (sex != null ? sex.hashCode() : 0);

        res = 31 * res + (age != null ? age.hashCode() : 0);

        res = 31 * res + (sibsp != null ? sibsp.hashCode() : 0);

        res = 31 * res + (parch != null ? parch.hashCode() : 0);

        res = 31 * res + (ticket != null ? ticket.hashCode() : 0);

        res = 31 * res + (fare != null ? fare.hashCode() : 0);

        res = 31 * res + (cabin != null ? cabin.hashCode() : 0);

        res = 31 * res + (embarked != null ? embarked.hashCode() : 0);

        return res;
    }

    /** {@inheritDoc} **/
    @Override public String toString() {
        return "Titanic [" + 
            "passengerid=" + passengerid + ", " + 
            "survived=" + survived + ", " + 
            "pclass=" + pclass + ", " + 
            "name=" + name + ", " + 
            "sex=" + sex + ", " + 
            "age=" + age + ", " + 
            "sibsp=" + sibsp + ", " + 
            "parch=" + parch + ", " + 
            "ticket=" + ticket + ", " + 
            "fare=" + fare + ", " + 
            "cabin=" + cabin + ", " + 
            "embarked=" + embarked +
        "]";
    }
}