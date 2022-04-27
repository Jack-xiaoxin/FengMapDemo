package com.example.fengmapdemo.pojo;

public class MyLocation {

    private Integer locationId;

    private Double x;

    private Double y;

    private Integer floor;

    private String imagePath;

    public Integer getLocationId() {
        return locationId;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Integer getFloor() {
        return floor;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "Location{" +
                "locationId=" + locationId +
                ", x=" + x +
                ", y=" + y +
                ", floor=" + floor +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}
