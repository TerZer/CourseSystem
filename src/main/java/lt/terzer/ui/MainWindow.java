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
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MainWindow extends JFrame {
    private JTree tree1;
    private JPanel panel1;
    private JList courseJList;
    private JLabel descriptionField;
    private JPanel coursePanel;
    private User user;

    public MainWindow(User user) {
        super("Program");
        this.user = user;
        setPreferredSize(new Dimension(600, 600));
        setContentPane(panel1);
        pack();
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

        coursePanel.setVisible(false);

        DefaultListModel<Course> model = new DefaultListModel<>();
        List<Course> courses = MainApplication.getCourseDatabase().getByIds(user.getAccessibleCourses());
        for (Course course : courses) {
            model.addElement(course);
        }
        courseJList.setModel(model);
        tree1.setCellRenderer(new FileTreeCellRenderer());

        courseJList.addListSelectionListener(e -> {
            if (courseJList.getSelectedValue() != null) {
                coursePanel.setVisible(true);
                descriptionField.setText(((Course) courseJList.getSelectedValue()).getDescription());
                tree1.setModel(createNodes((Course) courseJList.getSelectedValue()));
            } else {
                coursePanel.setVisible(false);
                tree1.setModel(null);
                descriptionField.setText(null);
            }
        });

        tree1.setRootVisible(false);
        tree1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (SwingUtilities.isRightMouseButton(e)) {
                    TreePath path = tree1.getPathForLocation(e.getX(), e.getY());
                    Course course = (Course) courseJList.getSelectedValue();
                    if (user.isCourseCreator()) {
                        if (!user.getEditableCourses().contains(course.getId()) || !user.isAdmin()) {
                            return;
                        }
                    } else {
                        if (!user.isAdmin())
                            return;
                    }

                    JPopupMenu menu = new JPopupMenu();
                    JMenuItem folderCreate = new JMenuItem("Add folder...");
                    JMenuItem fileCreate = new JMenuItem("Add file...");
                    JMenuItem delete = new JMenuItem("Delete");
                    folderCreate.addActionListener(e1 -> {
                        String name = JOptionPane.showInputDialog("Write name of a folder:");
                        if (name == null)
                            return;
                        Folder folder;
                        if (path == null) {
                            folder = new Folder(name);
                            MainApplication.getFileDatabase().save(folder);
                            course.addFileId(folder.getId());
                        } else {
                            folder = (Folder) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                            Folder nf = new Folder(name);
                            MainApplication.getFileDatabase().save(nf);
                            folder.addFile(nf);
                            MainApplication.getFileDatabase().save(folder);
                        }
                        MainApplication.getCourseDatabase().save(course);
                        tree1.setModel(createNodes(course)); //TODO add or remove node
                    });
                    fileCreate.addActionListener(e1 -> {
                        String name = JOptionPane.showInputDialog("Write name of a file:");
                        if (name == null)
                            return;
                        if (path == null) {
                            File file = new File(name);
                            MainApplication.getFileDatabase().save(file);
                            course.addFileId(file.getId());
                        } else {
                            Folder folder = (Folder) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                            File nf = new File(name);
                            MainApplication.getFileDatabase().save(nf);
                            folder.addFile(nf);
                            MainApplication.getFileDatabase().save(folder);
                        }
                        MainApplication.getCourseDatabase().save(course);
                        tree1.setModel(createNodes(course)); //TODO add or remove node
                    });
                    delete.addActionListener(e1 -> {
                        if (path != null) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();

                            File file = (File) node.getUserObject();
                            Folder folder = (Folder) parent.getUserObject();
                            removeFile(folder, file);
                            tree1.setModel(createNodes(course)); //TODO add or remove node
                        }
                    });
                    if (path != null) {
                        File file = (File) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                        if (file.isFolder()) {
                            menu.add(folderCreate);
                            menu.add(fileCreate);
                        }
                        menu.add(delete);
                    } else {
                        menu.add(folderCreate);
                        menu.add(fileCreate);
                    }
                    menu.show(tree1, e.getX(), e.getY());
                }
            }
        });

        courseJList.setSelectionModel(new DefaultListSelectionModel() {
            boolean gestureStarted = false;

            @Override
            public void setSelectionInterval(int index0, int index1) {
                if (!gestureStarted) {
                    if (index0 == index1) {
                        if (isSelectedIndex(index0)) {
                            removeSelectionInterval(index0, index0);
                            return;
                        }
                    }
                    super.setSelectionInterval(index0, index1);
                }
                gestureStarted = true;
            }

            @Override
            public void addSelectionInterval(int index0, int index1) {
                if (index0 == index1) {
                    if (isSelectedIndex(index0)) {
                        removeSelectionInterval(index0, index0);
                        return;
                    }
                    super.addSelectionInterval(index0, index1);
                }
            }

            @Override
            public void setValueIsAdjusting(boolean isAdjusting) {
                if (!isAdjusting) {
                    gestureStarted = false;
                }
            }

        });
        courseJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        courseJList.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    int index = courseJList.locationToIndex(e.getPoint());
                    if (index > -1 && courseJList.getCellBounds(index, index).contains(e.getPoint())) {
                        if (courseJList.getSelectedIndex() != index) {
                            courseJList.setSelectedIndex(index);
                            courseJList.setValueIsAdjusting(false);
                        }
                    } else {
                        courseJList.clearSelection();
                    }
                    JPopupMenu menu = new JPopupMenu();
                    if (courseJList.getSelectedValue() == null && (user.isCourseCreator() || user.isAdmin())) {
                        JMenuItem itemCreate = new JMenuItem("Create course...");
                        itemCreate.addActionListener(e1 -> {
                            new CourseCreationWindow(user, () -> {
                                DefaultListModel<Course> model = new DefaultListModel<>();
                                List<Course> courses = MainApplication.getCourseDatabase().getByIds(user.getAccessibleCourses());
                                for (Course course : courses) {
                                    model.addElement(course);
                                }
                                courseJList.setModel(model);
                            });
                        });
                        menu.add(itemCreate);
                        menu.show(courseJList, e.getPoint().x, e.getPoint().y);
                    } else {
                        if (user.getEditableCourses().contains(((Course) courseJList.getSelectedValue()).getId()) || user.isAdmin()) {
                            JMenuItem itemRemove = new JMenuItem("Remove course");
                            JMenuItem addUsers = new JMenuItem("Add users...");
                            JMenuItem removeUsers = new JMenuItem("Remove users...");
                            itemRemove.addActionListener(e1 -> {
                                Course course = (Course) courseJList.getSelectedValue();
                                MainApplication.getCourseDatabase().remove(course);
                                MainApplication.getUserDatabase().getAll().forEach(u -> {
                                    u.removeAccessibleCourse(course.getId());
                                    u.removeEditableCourse(course.getId());
                                    MainApplication.getUserDatabase().save(u);
                                });
                                DefaultListModel<Course> m = new DefaultListModel<>();
                                List<Course> courses1 = MainApplication.getCourseDatabase().getByIds(user.getAccessibleCourses());
                                for (Course c : courses1) {
                                    m.addElement(c);
                                }
                                courseJList.setModel(m);
                            });
                            addUsers.addActionListener(e1 -> {
                                List<User> users = MainApplication.getUserDatabase().getAll();
                                users.removeIf(u -> u.getAccessibleCourses().contains(((Course) courseJList.getSelectedValue()).getId()) || u.getId() == user.getId());
                                new UserSelectWindow(users, users1 -> {
                                    users1.forEach(u -> u.addAccessibleCourse(((Course) courseJList.getSelectedValue()).getId()));
                                    MainApplication.getUserDatabase().save(users1);
                                });
                            });
                            removeUsers.addActionListener(e1 -> {
                                List<User> users = MainApplication.getUserDatabase().getAll();
                                users.removeIf(u -> !u.getAccessibleCourses().contains(((Course) courseJList.getSelectedValue()).getId()) || u.getId() == user.getId());
                                new UserSelectWindow(users, users1 -> {
                                    users1.forEach(u -> u.removeAccessibleCourse(((Course) courseJList.getSelectedValue()).getId()));
                                    MainApplication.getUserDatabase().save(users1);
                                });
                            });
                            menu.add(addUsers);
                            menu.add(removeUsers);
                            menu.add(itemRemove);
                            menu.show(courseJList, e.getPoint().x, e.getPoint().y);
                        }
                    }
                }
            }
        });
    }

    private void removeFile(Folder parent, File file) {
        if (file.isFolder()) {
            for (File f : MainApplication.getFileDatabase().getByIds(((Folder) file).getFiles())) {
                removeFile((Folder) file, f);
            }
        }
        if (parent != null) {
            parent.removeFile(file.getId());
        }
        MainApplication.getFileDatabase().remove(file);
    }

    private DefaultTreeModel createNodes(Course course) {
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

    protected JMenuBar createMenuBar() {
        final JMenuBar menuBar = new JMenuBar();

        JMenu mFile = new JMenu("User");
        mFile.setMnemonic('f');
        JMenuItem item;
        if (user.isAdmin() || user.isCourseCreator()) {
            Action actionCreate = new AbstractAction("Create course...") {
                public void actionPerformed(ActionEvent e) {
                    new CourseCreationWindow(user, () -> {
                        DefaultListModel<Course> model = new DefaultListModel<>();
                        List<Course> courses = MainApplication.getCourseDatabase().getByIds(user.getAccessibleCourses());
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
                new LoginWindow(); //TODO change
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
        courseJList = new JList();
        courseJList.setSelectionMode(0);
        panel1.add(courseJList, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(50, 50), null, 0, false));
        coursePanel = new JPanel();
        coursePanel.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        coursePanel.setBackground(new Color(-1));
        coursePanel.setForeground(new Color(-1));
        panel1.add(coursePanel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        tree1 = new JTree();
        coursePanel.add(tree1, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 50), null, 0, false));
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
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
