package app.cui.ro.models

import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.lifecycle.ViewModel
import java.time.ZonedDateTime

class VMHealthConnect : ViewModel() {

    // Función para leer pasos desde Health Connect
    suspend fun readStepsForDate(client: HealthConnectClient, date: ZonedDateTime): Long {
        // Convertir ZonedDateTime a Instant
        val startTime = date.toInstant()
        val endTime = date.plusDays(1).toInstant()

        // Crear el filtro de rango de tiempo
        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)

        // Leer los registros de pasos
        val response = client.readRecords(
            ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = timeRangeFilter
            )
        )

        // Sumar el conteo de pasos de todos los registros
        return response.records.sumOf { it.count }
    }
}


