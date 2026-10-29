package gui.admin.TimetableComponents;

public class TimetableFilterSet extends TimetableFilterBar {
    public TimetableFilterSet(Runnable onFilterTrigger) {
        super(onFilterTrigger, () -> {});
    }
    public TimetableFilterSet(Runnable onFilterTrigger, Runnable onRefresh) {
        super(onFilterTrigger, onRefresh);
    }
}
