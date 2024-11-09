package com.example.exam.model;

import com.example.exam.converter.ExamStatusConverter;
import com.example.exam.enums.ExamStatus;
import com.example.qa.model.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "practice_exams")
@NoArgsConstructor
@AllArgsConstructor
public class PracticeExam extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "parent_exam_id")
    private Integer parentExamId;

    @Column(name = "name")
    private String name;

    @Column(name = "description", length = 512)
    private String description;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "allocated_time_in_min")
    private int allocatedTimeInMin;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "exam_status")
    @Convert(converter = ExamStatusConverter.class)
    private ExamStatus status = ExamStatus.NOT_SCHEDULED;

    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "total_marks")
    private Double totalMarks;

    @Column(name = "examinee_user_name")
    private String examineeUserName;

    @Column(name = "marks_gained")
    private Double marksGained;

    @Column(name = "probable_position")
    private Integer probablePosition;
}