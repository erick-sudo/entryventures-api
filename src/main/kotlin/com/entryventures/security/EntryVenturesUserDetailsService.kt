package com.entryventures.security

import com.entryventures.models.jpa.Authenticatable
import com.entryventures.repository.jpa.ClientRepository
import com.entryventures.repository.jpa.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class EntryVenturesUserDetailsService(
    private val userRepository: UserRepository,
    private val clientRepository: ClientRepository
) : UserDetailsService {
    override fun loadUserByUsername(identity: String): UserDetails {

        val (grantType, email) = identity.split("#")

        val optionalUser = userRepository.findByUserName(email)

        when(optionalUser.isPresent) {
            true -> return EntryVenturesUserDetails(optionalUser.get())
            else -> throw UsernameNotFoundException(String.format("User \'$email\' not found"))
        }
    }
}