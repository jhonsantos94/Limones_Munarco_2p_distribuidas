package ec.edu.espe.healthanalyzer.service;

import ec.edu.espe.healthanalyzer.constant.RiskLevel;
import ec.edu.espe.healthanalyzer.dto.VitalSignEventDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RiskAssessmentService {

    private static final double SEVERE_HEART_RATE_LOW = 50.0;
    private static final double SEVERE_HEART_RATE_HIGH = 120.0;
    private static final double SEVERE_BP_SYSTOLIC_LOW = 80.0;
    private static final double SEVERE_BP_SYSTOLIC_HIGH = 180.0;
    private static final double SEVERE_BP_DIASTOLIC_LOW = 50.0;
    private static final double SEVERE_BP_DIASTOLIC_HIGH = 110.0;
    private static final double SEVERE_TEMPERATURE_LOW = 35.0;
    private static final double SEVERE_TEMPERATURE_HIGH = 39.0;
    private static final double SEVERE_RESPIRATORY_RATE_LOW = 8.0;
    private static final double SEVERE_RESPIRATORY_RATE_HIGH = 30.0;
    private static final double SEVERE_OXYGEN_SATURATION = 90.0;

    public String assessRisk(VitalSignEventDto vitalSign, List<String> anomalies) {
        int riskScore = calculateRiskScore(vitalSign, anomalies);
        return determineRiskLevel(riskScore);
    }

    private int calculateRiskScore(VitalSignEventDto vitalSign, List<String> anomalies) {
        int score = 0;
        score += calculateVitalSignsSeverity(vitalSign);
        score += calculateAnomalyCount(anomalies);
        score += calculateCombinationRisk(vitalSign);
        return score;
    }

    private int calculateAnomalyCount(List<String> anomalies) {
        int criticalCount = 0;
        int normalCount = 0;

        for (String anomaly : anomalies) {
            if (anomaly.startsWith("Critical:")) {
                criticalCount++;
            } else {
                normalCount++;
            }
        }

        return (criticalCount * 3) + normalCount;
    }

    private String determineRiskLevel(int riskScore) {
        if (riskScore >= 15) {
            return RiskLevel.CRITICAL;
        } else if (riskScore >= 10) {
            return RiskLevel.HIGH;
        } else if (riskScore >= 7) {
            return RiskLevel.MEDIUM;
        } else if (riskScore >= 4) {
            return RiskLevel.CAUTION;
        } else if (riskScore >= 1) {
            return RiskLevel.LOW;
        } else {
            return RiskLevel.NORMAL;
        }
    }

    private int calculateVitalSignsSeverity(VitalSignEventDto vitalSign) {
        int severity = 0;
        severity += assessVitalSign(vitalSign.getHeartRate(), SEVERE_HEART_RATE_LOW, SEVERE_HEART_RATE_HIGH);
        severity += assessBloodPressure(vitalSign);
        severity += assessVitalSign(vitalSign.getTemperature(), SEVERE_TEMPERATURE_LOW, SEVERE_TEMPERATURE_HIGH);
        severity += assessVitalSign(vitalSign.getRespiratoryRate(), SEVERE_RESPIRATORY_RATE_LOW, SEVERE_RESPIRATORY_RATE_HIGH);
        severity += assessOxygenSaturation(vitalSign.getOxygenSaturation());
        return severity;
    }

    private int assessVitalSign(double value, double lowThreshold, double highThreshold) {
        if (value < lowThreshold || value > highThreshold) {
            return 3;
        } else if (isNearThreshold(value, lowThreshold, highThreshold)) {
            return 1;
        }
        return 0;
    }

    private boolean isNearThreshold(double value, double lowThreshold, double highThreshold) {
        double lowWarning = lowThreshold + (lowThreshold * 0.1);
        double highWarning = highThreshold - (highThreshold * 0.1);
        return value < lowWarning || value > highWarning;
    }

    private int assessBloodPressure(VitalSignEventDto vitalSign) {
        int systolicSeverity = assessVitalSign(
            vitalSign.getBloodPressureSystolic(), 
            SEVERE_BP_SYSTOLIC_LOW, 
            SEVERE_BP_SYSTOLIC_HIGH
        );
        
        int diastolicSeverity = assessVitalSign(
            vitalSign.getBloodPressureDiastolic(), 
            SEVERE_BP_DIASTOLIC_LOW, 
            SEVERE_BP_DIASTOLIC_HIGH
        );

        return Math.max(systolicSeverity, diastolicSeverity);
    }

    private int assessOxygenSaturation(double value) {
        if (value < SEVERE_OXYGEN_SATURATION) {
            return 3;
        } else if (value < SEVERE_OXYGEN_SATURATION + 5) {
            return 1;
        }
        return 0;
    }

    private int calculateCombinationRisk(VitalSignEventDto vitalSign) {
        int combinationScore = 0;

        // Low blood pressure with tachycardia
        if (vitalSign.getBloodPressureSystolic() < 90 && vitalSign.getHeartRate() > 100) {
            combinationScore += 3;
        }

        // Low oxygen with high respiratory rate
        if (vitalSign.getOxygenSaturation() < 90 && vitalSign.getRespiratoryRate() > 30) {
            combinationScore += 3;
        }

        // Fever/hypothermia with tachycardia
        if ((vitalSign.getTemperature() > 38.3 || vitalSign.getTemperature() < 36.0) &&
            vitalSign.getHeartRate() > 100) {
            combinationScore += 2;
        }

        return combinationScore;
    }
}
