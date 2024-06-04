package com.entryventures.repository.jpa

import com.entryventures.models.jpa.Employee
import org.springframework.data.jpa.repository.JpaRepository

interface EmployeeRepository: JpaRepository<Employee, String> {
}