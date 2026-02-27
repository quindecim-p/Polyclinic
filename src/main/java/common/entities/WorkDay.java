package common.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "WorkDays")
public class WorkDay implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DayOfWeek day;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "working_schedule_id", nullable = false)
    private WorkingSchedule workingSchedule;

    public WorkDay() {}

    public WorkDay(DayOfWeek day, LocalTime startTime, LocalTime endTime, WorkingSchedule workingSchedule) {
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.workingSchedule = workingSchedule;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public DayOfWeek getDay() { return day; }

    public void setDay(DayOfWeek day) { this.day = day; }

    public LocalTime getStartTime() { return startTime; }

    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }

    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public WorkingSchedule getWorkingSchedule() { return workingSchedule; }

    public void setWorkingSchedule(WorkingSchedule workingSchedule) { this.workingSchedule = workingSchedule; }

}