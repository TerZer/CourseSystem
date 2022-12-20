package lt.terzer.ui;

import lt.terzer.MainApplication;
import lt.terzer.courses.Course;
import lt.terzer.user.User;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class CourseMouseAdapter extends MouseAdapter {

    private final MainWindow mainWindow;
    private final JList<Course> courseJList;
    private final JLabel descriptionField;
    private User user;

    public CourseMouseAdapter(MainWindow mainWindow, JList<Course> courseJList, JLabel descriptionField, User user) {
        this.mainWindow = mainWindow;
        this.courseJList = courseJList;
        this.descriptionField = descriptionField;
        this.user = user;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
           onRightClick(e);
        }
    }

    private void onRightClick(MouseEvent e){
        setSelection(e);
        JPopupMenu menu = new JPopupMenu();
        if (courseJList.getSelectedValue() == null && (user.isCourseCreator() || user.isAdmin())) {
            menuAddCreate(menu);
            menu.show(courseJList, e.getPoint().x, e.getPoint().y);
        } else {
            if (courseJList.getSelectedValue() == null)
                return;
            if (user.getEditableCourses().contains((courseJList.getSelectedValue()).getId()) || user.isAdmin()) {
                menuAddEditable(menu);
                menu.show(courseJList, e.getPoint().x, e.getPoint().y);
            } else {
                Course course = courseJList.getSelectedValue();
                if (user.getAccessibleCourses().contains(course.getId()) && course.getOwnerId() != user.getId()) {
                    JMenuItem itemLeave = new JMenuItem("Leave course");
                    itemLeave.addActionListener(e1 -> {
                        user.removeAccessibleCourse(course.getId());
                        MainApplication.getUserDatabase().save(user);
                        courseJList.setSelectedValue(null, false);
                    });
                    menu.add(itemLeave);
                    menu.show(courseJList, e.getPoint().x, e.getPoint().y);
                }
            }
        }
    }

    private void menuAddCreate(JPopupMenu menu){
        JMenuItem itemCreate = new JMenuItem("Create course...");
        itemCreate.addActionListener(e1 -> {
            new CourseCreationWindow(user, () -> {
                DefaultListModel<Course> model = new DefaultListModel<>();
                List<Course> courses = MainApplication.getCourseDatabase().getAll();
                for (Course course : courses) {
                    model.addElement(course);
                }
                courseJList.setModel(model);
            });
        });
        menu.add(itemCreate);
    }

    private void menuAddEditable(JPopupMenu menu){
        JMenuItem itemRemove = new JMenuItem("Remove course");
        JMenuItem addModerator = new JMenuItem("Add moderator...");
        JMenuItem removeModerator = new JMenuItem("Remove moderator...");
        JMenuItem changeDescription = new JMenuItem("Change description...");
        JMenuItem addUsers = new JMenuItem("Add users...");
        JMenuItem removeUsers = new JMenuItem("Remove users...");
        itemRemove.addActionListener(e1 -> {
            Course course = courseJList.getSelectedValue();
            MainApplication.getCourseDatabase().remove(course);
            MainApplication.getUserDatabase().getAll().forEach(u -> {
                u.removeAccessibleCourse(course.getId());
                u.removeEditableCourse(course.getId());
                MainApplication.getUserDatabase().save(u);
            });
            mainWindow.removeFiles(MainApplication.getFileDatabase().getByIds(course.getFilesIds()));
            DefaultListModel<Course> m = new DefaultListModel<>();
            user = MainApplication.getUserDatabase().getById(user.getId());
            mainWindow.setUser(user);
            List<Course> courses1 = MainApplication.getCourseDatabase().getAll();
            for (Course c : courses1) {
                m.addElement(c);
            }
            courseJList.setModel(m);
        });
        changeDescription.addActionListener(e1 -> {
            String desc = JOptionPane.showInputDialog("New description name:");
            if (desc != null) {
                Course course = (courseJList.getSelectedValue());
                course.setDescription(desc);
                MainApplication.getCourseDatabase().save(course);
                descriptionField.setText(course.getDescription());
            }
        });
        addModerator.addActionListener(e1 -> {
            List<User> users = MainApplication.getUserDatabase().getAll();
            users.removeIf(u -> !u.getAccessibleCourses().contains((courseJList.getSelectedValue()).getId())
                    || u.getEditableCourses().contains((courseJList.getSelectedValue()).getId())
                    || (courseJList.getSelectedValue()).getOwnerId() == u.getId()
                    || u.getId() == user.getId());
            new UserSelectWindow(users, users1 -> {
                users1.forEach(u -> u.addEditableCourse((courseJList.getSelectedValue()).getId()));
                MainApplication.getUserDatabase().save(users1);
            });
        });
        removeModerator.addActionListener(e1 -> {
            List<User> users = MainApplication.getUserDatabase().getAll();
            users.removeIf(u -> !u.getEditableCourses().contains((courseJList.getSelectedValue()).getId()) || (courseJList.getSelectedValue()).getOwnerId() == u.getId() || u.getId() == user.getId());
            new UserSelectWindow(users, users1 -> {
                users1.forEach(u -> u.removeEditableCourse((courseJList.getSelectedValue()).getId()));
                MainApplication.getUserDatabase().save(users1);
            });
        });
        addUsers.addActionListener(e1 -> {
            List<User> users = MainApplication.getUserDatabase().getAll();
            users.removeIf(u -> u.getAccessibleCourses().contains((courseJList.getSelectedValue()).getId()) || (courseJList.getSelectedValue()).getOwnerId() == u.getId() || u.getId() == user.getId());
            new UserSelectWindow(users, users1 -> {
                users1.forEach(u -> u.addAccessibleCourse((courseJList.getSelectedValue()).getId()));
                MainApplication.getUserDatabase().save(users1);
            });
        });
        removeUsers.addActionListener(e1 -> {
            List<User> users = MainApplication.getUserDatabase().getAll();
            users.removeIf(u -> !u.getAccessibleCourses().contains((courseJList.getSelectedValue()).getId()) || courseJList.getSelectedValue().getOwnerId() == u.getId() || u.getId() == user.getId());
            new UserSelectWindow(users, users1 -> {
                users1.forEach(u -> {
                    u.removeAccessibleCourse((courseJList.getSelectedValue()).getId());
                    u.removeEditableCourse((courseJList.getSelectedValue()).getId());
                });
                MainApplication.getUserDatabase().save(users1);
            });
        });
        if (user.isAdmin() || (courseJList.getSelectedValue()).getOwnerId() == user.getId() || user.getEditableCourses().contains((courseJList.getSelectedValue()).getId())) {
            menu.add(addUsers);
            menu.add(removeUsers);
            menu.add(changeDescription);
        }
        if (user.isAdmin() || (courseJList.getSelectedValue()).getOwnerId() == user.getId()) {
            menu.add(addModerator);
            menu.add(removeModerator);
            menu.add(itemRemove);
        }
    }

    private void setSelection(MouseEvent e){
        int index = courseJList.locationToIndex(e.getPoint());
        if (index > -1 && courseJList.getCellBounds(index, index).contains(e.getPoint())) {
            if (courseJList.getSelectedIndex() != index) {
                courseJList.setSelectedIndex(index);
                courseJList.setValueIsAdjusting(false);
            }
        } else {
            courseJList.clearSelection();
        }
    }

}
