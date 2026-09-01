package com.mindguard.ai.di

import android.content.Context
import com.mindguard.ai.data.local.QuestionnaireLocalDataSource
import com.mindguard.ai.data.repository.AppointmentRepository
import com.mindguard.ai.data.repository.AppointmentRepositoryImpl
import com.mindguard.ai.data.repository.AssessmentRepository
import com.mindguard.ai.data.repository.AssessmentRepositoryImpl
import com.mindguard.ai.data.repository.AuthRepository
import com.mindguard.ai.data.repository.AuthRepositoryImpl
import com.mindguard.ai.data.repository.ConsultationRepository
import com.mindguard.ai.data.repository.ConsultationRepositoryImpl
import com.mindguard.ai.data.repository.ProfessionalRepository
import com.mindguard.ai.data.repository.ProfessionalRepositoryImpl
import com.mindguard.ai.data.repository.WellbeingRepository
import com.mindguard.ai.data.repository.WellbeingRepositoryImpl
import com.mindguard.ai.ml.ModelManager

interface AppContainer {
    val authRepository: AuthRepository
    val assessmentRepository: AssessmentRepository
    val wellbeingRepository: WellbeingRepository
    val professionalRepository: ProfessionalRepository
    val appointmentRepository: AppointmentRepository
    val consultationRepository: ConsultationRepository
    val modelManager: ModelManager
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val modelManager: ModelManager by lazy {
        ModelManager.getInstance(context)
    }

    private val questionnaireLocalDataSource: QuestionnaireLocalDataSource by lazy {
        QuestionnaireLocalDataSource(context)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl()
    }

    override val assessmentRepository: AssessmentRepository by lazy {
        AssessmentRepositoryImpl(
            localDataSource = questionnaireLocalDataSource,
            modelManager = modelManager
        )
    }

    override val wellbeingRepository: WellbeingRepository by lazy {
        WellbeingRepositoryImpl()
    }

    override val professionalRepository: ProfessionalRepository by lazy {
        ProfessionalRepositoryImpl()
    }

    override val appointmentRepository: AppointmentRepository by lazy {
        AppointmentRepositoryImpl()
    }

    override val consultationRepository: ConsultationRepository by lazy {
        ConsultationRepositoryImpl()
    }
}
