package com.entryventures.services

import com.entryventures.models.jpa.Group
import com.entryventures.models.jpa.Role
import com.entryventures.models.jpa.User
import com.entryventures.repository.jpa.GroupRepository
import com.entryventures.repository.jpa.RoleRepository
import com.entryventures.repository.jpa.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service

@Service
class SuperUserService(
    private val userRepository: UserRepository,
    private val groupRepository: GroupRepository,
    private val roleRepository: RoleRepository,
    private val controllerService: ControllerService
) {

    @Bean
    fun initializeSuperUser() {
        var superUser = userRepository.findByUserName("johndoe").let { when(it.isPresent) {
            true -> it.get()
            else -> null
        } }

        val roles = listOf(
                Role(name = "ROLE_LOAN_OFFICER", description = "Asses and approve loan applications"),
                Role(name = "ROLE_DISBURSEMENT_OFFICER", description = "Authorize to disburse approved loans"),
                Role(name = "ROLE_COLLECTION_OFFICER", description = "Manage loan collections based on schedule"),
                Role(name = "ROLE_SYSTEM_ADMINISTRATOR", description = "User management, system configuration and maintenance"),
                Role(name = "ROLE_MANAGER", description = "Oversee overall loan management process")
        )

        roles.filter { roleRepository.findByName(it.name) == null }.forEach { role ->
            roleRepository.save(role)
        }

        if(superUser == null) {
            superUser = User(
                "John Doe",
                "johndoe",
                "johndoe@example.com",
                "706087204"
            ).apply { id = "15364327-33cc-44d8-92b5-03494597ca64" }

            if (roleRepository.findByName("ROLE_ADMIN") == null) {
                val adminRole = Role("ROLE_ADMIN", "Super user")
                roleRepository.save(adminRole)
                superUser.addRole(adminRole)
            }

            if (groupRepository.findByName("GROUP_ADMIN") == null) {
                val adminGroup = Group("GROUP_ADMIN", "Super users")
                groupRepository.save(adminGroup)
                superUser.addGroup(adminGroup)
            }

            controllerService.saveUserWithPassword(superUser, "password")
        }
    }
}