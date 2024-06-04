package com.entryventures.repository.jpa

import com.entryventures.models.jpa.Salary
import org.springframework.data.jpa.repository.JpaRepository

interface SalaryRepository: JpaRepository<Salary, String> {
}