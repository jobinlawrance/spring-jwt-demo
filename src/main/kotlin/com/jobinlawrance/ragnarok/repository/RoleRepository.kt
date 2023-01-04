package com.jobinlawrance.ragnarok.repository

import com.jobinlawrance.ragnarok.models.ERole
import com.jobinlawrance.ragnarok.models.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: ERole): Optional<Role>
}