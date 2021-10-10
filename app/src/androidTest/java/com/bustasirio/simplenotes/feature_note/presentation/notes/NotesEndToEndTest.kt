package com.bustasirio.simplenotes.feature_note.presentation.notes

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bustasirio.simplenotes.core.util.TestTags.CONTENT_TEXT_FIELD
import com.bustasirio.simplenotes.core.util.TestTags.NOTE_ITEM
import com.bustasirio.simplenotes.core.util.TestTags.TITLE_TEXT_FIELD
import com.bustasirio.simplenotes.di.AppModule
import com.bustasirio.simplenotes.feature_note.presentation.MainActivity
import com.bustasirio.simplenotes.feature_note.presentation.add_edit_note.AddEditNoteScreen
import com.bustasirio.simplenotes.feature_note.presentation.util.Screen
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
@UninstallModules(AppModule::class)
class NotesEndToEndTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @ExperimentalAnimationApi
    @Before
    fun setUp() {
        hiltRule.inject()
        composeRule.setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Screen.NotesScreen.route
            ) {
                composable(route = Screen.NotesScreen.route) {
                    NotesScreen(navController = navController)
                }
                composable(
                    route = Screen.AddEditNoteScreen.route +
                            "?noteId={noteId}&noteColor={noteColor}",
                    arguments = listOf(
                        navArgument(
                            name = "noteId"
                        ) {
                            type = NavType.IntType
                            defaultValue = -1
                        },
                        navArgument(
                            name = "noteColor"
                        ) {
                            type = NavType.IntType
                            defaultValue = -1
                        }
                    )
                ) {
                    val color = it.arguments?.getInt("noteColor") ?: -1
                    AddEditNoteScreen(
                        navController = navController,
                        noteColor = color
                    )
                }
            }
        }
    }

    @Test
    fun saveNewNote_editAfterwards() {
        // * Click on FAB to get to add note screen
        composeRule.onNodeWithContentDescription("Add").performClick()

        // * Enter texts to title and content fields
        composeRule.onNodeWithTag(TITLE_TEXT_FIELD)
            .performTextInput("test-title")
        composeRule.onNodeWithTag(CONTENT_TEXT_FIELD)
            .performTextInput("test-content")
        // * Save the note
        composeRule.onNodeWithContentDescription("Save").performClick()
        // * Make sure the saved note is on the list
        composeRule.onNodeWithText("test-title").assertIsDisplayed()
        // * Edit note
        composeRule.onNodeWithText("test-title").performClick()

        // * Make sure the note contains the exact data
        composeRule.onNodeWithTag(TITLE_TEXT_FIELD)
            .assertTextEquals("test-title")
        composeRule.onNodeWithTag(CONTENT_TEXT_FIELD)
            .assertTextEquals("test-content")
        // * Edit the title and save
        composeRule.onNodeWithTag(TITLE_TEXT_FIELD)
            .performTextInput("2")
        composeRule.onNodeWithContentDescription("Save").performClick()
        // * Check if the title was updated in the list
        composeRule.onNodeWithText("test-title2").assertIsDisplayed()
    }

    @Test
    fun saveNewNotes_orderByTitleDescending() {
        // * Create three notes
        for(i in 1..3) {
            composeRule.onNodeWithContentDescription("Add").performClick()

            composeRule.onNodeWithTag(TITLE_TEXT_FIELD)
                .performTextInput(i.toString())
            composeRule.onNodeWithTag(CONTENT_TEXT_FIELD)
                .performTextInput(i.toString())

            composeRule.onNodeWithContentDescription("Save").performClick()
        }

        composeRule.onNodeWithText("1").assertIsDisplayed()
        composeRule.onNodeWithText("2").assertIsDisplayed()
        composeRule.onNodeWithText("3").assertIsDisplayed()

        composeRule
            .onNodeWithContentDescription("Sort")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Title")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Descending")
            .performClick()

        composeRule.onAllNodesWithTag(NOTE_ITEM)[0]
            .assertTextContains("3")
        composeRule.onAllNodesWithTag(NOTE_ITEM)[1]
            .assertTextContains("2")
        composeRule.onAllNodesWithTag(NOTE_ITEM)[2]
            .assertTextContains("1")
    }
}