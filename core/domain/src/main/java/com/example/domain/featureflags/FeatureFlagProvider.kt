package com.example.domain.featureflags

interface FeatureFlagProvider {
    val isGraphQlEnabled: Boolean
    val isComposeEnabled: Boolean
}
