package lt.terzer.ui;

import lt.terzer.MainApplication;
import lt.terzer.courses.Course;
import lt.terzer.files.File;
import lt.terzer.files.Folder;
import lt.terzer.user.User;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TreeMouseAdapter extends MouseAdapter {

    private final MainWindow mainWindow;
    private final JTree tree1;
    private final JList<Course> courseJList;
    private final User user;

    public TreeMouseAdapter(MainWindow mainWindow, JTree tree1, JList<Course> courseJList, User user) {
        this.mainWindow = mainWindow;
        this.tree1 = tree1;
        this.courseJList = courseJList;
        this.user = user;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        if (SwingUtilities.isRightMouseButton(e)) {
            onRightClick(e);
        }
    }

    private void onRightClick(MouseEvent e){
        TreePath path = tree1.getPathForLocation(e.getX(), e.getY());
        Course course = courseJList.getSelectedValue();
        if (!user.getEditableCourses().contains(course.getId()) && !user.isAdmin()) {
            return;
        }

        showMenu(e, course, path);
    }

    private void showMenu(MouseEvent e, Course course, TreePath path){
        JPopupMenu menu = new JPopupMenu();
        JMenuItem folderCreate = getFolderCreateItem(course, path);
        JMenuItem fileCreate = getFileCreateItem(course, path);
        JMenuItem delete = getDeleteItem(course, path);
        JMenuItem information = getInformationItem(path);
        if (path != null) {
            File file = (File) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
            if (file.isFolder()) {
                menu.add(folderCreate);
                menu.add(fileCreate);
            }
            menu.add(delete);
            menu.add(information);
        } else {
            menu.add(folderCreate);
            menu.add(fileCreate);
        }
        menu.show(tree1, e.getX(), e.getY());
    }

    private JMenuItem getFolderCreateItem(Course course, TreePath path){
        JMenuItem folderCreate = new JMenuItem("Add folder...");
        folderCreate.addActionListener(e1 -> {
            String name = JOptionPane.showInputDialog("Write name of a folder:");
            if (name == null)
                return;
            Folder folder;
            DefaultMutableTreeNode root;
            if (path == null) {
                folder = new Folder(name);
                MainApplication.getFileDatabase().save(folder);
                course.addFileId(folder.getId());
                root = (DefaultMutableTreeNode) tree1.getModel().getRoot();
            } else {
                folder = (Folder) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                Folder nf = new Folder(name);
                MainApplication.getFileDatabase().save(nf);
                folder.addFile(nf);
                MainApplication.getFileDatabase().save(folder);
                root = (DefaultMutableTreeNode) path.getLastPathComponent();
                folder = nf;
            }
            DefaultTreeModel model = ((DefaultTreeModel) tree1.getModel());
            MainApplication.getCourseDatabase().save(course);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(folder);
            root.add(node);
            tree1.expandPath(new TreePath(root));
            model.nodesWereInserted(root, new int[]{model.getIndexOfChild(root, node)});
        });
        return folderCreate;
    }

    private JMenuItem getFileCreateItem(Course course, TreePath path){
        JMenuItem fileCreate = new JMenuItem("Add file...");
        fileCreate.addActionListener(e1 -> {
            String name = JOptionPane.showInputDialog("Write name of a file:");
            if (name == null)
                return;
            File file;
            DefaultMutableTreeNode root;
            if (path == null) {
                file = new File(name);
                MainApplication.getFileDatabase().save(file);
                course.addFileId(file.getId());
                root = (DefaultMutableTreeNode) tree1.getModel().getRoot();
            } else {
                Folder folder = (Folder) ((DefaultMutableTreeNode) path.getLastPathComponent()).getUserObject();
                File nf = new File(name);
                MainApplication.getFileDatabase().save(nf);
                folder.addFile(nf);
                MainApplication.getFileDatabase().save(folder);
                file = nf;
                root = (DefaultMutableTreeNode) path.getLastPathComponent();
            }
            DefaultTreeModel model = ((DefaultTreeModel) tree1.getModel());
            MainApplication.getCourseDatabase().save(course);
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
            root.add(node);
            tree1.expandPath(new TreePath(root));
            model.nodesWereInserted(root, new int[]{model.getIndexOfChild(root, node)});
        });
        return fileCreate;
    }

    private JMenuItem getDeleteItem(Course course, TreePath path){
        JMenuItem delete = new JMenuItem("Delete");
        delete.addActionListener(e1 -> {
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                File file = (File) node.getUserObject();
                Folder folder = (Folder) parent.getUserObject();
                mainWindow.removeFile(course, folder, file);
                DefaultTreeModel model = ((DefaultTreeModel) tree1.getModel());
                int[] indices = new int[]{model.getIndexOfChild(parent, node)};
                parent.remove(node);
                model.nodesWereRemoved(parent, indices, new DefaultMutableTreeNode[]{node});
            }
        });
        return delete;
    }

    private JMenuItem getInformationItem(TreePath path){
        JMenuItem information = new JMenuItem("Info");
        information.addActionListener(e1 -> {
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                File file = (File) node.getUserObject();
                new FileInformationWindow(file);
            }
        });
        return information;
    }
}
