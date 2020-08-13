package com.delitx.taskmanager

import com.delitx.taskmanager.POJO.SwapTasksStruct
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

    @Test
    fun swapTwoOneByOneTasks() {
        val startBunch = SwapTasksStruct(
            task1 = Task(id = 1, goesBefore = 2),
            task2 = Task(id = 2),
            previousTask1 = null,
            previousTask2 = Task(id = 1, goesBefore = 2)
        )
        val resultBunch = SwapTasksStruct(
            task1 = Task(id = 2, goesBefore = 1),
            task2 = Task(id = 1),
            previousTask1 = null,
            previousTask2 = Task(id = 2, goesBefore = 1)
        )
        assertEquals(resultBunch, mAlgorithms.swapTasks(startBunch))
    }

    @Test
    fun swapWith3Tasks() {
        val startBunch = SwapTasksStruct(
            task1 = Task(id = 1, goesBefore = 2),
            task2 = Task(id = 3),
            previousTask1 = null,
            previousTask2 = Task(id = 2, goesBefore = 3)
        )
        val resultBunch = SwapTasksStruct(
            task1 = Task(id = 3, goesBefore = 2),
            task2 = Task(id = 1),
            previousTask1 = null,
            previousTask2 = Task(id = 2, goesBefore = 1)
        )
        assertEquals(resultBunch, mAlgorithms.swapTasks(startBunch))
    }

    @Test
    fun swapWith4Tasks() {
        val startBunch = SwapTasksStruct(
            task1 = Task(id = 1, goesBefore = 2),
            task2 = Task(id = 3),
            previousTask1 = Task(id = 0, goesBefore = 1),
            previousTask2 = Task(id = 2, goesBefore = 3)
        )
        val resultBunch = SwapTasksStruct(
            task1 = Task(id = 3, goesBefore = 2),
            task2 = Task(id = 1),
            previousTask1 = Task(id = 0, goesBefore = 3),
            previousTask2 = Task(id = 2, goesBefore = 1)
        )
        assertEquals(resultBunch, mAlgorithms.swapTasks(startBunch))
    }

    @Test
    fun swapWithMultipleTasks() {
        val startBunch = SwapTasksStruct(
            task1 = Task(id = 4, goesBefore = 5),
            task2 = Task(id = 8, goesBefore = 9),
            previousTask1 = Task(id = 3, goesBefore = 4),
            previousTask2 = Task(id = 7, goesBefore = 8)
        )
        val resultBunch = SwapTasksStruct(
            task1 = Task(id = 8, goesBefore = 5),
            task2 = Task(id = 4, goesBefore = 9),
            previousTask1 = Task(id = 3, goesBefore = 8),
            previousTask2 = Task(id = 7, goesBefore = 4)
        )
        assertEquals(resultBunch, mAlgorithms.swapTasks(startBunch))
    }

    @Test
    fun checkSwapForSymmetry() {
        val startBunch = SwapTasksStruct(
            task1 = Task(id = 4, goesBefore = 5),
            task2 = Task(id = 8, goesBefore = 9),
            previousTask1 = Task(id = 3, goesBefore = 4),
            previousTask2 = Task(id = 7, goesBefore = 8)
        )
        assertEquals(startBunch, mAlgorithms.swapTasks(mAlgorithms.swapTasks(startBunch)))
    }
}