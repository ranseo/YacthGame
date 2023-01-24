package com.ranseo.yatchgame.domain.usecase.write

import com.ranseo.yatchgame.data.model.log.LogModel
import com.ranseo.yatchgame.data.repo.LogModelRepository
import javax.inject.Inject

class WriteLogModelUseCase @Inject constructor(logModelRepository: LogModelRepository) {
    private val logModelWriter : suspend (logModel: LogModel) -> Unit = { logModel ->
        logModelRepository.writeLog(logModel)
    }

    suspend operator fun invoke(logModel: LogModel) {
        logModelWriter(logModel)
    }
}
