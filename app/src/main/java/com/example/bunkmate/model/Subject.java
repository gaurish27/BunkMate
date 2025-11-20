// com/example/bunkmate/model/Subject.java
package com.example.bunkmate.model;

public class Subject {
    public long id;
    public String name;
    public int attended;
    public int total;
    public int minRequired;
    public String professorName;

    public Subject(long id, String name, int attended, int total, int minRequired, String professorName) {
        this.id = id;
        this.name = name;
        this.attended = attended;
        this.total = total;
        this.minRequired = minRequired;
        this.professorName = professorName;
    }
    private int safeBunksForDisplay;
    public void setSafeBunksForDisplay(int value) { this.safeBunksForDisplay = value; }
    public int getSafeBunksForDisplay() { return safeBunksForDisplay; }


    public int calculateSafeBunks(int weeklyClasses) {
        if (minRequired <= 0) return 0;
        int monthly = weeklyClasses * 4;
        float safe = ((attended + monthly) * 100f / minRequired) - (total + monthly);
        return Math.max(0, (int) Math.floor(safe));
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAttended() {
        return attended;
    }

    public void setAttended(int attended) {
        this.attended = attended;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getMinRequired() {
        return minRequired;
    }

    public void setMinRequired(int minRequired) {
        this.minRequired = minRequired;
    }

    public String getProfessorName() {
        return professorName;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public int getPercent() {
        if (total <= 0) return 0;
        return Math.round((attended * 100f) / total);
    }

    public int getSafeBunks() {
        if (minRequired <= 0) return 0;
        float maxTotal = (attended * 100f) / Math.max(minRequired, 1);
        int safe = (int) Math.floor(maxTotal - total);
        return Math.max(safe, 0);
    }
}
