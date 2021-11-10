package lt.terzer;

import lt.terzer.databases.CourseDatabase;
import lt.terzer.databases.FileDatabase;
import lt.terzer.databases.UserDatabase;
import lt.terzer.ui.LoginWindow;

import javax.swing.*;

public class Main {

    private static UserDatabase userDatabase = new UserDatabase("localhost:3306", "test", "users", "root", "checkPass123");
    private static CourseDatabase courseDatabase = new CourseDatabase("localhost:3306", "test", "courses", "root", "checkPass123");
    private static FileDatabase fileDatabase = new FileDatabase("localhost:3306", "test", "files", "root", "checkPass123");


    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeLater(LoginWindow::new);
    }

    public static UserDatabase getUserDatabase(){
        return userDatabase;
    }

    public static CourseDatabase getCourseDatabase(){
        return courseDatabase;
    }

    public static FileDatabase getFileDatabase(){
        return fileDatabase;
    }

}
