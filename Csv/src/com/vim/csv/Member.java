package com.vim.csv;

public class Member {
	public int num;
	public String id = "";
	public String name = "";
	public String relativeName = "";
	public String hus = "";
	public String gender;
	public int age;

	public Member() {
	}

	@Override
	public String toString() {
		String result = num + "," + id + "," + name + "," + relativeName + ","
				+ hus + "," + age + "," + gender;
		result = result.replaceAll(" +", " ");
		return result;
	}
}
