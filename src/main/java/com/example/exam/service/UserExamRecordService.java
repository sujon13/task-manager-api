package com.example.exam.service;

import com.example.util.UserUtil;
import com.example.exam.enums.UserExamStatus;
import com.example.exam.entity.Exam;
import com.example.exam.entity.UserExamRecord;
import com.example.exam.repository.UserExamRecordRepository;
import com.example.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserExamRecordService {
    private final UserExamRecordRepository userExamRecordRepository;
    private final UserUtil userUtil;

    public boolean hasUserEnteredTheExam(final int examId) {
        return findByExamId(examId)
                .filter(examRecord -> UserExamStatus.ENTERED.equals(examRecord.getUserExamStatus()))
                .isPresent();
    }

    public boolean hasUserExitedFromTheExam(final int examId) {
        return findByExamId(examId)
                .filter(examRecord -> UserExamStatus.EXITED.equals(examRecord.getUserExamStatus()))
                .isPresent();
    }

    // will be called only from exam service (enterExam method)
    public void enter(final int examId) {
        if (hasUserEnteredTheExam(examId)) {
            log.info("{} already entered the exam", userUtil.getUserName());
            return;
        }

        UserExamRecord userExamRecord = new UserExamRecord();
        userExamRecord.setExamId(examId);
        userExamRecord.setExaminee(userUtil.getUserName());
        userExamRecord.setUserExamStatus(UserExamStatus.ENTERED);
        userExamRecordRepository.save(userExamRecord);
    }

    // will be called only from exam service (exitFromExam method)
    public void exit(Exam exam) {
        UserExamRecord userExamRecord = getUserExamRecord(exam.getId());
        userExamRecord.setUserExamStatus(UserExamStatus.EXITED);
    }

    public Optional<UserExamRecord> findByExamId(final int examId) {
        return userExamRecordRepository.findByExamIdAndExaminee(examId, userUtil.getUserName());
    }

    public UserExamRecord getUserExamRecord(final int examId) {
        return findByExamId(examId)
                .orElseThrow(() ->  new NotFoundException("Exam Record not found with examId " + examId +
                        " for user " + userUtil.getUserName()));

    }
}
