package lt.terzer.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import lt.terzer.MainApplication;
import lt.terzer.courses.Course;
import lt.terzer.files.File;
import lt.terzer.files.Folder;
import lt.terzer.user.User;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class MainWindow extends JFrame {
    private JTree tree1;
    private JPanel panel1;
    private JList<Course> courseJList;
    private JLabel descriptionField;
    private JPanel coursePanel;
    private JButton enrollButton;
    private transient User user;

    public MainWindow(User u) {
        super("Program");
        this.user = u;
        setPreferredSize(new Dimension(600, 600));
        setContentPane(panel1);
        pack();
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);

        tree1.setVisible(false);
        enrollButton.setVisible(false);
        coursePanel.setVisible(false);

        DefaultListModel<Course> model = new DefaultListModel<>();
        List<Course> courses = MainApplication.getCourseDatabase().getAll();
        for (Course course : courses) {
            model.addElement(course);
        }
        courseJList.setModel(model);
        tree1.setCellRenderer(new FileTreeCellRenderer());

        enrollButton.addActionListener(new EnrollAction(this, user, courseJList, enrollButton, tree1));
        courseJList.addListSelectionListener(new CourseListSelectionListener(this, user, courseJList, enrollButton, tree1, coursePanel, descriptionField));

        tree1.setRootVisible(false);
        tree1.addMouseListener(new TreeMouseAdapter(this, tree1, courseJList, user));

        courseJList.setSelectionModel(new CourseListSelectionModel());
        courseJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        courseJList.addMouseListener(new CourseMouseAdapter(this, courseJList, descriptionField, user));
    }

    public void removeFiles(List<File> files) {
        files.forEach(f -> {
            if (f.isFolder()) {
                removeFiles(MainApplication.getFileDatabase().getByIds(((Folder) f).getFiles()));
            }
            MainApplication.getFileDatabase().remove(f);
        });

    }

    public void removeFile(Course course, Folder parent, File file) {
        if (file.isFolder()) {
            for (File f : MainApplication.getFileDatabase().getByIds(((Folder) file).getFiles())) {
                removeFile(course, (Folder) file, f);
            }
        }
        if (parent != null) {
            parent.removeFile(file.getId());
        }
        course.removeFileId(file.getId());
        MainApplication.getCourseDatabase().save(course);
        MainApplication.getFileDatabase().remove(file);
    }

    public DefaultTreeModel createNodes(Course course) {
        List<File> files = MainApplication.getFileDatabase().getByIds(course.getFilesIds());
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        for (File file : files) {
            addNodes(root, file);
        }
        return new DefaultTreeModel(root);
    }

    private void addNodes(DefaultMutableTreeNode root, File file) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
        if (file.isFolder()) {
            List<File> files = MainApplication.getFileDatabase().getByIds(((Folder) file).getFiles());
            files.forEach(f -> addNodes(node, f));
        }
        root.add(node);
    }

    public void setUser(User user) {
        this.user = user;
    }

    protected JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();

        JMenu mFile = new JMenu("User");
        mFile.setMnemonic('f');
        JMenuItem item;
        if (user.isAdmin()) {
            Action actionAddCreator = new AbstractAction("Add course creator...") {
                public void actionPerformed(ActionEvent e) {
                    List<User> users = MainApplication.getUserDatabase().getAll().stream()
                            .filter(u -> !u.isCourseCreator() && !u.isAdmin())
                            .toList();
                    new UserSelectWindow(users, selectedUsers -> {
                        selectedUsers.forEach(u -> u.setCourseCreator(true));
                        MainApplication.getUserDatabase().save(selectedUsers);
                    });
                }
            };
            item = mFile.add(actionAddCreator);
            mFile.add(item);
            Action actionRemoveCreator = new AbstractAction("Remove course creator...") {
                public void actionPerformed(ActionEvent e) {
                    List<User> users = MainApplication.getUserDatabase().getAll().stream()
                            .filter(u -> u.isCourseCreator() && !u.isAdmin())
                            .toList();
                    new UserSelectWindow(users, selectedUsers -> {
                        selectedUsers.forEach(u -> u.setCourseCreator(false));
                        MainApplication.getUserDatabase().save(selectedUsers);
                    });
                }
            };
            item = mFile.add(actionRemoveCreator);
            mFile.add(item);
        }
        if (user.isAdmin() || user.isCourseCreator()) {
            Action actionCreate = new AbstractAction("Create course...") {
                public void actionPerformed(ActionEvent e) {
                    new CourseCreationWindow(user, () -> {
                        DefaultListModel<Course> model = new DefaultListModel<>();
                        List<Course> courses = MainApplication.getCourseDatabase().getAll();
                        for (Course course : courses) {
                            model.addElement(course);
                        }
                        courseJList.setModel(model);
                    });
                }
            };
            item = mFile.add(actionCreate);
            mFile.add(item);
        }

        mFile.addSeparator();
        Action actionSignout = new AbstractAction("Sign out") {
            public void actionPerformed(ActionEvent e) {
                dispose();
                new LoginWindow();
            }
        };
        item = mFile.add(actionSignout);
        item.setMnemonic('s');
        mFile.add(item);
        Action actionExit = new AbstractAction("Exit") {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        };
        item = mFile.add(actionExit);
        item.setMnemonic('x');
        menuBar.add(mFile);

        return menuBar;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        courseJList = new JList<>();
        courseJList.setSelectionMode(0);
        panel1.add(courseJList, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(50, 50), null, 0, false));
        coursePanel = new JPanel();
        coursePanel.setLayout(new GridLayoutManager(5, 1, new Insets(0, 0, 0, 0), -1, -1));
        coursePanel.setBackground(new Color(-1));
        coursePanel.setForeground(new Color(-1));
        panel1.add(coursePanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Description:");
        coursePanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        descriptionField = new JLabel();
        descriptionField.setText("");
        coursePanel.add(descriptionField, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        coursePanel.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText(" ");
        panel2.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enrollButton = new JButton();
        enrollButton.setText("Enroll");
        coursePanel.add(enrollButton, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        tree1 = new JTree();
        tree1.setEnabled(true);
        coursePanel.add(tree1, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
