package Model;

public class Slot {
    private String UserId;
    private String StartTime;
    private String EndTime;
    private String Department;
    private String UserName;
    private String Subject;

    public Slot() {
    }

    public Slot(String userId, String startTime, String endTime, String department, String userName, String subject) {
        UserId = userId;
        StartTime = startTime;
        EndTime = endTime;
        Department = department;
        UserName = userName;
        Subject = subject;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getSubject() {
        return Subject;
    }

    public void setSubject(String subject) {
        Subject = subject;
    }
}
