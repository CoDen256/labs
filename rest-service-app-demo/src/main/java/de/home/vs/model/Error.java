package de.home.vs.model;


public class Error {
    private String error;

    private Error(){
        // private constructor for deserialization
    }

    public Error(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
