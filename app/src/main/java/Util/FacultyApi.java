package Util;

import android.app.Application;

public class FacultyApi extends Application {
    private String userId;
    private String department;
    private String FacultyName;
    private static FacultyApi facultyInstance;

    public static FacultyApi getFacultyInstance() {
        if(facultyInstance == null)
            facultyInstance = new FacultyApi();
        return facultyInstance;
    }

    public FacultyApi() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getFacultyName() {
        return FacultyName;
    }

    public void setFacultyName(String facultyName) {
        FacultyName = facultyName;
    }
}
