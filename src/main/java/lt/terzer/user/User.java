package lt.terzer.user;

import lt.terzer.sql.data.DatabaseSavable;
import lt.terzer.sql.data.SerializableList;

import java.util.List;
import java.util.Objects;

public class User extends DatabaseSavable {

    private String username;
    private String name, surname;
    private String contactInformation;
    private String password;
    private SerializableList editableCourses;
    private SerializableList accessibleCourses;
    private boolean courseCreator;
    private boolean admin;

    public User(int id, String username, String name, String surname, String contactInformation, String password
            , boolean courseCreator, boolean admin, SerializableList editableCourses, SerializableList accessibleCourses) {
        super(id);
        this.username = username;
        this.name = name;
        this.surname = surname;
        this.contactInformation = contactInformation;
        this.password = password;
        this.courseCreator = courseCreator;
        this.admin = admin;
        this.editableCourses = Objects.requireNonNullElseGet(editableCourses, SerializableList::new);
        this.accessibleCourses = Objects.requireNonNullElseGet(accessibleCourses, SerializableList::new);
    }

    public User(String username, String name, String surname, String contactInformation, String password
            , boolean courseCreator, boolean admin, SerializableList editableCourses, SerializableList accessibleCourses) {
        this(-1, username, name, surname, contactInformation, password, courseCreator, admin, editableCourses, accessibleCourses);
    }

    public User(String username, String name, String surname, String contactInformation, String password, boolean courseCreator, boolean admin) {
        this(username, name, surname, contactInformation, password, courseCreator, admin, null, null);
    }


    public boolean isCompany(){
        return false;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
        setDirty();
    }

    public void setName(String name) {
        this.name = name;
        setDirty();
    }

    public SerializableList getEditableCourses() {
        return editableCourses;
    }

    public SerializableList getAccessibleCourses() {
        return accessibleCourses;
    }

    public void setEditableCourses(SerializableList editableCourses) {
        this.editableCourses = editableCourses;
        setDirty();
    }

    public void addEditableCourse(int courseId){
        editableCourses.add(courseId);
        setDirty();
    }

    public void setAccessibleCourses(SerializableList accessibleCourses) {
        this.accessibleCourses = accessibleCourses;
        setDirty();
    }

    public void addAccessibleCourse(int courseId){
        accessibleCourses.add(courseId);
        setDirty();
    }

    public boolean isCourseCreator() {
        return courseCreator;
    }

    public void setCourseCreator(boolean courseCreator) {
        this.courseCreator = courseCreator;
        setDirty();
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
        setDirty();
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
        setDirty();
    }

    public String getContactInformation() {
        return contactInformation;
    }

    public void setContactInformation(String contactInformation) {
        this.contactInformation = contactInformation;
        setDirty();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        setDirty();
    }

    @Override
    public String toString() {
        return "[ username=" + username + " " +
                "name=" + name + " " +
                "surname=" + surname + " " +
                "contactInformation=" + contactInformation + " " +
                "password=" + password + " " +
                "editableCourses=" + editableCourses + " " +
                "accessibleCourses=" + accessibleCourses + " " +
                "courseCreator=" + courseCreator + " " +
                "admin=" + admin + " " +
                "company=" + isCompany() + " ]";
    }
}
