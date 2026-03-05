package com.example.java_lms_group_01.model;

public class BMIModel {

    private double height;
    private double weight;
    private UnitType unitType;

    // Constructor param
    public BMIModel(double height, double weight, UnitType unitType) {
        this.height = height;
        this.weight = weight;
        this.unitType = unitType;
    }

    // Calculate BMI
    public double calculateBMI() {

        if (unitType == UnitType.METRIC) {
            // height in meters, weight in kg
            return weight / (height * height);
        } else {
            // height in inches, weight in pounds
            return (weight * 703) / (height * height);
        }
    }

    // Get BMI Status
    public String getStatus(double bmi) {

        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 24.9) {
            return "Normal weight";
        } else if (bmi < 29.9) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }

    // getter and setter

    public double getHeight() {
        return height;
    }

    public double getWeight() {
        return weight;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    // to print object for testing
    @Override
    public String toString() {
        return "BMIModel{" +
                "height=" + height +
                ", weight=" + weight +
                ", unitType=" + unitType +
                '}';
    }


}
