package com.example;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
public class Employee
{
	@NonNull
	private final String empName;
	private final int empId;
	
	public Employee(@NonNull String empName, int empId)
	{
		this.empName = empName;
		this.empId = empId;
	}
}
