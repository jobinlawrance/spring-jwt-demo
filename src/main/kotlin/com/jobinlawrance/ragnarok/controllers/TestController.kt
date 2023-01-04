package com.jobinlawrance.ragnarok.controllers

import com.jobinlawrance.ragnarok.models.ERole
import com.jobinlawrance.ragnarok.models.Role
import com.jobinlawrance.ragnarok.models.response.MessageResponse
import com.jobinlawrance.ragnarok.repository.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/test")
class TestController {
    @Autowired
    lateinit var roleRepository: RoleRepository
    @GetMapping("/all")
    fun allAccess(): String {
        return "Public Content."
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    fun userAccess(): String {
        return "User Content."
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    fun moderatorAccess(): String {
        return "Moderator Board."
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    fun adminAccess(): String {
        return "Admin Board."
    }

    @GetMapping("/insert-roles")
    fun insertRoles(): ResponseEntity<*> {
        roleRepository.save(Role(ERole.ROLE_USER))
        roleRepository.save(Role(ERole.ROLE_ADMIN))
        roleRepository.save(Role(ERole.ROLE_MODERATOR))
        return ResponseEntity.ok<Any>(MessageResponse("Roles entered"))
    }
}