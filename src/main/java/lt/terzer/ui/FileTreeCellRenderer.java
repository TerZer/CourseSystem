package lt.terzer.ui;

import lt.terzer.files.File;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class FileTreeCellRenderer extends DefaultTreeCellRenderer {

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
        Object file = node.getUserObject();

        if (file instanceof File){
            if (!((File)file).isFolder()) {
                this.setIcon(UIManager.getIcon("FileView.fileIcon"));
            } else {
                if (expanded) {
                    this.setIcon(getOpenIcon());
                } else {
                    this.setIcon(getClosedIcon());
                }
            }
        }

        return this;
    }
}
