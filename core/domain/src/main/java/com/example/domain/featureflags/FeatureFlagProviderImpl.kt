package com.example.domain.featureflags

import javax.inject.Inject

class FeatureFlagProviderImpl @Inject constructor() : FeatureFlagProvider {
    override val isGraphQlEnabled: Boolean = true
    override val isComposeEnabled: Boolean = false
}
