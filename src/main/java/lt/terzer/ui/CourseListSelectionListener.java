package lt.terzer.ui;

import lt.terzer.courses.Course;
import lt.terzer.user.User;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class CourseListSelectionListener implements ListSelectionListener {

    private final MainWindow mainWindow;
    private final User user;
    private final JList<Course> courseJList;
    private final JButton enrollButton;
    private final JLabel descriptionField;
    private final JPanel coursePanel;
    private final JTree tree1;

    public CourseListSelectionListener(MainWindow mainWindow, User user, JList<Course> courseJList, JButton enrollButton, JTree tree1, JPanel coursePanel, JLabel descriptionField){
        this.mainWindow = mainWindow;
        this.user = user;
        this.courseJList = courseJList;
        this.enrollButton = enrollButton;
        this.descriptionField = descriptionField;
        this.coursePanel = coursePanel;
        this.tree1 = tree1;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (courseJList.getSelectedValue() != null) {
            coursePanel.setVisible(true);
            Course course = (courseJList.getSelectedValue());
            descriptionField.setText(course.getDescription());
            if (user.isAdmin() || user.getAccessibleCourses().contains(course.getId()) || user.getEditableCourses().contains(course.getId()) || course.getOwnerId() == user.getId()) {
                tree1.setVisible(true);
                enrollButton.setVisible(false);
                tree1.setModel(mainWindow.createNodes(course));
            } else {
                enrollButton.setVisible(true);
                tree1.setVisible(false);
            }
        } else {
            coursePanel.setVisible(false);
            tree1.setModel(null);
            descriptionField.setText(null);
        }
    }
}
