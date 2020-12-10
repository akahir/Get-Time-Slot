package Util;

import android.app.Application;

public class StudentApi extends Application {
    private String userId;
    private String StudentName;
    private String ScholarNo;
    private static StudentApi studentInstance;

    public static StudentApi getStudentInstance() {
        if(studentInstance == null)
            studentInstance = new StudentApi();
        return studentInstance;
    }

    public StudentApi() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStudentName() {
        return StudentName;
    }

    public void setStudentName(String studentName) {
        StudentName = studentName;
    }

    public String getScholarNo() {
        return ScholarNo;
    }

    public void setScholarNo(String scholarNo) {
        ScholarNo = scholarNo;
    }
}
