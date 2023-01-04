package com.jobinlawrance.ragnarok.controllers

import com.jobinlawrance.ragnarok.models.ERole
import com.jobinlawrance.ragnarok.models.Role
import com.jobinlawrance.ragnarok.models.User
import com.jobinlawrance.ragnarok.models.requests.LoginRequest
import com.jobinlawrance.ragnarok.models.requests.SignupRequest
import com.jobinlawrance.ragnarok.models.response.JwtResponse
import com.jobinlawrance.ragnarok.models.response.MessageResponse
import com.jobinlawrance.ragnarok.repository.RoleRepository
import com.jobinlawrance.ragnarok.repository.UserRepository
import com.jobinlawrance.ragnarok.security.jwt.JwtUtils
import com.jobinlawrance.ragnarok.security.servcies.UserDetailsImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
class AuthController {

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    @Autowired
    lateinit var jwtUtils: JwtUtils

    @PostMapping("/signin")
    fun authenticateUser(@RequestBody loginRequest: @Valid LoginRequest): ResponseEntity<*> {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
        )
        SecurityContextHolder.getContext().authentication = authentication
        val jwt: String = jwtUtils.generateJwtToken(authentication)
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val roles: List<String> = userDetails.authorities.stream()
            .map { item -> item.authority }
            .collect(Collectors.toList())
        return ResponseEntity.ok<Any>(
            JwtResponse(
                jwt,
                userDetails.id,
                userDetails.username,
                userDetails.email,
                roles
            )
        )
    }

    @PostMapping("/signup")
    fun registerUser(@RequestBody signUpRequest: @Valid SignupRequest): ResponseEntity<*> {
        if (userRepository.existsByUsername(signUpRequest.username)) {
            return ResponseEntity
                .badRequest()
                .body<Any>(MessageResponse("Error: Username is already taken!"))
        }
        if (userRepository.existsByEmail(signUpRequest.email)) {
            return ResponseEntity
                .badRequest()
                .body<Any>(MessageResponse("Error: Email is already in use!"))
        }

        // Create new user's account
        val user = User(
            signUpRequest.username,
            signUpRequest.email,
            encoder.encode(signUpRequest.password)
        )
        val strRoles: Set<String> = signUpRequest.role
        val roles: MutableSet<Role> = HashSet<Role>()
        if (strRoles == null) {
            val userRole: Role = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow { RuntimeException("Error: Role is not found.") }
            roles.add(userRole)
        } else {
            strRoles.forEach(Consumer { role: String? ->
                when (role) {
                    "admin" -> {
                        val adminRole: Role = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow { RuntimeException("Error: Role is not found.") }
                        roles.add(adminRole)
                    }

                    "mod" -> {
                        val modRole: Role = roleRepository.findByName(ERole.ROLE_MODERATOR)
                            .orElseThrow { RuntimeException("Error: Role is not found.") }
                        roles.add(modRole)
                    }

                    else -> {
                        val userRole: Role = roleRepository.findByName(ERole.ROLE_USER)
                            .orElseThrow { RuntimeException("Error: Role is not found.") }
                        roles.add(userRole)
                    }
                }
            })
        }
        user.roles = roles
        userRepository.save(user)
        return ResponseEntity.ok<Any>(MessageResponse("User registered successfully!"))
    }
}