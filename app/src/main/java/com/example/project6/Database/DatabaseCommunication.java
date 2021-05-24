package com.example.project6.Database;

public interface DatabaseCommunication {

    void createNewPatient(Patient newPatient);

    void linkCurrentUserToDoctor(String doctorId);

    void postReading(Reading newReading);

}
