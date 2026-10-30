package gui.admin.TimetableComponents;

import dao.admin.AdminTimetableDAO;
import gui.admin.AdminTheme;
import model.TimetableEntry;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

public class TimetableActions extends JPanel {

    public TimetableActions(Frame owner, AdminTimetableDAO dao, Supplier<TimetableEntry> entrySupplier, Runnable onSuccess) {
        setLayout(null);
        setPreferredSize(new Dimension(1620, 56));
        setBackground(AdminTheme.WHITE);
        setOpaque(true);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AdminTheme.PASTEL_GREEN, 1),
                BorderFactory.createEmptyBorder(2, 10, 2, 10)
        ));

        ScheduleSlotComponent btnAdd = new ScheduleSlotComponent(owner, dao, onSuccess);
        btnAdd.setBounds(1000, 8, 160, 40); add(btnAdd);

        ViewTimetableComponent btnView = new ViewTimetableComponent(owner, entrySupplier);
        btnView.setBounds(1170, 8, 130, 40); add(btnView);

        EditTimetableComponent btnEdit = new EditTimetableComponent(owner, dao, entrySupplier, onSuccess);
        btnEdit.setBounds(1310, 8, 130, 40); add(btnEdit);

        DeleteTimetableComponent btnDelete = new DeleteTimetableComponent(owner, dao, entrySupplier, onSuccess);
        btnDelete.setBounds(1450, 8, 140, 40); add(btnDelete);
    }
}
