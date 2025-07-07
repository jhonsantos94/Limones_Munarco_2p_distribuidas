package ec.edu.espe.healthanalyzer.service;

import ec.edu.espe.healthanalyzer.dto.VitalSignEventDto;
import ec.edu.espe.healthanalyzer.model.PatientHealthProfile;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AnomalyDetectionService {
    private static final double MIN_HEART_RATE = 60.0;
    private static final double MAX_HEART_RATE = 100.0;
    private static final double MIN_BLOOD_PRESSURE_SYSTOLIC = 90.0;
    private static final double MAX_BLOOD_PRESSURE_SYSTOLIC = 140.0;
    private static final double MIN_BLOOD_PRESSURE_DIASTOLIC = 60.0;
    private static final double MAX_BLOOD_PRESSURE_DIASTOLIC = 90.0;
    private static final double MIN_TEMPERATURE = 36.0;
    private static final double MAX_TEMPERATURE = 37.5;
    private static final double MIN_RESPIRATORY_RATE = 12.0;
    private static final double MAX_RESPIRATORY_RATE = 20.0;
    private static final double MIN_OXYGEN_SATURATION = 95.0;
    private static final double SIGNIFICANT_DEVIATION = 0.15; // 15% deviation threshold

    public List<String> detectAnomalies(VitalSignEventDto vitalSign, PatientHealthProfile profile) {
        List<String> anomalies = new ArrayList<>();

        detectVitalSignAnomalies(vitalSign, anomalies);
        detectDeviationsFromBaseline(vitalSign, profile, anomalies);
        detectCriticalCombinations(vitalSign, anomalies);

        return anomalies;
    }

    private void detectVitalSignAnomalies(VitalSignEventDto vitalSign, List<String> anomalies) {
        checkHeartRate(vitalSign, anomalies);
        checkBloodPressure(vitalSign, anomalies);
        checkTemperature(vitalSign, anomalies);
        checkRespiratoryRate(vitalSign, anomalies);
        checkOxygenSaturation(vitalSign, anomalies);
    }

    private void checkHeartRate(VitalSignEventDto vitalSign, List<String> anomalies) {
        double heartRate = vitalSign.getHeartRate();
        if (heartRate < MIN_HEART_RATE) {
            anomalies.add("Heart rate below normal range");
        } else if (heartRate > MAX_HEART_RATE) {
            anomalies.add("Heart rate above normal range");
        }
    }

    private void checkBloodPressure(VitalSignEventDto vitalSign, List<String> anomalies) {
        double systolic = vitalSign.getBloodPressureSystolic();
        double diastolic = vitalSign.getBloodPressureDiastolic();

        if (systolic < MIN_BLOOD_PRESSURE_SYSTOLIC) {
            anomalies.add("Low systolic blood pressure");
        } else if (systolic > MAX_BLOOD_PRESSURE_SYSTOLIC) {
            anomalies.add("High systolic blood pressure");
        }

        if (diastolic < MIN_BLOOD_PRESSURE_DIASTOLIC) {
            anomalies.add("Low diastolic blood pressure");
        } else if (diastolic > MAX_BLOOD_PRESSURE_DIASTOLIC) {
            anomalies.add("High diastolic blood pressure");
        }
    }

    private void checkTemperature(VitalSignEventDto vitalSign, List<String> anomalies) {
        double temperature = vitalSign.getTemperature();
        if (temperature < MIN_TEMPERATURE) {
            anomalies.add("Temperature below normal range");
        } else if (temperature > MAX_TEMPERATURE) {
            anomalies.add("Temperature above normal range");
        }
    }

    private void checkRespiratoryRate(VitalSignEventDto vitalSign, List<String> anomalies) {
        double respiratoryRate = vitalSign.getRespiratoryRate();
        if (respiratoryRate < MIN_RESPIRATORY_RATE) {
            anomalies.add("Respiratory rate below normal range");
        } else if (respiratoryRate > MAX_RESPIRATORY_RATE) {
            anomalies.add("Respiratory rate above normal range");
        }
    }

    private void checkOxygenSaturation(VitalSignEventDto vitalSign, List<String> anomalies) {
        double oxygenSaturation = vitalSign.getOxygenSaturation();
        if (oxygenSaturation < MIN_OXYGEN_SATURATION) {
            anomalies.add("Low oxygen saturation");
        }
    }

    private void detectDeviationsFromBaseline(VitalSignEventDto vitalSign, PatientHealthProfile profile, List<String> anomalies) {
        if (profile.getReadingCount() > 0) {
            checkSignificantDeviation(vitalSign.getHeartRate(), profile.getHeartRateAvg(),
                    "Significant change in heart rate", anomalies);
            checkSignificantDeviation(vitalSign.getBloodPressureSystolic(), profile.getBloodPressureSystolicAvg(),
                    "Significant change in systolic blood pressure", anomalies);
            checkSignificantDeviation(vitalSign.getBloodPressureDiastolic(), profile.getBloodPressureDiastolicAvg(),
                    "Significant change in diastolic blood pressure", anomalies);
            checkSignificantDeviation(vitalSign.getTemperature(), profile.getTemperatureAvg(),
                    "Significant change in temperature", anomalies);
            checkSignificantDeviation(vitalSign.getRespiratoryRate(), profile.getRespiratoryRateAvg(),
                    "Significant change in respiratory rate", anomalies);
            checkSignificantDeviation(vitalSign.getOxygenSaturation(), profile.getOxygenSaturationAvg(),
                    "Significant change in oxygen saturation", anomalies);
        }
    }

    private void checkSignificantDeviation(double current, double baseline, String message, List<String> anomalies) {
        double deviation = Math.abs(current - baseline) / baseline;
        if (deviation > SIGNIFICANT_DEVIATION) {
            anomalies.add(message);
        }
    }

    private void detectCriticalCombinations(VitalSignEventDto vitalSign, List<String> anomalies) {
        checkVitalSignCombination(
                vitalSign.getBloodPressureSystolic() < 90 && vitalSign.getHeartRate() > 100,
                "Critical: Low blood pressure with tachycardia",
                anomalies
        );

        checkVitalSignCombination(
                vitalSign.getOxygenSaturation() < 90 && vitalSign.getRespiratoryRate() > 25,
                "Critical: Low oxygen saturation with high respiratory rate",
                anomalies
        );

        checkVitalSignCombination(
                (vitalSign.getTemperature() > 38.0 || vitalSign.getTemperature() < 36.0) && vitalSign.getHeartRate() > 90,
                "Critical: Abnormal temperature with elevated heart rate",
                anomalies
        );
    }

    private void checkVitalSignCombination(boolean condition, String message, List<String> anomalies) {
        if (condition) {
            anomalies.add(message);
        }
    }
}
