package lt.terzer.ui;

import lt.terzer.MainApplication;
import lt.terzer.courses.Course;
import lt.terzer.user.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnrollAction implements ActionListener {

    private final MainWindow mainWindow;
    private final User user;
    private final JList<Course> courseJList;
    private final JButton enrollButton;
    private final JTree tree1;

    public EnrollAction(MainWindow mainWindow, User user, JList<Course> courseJList, JButton enrollButton, JTree tree1) {
        this.mainWindow = mainWindow;
        this.user = user;
        this.courseJList = courseJList;
        this.enrollButton = enrollButton;
        this.tree1 = tree1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (courseJList.getSelectedValue() != null) {
            Course course = (courseJList.getSelectedValue());
            user.addAccessibleCourse(course.getId());
            MainApplication.getUserDatabase().save(user);
            enrollButton.setVisible(false);
            tree1.setVisible(true);
            tree1.setModel(mainWindow.createNodes(course));
        }
    }
}
