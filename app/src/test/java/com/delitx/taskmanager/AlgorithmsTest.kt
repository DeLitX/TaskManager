package com.delitx.taskmanager

import com.delitx.taskmanager.POJO.Task
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AlgorithmsTest {
    private lateinit var mAlgorithms: Algorithms

    @Before
    fun setUp() {
        mAlgorithms = Algorithms()
    }

    @Test
    fun orderEmptyList() {
        val starterList = mutableListOf<Task>()
        assertEquals(listOf<Task>(), mAlgorithms.orderTasks(starterList))
    }

    @Test
    fun orderOneItemList() {
        val starterList = mutableListOf(
            Task(
                id = 1,
                goesBefore = 0
            )
        )
        assertEquals(listOf(Task(id = 1, goesBefore = 0)), mAlgorithms.orderTasks(starterList))
    }

    @Test
    fun orderMultipleItemsList() {
        val starterList = mutableListOf(
            Task(
                id = 1,
                goesBefore = 6
            ),
            Task(
                id = 2,
                goesBefore = 5
            ),
            Task(
                id = 3,
                goesBefore = 2
            ),
            Task(
                id = 4,
                goesBefore = 3
            ),
            Task(
                id = 5,
                goesBefore = 0
            ),
            Task(
                id = 6,
                goesBefore = 4
            )

        )
        assertEquals(
            listOf(
                Task(
                    id = 1,
                    goesBefore = 6
                ),
                Task(
                    id = 6,
                    goesBefore = 4
                ),
                Task(
                    id = 4,
                    goesBefore = 3
                ),
                Task(
                    id = 3,
                    goesBefore = 2
                ),
                Task(
                    id = 2,
                    goesBefore = 5
                ),
                Task(
                    id = 5,
                    goesBefore = 0
                )
            ),
            mAlgorithms.orderTasks(starterList)
        )
    }
}