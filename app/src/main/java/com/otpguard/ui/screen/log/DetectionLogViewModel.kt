package com.otpguard.ui.screen.log

import androidx.lifecycle.ViewModel
import com.otpguard.data.local.entity.DetectionEventWithDetails
import com.otpguard.domain.repository.DetectionEventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class DetectionLogViewModel @Inject constructor(
    detectionEventRepository: DetectionEventRepository
) : ViewModel() {
    val events: Flow<List<DetectionEventWithDetails>> = detectionEventRepository.getAllEvents()
}
