package com.example.mhike;

public class Hike {
    private long id;
    private String name;
    private String location;
    private String date;
    private String parking;
    private String lengthKm;
    private String difficulty;
    private String description;
    private String weather;
    private String equipment;

    public Hike() {}

    public Hike(long id, String name, String location, String date, String parking,
                String lengthKm, String difficulty, String description,
                String weather, String equipment) {
        this.id = id; this.name = name; this.location = location; this.date = date;
        this.parking = parking; this.lengthKm = lengthKm; this.difficulty = difficulty;
        this.description = description; this.weather = weather; this.equipment = equipment;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getParking() { return parking; }
    public void setParking(String parking) { this.parking = parking; }
    public String getLengthKm() { return lengthKm; }
    public void setLengthKm(String lengthKm) { this.lengthKm = lengthKm; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }
    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }
}
