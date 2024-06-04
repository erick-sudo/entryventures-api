package com.entryventures.security

import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class AuthorizationTokenFilter(
    private val userDetailsService: UserDetailsService,
    private val jwtService: JwtService
) : OncePerRequestFilter() {

    /**
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        if(SecurityContextHolder.getContext().authentication == null) {
            val jwt = parseJwt(request)

            jwt?.let { token ->
                if (jwtService.validateToken(token)) {
                    val email = jwtService.getFieldFromJwtToken("email", token)
                    val grantType = jwtService.getFieldFromJwtToken("grant_type", token)
                    val userDetails = userDetailsService.loadUserByUsername("${grantType}#${email}")

                    val authentication = UsernamePasswordAuthenticationToken(
                        userDetails.username,
                        grantType,
                        userDetails.authorities
                    )

                    authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun parseJwt(req: HttpServletRequest): String? {
        val authorizationHeader = req.getHeader("Authorization")

        if(StringUtils.hasText(authorizationHeader)) {
            try {

                val authHeader = authorizationHeader.split(Regex("\\s+"))
                if (authHeader.first() == "Bearer" && StringUtils.hasText(authHeader[1])) {
                    return authHeader[1]
                }
            } catch (exc: Exception) {
                // Error obtaining token
            }
        }

        return null
    }
}