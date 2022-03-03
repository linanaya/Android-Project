package com.example.myapplication.utils.BluetoothTool;

public class GPSObj {
    private double latitude;
    private String latitudeHemisphere;
    private double longtitude;
    private String longtitudeHemisphere;
    private String time;
    private String status;
    private String model;

    @Override
    public String toString() {
        return "GPSObj{" +
                "latitude=" + latitude +
                ", latitudeHemisphere='" + latitudeHemisphere + '\'' +
                ", longtitude=" + longtitude +
                ", longtitudeHemisphere='" + longtitudeHemisphere + '\'' +
                ", time='" + time + '\'' +
                ", status='" + status + '\'' +
                ", model='" + model + '\'' +
                '}';
    }

    public GPSObj (String[] data){
        status = data[6].equals("")?null:data[6];
        latitude = data[1].equals("")?0.0:Double.parseDouble(data[1])/100;
        latitudeHemisphere = data[2].equals("")?null:data[2];
        longtitude = data[3].equals("")?0.0:Double.parseDouble(data[3])/100;
        longtitudeHemisphere = data[4].equals("")?null:data[4];
        time = data[5].equals("")?null:data[5];
        model = data[7].equals("")?null:data[7];
    }
    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getLatitudeHemisphere() {
        return latitudeHemisphere;
    }

    public void setLatitudeHemisphere(String latitudeHemisphere) {
        this.latitudeHemisphere = latitudeHemisphere;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getLongtitudeHemisphere() {
        return longtitudeHemisphere;
    }

    public void setLongtitudeHemisphere(String longtitudeHemisphere) {
        this.longtitudeHemisphere = longtitudeHemisphere;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }


}
