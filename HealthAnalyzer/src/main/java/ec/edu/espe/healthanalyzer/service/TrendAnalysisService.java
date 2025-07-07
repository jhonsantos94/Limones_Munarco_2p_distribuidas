package ec.edu.espe.healthanalyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrendAnalysisService {
    
    public String performTrendAnalysis(String patientId, String dataSource, int timeRangeHours) {
        log.info("Performing trend analysis for patient: {}, source: {}, range: {}h", 
                patientId, dataSource, timeRangeHours);
        
        // Simulación de análisis de tendencias
        // En un entorno real, aquí se analizarían los datos históricos
        
        StringBuilder result = new StringBuilder();
        result.append("{");
        result.append("\"analysisType\": \"TREND_ANALYSIS\",");
        result.append("\"timeRange\": ").append(timeRangeHours).append(",");
        result.append("\"dataSource\": \"").append(dataSource).append("\",");
        
        // Simular diferentes resultados basados en el paciente
        if (patientId.hashCode() % 4 == 0) {
            result.append("\"trend\": \"INCREASING\",");
            result.append("\"severity\": \"HIGH\",");
            result.append("\"confidence\": 0.85,");
            result.append("\"description\": \"Tendencia al alza en valores críticos detectada\"");
        } else if (patientId.hashCode() % 4 == 1) {
            result.append("\"trend\": \"STABLE\",");
            result.append("\"severity\": \"LOW\",");
            result.append("\"confidence\": 0.95,");
            result.append("\"description\": \"Valores estables dentro de rangos normales\"");
        } else if (patientId.hashCode() % 4 == 2) {
            result.append("\"trend\": \"DECREASING\",");
            result.append("\"severity\": \"MEDIUM\",");
            result.append("\"confidence\": 0.75,");
            result.append("\"description\": \"Tendencia a la baja requiere monitoreo\"");
        } else {
            result.append("\"trend\": \"FLUCTUATING\",");
            result.append("\"severity\": \"MEDIUM\",");
            result.append("\"confidence\": 0.70,");
            result.append("\"description\": \"Patrones irregulares detectados\"");
        }
        
        result.append("}");
        
        return result.toString();
    }
    
    public String analyzeTrends(ec.edu.espe.healthanalyzer.model.PatientHealthProfile profile) {
        log.info("Analyzing trends for patient profile: {}", profile.getPatientId());
        
        // Perform trend analysis based on the patient profile data
        StringBuilder trends = new StringBuilder();
        trends.append("{");
        trends.append("\"patientId\": \"").append(profile.getPatientId()).append("\",");
        trends.append("\"analysisDate\": \"").append(java.time.LocalDateTime.now()).append("\",");
        
        // Analyze heart rate trends
        if (profile.getHeartRateAvg() != null) {
            trends.append("\"heartRateTrend\": \"");
            if (profile.getHeartRateAvg() > 100) {
                trends.append("ELEVATED");
            } else if (profile.getHeartRateAvg() < 60) {
                trends.append("LOW");
            } else {
                trends.append("NORMAL");
            }
            trends.append("\",");
        }
        
        // Analyze temperature trends
        if (profile.getTemperatureAvg() != null) {
            trends.append("\"temperatureTrend\": \"");
            if (profile.getTemperatureAvg() > 37.5) {
                trends.append("ELEVATED");
            } else if (profile.getTemperatureAvg() < 36.0) {
                trends.append("LOW");
            } else {
                trends.append("NORMAL");
            }
            trends.append("\",");
        }
        
        trends.append("\"readingCount\": ").append(profile.getReadingCount() != null ? profile.getReadingCount() : 0);
        trends.append("}");
        
        return trends.toString();
    }
}
