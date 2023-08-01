package com.jeanbarrossilva.orca.core.feed

import com.jeanbarrossilva.orca.core.feed.profile.toot.Toot
import kotlinx.coroutines.flow.Flow

/**
 * Provides a user's feed (the [Toot]s shown to them based on who they follow) through [onProvide].
 **/
abstract class FeedProvider {
    /**
     * [IllegalArgumentException] thrown when a user that doesn't exist is requested to be provided.
     *
     * @param id ID of the user requested to be provided.
     **/
    class NonexistentUserException internal constructor(id: String) :
        IllegalArgumentException("User identified as \"$id\" doesn't exist.")

    /**
     * Provides the feed of the user identified as [userID].
     *
     * @param userID ID of the user whose feed will be provided.
     * @param page Index of the [Toot]s that compose the feed.
     * @throws NonexistentUserException If no user with such [userID] exists.
     * @throws IndexOutOfBoundsException If the page is invalid.
     * @see ensurePageValidity
     **/
    suspend fun provide(userID: String, page: Int): Flow<List<Toot>> {
        ensureContainsUser(userID)
        ensurePageValidity(page)
        return onProvide(userID, page)
    }

    /**
     * Provides the feed of the user identified as [userID].
     *
     * @param userID ID of the user whose feed will be provided.
     * @param page Index of the [Toot]s that compose the feed.
     **/
    protected abstract suspend fun onProvide(userID: String, page: Int): Flow<List<Toot>>

    /**
     * Whether a user identified as [userID] exists.
     *
     * @param userID ID of the user whose existence will be checked.
     **/
    protected abstract suspend fun containsUser(userID: String): Boolean

    /**
     * Ensures that a user identified as [userID] exists.
     *
     * @param userID ID of the user whose existence will be ensured.
     * @throws NonexistentUserException If no user with such [userID] exists.
     **/
    private suspend fun ensureContainsUser(userID: String) {
        if (!containsUser(userID)) {
            throw NonexistentUserException(userID)
        }
    }

    /**
     * Ensures that the [page] is valid; that is, if it is a positive [Int].
     *
     * @param page Page whose validity will be ensured.
     * @throws IndexOutOfBoundsException If the page is invalid.
     **/
    private fun ensurePageValidity(page: Int) {
        if (page < 0) {
            throw IndexOutOfBoundsException("Page out of range: $page.")
        }
    }
}