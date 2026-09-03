package com.mindguard.ai.data

import com.mindguard.ai.data.model.Appointment
import com.mindguard.ai.data.model.AppointmentStatus
import com.mindguard.ai.data.model.ConsultationMode
import com.mindguard.ai.data.model.Professional
import com.mindguard.ai.data.model.Slot
import org.junit.Assert.*
import org.junit.Test

class BookingUnitTest {

    @Test
    fun testProfessionalModelAttributes() {
        val prof = Professional(
            professionalId = "prof_test",
            name = "Dr. Ananya Sharma",
            title = "Clinical Psychologist",
            qualifications = "M.Phil, RCI Registered",
            specialty = "Anxiety & Stress",
            experienceYears = 8,
            bio = "Specializes in CBT and stress regulation.",
            languages = listOf("English", "Hindi"),
            rating = 4.9,
            reviewCount = 128,
            isVerified = true,
            consultationFee = 800.0
        )

        assertEquals("prof_test", prof.professionalId)
        assertTrue(prof.isVerified)
        assertEquals(2, prof.languages.size)
        assertEquals(800.0, prof.consultationFee, 0.001)
    }

    @Test
    fun testSlotSelectionAndBookingState() {
        val now = System.currentTimeMillis()
        val slot = Slot(
            slotId = "slot_01",
            professionalId = "prof_01",
            startTime = now + 3600000L,
            endTime = now + 7200000L,
            isBooked = false
        )

        assertFalse(slot.isBooked)
        assertNull(slot.bookedByUserId)

        val bookedSlot = slot.copy(isBooked = true, bookedByUserId = "user_abc")
        assertTrue(bookedSlot.isBooked)
        assertEquals("user_abc", bookedSlot.bookedByUserId)
    }

    @Test
    fun testAppointmentModelCreationAndModes() {
        val appt = Appointment(
            appointmentId = "appt_123",
            userId = "user_abc",
            userName = "Patient A",
            professionalId = "prof_01",
            professionalName = "Dr. Ananya Sharma",
            slotId = "slot_01",
            scheduledTime = System.currentTimeMillis() + 86400000L,
            mode = ConsultationMode.VIDEO,
            status = AppointmentStatus.CONFIRMED,
            notes = "Focus on anxiety regulation."
        )

        assertEquals("appt_123", appt.appointmentId)
        assertEquals(ConsultationMode.VIDEO, appt.mode)
        assertEquals(AppointmentStatus.CONFIRMED, appt.status)
        assertEquals("Focus on anxiety regulation.", appt.notes)
    }

    @Test
    fun testConsultationModesEnum() {
        val videoMode = ConsultationMode.valueOf("VIDEO")
        val audioMode = ConsultationMode.valueOf("AUDIO")
        val chatMode = ConsultationMode.valueOf("CHAT")

        assertEquals(ConsultationMode.VIDEO, videoMode)
        assertEquals(ConsultationMode.AUDIO, audioMode)
        assertEquals(ConsultationMode.CHAT, chatMode)
    }
}
