package org.vontech.morrow

import org.joda.time.DateTime

/**
 * A representation of task logged by the user.
 * @author Aaron Vontell (aaron@vontech.org), on behalf of Vontech Software, LLC
 */
class Task(
        var content: String,
        var currentAllocation: Day,
        var lastAllocated: Int,
        var created: Int) {

}
