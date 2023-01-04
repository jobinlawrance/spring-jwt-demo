package com.jobinlawrance.ragnarok.models.requests

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

data class SignupRequest(
    var username: @NotBlank @Size(min = 3, max = 20) String,
    var email: @NotBlank @Size(max = 50) @Email String,
    var role: Set<String>,
    var password: @NotBlank @Size(min = 6, max = 40) String
)
