package com.entryventures.aop

import com.entryventures.policies.loans.LoanPoliciesAspect
import com.entryventures.repository.jpa.LoanRepository
import com.entryventures.repository.jpa.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy

@Configuration
@EnableAspectJAutoProxy
class AOPConfig(
    private val loanRepository: LoanRepository,
    private val userRepository: UserRepository
) {

    @Bean
    fun loanPoliciesAspect(): LoanPoliciesAspect = LoanPoliciesAspect(
        loanRepository,
        userRepository
    )

}