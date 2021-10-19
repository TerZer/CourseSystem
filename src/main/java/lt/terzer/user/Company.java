package lt.terzer.user;

import lt.terzer.sql.data.SerializableList;

public class Company extends User {

    private String companyName;

    public Company(int id, String companyName, String username, String name, String surname, String contactInformation, String password
            , boolean courseCreator, boolean admin, SerializableList editableCourses, SerializableList accessibleCourses) {
        super(id, username, name, surname, contactInformation, password, courseCreator, admin, editableCourses, accessibleCourses);
        this.companyName = companyName;
    }

    public Company(String companyName, String username, String name, String surname, String contactInformation, String password
            , boolean courseCreator, boolean admin, SerializableList editableCourses, SerializableList accessibleCourses) {
        this(-1, companyName, username, name, surname, contactInformation, password, courseCreator, admin, editableCourses, accessibleCourses);
    }

    public Company(String companyName, String username, String name, String surname, String contactInformation, String password, boolean courseCreator, boolean admin) {
        this(companyName, username, name, surname, contactInformation, password, courseCreator, admin, null, null);
    }


    @Override
    public boolean isCompany(){
        return true;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
        setDirty();
    }
}
