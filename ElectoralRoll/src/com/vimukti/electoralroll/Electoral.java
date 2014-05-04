package com.vimukti.electoralroll;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Electoral implements Serializable {

	public static final String TABLE_NAME = "electoral";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SERIAL = "serial";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_VOTER_ID = "voter_id";
	public static final String COLUMN_RELATIVE = "relative";
	public static final String COLUMN_HOUSE_NO = "house_no";
	public static final String COLUMN_AGE = "age";
	public static final String COLUMN_GENDER = "gender";
	public static final String COLUMN_PART_NO = "partno";
	public static final String COLUMN_CONSTITUENCY = "constituency";

	private long id;

	private int serial;

	private String name;

	private String voterId;

	private String relative;

	private String houseNo;

	private int age;

	private String gender;

	private int partNo;

	private int constituency;

	/**
	 * @return the serial
	 */
	public int getSerial() {
		return serial;
	}

	/**
	 * @param serial
	 *            the serial to set
	 */
	public void setSerial(int serial) {
		this.serial = serial;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the voterId
	 */
	public String getVoterId() {
		return voterId;
	}

	/**
	 * @param voterId
	 *            the voterId to set
	 */
	public void setVoterId(String voterId) {
		this.voterId = voterId;
	}

	/**
	 * @return the relative
	 */
	public String getRelative() {
		return relative;
	}

	/**
	 * @param relative
	 *            the relative to set
	 */
	public void setRelative(String relative) {
		this.relative = relative;
	}

	/**
	 * @return the houseNo
	 */
	public String getHouseNo() {
		return houseNo;
	}

	/**
	 * @param houseNo
	 *            the houseNo to set
	 */
	public void setHouseNo(String houseNo) {
		this.houseNo = houseNo;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @param age
	 *            the age to set
	 */
	public void setAge(int age) {
		this.age = age;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the partNo
	 */
	public int getPartNo() {
		return partNo;
	}

	/**
	 * @param partNo
	 *            the partNo to set
	 */
	public void setPartNo(int partNo) {
		this.partNo = partNo;
	}

	/**
	 * @return the constituency
	 */
	public int getConstituency() {
		return constituency;
	}

	/**
	 * @param constituency
	 *            the constituency to set
	 */
	public void setConstituency(int constituency) {
		this.constituency = constituency;
	}

}
