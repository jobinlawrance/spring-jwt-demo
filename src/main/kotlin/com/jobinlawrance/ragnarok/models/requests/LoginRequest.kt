package com.jobinlawrance.ragnarok.models.requests

import javax.validation.constraints.NotBlank

data class LoginRequest(
    var username: @NotBlank String?,
    var password: @NotBlank String?
)