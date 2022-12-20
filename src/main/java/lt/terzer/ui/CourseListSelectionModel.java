package lt.terzer.ui;

import javax.swing.*;

public class CourseListSelectionModel extends DefaultListSelectionModel {

    boolean gestureStarted = false;

    @Override
    public void setSelectionInterval(int index0, int index1) {
        if (!gestureStarted) {
            if (index0 == index1 && isSelectedIndex(index0)) {
                removeSelectionInterval(index0, index0);
                return;
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

}
