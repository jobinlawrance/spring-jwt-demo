package com.jobinlawrance.ragnarok.models

import javax.persistence.*

@Entity
@Table(name = "roles")
class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    lateinit var eRole: ERole

    constructor()
    constructor(name: ERole) {
        this.eRole = name
    }
}