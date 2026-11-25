package com.guvi.languageplatform;

import com.guvi.languageplatform.controller.InstructorReportsController;
import com.guvi.languageplatform.controller.LearnerLessonController;
import com.guvi.languageplatform.controller.LearnerProgressController;
import com.guvi.languageplatform.model.ActivityLog;
import com.guvi.languageplatform.model.Feedback;
import com.guvi.languageplatform.model.Lesson;
import com.guvi.languageplatform.model.QuizAttempt;
import com.guvi.languageplatform.model.User;
import com.guvi.languageplatform.repository.ActivityLogRepository;
import com.guvi.languageplatform.repository.FeedbackRepository;
import com.guvi.languageplatform.repository.LessonRepository;
import com.guvi.languageplatform.repository.QuizAttemptRepository;
import com.guvi.languageplatform.security.CurrentUserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizAndProgressUnitTest {

    @Mock private LessonRepository lessonRepository;
    @Mock private QuizAttemptRepository quizAttemptRepository;
    @Mock private ActivityLogRepository activityLogRepository;
    @Mock private FeedbackRepository feedbackRepository;
    @Mock private CurrentUserService currentUserService;
    @Mock private HttpSession session;
    @Mock private Authentication authentication;

    private LearnerLessonController learnerLessonController;
    private LearnerProgressController learnerProgressController;
    private InstructorReportsController instructorReportsController;

    private User learner;
    private Lesson approvedLesson;

    @BeforeEach
    void setUp() {
        learnerLessonController = new LearnerLessonController(
                lessonRepository, quizAttemptRepository, activityLogRepository, currentUserService);
        learnerProgressController = new LearnerProgressController(
                quizAttemptRepository, feedbackRepository, currentUserService);
        instructorReportsController = new InstructorReportsController(
                quizAttemptRepository, currentUserService);

        learner = new User("Learner One", "learner@mail.com", "hashed", "LEARNER");
        approvedLesson = new Lesson(
                "Spanish Basics", "Spanish", "Beginner",
                "Hola means Hello.", "What does Hola mean?", "Hello",
                "APPROVED", "inst@mail.com");
    }

    @Test
    void checkQuizAcceptsCaseInsensitiveTrimmedAnswerAndRecordsAttempt() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(approvedLesson));
        when(currentUserService.getCurrentUser(session, authentication)).thenReturn(learner);
        Model model = new ExtendedModelMap();

        String view = learnerLessonController.checkQuiz(1L, "  hElLo  ", session, authentication, model);

        assertEquals("learner-lesson-detail", view);
        assertEquals("Correct! 🎉", model.getAttribute("result"));

        ArgumentCaptor<QuizAttempt> attemptCaptor = ArgumentCaptor.forClass(QuizAttempt.class);
        verify(quizAttemptRepository).save(attemptCaptor.capture());
        assertTrue(attemptCaptor.getValue().isCorrect());
        assertEquals("learner@mail.com", attemptCaptor.getValue().getLearnerEmail());
        assertEquals("Spanish Basics", attemptCaptor.getValue().getLessonTitle());

        verify(activityLogRepository).save(any(ActivityLog.class));
    }

    @Test
    void checkQuizRecordsIncorrectAttempt() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(approvedLesson));
        when(currentUserService.getCurrentUser(session, authentication)).thenReturn(learner);
        Model model = new ExtendedModelMap();

        String view = learnerLessonController.checkQuiz(1L, "Goodbye", session, authentication, model);

        assertEquals("learner-lesson-detail", view);
        assertEquals("Wrong, try again.", model.getAttribute("result"));

        ArgumentCaptor<QuizAttempt> attemptCaptor = ArgumentCaptor.forClass(QuizAttempt.class);
        verify(quizAttemptRepository).save(attemptCaptor.capture());
        assertFalse(attemptCaptor.getValue().isCorrect());
    }

    @Test
    void checkQuizRejectsBlankAnswerWithoutSavingAttempt() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.of(approvedLesson));
        Model model = new ExtendedModelMap();

        String view = learnerLessonController.checkQuiz(1L, "   ", session, authentication, model);

        assertEquals("learner-lesson-detail", view);
        assertEquals("Please enter an answer.", model.getAttribute("result"));
        verifyNoInteractions(quizAttemptRepository, activityLogRepository);
    }

    @Test
    void checkQuizRedirectsForUnapprovedLesson() {
        Lesson pending = new Lesson("Draft", "French", "Beginner", "Text", "Q", "A", "PENDING", "inst@mail.com");
        when(lessonRepository.findById(2L)).thenReturn(Optional.of(pending));
        Model model = new ExtendedModelMap();

        String view = learnerLessonController.checkQuiz(2L, "A", session, authentication, model);

        assertEquals("redirect:/learner/lessons", view);
        verifyNoInteractions(quizAttemptRepository);
    }

    @Test
    void learnerProgressCalculatesTotalsPercentagesAndGroupsByLesson() {
        when(currentUserService.getCurrentUser(session, authentication)).thenReturn(learner);
        when(quizAttemptRepository.findByLearnerEmail("learner@mail.com")).thenReturn(List.of(
                new QuizAttempt("learner@mail.com", 1L, "Spanish Basics", "inst@mail.com", true),
                new QuizAttempt("learner@mail.com", 1L, "Spanish Basics", "inst@mail.com", false),
                new QuizAttempt("learner@mail.com", 2L, "French Greetings", "inst@mail.com", true)
        ));
        when(feedbackRepository.findByLearnerEmail("learner@mail.com")).thenReturn(List.of(
                new Feedback("inst@mail.com", "learner@mail.com", "Great progress!")
        ));

        Model model = new ExtendedModelMap();
        String view = learnerProgressController.progress(session, authentication, model);

        assertEquals("learner-progress", view);
        assertEquals(3L, model.getAttribute("total"));
        assertEquals(2L, model.getAttribute("correct"));
        assertEquals(66L, model.getAttribute("percent"));

        @SuppressWarnings("unchecked")
        List<LearnerProgressController.LessonStat> stats =
                (List<LearnerProgressController.LessonStat>) model.getAttribute("stats");
        assertNotNull(stats);
        assertEquals(2, stats.size());
        // Sorted alphabetically by TreeMap: French Greetings, then Spanish Basics
        assertEquals("French Greetings", stats.get(0).getTitle());
        assertEquals(100L, stats.get(0).getPercent());
        assertEquals("Spanish Basics", stats.get(1).getTitle());
        assertEquals(50L, stats.get(1).getPercent());
    }

    @Test
    void lessonStatAndInstructorStatHandleZeroAttemptsSafely() {
        LearnerProgressController.LessonStat zeroLesson = new LearnerProgressController.LessonStat("Empty", 0, 0);
        assertEquals(0L, zeroLesson.getPercent());

        InstructorReportsController.Stat zeroStat = new InstructorReportsController.Stat("Empty", 0, 0);
        assertEquals(0L, zeroStat.getPercent());

        InstructorReportsController.Stat partialStat = new InstructorReportsController.Stat("Spanish", 4, 3);
        assertEquals(75L, partialStat.getPercent());
    }
}
