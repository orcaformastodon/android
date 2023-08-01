package com.jeanbarrossilva.orca.feature.profiledetails.conversion

import com.jeanbarrossilva.orca.core.feed.profile.Profile
import com.jeanbarrossilva.orca.core.feed.profile.type.editable.EditableProfile
import com.jeanbarrossilva.orca.core.feed.profile.type.followable.FollowableProfile
import com.jeanbarrossilva.orca.core.sample.feed.profile.sample
import com.jeanbarrossilva.orca.core.sample.feed.profile.type.editable.sample
import com.jeanbarrossilva.orca.core.sample.feed.profile.type.followable.sample
import com.jeanbarrossilva.orca.feature.profiledetails.ProfileDetails
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Test

internal class ProfileConverterFactoryTests {
    private val coroutineScope = TestScope()

    @Test
    fun createdConverterConvertsDefaultProfile() {
        assertEquals(
            ProfileDetails.Default.sample,
            ProfileConverterFactory.create(coroutineScope).convert(Profile.sample)
        )
    }

    @Test
    fun createdConverterConvertsEditableProfile() {
        assertEquals(
            ProfileDetails.Editable.sample,
            ProfileConverterFactory.create(coroutineScope).convert(EditableProfile.sample)
        )
    }

    @Test
    fun createdConverterConvertsFollowableProfile() {
        val onStatusToggle = { }
        assertEquals(
            ProfileDetails.Followable.createSample(onStatusToggle),
            ProfileConverterFactory
                .create(coroutineScope)
                .convert(FollowableProfile.sample)
                .let { it as ProfileDetails.Followable }
                .copy(onStatusToggle = onStatusToggle)
        )
    }
}